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
package au.id.aj.efbp.command;

import java.io.Serializable;

import au.id.aj.efbp.node.Node;

/**
 * Commands act on nodes in a recordable fashion; this is a pattern straight
 * from the GoF. Command instances are uniquely identified by a CommandId
 * instance, and can be applicable to specific network nodes, classes of nodes
 * or individuals. Information on whether a command is relevant to a node can
 * be gathered through the {@see #isFor(final Node node)} interface.
 *
 * Commands are serializable so that they may be stored and replayed in the
 * future. The implementation should store the relevant NodeId or Class<Node>
 * in a non-transient variable such that the command can be properly replayed.
 *
 * {@link AbstractCommand} is provided for convenience so that the transient
 * nature of the referred Node instance is less likely to be forgotten.
 */
public interface Command extends Serializable
{
    /**
     * Returns the session-unique identifier for the command. The identifier is
     * used to differentiate commands providing the same name/value pair at
     * distinct points in execution.
     *
     * @return The session-unique command identifier.
     */
    CommandId id();

    /**
     * Tests if a Command instance is relevant to a given node.
     *
     * @param node
     *          The node whose relevance to test.
     *
     * @return True if the command is applicable to the supplied Node, false
     * otherwise.
     */
    boolean isFor(final Node node);

    /**
     * Executes the command on the node.
     *
     * @param node
     *          The node on which to execute a command.
     */
    void execute(final Node node);
}
