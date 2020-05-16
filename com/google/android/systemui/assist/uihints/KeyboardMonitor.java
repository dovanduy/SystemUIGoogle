// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.os.IBinder;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import java.util.function.Consumer;
import java.util.Optional;
import android.app.PendingIntent;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;

final class KeyboardMonitor implements Callbacks, ConfigInfoListener
{
    private final Context mContext;
    private PendingIntent mOnKeyboardShowingChanged;
    private boolean mShowing;
    
    KeyboardMonitor(final Context mContext, final Optional<CommandQueue> optional) {
        this.mContext = mContext;
        optional.ifPresent(new _$$Lambda$KeyboardMonitor$nm4qnUIXxo_ZVrAXHyUl5c4SJDo(this));
    }
    
    private void trySendKeyboardShowing() {
        if (this.mOnKeyboardShowingChanged != null) {
            final Intent intent = new Intent();
            intent.putExtra("is_keyboard_showing", this.mShowing);
            try {
                this.mOnKeyboardShowingChanged.send(this.mContext, 0, intent);
            }
            catch (PendingIntent$CanceledException ex) {
                Log.e("KeyboardMonitor", "onKeyboardShowingChanged pending intent cancelled", (Throwable)ex);
            }
        }
    }
    
    @Override
    public void onConfigInfo(final ConfigInfo configInfo) {
        final PendingIntent mOnKeyboardShowingChanged = this.mOnKeyboardShowingChanged;
        final PendingIntent onKeyboardShowingChange = configInfo.onKeyboardShowingChange;
        if (mOnKeyboardShowingChanged != onKeyboardShowingChange) {
            this.mOnKeyboardShowingChanged = onKeyboardShowingChange;
            this.trySendKeyboardShowing();
        }
    }
    
    @Override
    public void setImeWindowStatus(final int n, final IBinder binder, final int n2, final int n3, final boolean b) {
        final boolean mShowing = this.mShowing;
        final boolean mShowing2 = (n2 & 0x2) != 0x0;
        this.mShowing = mShowing2;
        if (mShowing2 != mShowing) {
            this.trySendKeyboardShowing();
        }
    }
}
