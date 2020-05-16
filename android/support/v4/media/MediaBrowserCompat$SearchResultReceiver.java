// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import android.os.Parcelable;
import java.util.List;
import java.util.ArrayList;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

class MediaBrowserCompat$SearchResultReceiver extends ResultReceiver
{
    private final MediaBrowserCompat$SearchCallback mCallback;
    private final Bundle mExtras;
    private final String mQuery;
    
    @Override
    protected void onReceiveResult(int i, final Bundle bundle) {
        Bundle unparcelWithClassLoader = bundle;
        if (bundle != null) {
            unparcelWithClassLoader = MediaSessionCompat.unparcelWithClassLoader(bundle);
        }
        if (i == 0 && unparcelWithClassLoader != null && unparcelWithClassLoader.containsKey("search_results")) {
            final Parcelable[] parcelableArray = unparcelWithClassLoader.getParcelableArray("search_results");
            if (parcelableArray != null) {
                final ArrayList<MediaBrowserCompat$MediaItem> list = new ArrayList<MediaBrowserCompat$MediaItem>();
                int length;
                for (length = parcelableArray.length, i = 0; i < length; ++i) {
                    list.add((MediaBrowserCompat$MediaItem)parcelableArray[i]);
                }
                this.mCallback.onSearchResult(this.mQuery, this.mExtras, list);
            }
            else {
                this.mCallback.onError(this.mQuery, this.mExtras);
            }
            return;
        }
        this.mCallback.onError(this.mQuery, this.mExtras);
    }
}
