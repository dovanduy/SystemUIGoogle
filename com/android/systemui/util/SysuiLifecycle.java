// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import android.view.View$OnAttachStateChangeListener;
import androidx.lifecycle.LifecycleOwner;
import android.view.View;

public class SysuiLifecycle
{
    public static LifecycleOwner viewAttachLifecycle(final View view) {
        return new ViewLifecycle(view);
    }
    
    private static class ViewLifecycle implements LifecycleOwner, View$OnAttachStateChangeListener
    {
        private final LifecycleRegistry mLifecycle;
        
        ViewLifecycle(final View view) {
            this.mLifecycle = new LifecycleRegistry(this);
            view.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
            if (view.isAttachedToWindow()) {
                this.mLifecycle.markState(Lifecycle.State.RESUMED);
            }
        }
        
        @Override
        public Lifecycle getLifecycle() {
            return this.mLifecycle;
        }
        
        public void onViewAttachedToWindow(final View view) {
            this.mLifecycle.markState(Lifecycle.State.RESUMED);
        }
        
        public void onViewDetachedFromWindow(final View view) {
            this.mLifecycle.markState(Lifecycle.State.DESTROYED);
        }
    }
}
