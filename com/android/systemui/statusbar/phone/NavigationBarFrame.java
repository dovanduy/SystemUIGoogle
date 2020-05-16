// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.MotionEvent;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.policy.DeadZone;
import android.widget.FrameLayout;

public class NavigationBarFrame extends FrameLayout
{
    private DeadZone mDeadZone;
    
    public NavigationBarFrame(final Context context) {
        super(context);
        this.mDeadZone = null;
    }
    
    public NavigationBarFrame(final Context context, final AttributeSet set) {
        super(context, set);
        this.mDeadZone = null;
    }
    
    public NavigationBarFrame(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mDeadZone = null;
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4) {
            final DeadZone mDeadZone = this.mDeadZone;
            if (mDeadZone != null) {
                return mDeadZone.onTouchEvent(motionEvent);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
    
    public void setDeadZone(final DeadZone mDeadZone) {
        this.mDeadZone = mDeadZone;
    }
}
