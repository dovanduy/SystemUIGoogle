// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

public abstract class PipTouchGesture
{
    public abstract void onDown(final PipTouchState p0);
    
    public abstract boolean onMove(final PipTouchState p0);
    
    public abstract boolean onUp(final PipTouchState p0);
}
