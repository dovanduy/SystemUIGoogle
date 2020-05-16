// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import android.content.ContentResolver;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import android.util.Log;
import kotlin.Unit;
import android.net.Uri;
import kotlin.jvm.functions.Function1;
import android.provider.Settings$Secure;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import java.util.Optional;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.android.systemui.statusbar.policy.HeadsUpManager;

public final class UnpinNotifications extends Action
{
    private boolean hasPinnedHeadsUp;
    private final UnpinNotifications$headsUpChangedListener.UnpinNotifications$headsUpChangedListener$1 headsUpChangedListener;
    private HeadsUpManager headsUpManager;
    private ColumbusContentObserver settingsObserver;
    private boolean silenceSettingEnabled;
    
    public UnpinNotifications(final Optional<HeadsUpManager> optional, final Context context, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(optional, "headsUpManagerOptional");
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, null);
        this.headsUpChangedListener = new UnpinNotifications$headsUpChangedListener.UnpinNotifications$headsUpChangedListener$1(this);
        final Uri uri = Settings$Secure.getUriFor("assist_gesture_silence_alerts_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uri, "Settings.Secure.getUriFo\u2026E_SILENCE_ALERTS_ENABLED)");
        this.settingsObserver = factory.create(uri, (Function1<? super Uri, Unit>)new UnpinNotifications$settingsObserver.UnpinNotifications$settingsObserver$1(this));
        final HeadsUpManager headsUpManager = optional.orElse(null);
        this.headsUpManager = headsUpManager;
        if (headsUpManager != null) {
            this.updateHeadsUpListener();
            this.settingsObserver.activate();
        }
        else {
            Log.w("Columbus/UnpinNotifications", "No HeadsUpManager");
        }
    }
    
    private final void updateHeadsUpListener() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean silenceSettingEnabled = true;
        final int intForUser = Settings$Secure.getIntForUser(contentResolver, "assist_gesture_silence_alerts_enabled", 1, -2);
        final boolean b = false;
        if (intForUser == 0) {
            silenceSettingEnabled = false;
        }
        if (this.silenceSettingEnabled != silenceSettingEnabled) {
            this.silenceSettingEnabled = silenceSettingEnabled;
            if (silenceSettingEnabled) {
                final HeadsUpManager headsUpManager = this.headsUpManager;
                boolean hasPinnedHeadsUp = b;
                if (headsUpManager != null) {
                    hasPinnedHeadsUp = headsUpManager.hasPinnedHeadsUp();
                }
                this.hasPinnedHeadsUp = hasPinnedHeadsUp;
                final HeadsUpManager headsUpManager2 = this.headsUpManager;
                if (headsUpManager2 != null) {
                    headsUpManager2.addListener((OnHeadsUpChangedListener)this.headsUpChangedListener);
                }
            }
            else {
                this.hasPinnedHeadsUp = false;
                final HeadsUpManager headsUpManager3 = this.headsUpManager;
                if (headsUpManager3 != null) {
                    headsUpManager3.removeListener((OnHeadsUpChangedListener)this.headsUpChangedListener);
                }
            }
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.silenceSettingEnabled && this.hasPinnedHeadsUp;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        super.onProgress(n, detectionProperties);
        if (n == 3) {
            final HeadsUpManager headsUpManager = this.headsUpManager;
            if (headsUpManager != null) {
                headsUpManager.unpinAll(true);
            }
        }
    }
}
