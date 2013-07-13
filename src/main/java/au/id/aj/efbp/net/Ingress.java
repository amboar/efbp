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
package au.id.aj.efbp.net;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Ports;
import au.id.aj.efbp.transport.Connection;
import au.id.aj.efbp.transport.Inbound;

/**
 * Nodes implementing this interface accept packets from nodes implementing the
 * Egress interface. This is a low-level plumbing interface, typically users
 * will probably be interested in {@see Consumer} instead, though implementors
 * of consumers are advised to look through static methods available in {@see
 * #Ingress.Utils}.
 */
public interface Ingress<I> {

    /**
     * Fetch packets from all ingress queues. Implementations may be selective;
     * the interface exists to provide flexibility as to what packets should be
     * processed in the current scheduling cycle.
     *
     * @return The collection of packets to be processed in this scheduling
     *         cycle.
     */
    Iterable<Packet<I>> ingress();

    /**
     * Similar to {@see #ingress()}, but enforces a cap on the number of packets
     * to ingress.
     */
    Iterable<Packet<I>> ingress(final int max);

    /**
     * Any packets returned from {@see #ingress()} or {@see #ingress(final int *
     * max)} will be placed into the queue during , providing a network tap-like
     * capability for debugging. The implementation must track all queues passed
     * to inspect, but not queue packets more than once for any provided
     * reference.
     *
     * @param tap
     *            The tap into which to place ingressed packets
     */
    void addIngressTap(final Tap<I> tap);

/**
     * Unregister the provided tap for monitory; the counterpart to
     * {@see #addIngressTap(final Queue<Packet<I>>). If the supplied
     * tap is not registered an IllegalStateException is thrown.
     *
     * @param tap
     *          Once called packets will no longer be placed on the provided
     *          tap.
     *
     * @throws IllegalStateException
     */
    void removeIngressTap(final Tap<I> tap);

    /**
     * A collection of helper methods for implementors of the Consumer
     * interface. This class should be expanded to handle what would otherwise
     * be boilerplate implementations in Ingress definitions and consequently be
     * the go-to class for implementing the Ingress interface.
     */
    public static final class Utils {
        private Utils() {
        }

        public static <I> Collection<Packet<I>> drainFrom(
                final Inbound<I> connection) {
            final Collection<Packet<I>> drainedPackets = new LinkedList<>();
            return drainFrom(connection, drainedPackets);
        }

        public static <I> Collection<Packet<I>> drainFrom(
                final Inbound<I> from, final Collection<Packet<I>> to) {
            for (Packet<I> packet : from) {
                to.add(packet);
            }
            return to;
        }

        /**
         * Returns at most max elements available in the Queue packets, whilst
         * also removing the elements returned.
         *
         * @param connection
         *            The connection from which to drain elements. Note that any
         *            drained elements will no-longer be present in the
         *            connection.
         *
         * @param max
         *            The maximum number of elements to remove from connection
         *
         * @return A collection of all elements removed from connection. The
         *         size of the return collection is bounded by the max
         *         parameter.
         */
        public static <I> Collection<Packet<I>> drainFrom(
                final Inbound<I> connection, final int max) {
            final Collection<Packet<I>> drainedPackets = new LinkedList<>();
            return drainFrom(connection, drainedPackets, max);
        }

        public static <I> Collection<Packet<I>> drainFrom(
                final Inbound<I> from, final Collection<Packet<I>> to,
                final int max) {
            final Iterator<Packet<I>> iter = from.iterator();
            int i = 0;
            while (i++ < max && iter.hasNext()) {
                to.add(iter.next());
                // Inbound implementation expected to be destructive, however
                // call remove just in case, as this code is provided for all
                // implementations as a base-line. If the provided connection
                // is indeed destructive, then remove() should be a no-op.
                iter.remove();
            }
            return to;
        }

        public static <I> Collection<Packet<I>> ingress(final Ports<I> ports) {
            // Naive implementation
            if (ports.isEmpty()) {
                return Collections.emptyList();
            }
            final List<Packet<I>> packets = new LinkedList<>();
            for (Map.Entry<String, Connection<I>> e : ports.entrySet()) {
                drainFrom(e.getValue(), packets);
            }
            return Collections.unmodifiableList(packets);
        }

        public static <I> Collection<Packet<I>> ingress(final Ports<I> ports,
                final int max) {
            // Naive implementation - possible queue starvation
            if (ports.isEmpty()) {
                return Collections.emptyList();
            }
            int mutableMax = max;
            final List<Packet<I>> packets = new LinkedList<>();
            for (Map.Entry<String, Connection<I>> e : ports.entrySet()) {
                Ingress.Utils.drainFrom(e.getValue(), packets, mutableMax);
                mutableMax -= packets.size();
                if (0 == mutableMax) {
                    return packets;
                }
            }
            return Collections.unmodifiableList(packets);
        }
    }
}
