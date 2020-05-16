// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.qs;

import android.content.Intent;
import android.view.ViewGroup;
import android.view.View;
import android.content.Context;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(version = 1)
public interface DetailAdapter
{
    public static final int VERSION = 1;
    
    View createDetailView(final Context p0, final View p1, final ViewGroup p2);
    
    int getMetricsCategory();
    
    Intent getSettingsIntent();
    
    CharSequence getTitle();
    
    default boolean getToggleEnabled() {
        return true;
    }
    
    Boolean getToggleState();
    
    default boolean hasHeader() {
        return true;
    }
    
    void setToggleState(final boolean p0);
}
