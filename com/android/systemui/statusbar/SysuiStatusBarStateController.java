// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.plugins.statusbar.StatusBarStateController;

public interface SysuiStatusBarStateController extends StatusBarStateController
{
    @Deprecated
    void addCallback(final StateListener p0, final int p1);
    
    boolean fromShadeLocked();
    
    float getInterpolatedDozeAmount();
    
    boolean goingToFullShade();
    
    boolean isKeyguardRequested();
    
    boolean leaveOpenOnKeyguardHide();
    
    void setDozeAmount(final float p0, final boolean p1);
    
    void setFullscreenState(final boolean p0, final boolean p1);
    
    boolean setIsDozing(final boolean p0);
    
    void setKeyguardRequested(final boolean p0);
    
    void setLeaveOpenOnKeyguardHide(final boolean p0);
    
    void setPulsing(final boolean p0);
    
    boolean setState(final int p0);
    
    public static class RankedListener
    {
        final StateListener mListener;
        final int mRank;
        
        RankedListener(final StateListener mListener, final int mRank) {
            this.mListener = mListener;
            this.mRank = mRank;
        }
    }
}
