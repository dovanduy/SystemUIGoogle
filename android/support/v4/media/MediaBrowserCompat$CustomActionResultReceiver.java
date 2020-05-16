// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import android.util.Log;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

class MediaBrowserCompat$CustomActionResultReceiver extends ResultReceiver
{
    private final String mAction;
    private final MediaBrowserCompat$CustomActionCallback mCallback;
    private final Bundle mExtras;
    
    @Override
    protected void onReceiveResult(final int i, final Bundle obj) {
        if (this.mCallback == null) {
            return;
        }
        MediaSessionCompat.ensureClassLoader(obj);
        if (i != -1) {
            if (i != 0) {
                if (i != 1) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unknown result code: ");
                    sb.append(i);
                    sb.append(" (extras=");
                    sb.append(this.mExtras);
                    sb.append(", resultData=");
                    sb.append(obj);
                    sb.append(")");
                    Log.w("MediaBrowserCompat", sb.toString());
                }
                else {
                    this.mCallback.onProgressUpdate(this.mAction, this.mExtras, obj);
                }
            }
            else {
                this.mCallback.onResult(this.mAction, this.mExtras, obj);
            }
        }
        else {
            this.mCallback.onError(this.mAction, this.mExtras, obj);
        }
    }
}
