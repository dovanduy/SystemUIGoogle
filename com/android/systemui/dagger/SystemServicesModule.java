// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import android.view.WindowManager;
import android.net.wifi.WifiManager;
import android.app.WallpaperManager;
import android.os.Vibrator;
import android.os.UserManager;
import android.app.trust.TrustManager;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;
import android.content.pm.ShortcutManager;
import android.hardware.SensorPrivacyManager;
import android.content.res.Resources;
import android.os.PowerManager;
import com.android.systemui.shared.system.PackageManagerWrapper;
import android.content.pm.PackageManager;
import android.app.NotificationManager;
import android.net.NetworkScoreManager;
import android.annotation.SuppressLint;
import android.os.UserHandle;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import android.os.Handler;
import android.content.pm.LauncherApps;
import com.android.internal.util.LatencyTracker;
import android.app.KeyguardManager;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import android.app.IWallpaperManager$Stub;
import android.app.IWallpaperManager;
import com.android.internal.statusbar.IStatusBarService$Stub;
import com.android.internal.statusbar.IStatusBarService;
import android.content.pm.IPackageManager$Stub;
import android.content.pm.IPackageManager;
import android.service.dreams.IDreamManager$Stub;
import android.service.dreams.IDreamManager;
import com.android.internal.app.IBatteryStats$Stub;
import android.os.ServiceManager;
import com.android.internal.app.IBatteryStats;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.net.ConnectivityManager;
import android.media.AudioManager;
import android.app.AlarmManager;
import android.app.ActivityManager;
import android.view.accessibility.AccessibilityManager;
import android.content.Context;

public class SystemServicesModule
{
    static AccessibilityManager provideAccessibilityManager(final Context context) {
        return (AccessibilityManager)context.getSystemService((Class)AccessibilityManager.class);
    }
    
    static ActivityManager provideActivityManager(final Context context) {
        return (ActivityManager)context.getSystemService((Class)ActivityManager.class);
    }
    
    static AlarmManager provideAlarmManager(final Context context) {
        return (AlarmManager)context.getSystemService((Class)AlarmManager.class);
    }
    
    static AudioManager provideAudioManager(final Context context) {
        return (AudioManager)context.getSystemService((Class)AudioManager.class);
    }
    
    static ConnectivityManager provideConnectivityManagager(final Context context) {
        return (ConnectivityManager)context.getSystemService((Class)ConnectivityManager.class);
    }
    
    static ContentResolver provideContentResolver(final Context context) {
        return context.getContentResolver();
    }
    
    static DevicePolicyManager provideDevicePolicyManager(final Context context) {
        return (DevicePolicyManager)context.getSystemService((Class)DevicePolicyManager.class);
    }
    
    static int provideDisplayId(final Context context) {
        return context.getDisplayId();
    }
    
    static IActivityManager provideIActivityManager() {
        return ActivityManager.getService();
    }
    
    static IBatteryStats provideIBatteryStats() {
        return IBatteryStats$Stub.asInterface(ServiceManager.getService("batterystats"));
    }
    
    static IDreamManager provideIDreamManager() {
        return IDreamManager$Stub.asInterface(ServiceManager.checkService("dreams"));
    }
    
    static IPackageManager provideIPackageManager() {
        return IPackageManager$Stub.asInterface(ServiceManager.getService("package"));
    }
    
    static IStatusBarService provideIStatusBarService() {
        return IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
    }
    
    static IWallpaperManager provideIWallPaperManager() {
        return IWallpaperManager$Stub.asInterface(ServiceManager.getService("wallpaper"));
    }
    
    static IWindowManager provideIWindowManager() {
        return WindowManagerGlobal.getWindowManagerService();
    }
    
    static KeyguardManager provideKeyguardManager(final Context context) {
        return (KeyguardManager)context.getSystemService((Class)KeyguardManager.class);
    }
    
    static LatencyTracker provideLatencyTracker(final Context context) {
        return LatencyTracker.getInstance(context);
    }
    
    static LauncherApps provideLauncherApps(final Context context) {
        return (LauncherApps)context.getSystemService((Class)LauncherApps.class);
    }
    
    @SuppressLint({ "MissingPermission" })
    static LocalBluetoothManager provideLocalBluetoothController(final Context context, final Handler handler) {
        return LocalBluetoothManager.create(context, handler, UserHandle.ALL);
    }
    
    static NetworkScoreManager provideNetworkScoreManager(final Context context) {
        return (NetworkScoreManager)context.getSystemService((Class)NetworkScoreManager.class);
    }
    
    static NotificationManager provideNotificationManager(final Context context) {
        return (NotificationManager)context.getSystemService((Class)NotificationManager.class);
    }
    
    static PackageManager providePackageManager(final Context context) {
        return context.getPackageManager();
    }
    
    static PackageManagerWrapper providePackageManagerWrapper() {
        return PackageManagerWrapper.getInstance();
    }
    
    static PowerManager providePowerManager(final Context context) {
        return (PowerManager)context.getSystemService((Class)PowerManager.class);
    }
    
    static Resources provideResources(final Context context) {
        return context.getResources();
    }
    
    static SensorPrivacyManager provideSensorPrivacyManager(final Context context) {
        return (SensorPrivacyManager)context.getSystemService((Class)SensorPrivacyManager.class);
    }
    
    static ShortcutManager provideShortcutManager(final Context context) {
        return (ShortcutManager)context.getSystemService((Class)ShortcutManager.class);
    }
    
    static TelecomManager provideTelecomManager(final Context context) {
        return (TelecomManager)context.getSystemService((Class)TelecomManager.class);
    }
    
    static TelephonyManager provideTelephonyManager(final Context context) {
        return (TelephonyManager)context.getSystemService((Class)TelephonyManager.class);
    }
    
    static TrustManager provideTrustManager(final Context context) {
        return (TrustManager)context.getSystemService((Class)TrustManager.class);
    }
    
    static UserManager provideUserManager(final Context context) {
        return (UserManager)context.getSystemService((Class)UserManager.class);
    }
    
    static Vibrator provideVibrator(final Context context) {
        return (Vibrator)context.getSystemService((Class)Vibrator.class);
    }
    
    static WallpaperManager provideWallpaperManager(final Context context) {
        return (WallpaperManager)context.getSystemService("wallpaper");
    }
    
    static WifiManager provideWifiManager(final Context context) {
        return (WifiManager)context.getSystemService((Class)WifiManager.class);
    }
    
    static WindowManager provideWindowManager(final Context context) {
        return (WindowManager)context.getSystemService((Class)WindowManager.class);
    }
}
