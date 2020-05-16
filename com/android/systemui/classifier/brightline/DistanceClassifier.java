// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import java.util.Locale;
import java.util.Iterator;
import java.util.List;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.android.systemui.util.DeviceConfigProxy;

class DistanceClassifier extends FalsingClassifier
{
    private DistanceVectors mCachedDistance;
    private boolean mDistanceDirty;
    private final float mHorizontalFlingThresholdPx;
    private final float mHorizontalSwipeThresholdPx;
    private final float mVelocityToDistanceMultiplier;
    private final float mVerticalFlingThresholdPx;
    private final float mVerticalSwipeThresholdPx;
    
    DistanceClassifier(final FalsingDataProvider falsingDataProvider, final DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        this.mVelocityToDistanceMultiplier = deviceConfigProxy.getFloat("systemui", "brightline_falsing_distance_velcoity_to_distance", 30.0f);
        final float float1 = deviceConfigProxy.getFloat("systemui", "brightline_falsing_distance_horizontal_fling_threshold_in", 1.0f);
        final float float2 = deviceConfigProxy.getFloat("systemui", "brightline_falsing_distance_vertical_fling_threshold_in", 1.5f);
        final float float3 = deviceConfigProxy.getFloat("systemui", "brightline_falsing_distance_horizontal_swipe_threshold_in", 3.0f);
        final float float4 = deviceConfigProxy.getFloat("systemui", "brightline_falsing_distance_horizontal_swipe_threshold_in", 3.0f);
        final float float5 = deviceConfigProxy.getFloat("systemui", "brightline_falsing_distance_screen_fraction_max_distance", 0.8f);
        this.mHorizontalFlingThresholdPx = Math.min(this.getWidthPixels() * float5, float1 * this.getXdpi());
        this.mVerticalFlingThresholdPx = Math.min(this.getHeightPixels() * float5, float2 * this.getYdpi());
        this.mHorizontalSwipeThresholdPx = Math.min(this.getWidthPixels() * float5, float3 * this.getXdpi());
        this.mVerticalSwipeThresholdPx = Math.min(this.getHeightPixels() * float5, float4 * this.getYdpi());
        this.mDistanceDirty = true;
    }
    
