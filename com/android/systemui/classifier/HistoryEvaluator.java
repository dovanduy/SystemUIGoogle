// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.os.SystemClock;
import java.util.ArrayList;

public class HistoryEvaluator
{
    private final ArrayList<Data> mGestureWeights;
    private long mLastUpdate;
    private final ArrayList<Data> mStrokes;
    
    public HistoryEvaluator() {
        this.mStrokes = new ArrayList<Data>();
        this.mGestureWeights = new ArrayList<Data>();
        this.mLastUpdate = SystemClock.elapsedRealtime();
    }
    
    private void decayValue() {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        final long mLastUpdate = this.mLastUpdate;
        if (elapsedRealtime <= mLastUpdate) {
            return;
        }
        final float n = (float)Math.pow(0.8999999761581421, (elapsedRealtime - mLastUpdate) / 50.0f);
        this.decayValue(this.mStrokes, n);
        this.decayValue(this.mGestureWeights, n);
        this.mLastUpdate = elapsedRealtime;
    }
    
    private void decayValue(final ArrayList<Data> list, final float n) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Data data = list.get(i);
            data.weight *= n;
        }
        while (!list.isEmpty() && this.isZero(list.get(0).weight)) {
            list.remove(0);
        }
    }
    
    private boolean isZero(final float n) {
        return n <= 1.0E-5f && n >= -1.0E-5f;
    }
    
    private float weightedAverage(final ArrayList<Data> list) {
        final int size = list.size();
        int i = 0;
        float n2;
        float n = n2 = 0.0f;
        while (i < size) {
            final Data data = list.get(i);
            final float evaluation = data.evaluation;
            final float weight = data.weight;
            n2 += evaluation * weight;
            n += weight;
            ++i;
        }
        if (n == 0.0f) {
            return 0.0f;
        }
        return n2 / n;
    }
    
    public void addGesture(final float n) {
        this.decayValue();
        this.mGestureWeights.add(new Data(n));
    }
    
    public void addStroke(final float n) {
        this.decayValue();
        this.mStrokes.add(new Data(n));
    }
    
    public float getEvaluation() {
        return this.weightedAverage(this.mStrokes) + this.weightedAverage(this.mGestureWeights);
    }
    
    private static class Data
    {
        public float evaluation;
        public float weight;
        
        public Data(final float evaluation) {
            this.evaluation = evaluation;
            this.weight = 1.0f;
        }
    }
}
