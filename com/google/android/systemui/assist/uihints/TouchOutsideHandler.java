// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.app.PendingIntent;

class TouchOutsideHandler implements ConfigInfoListener
{
    private PendingIntent mTouchOutside;
    
    @Override
    public void onConfigInfo(final ConfigInfo configInfo) {
        this.mTouchOutside = configInfo.onTouchOutside;
    }
    
    void onTouchOutside() {
        final PendingIntent mTouchOutside = this.mTouchOutside;
        if (mTouchOutside != null) {
            try {
                mTouchOutside.send();
            }
            catch (PendingIntent$CanceledException ex) {
                Log.w("TouchOutsideHandler", "Touch outside PendingIntent canceled");
            }
        }
    }
}
