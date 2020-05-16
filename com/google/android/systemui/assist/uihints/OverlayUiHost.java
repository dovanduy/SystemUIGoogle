// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.WindowManager;
import android.view.WindowManager$LayoutParams;

class OverlayUiHost
{
    private boolean mAttached;
    private boolean mFocusable;
    private WindowManager$LayoutParams mLayoutParams;
    private final AssistUIView mRoot;
    private final WindowManager mWindowManager;
    
    public OverlayUiHost(final Context context, final TouchOutsideHandler touchOutside) {
        this.mAttached = false;
        this.mFocusable = false;
        (this.mRoot = (AssistUIView)LayoutInflater.from(context).inflate(R$layout.assist_ui, (ViewGroup)null, false)).setTouchOutside(touchOutside);
        this.mWindowManager = (WindowManager)context.getSystemService("window");
    }
    
    public ViewGroup getParent() {
        return (ViewGroup)this.mRoot;
    }
    
    void setAssistState(final boolean b, final boolean b2) {
        if (b && !this.mAttached) {
            final WindowManager$LayoutParams mLayoutParams = new WindowManager$LayoutParams(-1, -1, 0, 0, 2024, 262952, -3);
            this.mLayoutParams = mLayoutParams;
            this.mFocusable = b2;
            mLayoutParams.gravity = 80;
            mLayoutParams.privateFlags = 64;
            mLayoutParams.setTitle((CharSequence)"Assist");
            this.mWindowManager.addView((View)this.mRoot, (ViewGroup$LayoutParams)this.mLayoutParams);
            this.mAttached = true;
        }
        else if (!b && this.mAttached) {
            this.mWindowManager.removeViewImmediate((View)this.mRoot);
            this.mAttached = false;
        }
        else if (b && this.mFocusable != b2) {
            this.mWindowManager.updateViewLayout((View)this.mRoot, (ViewGroup$LayoutParams)this.mLayoutParams);
            this.mFocusable = b2;
        }
    }
}
