package examples;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.bootstrap.Bootstrap;
import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.net.AbstractConsumer;
import au.id.aj.efbp.net.AbstractProducer;
import au.id.aj.efbp.net.AbstractWorker;
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

public class DlrowOlleh {
    private static final Logger logger =
        LoggerFactory.getLogger(DlrowOlleh.class);

    public static void main(final String[] args) throws InterruptedException {
        final NetworkBuilder builder = new NetworkBuilder();
        final Network net = builder
            .addNode(new HelloWorldProducer())
            .addNode(new HelloWorldReverser())
            .connect(HelloWorldProducer.ID, HelloWorldReverser.ID,
                    HelloWorldReverser.IN)
            .addNode(new DlrowOllehConsumer())
            .connect(HelloWorldReverser.ID, DlrowOllehConsumer.ID,
                    DlrowOllehConsumer.IN)
            .get();
        final DefaultScheduler scheduler =
            new DefaultScheduler(new Bootstrap(1));
        final Pump pump = new DefaultPump(net, scheduler);
        logger.info("Priming network");
        pump.prime();
        logger.info("Pumping nodes");
        pump.pump();
        logger.info("Execution complete");
    }

    /**
     * A node that will produce a packet containing the string "Hello World!".
     */
    private static class HelloWorldProducer extends AbstractProducer<String> {
        public static final NodeId ID =
            new PliantNodeId<String>("HelloWorldProducer");
        private static final Packet<String> hw =
            new DataPacket<>("Hello World!");

        public HelloWorldProducer() {
            super(ID);
        }

        @Override
        public void schedule(final Scheduler scheduler) {
            super.schedule(scheduler);
            final NodeId id = new PliantNodeId<String>("Source");
            scheduler.schedule(new AbstractNode(id) {
                @Override
                public Set<Node> execute() {
                    inject(hw);
                    shutdown();
                    return Collections.emptySet();
                }
            });
        }
    }

    private static class HelloWorldReverser extends
            AbstractWorker<String, String> {
        public static final NodeId ID =
            new PliantNodeId<String>("HelloWorldReverser");
        public static final String IN = "IN";

        public HelloWorldReverser() {
            super(ID, Sink.Utils.<String>generatePortMap(IN));
        }

        @Override
        public void process(Packet<String> inbound,
                Collection<Packet<String>> outbound) throws ProcessingException {
            if (Packet.Type.COMMAND.equals(inbound.type())) {
                inbound.command(this);
                outbound.add(inbound);
                return;
            }
            final String reversed =
                new StringBuffer(inbound.data()).reverse().toString();
            outbound.add(new DataPacket<String>(reversed));
        }
    }

    /**
     * A node that will receive the "Hello World!" packet and print it to
     * System.out.
     */
    private static class DlrowOllehConsumer extends AbstractConsumer<String> {
        public static final NodeId ID =
            new PliantNodeId<String>("DlrowOllehConsumer");
        public static final String IN = "IN";

        public DlrowOllehConsumer() {
            super(ID, Sink.Utils.<String>generatePortMap(IN));
        }

        @Override
        public void process(Packet<String> inbound,
                Collection<Packet<Void>> outbound) throws ProcessingException {
            if (Packet.Type.COMMAND.equals(inbound.type())) {
                inbound.command(this);
                return;
            }
            System.out.println(inbound.data());
        }
    }
}
