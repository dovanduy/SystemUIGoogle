// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import com.android.systemui.qs.PageIndicator;

public final class ManagementPageIndicator extends PageIndicator
{
    private Function1<? super Integer, Unit> visibilityListener;
    
    public ManagementPageIndicator(final Context context, final AttributeSet set) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(set, "attrs");
        super(context, set);
        this.visibilityListener = (Function1<? super Integer, Unit>)ManagementPageIndicator$visibilityListener.ManagementPageIndicator$visibilityListener$1.INSTANCE;
    }
    
    protected void onVisibilityChanged(final View view, final int i) {
        Intrinsics.checkParameterIsNotNull(view, "changedView");
        super.onVisibilityChanged(view, i);
        if (Intrinsics.areEqual(view, this)) {
            this.visibilityListener.invoke(i);
        }
    }
    
    @Override
    public void setLocation(final float location) {
        if (this.getLayoutDirection() == 1) {
            super.setLocation(this.getChildCount() - 1 - location);
        }
        else {
            super.setLocation(location);
        }
    }
    
    public final void setVisibilityListener(final Function1<? super Integer, Unit> visibilityListener) {
        Intrinsics.checkParameterIsNotNull(visibilityListener, "<set-?>");
        this.visibilityListener = visibilityListener;
    }
}
