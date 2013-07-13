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
import java.util.HashMap;
import java.util.Map;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.endpoint.Source;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;

/**
 * Provides facilities for constructing a network.
 */
public class NetworkBuilder {
    private final InstanceContent ic;
    private final Lookup lookup;
    private final Network network;
    private final Map<NodeId, Node> map;

    public NetworkBuilder() {
        this.ic = new InstanceContent();
        this.lookup = new AbstractLookup(this.ic);
        this.network = new DefaultNetwork();
        this.map = new HashMap<>();
    }

    public NetworkBuilder addNode(final Node node) {
        this.ic.add(node);
        this.map.put(node.id(), node);
        return this;
    }

    @SuppressWarnings("unchecked")
    public NetworkBuilder connect(final NodeId from, final NodeId to,
            final String port) {
        network.node(from, Source.class).connect(network.node(to, Sink.class),
                port);
        return this;
    }

    public Network get() {
        return this.network;
    }

    private final class DefaultNetwork implements Network {
        @Override
        public Collection<? extends Node> nodes() {
            return NetworkBuilder.this.lookup.lookupAll(Node.class);
        }

        @Override
        public <T extends Node> Collection<? extends T> nodes(
                final Class<T> type) {
            return NetworkBuilder.this.lookup.lookupAll(type);
        }

        @Override
        public Node node(final NodeId nodeId) {
            return NetworkBuilder.this.map.get(nodeId);
        }

        @Override
        public <T extends Node> T node(final NodeId nodeId, final Class<T> type) {
            return type.cast(NetworkBuilder.this.map.get(nodeId));
        }

        @Override
        public <T> Collection<? extends T> capability(Class<T> capability) {
            return NetworkBuilder.this.lookup.lookupAll(capability);
        }
    }
}
