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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.command.AbstractCommand;
import au.id.aj.efbp.command.CommandId;
import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.net.AbstractConsumer;
import au.id.aj.efbp.net.Inject;
import au.id.aj.efbp.net.Process;
import au.id.aj.efbp.net.Process.Utils;
import au.id.aj.efbp.plug.Pluggable;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.node.PliantNodeId;

/**
 * Implements the core execution engine by processing Nodes and connecting its
 * input queue to its own output, feeding itself with the set of nodes N+1 which
 * is the result of executing the current set N.
 */
public class Bootstrap extends AbstractConsumer<Node> implements Inject<Node>,
       Pluggable {
    private static final Logger logger = LoggerFactory
            .getLogger(Bootstrap.class);

    public static final String IN = "IN";
    public static final NodeId ID = new PliantNodeId<String>("__"
            + Bootstrap.class.getSimpleName());

    private final Runnable job;
    private final ExecutorService executors;
    private final AtomicBoolean plugged;

    public Bootstrap(final int poolSize) {
        super(ID, Sink.Utils.<Node> generatePortMap(IN), new Control());
        this.plugged = new AtomicBoolean(false);
        this.executors = Executors.newFixedThreadPool(poolSize);
        this.job = new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName(ID.toString());
                synchronized (Bootstrap.this) {
                    logger.info("Triggering bootstrap");
                    Bootstrap.this.execute();
                }
            }
        };
    }

    public void plug()
    {
        this.plugged.set(true);
    }

    public void unplug()
    {
        this.plugged.set(false);
        this.executors.submit(this.job);
    }

    @Override
    public final void inject(final Packet<Node> packet) {
        if (getLookup().lookup(Control.class).shouldStop()) {
            logger.info("Halting network");
            return;
        }
        logger.info("Injecting node: {}", packet);
        ports().get(IN).enqueue(packet);
        logger.info("Queued nodes");
        if (this.plugged.get()) {
            logger.info("Bootstrap queue is plugged, execution delayed");
        } else {
            this.executors.submit(this.job);
            logger.info("Scheduled Bootstrap");
        }
    }

    @Override
    public final void inject(final Collection<Packet<Node>> packets) {
        if (getLookup().lookup(Control.class).shouldStop()) {
            logger.info("Halting network");
            return;
        }
        if (packets.isEmpty()) {
            logger.warn("Empty injection attempted, ignoring");
            return;
        }
        logger.info("Injecting nodes: {}", packets);
        ports().get(IN).enqueue(packets);
        if (this.plugged.get()) {
            logger.info("Bootstrap queue is plugged, execution delayed");
        } else {
            this.executors.submit(this.job);
            logger.info("Scheduled Bootstrap");
        }
    }

    private void processData(final Node node) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Set<Node> innerNodes;
                synchronized (node) {
                    Thread.currentThread().setName(node.id().toString());
                    innerNodes = node.execute();
                }
                final Set<Packet<Node>> nodePackets = new LinkedHashSet<>();
                for (Node innerNode : innerNodes) {
                    final Packet<Node> packet = new DataPacket<>(innerNode);
                    nodePackets.add(packet);
                }
                Bootstrap.this.inject(nodePackets);
            }
        };
        this.executors.submit(runnable);
    }

    @Override
    public Packet<Void> process(final Packet<Node> packet) {
        switch (packet.type()) {
        case COMMAND:
            logger.info("Processing command packet: {}", packet);
            packet.command(this);
            break;
        case DATA:
            logger.info("Processing node packet: {}", packet);
            processData(packet.data());
            break;
        }
        return null;
    }

    @Override
    public Collection<Packet<Void>> process(final Iterable<Packet<Node>> packets) {
        logger.info("Processing packets: {}", packets);
        Process.Utils.process(this, packets);
        return Collections.emptySet();
    }

    public void awaitTermination() throws InterruptedException {
        getLookup().lookup(Control.class).hasStopped();
    }

    public static class Control {
        private boolean stop = false;

        public synchronized boolean shouldStop() {
            return this.stop;
        }

        public synchronized void stop() {
            this.stop = true;
            notifyAll();
        }

        public synchronized void hasStopped() throws InterruptedException {
            if (this.stop) {
                return;
            }
            wait();
        }
    }

    public static class StopCommand extends AbstractCommand {
        private static final long serialVersionUID = 1341221514588307327L;

        public StopCommand(final CommandId id) {
            super(id, Bootstrap.ID);
        }

        @Override
        public void execute(Node node) {
            final Control control = node.getLookup().lookup(Control.class);
            if (null == control) {
                logger.error("Control lookup cannot be null");
                return;
            }
            control.stop();
        }
    }
}
