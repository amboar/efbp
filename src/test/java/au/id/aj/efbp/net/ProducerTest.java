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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.schedule.DummyScheduler;

@RunWith(JUnit4.class)
public class ProducerTest {
    @Test
    public void producerIngressSingle() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Packet<Object> packet = new DataPacket<>(new Object());
        producer.inject(packet);
        final Iterator<Packet<Object>> ingressed = producer.ingress()
                .iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packet, ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void producerIngressMultiple() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final List<Packet<Object>> packets = new ArrayList<>(2);
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        producer.inject(packets);
        final Iterator<Packet<Object>> ingressed = producer.ingress()
                .iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(0), ingressed.next());
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(1), ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void producerIngressLimited() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final List<Packet<Object>> packets = new ArrayList<>(2);
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        producer.inject(packets);
        final Iterator<Packet<Object>> ingressed = producer.ingress(1)
                .iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packets.get(0), ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void processSingle() throws ProcessingException {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Packet<Object> packet = new DataPacket<>(new Object());
        final List<Packet<Object>> out = new ArrayList<>(1);
        producer.process(packet, out);
        assertEquals(1, out.size());
        assertEquals(packet.data(), out.get(0).data());
    }

    private <T> Set<T> dataSet(final Collection<Packet<T>> packets) {
        final Set<T> set = new HashSet<>();
        for (Packet<T> p : packets) {
            set.add(p.data());
        }
        return set;
    }

    @Test
    public void processMultiple() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Collection<Packet<Object>> packets = new LinkedList<>();
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        final Collection<Packet<Object>> processed = producer.process(packets);
        assertEquals(dataSet(packets), dataSet(processed));
    }

    @Test
    public void producerEgressSingle() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Consumer<Object> consumer = new DummyConsumer<>();
        producer.connect(consumer, DummyConsumer.IN);
        final Packet<Object> packet = new DataPacket<>(new Object());
        final Collection<Node> execute = producer.egress(packet);
        assertTrue(execute.contains(consumer));
        final Iterator<Packet<Object>> ingressed = consumer.ingress()
                .iterator();
        assertTrue(ingressed.hasNext());
        assertEquals(packet, ingressed.next());
        assertTrue(!ingressed.hasNext());
    }

    @Test
    public void producerEgressMultiple() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Consumer<Object> consumer = new DummyConsumer<>();
        producer.connect(consumer, DummyConsumer.IN);
        final List<Packet<Object>> packets = new ArrayList<>(2);
        packets.add(new DataPacket<>(new Object()));
        packets.add(new DataPacket<>(new Object()));
        final Collection<Node> execute = producer.egress(packets);
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
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Tap<Object> tap = new DefaultTap<>();
        producer.addIngressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        producer.inject(packet);
        producer.execute();
        assertEquals(1, tap.size());
        assertEquals(packet.data(), tap.poll().data());
    }

    @Test
    public void removeIngressTap() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Tap<Object> tap = new DefaultTap<>();
        producer.addIngressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        producer.inject(packet);
        producer.execute();
        assertEquals(1, tap.size());
        assertEquals(packet.data(), tap.poll().data());
        tap.clear();
        producer.removeIngressTap(tap);
        producer.inject(packet);
        producer.execute();
        assertTrue(tap.isEmpty());
    }

    @Test
    public void addEgressTap() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Tap<Object> tap = new DefaultTap<>();
        producer.addEgressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        producer.inject(packet);
        producer.execute();
        System.out.println(tap.toString());
        assertEquals(1, tap.size());
        assertEquals(packet.data(), tap.poll().data());
    }

    @Test
    public void removeEgressTap() {
        final Producer<Object> producer = new DummyProducer<>();
        producer.schedule(new DummyScheduler());
        final Tap<Object> tap = new DefaultTap<>();
        producer.addEgressTap(tap);
        final Packet<Object> packet = new DataPacket<>(new Object());
        producer.inject(packet);
        producer.execute();
        assertEquals(1, tap.size());
        assertEquals(packet.data(), tap.poll().data());
        tap.clear();
        producer.removeEgressTap(tap);
        producer.inject(packet);
        producer.execute();
        assertTrue(tap.isEmpty());
    }
}
