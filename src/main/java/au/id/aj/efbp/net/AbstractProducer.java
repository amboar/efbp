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

import au.id.aj.efbp.control.Controller;
import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.ConnectionRegistry;
import au.id.aj.efbp.endpoint.Connections;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.endpoint.Source;
import au.id.aj.efbp.lifecycle.Lifecycle;
import au.id.aj.efbp.lifecycle.LifecycleContext;
import au.id.aj.efbp.node.AbstractNode;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.schedule.Scheduler;
import au.id.aj.efbp.transport.ConcurrentConnection;
import au.id.aj.efbp.transport.Connection;

public abstract class AbstractProducer<E> extends AbstractNode implements
        Producer<E>, LifecycleContext, Shutdown {

    private final Connection<E> inbound;
    private final Taps<E> ingressTaps;
    private final Taps<E> egressTaps;
    private final Connections<E> connections;

    protected AbstractProducer(final NodeId id, final Object... content) {
        super(id, content);
        this.inbound = new ConcurrentConnection<>();
        this.ingressTaps = new TapRegistry<>();
        this.egressTaps = new TapRegistry<>();
        this.connections = new ConnectionRegistry<>();
    }

    @Override
    public final void connect(Sink<E> sink, String name) {
        Source.Utils.connect(this.connections, sink, name);
    }

    @Override
    public final void inject(final Packet<E> packet) {
        this.inbound.enqueue(packet);
        getLookup().lookup(Scheduler.class).schedule(this);
    }

    @Override
    public final void inject(final Collection<Packet<E>> packets) {
        this.inbound.enqueue(packets);
        getLookup().lookup(Scheduler.class).schedule(this);
    }

    @Override
    public final void addIngressTap(final Tap<E> tap) {
        this.ingressTaps.add(tap);
    }

    @Override
    public final void removeIngressTap(final Tap<E> tap) {
        this.ingressTaps.remove(tap);
    }

    @Override
    public final void addEgressTap(final Tap<E> tap) {
        this.egressTaps.add(tap);
    }

    @Override
    public final void removeEgressTap(final Tap<E> tap) {
        this.egressTaps.remove(tap);
    }

    @Override
    public final void control(final Controller controller) {
        addContent(controller);
    }

    @Override
    public final void lifecycle(final Lifecycle lifecycle) {
        addContent(lifecycle);
    }

    @Override
    public Set<Node> execute() {
        return egress(process(ingress()));
    }

    @Override
    public Set<Node> execute(int max) {
        return egress(process(ingress(max)));
    }

    @Override
    public Iterable<Packet<E>> ingress() {
        if (this.ingressTaps.isEmpty()) {
            return this.inbound;
        }
        final Collection<Packet<E>> packets = Ingress.Utils
                .drainFrom(this.inbound);
        Taps.Utils.acquiesce(this.ingressTaps, packets);
        return packets;
    }

    @Override
    public Iterable<Packet<E>> ingress(int max) {
        final Collection<Packet<E>> packets = Ingress.Utils.drainFrom(
                this.inbound, max);
        if (!this.ingressTaps.isEmpty()) {
            Taps.Utils.acquiesce(this.ingressTaps, packets);
        }
        return packets;
    }

    @Override
    public Collection<Packet<E>> process(final Iterable<Packet<E>> packets) {
        return Process.Utils.process(this, packets);
    }

    @Override
    public void process(final Packet<E> inbound,
            final Collection<Packet<E>> outbound) throws ProcessingException {
        if (Packet.Type.COMMAND.equals(inbound.type())) {
            inbound.command(this);
            outbound.add(inbound);
        } else {
            assert Packet.Type.DATA.equals(inbound.type());
            process(inbound.data(), outbound);
        }
    }

    protected void process(final E data,
            final Collection<Packet<E>> outbound) throws ProcessingException {
        outbound.add(new DataPacket<E>(data));
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

    @Override
    public void schedule(final Scheduler scheduler) {
        addContent(scheduler);
    }

    @Override
    public void shutdown() {
        getLookup().lookup(Lifecycle.class).shutdown(this);
    }
}
