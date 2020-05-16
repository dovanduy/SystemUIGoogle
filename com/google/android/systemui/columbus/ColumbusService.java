// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.os.SystemClock;
import android.metrics.LogMaker;
import kotlin.TypeCastException;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.Log;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import com.android.internal.logging.MetricsLogger;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.Set;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import com.android.systemui.Dumpable;

public class ColumbusService implements Dumpable
{
    private final ColumbusService$actionListener.ColumbusService$actionListener$1 actionListener;
    private final List<Action> actions;
    private final Set<FeedbackEffect> effects;
    private final ColumbusService$gateListener.ColumbusService$gateListener$1 gateListener;
    private final Set<Gate> gates;
    private final GestureSensor.Listener gestureListener;
    private final GestureSensor gestureSensor;
    private Action lastActiveAction;
    private long lastProgressGesture;
    private int lastStage;
    private final MetricsLogger logger;
    private final PowerManagerWrapper powerManager;
    private final PowerManagerWrapper.WakeLockWrapper wakeLock;
    
    public ColumbusService(final List<Action> actions, final Set<FeedbackEffect> effects, final Set<Gate> gates, final GestureSensor gestureSensor, final PowerManagerWrapper powerManager, final MetricsLogger logger) {
        Intrinsics.checkParameterIsNotNull(actions, "actions");
        Intrinsics.checkParameterIsNotNull(effects, "effects");
        Intrinsics.checkParameterIsNotNull(gates, "gates");
        Intrinsics.checkParameterIsNotNull(gestureSensor, "gestureSensor");
        Intrinsics.checkParameterIsNotNull(powerManager, "powerManager");
        Intrinsics.checkParameterIsNotNull(logger, "logger");
        this.actions = actions;
        this.effects = effects;
        this.gates = gates;
        this.gestureSensor = gestureSensor;
        this.powerManager = powerManager;
        this.logger = logger;
        this.wakeLock = powerManager.newWakeLock(1, "Columbus/Service");
        this.actionListener = new ColumbusService$actionListener.ColumbusService$actionListener$1(this);
        this.gateListener = new ColumbusService$gateListener.ColumbusService$gateListener$1(this);
        this.gestureListener = new GestureListener();
        final Iterator<Action> iterator = this.actions.iterator();
        while (iterator.hasNext()) {
            iterator.next().setListener((Action.Listener)this.actionListener);
        }
        final Iterator<Gate> iterator2 = this.gates.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().setListener((Gate.Listener)this.gateListener);
        }
        this.gestureSensor.setGestureListener(this.gestureListener);
        this.updateSensorListener();
    }
    
    public static final /* synthetic */ Set access$getEffects$p(final ColumbusService columbusService) {
        return columbusService.effects;
    }
    
    public static final /* synthetic */ long access$getLastProgressGesture$p(final ColumbusService columbusService) {
        return columbusService.lastProgressGesture;
    }
    
    public static final /* synthetic */ int access$getLastStage$p(final ColumbusService columbusService) {
        return columbusService.lastStage;
    }
    
    public static final /* synthetic */ MetricsLogger access$getLogger$p(final ColumbusService columbusService) {
        return columbusService.logger;
    }
    
    public static final /* synthetic */ PowerManagerWrapper access$getPowerManager$p(final ColumbusService columbusService) {
        return columbusService.powerManager;
    }
    
    public static final /* synthetic */ PowerManagerWrapper.WakeLockWrapper access$getWakeLock$p(final ColumbusService columbusService) {
        return columbusService.wakeLock;
    }
    
    public static final /* synthetic */ void access$setLastProgressGesture$p(final ColumbusService columbusService, final long lastProgressGesture) {
        columbusService.lastProgressGesture = lastProgressGesture;
    }
    
    public static final /* synthetic */ void access$setLastStage$p(final ColumbusService columbusService, final int lastStage) {
        columbusService.lastStage = lastStage;
    }
    
    private final void activateGates() {
        final Iterator<Gate> iterator = this.gates.iterator();
        while (iterator.hasNext()) {
            iterator.next().activate();
        }
    }
    
    private final Gate blockingGate() {
        for (final Gate next : this.gates) {
            if (next.isBlocking()) {
                return next;
            }
        }
        return null;
    }
    
    private final void deactivateGates() {
        final Iterator<Gate> iterator = this.gates.iterator();
        while (iterator.hasNext()) {
            iterator.next().deactivate();
        }
    }
    
    private final Action firstAvailableAction() {
        for (final Action next : this.actions) {
            if (next.isAvailable()) {
                return next;
            }
        }
        return null;
    }
    
    private final void startListening() {
        if (!this.gestureSensor.isListening()) {
            this.gestureSensor.startListening(true);
        }
    }
    
    private final void stopListening() {
        if (this.gestureSensor.isListening()) {
            this.gestureSensor.stopListening();
            final Iterator<FeedbackEffect> iterator = this.effects.iterator();
            while (iterator.hasNext()) {
                iterator.next().onProgress(0, null);
            }
            final Action updateActiveAction = this.updateActiveAction();
            if (updateActiveAction != null) {
                updateActiveAction.onProgress(0, null);
            }
        }
    }
    
    private final Action updateActiveAction() {
        final Action firstAvailableAction = this.firstAvailableAction();
        final Action lastActiveAction = this.lastActiveAction;
        if (lastActiveAction != null && firstAvailableAction != lastActiveAction) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Switching action from ");
            sb.append(lastActiveAction);
            sb.append(" to ");
            sb.append(firstAvailableAction);
            Log.i("Columbus/Service", sb.toString());
            lastActiveAction.onProgress(0, null);
        }
        return this.lastActiveAction = firstAvailableAction;
    }
    
    private final void updateSensorListener() {
        final Action updateActiveAction = this.updateActiveAction();
        if (updateActiveAction == null) {
            Log.i("Columbus/Service", "No available actions");
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
            Log.i("Columbus/Service", sb.toString());
            this.stopListening();
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Unblocked; current action: ");
        sb2.append(updateActiveAction);
        Log.i("Columbus/Service", sb2.toString());
        this.startListening();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        final StringBuilder sb = new StringBuilder();
        sb.append(ColumbusService.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.println("  Gates:");
        final Iterator<Object> iterator = this.gates.iterator();
        while (true) {
            final boolean hasNext = iterator.hasNext();
            String s = "X ";
            if (!hasNext) {
                break;
            }
            final Gate gate = iterator.next();
            printWriter.print("    ");
            if (gate.getActive()) {
                if (!gate.isBlocking()) {
                    s = "O ";
                }
                printWriter.print(s);
            }
            else {
                printWriter.print("- ");
            }
            printWriter.println(gate.toString());
        }
        printWriter.println("  Actions:");
        for (final Action action : this.actions) {
            printWriter.print("    ");
            String s2;
            if (action.isAvailable()) {
                s2 = "O ";
            }
            else {
                s2 = "X ";
            }
            printWriter.print(s2);
            printWriter.println(action.toString());
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  Active: ");
        sb2.append(this.lastActiveAction);
        printWriter.println(sb2.toString());
        printWriter.println("  Feedback Effects:");
        for (final FeedbackEffect feedbackEffect : this.effects) {
            printWriter.print("    ");
            printWriter.println(feedbackEffect.toString());
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  Gesture Sensor: ");
        sb3.append(this.gestureSensor);
        printWriter.println(sb3.toString());
        final GestureSensor gestureSensor = this.gestureSensor;
        if (gestureSensor instanceof Dumpable) {
            if (gestureSensor == null) {
                throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.Dumpable");
            }
            ((Dumpable)gestureSensor).dump(fileDescriptor, printWriter, array);
        }
    }
    
    private final class GestureListener implements Listener
    {
        public GestureListener() {
        }
        
        private final void onGestureDetected(final DetectionProperties detectionProperties) {
            ColumbusService.access$getWakeLock$p(ColumbusService.this).acquire(2000);
            final boolean equal = Intrinsics.areEqual(ColumbusService.access$getPowerManager$p(ColumbusService.this).isInteractive(), Boolean.TRUE);
            int subtype = 1;
            if (detectionProperties != null && detectionProperties.isHostSuspended()) {
                subtype = 3;
            }
            else if (!equal) {
                subtype = 2;
            }
            final LogMaker setSubtype = new LogMaker(999).setType(4).setSubtype(subtype);
            long latency;
            if (equal) {
                latency = SystemClock.uptimeMillis() - ColumbusService.access$getLastProgressGesture$p(ColumbusService.this);
            }
            else {
                latency = 0L;
            }
            final LogMaker setLatency = setSubtype.setLatency(latency);
            ColumbusService.access$setLastProgressGesture$p(ColumbusService.this, 0L);
            final Action access$updateActiveAction = ColumbusService.this.updateActiveAction();
            if (access$updateActiveAction != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Triggering ");
                sb.append(access$updateActiveAction);
                Log.i("Columbus/Service", sb.toString());
                access$updateActiveAction.onProgress(3, detectionProperties);
                final Iterator iterator = ColumbusService.access$getEffects$p(ColumbusService.this).iterator();
                while (iterator.hasNext()) {
                    iterator.next().onProgress(3, detectionProperties);
                }
                Intrinsics.checkExpressionValueIsNotNull(setLatency, "logEntry");
                setLatency.setPackageName(access$updateActiveAction.getClass().getName());
            }
            ColumbusService.access$getLogger$p(ColumbusService.this).write(setLatency);
        }
        
        @Override
        public void onGestureProgress(final GestureSensor gestureSensor, final int n, final DetectionProperties detectionProperties) {
            Intrinsics.checkParameterIsNotNull(gestureSensor, "sensor");
            if (n == 3) {
                this.onGestureDetected(detectionProperties);
            }
            else {
                final Action access$updateActiveAction = ColumbusService.this.updateActiveAction();
                if (access$updateActiveAction != null) {
                    access$updateActiveAction.onProgress(n, detectionProperties);
                    final Iterator iterator = ColumbusService.access$getEffects$p(ColumbusService.this).iterator();
                    while (iterator.hasNext()) {
                        iterator.next().onProgress(n, detectionProperties);
                    }
                }
                if (n != ColumbusService.access$getLastStage$p(ColumbusService.this)) {
                    final long uptimeMillis = SystemClock.uptimeMillis();
                    if (n == 1) {
                        ColumbusService.access$getLogger$p(ColumbusService.this).action(998);
                        ColumbusService.access$setLastProgressGesture$p(ColumbusService.this, uptimeMillis);
                    }
                    else if (n == 0 && ColumbusService.access$getLastProgressGesture$p(ColumbusService.this) != 0L) {
                        ColumbusService.access$getLogger$p(ColumbusService.this).write(new LogMaker(997).setType(4).setLatency(uptimeMillis - ColumbusService.access$getLastProgressGesture$p(ColumbusService.this)));
                    }
                    ColumbusService.access$setLastStage$p(ColumbusService.this, n);
                }
            }
        }
    }
}
