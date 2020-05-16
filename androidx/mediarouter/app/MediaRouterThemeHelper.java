// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.graphics.Color;
import android.graphics.PorterDuff$Mode;
import android.widget.ProgressBar;
import android.view.View;
import android.app.Dialog;
import android.content.res.Resources$Theme;
import androidx.mediarouter.R$style;
import android.content.res.TypedArray;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import androidx.mediarouter.R$drawable;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.ColorUtils;
import androidx.mediarouter.R$attr;
import android.view.ContextThemeWrapper;
import android.content.Context;
import androidx.mediarouter.R$color;

final class MediaRouterThemeHelper
{
    private static final int COLOR_DARK_ON_LIGHT_BACKGROUND_RES_ID;
    
    static {
        COLOR_DARK_ON_LIGHT_BACKGROUND_RES_ID = R$color.mr_dynamic_dialog_icon_light;
    }
    
    static Context createThemedButtonContext(final Context context) {
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, getRouterThemeId(context));
        final int themeResource = getThemeResource((Context)contextThemeWrapper, R$attr.mediaRouteTheme);
        Object o = contextThemeWrapper;
        if (themeResource != 0) {
            o = new ContextThemeWrapper((Context)contextThemeWrapper, themeResource);
        }
        return (Context)o;
    }
    
    static Context createThemedDialogContext(final Context context, int n, final boolean b) {
        int themeResource = n;
        if (n == 0) {
            if (!b) {
                n = androidx.appcompat.R$attr.dialogTheme;
            }
            else {
                n = androidx.appcompat.R$attr.alertDialogTheme;
            }
            themeResource = getThemeResource(context, n);
        }
        ContextThemeWrapper contextThemeWrapper2;
        final ContextThemeWrapper contextThemeWrapper = contextThemeWrapper2 = new ContextThemeWrapper(context, themeResource);
        if (getThemeResource((Context)contextThemeWrapper, R$attr.mediaRouteTheme) != 0) {
            contextThemeWrapper2 = new ContextThemeWrapper((Context)contextThemeWrapper, getRouterThemeId((Context)contextThemeWrapper));
        }
        return (Context)contextThemeWrapper2;
    }
    
    static int createThemedDialogStyle(final Context context) {
        int n;
        if ((n = getThemeResource(context, R$attr.mediaRouteTheme)) == 0) {
            n = getRouterThemeId(context);
        }
        return n;
    }
    
    static int getButtonTextColor(final Context context) {
        final int themeColor = getThemeColor(context, 0, androidx.appcompat.R$attr.colorPrimary);
        if (ColorUtils.calculateContrast(themeColor, getThemeColor(context, 0, 16842801)) < 3.0) {
            return getThemeColor(context, 0, androidx.appcompat.R$attr.colorAccent);
        }
        return themeColor;
    }
    
    static Drawable getCheckBoxDrawableIcon(final Context context) {
        return getIconByDrawableId(context, R$drawable.mr_cast_checkbox);
    }
    
    static int getControllerColor(final Context context, final int n) {
        if (ColorUtils.calculateContrast(-1, getThemeColor(context, n, androidx.appcompat.R$attr.colorPrimary)) >= 3.0) {
            return -1;
        }
        return -570425344;
    }
    
    static Drawable getDefaultDrawableIcon(final Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteDefaultIconDrawable);
    }
    
    static float getDisabledAlpha(final Context context) {
        final TypedValue typedValue = new TypedValue();
        float float1;
        if (context.getTheme().resolveAttribute(16842803, typedValue, true)) {
            float1 = typedValue.getFloat();
        }
        else {
            float1 = 0.5f;
        }
        return float1;
    }
    
    private static Drawable getIconByAttrId(final Context context, final int n) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[] { n });
        final Drawable wrap = DrawableCompat.wrap(obtainStyledAttributes.getDrawable(0));
        if (isLightTheme(context)) {
            DrawableCompat.setTint(wrap, ContextCompat.getColor(context, MediaRouterThemeHelper.COLOR_DARK_ON_LIGHT_BACKGROUND_RES_ID));
        }
        obtainStyledAttributes.recycle();
        return wrap;
    }
    
    private static Drawable getIconByDrawableId(final Context context, final int n) {
        final Drawable wrap = DrawableCompat.wrap(ContextCompat.getDrawable(context, n));
        if (isLightTheme(context)) {
            DrawableCompat.setTint(wrap, ContextCompat.getColor(context, MediaRouterThemeHelper.COLOR_DARK_ON_LIGHT_BACKGROUND_RES_ID));
        }
        return wrap;
    }
    
    static Drawable getMuteButtonDrawableIcon(final Context context) {
        return getIconByDrawableId(context, R$drawable.mr_cast_mute_button);
    }
    
    private static int getRouterThemeId(final Context context) {
        int n;
        if (isLightTheme(context)) {
            if (getControllerColor(context, 0) == -570425344) {
                n = R$style.Theme_MediaRouter_Light;
            }
            else {
                n = R$style.Theme_MediaRouter_Light_DarkControlPanel;
            }
        }
        else if (getControllerColor(context, 0) == -570425344) {
            n = R$style.Theme_MediaRouter_LightControlPanel;
        }
        else {
            n = R$style.Theme_MediaRouter;
        }
        return n;
    }
    
    static Drawable getSpeakerDrawableIcon(final Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteSpeakerIconDrawable);
    }
    
    static Drawable getSpeakerGroupDrawableIcon(final Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteSpeakerGroupIconDrawable);
    }
    
    private static int getThemeColor(final Context context, int color, final int n) {
        if (color != 0) {
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(color, new int[] { n });
            color = obtainStyledAttributes.getColor(0, 0);
            obtainStyledAttributes.recycle();
            if (color != 0) {
                return color;
            }
        }
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(n, typedValue, true);
        if (typedValue.resourceId != 0) {
            return context.getResources().getColor(typedValue.resourceId);
        }
        return typedValue.data;
    }
    
    static int getThemeResource(final Context context, int resourceId) {
        final TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(resourceId, typedValue, true)) {
            resourceId = typedValue.resourceId;
        }
        else {
            resourceId = 0;
        }
        return resourceId;
    }
    
    static Drawable getTvDrawableIcon(final Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteTvIconDrawable);
    }
    
    private static boolean isLightTheme(final Context context) {
        final TypedValue typedValue = new TypedValue();
        final Resources$Theme theme = context.getTheme();
        final int isLightTheme = androidx.appcompat.R$attr.isLightTheme;
        boolean b = true;
        if (!theme.resolveAttribute(isLightTheme, typedValue, true) || typedValue.data == 0) {
            b = false;
        }
        return b;
    }
    
    static void setDialogBackgroundColor(final Context context, final Dialog dialog) {
        final View decorView = dialog.getWindow().getDecorView();
        int n;
        if (isLightTheme(context)) {
            n = R$color.mr_dynamic_dialog_background_light;
        }
        else {
            n = R$color.mr_dynamic_dialog_background_dark;
        }
        decorView.setBackgroundColor(ContextCompat.getColor(context, n));
    }
    
    static void setIndeterminateProgressBarColor(final Context context, final ProgressBar progressBar) {
        if (!progressBar.isIndeterminate()) {
            return;
        }
        int n;
        if (isLightTheme(context)) {
            n = R$color.mr_cast_progressbar_progress_and_thumb_light;
        }
        else {
            n = R$color.mr_cast_progressbar_progress_and_thumb_dark;
        }
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, n), PorterDuff$Mode.SRC_IN);
    }
    
    static void setMediaControlsBackgroundColor(final Context context, final View view, final View view2, final boolean b) {
        final int themeColor = getThemeColor(context, 0, androidx.appcompat.R$attr.colorPrimary);
        final int themeColor2 = getThemeColor(context, 0, androidx.appcompat.R$attr.colorPrimaryDark);
        int n = themeColor;
        int n2 = themeColor2;
        if (b) {
            n = themeColor;
            n2 = themeColor2;
            if (getControllerColor(context, 0) == -570425344) {
                n2 = themeColor;
                n = -1;
            }
        }
        view.setBackgroundColor(n);
        view2.setBackgroundColor(n2);
        view.setTag((Object)n);
        view2.setTag((Object)n2);
    }
    
    static void setVolumeSliderColor(final Context context, final MediaRouteVolumeSlider mediaRouteVolumeSlider) {
        int n;
        int n2;
        if (isLightTheme(context)) {
            n = ContextCompat.getColor(context, R$color.mr_cast_progressbar_progress_and_thumb_light);
            n2 = ContextCompat.getColor(context, R$color.mr_cast_progressbar_background_light);
        }
        else {
            n = ContextCompat.getColor(context, R$color.mr_cast_progressbar_progress_and_thumb_dark);
            n2 = ContextCompat.getColor(context, R$color.mr_cast_progressbar_background_dark);
        }
        mediaRouteVolumeSlider.setColor(n, n2);
    }
    
    static void setVolumeSliderColor(final Context context, final MediaRouteVolumeSlider mediaRouteVolumeSlider, final View view) {
        int color;
        final int n = color = getControllerColor(context, 0);
        if (Color.alpha(n) != 255) {
            color = ColorUtils.compositeColors(n, (int)view.getTag());
        }
        mediaRouteVolumeSlider.setColor(color);
    }
}
