// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import kotlin.jvm.internal.Intrinsics;
import java.util.List;
import android.content.ComponentName;

public final class StructureInfo
{
    private final ComponentName componentName;
    private final List<ControlInfo> controls;
    private final CharSequence structure;
    
    public StructureInfo(final ComponentName componentName, final CharSequence structure, final List<ControlInfo> controls) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(structure, "structure");
        Intrinsics.checkParameterIsNotNull(controls, "controls");
        this.componentName = componentName;
        this.structure = structure;
        this.controls = controls;
    }
    
    public final StructureInfo copy(final ComponentName componentName, final CharSequence charSequence, final List<ControlInfo> list) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(charSequence, "structure");
        Intrinsics.checkParameterIsNotNull(list, "controls");
        return new StructureInfo(componentName, charSequence, list);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof StructureInfo) {
                final StructureInfo structureInfo = (StructureInfo)o;
                if (Intrinsics.areEqual(this.componentName, structureInfo.componentName) && Intrinsics.areEqual(this.structure, structureInfo.structure) && Intrinsics.areEqual(this.controls, structureInfo.controls)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final ComponentName getComponentName() {
        return this.componentName;
    }
    
    public final List<ControlInfo> getControls() {
        return this.controls;
    }
    
    public final CharSequence getStructure() {
        return this.structure;
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
        final CharSequence structure = this.structure;
        int hashCode3;
        if (structure != null) {
            hashCode3 = structure.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        final List<ControlInfo> controls = this.controls;
        if (controls != null) {
            hashCode = controls.hashCode();
        }
        return (hashCode2 * 31 + hashCode3) * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("StructureInfo(componentName=");
        sb.append(this.componentName);
        sb.append(", structure=");
        sb.append(this.structure);
        sb.append(", controls=");
        sb.append(this.controls);
        sb.append(")");
        return sb.toString();
    }
}
