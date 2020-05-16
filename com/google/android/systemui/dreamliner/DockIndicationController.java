// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.os.UserHandle;
import android.content.Intent;
import android.view.View;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.R$string;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import android.view.animation.Animation$AnimationListener;
import android.view.animation.AnimationUtils;
import com.android.systemui.R$anim;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import java.util.concurrent.TimeUnit;
import com.android.systemui.statusbar.phone.StatusBar;
import android.widget.TextView;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.VisibleForTesting;
import android.view.View$OnAttachStateChangeListener;
import android.view.View$OnClickListener;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

public class DockIndicationController implements StateListener, View$OnClickListener, View$OnAttachStateChangeListener
{
    @VisibleForTesting
    static final String ACTION_ASSISTANT_POODLE = "com.google.android.systemui.dreamliner.ASSISTANT_POODLE";
    private static final long KEYGUARD_INDICATION_TIMEOUT_MILLIS;
    private static final long PROMO_SHOWING_TIME_MILLIS;
    private final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final Runnable mDisableLiveRegionRunnable;
    @VisibleForTesting
    FrameLayout mDockPromo;
    @VisibleForTesting
    ImageView mDockedTopIcon;
    private boolean mDocking;
    private boolean mDozing;
    private final Animation mHidePromoAnimation;
    private final Runnable mHidePromoRunnable;
    @VisibleForTesting
    boolean mIconViewsValidated;
    private KeyguardIndicationTextView mKeyguardIndicationTextView;
    private TextView mPromoText;
    private boolean mShowPromo;
    private final Animation mShowPromoAnimation;
    private int mShowPromoTimes;
    private final StatusBar mStatusBar;
    private boolean mTopIconShowing;
    
    static {
        PROMO_SHOWING_TIME_MILLIS = TimeUnit.SECONDS.toMillis(2L);
        KEYGUARD_INDICATION_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(15L);
    }
    
