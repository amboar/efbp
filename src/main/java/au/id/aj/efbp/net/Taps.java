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
import java.util.Set;

import au.id.aj.efbp.data.Packet;

public interface Taps<I> extends Set<Tap<I>> {
    public static final class Utils {
        private Utils() {
        }

        /**
         * Copy a collection of packets to a collection of inspection queues.
         *
         * @param inspectors
         *            The inspection queues onto which to place packets
         *
         * @param packet
         *            The packet to place on the inspection queues
         */
        public static <I> void acquiesce(final Taps<I> taps,
                final Packet<I> packet) {
            for (Tap<I> tap : taps) {
                tap.add(packet);
            }
        }

        /**
         * Copy a collection of packets to a collection of inspection queues.
         *
         * @param inspectors
         *            The inspection queues onto which to place packets
         *
         * @param packets
         *            The packets to place on the inspection queues
         */
        public static <I> void acquiesce(final Taps<I> taps,
                final Collection<Packet<I>> packets) {
            for (Tap<I> tap : taps) {
                tap.addAll(packets);
            }
        }
    }
}
