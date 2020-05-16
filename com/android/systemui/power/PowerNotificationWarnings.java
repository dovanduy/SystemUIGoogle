// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.app.Notification$Style;
import android.app.Notification$BigTextStyle;
import java.text.NumberFormat;
import com.android.settingslib.Utils;
import java.io.PrintWriter;
import com.android.systemui.R$style;
import android.text.method.LinkMovementMethod;
import android.app.Notification;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.SystemUI;
import com.android.systemui.R$drawable;
import android.app.Notification$Builder;
import com.android.systemui.util.NotificationChannels;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import android.app.PendingIntent;
import com.android.systemui.volume.Events;
import com.android.systemui.Dependency;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.provider.Settings$Global;
import android.content.DialogInterface;
import java.util.Objects;
import java.util.Locale;
import com.android.settingslib.utils.PowerUtil;
import android.text.TextPaint;
import android.content.ActivityNotFoundException;
import android.util.Log;
import android.net.Uri;
import android.view.View;
import android.text.style.URLSpan;
import android.text.Annotation;
import android.text.SpannableStringBuilder;
import android.text.SpannableString;
import android.text.TextUtils;
import com.android.systemui.R$string;
import android.util.Slog;
import android.os.UserHandle;
import android.os.Bundle;
import android.os.Looper;
import android.media.AudioAttributes$Builder;
import android.os.PowerManager;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.KeyguardManager;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.plugins.ActivityStarter;

public class PowerNotificationWarnings implements WarningsUI
{
    private static final boolean DEBUG;
    private static final String[] SHOWING_STRINGS;
    private ActivityStarter mActivityStarter;
    private int mBatteryLevel;
    private int mBucket;
    private final Context mContext;
    private BatteryStateSnapshot mCurrentBatterySnapshot;
    private final Handler mHandler;
    private SystemUIDialog mHighTempDialog;
    private boolean mHighTempWarning;
    private boolean mInvalidCharger;
    private final KeyguardManager mKeyguard;
    private final NotificationManager mNoMan;
    private final Intent mOpenBatterySettings;
    private boolean mPlaySound;
    private final PowerManager mPowerMan;
    private final Receiver mReceiver;
    private SystemUIDialog mSaverConfirmation;
    private boolean mShowAutoSaverSuggestion;
    private int mShowing;
    private SystemUIDialog mThermalShutdownDialog;
    SystemUIDialog mUsbHighTempDialog;
    private boolean mWarning;
    private long mWarningTriggerTimeMs;
    
    static {
        DEBUG = PowerUI.DEBUG;
        SHOWING_STRINGS = new String[] { "SHOWING_NOTHING", "SHOWING_WARNING", "SHOWING_SAVER", "SHOWING_INVALID_CHARGER", "SHOWING_AUTO_SAVER_SUGGESTION" };
        new AudioAttributes$Builder().setContentType(4).setUsage(13).build();
    }
    
