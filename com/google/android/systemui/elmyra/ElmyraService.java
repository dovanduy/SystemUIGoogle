// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import android.os.SystemClock;
import android.metrics.LogMaker;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.Log;
import java.util.function.Consumer;
import java.util.Collection;
import java.util.ArrayList;
import android.os.PowerManager$WakeLock;
import android.os.PowerManager;
import com.android.internal.logging.MetricsLogger;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import android.content.Context;
import java.util.List;
import com.google.android.systemui.elmyra.actions.Action;
import com.android.systemui.Dumpable;

public class ElmyraService implements Dumpable
{
    private final Action.Listener mActionListener;
    private final List<Action> mActions;
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects;
    private final Gate.Listener mGateListener;
    private final List<Gate> mGates;
    private final GestureSensor.Listener mGestureListener;
    private final GestureSensor mGestureSensor;
    private Action mLastActiveAction;
    private long mLastPrimedGesture;
    private int mLastStage;
    private final MetricsLogger mLogger;
    private final PowerManager mPowerManager;
    private final PowerManager$WakeLock mWakeLock;
    
    public ElmyraService(final Context mContext, final ServiceConfiguration serviceConfiguration) {
        this.mActionListener = new Action.Listener() {
            @Override
            public void onActionAvailabilityChanged(final Action action) {
                ElmyraService.this.updateSensorListener();
            }
        };
        this.mGateListener = new Gate.Listener() {
            @Override
            public void onGateChanged(final Gate gate) {
                ElmyraService.this.updateSensorListener();
            }
        };
        this.mGestureListener = new GestureListener();
        this.mContext = mContext;
        this.mLogger = new MetricsLogger();
        final PowerManager mPowerManager = (PowerManager)this.mContext.getSystemService("power");
        this.mPowerManager = mPowerManager;
        this.mWakeLock = mPowerManager.newWakeLock(1, "Elmyra/ElmyraService");
        (this.mActions = new ArrayList<Action>(serviceConfiguration.getActions())).forEach(new _$$Lambda$ElmyraService$AV8onMO5IkvT88F5MAxNGAFWl18(this));
        this.mFeedbackEffects = new ArrayList<FeedbackEffect>(serviceConfiguration.getFeedbackEffects());
        (this.mGates = new ArrayList<Gate>(serviceConfiguration.getGates())).forEach(new _$$Lambda$ElmyraService$BALyMaTEhjk9LjmmSMkHO_yFKc4(this));
        final GestureSensor gestureSensor = serviceConfiguration.getGestureSensor();
        this.mGestureSensor = gestureSensor;
        if (gestureSensor != null) {
            gestureSensor.setGestureListener(this.mGestureListener);
        }
        this.updateSensorListener();
    }
    
    private void activateGates() {
        for (int i = 0; i < this.mGates.size(); ++i) {
            this.mGates.get(i).activate();
        }
    }
    
    private Gate blockingGate() {
        for (int i = 0; i < this.mGates.size(); ++i) {
            if (this.mGates.get(i).isBlocking()) {
                return this.mGates.get(i);
            }
        }
        return null;
    }
    
    private void deactivateGates() {
        for (int i = 0; i < this.mGates.size(); ++i) {
            this.mGates.get(i).deactivate();
        }
    }
    
    private Action firstAvailableAction() {
        for (int i = 0; i < this.mActions.size(); ++i) {
            if (this.mActions.get(i).isAvailable()) {
                return this.mActions.get(i);
            }
        }
        return null;
    }
    
    private void startListening() {
        final GestureSensor mGestureSensor = this.mGestureSensor;
        if (mGestureSensor != null && !mGestureSensor.isListening()) {
            this.mGestureSensor.startListening();
        }
    }
    
