// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.res.TypedArray;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import android.os.Parcelable;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$dimen;
import android.view.View;
import android.graphics.Rect;
import android.os.UserHandle;
import android.os.Bundle;
import android.content.IntentFilter;
import android.text.style.RelativeSizeSpan;
import android.text.SpannableStringBuilder;
import libcore.icu.LocaleData;
import android.text.format.DateFormat;
import com.android.systemui.R$styleable;
import com.android.systemui.Dependency;
import android.os.SystemClock;
import android.content.Intent;
import java.util.TimeZone;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Handler;
import java.util.Locale;
import android.content.BroadcastReceiver;
import com.android.systemui.settings.CurrentUserTracker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.DemoMode;
import android.widget.TextView;

public class Clock extends TextView implements DemoMode, Tunable, Callbacks, DarkReceiver, ConfigurationListener
{
    private final int mAmPmStyle;
    private boolean mAttached;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private Calendar mCalendar;
    private SimpleDateFormat mClockFormat;
    private String mClockFormatString;
    private boolean mClockVisibleByPolicy;
    private boolean mClockVisibleByUser;
    private final CommandQueue mCommandQueue;
    private SimpleDateFormat mContentDescriptionFormat;
    private int mCurrentUserId;
    private final CurrentUserTracker mCurrentUserTracker;
    private boolean mDemoMode;
    private final BroadcastReceiver mIntentReceiver;
    private Locale mLocale;
    private int mNonAdaptedColor;
    private final BroadcastReceiver mScreenReceiver;
    private final Runnable mSecondTick;
    private Handler mSecondsHandler;
    private final boolean mShowDark;
    private boolean mShowSeconds;
    private boolean mUseWallpaperTextColor;
    
