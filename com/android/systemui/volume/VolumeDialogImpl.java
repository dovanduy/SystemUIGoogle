// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.os.Message;
import android.os.Looper;
import android.app.Dialog;
import android.view.accessibility.AccessibilityEvent;
import android.view.ViewPropertyAnimator;
import android.os.Debug;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.animation.DecelerateInterpolator;
import android.os.SystemClock;
import android.media.AudioManager;
import android.os.VibrationEffect;
import android.widget.Toast;
import com.android.settingslib.Utils;
import android.util.Log;
import com.android.systemui.plugins.ActivityStarter;
import android.content.Intent;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.animation.TimeInterpolator;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.view.View$OnClickListener;
import android.animation.ObjectAnimator;
import android.widget.SeekBar$OnSeekBarChangeListener;
import android.text.InputFilter$LengthFilter;
import android.text.InputFilter;
import android.widget.TextView;
import android.os.Handler;
import android.view.WindowManager$LayoutParams;
import com.android.systemui.R$drawable;
import android.media.AudioSystem;
import android.view.View$OnHoverListener;
import android.content.DialogInterface$OnShowListener;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$integer;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.content.res.Resources$NotFoundException;
import android.content.res.TypedArray;
import java.util.Iterator;
import android.util.Slog;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.View$AccessibilityDelegate;
import com.android.systemui.R$string;
import android.widget.SeekBar;
import com.android.systemui.Prefs;
import com.android.systemui.Dependency;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import java.util.ArrayList;
import android.widget.FrameLayout;
import android.view.Window;
import java.util.List;
import android.widget.ImageButton;
import android.view.ViewStub;
import android.view.View;
import android.app.KeyguardManager;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.plugins.VolumeDialogController;
import android.content.Context;
import android.app.ActivityManager;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.plugins.VolumeDialog;

public class VolumeDialogImpl implements VolumeDialog, ConfigurationListener
{
    private static final String TAG;
    private final Accessibility mAccessibility;
    private final AccessibilityManagerWrapper mAccessibilityMgr;
    private int mActiveStream;
    private final ActivityManager mActivityManager;
    private boolean mAutomute;
    private boolean mConfigChanged;
    private ConfigurableTexts mConfigurableTexts;
    private final Context mContext;
    private final VolumeDialogController mController;
    private final VolumeDialogController.Callbacks mControllerCallbackH;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private CustomDialog mDialog;
    private ViewGroup mDialogRowsView;
    private ViewGroup mDialogView;
    private final SparseBooleanArray mDynamic;
    private final H mHandler;
    private boolean mHasSeenODICaptionsTooltip;
    private boolean mHovering;
    private boolean mIsAnimatingDismiss;
    private final KeyguardManager mKeyguard;
    private CaptionsToggleImageButton mODICaptionsIcon;
    private View mODICaptionsTooltipView;
    private ViewStub mODICaptionsTooltipViewStub;
    private ViewGroup mODICaptionsView;
    private int mPrevActiveStream;
    private ViewGroup mRinger;
    private ImageButton mRingerIcon;
    private final List<VolumeRow> mRows;
    private SafetyWarningDialog mSafetyWarning;
    private final Object mSafetyWarningLock;
    private ImageButton mSettingsIcon;
    private View mSettingsView;
    private boolean mShowA11yStream;
    private boolean mShowActiveStreamOnly;
    private boolean mShowing;
    private boolean mSilentMode;
    private VolumeDialogController.State mState;
    private Window mWindow;
    private FrameLayout mZenIcon;
    
    static {
        TAG = Util.logTag(VolumeDialogImpl.class);
    }
    
    public VolumeDialogImpl(final Context context) {
        this.mHandler = new H();
        this.mRows = new ArrayList<VolumeRow>();
        this.mDynamic = new SparseBooleanArray();
        this.mSafetyWarningLock = new Object();
        this.mAccessibility = new Accessibility();
        this.mAutomute = true;
        this.mSilentMode = true;
        this.mHovering = false;
        this.mConfigChanged = false;
        this.mIsAnimatingDismiss = false;
        this.mODICaptionsTooltipView = null;
        this.mControllerCallbackH = new VolumeDialogController.Callbacks() {
            @Override
            public void onAccessibilityModeChanged(final Boolean b) {
                VolumeDialogImpl.this.mShowA11yStream = (b != null && b);
                final VolumeRow access$3300 = VolumeDialogImpl.this.getActiveRow();
                if (!VolumeDialogImpl.this.mShowA11yStream && 10 == access$3300.stream) {
                    VolumeDialogImpl.this.dismissH(7);
                }
                else {
                    VolumeDialogImpl.this.updateRowsH(access$3300);
                }
            }
            
            @Override
            public void onCaptionComponentStateChanged(final Boolean b, final Boolean b2) {
                VolumeDialogImpl.this.updateODICaptionsH(b, b2);
            }
            
            @Override
            public void onConfigurationChanged() {
                VolumeDialogImpl.this.mDialog.dismiss();
                VolumeDialogImpl.this.mConfigChanged = true;
            }
            
            @Override
            public void onDismissRequested(final int n) {
                VolumeDialogImpl.this.dismissH(n);
            }
            
            @Override
            public void onLayoutDirectionChanged(final int layoutDirection) {
                VolumeDialogImpl.this.mDialogView.setLayoutDirection(layoutDirection);
            }
            
            @Override
            public void onScreenOff() {
                VolumeDialogImpl.this.dismissH(4);
            }
            
            @Override
            public void onShowRequested(final int n) {
                VolumeDialogImpl.this.showH(n);
            }
            
            @Override
            public void onShowSafetyWarning(final int n) {
                VolumeDialogImpl.this.showSafetyWarningH(n);
            }
            
            @Override
            public void onShowSilentHint() {
                if (VolumeDialogImpl.this.mSilentMode) {
                    VolumeDialogImpl.this.mController.setRingerMode(2, false);
                }
            }
            
            @Override
            public void onShowVibrateHint() {
                if (VolumeDialogImpl.this.mSilentMode) {
                    VolumeDialogImpl.this.mController.setRingerMode(0, false);
                }
            }
            
            @Override
            public void onStateChanged(final State state) {
                VolumeDialogImpl.this.onStateChangedH(state);
            }
        };
        this.mContext = (Context)new ContextThemeWrapper(context, R$style.qs_theme);
        this.mController = Dependency.get(VolumeDialogController.class);
        this.mKeyguard = (KeyguardManager)this.mContext.getSystemService("keyguard");
        this.mActivityManager = (ActivityManager)this.mContext.getSystemService("activity");
        this.mAccessibilityMgr = Dependency.get(AccessibilityManagerWrapper.class);
        this.mDeviceProvisionedController = Dependency.get(DeviceProvisionedController.class);
        this.mShowActiveStreamOnly = this.showActiveStreamOnly();
        this.mHasSeenODICaptionsTooltip = Prefs.getBoolean(context, "HasSeenODICaptionsTooltip", false);
    }
    
