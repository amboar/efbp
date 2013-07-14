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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.node.Node;

@SuppressWarnings("rawtypes")
public class CommandPacket implements Packet {
    private static final Logger logger =
        LoggerFactory.getLogger(CommandPacket.class);

    private final Command command;

    public CommandPacket(final Command command) {
        this.command = command;
    }

    @Override
    public Type type() {
        return Packet.Type.COMMAND;
    }

    @Override
    public Object data() {
        logger.warn("Not a data packet, fix caller");
        return null;
    }

    @Override
    public void command(Node node) {
        if (!this.command.isFor(node)) {
            return;
        }
        this.command.execute(node);
    }

    @Override
    public String toString() {
        return this.command.toString();
    }
}
