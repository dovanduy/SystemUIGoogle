// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.statusbar.policy;

import android.app.Notification;
import android.os.UserHandle;
import android.app.Notification$Style;
import android.app.Notification$BigTextStyle;
import android.app.PendingIntent;
import com.android.systemui.R$drawable;
import android.app.Notification$Builder;
import android.content.Intent;
import android.app.NotificationChannel;
import android.os.SystemClock;
import com.android.systemui.R$string;
import android.os.Bundle;
import android.provider.Settings$Global;
import android.util.Log;
import android.text.TextUtils;
import android.os.SystemProperties;
import java.util.Collection;
import com.android.systemui.statusbar.policy.BatteryController;
import java.util.ArrayList;
import android.os.Handler;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.PowerManager;
import com.android.systemui.power.EnhancedEstimates;
import android.content.Context;
import android.app.AlarmManager$OnAlarmListener;
import com.google.android.systemui.reversecharging.ReverseWirelessCharger;
import java.util.Optional;
import android.content.SharedPreferences;
import android.app.NotificationManager;
import android.app.AlarmManager;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;

public class BatteryControllerImplGoogle extends BatteryControllerImpl
{
    static final String CHANNEL_NAME = "reverseChargingNotificationChannel";
    static final int NOTIFICATION_ID = 1234;
    static final String REVERSE_CHARGING_NTF_FIRST_LAUNCH_PREF = "reverse_charging_ntf_first_launch";
    private final AlarmManager mAlarmManager;
    int mCurrentRtxMode;
    private boolean mIsReverseSupported;
    private boolean mIsReverseSupportedMethodInvoked;
    private String mName;
    private final NotificationManager mNtfManager;
    private final SharedPreferences mPrefs;
    boolean mReverse;
    private final Optional<ReverseWirelessCharger> mRtxChargerManager;
    private final AlarmManager$OnAlarmListener mRtxFinishAlarmAction;
    private final AlarmManager$OnAlarmListener mRtxStartAlarmAction;
    private int mRxLevel;
    
    BatteryControllerImplGoogle(final Optional<ReverseWirelessCharger> mRtxChargerManager, final AlarmManager mAlarmManager, final Context context, final EnhancedEstimates enhancedEstimates, final PowerManager powerManager, final BroadcastDispatcher broadcastDispatcher, final Handler handler, final Handler handler2, final NotificationManager mNtfManager, final SharedPreferences mPrefs) {
        super(context, enhancedEstimates, powerManager, broadcastDispatcher, handler, handler2);
        this.mCurrentRtxMode = 0;
        this.mRtxFinishAlarmAction = (AlarmManager$OnAlarmListener)new _$$Lambda$BatteryControllerImplGoogle$PBp2r_JWX_MUITVYr5CNQBvGkWU(this);
        this.mRtxStartAlarmAction = (AlarmManager$OnAlarmListener)new _$$Lambda$BatteryControllerImplGoogle$VxrU3Jq1eO2FF8pPEXF6rT3vXco(this);
        this.mRtxChargerManager = mRtxChargerManager;
        this.mAlarmManager = mAlarmManager;
        this.mNtfManager = mNtfManager;
        this.mPrefs = mPrefs;
        this.resetReverseInfo();
        if (this.mRtxChargerManager.isPresent()) {
            this.mRtxChargerManager.get().addReverseChargingChangeListener((ReverseWirelessCharger.ReverseChargingChangeListener)new _$$Lambda$BatteryControllerImplGoogle$Qc3Ej5oaEX1DIEPUkjwVcY6W5Ig(this));
        }
    }
    
    private void cancelRtxTimer(final int n) {
        if (n != 0) {
            if (n == 1) {
                this.mAlarmManager.cancel(this.mRtxStartAlarmAction);
            }
        }
        else {
            this.mAlarmManager.cancel(this.mRtxFinishAlarmAction);
        }
    }
    
