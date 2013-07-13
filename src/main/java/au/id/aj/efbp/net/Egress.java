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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Connections;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.transport.Outbound;

/**
 * Nodes implementing this interface are able to pass packets to nodes
 * implementing the Ingress interface. This is a low-level plumbing interface,
 * typical users will probably be interested in the {@see Producer} interface
 * instead.
 */
public interface Egress<E> {
    /**
     * Egresses the packet to Ingress nodes registered via {@see
     * #connect(Ingress<E>, String)}. Packet egress may be selective, i.e. not
     * all registered Ingress instances may receive the packet. As such, the set
     * of nodes to which the Packet<E> instance was egressed must be provided as
     * the return value, which will in-turn instruct the scheduler which nodes
     * are available for execution.
     *
     * @param packet
     *            The packet to emit to registered Ingress<E> instances.
     *
     * @return The set of nodes whose ingress queues were populated with the
     *         provided Packet<E>.
     */
    Set<Node> egress(final Packet<E> packet);

    /**
     * Egresses multiple packets to registered Ingress instances. {@see
     * #egress(Packet<E>)} contains a description of the requirements of the
     * interface.
     */
    Set<Node> egress(final Collection<Packet<E>> packets);

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
    void addEgressTap(final Tap<E> tap);

/**
     * Unregister the provided tap for monitory; the counterpart to
     * {@see #addEgressTap(final Queue<Packet<E>>). If the supplied
     * tap is not registered an IllegalStateException is thrown.
     *
     * @param tap
     *          Once called packets will no longer be placed on the provided
     *          tap.
     *
     * @throws IllegalStateException
     */
    void removeEgressTap(final Tap<E> tap);

    /**
     * A collection of helper methods for implementors of the Producer
     * interface. This class should be expanded to handle what would otherwise
     * be boilerplate implementations in Egress definitions and consequently be
     * the go-to class for implementing the Egress interface.
     */
    public static final class Utils {
        private Utils() {
        }

        /**
         * Egresses a packet to all Ingress instances and queues recorded in
         * registry.
         *
         * @param registry
         *            The map of Ingress instances to queues maintained by
         *            {@see #connect(final Connections<E> registry, final
         *            Ingress<E> remote, final String name)}
         *
         * @param packet
         *            The packet to send to all registered Outbound connections.
         *
         * @return The set of Ingress instances which must now be executed to
         *         process the egressed packet.
         */
        public static <E> Set<Node> egress(final Connections<E> registry,
                final Packet<E> packet) {
            final Set<Node> triggered = new HashSet<>();
            for (Map.Entry<Sink<E>, Collection<Outbound<E>>> e : registry
                    .entrySet()) {
                for (Outbound<E> o : e.getValue()) {
                    o.enqueue(packet);
                    triggered.add(e.getKey());
                }
            }
            return Collections.unmodifiableSet(triggered);
        }

        /**
         * Egresses a collection of packets via the provided Egress
         * implementation. The purpose of this helper is to manage the returned
         * node set across multiple {@see Egress.egress(final
         * Collection<Outbound<E>> packet)} invocations.
         *
         * @param egressor
         *            An Egress implementation, upon which {@see
         *            Egress.egress(final Collection<Packet<E>> packet)} will be
         *            invoked.
         *
         * @param packets
         *            The collection of packets to egress the node.
         *
         * @return The set of Ingress instances which must now be executed to
         *         process the egressed packet.
         */
        public static <E> Set<Node> egress(final Egress<E> egressor,
                final Iterable<Packet<E>> packets) {
            final Set<Node> triggered = new HashSet<>();
            for (Packet<E> packet : packets) {
                triggered.addAll(egressor.egress(packet));
            }
            return Collections.unmodifiableSet(triggered);
        }

        public static <E> Set<Node> egress(final Connections<E> registry,
                final Iterable<Packet<E>> packets) {
            final Set<Node> triggered = new HashSet<>();
            for (Packet<E> packet : packets) {
                triggered.addAll(egress(registry, packet));
            }
            return Collections.unmodifiableSet(triggered);
        }
    }
}
