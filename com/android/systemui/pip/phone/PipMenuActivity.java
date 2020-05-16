// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.app.ActivityManager$TaskDescription;
import android.view.KeyEvent;
import android.transition.Transition;
import com.android.systemui.R$string;
import android.graphics.drawable.ColorDrawable;
import com.android.systemui.R$dimen;
import android.widget.FrameLayout$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.view.View$OnClickListener;
import android.graphics.drawable.Icon$OnDrawableLoadedListener;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.View$OnTouchListener;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import android.util.Pair;
import android.os.Parcelable;
import android.content.Intent;
import android.net.Uri;
import android.content.ComponentName;
import android.os.UserHandle;
import android.content.Context;
import android.app.ActivityManager;
import java.util.Collection;
import android.os.RemoteException;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.widget.ImageButton;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import java.util.Collections;
import android.graphics.Rect;
import android.content.pm.ParceledListSlice;
import android.os.Bundle;
import android.view.MotionEvent;
import android.os.Message;
import android.animation.ValueAnimator;
import java.util.ArrayList;
import android.os.Messenger;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.os.Handler;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.app.RemoteAction;
import java.util.List;
import android.view.accessibility.AccessibilityManager;
import android.app.Activity;

public class PipMenuActivity extends Activity
{
    private AccessibilityManager mAccessibilityManager;
    private final List<RemoteAction> mActions;
    private LinearLayout mActionsGroup;
    private boolean mAllowMenuTimeout;
    private boolean mAllowTouches;
    private Drawable mBackgroundDrawable;
    private int mBetweenActionPaddingLand;
    private View mDismissButton;
    private final Runnable mFinishRunnable;
    private Handler mHandler;
    private ValueAnimator$AnimatorUpdateListener mMenuBgUpdateListener;
    private View mMenuContainer;
    private AnimatorSet mMenuContainerAnimator;
    private int mMenuState;
    private Messenger mMessenger;
    private boolean mResize;
    private View mSettingsButton;
    private Messenger mToControllerMessenger;
    private View mViewRoot;
    
