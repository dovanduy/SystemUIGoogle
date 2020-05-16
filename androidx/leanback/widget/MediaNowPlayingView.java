// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.View;
import androidx.leanback.R$id;
import android.view.ViewGroup;
import androidx.leanback.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.content.Context;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MediaNowPlayingView extends LinearLayout
{
    private final ImageView mImage1;
    private final ImageView mImage2;
    private final ImageView mImage3;
    protected final LinearInterpolator mLinearInterpolator;
    private final ObjectAnimator mObjectAnimator1;
    private final ObjectAnimator mObjectAnimator2;
    private final ObjectAnimator mObjectAnimator3;
    
    public MediaNowPlayingView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mLinearInterpolator = new LinearInterpolator();
        LayoutInflater.from(context).inflate(R$layout.lb_playback_now_playing_bars, (ViewGroup)this, true);
        this.mImage1 = (ImageView)this.findViewById(R$id.bar1);
        this.mImage2 = (ImageView)this.findViewById(R$id.bar2);
        this.mImage3 = (ImageView)this.findViewById(R$id.bar3);
        final ImageView mImage1 = this.mImage1;
        mImage1.setPivotY((float)mImage1.getDrawable().getIntrinsicHeight());
        final ImageView mImage2 = this.mImage2;
        mImage2.setPivotY((float)mImage2.getDrawable().getIntrinsicHeight());
        final ImageView mImage3 = this.mImage3;
        mImage3.setPivotY((float)mImage3.getDrawable().getIntrinsicHeight());
        setDropScale((View)this.mImage1);
        setDropScale((View)this.mImage2);
        setDropScale((View)this.mImage3);
        (this.mObjectAnimator1 = ObjectAnimator.ofFloat((Object)this.mImage1, "scaleY", new float[] { 0.41666666f, 0.25f, 0.41666666f, 0.5833333f, 0.75f, 0.8333333f, 0.9166667f, 1.0f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.5f, 0.33333334f, 0.16666667f, 0.33333334f, 0.5f, 0.5833333f, 0.75f, 0.9166667f, 0.75f, 0.5833333f, 0.41666666f, 0.25f, 0.41666666f, 0.6666667f, 0.41666666f, 0.25f, 0.33333334f, 0.41666666f })).setRepeatCount(-1);
        this.mObjectAnimator1.setDuration(2320L);
        this.mObjectAnimator1.setInterpolator((TimeInterpolator)this.mLinearInterpolator);
        (this.mObjectAnimator2 = ObjectAnimator.ofFloat((Object)this.mImage2, "scaleY", new float[] { 1.0f, 0.9166667f, 0.8333333f, 0.9166667f, 1.0f, 0.9166667f, 0.75f, 0.5833333f, 0.75f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.8333333f, 1.0f, 0.9166667f, 0.75f, 0.41666666f, 0.25f, 0.41666666f, 0.6666667f, 0.8333333f, 1.0f, 0.8333333f, 0.75f, 0.6666667f, 1.0f })).setRepeatCount(-1);
        this.mObjectAnimator2.setDuration(2080L);
        this.mObjectAnimator2.setInterpolator((TimeInterpolator)this.mLinearInterpolator);
        (this.mObjectAnimator3 = ObjectAnimator.ofFloat((Object)this.mImage3, "scaleY", new float[] { 0.6666667f, 0.75f, 0.8333333f, 1.0f, 0.9166667f, 0.75f, 0.5833333f, 0.41666666f, 0.5833333f, 0.6666667f, 0.75f, 1.0f, 0.9166667f, 1.0f, 0.75f, 0.5833333f, 0.75f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.75f, 0.5833333f, 0.41666666f, 0.25f, 0.6666667f })).setRepeatCount(-1);
        this.mObjectAnimator3.setDuration(2000L);
        this.mObjectAnimator3.setInterpolator((TimeInterpolator)this.mLinearInterpolator);
    }
    
    static void setDropScale(final View view) {
        view.setScaleY(0.083333336f);
    }
    
    private void startAnimation() {
        this.startAnimation((Animator)this.mObjectAnimator1);
        this.startAnimation((Animator)this.mObjectAnimator2);
        this.startAnimation((Animator)this.mObjectAnimator3);
        this.mImage1.setVisibility(0);
        this.mImage2.setVisibility(0);
        this.mImage3.setVisibility(0);
    }
    
    private void startAnimation(final Animator animator) {
        if (!animator.isStarted()) {
            animator.start();
        }
    }
    
    private void stopAnimation() {
        this.stopAnimation((Animator)this.mObjectAnimator1, (View)this.mImage1);
        this.stopAnimation((Animator)this.mObjectAnimator2, (View)this.mImage2);
        this.stopAnimation((Animator)this.mObjectAnimator3, (View)this.mImage3);
        this.mImage1.setVisibility(8);
        this.mImage2.setVisibility(8);
        this.mImage3.setVisibility(8);
    }
    
    private void stopAnimation(final Animator animator, final View dropScale) {
        if (animator.isStarted()) {
            animator.cancel();
            setDropScale(dropScale);
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.getVisibility() == 0) {
            this.startAnimation();
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.stopAnimation();
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        if (visibility == 8) {
            this.stopAnimation();
        }
        else {
            this.startAnimation();
        }
    }
}
