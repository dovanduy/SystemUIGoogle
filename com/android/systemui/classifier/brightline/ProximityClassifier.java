// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
import com.android.systemui.util.sensors.ProximitySensor;
import java.util.Locale;
import com.android.systemui.util.DeviceConfigProxy;

class ProximityClassifier extends FalsingClassifier
{
    private final DistanceClassifier mDistanceClassifier;
    private long mGestureStartTimeNs;
    private boolean mNear;
    private long mNearDurationNs;
    private final float mPercentCoveredThreshold;
    private float mPercentNear;
    private long mPrevNearTimeNs;
    
    ProximityClassifier(final DistanceClassifier mDistanceClassifier, final FalsingDataProvider falsingDataProvider, final DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        this.mDistanceClassifier = mDistanceClassifier;
        this.mPercentCoveredThreshold = deviceConfigProxy.getFloat("systemui", "brightline_falsing_proximity_percent_covered_threshold", 0.1f);
    }
    
    private void update(final boolean mNear, final long n) {
        final long mPrevNearTimeNs = this.mPrevNearTimeNs;
        if (mPrevNearTimeNs != 0L && n > mPrevNearTimeNs && this.mNear) {
            this.mNearDurationNs += n - mPrevNearTimeNs;
            final StringBuilder sb = new StringBuilder();
            sb.append("Updating duration: ");
            sb.append(this.mNearDurationNs);
            FalsingClassifier.logDebug(sb.toString());
        }
        if (mNear) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Set prevNearTimeNs: ");
            sb2.append(n);
            FalsingClassifier.logDebug(sb2.toString());
            this.mPrevNearTimeNs = n;
        }
        this.mNear = mNear;
    }
    
    @Override
    String getReason() {
        return String.format(null, "{percentInProximity=%f, threshold=%f, distanceClassifier=%s}", this.mPercentNear, this.mPercentCoveredThreshold, this.mDistanceClassifier.getReason());
    }
    
    public boolean isFalseTouch() {
        if (this.getInteractionType() == 0) {
            return false;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Percent of gesture in proximity: ");
        sb.append(this.mPercentNear);
        FalsingClassifier.logInfo(sb.toString());
        return this.mPercentNear > this.mPercentCoveredThreshold && (this.mDistanceClassifier.isLongSwipe() ^ true);
    }
    
    public void onProximityEvent(final ProximitySensor.ProximityEvent proximityEvent) {
        final boolean near = proximityEvent.getNear();
        final long timestampNs = proximityEvent.getTimestampNs();
        final StringBuilder sb = new StringBuilder();
        sb.append("Sensor is: ");
        sb.append(near);
        sb.append(" at time ");
        sb.append(timestampNs);
        FalsingClassifier.logDebug(sb.toString());
        this.update(near, timestampNs);
    }
    
    @Override
    void onSessionEnded() {
        this.mPrevNearTimeNs = 0L;
        this.mPercentNear = 0.0f;
    }
    
    @Override
    void onSessionStarted() {
        this.mPrevNearTimeNs = 0L;
        this.mPercentNear = 0.0f;
    }
    
    public void onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mGestureStartTimeNs = motionEvent.getEventTimeNano();
            if (this.mPrevNearTimeNs > 0L) {
                this.mPrevNearTimeNs = motionEvent.getEventTimeNano();
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Gesture start time: ");
            sb.append(this.mGestureStartTimeNs);
            FalsingClassifier.logDebug(sb.toString());
            this.mNearDurationNs = 0L;
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.update(this.mNear, motionEvent.getEventTimeNano());
            final long lng = motionEvent.getEventTimeNano() - this.mGestureStartTimeNs;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Gesture duration, Proximity duration: ");
            sb2.append(lng);
            sb2.append(", ");
            sb2.append(this.mNearDurationNs);
            FalsingClassifier.logDebug(sb2.toString());
            if (lng == 0L) {
                float mPercentNear;
                if (this.mNear) {
                    mPercentNear = 1.0f;
                }
                else {
                    mPercentNear = 0.0f;
                }
                this.mPercentNear = mPercentNear;
            }
            else {
                this.mPercentNear = this.mNearDurationNs / (float)lng;
            }
        }
    }
}
