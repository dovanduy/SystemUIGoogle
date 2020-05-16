// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors.config;

import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.net.Uri;
import android.provider.Settings$Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import java.util.Collection;
import java.util.ArrayList;
import android.content.Context;
import java.util.List;
import java.util.function.Consumer;
import android.util.Range;

public class GestureConfiguration
{
    private static final Range<Float> SENSITIVITY_RANGE;
    private final Consumer<Adjustment> mAdjustmentCallback;
    private final List<Adjustment> mAdjustments;
    private final Context mContext;
    private Listener mListener;
    private float mSensitivity;
    
    static {
        SENSITIVITY_RANGE = Range.create((Comparable)0.0f, (Comparable)1.0f);
    }
    
    public GestureConfiguration(final Context mContext, final List<Adjustment> c) {
        this.mAdjustmentCallback = (Consumer<Adjustment>)new _$$Lambda$GestureConfiguration$3mm6FunisrpGZpM7qxO1no0tVbU(this);
        this.mContext = mContext;
        (this.mAdjustments = new ArrayList<Adjustment>(c)).forEach(new _$$Lambda$GestureConfiguration$F1rbWa9DGNKbISCQL2RDoKSl7Sw(this));
        new UserContentObserver(this.mContext, Settings$Secure.getUriFor("assist_gesture_sensitivity"), new _$$Lambda$GestureConfiguration$qyMZ0LytUPraF62LfdN_eAAd2vo(this));
        this.mSensitivity = this.getUserSensitivity();
    }
    
    private float getUserSensitivity() {
        float floatValue;
        if (!GestureConfiguration.SENSITIVITY_RANGE.contains((Comparable)(floatValue = DejankUtils.whitelistIpcs((Supplier<Float>)new _$$Lambda$GestureConfiguration$QLWAtX4EXmWvKAFiSGtiyMytNn4(this))))) {
            floatValue = 0.5f;
        }
        return floatValue;
    }
    
    public float getSensitivity() {
        float n = this.mSensitivity;
        for (int i = 0; i < this.mAdjustments.size(); ++i) {
            n = (float)GestureConfiguration.SENSITIVITY_RANGE.clamp((Comparable)this.mAdjustments.get(i).adjustSensitivity(n));
        }
        return n;
    }
    
    public void onSensitivityChanged() {
        this.mSensitivity = this.getUserSensitivity();
        final Listener mListener = this.mListener;
        if (mListener != null) {
            mListener.onGestureConfigurationChanged(this);
        }
    }
    
    public void setListener(final Listener mListener) {
        this.mListener = mListener;
    }
    
    public interface Listener
    {
        void onGestureConfigurationChanged(final GestureConfiguration p0);
    }
}
