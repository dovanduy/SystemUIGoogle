// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.util.Log;
import android.view.View;
import com.android.systemui.R$layout;
import android.view.ViewGroup;
import android.content.Context;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import com.android.systemui.statusbar.InflationTask;

public class RowInflaterTask implements InflationTask, OnInflateFinishedListener
{
    private boolean mCancelled;
    private NotificationEntry mEntry;
    private Throwable mInflateOrigin;
    private RowInflationFinishedListener mListener;
    
    @Override
    public void abort() {
        this.mCancelled = true;
    }
    
    public void inflate(final Context context, final ViewGroup viewGroup, final NotificationEntry mEntry, final RowInflationFinishedListener mListener) {
        this.mInflateOrigin = new Throwable("inflate requested here");
        this.mListener = mListener;
        final AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        (this.mEntry = mEntry).setInflationTask(this);
        asyncLayoutInflater.inflate(R$layout.status_bar_notification_row, viewGroup, (AsyncLayoutInflater.OnInflateFinishedListener)this);
    }
    
    @Override
    public void onInflateFinished(final View view, final int n, final ViewGroup viewGroup) {
        if (!this.mCancelled) {
            try {
                this.mEntry.onInflationTaskFinished();
                this.mListener.onInflationFinished((ExpandableNotificationRow)view);
            }
            finally {
                if (this.mInflateOrigin != null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Error in inflation finished listener: ");
                    final Throwable obj;
                    sb.append(obj);
                    Log.e("RowInflaterTask", sb.toString(), this.mInflateOrigin);
                    obj.addSuppressed(this.mInflateOrigin);
                }
            }
        }
    }
    
    public interface RowInflationFinishedListener
    {
        void onInflationFinished(final ExpandableNotificationRow p0);
    }
}
