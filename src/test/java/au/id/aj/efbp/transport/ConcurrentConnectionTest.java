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
package au.id.aj.efbp.transport;

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
import au.id.aj.efbp.transport.Outbound;

@RunWith(JUnit4.class)
public class ConcurrentConnectionTest
{
    @Test
    public void enqueueSingle()
    {
        final Outbound<Object> outbound = new ConcurrentConnection<>();
        outbound.enqueue(new DataPacket<Object>(new Object()));
    }

    @Test
    public void enqueueCollection()
    {
        final Outbound<Object> outbound = new ConcurrentConnection<>();
        final Collection<Packet<Object>> collection = new LinkedList<>();
        collection.add(new DataPacket<Object>(new Object()));
        collection.add(new DataPacket<Object>(new Object()));
        outbound.enqueue(collection);
    }

    @Test
    public void consumeSingle()
    {
        final Connection<Object> connection = new ConcurrentConnection<>();
        final Packet<Object> p = new DataPacket<>(new Object());
        connection.enqueue(p);
        final Iterator<Packet<Object>> populated = connection.iterator();
        assertTrue(populated.hasNext());
        assertEquals(p, populated.next());
        assertTrue(!populated.hasNext());
        final Iterator<Packet<Object>> empty = connection.iterator();
        assertTrue(!empty.hasNext());
    }

    @Test
    public void consumeMultiple()
    {
        final Connection<Object> connection = new ConcurrentConnection<>();
        final Collection<Packet<Object>> collection = new LinkedList<>();
        collection.add(new DataPacket<Object>(new Object()));
        collection.add(new DataPacket<Object>(new Object()));
        connection.enqueue(collection);
        final Iterator<Packet<Object>> populated = connection.iterator();
        assertTrue(populated.hasNext());
        populated.next();
        assertTrue(populated.hasNext());
        populated.next();
        assertTrue(!populated.hasNext());
        final Iterator<Packet<Object>> empty = connection.iterator();
        assertTrue(!empty.hasNext());
    }
}
