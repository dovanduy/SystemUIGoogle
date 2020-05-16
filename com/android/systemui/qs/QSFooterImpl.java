// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.os.Bundle;
import android.graphics.PorterDuff$Mode;
import com.android.settingslib.Utils;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.View$OnLayoutChangeListener;
import android.graphics.drawable.RippleDrawable;
import android.content.res.Configuration;
import com.android.internal.logging.MetricsLogger;
import android.provider.Settings$Global;
import android.os.UserManager;
import com.android.systemui.R$dimen;
import android.content.Intent;
import android.os.Build;
import android.os.Build$VERSION;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.systemui.R$id;
import android.widget.TextView;
import android.widget.Toast;
import com.android.systemui.R$string;
import com.android.systemui.tuner.TunerService;
import android.net.Uri;
import android.os.Handler;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.phone.SettingsButton;
import com.android.systemui.statusbar.phone.MultiUserSwitch;
import android.widget.ImageView;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.database.ContentObserver;
import com.android.systemui.plugins.ActivityStarter;
import android.view.View;
import com.android.systemui.statusbar.policy.UserInfoController;
import android.view.View$OnClickListener;
import android.widget.FrameLayout;

public class QSFooterImpl extends FrameLayout implements QSFooter, View$OnClickListener, OnUserInfoChangedListener
{
    private View mActionsContainer;
    private final ActivityStarter mActivityStarter;
    private final ContentObserver mDeveloperSettingsObserver;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private View mDragHandle;
    protected View mEdit;
    protected View mEditContainer;
    private View$OnClickListener mExpandClickListener;
    private boolean mExpanded;
    private float mExpansionAmount;
    protected TouchAnimator mFooterAnimator;
    private boolean mListening;
    private ImageView mMultiUserAvatar;
    protected MultiUserSwitch mMultiUserSwitch;
    private PageIndicator mPageIndicator;
    private boolean mQsDisabled;
    private QSPanel mQsPanel;
    private QuickQSPanel mQuickQsPanel;
    private SettingsButton mSettingsButton;
    private TouchAnimator mSettingsCogAnimator;
    protected View mSettingsContainer;
    private final UserInfoController mUserInfoController;
    
    public QSFooterImpl(final Context context, final AttributeSet set) {
        this(context, set, Dependency.get(ActivityStarter.class), Dependency.get(UserInfoController.class), Dependency.get(DeviceProvisionedController.class));
    }
    
    public QSFooterImpl(final Context context, final AttributeSet set, final ActivityStarter mActivityStarter, final UserInfoController mUserInfoController, final DeviceProvisionedController mDeviceProvisionedController) {
        super(context, set);
        this.mDeveloperSettingsObserver = new ContentObserver(new Handler(super.mContext.getMainLooper())) {
            public void onChange(final boolean b, final Uri uri) {
                super.onChange(b, uri);
                QSFooterImpl.this.setBuildText();
            }
        };
        this.mActivityStarter = mActivityStarter;
        this.mUserInfoController = mUserInfoController;
        this.mDeviceProvisionedController = mDeviceProvisionedController;
    }
    
    private TouchAnimator createFooterAnimator() {
        final TouchAnimator.Builder builder = new TouchAnimator.Builder();
        builder.addFloat(this.mActionsContainer, "alpha", 0.0f, 1.0f);
        builder.addFloat(this.mEditContainer, "alpha", 0.0f, 1.0f);
        builder.addFloat(this.mDragHandle, "alpha", 1.0f, 0.0f, 0.0f);
        builder.addFloat(this.mPageIndicator, "alpha", 0.0f, 1.0f);
        builder.setStartDelay(0.15f);
        return builder.build();
    }
    
