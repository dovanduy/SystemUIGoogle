// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.carrier;

import kotlin.jvm.internal.Intrinsics;

public final class CellSignalState
{
    public final String contentDescription;
    public final int mobileSignalIconId;
    public final boolean roaming;
    public final String typeContentDescription;
    public final boolean visible;
    
    public CellSignalState() {
        this(false, 0, null, null, false, 31, null);
    }
    
    public CellSignalState(final boolean visible, final int mobileSignalIconId, final String contentDescription, final String typeContentDescription, final boolean roaming) {
        this.visible = visible;
        this.mobileSignalIconId = mobileSignalIconId;
        this.contentDescription = contentDescription;
        this.typeContentDescription = typeContentDescription;
        this.roaming = roaming;
    }
    
    public static /* synthetic */ CellSignalState copy$default(final CellSignalState cellSignalState, boolean visible, int mobileSignalIconId, String contentDescription, String typeContentDescription, boolean roaming, final int n, final Object o) {
        if ((n & 0x1) != 0x0) {
            visible = cellSignalState.visible;
        }
        if ((n & 0x2) != 0x0) {
            mobileSignalIconId = cellSignalState.mobileSignalIconId;
        }
        if ((n & 0x4) != 0x0) {
            contentDescription = cellSignalState.contentDescription;
        }
        if ((n & 0x8) != 0x0) {
            typeContentDescription = cellSignalState.typeContentDescription;
        }
        if ((n & 0x10) != 0x0) {
            roaming = cellSignalState.roaming;
        }
        return cellSignalState.copy(visible, mobileSignalIconId, contentDescription, typeContentDescription, roaming);
    }
    
    public final CellSignalState changeVisibility(final boolean b) {
        if (this.visible == b) {
            return this;
        }
        return copy$default(this, b, 0, null, null, false, 30, null);
    }
    
    public final CellSignalState copy(final boolean b, final int n, final String s, final String s2, final boolean b2) {
        return new CellSignalState(b, n, s, s2, b2);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof CellSignalState) {
                final CellSignalState cellSignalState = (CellSignalState)o;
                if (this.visible == cellSignalState.visible && this.mobileSignalIconId == cellSignalState.mobileSignalIconId && Intrinsics.areEqual(this.contentDescription, cellSignalState.contentDescription) && Intrinsics.areEqual(this.typeContentDescription, cellSignalState.typeContentDescription) && this.roaming == cellSignalState.roaming) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int visible = this.visible ? 1 : 0;
        int n = 1;
        int n2 = visible;
        if (visible != 0) {
            n2 = 1;
        }
        final int hashCode = Integer.hashCode(this.mobileSignalIconId);
        final String contentDescription = this.contentDescription;
        int hashCode2 = 0;
        int hashCode3;
        if (contentDescription != null) {
            hashCode3 = contentDescription.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        final String typeContentDescription = this.typeContentDescription;
        if (typeContentDescription != null) {
            hashCode2 = typeContentDescription.hashCode();
        }
        final int roaming = this.roaming ? 1 : 0;
        if (roaming == 0) {
            n = roaming;
        }
        return (((n2 * 31 + hashCode) * 31 + hashCode3) * 31 + hashCode2) * 31 + n;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CellSignalState(visible=");
        sb.append(this.visible);
        sb.append(", mobileSignalIconId=");
        sb.append(this.mobileSignalIconId);
        sb.append(", contentDescription=");
        sb.append(this.contentDescription);
        sb.append(", typeContentDescription=");
        sb.append(this.typeContentDescription);
        sb.append(", roaming=");
        sb.append(this.roaming);
        sb.append(")");
        return sb.toString();
    }
}
