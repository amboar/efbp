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


/**
 * A container class for pushing data and metadata through the network. A
 * packet can optionally hold data of type T, and concurrently transport
 * multiple Command instances between nodes.
 */
public interface Packet<T>
{
    public enum Type { DATA, COMMAND };

    Type type();

    /**
     * Retrieve data held in the packet, if any.
     *
     * @return An instance of T if available, otherwise null.
     */
    T data();

    /**
     * Retrieve all commands held by the packet.
     */
    void command(final Node node);
}
