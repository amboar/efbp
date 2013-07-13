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
package au.id.aj.efbp.transport;

import java.util.Collection;

import au.id.aj.efbp.data.Packet;

/**
 * An interface for sending packets to a {@see Connection} consumer. Note that
 * for type-safety a varargs enqueue interface has not been specified due to
 * possible heap pollution[1].
 *
 * [1] http://docs.oracle.com/javase/tutorial/java/generics/
 * nonReifiableVarargsType.html#heap_pollution
 */
public interface Outbound<T> {
    /**
     * Makes Packets available to the consumer ({@see Inbound}) of the
     * connection.
     *
     * @param packet
     *            The packet to send across the connection
     */
    void enqueue(final Packet<T> packet);

    /**
     * Makes Packets available to the consumer ({@see Inbound}) of the
     * connection.
     *
     * @param packets
     *            The collection of packets to send across the connection
     */
    void enqueue(final Collection<Packet<T>> packets);
}
