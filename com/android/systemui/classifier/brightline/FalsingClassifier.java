// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import com.android.systemui.util.sensors.ProximitySensor;
import java.util.List;
import android.view.MotionEvent;

abstract class FalsingClassifier
{
    private final FalsingDataProvider mDataProvider;
    
    FalsingClassifier(final FalsingDataProvider mDataProvider) {
        this.mDataProvider = mDataProvider;
    }
    
    static void logDebug(final String s) {
        BrightLineFalsingManager.logDebug(s);
    }
    
    static void logInfo(final String s) {
        BrightLineFalsingManager.logInfo(s);
    }
    
    float getAngle() {
        return this.mDataProvider.getAngle();
    }
    
    MotionEvent getFirstMotionEvent() {
        return this.mDataProvider.getFirstRecentMotionEvent();
    }
    
    int getHeightPixels() {
        return this.mDataProvider.getHeightPixels();
    }
    
    final int getInteractionType() {
        return this.mDataProvider.getInteractionType();
    }
    
    MotionEvent getLastMotionEvent() {
        return this.mDataProvider.getLastMotionEvent();
    }
    
    abstract String getReason();
    
    List<MotionEvent> getRecentMotionEvents() {
        return this.mDataProvider.getRecentMotionEvents();
    }
    
    int getWidthPixels() {
        return this.mDataProvider.getWidthPixels();
    }
    
    float getXdpi() {
        return this.mDataProvider.getXdpi();
    }
    
    float getYdpi() {
        return this.mDataProvider.getYdpi();
    }
    
    abstract boolean isFalseTouch();
    
    boolean isHorizontal() {
        return this.mDataProvider.isHorizontal();
    }
    
    boolean isRight() {
        return this.mDataProvider.isRight();
    }
    
    boolean isUp() {
        return this.mDataProvider.isUp();
    }
    
    boolean isVertical() {
        return this.mDataProvider.isVertical();
    }
    
    void onProximityEvent(final ProximitySensor.ProximityEvent proximityEvent) {
    }
    
    void onSessionEnded() {
    }
    
    void onSessionStarted() {
    }
    
    void onTouchEvent(final MotionEvent motionEvent) {
    }
}
