package au.id.aj.efbp.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CyclicIterator<U> implements Iterator<U> {
    private Iterable<U> us;
    private Iterator<U> current;

    public CyclicIterator(final Iterable<U> us) {
        this.us = us;
        this.current = null;
    }

    private boolean findNext() {
        // Handle the initial case
        if (this.current == null) {
            final Iterator<U> test = this.us.iterator();
            // Check if the iterable has any elements
            if (!test.hasNext()) {
                return false;
            }
            this.current = test;
        } else {
            // Restart iterating by generating a new iterator
            if (!this.current.hasNext()) {
                this.current = this.us.iterator();
            }
        }
        return this.current.hasNext();
    }

    @Override
    public boolean hasNext() {
        return findNext();
    }

    @Override
    public U next() {
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
