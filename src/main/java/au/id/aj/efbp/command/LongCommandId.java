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
package au.id.aj.efbp.command;

import java.util.concurrent.atomic.AtomicLong;

public class LongCommandId implements CommandId {

    private static final AtomicLong ids = new AtomicLong();

    public static CommandId next() {
        return new LongCommandId(ids.getAndIncrement());
    }

    private final long id;
    private final int hashCode;
    private final String string;

    public LongCommandId(final long id) {
        this.id = id;
        final Long value = Long.valueOf(id);
        this.hashCode = value.hashCode();
        this.string = value.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o) {
            return false;
        }
        if (!(o instanceof LongCommandId)) {
            return false;
        }
        final LongCommandId other = (LongCommandId) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
