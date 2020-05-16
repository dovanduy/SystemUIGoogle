// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import android.view.WindowInsets$Type;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.Point;
import android.view.InsetsState;
import android.view.InsetsSourceControl;
import android.animation.ValueAnimator;
import android.view.IDisplayWindowInsetsController$Stub;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.util.Slog;
import android.view.IDisplayWindowInsetsController;
import android.view.InsetsSource;
import java.util.Iterator;
import android.view.SurfaceControl$Transaction;
import android.view.animation.PathInterpolator;
import com.android.systemui.TransactionPool;
import java.util.ArrayList;
import android.util.SparseArray;
import android.os.Handler;
import android.view.animation.Interpolator;

public class DisplayImeController implements OnDisplaysChangedListener
{
    public static final Interpolator INTERPOLATOR;
    final Handler mHandler;
    final SparseArray<PerDisplay> mImePerDisplay;
    final ArrayList<ImePositionProcessor> mPositionProcessors;
    SystemWindows mSystemWindows;
    final TransactionPool mTransactionPool;
    
    static {
        INTERPOLATOR = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    }
    
    public DisplayImeController(final SystemWindows mSystemWindows, final DisplayController displayController, final Handler mHandler, final TransactionPool mTransactionPool) {
        this.mImePerDisplay = (SparseArray<PerDisplay>)new SparseArray();
        this.mPositionProcessors = new ArrayList<ImePositionProcessor>();
        this.mHandler = mHandler;
        this.mSystemWindows = mSystemWindows;
        this.mTransactionPool = mTransactionPool;
        displayController.addDisplayWindowListener((DisplayController.OnDisplaysChangedListener)this);
    }
    
    private void dispatchEndPositioning(final int n, final boolean b, final SurfaceControl$Transaction surfaceControl$Transaction) {
        synchronized (this.mPositionProcessors) {
            final Iterator<ImePositionProcessor> iterator = this.mPositionProcessors.iterator();
            while (iterator.hasNext()) {
                iterator.next().onImeEndPositioning(n, b, surfaceControl$Transaction);
            }
        }
    }
    
    private void dispatchPositionChanged(final int n, final int n2, final SurfaceControl$Transaction surfaceControl$Transaction) {
        synchronized (this.mPositionProcessors) {
            final Iterator<ImePositionProcessor> iterator = this.mPositionProcessors.iterator();
            while (iterator.hasNext()) {
                iterator.next().onImePositionChanged(n, n2, surfaceControl$Transaction);
            }
        }
    }
    
    private void dispatchStartPositioning(final int n, final int n2, final int n3, final boolean b, final SurfaceControl$Transaction surfaceControl$Transaction) {
        synchronized (this.mPositionProcessors) {
            final Iterator<ImePositionProcessor> iterator = this.mPositionProcessors.iterator();
            while (iterator.hasNext()) {
                iterator.next().onImeStartPositioning(n, n2, n3, b, surfaceControl$Transaction);
            }
        }
    }
    
