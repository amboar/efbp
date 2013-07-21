package au.id.aj.efbp.schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultSchedulerTest {
    private static DefaultScheduler scheduler;
    private LinearIoContext io;

    @BeforeClass
    public static void setupClass() {
        scheduler = new DefaultScheduler(null);
    }

    @Before
    public void setup() {
        this.io = scheduler.newLinearIoContext(new Object());
    }

    @Test
    public void submitOneLinearIo() throws InterruptedException,
            ExecutionException {
        Future<Void> f = this.io.schedule(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });
        assertNull(f.get());
    }

    @Test
    public void submitMultipleLinearIo() throws InterruptedException,
            ExecutionException, IOException {
        // Set limits. The strategy is that we'll submit multiple jobs that
        // write a sequence of integers, and verify that the integers
        // monotonically increase for the length of the job (i.e. the two jobs
        // aren't mixed in the output).
        final int max = 1000000;
        final int schedule = 5;
        // Create an in-memory structure to avoid disk access and useless
        // temporary files
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os));
        final Callable<Void> job = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 0; i < max; i++) {
                    writer.write(Integer.toString(i));
                    writer.write("\n");
                }
                writer.flush();
                return null;
            }
        };

        Future<Void> last = null;
        for (int i = 0; i < schedule; i++) {
            last = this.io.schedule(job);
        }
        assertNotNull(last);
        // Await completion of the last submitted job
        last.get();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(os.toByteArray())));
        for (int i = 0; i < schedule; i++) {
            for (int j = 0; j < max; j++) {
                final int value = Integer.parseInt(reader.readLine());
                assertEquals(j, value);
            }
        }
    }

    @Test(expected=Exception.class)
    public void submitMultipleExceptionLinearIo() throws InterruptedException,
            ExecutionException {
        final Future<Void> f = this.io.schedule(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                throw new Exception("Testing force!");
            }
        });
        this.io.schedule(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });
        // Force the exception?
        f.get();
    }

    @Test
    public void submitMultipleForceLinearIo() throws InterruptedException,
            ExecutionException {
        this.io.schedule(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                throw new Exception("Testing force!");
            }
        });
        final Future<Void> f = this.io.schedule(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        }, true);
        assertNull(f.get());
    }
}
