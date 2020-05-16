// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.adapter;

import androidx.recyclerview.widget.RecyclerView;

public abstract class FragmentStateAdapter extends Adapter<FragmentViewHolder> implements StatefulAdapter
{
    abstract void placeFragmentInViewHolder(final FragmentViewHolder p0);
    
    abstract boolean shouldDelayFragmentTransactions();
    
    class FragmentMaxLifecycleEnforcer
    {
        abstract void updateFragmentMaxLifecycle(final boolean p0);
    }
}
