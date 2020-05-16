// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.view.View;

class Util extends com.android.settingslib.volume.Util
{
    public static String logTag(final Class<?> clazz) {
        final StringBuilder sb = new StringBuilder();
        sb.append("vol.");
        sb.append(clazz.getSimpleName());
        String s = sb.toString();
        if (s.length() >= 23) {
            s = s.substring(0, 23);
        }
        return s;
    }
    
    public static String ringerModeToString(final int i) {
        if (i == 0) {
            return "RINGER_MODE_SILENT";
        }
        if (i == 1) {
            return "RINGER_MODE_VIBRATE";
        }
        if (i != 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("RINGER_MODE_UNKNOWN_");
            sb.append(i);
            return sb.toString();
        }
        return "RINGER_MODE_NORMAL";
    }
    
    public static final void setVisOrGone(final View view, final boolean b) {
        if (view != null) {
            final int visibility = view.getVisibility();
            int visibility2 = 0;
            if (visibility == 0 != b) {
                if (!b) {
                    visibility2 = 8;
                }
                view.setVisibility(visibility2);
            }
        }
    }
}
