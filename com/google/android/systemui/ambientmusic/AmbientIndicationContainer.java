// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.ambientmusic;

import android.media.MediaMetadata;
import android.os.SystemClock;
import com.android.systemui.Dependency;
import android.view.View$OnLayoutChangeListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.MathUtils;
import com.android.systemui.R$drawable;
import android.text.TextUtils;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.doze.util.BurnInHelperKt;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.res.ColorStateList;
import com.android.systemui.R$dimen;
import com.android.systemui.R$anim;
import com.android.systemui.R$id;
import android.view.View;
import android.os.Looper;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.util.wakelock.WakeLock;
import android.animation.ValueAnimator;
import android.widget.TextView;
import com.android.systemui.statusbar.phone.StatusBar;
import android.graphics.Rect;
import android.os.Handler;
import android.app.PendingIntent;
import android.graphics.drawable.Drawable;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.view.View$OnClickListener;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.AutoReinflateContainer;

public class AmbientIndicationContainer extends AutoReinflateContainer implements DozeReceiver, View$OnClickListener, StateListener, MediaListener
{
    private int mAmbientIndicationIconSize;
    private Drawable mAmbientMusicAnimation;
    private PendingIntent mAmbientMusicIntent;
    private CharSequence mAmbientMusicText;
    private boolean mAmbientSkipUnlock;
    private int mBurnInPreventionOffset;
    private float mDozeAmount;
    private boolean mDozing;
    private int mDrawablePadding;
    private final Handler mHandler;
    private final Rect mIconBounds;
    private int mMediaPlaybackState;
    private boolean mNotificationsHidden;
    private CharSequence mReverseChargingMessage;
    private StatusBar mStatusBar;
    private TextView mText;
    private int mTextColor;
    private ValueAnimator mTextColorAnimator;
    private final WakeLock mWakeLock;
    
    public AmbientIndicationContainer(final Context context, final AttributeSet set) {
        super(context, set);
        this.mIconBounds = new Rect();
        final Handler mHandler = new Handler(Looper.getMainLooper());
        this.mHandler = mHandler;
        this.mWakeLock = this.createWakeLock(super.mContext, mHandler);
    }
    
