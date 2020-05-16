// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import android.util.Property;

public abstract class Parallax<PropertyT extends Property>
{
    private final List<ParallaxEffect> mEffects;
    private float[] mFloatValues;
    final List<PropertyT> mProperties;
    private int[] mValues;
    
    public Parallax() {
        Collections.unmodifiableList((List<?>)(this.mProperties = new ArrayList<PropertyT>()));
        this.mValues = new int[4];
        this.mFloatValues = new float[4];
        this.mEffects = new ArrayList<ParallaxEffect>(4);
    }
    
    final float getFloatPropertyValue(final int n) {
        return this.mFloatValues[n];
    }
    
    public void updateValues() {
        for (int i = 0; i < this.mEffects.size(); ++i) {
            this.mEffects.get(i).performMapping(this);
        }
    }
    
    final void verifyFloatProperties() throws IllegalStateException {
        if (this.mProperties.size() < 2) {
            return;
        }
        float floatPropertyValue = this.getFloatPropertyValue(0);
        float floatPropertyValue2;
        for (int i = 1; i < this.mProperties.size(); ++i, floatPropertyValue = floatPropertyValue2) {
            floatPropertyValue2 = this.getFloatPropertyValue(i);
            if (floatPropertyValue2 < floatPropertyValue) {
                final String name = this.mProperties.get(i).getName();
                final int j = i - 1;
                throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is smaller than Property[%d]\"%s\"", i, name, j, this.mProperties.get(j).getName()));
            }
            if (floatPropertyValue == -3.4028235E38f && floatPropertyValue2 == Float.MAX_VALUE) {
                final int k = i - 1;
                throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is UNKNOWN_BEFORE and Property[%d]\"%s\" is UNKNOWN_AFTER", k, this.mProperties.get(k).getName(), i, this.mProperties.get(i).getName()));
            }
        }
    }
}
