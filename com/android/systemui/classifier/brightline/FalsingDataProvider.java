// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import java.util.Iterator;
import java.util.Collection;
import android.view.MotionEvent$PointerCoords;
import android.view.MotionEvent$PointerProperties;
import java.util.ArrayList;
import java.util.List;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class FalsingDataProvider
{
    private float mAngle;
    private boolean mDirty;
    private MotionEvent mFirstRecentMotionEvent;
    private final int mHeightPixels;
    private int mInteractionType;
    private MotionEvent mLastMotionEvent;
    private final TimeLimitedMotionEventBuffer mRecentMotionEvents;
    private final int mWidthPixels;
    private final float mXdpi;
    private final float mYdpi;
    
    public FalsingDataProvider(final DisplayMetrics displayMetrics) {
        this.mRecentMotionEvents = new TimeLimitedMotionEventBuffer(1000L);
        this.mDirty = true;
        this.mAngle = 0.0f;
        this.mXdpi = displayMetrics.xdpi;
        this.mYdpi = displayMetrics.ydpi;
        this.mWidthPixels = displayMetrics.widthPixels;
        this.mHeightPixels = displayMetrics.heightPixels;
        final StringBuilder sb = new StringBuilder();
        sb.append("xdpi, ydpi: ");
        sb.append(this.getXdpi());
        sb.append(", ");
        sb.append(this.getYdpi());
        FalsingClassifier.logInfo(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("width, height: ");
        sb2.append(this.getWidthPixels());
        sb2.append(", ");
        sb2.append(this.getHeightPixels());
        FalsingClassifier.logInfo(sb2.toString());
    }
    
    private void calculateAngleInternal() {
        if (this.mRecentMotionEvents.size() < 2) {
            this.mAngle = Float.MAX_VALUE;
        }
        else {
            this.mAngle = (float)Math.atan2(this.mLastMotionEvent.getY() - this.mFirstRecentMotionEvent.getY(), this.mLastMotionEvent.getX() - this.mFirstRecentMotionEvent.getX());
            while (true) {
                final float mAngle = this.mAngle;
                if (mAngle >= 0.0f) {
                    break;
                }
                this.mAngle = mAngle + 6.2831855f;
            }
            while (true) {
                final float mAngle2 = this.mAngle;
                if (mAngle2 <= 6.2831855f) {
                    break;
                }
                this.mAngle = mAngle2 - 6.2831855f;
            }
        }
    }
    
    private void recalculateData() {
        if (!this.mDirty) {
            return;
        }
        if (this.mRecentMotionEvents.isEmpty()) {
            this.mFirstRecentMotionEvent = null;
            this.mLastMotionEvent = null;
        }
        else {
            this.mFirstRecentMotionEvent = this.mRecentMotionEvents.get(0);
            final TimeLimitedMotionEventBuffer mRecentMotionEvents = this.mRecentMotionEvents;
            this.mLastMotionEvent = mRecentMotionEvents.get(mRecentMotionEvents.size() - 1);
        }
        this.calculateAngleInternal();
        this.mDirty = false;
    }
    
    private List<MotionEvent> unpackMotionEvent(final MotionEvent motionEvent) {
        final ArrayList<MotionEvent> list = new ArrayList<MotionEvent>();
        final ArrayList<MotionEvent$PointerProperties> list2 = new ArrayList<MotionEvent$PointerProperties>();
        final int pointerCount = motionEvent.getPointerCount();
        final int n = 0;
        for (int i = 0; i < pointerCount; ++i) {
            final MotionEvent$PointerProperties motionEvent$PointerProperties = new MotionEvent$PointerProperties();
            motionEvent.getPointerProperties(i, motionEvent$PointerProperties);
            list2.add(motionEvent$PointerProperties);
        }
        final MotionEvent$PointerProperties[] array = new MotionEvent$PointerProperties[list2.size()];
        list2.toArray(array);
        final int historySize = motionEvent.getHistorySize();
        final int n2 = 0;
        final int n3 = n;
        for (int j = n2; j < historySize; ++j) {
            final ArrayList<MotionEvent$PointerCoords> list3 = new ArrayList<MotionEvent$PointerCoords>();
            for (int k = n3; k < pointerCount; ++k) {
                final MotionEvent$PointerCoords motionEvent$PointerCoords = new MotionEvent$PointerCoords();
                motionEvent.getHistoricalPointerCoords(k, j, motionEvent$PointerCoords);
                list3.add(motionEvent$PointerCoords);
            }
            list.add(MotionEvent.obtain(motionEvent.getDownTime(), motionEvent.getHistoricalEventTime(j), motionEvent.getAction(), pointerCount, array, (MotionEvent$PointerCoords[])list3.toArray(new MotionEvent$PointerCoords[n3]), motionEvent.getMetaState(), motionEvent.getButtonState(), motionEvent.getXPrecision(), motionEvent.getYPrecision(), motionEvent.getDeviceId(), motionEvent.getEdgeFlags(), motionEvent.getSource(), motionEvent.getFlags()));
        }
        list.add(MotionEvent.obtainNoHistory(motionEvent));
        return list;
    }
    
    float getAngle() {
        this.recalculateData();
        return this.mAngle;
    }
    
    MotionEvent getFirstRecentMotionEvent() {
        this.recalculateData();
        return this.mFirstRecentMotionEvent;
    }
    
    int getHeightPixels() {
        return this.mHeightPixels;
    }
    
    final int getInteractionType() {
        return this.mInteractionType;
    }
    
    MotionEvent getLastMotionEvent() {
        this.recalculateData();
        return this.mLastMotionEvent;
    }
    
    List<MotionEvent> getRecentMotionEvents() {
        return this.mRecentMotionEvents;
    }
    
    int getWidthPixels() {
        return this.mWidthPixels;
    }
    
    float getXdpi() {
        return this.mXdpi;
    }
    
    float getYdpi() {
        return this.mYdpi;
    }
    
    public boolean isDirty() {
        return this.mDirty;
    }
    
    boolean isHorizontal() {
        this.recalculateData();
        final boolean empty = this.mRecentMotionEvents.isEmpty();
        boolean b = false;
        if (empty) {
            return false;
        }
        if (Math.abs(this.mFirstRecentMotionEvent.getX() - this.mLastMotionEvent.getX()) > Math.abs(this.mFirstRecentMotionEvent.getY() - this.mLastMotionEvent.getY())) {
            b = true;
        }
        return b;
    }
    
    boolean isRight() {
        this.recalculateData();
        final boolean empty = this.mRecentMotionEvents.isEmpty();
        boolean b = false;
        if (empty) {
            return false;
        }
        if (this.mLastMotionEvent.getX() > this.mFirstRecentMotionEvent.getX()) {
            b = true;
        }
        return b;
    }
    
    boolean isUp() {
        this.recalculateData();
        final boolean empty = this.mRecentMotionEvents.isEmpty();
        boolean b = false;
        if (empty) {
            return false;
        }
        if (this.mLastMotionEvent.getY() < this.mFirstRecentMotionEvent.getY()) {
            b = true;
        }
        return b;
    }
    
    boolean isVertical() {
        return this.isHorizontal() ^ true;
    }
    
    void onMotionEvent(final MotionEvent motionEvent) {
        motionEvent.getActionMasked();
        final List<MotionEvent> unpackMotionEvent = this.unpackMotionEvent(motionEvent);
        final StringBuilder sb = new StringBuilder();
        sb.append("Unpacked into: ");
        sb.append(unpackMotionEvent.size());
        FalsingClassifier.logDebug(sb.toString());
        if (BrightLineFalsingManager.DEBUG) {
            for (final MotionEvent motionEvent2 : unpackMotionEvent) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("x,y,t: ");
                sb2.append(motionEvent2.getX());
                sb2.append(",");
                sb2.append(motionEvent2.getY());
                sb2.append(",");
                sb2.append(motionEvent2.getEventTime());
                FalsingClassifier.logDebug(sb2.toString());
            }
        }
        if (motionEvent.getActionMasked() == 0) {
            this.mRecentMotionEvents.clear();
        }
        this.mRecentMotionEvents.addAll(unpackMotionEvent);
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("Size: ");
        sb3.append(this.mRecentMotionEvents.size());
        FalsingClassifier.logDebug(sb3.toString());
        this.mDirty = true;
    }
    
    void onSessionEnd() {
        final Iterator<MotionEvent> iterator = this.mRecentMotionEvents.iterator();
        while (iterator.hasNext()) {
            iterator.next().recycle();
        }
        this.mRecentMotionEvents.clear();
        this.mDirty = true;
    }
    
    final void setInteractionType(final int mInteractionType) {
        this.mInteractionType = mInteractionType;
    }
}
