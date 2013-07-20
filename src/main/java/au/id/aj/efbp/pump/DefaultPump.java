/* Copyright 2013 Andrew Jeffery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.id.aj.efbp.pump;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.bootstrap.HaltCommand;
import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.command.CommandPacket;
import au.id.aj.efbp.command.LongCommandId;
import au.id.aj.efbp.control.ControlContext;
import au.id.aj.efbp.control.Controller;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.lifecycle.Lifecycle;
import au.id.aj.efbp.lifecycle.LifecycleContext;
import au.id.aj.efbp.lifecycle.ShutdownCommand;
import au.id.aj.efbp.net.Consumer;
import au.id.aj.efbp.net.Inject;
import au.id.aj.efbp.net.Network;
import au.id.aj.efbp.net.Producer;
import au.id.aj.efbp.schedule.DefaultScheduler;
import au.id.aj.efbp.schedule.ScheduleContext;

public class DefaultPump implements Controller, Lifecycle, Pump {
    private static final Logger logger =
        LoggerFactory.getLogger(DefaultPump.class);

    private final Network network;
    private final DefaultScheduler scheduler;
    @SuppressWarnings("rawtypes")
    private final Trigger<Producer> beginShutdown;
    @SuppressWarnings("rawtypes")
    private final Trigger<Consumer> endShutdown;

    public DefaultPump(final Network network, final DefaultScheduler scheduler) {
        this.network = network;
        this.scheduler = scheduler;
        {
            final Command command = new ShutdownCommand(LongCommandId.next());
            this.beginShutdown = new Trigger<>(Producer.class, command);
        }
        {
            final Command command = new HaltCommand(LongCommandId.next());
            this.endShutdown = new Trigger<>(Consumer.class, command);
        }
    }

    @Override
    public void shutdown(final Producer<?> producer) {
        this.beginShutdown.accept(producer);
    }

    @Override
    public void shutdown(final Consumer<?> consumer) {
        this.endShutdown.accept(consumer);
    }

    @Override
    public void prime() {
        this.scheduler.plug();
        // Set Lifecycle manager for organic halt
        final Collection<? extends LifecycleContext> alive = this.network
                .capability(LifecycleContext.class);
        for (LifecycleContext element : alive) {
            element.lifecycle(this);
        }
        // Ensure relevant nodes can submit commands
        final Collection<? extends ControlContext> commandSources = this.network
                .capability(ControlContext.class);
        for (ControlContext element : commandSources) {
            element.control(this);
        }
        // Ensure relevant nodes can schedule jobs
        final Collection<? extends ScheduleContext> schedulable = this.network
                .capability(ScheduleContext.class);
        for (ScheduleContext element : schedulable) {
            element.schedule(this.scheduler);
        }
        this.scheduler.unplug();
    }

    @Override
    public void pump() throws InterruptedException {
        this.scheduler.awaitTermination();
    }

    @Override
    public Future<PigState> pig() {
        return null;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void submit(final Command command) {
        // TODO: Add support for tracking submitted commands. Proposal:
        // compressed JSON representation of commands.
        this.scheduler.submit(command);
        final Packet packet = new CommandPacket(command);
        final Collection<? extends Inject> injectables = this.network
                .capability(Inject.class);
        for (Inject inject : injectables) {
            inject.inject(packet);
        }
    }

    private class Trigger<T> {
        private final Set<T> space;
        private final Set<T> test;
        private final Command command;

        public Trigger(final Class<T> type, final Command command) {
            this.command = command;
            this.space = generateSpace(DefaultPump.this.network, type);
            final Map<T, Boolean> map = new ConcurrentHashMap<T, Boolean>();
            this.test = Collections.newSetFromMap(map);
        }

        private Set<T> generateSpace(final Network network,
                final Class<T> type) {
            final Collection<? extends T> ts = network.capability(type);
            logger.debug("Trigger type space for {}: {}", type, ts);
            return Collections.unmodifiableSet(new HashSet<>(ts));
        }

        public void accept(final T candidate) {
            if (this.test.contains(candidate)) {
                final String msg = "Candidate already registered";
                throw new IllegalStateException(msg);
            }
            this.test.add(candidate);
            logger.debug("Added candidate to test set: {}", this.test);
            logger.debug("Comparing test set with type space: {}", this.space);
            if (this.test.containsAll(this.space)) {
                final String msg =
                    "All candidates accounted for, submitting command: {}";
                logger.debug(msg, this.command);
                DefaultPump.this.submit(this.command);
            }
        }
    }
}
