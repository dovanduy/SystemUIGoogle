// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.view.View$OnGenericMotionListener;
import android.view.MotionEvent;
import android.view.View$OnTouchListener;
import android.view.View;

public class Interaction
{
    public static void register(final View view, final Callback callback) {
        view.setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                callback.onInteraction();
                return false;
            }
        });
        view.setOnGenericMotionListener((View$OnGenericMotionListener)new View$OnGenericMotionListener() {
            public boolean onGenericMotion(final View view, final MotionEvent motionEvent) {
                callback.onInteraction();
                return false;
            }
        });
    }
    
    public interface Callback
    {
        void onInteraction();
    }
}
