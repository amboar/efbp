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
package au.id.aj.efbp.endpoint;

import java.util.Collection;
import java.util.HashMap;

import au.id.aj.efbp.transport.Outbound;

public class ConnectionRegistry<E> extends
        HashMap<Sink<E>, Collection<Outbound<E>>> implements Connections<E> {
    private static final long serialVersionUID = -3492076269831687096L;
}
