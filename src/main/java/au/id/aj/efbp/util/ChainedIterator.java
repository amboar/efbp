package au.id.aj.efbp.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates all elements of all iterators.
 */
public class ChainedIterator<T> implements Iterator<T> {
    private final Iterator<Iterator<T>> iteratorIterator;
    private Iterator<T> current;

    public ChainedIterator(final Iterator<Iterator<T>> iterators) {
        this.iteratorIterator = iterators;
        this.current = null;
    }

    private boolean findNext() {
        while (this.current == null || !this.current.hasNext()) {
            if (this.iteratorIterator.hasNext()) {
                this.current = this.iteratorIterator.next();
                assert (this.current != null);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasNext() {
        return findNext();
    }

    @Override
    public T next() {
        if (!findNext()) {
            throw new NoSuchElementException();
        }
        return this.current.next();
    }

    @Override
    public void remove() {
        if (this.current == null) {
            throw new IllegalStateException();
        }
        this.current.remove();
    }
}
