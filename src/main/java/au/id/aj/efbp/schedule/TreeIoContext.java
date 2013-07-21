package au.id.aj.efbp.schedule;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface TreeIoContext {
    <T, U> Future<T> schedule(final Callable<T> callable, final Future<U> parent);

    <T, U> Future<T> schedule(final Callable<T> callable,
            final Future<U> parent, final boolean force);
}