    private void stopListening() {
        final GestureSensor mGestureSensor = this.mGestureSensor;
        if (mGestureSensor != null && mGestureSensor.isListening()) {
            this.mGestureSensor.stopListening();
            for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
                this.mFeedbackEffects.get(i).onRelease();
            }
            final Action updateActiveAction = this.updateActiveAction();
            if (updateActiveAction != null) {
                updateActiveAction.onProgress(0.0f, 0);
            }
        }
    }
    
    private Action updateActiveAction() {
        final Action firstAvailableAction = this.firstAvailableAction();
        final Action mLastActiveAction = this.mLastActiveAction;
        if (mLastActiveAction != null && firstAvailableAction != mLastActiveAction) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Switching action from ");
            sb.append(this.mLastActiveAction);
            sb.append(" to ");
            sb.append(firstAvailableAction);
            Log.i("Elmyra/ElmyraService", sb.toString());
            this.mLastActiveAction.onProgress(0.0f, 0);
        }
        return this.mLastActiveAction = firstAvailableAction;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ElmyraService.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.println("  Gates:");
        final int n = 0;
        int n2 = 0;
        while (true) {
            final int size = this.mGates.size();
            String s = "X ";
            if (n2 >= size) {
                break;
            }
            printWriter.print("    ");
            if (this.mGates.get(n2).isActive()) {
                if (!this.mGates.get(n2).isBlocking()) {
                    s = "O ";
                }
                printWriter.print(s);
            }
            else {
                printWriter.print("- ");
            }
            printWriter.println(this.mGates.get(n2).toString());
            ++n2;
        }
        printWriter.println("  Actions:");
        for (int i = 0; i < this.mActions.size(); ++i) {
            printWriter.print("    ");
            String s2;
            if (this.mActions.get(i).isAvailable()) {
                s2 = "O ";
            }
            else {
                s2 = "X ";
            }
            printWriter.print(s2);
            printWriter.println(this.mActions.get(i).toString());
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  Active: ");
        sb2.append(this.mLastActiveAction);
        printWriter.println(sb2.toString());
        printWriter.println("  Feedback Effects:");
        for (int j = n; j < this.mFeedbackEffects.size(); ++j) {
            printWriter.print("    ");
            printWriter.println(this.mFeedbackEffects.get(j).toString());
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  Gesture Sensor: ");
        sb3.append(this.mGestureSensor.toString());
        printWriter.println(sb3.toString());
        final GestureSensor mGestureSensor = this.mGestureSensor;
        if (mGestureSensor instanceof Dumpable) {
            ((Dumpable)mGestureSensor).dump(fileDescriptor, printWriter, array);
        }
    }
    
    protected void updateSensorListener() {
        final Action updateActiveAction = this.updateActiveAction();
        if (updateActiveAction == null) {
            Log.i("Elmyra/ElmyraService", "No available actions");
            this.deactivateGates();
            this.stopListening();
            return;
        }
        this.activateGates();
        final Gate blockingGate = this.blockingGate();
        if (blockingGate != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Gated by ");
            sb.append(blockingGate);
            Log.i("Elmyra/ElmyraService", sb.toString());
            this.stopListening();
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Unblocked; current action: ");
        sb2.append(updateActiveAction);
        Log.i("Elmyra/ElmyraService", sb2.toString());
        this.startListening();
    }
    
    private class GestureListener implements Listener
    {
        @Override
        public void onGestureDetected(final GestureSensor gestureSensor, final DetectionProperties detectionProperties) {
            ElmyraService.this.mWakeLock.acquire(2000L);
            final boolean interactive = ElmyraService.this.mPowerManager.isInteractive();
            int subtype;
            if (detectionProperties != null && detectionProperties.isHostSuspended()) {
                subtype = 3;
            }
            else if (!interactive) {
                subtype = 2;
            }
            else {
                subtype = 1;
            }
            final LogMaker setSubtype = new LogMaker(999).setType(4).setSubtype(subtype);
            long latency;
            if (interactive) {
                latency = SystemClock.uptimeMillis() - ElmyraService.this.mLastPrimedGesture;
            }
            else {
                latency = 0L;
            }
            final LogMaker setLatency = setSubtype.setLatency(latency);
            ElmyraService.this.mLastPrimedGesture = 0L;
            final Action access$100 = ElmyraService.this.updateActiveAction();
            if (access$100 != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Triggering ");
                sb.append(access$100);
                Log.i("Elmyra/ElmyraService", sb.toString());
                access$100.onTrigger(detectionProperties);
                for (int i = 0; i < ElmyraService.this.mFeedbackEffects.size(); ++i) {
                    ((FeedbackEffect)ElmyraService.this.mFeedbackEffects.get(i)).onResolve(detectionProperties);
                }
                setLatency.setPackageName(access$100.getClass().getName());
            }
            ElmyraService.this.mLogger.write(setLatency);
        }
        
        @Override
        public void onGestureProgress(final GestureSensor gestureSensor, final float n, final int n2) {
            final Action access$100 = ElmyraService.this.updateActiveAction();
            if (access$100 != null) {
                access$100.onProgress(n, n2);
                for (int i = 0; i < ElmyraService.this.mFeedbackEffects.size(); ++i) {
                    ((FeedbackEffect)ElmyraService.this.mFeedbackEffects.get(i)).onProgress(n, n2);
                }
            }
            if (n2 != ElmyraService.this.mLastStage) {
                final long uptimeMillis = SystemClock.uptimeMillis();
                if (n2 == 2) {
                    ElmyraService.this.mLogger.action(998);
                    ElmyraService.this.mLastPrimedGesture = uptimeMillis;
                }
                else if (n2 == 0 && ElmyraService.this.mLastPrimedGesture != 0L) {
                    ElmyraService.this.mLogger.write(new LogMaker(997).setType(4).setLatency(uptimeMillis - ElmyraService.this.mLastPrimedGesture));
                }
                ElmyraService.this.mLastStage = n2;
            }
        }
    }
}
