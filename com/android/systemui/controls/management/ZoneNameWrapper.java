// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;

public final class ZoneNameWrapper extends ElementWrapper
{
    private final CharSequence zoneName;
    
    public ZoneNameWrapper(final CharSequence zoneName) {
        Intrinsics.checkParameterIsNotNull(zoneName, "zoneName");
        super(null);
        this.zoneName = zoneName;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ZoneNameWrapper && Intrinsics.areEqual(this.zoneName, ((ZoneNameWrapper)o).zoneName));
    }
    
    public final CharSequence getZoneName() {
        return this.zoneName;
    }
    
    @Override
    public int hashCode() {
        final CharSequence zoneName = this.zoneName;
        int hashCode;
        if (zoneName != null) {
            hashCode = zoneName.hashCode();
        }
        else {
            hashCode = 0;
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ZoneNameWrapper(zoneName=");
        sb.append(this.zoneName);
        sb.append(")");
        return sb.toString();
    }
}
