// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.internal.view.RotationPolicy;
import java.util.Iterator;
import com.android.internal.view.RotationPolicy$RotationPolicyListener;
import android.content.Context;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RotationLockControllerImpl implements RotationLockController
{
    private final CopyOnWriteArrayList<RotationLockControllerCallback> mCallbacks;
    private final Context mContext;
    private final RotationPolicy$RotationPolicyListener mRotationPolicyListener;
    
    public RotationLockControllerImpl(final Context mContext) {
        this.mCallbacks = new CopyOnWriteArrayList<RotationLockControllerCallback>();
        this.mRotationPolicyListener = new RotationPolicy$RotationPolicyListener() {
            public void onChange() {
                RotationLockControllerImpl.this.notifyChanged();
            }
        };
        this.mContext = mContext;
        this.setListening(true);
    }
    
    private void notifyChanged() {
        final Iterator<RotationLockControllerCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            this.notifyChanged(iterator.next());
        }
    }
    
    private void notifyChanged(final RotationLockControllerCallback rotationLockControllerCallback) {
        rotationLockControllerCallback.onRotationLockStateChanged(RotationPolicy.isRotationLocked(this.mContext), RotationPolicy.isRotationLockToggleVisible(this.mContext));
    }
    
    @Override
    public void addCallback(final RotationLockControllerCallback e) {
        this.mCallbacks.add(e);
        this.notifyChanged(e);
    }
    
    @Override
    public int getRotationLockOrientation() {
        return RotationPolicy.getRotationLockOrientation(this.mContext);
    }
    
    @Override
    public boolean isRotationLocked() {
        return RotationPolicy.isRotationLocked(this.mContext);
    }
    
    @Override
    public void removeCallback(final RotationLockControllerCallback o) {
        this.mCallbacks.remove(o);
    }
    
    public void setListening(final boolean b) {
        if (b) {
            RotationPolicy.registerRotationPolicyListener(this.mContext, this.mRotationPolicyListener, -1);
        }
        else {
            RotationPolicy.unregisterRotationPolicyListener(this.mContext, this.mRotationPolicyListener);
        }
    }
    
    @Override
    public void setRotationLocked(final boolean b) {
        RotationPolicy.setRotationLock(this.mContext, b);
    }
    
    @Override
    public void setRotationLockedAtAngle(final boolean b, final int n) {
        RotationPolicy.setRotationLockAtAngle(this.mContext, b, n);
    }
}
