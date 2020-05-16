// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import kotlin.jvm.internal.Intrinsics;

public final class ControlInfo
{
    private final String controlId;
    private final CharSequence controlSubtitle;
    private final CharSequence controlTitle;
    private final int deviceType;
    
    public ControlInfo(final String controlId, final CharSequence controlTitle, final CharSequence controlSubtitle, final int deviceType) {
        Intrinsics.checkParameterIsNotNull(controlId, "controlId");
        Intrinsics.checkParameterIsNotNull(controlTitle, "controlTitle");
        Intrinsics.checkParameterIsNotNull(controlSubtitle, "controlSubtitle");
        this.controlId = controlId;
        this.controlTitle = controlTitle;
        this.controlSubtitle = controlSubtitle;
        this.deviceType = deviceType;
    }
    
    public final ControlInfo copy(final String s, final CharSequence charSequence, final CharSequence charSequence2, final int n) {
        Intrinsics.checkParameterIsNotNull(s, "controlId");
        Intrinsics.checkParameterIsNotNull(charSequence, "controlTitle");
        Intrinsics.checkParameterIsNotNull(charSequence2, "controlSubtitle");
        return new ControlInfo(s, charSequence, charSequence2, n);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof ControlInfo) {
                final ControlInfo controlInfo = (ControlInfo)o;
                if (Intrinsics.areEqual(this.controlId, controlInfo.controlId) && Intrinsics.areEqual(this.controlTitle, controlInfo.controlTitle) && Intrinsics.areEqual(this.controlSubtitle, controlInfo.controlSubtitle) && this.deviceType == controlInfo.deviceType) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final String getControlId() {
        return this.controlId;
    }
    
    public final CharSequence getControlSubtitle() {
        return this.controlSubtitle;
    }
    
    public final CharSequence getControlTitle() {
        return this.controlTitle;
    }
    
    public final int getDeviceType() {
        return this.deviceType;
    }
    
    @Override
    public int hashCode() {
        final String controlId = this.controlId;
        int hashCode = 0;
        int hashCode2;
        if (controlId != null) {
            hashCode2 = controlId.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final CharSequence controlTitle = this.controlTitle;
        int hashCode3;
        if (controlTitle != null) {
            hashCode3 = controlTitle.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        final CharSequence controlSubtitle = this.controlSubtitle;
        if (controlSubtitle != null) {
            hashCode = controlSubtitle.hashCode();
        }
        return ((hashCode2 * 31 + hashCode3) * 31 + hashCode) * 31 + Integer.hashCode(this.deviceType);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(':');
        sb.append(this.controlId);
        sb.append(':');
        sb.append(this.controlTitle);
        sb.append(':');
        sb.append(this.deviceType);
        return sb.toString();
    }
    
    public static final class Builder
    {
        public String controlId;
        public CharSequence controlSubtitle;
        public CharSequence controlTitle;
        private int deviceType;
        
        public final ControlInfo build() {
            final String controlId = this.controlId;
            if (controlId == null) {
                Intrinsics.throwUninitializedPropertyAccessException("controlId");
                throw null;
            }
            final CharSequence controlTitle = this.controlTitle;
            if (controlTitle == null) {
                Intrinsics.throwUninitializedPropertyAccessException("controlTitle");
                throw null;
            }
            final CharSequence controlSubtitle = this.controlSubtitle;
            if (controlSubtitle != null) {
                return new ControlInfo(controlId, controlTitle, controlSubtitle, this.deviceType);
            }
            Intrinsics.throwUninitializedPropertyAccessException("controlSubtitle");
            throw null;
        }
        
        public final void setControlId(final String controlId) {
            Intrinsics.checkParameterIsNotNull(controlId, "<set-?>");
            this.controlId = controlId;
        }
        
        public final void setControlSubtitle(final CharSequence controlSubtitle) {
            Intrinsics.checkParameterIsNotNull(controlSubtitle, "<set-?>");
            this.controlSubtitle = controlSubtitle;
        }
        
        public final void setControlTitle(final CharSequence controlTitle) {
            Intrinsics.checkParameterIsNotNull(controlTitle, "<set-?>");
            this.controlTitle = controlTitle;
        }
        
        public final void setDeviceType(final int deviceType) {
            this.deviceType = deviceType;
        }
    }
}
