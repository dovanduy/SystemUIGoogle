// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.analytics;

import android.os.Build;
import android.hardware.SensorEvent;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.nano.TouchAnalyticsProto$Session;
import java.util.ArrayList;

public class SensorLoggerSession
{
    private long mEndTimestampMillis;
    private ArrayList<TouchAnalyticsProto$Session.TouchEvent> mMotionEvents;
    private ArrayList<TouchAnalyticsProto$Session.PhoneEvent> mPhoneEvents;
    private int mResult;
    private ArrayList<TouchAnalyticsProto$Session.SensorEvent> mSensorEvents;
    private final long mStartSystemTimeNanos;
    private final long mStartTimestampMillis;
    private int mTouchAreaHeight;
    private int mTouchAreaWidth;
    private int mType;
    
    public SensorLoggerSession(final long mStartTimestampMillis, final long mStartSystemTimeNanos) {
        this.mMotionEvents = new ArrayList<TouchAnalyticsProto$Session.TouchEvent>();
        this.mSensorEvents = new ArrayList<TouchAnalyticsProto$Session.SensorEvent>();
        this.mPhoneEvents = new ArrayList<TouchAnalyticsProto$Session.PhoneEvent>();
        this.mResult = 2;
        this.mStartTimestampMillis = mStartTimestampMillis;
        this.mStartSystemTimeNanos = mStartSystemTimeNanos;
        this.mType = 3;
    }
    
    private TouchAnalyticsProto$Session.TouchEvent motionEventToProto(final MotionEvent motionEvent) {
        final int pointerCount = motionEvent.getPointerCount();
        final TouchAnalyticsProto$Session.TouchEvent touchEvent = new TouchAnalyticsProto$Session.TouchEvent();
        touchEvent.timeOffsetNanos = motionEvent.getEventTimeNano() - this.mStartSystemTimeNanos;
        touchEvent.action = motionEvent.getActionMasked();
        touchEvent.actionIndex = motionEvent.getActionIndex();
        touchEvent.pointers = new TouchAnalyticsProto$Session.TouchEvent.Pointer[pointerCount];
        for (int i = 0; i < pointerCount; ++i) {
            final TouchAnalyticsProto$Session.TouchEvent.Pointer pointer = new TouchAnalyticsProto$Session.TouchEvent.Pointer();
            pointer.x = motionEvent.getX(i);
            pointer.y = motionEvent.getY(i);
            pointer.size = motionEvent.getSize(i);
            pointer.pressure = motionEvent.getPressure(i);
            pointer.id = motionEvent.getPointerId(i);
            touchEvent.pointers[i] = pointer;
        }
        return touchEvent;
    }
    
    private TouchAnalyticsProto$Session.PhoneEvent phoneEventToProto(final int type, final long n) {
        final TouchAnalyticsProto$Session.PhoneEvent phoneEvent = new TouchAnalyticsProto$Session.PhoneEvent();
        phoneEvent.type = type;
        phoneEvent.timeOffsetNanos = n - this.mStartSystemTimeNanos;
        return phoneEvent;
    }
    
    private TouchAnalyticsProto$Session.SensorEvent sensorEventToProto(final SensorEvent sensorEvent, final long n) {
        final TouchAnalyticsProto$Session.SensorEvent sensorEvent2 = new TouchAnalyticsProto$Session.SensorEvent();
        sensorEvent2.type = sensorEvent.sensor.getType();
        sensorEvent2.timeOffsetNanos = n - this.mStartSystemTimeNanos;
        sensorEvent2.timestamp = sensorEvent.timestamp;
        sensorEvent2.values = sensorEvent.values.clone();
        return sensorEvent2;
    }
    
    public void addMotionEvent(final MotionEvent motionEvent) {
        this.mMotionEvents.add(this.motionEventToProto(motionEvent));
    }
    
    public void addPhoneEvent(final int n, final long n2) {
        this.mPhoneEvents.add(this.phoneEventToProto(n, n2));
    }
    
    public void addSensorEvent(final SensorEvent sensorEvent, final long n) {
        this.mSensorEvents.add(this.sensorEventToProto(sensorEvent, n));
    }
    
    public void end(final long mEndTimestampMillis, final int mResult) {
        this.mResult = mResult;
        this.mEndTimestampMillis = mEndTimestampMillis;
    }
    
    public int getResult() {
        return this.mResult;
    }
    
    public void setTouchArea(final int mTouchAreaWidth, final int mTouchAreaHeight) {
        this.mTouchAreaWidth = mTouchAreaWidth;
        this.mTouchAreaHeight = mTouchAreaHeight;
    }
    
    public void setType(final int mType) {
        this.mType = mType;
    }
    
    public TouchAnalyticsProto$Session toProto() {
        final TouchAnalyticsProto$Session touchAnalyticsProto$Session = new TouchAnalyticsProto$Session();
        final long mStartTimestampMillis = this.mStartTimestampMillis;
        touchAnalyticsProto$Session.startTimestampMillis = mStartTimestampMillis;
        touchAnalyticsProto$Session.durationMillis = this.mEndTimestampMillis - mStartTimestampMillis;
        touchAnalyticsProto$Session.build = Build.FINGERPRINT;
        touchAnalyticsProto$Session.deviceId = Build.DEVICE;
        touchAnalyticsProto$Session.result = this.mResult;
        touchAnalyticsProto$Session.type = this.mType;
        touchAnalyticsProto$Session.sensorEvents = this.mSensorEvents.toArray(touchAnalyticsProto$Session.sensorEvents);
        touchAnalyticsProto$Session.touchEvents = this.mMotionEvents.toArray(touchAnalyticsProto$Session.touchEvents);
        touchAnalyticsProto$Session.phoneEvents = this.mPhoneEvents.toArray(touchAnalyticsProto$Session.phoneEvents);
        touchAnalyticsProto$Session.touchAreaWidth = this.mTouchAreaWidth;
        touchAnalyticsProto$Session.touchAreaHeight = this.mTouchAreaHeight;
        return touchAnalyticsProto$Session;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Session{");
        sb.append("mStartTimestampMillis=");
        sb.append(this.mStartTimestampMillis);
        sb.append(", mStartSystemTimeNanos=");
        sb.append(this.mStartSystemTimeNanos);
        sb.append(", mEndTimestampMillis=");
        sb.append(this.mEndTimestampMillis);
        sb.append(", mResult=");
        sb.append(this.mResult);
        sb.append(", mTouchAreaHeight=");
        sb.append(this.mTouchAreaHeight);
        sb.append(", mTouchAreaWidth=");
        sb.append(this.mTouchAreaWidth);
        sb.append(", mMotionEvents=[size=");
        sb.append(this.mMotionEvents.size());
        sb.append("]");
        sb.append(", mSensorEvents=[size=");
        sb.append(this.mSensorEvents.size());
        sb.append("]");
        sb.append(", mPhoneEvents=[size=");
        sb.append(this.mPhoneEvents.size());
        sb.append("]");
        sb.append('}');
        return sb.toString();
    }
}
