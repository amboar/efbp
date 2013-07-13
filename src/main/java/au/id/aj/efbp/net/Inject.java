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

import java.util.Collection;

import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.data.Packet;

public interface Inject<E> {
    /**
     * Injects a packet into the start of a data flow. The injected packet is
     * checked for any {@link Command}s that may be relevent, the enqueued on
     * connected Outbound queues. This method is different to {@see
     * #egress(final Packet<E> packet)} in that egress will not check for
     * relevant Commands on the packets to egress.
     *
     * As injected packets are eventually egressed, the set of Nodes resulting
     * from the egressed packet are returned for scheduling.
     *
     * @param packet
     *            The packet instance to inject into the network.
     */
    void inject(final Packet<E> packet);

    /**
     * Injects a collection of packets into the start of a data flow. The
     * injected packet is checked for any {@link Command}s that may be relevent,
     * the enqueued on connected Outbound queues. In the same vein as {@see
     * #inject(final Packet<E> packet), this method will check all packets in
     * the collection for {@link Command}s applicable to the instance.
     *
     * @param packets
     *            The packet instance to inject into the network.
     *
     * @return The set of nodes whose ingress queues were populated with the
     *         provided collection of packets.
     */
    void inject(final Collection<Packet<E>> packets);
}
