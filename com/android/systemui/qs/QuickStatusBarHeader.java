// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.qs.customize.QSCustomizer;
import android.widget.RelativeLayout$LayoutParams;
import android.content.IntentFilter;
import com.android.systemui.statusbar.policy.DateView;
import android.content.res.ColorStateList;
import com.android.systemui.qs.carrier.QSCarrierGroup;
import android.view.ViewGroup;
import com.android.systemui.statusbar.phone.StatusIconContainer;
import com.android.systemui.R$id;
import android.content.res.Configuration;
import android.util.Log;
import android.util.Pair;
import android.view.DisplayCutout;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import android.view.WindowInsets;
import com.android.systemui.R$string;
import com.android.systemui.R$drawable;
import android.service.notification.ZenModeConfig;
import android.content.res.Resources;
import android.view.ViewGroup$LayoutParams;
import com.android.systemui.util.Utils;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.R$dimen;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.text.format.DateFormat;
import android.app.ActivityManager;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.graphics.Rect;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.widget.TextView;
import android.widget.ImageView;
import android.app.AlarmManager$AlarmClockInfo;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import android.view.View;
import com.android.systemui.DualToneHandler;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import android.view.View$OnClickListener;
import android.widget.RelativeLayout;

public class QuickStatusBarHeader extends RelativeLayout implements View$OnClickListener, NextAlarmChangeCallback, Callback
{
    private final ActivityStarter mActivityStarter;
    private final NextAlarmController mAlarmController;
    private BatteryMeterView mBatteryRemainingIcon;
    private BroadcastDispatcher mBroadcastDispatcher;
    private Clock mClockView;
    private final CommandQueue mCommandQueue;
    private DualToneHandler mDualToneHandler;
    private boolean mExpanded;
    protected QuickQSPanel mHeaderQsPanel;
    private TouchAnimator mHeaderTextContainerAlphaAnimator;
    private View mHeaderTextContainerView;
    private StatusBarIconController.TintedIconManager mIconManager;
    private boolean mListening;
    private AlarmManager$AlarmClockInfo mNextAlarm;
    private View mNextAlarmContainer;
    private ImageView mNextAlarmIcon;
    private TextView mNextAlarmTextView;
    private boolean mQsDisabled;
    private QSPanel mQsPanel;
    private View mQuickQsStatusIcons;
    private View mRingerContainer;
    private int mRingerMode;
    private ImageView mRingerModeIcon;
    private TextView mRingerModeTextView;
    private final BroadcastReceiver mRingerReceiver;
    private int mRoundedCornerPadding;
    private final StatusBarIconController mStatusBarIconController;
    private TouchAnimator mStatusIconsAlphaAnimator;
    private View mStatusSeparator;
    private View mSystemIconsView;
    private final ZenModeController mZenController;
    
