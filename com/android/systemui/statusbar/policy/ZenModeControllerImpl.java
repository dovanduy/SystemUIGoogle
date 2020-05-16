// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.provider.Settings$Secure;
import android.provider.Settings$Global;
import android.content.ContentResolver;
import android.database.ContentObserver;
import java.util.Objects;
import android.net.Uri;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.app.AlarmManager$AlarmClockInfo;
import com.android.internal.annotations.VisibleForTesting;
import android.text.format.DateFormat;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.service.notification.ZenModeConfig$ZenRule;
import java.util.function.Consumer;
import java.util.List;
import com.android.systemui.util.Utils;
import android.content.Intent;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import android.util.Log;
import android.os.UserManager;
import android.content.BroadcastReceiver;
import android.app.NotificationManager;
import android.content.Context;
import android.app.NotificationManager$Policy;
import com.android.systemui.qs.GlobalSetting;
import android.service.notification.ZenModeConfig;
import java.util.ArrayList;
import android.app.AlarmManager;
import com.android.systemui.Dumpable;
import com.android.systemui.settings.CurrentUserTracker;

public class ZenModeControllerImpl extends CurrentUserTracker implements ZenModeController, Dumpable
{
    private final AlarmManager mAlarmManager;
    private final ArrayList<Callback> mCallbacks;
    private final Object mCallbacksLock;
    private ZenModeConfig mConfig;
    private final GlobalSetting mConfigSetting;
    private NotificationManager$Policy mConsolidatedNotificationPolicy;
    private final Context mContext;
    private final GlobalSetting mModeSetting;
    private final NotificationManager mNoMan;
    private final BroadcastReceiver mReceiver;
    private boolean mRegistered;
    private final SetupObserver mSetupObserver;
    private int mUserId;
    private final UserManager mUserManager;
    private int mZenMode;
    private long mZenUpdateTime;
    
    static {
        Log.isLoggable("ZenModeController", 3);
    }
    
