// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import java.util.ArrayList;
import java.util.List;

public abstract class ParallaxEffect
{
    final List<Object> mMarkerValues;
    final List<ParallaxTarget> mTargets;
    
    ParallaxEffect() {
        this.mMarkerValues = new ArrayList<Object>(2);
        new ArrayList(2);
        new ArrayList(2);
        this.mTargets = new ArrayList<ParallaxTarget>(4);
    }
    
    abstract Number calculateDirectValue(final Parallax p0);
    
    abstract float calculateFraction(final Parallax p0);
    
    public final void performMapping(final Parallax parallax) {
        if (this.mMarkerValues.size() < 2) {
            return;
        }
        parallax.verifyFloatProperties();
        float calculateFraction = 0.0f;
        Number n = null;
        int i = 0;
        int n2 = 0;
        while (i < this.mTargets.size()) {
            final ParallaxTarget parallaxTarget = this.mTargets.get(i);
            if (parallaxTarget.isDirectMapping()) {
                Number calculateDirectValue;
                if ((calculateDirectValue = n) == null) {
                    calculateDirectValue = this.calculateDirectValue(parallax);
                }
                parallaxTarget.directUpdate(calculateDirectValue);
                n = calculateDirectValue;
            }
            else {
                int n3;
                if ((n3 = n2) == 0) {
                    calculateFraction = this.calculateFraction(parallax);
                    n3 = 1;
                }
                parallaxTarget.update(calculateFraction);
                n2 = n3;
            }
            ++i;
        }
    }
}
