// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.util.Pools$SimplePool;

public class ActionListTransformState extends TransformState
{
    private static Pools$SimplePool<ActionListTransformState> sInstancePool;
    
    static {
        ActionListTransformState.sInstancePool = (Pools$SimplePool<ActionListTransformState>)new Pools$SimplePool(40);
    }
    
    public static ActionListTransformState obtain() {
        final ActionListTransformState actionListTransformState = (ActionListTransformState)ActionListTransformState.sInstancePool.acquire();
        if (actionListTransformState != null) {
            return actionListTransformState;
        }
        return new ActionListTransformState();
    }
    
    @Override
    public void recycle() {
        super.recycle();
        ActionListTransformState.sInstancePool.release((Object)this);
    }
    
    @Override
    protected void resetTransformedView() {
        final float translationY = this.getTransformedView().getTranslationY();
        super.resetTransformedView();
        this.getTransformedView().setTranslationY(translationY);
    }
    
    @Override
    protected boolean sameAs(final TransformState transformState) {
        return transformState instanceof ActionListTransformState;
    }
    
    @Override
    public void transformViewFullyFrom(final TransformState transformState, final float n) {
    }
    
    @Override
    public void transformViewFullyTo(final TransformState transformState, final float n) {
    }
}
