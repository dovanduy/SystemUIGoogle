// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import java.util.Iterator;
import java.util.Collection;
import java.util.ListIterator;
import java.util.LinkedList;
import android.view.MotionEvent;
import java.util.List;

public class TimeLimitedMotionEventBuffer implements List<MotionEvent>
{
    private long mMaxAgeMs;
    private final LinkedList<MotionEvent> mMotionEvents;
    
    TimeLimitedMotionEventBuffer(final long mMaxAgeMs) {
        this.mMaxAgeMs = mMaxAgeMs;
        this.mMotionEvents = new LinkedList<MotionEvent>();
    }
    
    private void ejectOldEvents() {
        if (this.mMotionEvents.isEmpty()) {
            return;
        }
        final ListIterator<MotionEvent> listIterator = this.listIterator();
        final long eventTime = this.mMotionEvents.getLast().getEventTime();
        while (listIterator.hasNext()) {
            final MotionEvent motionEvent = listIterator.next();
            if (eventTime - motionEvent.getEventTime() > this.mMaxAgeMs) {
                listIterator.remove();
                motionEvent.recycle();
            }
        }
    }
    
    @Override
    public void add(final int n, final MotionEvent motionEvent) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean add(final MotionEvent e) {
        final boolean add = this.mMotionEvents.add(e);
        this.ejectOldEvents();
        return add;
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends MotionEvent> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends MotionEvent> c) {
        final boolean addAll = this.mMotionEvents.addAll(c);
        this.ejectOldEvents();
        return addAll;
    }
    
    @Override
    public void clear() {
        this.mMotionEvents.clear();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.mMotionEvents.contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.mMotionEvents.containsAll(c);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.mMotionEvents.equals(o);
    }
    
    @Override
    public MotionEvent get(final int index) {
        return this.mMotionEvents.get(index);
    }
    
    @Override
    public int hashCode() {
        return this.mMotionEvents.hashCode();
    }
    
    @Override
    public int indexOf(final Object o) {
        return this.mMotionEvents.indexOf(o);
    }
    
    @Override
    public boolean isEmpty() {
        return this.mMotionEvents.isEmpty();
    }
    
    @Override
    public Iterator<MotionEvent> iterator() {
        return this.mMotionEvents.iterator();
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        return this.mMotionEvents.lastIndexOf(o);
    }
    
    @Override
    public ListIterator<MotionEvent> listIterator() {
        return new Iter(0);
    }
    
    @Override
    public ListIterator<MotionEvent> listIterator(final int n) {
        return new Iter(n);
    }
    
    @Override
    public MotionEvent remove(final int index) {
        return this.mMotionEvents.remove(index);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.mMotionEvents.remove(o);
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.mMotionEvents.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.mMotionEvents.retainAll(c);
    }
    
    @Override
    public MotionEvent set(final int n, final MotionEvent motionEvent) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.mMotionEvents.size();
    }
    
    @Override
    public List<MotionEvent> subList(final int n, final int n2) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object[] toArray() {
        return this.mMotionEvents.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this.mMotionEvents.toArray(a);
    }
    
    class Iter implements ListIterator<MotionEvent>
    {
        private final ListIterator<MotionEvent> mIterator;
        
        Iter(final TimeLimitedMotionEventBuffer timeLimitedMotionEventBuffer, final int index) {
            this.mIterator = timeLimitedMotionEventBuffer.mMotionEvents.listIterator(index);
        }
        
        @Override
        public void add(final MotionEvent motionEvent) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean hasNext() {
            return this.mIterator.hasNext();
        }
        
        @Override
        public boolean hasPrevious() {
            return this.mIterator.hasPrevious();
        }
        
        @Override
        public MotionEvent next() {
            return this.mIterator.next();
        }
        
        @Override
        public int nextIndex() {
            return this.mIterator.nextIndex();
        }
        
        @Override
        public MotionEvent previous() {
            return this.mIterator.previous();
        }
        
        @Override
        public int previousIndex() {
            return this.mIterator.previousIndex();
        }
        
        @Override
        public void remove() {
            this.mIterator.remove();
        }
        
        @Override
        public void set(final MotionEvent motionEvent) {
            throw new UnsupportedOperationException();
        }
    }
}
