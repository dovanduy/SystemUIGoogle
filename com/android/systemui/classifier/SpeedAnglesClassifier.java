// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import java.util.ArrayList;
import java.util.List;
import android.view.MotionEvent;
import android.os.SystemProperties;
import android.os.Build;
import java.util.HashMap;

public class SpeedAnglesClassifier extends StrokeClassifier
{
    public static final boolean VERBOSE;
    private HashMap<Stroke, Data> mStrokeMap;
    
    static {
        VERBOSE = SystemProperties.getBoolean("debug.falsing_log.spd_ang", Build.IS_DEBUGGABLE);
    }
    
    public SpeedAnglesClassifier(final ClassifierData mClassifierData) {
        this.mStrokeMap = new HashMap<Stroke, Data>();
        super.mClassifierData = mClassifierData;
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke key) {
        final Data data = this.mStrokeMap.get(key);
        return SpeedVarianceEvaluator.evaluate(data.getAnglesVariance()) + SpeedAnglesPercentageEvaluator.evaluate(data.getAnglesPercentage());
    }
    
    @Override
    public String getTag() {
        return "SPD_ANG";
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mStrokeMap.clear();
        }
        for (int i = 0; i < motionEvent.getPointerCount(); ++i) {
            final Stroke stroke = super.mClassifierData.getStroke(motionEvent.getPointerId(i));
            if (this.mStrokeMap.get(stroke) == null) {
                this.mStrokeMap.put(stroke, new Data());
            }
            if (actionMasked != 1 && actionMasked != 3 && (actionMasked != 6 || i != motionEvent.getActionIndex())) {
                this.mStrokeMap.get(stroke).addPoint(stroke.getPoints().get(stroke.getPoints().size() - 1));
            }
        }
    }
    
    private static class Data
    {
        private float mAcceleratingAngles;
        private float mAnglesCount;
        private float mCount;
        private float mDist;
        private List<Point> mLastThreePoints;
        private float mPreviousAngle;
        private Point mPreviousPoint;
        private float mSum;
        private float mSumSquares;
        
        public Data() {
            this.mLastThreePoints = new ArrayList<Point>();
            this.mPreviousPoint = null;
            this.mPreviousAngle = 3.1415927f;
            this.mSumSquares = 0.0f;
            this.mSum = 0.0f;
            this.mCount = 1.0f;
            this.mDist = 0.0f;
            this.mAcceleratingAngles = 0.0f;
            this.mAnglesCount = 0.0f;
        }
        
        public void addPoint(Point mPreviousPoint) {
            final Point mPreviousPoint2 = this.mPreviousPoint;
            if (mPreviousPoint2 != null) {
                this.mDist += mPreviousPoint2.dist(mPreviousPoint);
            }
            this.mPreviousPoint = mPreviousPoint;
            mPreviousPoint = new Point(mPreviousPoint.timeOffsetNano / 1.0E8f, this.mDist / 1.0f);
            if (!this.mLastThreePoints.isEmpty()) {
                final List<Point> mLastThreePoints = this.mLastThreePoints;
                if (mLastThreePoints.get(mLastThreePoints.size() - 1).equals(mPreviousPoint)) {
                    return;
                }
            }
            this.mLastThreePoints.add(mPreviousPoint);
            if (this.mLastThreePoints.size() == 4) {
                this.mLastThreePoints.remove(0);
                final float angle = this.mLastThreePoints.get(1).getAngle(this.mLastThreePoints.get(0), this.mLastThreePoints.get(2));
                ++this.mAnglesCount;
                if (angle >= 2.8274336f) {
                    ++this.mAcceleratingAngles;
                }
                final float n = angle - this.mPreviousAngle;
                this.mSum += n;
                this.mSumSquares += n * n;
                ++this.mCount;
                this.mPreviousAngle = angle;
            }
        }
        
        public float getAnglesPercentage() {
            final float mAnglesCount = this.mAnglesCount;
            if (mAnglesCount == 0.0f) {
                return 1.0f;
            }
            final float f = this.mAcceleratingAngles / mAnglesCount;
            if (SpeedAnglesClassifier.VERBOSE) {
                final StringBuilder sb = new StringBuilder();
                sb.append("getAnglesPercentage: angles=");
                sb.append(this.mAcceleratingAngles);
                sb.append(" count=");
                sb.append(this.mAnglesCount);
                sb.append(" result=");
                sb.append(f);
                FalsingLog.i("SPD_ANG", sb.toString());
            }
            return f;
        }
        
        public float getAnglesVariance() {
            final float mSumSquares = this.mSumSquares;
            final float mCount = this.mCount;
            final float n = mSumSquares / mCount;
            final float mSum = this.mSum;
            final float f = n - mSum / mCount * (mSum / mCount);
            if (SpeedAnglesClassifier.VERBOSE) {
                final StringBuilder sb = new StringBuilder();
                sb.append("getAnglesVariance: sum^2=");
                sb.append(this.mSumSquares);
                sb.append(" count=");
                sb.append(this.mCount);
                sb.append(" result=");
                sb.append(f);
                FalsingLog.i("SPD_ANG", sb.toString());
            }
            return f;
        }
    }
}
