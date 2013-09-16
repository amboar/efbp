package au.id.aj.efbp.util;

import java.util.Collection;
import java.util.Iterator;

public class Coil<T> implements Iterable<T> {
    private final Iterator<Iterator<T>> cycler;

    public Coil(final Collection<? extends Iterable<T>> iterables) {
        this.cycler = new Cycle<>(IteratorUtils.convertIterable(iterables)).iterator();
    }

    public Coil(final Cycle<Iterator<T>> cycle) {
        this.cycler = cycle.iterator();
    }

    @Override
    public Iterator<T> iterator() {
        return new CoiledIterator<T>(this.cycler);
    }
}
