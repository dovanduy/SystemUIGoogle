// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

class DeleteImageInBackgroundTask extends AsyncTask<Uri, Void, Void>
{
    private Context mContext;
    
    DeleteImageInBackgroundTask(final Context mContext) {
        this.mContext = mContext;
    }
    
    protected Void doInBackground(final Uri... array) {
        if (array.length != 1) {
            return null;
        }
        this.mContext.getContentResolver().delete(array[0], (String)null, (String[])null);
        return null;
    }
}
