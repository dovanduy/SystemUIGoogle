// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.util.SparseArray;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.Dependency;
import android.os.Bundle;
import com.android.systemui.statusbar.policy.EncryptionHelper;
import com.android.systemui.R$bool;
import android.view.ViewGroup;
import android.view.ViewStub;
import com.android.systemui.R$id;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.view.View;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import android.app.Fragment;

public class CollapsedStatusBarFragment extends Fragment implements Callbacks, StateListener
{
    private View mCenteredIconArea;
    private View mClockView;
    private CommandQueue mCommandQueue;
    private StatusBarIconController.DarkIconManager mDarkIconManager;
    private int mDisabled1;
    private KeyguardStateController mKeyguardStateController;
    private NetworkController mNetworkController;
    private View mNotificationIconAreaInner;
    private View mOperatorNameFrame;
    private NetworkController.SignalCallback mSignalCallback;
    private PhoneStatusBarView mStatusBar;
    private StatusBar mStatusBarComponent;
    private StatusBarStateController mStatusBarStateController;
    private LinearLayout mSystemIconArea;
    
    public CollapsedStatusBarFragment() {
        this.mSignalCallback = new NetworkController.SignalCallback() {
            @Override
            public void setIsAirplaneMode(final IconState iconState) {
                CollapsedStatusBarFragment.this.mCommandQueue.recomputeDisableFlags(CollapsedStatusBarFragment.this.getContext().getDisplayId(), true);
            }
        };
    }
    
    private void animateHiddenState(final View view, final int visibility, final boolean b) {
        view.animate().cancel();
        if (!b) {
            view.setAlpha(0.0f);
            view.setVisibility(visibility);
            return;
        }
        view.animate().alpha(0.0f).setDuration(160L).setStartDelay(0L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).withEndAction((Runnable)new _$$Lambda$CollapsedStatusBarFragment$27RMKG7VU7GD3kVXbGdyl_3FVd4(view, visibility));
    }
    
    private void animateHide(final View view, final boolean b) {
        this.animateHiddenState(view, 4, b);
    }
    
    private void animateShow(final View view, final boolean b) {
        view.animate().cancel();
        view.setVisibility(0);
        if (!b) {
            view.setAlpha(1.0f);
            return;
        }
        view.animate().alpha(1.0f).setDuration(320L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN).setStartDelay(50L).withEndAction((Runnable)null);
        if (this.mKeyguardStateController.isKeyguardFadingAway()) {
            view.animate().setDuration(this.mKeyguardStateController.getKeyguardFadingAwayDuration()).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(this.mKeyguardStateController.getKeyguardFadingAwayDelay()).start();
        }
    }
    
    private int clockHiddenMode() {
        if (!this.mStatusBar.isClosed() && !this.mKeyguardStateController.isShowing() && !this.mStatusBarStateController.isDozing()) {
            return 4;
        }
        return 8;
    }
    
    private void initEmergencyCryptkeeperText() {
        final View viewById = this.mStatusBar.findViewById(R$id.emergency_cryptkeeper_text);
        if (this.mNetworkController.hasEmergencyCryptKeeperText()) {
            if (viewById != null) {
                ((ViewStub)viewById).inflate();
            }
            this.mNetworkController.addCallback(this.mSignalCallback);
        }
        else if (viewById != null) {
            ((ViewGroup)viewById.getParent()).removeView(viewById);
        }
    }
    
    private void initOperatorName() {
        if (this.getResources().getBoolean(R$bool.config_showOperatorNameInStatusBar)) {
            this.mOperatorNameFrame = ((ViewStub)this.mStatusBar.findViewById(R$id.operator_name)).inflate();
        }
    }
    
    private boolean shouldHideNotificationIcons() {
        return (!this.mStatusBar.isClosed() && this.mStatusBarComponent.hideStatusBarIconsWhenExpanded()) || this.mStatusBarComponent.hideStatusBarIconsForBouncer();
    }
    
