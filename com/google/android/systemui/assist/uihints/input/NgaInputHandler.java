// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.input;

import android.view.InputEvent;
import android.os.Looper;
import android.view.InputChannel;
import android.util.Log;
import android.hardware.input.InputManager;
import android.graphics.Region$Op;
import java.util.Iterator;
import java.util.function.Consumer;
import android.graphics.Region;
import android.view.MotionEvent;
import com.google.android.systemui.assist.uihints.TouchInsideHandler;
import java.util.Set;
import android.view.InputMonitor;
import android.view.InputEventReceiver;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;

public class NgaInputHandler implements EdgeLightsInfoListener
{
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private final Set<TouchActionRegion> mTouchActionRegions;
    private final TouchInsideHandler mTouchInsideHandler;
    private final Set<TouchInsideRegion> mTouchInsideRegions;
    
    NgaInputHandler(final TouchInsideHandler mTouchInsideHandler, final Set<TouchActionRegion> mTouchActionRegions, final Set<TouchInsideRegion> mTouchInsideRegions) {
        this.mTouchInsideHandler = mTouchInsideHandler;
        this.mTouchActionRegions = mTouchActionRegions;
        this.mTouchInsideRegions = mTouchInsideRegions;
    }
    
    private void handleMotionEvent(final MotionEvent motionEvent) {
        final int n = (int)motionEvent.getRawX();
        final int n2 = (int)motionEvent.getRawY();
        final Region region = new Region();
        final Iterator<TouchInsideRegion> iterator = this.mTouchInsideRegions.iterator();
        while (iterator.hasNext()) {
            iterator.next().getTouchInsideRegion().ifPresent(new _$$Lambda$NgaInputHandler$xgxHEq1bJriTtYO6BFkYrN5ku5A(region));
        }
        final Iterator<TouchActionRegion> iterator2 = this.mTouchActionRegions.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().getTouchActionRegion().ifPresent(new _$$Lambda$NgaInputHandler$HXylmJL8pUnoo9KedGKjZylUkBs(region));
        }
        if (region.contains(n, n2)) {
            this.mTouchInsideHandler.onTouchInside();
        }
    }
    
    private void startMonitoring() {
        if (this.mInputEventReceiver == null && this.mInputMonitor == null) {
            this.mInputMonitor = InputManager.getInstance().monitorGestureInput("NgaInputHandler", 0);
            this.mInputEventReceiver = new NgaInputEventReceiver(this.mInputMonitor.getInputChannel());
            return;
        }
        Log.w("NgaInputHandler", "Already monitoring");
    }
    
    private void stopMonitoring() {
        final InputEventReceiver mInputEventReceiver = this.mInputEventReceiver;
        if (mInputEventReceiver != null) {
            mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        final InputMonitor mInputMonitor = this.mInputMonitor;
        if (mInputMonitor != null) {
            mInputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }
    
    @Override
    public void onEdgeLightsInfo(final String anObject, final boolean b) {
        if ("HALF_LISTENING".equals(anObject)) {
            this.startMonitoring();
        }
        else {
            this.stopMonitoring();
        }
    }
    
    private class NgaInputEventReceiver extends InputEventReceiver
    {
        private NgaInputEventReceiver(final InputChannel inputChannel) {
            super(inputChannel, Looper.getMainLooper());
        }
        
        public void onInputEvent(final InputEvent inputEvent) {
            if (inputEvent instanceof MotionEvent) {
                NgaInputHandler.this.handleMotionEvent((MotionEvent)inputEvent);
            }
            this.finishInputEvent(inputEvent, false);
        }
    }
}
