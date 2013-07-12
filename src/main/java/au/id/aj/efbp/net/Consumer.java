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
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Sink;

public interface Consumer<I> extends Ingress<I>, Process<I, Void>, Sink<I>
{
    public final class Utils
    {
        private static final Logger logger =
            LoggerFactory.getLogger(Consumer.class);

        private Utils()
        {
        }

        /**
         * Similar to Process.Utils.process(), except for in the consumer case
         * it is the end of the road for packets, therefore we shouldn't bother
         * maintaining a collection to return.
         */
        public static <I, E> Collection<Packet<E>> process(
                final Process<I, E> worker, final Iterable<Packet<I>> packets)
        {
            for (Packet<I> packet : packets) {
                try {
                    worker.process(packet);
                } catch (ProcessingException e) {
                    logger.warn("{} dropped packet: {}", worker, packet);
                }
            }
            return Collections.emptySet();
        }
    }
}
