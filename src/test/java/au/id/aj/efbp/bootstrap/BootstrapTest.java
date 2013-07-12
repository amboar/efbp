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
package au.id.aj.efbp.bootstrap;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.command.CommandId;
import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.net.DummyConsumer;
import au.id.aj.efbp.net.DummyProducer;
import au.id.aj.efbp.net.DummyWorker;
import au.id.aj.efbp.net.Network;
import au.id.aj.efbp.net.NetworkBuilder;
import au.id.aj.efbp.node.AbstractNode;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.node.PliantNodeId;
import au.id.aj.efbp.pump.DefaultPump;
import au.id.aj.efbp.pump.Pump;
import au.id.aj.efbp.schedule.DefaultScheduler;
import au.id.aj.efbp.schedule.ScheduleContext;
import au.id.aj.efbp.schedule.Scheduler;

@RunWith(JUnit4.class)
public class BootstrapTest
{
    private static final Logger logger =
        LoggerFactory.getLogger(BootstrapTest.class);

    private Bootstrap bootstrap;
    private Scheduler scheduler;

    @Before
    public void setUp()
    {
        this.bootstrap = new Bootstrap(1);
        this.scheduler = new DefaultScheduler(this.bootstrap);
    }

    @Test
    public void producerConsumer() throws InterruptedException
    {
        final NetworkBuilder builder = new NetworkBuilder();
        final Network network = builder
            .addNode(new ObjectProducer())
            .addNode(new ObjectConsumer())
            .connect(DummyProducer.ID, DummyConsumer.ID, DummyConsumer.IN)
            .get();
        final Pump pump = new DefaultPump(network, this.scheduler);
        final ObjectConsumer consumer =
            network.node(DummyConsumer.ID, ObjectConsumer.class);
        logger.info("Priming network");
        pump.prime();
        logger.info("Testing packet receipt");
        synchronized (consumer.received) {
            if (consumer.received.isEmpty()) {
                logger.info("Awaiting packet");
                consumer.received.wait();
            }
            logger.info("Packet receipt signalled");
            assertTrue(!consumer.received.isEmpty());
            logger.info("Done");
        }
    }

    @Test
    public void producerWorkerConsumer() throws InterruptedException
    {
        final NetworkBuilder builder = new NetworkBuilder();
        final Network network = builder
            .addNode(new ObjectProducer())
            .addNode(new DummyWorker())
            .connect(DummyProducer.ID, DummyWorker.ID, DummyWorker.IN)
            .addNode(new ObjectConsumer())
            .connect(DummyWorker.ID, DummyConsumer.ID, DummyConsumer.IN)
            .get();
        final Pump pump = new DefaultPump(network, this.scheduler);
        final ObjectConsumer consumer =
            network.node(DummyConsumer.ID, ObjectConsumer.class);
        logger.info("Priming network");
        pump.prime();
        logger.info("Testing packet receipt");
        synchronized (consumer.received) {
            if (consumer.received.isEmpty()) {
                logger.info("Awaiting packet");
                consumer.received.wait();
            }
            logger.info("Packet receipt signalled");
            assertTrue(!consumer.received.isEmpty());
            logger.info("Done");
        }

    }

    @Test
    public void stopNetwork() throws InterruptedException
    {
        final Network network = (new NetworkBuilder()).get();
        final DefaultPump pump = new DefaultPump(network, this.scheduler);
        pump.prime();
        pump.submit(new Bootstrap.StopCommand(new CommandId() { }));
        pump.pump();
    }

    @Test
    public void timerTask() throws InterruptedException
    {
        final int N = 3;
        final NetworkBuilder builder = new NetworkBuilder();
        final Network network = builder
            .addNode(new TimedObjectProducer(N))
            .addNode(new ObjectConsumer(N))
            .connect(DummyProducer.ID, DummyConsumer.ID, DummyConsumer.IN)
            .get();
        final Pump pump = new DefaultPump(network, this.scheduler);
        final ObjectConsumer consumer =
            network.node(DummyConsumer.ID, ObjectConsumer.class);
        logger.info("Priming network");
        pump.prime();
        logger.info("Testing packet receipt");
        synchronized (consumer.received) {
            if (consumer.received.isEmpty()) {
                logger.info("Awaiting packet");
                consumer.received.wait();
            }
            logger.info("Packet receipt signalled");
            assertTrue(!consumer.received.isEmpty());
            logger.info("Done");
        }

    }

    private static class ObjectProducer extends DummyProducer<Object>
            implements ScheduleContext
    {
        @Override
        public Packet<Object> process(final Packet<Object> packet)
        {
            logger.info("Processing packet: {}", packet);
            return super.process(packet);
        }

        @Override
        public void schedule(final Scheduler scheduler)
        {
            super.schedule(scheduler);
            logger.info("Scheduling producer IO task");
            scheduler.scheduleIo(new Runnable()
            {
                @Override
                public void run()
                {
                    logger.info("Injecting packet into producer");
                    ObjectProducer.this.inject(new DataPacket<>(new Object()));
                }
            });
        }
    }

    private static class TimedObjectProducer extends DummyProducer<Object>
    {
        private final int n;
        private int c = 0;
        private volatile TimerTask t;

        public TimedObjectProducer(final int n)
        {
            this.n = n < 0 ? 1 : n;
            this.c = 0;
        }

        private void generated()
        {
            assert null != t;
            this.c++;
            if (this.n == this.c) {
                this.t.cancel();
            }
        }

        @Override
        public void schedule(final Scheduler scheduler)
        {
            super.schedule(scheduler);
            logger.info("Scheduling timed IO task");
            final NodeId id = new PliantNodeId<String>("Source");
            final Node node = new AbstractNode(id)
            {
                @Override
                public Set<Node> execute()
                {
                    inject(new DataPacket<>(new Object()));
                    generated();
                    return Collections.emptySet();
                }
            };
            this.t = scheduler.schedule(node, 0, 1, TimeUnit.SECONDS);
        }
    }

    private class ObjectConsumer extends DummyConsumer<Object>
    {
        public final List<Object> received = new LinkedList<>();

        private final int n;
        private int c;

        public ObjectConsumer()
        {
            this(1);
        }

        public ObjectConsumer(final int n)
        {
            this.n = n < 0 ? 1 : n;
            this.c = 0;
        }

        @Override
        public Packet<Void> process(final Packet<Object> packet)
        {
            logger.info("Processing packet: {}", packet);
            this.c++;
            if (this.n > this.c) {
                return null;
            }
            logger.info("Locking received list");
            synchronized (this.received) {
                logger.info("Received list locked");
                received.add(packet.data());
                logger.info("Notifying listeners");
                received.notifyAll();
            }
            return null;
        }
    }
}
