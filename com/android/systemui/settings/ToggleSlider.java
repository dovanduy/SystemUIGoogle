// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

public interface ToggleSlider
{
    int getValue();
    
    default void setChecked(final boolean b) {
    }
    
    void setMax(final int p0);
    
    void setOnChangedListener(final Listener p0);
    
    void setValue(final int p0);
    
    public interface Listener
    {
        void onChanged(final ToggleSlider p0, final boolean p1, final boolean p2, final int p3, final boolean p4);
        
        void onInit(final ToggleSlider p0);
    }
}
