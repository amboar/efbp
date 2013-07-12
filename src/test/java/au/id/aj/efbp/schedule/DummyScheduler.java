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

import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.schedule.Scheduler;

public class DummyScheduler implements Scheduler {
    @Override
    public TimerTask schedule(Node node, long delay, long period, TimeUnit unit) {
        return null;
    }

    @Override
    public void schedule(Node node) {
    }

    @Override
    public void scheduleIo(Runnable runnable) {
    }

    @Override
    public <T> Future<T> scheduleIo(Callable<T> callable) {
        return null;
    }

    @Override
    public void awaitTermination() throws InterruptedException {
    }

    @Override
    public void submit(Command command) {
    }
}
