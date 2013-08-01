package examples;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.bootstrap.Bootstrap;
import au.id.aj.efbp.command.AbstractCommand;
import au.id.aj.efbp.command.CommandPacket;
import au.id.aj.efbp.command.LongCommandId;
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

public class FlippingHelloWorld {
    private static final Logger logger =
        LoggerFactory.getLogger(DlrowOlleh.class);

    public static void main(final String[] args) throws InterruptedException {
        final NetworkBuilder builder = new NetworkBuilder();
        final Network net = builder
            .addNode(new HelloWorldProducer())
            .addNode(new HelloWorldReverser())
            .connect(HelloWorldProducer.ID, HelloWorldReverser.ID,
                    HelloWorldReverser.IN)
            .addNode(new HelloWorldConsumer())
            .connect(HelloWorldReverser.ID, HelloWorldConsumer.ID,
                    HelloWorldConsumer.IN)
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

    public interface Reverser {
        void setReverse(final boolean reverse);
    }

    public static class ReverseCommand extends AbstractCommand {
        private static final long serialVersionUID = -3435479064223360227L;
        private final boolean reverse;

        public ReverseCommand(final boolean reverse) {
            super(LongCommandId.next(), HelloWorldReverser.ID);
            this.reverse = reverse;
        }

        @Override
        public void execute(Node node) {
            node.getLookup().lookup(Reverser.class).setReverse(this.reverse);
        }
    }

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
                    final Collection<Packet<String>> packets = new LinkedList<>();
                    packets.add(hw);
                    packets.add(new CommandPacket(new ReverseCommand(true)));
                    packets.add(hw);
                    packets.add(hw);
                    packets.add(new CommandPacket(new ReverseCommand(false)));
                    packets.add(hw);
                    inject(packets);
                    shutdown();
                    return Collections.emptySet();
                }
            });
        }
    }

    private static class HelloWorldReverser extends
            AbstractWorker<String, String> implements Reverser {
        public static final NodeId ID =
            new PliantNodeId<String>("HelloWorldReverser");
        public static final String IN = "IN";

        private boolean reverse = false;

        public HelloWorldReverser() {
            super(ID, Sink.Utils.<String>generatePortMap(IN));
            addContent(this);
        }

        @Override
        protected void process(final String inbound,
                final Collection<Packet<String>> outbound) {
            final String newData;
            if (this.reverse) {
                newData = new StringBuffer(inbound).reverse().toString();
            } else {
                newData = inbound;
            }
            outbound.add(new DataPacket<String>(newData));
        }

        @Override
        public void setReverse(final boolean reverse) {
            this.reverse = reverse;
        }
    }

    private static class HelloWorldConsumer extends AbstractConsumer<String> {
        public static final NodeId ID =
            new PliantNodeId<String>("HelloWorldConsumer");
        public static final String IN = "IN";

        public HelloWorldConsumer() {
            super(ID, Sink.Utils.<String>generatePortMap(IN));
        }

        @Override
        protected void process(final String data) throws ProcessingException {
            System.out.println(data);
        }
    }
}
