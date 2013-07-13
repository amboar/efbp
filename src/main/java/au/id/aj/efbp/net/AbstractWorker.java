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
import java.util.Set;

import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.ConnectionRegistry;
import au.id.aj.efbp.endpoint.Connections;
import au.id.aj.efbp.endpoint.Ports;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.endpoint.Source;
import au.id.aj.efbp.node.AbstractNode;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.transport.Outbound;

public abstract class AbstractWorker<I, E> extends AbstractNode implements
        Worker<I, E> {
    private final Ports<I> ports;
    private final Taps<I> ingressTaps;
    private final Taps<E> egressTaps;
    private final Connections<E> connections;

    protected AbstractWorker(final NodeId id, final Ports<I> ports,
            final Object... content) {
        super(id, content);
        this.ports = ports;
        this.ingressTaps = new TapRegistry<>();
        this.egressTaps = new TapRegistry<>();
        this.connections = new ConnectionRegistry<>();
    }

    protected final Ports<I> ports() {
        return this.ports;
    }

    @Override
    public final Outbound<I> port(final String name) {
        return this.ports.get(name);
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

    @Override
    public final void addEgressTap(final Tap<E> tap) {
        this.egressTaps.add(tap);
    }

    @Override
    public final void removeEgressTap(final Tap<E> tap) {
        this.egressTaps.remove(tap);
    }

    protected final Taps<E> egressTaps() {
        return this.egressTaps;
    }

    @Override
    public final void connect(final Sink<E> remote, final String name) {
        Source.Utils.connect(this.connections, remote, name);
    }

    protected final Connections<E> connections() {
        return this.connections;
    }

    @Override
    public Set<Node> execute() {
        return egress(process(ingress()));
    }

    @Override
    public Set<Node> execute(final int max) {
        return egress(process(ingress(max)));
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
    public Collection<Packet<E>> process(Iterable<Packet<I>> packets) {
        return Process.Utils.process(this, packets);
    }

    @Override
    public Set<Node> egress(final Packet<E> packet) {
        Taps.Utils.acquiesce(this.egressTaps, packet);
        return Egress.Utils.egress(this.connections, packet);
    }

    @Override
    public Set<Node> egress(final Collection<Packet<E>> packets) {
        Taps.Utils.acquiesce(this.egressTaps, packets);
        return Egress.Utils.egress(this.connections, packets);
    }
}
