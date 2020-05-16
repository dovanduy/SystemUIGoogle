// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import kotlin.jvm.internal.Intrinsics;
import android.content.ComponentName;

final class ControlKey
{
    private final ComponentName componentName;
    private final String controlId;
    
    public ControlKey(final ComponentName componentName, final String controlId) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(controlId, "controlId");
        this.componentName = componentName;
        this.controlId = controlId;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof ControlKey) {
                final ControlKey controlKey = (ControlKey)o;
                if (Intrinsics.areEqual(this.componentName, controlKey.componentName) && Intrinsics.areEqual(this.controlId, controlKey.controlId)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final ComponentName componentName = this.componentName;
        int hashCode = 0;
        int hashCode2;
        if (componentName != null) {
            hashCode2 = componentName.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final String controlId = this.controlId;
        if (controlId != null) {
            hashCode = controlId.hashCode();
        }
        return hashCode2 * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ControlKey(componentName=");
        sb.append(this.componentName);
        sb.append(", controlId=");
        sb.append(this.controlId);
        sb.append(")");
        return sb.toString();
    }
}
