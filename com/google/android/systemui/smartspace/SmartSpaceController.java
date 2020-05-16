// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Collection;
import com.android.systemui.util.Assert;
import com.android.systemui.smartspace.nano.SmartspaceProto$CardWrapper;
import android.util.KeyValueListParser;
import android.provider.Settings$Global;
import com.google.protobuf.nano.MessageNano;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.UserHandle;
import android.os.HandlerThread;
import android.os.Looper;
import com.android.systemui.dump.DumpManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.util.Log;
import java.util.ArrayList;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.app.AlarmManager$OnAlarmListener;
import android.os.Handler;
import android.content.Context;
import android.app.AlarmManager;
import com.android.systemui.Dumpable;

public class SmartSpaceController implements Dumpable
{
    static final boolean DEBUG;
    private final AlarmManager mAlarmManager;
    private boolean mAlarmRegistered;
    private final Context mAppContext;
    private final Handler mBackgroundHandler;
    private final Context mContext;
    private int mCurrentUserId;
    private final SmartSpaceData mData;
    private final AlarmManager$OnAlarmListener mExpireAlarmAction;
    private boolean mHidePrivateData;
    private boolean mHideWorkData;
    private final KeyguardUpdateMonitorCallback mKeyguardMonitorCallback;
    private final ArrayList<SmartSpaceUpdateListener> mListeners;
    private boolean mSmartSpaceEnabledBroadcastSent;
    private final ProtoStore mStore;
    private final Handler mUiHandler;
    
    static {
        DEBUG = Log.isLoggable("SmartSpaceController", 3);
    }
    
