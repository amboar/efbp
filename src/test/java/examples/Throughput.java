package examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import au.id.aj.efbp.bootstrap.Bootstrap;
import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.net.AbstractConsumer;
import au.id.aj.efbp.net.AbstractProducer;
import au.id.aj.efbp.net.Network;
import au.id.aj.efbp.net.NetworkBuilder;
import au.id.aj.efbp.net.ProcessingException;
import au.id.aj.efbp.node.AbstractNode;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.node.PliantNodeId;
import au.id.aj.efbp.pump.DefaultPump;
import au.id.aj.efbp.pump.Pump;
import au.id.aj.efbp.schedule.DefaultScheduler;
import au.id.aj.efbp.schedule.Scheduler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class Throughput {

    public static void main(final String[] args) throws InterruptedException {
        ((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.OFF);
        final NetworkBuilder builder = new NetworkBuilder();
        final Network net = builder
            .addNode(new ObjectProducer(100000, 100))
            .addNode(new ObjectConsumer())
            .connect(ObjectProducer.ID, ObjectConsumer.ID,
                    ObjectConsumer.IN)
            .get();
        final DefaultScheduler scheduler =
            new DefaultScheduler(new Bootstrap(1));
        final Pump pump = new DefaultPump(net, scheduler);
        final long start = System.nanoTime();
        pump.prime();
        pump.pump();
        final long end = System.nanoTime();
        System.out.println("Network processing time: " + (end - start) + "ns");
    }

    private static class ObjectProducer extends AbstractProducer<Object> {
        public static final NodeId ID =
            new PliantNodeId<String>("ObjectProducer");
        private final int maxPackets;
        private final int batchSize;

        public ObjectProducer(final int maxPackets, final int batchSize) {
            super(ID);
            this.maxPackets = maxPackets;
            this.batchSize = batchSize;
        }

        @Override
        public void schedule(final Scheduler scheduler) {
            super.schedule(scheduler);
            final NodeId id = new PliantNodeId<String>("Source");
            scheduler.schedule(new AbstractNode(id) {
                @Override
                public Set<Node> execute() {
                    final long start = System.nanoTime();
                    int left = maxPackets;
                    int leftBatch = 0;
                    final List<Packet<Object>> list = new ArrayList<>(batchSize);
                    while(0 < (left -= leftBatch)) {
                        leftBatch = Math.min(batchSize, left);
                        for (long i = 0; i < leftBatch; i++) {
                            list.add(new DataPacket<>(new Object()));
                        }
                        inject(list);
                        list.clear();
                    }
                    final long end = System.nanoTime();
                    System.out.println("Produced for " + (end - start) + "ns");
                    shutdown();
                    return Collections.emptySet();
                }
            });
        }
    }

    private static class ObjectConsumer extends AbstractConsumer<Object> {
        public static final NodeId ID =
            new PliantNodeId<String>("ObjectConsumer");
        public static final String IN = "IN";
        private long count;

        public ObjectConsumer() {
            super(ID, Sink.Utils.<Object>generatePortMap(IN));
            this.count = 0;
        }

        @Override
        protected void process(final Object inbound) throws ProcessingException {
            this.count++;
        }

        @Override
        public void shutdown() {
            System.out.println("Consumed " + this.count + " packets");
            super.shutdown();
        }
    }
}
