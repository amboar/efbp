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
package au.id.aj.efbp.endpoint;

import java.util.HashSet;

import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.transport.Outbound;

public interface Source<E> extends Node
{
    /**
     * Register inbound queue of sink as an outbound queue of the Source
     * instance. Sink instances can have multiple named inbound queues,
     * therefore the name parameter exists to instruct the particular queue
     * required.
     *
     * @param remote
     *          Packets egressing this node will ingress the provided node.
     *
     * @param name
     *          The name of the queue on the Sink instance to which packets
     *          should egress.
     */
    void connect(final Sink<E> sink, final String name);

    public static final class Utils
    {
        private Utils()
        {
        }

        /**
         * Manages the registration of Ingress ports on the Egress
         * implementation. Each Egress implementation should maintain a
         * Connections instance internally, whose values will be iterated
         * during execution of the {@see #egress(final Packet<E> packet)} method.
         */
        public static <E> void connect(final Connections<E> registry,
                final Sink<E> remote, final String name)
        {
            if (!registry.containsKey(remote)) {
                registry.put(remote, new HashSet<Outbound<E>>());
            }
            registry.get(remote).add(remote.port(name));
        }
    }
}
