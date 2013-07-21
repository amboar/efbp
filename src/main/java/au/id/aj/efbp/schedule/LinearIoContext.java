package au.id.aj.efbp.schedule;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface LinearIoContext {
    <T> Future<T> schedule(final Callable<T> callable);

    <T> Future<T> schedule(final Callable<T> callable, final boolean force);
}
