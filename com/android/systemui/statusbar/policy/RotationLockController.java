// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

public interface RotationLockController extends Object, CallbackController<RotationLockControllerCallback>
{
    int getRotationLockOrientation();
    
    boolean isRotationLocked();
    
    void setRotationLocked(final boolean p0);
    
    void setRotationLockedAtAngle(final boolean p0, final int p1);
    
    public interface RotationLockControllerCallback
    {
        void onRotationLockStateChanged(final boolean p0, final boolean p1);
    }
}
