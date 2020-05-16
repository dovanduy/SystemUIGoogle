// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder.pluggable;

public abstract class Pluggable<This>
{
    private PluggableListener<This> mListener;
    private final String mName;
    
    Pluggable(final String mName) {
        this.mName = mName;
    }
    
    public final String getName() {
        return this.mName;
    }
    
    public final void invalidateList() {
        final PluggableListener<This> mListener = this.mListener;
        if (mListener != null) {
            mListener.onPluggableInvalidated((This)this);
        }
    }
    
    public void setInvalidationListener(final PluggableListener<This> mListener) {
        this.mListener = mListener;
    }
    
    public interface PluggableListener<T>
    {
        void onPluggableInvalidated(final T p0);
    }
}
