// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.widget.ImageView;
import android.view.View;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.notification.row.HybridNotificationView;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.R$id;
import android.graphics.drawable.Icon;
import android.util.Pools$SimplePool;

public class ImageTransformState extends TransformState
{
    public static final int ICON_TAG;
    private static Pools$SimplePool<ImageTransformState> sInstancePool;
    private Icon mIcon;
    
    static {
        ICON_TAG = R$id.image_icon_tag;
        ImageTransformState.sInstancePool = (Pools$SimplePool<ImageTransformState>)new Pools$SimplePool(40);
    }
    
    private static float mapToDuration(final float n) {
        return Math.max(Math.min((n * 360.0f - 150.0f) / 210.0f, 1.0f), 0.0f);
    }
    
    public static ImageTransformState obtain() {
        final ImageTransformState imageTransformState = (ImageTransformState)ImageTransformState.sInstancePool.acquire();
        if (imageTransformState != null) {
            return imageTransformState;
        }
        return new ImageTransformState();
    }
    
    @Override
    public void appear(float n, final TransformableView transformableView) {
        if (transformableView instanceof HybridNotificationView) {
            if (n == 0.0f) {
                super.mTransformedView.setPivotY(0.0f);
                final View mTransformedView = super.mTransformedView;
                mTransformedView.setPivotX((float)(mTransformedView.getWidth() / 2));
                this.prepareFadeIn();
            }
            n = mapToDuration(n);
            CrossFadeHelper.fadeIn(super.mTransformedView, n, false);
            n = Interpolators.LINEAR_OUT_SLOW_IN.getInterpolation(n);
            super.mTransformedView.setScaleX(n);
            super.mTransformedView.setScaleY(n);
        }
        else {
            super.appear(n, transformableView);
        }
    }
    
    @Override
    public void disappear(float n, final TransformableView transformableView) {
        if (transformableView instanceof HybridNotificationView) {
            if (n == 0.0f) {
                super.mTransformedView.setPivotY(0.0f);
                final View mTransformedView = super.mTransformedView;
                mTransformedView.setPivotX((float)(mTransformedView.getWidth() / 2));
            }
            n = mapToDuration(1.0f - n);
            CrossFadeHelper.fadeOut(super.mTransformedView, 1.0f - n, false);
            n = Interpolators.LINEAR_OUT_SLOW_IN.getInterpolation(n);
            super.mTransformedView.setScaleX(n);
            super.mTransformedView.setScaleY(n);
        }
        else {
            super.disappear(n, transformableView);
        }
    }
    
    public Icon getIcon() {
        return this.mIcon;
    }
    
    @Override
    public void initFrom(final View view, final TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        if (view instanceof ImageView) {
            this.mIcon = (Icon)view.getTag(ImageTransformState.ICON_TAG);
        }
    }
    
    @Override
    public void recycle() {
        super.recycle();
        if (this.getClass() == ImageTransformState.class) {
            ImageTransformState.sInstancePool.release((Object)this);
        }
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.mIcon = null;
    }
    
    @Override
    protected boolean sameAs(final TransformState transformState) {
        final boolean sameAs = super.sameAs(transformState);
        boolean b = true;
        if (sameAs) {
            return true;
        }
        if (transformState instanceof ImageTransformState) {
            final Icon mIcon = this.mIcon;
            if (mIcon == null || !mIcon.sameAs(((ImageTransformState)transformState).getIcon())) {
                b = false;
            }
            return b;
        }
        return false;
    }
}
