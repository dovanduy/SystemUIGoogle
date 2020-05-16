// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.qs.QSPanel;
import android.graphics.drawable.Drawable;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import android.content.res.Configuration;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.Dependency;
import android.view.WindowInsets;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.ViewParent;
import android.widget.LinearLayout$LayoutParams;
import android.widget.RelativeLayout$LayoutParams;
import com.android.systemui.ScreenDecorations;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import com.android.systemui.R$id;
import com.android.systemui.R$color;
import android.graphics.Color;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.view.ViewPropertyAnimator;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.ViewTreeObserver$OnPreDrawListener;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import android.view.ViewGroup;
import android.util.Pair;
import android.widget.ImageView;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import android.graphics.Rect;
import android.view.DisplayCutout;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.BatteryController;
import android.widget.RelativeLayout;

public class KeyguardStatusBarView extends RelativeLayout implements BatteryStateChangeCallback, OnUserInfoChangedListener, ConfigurationListener
{
    private boolean mBatteryCharging;
    private BatteryController mBatteryController;
    private boolean mBatteryListening;
    private BatteryMeterView mBatteryView;
    private TextView mCarrierLabel;
    private int mCutoutSideNudge;
    private View mCutoutSpace;
    private DisplayCutout mDisplayCutout;
    private final Rect mEmptyRect;
    private StatusBarIconController.TintedIconManager mIconManager;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    private boolean mKeyguardUserSwitcherShowing;
    private int mLayoutState;
    private ImageView mMultiUserAvatar;
    private MultiUserSwitch mMultiUserSwitch;
    private Pair<Integer, Integer> mPadding;
    private int mRoundedCornerPadding;
    private boolean mShowPercentAvailable;
    private ViewGroup mStatusIconArea;
    private int mSystemIconsBaseMargin;
    private View mSystemIconsContainer;
    private int mSystemIconsSwitcherHiddenExpandedMargin;
    private UserSwitcherController mUserSwitcherController;
    
    public KeyguardStatusBarView(final Context context, final AttributeSet set) {
        super(context, set);
        final Integer value = 0;
        this.mEmptyRect = new Rect(0, 0, 0, 0);
        this.mLayoutState = 0;
        this.mCutoutSideNudge = 0;
        this.mRoundedCornerPadding = 0;
        this.mPadding = (Pair<Integer, Integer>)new Pair((Object)value, (Object)value);
    }
    
