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
package au.id.aj.efbp.node;

import java.util.Set;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import au.id.aj.efbp.util.Statistics;

/**
 * An abstract representation of an entity in a network. This abstraction
 * primarily exists for scheduling purposes; the interface is intentionally
 * tight to discourage direct use.
 *
 * Nodes must be uniquely identified in the network for debugging purposes,
 * where the id of the node can be fetched through the {@see #id()} call.
 */
public interface Node extends Lookup.Provider
{
    /**
     * Transform packets, optionally receiving and sending them depending on
     * the implementation.
     */
    Set<Node> execute();

    /**
     * Process at most max packets in the vein of {@see #execute()}.
     *
     * @param max
     *          The maximum number of packets to receive, transform or send.
     */
    Set<Node> execute(final int max);

    /**
     * Provides the unique identifier for the Node instance.
     *
     * @return The unique identify of the node.
     */
    NodeId id();

    /**
     * As a node instance may process multiple packets through its execute()
     * method the scheduler cannot accurately determine processing time per
     * packet. {@see #reportStatistics()} should be able to provide the
     * scheduler with this information.
     *
     * @return A NodeStatistics instance containing metrics on packet
     * processing.
     */
    Statistics reportStatistics();

    public static final class Utils
    {
        private Utils()
        {
        }

        public static InstanceContent generateInstanceContent(
                final Object... content)
        {
            final InstanceContent ic = new InstanceContent();
            for (Object o : content) {
                ic.add(o);
            }
            return ic;
        }

        public static Lookup generateLookup(final Object... content)
        {
            return new AbstractLookup(generateInstanceContent(content));
        }
    }
}
