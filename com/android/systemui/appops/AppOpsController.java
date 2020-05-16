// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.appops;

public interface AppOpsController
{
    void addCallback(final int[] p0, final Callback p1);
    
    public interface Callback
    {
        void onActiveStateChanged(final int p0, final int p1, final String p2, final boolean p3);
    }
}
