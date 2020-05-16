// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import android.os.Temperature;
import android.os.IThermalEventListener$Stub;
import com.android.settingslib.utils.ThreadUtils;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.database.ContentObserver;
import com.android.systemui.Dependency;
import com.android.settingslib.fuelgauge.Estimate;
import android.os.SystemClock;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.RemoteException;
import android.os.IThermalService$Stub;
import android.os.ServiceManager;
import com.android.systemui.R$integer;
import android.content.SharedPreferences;
import android.provider.Settings$SettingNotFoundException;
import android.util.Slog;
import android.provider.Settings$Global;
import android.content.Context;
import java.time.Duration;
import android.util.Log;
import android.os.IThermalService;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import android.os.IThermalEventListener;
import android.os.PowerManager;
import java.util.concurrent.Future;
import android.content.res.Configuration;
import android.os.Handler;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class PowerUI extends SystemUI implements Callbacks
{
    static final boolean DEBUG;
    private static final long SIX_HOURS_MILLIS;
    @VisibleForTesting
    int mBatteryLevel;
    @VisibleForTesting
    int mBatteryStatus;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final CommandQueue mCommandQueue;
    @VisibleForTesting
    BatteryStateSnapshot mCurrentBatteryStateSnapshot;
    private boolean mEnableSkinTemperatureWarning;
    private boolean mEnableUsbTemperatureAlarm;
    private EnhancedEstimates mEnhancedEstimates;
    private final Handler mHandler;
    private int mInvalidCharger;
    @VisibleForTesting
    BatteryStateSnapshot mLastBatteryStateSnapshot;
    private final Configuration mLastConfiguration;
    private Future mLastShowWarningTask;
    private int mLowBatteryAlertCloseLevel;
    private final int[] mLowBatteryReminderLevels;
    @VisibleForTesting
    boolean mLowWarningShownThisChargeCycle;
    private InattentiveSleepWarningView mOverlayView;
    private int mPlugType;
    private PowerManager mPowerManager;
    @VisibleForTesting
    final Receiver mReceiver;
    private long mScreenOffTime;
    @VisibleForTesting
    boolean mSevereWarningShownThisChargeCycle;
    private IThermalEventListener mSkinThermalEventListener;
    private final Lazy<StatusBar> mStatusBarLazy;
    @VisibleForTesting
    IThermalService mThermalService;
    private IThermalEventListener mUsbThermalEventListener;
    private WarningsUI mWarnings;
    
    static {
        DEBUG = Log.isLoggable("PowerUI", 3);
        SIX_HOURS_MILLIS = Duration.ofHours(6L).toMillis();
    }
    
    public PowerUI(final Context context, final BroadcastDispatcher mBroadcastDispatcher, final CommandQueue mCommandQueue, final Lazy<StatusBar> mStatusBarLazy) {
        super(context);
        this.mHandler = new Handler();
        this.mReceiver = new Receiver();
        this.mLastConfiguration = new Configuration();
        this.mPlugType = 0;
        this.mInvalidCharger = 0;
        this.mLowBatteryReminderLevels = new int[2];
        this.mScreenOffTime = -1L;
        this.mBatteryLevel = 100;
        this.mBatteryStatus = 1;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mCommandQueue = mCommandQueue;
        this.mStatusBarLazy = mStatusBarLazy;
    }
    
    private int findBatteryLevelBucket(final int n) {
        if (n >= this.mLowBatteryAlertCloseLevel) {
            return 1;
        }
        final int[] mLowBatteryReminderLevels = this.mLowBatteryReminderLevels;
        if (n > mLowBatteryReminderLevels[0]) {
            return 0;
        }
        for (int i = mLowBatteryReminderLevels.length - 1; i >= 0; --i) {
            if (n <= this.mLowBatteryReminderLevels[i]) {
                return -1 - i;
            }
        }
        throw new RuntimeException("not possible!");
    }
    
    private void initThermalEventListeners() {
        this.doSkinThermalEventListenerRegistration();
        this.doUsbThermalEventListenerRegistration();
    }
    
    private void showWarnOnThermalShutdown() {
        final SharedPreferences sharedPreferences = super.mContext.getSharedPreferences("powerui_prefs", 0);
        int int1 = -1;
        final int int2 = sharedPreferences.getInt("boot_count", -1);
        try {
            int1 = Settings$Global.getInt(super.mContext.getContentResolver(), "boot_count");
        }
        catch (Settings$SettingNotFoundException ex) {
            Slog.e("PowerUI", "Failed to read system boot count from Settings.Global.BOOT_COUNT");
        }
        if (int1 > int2) {
            super.mContext.getSharedPreferences("powerui_prefs", 0).edit().putInt("boot_count", int1).apply();
            if (this.mPowerManager.getLastShutdownReason() == 4) {
                this.mWarnings.showThermalShutdownWarning();
            }
        }
    }
    
    @Override
    public void dismissInattentiveSleepWarning(final boolean b) {
        final InattentiveSleepWarningView mOverlayView = this.mOverlayView;
        if (mOverlayView != null) {
            mOverlayView.dismiss(b);
        }
    }
    
    @VisibleForTesting
    void doSkinThermalEventListenerRegistration() {
        synchronized (this) {
            final boolean mEnableSkinTemperatureWarning = this.mEnableSkinTemperatureWarning;
            final int int1 = Settings$Global.getInt(super.mContext.getContentResolver(), "show_temperature_warning", super.mContext.getResources().getInteger(R$integer.config_showTemperatureWarning));
            final boolean b = true;
            final boolean mEnableSkinTemperatureWarning2 = int1 != 0;
            this.mEnableSkinTemperatureWarning = mEnableSkinTemperatureWarning2;
            if (mEnableSkinTemperatureWarning2 != mEnableSkinTemperatureWarning) {
                boolean b2;
                try {
                    if (this.mSkinThermalEventListener == null) {
                        this.mSkinThermalEventListener = (IThermalEventListener)new SkinThermalEventListener();
                    }
                    if (this.mThermalService == null) {
                        this.mThermalService = IThermalService$Stub.asInterface(ServiceManager.getService("thermalservice"));
                    }
                    if (this.mEnableSkinTemperatureWarning) {
                        b2 = this.mThermalService.registerThermalEventListenerWithType(this.mSkinThermalEventListener, 3);
                    }
                    else {
                        b2 = this.mThermalService.unregisterThermalEventListener(this.mSkinThermalEventListener);
                    }
                }
                catch (RemoteException ex) {
                    Slog.e("PowerUI", "Exception while (un)registering skin thermal event listener.", (Throwable)ex);
                    b2 = false;
                }
                if (!b2) {
                    this.mEnableSkinTemperatureWarning = (!this.mEnableSkinTemperatureWarning && b);
                    Slog.e("PowerUI", "Failed to register or unregister skin thermal event listener.");
                }
            }
        }
    }
    
    @VisibleForTesting
    void doUsbThermalEventListenerRegistration() {
        synchronized (this) {
            final boolean mEnableUsbTemperatureAlarm = this.mEnableUsbTemperatureAlarm;
            final int int1 = Settings$Global.getInt(super.mContext.getContentResolver(), "show_usb_temperature_alarm", super.mContext.getResources().getInteger(R$integer.config_showUsbPortAlarm));
            final boolean b = true;
            final boolean mEnableUsbTemperatureAlarm2 = int1 != 0;
            this.mEnableUsbTemperatureAlarm = mEnableUsbTemperatureAlarm2;
            if (mEnableUsbTemperatureAlarm2 != mEnableUsbTemperatureAlarm) {
                boolean b2;
                try {
                    if (this.mUsbThermalEventListener == null) {
                        this.mUsbThermalEventListener = (IThermalEventListener)new UsbThermalEventListener();
                    }
                    if (this.mThermalService == null) {
                        this.mThermalService = IThermalService$Stub.asInterface(ServiceManager.getService("thermalservice"));
                    }
                    if (this.mEnableUsbTemperatureAlarm) {
                        b2 = this.mThermalService.registerThermalEventListenerWithType(this.mUsbThermalEventListener, 4);
                    }
                    else {
                        b2 = this.mThermalService.unregisterThermalEventListener(this.mUsbThermalEventListener);
                    }
                }
                catch (RemoteException ex) {
                    Slog.e("PowerUI", "Exception while (un)registering usb thermal event listener.", (Throwable)ex);
                    b2 = false;
                }
                if (!b2) {
                    this.mEnableUsbTemperatureAlarm = (!this.mEnableUsbTemperatureAlarm && b);
                    Slog.e("PowerUI", "Failed to register or unregister usb thermal event listener.");
                }
            }
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("mLowBatteryAlertCloseLevel=");
        printWriter.println(this.mLowBatteryAlertCloseLevel);
        printWriter.print("mLowBatteryReminderLevels=");
        printWriter.println(Arrays.toString(this.mLowBatteryReminderLevels));
        printWriter.print("mBatteryLevel=");
        printWriter.println(Integer.toString(this.mBatteryLevel));
        printWriter.print("mBatteryStatus=");
        printWriter.println(Integer.toString(this.mBatteryStatus));
        printWriter.print("mPlugType=");
        printWriter.println(Integer.toString(this.mPlugType));
        printWriter.print("mInvalidCharger=");
        printWriter.println(Integer.toString(this.mInvalidCharger));
        printWriter.print("mScreenOffTime=");
        printWriter.print(this.mScreenOffTime);
        if (this.mScreenOffTime >= 0L) {
            printWriter.print(" (");
            printWriter.print(SystemClock.elapsedRealtime() - this.mScreenOffTime);
            printWriter.print(" ago)");
        }
        printWriter.println();
        printWriter.print("soundTimeout=");
        printWriter.println(Settings$Global.getInt(super.mContext.getContentResolver(), "low_battery_sound_timeout", 0));
        printWriter.print("bucket: ");
        printWriter.println(Integer.toString(this.findBatteryLevelBucket(this.mBatteryLevel)));
        printWriter.print("mEnableSkinTemperatureWarning=");
        printWriter.println(this.mEnableSkinTemperatureWarning);
        printWriter.print("mEnableUsbTemperatureAlarm=");
        printWriter.println(this.mEnableUsbTemperatureAlarm);
        this.mWarnings.dump(printWriter);
    }
    
    protected void maybeShowBatteryWarning(final BatteryStateSnapshot batteryStateSnapshot, final BatteryStateSnapshot batteryStateSnapshot2) {
        final boolean b = batteryStateSnapshot.getBucket() != batteryStateSnapshot2.getBucket() || batteryStateSnapshot2.getPlugged();
        if (this.shouldShowLowBatteryWarning(batteryStateSnapshot, batteryStateSnapshot2)) {
            this.mWarnings.showLowBatteryWarning(b);
        }
        else if (this.shouldDismissLowBatteryWarning(batteryStateSnapshot, batteryStateSnapshot2)) {
            this.mWarnings.dismissLowBatteryWarning();
        }
        else {
            this.mWarnings.updateLowBatteryWarning();
        }
    }
    
    protected void maybeShowBatteryWarningV2(final boolean b, final int n) {
        final boolean hybridNotificationEnabled = this.mEnhancedEstimates.isHybridNotificationEnabled();
        final boolean powerSaveMode = this.mPowerManager.isPowerSaveMode();
        if (PowerUI.DEBUG) {
            Slog.d("PowerUI", "evaluating which notification to show");
        }
        if (hybridNotificationEnabled) {
            if (PowerUI.DEBUG) {
                Slog.d("PowerUI", "using hybrid");
            }
            final Estimate refreshEstimateIfNeeded = this.refreshEstimateIfNeeded();
            final int mBatteryLevel = this.mBatteryLevel;
            final int mBatteryStatus = this.mBatteryStatus;
            final int[] mLowBatteryReminderLevels = this.mLowBatteryReminderLevels;
            this.mCurrentBatteryStateSnapshot = new BatteryStateSnapshot(mBatteryLevel, powerSaveMode, b, n, mBatteryStatus, mLowBatteryReminderLevels[1], mLowBatteryReminderLevels[0], refreshEstimateIfNeeded.getEstimateMillis(), refreshEstimateIfNeeded.getAverageDischargeTime(), this.mEnhancedEstimates.getSevereWarningThreshold(), this.mEnhancedEstimates.getLowWarningThreshold(), refreshEstimateIfNeeded.isBasedOnUsage(), this.mEnhancedEstimates.getLowWarningEnabled());
        }
        else {
            if (PowerUI.DEBUG) {
                Slog.d("PowerUI", "using standard");
            }
            final int mBatteryLevel2 = this.mBatteryLevel;
            final int mBatteryStatus2 = this.mBatteryStatus;
            final int[] mLowBatteryReminderLevels2 = this.mLowBatteryReminderLevels;
            this.mCurrentBatteryStateSnapshot = new BatteryStateSnapshot(mBatteryLevel2, powerSaveMode, b, n, mBatteryStatus2, mLowBatteryReminderLevels2[1], mLowBatteryReminderLevels2[0]);
        }
        this.mWarnings.updateSnapshot(this.mCurrentBatteryStateSnapshot);
        if (this.mCurrentBatteryStateSnapshot.isHybrid()) {
            this.maybeShowHybridWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot);
        }
        else {
            this.maybeShowBatteryWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot);
        }
    }
    
    @VisibleForTesting
    void maybeShowHybridWarning(final BatteryStateSnapshot batteryStateSnapshot, final BatteryStateSnapshot batteryStateSnapshot2) {
        final int batteryLevel = batteryStateSnapshot.getBatteryLevel();
        boolean b = false;
        if (batteryLevel >= 45 && batteryStateSnapshot.getTimeRemainingMillis() > PowerUI.SIX_HOURS_MILLIS) {
            this.mLowWarningShownThisChargeCycle = false;
            this.mSevereWarningShownThisChargeCycle = false;
            if (PowerUI.DEBUG) {
                Slog.d("PowerUI", "Charge cycle reset! Can show warnings again");
            }
        }
        if (batteryStateSnapshot.getBucket() != batteryStateSnapshot2.getBucket() || batteryStateSnapshot2.getPlugged()) {
            b = true;
        }
        if (this.shouldShowHybridWarning(batteryStateSnapshot)) {
            this.mWarnings.showLowBatteryWarning(b);
            if (batteryStateSnapshot.getTimeRemainingMillis() > batteryStateSnapshot.getSevereThresholdMillis() && batteryStateSnapshot.getBatteryLevel() > batteryStateSnapshot.getSevereLevelThreshold()) {
                Slog.d("PowerUI", "Low warning marked as shown this cycle");
                this.mLowWarningShownThisChargeCycle = true;
            }
            else {
                this.mSevereWarningShownThisChargeCycle = true;
                this.mLowWarningShownThisChargeCycle = true;
                if (PowerUI.DEBUG) {
                    Slog.d("PowerUI", "Severe warning marked as shown this cycle");
                }
            }
        }
        else if (this.shouldDismissHybridWarning(batteryStateSnapshot)) {
            if (PowerUI.DEBUG) {
                Slog.d("PowerUI", "Dismissing warning");
            }
            this.mWarnings.dismissLowBatteryWarning();
        }
        else {
            if (PowerUI.DEBUG) {
                Slog.d("PowerUI", "Updating warning");
            }
            this.mWarnings.updateLowBatteryWarning();
        }
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        if ((this.mLastConfiguration.updateFrom(configuration) & 0x3) != 0x0) {
            this.mHandler.post((Runnable)new _$$Lambda$PowerUI$QV7l9YjJI0jIQa7PQUr5PFep9Kg(this));
        }
    }
    
    @VisibleForTesting
    Estimate refreshEstimateIfNeeded() {
        final BatteryStateSnapshot mLastBatteryStateSnapshot = this.mLastBatteryStateSnapshot;
        if (mLastBatteryStateSnapshot != null && mLastBatteryStateSnapshot.getTimeRemainingMillis() != -1L && this.mBatteryLevel == this.mLastBatteryStateSnapshot.getBatteryLevel()) {
            return new Estimate(this.mLastBatteryStateSnapshot.getTimeRemainingMillis(), this.mLastBatteryStateSnapshot.isBasedOnUsage(), this.mLastBatteryStateSnapshot.getAverageTimeToDischargeMillis());
        }
        final Estimate estimate = this.mEnhancedEstimates.getEstimate();
        if (PowerUI.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("updated estimate: ");
            sb.append(estimate.getEstimateMillis());
            Slog.d("PowerUI", sb.toString());
        }
        return estimate;
    }
    
    @VisibleForTesting
    boolean shouldDismissHybridWarning(final BatteryStateSnapshot batteryStateSnapshot) {
        return batteryStateSnapshot.getPlugged() || batteryStateSnapshot.getTimeRemainingMillis() > batteryStateSnapshot.getLowThresholdMillis();
    }
    
    @VisibleForTesting
    boolean shouldDismissLowBatteryWarning(final BatteryStateSnapshot batteryStateSnapshot, final BatteryStateSnapshot batteryStateSnapshot2) {
        return batteryStateSnapshot.isPowerSaver() || batteryStateSnapshot.getPlugged() || (batteryStateSnapshot.getBucket() > batteryStateSnapshot2.getBucket() && batteryStateSnapshot.getBucket() > 0);
    }
    
    @VisibleForTesting
    boolean shouldShowHybridWarning(final BatteryStateSnapshot batteryStateSnapshot) {
        final boolean plugged = batteryStateSnapshot.getPlugged();
        final boolean b = false;
        boolean b2 = true;
        if (!plugged && batteryStateSnapshot.getBatteryStatus() != 1) {
            final boolean b3 = batteryStateSnapshot.isLowWarningEnabled() && !this.mLowWarningShownThisChargeCycle && !batteryStateSnapshot.isPowerSaver() && (batteryStateSnapshot.getTimeRemainingMillis() < batteryStateSnapshot.getLowThresholdMillis() || batteryStateSnapshot.getBatteryLevel() <= batteryStateSnapshot.getLowLevelThreshold());
            final boolean b4 = !this.mSevereWarningShownThisChargeCycle && (batteryStateSnapshot.getTimeRemainingMillis() < batteryStateSnapshot.getSevereThresholdMillis() || batteryStateSnapshot.getBatteryLevel() <= batteryStateSnapshot.getSevereLevelThreshold());
            boolean b5 = false;
            Label_0133: {
                if (!b3) {
                    b5 = b;
                    if (!b4) {
                        break Label_0133;
                    }
                }
                b5 = true;
            }
            if (PowerUI.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Enhanced trigger is: ");
                sb.append(b5);
                sb.append("\nwith battery snapshot: mLowWarningShownThisChargeCycle: ");
                sb.append(this.mLowWarningShownThisChargeCycle);
                sb.append(" mSevereWarningShownThisChargeCycle: ");
                sb.append(this.mSevereWarningShownThisChargeCycle);
                sb.append("\n");
                sb.append(batteryStateSnapshot.toString());
                Slog.d("PowerUI", sb.toString());
            }
            return b5;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("can't show warning due to - plugged: ");
        sb2.append(batteryStateSnapshot.getPlugged());
        sb2.append(" status unknown: ");
        if (batteryStateSnapshot.getBatteryStatus() != 1) {
            b2 = false;
        }
        sb2.append(b2);
        Slog.d("PowerUI", sb2.toString());
        return false;
    }
    
    @VisibleForTesting
    boolean shouldShowLowBatteryWarning(final BatteryStateSnapshot batteryStateSnapshot, final BatteryStateSnapshot batteryStateSnapshot2) {
        final boolean plugged = batteryStateSnapshot.getPlugged();
        boolean b = true;
        if (plugged || batteryStateSnapshot.isPowerSaver() || (batteryStateSnapshot.getBucket() >= batteryStateSnapshot2.getBucket() && !batteryStateSnapshot2.getPlugged()) || batteryStateSnapshot.getBucket() >= 0 || batteryStateSnapshot.getBatteryStatus() == 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void showInattentiveSleepWarning() {
        if (this.mOverlayView == null) {
            this.mOverlayView = new InattentiveSleepWarningView(super.mContext);
        }
        this.mOverlayView.show();
    }
    
    @Override
    public void start() {
        final PowerManager mPowerManager = (PowerManager)super.mContext.getSystemService("power");
        this.mPowerManager = mPowerManager;
        long elapsedRealtime;
        if (mPowerManager.isScreenOn()) {
            elapsedRealtime = -1L;
        }
        else {
            elapsedRealtime = SystemClock.elapsedRealtime();
        }
        this.mScreenOffTime = elapsedRealtime;
        this.mWarnings = Dependency.get(WarningsUI.class);
        this.mEnhancedEstimates = Dependency.get(EnhancedEstimates.class);
        this.mLastConfiguration.setTo(super.mContext.getResources().getConfiguration());
        final ContentObserver contentObserver = new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                PowerUI.this.updateBatteryWarningLevels();
            }
        };
        final ContentResolver contentResolver = super.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings$Global.getUriFor("low_power_trigger_level"), false, (ContentObserver)contentObserver, -1);
        this.updateBatteryWarningLevels();
        this.mReceiver.init();
        this.showWarnOnThermalShutdown();
        contentResolver.registerContentObserver(Settings$Global.getUriFor("show_temperature_warning"), false, (ContentObserver)new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                PowerUI.this.doSkinThermalEventListenerRegistration();
            }
        });
        contentResolver.registerContentObserver(Settings$Global.getUriFor("show_usb_temperature_alarm"), false, (ContentObserver)new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                PowerUI.this.doUsbThermalEventListenerRegistration();
            }
        });
        this.initThermalEventListeners();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
    }
    
    void updateBatteryWarningLevels() {
        final int integer = super.mContext.getResources().getInteger(17694765);
        int integer2;
        if ((integer2 = super.mContext.getResources().getInteger(17694829)) < integer) {
            integer2 = integer;
        }
        final int[] mLowBatteryReminderLevels = this.mLowBatteryReminderLevels;
        mLowBatteryReminderLevels[0] = integer2;
        mLowBatteryReminderLevels[1] = integer;
        this.mLowBatteryAlertCloseLevel = mLowBatteryReminderLevels[0] + super.mContext.getResources().getInteger(17694828);
    }
    
    @VisibleForTesting
    final class Receiver extends BroadcastReceiver
    {
        public void init() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            PowerUI.this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, PowerUI.this.mHandler);
        }
        
        public void onReceive(final Context context, final Intent obj) {
            final String action = obj.getAction();
            if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(action)) {
                ThreadUtils.postOnBackgroundThread(new _$$Lambda$PowerUI$Receiver$r1RcZjs8DVXWaC4Afqm8W0WAvm8(this));
            }
            else if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                final PowerUI this$0 = PowerUI.this;
                final int mBatteryLevel = this$0.mBatteryLevel;
                this$0.mBatteryLevel = obj.getIntExtra("level", 100);
                final PowerUI this$2 = PowerUI.this;
                final int mBatteryStatus = this$2.mBatteryStatus;
                this$2.mBatteryStatus = obj.getIntExtra("status", 1);
                final int access$200 = PowerUI.this.mPlugType;
                PowerUI.this.mPlugType = obj.getIntExtra("plugged", 1);
                final int access$201 = PowerUI.this.mInvalidCharger;
                PowerUI.this.mInvalidCharger = obj.getIntExtra("invalid_charger", 0);
                final PowerUI this$3 = PowerUI.this;
                this$3.mLastBatteryStateSnapshot = this$3.mCurrentBatteryStateSnapshot;
                final boolean b = this$3.mPlugType != 0;
                final boolean b2 = access$200 != 0;
                final int access$202 = PowerUI.this.findBatteryLevelBucket(mBatteryLevel);
                final PowerUI this$4 = PowerUI.this;
                final int access$203 = this$4.findBatteryLevelBucket(this$4.mBatteryLevel);
                if (PowerUI.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("buckets   .....");
                    sb.append(PowerUI.this.mLowBatteryAlertCloseLevel);
                    sb.append(" .. ");
                    sb.append(PowerUI.this.mLowBatteryReminderLevels[0]);
                    sb.append(" .. ");
                    sb.append(PowerUI.this.mLowBatteryReminderLevels[1]);
                    Slog.d("PowerUI", sb.toString());
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("level          ");
                    sb2.append(mBatteryLevel);
                    sb2.append(" --> ");
                    sb2.append(PowerUI.this.mBatteryLevel);
                    Slog.d("PowerUI", sb2.toString());
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("status         ");
                    sb3.append(mBatteryStatus);
                    sb3.append(" --> ");
                    sb3.append(PowerUI.this.mBatteryStatus);
                    Slog.d("PowerUI", sb3.toString());
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("plugType       ");
                    sb4.append(access$200);
                    sb4.append(" --> ");
                    sb4.append(PowerUI.this.mPlugType);
                    Slog.d("PowerUI", sb4.toString());
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append("invalidCharger ");
                    sb5.append(access$201);
                    sb5.append(" --> ");
                    sb5.append(PowerUI.this.mInvalidCharger);
                    Slog.d("PowerUI", sb5.toString());
                    final StringBuilder sb6 = new StringBuilder();
                    sb6.append("bucket         ");
                    sb6.append(access$202);
                    sb6.append(" --> ");
                    sb6.append(access$203);
                    Slog.d("PowerUI", sb6.toString());
                    final StringBuilder sb7 = new StringBuilder();
                    sb7.append("plugged        ");
                    sb7.append(b2);
                    sb7.append(" --> ");
                    sb7.append(b);
                    Slog.d("PowerUI", sb7.toString());
                }
                final WarningsUI access$204 = PowerUI.this.mWarnings;
                final PowerUI this$5 = PowerUI.this;
                access$204.update(this$5.mBatteryLevel, access$203, this$5.mScreenOffTime);
                if (access$201 == 0 && PowerUI.this.mInvalidCharger != 0) {
                    Slog.d("PowerUI", "showing invalid charger warning");
                    PowerUI.this.mWarnings.showInvalidChargerWarning();
                    return;
                }
                if (access$201 != 0 && PowerUI.this.mInvalidCharger == 0) {
                    PowerUI.this.mWarnings.dismissInvalidChargerWarning();
                }
                else if (PowerUI.this.mWarnings.isInvalidChargerWarningShowing()) {
                    if (PowerUI.DEBUG) {
                        Slog.d("PowerUI", "Bad Charger");
                    }
                    return;
                }
                if (PowerUI.this.mLastShowWarningTask != null) {
                    PowerUI.this.mLastShowWarningTask.cancel(true);
                    if (PowerUI.DEBUG) {
                        Slog.d("PowerUI", "cancelled task");
                    }
                }
                PowerUI.this.mLastShowWarningTask = ThreadUtils.postOnBackgroundThread(new _$$Lambda$PowerUI$Receiver$YHQ7eAdH8G2eZkWaBryO_zqzv1I(this, b, access$203));
            }
            else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                PowerUI.this.mScreenOffTime = SystemClock.elapsedRealtime();
            }
            else if ("android.intent.action.SCREEN_ON".equals(action)) {
                PowerUI.this.mScreenOffTime = -1L;
            }
            else if ("android.intent.action.USER_SWITCHED".equals(action)) {
                PowerUI.this.mWarnings.userSwitched();
            }
            else {
                final StringBuilder sb8 = new StringBuilder();
                sb8.append("unknown intent: ");
                sb8.append(obj);
                Slog.w("PowerUI", sb8.toString());
            }
        }
    }
    
    @VisibleForTesting
    final class SkinThermalEventListener extends IThermalEventListener$Stub
    {
        public void notifyThrottling(final Temperature temperature) {
            final int status = temperature.getStatus();
            if (status >= 5) {
                if (!PowerUI.this.mStatusBarLazy.get().isDeviceInVrMode()) {
                    PowerUI.this.mWarnings.showHighTemperatureWarning();
                    final StringBuilder sb = new StringBuilder();
                    sb.append("SkinThermalEventListener: notifyThrottling was called , current skin status = ");
                    sb.append(status);
                    sb.append(", temperature = ");
                    sb.append(temperature.getValue());
                    Slog.d("PowerUI", sb.toString());
                }
            }
            else {
                PowerUI.this.mWarnings.dismissHighTemperatureWarning();
            }
        }
    }
    
    @VisibleForTesting
    final class UsbThermalEventListener extends IThermalEventListener$Stub
    {
        public void notifyThrottling(final Temperature temperature) {
            final int status = temperature.getStatus();
            if (status >= 5) {
                PowerUI.this.mWarnings.showUsbHighTemperatureAlarm();
                final StringBuilder sb = new StringBuilder();
                sb.append("UsbThermalEventListener: notifyThrottling was called , current usb port status = ");
                sb.append(status);
                sb.append(", temperature = ");
                sb.append(temperature.getValue());
                Slog.d("PowerUI", sb.toString());
            }
        }
    }
    
    public interface WarningsUI
    {
        void dismissHighTemperatureWarning();
        
        void dismissInvalidChargerWarning();
        
        void dismissLowBatteryWarning();
        
        void dump(final PrintWriter p0);
        
        boolean isInvalidChargerWarningShowing();
        
        void showHighTemperatureWarning();
        
        void showInvalidChargerWarning();
        
        void showLowBatteryWarning(final boolean p0);
        
        void showThermalShutdownWarning();
        
        void showUsbHighTemperatureAlarm();
        
        void update(final int p0, final int p1, final long p2);
        
        void updateLowBatteryWarning();
        
        void updateSnapshot(final BatteryStateSnapshot p0);
        
        void userSwitched();
    }
}
