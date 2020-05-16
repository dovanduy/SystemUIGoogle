// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls;

import kotlin.jvm.internal.Intrinsics;
import android.service.controls.Control;
import android.content.ComponentName;

public final class ControlStatus
{
    private final ComponentName component;
    private final Control control;
    private boolean favorite;
    private final boolean removed;
    
    public ControlStatus(final Control control, final ComponentName component, final boolean favorite, final boolean removed) {
        Intrinsics.checkParameterIsNotNull(control, "control");
        Intrinsics.checkParameterIsNotNull(component, "component");
        this.control = control;
        this.component = component;
        this.favorite = favorite;
        this.removed = removed;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof ControlStatus) {
                final ControlStatus controlStatus = (ControlStatus)o;
                if (Intrinsics.areEqual(this.control, controlStatus.control) && Intrinsics.areEqual(this.component, controlStatus.component) && this.favorite == controlStatus.favorite && this.removed == controlStatus.removed) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final ComponentName getComponent() {
        return this.component;
    }
    
    public final Control getControl() {
        return this.control;
    }
    
    public final boolean getFavorite() {
        return this.favorite;
    }
    
    public final boolean getRemoved() {
        return this.removed;
    }
    
    @Override
    public int hashCode() {
        final Control control = this.control;
        int hashCode = 0;
        int hashCode2;
        if (control != null) {
            hashCode2 = control.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final ComponentName component = this.component;
        if (component != null) {
            hashCode = component.hashCode();
        }
        final int favorite = this.favorite ? 1 : 0;
        int n = 1;
        int n2 = favorite;
        if (favorite != 0) {
            n2 = 1;
        }
        final int removed = this.removed ? 1 : 0;
        if (removed == 0) {
            n = removed;
        }
        return ((hashCode2 * 31 + hashCode) * 31 + n2) * 31 + n;
    }
    
    public final void setFavorite(final boolean favorite) {
        this.favorite = favorite;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ControlStatus(control=");
        sb.append(this.control);
        sb.append(", component=");
        sb.append(this.component);
        sb.append(", favorite=");
        sb.append(this.favorite);
        sb.append(", removed=");
        sb.append(this.removed);
        sb.append(")");
        return sb.toString();
    }
}