    public PowerNotificationWarnings(final Context mContext, final ActivityStarter mActivityStarter) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mReceiver = new Receiver();
        this.mOpenBatterySettings = settings("android.intent.action.POWER_USAGE_SUMMARY");
        this.mContext = mContext;
        this.mNoMan = (NotificationManager)mContext.getSystemService((Class)NotificationManager.class);
        this.mPowerMan = (PowerManager)mContext.getSystemService("power");
        this.mKeyguard = (KeyguardManager)this.mContext.getSystemService((Class)KeyguardManager.class);
        this.mReceiver.init();
        this.mActivityStarter = mActivityStarter;
    }
    
    private void dismissAutoSaverSuggestion() {
        this.mShowAutoSaverSuggestion = false;
        this.updateNotification();
    }
    
    private void dismissHighTemperatureWarningInternal() {
        this.mNoMan.cancelAsUser("high_temp", 4, UserHandle.ALL);
        this.mHighTempWarning = false;
    }
    
    private void dismissInvalidChargerNotification() {
        if (this.mInvalidCharger) {
            Slog.i("PowerUI.Notification", "dismissing invalid charger notification");
        }
        this.mInvalidCharger = false;
        this.updateNotification();
    }
    
    private void dismissLowBatteryNotification() {
        if (this.mWarning) {
            Slog.i("PowerUI.Notification", "dismissing low battery notification");
        }
        this.mWarning = false;
        this.updateNotification();
    }
    
    private CharSequence getBatterySaverDescription() {
        final String string = this.mContext.getText(R$string.help_uri_battery_saver_learn_more_link_target).toString();
        if (TextUtils.isEmpty((CharSequence)string)) {
            return this.mContext.getText(17039759);
        }
        final SpannableString spannableString = new SpannableString(this.mContext.getText(17039760));
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder((CharSequence)spannableString);
        final int length = spannableString.length();
        int i = 0;
        for (Annotation[] array = (Annotation[])spannableString.getSpans(0, length, (Class)Annotation.class); i < array.length; ++i) {
            final Annotation annotation = array[i];
            if ("url".equals(annotation.getValue())) {
                final int spanStart = spannableString.getSpanStart((Object)annotation);
                final int spanEnd = spannableString.getSpanEnd((Object)annotation);
                final URLSpan urlSpan = new URLSpan(string) {
                    public void onClick(View setFlags) {
                        if (PowerNotificationWarnings.this.mSaverConfirmation != null) {
                            PowerNotificationWarnings.this.mSaverConfirmation.dismiss();
                        }
                        PowerNotificationWarnings.this.mContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS").setFlags(268435456));
                        final Uri parse = Uri.parse(this.getURL());
                        final Context context = setFlags.getContext();
                        setFlags = (View)new Intent("android.intent.action.VIEW", parse).setFlags(268435456);
                        try {
                            context.startActivity((Intent)setFlags);
                        }
                        catch (ActivityNotFoundException ex) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Activity was not found for intent, ");
                            sb.append(((Intent)setFlags).toString());
                            Log.w("PowerUI.Notification", sb.toString());
                        }
                    }
                    
                    public void updateDrawState(final TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        textPaint.setUnderlineText(false);
                    }
                };
                spannableStringBuilder.setSpan((Object)urlSpan, spanStart, spanEnd, spannableString.getSpanFlags((Object)urlSpan));
            }
        }
        return (CharSequence)spannableStringBuilder;
    }
    
    private String getHybridContentString(final String s) {
        return PowerUtil.getBatteryRemainingStringFormatted(this.mContext, this.mCurrentBatterySnapshot.getTimeRemainingMillis(), s, this.mCurrentBatterySnapshot.isBasedOnUsage());
    }
    
    private boolean hasBatterySettings() {
        return this.mOpenBatterySettings.resolveActivity(this.mContext.getPackageManager()) != null;
    }
    
    private boolean isEnglishLocale() {
        return Objects.equals(Locale.getDefault().getLanguage(), Locale.ENGLISH.getLanguage());
    }
    
    private PendingIntent pendingBroadcast(final String s) {
        return PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent(s).setPackage(this.mContext.getPackageName()).setFlags(268435456), 0, UserHandle.CURRENT);
    }
    
    private void setSaverMode(final boolean b, final boolean b2) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, b, b2);
    }
    
    private static Intent settings(final String s) {
        return new Intent(s).setFlags(1551892480);
    }
    
    private void showAutoSaverSuggestion() {
        this.mShowAutoSaverSuggestion = true;
        this.updateNotification();
    }
    
    private void showAutoSaverSuggestionNotification() {
        final Notification$Builder setContentText = new Notification$Builder(this.mContext, NotificationChannels.HINTS).setSmallIcon(R$drawable.ic_power_saver).setWhen(0L).setShowWhen(false).setContentTitle((CharSequence)this.mContext.getString(R$string.auto_saver_title)).setContentText((CharSequence)this.mContext.getString(R$string.auto_saver_text));
        setContentText.setContentIntent(this.pendingBroadcast("PNW.enableAutoSaver"));
        setContentText.setDeleteIntent(this.pendingBroadcast("PNW.dismissAutoSaverSuggestion"));
        setContentText.addAction(0, (CharSequence)this.mContext.getString(R$string.no_auto_saver_action), this.pendingBroadcast("PNW.autoSaverNoThanks"));
        SystemUI.overrideNotificationAppName(this.mContext, setContentText, false);
        this.mNoMan.notifyAsUser("auto_saver", 49, setContentText.build(), UserHandle.ALL);
    }
    
    private void showHighTemperatureDialog() {
        if (this.mHighTempDialog != null) {
            return;
        }
        final SystemUIDialog mHighTempDialog = new SystemUIDialog(this.mContext);
        mHighTempDialog.setIconAttribute(16843605);
        mHighTempDialog.setTitle(R$string.high_temp_title);
        mHighTempDialog.setMessage(R$string.high_temp_dialog_message);
        mHighTempDialog.setPositiveButton(17039370, null);
        mHighTempDialog.setShowForAllUsers(true);
        mHighTempDialog.setOnDismissListener((DialogInterface$OnDismissListener)new _$$Lambda$PowerNotificationWarnings$PU_JpsxNcz7jXGNa_DRkuMbEWwU(this));
        mHighTempDialog.show();
        this.mHighTempDialog = mHighTempDialog;
    }
    
    private void showInvalidChargerNotification() {
        final Notification$Builder setColor = new Notification$Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R$drawable.ic_power_low).setWhen(0L).setShowWhen(false).setOngoing(true).setContentTitle((CharSequence)this.mContext.getString(R$string.invalid_charger_title)).setContentText((CharSequence)this.mContext.getString(R$string.invalid_charger_text)).setColor(this.mContext.getColor(17170460));
        SystemUI.overrideNotificationAppName(this.mContext, setColor, false);
        final Notification build = setColor.build();
        this.mNoMan.cancelAsUser("low_battery", 3, UserHandle.ALL);
        this.mNoMan.notifyAsUser("low_battery", 2, build, UserHandle.ALL);
    }
    
    private void showStartSaverConfirmation(final Bundle bundle) {
        if (this.mSaverConfirmation != null) {
            return;
        }
        final SystemUIDialog mSaverConfirmation = new SystemUIDialog(this.mContext);
        final boolean boolean1 = bundle.getBoolean("extra_confirm_only");
        final int int1 = bundle.getInt("extra_power_save_mode_trigger", 0);
        final int int2 = bundle.getInt("extra_power_save_mode_trigger_level", 0);
        mSaverConfirmation.setMessage(this.getBatterySaverDescription());
        if (this.isEnglishLocale()) {
            mSaverConfirmation.setMessageHyphenationFrequency(0);
        }
        mSaverConfirmation.setMessageMovementMethod(LinkMovementMethod.getInstance());
        if (boolean1) {
            mSaverConfirmation.setTitle(R$string.battery_saver_confirmation_title_generic);
            mSaverConfirmation.setPositiveButton(17039979, (DialogInterface$OnClickListener)new _$$Lambda$PowerNotificationWarnings$i9YMNbne4kaewl8DwiUWlEIhHLU(this, int1, int2));
        }
        else {
            mSaverConfirmation.setTitle(R$string.battery_saver_confirmation_title);
            mSaverConfirmation.setPositiveButton(R$string.battery_saver_confirmation_ok, (DialogInterface$OnClickListener)new _$$Lambda$PowerNotificationWarnings$Uf_fCz3D5JaMRKgj_soLcPpUL04(this));
            mSaverConfirmation.setNegativeButton(17039360, null);
        }
        mSaverConfirmation.setShowForAllUsers(true);
        mSaverConfirmation.setOnDismissListener((DialogInterface$OnDismissListener)new _$$Lambda$PowerNotificationWarnings$AE5LLn9E8Dx1b7_xgN4SxgDN7R4(this));
        mSaverConfirmation.show();
        this.mSaverConfirmation = mSaverConfirmation;
    }
    
    private void showThermalShutdownDialog() {
        if (this.mThermalShutdownDialog != null) {
            return;
        }
        final SystemUIDialog mThermalShutdownDialog = new SystemUIDialog(this.mContext);
        mThermalShutdownDialog.setIconAttribute(16843605);
        mThermalShutdownDialog.setTitle(R$string.thermal_shutdown_title);
        mThermalShutdownDialog.setMessage(R$string.thermal_shutdown_dialog_message);
        mThermalShutdownDialog.setPositiveButton(17039370, null);
        mThermalShutdownDialog.setShowForAllUsers(true);
        mThermalShutdownDialog.setOnDismissListener((DialogInterface$OnDismissListener)new _$$Lambda$PowerNotificationWarnings$O5nkGS5PG2ihQrXqunpOO_aZDms(this));
        mThermalShutdownDialog.show();
        this.mThermalShutdownDialog = mThermalShutdownDialog;
    }
    
    private void showUsbHighTemperatureAlarmInternal() {
        if (this.mUsbHighTempDialog != null) {
            return;
        }
        final SystemUIDialog mUsbHighTempDialog = new SystemUIDialog(this.mContext, R$style.Theme_SystemUI_Dialog_Alert);
        mUsbHighTempDialog.setCancelable(false);
        mUsbHighTempDialog.setIconAttribute(16843605);
        mUsbHighTempDialog.setTitle(R$string.high_temp_alarm_title);
        mUsbHighTempDialog.setShowForAllUsers(true);
        mUsbHighTempDialog.setMessage((CharSequence)this.mContext.getString(R$string.high_temp_alarm_notify_message, new Object[] { "" }));
        mUsbHighTempDialog.setPositiveButton(17039370, (DialogInterface$OnClickListener)new _$$Lambda$PowerNotificationWarnings$wL6F1WmvK9p9dyYXQnu9ScZBxSA(this));
        mUsbHighTempDialog.setNegativeButton(R$string.high_temp_alarm_help_care_steps, (DialogInterface$OnClickListener)new _$$Lambda$PowerNotificationWarnings$dkzsXROJlAvy2zSj_OYf_kxpKFc(this));
        mUsbHighTempDialog.setOnDismissListener((DialogInterface$OnDismissListener)new _$$Lambda$PowerNotificationWarnings$_C7Tc72CcSASuMrkeFnJC4Oj07o(this));
        mUsbHighTempDialog.getWindow().addFlags(2097280);
        mUsbHighTempDialog.show();
        this.mUsbHighTempDialog = mUsbHighTempDialog;
        Events.writeEvent(19, 3, this.mKeyguard.isKeyguardLocked());
    }
    
    private void startBatterySaverSchedulePage() {
        final Intent intent = new Intent("com.android.settings.BATTERY_SAVER_SCHEDULE_SETTINGS");
        intent.setFlags(268468224);
        this.mActivityStarter.startActivity(intent, true);
    }
    
    private void updateNotification() {
        if (PowerNotificationWarnings.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("updateNotification mWarning=");
            sb.append(this.mWarning);
            sb.append(" mPlaySound=");
            sb.append(this.mPlaySound);
            sb.append(" mInvalidCharger=");
            sb.append(this.mInvalidCharger);
            Slog.d("PowerUI.Notification", sb.toString());
        }
        if (this.mInvalidCharger) {
            this.showInvalidChargerNotification();
            this.mShowing = 3;
        }
        else if (this.mWarning) {
            this.showWarningNotification();
            this.mShowing = 1;
        }
        else if (this.mShowAutoSaverSuggestion) {
            if (this.mShowing != 4) {
                this.showAutoSaverSuggestionNotification();
            }
            this.mShowing = 4;
        }
        else {
            this.mNoMan.cancelAsUser("low_battery", 2, UserHandle.ALL);
            this.mNoMan.cancelAsUser("low_battery", 3, UserHandle.ALL);
            this.mNoMan.cancelAsUser("auto_saver", 49, UserHandle.ALL);
            this.mShowing = 0;
        }
    }
    
    @Override
    public void dismissHighTemperatureWarning() {
        if (!this.mHighTempWarning) {
            return;
        }
        this.dismissHighTemperatureWarningInternal();
    }
    
    @Override
    public void dismissInvalidChargerWarning() {
        this.dismissInvalidChargerNotification();
    }
    
    @Override
    public void dismissLowBatteryWarning() {
        if (PowerNotificationWarnings.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("dismissing low battery warning: level=");
            sb.append(this.mBatteryLevel);
            Slog.d("PowerUI.Notification", sb.toString());
        }
        this.dismissLowBatteryNotification();
    }
    
    void dismissThermalShutdownWarning() {
        this.mNoMan.cancelAsUser("high_temp", 39, UserHandle.ALL);
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        printWriter.print("mWarning=");
        printWriter.println(this.mWarning);
        printWriter.print("mPlaySound=");
        printWriter.println(this.mPlaySound);
        printWriter.print("mInvalidCharger=");
        printWriter.println(this.mInvalidCharger);
        printWriter.print("mShowing=");
        printWriter.println(PowerNotificationWarnings.SHOWING_STRINGS[this.mShowing]);
        printWriter.print("mSaverConfirmation=");
        final SystemUIDialog mSaverConfirmation = this.mSaverConfirmation;
        final String s = "not null";
        String x;
        if (mSaverConfirmation != null) {
            x = "not null";
        }
        else {
            x = null;
        }
        printWriter.println(x);
        printWriter.print("mSaverEnabledConfirmation=");
        printWriter.print("mHighTempWarning=");
        printWriter.println(this.mHighTempWarning);
        printWriter.print("mHighTempDialog=");
        String x2;
        if (this.mHighTempDialog != null) {
            x2 = "not null";
        }
        else {
            x2 = null;
        }
        printWriter.println(x2);
        printWriter.print("mThermalShutdownDialog=");
        String x3;
        if (this.mThermalShutdownDialog != null) {
            x3 = "not null";
        }
        else {
            x3 = null;
        }
        printWriter.println(x3);
        printWriter.print("mUsbHighTempDialog=");
        String x4;
        if (this.mUsbHighTempDialog != null) {
            x4 = s;
        }
        else {
            x4 = null;
        }
        printWriter.println(x4);
    }
    
    @Override
    public boolean isInvalidChargerWarningShowing() {
        return this.mInvalidCharger;
    }
    
    @Override
    public void showHighTemperatureWarning() {
        if (this.mHighTempWarning) {
            return;
        }
        this.mHighTempWarning = true;
        final Notification$Builder setColor = new Notification$Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R$drawable.ic_device_thermostat_24).setWhen(0L).setShowWhen(false).setContentTitle((CharSequence)this.mContext.getString(R$string.high_temp_title)).setContentText((CharSequence)this.mContext.getString(R$string.high_temp_notif_message)).setVisibility(1).setContentIntent(this.pendingBroadcast("PNW.clickedTempWarning")).setDeleteIntent(this.pendingBroadcast("PNW.dismissedTempWarning")).setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        SystemUI.overrideNotificationAppName(this.mContext, setColor, false);
        this.mNoMan.notifyAsUser("high_temp", 4, setColor.build(), UserHandle.ALL);
    }
    
    @Override
    public void showInvalidChargerWarning() {
        this.mInvalidCharger = true;
        this.updateNotification();
    }
    
    @Override
    public void showLowBatteryWarning(final boolean b) {
        final StringBuilder sb = new StringBuilder();
        sb.append("show low battery warning: level=");
        sb.append(this.mBatteryLevel);
        sb.append(" [");
        sb.append(this.mBucket);
        sb.append("] playSound=");
        sb.append(b);
        Slog.i("PowerUI.Notification", sb.toString());
        this.mPlaySound = b;
        this.mWarning = true;
        this.updateNotification();
    }
    
    @Override
    public void showThermalShutdownWarning() {
        final Notification$Builder setColor = new Notification$Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R$drawable.ic_device_thermostat_24).setWhen(0L).setShowWhen(false).setContentTitle((CharSequence)this.mContext.getString(R$string.thermal_shutdown_title)).setContentText((CharSequence)this.mContext.getString(R$string.thermal_shutdown_message)).setVisibility(1).setContentIntent(this.pendingBroadcast("PNW.clickedThermalShutdownWarning")).setDeleteIntent(this.pendingBroadcast("PNW.dismissedThermalShutdownWarning")).setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        SystemUI.overrideNotificationAppName(this.mContext, setColor, false);
        this.mNoMan.notifyAsUser("high_temp", 39, setColor.build(), UserHandle.ALL);
    }
    
    @Override
    public void showUsbHighTemperatureAlarm() {
        this.mHandler.post((Runnable)new _$$Lambda$PowerNotificationWarnings$BgW0sVGH4tN6GoBK_M1noXhk8wA(this));
    }
    
    protected void showWarningNotification() {
        final String format = NumberFormat.getPercentInstance().format(this.mCurrentBatterySnapshot.getBatteryLevel() / 100.0);
        final String string = this.mContext.getString(R$string.battery_low_title);
        String contentText;
        if (this.mCurrentBatterySnapshot.isHybrid()) {
            contentText = this.getHybridContentString(format);
        }
        else {
            contentText = this.mContext.getString(R$string.battery_low_percent_format, new Object[] { format });
        }
        final Notification$Builder setVisibility = new Notification$Builder(this.mContext, NotificationChannels.BATTERY).setSmallIcon(R$drawable.ic_power_low).setWhen(this.mWarningTriggerTimeMs).setShowWhen(false).setContentText((CharSequence)contentText).setContentTitle((CharSequence)string).setOnlyAlertOnce(true).setDeleteIntent(this.pendingBroadcast("PNW.dismissedWarning")).setStyle((Notification$Style)new Notification$BigTextStyle().bigText((CharSequence)contentText)).setVisibility(1);
        if (this.hasBatterySettings()) {
            setVisibility.setContentIntent(this.pendingBroadcast("PNW.batterySettings"));
        }
        if (!this.mCurrentBatterySnapshot.isHybrid() || this.mBucket < 0 || this.mCurrentBatterySnapshot.getTimeRemainingMillis() < this.mCurrentBatterySnapshot.getSevereThresholdMillis()) {
            setVisibility.setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        }
        if (!this.mPowerMan.isPowerSaveMode()) {
            setVisibility.addAction(0, (CharSequence)this.mContext.getString(R$string.battery_saver_start_action), this.pendingBroadcast("PNW.startSaver"));
        }
        setVisibility.setOnlyAlertOnce(this.mPlaySound ^ true);
        this.mPlaySound = false;
        SystemUI.overrideNotificationAppName(this.mContext, setVisibility, false);
        final Notification build = setVisibility.build();
        this.mNoMan.cancelAsUser("low_battery", 2, UserHandle.ALL);
        this.mNoMan.notifyAsUser("low_battery", 3, build, UserHandle.ALL);
    }
    
    @Override
    public void update(final int mBatteryLevel, final int mBucket, final long n) {
        this.mBatteryLevel = mBatteryLevel;
        if (mBucket >= 0) {
            this.mWarningTriggerTimeMs = 0L;
        }
        else if (mBucket < this.mBucket) {
            this.mWarningTriggerTimeMs = System.currentTimeMillis();
        }
        this.mBucket = mBucket;
    }
    
    @Override
    public void updateLowBatteryWarning() {
        this.updateNotification();
    }
    
    @Override
    public void updateSnapshot(final BatteryStateSnapshot mCurrentBatterySnapshot) {
        this.mCurrentBatterySnapshot = mCurrentBatterySnapshot;
    }
    
    @Override
    public void userSwitched() {
        this.updateNotification();
    }
    
    private final class Receiver extends BroadcastReceiver
    {
        public void init() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("PNW.batterySettings");
            intentFilter.addAction("PNW.startSaver");
            intentFilter.addAction("PNW.dismissedWarning");
            intentFilter.addAction("PNW.clickedTempWarning");
            intentFilter.addAction("PNW.dismissedTempWarning");
            intentFilter.addAction("PNW.clickedThermalShutdownWarning");
            intentFilter.addAction("PNW.dismissedThermalShutdownWarning");
            intentFilter.addAction("PNW.startSaverConfirmation");
            intentFilter.addAction("PNW.autoSaverSuggestion");
            intentFilter.addAction("PNW.enableAutoSaver");
            intentFilter.addAction("PNW.autoSaverNoThanks");
            intentFilter.addAction("PNW.dismissAutoSaverSuggestion");
            PowerNotificationWarnings.this.mContext.registerReceiverAsUser((BroadcastReceiver)this, UserHandle.ALL, intentFilter, "android.permission.DEVICE_POWER", PowerNotificationWarnings.this.mHandler);
        }
        
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            final StringBuilder sb = new StringBuilder();
            sb.append("Received ");
            sb.append(action);
            Slog.i("PowerUI.Notification", sb.toString());
            if (action.equals("PNW.batterySettings")) {
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
                PowerNotificationWarnings.this.mContext.startActivityAsUser(PowerNotificationWarnings.this.mOpenBatterySettings, UserHandle.CURRENT);
            }
            else if (action.equals("PNW.startSaver")) {
                PowerNotificationWarnings.this.setSaverMode(true, true);
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
            }
            else if (action.equals("PNW.startSaverConfirmation")) {
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
                PowerNotificationWarnings.this.showStartSaverConfirmation(intent.getExtras());
            }
            else if (action.equals("PNW.dismissedWarning")) {
                PowerNotificationWarnings.this.dismissLowBatteryWarning();
            }
            else if ("PNW.clickedTempWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissHighTemperatureWarningInternal();
                PowerNotificationWarnings.this.showHighTemperatureDialog();
            }
            else if ("PNW.dismissedTempWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissHighTemperatureWarningInternal();
            }
            else if ("PNW.clickedThermalShutdownWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissThermalShutdownWarning();
                PowerNotificationWarnings.this.showThermalShutdownDialog();
            }
            else if ("PNW.dismissedThermalShutdownWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissThermalShutdownWarning();
            }
            else if ("PNW.autoSaverSuggestion".equals(action)) {
                PowerNotificationWarnings.this.showAutoSaverSuggestion();
            }
            else if ("PNW.dismissAutoSaverSuggestion".equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
            }
            else if ("PNW.enableAutoSaver".equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
                PowerNotificationWarnings.this.startBatterySaverSchedulePage();
            }
            else if ("PNW.autoSaverNoThanks".equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
                BatterySaverUtils.suppressAutoBatterySaver(context);
            }
        }
    }
}
