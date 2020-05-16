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

public class AnglesClassifier extends StrokeClassifier
{
    private static String TAG;
    public static final boolean VERBOSE;
    private HashMap<Stroke, Data> mStrokeMap;
    
    static {
        VERBOSE = SystemProperties.getBoolean("debug.falsing_log.ang", Build.IS_DEBUGGABLE);
        AnglesClassifier.TAG = "ANG";
    }
    
    public AnglesClassifier(final ClassifierData mClassifierData) {
        this.mStrokeMap = new HashMap<Stroke, Data>();
        super.mClassifierData = mClassifierData;
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke key) {
        final Data data = this.mStrokeMap.get(key);
        return AnglesVarianceEvaluator.evaluate(data.getAnglesVariance(), n) + AnglesPercentageEvaluator.evaluate(data.getAnglesPercentage(), n);
    }
    
    @Override
    public String getTag() {
        return AnglesClassifier.TAG;
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mStrokeMap.clear();
        }
        for (int i = 0; i < motionEvent.getPointerCount(); ++i) {
            final Stroke stroke = super.mClassifierData.getStroke(motionEvent.getPointerId(i));
            if (this.mStrokeMap.get(stroke) == null) {
                this.mStrokeMap.put(stroke, new Data());
            }
            this.mStrokeMap.get(stroke).addPoint(stroke.getPoints().get(stroke.getPoints().size() - 1));
        }
    }
    
    private static class Data
    {
        private float mAnglesCount;
        private float mBiggestAngle;
        private float mCount;
        private float mFirstAngleVariance;
        private float mFirstLength;
        private List<Point> mLastThreePoints;
        private float mLeftAngles;
        private float mLength;
        private float mPreviousAngle;
        private float mRightAngles;
        private float mSecondCount;
        private float mSecondSum;
        private float mSecondSumSquares;
        private float mStraightAngles;
        private float mSum;
        private float mSumSquares;
        
        public Data() {
            this.mLastThreePoints = new ArrayList<Point>();
            this.mFirstAngleVariance = 0.0f;
            this.mPreviousAngle = 3.1415927f;
            this.mBiggestAngle = 0.0f;
            this.mSecondSumSquares = 0.0f;
            this.mSumSquares = 0.0f;
            this.mSecondSum = 0.0f;
            this.mSum = 0.0f;
            this.mSecondCount = 1.0f;
            this.mCount = 1.0f;
            this.mFirstLength = 0.0f;
            this.mLength = 0.0f;
            this.mStraightAngles = 0.0f;
            this.mRightAngles = 0.0f;
            this.mLeftAngles = 0.0f;
            this.mAnglesCount = 0.0f;
        }
        
        public void addPoint(final Point point) {
            if (!this.mLastThreePoints.isEmpty()) {
                final List<Point> mLastThreePoints = this.mLastThreePoints;
                if (mLastThreePoints.get(mLastThreePoints.size() - 1).equals(point)) {
                    return;
                }
            }
            if (!this.mLastThreePoints.isEmpty()) {
                final float mLength = this.mLength;
                final List<Point> mLastThreePoints2 = this.mLastThreePoints;
                this.mLength = mLength + mLastThreePoints2.get(mLastThreePoints2.size() - 1).dist(point);
            }
            this.mLastThreePoints.add(point);
            if (this.mLastThreePoints.size() == 4) {
                this.mLastThreePoints.remove(0);
                final float angle = this.mLastThreePoints.get(1).getAngle(this.mLastThreePoints.get(0), this.mLastThreePoints.get(2));
                ++this.mAnglesCount;
                final double n = angle;
                if (n < 2.9845130165391645) {
                    ++this.mLeftAngles;
                }
                else if (n <= 3.298672290640422) {
                    ++this.mStraightAngles;
                }
                else {
                    ++this.mRightAngles;
                }
                final float n2 = angle - this.mPreviousAngle;
                if (this.mBiggestAngle < angle) {
                    this.mBiggestAngle = angle;
                    this.mFirstLength = this.mLength;
                    this.mFirstAngleVariance = this.getAnglesVariance(this.mSumSquares, this.mSum, this.mCount);
                    this.mSecondSumSquares = 0.0f;
                    this.mSecondSum = 0.0f;
                    this.mSecondCount = 1.0f;
                }
                else {
                    this.mSecondSum += n2;
                    this.mSecondSumSquares += n2 * n2;
                    ++this.mSecondCount;
                }
                this.mSum += n2;
                this.mSumSquares += n2 * n2;
                ++this.mCount;
                this.mPreviousAngle = angle;
            }
        }
        
        public float getAnglesPercentage() {
            if (this.mAnglesCount == 0.0f) {
                if (AnglesClassifier.VERBOSE) {
                    FalsingLog.i(AnglesClassifier.TAG, "getAnglesPercentage: count==0, result=1");
                }
                return 1.0f;
            }
            final float f = (Math.max(this.mLeftAngles, this.mRightAngles) + this.mStraightAngles) / this.mAnglesCount;
            if (AnglesClassifier.VERBOSE) {
                final String access$000 = AnglesClassifier.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("getAnglesPercentage: left=");
                sb.append(this.mLeftAngles);
                sb.append(" right=");
                sb.append(this.mRightAngles);
                sb.append(" straight=");
                sb.append(this.mStraightAngles);
                sb.append(" count=");
                sb.append(this.mAnglesCount);
                sb.append(" result=");
                sb.append(f);
                FalsingLog.i(access$000, sb.toString());
            }
            return f;
        }
        
        public float getAnglesVariance() {
            final float anglesVariance = this.getAnglesVariance(this.mSumSquares, this.mSum, this.mCount);
            if (AnglesClassifier.VERBOSE) {
                final String access$000 = AnglesClassifier.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("getAnglesVariance: (first pass) ");
                sb.append(anglesVariance);
                FalsingLog.i(access$000, sb.toString());
                final String access$2 = AnglesClassifier.TAG;
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("   - mFirstLength=");
                sb2.append(this.mFirstLength);
                FalsingLog.i(access$2, sb2.toString());
                final String access$3 = AnglesClassifier.TAG;
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("   - mLength=");
                sb3.append(this.mLength);
                FalsingLog.i(access$3, sb3.toString());
            }
            float min = anglesVariance;
            if (this.mFirstLength < this.mLength / 2.0f) {
                final float f = min = Math.min(anglesVariance, this.mFirstAngleVariance + this.getAnglesVariance(this.mSecondSumSquares, this.mSecondSum, this.mSecondCount));
                if (AnglesClassifier.VERBOSE) {
                    final String access$4 = AnglesClassifier.TAG;
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("getAnglesVariance: (second pass) ");
                    sb4.append(f);
                    FalsingLog.i(access$4, sb4.toString());
                    min = f;
                }
            }
            return min;
        }
        
        public float getAnglesVariance(float n, float n2, final float n3) {
            n /= n3;
            n2 /= n3;
            return n - n2 * n2;
        }
    }
}
