// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.widget;

import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.ArrayList;
import java.util.List;

final class CompositeOnPageChangeCallback extends OnPageChangeCallback
{
    private final List<OnPageChangeCallback> mCallbacks;
    
    CompositeOnPageChangeCallback(final int initialCapacity) {
        this.mCallbacks = new ArrayList<OnPageChangeCallback>(initialCapacity);
    }
    
    private void throwCallbackListModifiedWhileInUse(final ConcurrentModificationException cause) {
        throw new IllegalStateException("Adding and removing callbacks during dispatch to callbacks is not supported", cause);
    }
    
    void addOnPageChangeCallback(final OnPageChangeCallback onPageChangeCallback) {
        this.mCallbacks.add(onPageChangeCallback);
    }
    
    @Override
    public void onPageScrollStateChanged(final int n) {
        try {
            final Iterator<OnPageChangeCallback> iterator = this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onPageScrollStateChanged(n);
            }
        }
        catch (ConcurrentModificationException ex) {
            this.throwCallbackListModifiedWhileInUse(ex);
            throw null;
        }
    }
    
    @Override
    public void onPageScrolled(final int n, final float n2, final int n3) {
        try {
            final Iterator<OnPageChangeCallback> iterator = this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onPageScrolled(n, n2, n3);
            }
        }
        catch (ConcurrentModificationException ex) {
            this.throwCallbackListModifiedWhileInUse(ex);
            throw null;
        }
    }
    
    @Override
    public void onPageSelected(final int n) {
        try {
            final Iterator<OnPageChangeCallback> iterator = this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onPageSelected(n);
            }
        }
        catch (ConcurrentModificationException ex) {
            this.throwCallbackListModifiedWhileInUse(ex);
            throw null;
        }
    }
}
