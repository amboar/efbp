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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.transport.ConcurrentConnection;
import au.id.aj.efbp.transport.Connection;
import au.id.aj.efbp.transport.Outbound;

public interface Sink<I> extends Node {
    /**
     * Retrieves a specific ingress queue given its name. Typically {@see
     * #port(String)} will be used by the {@see Egress#connect(Ingress<E, ?>,
     * String)} implementation for connecting Ingress and Egress instances
     * together.
     *
     * @param name
     *            The name of the Ingress queue to return
     *
     * @return The ingress queue
     */
    Outbound<I> port(final String name);

    public static final class Utils {
        private Utils() {
        }

        /**
         * Generate a port map through varargs names.
         *
         * @return names A varargs container of port names
         *
         * @return An unmodifiable port map.
         */
        public static <I> Ports<I> generatePortMap(final String... names) {
            final Map<String, Connection<I>> portMap = new HashMap<>();
            for (final String name : names) {
                portMap.put(name, new ConcurrentConnection<I>());
            }
            return new PortRegistry<I>(Collections.unmodifiableMap(portMap));
        }

        /**
         * Generate a port map through a collection of names.
         *
         * @return names A collection of port names
         *
         * @return An unmodifiable port map.
         */
        public static <I> Ports<I> generatePortMap(
                final Collection<String> names) {
            final Map<String, Connection<I>> portMap = new HashMap<>();
            for (final String name : names) {
                portMap.put(name, new ConcurrentConnection<I>());
            }
            return new PortRegistry<I>(Collections.unmodifiableMap(portMap));
        }
    }
}
