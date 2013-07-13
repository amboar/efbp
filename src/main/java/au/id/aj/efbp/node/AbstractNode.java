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

import java.util.Collections;
import java.util.Set;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import au.id.aj.efbp.util.Statistics;
import au.id.aj.efbp.util.StopWatch;

public class AbstractNode implements Node
{
    private final NodeId id;
    private final StopWatch stopwatch;
    private final InstanceContent ic;
    private final Lookup lookup;

    protected AbstractNode(final NodeId id, final Object... content)
    {
        this.id = id;
        this.stopwatch = new StopWatch();
        this.ic = Node.Utils.generateInstanceContent(content);
        this.lookup = new AbstractLookup(this.ic);
    }

    @Override
    public final NodeId id() {
        return this.id;
    }

    @Override
    public final Statistics reportStatistics() {
        return this.stopwatch.report();
    }

    @Override
    public final Lookup getLookup() {
        return this.lookup;
    }

    protected void addContent(final Object content)
    {
        this.ic.add(content);
    }

    protected void removeContent(final Object content)
    {
        this.ic.remove(content);
    }

    @Override
    public Set<Node> execute() {
        return Collections.emptySet();
    }

    @Override
    public Set<Node> execute(int max) {
        return execute();
    }
}
