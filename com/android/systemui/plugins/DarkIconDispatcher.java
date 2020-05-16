// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.widget.ImageView;
import android.view.View;
import android.graphics.Rect;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = DarkReceiver.class)
@ProvidesInterface(version = 1)
public interface DarkIconDispatcher
{
    public static final int DEFAULT_ICON_TINT = -1;
    public static final int VERSION = 1;
    public static final int[] sTmpInt2 = new int[2];
    public static final Rect sTmpRect = new Rect();
    
    default float getDarkIntensity(final Rect rect, final View view, final float n) {
        if (isInArea(rect, view)) {
            return n;
        }
        return 0.0f;
    }
    
    default int getTint(final Rect rect, final View view, final int n) {
        if (isInArea(rect, view)) {
            return n;
        }
        return -1;
    }
    
    default boolean isInArea(final Rect rect, final View view) {
        final boolean empty = rect.isEmpty();
        boolean b = true;
        if (empty) {
            return true;
        }
        DarkIconDispatcher.sTmpRect.set(rect);
        view.getLocationOnScreen(DarkIconDispatcher.sTmpInt2);
        final int a = DarkIconDispatcher.sTmpInt2[0];
        final int max = Math.max(0, Math.min(a + view.getWidth(), rect.right) - Math.max(a, rect.left));
        final boolean b2 = rect.top <= 0;
        if (max * 2 <= view.getWidth() || !b2) {
            b = false;
        }
        return b;
    }
    
    void addDarkReceiver(final ImageView p0);
    
    void addDarkReceiver(final DarkReceiver p0);
    
    void applyDark(final DarkReceiver p0);
    
    void removeDarkReceiver(final ImageView p0);
    
    void removeDarkReceiver(final DarkReceiver p0);
    
    void setIconsDarkArea(final Rect p0);
    
    @ProvidesInterface(version = 1)
    public interface DarkReceiver
    {
        public static final int VERSION = 1;
        
        void onDarkChanged(final Rect p0, final float p1, final int p2);
    }
}
