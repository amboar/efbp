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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.transport.Outbound;

@RunWith(JUnit4.class)
public class WorkerTest {
    @Test
    public void transformFilledCollection() {
        final Process<Object, Object> worker = new DummyWorker();
        final Collection<Packet<Object>> source = new LinkedList<>();
        source.add(new DataPacket<Object>(new Object()));
        final Collection<Packet<Object>> transformed = Process.Utils.process(
                worker, source);
        assertTrue(transformed.containsAll(source));
    }

    @Test
    public void workerIngressSingle() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Outbound<Object> connection = worker.port(DummyWorker.IN);
        final Packet<Object> packet = new DataPacket<>(new Object());
        connection.enqueue(packet);
        final Iterator<Packet<Object>> ingressed = worker.ingress().iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packet, ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void workerIngressMultiple() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Outbound<Object> connection = worker.port(DummyWorker.IN);
        final List<Packet<Object>> packets = new ArrayList<>(2);
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        connection.enqueue(packets);
        final Iterator<Packet<Object>> ingressed = worker.ingress().iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(0), ingressed.next());
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(1), ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void workerIngressLimited() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Outbound<Object> connection = worker.port(DummyWorker.IN);
        final List<Packet<Object>> packets = new ArrayList<>(2);
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        connection.enqueue(packets);
        final Iterator<Packet<Object>> ingressed = worker.ingress(1).iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(0), ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void processSingle() throws ProcessingException {
        final Worker<Object, Object> worker = new DummyWorker();
        final Packet<Object> packet = new DataPacket<>(new Object());
        assertEquals(packet, worker.process(packet));
    }

    @Test
    public void processMultiple() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Collection<Packet<Object>> packets = new LinkedList<>();
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        final Collection<Packet<Object>> processed = worker.process(packets);
        assertEquals(packets, processed);
    }

    @Test
    public void workerEgressSingle() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Consumer<Object> consumer = new DummyConsumer<>();
        worker.connect(consumer, DummyConsumer.IN);
        final Packet<Object> packet = new DataPacket<>(new Object());
        final Collection<Node> execute = worker.egress(packet);
        assertTrue(execute.contains(consumer));
        final Iterator<Packet<Object>> ingressed = consumer.ingress()
                .iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packet, ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void workerEgressMultiple() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Consumer<Object> consumer = new DummyConsumer<>();
        worker.connect(consumer, DummyConsumer.IN);
        final List<Packet<Object>> packets = new ArrayList<>(2);
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        final Collection<Node> execute = worker.egress(packets);
        assertTrue(execute.contains(consumer));
        final Iterator<Packet<Object>> ingressed = consumer.ingress()
                .iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(0), ingressed.next());
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(1), ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void addIngressTap() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Outbound<Object> connection = worker.port(DummyWorker.IN);
        final Tap<Object> tap = new DefaultTap<>();
        worker.addIngressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        connection.enqueue(packet);
        worker.execute();
        assertEquals(1, tap.size());
        assertTrue(tap.contains(packet));
    }

    @Test
    public void removeIngressTap() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Outbound<Object> connection = worker.port(DummyWorker.IN);
        final Tap<Object> tap = new DefaultTap<>();
        worker.addIngressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        connection.enqueue(packet);
        worker.execute();
        assertEquals(1, tap.size());
        assertTrue(tap.contains(packet));
        tap.clear();
        worker.removeIngressTap(tap);
        connection.enqueue(packet);
        worker.execute();
        assertTrue(tap.isEmpty());
    }

    @Test
    public void addEgressTap() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Outbound<Object> connection = worker.port(DummyWorker.IN);
        final Tap<Object> tap = new DefaultTap<>();
        worker.addEgressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        connection.enqueue(packet);
        worker.execute();
        System.out.println(tap.toString());
        assertEquals(1, tap.size());
        assertTrue(tap.contains(packet));
    }

    @Test
    public void removeEgressTap() {
        final Worker<Object, Object> worker = new DummyWorker();
        final Outbound<Object> connection = worker.port(DummyWorker.IN);
        final Tap<Object> tap = new DefaultTap<>();
        worker.addEgressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        connection.enqueue(packet);
        worker.execute();
        assertEquals(1, tap.size());
        assertTrue(tap.contains(packet));
        tap.clear();
        worker.removeEgressTap(tap);
        connection.enqueue(packet);
        worker.execute();
        assertTrue(tap.isEmpty());
    }
}
