// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import android.os.Parcelable;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

class MediaBrowserCompat$ItemReceiver extends ResultReceiver
{
    private final MediaBrowserCompat$ItemCallback mCallback;
    private final String mMediaId;
    
    @Override
    protected void onReceiveResult(final int n, final Bundle bundle) {
        Bundle unparcelWithClassLoader = bundle;
        if (bundle != null) {
            unparcelWithClassLoader = MediaSessionCompat.unparcelWithClassLoader(bundle);
        }
        if (n == 0 && unparcelWithClassLoader != null && unparcelWithClassLoader.containsKey("media_item")) {
            final Parcelable parcelable = unparcelWithClassLoader.getParcelable("media_item");
            if (parcelable != null && !(parcelable instanceof MediaBrowserCompat$MediaItem)) {
                this.mCallback.onError(this.mMediaId);
            }
            else {
                this.mCallback.onItemLoaded((MediaBrowserCompat$MediaItem)parcelable);
            }
            return;
        }
        this.mCallback.onError(this.mMediaId);
    }
}
