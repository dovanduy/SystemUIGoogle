// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.controls.ControlStatus;

public final class ControlWrapper extends ElementWrapper
{
    private final ControlStatus controlStatus;
    
    public ControlWrapper(final ControlStatus controlStatus) {
        Intrinsics.checkParameterIsNotNull(controlStatus, "controlStatus");
        super(null);
        this.controlStatus = controlStatus;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ControlWrapper && Intrinsics.areEqual(this.controlStatus, ((ControlWrapper)o).controlStatus));
    }
    
    public final ControlStatus getControlStatus() {
        return this.controlStatus;
    }
    
    @Override
    public int hashCode() {
        final ControlStatus controlStatus = this.controlStatus;
        int hashCode;
        if (controlStatus != null) {
            hashCode = controlStatus.hashCode();
        }
        else {
            hashCode = 0;
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ControlWrapper(controlStatus=");
        sb.append(this.controlStatus);
        sb.append(")");
        return sb.toString();
    }
}
