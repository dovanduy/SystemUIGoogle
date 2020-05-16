// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.wakelock;

import android.view.animation.Animation;
import android.animation.Animator;
import com.android.systemui.util.Assert;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import android.view.animation.Animation$AnimationListener;
import android.animation.AnimatorListenerAdapter;

public class KeepAwakeAnimationListener extends AnimatorListenerAdapter implements Animation$AnimationListener
{
    @VisibleForTesting
    static WakeLock sWakeLock;
    
    public KeepAwakeAnimationListener(final Context context) {
        Assert.isMainThread();
        if (KeepAwakeAnimationListener.sWakeLock == null) {
            KeepAwakeAnimationListener.sWakeLock = WakeLock.createPartial(context, "animation");
        }
    }
    
    private void onEnd() {
        Assert.isMainThread();
        KeepAwakeAnimationListener.sWakeLock.release("KeepAwakeAnimListener");
    }
    
    private void onStart() {
        Assert.isMainThread();
        KeepAwakeAnimationListener.sWakeLock.acquire("KeepAwakeAnimListener");
    }
    
    public void onAnimationEnd(final Animator animator) {
        this.onEnd();
    }
    
    public void onAnimationEnd(final Animation animation) {
        this.onEnd();
    }
    
    public void onAnimationRepeat(final Animation animation) {
    }
    
    public void onAnimationStart(final Animator animator) {
        this.onStart();
    }
    
    public void onAnimationStart(final Animation animation) {
        this.onStart();
    }
}
