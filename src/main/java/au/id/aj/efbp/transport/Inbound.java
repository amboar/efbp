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

import au.id.aj.efbp.data.Packet;

/**
 * Provide methods relating to consumption of in-bound Packets. Note that the
 * iterator implementation is expected to be destructive, i.e. iteration
 * removes elements from the underlying collection.
 */
public interface Inbound<T> extends Iterable<Packet<T>>
{
}
