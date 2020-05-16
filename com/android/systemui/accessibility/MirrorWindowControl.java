// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.accessibility;

import android.os.IBinder;

public abstract class MirrorWindowControl
{
    public final void destroyControl() {
        throw null;
    }
    
    public abstract void setWindowDelegate(final MirrorWindowDelegate p0);
    
    public final void showControl(final IBinder binder) {
        throw null;
    }
    
    public interface MirrorWindowDelegate
    {
    }
}
