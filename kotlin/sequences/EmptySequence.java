// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.collections.EmptyIterator;
import java.util.Iterator;

final class EmptySequence implements Sequence, DropTakeSequence
{
    public static final EmptySequence INSTANCE;
    
    static {
        INSTANCE = new EmptySequence();
    }
    
    private EmptySequence() {
    }
    
    @Override
    public Iterator iterator() {
        return EmptyIterator.INSTANCE;
    }
    
    @Override
    public EmptySequence take(final int n) {
        return EmptySequence.INSTANCE;
    }
}
