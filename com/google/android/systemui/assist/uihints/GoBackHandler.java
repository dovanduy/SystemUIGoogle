// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.InputEvent;
import android.view.KeyEvent;
import android.hardware.input.InputManager;
import android.os.SystemClock;

class GoBackHandler implements GoBackListener
{
    private void injectBackKeyEvent(final int n) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        InputManager.getInstance().injectInputEvent((InputEvent)new KeyEvent(uptimeMillis, uptimeMillis, n, 4, 0, 0, -1, 0, 72, 257), 0);
    }
    
    @Override
    public void onGoBack() {
        this.injectBackKeyEvent(0);
        this.injectBackKeyEvent(1);
    }
}
