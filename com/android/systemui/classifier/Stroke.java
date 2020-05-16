// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import java.util.ArrayList;

public class Stroke
{
    private final float mDpi;
    private long mEndTimeNano;
    private float mLength;
    private ArrayList<Point> mPoints;
    private long mStartTimeNano;
    
    public Stroke(final long n, final float mDpi) {
        this.mPoints = new ArrayList<Point>();
        this.mDpi = mDpi;
        this.mEndTimeNano = n;
        this.mStartTimeNano = n;
    }
    
    public void addPoint(float mLength, final float n, final long mEndTimeNano) {
        this.mEndTimeNano = mEndTimeNano;
        final float mDpi = this.mDpi;
        final Point e = new Point(mLength / mDpi, n / mDpi, mEndTimeNano - this.mStartTimeNano);
        if (!this.mPoints.isEmpty()) {
            mLength = this.mLength;
            final ArrayList<Point> mPoints = this.mPoints;
            this.mLength = mLength + mPoints.get(mPoints.size() - 1).dist(e);
        }
        this.mPoints.add(e);
    }
    
    public int getCount() {
        return this.mPoints.size();
    }
    
    public long getDurationNanos() {
        return this.mEndTimeNano - this.mStartTimeNano;
    }
    
    public float getDurationSeconds() {
        return this.getDurationNanos() / 1.0E9f;
    }
    
    public float getEndPointLength() {
        final Point point = this.mPoints.get(0);
        final ArrayList<Point> mPoints = this.mPoints;
        return point.dist(mPoints.get(mPoints.size() - 1));
    }
    
    public long getLastEventTimeNano() {
        if (this.mPoints.isEmpty()) {
            return this.mStartTimeNano;
        }
        final ArrayList<Point> mPoints = this.mPoints;
        return mPoints.get(mPoints.size() - 1).timeOffsetNano;
    }
    
    public ArrayList<Point> getPoints() {
        return this.mPoints;
    }
    
    public float getTotalLength() {
        return this.mLength;
    }
}
