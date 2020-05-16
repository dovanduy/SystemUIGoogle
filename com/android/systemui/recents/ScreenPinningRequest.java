// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import android.os.IBinder;
import android.content.IntentFilter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TypeEvaluator;
import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.app.ActivityManager;
import com.android.systemui.R$color;
import android.util.DisplayMetrics;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import com.android.systemui.R$string;
import java.util.function.Function;
import com.android.systemui.statusbar.phone.NavigationBarView;
import android.widget.Button;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.R$layout;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import com.android.systemui.util.leak.RotationUtils;
import android.content.BroadcastReceiver;
import android.view.ViewGroup;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.widget.FrameLayout;
import android.view.ViewGroup$LayoutParams;
import android.os.RemoteException;
import android.app.ActivityTaskManager;
import com.android.systemui.R$id;
import android.widget.FrameLayout$LayoutParams;
import android.view.View;
import android.os.Binder;
import android.view.WindowManager$LayoutParams;
import com.android.systemui.Dependency;
import android.view.WindowManager;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.statusbar.phone.NavigationModeController;
import android.view.View$OnClickListener;

public class ScreenPinningRequest implements View$OnClickListener, ModeChangedListener
{
    private final AccessibilityManager mAccessibilityService;
    private final Context mContext;
    private int mNavBarMode;
    private RequestWindowView mRequestWindow;
    private final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy;
    private final WindowManager mWindowManager;
    private int taskId;
    
    public ScreenPinningRequest(final Context mContext, final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy) {
        this.mContext = mContext;
        this.mStatusBarOptionalLazy = mStatusBarOptionalLazy;
        this.mAccessibilityService = (AccessibilityManager)mContext.getSystemService("accessibility");
        this.mWindowManager = (WindowManager)this.mContext.getSystemService("window");
        final OverviewProxyService overviewProxyService = Dependency.get(OverviewProxyService.class);
        this.mNavBarMode = Dependency.get(NavigationModeController.class).addListener((NavigationModeController.ModeChangedListener)this);
    }
    
