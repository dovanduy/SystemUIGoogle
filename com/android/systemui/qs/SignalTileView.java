// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.plugins.qs.QSTile;
import android.view.View$MeasureSpec;
import com.android.systemui.qs.tileimpl.SlashImageView;
import android.view.ViewPropertyAnimator;
import android.view.View;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import android.content.Context;
import android.animation.ValueAnimator;
import android.widget.ImageView;
import android.widget.FrameLayout;
import com.android.systemui.qs.tileimpl.QSIconViewImpl;

public class SignalTileView extends QSIconViewImpl
{
    private static final long DEFAULT_DURATION;
    private static final long SHORT_DURATION;
    protected FrameLayout mIconFrame;
    private ImageView mIn;
    private ImageView mOut;
    private ImageView mOverlay;
    protected ImageView mSignal;
    private int mSignalIndicatorToIconFrameSpacing;
    private int mWideOverlayIconStartPadding;
    
    static {
        SHORT_DURATION = (DEFAULT_DURATION = new ValueAnimator().getDuration()) / 3L;
    }
    
    public SignalTileView(final Context context) {
        super(context);
        this.mIn = this.addTrafficView(R$drawable.ic_qs_signal_in);
        this.mOut = this.addTrafficView(R$drawable.ic_qs_signal_out);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.mWideOverlayIconStartPadding = context.getResources().getDimensionPixelSize(R$dimen.wide_type_icon_start_padding_qs);
        this.mSignalIndicatorToIconFrameSpacing = context.getResources().getDimensionPixelSize(R$dimen.signal_indicator_to_icon_frame_spacing);
    }
    
    private ImageView addTrafficView(final int imageResource) {
        final ImageView imageView = new ImageView(super.mContext);
        imageView.setImageResource(imageResource);
        imageView.setAlpha(0.0f);
        this.addView((View)imageView);
        return imageView;
    }
    
    private void layoutIndicator(final View view) {
        final int layoutDirection = this.getLayoutDirection();
        boolean b = true;
        if (layoutDirection != 1) {
            b = false;
        }
        int n;
        int n2;
        if (b) {
            n = this.getLeft() - this.mSignalIndicatorToIconFrameSpacing;
            n2 = n - view.getMeasuredWidth();
        }
        else {
            n2 = this.mSignalIndicatorToIconFrameSpacing + this.getRight();
            n = view.getMeasuredWidth() + n2;
        }
        view.layout(n2, this.mIconFrame.getBottom() - view.getMeasuredHeight(), n, this.mIconFrame.getBottom());
    }
    
    private void setVisibility(final View view, final boolean b, final boolean b2) {
        float alpha;
        if (b && b2) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.0f;
        }
        if (view.getAlpha() == alpha) {
            return;
        }
        if (b) {
            final ViewPropertyAnimator animate = view.animate();
            long duration;
            if (b2) {
                duration = SignalTileView.SHORT_DURATION;
            }
            else {
                duration = SignalTileView.DEFAULT_DURATION;
            }
            animate.setDuration(duration).alpha(alpha).start();
        }
        else {
            view.setAlpha(alpha);
        }
    }
    
    @Override
    protected View createIcon() {
        this.mIconFrame = new FrameLayout(super.mContext);
        final SlashImageView slashImageView = this.createSlashImageView(super.mContext);
        this.mSignal = slashImageView;
        this.mIconFrame.addView((View)slashImageView);
        final ImageView mOverlay = new ImageView(super.mContext);
        this.mOverlay = mOverlay;
        this.mIconFrame.addView((View)mOverlay, -2, -2);
        return (View)this.mIconFrame;
    }
    
    protected SlashImageView createSlashImageView(final Context context) {
        return new SlashImageView(context);
    }
    
    @Override
    protected int getIconMeasureMode() {
        return Integer.MIN_VALUE;
    }
    
    @Override
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.layoutIndicator((View)this.mIn);
        this.layoutIndicator((View)this.mOut);
    }
    
    @Override
    protected void onMeasure(int measureSpec, int measureSpec2) {
        super.onMeasure(measureSpec, measureSpec2);
        measureSpec2 = View$MeasureSpec.makeMeasureSpec(this.mIconFrame.getMeasuredHeight(), 1073741824);
        measureSpec = View$MeasureSpec.makeMeasureSpec(this.mIconFrame.getMeasuredHeight(), Integer.MIN_VALUE);
        this.mIn.measure(measureSpec, measureSpec2);
        this.mOut.measure(measureSpec, measureSpec2);
    }
    
    @Override
    public void setIcon(final QSTile.State state, final boolean b) {
        final QSTile.SignalState signalState = (QSTile.SignalState)state;
        this.setIcon(this.mSignal, signalState, b);
        final int overlayIconId = signalState.overlayIconId;
        final boolean b2 = false;
        if (overlayIconId > 0) {
            this.mOverlay.setVisibility(0);
            this.mOverlay.setImageResource(signalState.overlayIconId);
        }
        else {
            this.mOverlay.setVisibility(8);
        }
        if (signalState.overlayIconId > 0 && signalState.isOverlayIconWide) {
            this.mSignal.setPaddingRelative(this.mWideOverlayIconStartPadding, 0, 0, 0);
        }
        else {
            this.mSignal.setPaddingRelative(0, 0, 0, 0);
        }
        boolean b3 = b2;
        if (b) {
            b3 = b2;
            if (this.isShown()) {
                b3 = true;
            }
        }
        this.setVisibility((View)this.mIn, b3, signalState.activityIn);
        this.setVisibility((View)this.mOut, b3, signalState.activityOut);
    }
}
