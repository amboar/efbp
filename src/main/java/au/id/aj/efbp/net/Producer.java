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

import au.id.aj.efbp.control.ControlContext;
import au.id.aj.efbp.endpoint.Source;
import au.id.aj.efbp.schedule.ScheduleContext;

/**
 * An interface for edge nodes in the network whose purpose is to provide the
 * network with packets to process. Such nodes are often triggered by external
 * events such as timers. Nodes implementing this interface are likely to be
 * used by the scheduler to prime the network with packets, triggering the
 * execution of downstream nodes in an event-based manner.
 */
public interface Producer<E> extends Source<E>, Inject<E>, Ingress<E>, Process<E,E>,
        Egress<E>, ScheduleContext, ControlContext
{
}
