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

import au.id.aj.efbp.command.Command;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.node.Node;

@SuppressWarnings("rawtypes")
public class CommandPacket implements Packet {
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
        final String msg = "No data in command packet";
        throw new UnsupportedOperationException(msg);
    }

    @Override
    public void command(Node node) {
        if (!this.command.isFor(node)) {
            return;
        }
        this.command.execute(node);
    }
}
