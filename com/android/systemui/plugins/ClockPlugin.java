// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.graphics.Paint$Style;
import java.util.TimeZone;
import android.graphics.Bitmap;
import android.view.View;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_CLOCK", version = 5)
public interface ClockPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_CLOCK";
    public static final int VERSION = 5;
    
    default View getBigClockView() {
        return null;
    }
    
    String getName();
    
    int getPreferredY(final int p0);
    
    Bitmap getPreview(final int p0, final int p1);
    
    Bitmap getThumbnail();
    
    String getTitle();
    
    View getView();
    
    void onDestroyView();
    
    default void onTimeTick() {
    }
    
    default void onTimeZoneChanged(final TimeZone timeZone) {
    }
    
    default void setColorPalette(final boolean b, final int[] array) {
    }
    
    default void setDarkAmount(final float n) {
    }
    
    void setStyle(final Paint$Style p0);
    
    void setTextColor(final int p0);
    
    default boolean shouldShowStatusArea() {
        return true;
    }
}
