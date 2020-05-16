// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.util.Pools$SimplePool;

public class ProgressTransformState extends TransformState
{
    private static Pools$SimplePool<ProgressTransformState> sInstancePool;
    
    static {
        ProgressTransformState.sInstancePool = (Pools$SimplePool<ProgressTransformState>)new Pools$SimplePool(40);
    }
    
    public static ProgressTransformState obtain() {
        final ProgressTransformState progressTransformState = (ProgressTransformState)ProgressTransformState.sInstancePool.acquire();
        if (progressTransformState != null) {
            return progressTransformState;
        }
        return new ProgressTransformState();
    }
    
    @Override
    public void recycle() {
        super.recycle();
        ProgressTransformState.sInstancePool.release((Object)this);
    }
    
    @Override
    protected boolean sameAs(final TransformState transformState) {
        return transformState instanceof ProgressTransformState || super.sameAs(transformState);
    }
}