    protected int adjustDisableFlags(int n) {
        final boolean headsUpShouldBeVisible = this.mStatusBarComponent.headsUpShouldBeVisible();
        int n2 = n;
        if (headsUpShouldBeVisible) {
            n2 = (n | 0x800000);
        }
        int n3 = n2;
        Label_0091: {
            if (!this.mKeyguardStateController.isLaunchTransitionFadingAway()) {
                n3 = n2;
                if (!this.mKeyguardStateController.isKeyguardFadingAway()) {
                    n3 = n2;
                    if (this.shouldHideNotificationIcons()) {
                        if (this.mStatusBarStateController.getState() == 1) {
                            n3 = n2;
                            if (headsUpShouldBeVisible) {
                                break Label_0091;
                            }
                        }
                        n3 = (n2 | 0x20000 | 0x100000 | 0x800000);
                    }
                }
            }
        }
        final NetworkController mNetworkController = this.mNetworkController;
        n = n3;
        if (mNetworkController != null) {
            n = n3;
            if (EncryptionHelper.IS_DATA_ENCRYPTED) {
                int n4 = n3;
                if (mNetworkController.hasEmergencyCryptKeeperText()) {
                    n4 = (n3 | 0x20000);
                }
                n = n4;
                if (!this.mNetworkController.isRadioOn()) {
                    n = (n4 | 0x100000);
                }
            }
        }
        int n5 = n;
        if (this.mStatusBarStateController.isDozing()) {
            n5 = n;
            if (this.mStatusBarComponent.getPanelController().hasCustomClock()) {
                n5 = (n | 0x900000);
            }
        }
        return n5;
    }
    
    public void disable(int n, int adjustDisableFlags, final int n2, final boolean b) {
        if (n != this.getContext().getDisplayId()) {
            return;
        }
        adjustDisableFlags = this.adjustDisableFlags(adjustDisableFlags);
        n = (this.mDisabled1 ^ adjustDisableFlags);
        this.mDisabled1 = adjustDisableFlags;
        if ((n & 0x100000) != 0x0) {
            if ((0x100000 & adjustDisableFlags) != 0x0) {
                this.hideSystemIconArea(b);
                this.hideOperatorName(b);
            }
            else {
                this.showSystemIconArea(b);
                this.showOperatorName(b);
            }
        }
        if ((n & 0x20000) != 0x0) {
            if ((0x20000 & adjustDisableFlags) != 0x0) {
                this.hideNotificationIconArea(b);
            }
            else {
                this.showNotificationIconArea(b);
            }
        }
        if ((n & 0x800000) != 0x0 || this.mClockView.getVisibility() != this.clockHiddenMode()) {
            if ((adjustDisableFlags & 0x800000) != 0x0) {
                this.hideClock(b);
            }
            else {
                this.showClock(b);
            }
        }
    }
    
    public void hideClock(final boolean b) {
        this.animateHiddenState(this.mClockView, this.clockHiddenMode(), b);
    }
    
    public void hideNotificationIconArea(final boolean b) {
        this.animateHide(this.mNotificationIconAreaInner, b);
        this.animateHide(this.mCenteredIconArea, b);
    }
    
    public void hideOperatorName(final boolean b) {
        final View mOperatorNameFrame = this.mOperatorNameFrame;
        if (mOperatorNameFrame != null) {
            this.animateHide(mOperatorNameFrame, b);
        }
    }
    
    public void hideSystemIconArea(final boolean b) {
        this.animateHide((View)this.mSystemIconArea, b);
    }
    
