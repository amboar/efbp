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
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import au.id.aj.efbp.data.Packet;

public final class ConcurrentConnection<T> implements Connection<T>
{
    private final Queue<Packet<T>> queue;

    public ConcurrentConnection()
    {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void enqueue(final Packet<T> packet)
    {
        this.queue.add(packet);
    }

    @Override
    public void enqueue(final Collection<Packet<T>> packets)
    {
        this.queue.addAll(packets);
    }

    @Override
    public Iterator<Packet<T>> iterator()
    {
        return new Connection.DestructiveIterator<>(this.queue.iterator());
    }
}