    private void addAccessibilityDescription(final View view, int n, final String s) {
        if (n != 0) {
            if (n != 1) {
                n = R$string.volume_ringer_status_normal;
            }
            else {
                n = R$string.volume_ringer_status_vibrate;
            }
        }
        else {
            n = R$string.volume_ringer_status_silent;
        }
        view.setContentDescription((CharSequence)this.mContext.getString(n));
        view.setAccessibilityDelegate((View$AccessibilityDelegate)new View$AccessibilityDelegate(this) {
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(16, (CharSequence)s));
            }
        });
    }
    
    private void addExistingRows() {
        for (int size = this.mRows.size(), i = 0; i < size; ++i) {
            final VolumeRow volumeRow = this.mRows.get(i);
            this.initRow(volumeRow, volumeRow.stream, volumeRow.iconRes, volumeRow.iconMuteRes, volumeRow.important, volumeRow.defaultStream);
            this.mDialogRowsView.addView(volumeRow.view);
            this.updateVolumeRowH(volumeRow);
        }
    }
    
    private void addRow(final int n, final int n2, final int n3, final boolean b, final boolean b2) {
        this.addRow(n, n2, n3, b, b2, false);
    }
    
    private void addRow(final int i, final int n, final int n2, final boolean b, final boolean b2, final boolean b3) {
        if (D.BUG) {
            final String tag = VolumeDialogImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Adding row for stream ");
            sb.append(i);
            Slog.d(tag, sb.toString());
        }
        final VolumeRow volumeRow = new VolumeRow();
        this.initRow(volumeRow, i, n, n2, b, b2);
        this.mDialogRowsView.addView(volumeRow.view);
        this.mRows.add(volumeRow);
    }
    
    private void checkODICaptionsTooltip(final boolean b) {
        if (!this.mHasSeenODICaptionsTooltip && !b && this.mODICaptionsTooltipViewStub != null) {
            this.mController.getCaptionsComponentState(true);
        }
        else if (this.mHasSeenODICaptionsTooltip && b && this.mODICaptionsTooltipView != null) {
            this.hideCaptionsTooltip();
        }
    }
    
    private int computeTimeoutH() {
        if (this.mHovering) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(16000, 4);
        }
        if (this.mSafetyWarning != null) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(5000, 6);
        }
        if (!this.mHasSeenODICaptionsTooltip && this.mODICaptionsTooltipView != null) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(5000, 6);
        }
        return this.mAccessibilityMgr.getRecommendedTimeoutMillis(3000, 4);
    }
    
    private void enableRingerViewsH(final boolean enabled) {
        final ImageButton mRingerIcon = this.mRingerIcon;
        if (mRingerIcon != null) {
            mRingerIcon.setEnabled(enabled);
        }
        final FrameLayout mZenIcon = this.mZenIcon;
        if (mZenIcon != null) {
            int visibility;
            if (enabled) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            mZenIcon.setVisibility(visibility);
        }
    }
    
    private void enableVolumeRowViewsH(final VolumeRow volumeRow, final boolean b) {
        final FrameLayout access$900 = volumeRow.dndIcon;
        int visibility;
        if (b ^ true) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        access$900.setVisibility(visibility);
    }
    
    private VolumeRow findRow(final int n) {
        for (final VolumeRow volumeRow : this.mRows) {
            if (volumeRow.stream == n) {
                return volumeRow;
            }
        }
        return null;
    }
    
    private VolumeRow getActiveRow() {
        for (final VolumeRow volumeRow : this.mRows) {
            if (volumeRow.stream == this.mActiveStream) {
                return volumeRow;
            }
        }
        for (final VolumeRow volumeRow2 : this.mRows) {
            if (volumeRow2.stream == 3) {
                return volumeRow2;
            }
        }
        return this.mRows.get(0);
    }
    
    private int getAlphaAttr(final int n) {
        final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[] { n });
        final float float1 = obtainStyledAttributes.getFloat(0, 0.0f);
        obtainStyledAttributes.recycle();
        return (int)(float1 * 255.0f);
    }
    
    private static int getImpliedLevel(final SeekBar seekBar, int n) {
        final int max = seekBar.getMax();
        final int n2 = max / 100;
        if (n == 0) {
            n = 0;
        }
        else if (n == max) {
            n = n2;
        }
        else {
            n = (int)(n / (float)max * (n2 - 1)) + 1;
        }
        return n;
    }
    
    private Runnable getSinglePressFor(final ImageButton imageButton) {
        return new _$$Lambda$VolumeDialogImpl$EL__xLq17J_BDlmCmJk3kWI_8E8(this, imageButton);
    }
    
    private Runnable getSingleUnpressFor(final ImageButton imageButton) {
        return new _$$Lambda$VolumeDialogImpl$A9JxlbuHI6pR__4OJL5e0cwBcPs(imageButton);
    }
    
    private String getStreamLabelH(final VolumeDialogController.StreamState obj) {
        if (obj == null) {
            return "";
        }
        final String remoteLabel = obj.remoteLabel;
        if (remoteLabel != null) {
            return remoteLabel;
        }
        try {
            return this.mContext.getResources().getString(obj.name);
        }
        catch (Resources$NotFoundException ex) {
            final String tag = VolumeDialogImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Can't find translation for stream ");
            sb.append(obj);
            Slog.e(tag, sb.toString());
            return "";
        }
    }
    
    private void hideCaptionsTooltip() {
        final View modiCaptionsTooltipView = this.mODICaptionsTooltipView;
        if (modiCaptionsTooltipView != null && modiCaptionsTooltipView.getVisibility() == 0) {
            this.mODICaptionsTooltipView.animate().cancel();
            this.mODICaptionsTooltipView.setAlpha(1.0f);
            this.mODICaptionsTooltipView.animate().alpha(0.0f).setStartDelay(0L).setDuration(250L).withEndAction((Runnable)new _$$Lambda$VolumeDialogImpl$eJIc7NaYfyZjv9kbw4RrRBwcYRI(this)).start();
        }
    }
    
    private void incrementManualToggleCount() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        Settings$Secure.putInt(contentResolver, "manual_ringer_toggle_count", Settings$Secure.getInt(contentResolver, "manual_ringer_toggle_count", 0) + 1);
    }
    
    private void initDialog() {
        this.mDialog = new CustomDialog(this.mContext);
        this.mConfigurableTexts = new ConfigurableTexts(this.mContext);
        this.mHovering = false;
        this.mShowing = false;
        (this.mWindow = this.mDialog.getWindow()).requestFeature(1);
        this.mWindow.setBackgroundDrawable((Drawable)new ColorDrawable(0));
        this.mWindow.clearFlags(65538);
        this.mWindow.addFlags(17563944);
        this.mWindow.setType(2020);
        this.mWindow.setWindowAnimations(16973828);
        final WindowManager$LayoutParams attributes = this.mWindow.getAttributes();
        attributes.format = -3;
        attributes.setTitle((CharSequence)VolumeDialogImpl.class.getSimpleName());
        attributes.windowAnimations = -1;
        attributes.gravity = this.mContext.getResources().getInteger(R$integer.volume_dialog_gravity);
        this.mWindow.setAttributes(attributes);
        this.mWindow.setLayout(-2, -2);
        this.mDialog.setContentView(R$layout.volume_dialog);
        (this.mDialogView = (ViewGroup)this.mDialog.findViewById(R$id.volume_dialog)).setAlpha(0.0f);
        this.mDialog.setCanceledOnTouchOutside(true);
        this.mDialog.setOnShowListener((DialogInterface$OnShowListener)new _$$Lambda$VolumeDialogImpl$8BZhTIdOE2rPYfFa5HbcUDCtXeM(this));
        this.mDialogView.setOnHoverListener((View$OnHoverListener)new _$$Lambda$VolumeDialogImpl$T52d0W13mYvykk6ORgbytqfZsps(this));
        this.mDialogRowsView = (ViewGroup)this.mDialog.findViewById(R$id.volume_dialog_rows);
        final ViewGroup mRinger = (ViewGroup)this.mDialog.findViewById(R$id.ringer);
        this.mRinger = mRinger;
        if (mRinger != null) {
            this.mRingerIcon = (ImageButton)mRinger.findViewById(R$id.ringer_icon);
            this.mZenIcon = (FrameLayout)this.mRinger.findViewById(R$id.dnd_icon);
        }
        final ViewGroup modiCaptionsView = (ViewGroup)this.mDialog.findViewById(R$id.odi_captions);
        if ((this.mODICaptionsView = modiCaptionsView) != null) {
            this.mODICaptionsIcon = (CaptionsToggleImageButton)modiCaptionsView.findViewById(R$id.odi_captions_icon);
        }
        final ViewStub modiCaptionsTooltipViewStub = (ViewStub)this.mDialog.findViewById(R$id.odi_captions_tooltip_stub);
        this.mODICaptionsTooltipViewStub = modiCaptionsTooltipViewStub;
        if (this.mHasSeenODICaptionsTooltip && modiCaptionsTooltipViewStub != null) {
            this.mDialogView.removeView((View)modiCaptionsTooltipViewStub);
            this.mODICaptionsTooltipViewStub = null;
        }
        this.mSettingsView = this.mDialog.findViewById(R$id.settings_container);
        this.mSettingsIcon = (ImageButton)this.mDialog.findViewById(R$id.settings);
        if (this.mRows.isEmpty()) {
            if (!AudioSystem.isSingleVolume(this.mContext)) {
                final int ic_volume_accessibility = R$drawable.ic_volume_accessibility;
                this.addRow(10, ic_volume_accessibility, ic_volume_accessibility, true, false);
            }
            this.addRow(3, R$drawable.ic_volume_media, R$drawable.ic_volume_media_mute, true, true);
            if (!AudioSystem.isSingleVolume(this.mContext)) {
                this.addRow(2, R$drawable.ic_volume_ringer, R$drawable.ic_volume_ringer_mute, true, false);
                this.addRow(4, R$drawable.ic_alarm, R$drawable.ic_volume_alarm_mute, true, false);
                this.addRow(0, 17302783, 17302783, false, false);
                final int ic_volume_bt_sco = R$drawable.ic_volume_bt_sco;
                this.addRow(6, ic_volume_bt_sco, ic_volume_bt_sco, false, false);
                this.addRow(1, R$drawable.ic_volume_system, R$drawable.ic_volume_system_mute, false, false);
            }
        }
        else {
            this.addExistingRows();
        }
        this.updateRowsH(this.getActiveRow());
        this.initRingerH();
        this.initSettingsH();
        this.initODICaptionsH();
    }
    
    private void initODICaptionsH() {
        final CaptionsToggleImageButton modiCaptionsIcon = this.mODICaptionsIcon;
        if (modiCaptionsIcon != null) {
            modiCaptionsIcon.setOnConfirmedTapListener((CaptionsToggleImageButton.ConfirmedTapListener)new _$$Lambda$VolumeDialogImpl$HIlX6MPuNck4Zm6cfzTdHTUxqn4(this), this.mHandler);
        }
        this.mController.getCaptionsComponentState(false);
    }
    
    @SuppressLint({ "InflateParams" })
    private void initRow(final VolumeRow tag, final int n, final int imageResource, final int n2, final boolean b, final boolean b2) {
        tag.stream = n;
        tag.iconRes = imageResource;
        tag.iconMuteRes = n2;
        tag.important = b;
        tag.defaultStream = b2;
        tag.view = this.mDialog.getLayoutInflater().inflate(R$layout.volume_dialog_row, (ViewGroup)null);
        tag.view.setId(tag.stream);
        tag.view.setTag((Object)tag);
        tag.header = (TextView)tag.view.findViewById(R$id.volume_row_header);
        tag.header.setId(tag.stream * 20);
        if (n == 10) {
            tag.header.setFilters(new InputFilter[] { (InputFilter)new InputFilter$LengthFilter(13) });
        }
        tag.dndIcon = (FrameLayout)tag.view.findViewById(R$id.dnd_icon);
        tag.slider = (SeekBar)tag.view.findViewById(R$id.volume_row_slider);
        tag.slider.setOnSeekBarChangeListener((SeekBar$OnSeekBarChangeListener)new VolumeSeekBarChangeListener(tag));
        tag.anim = null;
        tag.icon = (ImageButton)tag.view.findViewById(R$id.volume_row_icon);
        tag.icon.setImageResource(imageResource);
        if (tag.stream != 10) {
            tag.icon.setOnClickListener((View$OnClickListener)new _$$Lambda$VolumeDialogImpl$I_0sumSTzcnKtt5xn4YVlQQget8(this, tag, n));
        }
        else {
            tag.icon.setImportantForAccessibility(2);
        }
    }
    
    private boolean isLandscape() {
        return this.mContext.getResources().getConfiguration().orientation == 2;
    }
    
    private boolean isStreamMuted(final VolumeDialogController.StreamState streamState) {
        return (this.mAutomute && streamState.level == 0) || streamState.muted;
    }
    
    private void maybeShowToastH(final int n) {
        final int int1 = Prefs.getInt(this.mContext, "RingerGuidanceCount", 0);
        if (int1 > 12) {
            return;
        }
        CharSequence charSequence = null;
        if (n != 0) {
            if (n != 2) {
                charSequence = this.mContext.getString(17041410);
            }
            else {
                final VolumeDialogController.StreamState streamState = (VolumeDialogController.StreamState)this.mState.states.get(2);
                if (streamState != null) {
                    charSequence = this.mContext.getString(R$string.volume_dialog_ringer_guidance_ring, new Object[] { Utils.formatPercentage(streamState.level, streamState.levelMax) });
                }
            }
        }
        else {
            charSequence = this.mContext.getString(17041409);
        }
        Toast.makeText(this.mContext, charSequence, 0).show();
        Prefs.putInt(this.mContext, "RingerGuidanceCount", int1 + 1);
    }
    
    private void onCaptionIconClicked() {
        this.mController.setCaptionsEnabled(this.mController.areCaptionsEnabled() ^ true);
        this.updateCaptionsIcon();
    }
    
    private void provideTouchFeedbackH(final int n) {
        VibrationEffect vibrationEffect;
        if (n != 0) {
            if (n != 2) {
                vibrationEffect = VibrationEffect.get(1);
            }
            else {
                this.mController.scheduleTouchFeedback();
                vibrationEffect = null;
            }
        }
        else {
            vibrationEffect = VibrationEffect.get(0);
        }
        if (vibrationEffect != null) {
            this.mController.vibrate(vibrationEffect);
        }
    }
    
    private void recheckH(final VolumeRow volumeRow) {
        if (volumeRow == null) {
            if (D.BUG) {
                Log.d(VolumeDialogImpl.TAG, "recheckH ALL");
            }
            this.trimObsoleteH();
            final Iterator<VolumeRow> iterator = this.mRows.iterator();
            while (iterator.hasNext()) {
                this.updateVolumeRowH(iterator.next());
            }
        }
        else {
            if (D.BUG) {
                final String tag = VolumeDialogImpl.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("recheckH ");
                sb.append(volumeRow.stream);
                Log.d(tag, sb.toString());
            }
            this.updateVolumeRowH(volumeRow);
        }
    }
    
    private void setStreamImportantH(final int n, final boolean b) {
        for (final VolumeRow volumeRow : this.mRows) {
            if (volumeRow.stream == n) {
                volumeRow.important = b;
                break;
            }
        }
    }
    
    private boolean shouldBeVisibleH(final VolumeRow volumeRow, final VolumeRow volumeRow2) {
        final int access$300 = volumeRow.stream;
        final int access$301 = volumeRow2.stream;
        final boolean b = false;
        if (access$300 == access$301) {
            return true;
        }
        boolean b2 = b;
        if (!this.mShowActiveStreamOnly) {
            if (volumeRow.stream == 10) {
                return this.mShowA11yStream;
            }
            if (volumeRow2.stream == 10 && volumeRow.stream == this.mPrevActiveStream) {
                return true;
            }
            b2 = b;
            if (volumeRow.defaultStream) {
                if (volumeRow2.stream != 2 && volumeRow2.stream != 4 && volumeRow2.stream != 0 && volumeRow2.stream != 10) {
                    b2 = b;
                    if (!this.mDynamic.get(volumeRow2.stream)) {
                        return b2;
                    }
                }
                b2 = true;
            }
        }
        return b2;
    }
    
    private boolean showActiveStreamOnly() {
        return this.mContext.getPackageManager().hasSystemFeature("android.software.leanback") || this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.television");
    }
    
    private void showH(final int i) {
        if (D.BUG) {
            final String tag = VolumeDialogImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("showH r=");
            sb.append(Events.SHOW_REASONS[i]);
            Log.d(tag, sb.toString());
        }
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        this.rescheduleTimeoutH();
        if (this.mConfigChanged) {
            this.initDialog();
            this.mConfigurableTexts.update();
            this.mConfigChanged = false;
        }
        this.initSettingsH();
        this.mShowing = true;
        this.mIsAnimatingDismiss = false;
        this.mDialog.show();
        Events.writeEvent(0, i, this.mKeyguard.isKeyguardLocked());
        this.mController.notifyVisible(true);
        this.mController.getCaptionsComponentState(false);
        this.checkODICaptionsTooltip(false);
    }
    
    private void showSafetyWarningH(final int n) {
        Label_0070: {
            if ((n & 0x401) == 0x0 && !this.mShowing) {
                break Label_0070;
            }
            synchronized (this.mSafetyWarningLock) {
                if (this.mSafetyWarning != null) {
                    return;
                }
                (this.mSafetyWarning = new SafetyWarningDialog(this.mContext, this.mController.getAudioManager()) {
                    @Override
                    protected void cleanUp() {
                        synchronized (VolumeDialogImpl.this.mSafetyWarningLock) {
                            VolumeDialogImpl.this.mSafetyWarning = null;
                            // monitorexit(VolumeDialogImpl.access$2200(this.this$0))
                            VolumeDialogImpl.this.recheckH(null);
                        }
                    }
                }).show();
                // monitorexit(this.mSafetyWarningLock)
                this.recheckH(null);
                this.rescheduleTimeoutH();
            }
        }
    }
    
    private void trimObsoleteH() {
        if (D.BUG) {
            Log.d(VolumeDialogImpl.TAG, "trimObsoleteH");
        }
        for (int i = this.mRows.size() - 1; i >= 0; --i) {
            final VolumeRow volumeRow = this.mRows.get(i);
            if (volumeRow.ss != null) {
                if (volumeRow.ss.dynamic) {
                    if (!this.mDynamic.get(volumeRow.stream)) {
                        this.mRows.remove(i);
                        this.mDialogRowsView.removeView(volumeRow.view);
                        this.mConfigurableTexts.remove(volumeRow.header);
                    }
                }
            }
        }
    }
    
    private void updateCaptionsIcon() {
        final boolean captionsEnabled = this.mController.areCaptionsEnabled();
        if (this.mODICaptionsIcon.getCaptionsEnabled() != captionsEnabled) {
            this.mHandler.post(this.mODICaptionsIcon.setCaptionsEnabled(captionsEnabled));
        }
        final boolean captionStreamOptedOut = this.mController.isCaptionStreamOptedOut();
        if (this.mODICaptionsIcon.getOptedOut() != captionStreamOptedOut) {
            this.mHandler.post((Runnable)new _$$Lambda$VolumeDialogImpl$lHJr2h1jrFiBPAxP01FnOgolTSg(this, captionStreamOptedOut));
        }
    }
    
    private void updateODICaptionsH(final boolean b, final boolean b2) {
        final ViewGroup modiCaptionsView = this.mODICaptionsView;
        if (modiCaptionsView != null) {
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            modiCaptionsView.setVisibility(visibility);
        }
        if (!b) {
            return;
        }
        this.updateCaptionsIcon();
        if (b2) {
            this.showCaptionsTooltip();
        }
    }
    
    private void updateRowsH(final VolumeRow volumeRow) {
        if (D.BUG) {
            Log.d(VolumeDialogImpl.TAG, "updateRowsH");
        }
        if (!this.mShowing) {
            this.trimObsoleteH();
        }
        for (final VolumeRow volumeRow2 : this.mRows) {
            final boolean b = volumeRow2 == volumeRow;
            Util.setVisOrGone(volumeRow2.view, this.shouldBeVisibleH(volumeRow2, volumeRow));
            if (volumeRow2.view.isShown()) {
                this.updateVolumeRowTintH(volumeRow2, b);
            }
        }
    }
    
    private void updateVolumeRowH(final VolumeRow volumeRow) {
        if (D.BUG) {
            final String tag = VolumeDialogImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("updateVolumeRowH s=");
            sb.append(volumeRow.stream);
            Log.i(tag, sb.toString());
        }
        final VolumeDialogController.State mState = this.mState;
        if (mState == null) {
            return;
        }
        final VolumeDialogController.StreamState streamState = (VolumeDialogController.StreamState)mState.states.get(volumeRow.stream);
        if (streamState == null) {
            return;
        }
        volumeRow.ss = streamState;
        final int level = streamState.level;
        if (level > 0) {
            volumeRow.lastAudibleLevel = level;
        }
        if (streamState.level == volumeRow.requestedLevel) {
            volumeRow.requestedLevel = -1;
        }
        final int access$300 = volumeRow.stream;
        final int n = 0;
        final boolean b = access$300 == 10;
        final int access$301 = volumeRow.stream;
        final int n2 = 2;
        final boolean b2 = access$301 == 2;
        final boolean b3 = volumeRow.stream == 1;
        final boolean b4 = volumeRow.stream == 4;
        final boolean b5 = volumeRow.stream == 3;
        final boolean b6 = b2 && this.mState.ringerModeInternal == 1;
        final boolean b7 = b2 && this.mState.ringerModeInternal == 0;
        final boolean b8 = this.mState.zenMode == 1;
        final boolean b9 = this.mState.zenMode == 3;
        final boolean b10 = this.mState.zenMode == 2;
        boolean b11 = false;
        Label_0444: {
            Label_0441: {
                if (b9) {
                    if (!b2 && !b3) {
                        break Label_0441;
                    }
                }
                else if (b10) {
                    if (!b2 && !b3 && !b4) {
                        if (!b5) {
                            break Label_0441;
                        }
                    }
                }
                else {
                    if (!b8) {
                        break Label_0441;
                    }
                    if ((!b4 || !this.mState.disallowAlarms) && (!b5 || !this.mState.disallowMedia) && (!b2 || !this.mState.disallowRinger)) {
                        if (!b3 || !this.mState.disallowSystem) {
                            break Label_0441;
                        }
                    }
                }
                b11 = true;
                break Label_0444;
            }
            b11 = false;
        }
        final int max = streamState.levelMax * 100;
        if (max != volumeRow.slider.getMax()) {
            volumeRow.slider.setMax(max);
        }
        final int min = streamState.levelMin * 100;
        if (min != volumeRow.slider.getMin()) {
            volumeRow.slider.setMin(min);
        }
        com.android.settingslib.volume.Util.setText(volumeRow.header, this.getStreamLabelH(streamState));
        volumeRow.slider.setContentDescription(volumeRow.header.getText());
        this.mConfigurableTexts.add(volumeRow.header, streamState.name);
        final boolean enabled = (this.mAutomute || streamState.muteSupported) && !b11;
        volumeRow.icon.setEnabled(enabled);
        final ImageButton access$302 = volumeRow.icon;
        float alpha;
        if (enabled) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.5f;
        }
        access$302.setAlpha(alpha);
        int imageResource;
        if (b6) {
            imageResource = R$drawable.ic_volume_ringer_vibrate;
        }
        else if (!b7 && !b11) {
            if (streamState.routedToBluetooth) {
                if (this.isStreamMuted(streamState)) {
                    imageResource = R$drawable.ic_volume_media_bt_mute;
                }
                else {
                    imageResource = R$drawable.ic_volume_media_bt;
                }
            }
            else if (this.isStreamMuted(streamState)) {
                imageResource = volumeRow.iconMuteRes;
            }
            else {
                imageResource = volumeRow.iconRes;
            }
        }
        else {
            imageResource = volumeRow.iconMuteRes;
        }
        volumeRow.icon.setImageResource(imageResource);
        int n3;
        if (imageResource == R$drawable.ic_volume_ringer_vibrate) {
            n3 = 3;
        }
        else {
            n3 = n2;
            if (imageResource != R$drawable.ic_volume_media_bt_mute) {
                if (imageResource == volumeRow.iconMuteRes) {
                    n3 = n2;
                }
                else if (imageResource != R$drawable.ic_volume_media_bt && imageResource != volumeRow.iconRes) {
                    n3 = 0;
                }
                else {
                    n3 = 1;
                }
            }
        }
        volumeRow.iconState = n3;
        if (enabled) {
            if (b2) {
                if (b6) {
                    volumeRow.icon.setContentDescription((CharSequence)this.mContext.getString(R$string.volume_stream_content_description_unmute, new Object[] { this.getStreamLabelH(streamState) }));
                }
                else if (this.mController.hasVibrator()) {
                    final ImageButton access$303 = volumeRow.icon;
                    final Context mContext = this.mContext;
                    int n4;
                    if (this.mShowA11yStream) {
                        n4 = R$string.volume_stream_content_description_vibrate_a11y;
                    }
                    else {
                        n4 = R$string.volume_stream_content_description_vibrate;
                    }
                    access$303.setContentDescription((CharSequence)mContext.getString(n4, new Object[] { this.getStreamLabelH(streamState) }));
                }
                else {
                    final ImageButton access$304 = volumeRow.icon;
                    final Context mContext2 = this.mContext;
                    int n5;
                    if (this.mShowA11yStream) {
                        n5 = R$string.volume_stream_content_description_mute_a11y;
                    }
                    else {
                        n5 = R$string.volume_stream_content_description_mute;
                    }
                    access$304.setContentDescription((CharSequence)mContext2.getString(n5, new Object[] { this.getStreamLabelH(streamState) }));
                }
            }
            else if (b) {
                volumeRow.icon.setContentDescription((CharSequence)this.getStreamLabelH(streamState));
            }
            else if (!streamState.muted && (!this.mAutomute || streamState.level != 0)) {
                final ImageButton access$305 = volumeRow.icon;
                final Context mContext3 = this.mContext;
                int n6;
                if (this.mShowA11yStream) {
                    n6 = R$string.volume_stream_content_description_mute_a11y;
                }
                else {
                    n6 = R$string.volume_stream_content_description_mute;
                }
                access$305.setContentDescription((CharSequence)mContext3.getString(n6, new Object[] { this.getStreamLabelH(streamState) }));
            }
            else {
                volumeRow.icon.setContentDescription((CharSequence)this.mContext.getString(R$string.volume_stream_content_description_unmute, new Object[] { this.getStreamLabelH(streamState) }));
            }
        }
        else {
            volumeRow.icon.setContentDescription((CharSequence)this.getStreamLabelH(streamState));
        }
        if (b11) {
            volumeRow.tracking = false;
        }
        this.enableVolumeRowViewsH(volumeRow, b11 ^ true);
        int level2;
        if (volumeRow.ss.muted && !b2 && !b11) {
            level2 = n;
        }
        else {
            level2 = volumeRow.ss.level;
        }
        this.updateVolumeRowSliderH(volumeRow, b11 ^ true, level2);
    }
    
    private void updateVolumeRowSliderH(final VolumeRow volumeRow, final boolean enabled, int n) {
        volumeRow.slider.setEnabled(enabled);
        this.updateVolumeRowTintH(volumeRow, volumeRow.stream == this.mActiveStream);
        if (volumeRow.tracking) {
            return;
        }
        final int progress = volumeRow.slider.getProgress();
        final int impliedLevel = getImpliedLevel(volumeRow.slider, progress);
        final boolean b = volumeRow.view.getVisibility() == 0;
        final boolean b2 = SystemClock.uptimeMillis() - volumeRow.userAttempt < 1000L;
        this.mHandler.removeMessages(3, (Object)volumeRow);
        if (this.mShowing && b && b2) {
            if (D.BUG) {
                Log.d(VolumeDialogImpl.TAG, "inGracePeriod");
            }
            final H mHandler = this.mHandler;
            mHandler.sendMessageAtTime(mHandler.obtainMessage(3, (Object)volumeRow), volumeRow.userAttempt + 1000L);
            return;
        }
        if (n == impliedLevel && this.mShowing && b) {
            return;
        }
        n *= 100;
        if (progress != n) {
            if (this.mShowing && b) {
                if (volumeRow.anim != null && volumeRow.anim.isRunning() && volumeRow.animTargetProgress == n) {
                    return;
                }
                if (volumeRow.anim == null) {
                    volumeRow.anim = ObjectAnimator.ofInt((Object)volumeRow.slider, "progress", new int[] { progress, n });
                    volumeRow.anim.setInterpolator((TimeInterpolator)new DecelerateInterpolator());
                }
                else {
                    volumeRow.anim.cancel();
                    volumeRow.anim.setIntValues(new int[] { progress, n });
                }
                volumeRow.animTargetProgress = n;
                volumeRow.anim.setDuration(80L);
                volumeRow.anim.start();
            }
            else {
                if (volumeRow.anim != null) {
                    volumeRow.anim.cancel();
                }
                volumeRow.slider.setProgress(n, true);
            }
        }
    }
    
    private void updateVolumeRowTintH(final VolumeRow volumeRow, final boolean b) {
        if (b) {
            volumeRow.slider.requestFocus();
        }
        final boolean b2 = b && volumeRow.slider.isEnabled();
        ColorStateList list;
        if (b2) {
            list = Utils.getColorAccent(this.mContext);
        }
        else {
            list = Utils.getColorAttr(this.mContext, 16842800);
        }
        int imageAlpha;
        if (b2) {
            imageAlpha = Color.alpha(list.getDefaultColor());
        }
        else {
            imageAlpha = this.getAlphaAttr(16844115);
        }
        if (list == volumeRow.cachedTint) {
            return;
        }
        volumeRow.slider.setProgressTintList(list);
        volumeRow.slider.setThumbTintList(list);
        volumeRow.slider.setProgressBackgroundTintList(list);
        volumeRow.slider.setAlpha(imageAlpha / 255.0f);
        volumeRow.icon.setImageTintList(list);
        volumeRow.icon.setImageAlpha(imageAlpha);
        volumeRow.cachedTint = list;
    }
    
    CharSequence composeWindowTitle() {
        return this.mContext.getString(R$string.volume_dialog_title, new Object[] { this.getStreamLabelH(this.getActiveRow().ss) });
    }
    
    @Override
    public void destroy() {
        this.mController.removeCallback(this.mControllerCallbackH);
        this.mHandler.removeCallbacksAndMessages((Object)null);
        Dependency.get(ConfigurationController.class).removeCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    protected void dismissH(final int i) {
        if (D.BUG) {
            final String tag = VolumeDialogImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("mDialog.dismiss() reason: ");
            sb.append(Events.DISMISS_REASONS[i]);
            sb.append(" from: ");
            sb.append(Debug.getCaller());
            Log.d(tag, sb.toString());
        }
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(1);
        if (this.mIsAnimatingDismiss) {
            return;
        }
        this.mIsAnimatingDismiss = true;
        this.mDialogView.animate().cancel();
        if (this.mShowing) {
            this.mShowing = false;
            Events.writeEvent(1, i);
        }
        this.mDialogView.setTranslationX(0.0f);
        this.mDialogView.setAlpha(1.0f);
        final ViewPropertyAnimator withEndAction = this.mDialogView.animate().alpha(0.0f).setDuration(250L).setInterpolator((TimeInterpolator)new SystemUIInterpolators$LogAccelerateInterpolator()).withEndAction((Runnable)new _$$Lambda$VolumeDialogImpl$DPdXKFGeK_9VznmPgQ7xFJyJSxk(this));
        if (!this.isLandscape()) {
            withEndAction.translationX(this.mDialogView.getWidth() / 2.0f);
        }
        withEndAction.start();
        this.checkODICaptionsTooltip(true);
        this.mController.notifyVisible(false);
        synchronized (this.mSafetyWarningLock) {
            if (this.mSafetyWarning != null) {
                if (D.BUG) {
                    Log.d(VolumeDialogImpl.TAG, "SafetyWarning dismissed");
                }
                this.mSafetyWarning.dismiss();
            }
        }
    }
    
    @Override
    public void init(final int n, final Callback callback) {
        this.initDialog();
        this.mAccessibility.init();
        this.mController.addCallback(this.mControllerCallbackH, this.mHandler);
        this.mController.getState();
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    public void initRingerH() {
        final ImageButton mRingerIcon = this.mRingerIcon;
        if (mRingerIcon != null) {
            mRingerIcon.setAccessibilityLiveRegion(1);
            this.mRingerIcon.setOnClickListener((View$OnClickListener)new _$$Lambda$VolumeDialogImpl$leUR0c6hrY1TNx5XUG_xhXI1EHk(this));
        }
        this.updateRingerH();
    }
    
    public void initSettingsH() {
        final View mSettingsView = this.mSettingsView;
        if (mSettingsView != null) {
            int visibility;
            if (this.mDeviceProvisionedController.isCurrentUserSetup() && this.mActivityManager.getLockTaskModeState() == 0) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mSettingsView.setVisibility(visibility);
        }
        final ImageButton mSettingsIcon = this.mSettingsIcon;
        if (mSettingsIcon != null) {
            mSettingsIcon.setOnClickListener((View$OnClickListener)new _$$Lambda$VolumeDialogImpl$7RdQKc1FND8ZrjtxSEsHEKXSyeY(this));
        }
    }
    
    protected void onStateChangedH(final VolumeDialogController.State mState) {
        if (D.BUG) {
            final String tag = VolumeDialogImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("onStateChangedH() state: ");
            sb.append(mState.toString());
            Log.d(tag, sb.toString());
        }
        final VolumeDialogController.State mState2 = this.mState;
        if (mState2 != null && mState != null) {
            final int ringerModeInternal = mState2.ringerModeInternal;
            final int ringerModeInternal2 = mState.ringerModeInternal;
            if (ringerModeInternal != ringerModeInternal2 && ringerModeInternal2 == 1) {
                this.mController.vibrate(VibrationEffect.get(5));
            }
        }
        this.mState = mState;
        this.mDynamic.clear();
        for (int i = 0; i < mState.states.size(); ++i) {
            final int key = mState.states.keyAt(i);
            if (((VolumeDialogController.StreamState)mState.states.valueAt(i)).dynamic) {
                this.mDynamic.put(key, true);
                if (this.findRow(key) == null) {
                    this.addRow(key, R$drawable.ic_volume_remote, R$drawable.ic_volume_remote_mute, true, false, true);
                }
            }
        }
        final int mActiveStream = this.mActiveStream;
        final int activeStream = mState.activeStream;
        if (mActiveStream != activeStream) {
            this.mPrevActiveStream = mActiveStream;
            this.mActiveStream = activeStream;
            this.updateRowsH(this.getActiveRow());
            if (this.mShowing) {
                this.rescheduleTimeoutH();
            }
        }
        final Iterator<VolumeRow> iterator = this.mRows.iterator();
        while (iterator.hasNext()) {
            this.updateVolumeRowH(iterator.next());
        }
        this.updateRingerH();
        this.mWindow.setTitle(this.composeWindowTitle());
    }
    
    @Override
    public void onUiModeChanged() {
        this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
    }
    
    protected void rescheduleTimeoutH() {
        this.mHandler.removeMessages(2);
        final int computeTimeoutH = this.computeTimeoutH();
        final H mHandler = this.mHandler;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(2, 3, 0), (long)computeTimeoutH);
        if (D.BUG) {
            final String tag = VolumeDialogImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("rescheduleTimeout ");
            sb.append(computeTimeoutH);
            sb.append(" ");
            sb.append(Debug.getCaller());
            Log.d(tag, sb.toString());
        }
        this.mController.userActivity();
    }
    
    public void setAutomute(final boolean mAutomute) {
        if (this.mAutomute == mAutomute) {
            return;
        }
        this.mAutomute = mAutomute;
        this.mHandler.sendEmptyMessage(4);
    }
    
    public void setSilentMode(final boolean mSilentMode) {
        if (this.mSilentMode == mSilentMode) {
            return;
        }
        this.mSilentMode = mSilentMode;
        this.mHandler.sendEmptyMessage(4);
    }
    
    public void setStreamImportant(final int n, final boolean b) {
        this.mHandler.obtainMessage(5, n, (int)(b ? 1 : 0)).sendToTarget();
    }
    
    protected void showCaptionsTooltip() {
        if (!this.mHasSeenODICaptionsTooltip) {
            final ViewStub modiCaptionsTooltipViewStub = this.mODICaptionsTooltipViewStub;
            if (modiCaptionsTooltipViewStub != null) {
                final View inflate = modiCaptionsTooltipViewStub.inflate();
                this.mODICaptionsTooltipView = inflate;
                inflate.findViewById(R$id.dismiss).setOnClickListener((View$OnClickListener)new _$$Lambda$VolumeDialogImpl$TUvPGuqHQwDl__z3hgYr3GMVgOs(this));
                this.mODICaptionsTooltipViewStub = null;
                this.rescheduleTimeoutH();
            }
        }
        final View modiCaptionsTooltipView = this.mODICaptionsTooltipView;
        if (modiCaptionsTooltipView != null) {
            modiCaptionsTooltipView.setAlpha(0.0f);
            this.mODICaptionsTooltipView.animate().alpha(1.0f).setStartDelay(300L).withEndAction((Runnable)new _$$Lambda$VolumeDialogImpl$j7bv45Q5uulCvMsn_IeT1Mv2PxI(this)).start();
        }
    }
    
    protected void tryToRemoveCaptionsTooltip() {
        if (this.mHasSeenODICaptionsTooltip && this.mODICaptionsTooltipView != null) {
            ((ViewGroup)this.mDialog.findViewById(R$id.volume_dialog_container)).removeView(this.mODICaptionsTooltipView);
            this.mODICaptionsTooltipView = null;
        }
    }
    
    protected void updateRingerH() {
        if (this.mRinger != null) {
            final VolumeDialogController.State mState = this.mState;
            if (mState != null) {
                final VolumeDialogController.StreamState streamState = (VolumeDialogController.StreamState)mState.states.get(2);
                if (streamState == null) {
                    return;
                }
                final VolumeDialogController.State mState2 = this.mState;
                final int zenMode = mState2.zenMode;
                boolean b = false;
                final boolean b2 = zenMode == 3 || zenMode == 2 || (zenMode == 1 && mState2.disallowRinger);
                this.enableRingerViewsH(b2 ^ true);
                final int ringerModeInternal = this.mState.ringerModeInternal;
                if (ringerModeInternal != 0) {
                    if (ringerModeInternal != 1) {
                        if ((this.mAutomute && streamState.level == 0) || streamState.muted) {
                            b = true;
                        }
                        if (!b2 && b) {
                            this.mRingerIcon.setImageResource(R$drawable.ic_volume_ringer_mute);
                            this.addAccessibilityDescription((View)this.mRingerIcon, 2, this.mContext.getString(R$string.volume_ringer_hint_unmute));
                            this.mRingerIcon.setTag((Object)2);
                        }
                        else {
                            this.mRingerIcon.setImageResource(R$drawable.ic_volume_ringer);
                            if (this.mController.hasVibrator()) {
                                this.addAccessibilityDescription((View)this.mRingerIcon, 2, this.mContext.getString(R$string.volume_ringer_hint_vibrate));
                            }
                            else {
                                this.addAccessibilityDescription((View)this.mRingerIcon, 2, this.mContext.getString(R$string.volume_ringer_hint_mute));
                            }
                            this.mRingerIcon.setTag((Object)1);
                        }
                    }
                    else {
                        this.mRingerIcon.setImageResource(R$drawable.ic_volume_ringer_vibrate);
                        this.addAccessibilityDescription((View)this.mRingerIcon, 1, this.mContext.getString(R$string.volume_ringer_hint_mute));
                        this.mRingerIcon.setTag((Object)3);
                    }
                }
                else {
                    this.mRingerIcon.setImageResource(R$drawable.ic_volume_ringer_mute);
                    this.mRingerIcon.setTag((Object)2);
                    this.addAccessibilityDescription((View)this.mRingerIcon, 0, this.mContext.getString(R$string.volume_ringer_hint_unmute));
                }
            }
        }
    }
    
    private final class Accessibility extends View$AccessibilityDelegate
    {
        public boolean dispatchPopulateAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.getText().add(VolumeDialogImpl.this.composeWindowTitle());
            return true;
        }
        
        public void init() {
            VolumeDialogImpl.this.mDialogView.setAccessibilityDelegate((View$AccessibilityDelegate)this);
        }
        
        public boolean onRequestSendAccessibilityEvent(final ViewGroup viewGroup, final View view, final AccessibilityEvent accessibilityEvent) {
            VolumeDialogImpl.this.rescheduleTimeoutH();
            return super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }
    
    private final class CustomDialog extends Dialog implements DialogInterface
    {
        public CustomDialog(final Context context) {
            super(context, R$style.qs_theme);
        }
        
        public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
            VolumeDialogImpl.this.rescheduleTimeoutH();
            return super.dispatchTouchEvent(motionEvent);
        }
        
        protected void onStart() {
            super.setCanceledOnTouchOutside(true);
            super.onStart();
        }
        
        protected void onStop() {
            super.onStop();
            VolumeDialogImpl.this.mHandler.sendEmptyMessage(4);
        }
        
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            if (VolumeDialogImpl.this.mShowing && motionEvent.getAction() == 4) {
                VolumeDialogImpl.this.dismissH(1);
                return true;
            }
            return false;
        }
    }
    
    private final class H extends Handler
    {
        public H() {
            super(Looper.getMainLooper());
        }
        
        public void handleMessage(final Message message) {
            switch (message.what) {
                case 7: {
                    final VolumeDialogImpl this$0 = VolumeDialogImpl.this;
                    this$0.onStateChangedH(this$0.mState);
                    break;
                }
                case 6: {
                    VolumeDialogImpl.this.rescheduleTimeoutH();
                    break;
                }
                case 5: {
                    VolumeDialogImpl.this.setStreamImportantH(message.arg1, message.arg2 != 0);
                    break;
                }
                case 4: {
                    VolumeDialogImpl.this.recheckH(null);
                    break;
                }
                case 3: {
                    VolumeDialogImpl.this.recheckH((VolumeRow)message.obj);
                    break;
                }
                case 2: {
                    VolumeDialogImpl.this.dismissH(message.arg1);
                    break;
                }
                case 1: {
                    VolumeDialogImpl.this.showH(message.arg1);
                    break;
                }
            }
        }
    }
    
    private static class VolumeRow
    {
        private ObjectAnimator anim;
        private int animTargetProgress;
        private ColorStateList cachedTint;
        private boolean defaultStream;
        private FrameLayout dndIcon;
        private TextView header;
        private ImageButton icon;
        private int iconMuteRes;
        private int iconRes;
        private int iconState;
        private boolean important;
        private int lastAudibleLevel;
        private int requestedLevel;
        private SeekBar slider;
        private VolumeDialogController.StreamState ss;
        private int stream;
        private boolean tracking;
        private long userAttempt;
        private View view;
        
        private VolumeRow() {
            this.requestedLevel = -1;
            this.lastAudibleLevel = 1;
        }
    }
    
    private final class VolumeSeekBarChangeListener implements SeekBar$OnSeekBarChangeListener
    {
        private final VolumeRow mRow;
        
        private VolumeSeekBarChangeListener(final VolumeRow mRow) {
            this.mRow = mRow;
        }
        
        public void onProgressChanged(final SeekBar seekBar, int access$4100, final boolean b) {
            if (this.mRow.ss == null) {
                return;
            }
            if (D.BUG) {
                final String access$4101 = VolumeDialogImpl.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(AudioSystem.streamToString(this.mRow.stream));
                sb.append(" onProgressChanged ");
                sb.append(access$4100);
                sb.append(" fromUser=");
                sb.append(b);
                Log.d(access$4101, sb.toString());
            }
            if (!b) {
                return;
            }
            int n = access$4100;
            if (this.mRow.ss.levelMin > 0) {
                final int progress = this.mRow.ss.levelMin * 100;
                if ((n = access$4100) < progress) {
                    seekBar.setProgress(progress);
                    n = progress;
                }
            }
            access$4100 = getImpliedLevel(seekBar, n);
            if (this.mRow.ss.level != access$4100 || (this.mRow.ss.muted && access$4100 > 0)) {
                this.mRow.userAttempt = SystemClock.uptimeMillis();
                if (this.mRow.requestedLevel != access$4100) {
                    VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
                    VolumeDialogImpl.this.mController.setStreamVolume(this.mRow.stream, access$4100);
                    this.mRow.requestedLevel = access$4100;
                    Events.writeEvent(9, this.mRow.stream, access$4100);
                }
            }
        }
        
        public void onStartTrackingTouch(final SeekBar seekBar) {
            if (D.BUG) {
                final String access$4000 = VolumeDialogImpl.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("onStartTrackingTouch ");
                sb.append(this.mRow.stream);
                Log.d(access$4000, sb.toString());
            }
            VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
            this.mRow.tracking = true;
        }
        
        public void onStopTrackingTouch(final SeekBar seekBar) {
            if (D.BUG) {
                final String access$4000 = VolumeDialogImpl.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("onStopTrackingTouch ");
                sb.append(this.mRow.stream);
                Log.d(access$4000, sb.toString());
            }
            this.mRow.tracking = false;
            this.mRow.userAttempt = SystemClock.uptimeMillis();
            final int access$4001 = getImpliedLevel(seekBar, seekBar.getProgress());
            Events.writeEvent(16, this.mRow.stream, access$4001);
            if (this.mRow.ss.level != access$4001) {
                VolumeDialogImpl.this.mHandler.sendMessageDelayed(VolumeDialogImpl.this.mHandler.obtainMessage(3, (Object)this.mRow), 1000L);
            }
        }
    }
}
