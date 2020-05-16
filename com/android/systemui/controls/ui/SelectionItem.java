// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import kotlin.jvm.internal.Intrinsics;
import android.graphics.drawable.Drawable;
import android.content.ComponentName;

final class SelectionItem
{
    private final CharSequence appName;
    private final ComponentName componentName;
    private final Drawable icon;
    private final CharSequence structure;
    
    public SelectionItem(final CharSequence appName, final CharSequence structure, final Drawable icon, final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(appName, "appName");
        Intrinsics.checkParameterIsNotNull(structure, "structure");
        Intrinsics.checkParameterIsNotNull(icon, "icon");
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        this.appName = appName;
        this.structure = structure;
        this.icon = icon;
        this.componentName = componentName;
    }
    
    public final SelectionItem copy(final CharSequence charSequence, final CharSequence charSequence2, final Drawable drawable, final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(charSequence, "appName");
        Intrinsics.checkParameterIsNotNull(charSequence2, "structure");
        Intrinsics.checkParameterIsNotNull(drawable, "icon");
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        return new SelectionItem(charSequence, charSequence2, drawable, componentName);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof SelectionItem) {
                final SelectionItem selectionItem = (SelectionItem)o;
                if (Intrinsics.areEqual(this.appName, selectionItem.appName) && Intrinsics.areEqual(this.structure, selectionItem.structure) && Intrinsics.areEqual(this.icon, selectionItem.icon) && Intrinsics.areEqual(this.componentName, selectionItem.componentName)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final CharSequence getAppName() {
        return this.appName;
    }
    
    public final ComponentName getComponentName() {
        return this.componentName;
    }
    
    public final Drawable getIcon() {
        return this.icon;
    }
    
    public final CharSequence getStructure() {
        return this.structure;
    }
    
    public final CharSequence getTitle() {
        CharSequence charSequence;
        if (this.structure.length() == 0) {
            charSequence = this.appName;
        }
        else {
            charSequence = this.structure;
        }
        return charSequence;
    }
    
    @Override
    public int hashCode() {
        final CharSequence appName = this.appName;
        int hashCode = 0;
        int hashCode2;
        if (appName != null) {
            hashCode2 = appName.hashCode();
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
        final Drawable icon = this.icon;
        int hashCode4;
        if (icon != null) {
            hashCode4 = icon.hashCode();
        }
        else {
            hashCode4 = 0;
        }
        final ComponentName componentName = this.componentName;
        if (componentName != null) {
            hashCode = componentName.hashCode();
        }
        return ((hashCode2 * 31 + hashCode3) * 31 + hashCode4) * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SelectionItem(appName=");
        sb.append(this.appName);
        sb.append(", structure=");
        sb.append(this.structure);
        sb.append(", icon=");
        sb.append(this.icon);
        sb.append(", componentName=");
        sb.append(this.componentName);
        sb.append(")");
        return sb.toString();
    }
}
