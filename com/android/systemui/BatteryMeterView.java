// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.text.TextUtils;
import android.net.Uri;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import android.provider.Settings$Global;
import android.app.ActivityManager;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.function.Supplier;
import android.animation.TimeInterpolator;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.LayoutTransition;
import java.text.NumberFormat;
import android.content.res.Resources;
import android.widget.LinearLayout$LayoutParams;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.res.TypedArray;
import com.android.systemui.util.SysuiLifecycle;
import android.provider.Settings$System;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.ViewGroup$MarginLayoutParams;
import android.graphics.drawable.Drawable;
import android.view.View$OnAttachStateChangeListener;
import com.android.systemui.util.Utils;
import com.android.systemui.statusbar.CommandQueue;
import android.os.Handler;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.settingslib.graph.ThemedBatteryDrawable;
import android.widget.TextView;
import android.widget.ImageView;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.statusbar.policy.BatteryController;
import android.widget.LinearLayout;

public class BatteryMeterView extends LinearLayout implements BatteryStateChangeCallback, Tunable, DarkReceiver, ConfigurationListener
{
    private BatteryController mBatteryController;
    private final ImageView mBatteryIconView;
    private TextView mBatteryPercentView;
    private boolean mCharging;
    private final ThemedBatteryDrawable mDrawable;
    private DualToneHandler mDualToneHandler;
    private boolean mForceShowPercent;
    private boolean mIgnoreTunerUpdates;
    private boolean mIsSubscribedForTunerUpdates;
    private int mLevel;
    private int mNonAdaptedBackgroundColor;
    private int mNonAdaptedForegroundColor;
    private int mNonAdaptedSingleToneColor;
    private final int mPercentageStyleId;
    private SettingObserver mSettingObserver;
    private boolean mShowPercentAvailable;
    private int mShowPercentMode;
    private final String mSlotBattery;
    private int mTextColor;
    private boolean mUseWallpaperTextColors;
    private int mUser;
    private final CurrentUserTracker mUserTracker;
    
