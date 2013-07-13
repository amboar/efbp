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

import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.node.PliantNodeId;

public class DummyWorker extends AbstractWorker<Object, Object> {
    public static final NodeId ID = new PliantNodeId<String>(
            DummyWorker.class.getSimpleName());
    public static final String IN = "IN";
    public static final String DUMMY_IN = "DUMMY_IN";

    public DummyWorker() {
        super(ID, Sink.Utils.<Object> generatePortMap(IN, DUMMY_IN));
    }

    @Override
    public Packet<Object> process(final Packet<Object> packet) {
        return packet;
    }
}
