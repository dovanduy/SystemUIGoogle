// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;

public final class StructureContainer
{
    private final ControlsModel model;
    private final CharSequence structureName;
    
    public StructureContainer(final CharSequence structureName, final ControlsModel model) {
        Intrinsics.checkParameterIsNotNull(structureName, "structureName");
        Intrinsics.checkParameterIsNotNull(model, "model");
        this.structureName = structureName;
        this.model = model;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof StructureContainer) {
                final StructureContainer structureContainer = (StructureContainer)o;
                if (Intrinsics.areEqual(this.structureName, structureContainer.structureName) && Intrinsics.areEqual(this.model, structureContainer.model)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final ControlsModel getModel() {
        return this.model;
    }
    
    public final CharSequence getStructureName() {
        return this.structureName;
    }
    
    @Override
    public int hashCode() {
        final CharSequence structureName = this.structureName;
        int hashCode = 0;
        int hashCode2;
        if (structureName != null) {
            hashCode2 = structureName.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final ControlsModel model = this.model;
        if (model != null) {
            hashCode = model.hashCode();
        }
        return hashCode2 * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("StructureContainer(structureName=");
        sb.append(this.structureName);
        sb.append(", model=");
        sb.append(this.model);
        sb.append(")");
        return sb.toString();
    }
}
