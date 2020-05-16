// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

public interface DataSaverController extends CallbackController<Listener>
{
    boolean isDataSaverEnabled();
    
    void setDataSaverEnabled(final boolean p0);
    
    public interface Listener
    {
        void onDataSaverChanged(final boolean p0);
    }
}