    public Clock(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public Clock(final Context context, AttributeSet obtainStyledAttributes, final int n) {
        super(context, obtainStyledAttributes, n);
        this.mClockVisibleByPolicy = true;
        this.mClockVisibleByUser = true;
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final Handler handler = Clock.this.getHandler();
                if (handler == null) {
                    return;
                }
                final String action = intent.getAction();
                if (action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                    handler.post((Runnable)new _$$Lambda$Clock$2$NVwlBsd8V0hLupY9sb0smFA7zNw(this, intent.getStringExtra("time-zone")));
                }
                else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                    handler.post((Runnable)new _$$Lambda$Clock$2$BzKxslldgL1SP5a4jbR8GDSq90w(this, Clock.this.getResources().getConfiguration().locale));
                }
                handler.post((Runnable)new _$$Lambda$Clock$2$mOTwR4Tu5xrxBBIUbNE9701lx_4(this));
            }
        };
        this.mScreenReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    if (Clock.this.mSecondsHandler != null) {
                        Clock.this.mSecondsHandler.removeCallbacks(Clock.this.mSecondTick);
                    }
                }
                else if ("android.intent.action.SCREEN_ON".equals(action) && Clock.this.mSecondsHandler != null) {
                    Clock.this.mSecondsHandler.postAtTime(Clock.this.mSecondTick, SystemClock.uptimeMillis() / 1000L * 1000L + 1000L);
                }
            }
        };
        this.mSecondTick = new Runnable() {
            @Override
            public void run() {
                if (Clock.this.mCalendar != null) {
                    Clock.this.updateClock();
                }
                Clock.this.mSecondsHandler.postAtTime((Runnable)this, SystemClock.uptimeMillis() / 1000L * 1000L + 1000L);
            }
        };
        this.mCommandQueue = Dependency.get(CommandQueue.class);
        obtainStyledAttributes = (AttributeSet)context.getTheme().obtainStyledAttributes(obtainStyledAttributes, R$styleable.Clock, 0, 0);
        try {
            this.mAmPmStyle = ((TypedArray)obtainStyledAttributes).getInt(R$styleable.Clock_amPmStyle, 2);
            this.mShowDark = ((TypedArray)obtainStyledAttributes).getBoolean(R$styleable.Clock_showDark, true);
            this.mNonAdaptedColor = this.getCurrentTextColor();
            ((TypedArray)obtainStyledAttributes).recycle();
            final BroadcastDispatcher mBroadcastDispatcher = Dependency.get(BroadcastDispatcher.class);
            this.mBroadcastDispatcher = mBroadcastDispatcher;
            this.mCurrentUserTracker = new CurrentUserTracker(mBroadcastDispatcher) {
                @Override
                public void onUserSwitched(final int n) {
                    Clock.this.mCurrentUserId = n;
                }
            };
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private final CharSequence getSmallTime() {
        final Context context = this.getContext();
        final boolean is24HourFormat = DateFormat.is24HourFormat(context, this.mCurrentUserId);
        final LocaleData value = LocaleData.get(context.getResources().getConfiguration().locale);
        String pattern;
        if (this.mShowSeconds) {
            if (is24HourFormat) {
                pattern = value.timeFormat_Hms;
            }
            else {
                pattern = value.timeFormat_hms;
            }
        }
        else if (is24HourFormat) {
            pattern = value.timeFormat_Hm;
        }
        else {
            pattern = value.timeFormat_hm;
        }
        SimpleDateFormat mClockFormat;
        if (!pattern.equals(this.mClockFormatString)) {
            this.mContentDescriptionFormat = new SimpleDateFormat(pattern);
            String string = pattern;
            Label_0284: {
                if (this.mAmPmStyle != 0) {
                    int n;
                    int i = n = 0;
                    while (true) {
                        while (i < pattern.length()) {
                            final char char1 = pattern.charAt(i);
                            int n2 = n;
                            if (char1 == '\'') {
                                n2 = (n ^ 0x1);
                            }
                            if (n2 == 0 && char1 == 'a') {
                                string = pattern;
                                if (i >= 0) {
                                    int n3;
                                    for (n3 = i; n3 > 0 && Character.isWhitespace(pattern.charAt(n3 - 1)); --n3) {}
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append(pattern.substring(0, n3));
                                    sb.append('\uef00');
                                    sb.append(pattern.substring(n3, i));
                                    sb.append("a");
                                    sb.append('\uef01');
                                    sb.append(pattern.substring(i + 1));
                                    string = sb.toString();
                                }
                                break Label_0284;
                            }
                            else {
                                ++i;
                                n = n2;
                            }
                        }
                        i = -1;
                        continue;
                    }
                }
            }
            mClockFormat = new SimpleDateFormat(string);
            this.mClockFormat = mClockFormat;
            this.mClockFormatString = string;
        }
        else {
            mClockFormat = this.mClockFormat;
        }
        final String format = mClockFormat.format(this.mCalendar.getTime());
        if (this.mAmPmStyle != 0) {
            final int index = format.indexOf(61184);
            final int index2 = format.indexOf(61185);
            if (index >= 0 && index2 > index) {
                final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder((CharSequence)format);
                final int mAmPmStyle = this.mAmPmStyle;
                if (mAmPmStyle == 2) {
                    spannableStringBuilder.delete(index, index2 + 1);
                }
                else {
                    if (mAmPmStyle == 1) {
                        spannableStringBuilder.setSpan((Object)new RelativeSizeSpan(0.7f), index, index2, 34);
                    }
                    spannableStringBuilder.delete(index2, index2 + 1);
                    spannableStringBuilder.delete(index, index + 1);
                }
                return (CharSequence)spannableStringBuilder;
            }
        }
        return format;
    }
    
    private boolean shouldBeVisible() {
        return this.mClockVisibleByPolicy && this.mClockVisibleByUser;
    }
    
    private void updateClockVisibility() {
        int visibility;
        if (this.shouldBeVisible()) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        super.setVisibility(visibility);
    }
    
    private void updateShowSeconds() {
        if (this.mShowSeconds) {
            if (this.mSecondsHandler == null && this.getDisplay() != null) {
                this.mSecondsHandler = new Handler();
                if (this.getDisplay().getState() == 2) {
                    this.mSecondsHandler.postAtTime(this.mSecondTick, SystemClock.uptimeMillis() / 1000L * 1000L + 1000L);
                }
                final IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
                intentFilter.addAction("android.intent.action.SCREEN_ON");
                this.mBroadcastDispatcher.registerReceiver(this.mScreenReceiver, intentFilter);
            }
        }
        else if (this.mSecondsHandler != null) {
            this.mBroadcastDispatcher.unregisterReceiver(this.mScreenReceiver);
            this.mSecondsHandler.removeCallbacks(this.mSecondTick);
            this.mSecondsHandler = null;
            this.updateClock();
        }
    }
    
    public void disable(final int n, final int n2, final int n3, final boolean b) {
        if (n != this.getDisplay().getDisplayId()) {
            return;
        }
        final boolean clockVisibilityByPolicy = (0x800000 & n2) == 0x0;
        if (clockVisibilityByPolicy != this.mClockVisibleByPolicy) {
            this.setClockVisibilityByPolicy(clockVisibilityByPolicy);
        }
    }
    
    public void dispatchDemoCommand(String string, final Bundle bundle) {
        if (!this.mDemoMode && string.equals("enter")) {
            this.mDemoMode = true;
        }
        else if (this.mDemoMode && string.equals("exit")) {
            this.mDemoMode = false;
            this.updateClock();
        }
        else if (this.mDemoMode && string.equals("clock")) {
            string = bundle.getString("millis");
            final String string2 = bundle.getString("hhmm");
            if (string != null) {
                this.mCalendar.setTimeInMillis(Long.parseLong(string));
            }
            else if (string2 != null && string2.length() == 4) {
                final int int1 = Integer.parseInt(string2.substring(0, 2));
                final int int2 = Integer.parseInt(string2.substring(2));
                if (DateFormat.is24HourFormat(this.getContext(), this.mCurrentUserId)) {
                    this.mCalendar.set(11, int1);
                }
                else {
                    this.mCalendar.set(10, int1);
                }
                this.mCalendar.set(12, int2);
            }
            this.setText(this.getSmallTime());
            this.setContentDescription((CharSequence)this.mContentDescriptionFormat.format(this.mCalendar.getTime()));
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mAttached) {
            this.mAttached = true;
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_TICK");
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            this.mBroadcastDispatcher.registerReceiverWithHandler(this.mIntentReceiver, intentFilter, Dependency.get(Dependency.TIME_TICK_HANDLER), UserHandle.ALL);
            Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "clock_seconds", "icon_blacklist");
            this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
            if (this.mShowDark) {
                Dependency.get(DarkIconDispatcher.class).addDarkReceiver((DarkIconDispatcher.DarkReceiver)this);
            }
            this.mCurrentUserTracker.startTracking();
            this.mCurrentUserId = this.mCurrentUserTracker.getCurrentUserId();
        }
        this.mCalendar = Calendar.getInstance(TimeZone.getDefault());
        this.mClockFormatString = "";
        this.updateClock();
        this.updateClockVisibility();
        this.updateShowSeconds();
    }
    
    public void onDarkChanged(final Rect rect, final float n, int tint) {
        tint = DarkIconDispatcher.getTint(rect, (View)this, tint);
        this.mNonAdaptedColor = tint;
        if (!this.mUseWallpaperTextColor) {
            this.setTextColor(tint);
        }
    }
    
    public void onDensityOrFontScaleChanged() {
        FontSizeUtils.updateFontSize(this, R$dimen.status_bar_clock_size);
        this.setPaddingRelative(super.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_clock_starting_padding), 0, super.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_clock_end_padding), 0);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            this.mBroadcastDispatcher.unregisterReceiver(this.mIntentReceiver);
            this.mAttached = false;
            Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
            this.mCommandQueue.removeCallback((CommandQueue.Callbacks)this);
            if (this.mShowDark) {
                Dependency.get(DarkIconDispatcher.class).removeDarkReceiver((DarkIconDispatcher.DarkReceiver)this);
            }
            this.mCurrentUserTracker.stopTracking();
        }
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable instanceof Bundle) {
            final Bundle bundle = (Bundle)parcelable;
            super.onRestoreInstanceState(bundle.getParcelable("clock_super_parcelable"));
            if (bundle.containsKey("current_user_id")) {
                this.mCurrentUserId = bundle.getInt("current_user_id");
            }
            this.mClockVisibleByPolicy = bundle.getBoolean("visible_by_policy", true);
            this.mClockVisibleByUser = bundle.getBoolean("visible_by_user", true);
            this.mShowSeconds = bundle.getBoolean("show_seconds", false);
            if (bundle.containsKey("visibility")) {
                super.setVisibility(bundle.getInt("visibility"));
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("clock_super_parcelable", super.onSaveInstanceState());
        bundle.putInt("current_user_id", this.mCurrentUserId);
        bundle.putBoolean("visible_by_policy", this.mClockVisibleByPolicy);
        bundle.putBoolean("visible_by_user", this.mClockVisibleByUser);
        bundle.putBoolean("show_seconds", this.mShowSeconds);
        bundle.putInt("visibility", this.getVisibility());
        return (Parcelable)bundle;
    }
    
    public void onTuningChanged(final String anObject, final String s) {
        if ("clock_seconds".equals(anObject)) {
            this.mShowSeconds = TunerService.parseIntegerSwitch(s, false);
            this.updateShowSeconds();
        }
        else {
            this.setClockVisibleByUser(StatusBarIconController.getIconBlacklist(this.getContext(), s).contains((Object)"clock") ^ true);
            this.updateClockVisibility();
        }
    }
    
    public void setClockVisibilityByPolicy(final boolean mClockVisibleByPolicy) {
        this.mClockVisibleByPolicy = mClockVisibleByPolicy;
        this.updateClockVisibility();
    }
    
    public void setClockVisibleByUser(final boolean mClockVisibleByUser) {
        this.mClockVisibleByUser = mClockVisibleByUser;
        this.updateClockVisibility();
    }
    
    public void setVisibility(final int visibility) {
        if (visibility == 0 && !this.shouldBeVisible()) {
            return;
        }
        super.setVisibility(visibility);
    }
    
    final void updateClock() {
        if (this.mDemoMode) {
            return;
        }
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        this.setText(this.getSmallTime());
        this.setContentDescription((CharSequence)this.mContentDescriptionFormat.format(this.mCalendar.getTime()));
    }
    
    public void useWallpaperTextColor(final boolean mUseWallpaperTextColor) {
        if (mUseWallpaperTextColor == this.mUseWallpaperTextColor) {
            return;
        }
        this.mUseWallpaperTextColor = mUseWallpaperTextColor;
        if (mUseWallpaperTextColor) {
            this.setTextColor(Utils.getColorAttr(super.mContext, R$attr.wallpaperTextColor));
        }
        else {
            this.setTextColor(this.mNonAdaptedColor);
        }
    }
}
