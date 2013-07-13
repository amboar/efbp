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

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.id.aj.efbp.util.StopWatch;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class StopWatchTest {
    @Test
    public void newStopWatch() {
        @SuppressWarnings("unused")
        final StopWatch watch = new StopWatch();
    }

    @Test
    public void zeroReport() {
        final StopWatch watch = new StopWatch();
        assertEquals(StopWatch.ZERO_NODE_STATISTICS, watch.report());
    }

    @Test
    public void resetInitialised() {
        final StopWatch watch = new StopWatch();
        watch.reset();
    }

    @Test(expected = IllegalStateException.class)
    public void reportWhenStarted() {
        final StopWatch watch = new StopWatch();
        watch.start();
        watch.report();
    }

    @Test(expected = IllegalStateException.class)
    public void startWhenStarted() {
        final StopWatch watch = new StopWatch();
        watch.start();
        watch.start();
    }

    @Test
    public void resetWhenStarted() {
        final StopWatch watch = new StopWatch();
        watch.start();
        watch.reset();
    }

    @Test
    public void stopWhenStarted() {
        final StopWatch watch = new StopWatch();
        watch.start();
        watch.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void stopWhenStopped() {
        final StopWatch watch = new StopWatch();
        watch.start();
        watch.stop();
        watch.stop();
    }

    @Test
    public void getTime() throws InterruptedException {
        final StopWatch watch = new StopWatch();
        watch.start();
        TimeUnit.MILLISECONDS.sleep(1);
        watch.stop();
        assertTrue(1L <= watch.getTime(TimeUnit.MILLISECONDS));
    }

    @Test(expected = IllegalStateException.class)
    public void splitWhenInitialised() {
        final StopWatch watch = new StopWatch();
        watch.split();
    }

    @Test
    public void splitWhenStarted() {
        final StopWatch watch = new StopWatch();
        watch.start();
        watch.split();
    }

    @Test(expected = IllegalStateException.class)
    public void splitWhenStopped() {
        final StopWatch watch = new StopWatch();
        watch.start();
        watch.stop();
        watch.split();
    }

    @Test
    public void reportWhenStopped() throws InterruptedException {
        final StopWatch watch = new StopWatch();
        watch.start();
        TimeUnit.MILLISECONDS.sleep(1);
        watch.stop();
        final Statistics stats = watch.report();
        assertEquals(1, stats.mean(TimeUnit.MILLISECONDS), 0.1);
        assertEquals(0, stats.stdev(TimeUnit.MILLISECONDS), 0.0);
        assertEquals(1, stats.elements());
    }

    @Test
    public void reportSplitStatistics() throws InterruptedException {
        final StopWatch watch = new StopWatch();
        watch.start();
        TimeUnit.MILLISECONDS.sleep(1);
        watch.split();
        TimeUnit.MILLISECONDS.sleep(1);
        watch.stop();
        final Statistics stats = watch.report();
        assertEquals(1, stats.mean(TimeUnit.MILLISECONDS), 0.1);
        assertEquals(0, stats.stdev(TimeUnit.MILLISECONDS), 0.1);
        assertEquals(2, stats.elements());
    }
}