    public void initNotificationIconArea(final NotificationIconAreaController notificationIconAreaController) {
        final ViewGroup viewGroup = (ViewGroup)this.mStatusBar.findViewById(R$id.notification_icon_area);
        final View notificationInnerAreaView = notificationIconAreaController.getNotificationInnerAreaView();
        this.mNotificationIconAreaInner = notificationInnerAreaView;
        if (notificationInnerAreaView.getParent() != null) {
            ((ViewGroup)this.mNotificationIconAreaInner.getParent()).removeView(this.mNotificationIconAreaInner);
        }
        viewGroup.addView(this.mNotificationIconAreaInner);
        final ViewGroup viewGroup2 = (ViewGroup)this.mStatusBar.findViewById(R$id.centered_icon_area);
        final View centeredNotificationAreaView = notificationIconAreaController.getCenteredNotificationAreaView();
        this.mCenteredIconArea = centeredNotificationAreaView;
        if (centeredNotificationAreaView.getParent() != null) {
            ((ViewGroup)this.mCenteredIconArea.getParent()).removeView(this.mCenteredIconArea);
        }
        viewGroup2.addView(this.mCenteredIconArea);
        this.showNotificationIconArea(false);
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
        this.mNetworkController = Dependency.get(NetworkController.class);
        this.mStatusBarStateController = Dependency.get(StatusBarStateController.class);
        this.mStatusBarComponent = Dependency.get(StatusBar.class);
        this.mCommandQueue = Dependency.get(CommandQueue.class);
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        return layoutInflater.inflate(R$layout.status_bar, viewGroup, false);
    }
    
    public void onDestroyView() {
        super.onDestroyView();
        Dependency.get(StatusBarIconController.class).removeIconGroup((StatusBarIconController.IconManager)this.mDarkIconManager);
        if (this.mNetworkController.hasEmergencyCryptKeeperText()) {
            this.mNetworkController.removeCallback(this.mSignalCallback);
        }
    }
    
    public void onDozingChanged(final boolean b) {
        final int displayId = this.getContext().getDisplayId();
        final int mDisabled1 = this.mDisabled1;
        this.disable(displayId, mDisabled1, mDisabled1, false);
    }
    
    public void onPause() {
        super.onPause();
        this.mCommandQueue.removeCallback((CommandQueue.Callbacks)this);
        this.mStatusBarStateController.removeCallback((StatusBarStateController.StateListener)this);
    }
    
    public void onResume() {
        super.onResume();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
        this.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        final SparseArray sparseArray = new SparseArray();
        this.mStatusBar.saveHierarchyState(sparseArray);
        bundle.putSparseParcelableArray("panel_state", sparseArray);
    }
    
    public void onStateChanged(final int n) {
    }
    
    public void onViewCreated(final View view, final Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mStatusBar = (PhoneStatusBarView)view;
        if (bundle != null && bundle.containsKey("panel_state")) {
            this.mStatusBar.restoreHierarchyState(bundle.getSparseParcelableArray("panel_state"));
        }
        ((StatusBarIconController.IconManager)(this.mDarkIconManager = new StatusBarIconController.DarkIconManager((LinearLayout)view.findViewById(R$id.statusIcons), Dependency.get(CommandQueue.class)))).setShouldLog(true);
        Dependency.get(StatusBarIconController.class).addIconGroup((StatusBarIconController.IconManager)this.mDarkIconManager);
        this.mSystemIconArea = (LinearLayout)this.mStatusBar.findViewById(R$id.system_icon_area);
        this.mClockView = this.mStatusBar.findViewById(R$id.clock);
        this.showSystemIconArea(false);
        this.showClock(false);
        this.initEmergencyCryptkeeperText();
        this.initOperatorName();
    }
    
    public void showClock(final boolean b) {
        this.animateShow(this.mClockView, b);
    }
    
    public void showNotificationIconArea(final boolean b) {
        this.animateShow(this.mNotificationIconAreaInner, b);
        this.animateShow(this.mCenteredIconArea, b);
    }
    
    public void showOperatorName(final boolean b) {
        final View mOperatorNameFrame = this.mOperatorNameFrame;
        if (mOperatorNameFrame != null) {
            this.animateShow(mOperatorNameFrame, b);
        }
    }
    
    public void showSystemIconArea(final boolean b) {
        this.animateShow((View)this.mSystemIconArea, b);
    }
}
