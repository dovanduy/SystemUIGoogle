// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.R$drawable;
import com.android.systemui.plugins.IntentButtonProvider.IntentButton;
import android.os.Message;
import android.content.pm.ResolveInfo;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.R$id;
import com.android.internal.widget.LockPatternUtils;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import com.android.systemui.R$dimen;
import android.content.res.Configuration;
import android.os.Handler;
import android.content.IntentFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.android.systemui.tuner.LockscreenFragment;
import android.view.WindowInsets;
import com.android.internal.annotations.VisibleForTesting;
import android.os.RemoteException;
import android.app.ProfilerInfo;
import android.app.IApplicationThread;
import android.app.ActivityTaskManager;
import android.app.ActivityOptions;
import com.android.systemui.doze.util.BurnInHelperKt;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.os.UserHandle;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.tuner.TunerService;
import android.text.TextUtils;
import android.os.AsyncTask;
import android.telecom.TelecomManager;
import android.content.pm.PackageManager;
import com.android.systemui.Dependency;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.R$bool;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import com.android.systemui.R$string;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.IBinder;
import android.content.ComponentName;
import android.util.AttributeSet;
import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.os.Messenger;
import android.content.ServiceConnection;
import com.android.systemui.statusbar.policy.PreviewInflater;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.plugins.IntentButtonProvider;
import android.graphics.drawable.Drawable;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.content.BroadcastReceiver;
import android.view.View;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.ActivityIntentHelper;
import android.view.View$AccessibilityDelegate;
import android.content.Intent;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.view.View$OnClickListener;
import android.widget.FrameLayout;

public class KeyguardBottomAreaView extends FrameLayout implements View$OnClickListener, Callback, AccessibilityStateChangedCallback
{
    public static final Intent INSECURE_CAMERA_INTENT;
    private static final Intent PHONE_INTENT;
    private static final Intent SECURE_CAMERA_INTENT;
    private AccessibilityController mAccessibilityController;
    private View$AccessibilityDelegate mAccessibilityDelegate;
    private ActivityIntentHelper mActivityIntentHelper;
    private ActivityStarter mActivityStarter;
    private KeyguardAffordanceHelper mAffordanceHelper;
    private int mBurnInXOffset;
    private int mBurnInYOffset;
    private View mCameraPreview;
    private float mDarkAmount;
    private final BroadcastReceiver mDevicePolicyReceiver;
    private boolean mDozing;
    private ViewGroup mIndicationArea;
    private int mIndicationBottomMargin;
    private TextView mIndicationText;
    private KeyguardStateController mKeyguardStateController;
    private KeyguardAffordanceView mLeftAffordanceView;
    private Drawable mLeftAssistIcon;
    private IntentButton mLeftButton;
    private String mLeftButtonStr;
    private ExtensionController.Extension<IntentButton> mLeftExtension;
    private boolean mLeftIsVoiceAssist;
    private View mLeftPreview;
    private ViewGroup mOverlayContainer;
    private ViewGroup mPreviewContainer;
    private PreviewInflater mPreviewInflater;
    private boolean mPrewarmBound;
    private final ServiceConnection mPrewarmConnection;
    private Messenger mPrewarmMessenger;
    private KeyguardAffordanceView mRightAffordanceView;
    private IntentButton mRightButton;
    private String mRightButtonStr;
    private ExtensionController.Extension<IntentButton> mRightExtension;
    private final boolean mShowCameraAffordance;
    private final boolean mShowLeftAffordance;
    private StatusBar mStatusBar;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private boolean mUserSetupComplete;
    
    static {
        SECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE").addFlags(8388608);
        INSECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA");
        PHONE_INTENT = new Intent("android.intent.action.DIAL");
    }
    
    public KeyguardBottomAreaView(final Context context) {
        this(context, null);
    }
    
