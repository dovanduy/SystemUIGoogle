// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.content.res.Resources$Theme;
import com.android.systemui.R$drawable;
import java.util.Map;
import kotlin.collections.MapsKt;
import kotlin.Pair;
import android.content.Context;
import kotlin.jvm.internal.Intrinsics;
import android.util.SparseArray;
import android.graphics.drawable.Drawable;
import android.content.ComponentName;
import android.util.ArrayMap;

public final class RenderInfo
{
    public static final Companion Companion;
    private static final ArrayMap<ComponentName, Drawable> appIconMap;
    private static final SparseArray<Drawable> iconMap;
    private final int enabledBackground;
    private final int foreground;
    private final Drawable icon;
    
    static {
        Companion = new Companion(null);
        iconMap = new SparseArray();
        appIconMap = new ArrayMap();
    }
    
    public RenderInfo(final Drawable icon, final int foreground, final int enabledBackground) {
        Intrinsics.checkParameterIsNotNull(icon, "icon");
        this.icon = icon;
        this.foreground = foreground;
        this.enabledBackground = enabledBackground;
    }
    
    public static final /* synthetic */ ArrayMap access$getAppIconMap$cp() {
        return RenderInfo.appIconMap;
    }
    
    public static final /* synthetic */ SparseArray access$getIconMap$cp() {
        return RenderInfo.iconMap;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof RenderInfo) {
                final RenderInfo renderInfo = (RenderInfo)o;
                if (Intrinsics.areEqual(this.icon, renderInfo.icon) && this.foreground == renderInfo.foreground && this.enabledBackground == renderInfo.enabledBackground) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final int getEnabledBackground() {
        return this.enabledBackground;
    }
    
    public final int getForeground() {
        return this.foreground;
    }
    
    public final Drawable getIcon() {
        return this.icon;
    }
    
    @Override
    public int hashCode() {
        final Drawable icon = this.icon;
        int hashCode;
        if (icon != null) {
            hashCode = icon.hashCode();
        }
        else {
            hashCode = 0;
        }
        return (hashCode * 31 + Integer.hashCode(this.foreground)) * 31 + Integer.hashCode(this.enabledBackground);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RenderInfo(icon=");
        sb.append(this.icon);
        sb.append(", foreground=");
        sb.append(this.foreground);
        sb.append(", enabledBackground=");
        sb.append(this.enabledBackground);
        sb.append(")");
        return sb.toString();
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final void clearCache() {
            RenderInfo.access$getIconMap$cp().clear();
            RenderInfo.access$getAppIconMap$cp().clear();
        }
        
        public final RenderInfo lookup(final Context context, final ComponentName componentName, int intValue, final boolean b, int intValue2) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(componentName, "componentName");
            int n = intValue;
            if (intValue2 > 0) {
                n = intValue * 1000 + intValue2;
            }
            final Pair<Number, B> pair = MapsKt.getValue((Map<Integer, ? extends Pair<Number, B>>)RenderInfoKt.access$getDeviceColorMap$p(), Integer.valueOf(n));
            intValue2 = pair.component1().intValue();
            intValue = ((Number)pair.component2()).intValue();
            final int value = MapsKt.getValue((Map<Integer, ? extends IconState>)RenderInfoKt.access$getDeviceIconMap$p(), Integer.valueOf(n)).get(b);
            Drawable drawable;
            if (value == -1) {
                if ((drawable = (Drawable)RenderInfo.access$getAppIconMap$cp().get((Object)componentName)) == null) {
                    drawable = context.getResources().getDrawable(R$drawable.ic_device_unknown_gm2_24px, (Resources$Theme)null);
                    RenderInfo.access$getAppIconMap$cp().put((Object)componentName, (Object)drawable);
                }
            }
            else {
                drawable = (Drawable)RenderInfo.access$getIconMap$cp().get(value);
                if (drawable == null) {
                    drawable = context.getResources().getDrawable(value, (Resources$Theme)null);
                    drawable.mutate();
                    RenderInfo.access$getIconMap$cp().put(value, (Object)drawable);
                }
            }
            if (drawable != null) {
                return new RenderInfo(drawable, intValue2, intValue);
            }
            Intrinsics.throwNpe();
            throw null;
        }
        
        public final void registerComponentIcon(final ComponentName componentName, final Drawable drawable) {
            Intrinsics.checkParameterIsNotNull(componentName, "componentName");
            Intrinsics.checkParameterIsNotNull(drawable, "icon");
            RenderInfo.access$getAppIconMap$cp().put((Object)componentName, (Object)drawable);
        }
    }
}
