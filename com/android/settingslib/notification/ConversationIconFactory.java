// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.notification;

import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.util.Log;
import android.graphics.Canvas;
import android.graphics.Paint$Style;
import android.graphics.Paint;
import android.os.UserHandle;
import android.content.pm.ShortcutInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.content.res.Resources$Theme;
import com.android.settingslib.R$color;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.LauncherApps;
import android.util.IconDrawableFactory;
import com.android.launcher3.icons.BaseIconFactory;

public class ConversationIconFactory extends BaseIconFactory
{
    final IconDrawableFactory mIconDrawableFactory;
    private int mImportantConversationColor;
    final LauncherApps mLauncherApps;
    final PackageManager mPackageManager;
    
    public ConversationIconFactory(final Context context, final LauncherApps mLauncherApps, final PackageManager mPackageManager, final IconDrawableFactory mIconDrawableFactory, final int n) {
        super(context, context.getResources().getConfiguration().densityDpi, n);
        this.mLauncherApps = mLauncherApps;
        this.mPackageManager = mPackageManager;
        this.mIconDrawableFactory = mIconDrawableFactory;
        this.mImportantConversationColor = context.getResources().getColor(R$color.important_conversation, (Resources$Theme)null);
    }
    
    private Drawable getAppBadge(final String s, final int n) {
        Drawable drawable;
        try {
            drawable = this.mIconDrawableFactory.getBadgedIcon(this.mPackageManager.getApplicationInfoAsUser(s, 128, n), n);
        }
        catch (PackageManager$NameNotFoundException ex) {
            drawable = this.mPackageManager.getDefaultActivityIcon();
        }
        return drawable;
    }
    
    private Drawable getBaseIconDrawable(final ShortcutInfo shortcutInfo) {
        return this.mLauncherApps.getShortcutIconDrawable(shortcutInfo, super.mFillResIconDpi);
    }
    
    public Drawable getConversationDrawable(final ShortcutInfo shortcutInfo, final String s, final int n, final boolean b) {
        return this.getConversationDrawable(this.getBaseIconDrawable(shortcutInfo), s, n, b);
    }
    
    public Drawable getConversationDrawable(final Drawable drawable, final String s, final int n, final boolean b) {
        return new ConversationIconDrawable(drawable, this.getAppBadge(s, UserHandle.getUserId(n)), super.mIconBitmapSize, this.mImportantConversationColor, b);
    }
    
    public static class ConversationIconDrawable extends Drawable
    {
        private Drawable mBadgeIcon;
        private Drawable mBaseIcon;
        private int mIconSize;
        private Paint mRingPaint;
        private boolean mShowRing;
        
        public ConversationIconDrawable(final Drawable mBaseIcon, final Drawable mBadgeIcon, final int mIconSize, final int color, final boolean mShowRing) {
            this.mBaseIcon = mBaseIcon;
            this.mBadgeIcon = mBadgeIcon;
            this.mIconSize = mIconSize;
            this.mShowRing = mShowRing;
            (this.mRingPaint = new Paint()).setStyle(Paint$Style.STROKE);
            this.mRingPaint.setColor(color);
        }
        
        public void draw(final Canvas canvas) {
            final Rect bounds = this.getBounds();
            final float n = bounds.width() / 48.0f;
            final int centerX = bounds.centerX();
            final int centerX2 = bounds.centerX();
            final int n2 = (int)(2.0f * n);
            final int n3 = (int)(42.0f * n);
            final int n4 = (int)(n * 16.800001f);
            final Drawable mBaseIcon = this.mBaseIcon;
            if (mBaseIcon != null) {
                final int n5 = n3 / 2;
                mBaseIcon.setBounds(centerX - n5, centerX2 - n5, centerX + n5, centerX2 + n5);
                this.mBaseIcon.draw(canvas);
            }
            else {
                Log.w("ConversationIconFactory", "ConversationIconDrawable has null base icon");
            }
            final Drawable mBadgeIcon = this.mBadgeIcon;
            if (mBadgeIcon != null) {
                final int right = bounds.right;
                final int bottom = bounds.bottom;
                mBadgeIcon.setBounds(right - n4 - n2, bottom - n4 - n2, right - n2, bottom - n2);
                this.mBadgeIcon.draw(canvas);
            }
            else {
                Log.w("ConversationIconFactory", "ConversationIconDrawable has null badge icon");
            }
            if (this.mShowRing) {
                final Paint mRingPaint = this.mRingPaint;
                final float strokeWidth = (float)n2;
                mRingPaint.setStrokeWidth(strokeWidth);
                final float n6 = n4 * 0.5f;
                canvas.drawCircle(bounds.right - n6 - strokeWidth, bounds.bottom - n6 - strokeWidth, 0.5f * strokeWidth + n6, this.mRingPaint);
            }
        }
        
        public int getIntrinsicHeight() {
            return this.mIconSize;
        }
        
        public int getIntrinsicWidth() {
            return this.mIconSize;
        }
        
        public int getOpacity() {
            return 0;
        }
        
        public void setAlpha(final int n) {
        }
        
        public void setColorFilter(final ColorFilter colorFilter) {
        }
    }
}
