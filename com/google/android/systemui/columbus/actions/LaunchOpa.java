// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import android.os.Bundle;
import android.content.ContentResolver;
import kotlin.Unit;
import android.net.Uri;
import kotlin.jvm.functions.Function1;
import android.provider.Settings$Secure;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.assist.AssistManager;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.Set;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.assist.OpaEnabledListener;
import android.app.KeyguardManager;
import com.google.android.systemui.assist.AssistManagerGoogle;
import com.android.systemui.tuner.TunerService;

public class LaunchOpa extends Action implements Tunable
{
    private final AssistManagerGoogle assistManager;
    private boolean enableForAnyAssistant;
    private boolean isGestureEnabled;
    private boolean isOpaEnabled;
    private final KeyguardManager keyguardManager;
    private final OpaEnabledListener opaEnabledListener;
    private final ColumbusContentObserver settingsObserver;
    private final StatusBar statusBar;
    
    public LaunchOpa(final Context context, final StatusBar statusBar, final Set<FeedbackEffect> set, final AssistManager assistManager, final TunerService tunerService, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(statusBar, "statusBar");
        Intrinsics.checkParameterIsNotNull(set, "feedbackEffects");
        Intrinsics.checkParameterIsNotNull(assistManager, "assistManager");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, CollectionsKt.toList((Iterable<? extends FeedbackEffect>)set));
        this.statusBar = statusBar;
        AssistManager assistManager2 = assistManager;
        if (!(assistManager instanceof AssistManagerGoogle)) {
            assistManager2 = null;
        }
        this.assistManager = (AssistManagerGoogle)assistManager2;
        this.keyguardManager = (KeyguardManager)context.getSystemService("keyguard");
        this.opaEnabledListener = (OpaEnabledListener)new LaunchOpa$opaEnabledListener.LaunchOpa$opaEnabledListener$1(this);
        final Uri uri = Settings$Secure.getUriFor("assist_gesture_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uri, "Settings.Secure.getUriFo\u2026e.ASSIST_GESTURE_ENABLED)");
        this.settingsObserver = factory.create(uri, (Function1<? super Uri, Unit>)new LaunchOpa$settingsObserver.LaunchOpa$settingsObserver$1(this));
        this.isGestureEnabled = this.fetchIsGestureEnabled();
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean enableForAnyAssistant = false;
        if (Settings$Secure.getInt(contentResolver, "assist_gesture_any_assistant", 0) == 1) {
            enableForAnyAssistant = true;
        }
        this.enableForAnyAssistant = enableForAnyAssistant;
        this.settingsObserver.activate();
        tunerService.addTunable((TunerService.Tunable)this, "assist_gesture_any_assistant");
        final AssistManagerGoogle assistManager3 = this.assistManager;
        if (assistManager3 != null) {
            assistManager3.addOpaEnabledListener(this.opaEnabledListener);
        }
    }
    
    private final boolean fetchIsGestureEnabled() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = true;
        if (Settings$Secure.getIntForUser(contentResolver, "assist_gesture_enabled", 1, -2) == 0) {
            b = false;
        }
        return b;
    }
    
    private final void launchOpa() {
        this.launchOpa(0L);
    }
    
    private final void launchOpa(final long n) {
        final Bundle bundle = new Bundle();
        final KeyguardManager keyguardManager = this.keyguardManager;
        int n2;
        if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
            n2 = 14;
        }
        else {
            n2 = 13;
        }
        bundle.putInt("triggered_by", n2);
        bundle.putLong("latency_id", n);
        bundle.putInt("invocation_type", 2);
        final AssistManagerGoogle assistManager = this.assistManager;
        if (assistManager != null) {
            assistManager.startAssist(bundle);
        }
    }
    
    private final void updateGestureEnabled() {
        final boolean fetchIsGestureEnabled = this.fetchIsGestureEnabled();
        if (this.isGestureEnabled != fetchIsGestureEnabled) {
            this.isGestureEnabled = fetchIsGestureEnabled;
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.isGestureEnabled && this.isOpaEnabled;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        this.updateFeedbackEffects(n, detectionProperties);
        if (n == 3) {
            this.statusBar.collapseShade();
            long actionId;
            if (detectionProperties != null) {
                actionId = detectionProperties.getActionId();
            }
            else {
                actionId = 0L;
            }
            this.launchOpa(actionId);
        }
    }
    
    @Override
    public void onTrigger() {
        this.launchOpa();
    }
    
    @Override
    public void onTuningChanged(final String s, final String s2) {
        Intrinsics.checkParameterIsNotNull(s, "key");
        if (Intrinsics.areEqual("assist_gesture_any_assistant", s)) {
            this.enableForAnyAssistant = Intrinsics.areEqual("1", s2);
            final AssistManagerGoogle assistManager = this.assistManager;
            if (assistManager != null) {
                assistManager.dispatchOpaEnabledState();
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isGestureEnabled -> ");
        sb.append(this.isGestureEnabled);
        sb.append("; isOpaEnabled -> ");
        sb.append(this.isOpaEnabled);
        sb.append("]");
        return sb.toString();
    }
}