    public QuickStatusBarHeader(final Context context, final AttributeSet set, final NextAlarmController mAlarmController, final ZenModeController mZenController, final StatusBarIconController mStatusBarIconController, final ActivityStarter mActivityStarter, final CommandQueue mCommandQueue, final BroadcastDispatcher mBroadcastDispatcher) {
        super(context, set);
        new Handler();
        this.mRingerMode = 2;
        this.mRingerReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                QuickStatusBarHeader.this.mRingerMode = intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
                QuickStatusBarHeader.this.updateStatusText();
            }
        };
        this.mRoundedCornerPadding = 0;
        this.mAlarmController = mAlarmController;
        this.mZenController = mZenController;
        this.mStatusBarIconController = mStatusBarIconController;
        this.mActivityStarter = mActivityStarter;
        this.mDualToneHandler = new DualToneHandler((Context)new ContextThemeWrapper(context, R$style.QSHeaderTheme));
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mCommandQueue = mCommandQueue;
    }
    
    private void applyDarkness(final int n, final Rect rect, final float n2, final int n3) {
        final View viewById = this.findViewById(n);
        if (viewById instanceof DarkIconDispatcher.DarkReceiver) {
            ((DarkIconDispatcher.DarkReceiver)viewById).onDarkChanged(rect, n2, n3);
        }
    }
    
    private String formatNextAlarm(final AlarmManager$AlarmClockInfo alarmManager$AlarmClockInfo) {
        if (alarmManager$AlarmClockInfo == null) {
            return "";
        }
        String s;
        if (DateFormat.is24HourFormat(super.mContext, ActivityManager.getCurrentUser())) {
            s = "EHm";
        }
        else {
            s = "Ehma";
        }
        return DateFormat.format((CharSequence)DateFormat.getBestDateTimePattern(Locale.getDefault(), s), alarmManager$AlarmClockInfo.getTriggerTime()).toString();
    }
    
    public static float getColorIntensity(final int n) {
        float n2;
        if (n == -1) {
            n2 = 0.0f;
        }
        else {
            n2 = 1.0f;
        }
        return n2;
    }
    
    private List<String> getIgnoredIconSlots() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add(super.mContext.getResources().getString(17041283));
        list.add(super.mContext.getResources().getString(17041295));
        return list;
    }
    
    private boolean updateAlarmStatus() {
        final int visibility = this.mNextAlarmTextView.getVisibility();
        final boolean b = true;
        final boolean b2 = visibility == 0;
        final CharSequence text = this.mNextAlarmTextView.getText();
        final AlarmManager$AlarmClockInfo mNextAlarm = this.mNextAlarm;
        int n;
        if (mNextAlarm != null) {
            this.mNextAlarmTextView.setText((CharSequence)this.formatNextAlarm(mNextAlarm));
            n = 1;
        }
        else {
            n = 0;
        }
        final ImageView mNextAlarmIcon = this.mNextAlarmIcon;
        final int n2 = 8;
        int visibility2;
        if (n != 0) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mNextAlarmIcon.setVisibility(visibility2);
        final TextView mNextAlarmTextView = this.mNextAlarmTextView;
        int visibility3;
        if (n != 0) {
            visibility3 = 0;
        }
        else {
            visibility3 = 8;
        }
        mNextAlarmTextView.setVisibility(visibility3);
        final View mNextAlarmContainer = this.mNextAlarmContainer;
        int visibility4 = n2;
        if (n != 0) {
            visibility4 = 0;
        }
        mNextAlarmContainer.setVisibility(visibility4);
        boolean b3 = b;
        if ((b2 ? 1 : 0) == n) {
            b3 = (!Objects.equals(text, this.mNextAlarmTextView.getText()) && b);
        }
        return b3;
    }
    
    private void updateHeaderTextContainerAlphaAnimator() {
        final TouchAnimator.Builder builder = new TouchAnimator.Builder();
        builder.addFloat(this.mHeaderTextContainerView, "alpha", 0.0f, 0.0f, 1.0f);
        this.mHeaderTextContainerAlphaAnimator = builder.build();
    }
    
    private void updateMinimumHeight() {
        this.setMinimumHeight(super.mContext.getResources().getDimensionPixelSize(17105471) + super.mContext.getResources().getDimensionPixelSize(R$dimen.qs_quick_header_panel_height));
    }
    
    private void updateResources() {
        final Resources resources = super.mContext.getResources();
        this.updateMinimumHeight();
        this.mRoundedCornerPadding = resources.getDimensionPixelSize(R$dimen.rounded_corner_content_padding);
        this.mHeaderTextContainerView.getLayoutParams().height = resources.getDimensionPixelSize(R$dimen.qs_header_tooltip_height);
        final View mHeaderTextContainerView = this.mHeaderTextContainerView;
        mHeaderTextContainerView.setLayoutParams(mHeaderTextContainerView.getLayoutParams());
        this.mSystemIconsView.getLayoutParams().height = resources.getDimensionPixelSize(17105427);
        final View mSystemIconsView = this.mSystemIconsView;
        mSystemIconsView.setLayoutParams(mSystemIconsView.getLayoutParams());
        final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)this.getLayoutParams();
        if (this.mQsDisabled) {
            layoutParams.height = resources.getDimensionPixelSize(17105427);
        }
        else if (Utils.useQsMediaPlayer(super.mContext) && this.mHeaderQsPanel.hasMediaPlayer()) {
            layoutParams.height = Math.max(this.getMinimumHeight(), resources.getDimensionPixelSize(17105429));
        }
        else {
            layoutParams.height = Math.max(this.getMinimumHeight(), resources.getDimensionPixelSize(17105428));
        }
        this.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        this.updateStatusIconAlphaAnimator();
        this.updateHeaderTextContainerAlphaAnimator();
    }
    
    private boolean updateRingerStatus() {
        final int visibility = this.mRingerModeTextView.getVisibility();
        final boolean b = true;
        final boolean b2 = visibility == 0;
        final CharSequence text = this.mRingerModeTextView.getText();
        int n = 0;
        Label_0122: {
            Label_0119: {
                if (!ZenModeConfig.isZenOverridingRinger(this.mZenController.getZen(), this.mZenController.getConsolidatedPolicy())) {
                    final int mRingerMode = this.mRingerMode;
                    if (mRingerMode == 1) {
                        this.mRingerModeIcon.setImageResource(R$drawable.ic_volume_ringer_vibrate);
                        this.mRingerModeTextView.setText(R$string.qs_status_phone_vibrate);
                    }
                    else {
                        if (mRingerMode != 0) {
                            break Label_0119;
                        }
                        this.mRingerModeIcon.setImageResource(R$drawable.ic_volume_ringer_mute);
                        this.mRingerModeTextView.setText(R$string.qs_status_phone_muted);
                    }
                    n = 1;
                    break Label_0122;
                }
            }
            n = 0;
        }
        final ImageView mRingerModeIcon = this.mRingerModeIcon;
        final int n2 = 8;
        int visibility2;
        if (n != 0) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mRingerModeIcon.setVisibility(visibility2);
        final TextView mRingerModeTextView = this.mRingerModeTextView;
        int visibility3;
        if (n != 0) {
            visibility3 = 0;
        }
        else {
            visibility3 = 8;
        }
        mRingerModeTextView.setVisibility(visibility3);
        final View mRingerContainer = this.mRingerContainer;
        int visibility4 = n2;
        if (n != 0) {
            visibility4 = 0;
        }
        mRingerContainer.setVisibility(visibility4);
        boolean b3 = b;
        if ((b2 ? 1 : 0) == n) {
            b3 = (!Objects.equals(text, this.mRingerModeTextView.getText()) && b);
        }
        return b3;
    }
    
    private void updateStatusIconAlphaAnimator() {
        final TouchAnimator.Builder builder = new TouchAnimator.Builder();
        builder.addFloat(this.mQuickQsStatusIcons, "alpha", 1.0f, 0.0f, 0.0f);
        this.mStatusIconsAlphaAnimator = builder.build();
    }
    
    private void updateStatusText() {
        final boolean updateRingerStatus = this.updateRingerStatus();
        boolean b = true;
        final int n = 0;
        if (updateRingerStatus || this.updateAlarmStatus()) {
            final boolean b2 = this.mNextAlarmTextView.getVisibility() == 0;
            if (this.mRingerModeTextView.getVisibility() != 0) {
                b = false;
            }
            final View mStatusSeparator = this.mStatusSeparator;
            int visibility;
            if (b2 && b) {
                visibility = n;
            }
            else {
                visibility = 8;
            }
            mStatusSeparator.setVisibility(visibility);
        }
    }
    
    public void disable(int n, final int n2, final boolean b) {
        boolean b2 = true;
        final int n3 = 0;
        if ((n2 & 0x1) == 0x0) {
            b2 = false;
        }
        if (b2 == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = b2;
        this.mHeaderQsPanel.setDisabledByPolicy(b2);
        final View mHeaderTextContainerView = this.mHeaderTextContainerView;
        if (this.mQsDisabled) {
            n = 8;
        }
        else {
            n = 0;
        }
        mHeaderTextContainerView.setVisibility(n);
        final View mQuickQsStatusIcons = this.mQuickQsStatusIcons;
        n = n3;
        if (this.mQsDisabled) {
            n = 8;
        }
        mQuickQsStatusIcons.setVisibility(n);
        this.updateResources();
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        this.setPadding(this.mRoundedCornerPadding, this.getPaddingTop(), this.mRoundedCornerPadding, this.getPaddingBottom());
        final DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        final Pair<Integer, Integer> paddingNeededForCutoutAndRoundedCorner = StatusBarWindowView.paddingNeededForCutoutAndRoundedCorner(displayCutout, StatusBarWindowView.cornerCutoutMargins(displayCutout, this.getDisplay()), this.mRoundedCornerPadding);
        int top;
        if (displayCutout == null) {
            top = 0;
        }
        else {
            top = displayCutout.getWaterfallInsets().top;
        }
        int n;
        if (this.isLayoutRtl()) {
            n = this.getResources().getDimensionPixelSize(R$dimen.status_bar_padding_end);
        }
        else {
            n = this.getResources().getDimensionPixelSize(R$dimen.status_bar_padding_start);
        }
        int n2;
        if (this.isLayoutRtl()) {
            n2 = this.getResources().getDimensionPixelSize(R$dimen.status_bar_padding_start);
        }
        else {
            n2 = this.getResources().getDimensionPixelSize(R$dimen.status_bar_padding_end);
        }
        this.mSystemIconsView.setPadding(Math.max((int)paddingNeededForCutoutAndRoundedCorner.first + n - this.mRoundedCornerPadding, 0), top, Math.max((int)paddingNeededForCutoutAndRoundedCorner.second + n2 - this.mRoundedCornerPadding, 0), 0);
        return super.onApplyWindowInsets(windowInsets);
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mStatusBarIconController.addIconGroup((StatusBarIconController.IconManager)this.mIconManager);
        this.requestApplyInsets();
    }
    
    public void onClick(final View view) {
        if (view == this.mClockView) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.intent.action.SHOW_ALARMS"), 0);
        }
        else {
            final View mNextAlarmContainer = this.mNextAlarmContainer;
            if (view == mNextAlarmContainer && mNextAlarmContainer.isVisibleToUser()) {
                if (this.mNextAlarm.getShowIntent() != null) {
                    this.mActivityStarter.postStartActivityDismissingKeyguard(this.mNextAlarm.getShowIntent());
                }
                else {
                    Log.d("QuickStatusBarHeader", "No PendingIntent for next alarm. Using default intent");
                    this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.intent.action.SHOW_ALARMS"), 0);
                }
            }
            else {
                final View mRingerContainer = this.mRingerContainer;
                if (view == mRingerContainer && mRingerContainer.isVisibleToUser()) {
                    this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.SOUND_SETTINGS"), 0);
                }
            }
        }
    }
    
    public void onConfigChanged(final ZenModeConfig zenModeConfig) {
        this.updateStatusText();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateResources();
        this.mClockView.useWallpaperTextColor(configuration.orientation == 2);
    }
    
    public void onDetachedFromWindow() {
        this.setListening(false);
        this.mStatusBarIconController.removeIconGroup((StatusBarIconController.IconManager)this.mIconManager);
        super.onDetachedFromWindow();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderQsPanel = (QuickQSPanel)this.findViewById(R$id.quick_qs_panel);
        this.mSystemIconsView = this.findViewById(R$id.quick_status_bar_system_icons);
        this.mQuickQsStatusIcons = this.findViewById(R$id.quick_qs_status_icons);
        final StatusIconContainer statusIconContainer = (StatusIconContainer)this.findViewById(R$id.statusIcons);
        statusIconContainer.addIgnoredSlots(this.getIgnoredIconSlots());
        statusIconContainer.setShouldRestrictIcons(false);
        this.mIconManager = new StatusBarIconController.TintedIconManager((ViewGroup)statusIconContainer, this.mCommandQueue);
        this.mHeaderTextContainerView = this.findViewById(R$id.header_text_container);
        this.mStatusSeparator = this.findViewById(R$id.status_separator);
        this.mNextAlarmIcon = (ImageView)this.findViewById(R$id.next_alarm_icon);
        this.mNextAlarmTextView = (TextView)this.findViewById(R$id.next_alarm_text);
        (this.mNextAlarmContainer = this.findViewById(R$id.alarm_container)).setOnClickListener((View$OnClickListener)new _$$Lambda$p8TkVReSUo0LsQ3y_9iKja9mJXE(this));
        this.mRingerModeIcon = (ImageView)this.findViewById(R$id.ringer_mode_icon);
        this.mRingerModeTextView = (TextView)this.findViewById(R$id.ringer_mode_text);
        (this.mRingerContainer = this.findViewById(R$id.ringer_container)).setOnClickListener((View$OnClickListener)new _$$Lambda$p8TkVReSUo0LsQ3y_9iKja9mJXE(this));
        final QSCarrierGroup qsCarrierGroup = (QSCarrierGroup)this.findViewById(R$id.carrier_group);
        this.updateResources();
        final Rect rect = new Rect(0, 0, 0, 0);
        final int singleColor = this.mDualToneHandler.getSingleColor(getColorIntensity(com.android.settingslib.Utils.getColorAttrDefaultColor(this.getContext(), 16842800)));
        this.applyDarkness(R$id.clock, rect, 0.0f, -1);
        this.mIconManager.setTint(singleColor);
        this.mNextAlarmIcon.setImageTintList(ColorStateList.valueOf(singleColor));
        this.mRingerModeIcon.setImageTintList(ColorStateList.valueOf(singleColor));
        (this.mClockView = (Clock)this.findViewById(R$id.clock)).setOnClickListener((View$OnClickListener)this);
        final DateView dateView = (DateView)this.findViewById(R$id.date);
        (this.mBatteryRemainingIcon = (BatteryMeterView)this.findViewById(R$id.batteryRemainingIcon)).setIgnoreTunerUpdates(true);
        this.mBatteryRemainingIcon.setPercentShowMode(3);
        this.mRingerModeTextView.setSelected(true);
        this.mNextAlarmTextView.setSelected(true);
    }
    
    public void onNextAlarmChanged(final AlarmManager$AlarmClockInfo mNextAlarm) {
        this.mNextAlarm = mNextAlarm;
        this.updateStatusText();
    }
    
    public void onRtlPropertiesChanged(final int n) {
        super.onRtlPropertiesChanged(n);
        this.updateResources();
    }
    
    public void onZenChanged(final int n) {
        this.updateStatusText();
    }
    
    public void setCallback(final QSDetail.Callback callback) {
        this.mHeaderQsPanel.setCallback(callback);
    }
    
    public void setExpanded(final boolean b) {
        if (this.mExpanded == b) {
            return;
        }
        this.mExpanded = b;
        this.mHeaderQsPanel.setExpanded(b);
        this.updateEverything();
    }
    
    public void setExpansion(final boolean b, final float n, final float translationY) {
        float n2;
        if (b) {
            n2 = 1.0f;
        }
        else {
            n2 = n;
        }
        final TouchAnimator mStatusIconsAlphaAnimator = this.mStatusIconsAlphaAnimator;
        if (mStatusIconsAlphaAnimator != null) {
            mStatusIconsAlphaAnimator.setPosition(n2);
        }
        if (b) {
            this.mHeaderTextContainerView.setTranslationY(translationY);
        }
        else {
            this.mHeaderTextContainerView.setTranslationY(0.0f);
        }
        final TouchAnimator mHeaderTextContainerAlphaAnimator = this.mHeaderTextContainerAlphaAnimator;
        if (mHeaderTextContainerAlphaAnimator != null) {
            mHeaderTextContainerAlphaAnimator.setPosition(n2);
            if (n2 > 0.0f) {
                this.mHeaderTextContainerView.setVisibility(0);
            }
            else {
                this.mHeaderTextContainerView.setVisibility(4);
            }
        }
        if (n < 1.0f && n > 0.99 && this.mHeaderQsPanel.switchTileLayout()) {
            this.updateResources();
        }
    }
    
    public void setListening(final boolean b) {
        if (b == this.mListening) {
            return;
        }
        this.mHeaderQsPanel.setListening(b);
        if (this.mHeaderQsPanel.switchTileLayout()) {
            this.updateResources();
        }
        this.mListening = b;
        if (b) {
            this.mZenController.addCallback((ZenModeController.Callback)this);
            this.mAlarmController.addCallback((NextAlarmController.NextAlarmChangeCallback)this);
            this.mBroadcastDispatcher.registerReceiver(this.mRingerReceiver, new IntentFilter("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION"));
        }
        else {
            this.mZenController.removeCallback((ZenModeController.Callback)this);
            this.mAlarmController.removeCallback((NextAlarmController.NextAlarmChangeCallback)this);
            this.mBroadcastDispatcher.unregisterReceiver(this.mRingerReceiver);
        }
    }
    
    public void setMargins(final int n) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child != this.mSystemIconsView && child != this.mQuickQsStatusIcons && child != this.mHeaderQsPanel) {
                if (child != this.mHeaderTextContainerView) {
                    final RelativeLayout$LayoutParams relativeLayout$LayoutParams = (RelativeLayout$LayoutParams)child.getLayoutParams();
                    relativeLayout$LayoutParams.leftMargin = n;
                    relativeLayout$LayoutParams.rightMargin = n;
                }
            }
        }
    }
    
    public void setQSPanel(final QSPanel mQsPanel) {
        this.mQsPanel = mQsPanel;
        this.setupHost(mQsPanel.getHost());
    }
    
    public void setupHost(final QSTileHost qsTileHost) {
        this.mHeaderQsPanel.setQSPanelAndHeader(this.mQsPanel, (View)this);
        this.mHeaderQsPanel.setHost(qsTileHost, null);
        final Rect rect = new Rect(0, 0, 0, 0);
        final float colorIntensity = getColorIntensity(com.android.settingslib.Utils.getColorAttrDefaultColor(this.getContext(), 16842800));
        this.mBatteryRemainingIcon.onDarkChanged(rect, colorIntensity, this.mDualToneHandler.getSingleColor(colorIntensity));
    }
    
    public void updateEverything() {
        this.post((Runnable)new _$$Lambda$QuickStatusBarHeader$AvsHoBxZXMvvH_WD73mLXoXpNWs(this));
    }
}
