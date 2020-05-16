// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import com.android.systemui.statusbar.ScalingDrawableWrapper;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import com.android.systemui.R$layout;
import android.content.Context;
import androidx.preference.CheckBoxPreference;

public class SelectablePreference extends CheckBoxPreference
{
    private final int mSize;
    
    public SelectablePreference(final Context context) {
        super(context);
        this.setWidgetLayoutResource(R$layout.preference_widget_radiobutton);
        this.setSelectable(true);
        this.mSize = (int)TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
    }
    
    @Override
    public void setIcon(final Drawable drawable) {
        super.setIcon((Drawable)new ScalingDrawableWrapper(drawable, this.mSize / (float)drawable.getIntrinsicWidth()));
    }
    
    @Override
    public String toString() {
        return "";
    }
}
