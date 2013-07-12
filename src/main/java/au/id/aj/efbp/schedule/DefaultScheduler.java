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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import au.id.aj.efbp.bootstrap.Bootstrap;
import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.command.CommandPacket;
import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.node.Node;

public class DefaultScheduler implements Scheduler {
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
    public void submit(Command command) {
        this.bootstrap.inject(new CommandPacket(command));
    }
}
