// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.util.Log;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.graphics.PorterDuff$Mode;
import androidx.appcompat.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatSeekBar;

class MediaRouteVolumeSlider extends AppCompatSeekBar
{
    private int mBackgroundColor;
    private final float mDisabledAlpha;
    private boolean mHideThumb;
    private int mProgressAndThumbColor;
    private Drawable mThumb;
    
    public MediaRouteVolumeSlider(final Context context) {
        this(context, null);
    }
    
    public MediaRouteVolumeSlider(final Context context, final AttributeSet set) {
        this(context, set, R$attr.seekBarStyle);
    }
    
    public MediaRouteVolumeSlider(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(context);
    }
    
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int n;
        if (this.isEnabled()) {
            n = 255;
        }
        else {
            n = (int)(this.mDisabledAlpha * 255.0f);
        }
        this.mThumb.setColorFilter(this.mProgressAndThumbColor, PorterDuff$Mode.SRC_IN);
        this.mThumb.setAlpha(n);
        Drawable drawable;
        if ((drawable = this.getProgressDrawable()) instanceof LayerDrawable) {
            final LayerDrawable layerDrawable = (LayerDrawable)this.getProgressDrawable();
            drawable = layerDrawable.findDrawableByLayerId(16908301);
            layerDrawable.findDrawableByLayerId(16908288).setColorFilter(this.mBackgroundColor, PorterDuff$Mode.SRC_IN);
        }
        drawable.setColorFilter(this.mProgressAndThumbColor, PorterDuff$Mode.SRC_IN);
        drawable.setAlpha(n);
    }
    
    public void setColor(final int n) {
        this.setColor(n, n);
    }
    
    public void setColor(final int n, final int n2) {
        if (this.mProgressAndThumbColor != n) {
            if (Color.alpha(n) != 255) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Volume slider progress and thumb color cannot be translucent: #");
                sb.append(Integer.toHexString(n));
                Log.e("MediaRouteVolumeSlider", sb.toString());
            }
            this.mProgressAndThumbColor = n;
        }
        if (this.mBackgroundColor != n2) {
            if (Color.alpha(n2) != 255) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Volume slider background color cannot be translucent: #");
                sb2.append(Integer.toHexString(n2));
                Log.e("MediaRouteVolumeSlider", sb2.toString());
            }
            this.mBackgroundColor = n2;
        }
    }
    
    public void setHideThumb(final boolean mHideThumb) {
        if (this.mHideThumb == mHideThumb) {
            return;
        }
        this.mHideThumb = mHideThumb;
        Drawable mThumb;
        if (mHideThumb) {
            mThumb = null;
        }
        else {
            mThumb = this.mThumb;
        }
        super.setThumb(mThumb);
    }
    
    public void setThumb(Drawable drawable) {
        this.mThumb = drawable;
        if (this.mHideThumb) {
            drawable = null;
        }
        super.setThumb(drawable);
    }
}
