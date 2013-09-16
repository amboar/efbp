package au.id.aj.efbp.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CoiledIterator<T> implements Iterator<T> {
    private Iterator<Iterator<T>> cycle;
    private Iterator<T> iterator;
    private T element;
    private boolean valid;

    public CoiledIterator(final Iterator<Iterator<T>> cycle) {
        if (!(cycle instanceof CyclicIterator)) {
            throw new IllegalArgumentException("Requires Cycle.CycleIterator");
        }
        this.cycle = cycle;
    }

    private boolean findNext() {
        if (this.valid) {
            return true;
        }
        this.valid = false;
        boolean empty;
        do {
            if (!this.cycle.hasNext()) {
                return false;
            }
            this.iterator = this.cycle.next();
            empty = !this.iterator.hasNext();
            if (empty) {
                this.cycle.remove();
            }
        } while (empty);
        assert (this.iterator != null);
        assert (this.iterator.hasNext());
        this.element = this.iterator.next();
        this.valid = true;
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
        try {
            return this.element;
        } finally {
            this.valid = false;
        }
    }

    @Override
    public void remove() {
        if (this.iterator == null) {
            throw new IllegalStateException();
        }
        this.iterator.remove();
    }
}
