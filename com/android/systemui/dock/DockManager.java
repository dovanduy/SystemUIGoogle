// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dock;

public interface DockManager
{
    void addAlignmentStateListener(final AlignmentStateListener p0);
    
    void addListener(final DockEventListener p0);
    
    boolean isDocked();
    
    boolean isHidden();
    
    void removeListener(final DockEventListener p0);
    
    public interface AlignmentStateListener
    {
        void onAlignmentStateChanged(final int p0);
    }
    
    public interface DockEventListener
    {
        void onEvent(final int p0);
    }
}
