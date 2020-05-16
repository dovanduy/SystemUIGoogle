// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.widget;

import androidx.recyclerview.widget.RecyclerView;

final class FakeDrag
{
    private final ScrollEventAdapter mScrollEventAdapter;
    
    FakeDrag(final ViewPager2 viewPager2, final ScrollEventAdapter mScrollEventAdapter, final RecyclerView recyclerView) {
        this.mScrollEventAdapter = mScrollEventAdapter;
    }
    
    boolean isFakeDragging() {
        return this.mScrollEventAdapter.isFakeDragging();
    }
}
