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
package au.id.aj.efbp.node;

public class PliantNodeId<T> implements NodeId {
    private final T id;

    public PliantNodeId(final T id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        return this.id.equals(o);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
