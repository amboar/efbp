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

import java.util.concurrent.Future;

public interface Pump
{
    /**
     * Configures the network for execution, including scheduling timers that
     * prime the scheduling collection.
     */
    void prime();

    /**
     * {@see #pump()} moves packets through the network in an event-based
     * manner by consuming nodes from a collection and calling execute() on
     * each. Nodes are placed in the scheduling queue by their upstreams once
     * they have egressed a packet.
     */
    void pump() throws InterruptedException;

    /**
     * Perform an operation similar to the job of a Pipeline Inspection Gauge.
     * Test if a tag flows from all producers to all consumers in the network.
     *
     * @return A Future representing the operation that will result in a
     * PigState enumeration value being made available.
     */
    Future<PigState> pig();

    /**
     * An enumeration representing the network health.
     */
    public enum PigState { PROGRESSING, FAILED, PASSED }
}