    private boolean isImeShowing(final int n) {
        final PerDisplay perDisplay = (PerDisplay)this.mImePerDisplay.get(n);
        final boolean b = false;
        if (perDisplay == null) {
            return false;
        }
        final InsetsSource source = perDisplay.mInsetsState.getSource(13);
        boolean b2 = b;
        if (source != null) {
            b2 = b;
            if (perDisplay.mImeSourceControl != null) {
                b2 = b;
                if (source.isVisible()) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public void addPositionProcessor(final ImePositionProcessor imePositionProcessor) {
        synchronized (this.mPositionProcessors) {
            if (this.mPositionProcessors.contains(imePositionProcessor)) {
                return;
            }
            this.mPositionProcessors.add(imePositionProcessor);
        }
    }
    
    @Override
    public void onDisplayAdded(final int i) {
        final PerDisplay perDisplay = new PerDisplay(i, this.mSystemWindows.mDisplayController.getDisplayLayout(i).rotation());
        try {
            this.mSystemWindows.mWmService.setDisplayWindowInsetsController(i, (IDisplayWindowInsetsController)perDisplay);
        }
        catch (RemoteException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unable to set insets controller on display ");
            sb.append(i);
            Slog.w("DisplayImeController", sb.toString());
        }
        this.mImePerDisplay.put(i, (Object)perDisplay);
    }
    
    @Override
    public void onDisplayConfigurationChanged(final int n, final Configuration configuration) {
        final PerDisplay perDisplay = (PerDisplay)this.mImePerDisplay.get(n);
        if (perDisplay == null) {
            return;
        }
        if (this.mSystemWindows.mDisplayController.getDisplayLayout(n).rotation() != perDisplay.mRotation && this.isImeShowing(n)) {
            perDisplay.startAnimation(true, false);
        }
    }
    
    @Override
    public void onDisplayRemoved(final int i) {
        try {
            this.mSystemWindows.mWmService.setDisplayWindowInsetsController(i, (IDisplayWindowInsetsController)null);
        }
        catch (RemoteException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unable to remove insets controller on display ");
            sb.append(i);
            Slog.w("DisplayImeController", sb.toString());
        }
        this.mImePerDisplay.remove(i);
    }
    
    public interface ImePositionProcessor
    {
        default void onImeEndPositioning(final int n, final boolean b, final SurfaceControl$Transaction surfaceControl$Transaction) {
        }
        
        default void onImePositionChanged(final int n, final int n2, final SurfaceControl$Transaction surfaceControl$Transaction) {
        }
        
        default void onImeStartPositioning(final int n, final int n2, final int n3, final boolean b, final SurfaceControl$Transaction surfaceControl$Transaction) {
        }
    }
    
    class PerDisplay extends IDisplayWindowInsetsController$Stub
    {
        ValueAnimator mAnimation;
        int mAnimationDirection;
        final int mDisplayId;
        boolean mImeShowing;
        InsetsSourceControl mImeSourceControl;
        final InsetsState mInsetsState;
        int mRotation;
        
        PerDisplay(final int mDisplayId, final int mRotation) {
            this.mInsetsState = new InsetsState();
            this.mImeSourceControl = null;
            this.mAnimationDirection = 0;
            this.mAnimation = null;
            this.mRotation = 0;
            this.mImeShowing = false;
            this.mDisplayId = mDisplayId;
            this.mRotation = mRotation;
        }
        
        private int imeTop(final InsetsSource insetsSource, final float n) {
            return insetsSource.getFrame().top + (int)n;
        }
        
        private void setVisibleDirectly(final boolean visible) {
            this.mInsetsState.getSource(13).setVisible(visible);
            try {
                DisplayImeController.this.mSystemWindows.mWmService.modifyDisplayWindowInsets(this.mDisplayId, this.mInsetsState);
            }
            catch (RemoteException ex) {}
        }
        
        private void startAnimation(final boolean b, final boolean b2) {
            final InsetsSource source = this.mInsetsState.getSource(13);
            if (source != null) {
                if (this.mImeSourceControl != null) {
                    DisplayImeController.this.mHandler.post((Runnable)new _$$Lambda$DisplayImeController$PerDisplay$irpE97V1AFyqzjtDfrtehF8szPU(this, b, b2, source));
                }
            }
        }
        
        public void hideInsets(final int n, final boolean b) {
            if ((n & WindowInsets$Type.ime()) == 0x0) {
                return;
            }
            this.startAnimation(false, false);
        }
        
        public void insetsChanged(final InsetsState insetsState) {
            if (this.mInsetsState.equals((Object)insetsState)) {
                return;
            }
            this.mInsetsState.set(insetsState, true);
        }
        
        public void insetsControlChanged(final InsetsState insetsState, final InsetsSourceControl[] array) {
            this.insetsChanged(insetsState);
            if (array != null) {
                for (final InsetsSourceControl insetsSourceControl : array) {
                    if (insetsSourceControl != null) {
                        if (insetsSourceControl.getType() == 13) {
                            DisplayImeController.this.mHandler.post((Runnable)new _$$Lambda$DisplayImeController$PerDisplay$qntN7Oa_7XpsmksyGcv5yMS4eOA(this, insetsSourceControl));
                        }
                    }
                }
            }
        }
        
        public void showInsets(final int n, final boolean b) {
            if ((n & WindowInsets$Type.ime()) == 0x0) {
                return;
            }
            this.startAnimation(true, false);
        }
    }
}