    public PipMenuActivity() {
        this.mResize = true;
        this.mAllowMenuTimeout = true;
        this.mAllowTouches = true;
        this.mActions = new ArrayList<RemoteAction>();
        this.mMenuBgUpdateListener = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                PipMenuActivity.this.mBackgroundDrawable.setAlpha((int)((float)valueAnimator.getAnimatedValue() * 0.3f * 255.0f));
            }
        };
        this.mHandler = new Handler();
        this.mMessenger = new Messenger((Handler)new Handler() {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    case 7: {
                        PipMenuActivity.this.dispatchPointerEvent((MotionEvent)message.obj);
                        break;
                    }
                    case 6: {
                        PipMenuActivity.this.mAllowTouches = true;
                        break;
                    }
                    case 5: {
                        PipMenuActivity.this.updateDismissFraction(((Bundle)message.obj).getFloat("dismiss_fraction"));
                        break;
                    }
                    case 4: {
                        final Bundle bundle = (Bundle)message.obj;
                        final ParceledListSlice parceledListSlice = (ParceledListSlice)bundle.getParcelable("actions");
                        final PipMenuActivity this$0 = PipMenuActivity.this;
                        final Rect rect = (Rect)bundle.getParcelable("stack_bounds");
                        List list;
                        if (parceledListSlice != null) {
                            list = parceledListSlice.getList();
                        }
                        else {
                            list = Collections.EMPTY_LIST;
                        }
                        this$0.setActions(rect, list);
                        break;
                    }
                    case 3: {
                        PipMenuActivity.this.hideMenu((Runnable)message.obj);
                        break;
                    }
                    case 2: {
                        PipMenuActivity.this.cancelDelayedFinish();
                        break;
                    }
                    case 1: {
                        final Bundle bundle2 = (Bundle)message.obj;
                        PipMenuActivity.this.showMenu(bundle2.getInt("menu_state"), (Rect)bundle2.getParcelable("stack_bounds"), (Rect)bundle2.getParcelable("movement_bounds"), bundle2.getBoolean("allow_timeout"), bundle2.getBoolean("resize_menu_on_show"));
                        break;
                    }
                }
            }
        });
        this.mFinishRunnable = new Runnable() {
            @Override
            public void run() {
                PipMenuActivity.this.hideMenu();
            }
        };
    }
    
    private void cancelDelayedFinish() {
        this.mHandler.removeCallbacks(this.mFinishRunnable);
    }
    
    private void dismissPip() {
        this.hideMenu(new _$$Lambda$PipMenuActivity$guHLrBiStjvmB9r01MbFqRGaK3c(this), false, true);
    }
    
    private void dispatchPointerEvent(final MotionEvent motionEvent) {
        if (motionEvent.isTouchEvent()) {
            this.dispatchTouchEvent(motionEvent);
        }
        else {
            this.dispatchGenericMotionEvent(motionEvent);
        }
    }
    
    private void expandPip() {
        this.hideMenu(new _$$Lambda$PipMenuActivity$gxeJOYpgn30UbyKen9nD4GpRdFQ(this), false, false);
    }
    
    private void hideMenu() {
        this.hideMenu(null);
    }
    
    private void hideMenu(final Runnable runnable) {
        this.hideMenu(runnable, true, false);
    }
    
    private void hideMenu(final Runnable runnable, final boolean b, final boolean b2) {
        if (this.mMenuState != 0) {
            this.cancelDelayedFinish();
            if (b) {
                this.notifyMenuStateChange(0, this.mResize);
            }
            this.mMenuContainerAnimator = new AnimatorSet();
            final View mMenuContainer = this.mMenuContainer;
            final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)mMenuContainer, View.ALPHA, new float[] { mMenuContainer.getAlpha(), 0.0f });
            ofFloat.addUpdateListener(this.mMenuBgUpdateListener);
            final View mSettingsButton = this.mSettingsButton;
            final ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object)mSettingsButton, View.ALPHA, new float[] { mSettingsButton.getAlpha(), 0.0f });
            final View mDismissButton = this.mDismissButton;
            this.mMenuContainerAnimator.playTogether(new Animator[] { (Animator)ofFloat, (Animator)ofFloat2, (Animator)ObjectAnimator.ofFloat((Object)mDismissButton, View.ALPHA, new float[] { mDismissButton.getAlpha(), 0.0f }) });
            this.mMenuContainerAnimator.setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT);
            this.mMenuContainerAnimator.setDuration(125L);
            this.mMenuContainerAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    final Runnable val$animationFinishedRunnable = runnable;
                    if (val$animationFinishedRunnable != null) {
                        val$animationFinishedRunnable.run();
                    }
                    if (!b2) {
                        PipMenuActivity.this.finish();
                    }
                }
            });
            this.mMenuContainerAnimator.start();
        }
        else {
            this.finish();
        }
    }
    
    private void notifyActivityCallback(final Messenger replyTo) {
        final Message obtain = Message.obtain();
        obtain.what = 104;
        obtain.replyTo = replyTo;
        obtain.arg1 = (this.mResize ? 1 : 0);
        this.sendMessage(obtain, "Could not notify controller of activity finished");
    }
    
    private void notifyMenuStateChange(final int n, final boolean b) {
        this.mMenuState = n;
        this.mResize = b;
        final Message obtain = Message.obtain();
        obtain.what = 100;
        obtain.arg1 = n;
        obtain.arg2 = (b ? 1 : 0);
        this.sendMessage(obtain, "Could not notify controller of PIP menu visibility");
    }
    
    private void repostDelayedFinish(int recommendedTimeoutMillis) {
        recommendedTimeoutMillis = this.mAccessibilityManager.getRecommendedTimeoutMillis(recommendedTimeoutMillis, 5);
        this.mHandler.removeCallbacks(this.mFinishRunnable);
        this.mHandler.postDelayed(this.mFinishRunnable, (long)recommendedTimeoutMillis);
    }
    
    private void sendEmptyMessage(final int what, final String s) {
        final Message obtain = Message.obtain();
        obtain.what = what;
        this.sendMessage(obtain, s);
    }
    
    private void sendMessage(final Message message, final String s) {
        final Messenger mToControllerMessenger = this.mToControllerMessenger;
        if (mToControllerMessenger == null) {
            return;
        }
        try {
            mToControllerMessenger.send(message);
        }
        catch (RemoteException ex) {
            Log.e("PipMenuActivity", s, (Throwable)ex);
        }
    }
    
    private void setActions(final Rect rect, final List<RemoteAction> list) {
        this.mActions.clear();
        this.mActions.addAll(list);
        this.updateActionViews(rect);
    }
    
    private void showMenu(final int n, final Rect rect, final Rect rect2, final boolean mAllowMenuTimeout, final boolean b) {
        this.mAllowMenuTimeout = mAllowMenuTimeout;
        final int mMenuState = this.mMenuState;
        if (mMenuState != n) {
            this.mAllowTouches = ((b && (mMenuState == 2 || n == 2)) ^ true);
            this.cancelDelayedFinish();
            this.updateActionViews(rect);
            final AnimatorSet mMenuContainerAnimator = this.mMenuContainerAnimator;
            if (mMenuContainerAnimator != null) {
                mMenuContainerAnimator.cancel();
            }
            this.notifyMenuStateChange(n, b);
            this.mMenuContainerAnimator = new AnimatorSet();
            final View mMenuContainer = this.mMenuContainer;
            final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)mMenuContainer, View.ALPHA, new float[] { mMenuContainer.getAlpha(), 1.0f });
            ofFloat.addUpdateListener(this.mMenuBgUpdateListener);
            final View mSettingsButton = this.mSettingsButton;
            final ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object)mSettingsButton, View.ALPHA, new float[] { mSettingsButton.getAlpha(), 1.0f });
            final View mDismissButton = this.mDismissButton;
            final ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat((Object)mDismissButton, View.ALPHA, new float[] { mDismissButton.getAlpha(), 1.0f });
            if (n == 2) {
                this.mMenuContainerAnimator.playTogether(new Animator[] { (Animator)ofFloat, (Animator)ofFloat2, (Animator)ofFloat3 });
            }
            else {
                this.mMenuContainerAnimator.playTogether(new Animator[] { (Animator)ofFloat3 });
            }
            this.mMenuContainerAnimator.setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN);
            this.mMenuContainerAnimator.setDuration(125L);
            if (mAllowMenuTimeout) {
                this.mMenuContainerAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        PipMenuActivity.this.repostDelayedFinish(3500);
                    }
                });
            }
            this.mMenuContainerAnimator.start();
        }
        else if (mAllowMenuTimeout) {
            this.repostDelayedFinish(2000);
        }
    }
    
    private void showSettings() {
        final Pair<ComponentName, Integer> topPipActivity = PipUtils.getTopPipActivity((Context)this, ActivityManager.getService());
        if (topPipActivity.first != null) {
            final UserHandle of = UserHandle.of((int)topPipActivity.second);
            final Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS", Uri.fromParts("package", ((ComponentName)topPipActivity.first).getPackageName(), (String)null));
            intent.putExtra("android.intent.extra.user_handle", (Parcelable)of);
            intent.setFlags(268468224);
            this.startActivity(intent);
        }
    }
    
    private void updateActionViews(final Rect rect) {
        final ViewGroup viewGroup = (ViewGroup)this.findViewById(R$id.expand_container);
        final ViewGroup viewGroup2 = (ViewGroup)this.findViewById(R$id.actions_container);
        viewGroup2.setOnTouchListener((View$OnTouchListener)_$$Lambda$PipMenuActivity$BXxmOnLU_s8BTsc_oWau4TVb1pE.INSTANCE);
        if (!this.mActions.isEmpty()) {
            final int mMenuState = this.mMenuState;
            final int n = 1;
            if (mMenuState != 1) {
                viewGroup2.setVisibility(0);
                if (this.mActionsGroup != null) {
                    final LayoutInflater from = LayoutInflater.from((Context)this);
                    while (this.mActionsGroup.getChildCount() < this.mActions.size()) {
                        this.mActionsGroup.addView((View)from.inflate(R$layout.pip_menu_action, (ViewGroup)this.mActionsGroup, false));
                    }
                    for (int i = 0; i < this.mActionsGroup.getChildCount(); ++i) {
                        final View child = this.mActionsGroup.getChildAt(i);
                        int visibility;
                        if (i < this.mActions.size()) {
                            visibility = 0;
                        }
                        else {
                            visibility = 8;
                        }
                        child.setVisibility(visibility);
                    }
                    int n2;
                    if (rect != null && rect.width() > rect.height()) {
                        n2 = n;
                    }
                    else {
                        n2 = 0;
                    }
                    for (int j = 0; j < this.mActions.size(); ++j) {
                        final RemoteAction remoteAction = this.mActions.get(j);
                        final ImageButton imageButton = (ImageButton)this.mActionsGroup.getChildAt(j);
                        remoteAction.getIcon().loadDrawableAsync((Context)this, (Icon$OnDrawableLoadedListener)new _$$Lambda$PipMenuActivity$FgVNA6rqcnXmAeLQlbztL7Zw7mU(imageButton), this.mHandler);
                        imageButton.setContentDescription(remoteAction.getContentDescription());
                        if (remoteAction.isEnabled()) {
                            imageButton.setOnClickListener((View$OnClickListener)new _$$Lambda$PipMenuActivity$Ts5um0YR6IQ0YRdLS2dyHj4GSpg(this, remoteAction));
                        }
                        imageButton.setEnabled(remoteAction.isEnabled());
                        float alpha;
                        if (remoteAction.isEnabled()) {
                            alpha = 1.0f;
                        }
                        else {
                            alpha = 0.54f;
                        }
                        imageButton.setAlpha(alpha);
                        final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)imageButton.getLayoutParams();
                        int mBetweenActionPaddingLand;
                        if (n2 != 0 && j > 0) {
                            mBetweenActionPaddingLand = this.mBetweenActionPaddingLand;
                        }
                        else {
                            mBetweenActionPaddingLand = 0;
                        }
                        linearLayout$LayoutParams.leftMargin = mBetweenActionPaddingLand;
                    }
                }
                final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)viewGroup.getLayoutParams();
                frameLayout$LayoutParams.topMargin = this.getResources().getDimensionPixelSize(R$dimen.pip_action_padding);
                frameLayout$LayoutParams.bottomMargin = this.getResources().getDimensionPixelSize(R$dimen.pip_expand_container_edge_margin);
                viewGroup.requestLayout();
                return;
            }
        }
        viewGroup2.setVisibility(4);
    }
    
    private void updateDismissFraction(final float n) {
        final float n2 = 1.0f - n;
        final int mMenuState = this.mMenuState;
        int alpha;
        if (mMenuState == 2) {
            this.mMenuContainer.setAlpha(n2);
            this.mSettingsButton.setAlpha(n2);
            this.mDismissButton.setAlpha(n2);
            alpha = (int)((n2 * 0.3f + n * 0.6f) * 255.0f);
        }
        else {
            if (mMenuState == 1) {
                this.mDismissButton.setAlpha(n2);
            }
            alpha = (int)(n * 0.6f * 255.0f);
        }
        this.mBackgroundDrawable.setAlpha(alpha);
    }
    
    private void updateFromIntent(final Intent intent) {
        final Messenger mToControllerMessenger = (Messenger)intent.getParcelableExtra("messenger");
        this.mToControllerMessenger = mToControllerMessenger;
        if (mToControllerMessenger == null) {
            Log.w("PipMenuActivity", "Controller messenger is null. Stopping.");
            this.finish();
            return;
        }
        this.notifyActivityCallback(this.mMessenger);
        final ParceledListSlice parceledListSlice = (ParceledListSlice)intent.getParcelableExtra("actions");
        if (parceledListSlice != null) {
            this.mActions.clear();
            this.mActions.addAll(parceledListSlice.getList());
        }
        final int intExtra = intent.getIntExtra("menu_state", 0);
        if (intExtra != 0) {
            this.showMenu(intExtra, (Rect)intent.getParcelableExtra("stack_bounds"), (Rect)intent.getParcelableExtra("movement_bounds"), intent.getBooleanExtra("allow_timeout", true), intent.getBooleanExtra("resize_menu_on_show", false));
        }
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        if (!this.mAllowTouches) {
            return false;
        }
        if (motionEvent.getAction() != 4) {
            return super.dispatchTouchEvent(motionEvent);
        }
        this.hideMenu();
        return true;
    }
    
    public void finish() {
        this.notifyActivityCallback(null);
        super.finish();
    }
    
    protected void onCreate(final Bundle bundle) {
        this.getWindow().addFlags(262144);
        super.onCreate(bundle);
        this.setContentView(R$layout.pip_menu_activity);
        this.mAccessibilityManager = (AccessibilityManager)this.getSystemService((Class)AccessibilityManager.class);
        (this.mBackgroundDrawable = (Drawable)new ColorDrawable(-16777216)).setAlpha(0);
        (this.mViewRoot = this.findViewById(R$id.background)).setBackground(this.mBackgroundDrawable);
        (this.mMenuContainer = this.findViewById(R$id.menu_container)).setAlpha(0.0f);
        (this.mSettingsButton = this.findViewById(R$id.settings)).setAlpha(0.0f);
        this.mSettingsButton.setOnClickListener((View$OnClickListener)new _$$Lambda$PipMenuActivity$4MVIZwVdJN3lkWpqrFrI53Q9bPQ(this));
        (this.mDismissButton = this.findViewById(R$id.dismiss)).setAlpha(0.0f);
        this.mDismissButton.setOnClickListener((View$OnClickListener)new _$$Lambda$PipMenuActivity$lkNLpysIkUfrlXCWX9bvozrYe1U(this));
        this.findViewById(R$id.expand_button).setOnClickListener((View$OnClickListener)new _$$Lambda$PipMenuActivity$70yHDyzrwE1GNEVEQrmSEL7H6fY(this));
        this.mActionsGroup = (LinearLayout)this.findViewById(R$id.actions_group);
        this.mBetweenActionPaddingLand = this.getResources().getDimensionPixelSize(R$dimen.pip_between_action_padding_land);
        this.updateFromIntent(this.getIntent());
        this.setTitle(R$string.pip_menu_title);
        this.setDisablePreviewScreenshots(true);
        this.getWindow().setExitTransition((Transition)null);
    }
    
    protected void onDestroy() {
        super.onDestroy();
        this.notifyActivityCallback(null);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (n == 111) {
            this.hideMenu();
            return true;
        }
        return super.onKeyUp(n, keyEvent);
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.updateFromIntent(intent);
    }
    
    public void onPictureInPictureModeChanged(final boolean b) {
        if (!b) {
            this.finish();
        }
    }
    
    protected void onStop() {
        super.onStop();
        this.hideMenu();
        this.cancelDelayedFinish();
    }
    
    public void onUserInteraction() {
        if (this.mAllowMenuTimeout) {
            this.repostDelayedFinish(2000);
        }
    }
    
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        this.hideMenu();
    }
    
    public void setTaskDescription(final ActivityManager$TaskDescription activityManager$TaskDescription) {
    }
}
