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
import java.util.Set;

import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Ports;
import au.id.aj.efbp.node.AbstractNode;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.transport.Outbound;

public abstract class AbstractConsumer<I> extends AbstractNode implements
        Consumer<I> {

    private final Ports<I> ports;
    private final Taps<I> ingressTaps;

    protected AbstractConsumer(final NodeId id, final Ports<I> ports,
            final Object... content) {
        super(id, content);
        this.ports = ports;
        this.ingressTaps = new TapRegistry<>();
    }

    @Override
    public final void addIngressTap(final Tap<I> tap) {
        this.ingressTaps.add(tap);
    }

    @Override
    public final void removeIngressTap(final Tap<I> tap) {
        this.ingressTaps.remove(tap);
    }

    protected final Taps<I> ingressTaps() {
        return this.ingressTaps;
    }

    protected final Ports<I> ports() {
        return this.ports;
    }

    @Override
    public final Outbound<I> port(String name) {
        return this.ports.get(name);
    }

    @Override
    public Set<Node> execute() {
        process(ingress());
        return Collections.emptySet();
    }

    @Override
    public Set<Node> execute(int max) {
        process(ingress(max));
        return Collections.emptySet();
    }

    @Override
    public Iterable<Packet<I>> ingress() {
        final Collection<Packet<I>> packets = Ingress.Utils.ingress(this.ports);
        Taps.Utils.acquiesce(this.ingressTaps, packets);
        return packets;
    }

    @Override
    public Iterable<Packet<I>> ingress(final int max) {
        final Collection<Packet<I>> packets = Ingress.Utils.ingress(this.ports,
                max);
        Taps.Utils.acquiesce(this.ingressTaps, packets);
        return packets;
    }

    @Override
    public Collection<Packet<Void>> process(Iterable<Packet<I>> packets) {
        return Consumer.Utils.process(this, packets);
    }
}
