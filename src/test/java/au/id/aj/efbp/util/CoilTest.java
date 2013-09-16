package au.id.aj.efbp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Test;

public class CoilTest {
    @Test
    public void zeroElementsOneListHasNext() {
        final Set<Set<Object>> collection =
            Collections.singleton(Collections.emptySet());
        final Iterator<Object> coil = new Coil<Object>(collection).iterator();
        assertFalse(coil.hasNext());
    }

    @Test (expected = NoSuchElementException.class)
    public void zeroElementsNext() {
        final Set<Set<Object>> collection =
            Collections.singleton(Collections.emptySet());
        final Iterator<Object> coil = new Coil<>(collection).iterator();
        assertNotNull(coil.next());
    }

    @Test (expected = IllegalStateException.class)
    public void zeroElementsRemove() {
        final Set<Set<Object>> collection =
            Collections.singleton(Collections.emptySet());
        final Iterator<Object> coil = new Coil<>(collection).iterator();
        coil.remove();
    }

    @Test
    public void oneElementOneListHasNext() {
        final Collection<Object> c = new ArrayList<>(1);
        c.add(new Object());
        final Iterator<Object> coil =
            new Coil<>(Collections.singleton(c)).iterator();
        assertTrue(coil.hasNext());
    }

    @Test
    public void oneElementOneListHasNextIdempotent() {
        final Collection<Object> c = new ArrayList<>(1);
        c.add(new Object());
        final Iterator<Object> coil =
            new Coil<>(Collections.singleton(c)).iterator();
        assertTrue(coil.hasNext());
        assertTrue(coil.hasNext());
    }

    @Test
    public void oneElementOneListNext() {
        final Object o = new Object();
        final Collection<Object> c = new ArrayList<>(1);
        c.add(o);
        final Iterator<Object> coil =
            new Coil<>(Collections.singleton(c)).iterator();
        assertEquals(o, coil.next());
        assertFalse(coil.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void oneElementOneListNextNoSuchElement() {
        final Object o = new Object();
        final Collection<Object> c = new ArrayList<>(1);
        c.add(o);
        final Iterator<Object> coil =
            new Coil<>(Collections.singleton(c)).iterator();
        assertEquals(o, coil.next());
        coil.next();
    }

    @Test
    public void oneElementTwoListsNext() {
        final Object o = new Object();
        final Collection<Object> c = new ArrayList<>(1);
        c.add(o);
        final Collection<Collection<Object>> p = new ArrayList<>(2);
        p.add(Collections.emptySet());
        p.add(c);
        final Iterator<Object> coil = new Coil<>(p).iterator();
        assertEquals(o, coil.next());
        assertFalse(coil.hasNext());
    }

    @Test
    public void oneElementOneListRemove() {
        final Object o = new Object();
        final Collection<Object> c = new ArrayList<>(1);
        c.add(o);
        final Iterator<Object> coil =
            new Coil<>(Collections.singleton(c)).iterator();
        assertEquals(o, coil.next());
        coil.remove();
    }

    @Test
    public void twoElementsOneList() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Collection<Object> c = new ArrayList<>(2);
        c.add(o1);
        c.add(o2);
        final Iterator<Object> coil =
            new Coil<>(Collections.singleton(c)).iterator();
        assertEquals(o1, coil.next());
        assertEquals(o2, coil.next());
        assertFalse(coil.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void twoElementsOneListNoSuchElement() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Collection<Object> c = new ArrayList<>(2);
        c.add(o1);
        c.add(o2);
        final Iterator<Object> coil =
            new Coil<>(Collections.singleton(c)).iterator();
        assertEquals(o1, coil.next());
        assertEquals(o2, coil.next());
        coil.next();
    }

    @Test
    public void twoElementsTwoLists() {
        final Collection<Collection<Object>> p = new ArrayList<>(2);
        final Object o1 = new Object();
        final Collection<Object> c1 = new ArrayList<>(1);
        c1.add(o1);
        p.add(c1);
        final Object o2 = new Object();
        final Collection<Object> c2 = new ArrayList<>(1);
        c2.add(o2);
        p.add(c2);
        final Iterator<Object> coil = new Coil<>(p).iterator();
        assertEquals(o1, coil.next());
        assertEquals(o2, coil.next());
        assertFalse(coil.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void twoElementsTwoListsNoSuchElement() {
        final Collection<Collection<Object>> p = new ArrayList<>(2);
        final Object o1 = new Object();
        final Collection<Object> c1 = new ArrayList<>(1);
        c1.add(o1);
        p.add(c1);
        final Object o2 = new Object();
        final Collection<Object> c2 = new ArrayList<>(1);
        c2.add(o2);
        p.add(c2);
        final Iterator<Object> coil = new Coil<>(p).iterator();
        assertEquals(o1, coil.next());
        assertEquals(o2, coil.next());
        coil.next();
    }

    @Test
    public void threeElementsTwoLists() {
        final Collection<Collection<Object>> p = new ArrayList<>(2);
        final Collection<Object> c1 = new ArrayList<>(1);
        final Object o1 = new Object();
        c1.add(o1);
        final Object o2 = new Object();
        c1.add(o2);
        p.add(c1);
        final Object o3 = new Object();
        final Collection<Object> c2 = new ArrayList<>(1);
        c2.add(o3);
        p.add(c2);
        final Iterator<Object> coil = new Coil<>(p).iterator();
        assertEquals(o1, coil.next());
        assertEquals(o3, coil.next());
        assertEquals(o2, coil.next());
        assertFalse(coil.hasNext());
    }
}
