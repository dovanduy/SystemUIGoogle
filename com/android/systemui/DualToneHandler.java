// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import kotlin.TypeCastException;
import android.animation.ArgbEvaluator;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;

public final class DualToneHandler
{
    private Color darkColor;
    private Color lightColor;
    
    public DualToneHandler(final Context colorsFromContext) {
        Intrinsics.checkParameterIsNotNull(colorsFromContext, "context");
        this.setColorsFromContext(colorsFromContext);
    }
    
    private final int getColorForDarkIntensity(final float n, final int i, final int j) {
        final Object evaluate = ArgbEvaluator.getInstance().evaluate(n, (Object)i, (Object)j);
        if (evaluate != null) {
            return (int)evaluate;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.Int");
    }
    
    public final int getBackgroundColor(final float n) {
        final Color lightColor = this.lightColor;
        if (lightColor == null) {
            Intrinsics.throwUninitializedPropertyAccessException("lightColor");
            throw null;
        }
        final int background = lightColor.getBackground();
        final Color darkColor = this.darkColor;
        if (darkColor != null) {
            return this.getColorForDarkIntensity(n, background, darkColor.getBackground());
        }
        Intrinsics.throwUninitializedPropertyAccessException("darkColor");
        throw null;
    }
    
    public final int getFillColor(final float n) {
        final Color lightColor = this.lightColor;
        if (lightColor == null) {
            Intrinsics.throwUninitializedPropertyAccessException("lightColor");
            throw null;
        }
        final int fill = lightColor.getFill();
        final Color darkColor = this.darkColor;
        if (darkColor != null) {
            return this.getColorForDarkIntensity(n, fill, darkColor.getFill());
        }
        Intrinsics.throwUninitializedPropertyAccessException("darkColor");
        throw null;
    }
    
    public final int getSingleColor(final float n) {
        final Color lightColor = this.lightColor;
        if (lightColor == null) {
            Intrinsics.throwUninitializedPropertyAccessException("lightColor");
            throw null;
        }
        final int single = lightColor.getSingle();
        final Color darkColor = this.darkColor;
        if (darkColor != null) {
            return this.getColorForDarkIntensity(n, single, darkColor.getSingle());
        }
        Intrinsics.throwUninitializedPropertyAccessException("darkColor");
        throw null;
    }
    
    public final void setColorsFromContext(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.darkIconTheme));
        final ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.lightIconTheme));
        this.darkColor = new Color(Utils.getColorAttrDefaultColor((Context)contextThemeWrapper, R$attr.singleToneColor), Utils.getColorAttrDefaultColor((Context)contextThemeWrapper, R$attr.backgroundColor), Utils.getColorAttrDefaultColor((Context)contextThemeWrapper, R$attr.fillColor));
        this.lightColor = new Color(Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.singleToneColor), Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.backgroundColor), Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.fillColor));
    }
    
    private static final class Color
    {
        private final int background;
        private final int fill;
        private final int single;
        
        public Color(final int single, final int background, final int fill) {
            this.single = single;
            this.background = background;
            this.fill = fill;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof Color) {
                    final Color color = (Color)o;
                    if (this.single == color.single && this.background == color.background && this.fill == color.fill) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public final int getBackground() {
            return this.background;
        }
        
        public final int getFill() {
            return this.fill;
        }
        
        public final int getSingle() {
            return this.single;
        }
        
        @Override
        public int hashCode() {
            return (Integer.hashCode(this.single) * 31 + Integer.hashCode(this.background)) * 31 + Integer.hashCode(this.fill);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Color(single=");
            sb.append(this.single);
            sb.append(", background=");
            sb.append(this.background);
            sb.append(", fill=");
            sb.append(this.fill);
            sb.append(")");
            return sb.toString();
        }
    }
}
