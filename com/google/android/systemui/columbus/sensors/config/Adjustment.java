// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors.config;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public abstract class Adjustment
{
    public abstract float adjustSensitivity(final float p0);
    
    public final void setCallback(final Function1<? super Adjustment, Unit> function1) {
    }
}
