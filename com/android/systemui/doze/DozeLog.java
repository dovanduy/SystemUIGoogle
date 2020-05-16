// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.util.TimeUtils;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.dump.DumpManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dumpable;

public class DozeLog implements Dumpable
{
    private SummaryStats mEmergencyCallStats;
    private final KeyguardUpdateMonitorCallback mKeyguardCallback;
    private final DozeLogger mLogger;
    private SummaryStats mNotificationPulseStats;
    private SummaryStats mPickupPulseNearVibrationStats;
    private SummaryStats mPickupPulseNotNearVibrationStats;
    private SummaryStats[][] mProxStats;
    private boolean mPulsing;
    private SummaryStats mScreenOnNotPulsingStats;
    private SummaryStats mScreenOnPulsingStats;
    private long mSince;
    
    public DozeLog(final KeyguardUpdateMonitor keyguardUpdateMonitor, final DumpManager dumpManager, final DozeLogger mLogger) {
        this.mKeyguardCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onEmergencyCallAction() {
                DozeLog.this.traceEmergencyCall();
            }
            
            @Override
            public void onFinishedGoingToSleep(final int n) {
                DozeLog.this.traceScreenOff(n);
            }
            
            @Override
            public void onKeyguardBouncerChanged(final boolean b) {
                DozeLog.this.traceKeyguardBouncerChanged(b);
            }
            
            @Override
            public void onKeyguardVisibilityChanged(final boolean b) {
                DozeLog.this.traceKeyguard(b);
            }
            
            @Override
            public void onStartedWakingUp() {
                DozeLog.this.traceScreenOn();
            }
        };
        this.mLogger = mLogger;
        this.mSince = System.currentTimeMillis();
        this.mPickupPulseNearVibrationStats = new SummaryStats();
        this.mPickupPulseNotNearVibrationStats = new SummaryStats();
        this.mNotificationPulseStats = new SummaryStats();
        this.mScreenOnPulsingStats = new SummaryStats();
        this.mScreenOnNotPulsingStats = new SummaryStats();
        this.mEmergencyCallStats = new SummaryStats();
        this.mProxStats = new SummaryStats[10][2];
        for (int i = 0; i < 10; ++i) {
            this.mProxStats[i][0] = new SummaryStats();
            this.mProxStats[i][1] = new SummaryStats();
        }
        if (keyguardUpdateMonitor != null) {
            keyguardUpdateMonitor.registerCallback(this.mKeyguardCallback);
        }
        dumpManager.registerDumpable("DumpStats", this);
    }
    
    public static String reasonToString(final int i) {
        switch (i) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("invalid reason: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
            }
            case 9: {
                return "tap";
            }
            case 8: {
                return "wakelockscreen";
            }
            case 7: {
                return "wakeup";
            }
            case 6: {
                return "docking";
            }
            case 5: {
                return "longpress";
            }
            case 4: {
                return "doubletap";
            }
            case 3: {
                return "pickup";
            }
            case 2: {
                return "sigmotion";
            }
            case 1: {
                return "notification";
            }
            case 0: {
                return "intent";
            }
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        synchronized (DozeLog.class) {
            printWriter.print("  Doze summary stats (for ");
            TimeUtils.formatDuration(System.currentTimeMillis() - this.mSince, printWriter);
            printWriter.println("):");
            this.mPickupPulseNearVibrationStats.dump(printWriter, "Pickup pulse (near vibration)");
            this.mPickupPulseNotNearVibrationStats.dump(printWriter, "Pickup pulse (not near vibration)");
            this.mNotificationPulseStats.dump(printWriter, "Notification pulse");
            this.mScreenOnPulsingStats.dump(printWriter, "Screen on (pulsing)");
            this.mScreenOnNotPulsingStats.dump(printWriter, "Screen on (not pulsing)");
            this.mEmergencyCallStats.dump(printWriter, "Emergency call");
            for (int i = 0; i < 10; ++i) {
                final String reasonToString = reasonToString(i);
                final SummaryStats summaryStats = this.mProxStats[i][0];
                final StringBuilder sb = new StringBuilder();
                sb.append("Proximity near (");
                sb.append(reasonToString);
                sb.append(")");
                summaryStats.dump(printWriter, sb.toString());
                final SummaryStats summaryStats2 = this.mProxStats[i][1];
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Proximity far (");
                sb2.append(reasonToString);
                sb2.append(")");
                summaryStats2.dump(printWriter, sb2.toString());
            }
        }
    }
    
    public void traceDozeSuppressed(final DozeMachine.State state) {
        this.mLogger.logDozeSuppressed(state);
    }
    
    public void traceDozing(final boolean b) {
        this.mLogger.logDozing(b);
        this.mPulsing = false;
    }
    
    public void traceEmergencyCall() {
        this.mLogger.logEmergencyCall();
        this.mEmergencyCallStats.append();
    }
    
    public void traceFling(final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        this.mLogger.logFling(b, b2, b3, b4);
    }
    
    public void traceKeyguard(final boolean b) {
        this.mLogger.logKeyguardVisibilityChange(b);
        if (!b) {
            this.mPulsing = false;
        }
    }
    
    public void traceKeyguardBouncerChanged(final boolean b) {
        this.mLogger.logKeyguardBouncerChanged(b);
    }
    
    public void traceMissedTick(final String s) {
        this.mLogger.logMissedTick(s);
    }
    
    public void traceNotificationPulse() {
        this.mLogger.logNotificationPulse();
        this.mNotificationPulseStats.append();
    }
    
    public void tracePickupWakeUp(final boolean b) {
        this.mLogger.logPickupWakeup(b);
        SummaryStats summaryStats;
        if (b) {
            summaryStats = this.mPickupPulseNearVibrationStats;
        }
        else {
            summaryStats = this.mPickupPulseNotNearVibrationStats;
        }
        summaryStats.append();
    }
    
    public void traceProximityResult(final boolean b, final long n, final int n2) {
        this.mLogger.logProximityResult(b, n, n2);
        this.mProxStats[n2][b ^ true].append();
    }
    
    public void tracePulseDropped(final String s) {
        this.mLogger.logPulseDropped(s);
    }
    
    public void tracePulseDropped(final boolean b, final DozeMachine.State state, final boolean b2) {
        this.mLogger.logPulseDropped(b, state, b2);
    }
    
    public void tracePulseFinish() {
        this.mLogger.logPulseFinish();
        this.mPulsing = false;
    }
    
    public void tracePulseStart(final int n) {
        this.mLogger.logPulseStart(n);
        this.mPulsing = true;
    }
    
    public void tracePulseTouchDisabledByProx(final boolean b) {
        this.mLogger.logPulseTouchDisabledByProx(b);
    }
    
    public void traceScreenOff(final int n) {
        this.mLogger.logScreenOff(n);
    }
    
    public void traceScreenOn() {
        this.mLogger.logScreenOn(this.mPulsing);
        SummaryStats summaryStats;
        if (this.mPulsing) {
            summaryStats = this.mScreenOnPulsingStats;
        }
        else {
            summaryStats = this.mScreenOnNotPulsingStats;
        }
        summaryStats.append();
        this.mPulsing = false;
    }
    
    public void traceSensor(final int n) {
        this.mLogger.logSensorTriggered(n);
    }
    
    public void traceState(final DozeMachine.State state) {
        this.mLogger.logDozeStateChanged(state);
    }
    
    public void traceTimeTickScheduled(final long n, final long n2) {
        this.mLogger.logTimeTickScheduled(n, n2);
    }
    
    public void traceWakeDisplay(final boolean b) {
        this.mLogger.logWakeDisplay(b);
    }
    
    private class SummaryStats
    {
        private int mCount;
        
        public void append() {
            ++this.mCount;
        }
        
        public void dump(final PrintWriter printWriter, final String s) {
            if (this.mCount == 0) {
                return;
            }
            printWriter.print("    ");
            printWriter.print(s);
            printWriter.print(": n=");
            printWriter.print(this.mCount);
            printWriter.print(" (");
            printWriter.print(this.mCount / (double)(System.currentTimeMillis() - DozeLog.this.mSince) * 1000.0 * 60.0 * 60.0);
            printWriter.print("/hr)");
            printWriter.println();
        }
    }
}
