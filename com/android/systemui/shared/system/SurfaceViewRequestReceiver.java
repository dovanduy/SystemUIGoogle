// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.view.SurfaceControl;
import android.view.WindowManager$LayoutParams;
import android.view.WindowlessWindowManager;
import android.hardware.display.DisplayManager;
import android.util.Size;
import android.view.View;
import android.os.Bundle;
import android.content.Context;
import android.view.SurfaceControlViewHost;

public class SurfaceViewRequestReceiver
{
    private final int mOpacity;
    private SurfaceControlViewHost mSurfaceControlViewHost;
    
    public SurfaceViewRequestReceiver() {
        this(-2);
    }
    
    public SurfaceViewRequestReceiver(final int mOpacity) {
        this.mOpacity = mOpacity;
    }
    
    public void onReceive(final Context context, final Bundle bundle, final View view) {
        this.onReceive(context, bundle, view, null);
    }
    
    public void onReceive(final Context context, final Bundle bundle, final View view, final Size size) {
        final SurfaceControlViewHost mSurfaceControlViewHost = this.mSurfaceControlViewHost;
        if (mSurfaceControlViewHost != null) {
            mSurfaceControlViewHost.die();
        }
        final SurfaceControl surfaceControl = SurfaceViewRequestUtils.getSurfaceControl(bundle);
        if (surfaceControl != null) {
            Size size2;
            if ((size2 = size) == null) {
                size2 = new Size(surfaceControl.getWidth(), surfaceControl.getHeight());
            }
            this.mSurfaceControlViewHost = new SurfaceControlViewHost(context, ((DisplayManager)context.getSystemService("display")).getDisplay(SurfaceViewRequestUtils.getDisplayId(bundle)), new WindowlessWindowManager(context.getResources().getConfiguration(), surfaceControl, SurfaceViewRequestUtils.getHostToken(bundle)));
            final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(size2.getWidth(), size2.getHeight(), 2, 16777216, this.mOpacity);
            final float min = Math.min(surfaceControl.getWidth() / (float)size2.getWidth(), surfaceControl.getHeight() / (float)size2.getHeight());
            view.setScaleX(min);
            view.setScaleY(min);
            view.setPivotX(0.0f);
            view.setPivotY(0.0f);
            view.setTranslationX((surfaceControl.getWidth() - size2.getWidth() * min) / 2.0f);
            view.setTranslationY((surfaceControl.getHeight() - min * size2.getHeight()) / 2.0f);
            this.mSurfaceControlViewHost.setView(view, windowManager$LayoutParams);
        }
    }
}
