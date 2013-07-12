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

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.transport.Connection;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ConnectionTest
{
    @Test
    public void destructiveIterator()
    {
        final List<Object> list = new LinkedList<>();
        final Object o = new Object();
        list.add(o);
        final Iterator<Object> iterator =
            new Connection.DestructiveIterator<>(list.iterator());
        assertTrue(iterator.hasNext());
        assertEquals(o, iterator.next());
        assertTrue(!iterator.hasNext());
        assertTrue(list.isEmpty());
    }
}
