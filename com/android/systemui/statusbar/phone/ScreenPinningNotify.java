// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.R$string;
import android.util.Slog;
import android.os.SystemClock;
import com.android.systemui.SysUIToast;
import android.widget.Toast;
import android.content.Context;

public class ScreenPinningNotify
{
    private final Context mContext;
    private long mLastShowToastTime;
    private Toast mLastToast;
    
    public ScreenPinningNotify(final Context mContext) {
        this.mContext = mContext;
    }
    
    private Toast makeAllUserToastAndShow(final int n) {
        final Toast text = SysUIToast.makeText(this.mContext, n, 1);
        text.show();
        return text;
    }
    
    public void showEscapeToast(final boolean b, final boolean b2) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime - this.mLastShowToastTime < 1000L) {
            Slog.i("ScreenPinningNotify", "Ignore toast since it is requested in very short interval.");
            return;
        }
        final Toast mLastToast = this.mLastToast;
        if (mLastToast != null) {
            mLastToast.cancel();
        }
        int n;
        if (b) {
            n = R$string.screen_pinning_toast_gesture_nav;
        }
        else if (b2) {
            n = R$string.screen_pinning_toast;
        }
        else {
            n = R$string.screen_pinning_toast_recents_invisible;
        }
        this.mLastToast = this.makeAllUserToastAndShow(n);
        this.mLastShowToastTime = elapsedRealtime;
    }
    
    public void showPinningExitToast() {
        this.makeAllUserToastAndShow(R$string.screen_pinning_exit);
    }
    
    public void showPinningStartToast() {
        this.makeAllUserToastAndShow(R$string.screen_pinning_start);
    }
}
