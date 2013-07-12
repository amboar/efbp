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
import java.util.concurrent.Future;

import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.command.CommandPacket;
import au.id.aj.efbp.control.ControlContext;
import au.id.aj.efbp.control.Controller;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.net.Inject;
import au.id.aj.efbp.net.Network;
import au.id.aj.efbp.schedule.ScheduleContext;
import au.id.aj.efbp.schedule.Scheduler;

public class DefaultPump implements Pump, Controller {
    private final Network network;
    private final Scheduler scheduler;

    public DefaultPump(final Network network, final Scheduler scheduler) {
        this.network = network;
        this.scheduler = scheduler;
    }

    @Override
    public void prime() {
        // Ensure relevant nodes can submit commands
        final Collection<? extends ControlContext> commandSources =
            this.network.capability(ControlContext.class);
        for (ControlContext element : commandSources) {
            element.control(this);
        }
        // Ensure relevant nodes can schedule jobs
        final Collection<? extends ScheduleContext> schedulable =
            this.network.capability(ScheduleContext.class);
        for (ScheduleContext element : schedulable) {
            element.schedule(this.scheduler);
        }
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
    public void submit(final Command command)
    {
        // TODO: Add support for tracking submitted commands. Proposal:
        // compressed JSON representation of commands.
        this.scheduler.submit(command);
        final Packet packet = new CommandPacket(command);
        final Collection<? extends Inject> injectables =
            this.network.capability(Inject.class);
        for (Inject inject : injectables) {
            inject.inject(packet);
        }
    }
}