    private DistanceVectors calculateDistances() {
        final VelocityTracker obtain = VelocityTracker.obtain();
        final List<MotionEvent> recentMotionEvents = this.getRecentMotionEvents();
        if (recentMotionEvents.size() < 3) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Only ");
            sb.append(recentMotionEvents.size());
            sb.append(" motion events recorded.");
            FalsingClassifier.logDebug(sb.toString());
            return new DistanceVectors(0.0f, 0.0f, 0.0f, 0.0f);
        }
        final Iterator<MotionEvent> iterator = recentMotionEvents.iterator();
        while (iterator.hasNext()) {
            obtain.addMovement((MotionEvent)iterator.next());
        }
        obtain.computeCurrentVelocity(1);
        final float xVelocity = obtain.getXVelocity();
        final float yVelocity = obtain.getYVelocity();
        obtain.recycle();
        final float f = this.getLastMotionEvent().getX() - this.getFirstMotionEvent().getX();
        final float f2 = this.getLastMotionEvent().getY() - this.getFirstMotionEvent().getY();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("dX: ");
        sb2.append(f);
        sb2.append(" dY: ");
        sb2.append(f2);
        sb2.append(" xV: ");
        sb2.append(xVelocity);
        sb2.append(" yV: ");
        sb2.append(yVelocity);
        FalsingClassifier.logInfo(sb2.toString());
        return new DistanceVectors(f, f2, xVelocity, yVelocity);
    }
    
    private DistanceVectors getDistances() {
        if (this.mDistanceDirty) {
            this.mCachedDistance = this.calculateDistances();
            this.mDistanceDirty = false;
        }
        return this.mCachedDistance;
    }
    
    private boolean getPassedDistanceThreshold() {
        final DistanceVectors distances = this.getDistances();
        final boolean horizontal = this.isHorizontal();
        final boolean b = true;
        boolean b2 = true;
        if (horizontal) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Horizontal swipe distance: ");
            sb.append(Math.abs(distances.mDx));
            FalsingClassifier.logDebug(sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Threshold: ");
            sb2.append(this.mHorizontalSwipeThresholdPx);
            FalsingClassifier.logDebug(sb2.toString());
            if (Math.abs(distances.mDx) < this.mHorizontalSwipeThresholdPx) {
                b2 = false;
            }
            return b2;
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("Vertical swipe distance: ");
        sb3.append(Math.abs(distances.mDy));
        FalsingClassifier.logDebug(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("Threshold: ");
        sb4.append(this.mVerticalSwipeThresholdPx);
        FalsingClassifier.logDebug(sb4.toString());
        return Math.abs(distances.mDy) >= this.mVerticalSwipeThresholdPx && b;
    }
    
    private boolean getPassedFlingThreshold() {
        final DistanceVectors distances = this.getDistances();
        final float mDx = distances.mDx;
        final float access$000 = distances.mVx;
        final float mVelocityToDistanceMultiplier = this.mVelocityToDistanceMultiplier;
        final float mDy = distances.mDy;
        final float access$2 = distances.mVy;
        final float mVelocityToDistanceMultiplier2 = this.mVelocityToDistanceMultiplier;
        final boolean horizontal = this.isHorizontal();
        final boolean b = true;
        boolean b2 = true;
        if (horizontal) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Horizontal swipe and fling distance: ");
            sb.append(distances.mDx);
            sb.append(", ");
            sb.append(distances.mVx * this.mVelocityToDistanceMultiplier);
            FalsingClassifier.logDebug(sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Threshold: ");
            sb2.append(this.mHorizontalFlingThresholdPx);
            FalsingClassifier.logDebug(sb2.toString());
            if (Math.abs(mDx + access$000 * mVelocityToDistanceMultiplier) < this.mHorizontalFlingThresholdPx) {
                b2 = false;
            }
            return b2;
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("Vertical swipe and fling distance: ");
        sb3.append(distances.mDy);
        sb3.append(", ");
        sb3.append(distances.mVy * this.mVelocityToDistanceMultiplier);
        FalsingClassifier.logDebug(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("Threshold: ");
        sb4.append(this.mVerticalFlingThresholdPx);
        FalsingClassifier.logDebug(sb4.toString());
        return Math.abs(mDy + access$2 * mVelocityToDistanceMultiplier2) >= this.mVerticalFlingThresholdPx && b;
    }
    
    @Override
    String getReason() {
        return String.format(null, "{distanceVectors=%s, isHorizontal=%s, velocityToDistanceMultiplier=%f, horizontalFlingThreshold=%f, verticalFlingThreshold=%f, horizontalSwipeThreshold=%f, verticalSwipeThreshold=%s}", this.getDistances(), this.isHorizontal(), this.mVelocityToDistanceMultiplier, this.mHorizontalFlingThresholdPx, this.mVerticalFlingThresholdPx, this.mHorizontalSwipeThresholdPx, this.mVerticalSwipeThresholdPx);
    }
    
    public boolean isFalseTouch() {
        return this.getPassedFlingThreshold() ^ true;
    }
    
    boolean isLongSwipe() {
        final boolean passedDistanceThreshold = this.getPassedDistanceThreshold();
        final StringBuilder sb = new StringBuilder();
        sb.append("Is longSwipe? ");
        sb.append(passedDistanceThreshold);
        FalsingClassifier.logDebug(sb.toString());
        return passedDistanceThreshold;
    }
    
    public void onTouchEvent(final MotionEvent motionEvent) {
        this.mDistanceDirty = true;
    }
    
    private class DistanceVectors
    {
        final float mDx;
        final float mDy;
        private final float mVx;
        private final float mVy;
        
        DistanceVectors(final DistanceClassifier distanceClassifier, final float mDx, final float mDy, final float mVx, final float mVy) {
            this.mDx = mDx;
            this.mDy = mDy;
            this.mVx = mVx;
            this.mVy = mVy;
        }
        
        @Override
        public String toString() {
            return String.format(null, "{dx=%f, vx=%f, dy=%f, vy=%f}", this.mDx, this.mVx, this.mDy, this.mVy);
        }
    }
}