    public SmartSpaceController(final Context context, final KeyguardUpdateMonitor keyguardUpdateMonitor, final Handler mBackgroundHandler, final AlarmManager mAlarmManager, final DumpManager dumpManager) {
        this.mListeners = new ArrayList<SmartSpaceUpdateListener>();
        this.mExpireAlarmAction = (AlarmManager$OnAlarmListener)new _$$Lambda$SmartSpaceController$2JD3Kr_LWtVJvDpjst0cS_5HQXs(this);
        this.mKeyguardMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onTimeChanged() {
                if (SmartSpaceController.this.mData != null && SmartSpaceController.this.mData.hasCurrent() && SmartSpaceController.this.mData.getExpirationRemainingMillis() > 0L) {
                    SmartSpaceController.this.update();
                }
            }
        };
        this.mContext = context;
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mStore = new ProtoStore(this.mContext);
        new HandlerThread("smartspace-background").start();
        this.mBackgroundHandler = mBackgroundHandler;
        this.mCurrentUserId = UserHandle.myUserId();
        this.mAppContext = context;
        this.mAlarmManager = mAlarmManager;
        this.mData = new SmartSpaceData();
        if (this.isSmartSpaceDisabledByExperiments()) {
            return;
        }
        keyguardUpdateMonitor.registerCallback(this.mKeyguardMonitorCallback);
        this.reloadData();
        this.onGsaChanged();
        context.registerReceiver((BroadcastReceiver)new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                SmartSpaceController.this.onGsaChanged();
            }
        }, GSAIntents.getGsaPackageFilter("android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED", "android.intent.action.PACKAGE_DATA_CLEARED"));
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        context.registerReceiver((BroadcastReceiver)new UserSwitchReceiver(), intentFilter);
        context.registerReceiver((BroadcastReceiver)new SmartSpaceBroadcastReceiver(this), new IntentFilter("com.google.android.apps.nexuslauncher.UPDATE_SMARTSPACE"));
        dumpManager.registerDumpable(SmartSpaceController.class.getName(), this);
    }
    
    private void clearStore() {
        final ProtoStore mStore = this.mStore;
        final StringBuilder sb = new StringBuilder();
        sb.append("smartspace_");
        sb.append(this.mCurrentUserId);
        sb.append("_true");
        mStore.store(null, sb.toString());
        final ProtoStore mStore2 = this.mStore;
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("smartspace_");
        sb2.append(this.mCurrentUserId);
        sb2.append("_false");
        mStore2.store(null, sb2.toString());
    }
    
    private boolean isSmartSpaceDisabledByExperiments() {
        final String string = Settings$Global.getString(this.mContext.getContentResolver(), "always_on_display_constants");
        final KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        boolean boolean1;
        try {
            keyValueListParser.setString(string);
            boolean1 = keyValueListParser.getBoolean("smart_space_enabled", true);
        }
        catch (IllegalArgumentException ex) {
            Log.e("SmartSpaceController", "Bad AOD constants");
            boolean1 = true;
        }
        return boolean1 ^ true;
    }
    
    private SmartSpaceCard loadSmartSpaceData(final boolean b) {
        final SmartspaceProto$CardWrapper smartspaceProto$CardWrapper = new SmartspaceProto$CardWrapper();
        final ProtoStore mStore = this.mStore;
        final StringBuilder sb = new StringBuilder();
        sb.append("smartspace_");
        sb.append(this.mCurrentUserId);
        sb.append("_");
        sb.append(b);
        if (mStore.load(sb.toString(), smartspaceProto$CardWrapper)) {
            return SmartSpaceCard.fromWrapper(this.mContext, smartspaceProto$CardWrapper, b ^ true);
        }
        return null;
    }
    
    private void onExpire(final boolean b) {
        Assert.isMainThread();
        this.mAlarmRegistered = false;
        if (!this.mData.handleExpire() && !b) {
            if (SmartSpaceController.DEBUG) {
                Log.d("SmartSpaceController", "onExpire - cancelled");
            }
        }
        else {
            this.update();
        }
    }
    
    private void onGsaChanged() {
        if (SmartSpaceController.DEBUG) {
            Log.d("SmartSpaceController", "onGsaChanged");
        }
        if (UserHandle.myUserId() == 0) {
            this.mAppContext.sendBroadcast(new Intent("com.google.android.systemui.smartspace.ENABLE_UPDATE").setPackage("com.google.android.googlequicksearchbox").addFlags(268435456));
            this.mSmartSpaceEnabledBroadcastSent = true;
        }
        final ArrayList<SmartSpaceUpdateListener> list = new ArrayList<SmartSpaceUpdateListener>(this.mListeners);
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).onGsaChanged();
        }
    }
    
    private void update() {
        Assert.isMainThread();
        if (SmartSpaceController.DEBUG) {
            Log.d("SmartSpaceController", "update");
        }
        final boolean mAlarmRegistered = this.mAlarmRegistered;
        int i = 0;
        if (mAlarmRegistered) {
            this.mAlarmManager.cancel(this.mExpireAlarmAction);
            this.mAlarmRegistered = false;
        }
        final long expiresAtMillis = this.mData.getExpiresAtMillis();
        if (expiresAtMillis > 0L) {
            this.mAlarmManager.set(0, expiresAtMillis, "SmartSpace", this.mExpireAlarmAction, this.mUiHandler);
            this.mAlarmRegistered = true;
        }
        if (this.mListeners != null) {
            if (SmartSpaceController.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("notifying listeners data=");
                sb.append(this.mData);
                Log.d("SmartSpaceController", sb.toString());
            }
            for (ArrayList<SmartSpaceUpdateListener> list = new ArrayList<SmartSpaceUpdateListener>(this.mListeners); i < list.size(); ++i) {
                list.get(i).onSmartSpaceUpdated(this.mData);
            }
        }
    }
    
    public void addListener(final SmartSpaceUpdateListener e) {
        Assert.isMainThread();
        this.mListeners.add(e);
        final SmartSpaceData mData = this.mData;
        if (mData != null && e != null) {
            e.onSmartSpaceUpdated(mData);
        }
        if (e != null) {
            e.onSensitiveModeChanged(this.mHidePrivateData, this.mHideWorkData);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println();
        printWriter.println("SmartspaceController");
        final StringBuilder sb = new StringBuilder();
        sb.append("  initial broadcast: ");
        sb.append(this.mSmartSpaceEnabledBroadcastSent);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  weather ");
        sb2.append(this.mData.mWeatherCard);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  current ");
        sb3.append(this.mData.mCurrentCard);
        printWriter.println(sb3.toString());
        printWriter.println("serialized:");
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  weather ");
        sb4.append(this.loadSmartSpaceData(false));
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  current ");
        sb5.append(this.loadSmartSpaceData(true));
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("disabled by experiment: ");
        sb6.append(this.isSmartSpaceDisabledByExperiments());
        printWriter.println(sb6.toString());
    }
    
    public void onNewCard(final NewCardInfo obj) {
        if (SmartSpaceController.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onNewCard: ");
            sb.append(obj);
            Log.d("SmartSpaceController", sb.toString());
        }
        if (obj != null) {
            if (obj.getUserId() != this.mCurrentUserId) {
                if (SmartSpaceController.DEBUG) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Ignore card that belongs to another user target: ");
                    sb2.append(this.mCurrentUserId);
                    sb2.append(" current: ");
                    sb2.append(this.mCurrentUserId);
                    Log.d("SmartSpaceController", sb2.toString());
                }
                return;
            }
            this.mBackgroundHandler.post((Runnable)new _$$Lambda$SmartSpaceController$9U6GvXj5mJZXQ9EeZasJ48wS8Es(this, obj));
        }
    }
    
    public void reloadData() {
        this.mData.mCurrentCard = this.loadSmartSpaceData(true);
        this.mData.mWeatherCard = this.loadSmartSpaceData(false);
        this.update();
    }
    
    public void removeListener(final SmartSpaceUpdateListener o) {
        Assert.isMainThread();
        this.mListeners.remove(o);
    }
    
    public void setHideSensitiveData(final boolean mHidePrivateData, final boolean mHideWorkData) {
        if (this.mHidePrivateData == mHidePrivateData && this.mHideWorkData == mHideWorkData) {
            return;
        }
        this.mHidePrivateData = mHidePrivateData;
        this.mHideWorkData = mHideWorkData;
        final ArrayList<SmartSpaceUpdateListener> list = new ArrayList<SmartSpaceUpdateListener>(this.mListeners);
        final int n = 0;
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).onSensitiveModeChanged(mHidePrivateData, mHideWorkData);
        }
        if (this.mData.getCurrentCard() != null) {
            final boolean b = this.mHidePrivateData && !this.mData.getCurrentCard().isWorkProfile();
            int n2 = n;
            if (this.mHideWorkData) {
                n2 = n;
                if (this.mData.getCurrentCard().isWorkProfile()) {
                    n2 = 1;
                }
            }
            if (b || n2 != 0) {
                this.clearStore();
            }
        }
    }
    
    private class UserSwitchReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            if (SmartSpaceController.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Switching user: ");
                sb.append(intent.getAction());
                sb.append(" uid: ");
                sb.append(UserHandle.myUserId());
                Log.d("SmartSpaceController", sb.toString());
            }
            if (intent.getAction().equals("android.intent.action.USER_SWITCHED")) {
                SmartSpaceController.this.mCurrentUserId = intent.getIntExtra("android.intent.extra.user_handle", -1);
                SmartSpaceController.this.mData.clear();
                SmartSpaceController.this.onExpire(true);
            }
            SmartSpaceController.this.onExpire(true);
        }
    }
}
