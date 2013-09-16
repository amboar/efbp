package au.id.aj.efbp.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class CapturedCyclicIterator<T> implements Iterator<T> {
    private Iterator<T> provided;
    private List<T> captured;
    private boolean exhausted;

    public CapturedCyclicIterator(final Iterator<T> iterator) {
        this.exhausted = false;
        this.provided = iterator;
        this.captured = new LinkedList<>();
    }

    @Override
    public boolean hasNext() {
        // I think this is wrong as we replace provided
        if (!this.provided.hasNext()) {
            if (this.captured.isEmpty()) {
                return false;
            }
            this.exhausted = true;
            this.provided = this.captured.iterator();
        }
        return this.provided.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final T u = this.provided.next();
        if (!this.exhausted) {
            this.captured.add(u);
        }
        return u;
    }

    @Override
    public void remove() {
        this.provided.remove();
        if (!this.exhausted) {
            this.captured.remove(this.captured.size() - 1);
        }
    }
}

