package au.id.aj.efbp.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BoundedIterator<T> implements Iterator<T> {

    private final Iterator<T> source;
    private int bound;

    public BoundedIterator(final Iterator<T> source, final int bound) {
        this.source = source;
        this.bound = bound;
    }

    @Override
    public boolean hasNext() {
        return this.source.hasNext() && 0 < this.bound;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final T next = this.source.next();
        this.bound--;
        return next;
    }

    @Override
    public void remove() {
        this.source.remove();
    }
}