    private void setBuildText() {
        final TextView textView = (TextView)this.findViewById(R$id.build);
        if (textView == null) {
            return;
        }
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(super.mContext)) {
            textView.setText((CharSequence)super.mContext.getString(17039779, new Object[] { Build$VERSION.RELEASE_OR_CODENAME, Build.ID }));
            textView.setVisibility(0);
        }
        else {
            textView.setVisibility(8);
        }
    }
    
    private boolean showUserSwitcher() {
        return this.mExpanded && this.mMultiUserSwitch.isMultiUserEnabled();
    }
    
    private void startSettingsActivity() {
        this.mActivityStarter.startActivity(new Intent("android.settings.SETTINGS"), true);
    }
    
    private void updateAnimator(int dimensionPixelOffset) {
        final QuickQSPanel mQuickQsPanel = this.mQuickQsPanel;
        int n;
        if (mQuickQsPanel != null) {
            n = mQuickQsPanel.getNumQuickTiles();
        }
        else {
            n = QuickQSPanel.getDefaultMaxTiles();
        }
        final int n2 = (dimensionPixelOffset - (super.mContext.getResources().getDimensionPixelSize(R$dimen.qs_quick_tile_size) - super.mContext.getResources().getDimensionPixelSize(R$dimen.qs_quick_tile_padding)) * n) / (n - 1);
        dimensionPixelOffset = super.mContext.getResources().getDimensionPixelOffset(R$dimen.default_gear_space);
        final TouchAnimator.Builder builder = new TouchAnimator.Builder();
        final View mSettingsContainer = this.mSettingsContainer;
        final boolean layoutRtl = this.isLayoutRtl();
        dimensionPixelOffset = n2 - dimensionPixelOffset;
        if (!layoutRtl) {
            dimensionPixelOffset = -dimensionPixelOffset;
        }
        builder.addFloat(mSettingsContainer, "translationX", (float)dimensionPixelOffset, 0.0f);
        builder.addFloat(this.mSettingsButton, "rotation", -120.0f, 0.0f);
        this.mSettingsCogAnimator = builder.build();
        this.setExpansion(this.mExpansionAmount);
    }
    
    private void updateClickabilities() {
        final MultiUserSwitch mMultiUserSwitch = this.mMultiUserSwitch;
        final int visibility = mMultiUserSwitch.getVisibility();
        final boolean b = true;
        mMultiUserSwitch.setClickable(visibility == 0);
        final View mEdit = this.mEdit;
        mEdit.setClickable(mEdit.getVisibility() == 0);
        final SettingsButton mSettingsButton = this.mSettingsButton;
        mSettingsButton.setClickable(mSettingsButton.getVisibility() == 0 && b);
    }
    
    private void updateFooterAnimator() {
        this.mFooterAnimator = this.createFooterAnimator();
    }
    
    private void updateListeners() {
        if (this.mListening) {
            this.mUserInfoController.addCallback((UserInfoController.OnUserInfoChangedListener)this);
        }
        else {
            this.mUserInfoController.removeCallback((UserInfoController.OnUserInfoChangedListener)this);
        }
    }
    
    private void updateResources() {
        this.updateFooterAnimator();
    }
    
    private void updateVisibilities() {
        final View mSettingsContainer = this.mSettingsContainer;
        final boolean mQsDisabled = this.mQsDisabled;
        final int n = 0;
        int visibility;
        if (mQsDisabled) {
            visibility = 8;
        }
        else {
            visibility = 0;
        }
        mSettingsContainer.setVisibility(visibility);
        final View viewById = this.mSettingsContainer.findViewById(R$id.tuner_icon);
        int visibility2;
        if (TunerService.isTunerEnabled(super.mContext)) {
            visibility2 = 0;
        }
        else {
            visibility2 = 4;
        }
        viewById.setVisibility(visibility2);
        final boolean deviceInDemoMode = UserManager.isDeviceInDemoMode(super.mContext);
        final MultiUserSwitch mMultiUserSwitch = this.mMultiUserSwitch;
        int visibility3;
        if (this.showUserSwitcher()) {
            visibility3 = 0;
        }
        else {
            visibility3 = 4;
        }
        mMultiUserSwitch.setVisibility(visibility3);
        final View mEditContainer = this.mEditContainer;
        int visibility4;
        if (!deviceInDemoMode && this.mExpanded) {
            visibility4 = 0;
        }
        else {
            visibility4 = 4;
        }
        mEditContainer.setVisibility(visibility4);
        final SettingsButton mSettingsButton = this.mSettingsButton;
        int visibility5 = 0;
        Label_0159: {
            if (!deviceInDemoMode) {
                visibility5 = n;
                if (this.mExpanded) {
                    break Label_0159;
                }
            }
            visibility5 = 4;
        }
        mSettingsButton.setVisibility(visibility5);
    }
    
    public void disable(final int n, final int n2, final boolean b) {
        boolean mQsDisabled = true;
        if ((n2 & 0x1) == 0x0) {
            mQsDisabled = false;
        }
        if (mQsDisabled == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = mQsDisabled;
        this.updateEverything();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        super.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("development_settings_enabled"), false, this.mDeveloperSettingsObserver, -1);
    }
    
    public void onClick(final View view) {
        if (!this.mExpanded) {
            return;
        }
        if (view == this.mSettingsButton) {
            if (!this.mDeviceProvisionedController.isCurrentUserSetup()) {
                this.mActivityStarter.postQSRunnableDismissingKeyguard((Runnable)_$$Lambda$QSFooterImpl$ORlOcuwnOcEc1bdhJcTagEFJfI4.INSTANCE);
                return;
            }
            final Context mContext = super.mContext;
            int n;
            if (this.mExpanded) {
                n = 406;
            }
            else {
                n = 490;
            }
            MetricsLogger.action(mContext, n);
            if (this.mSettingsButton.isTunerClick()) {
                this.mActivityStarter.postQSRunnableDismissingKeyguard(new _$$Lambda$QSFooterImpl$QqFCwKmpQEaqoIsbaA3_odDeJWo(this));
            }
            else {
                this.startSettingsActivity();
            }
        }
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateResources();
    }
    
    public void onDetachedFromWindow() {
        this.setListening(false);
        super.mContext.getContentResolver().unregisterContentObserver(this.mDeveloperSettingsObserver);
        super.onDetachedFromWindow();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        (this.mEdit = this.findViewById(16908291)).setOnClickListener((View$OnClickListener)new _$$Lambda$QSFooterImpl$3QBg0cgvu2IRpUDq3RvpL257x8c(this));
        this.mPageIndicator = (PageIndicator)this.findViewById(R$id.footer_page_indicator);
        this.mSettingsButton = (SettingsButton)this.findViewById(R$id.settings_button);
        this.mSettingsContainer = this.findViewById(R$id.settings_button_container);
        this.mSettingsButton.setOnClickListener((View$OnClickListener)this);
        final MultiUserSwitch mMultiUserSwitch = (MultiUserSwitch)this.findViewById(R$id.multi_user_switch);
        this.mMultiUserSwitch = mMultiUserSwitch;
        this.mMultiUserAvatar = (ImageView)mMultiUserSwitch.findViewById(R$id.multi_user_avatar);
        this.mDragHandle = this.findViewById(R$id.qs_drag_handle_view);
        this.mActionsContainer = this.findViewById(R$id.qs_footer_actions_container);
        this.mEditContainer = this.findViewById(R$id.qs_footer_actions_edit_container);
        ((RippleDrawable)this.mSettingsButton.getBackground()).setForceSoftware(true);
        this.updateResources();
        this.addOnLayoutChangeListener((View$OnLayoutChangeListener)new _$$Lambda$QSFooterImpl$GSAG9gEF755NpvH4khVvAa75uPs(this));
        this.setImportantForAccessibility(1);
        this.updateEverything();
        this.setBuildText();
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_EXPAND);
    }
    
    public void onRtlPropertiesChanged(final int n) {
        super.onRtlPropertiesChanged(n);
        this.updateResources();
    }
    
    public void onUserInfoChanged(final String s, final Drawable drawable, final String s2) {
        Drawable mutate = drawable;
        if (drawable != null) {
            mutate = drawable;
            if (UserManager.get(super.mContext).isGuestUser(KeyguardUpdateMonitor.getCurrentUser())) {
                mutate = drawable;
                if (!(drawable instanceof UserIconDrawable)) {
                    mutate = drawable.getConstantState().newDrawable(super.mContext.getResources()).mutate();
                    mutate.setColorFilter(Utils.getColorAttrDefaultColor(super.mContext, 16842800), PorterDuff$Mode.SRC_IN);
                }
            }
        }
        this.mMultiUserAvatar.setImageDrawable(mutate);
    }
    
    public boolean performAccessibilityAction(final int n, final Bundle bundle) {
        if (n == 262144) {
            final View$OnClickListener mExpandClickListener = this.mExpandClickListener;
            if (mExpandClickListener != null) {
                mExpandClickListener.onClick((View)null);
                return true;
            }
        }
        return super.performAccessibilityAction(n, bundle);
    }
    
    public void setExpandClickListener(final View$OnClickListener mExpandClickListener) {
        this.mExpandClickListener = mExpandClickListener;
    }
    
    public void setExpanded(final boolean mExpanded) {
        if (this.mExpanded == mExpanded) {
            return;
        }
        this.mExpanded = mExpanded;
        this.updateEverything();
    }
    
    public void setExpansion(final float position) {
        this.mExpansionAmount = position;
        final TouchAnimator mSettingsCogAnimator = this.mSettingsCogAnimator;
        if (mSettingsCogAnimator != null) {
            mSettingsCogAnimator.setPosition(position);
        }
        final TouchAnimator mFooterAnimator = this.mFooterAnimator;
        if (mFooterAnimator != null) {
            mFooterAnimator.setPosition(position);
        }
    }
    
    public void setKeyguardShowing(final boolean b) {
        this.setExpansion(this.mExpansionAmount);
    }
    
    public void setListening(final boolean mListening) {
        if (mListening == this.mListening) {
            return;
        }
        this.mListening = mListening;
        this.updateListeners();
    }
    
    public void setQSPanel(final QSPanel qsPanel) {
        this.mQsPanel = qsPanel;
        if (qsPanel != null) {
            this.mMultiUserSwitch.setQsPanel(qsPanel);
            this.mQsPanel.setFooterPageIndicator(this.mPageIndicator);
        }
    }
    
    public void updateEverything() {
        this.post((Runnable)new _$$Lambda$QSFooterImpl$FK1In3z_Y3ppRrcllMggnruYa_s(this));
    }
}
