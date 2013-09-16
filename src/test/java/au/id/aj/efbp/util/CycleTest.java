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

public class CycleTest {
    @Test
    public void zeroElements() {
        final Iterator<Object> cycle =
            new Cycle<>(Collections.emptySet()).iterator();
        assertFalse(cycle.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void zeroElementsNoSuchElement() {
        final Iterator<Object> cycle =
            new Cycle<>(Collections.emptySet()).iterator();
        cycle.next();
    }

    @Test
    public void oneElementHasNext() {
        final Iterator<Object> cycle =
            new Cycle<>(Collections.emptySet()).iterator();
        assertFalse(cycle.hasNext());
    }

    @Test
    public void oneElementOnce() {
        final Object o = new Object();
        final Iterator<Object> cycle =
            new Cycle<>(Collections.singleton(o)).iterator();
        assertEquals(o, cycle.next());
    }

    @Test
    public void oneElementTwice() {
        final Object o = new Object();
        final Iterator<Object> cycle =
            new Cycle<>(Collections.singleton(o)).iterator();
        assertEquals(o, cycle.next());
        assertEquals(o, cycle.next());
    }

    @Test
    public void twoElementsOnce() {
        final Collection<Object> c = new ArrayList<>(2);
        final Object o1 = new Object();
        c.add(o1);
        final Object o2 = new Object();
        c.add(o2);
        final Iterator<Object> cycle = new Cycle<>(c).iterator();
        assertTrue(cycle.hasNext());
        assertEquals(o1, cycle.next());
        assertTrue(cycle.hasNext());
        assertEquals(o2, cycle.next());
    }

    @Test
    public void twoElementsTwice() {
        final Collection<Object> c = new ArrayList<>(2);
        final Object o1 = new Object();
        c.add(o1);
        final Object o2 = new Object();
        c.add(o2);
        final Iterator<Object> cycle = new Cycle<>(c).iterator();
        assertTrue(cycle.hasNext());
        assertEquals(o1, cycle.next());
        assertTrue(cycle.hasNext());
        assertEquals(o2, cycle.next());
        assertTrue(cycle.hasNext());
        assertEquals(o1, cycle.next());
        assertTrue(cycle.hasNext());
        assertEquals(o2, cycle.next());
    }

    @Test
    public void oneElementRemove() {
        final Collection<Object> c = new ArrayList<>(1);
        final Object o = new Object();
        c.add(o);
        final Iterator<Object> cycle = new Cycle<>(c).iterator();
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
        final Iterator<Object> cycle = new Cycle<>(c).iterator();
        assertEquals(o, cycle.next());
        cycle.remove();
        cycle.next();
    }
}
