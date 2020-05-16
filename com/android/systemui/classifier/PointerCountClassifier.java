// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.view.MotionEvent;

public class PointerCountClassifier extends GestureClassifier
{
    private int mCount;
    
    public PointerCountClassifier(final ClassifierData classifierData) {
        this.mCount = 0;
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n) {
        return PointerCountEvaluator.evaluate(this.mCount);
    }
    
    @Override
    public String getTag() {
        return "PTR_CNT";
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mCount = 1;
        }
        if (actionMasked == 5) {
            ++this.mCount;
        }
    }
}
