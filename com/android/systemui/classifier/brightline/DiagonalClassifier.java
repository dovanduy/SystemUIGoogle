// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import java.util.Locale;
import com.android.systemui.util.DeviceConfigProxy;

class DiagonalClassifier extends FalsingClassifier
{
    private final float mHorizontalAngleRange;
    private final float mVerticalAngleRange;
    
    DiagonalClassifier(final FalsingDataProvider falsingDataProvider, final DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        this.mHorizontalAngleRange = deviceConfigProxy.getFloat("systemui", "brightline_falsing_diagonal_horizontal_angle_range", 0.08726646f);
        this.mVerticalAngleRange = deviceConfigProxy.getFloat("systemui", "brightline_falsing_diagonal_horizontal_angle_range", 0.08726646f);
    }
    
    private boolean angleBetween(final float n, float normalizeAngle, float normalizeAngle2) {
        normalizeAngle = this.normalizeAngle(normalizeAngle);
        normalizeAngle2 = this.normalizeAngle(normalizeAngle2);
        boolean b = true;
        final boolean b2 = true;
        if (normalizeAngle > normalizeAngle2) {
            boolean b3 = b2;
            if (n < normalizeAngle) {
                b3 = (n <= normalizeAngle2 && b2);
            }
            return b3;
        }
        if (n < normalizeAngle || n > normalizeAngle2) {
            b = false;
        }
        return b;
    }
    
    private float normalizeAngle(final float n) {
        if (n < 0.0f) {
            return n % 6.2831855f + 6.2831855f;
        }
        float n2 = n;
        if (n > 6.2831855f) {
            n2 = n % 6.2831855f;
        }
        return n2;
    }
    
    @Override
    String getReason() {
        return String.format(null, "{angle=%f, vertical=%s}", this.getAngle(), this.isVertical());
    }
    
    @Override
    boolean isFalseTouch() {
        final float angle = this.getAngle();
        final boolean b = false;
        if (angle == Float.MAX_VALUE) {
            return false;
        }
        boolean b2 = b;
        if (this.getInteractionType() != 5) {
            if (this.getInteractionType() == 6) {
                b2 = b;
            }
            else {
                final float mHorizontalAngleRange = this.mHorizontalAngleRange;
                float n = 0.7853982f - mHorizontalAngleRange;
                float n2 = mHorizontalAngleRange + 0.7853982f;
                if (this.isVertical()) {
                    final float mVerticalAngleRange = this.mVerticalAngleRange;
                    n = 0.7853982f - mVerticalAngleRange;
                    n2 = mVerticalAngleRange + 0.7853982f;
                }
                if (!this.angleBetween(angle, n, n2) && !this.angleBetween(angle, n + 1.5707964f, n2 + 1.5707964f) && !this.angleBetween(angle, n - 1.5707964f, n2 - 1.5707964f)) {
                    b2 = b;
                    if (!this.angleBetween(angle, n + 3.1415927f, n2 + 3.1415927f)) {
                        return b2;
                    }
                }
                b2 = true;
            }
        }
        return b2;
    }
}
