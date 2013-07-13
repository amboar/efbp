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
package au.id.aj.efbp.util;

import java.util.concurrent.TimeUnit;

/**
 * Records processing statistics of packets on nodes.
 */
public interface Statistics {
    /**
     * The mean processing time of a given packet.
     *
     * @param unit
     *            The time unit of the returned value.
     *
     * @return The mean processing time.
     */
    double mean(final TimeUnit unit);

    /**
     * The standard deviation of the processing time across the number of
     * packets processed.
     *
     * @param unit
     *            The time unit of the returned value.
     *
     * @return The standard deviation of packet processing time.
     */
    double stdev(final TimeUnit unit);

    /**
     * @return The number of packets across which the statistics were generated.
     */
    int elements();
}
