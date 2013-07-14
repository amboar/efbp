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

import au.id.aj.efbp.control.Controller;
import au.id.aj.efbp.node.Node;

/**
 * Exposes the ability to periodically schedule Nodes for execution. Scheduling
 * of nodes is primarily useful for {@see Producer} instances which may need to
 * pull data from an external source periodically.
 */
public interface Scheduler {
    /**
     * Schedules a Node for periodic scheduling on the execution queue.
     *
     * @param node
     *            The node to schedule for execution.
     *
     * @param delay
     *            The amount of time to pass before the first scheduling.
     *
     * @param period
     *            The amount of time between schedulings of node's execution.
     *
     * @return The generated timer task, allowing the caller to cancel if
     *         necessary.
     */
    TimerTask schedule(final Node node, final long delay, final long period,
            final TimeUnit unit);

    /**
     * Immediately schedule node for execution. This is useful for callbacks
     * where data is made available from external services such as ActiveMQ,
     * allowing the node to be scheduled in a consistent manner rather than
     * forcing the calling thread to do processing work.
     *
     * @param node
     *            The Node instance to schedule for execution.
     */
    void schedule(final Node node);

    /**
     * Starts a thread dedicated to performing the provided task.
     */
    void scheduleIo(final Runnable runnable);

    /**
     * Starts a thread dedicated to performing the provided task.
     */
    <T> Future<T> scheduleIo(final Callable<T> callable);

    void awaitTermination() throws InterruptedException;

    public static interface PeriodicTask {
        Node node();

        TimerTask task();
    }
}