    public DockIndicationController(final Context mContext, final StatusBar mStatusBar) {
        this.mContext = mContext;
        this.mStatusBar = mStatusBar;
        Dependency.get((Class<SysuiStatusBarStateController>)StatusBarStateController.class).addCallback((StatusBarStateController.StateListener)this);
        this.mHidePromoRunnable = new _$$Lambda$DockIndicationController$s6fUyLW5XozGsDeEBsTZ3gklz3U(this);
        this.mDisableLiveRegionRunnable = new _$$Lambda$DockIndicationController$_jY_ZZzy1a19Vpytf_mtUJOovaI(this);
        (this.mShowPromoAnimation = AnimationUtils.loadAnimation(this.mContext, R$anim.dock_promo_animation)).setAnimationListener((Animation$AnimationListener)new PhotoAnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                final DockIndicationController this$0 = DockIndicationController.this;
                this$0.mDockPromo.postDelayed(this$0.mHidePromoRunnable, DockIndicationController.this.getRecommendedTimeoutMillis(DockIndicationController.PROMO_SHOWING_TIME_MILLIS));
            }
        });
        (this.mHidePromoAnimation = AnimationUtils.loadAnimation(this.mContext, R$anim.dock_promo_fade_out)).setAnimationListener((Animation$AnimationListener)new PhotoAnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                if (DockIndicationController.this.mShowPromoTimes < 5) {
                    DockIndicationController.this.showPromoInner();
                    return;
                }
                DockIndicationController.this.mDockPromo.setVisibility(8);
            }
        });
        this.mAccessibilityManager = (AccessibilityManager)this.mContext.getSystemService("accessibility");
    }
    
    private void disableLiveRegion() {
        if (this.mDocking && this.mDozing) {
            this.mKeyguardIndicationTextView.setAccessibilityLiveRegion(0);
        }
    }
    
    private long getRecommendedTimeoutMillis(long value) {
        final AccessibilityManager mAccessibilityManager = this.mAccessibilityManager;
        if (mAccessibilityManager != null) {
            value = mAccessibilityManager.getRecommendedTimeoutMillis(Math.toIntExact(value), 2);
        }
        return value;
    }
    
    private void hidePromo() {
        if (this.mDozing && this.mDocking) {
            this.mDockPromo.startAnimation(this.mHidePromoAnimation);
        }
    }
    
    private void showPromoInner() {
        if (this.mDozing && this.mDocking && this.mShowPromo) {
            this.mDockPromo.setVisibility(0);
            this.mDockPromo.startAnimation(this.mShowPromoAnimation);
            ++this.mShowPromoTimes;
        }
    }
    
    private void updateLiveRegionIfNeeded() {
        final int accessibilityLiveRegion = this.mKeyguardIndicationTextView.getAccessibilityLiveRegion();
        if (this.mDozing && this.mDocking) {
            this.mKeyguardIndicationTextView.removeCallbacks(this.mDisableLiveRegionRunnable);
            this.mKeyguardIndicationTextView.postDelayed(this.mDisableLiveRegionRunnable, this.getRecommendedTimeoutMillis(DockIndicationController.KEYGUARD_INDICATION_TIMEOUT_MILLIS));
            return;
        }
        if (accessibilityLiveRegion != 1) {
            this.mKeyguardIndicationTextView.setAccessibilityLiveRegion(1);
        }
    }
    
    private void updateVisibility() {
        if (!this.mIconViewsValidated) {
            this.initializeIconViews();
        }
        if (!this.mDozing || !this.mDocking) {
            this.mDockPromo.setVisibility(8);
            this.mDockedTopIcon.setVisibility(8);
            return;
        }
        if (!this.mTopIconShowing) {
            this.mDockedTopIcon.setVisibility(8);
            return;
        }
        this.mDockedTopIcon.setVisibility(0);
    }
    
    @VisibleForTesting
    void initializeIconViews() {
        final NotificationShadeWindowView notificationShadeWindowView = this.mStatusBar.getNotificationShadeWindowView();
        (this.mDockedTopIcon = (ImageView)notificationShadeWindowView.findViewById(R$id.docked_top_icon)).setImageResource(R$drawable.ic_assistant_logo);
        this.mDockedTopIcon.setContentDescription((CharSequence)this.mContext.getString(R$string.accessibility_assistant_poodle));
        this.mDockedTopIcon.setTooltipText((CharSequence)this.mContext.getString(R$string.accessibility_assistant_poodle));
        this.mDockedTopIcon.setOnClickListener((View$OnClickListener)this);
        this.mDockPromo = (FrameLayout)notificationShadeWindowView.findViewById(R$id.dock_promo);
        (this.mPromoText = (TextView)notificationShadeWindowView.findViewById(R$id.photo_promo_text)).setAutoSizeTextTypeUniformWithConfiguration(10, 16, 1, 2);
        notificationShadeWindowView.findViewById(R$id.ambient_indication).addOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
        this.mKeyguardIndicationTextView = (KeyguardIndicationTextView)notificationShadeWindowView.findViewById(R$id.keyguard_indication_text);
        this.mIconViewsValidated = true;
    }
    
    public void onClick(final View view) {
        if (view.getId() == R$id.docked_top_icon) {
            final Intent obj = new Intent("com.google.android.systemui.dreamliner.ASSISTANT_POODLE");
            obj.addFlags(1073741824);
            try {
                this.mContext.sendBroadcastAsUser(obj, UserHandle.CURRENT);
            }
            catch (SecurityException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Cannot send event for intent= ");
                sb.append(obj);
                Log.w("DLIndicator", sb.toString(), (Throwable)ex);
            }
        }
    }
    
    @Override
    public void onDozingChanged(final boolean mDozing) {
        this.mDozing = mDozing;
        this.updateVisibility();
        this.updateLiveRegionIfNeeded();
        if (!this.mDozing) {
            this.mShowPromo = false;
        }
        else {
            this.showPromoInner();
        }
    }
    
    public void onViewAttachedToWindow(final View view) {
    }
    
    public void onViewDetachedFromWindow(final View view) {
        view.removeOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
        this.mIconViewsValidated = false;
        this.mDockedTopIcon = null;
    }
    
    public void setDocking(final boolean mDocking) {
        if (!(this.mDocking = mDocking)) {
            this.mTopIconShowing = false;
            this.mShowPromo = false;
        }
        this.updateVisibility();
        this.updateLiveRegionIfNeeded();
    }
    
    public void setShowing(final boolean mTopIconShowing) {
        this.mTopIconShowing = mTopIconShowing;
        this.updateVisibility();
    }
    
    public void showPromo(final ResultReceiver resultReceiver) {
        this.mShowPromoTimes = 0;
        this.mShowPromo = true;
        if (this.mDozing && this.mDocking) {
            this.showPromoInner();
            resultReceiver.send(0, (Bundle)null);
        }
        else {
            resultReceiver.send(1, (Bundle)null);
        }
    }
    
    private static class PhotoAnimationListener implements Animation$AnimationListener
    {
        public void onAnimationRepeat(final Animation animation) {
        }
        
        public void onAnimationStart(final Animation animation) {
        }
    }
}
