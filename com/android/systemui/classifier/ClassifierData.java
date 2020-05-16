// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.view.MotionEvent;
import java.util.ArrayList;
import android.util.SparseArray;

public class ClassifierData
{
    private SparseArray<Stroke> mCurrentStrokes;
    private final float mDpi;
    private ArrayList<Stroke> mEndingStrokes;
    
    public ClassifierData(final float mDpi) {
        this.mCurrentStrokes = (SparseArray<Stroke>)new SparseArray();
        this.mEndingStrokes = new ArrayList<Stroke>();
        this.mDpi = mDpi;
    }
    
    public void cleanUp(final MotionEvent motionEvent) {
        this.mEndingStrokes.clear();
        final int actionMasked = motionEvent.getActionMasked();
        for (int i = 0; i < motionEvent.getPointerCount(); ++i) {
            final int pointerId = motionEvent.getPointerId(i);
            if (actionMasked == 1 || actionMasked == 3 || (actionMasked == 6 && i == motionEvent.getActionIndex())) {
                this.mCurrentStrokes.remove(pointerId);
            }
        }
    }
    
    public ArrayList<Stroke> getEndingStrokes() {
        return this.mEndingStrokes;
    }
    
    public Stroke getStroke(final int n) {
        return (Stroke)this.mCurrentStrokes.get(n);
    }
    
    public boolean update(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final int n = 0;
        if (actionMasked == 2 && this.mCurrentStrokes.size() != 0 && motionEvent.getEventTimeNano() - ((Stroke)this.mCurrentStrokes.valueAt(0)).getLastEventTimeNano() < 14166666L) {
            return false;
        }
        this.mEndingStrokes.clear();
        final int actionMasked2 = motionEvent.getActionMasked();
        int i = n;
        if (actionMasked2 == 0) {
            this.mCurrentStrokes.clear();
            i = n;
        }
        while (i < motionEvent.getPointerCount()) {
            final int pointerId = motionEvent.getPointerId(i);
            if (this.mCurrentStrokes.get(pointerId) == null) {
                this.mCurrentStrokes.put(pointerId, (Object)new Stroke(motionEvent.getEventTimeNano(), this.mDpi));
            }
            ((Stroke)this.mCurrentStrokes.get(pointerId)).addPoint(motionEvent.getX(i), motionEvent.getY(i), motionEvent.getEventTimeNano());
            if (actionMasked2 == 1 || actionMasked2 == 3 || (actionMasked2 == 6 && i == motionEvent.getActionIndex())) {
                this.mEndingStrokes.add(this.getStroke(pointerId));
            }
            ++i;
        }
        return true;
    }
}
