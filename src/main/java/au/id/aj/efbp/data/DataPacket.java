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
package au.id.aj.efbp.data;

import au.id.aj.efbp.node.Node;

public class DataPacket<T> implements Packet<T>
{
    private final T data;

    public DataPacket(final T data)
    {
        this.data = data;
    }

    @Override
    public Type type()
    {
        return Type.DATA;
    }

    @Override
    public T data()
    {
        return this.data;
    }

    @Override
    public void command(final Node node)
    {
        throw new UnsupportedOperationException("No command in data packet");
    }

    @Override
    public String toString()
    {
        return this.data.toString();
    }
}