    private WindowManager$LayoutParams getWindowLayoutParams() {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-1, -1, 2024, 264, -3);
        windowManager$LayoutParams.token = (IBinder)new Binder();
        windowManager$LayoutParams.privateFlags |= 0x10;
        windowManager$LayoutParams.setTitle((CharSequence)"ScreenPinningConfirmation");
        windowManager$LayoutParams.gravity = 119;
        windowManager$LayoutParams.setFitInsetsTypes(0);
        return windowManager$LayoutParams;
    }
    
    public void clearPrompt() {
        final RequestWindowView mRequestWindow = this.mRequestWindow;
        if (mRequestWindow != null) {
            this.mWindowManager.removeView((View)mRequestWindow);
            this.mRequestWindow = null;
        }
    }
    
    public FrameLayout$LayoutParams getRequestLayoutParams(int n) {
        if (n == 2) {
            n = 19;
        }
        else if (n == 1) {
            n = 21;
        }
        else {
            n = 81;
        }
        return new FrameLayout$LayoutParams(-2, -2, n);
    }
    
    public void onClick(final View view) {
        while (true) {
            if (view.getId() != R$id.screen_pinning_ok_button) {
                if (this.mRequestWindow != view) {
                    break Label_0030;
                }
            }
            try {
                ActivityTaskManager.getService().startSystemLockTaskMode(this.taskId);
                this.clearPrompt();
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
    
    public void onConfigurationChanged() {
        final RequestWindowView mRequestWindow = this.mRequestWindow;
        if (mRequestWindow != null) {
            mRequestWindow.onConfigurationChanged();
        }
    }
    
    public void onNavigationModeChanged(final int mNavBarMode) {
        this.mNavBarMode = mNavBarMode;
    }
    
    public void showPrompt(final int taskId, final boolean b) {
        while (true) {
            try {
                this.clearPrompt();
                this.taskId = taskId;
                (this.mRequestWindow = new RequestWindowView(this.mContext, b)).setSystemUiVisibility(256);
                this.mWindowManager.addView((View)this.mRequestWindow, (ViewGroup$LayoutParams)this.getWindowLayoutParams());
            }
            catch (IllegalArgumentException ex) {
                continue;
            }
            break;
        }
    }
    
    private class RequestWindowView extends FrameLayout
    {
        private final BroadcastDispatcher mBroadcastDispatcher;
        private final ColorDrawable mColor;
        private ValueAnimator mColorAnim;
        private ViewGroup mLayout;
        private final BroadcastReceiver mReceiver;
        private boolean mShowCancel;
        private final Runnable mUpdateLayoutRunnable;
        final /* synthetic */ ScreenPinningRequest this$0;
        
        public RequestWindowView(final Context context, final boolean mShowCancel) {
            super(context);
            this.mColor = new ColorDrawable(0);
            this.mBroadcastDispatcher = Dependency.get(BroadcastDispatcher.class);
            this.mUpdateLayoutRunnable = new Runnable() {
                @Override
                public void run() {
                    if (RequestWindowView.this.mLayout != null && RequestWindowView.this.mLayout.getParent() != null) {
                        final ViewGroup access$500 = RequestWindowView.this.mLayout;
                        final RequestWindowView this$1 = RequestWindowView.this;
                        access$500.setLayoutParams((ViewGroup$LayoutParams)this$1.this$0.getRequestLayoutParams(RotationUtils.getRotation(this$1.mContext)));
                    }
                }
            };
            this.mReceiver = new BroadcastReceiver() {
                public void onReceive(final Context context, final Intent intent) {
                    if (intent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
                        final RequestWindowView this$1 = RequestWindowView.this;
                        this$1.post(this$1.mUpdateLayoutRunnable);
                    }
                    else if (intent.getAction().equals("android.intent.action.USER_SWITCHED") || intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                        ScreenPinningRequest.this.clearPrompt();
                    }
                }
            };
            this.setClickable(true);
            this.setOnClickListener((View$OnClickListener)ScreenPinningRequest.this);
            this.setBackground((Drawable)this.mColor);
            this.mShowCancel = mShowCancel;
        }
        
        private void inflateView(final int n) {
            final Context context = this.getContext();
            final int n2 = 1;
            int n3;
            if (n == 2) {
                n3 = R$layout.screen_pinning_request_sea_phone;
            }
            else if (n == 1) {
                n3 = R$layout.screen_pinning_request_land_phone;
            }
            else {
                n3 = R$layout.screen_pinning_request;
            }
            (this.mLayout = (ViewGroup)View.inflate(context, n3, (ViewGroup)null)).setClickable(true);
            final ViewGroup mLayout = this.mLayout;
            final int n4 = 0;
            mLayout.setLayoutDirection(0);
            this.mLayout.findViewById(R$id.screen_pinning_text_area).setLayoutDirection(3);
            final View viewById = this.mLayout.findViewById(R$id.screen_pinning_buttons);
            final WindowManagerWrapper instance = WindowManagerWrapper.getInstance();
            if (!QuickStepContract.isGesturalMode(ScreenPinningRequest.this.mNavBarMode) && instance.hasSoftNavigationBar(super.mContext.getDisplayId())) {
                viewById.setLayoutDirection(3);
                this.swapChildrenIfRtlAndVertical(viewById);
            }
            else {
                viewById.setVisibility(8);
            }
            ((Button)this.mLayout.findViewById(R$id.screen_pinning_ok_button)).setOnClickListener((View$OnClickListener)ScreenPinningRequest.this);
            if (this.mShowCancel) {
                ((Button)this.mLayout.findViewById(R$id.screen_pinning_cancel_button)).setOnClickListener((View$OnClickListener)ScreenPinningRequest.this);
            }
            else {
                ((Button)this.mLayout.findViewById(R$id.screen_pinning_cancel_button)).setVisibility(4);
            }
            final NavigationBarView navigationBarView = ScreenPinningRequest.this.mStatusBarOptionalLazy.map((Function<? super Object, ? extends NavigationBarView>)_$$Lambda$ScreenPinningRequest$RequestWindowView$iq7_kF2IL9FTwkRZM6zjXuxpxgs.INSTANCE).orElse(null);
            int n5;
            if (navigationBarView != null && navigationBarView.isRecentsButtonVisible()) {
                n5 = n2;
            }
            else {
                n5 = 0;
            }
            final boolean touchExplorationEnabled = ScreenPinningRequest.this.mAccessibilityService.isTouchExplorationEnabled();
            int text;
            if (QuickStepContract.isGesturalMode(ScreenPinningRequest.this.mNavBarMode)) {
                text = R$string.screen_pinning_description_gestural;
            }
            else if (n5 != 0) {
                this.mLayout.findViewById(R$id.screen_pinning_recents_group).setVisibility(0);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg_light).setVisibility(4);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg).setVisibility(4);
                if (touchExplorationEnabled) {
                    text = R$string.screen_pinning_description_accessible;
                }
                else {
                    text = R$string.screen_pinning_description;
                }
            }
            else {
                this.mLayout.findViewById(R$id.screen_pinning_recents_group).setVisibility(4);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg_light).setVisibility(0);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg).setVisibility(0);
                if (touchExplorationEnabled) {
                    text = R$string.screen_pinning_description_recents_invisible_accessible;
                }
                else {
                    text = R$string.screen_pinning_description_recents_invisible;
                }
            }
            if (navigationBarView != null) {
                ((ImageView)this.mLayout.findViewById(R$id.screen_pinning_back_icon)).setImageDrawable((Drawable)navigationBarView.getBackDrawable());
                ((ImageView)this.mLayout.findViewById(R$id.screen_pinning_home_icon)).setImageDrawable((Drawable)navigationBarView.getHomeDrawable());
            }
            ((TextView)this.mLayout.findViewById(R$id.screen_pinning_description)).setText(text);
            int n6 = n4;
            if (touchExplorationEnabled) {
                n6 = 4;
            }
            this.mLayout.findViewById(R$id.screen_pinning_back_bg).setVisibility(n6);
            this.mLayout.findViewById(R$id.screen_pinning_back_bg_light).setVisibility(n6);
            this.addView((View)this.mLayout, (ViewGroup$LayoutParams)ScreenPinningRequest.this.getRequestLayoutParams(n));
        }
        
        private void swapChildrenIfRtlAndVertical(final View view) {
            if (super.mContext.getResources().getConfiguration().getLayoutDirection() != 1) {
                return;
            }
            final LinearLayout linearLayout = (LinearLayout)view;
            if (linearLayout.getOrientation() == 1) {
                final int childCount = linearLayout.getChildCount();
                final ArrayList list = new ArrayList<View>(childCount);
                for (int i = 0; i < childCount; ++i) {
                    list.add(linearLayout.getChildAt(i));
                }
                linearLayout.removeAllViews();
                for (int j = childCount - 1; j >= 0; --j) {
                    linearLayout.addView((View)list.get(j));
                }
            }
        }
        
        public void onAttachedToWindow() {
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            ScreenPinningRequest.this.mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
            final float density = displayMetrics.density;
            final int rotation = RotationUtils.getRotation(super.mContext);
            this.inflateView(rotation);
            final int color = super.mContext.getColor(R$color.screen_pinning_request_window_bg);
            if (ActivityManager.isHighEndGfx()) {
                this.mLayout.setAlpha(0.0f);
                if (rotation == 2) {
                    this.mLayout.setTranslationX(density * -96.0f);
                }
                else if (rotation == 1) {
                    this.mLayout.setTranslationX(density * 96.0f);
                }
                else {
                    this.mLayout.setTranslationY(density * 96.0f);
                }
                this.mLayout.animate().alpha(1.0f).translationX(0.0f).translationY(0.0f).setDuration(300L).setInterpolator((TimeInterpolator)new DecelerateInterpolator()).start();
                (this.mColorAnim = ValueAnimator.ofObject((TypeEvaluator)new ArgbEvaluator(), new Object[] { 0, color })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                    public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                        RequestWindowView.this.mColor.setColor((int)valueAnimator.getAnimatedValue());
                    }
                });
                this.mColorAnim.setDuration(1000L);
                this.mColorAnim.start();
            }
            else {
                this.mColor.setColor(color);
            }
            final IntentFilter intentFilter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter);
        }
        
        protected void onConfigurationChanged() {
            this.removeAllViews();
            this.inflateView(RotationUtils.getRotation(super.mContext));
        }
        
        public void onDetachedFromWindow() {
            this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
        }
    }
}
