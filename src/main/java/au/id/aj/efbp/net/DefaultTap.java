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

import java.util.LinkedList;
import java.util.List;

import au.id.aj.efbp.data.Packet;

public class DefaultTap<I> extends LinkedList<Packet<I>> implements Tap<I>, Comparable<List<Packet<I>>>
{
    private static final long serialVersionUID = 1479818015740106119L;

    @Override
    public boolean equals(final Object o)
    {
        return this == o;
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
    }

    @Override
    public int compareTo(List<Packet<I>> o)
    {
        final int value = hashCode() - o.hashCode();
        if (0 == value) {
            System.out.println("Hash subtraction was zero");
            return equals(o) ? 0 : -1;
        }
        System.out.println("Non-zero hash subtraction");
        return value;
    }
}
