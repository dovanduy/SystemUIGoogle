// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import java.util.List;
import android.os.Bundle;

public abstract class MediaBrowserCompat$SearchCallback
{
    public abstract void onError(final String p0, final Bundle p1);
    
    public abstract void onSearchResult(final String p0, final Bundle p1, final List<MediaBrowserCompat$MediaItem> p2);
}
