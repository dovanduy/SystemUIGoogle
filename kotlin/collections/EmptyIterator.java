// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.NoSuchElementException;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.ListIterator;

public final class EmptyIterator implements ListIterator, KMappedMarker
{
    public static final EmptyIterator INSTANCE;
    
    static {
        INSTANCE = new EmptyIterator();
    }
    
    private EmptyIterator() {
    }
    
    @Override
    public boolean hasNext() {
        return false;
    }
    
    @Override
    public boolean hasPrevious() {
        return false;
    }
    
    @Override
    public Void next() {
        throw new NoSuchElementException();
    }
    
    @Override
    public int nextIndex() {
        return 0;
    }
    
    @Override
    public Void previous() {
        throw new NoSuchElementException();
    }
    
    @Override
    public int previousIndex() {
        return -1;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
