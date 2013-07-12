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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.transport.ConcurrentConnection;
import au.id.aj.efbp.transport.Connection;

@RunWith(JUnit4.class)
public class IngressTest
{
    private <T> Connection<T> newFilledConnection(@SuppressWarnings("unchecked") final T... ts)
    {
        final Connection<T> connection = new ConcurrentConnection<T>();
        for (T t : ts) {
            final Packet<T> packet = new DataPacket<T>(t);
            connection.enqueue(packet);
        }
        return connection;
    }

    @Test
    public void drainFromAll()
    {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Connection<Object> source = newFilledConnection(o1, o2);
        final Collection<Packet<Object>> drained =
            Ingress.Utils.drainFrom(source);
        assertTrue(!source.iterator().hasNext());
        assertEquals(2, drained.size());
        final Iterator<Packet<Object>> iter = drained.iterator();
        assertTrue(o1.equals(iter.next().data()));
        assertTrue(o2.equals(iter.next().data()));
        assertTrue(!iter.hasNext());
    }

    @Test
    public void drainFromMaxLess()
    {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Connection<Object> source = newFilledConnection(o1, o2);
        final Collection<Packet<Object>> drained =
            Ingress.Utils.drainFrom(source, 1);
        assertTrue(source.iterator().hasNext());
        assertEquals(1, drained.size());
        final Iterator<Packet<Object>> iter = drained.iterator();
        assertTrue(o1.equals(iter.next().data()));
        assertTrue(!iter.hasNext());
    }

    @Test
    public void drainFromMaxEqual()
    {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Connection<Object> source = newFilledConnection(o1, o2);
        final Collection<Packet<Object>> drained =
            Ingress.Utils.drainFrom(source, 2);
        assertTrue(!source.iterator().hasNext());
        assertEquals(2, drained.size());
        final Iterator<Packet<Object>> iter = drained.iterator();
        assertTrue(o1.equals(iter.next().data()));
        assertTrue(o2.equals(iter.next().data()));
        assertTrue(!iter.hasNext());
    }

    @Test
    public void drainFromMaxGreater()
    {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Connection<Object> source = newFilledConnection(o1, o2);
        final Collection<Packet<Object>> drained =
            Ingress.Utils.drainFrom(source, 3);
        assertTrue(!source.iterator().hasNext());
        assertEquals(2, drained.size());
        final Iterator<Packet<Object>> iter = drained.iterator();
        assertTrue(o1.equals(iter.next().data()));
        assertTrue(o2.equals(iter.next().data()));
        assertTrue(!iter.hasNext());
    }

    @SuppressWarnings("unchecked")
	@Test
    public void acquiesceOneInspector()
    {
        final Collection<Packet<Object>> packets = new LinkedList<>();
        packets.add(new DataPacket<Object>(new Object()));
        packets.add(new DataPacket<Object>(new Object()));
        final Taps<Object> taps = new TapRegistry<>();
        taps.add(new DefaultTap<Object>());
        Taps.Utils.acquiesce(taps, packets);
        assertTrue(taps.toArray(new Tap[taps.size()])[0].containsAll(packets));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void acquiesceTwoInspectors()
    {
        final Collection<Packet<Object>> packets = new LinkedList<>();
        packets.add(new DataPacket<Object>(new Object()));
        packets.add(new DataPacket<Object>(new Object()));
        final Taps<Object> taps = new TapRegistry<>();
        taps.add(new DefaultTap<Object>());
        taps.add(new DefaultTap<Object>());
        assertEquals(2, taps.size());
        Taps.Utils.acquiesce(taps, packets);
        final Tap[] aTaps = taps.toArray(new Tap[taps.size()]);
        assertEquals(2, aTaps.length);
        assertTrue(aTaps[0].containsAll(packets));
        assertTrue(aTaps[1].containsAll(packets));
    }
}
