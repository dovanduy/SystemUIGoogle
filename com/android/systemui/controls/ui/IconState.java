// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

public final class IconState
{
    private final int disabledResourceId;
    private final int enabledResourceId;
    
    public IconState(final int disabledResourceId, final int enabledResourceId) {
        this.disabledResourceId = disabledResourceId;
        this.enabledResourceId = enabledResourceId;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof IconState) {
                final IconState iconState = (IconState)o;
                if (this.disabledResourceId == iconState.disabledResourceId && this.enabledResourceId == iconState.enabledResourceId) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final int get(final boolean b) {
        int n;
        if (b) {
            n = this.enabledResourceId;
        }
        else {
            n = this.disabledResourceId;
        }
        return n;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(this.disabledResourceId) * 31 + Integer.hashCode(this.enabledResourceId);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("IconState(disabledResourceId=");
        sb.append(this.disabledResourceId);
        sb.append(", enabledResourceId=");
        sb.append(this.enabledResourceId);
        sb.append(")");
        return sb.toString();
    }
}
