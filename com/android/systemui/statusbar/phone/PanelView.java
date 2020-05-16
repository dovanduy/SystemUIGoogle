// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.View$OnTouchListener;
import android.view.MotionEvent;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public abstract class PanelView extends FrameLayout
{
    private OnConfigurationChangedListener mOnConfigurationChangedListener;
    private PanelViewController.TouchHandler mTouchHandler;
    
    public PanelView(final Context context) {
        super(context);
    }
    
    public PanelView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public PanelView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public PanelView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public void dispatchConfigurationChanged(final Configuration configuration) {
        super.dispatchConfigurationChanged(configuration);
        this.mOnConfigurationChangedListener.onConfigurationChanged(configuration);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.mTouchHandler.onInterceptTouchEvent(motionEvent);
    }
    
    public void setOnConfigurationChangedListener(final OnConfigurationChangedListener mOnConfigurationChangedListener) {
        this.mOnConfigurationChangedListener = mOnConfigurationChangedListener;
    }
    
    public void setOnTouchListener(final PanelViewController.TouchHandler touchHandler) {
        super.setOnTouchListener((View$OnTouchListener)touchHandler);
        this.mTouchHandler = touchHandler;
    }
    
    interface OnConfigurationChangedListener
    {
        void onConfigurationChanged(final Configuration p0);
    }
}