    public BatteryMeterView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public BatteryMeterView(final Context context, final AttributeSet set, int color) {
        super(context, set, color);
        this.mShowPercentMode = 0;
        final BroadcastDispatcher broadcastDispatcher = Dependency.get(BroadcastDispatcher.class);
        this.setOrientation(0);
        this.setGravity(8388627);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.BatteryMeterView, color, 0);
        color = obtainStyledAttributes.getColor(R$styleable.BatteryMeterView_frameColor, context.getColor(R$color.meter_background_color));
        this.mPercentageStyleId = obtainStyledAttributes.getResourceId(R$styleable.BatteryMeterView_textAppearance, 0);
        this.mDrawable = new ThemedBatteryDrawable(context, color);
        obtainStyledAttributes.recycle();
        this.mSettingObserver = new SettingObserver(new Handler(context.getMainLooper()));
        this.mShowPercentAvailable = context.getResources().getBoolean(17891375);
        this.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new Utils.DisableStateTracker(0, 2, Dependency.get(CommandQueue.class)));
        this.setupLayoutTransition();
        this.mSlotBattery = context.getString(17041281);
        (this.mBatteryIconView = new ImageView(context)).setImageDrawable((Drawable)this.mDrawable);
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = new ViewGroup$MarginLayoutParams(this.getResources().getDimensionPixelSize(R$dimen.status_bar_battery_icon_width), this.getResources().getDimensionPixelSize(R$dimen.status_bar_battery_icon_height));
        viewGroup$MarginLayoutParams.setMargins(0, 0, 0, this.getResources().getDimensionPixelOffset(R$dimen.battery_margin_bottom));
        this.addView((View)this.mBatteryIconView, (ViewGroup$LayoutParams)viewGroup$MarginLayoutParams);
        this.updateShowPercent();
        this.mDualToneHandler = new DualToneHandler(context);
        this.onDarkChanged(new Rect(), 0.0f, -1);
        this.mUserTracker = new CurrentUserTracker(broadcastDispatcher) {
            @Override
            public void onUserSwitched(final int n) {
                BatteryMeterView.this.mUser = n;
                BatteryMeterView.this.getContext().getContentResolver().unregisterContentObserver((ContentObserver)BatteryMeterView.this.mSettingObserver);
                BatteryMeterView.this.getContext().getContentResolver().registerContentObserver(Settings$System.getUriFor("status_bar_show_battery_percent"), false, (ContentObserver)BatteryMeterView.this.mSettingObserver, n);
                BatteryMeterView.this.updateShowPercent();
            }
        };
        this.setClipChildren(false);
        this.setClipToPadding(false);
        Dependency.get(ConfigurationController.class).observe(SysuiLifecycle.viewAttachLifecycle((View)this), (ConfigurationController.ConfigurationListener)this);
    }
    
    private TextView loadPercentView() {
        return (TextView)LayoutInflater.from(this.getContext()).inflate(R$layout.battery_percentage_view, (ViewGroup)null);
    }
    
    private void scaleBatteryMeterViews() {
        final Resources resources = this.getContext().getResources();
        final TypedValue typedValue = new TypedValue();
        resources.getValue(R$dimen.status_bar_icon_scale_factor, typedValue, true);
        final float float1 = typedValue.getFloat();
        final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.status_bar_battery_icon_height);
        final int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.status_bar_battery_icon_width);
        final int dimensionPixelSize3 = resources.getDimensionPixelSize(R$dimen.battery_margin_bottom);
        final LinearLayout$LayoutParams layoutParams = new LinearLayout$LayoutParams((int)(dimensionPixelSize2 * float1), (int)(dimensionPixelSize * float1));
        layoutParams.setMargins(0, 0, 0, dimensionPixelSize3);
        this.mBatteryIconView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    private void setPercentTextAtCurrentLevel() {
        this.mBatteryPercentView.setText((CharSequence)NumberFormat.getPercentInstance().format(this.mLevel / 100.0f));
        final Context context = this.getContext();
        int n;
        if (this.mCharging) {
            n = R$string.accessibility_battery_level_charging;
        }
        else {
            n = R$string.accessibility_battery_level;
        }
        this.setContentDescription((CharSequence)context.getString(n, new Object[] { this.mLevel }));
    }
    
    private void setupLayoutTransition() {
        final LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(200L);
        layoutTransition.setAnimator(2, (Animator)ObjectAnimator.ofFloat((Object)null, "alpha", new float[] { 0.0f, 1.0f }));
        layoutTransition.setInterpolator(2, (TimeInterpolator)Interpolators.ALPHA_IN);
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)null, "alpha", new float[] { 1.0f, 0.0f });
        layoutTransition.setInterpolator(3, (TimeInterpolator)Interpolators.ALPHA_OUT);
        layoutTransition.setAnimator(3, (Animator)ofFloat);
        this.setLayoutTransition(layoutTransition);
    }
    
    private void subscribeForTunerUpdates() {
        if (!this.mIsSubscribedForTunerUpdates) {
            if (!this.mIgnoreTunerUpdates) {
                Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "icon_blacklist");
                this.mIsSubscribedForTunerUpdates = true;
            }
        }
    }
    
    private void unsubscribeFromTunerUpdates() {
        if (!this.mIsSubscribedForTunerUpdates) {
            return;
        }
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
        this.mIsSubscribedForTunerUpdates = false;
    }
    
    private void updateColors(final int n, final int n2, final int n3) {
        this.mDrawable.setColors(n, n2, n3);
        this.mTextColor = n3;
        final TextView mBatteryPercentView = this.mBatteryPercentView;
        if (mBatteryPercentView != null) {
            mBatteryPercentView.setTextColor(n3);
        }
    }
    
    private void updatePercentText() {
        final BatteryController mBatteryController = this.mBatteryController;
        if (mBatteryController == null) {
            return;
        }
        if (this.mBatteryPercentView != null) {
            if (this.mShowPercentMode == 3 && !this.mCharging) {
                mBatteryController.getEstimatedTimeRemainingString((BatteryController.EstimateFetchCompletion)new _$$Lambda$BatteryMeterView$yZDQalqWJG2q_49RDLUqR8bhWwM(this));
            }
            else {
                this.setPercentTextAtCurrentLevel();
            }
        }
        else {
            final Context context = this.getContext();
            int n;
            if (this.mCharging) {
                n = R$string.accessibility_battery_level_charging;
            }
            else {
                n = R$string.accessibility_battery_level;
            }
            this.setContentDescription((CharSequence)context.getString(n, new Object[] { this.mLevel }));
        }
    }
    
    private void updateShowPercent() {
        final TextView mBatteryPercentView = this.mBatteryPercentView;
        boolean b = false;
        final boolean b2 = mBatteryPercentView != null;
        if (DejankUtils.whitelistIpcs((Supplier<Integer>)new _$$Lambda$BatteryMeterView$65vHpQiubDRpix2SSD9dASDdHfc(this)) != 0) {
            b = true;
        }
        if (!this.mShowPercentAvailable || !b || this.mShowPercentMode == 2) {
            final int mShowPercentMode = this.mShowPercentMode;
            if (mShowPercentMode != 1) {
                if (mShowPercentMode != 3) {
                    if (b2) {
                        this.removeView((View)this.mBatteryPercentView);
                        this.mBatteryPercentView = null;
                    }
                    return;
                }
            }
        }
        if (!b2) {
            final TextView loadPercentView = this.loadPercentView();
            this.mBatteryPercentView = loadPercentView;
            final int mPercentageStyleId = this.mPercentageStyleId;
            if (mPercentageStyleId != 0) {
                loadPercentView.setTextAppearance(mPercentageStyleId);
            }
            final int mTextColor = this.mTextColor;
            if (mTextColor != 0) {
                this.mBatteryPercentView.setTextColor(mTextColor);
            }
            this.updatePercentText();
            this.addView((View)this.mBatteryPercentView, new ViewGroup$LayoutParams(-2, -1));
        }
    }
    
    private void updateTunerSubscription() {
        if (this.mIgnoreTunerUpdates) {
            this.unsubscribeFromTunerUpdates();
        }
        else {
            this.subscribeForTunerUpdates();
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final ThemedBatteryDrawable mDrawable = this.mDrawable;
        Object text = null;
        String string;
        if (mDrawable == null) {
            string = null;
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.mDrawable.getPowerSaveEnabled());
            sb.append("");
            string = sb.toString();
        }
        final TextView mBatteryPercentView = this.mBatteryPercentView;
        if (mBatteryPercentView != null) {
            text = mBatteryPercentView.getText();
        }
        printWriter.println("  BatteryMeterView:");
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("    mDrawable.getPowerSave: ");
        sb2.append(string);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("    mBatteryPercentView.getText(): ");
        sb3.append(text);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("    mTextColor: #");
        sb4.append(Integer.toHexString(this.mTextColor));
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("    mLevel: ");
        sb5.append(this.mLevel);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("    mForceShowPercent: ");
        sb6.append(this.mForceShowPercent);
        printWriter.println(sb6.toString());
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        (this.mBatteryController = Dependency.get(BatteryController.class)).addCallback((BatteryController.BatteryStateChangeCallback)this);
        this.mUser = ActivityManager.getCurrentUser();
        this.getContext().getContentResolver().registerContentObserver(Settings$System.getUriFor("status_bar_show_battery_percent"), false, (ContentObserver)this.mSettingObserver, this.mUser);
        this.getContext().getContentResolver().registerContentObserver(Settings$Global.getUriFor("battery_estimates_last_update_time"), false, (ContentObserver)this.mSettingObserver);
        this.updateShowPercent();
        this.subscribeForTunerUpdates();
        this.mUserTracker.startTracking();
    }
    
    public void onBatteryLevelChanged(final int n, final boolean b, final boolean b2) {
        this.mDrawable.setCharging(b);
        this.mDrawable.setBatteryLevel(n);
        this.mCharging = b;
        this.mLevel = n;
        this.updatePercentText();
    }
    
    public void onDarkChanged(final Rect rect, float n, int backgroundColor) {
        if (!DarkIconDispatcher.isInArea(rect, (View)this)) {
            n = 0.0f;
        }
        this.mNonAdaptedSingleToneColor = this.mDualToneHandler.getSingleColor(n);
        this.mNonAdaptedForegroundColor = this.mDualToneHandler.getFillColor(n);
        backgroundColor = this.mDualToneHandler.getBackgroundColor(n);
        this.mNonAdaptedBackgroundColor = backgroundColor;
        if (!this.mUseWallpaperTextColors) {
            this.updateColors(this.mNonAdaptedForegroundColor, backgroundColor, this.mNonAdaptedSingleToneColor);
        }
    }
    
    public void onDensityOrFontScaleChanged() {
        this.scaleBatteryMeterViews();
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mUserTracker.stopTracking();
        this.mBatteryController.removeCallback((BatteryController.BatteryStateChangeCallback)this);
        this.getContext().getContentResolver().unregisterContentObserver((ContentObserver)this.mSettingObserver);
        this.unsubscribeFromTunerUpdates();
    }
    
    public void onPowerSaveChanged(final boolean powerSaveEnabled) {
        this.mDrawable.setPowerSaveEnabled(powerSaveEnabled);
    }
    
    public void onTuningChanged(final String anObject, final String s) {
        if ("icon_blacklist".equals(anObject)) {
            int visibility;
            if (StatusBarIconController.getIconBlacklist(this.getContext(), s).contains((Object)this.mSlotBattery)) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            this.setVisibility(visibility);
        }
    }
    
    public void setColorsFromContext(final Context colorsFromContext) {
        if (colorsFromContext == null) {
            return;
        }
        this.mDualToneHandler.setColorsFromContext(colorsFromContext);
    }
    
    public void setForceShowPercent(final boolean percentShowMode) {
        this.setPercentShowMode(percentShowMode ? 1 : 0);
    }
    
    public void setIgnoreTunerUpdates(final boolean mIgnoreTunerUpdates) {
        this.mIgnoreTunerUpdates = mIgnoreTunerUpdates;
        this.updateTunerSubscription();
    }
    
    public void setPercentShowMode(final int mShowPercentMode) {
        this.mShowPercentMode = mShowPercentMode;
        this.updateShowPercent();
    }
    
    public void updatePercentView() {
        final TextView mBatteryPercentView = this.mBatteryPercentView;
        if (mBatteryPercentView != null) {
            this.removeView((View)mBatteryPercentView);
            this.mBatteryPercentView = null;
        }
        this.updateShowPercent();
    }
    
    private final class SettingObserver extends ContentObserver
    {
        public SettingObserver(final Handler handler) {
            super(handler);
        }
        
        public void onChange(final boolean b, final Uri uri) {
            super.onChange(b, uri);
            BatteryMeterView.this.updateShowPercent();
            if (TextUtils.equals((CharSequence)uri.getLastPathSegment(), (CharSequence)"battery_estimates_last_update_time")) {
                BatteryMeterView.this.updatePercentText();
            }
        }
    }
}
