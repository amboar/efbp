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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Ports;
import au.id.aj.efbp.transport.Connection;
import au.id.aj.efbp.util.Bound;
import au.id.aj.efbp.util.CoiledIterator;
import au.id.aj.efbp.util.CyclicIterator;
import au.id.aj.efbp.util.Wrap;

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

        public static <I> Collection<I> drainFrom(final Iterable<I> connection) {
            final Collection<I> drainedPackets = new LinkedList<>();
            return drainFrom(connection, drainedPackets);
        }

        public static <I> Collection<I> drainFrom(final Iterable<I> from,
                final Collection<I> to) {
            for (I elem : from) {
                to.add(elem);
            }
            return to;
        }

        public static <I> Iterable<Packet<I>> ingress(final Ports<I> ports) {
            switch (ports.size()) {
            case 0:
                return Collections.<Packet<I>> emptyList();
            case 1:
                return ports.values().iterator().next();
            default:
                final Collection<Iterator<Packet<I>>> iterators = new ArrayList<>(
                        ports.size());
                for (Connection<I> c : ports.values()) {
                    iterators.add(c.iterator());
                }
                return new Wrap<>(new CoiledIterator<>(new CyclicIterator<>(iterators)));
            }
        }

        public static <I> Iterable<Packet<I>> ingress(final Ports<I> ports,
                final int max) {
            return new Bound<>(ingress(ports), max);
        }

        public static <I> Collection<Packet<I>> ingressCopy(final Ports<I> ports) {
            final List<Packet<I>> packets = new LinkedList<>();
            drainFrom(ingress(ports), packets);
            return Collections.unmodifiableList(packets);
        }

        public static <I> Collection<Packet<I>> ingressCopy(
                final Ports<I> ports, final int max) {
            final List<Packet<I>> packets = new LinkedList<>();
            drainFrom(ingress(ports, max), packets);
            return Collections.unmodifiableList(packets);
        }
    }
}