    private void sendBroadcastWithoutDismissingKeyguard(final PendingIntent pendingIntent) {
        if (pendingIntent.isActivity()) {
            return;
        }
        try {
            pendingIntent.send();
        }
        catch (PendingIntent$CanceledException obj) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Sending intent failed: ");
            sb.append(obj);
            Log.w("AmbientIndication", sb.toString());
        }
    }
    
    private void updateBottomPadding() {
        final NotificationPanelViewController panelController = this.mStatusBar.getPanelController();
        int ambientIndicationBottomPadding;
        if (this.mText.getVisibility() == 0) {
            ambientIndicationBottomPadding = this.mStatusBar.getNotificationScrollLayout().getBottom() - this.getTop();
        }
        else {
            ambientIndicationBottomPadding = 0;
        }
        panelController.setAmbientIndicationBottomPadding(ambientIndicationBottomPadding);
    }
    
    private void updateBurnInOffsets() {
        final int burnInOffset = BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffset * 2, true);
        final int mBurnInPreventionOffset = this.mBurnInPreventionOffset;
        final float n = (float)(burnInOffset - mBurnInPreventionOffset);
        final float n2 = (float)(BurnInHelperKt.getBurnInOffset(mBurnInPreventionOffset * 2, false) - this.mBurnInPreventionOffset);
        this.setTranslationX(n * this.mDozeAmount);
        this.setTranslationY(n2 * this.mDozeAmount);
    }
    
    private void updateColors() {
        final ValueAnimator mTextColorAnimator = this.mTextColorAnimator;
        if (mTextColorAnimator != null && mTextColorAnimator.isRunning()) {
            this.mTextColorAnimator.cancel();
        }
        final int defaultColor = this.mText.getTextColors().getDefaultColor();
        int mTextColor;
        if (this.mDozing) {
            mTextColor = -1;
        }
        else {
            mTextColor = this.mTextColor;
        }
        if (defaultColor == mTextColor) {
            return;
        }
        (this.mTextColorAnimator = ValueAnimator.ofArgb(new int[] { defaultColor, mTextColor })).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        this.mTextColorAnimator.setDuration(500L);
        this.mTextColorAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AmbientIndicationContainer$mS9qBZr1S1EQgiLgc559JZaVKfE(this));
        this.mTextColorAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                AmbientIndicationContainer.this.mTextColorAnimator = null;
            }
        });
        this.mTextColorAnimator.start();
    }
    
    private void updatePill() {
        final boolean empty = TextUtils.isEmpty(this.mReverseChargingMessage);
        int visibility = 0;
        if (!empty) {
            Drawable drawable = this.getResources().getDrawable(R$drawable.ic_qs_reverse_charging, super.mContext.getTheme());
            this.mText.setClickable(false);
            this.mText.setText(this.mReverseChargingMessage);
            this.mText.setContentDescription(this.mReverseChargingMessage);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            Drawable drawable2;
            if (this.isLayoutRtl()) {
                drawable2 = null;
            }
            else {
                drawable2 = drawable;
            }
            if (drawable2 != null) {
                drawable = null;
            }
            this.mText.setCompoundDrawables(drawable2, (Drawable)null, drawable, (Drawable)null);
            this.mText.setCompoundDrawablePadding(this.mDrawablePadding);
            this.mText.setVisibility(0);
            this.updateBottomPadding();
            return;
        }
        final CharSequence mAmbientMusicText = this.mAmbientMusicText;
        final Drawable mAmbientMusicAnimation = this.mAmbientMusicAnimation;
        final int n = 1;
        final boolean b = mAmbientMusicText != null && mAmbientMusicText.length() == 0;
        this.mText.setClickable(this.mAmbientMusicIntent != null);
        this.mText.setText(mAmbientMusicText);
        this.mText.setContentDescription(mAmbientMusicText);
        if (mAmbientMusicAnimation != null) {
            this.mIconBounds.set(0, 0, mAmbientMusicAnimation.getIntrinsicWidth(), mAmbientMusicAnimation.getIntrinsicHeight());
            MathUtils.fitRect(this.mIconBounds, this.mAmbientIndicationIconSize);
            mAmbientMusicAnimation.setBounds(this.mIconBounds);
        }
        Drawable drawable3;
        if (this.isLayoutRtl()) {
            drawable3 = null;
        }
        else {
            drawable3 = mAmbientMusicAnimation;
        }
        Drawable drawable4;
        if (drawable3 == null) {
            drawable4 = mAmbientMusicAnimation;
        }
        else {
            drawable4 = null;
        }
        this.mText.setCompoundDrawables(drawable3, (Drawable)null, drawable4, (Drawable)null);
        final TextView mText = this.mText;
        int mDrawablePadding;
        if (b) {
            mDrawablePadding = 0;
        }
        else {
            mDrawablePadding = this.mDrawablePadding;
        }
        mText.setCompoundDrawablePadding(mDrawablePadding);
        final boolean b2 = (!TextUtils.isEmpty(mAmbientMusicText) || b) && !this.mNotificationsHidden;
        int n2;
        if (this.mText.getVisibility() == 0) {
            n2 = n;
        }
        else {
            n2 = 0;
        }
        final TextView mText2 = this.mText;
        if (!b2) {
            visibility = 8;
        }
        mText2.setVisibility(visibility);
        if (b2) {
            if (n2 == 0) {
                this.mWakeLock.acquire("AmbientIndication");
                if (mAmbientMusicAnimation instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable)mAmbientMusicAnimation).start();
                }
                final TextView mText3 = this.mText;
                mText3.setTranslationY((float)(mText3.getHeight() / 2));
                this.mText.setAlpha(0.0f);
                this.mText.animate().withLayer().alpha(1.0f).translationY(0.0f).setStartDelay(150L).setDuration(100L).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        AmbientIndicationContainer.this.mWakeLock.release("AmbientIndication");
                    }
                }).setInterpolator((TimeInterpolator)Interpolators.DECELERATE_QUINT).start();
            }
            else {
                this.mHandler.post(this.mWakeLock.wrap((Runnable)_$$Lambda$AmbientIndicationContainer$20zsf8sDdIOT7QAgQCZNU_NsXjE.INSTANCE));
            }
        }
        else {
            this.mText.animate().cancel();
            if (mAmbientMusicAnimation instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable)mAmbientMusicAnimation).reset();
            }
            this.mHandler.post(this.mWakeLock.wrap((Runnable)_$$Lambda$AmbientIndicationContainer$KVRCJYirK1TYPyrHOw9GLDna6JI.INSTANCE));
        }
        this.updateBottomPadding();
    }
    
    @VisibleForTesting
    WakeLock createWakeLock(final Context context, final Handler handler) {
        return new DelayedWakeLock(handler, WakeLock.createPartial(context, "AmbientIndication"));
    }
    
    @Override
    public void dozeTimeTick() {
        this.updatePill();
        this.updateBurnInOffsets();
    }
    
    public void hideAmbientMusic() {
        this.setAmbientMusic(null, null, false);
    }
    
    public void initializeView(final StatusBar mStatusBar) {
        this.mStatusBar = mStatusBar;
        this.addInflateListener((InflateListener)new _$$Lambda$AmbientIndicationContainer$twCws3rDzM5dVwusEJjMOsyibCo(this));
        this.addOnLayoutChangeListener((View$OnLayoutChangeListener)new _$$Lambda$AmbientIndicationContainer$tovSRWTlvrzeaFrrBvb1F9ZQ9_4(this));
    }
    
    boolean isMediaPlaying() {
        return NotificationMediaManager.isPlayingState(this.mMediaPlaybackState);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(StatusBarStateController.class).addCallback((StatusBarStateController.StateListener)this);
        Dependency.get(NotificationMediaManager.class).addCallback((NotificationMediaManager.MediaListener)this);
    }
    
    public void onClick(final View view) {
        if (this.mAmbientMusicIntent != null) {
            this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), (View)this.mText, "AMBIENT_MUSIC_CLICK");
            if (this.mAmbientSkipUnlock) {
                this.sendBroadcastWithoutDismissingKeyguard(this.mAmbientMusicIntent);
            }
            else {
                this.mStatusBar.startPendingIntentDismissingKeyguard(this.mAmbientMusicIntent);
            }
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(StatusBarStateController.class).removeCallback((StatusBarStateController.StateListener)this);
        Dependency.get(NotificationMediaManager.class).removeCallback((NotificationMediaManager.MediaListener)this);
        this.mMediaPlaybackState = 0;
    }
    
    public void onDozeAmountChanged(final float n, final float mDozeAmount) {
        this.mDozeAmount = mDozeAmount;
        this.updateBurnInOffsets();
    }
    
    public void onDozingChanged(final boolean mDozing) {
        this.mDozing = mDozing;
        this.mText.setEnabled(mDozing ^ true);
        this.updateColors();
        this.updateBurnInOffsets();
    }
    
    public void onMetadataOrStateChanged(final MediaMetadata mediaMetadata, final int mMediaPlaybackState) {
        if (this.mMediaPlaybackState != mMediaPlaybackState) {
            this.mMediaPlaybackState = mMediaPlaybackState;
            if (this.isMediaPlaying()) {
                this.hideAmbientMusic();
            }
        }
    }
    
    public void onStateChanged(final int n) {
    }
    
    public void setAmbientMusic(final CharSequence mAmbientMusicText, final PendingIntent mAmbientMusicIntent, final boolean mAmbientSkipUnlock) {
        this.mAmbientMusicText = mAmbientMusicText;
        this.mAmbientMusicIntent = mAmbientMusicIntent;
        this.mAmbientSkipUnlock = mAmbientSkipUnlock;
        this.updatePill();
    }
    
    public void setReverseChargingMessage(final CharSequence mReverseChargingMessage) {
        this.mReverseChargingMessage = mReverseChargingMessage;
        this.updatePill();
    }
}