    private void animateNextLayoutChange() {
        this.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            final /* synthetic */ int val$systemIconsCurrentX = KeyguardStatusBarView.this.mSystemIconsContainer.getLeft();
            final /* synthetic */ boolean val$userSwitcherVisible = KeyguardStatusBarView.this.mMultiUserSwitch.getParent() == KeyguardStatusBarView.this.mStatusIconArea;
            
            public boolean onPreDraw() {
                KeyguardStatusBarView.this.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                final boolean b = this.val$userSwitcherVisible && KeyguardStatusBarView.this.mMultiUserSwitch.getParent() != KeyguardStatusBarView.this.mStatusIconArea;
                KeyguardStatusBarView.this.mSystemIconsContainer.setX((float)this.val$systemIconsCurrentX);
                final ViewPropertyAnimator setDuration = KeyguardStatusBarView.this.mSystemIconsContainer.animate().translationX(0.0f).setDuration(400L);
                long startDelay;
                if (b) {
                    startDelay = 300L;
                }
                else {
                    startDelay = 0L;
                }
                setDuration.setStartDelay(startDelay).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).start();
                if (b) {
                    KeyguardStatusBarView.this.getOverlay().add((View)KeyguardStatusBarView.this.mMultiUserSwitch);
                    KeyguardStatusBarView.this.mMultiUserSwitch.animate().alpha(0.0f).setDuration(300L).setStartDelay(0L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).withEndAction((Runnable)new _$$Lambda$KeyguardStatusBarView$1$DyabYtIeJMptnepd5jqXSnZ7UZ0(this)).start();
                }
                else {
                    KeyguardStatusBarView.this.mMultiUserSwitch.setAlpha(0.0f);
                    KeyguardStatusBarView.this.mMultiUserSwitch.animate().alpha(1.0f).setDuration(300L).setStartDelay(200L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN);
                }
                return true;
            }
        });
    }
    
    private void applyDarkness(final int n, final Rect rect, final float n2, final int n3) {
        final View viewById = this.findViewById(n);
        if (viewById instanceof DarkIconDispatcher.DarkReceiver) {
            ((DarkIconDispatcher.DarkReceiver)viewById).onDarkChanged(rect, n2, n3);
        }
    }
    
    private int calculateMargin(final int n, final int n2) {
        if (n2 >= n) {
            return 0;
        }
        return n - n2;
    }
    
    private void loadDimens() {
        final Resources resources = this.getResources();
        this.mSystemIconsSwitcherHiddenExpandedMargin = resources.getDimensionPixelSize(R$dimen.system_icons_switcher_hidden_expanded_margin);
        this.mSystemIconsBaseMargin = resources.getDimensionPixelSize(R$dimen.system_icons_super_container_avatarless_margin_end);
        this.mCutoutSideNudge = this.getResources().getDimensionPixelSize(R$dimen.display_cutout_margin_consumption);
        this.mShowPercentAvailable = this.getContext().getResources().getBoolean(17891375);
        this.mRoundedCornerPadding = resources.getDimensionPixelSize(R$dimen.rounded_corner_content_padding);
    }
    
    private void updateIconsAndTextColors() {
        final int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(super.mContext, R$attr.wallpaperTextColor);
        final Context mContext = super.mContext;
        int n;
        if (Color.luminance(colorAttrDefaultColor) < 0.5) {
            n = R$color.dark_mode_icon_color_single_tone;
        }
        else {
            n = R$color.light_mode_icon_color_single_tone;
        }
        final int colorStateListDefaultColor = Utils.getColorStateListDefaultColor(mContext, n);
        float n2;
        if (colorAttrDefaultColor == -1) {
            n2 = 0.0f;
        }
        else {
            n2 = 1.0f;
        }
        this.mCarrierLabel.setTextColor(colorStateListDefaultColor);
        final StatusBarIconController.TintedIconManager mIconManager = this.mIconManager;
        if (mIconManager != null) {
            mIconManager.setTint(colorStateListDefaultColor);
        }
        this.applyDarkness(R$id.battery, this.mEmptyRect, n2, colorStateListDefaultColor);
        this.applyDarkness(R$id.clock, this.mEmptyRect, n2, colorStateListDefaultColor);
    }
    
    private void updateKeyguardStatusBarHeight() {
        final DisplayCutout mDisplayCutout = this.mDisplayCutout;
        int top;
        if (mDisplayCutout == null) {
            top = 0;
        }
        else {
            top = mDisplayCutout.getWaterfallInsets().top;
        }
        final ViewGroup$MarginLayoutParams layoutParams = (ViewGroup$MarginLayoutParams)this.getLayoutParams();
        layoutParams.height = this.getResources().getDimensionPixelSize(R$dimen.status_bar_header_height_keyguard) + top;
        this.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    private boolean updateLayoutConsideringCutout() {
        this.mDisplayCutout = this.getRootWindowInsets().getDisplayCutout();
        this.updateKeyguardStatusBarHeight();
        final Pair<Integer, Integer> cornerCutoutMargins = StatusBarWindowView.cornerCutoutMargins(this.mDisplayCutout, this.getDisplay());
        this.updatePadding(cornerCutoutMargins);
        if (this.mDisplayCutout != null && cornerCutoutMargins == null) {
            return this.updateLayoutParamsForCutout();
        }
        return this.updateLayoutParamsNoCutout();
    }
    
    private boolean updateLayoutParamsForCutout() {
        if (this.mLayoutState == 1) {
            return false;
        }
        this.mLayoutState = 1;
        if (this.mCutoutSpace == null) {
            this.updateLayoutParamsNoCutout();
        }
        final Rect rect = new Rect();
        ScreenDecorations.DisplayCutoutView.boundsFromDirection(this.mDisplayCutout, 48, rect);
        this.mCutoutSpace.setVisibility(0);
        final RelativeLayout$LayoutParams relativeLayout$LayoutParams = (RelativeLayout$LayoutParams)this.mCutoutSpace.getLayoutParams();
        final int left = rect.left;
        final int mCutoutSideNudge = this.mCutoutSideNudge;
        rect.left = left + mCutoutSideNudge;
        rect.right -= mCutoutSideNudge;
        relativeLayout$LayoutParams.width = rect.width();
        relativeLayout$LayoutParams.height = rect.height();
        relativeLayout$LayoutParams.addRule(13);
        ((RelativeLayout$LayoutParams)this.mCarrierLabel.getLayoutParams()).addRule(16, R$id.cutout_space_view);
        final RelativeLayout$LayoutParams relativeLayout$LayoutParams2 = (RelativeLayout$LayoutParams)this.mStatusIconArea.getLayoutParams();
        relativeLayout$LayoutParams2.addRule(1, R$id.cutout_space_view);
        relativeLayout$LayoutParams2.width = -1;
        ((LinearLayout$LayoutParams)this.mSystemIconsContainer.getLayoutParams()).setMarginStart(0);
        return true;
    }
    
    private boolean updateLayoutParamsNoCutout() {
        if (this.mLayoutState == 2) {
            return false;
        }
        this.mLayoutState = 2;
        final View mCutoutSpace = this.mCutoutSpace;
        if (mCutoutSpace != null) {
            mCutoutSpace.setVisibility(8);
        }
        ((RelativeLayout$LayoutParams)this.mCarrierLabel.getLayoutParams()).addRule(16, R$id.status_icon_area);
        final RelativeLayout$LayoutParams relativeLayout$LayoutParams = (RelativeLayout$LayoutParams)this.mStatusIconArea.getLayoutParams();
        relativeLayout$LayoutParams.removeRule(1);
        relativeLayout$LayoutParams.width = -2;
        ((LinearLayout$LayoutParams)this.mSystemIconsContainer.getLayoutParams()).setMarginStart(this.getResources().getDimensionPixelSize(R$dimen.system_icons_super_container_margin_start));
        return true;
    }
    
    private void updatePadding(final Pair<Integer, Integer> pair) {
        final DisplayCutout mDisplayCutout = this.mDisplayCutout;
        int top;
        if (mDisplayCutout == null) {
            top = 0;
        }
        else {
            top = mDisplayCutout.getWaterfallInsets().top;
        }
        final Pair<Integer, Integer> paddingNeededForCutoutAndRoundedCorner = StatusBarWindowView.paddingNeededForCutoutAndRoundedCorner(this.mDisplayCutout, pair, this.mRoundedCornerPadding);
        this.mPadding = paddingNeededForCutoutAndRoundedCorner;
        this.setPadding((int)paddingNeededForCutoutAndRoundedCorner.first, top, (int)this.mPadding.second, 0);
    }
    
    private void updateSystemIconsLayoutParams() {
        final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)this.mSystemIconsContainer.getLayoutParams();
        int n;
        if (this.mMultiUserSwitch.getVisibility() == 8) {
            n = this.mSystemIconsBaseMargin;
        }
        else {
            n = 0;
        }
        if (this.mKeyguardUserSwitcherShowing) {
            n = this.mSystemIconsSwitcherHiddenExpandedMargin;
        }
        final int calculateMargin = this.calculateMargin(n, (int)this.mPadding.second);
        if (calculateMargin != layoutParams.getMarginEnd()) {
            layoutParams.setMarginEnd(calculateMargin);
            this.mSystemIconsContainer.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
    }
    
    private void updateUserSwitcher() {
        final boolean keyguardMode = this.mKeyguardUserSwitcher != null;
        this.mMultiUserSwitch.setClickable(keyguardMode);
        this.mMultiUserSwitch.setFocusable(keyguardMode);
        this.mMultiUserSwitch.setKeyguardMode(keyguardMode);
    }
    
    private void updateVisibilities() {
        final ViewParent parent = this.mMultiUserSwitch.getParent();
        final ViewGroup mStatusIconArea = this.mStatusIconArea;
        final boolean b = false;
        if (parent != mStatusIconArea && !this.mKeyguardUserSwitcherShowing) {
            if (this.mMultiUserSwitch.getParent() != null) {
                this.getOverlay().remove((View)this.mMultiUserSwitch);
            }
            this.mStatusIconArea.addView((View)this.mMultiUserSwitch, 0);
        }
        else {
            final ViewParent parent2 = this.mMultiUserSwitch.getParent();
            final ViewGroup mStatusIconArea2 = this.mStatusIconArea;
            if (parent2 == mStatusIconArea2 && this.mKeyguardUserSwitcherShowing) {
                mStatusIconArea2.removeView((View)this.mMultiUserSwitch);
            }
        }
        if (this.mKeyguardUserSwitcher == null) {
            if (this.mMultiUserSwitch.isMultiUserEnabled()) {
                this.mMultiUserSwitch.setVisibility(0);
            }
            else {
                this.mMultiUserSwitch.setVisibility(8);
            }
        }
        final BatteryMeterView mBatteryView = this.mBatteryView;
        boolean forceShowPercent = b;
        if (this.mBatteryCharging) {
            forceShowPercent = b;
            if (this.mShowPercentAvailable) {
                forceShowPercent = true;
            }
        }
        mBatteryView.setForceShowPercent(forceShowPercent);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyguardStatusBarView:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mBatteryCharging: ");
        sb.append(this.mBatteryCharging);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mKeyguardUserSwitcherShowing: ");
        sb2.append(this.mKeyguardUserSwitcherShowing);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mBatteryListening: ");
        sb3.append(this.mBatteryListening);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mLayoutState: ");
        sb4.append(this.mLayoutState);
        printWriter.println(sb4.toString());
        final BatteryMeterView mBatteryView = this.mBatteryView;
        if (mBatteryView != null) {
            mBatteryView.dump(fileDescriptor, printWriter, array);
        }
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        this.mLayoutState = 0;
        if (this.updateLayoutConsideringCutout()) {
            this.requestLayout();
        }
        return super.onApplyWindowInsets(windowInsets);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final UserInfoController userInfoController = Dependency.get(UserInfoController.class);
        userInfoController.addCallback((UserInfoController.OnUserInfoChangedListener)this);
        final UserSwitcherController userSwitcherController = Dependency.get(UserSwitcherController.class);
        this.mUserSwitcherController = userSwitcherController;
        this.mMultiUserSwitch.setUserSwitcherController(userSwitcherController);
        userInfoController.reloadUserInfo();
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
        this.mIconManager = new StatusBarIconController.TintedIconManager((ViewGroup)this.findViewById(R$id.statusIcons), Dependency.get(CommandQueue.class));
        Dependency.get(StatusBarIconController.class).addIconGroup((StatusBarIconController.IconManager)this.mIconManager);
        this.onThemeChanged();
    }
    
    public void onBatteryLevelChanged(final int n, final boolean b, final boolean mBatteryCharging) {
        if (this.mBatteryCharging != mBatteryCharging) {
            this.mBatteryCharging = mBatteryCharging;
            this.updateVisibilities();
        }
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final ViewGroup$MarginLayoutParams layoutParams = (ViewGroup$MarginLayoutParams)this.mMultiUserAvatar.getLayoutParams();
        final int dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.multi_user_avatar_keyguard_size);
        layoutParams.height = dimensionPixelSize;
        layoutParams.width = dimensionPixelSize;
        this.mMultiUserAvatar.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        final ViewGroup$MarginLayoutParams layoutParams2 = (ViewGroup$MarginLayoutParams)this.mMultiUserSwitch.getLayoutParams();
        layoutParams2.width = this.getResources().getDimensionPixelSize(R$dimen.multi_user_switch_width_keyguard);
        layoutParams2.setMarginEnd(this.getResources().getDimensionPixelSize(R$dimen.multi_user_switch_keyguard_margin));
        this.mMultiUserSwitch.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
        final ViewGroup$MarginLayoutParams layoutParams3 = (ViewGroup$MarginLayoutParams)this.mSystemIconsContainer.getLayoutParams();
        layoutParams3.setMarginStart(this.getResources().getDimensionPixelSize(R$dimen.system_icons_super_container_margin_start));
        this.mSystemIconsContainer.setLayoutParams((ViewGroup$LayoutParams)layoutParams3);
        final View mSystemIconsContainer = this.mSystemIconsContainer;
        mSystemIconsContainer.setPaddingRelative(mSystemIconsContainer.getPaddingStart(), this.mSystemIconsContainer.getPaddingTop(), this.getResources().getDimensionPixelSize(R$dimen.system_icons_keyguard_padding_end), this.mSystemIconsContainer.getPaddingBottom());
        this.mCarrierLabel.setTextSize(0, (float)this.getResources().getDimensionPixelSize(17105499));
        final ViewGroup$MarginLayoutParams layoutParams4 = (ViewGroup$MarginLayoutParams)this.mCarrierLabel.getLayoutParams();
        layoutParams4.setMarginStart(this.calculateMargin(this.getResources().getDimensionPixelSize(R$dimen.keyguard_carrier_text_margin), (int)this.mPadding.first));
        this.mCarrierLabel.setLayoutParams((ViewGroup$LayoutParams)layoutParams4);
        this.updateKeyguardStatusBarHeight();
    }
    
    public void onDensityOrFontScaleChanged() {
        this.loadDimens();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(UserInfoController.class).removeCallback((UserInfoController.OnUserInfoChangedListener)this);
        Dependency.get(StatusBarIconController.class).removeIconGroup((StatusBarIconController.IconManager)this.mIconManager);
        Dependency.get(ConfigurationController.class).removeCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSystemIconsContainer = this.findViewById(R$id.system_icons_container);
        this.mMultiUserSwitch = (MultiUserSwitch)this.findViewById(R$id.multi_user_switch);
        this.mMultiUserAvatar = (ImageView)this.findViewById(R$id.multi_user_avatar);
        this.mCarrierLabel = (TextView)this.findViewById(R$id.keyguard_carrier_text);
        this.mBatteryView = (BatteryMeterView)this.mSystemIconsContainer.findViewById(R$id.battery);
        this.mCutoutSpace = this.findViewById(R$id.cutout_space_view);
        this.mStatusIconArea = (ViewGroup)this.findViewById(R$id.status_icon_area);
        final StatusIconContainer statusIconContainer = (StatusIconContainer)this.findViewById(R$id.statusIcons);
        this.loadDimens();
        this.updateUserSwitcher();
        this.mBatteryController = Dependency.get(BatteryController.class);
    }
    
    public void onOverlayChanged() {
        this.mCarrierLabel.setTextAppearance(Utils.getThemeAttr(super.mContext, 16842818));
        this.onThemeChanged();
        this.mBatteryView.updatePercentView();
    }
    
    public void onPowerSaveChanged(final boolean b) {
    }
    
    public void onThemeChanged() {
        this.mBatteryView.setColorsFromContext(super.mContext);
        this.updateIconsAndTextColors();
        Dependency.get((Class<UserInfoControllerImpl>)UserInfoController.class).onDensityOrFontScaleChanged();
    }
    
    public void onUserInfoChanged(final String s, final Drawable imageDrawable, final String s2) {
        this.mMultiUserAvatar.setImageDrawable(imageDrawable);
    }
    
    public void setKeyguardUserSwitcher(final KeyguardUserSwitcher keyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = keyguardUserSwitcher;
        this.mMultiUserSwitch.setKeyguardUserSwitcher(keyguardUserSwitcher);
        this.updateUserSwitcher();
    }
    
    public void setKeyguardUserSwitcherShowing(final boolean mKeyguardUserSwitcherShowing, final boolean b) {
        this.mKeyguardUserSwitcherShowing = mKeyguardUserSwitcherShowing;
        if (b) {
            this.animateNextLayoutChange();
        }
        this.updateVisibilities();
        this.updateLayoutConsideringCutout();
        this.updateSystemIconsLayoutParams();
    }
    
    public void setListening(final boolean mBatteryListening) {
        if (mBatteryListening == this.mBatteryListening) {
            return;
        }
        this.mBatteryListening = mBatteryListening;
        if (mBatteryListening) {
            this.mBatteryController.addCallback((BatteryController.BatteryStateChangeCallback)this);
        }
        else {
            this.mBatteryController.removeCallback((BatteryController.BatteryStateChangeCallback)this);
        }
    }
    
    public void setQSPanel(final QSPanel qsPanel) {
        this.mMultiUserSwitch.setQsPanel(qsPanel);
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        if (visibility != 0) {
            this.mSystemIconsContainer.animate().cancel();
            this.mSystemIconsContainer.setTranslationX(0.0f);
            this.mMultiUserSwitch.animate().cancel();
            this.mMultiUserSwitch.setAlpha(1.0f);
        }
        else {
            this.updateVisibilities();
            this.updateSystemIconsLayoutParams();
        }
    }
}
