// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

public interface BootCompleteCache
{
    boolean addListener(final BootCompleteListener p0);
    
    boolean isBootComplete();
    
    void removeListener(final BootCompleteListener p0);
    
    public interface BootCompleteListener
    {
        void onBootComplete();
    }
}
