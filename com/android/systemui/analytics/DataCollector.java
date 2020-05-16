// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.analytics;

import android.widget.Toast;
import android.net.Uri;
import android.view.MotionEvent;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.os.Build;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import com.google.protobuf.nano.MessageNano;
import com.android.systemui.Dependency;
import com.android.systemui.shared.plugins.PluginManager;
import android.provider.Settings$Secure;
import com.android.systemui.plugins.Plugin;
import android.os.Looper;
import android.database.ContentObserver;
import com.android.systemui.plugins.PluginListener;
import android.os.Handler;
import com.android.systemui.plugins.FalsingPlugin;
import android.content.Context;
import android.hardware.SensorEventListener;

public class DataCollector implements SensorEventListener
{
    private static DataCollector sInstance;
    private boolean mAllowReportRejectedTouch;
    private boolean mCollectBadTouches;
    private final Context mContext;
    private boolean mCornerSwiping;
    private SensorLoggerSession mCurrentSession;
    private boolean mDisableUnlocking;
    private boolean mEnableCollector;
    private FalsingPlugin mFalsingPlugin;
    private final Handler mHandler;
    private final PluginListener mPluginListener;
    protected final ContentObserver mSettingsObserver;
    private boolean mTrackingStarted;
    
    private DataCollector(final Context mContext) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mCurrentSession = null;
        this.mEnableCollector = false;
        this.mCollectBadTouches = false;
        this.mCornerSwiping = false;
        this.mTrackingStarted = false;
        this.mAllowReportRejectedTouch = false;
        this.mDisableUnlocking = false;
        this.mFalsingPlugin = null;
        this.mSettingsObserver = new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                DataCollector.this.updateConfiguration();
            }
        };
        this.mPluginListener = new PluginListener<FalsingPlugin>() {
            @Override
            public void onPluginConnected(final FalsingPlugin falsingPlugin, final Context context) {
                DataCollector.this.mFalsingPlugin = falsingPlugin;
            }
            
            @Override
            public void onPluginDisconnected(final FalsingPlugin falsingPlugin) {
                DataCollector.this.mFalsingPlugin = null;
            }
        };
        this.mContext = mContext;
        mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("data_collector_enable"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("data_collector_collect_bad_touches"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("data_collector_allow_rejected_touch_reports"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("data_collector_disable_unlocking"), false, this.mSettingsObserver, -1);
        this.updateConfiguration();
        Dependency.get(PluginManager.class).addPluginListener((PluginListener<Plugin>)this.mPluginListener, FalsingPlugin.class);
    }
    
    private void addEvent(final int n) {
        if (this.isEnabled()) {
            final SensorLoggerSession mCurrentSession = this.mCurrentSession;
            if (mCurrentSession != null) {
                mCurrentSession.addPhoneEvent(n, System.nanoTime());
            }
        }
    }
    
    public static DataCollector getInstance(final Context context) {
        if (DataCollector.sInstance == null) {
            DataCollector.sInstance = new DataCollector(context);
        }
        return DataCollector.sInstance;
    }
    
    private void onSessionEnd(final int n) {
        final SensorLoggerSession mCurrentSession = this.mCurrentSession;
        this.mCurrentSession = null;
        if (this.mEnableCollector || this.mDisableUnlocking) {
            mCurrentSession.end(System.currentTimeMillis(), n);
            this.queueSession(mCurrentSession);
        }
    }
    
    private void onSessionStart() {
        this.mCornerSwiping = false;
        this.mTrackingStarted = false;
        this.mCurrentSession = new SensorLoggerSession(System.currentTimeMillis(), System.nanoTime());
    }
    
    private void queueSession(final SensorLoggerSession sensorLoggerSession) {
        AsyncTask.execute((Runnable)new Runnable() {
            @Override
            public void run() {
                final byte[] byteArray = MessageNano.toByteArray(sensorLoggerSession.toProto());
                final FalsingPlugin access$100 = DataCollector.this.mFalsingPlugin;
                boolean b = true;
                if (access$100 != null) {
                    final FalsingPlugin access$101 = DataCollector.this.mFalsingPlugin;
                    if (sensorLoggerSession.getResult() != 1) {
                        b = false;
                    }
                    access$101.dataCollected(b, byteArray);
                    return;
                }
                final String absolutePath = DataCollector.this.mContext.getFilesDir().getAbsolutePath();
                String pathname;
                if (sensorLoggerSession.getResult() != 1) {
                    if (!DataCollector.this.mDisableUnlocking && !DataCollector.this.mCollectBadTouches) {
                        return;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append(absolutePath);
                    sb.append("/bad_touches");
                    pathname = sb.toString();
                }
                else {
                    pathname = absolutePath;
                    if (!DataCollector.this.mDisableUnlocking) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append(absolutePath);
                        sb2.append("/good_touches");
                        pathname = sb2.toString();
                    }
                }
                final File parent = new File(pathname);
                parent.mkdir();
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("trace_");
                sb3.append(System.currentTimeMillis());
                final File file = new File(parent, sb3.toString());
                try {
                    new FileOutputStream(file).write(byteArray);
                }
                catch (IOException cause) {
                    throw new RuntimeException(cause);
                }
            }
        });
    }
    
    private boolean sessionEntrypoint() {
        if (this.isEnabled() && this.mCurrentSession == null) {
            this.onSessionStart();
            return true;
        }
        return false;
    }
    
    private void sessionExitpoint(final int n) {
        if (this.mCurrentSession != null) {
            this.onSessionEnd(n);
        }
    }
    
    private void updateConfiguration() {
        final boolean is_DEBUGGABLE = Build.IS_DEBUGGABLE;
        final boolean b = true;
        final boolean mEnableCollector = is_DEBUGGABLE && Settings$Secure.getInt(this.mContext.getContentResolver(), "data_collector_enable", 0) != 0;
        this.mEnableCollector = mEnableCollector;
        this.mCollectBadTouches = (mEnableCollector && Settings$Secure.getInt(this.mContext.getContentResolver(), "data_collector_collect_bad_touches", 0) != 0);
        this.mAllowReportRejectedTouch = (Build.IS_DEBUGGABLE && Settings$Secure.getInt(this.mContext.getContentResolver(), "data_collector_allow_rejected_touch_reports", 0) != 0);
        this.mDisableUnlocking = (this.mEnableCollector && Build.IS_DEBUGGABLE && Settings$Secure.getInt(this.mContext.getContentResolver(), "data_collector_disable_unlocking", 0) != 0 && b);
    }
    
    public boolean isEnabled() {
        return this.mEnableCollector || this.mAllowReportRejectedTouch || this.mDisableUnlocking;
    }
    
    public boolean isEnabledFull() {
        return this.mEnableCollector;
    }
    
    public boolean isReportingEnabled() {
        return this.mAllowReportRejectedTouch;
    }
    
    public boolean isUnlockingDisabled() {
        return this.mDisableUnlocking;
    }
    
    public void onAccuracyChanged(final Sensor sensor, final int n) {
    }
    
    public void onAffordanceSwipingAborted() {
        if (this.mCornerSwiping) {
            this.mCornerSwiping = false;
            this.addEvent(23);
        }
    }
    
    public void onAffordanceSwipingStarted(final boolean b) {
        this.mCornerSwiping = true;
        if (b) {
            this.addEvent(21);
        }
        else {
            this.addEvent(22);
        }
    }
    
    public void onBouncerHidden() {
        this.addEvent(5);
    }
    
    public void onBouncerShown() {
        this.addEvent(4);
    }
    
    public void onCameraHintStarted() {
        this.addEvent(27);
    }
    
    public void onCameraOn() {
        this.addEvent(24);
    }
    
    public void onExpansionFromPulseStopped() {
    }
    
    public void onFalsingSessionStarted() {
        this.sessionEntrypoint();
    }
    
    public void onLeftAffordanceHintStarted() {
        this.addEvent(28);
    }
    
    public void onLeftAffordanceOn() {
        this.addEvent(25);
    }
    
    public void onNotificationActive() {
        this.addEvent(11);
    }
    
    public void onNotificationDismissed() {
        this.addEvent(18);
    }
    
    public void onNotificationDoubleTap() {
        this.addEvent(13);
    }
    
    public void onNotificatonStartDismissing() {
        this.addEvent(19);
    }
    
    public void onNotificatonStartDraggingDown() {
        this.addEvent(16);
    }
    
    public void onNotificatonStopDismissing() {
        this.addEvent(20);
    }
    
    public void onNotificatonStopDraggingDown() {
        this.addEvent(17);
    }
    
    public void onQsDown() {
        this.addEvent(6);
    }
    
    public void onScreenOff() {
        this.addEvent(2);
        this.sessionExitpoint(0);
    }
    
    public void onScreenOnFromTouch() {
        if (this.sessionEntrypoint()) {
            this.addEvent(1);
        }
    }
    
    public void onScreenTurningOn() {
        if (this.sessionEntrypoint()) {
            this.addEvent(0);
        }
    }
    
    public void onSensorChanged(final SensorEvent sensorEvent) {
        synchronized (this) {
            if (this.isEnabled() && this.mCurrentSession != null) {
                this.mCurrentSession.addSensorEvent(sensorEvent, System.nanoTime());
            }
        }
    }
    
    public void onStartExpandingFromPulse() {
    }
    
    public void onSucccessfulUnlock() {
        this.addEvent(3);
        this.sessionExitpoint(1);
    }
    
    public void onTouchEvent(final MotionEvent motionEvent, final int n, final int n2) {
        final SensorLoggerSession mCurrentSession = this.mCurrentSession;
        if (mCurrentSession != null) {
            mCurrentSession.addMotionEvent(motionEvent);
            this.mCurrentSession.setTouchArea(n, n2);
        }
    }
    
    public void onTrackingStarted() {
        this.mTrackingStarted = true;
        this.addEvent(9);
    }
    
    public void onTrackingStopped() {
        if (this.mTrackingStarted) {
            this.mTrackingStarted = false;
            this.addEvent(10);
        }
    }
    
    public void onUnlockHintStarted() {
        this.addEvent(26);
    }
    
    public Uri reportRejectedTouch() {
        final SensorLoggerSession mCurrentSession = this.mCurrentSession;
        if (mCurrentSession == null) {
            Toast.makeText(this.mContext, (CharSequence)"Generating rejected touch report failed: session timed out.", 1).show();
            return null;
        }
        mCurrentSession.setType(4);
        mCurrentSession.end(System.currentTimeMillis(), 1);
        final byte[] byteArray = MessageNano.toByteArray(mCurrentSession.toProto());
        final File parent = new File(this.mContext.getExternalCacheDir(), "rejected_touch_reports");
        parent.mkdir();
        final StringBuilder sb = new StringBuilder();
        sb.append("rejected_touch_report_");
        sb.append(System.currentTimeMillis());
        final File file = new File(parent, sb.toString());
        try {
            new FileOutputStream(file).write(byteArray);
            return Uri.fromFile(file);
        }
        catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }
    
    public void setNotificationExpanded() {
        this.addEvent(14);
    }
    
    public void setQsExpanded(final boolean b) {
        if (b) {
            this.addEvent(7);
        }
        else {
            this.addEvent(8);
        }
    }
}