    private void fireReverseChanged() {
        synchronized (super.mChangeCallbacks) {
            final ArrayList<BatteryStateChangeCallback> list = new ArrayList<BatteryStateChangeCallback>(super.mChangeCallbacks);
            for (int size = list.size(), i = 0; i < size; ++i) {
                list.get(i).onReverseChanged(this.mReverse, this.mRxLevel, this.mName);
            }
        }
    }
    
    private long getRtxTimeOut() {
        final String value = SystemProperties.get("rtx.timeout");
        if (!TextUtils.isEmpty((CharSequence)value)) {
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException obj) {
                final StringBuilder sb = new StringBuilder();
                sb.append("getRtxTimeOut(): invalid timeout, ");
                sb.append(obj);
                Log.w("BatteryControllerImplGoogle", sb.toString());
            }
        }
        return 180000L;
    }
    
    private boolean isLowBattery() {
        final int i = Settings$Global.getInt(super.mContext.getContentResolver(), "advanced_battery_usage_amount", 2) * 5;
        if (super.mLevel < i) {
            final StringBuilder sb = new StringBuilder();
            sb.append("The battery is lower than threshold turn off reverse charging ! level : ");
            sb.append(super.mLevel);
            sb.append(", threshold : ");
            sb.append(i);
            Log.w("BatteryControllerImplGoogle", sb.toString());
            return true;
        }
        return false;
    }
    
    private void onAlarmRtxFinish() {
        if (this.mReverse) {
            this.setReverseState(this.mReverse = false);
        }
    }
    
    private void onAlarmRtxStart() {
        if (!this.mReverse) {
            this.setReverseState(this.mReverse = true);
        }
    }
    
    private void onReverseStateChanged(final Bundle bundle) {
        if (!this.isReverseSupported()) {
            return;
        }
        final int int1 = bundle.getInt("key_rtx_mode");
        this.showNotificationIfNecessary(int1, bundle.getInt("key_reason_type"));
        this.mCurrentRtxMode = int1;
        this.resetReverseInfo();
        if (int1 == 1) {
            this.mReverse = true;
            if (!bundle.getBoolean("key_rtx_connection")) {
                this.mRxLevel = -1;
            }
            else {
                this.mRxLevel = bundle.getInt("key_rtx_level");
            }
        }
        this.fireReverseChanged();
        this.cancelRtxTimer(0);
        if (this.mReverse && this.mRxLevel == -1) {
            this.setRtxTimer(0, this.getRtxTimeOut());
        }
    }
    
    private void resetReverseInfo() {
        this.mReverse = false;
        this.mRxLevel = -1;
        this.mName = null;
    }
    
    private void sendErrorNotification() {
        this.showNotification(super.mContext.getString(R$string.reverse_charging_off_notification_title), super.mContext.getString(R$string.reverse_charging_off_notification_content));
    }
    
    private void setRtxMode(final boolean rtxMode) {
        if (this.mRtxChargerManager.isPresent()) {
            this.mRtxChargerManager.get().setRtxMode(rtxMode);
        }
    }
    
    private void setRtxTimer(final int n, final long n2) {
        if (n != 0) {
            if (n == 1) {
                this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + n2, "BatteryControllerImplGoogle", this.mRtxStartAlarmAction, (Handler)null);
            }
        }
        else {
            this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + n2, "BatteryControllerImplGoogle", this.mRtxFinishAlarmAction, (Handler)null);
        }
    }
    
    private void showNotification(final String contentTitle, final String contentText) {
        if (this.mNtfManager == null) {
            Log.w("BatteryControllerImplGoogle", "showNotification() NotificationManager is null!");
            return;
        }
        final String string = super.mContext.getString(R$string.reverse_charging_title);
        final NotificationChannel notificationChannel = new NotificationChannel("reverseChargingNotificationChannel", (CharSequence)string, 3);
        notificationChannel.enableVibration(true);
        notificationChannel.enableLights(true);
        final Intent intent = new Intent("android.settings.REVERSE_CHARGING_SETTINGS");
        intent.setPackage("com.android.settings");
        intent.setFlags(268468224);
        final Notification build = new Notification$Builder(super.mContext, "reverseChargingNotificationChannel").setCategory("sys").setSmallIcon(R$drawable.ic_qs_reverse_charging).setContentTitle((CharSequence)contentTitle).setContentText((CharSequence)contentText).setContentIntent(PendingIntent.getActivity(super.mContext, 0, intent, 134217728)).setSubText((CharSequence)string).setStyle((Notification$Style)new Notification$BigTextStyle().bigText((CharSequence)contentText)).setLocalOnly(true).build();
        this.mNtfManager.createNotificationChannel(notificationChannel);
        this.mNtfManager.notifyAsUser("BatteryControllerImplGoogle", 1234, build, UserHandle.CURRENT);
    }
    
    @Override
    public void addCallback(final BatteryStateChangeCallback batteryStateChangeCallback) {
        super.addCallback(batteryStateChangeCallback);
        batteryStateChangeCallback.onReverseChanged(this.mReverse, this.mRxLevel, this.mName);
    }
    
    public boolean isReverseOn() {
        if (this.mRtxChargerManager.isPresent()) {
            return this.mRtxChargerManager.get().isRtxModeOn();
        }
        Log.w("BatteryControllerImplGoogle", "isReverseOn() mRtxChargerManager is null!");
        return false;
    }
    
    public boolean isReverseSupported() {
        if (this.mIsReverseSupportedMethodInvoked) {
            return this.mIsReverseSupported;
        }
        this.mIsReverseSupportedMethodInvoked = true;
        if (this.mRtxChargerManager.isPresent()) {
            return this.mIsReverseSupported = this.mRtxChargerManager.get().isRtxSupported();
        }
        Log.w("BatteryControllerImplGoogle", "isReverseSupported() mRtxChargerManager is null!");
        return false;
    }
    
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final boolean mPluggedIn = super.mPluggedIn;
        super.onReceive(context, intent);
        final String action = intent.getAction();
        if (action.equals("android.intent.action.BATTERY_CHANGED")) {
            if (!this.isReverseSupported()) {
                return;
            }
            if (this.mReverse && !super.mPluggedIn && super.mCharging) {
                this.setRtxTimer(0, 0L);
            }
            if (!this.mReverse && !mPluggedIn && super.mPluggedIn && super.mCharging) {
                this.setRtxTimer(1, 0L);
            }
            if (this.mReverse && this.isLowBattery()) {
                this.sendErrorNotification();
                this.setReverseState(false);
            }
        }
        else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
            if (!this.isReverseSupported()) {
                return;
            }
            if (this.mReverse && super.mPowerSave) {
                this.setRtxTimer(0, 0L);
            }
        }
    }
    
    public void setReverseState(final boolean b) {
        if (!this.isReverseSupported()) {
            return;
        }
        if (!this.isReverseOn() && (this.isPowerSave() || this.isLowBattery())) {
            return;
        }
        if ((this.mReverse = b) != this.isReverseOn()) {
            this.setRtxMode(b);
        }
        this.fireReverseChanged();
    }
    
    void showNotificationIfNecessary(final int n, final int i) {
        if (n == 1 && this.mPrefs.getBoolean("reverse_charging_ntf_first_launch", true)) {
            this.showNotification(super.mContext.getString(R$string.reverse_charging_on_notification_title), super.mContext.getString(R$string.reverse_charging_on_notification_content));
            this.mPrefs.edit().putBoolean("reverse_charging_ntf_first_launch", false).apply();
        }
        else if (this.mCurrentRtxMode == 1 && n != 1 && i != 0) {
            this.sendErrorNotification();
            final StringBuilder sb = new StringBuilder();
            sb.append("Reverse charging error happened : ");
            sb.append(i);
            Log.w("BatteryControllerImplGoogle", sb.toString());
        }
        else if (this.mCurrentRtxMode != 1 && n == 1) {
            final NotificationManager mNtfManager = this.mNtfManager;
            if (mNtfManager != null) {
                mNtfManager.cancel("BatteryControllerImplGoogle", 1234);
            }
        }
    }
}
