// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.animation;

import kotlin.jvm.internal.FloatCompanionObject;
import java.util.WeakHashMap;

public final class PhysicsAnimatorKt
{
    private static final float UNSET;
    private static final WeakHashMap<Object, PhysicsAnimator<?>> animators;
    private static final PhysicsAnimator.FlingConfig defaultFling;
    private static final PhysicsAnimator.SpringConfig defaultSpring;
    private static boolean verboseLogging;
    
    static {
        UNSET = -FloatCompanionObject.INSTANCE.getMAX_VALUE();
        animators = new WeakHashMap<Object, PhysicsAnimator<?>>();
        defaultSpring = new PhysicsAnimator.SpringConfig(1500.0f, 0.5f);
        defaultFling = new PhysicsAnimator.FlingConfig(1.0f, -FloatCompanionObject.INSTANCE.getMAX_VALUE(), FloatCompanionObject.INSTANCE.getMAX_VALUE());
    }
    
    public static final WeakHashMap<Object, PhysicsAnimator<?>> getAnimators() {
        return PhysicsAnimatorKt.animators;
    }
}
