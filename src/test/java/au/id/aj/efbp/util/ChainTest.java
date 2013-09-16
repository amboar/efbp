package au.id.aj.efbp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

public class ChainTest {
    @Test
    public void zeroIterators() {
        final Collection<Iterable<Object>> iterables =
            Collections.<Iterable<Object>>emptySet();
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertFalse(chain.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void zeroIteratorsNoSuchElement() {
        final Collection<Iterable<Object>> iterables =
            Collections.<Iterable<Object>>emptySet();
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        chain.next();
    }

    @Test
    public void oneEmptyIterator() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        iterables.add(Collections.emptySet());
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertFalse(chain.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void oneEmptyIteratorNoSuchElement() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        iterables.add(Collections.emptySet());
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        chain.next();
    }

    @Test
    public void twoEmptyIterators() {
        final List<Iterable<Object>> iterables = new ArrayList<>(2);
        iterables.add(Collections.emptySet());
        iterables.add(Collections.emptySet());
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertFalse(chain.hasNext());
    }

    @Test
    public void onePopulatedIterator() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o = new Object();
        iterables.add(Collections.singleton(o));
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertEquals(o, chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void onePopulatedIteratorIdempotentHasNext() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o = new Object();
        iterables.add(Collections.singleton(o));
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertTrue(chain.hasNext());
        assertEquals(o, chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void twoIteratorsPopulatedEmpty() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o = new Object();
        iterables.add(Collections.singleton(o));
        iterables.add(Collections.emptySet());
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertEquals(o, chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void twoIteratorsEmptyPopulated() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o = new Object();
        iterables.add(Collections.emptySet());
        iterables.add(Collections.singleton(o));
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertEquals(o, chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void twoIteratorsPopulatedPopulated() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o1 = new Object();
        iterables.add(Collections.singleton(o1));
        final Object o2 = new Object();
        iterables.add(Collections.singleton(o2));
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertEquals(o1, chain.next());
        assertTrue(chain.hasNext());
        assertEquals(o2, chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void threeIteratorsAllPopulated() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o1 = new Object();
        iterables.add(Collections.singleton(o1));
        final Object o2 = new Object();
        iterables.add(Collections.singleton(o2));
        final Object o3 = new Object();
        iterables.add(Collections.singleton(o3));
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertEquals(o1, chain.next());
        assertTrue(chain.hasNext());
        assertEquals(o2, chain.next());
        assertTrue(chain.hasNext());
        assertEquals(o3, chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void threeIteratorsPopulatedEmptyPopulated() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o1 = new Object();
        iterables.add(Collections.singleton(o1));
        iterables.add(Collections.emptySet());
        final Object o3 = new Object();
        iterables.add(Collections.singleton(o3));
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertEquals(o1, chain.next());
        assertTrue(chain.hasNext());
        assertEquals(o3, chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void remove() {
        final List<Iterable<Object>> iterables = new ArrayList<>(1);
        final Object o = new Object();
        final List<Object> l = new ArrayList<>(1);
        l.add(o);
        iterables.add(l);
        final Iterator<Object> chain =
            new Chain<Object>(iterables).iterator();
        assertTrue(chain.hasNext());
        assertEquals(o, chain.next());
        chain.remove();
        assertFalse(chain.hasNext());
        assertTrue(l.isEmpty());
    }
}
