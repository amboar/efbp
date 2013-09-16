package au.id.aj.efbp.util;

import java.util.Iterator;

public class Cycle<T> implements Iterable<T> {
    private final Iterable<T> ts;

    public Cycle(final Iterable<T> ts) {
        this.ts = ts;
    }

    @Override
    public Iterator<T> iterator() {
        return new CyclicIterator<T>(this.ts);
    }
}
