package au.id.aj.efbp.util;

import java.util.Iterator;

/**
 * Given a set of iterables, provides an iterable X such that X.iterator() will
 * iterate all elements in all provided iterables.
 */
public class Chain<T> implements Iterable<T> {

    private final Iterable<Iterable<T>> iterables;

    public Chain(final Iterable<Iterable<T>> iterables) {
        this.iterables = iterables;
    }

    @Override
    public Iterator<T> iterator() {
        return new ChainedIterator<T>(IteratorUtils.convertIterator(this.iterables));
    }
}
