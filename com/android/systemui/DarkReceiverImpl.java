// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.graphics.Rect;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.view.View;

public final class DarkReceiverImpl extends View implements DarkReceiver
{
    private final DualToneHandler dualToneHandler;
    
    public DarkReceiverImpl(final Context context, final AttributeSet set) {
        this(context, set, 0, 0, 12, null);
    }
    
    public DarkReceiverImpl(final Context context, final AttributeSet set, final int n, final int n2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, set, n, n2);
        this.dualToneHandler = new DualToneHandler(context);
        this.onDarkChanged(new Rect(), 1.0f, -1);
    }
    
    public void onDarkChanged(final Rect rect, float n, final int n2) {
        if (!DarkIconDispatcher.isInArea(rect, this)) {
            n = 0.0f;
        }
        this.setBackgroundColor(this.dualToneHandler.getSingleColor(n));
    }
}
