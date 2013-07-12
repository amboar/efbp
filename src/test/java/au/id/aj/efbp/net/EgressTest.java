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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.ConnectionRegistry;
import au.id.aj.efbp.endpoint.Connections;
import au.id.aj.efbp.endpoint.Source;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.transport.Connection;
import au.id.aj.efbp.transport.Inbound;
import au.id.aj.efbp.transport.Outbound;

@RunWith(JUnit4.class)
public class EgressTest
{
    private int inboundSize(final Inbound<?> inbound)
    {
        int i = 0;
        for (@SuppressWarnings("unused") Packet<?> packet : inbound) {
            i++;
        }
        return i;
    }

    @Test
    public void egressOneToSingle()
    {
        final Connections<Object> connections = new ConnectionRegistry<>();
        final Consumer<Object> ingress = new DummyConsumer<>();
        Source.Utils.connect(connections, ingress, DummyConsumer.IN);
        final Packet<Object> packet = new DataPacket<Object>(new Object());
        final Set<Node> next = Egress.Utils.egress(connections, packet);
        final Outbound<Object> outbound = ingress.port(DummyConsumer.IN);
        assertTrue(outbound instanceof Connection);
        final Connection<Object> connection = (Connection<Object>) outbound;
        assertEquals(1, inboundSize(connection));
        assertTrue(next.contains(ingress));
    }

    @Test
    public void egressMultipleToSingle()
    {
        final Consumer<Object> ingress = new DummyConsumer<>();
        final Producer<Object> egress = new DummyProducer<>();
        egress.connect(ingress, DummyConsumer.IN);
        final List<Packet<Object>> packets = new LinkedList<>();
        {
            for (int i = 0; i < 2; i++) {
                final Packet<Object> packet =
                    new DataPacket<Object>(new Object());
                packets.add(packet);
            }
        }
        assertEquals(2, packets.size());
        final Set<Node> next = egress.egress(packets);
        final Outbound<Object> outbound = ingress.port(DummyConsumer.IN);
        assertTrue(outbound instanceof Connection);
        final Connection<Object> connection = (Connection<Object>) outbound;
        assertEquals(2, inboundSize(connection));
        assertTrue(next.contains(ingress));
    }
}