    public KeyguardBottomAreaView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyguardBottomAreaView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public KeyguardBottomAreaView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mPrewarmConnection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                KeyguardBottomAreaView.this.mPrewarmMessenger = new Messenger(binder);
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                KeyguardBottomAreaView.this.mPrewarmMessenger = null;
            }
        };
        this.mRightButton = new DefaultRightButton();
        this.mLeftButton = new DefaultLeftButton();
        this.mAccessibilityDelegate = new View$AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                String s;
                if (view == KeyguardBottomAreaView.this.mRightAffordanceView) {
                    s = KeyguardBottomAreaView.this.getResources().getString(R$string.camera_label);
                }
                else if (view == KeyguardBottomAreaView.this.mLeftAffordanceView) {
                    if (KeyguardBottomAreaView.this.mLeftIsVoiceAssist) {
                        s = KeyguardBottomAreaView.this.getResources().getString(R$string.voice_assist_label);
                    }
                    else {
                        s = KeyguardBottomAreaView.this.getResources().getString(R$string.phone_label);
                    }
                }
                else {
                    s = null;
                }
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(16, (CharSequence)s));
            }
            
            public boolean performAccessibilityAction(final View view, final int n, final Bundle bundle) {
                if (n == 16) {
                    if (view == KeyguardBottomAreaView.this.mRightAffordanceView) {
                        KeyguardBottomAreaView.this.launchCamera("lockscreen_affordance");
                        return true;
                    }
                    if (view == KeyguardBottomAreaView.this.mLeftAffordanceView) {
                        KeyguardBottomAreaView.this.launchLeftAffordance();
                        return true;
                    }
                }
                return super.performAccessibilityAction(view, n, bundle);
            }
        };
        this.mDevicePolicyReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                KeyguardBottomAreaView.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        KeyguardBottomAreaView.this.updateCameraVisibility();
                    }
                });
            }
        };
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitchComplete(final int n) {
                KeyguardBottomAreaView.this.updateCameraVisibility();
            }
            
            @Override
            public void onUserUnlocked() {
                KeyguardBottomAreaView.this.inflateCameraPreview();
                KeyguardBottomAreaView.this.updateCameraVisibility();
                KeyguardBottomAreaView.this.updateLeftAffordance();
            }
        };
        this.mShowLeftAffordance = this.getResources().getBoolean(R$bool.config_keyguardShowLeftAffordance);
        this.mShowCameraAffordance = this.getResources().getBoolean(R$bool.config_keyguardShowCameraAffordance);
    }
    
    private boolean canLaunchVoiceAssist() {
        return Dependency.get(AssistManager.class).canVoiceAssistBeLaunchedFromKeyguard();
    }
    
    private Intent getCameraIntent() {
        return this.mRightButton.getIntent();
    }
    
    private void inflateCameraPreview() {
        final View mCameraPreview = this.mCameraPreview;
        final int n = 0;
        boolean b = false;
        Label_0033: {
            if (mCameraPreview != null) {
                this.mPreviewContainer.removeView(mCameraPreview);
                if (mCameraPreview.getVisibility() == 0) {
                    b = true;
                    break Label_0033;
                }
            }
            b = false;
        }
        final View inflatePreview = this.mPreviewInflater.inflatePreview(this.getCameraIntent());
        this.mCameraPreview = inflatePreview;
        if (inflatePreview != null) {
            this.mPreviewContainer.addView(inflatePreview);
            final View mCameraPreview2 = this.mCameraPreview;
            int visibility;
            if (b) {
                visibility = n;
            }
            else {
                visibility = 4;
            }
            mCameraPreview2.setVisibility(visibility);
        }
        final KeyguardAffordanceHelper mAffordanceHelper = this.mAffordanceHelper;
        if (mAffordanceHelper != null) {
            mAffordanceHelper.updatePreviews();
        }
    }
    
    private void initAccessibility() {
        this.mLeftAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
        this.mRightAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
    }
    
    private boolean isPhoneVisible() {
        final PackageManager packageManager = super.mContext.getPackageManager();
        final boolean hasSystemFeature = packageManager.hasSystemFeature("android.hardware.telephony");
        boolean b = false;
        if (hasSystemFeature) {
            b = b;
            if (packageManager.resolveActivity(KeyguardBottomAreaView.PHONE_INTENT, 0) != null) {
                b = true;
            }
        }
        return b;
    }
    
    private static boolean isSuccessfulLaunch(final int n) {
        return n == 0 || n == 3 || n == 2;
    }
    
    private void launchPhone() {
        final TelecomManager from = TelecomManager.from(super.mContext);
        if (from.isInCall()) {
            AsyncTask.execute((Runnable)new Runnable(this) {
                @Override
                public void run() {
                    from.showInCallScreen(false);
                }
            });
        }
        else {
            final boolean empty = TextUtils.isEmpty((CharSequence)this.mLeftButtonStr);
            boolean b = true;
            if (empty || Dependency.get(TunerService.class).getValue("sysui_keyguard_left_unlock", 1) == 0) {
                b = false;
            }
            this.mActivityStarter.startActivity(this.mLeftButton.getIntent(), b);
        }
    }
    
    private void setLeftButton(final IntentButton mLeftButton) {
        this.mLeftButton = mLeftButton;
        if (!(mLeftButton instanceof DefaultLeftButton)) {
            this.mLeftIsVoiceAssist = false;
        }
        this.updateLeftAffordance();
    }
    
    private void setRightButton(final IntentButton mRightButton) {
        this.mRightButton = mRightButton;
        this.updateRightAffordanceIcon();
        this.updateCameraVisibility();
        this.inflateCameraPreview();
    }
    
    private void startFinishDozeAnimationElement(final View view, final long startDelay) {
        view.setAlpha(0.0f);
        view.setTranslationY((float)(view.getHeight() / 2));
        view.animate().alpha(1.0f).translationY(0.0f).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(startDelay).setDuration(250L);
    }
    
    private void updateCameraVisibility() {
        final KeyguardAffordanceView mRightAffordanceView = this.mRightAffordanceView;
        if (mRightAffordanceView == null) {
            return;
        }
        int visibility;
        if (!this.mDozing && this.mShowCameraAffordance && this.mRightButton.getIcon().isVisible) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mRightAffordanceView.setVisibility(visibility);
    }
    
    private void updateLeftAffordanceIcon() {
        final boolean mShowLeftAffordance = this.mShowLeftAffordance;
        int visibility = 8;
        if (mShowLeftAffordance && !this.mDozing) {
            final IntentButtonProvider.IntentButton.IconState icon = this.mLeftButton.getIcon();
            final KeyguardAffordanceView mLeftAffordanceView = this.mLeftAffordanceView;
            if (icon.isVisible) {
                visibility = 0;
            }
            mLeftAffordanceView.setVisibility(visibility);
            if (icon.drawable != this.mLeftAffordanceView.getDrawable() || icon.tint != this.mLeftAffordanceView.shouldTint()) {
                this.mLeftAffordanceView.setImageDrawable(icon.drawable, icon.tint);
            }
            this.mLeftAffordanceView.setContentDescription(icon.contentDescription);
            return;
        }
        this.mLeftAffordanceView.setVisibility(8);
    }
    
    private void updateLeftPreview() {
        final View mLeftPreview = this.mLeftPreview;
        if (mLeftPreview != null) {
            this.mPreviewContainer.removeView(mLeftPreview);
        }
        if (this.mLeftIsVoiceAssist) {
            if (Dependency.get(AssistManager.class).getVoiceInteractorComponentName() != null) {
                this.mLeftPreview = this.mPreviewInflater.inflatePreviewFromService(Dependency.get(AssistManager.class).getVoiceInteractorComponentName());
            }
        }
        else {
            this.mLeftPreview = this.mPreviewInflater.inflatePreview(this.mLeftButton.getIntent());
        }
        final View mLeftPreview2 = this.mLeftPreview;
        if (mLeftPreview2 != null) {
            this.mPreviewContainer.addView(mLeftPreview2);
            this.mLeftPreview.setVisibility(4);
        }
        final KeyguardAffordanceHelper mAffordanceHelper = this.mAffordanceHelper;
        if (mAffordanceHelper != null) {
            mAffordanceHelper.updatePreviews();
        }
    }
    
    private void updateRightAffordanceIcon() {
        final IntentButtonProvider.IntentButton.IconState icon = this.mRightButton.getIcon();
        final KeyguardAffordanceView mRightAffordanceView = this.mRightAffordanceView;
        int visibility;
        if (!this.mDozing && icon.isVisible) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mRightAffordanceView.setVisibility(visibility);
        if (icon.drawable != this.mRightAffordanceView.getDrawable() || icon.tint != this.mRightAffordanceView.shouldTint()) {
            this.mRightAffordanceView.setImageDrawable(icon.drawable, icon.tint);
        }
        this.mRightAffordanceView.setContentDescription(icon.contentDescription);
    }
    
    public void bindCameraPrewarmService() {
        final ActivityInfo targetActivityInfo = this.mActivityIntentHelper.getTargetActivityInfo(this.getCameraIntent(), KeyguardUpdateMonitor.getCurrentUser(), true);
        if (targetActivityInfo != null) {
            final Bundle metaData = targetActivityInfo.metaData;
            if (metaData != null) {
                final String string = metaData.getString("android.media.still_image_camera_preview_service");
                if (string != null) {
                    final Intent intent = new Intent();
                    intent.setClassName(targetActivityInfo.packageName, string);
                    intent.setAction("android.service.media.CameraPrewarmService.ACTION_PREWARM");
                    try {
                        if (this.getContext().bindServiceAsUser(intent, this.mPrewarmConnection, 67108865, new UserHandle(-2))) {
                            this.mPrewarmBound = true;
                        }
                    }
                    catch (SecurityException ex) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Unable to bind to prewarm service package=");
                        sb.append(targetActivityInfo.packageName);
                        sb.append(" class=");
                        sb.append(string);
                        Log.w("StatusBar/KeyguardBottomAreaView", sb.toString(), (Throwable)ex);
                    }
                }
            }
        }
    }
    
    public void dozeTimeTick() {
        this.mIndicationArea.setTranslationY((BurnInHelperKt.getBurnInOffset(this.mBurnInYOffset * 2, false) - this.mBurnInYOffset) * this.mDarkAmount);
    }
    
    public View getIndicationArea() {
        return (View)this.mIndicationArea;
    }
    
    public View getLeftPreview() {
        return this.mLeftPreview;
    }
    
    public KeyguardAffordanceView getLeftView() {
        return this.mLeftAffordanceView;
    }
    
    public View getRightPreview() {
        return this.mCameraPreview;
    }
    
    public KeyguardAffordanceView getRightView() {
        return this.mRightAffordanceView;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public void initFrom(final KeyguardBottomAreaView keyguardBottomAreaView) {
        this.setStatusBar(keyguardBottomAreaView.mStatusBar);
    }
    
    public boolean isLeftVoiceAssist() {
        return this.mLeftIsVoiceAssist;
    }
    
    public void launchCamera(final String s) {
        final Intent cameraIntent = this.getCameraIntent();
        cameraIntent.putExtra("com.android.systemui.camera_launch_source", s);
        final boolean wouldLaunchResolverActivity = this.mActivityIntentHelper.wouldLaunchResolverActivity(cameraIntent, KeyguardUpdateMonitor.getCurrentUser());
        if (cameraIntent == KeyguardBottomAreaView.SECURE_CAMERA_INTENT && !wouldLaunchResolverActivity) {
            AsyncTask.execute((Runnable)new Runnable() {
                @Override
                public void run() {
                    final ActivityOptions basic = ActivityOptions.makeBasic();
                    basic.setDisallowEnterPictureInPictureWhileLaunching(true);
                    basic.setRotationAnimationHint(3);
                    int startActivityAsUser;
                    try {
                        startActivityAsUser = ActivityTaskManager.getService().startActivityAsUser((IApplicationThread)null, KeyguardBottomAreaView.this.getContext().getBasePackageName(), KeyguardBottomAreaView.this.getContext().getAttributionTag(), cameraIntent, cameraIntent.resolveTypeIfNeeded(KeyguardBottomAreaView.this.getContext().getContentResolver()), (IBinder)null, (String)null, 0, 268435456, (ProfilerInfo)null, basic.toBundle(), UserHandle.CURRENT.getIdentifier());
                    }
                    catch (RemoteException ex) {
                        Log.w("StatusBar/KeyguardBottomAreaView", "Unable to start camera activity", (Throwable)ex);
                        startActivityAsUser = -96;
                    }
                    KeyguardBottomAreaView.this.post((Runnable)new Runnable() {
                        final /* synthetic */ boolean val$launched = isSuccessfulLaunch(startActivityAsUser);
                        
                        @Override
                        public void run() {
                            KeyguardBottomAreaView.this.unbindCameraPrewarmService(this.val$launched);
                        }
                    });
                }
            });
        }
        else {
            this.mActivityStarter.startActivity(cameraIntent, false, (ActivityStarter.Callback)new ActivityStarter.Callback() {
                @Override
                public void onActivityStarted(final int n) {
                    KeyguardBottomAreaView.this.unbindCameraPrewarmService(isSuccessfulLaunch(n));
                }
            });
        }
    }
    
    public void launchLeftAffordance() {
        if (this.mLeftIsVoiceAssist) {
            this.launchVoiceAssist();
        }
        else {
            this.launchPhone();
        }
    }
    
    @VisibleForTesting
    void launchVoiceAssist() {
        final Runnable runnable = new Runnable(this) {
            @Override
            public void run() {
                Dependency.get(AssistManager.class).launchVoiceAssistFromKeyguard();
            }
        };
        if (!this.mKeyguardStateController.canDismissLockScreen()) {
            AsyncTask.execute((Runnable)runnable);
        }
        else {
            this.mStatusBar.executeRunnableDismissingKeyguard(runnable, null, !TextUtils.isEmpty((CharSequence)this.mRightButtonStr) && Dependency.get(TunerService.class).getValue("sysui_keyguard_right_unlock", 1) != 0, false, true);
        }
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        int safeInsetBottom;
        if (windowInsets.getDisplayCutout() != null) {
            safeInsetBottom = windowInsets.getDisplayCutout().getSafeInsetBottom();
        }
        else {
            safeInsetBottom = 0;
        }
        if (this.isPaddingRelative()) {
            this.setPaddingRelative(this.getPaddingStart(), this.getPaddingTop(), this.getPaddingEnd(), safeInsetBottom);
        }
        else {
            this.setPadding(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), safeInsetBottom);
        }
        return windowInsets;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAccessibilityController.addStateChangedCallback((AccessibilityController.AccessibilityStateChangedCallback)this);
        final ExtensionController.ExtensionBuilder<IntentButton> extension = Dependency.get(ExtensionController.class).newExtension(IntentButton.class);
        extension.withPlugin(IntentButtonProvider.class, "com.android.systemui.action.PLUGIN_LOCKSCREEN_RIGHT_BUTTON", (ExtensionController.PluginConverter<IntentButton, IntentButtonProvider>)_$$Lambda$KeyguardBottomAreaView$g4KaNPI9kzVsHrOlMY_mA_f9J2Y.INSTANCE);
        extension.withTunerFactory(new LockscreenFragment.LockButtonFactory(super.mContext, "sysui_keyguard_right"));
        extension.withDefault(new _$$Lambda$KeyguardBottomAreaView$41MKD52m3LHIf9RRtKFf6LfUif0(this));
        extension.withCallback(new _$$Lambda$KeyguardBottomAreaView$Z_R5g5wpXUcfPYLHCfZHekG4xK0(this));
        this.mRightExtension = extension.build();
        final ExtensionController.ExtensionBuilder<IntentButton> extension2 = Dependency.get(ExtensionController.class).newExtension(IntentButton.class);
        extension2.withPlugin(IntentButtonProvider.class, "com.android.systemui.action.PLUGIN_LOCKSCREEN_LEFT_BUTTON", (ExtensionController.PluginConverter<IntentButton, IntentButtonProvider>)_$$Lambda$KeyguardBottomAreaView$Eh9_ou4HbbT4H4ZFilpDDtanY4k.INSTANCE);
        extension2.withTunerFactory(new LockscreenFragment.LockButtonFactory(super.mContext, "sysui_keyguard_left"));
        extension2.withDefault(new _$$Lambda$KeyguardBottomAreaView$W_hTEBW5YZVW2MsKtz0LzBCynHY(this));
        extension2.withCallback(new _$$Lambda$KeyguardBottomAreaView$owXxFBBnubMOAUdfyf5a48bf_Zo(this));
        this.mLeftExtension = extension2.build();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        this.getContext().registerReceiverAsUser(this.mDevicePolicyReceiver, UserHandle.ALL, intentFilter, (String)null, (Handler)null);
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mUpdateMonitorCallback);
        this.mKeyguardStateController.addCallback((KeyguardStateController.Callback)this);
    }
    
    public void onClick(final View view) {
        if (view == this.mRightAffordanceView) {
            this.launchCamera("lockscreen_affordance");
        }
        else if (view == this.mLeftAffordanceView) {
            this.launchLeftAffordance();
        }
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mIndicationBottomMargin = this.getResources().getDimensionPixelSize(R$dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = this.getResources().getDimensionPixelSize(R$dimen.default_burn_in_prevention_offset);
        final ViewGroup$MarginLayoutParams layoutParams = (ViewGroup$MarginLayoutParams)this.mIndicationArea.getLayoutParams();
        final int bottomMargin = layoutParams.bottomMargin;
        final int mIndicationBottomMargin = this.mIndicationBottomMargin;
        if (bottomMargin != mIndicationBottomMargin) {
            layoutParams.bottomMargin = mIndicationBottomMargin;
            this.mIndicationArea.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
        this.mIndicationText.setTextSize(0, (float)this.getResources().getDimensionPixelSize(17105499));
        final ViewGroup$LayoutParams layoutParams2 = this.mRightAffordanceView.getLayoutParams();
        layoutParams2.width = this.getResources().getDimensionPixelSize(R$dimen.keyguard_affordance_width);
        layoutParams2.height = this.getResources().getDimensionPixelSize(R$dimen.keyguard_affordance_height);
        this.mRightAffordanceView.setLayoutParams(layoutParams2);
        this.updateRightAffordanceIcon();
        final ViewGroup$LayoutParams layoutParams3 = this.mLeftAffordanceView.getLayoutParams();
        layoutParams3.width = this.getResources().getDimensionPixelSize(R$dimen.keyguard_affordance_width);
        layoutParams3.height = this.getResources().getDimensionPixelSize(R$dimen.keyguard_affordance_height);
        this.mLeftAffordanceView.setLayoutParams(layoutParams3);
        this.updateLeftAffordanceIcon();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardStateController.removeCallback((KeyguardStateController.Callback)this);
        this.mAccessibilityController.removeStateChangedCallback((AccessibilityController.AccessibilityStateChangedCallback)this);
        this.mRightExtension.destroy();
        this.mLeftExtension.destroy();
        this.getContext().unregisterReceiver(this.mDevicePolicyReceiver);
        Dependency.get(KeyguardUpdateMonitor.class).removeCallback(this.mUpdateMonitorCallback);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPreviewInflater = new PreviewInflater(super.mContext, new LockPatternUtils(super.mContext), new ActivityIntentHelper(super.mContext));
        this.mPreviewContainer = (ViewGroup)this.findViewById(R$id.preview_container);
        this.mOverlayContainer = (ViewGroup)this.findViewById(R$id.overlay_container);
        this.mRightAffordanceView = (KeyguardAffordanceView)this.findViewById(R$id.camera_button);
        this.mLeftAffordanceView = (KeyguardAffordanceView)this.findViewById(R$id.left_button);
        this.mIndicationArea = (ViewGroup)this.findViewById(R$id.keyguard_indication_area);
        this.mIndicationText = (TextView)this.findViewById(R$id.keyguard_indication_text);
        this.mIndicationBottomMargin = this.getResources().getDimensionPixelSize(R$dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = this.getResources().getDimensionPixelSize(R$dimen.default_burn_in_prevention_offset);
        this.updateCameraVisibility();
        (this.mKeyguardStateController = Dependency.get(KeyguardStateController.class)).addCallback((KeyguardStateController.Callback)this);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.inflateCameraPreview();
        this.mRightAffordanceView.setOnClickListener((View$OnClickListener)this);
        this.mLeftAffordanceView.setOnClickListener((View$OnClickListener)this);
        this.initAccessibility();
        this.mActivityStarter = Dependency.get(ActivityStarter.class);
        final FlashlightController flashlightController = Dependency.get(FlashlightController.class);
        this.mAccessibilityController = Dependency.get(AccessibilityController.class);
        this.mActivityIntentHelper = new ActivityIntentHelper(this.getContext());
        this.updateLeftAffordance();
    }
    
    public void onStateChanged(final boolean b, final boolean b2) {
        this.mRightAffordanceView.setClickable(b2);
        this.mLeftAffordanceView.setClickable(b2);
        this.mRightAffordanceView.setFocusable(b);
        this.mLeftAffordanceView.setFocusable(b);
    }
    
    public void onUnlockedChanged() {
        this.updateCameraVisibility();
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (view == this && n == 0) {
            this.updateCameraVisibility();
        }
    }
    
    public ResolveInfo resolveCameraIntent() {
        return super.mContext.getPackageManager().resolveActivityAsUser(this.getCameraIntent(), 65536, KeyguardUpdateMonitor.getCurrentUser());
    }
    
    public void setAffordanceAlpha(final float alpha) {
        this.mLeftAffordanceView.setAlpha(alpha);
        this.mRightAffordanceView.setAlpha(alpha);
        this.mIndicationArea.setAlpha(alpha);
    }
    
    public void setAffordanceHelper(final KeyguardAffordanceHelper mAffordanceHelper) {
        this.mAffordanceHelper = mAffordanceHelper;
    }
    
    public void setAntiBurnInOffsetX(final int mBurnInXOffset) {
        if (this.mBurnInXOffset == mBurnInXOffset) {
            return;
        }
        this.mBurnInXOffset = mBurnInXOffset;
        this.mIndicationArea.setTranslationX((float)mBurnInXOffset);
    }
    
    public void setDarkAmount(final float mDarkAmount) {
        if (mDarkAmount == this.mDarkAmount) {
            return;
        }
        this.mDarkAmount = mDarkAmount;
        this.dozeTimeTick();
    }
    
    public void setDozing(final boolean mDozing, final boolean b) {
        this.mDozing = mDozing;
        this.updateCameraVisibility();
        this.updateLeftAffordanceIcon();
        if (mDozing) {
            this.mOverlayContainer.setVisibility(4);
        }
        else {
            this.mOverlayContainer.setVisibility(0);
            if (b) {
                this.startFinishDozeAnimation();
            }
        }
    }
    
    public void setStatusBar(final StatusBar mStatusBar) {
        this.mStatusBar = mStatusBar;
        this.updateCameraVisibility();
    }
    
    public void setUserSetupComplete(final boolean mUserSetupComplete) {
        this.mUserSetupComplete = mUserSetupComplete;
        this.updateCameraVisibility();
        this.updateLeftAffordanceIcon();
    }
    
    public void startFinishDozeAnimation() {
        final int visibility = this.mLeftAffordanceView.getVisibility();
        long n = 0L;
        if (visibility == 0) {
            this.startFinishDozeAnimationElement((View)this.mLeftAffordanceView, 0L);
            n = 48L;
        }
        if (this.mRightAffordanceView.getVisibility() == 0) {
            this.startFinishDozeAnimationElement((View)this.mRightAffordanceView, n);
        }
    }
    
    public void unbindCameraPrewarmService(final boolean b) {
        if (this.mPrewarmBound) {
            final Messenger mPrewarmMessenger = this.mPrewarmMessenger;
            if (mPrewarmMessenger != null && b) {
                try {
                    mPrewarmMessenger.send(Message.obtain((Handler)null, 1));
                }
                catch (RemoteException ex) {
                    Log.w("StatusBar/KeyguardBottomAreaView", "Error sending camera fired message", (Throwable)ex);
                }
            }
            super.mContext.unbindService(this.mPrewarmConnection);
            this.mPrewarmBound = false;
        }
    }
    
    public void updateLeftAffordance() {
        this.updateLeftAffordanceIcon();
        this.updateLeftPreview();
    }
    
    private class DefaultLeftButton implements IntentButton
    {
        private IconState mIconState;
        
        private DefaultLeftButton() {
            this.mIconState = new IconState();
        }
        
        @Override
        public IconState getIcon() {
            final KeyguardBottomAreaView this$0 = KeyguardBottomAreaView.this;
            this$0.mLeftIsVoiceAssist = this$0.canLaunchVoiceAssist();
            final boolean access$500 = KeyguardBottomAreaView.this.mLeftIsVoiceAssist;
            final boolean b = true;
            boolean isVisible = true;
            if (access$500) {
                final IconState mIconState = this.mIconState;
                if (!KeyguardBottomAreaView.this.mUserSetupComplete || !KeyguardBottomAreaView.this.mShowLeftAffordance) {
                    isVisible = false;
                }
                mIconState.isVisible = isVisible;
                if (KeyguardBottomAreaView.this.mLeftAssistIcon == null) {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(R$drawable.ic_mic_26dp);
                }
                else {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mLeftAssistIcon;
                }
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R$string.accessibility_voice_assist_button);
            }
            else {
                this.mIconState.isVisible = (KeyguardBottomAreaView.this.mUserSetupComplete && KeyguardBottomAreaView.this.mShowLeftAffordance && KeyguardBottomAreaView.this.isPhoneVisible() && b);
                this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(17302783);
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R$string.accessibility_phone_button);
            }
            return this.mIconState;
        }
        
        @Override
        public Intent getIntent() {
            return KeyguardBottomAreaView.PHONE_INTENT;
        }
    }
    
    private class DefaultRightButton implements IntentButton
    {
        private IconState mIconState;
        
        private DefaultRightButton() {
            this.mIconState = new IconState();
        }
        
        @Override
        public IconState getIcon() {
            final StatusBar access$1900 = KeyguardBottomAreaView.this.mStatusBar;
            boolean isVisible = true;
            final boolean b = access$1900 != null && !KeyguardBottomAreaView.this.mStatusBar.isCameraAllowedByAdmin();
            final IconState mIconState = this.mIconState;
            if (b || !KeyguardBottomAreaView.this.mShowCameraAffordance || !KeyguardBottomAreaView.this.mUserSetupComplete || KeyguardBottomAreaView.this.resolveCameraIntent() == null) {
                isVisible = false;
            }
            mIconState.isVisible = isVisible;
            this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(R$drawable.ic_camera_alt_24dp);
            this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R$string.accessibility_camera_button);
            return this.mIconState;
        }
        
        @Override
        public Intent getIntent() {
            final boolean canDismissLockScreen = KeyguardBottomAreaView.this.mKeyguardStateController.canDismissLockScreen();
            Intent intent;
            if (KeyguardBottomAreaView.this.mKeyguardStateController.isMethodSecure() && !canDismissLockScreen) {
                intent = KeyguardBottomAreaView.SECURE_CAMERA_INTENT;
            }
            else {
                intent = KeyguardBottomAreaView.INSECURE_CAMERA_INTENT;
            }
            return intent;
        }
    }
}
