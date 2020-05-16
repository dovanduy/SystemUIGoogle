// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.Iterator;
import java.util.ArrayList;
import android.content.Context;
import android.hardware.SensorPrivacyManager;
import java.util.List;
import android.hardware.SensorPrivacyManager$OnSensorPrivacyChangedListener;

public class SensorPrivacyControllerImpl implements SensorPrivacyController, SensorPrivacyManager$OnSensorPrivacyChangedListener
{
    private final List<OnSensorPrivacyChangedListener> mListeners;
    private Object mLock;
    private boolean mSensorPrivacyEnabled;
    private SensorPrivacyManager mSensorPrivacyManager;
    
    public SensorPrivacyControllerImpl(final Context context) {
        this.mLock = new Object();
        final SensorPrivacyManager mSensorPrivacyManager = (SensorPrivacyManager)context.getSystemService("sensor_privacy");
        this.mSensorPrivacyManager = mSensorPrivacyManager;
        this.mSensorPrivacyEnabled = mSensorPrivacyManager.isSensorPrivacyEnabled();
        this.mSensorPrivacyManager.addSensorPrivacyListener((SensorPrivacyManager$OnSensorPrivacyChangedListener)this);
        this.mListeners = new ArrayList<OnSensorPrivacyChangedListener>(1);
    }
    
    private void notifyListenerLocked(final OnSensorPrivacyChangedListener onSensorPrivacyChangedListener) {
        onSensorPrivacyChangedListener.onSensorPrivacyChanged(this.mSensorPrivacyEnabled);
    }
    
    public void addCallback(final OnSensorPrivacyChangedListener onSensorPrivacyChangedListener) {
        synchronized (this.mLock) {
            this.mListeners.add(onSensorPrivacyChangedListener);
            this.notifyListenerLocked(onSensorPrivacyChangedListener);
        }
    }
    
    @Override
    public boolean isSensorPrivacyEnabled() {
        synchronized (this.mLock) {
            return this.mSensorPrivacyEnabled;
        }
    }
    
    public void onSensorPrivacyChanged(final boolean mSensorPrivacyEnabled) {
        synchronized (this.mLock) {
            this.mSensorPrivacyEnabled = mSensorPrivacyEnabled;
            final Iterator<OnSensorPrivacyChangedListener> iterator = this.mListeners.iterator();
            while (iterator.hasNext()) {
                this.notifyListenerLocked(iterator.next());
            }
        }
    }
    
    public void removeCallback(final OnSensorPrivacyChangedListener onSensorPrivacyChangedListener) {
        synchronized (this.mLock) {
            this.mListeners.remove(onSensorPrivacyChangedListener);
        }
    }
}
