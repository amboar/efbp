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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.transport.Connection;

@RunWith(JUnit4.class)
public class SinkTest
{
    @Test
    public void generateOnePortVarargs()
    {
        final String name = "IN";
        final Map<String, Connection<Object>> portMap =
            Sink.Utils.<Object>generatePortMap(name);
        assertTrue(portMap.keySet().contains(name));
        assertEquals(1, portMap.keySet().size());
    }

    @Test
    public void generateTwoPortsVarargs()
    {
        final String in1 = "IN1";
        final String in2 = "IN2";
        final Map<String, Connection<Object>> portMap =
            Sink.Utils.<Object>generatePortMap(in1, in2);
        assertTrue(portMap.keySet().contains(in1));
        assertTrue(portMap.keySet().contains(in2));
        assertEquals(2, portMap.keySet().size());
    }

    @Test
    public void generateOnePortCollection()
    {
        final String name = "IN";
        final Map<String, Connection<Object>> portMap =
            Sink.Utils.<Object>generatePortMap(Collections.singleton(name));
        assertTrue(portMap.keySet().contains(name));
        assertEquals(1, portMap.keySet().size());

    }

    @Test
    public void generateTwoPortsCollection()
    {
        final Set<String> names = new HashSet<>();
        names.add("IN1");
        names.add("IN2");
        final Map<String, Connection<Object>> portMap =
            Sink.Utils.<Object>generatePortMap(names);
        assertTrue(portMap.keySet().containsAll(names));
        assertEquals(2, portMap.keySet().size());
    }
}
