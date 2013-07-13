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
package au.id.aj.efbp.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StopWatch {
    public static final Statistics ZERO_NODE_STATISTICS;

    static {
        ZERO_NODE_STATISTICS = generateEmptyStatistics();
    }

    private static Statistics generateEmptyStatistics() {
        final List<Long> list = new LinkedList<>();
        list.add(Long.valueOf(0));
        return new ImmutableReport(0, list, TimeUnit.NANOSECONDS);
    }

    private enum TimerState {
        INITIALISED, STARTED, STOPPED
    }

    private List<Long> times;
    private TimerState state;

    public StopWatch() {
        this.state = TimerState.INITIALISED;
        this.times = new LinkedList<>();
    }

    public void reset() {
        this.times.clear();
        this.state = TimerState.INITIALISED;
    }

    public void start() {
        if (!TimerState.INITIALISED.equals(this.state)) {
            throw new IllegalStateException("Timer not in initial state");
        }
        this.times.add(System.nanoTime());
        this.state = TimerState.STARTED;
    }

    public void stop() {
        if (!TimerState.STARTED.equals(this.state)) {
            throw new IllegalStateException("Timer not started");
        }
        this.times.add(System.nanoTime());
        this.times = Collections.unmodifiableList(this.times);
        this.state = TimerState.STOPPED;
    }

    public void split() {
        if (!TimerState.STARTED.equals(this.state)) {
            throw new IllegalStateException("Cannot split un-started timer");
        }
        this.times.add(System.nanoTime());
    }

    private void reportingPreconditions() {
        switch (this.state) {
        case INITIALISED:
            throw new IllegalStateException("Timer is yet to start");
        case STARTED:
            throw new IllegalStateException("Timer is still running");
        case STOPPED:
            assert 2 <= this.times.size();
            break;
        }
    }

    public long getTime(final TimeUnit unit) {
        reportingPreconditions();
        final long time = this.times.get(this.times.size() - 1)
                - this.times.get(0);
        return unit.convert(time, TimeUnit.NANOSECONDS);
    }

    private List<Long> deltas(final TimeUnit unit) {
        reportingPreconditions();
        final Iterator<Long> iter = this.times.iterator();
        final List<Long> deltas = new LinkedList<>();
        Long prev = iter.next();
        while (iter.hasNext()) {
            final Long cur = iter.next();
            deltas.add(unit.convert(cur - prev, TimeUnit.NANOSECONDS));
            prev = cur;
        }
        return Collections.unmodifiableList(deltas);
    }

    public Statistics report() {
        if (TimerState.INITIALISED.equals(this.state)) {
            return ZERO_NODE_STATISTICS;
        }
        reportingPreconditions();
        final TimeUnit unit = TimeUnit.NANOSECONDS;
        return new ImmutableReport(getTime(unit), deltas(unit), unit);
    }

    private static class ImmutableReport implements Statistics {
        private final long span;
        private final List<Long> deltas;
        private final TimeUnit dataUnit;

        public ImmutableReport(final long span, final List<Long> deltas,
                final TimeUnit units) {
            assert 0 < deltas.size();
            this.span = span;
            this.deltas = deltas;
            this.dataUnit = units;
        }

        @Override
        public double mean(final TimeUnit unit) {
            final long unitSpan = unit.convert(this.span, TimeUnit.NANOSECONDS);
            // Subtract one from size as times contains the start and stop
            // events;
            // what we want to calculate is the average of the intermediate
            // deltas.
            return unitSpan / (double) this.deltas.size();
        }

        private double variance(final TimeUnit unit) {
            final double mean = mean(unit);
            double sumSquareError = 0.0;
            for (Long d : this.deltas) {
                final double error = mean - unit.convert(d, this.dataUnit);
                sumSquareError += error * error;
            }
            return sumSquareError / deltas.size();
        }

        @Override
        public double stdev(final TimeUnit unit) {
            return Math.sqrt(variance(unit));
        }

        @Override
        public int elements() {
            return this.deltas.size();
        }
    }
}
