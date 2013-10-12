package au.id.aj.efbp.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class IteratorUtils {
    private IteratorUtils() {
    }

    public static <U> Iterable<Iterator<U>> convertIterable(
            final Iterable<? extends Iterable<U>> iterables) {
        final List<Iterator<U>> iterators = new LinkedList<>();
        for (Iterable<U> i : iterables) {
            iterators.add(i.iterator());
        }
        return iterators;
    }

    public static <U> Iterator<Iterator<U>> convertIterator(
            final Iterable<? extends Iterable<U>> iterables) {
        return convertIterable(iterables).iterator();
    }
}
