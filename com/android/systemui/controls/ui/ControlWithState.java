// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import kotlin.jvm.internal.Intrinsics;
import android.service.controls.Control;
import android.content.ComponentName;
import com.android.systemui.controls.controller.ControlInfo;

public final class ControlWithState
{
    private final ControlInfo ci;
    private final ComponentName componentName;
    private final Control control;
    
    public ControlWithState(final ComponentName componentName, final ControlInfo ci, final Control control) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(ci, "ci");
        this.componentName = componentName;
        this.ci = ci;
        this.control = control;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof ControlWithState) {
                final ControlWithState controlWithState = (ControlWithState)o;
                if (Intrinsics.areEqual(this.componentName, controlWithState.componentName) && Intrinsics.areEqual(this.ci, controlWithState.ci) && Intrinsics.areEqual(this.control, controlWithState.control)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final ControlInfo getCi() {
        return this.ci;
    }
    
    public final ComponentName getComponentName() {
        return this.componentName;
    }
    
    public final Control getControl() {
        return this.control;
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
        final ControlInfo ci = this.ci;
        int hashCode3;
        if (ci != null) {
            hashCode3 = ci.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        final Control control = this.control;
        if (control != null) {
            hashCode = control.hashCode();
        }
        return (hashCode2 * 31 + hashCode3) * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ControlWithState(componentName=");
        sb.append(this.componentName);
        sb.append(", ci=");
        sb.append(this.ci);
        sb.append(", control=");
        sb.append(this.control);
        sb.append(")");
        return sb.toString();
    }
}
