// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import androidx.core.os.CancellationSignal;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public abstract class BindRequester
{
    private BindRequestListener mBindRequestListener;
    
    public final CancellationSignal requestRebind(final NotificationEntry notificationEntry, final NotifBindPipeline.BindCallback bindCallback) {
        final CancellationSignal cancellationSignal = new CancellationSignal();
        final BindRequestListener mBindRequestListener = this.mBindRequestListener;
        if (mBindRequestListener != null) {
            mBindRequestListener.onBindRequest(notificationEntry, cancellationSignal, bindCallback);
        }
        return cancellationSignal;
    }
    
    final void setBindRequestListener(final BindRequestListener mBindRequestListener) {
        this.mBindRequestListener = mBindRequestListener;
    }
    
    public interface BindRequestListener
    {
        void onBindRequest(final NotificationEntry p0, final CancellationSignal p1, final NotifBindPipeline.BindCallback p2);
    }
}
