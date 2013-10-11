package au.id.aj.efbp.util;

import java.util.Iterator;

public class Bound<T> implements Iterable<T> {

    private final Iterator<T> iterator;
    private final int bound;

    public Bound(final Iterable<T> iterable, final int bound) {
        this.iterator = iterable.iterator();
        this.bound = bound;
    }

    public Bound(final Iterator<T> iterator, final int bound) {
        this.iterator = iterator;
        this.bound = bound;
    }

    @Override
    public Iterator<T> iterator() {
        return new BoundedIterator<>(this.iterator, this.bound);
    }
}
