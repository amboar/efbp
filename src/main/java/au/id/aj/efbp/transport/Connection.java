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

public interface Connection<T> extends Inbound<T>, Outbound<T> {
    /**
     * Wraps a generic iterator to make it destructive, that is, iteration of
     * the collection simultaneously removes elements as they are iterated. In
     * context, this is used to drain Inbound queues via iteration, matching the
     * intuitive idea of consuming elements from the queue without substantial
     * boilerplate code.
     */
    public static class DestructiveIterator<U> implements Iterator<U> {
        private final Iterator<U> iterator;

        public DestructiveIterator(final Iterator<U> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public U next() {
            final U packet = this.iterator.next();
            this.iterator.remove();
            return packet;
        }

        @Override
        public void remove() {
            // Not implemented as next() is destructive
        }
    }
}
