// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.graphics.drawable.Icon;
import android.content.pm.LauncherApps;
import android.app.Notification$BubbleMetadata;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.UserHandle;
import android.graphics.Bitmap;
import com.android.launcher3.icons.ShadowGenerator;
import android.graphics.Canvas;
import com.android.launcher3.icons.BitmapInfo;
import android.graphics.drawable.Drawable;
import com.android.systemui.R$dimen;
import android.content.Context;
import com.android.launcher3.icons.BaseIconFactory;

public class BubbleIconFactory extends BaseIconFactory
{
    protected BubbleIconFactory(final Context context) {
        super(context, context.getResources().getConfiguration().densityDpi, context.getResources().getDimensionPixelSize(R$dimen.individual_bubble_size));
    }
    
    BitmapInfo getBadgeBitmap(final Drawable drawable) {
        final Bitmap iconBitmap = this.createIconBitmap(drawable, 1.0f, this.getBadgeSize());
        final Canvas canvas = new Canvas();
        final ShadowGenerator shadowGenerator = new ShadowGenerator(this.getBadgeSize());
        canvas.setBitmap(iconBitmap);
        shadowGenerator.recreateIcon(Bitmap.createBitmap(iconBitmap), canvas);
        return this.createIconBitmap(iconBitmap);
    }
    
    int getBadgeSize() {
        return super.mContext.getResources().getDimensionPixelSize(com.android.launcher3.icons.R$dimen.profile_badge_size);
    }
    
    BitmapInfo getBubbleBitmap(final Drawable drawable, final BitmapInfo bitmapInfo) {
        final BitmapInfo badgedIconBitmap = this.createBadgedIconBitmap(drawable, null, true);
        this.badgeWithDrawable(badgedIconBitmap.icon, (Drawable)new BitmapDrawable(super.mContext.getResources(), bitmapInfo.icon));
        return badgedIconBitmap;
    }
    
    Drawable getBubbleDrawable(final Context context, final ShortcutInfo shortcutInfo, final Notification$BubbleMetadata notification$BubbleMetadata) {
        if (shortcutInfo != null) {
            return ((LauncherApps)context.getSystemService("launcherapps")).getShortcutIconDrawable(shortcutInfo, context.getResources().getConfiguration().densityDpi);
        }
        final Icon icon = notification$BubbleMetadata.getIcon();
        if (icon != null) {
            if (icon.getType() == 4 || icon.getType() == 6) {
                context.grantUriPermission(context.getPackageName(), icon.getUri(), 1);
            }
            return icon.loadDrawable(context);
        }
        return null;
    }
}
