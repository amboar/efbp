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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.aj.efbp.data.DataPacket;
import au.id.aj.efbp.data.Packet;
import au.id.aj.efbp.endpoint.Sink;
import au.id.aj.efbp.net.AbstractConsumer;
import au.id.aj.efbp.net.Ingress;
import au.id.aj.efbp.net.Inject;
import au.id.aj.efbp.net.Process;
import au.id.aj.efbp.node.Node;
import au.id.aj.efbp.node.NodeId;
import au.id.aj.efbp.node.PliantNodeId;
import au.id.aj.efbp.plug.Pluggable;

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
    private final AtomicBoolean submitted;
    private final Map<Node, Callable<Void>> nodeJobs;
    private final Queue<Future<Void>> executingJobs;
    private final Control control;
    private final Process.Utils<Node, Void> processor;

    public Bootstrap(final int poolSize) {
        super(ID, Sink.Utils.<Node> generatePortMap(IN));
        this.plugged = new AtomicBoolean(false);
        this.submitted = new AtomicBoolean(false);
        this.executors = Executors.newFixedThreadPool(poolSize);
        this.nodeJobs = new HashMap<>();
        this.executingJobs = new LinkedList<Future<Void>>();
        this.control = new Control();
        addContent(this.control);
        this.job = new Runnable() {
            @Override
            public void run() {
                submitted.set(false);
                Thread.currentThread().setName(ID.toString());
                synchronized (Bootstrap.this) {
                    logger.info("Triggering bootstrap");
                    Bootstrap.this.execute();
                }
            }
        };
        this.processor = new Process.Utils<>(this);
    }

    private void trigger() {
        if (this.submitted.compareAndSet(false, true)) {
            this.executors.submit(this.job);
        }
    }

    @Override
    public void plug() {
        this.plugged.set(true);
    }

    @Override
    public void unplug() {
        this.plugged.set(false);
        trigger();
    }

    @Override
    public final void inject(final Packet<Node> packet) {
        if (this.control.shouldStop()) {
            logger.info("Halting network");
            return;
        }
        logger.debug("Injecting node: {}", packet);
        ports().get(IN).enqueue(packet);
        logger.debug("Queued nodes");
        if (this.plugged.get()) {
            logger.warn("Bootstrap queue is plugged, execution delayed");
        } else {
            trigger();
            logger.debug("Scheduled Bootstrap");
        }
    }

    @Override
    public final void inject(final Collection<Packet<Node>> packets) {
        if (this.control.shouldStop()) {
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
            trigger();
            logger.info("Scheduled Bootstrap");
        }
    }

    @Override
    protected void process(final Node node) {
        Callable<Void> callable = this.nodeJobs.get(node);
        if (null == callable) {
            callable = new NetworkJob(node);
            this.nodeJobs.put(node, callable);
        }
        final Future<Void> future = this.executors.submit(callable);
        logger.debug("Adding Future to execution queue: {}", future);
        this.executingJobs.add(future);
    }

    @Override
    public void shutdown() {
        // Ignore this, wait for StopCommand instead
        return;
    }

    private void cleanupJobs() {
        logger.debug("Cleaning up finished jobs");
        final Iterator<Future<Void>> jobs = this.executingJobs.iterator();
        while (jobs.hasNext()) {
            final Future<Void> job = jobs.next();
            if (job.isDone()) {
                try {
                    // Test if an exception occurred by trying to fetch the
                    // result
                    job.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    logger.error("Job execution failed", e);
                } finally {
                    jobs.remove();
                    logger.debug("Job {} complete", job);
                }
            }
        }
    }

    private static <T> Iterable<T> condense(final Iterable<T> packets) {
        logger.debug("Condensing jobs");
        final Set<T> condensed = new LinkedHashSet<>();
        Ingress.Utils.drainFrom(packets, condensed);
        return condensed;
    }

    @Override
    public Collection<Packet<Void>> process(final Iterable<Packet<Node>> packets) {
        cleanupJobs();
        logger.debug("Processing packets: {}", packets);
        this.processor.process(condense(packets));
        return Collections.emptySet();
    }

    public void awaitTermination() throws InterruptedException {
        this.control.hasStopped();
    }

    public class Control {
        private boolean stop = false;

        public synchronized boolean shouldStop() {
            return this.stop;
        }

        public synchronized void stop() {
            this.stop = true;
            executors.shutdown();
            notifyAll();
        }

        public synchronized void hasStopped() throws InterruptedException {
            if (this.stop) {
                return;
            }
            wait();
        }
    }

    private class NetworkJob implements Callable<Void> {
        private final Node node;

        public NetworkJob(final Node node) {
            this.node = node;
        }

        @Override
        public Void call() {
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
            if (!nodePackets.isEmpty()) {
                Bootstrap.this.inject(nodePackets);
            }
            return null;
        }
    }
}
