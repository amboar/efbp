package au.id.aj.efbp.util;

import java.util.Iterator;

public class Wrap<T> implements Iterable<T> {

    private final Iterator<T> iterator;

    public Wrap(final Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<T> iterator() {
        return this.iterator;
    }
}
