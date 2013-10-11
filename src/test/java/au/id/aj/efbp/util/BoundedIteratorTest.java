package au.id.aj.efbp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class BoundedIteratorTest {
    @Test
    public void zeroElements() {
        final Iterator<Object> cycle =
            new BoundedIterator<>(Collections.emptySet().iterator(), 0);
        assertFalse(cycle.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void zeroElementsNoSuchElement() {
        final Iterator<Object> cycle =
            new BoundedIterator<>(Collections.emptySet().iterator(), 0);
        cycle.next();
    }

    @Test
    public void oneElementHasNext() {
        final Iterator<Object> cycle =
            new BoundedIterator<>(Collections.emptySet().iterator(), 1);
        assertFalse(cycle.hasNext());
    }

    @Test
    public void oneElement() {
        final Object o = new Object();
        final Iterator<Object> cycle =
            new BoundedIterator<>(Collections.singleton(o).iterator(), 1);
        assertEquals(o, cycle.next());
    }

    @Test
    public void twoElements() {
        final Collection<Object> c = new ArrayList<>(2);
        final Object o1 = new Object();
        c.add(o1);
        final Object o2 = new Object();
        c.add(o2);
        final Iterator<Object> cycle = new BoundedIterator<>(c.iterator(), 2);
        assertTrue(cycle.hasNext());
        assertEquals(o1, cycle.next());
        assertTrue(cycle.hasNext());
        assertEquals(o2, cycle.next());
    }

    @Test
    public void twoElementsBoundedHasNext() {
        final Collection<Object> c = new ArrayList<>(2);
        final Object o1 = new Object();
        c.add(o1);
        final Object o2 = new Object();
        c.add(o2);
        final Iterator<Object> cycle = new BoundedIterator<>(c.iterator(), 1);
        assertTrue(cycle.hasNext());
        assertEquals(o1, cycle.next());
        assertFalse(cycle.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void twoElementsBoundedNext() {
        final Collection<Object> c = new ArrayList<>(2);
        final Object o1 = new Object();
        c.add(o1);
        final Object o2 = new Object();
        c.add(o2);
        final Iterator<Object> cycle = new BoundedIterator<>(c.iterator(), 1);
        assertTrue(cycle.hasNext());
        assertEquals(o1, cycle.next());
        cycle.next();
    }

    @Test
    public void oneElementRemove() {
        final Collection<Object> c = new ArrayList<>(1);
        final Object o = new Object();
        c.add(o);
        final Iterator<Object> cycle = new BoundedIterator<>(c.iterator(), 1);
        assertTrue(cycle.hasNext());
        assertEquals(o, cycle.next());
        cycle.remove();
        assertFalse(cycle.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void oneElementRemoveNoSuchElement() {
        final Collection<Object> c = new ArrayList<>(1);
        final Object o = new Object();
        c.add(o);
        final Iterator<Object> cycle = new BoundedIterator<>(c.iterator(), 1);
        assertEquals(o, cycle.next());
        cycle.remove();
        cycle.next();
    }
}
