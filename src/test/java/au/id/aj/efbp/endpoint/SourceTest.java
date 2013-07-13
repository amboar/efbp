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
package au.id.aj.efbp.endpoint;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.endpoint.ConnectionRegistry;
import au.id.aj.efbp.endpoint.Connections;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.endpoint.Source;
import au.id.aj.efbp.net.Consumer;
import au.id.aj.efbp.net.DummyConsumer;

@RunWith(JUnit4.class)
public class SourceTest {
    @Test
    public void connectOne() {
        final Connections<Object> connections = new ConnectionRegistry<>();
        final Sink<Object> ingress = new DummyConsumer<>();
        Source.Utils.connect(connections, ingress, DummyConsumer.IN);
        assertTrue(!connections.isEmpty());
        assertEquals(1, connections.size());
    }

    @Test
    public void connectTwo() {
        final Connections<Object> connections = new ConnectionRegistry<>();
        final Consumer<Object> i1 = new DummyConsumer<>();
        Source.Utils.connect(connections, i1, DummyConsumer.IN);
        final Consumer<Object> i2 = new DummyConsumer<>();
        Source.Utils.connect(connections, i2, DummyConsumer.IN);
        assertTrue(!connections.isEmpty());
        assertEquals(2, connections.size());
    }

    @Test
    public void connectBoth() {
        final Connections<Object> connections = new ConnectionRegistry<>();
        final Consumer<Object> ingress = new DummyConsumer<>();
        Source.Utils.connect(connections, ingress, DummyConsumer.IN);
        Source.Utils.connect(connections, ingress, DummyConsumer.DUMMY_IN);
        assertTrue(!connections.isEmpty());
        assertEquals(1, connections.size());
    }
}