    public ZenModeControllerImpl(final Context mContext, final Handler handler, final BroadcastDispatcher broadcastDispatcher) {
        super(broadcastDispatcher);
        this.mCallbacks = new ArrayList<Callback>();
        this.mCallbacksLock = new Object();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(intent.getAction())) {
                    ZenModeControllerImpl.this.fireNextAlarmChanged();
                }
                if ("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED".equals(intent.getAction())) {
                    ZenModeControllerImpl.this.fireEffectsSuppressorChanged();
                }
            }
        };
        this.mContext = mContext;
        this.mModeSetting = new GlobalSetting(this.mContext, handler, "zen_mode") {
            @Override
            protected void handleValueChanged(final int n) {
                ZenModeControllerImpl.this.updateZenMode(n);
                ZenModeControllerImpl.this.fireZenChanged(n);
            }
        };
        this.mConfigSetting = new GlobalSetting(this.mContext, handler, "zen_mode_config_etag") {
            @Override
            protected void handleValueChanged(final int n) {
                ZenModeControllerImpl.this.updateZenModeConfig();
            }
        };
        this.mNoMan = (NotificationManager)mContext.getSystemService("notification");
        this.mModeSetting.setListening(true);
        this.updateZenMode(this.mModeSetting.getValue());
        this.mConfigSetting.setListening(true);
        this.updateZenModeConfig();
        this.updateConsolidatedNotificationPolicy();
        this.mAlarmManager = (AlarmManager)mContext.getSystemService("alarm");
        (this.mSetupObserver = new SetupObserver(handler)).register();
        this.mUserManager = (UserManager)mContext.getSystemService((Class)UserManager.class);
        this.startTracking();
    }
    
    private void fireConsolidatedPolicyChanged(final NotificationManager$Policy notificationManager$Policy) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new _$$Lambda$ZenModeControllerImpl$8ESweSQi2XbEG_Qu7VUYzDq1Zcs(notificationManager$Policy));
        }
    }
    
    private void fireEffectsSuppressorChanged() {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, (Consumer<Callback>)_$$Lambda$ZenModeControllerImpl$SV0AVEr3ZD6I5F0ZOAtC6EOyn_k.INSTANCE);
        }
    }
    
    private void fireManualRuleChanged(final ZenModeConfig$ZenRule zenModeConfig$ZenRule) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new _$$Lambda$ZenModeControllerImpl$8iaDxlkHjmysoUP7KwjUaBzkBiQ(zenModeConfig$ZenRule));
        }
    }
    
    private void fireNextAlarmChanged() {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, (Consumer<Callback>)_$$Lambda$ZenModeControllerImpl$6_S_aAoRd9fsiJr9D0TIwCJGb6M.INSTANCE);
        }
    }
    
    private void fireZenAvailableChanged(final boolean b) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new _$$Lambda$ZenModeControllerImpl$SZ6Og1sK4NAner_jv0COJMr2bCU(b));
        }
    }
    
    private void fireZenChanged(final int n) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new _$$Lambda$ZenModeControllerImpl$d6ICAgvR9KT8NKs4p_zRwBgYI2g(n));
        }
    }
    
    @Override
    public void addCallback(final Callback e) {
        synchronized (this.mCallbacksLock) {
            this.mCallbacks.add(e);
        }
    }
    
    @Override
    public boolean areNotificationsHiddenInShade() {
        final int mZenMode = this.mZenMode;
        boolean b = false;
        if (mZenMode != 0) {
            b = b;
            if ((this.mConsolidatedNotificationPolicy.suppressedVisualEffects & 0x100) != 0x0) {
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("ZenModeControllerImpl:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mZenMode=");
        sb.append(this.mZenMode);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mConfig=");
        sb2.append(this.mConfig);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mConsolidatedNotificationPolicy=");
        sb3.append(this.mConsolidatedNotificationPolicy);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mZenUpdateTime=");
        sb4.append((Object)DateFormat.format((CharSequence)"MM-dd HH:mm:ss", this.mZenUpdateTime));
        printWriter.println(sb4.toString());
    }
    
    @VisibleForTesting
    protected void fireConfigChanged(final ZenModeConfig zenModeConfig) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new _$$Lambda$ZenModeControllerImpl$idmtZJFosRgAGQLYktOBo_UGp5E(zenModeConfig));
        }
    }
    
    @Override
    public ZenModeConfig getConfig() {
        return this.mConfig;
    }
    
    @Override
    public NotificationManager$Policy getConsolidatedPolicy() {
        return this.mConsolidatedNotificationPolicy;
    }
    
    @Override
    public ZenModeConfig$ZenRule getManualRule() {
        final ZenModeConfig mConfig = this.mConfig;
        ZenModeConfig$ZenRule manualRule;
        if (mConfig == null) {
            manualRule = null;
        }
        else {
            manualRule = mConfig.manualRule;
        }
        return manualRule;
    }
    
    @Override
    public long getNextAlarm() {
        final AlarmManager$AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(this.mUserId);
        long triggerTime;
        if (nextAlarmClock != null) {
            triggerTime = nextAlarmClock.getTriggerTime();
        }
        else {
            triggerTime = 0L;
        }
        return triggerTime;
    }
    
    @Override
    public int getZen() {
        return this.mZenMode;
    }
    
    @Override
    public boolean isVolumeRestricted() {
        return this.mUserManager.hasUserRestriction("no_adjust_volume", new UserHandle(this.mUserId));
    }
    
    public boolean isZenAvailable() {
        return this.mSetupObserver.isDeviceProvisioned() && this.mSetupObserver.isUserSetup();
    }
    
    @Override
    public void onUserSwitched(final int mUserId) {
        this.mUserId = mUserId;
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        final IntentFilter intentFilter = new IntentFilter("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        intentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
        this.mContext.registerReceiverAsUser(this.mReceiver, new UserHandle(this.mUserId), intentFilter, (String)null, (Handler)null);
        this.mRegistered = true;
        this.mSetupObserver.register();
    }
    
    @Override
    public void removeCallback(final Callback o) {
        synchronized (this.mCallbacksLock) {
            this.mCallbacks.remove(o);
        }
    }
    
    @Override
    public void setZen(final int n, final Uri uri, final String s) {
        this.mNoMan.setZenMode(n, uri, s);
    }
    
    @VisibleForTesting
    protected void updateConsolidatedNotificationPolicy() {
        final NotificationManager$Policy consolidatedNotificationPolicy = this.mNoMan.getConsolidatedNotificationPolicy();
        if (!Objects.equals(consolidatedNotificationPolicy, this.mConsolidatedNotificationPolicy)) {
            this.fireConsolidatedPolicyChanged(this.mConsolidatedNotificationPolicy = consolidatedNotificationPolicy);
        }
    }
    
    @VisibleForTesting
    protected void updateZenMode(final int mZenMode) {
        this.mZenMode = mZenMode;
        this.mZenUpdateTime = System.currentTimeMillis();
    }
    
    @VisibleForTesting
    protected void updateZenModeConfig() {
        final ZenModeConfig zenModeConfig = this.mNoMan.getZenModeConfig();
        if (Objects.equals(zenModeConfig, this.mConfig)) {
            return;
        }
        final ZenModeConfig mConfig = this.mConfig;
        ZenModeConfig$ZenRule manualRule = null;
        ZenModeConfig$ZenRule manualRule2;
        if (mConfig != null) {
            manualRule2 = mConfig.manualRule;
        }
        else {
            manualRule2 = null;
        }
        this.mConfig = zenModeConfig;
        this.mZenUpdateTime = System.currentTimeMillis();
        this.fireConfigChanged(zenModeConfig);
        if (zenModeConfig != null) {
            manualRule = zenModeConfig.manualRule;
        }
        if (!Objects.equals(manualRule2, manualRule)) {
            this.fireManualRuleChanged(manualRule);
        }
        final NotificationManager$Policy consolidatedNotificationPolicy = this.mNoMan.getConsolidatedNotificationPolicy();
        if (!Objects.equals(consolidatedNotificationPolicy, this.mConsolidatedNotificationPolicy)) {
            this.fireConsolidatedPolicyChanged(this.mConsolidatedNotificationPolicy = consolidatedNotificationPolicy);
        }
    }
    
    private final class SetupObserver extends ContentObserver
    {
        private boolean mRegistered;
        private final ContentResolver mResolver;
        
        public SetupObserver(final Handler handler) {
            super(handler);
            this.mResolver = ZenModeControllerImpl.this.mContext.getContentResolver();
        }
        
        public boolean isDeviceProvisioned() {
            final ContentResolver mResolver = this.mResolver;
            boolean b = false;
            if (Settings$Global.getInt(mResolver, "device_provisioned", 0) != 0) {
                b = true;
            }
            return b;
        }
        
        public boolean isUserSetup() {
            final ContentResolver mResolver = this.mResolver;
            final int access$400 = ZenModeControllerImpl.this.mUserId;
            boolean b = false;
            if (Settings$Secure.getIntForUser(mResolver, "user_setup_complete", 0, access$400) != 0) {
                b = true;
            }
            return b;
        }
        
        public void onChange(final boolean b, final Uri uri) {
            if (Settings$Global.getUriFor("device_provisioned").equals((Object)uri) || Settings$Secure.getUriFor("user_setup_complete").equals((Object)uri)) {
                final ZenModeControllerImpl this$0 = ZenModeControllerImpl.this;
                this$0.fireZenAvailableChanged(this$0.isZenAvailable());
            }
        }
        
        public void register() {
            if (this.mRegistered) {
                this.mResolver.unregisterContentObserver((ContentObserver)this);
            }
            this.mResolver.registerContentObserver(Settings$Global.getUriFor("device_provisioned"), false, (ContentObserver)this);
            this.mResolver.registerContentObserver(Settings$Secure.getUriFor("user_setup_complete"), false, (ContentObserver)this, ZenModeControllerImpl.this.mUserId);
            this.mRegistered = true;
            final ZenModeControllerImpl this$0 = ZenModeControllerImpl.this;
            this$0.fireZenAvailableChanged(this$0.isZenAvailable());
        }
    }
}
