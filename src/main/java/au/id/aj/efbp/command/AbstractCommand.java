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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;

@SuppressWarnings("serial")
public abstract class AbstractCommand implements Command {
    private final CommandId id;
    private final Set<NodeId> nodeIds;
    private final Class<?> target;

    public AbstractCommand(final CommandId id, final NodeId... nodeIds) {
        this.id = id;
        this.nodeIds = new HashSet<>(Arrays.asList(nodeIds));
        this.target = null;
    }

    public AbstractCommand(final CommandId id, final Class<?> target) {
        this.id = id;
        this.nodeIds = Collections.emptySet();
        this.target = target;
    }

    @Override
    public final CommandId id() {
        return this.id;
    }

    @Override
    public final boolean isFor(Node node) {
        if (this.nodeIds.isEmpty() && null == this.target) {
            return true;
        }
        if (this.nodeIds.isEmpty()) {
            assert null != this.target;
            return this.target.isAssignableFrom(node.getClass());
        }
        return this.nodeIds.contains(node.id());
    }
}
