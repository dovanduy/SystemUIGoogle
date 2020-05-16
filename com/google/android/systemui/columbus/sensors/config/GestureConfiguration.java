// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors.config;

import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import java.util.Iterator;
import android.net.Uri;
import android.provider.Settings$Secure;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import java.util.Set;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import android.content.Context;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import android.util.Range;

public final class GestureConfiguration
{
    private static final Range<Float> SENSITIVITY_RANGE;
    private final Function1<Adjustment, Unit> adjustmentCallback;
    private final List<Adjustment> adjustmentsList;
    private final Context context;
    private Listener listener;
    private float sensitivity;
    private final ColumbusContentObserver settingsObserver;
    
    static {
        SENSITIVITY_RANGE = Range.create((Comparable)0.0f, (Comparable)1.0f);
    }
    
    public GestureConfiguration(final Context context, final Set<Adjustment> set, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(set, "adjustments");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        this.context = context;
        this.adjustmentsList = CollectionsKt.toList((Iterable<? extends Adjustment>)set);
        this.adjustmentCallback = (Function1<Adjustment, Unit>)new GestureConfiguration$adjustmentCallback.GestureConfiguration$adjustmentCallback$1(this);
        final Uri uri = Settings$Secure.getUriFor("assist_gesture_sensitivity");
        Intrinsics.checkExpressionValueIsNotNull(uri, "Settings.Secure.getUriFo\u2026SIST_GESTURE_SENSITIVITY)");
        this.settingsObserver = factory.create(uri, (Function1<? super Uri, Unit>)new GestureConfiguration$settingsObserver.GestureConfiguration$settingsObserver$1(this));
        this.sensitivity = this.getUserSensitivity();
        this.settingsObserver.activate();
        final Iterator<Adjustment> iterator = this.adjustmentsList.iterator();
        while (iterator.hasNext()) {
            iterator.next().setCallback(this.adjustmentCallback);
        }
    }
    
    private final float getUserSensitivity() {
        Float value;
        if (!GestureConfiguration.SENSITIVITY_RANGE.contains((Comparable)(value = DejankUtils.whitelistIpcs((Supplier<Float>)new GestureConfiguration$getUserSensitivity$sensitivity.GestureConfiguration$getUserSensitivity$sensitivity$1(this))))) {
            value = 0.5f;
        }
        Intrinsics.checkExpressionValueIsNotNull(value, "sensitivity");
        return value;
    }
    
    public final float getSensitivity() {
        float n = this.sensitivity;
        final Iterator<Adjustment> iterator = this.adjustmentsList.iterator();
        while (iterator.hasNext()) {
            final Comparable clamp = GestureConfiguration.SENSITIVITY_RANGE.clamp((Comparable)iterator.next().adjustSensitivity(n));
            Intrinsics.checkExpressionValueIsNotNull(clamp, "SENSITIVITY_RANGE.clamp(\u2026Sensitivity(sensitivity))");
            n = ((Number)clamp).floatValue();
        }
        return n;
    }
    
    public final void onSensitivityChanged() {
        this.sensitivity = this.getUserSensitivity();
        final Listener listener = this.listener;
        if (listener != null) {
            listener.onGestureConfigurationChanged(this);
        }
    }
    
    public final void setListener(final Listener listener) {
        this.listener = listener;
    }
    
    public interface Listener
    {
        void onGestureConfigurationChanged(final GestureConfiguration p0);
    }
}
