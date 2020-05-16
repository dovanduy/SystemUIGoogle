// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

public class DockAlignInfo
{
    private final int mAlignPct;
    private final int mAlignState;
    
    public DockAlignInfo(final int mAlignState, final int mAlignPct) {
        this.mAlignState = mAlignState;
        this.mAlignPct = mAlignPct;
    }
    
    public int getAlignPct() {
        return this.mAlignPct;
    }
    
    public int getAlignState() {
        return this.mAlignState;
    }
}
