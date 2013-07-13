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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.transport.Outbound;

@RunWith(JUnit4.class)
public class ConsumerTest {
    @Test
    public void addIngressTap() {
        final Consumer<Object> consumer = new DummyConsumer<>();
        final Tap<Object> tap = new DefaultTap<>();
        consumer.addIngressTap(tap);
        final Packet<Object> p = new DataPacket<>(new Object());
        consumer.port(DummyConsumer.IN).enqueue(p);
        consumer.execute();
        assertTrue(tap.contains(p));
    }

    @Test
    public void removeIngressTap() {
        final Consumer<Object> consumer = new DummyConsumer<>();
        final Tap<Object> tap = new DefaultTap<>();
        consumer.addIngressTap(tap);
        final Outbound<Object> port = consumer.port(DummyConsumer.IN);
        final Packet<Object> p1 = new DataPacket<>(new Object());
        port.enqueue(p1);
        consumer.execute();
        assertTrue(tap.contains(p1));
        consumer.removeIngressTap(tap);
        final Packet<Object> p2 = new DataPacket<>(new Object());
        port.enqueue(p2);
        consumer.execute();
        assertTrue(!tap.contains(p2));
    }

    @Test
    public void ingressMaxOneOfTwo() {
        final Consumer<Object> consumer = new DummyConsumer<>();
        final Tap<Object> tap = new DefaultTap<>();
        consumer.addIngressTap(tap);
        final Collection<Packet<Object>> ps = new LinkedList<>();
        ps.add(new DataPacket<>(new Object()));
        ps.add(new DataPacket<>(new Object()));
        consumer.port(DummyConsumer.IN).enqueue(ps);
        consumer.execute(1);
        assertEquals(1, tap.size());
        tap.clear();
        consumer.execute(1);
        assertEquals(1, tap.size());
        tap.clear();
        consumer.execute(1);
        assertEquals(0, tap.size());
    }
}
