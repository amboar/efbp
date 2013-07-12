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

import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;

/**
 * Represents a network as a whole, holding the collection of relevant Node
 * instances. The interface allows access to all nodes, classes, or specific
 * instances.
 */
public interface Network
{
    /**
     * Provides access to all nodes in the network.
     *
     * @return All nodes registered in the network.
     */
    Collection<? extends Node> nodes();

    /**
     * Provides access to all nodes in the network of a given type.
     *
     * @param type
     *          The class of nodes to return
     *
     * @return All nodes that are of type T.
     */
    <T extends Node> Collection<? extends T> nodes(final Class<T> type);

    <T> Collection<? extends T> capability(final Class<T> capability);

    /**
     * Fetches a node from the network whose NodeId matches that which was
     * provided.
     *
     * @param nodeId
     *          The NodeId of the node of interest.
     *
     * @return A Node instance if the NodeId exists in the network, null
     * otherwise.
     */
    Node node(final NodeId nodeId);

    /**
     * Fetches a node from the network whose NodeId matches that which was
     * provided and whose type is T. This is essentially a helper method to
     * provide type-safety through centralising any type-system nastiness.
     *
     * @param nodeId
     *          The NodeId of the node of interest.
     *
     * @param type
     *          Defines the return value type.
     *
     * @return A network Node of type T with an ID of nodeId if such a node
     * exists, otherwise null.
     */
    <T extends Node> T node(final NodeId nodeId, final Class<T> type);
}
