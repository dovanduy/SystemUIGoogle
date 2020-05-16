// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
import java.util.Locale;

class PointerCountClassifier extends FalsingClassifier
{
    private int mMaxPointerCount;
    
    PointerCountClassifier(final FalsingDataProvider falsingDataProvider) {
        super(falsingDataProvider);
    }
    
    @Override
    String getReason() {
        return String.format(null, "{pointersObserved=%d, threshold=%d}", this.mMaxPointerCount, 1);
    }
    
    public boolean isFalseTouch() {
        final int interactionType = this.getInteractionType();
        final boolean b = false;
        boolean b2 = false;
        if (interactionType != 0 && interactionType != 2) {
            if (this.mMaxPointerCount > 1) {
                b2 = true;
            }
            return b2;
        }
        boolean b3 = b;
        if (this.mMaxPointerCount > 2) {
            b3 = true;
        }
        return b3;
    }
    
    public void onTouchEvent(final MotionEvent motionEvent) {
        final int mMaxPointerCount = this.mMaxPointerCount;
        if (motionEvent.getActionMasked() == 0) {
            this.mMaxPointerCount = motionEvent.getPointerCount();
        }
        else {
            this.mMaxPointerCount = Math.max(this.mMaxPointerCount, motionEvent.getPointerCount());
        }
        if (mMaxPointerCount != this.mMaxPointerCount) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Pointers observed:");
            sb.append(this.mMaxPointerCount);
            FalsingClassifier.logDebug(sb.toString());
        }
    }
}
