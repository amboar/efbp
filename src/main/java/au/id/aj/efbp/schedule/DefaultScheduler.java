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
package au.id.aj.efbp.schedule;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import au.id.aj.efbp.bootstrap.Bootstrap;
import au.id.aj.efbp.bootstrap.HaltCommand;
import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.command.CommandPacket;
import au.id.aj.efbp.command.LongCommandId;
import au.id.aj.efbp.control.Controller;
import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.plug.Pluggable;

public class DefaultScheduler implements Controller, Scheduler, Pluggable {
    private final Bootstrap bootstrap;
    private final Timer timer;
    private final ExecutorService executors;

    public DefaultScheduler(final Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.timer = new Timer();
        this.executors = Executors.newCachedThreadPool();
    }

    @Override
    public TimerTask schedule(final Node node, final long delay,
            final long period, final TimeUnit unit) {
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                DefaultScheduler.this.bootstrap.inject(new DataPacket<>(node));
            }
        };
        final long delayMs = TimeUnit.MILLISECONDS.convert(delay, unit);
        final long periodMs = TimeUnit.MILLISECONDS.convert(period, unit);
        this.timer.scheduleAtFixedRate(task, delayMs, periodMs);
        return task;
    }

    @Override
    public void plug() {
        this.bootstrap.plug();
    }

    @Override
    public void unplug() {
        this.bootstrap.unplug();
    }

    @Override
    public void schedule(final Node node) {
        this.bootstrap.inject(new DataPacket<>(node));
    }

    @Override
    public void scheduleIo(final Runnable runnable) {
        this.executors.submit(runnable);
    }

    @Override
    public <T> Future<T> scheduleIo(final Callable<T> callable) {
        return this.executors.submit(callable);
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        this.bootstrap.awaitTermination();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void submit(final Command command) {
        this.bootstrap.inject(new CommandPacket(command));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void shutdown() {
        this.timer.cancel();
        this.executors.shutdown();
        @SuppressWarnings("rawtypes")
        final Packet halt =
            new CommandPacket(new HaltCommand(LongCommandId.next()));
        this.bootstrap.inject(halt);
    }

    @Override
    public LinearIoContext newLinearIoContext(final Object io) {
        return new LinearIo(io);
    }

    @Override
    public TreeIoContext newTreeIoContext(final Object io) {
        return new TreeIo(io);
    }

    private class LinearIo implements LinearIoContext {
        private Future<?> parent = null;
        private TreeIo context;

        public LinearIo(final Object io) {
            this.context = new TreeIo(io);
        }

        @Override
        public <T> Future<T> schedule(final Callable<T> callable) {
            return schedule(callable, false);
        }

        @Override
        public <T> Future<T> schedule(final Callable<T> callable,
                final boolean force) {
            synchronized (this) {
                final Future<T> newParent =
                    context.schedule(callable, this.parent, force);
                this.parent = newParent;
                return newParent;
            }
        }
    }

    private class TreeIo implements TreeIoContext {
        private final Object io;

        public TreeIo(final Object io) {
            this.io = io;
        }

        @Override
        public <T, U> Future<T> schedule(final Callable<T> callable,
                final Future<U> parent) {
            return schedule(callable, parent, false);
        }

        @Override
        public <T, U> Future<T> schedule(final Callable<T> callable,
                final Future<U> parent, final boolean force) {
            final Callable<T> wrapper = new Callable<T>() {
                @Override
                public T call() throws Exception {
                    if (null != parent) {
                        try {
                            parent.get();
                        } catch (ExecutionException | CancellationException e) {
                            if (!force) {
                                throw e;
                            }
                        }
                    }
                    synchronized (io) {
                        return callable.call();
                    }
                }
            };
            return executors.submit(wrapper);
        }
    }
}
