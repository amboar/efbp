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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.data.Packet;

/**
 * A Worker implements the full Ingress and Egress process and provides a
 * mechanism for transfoming ingressed data before it egresses.
 */
public interface Process<I, E> {
    /**
     * Transforms a packet from one type to another. The types do not
     * necessarily have to be different; arbitrary manipulations of the data can
     * be carried out.
     *
     * @param inbound
     *            The inbound packet to process
     *
     * @param outbound
     *            A collection to hold packets generated in response to the
     *            provided inbound packet. These may be zero or more data or
     *            command packets.
     *
     * @return The processed packet.
     */
    void process(final Packet<I> inbound, final Collection<Packet<E>> outbound)
        throws ProcessingException;

    /**
     * Transforms multiple input packets to their output type.
     *
     * @param packets
     *            The collection of packets to process
     *
     * @return The collection of processed packets
     */
    Collection<Packet<E>> process(final Iterable<Packet<I>> packets);

    public static final class Utils {
        private static final Logger logger = LoggerFactory
                .getLogger(Utils.class);

        private Utils() {
        }

        /**
         * Apply the single packet transform of the provided worker to the
         * collection.
         *
         * @param worker
         *            The Worker implementation on which to invoke the
         *            single-packet transform method.
         *
         * @param packets
         *            The packets to process
         *
         * @return
         */
        public static <I, E> Collection<Packet<E>> process(
                final Process<I, E> worker, final Iterable<Packet<I>> packets) {
            final List<Packet<E>> collection = new LinkedList<>();
            final List<Packet<E>> processed = new ArrayList<>();
            for (Packet<I> packet : packets) {
                try {
                    processed.clear();
                    worker.process(packet, processed);
                    collection.addAll(processed);
                } catch (ProcessingException e) {
                    logger.warn("{} dropped packet: {}", worker, packet);
                }
            }
            return Collections.unmodifiableList(collection);
        }
    }
}
