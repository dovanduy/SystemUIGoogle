// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import java.io.IOException;
import java.util.function.Predicate;
import android.os.AsyncTask;
import java.util.concurrent.ExecutionException;
import android.util.Log;
import android.graphics.drawable.Drawable;
import java.util.Map;
import java.util.Set;
import android.net.Uri;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationInlineImageCache implements ImageCache
{
    private static final String TAG = "NotificationInlineImageCache";
    private final ConcurrentHashMap<Uri, PreloadImageTask> mCache;
    private NotificationInlineImageResolver mResolver;
    
    public NotificationInlineImageCache() {
        this.mCache = new ConcurrentHashMap<Uri, PreloadImageTask>();
    }
    
    @Override
    public Drawable get(Uri uri) {
        try {
            uri = (Uri)this.mCache.get(uri).get();
        }
        catch (InterruptedException | ExecutionException ex) {
            final String tag = NotificationInlineImageCache.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("get: Failed get image from ");
            sb.append(uri);
            Log.d(tag, sb.toString());
            uri = null;
        }
        return (Drawable)uri;
    }
    
    @Override
    public boolean hasEntry(final Uri key) {
        return this.mCache.containsKey(key);
    }
    
    @Override
    public void preload(final Uri key) {
        final PreloadImageTask value = new PreloadImageTask(this.mResolver);
        value.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])new Uri[] { key });
        this.mCache.put(key, value);
    }
    
    @Override
    public void purge() {
        this.mCache.entrySet().removeIf(new _$$Lambda$NotificationInlineImageCache$W1d4bA0jU1G2gSKuFNWjVLFgYyA(this.mResolver.getWantedUriSet()));
    }
    
    @Override
    public void setImageResolver(final NotificationInlineImageResolver mResolver) {
        this.mResolver = mResolver;
    }
    
    private static class PreloadImageTask extends AsyncTask<Uri, Void, Drawable>
    {
        private final NotificationInlineImageResolver mResolver;
        
        PreloadImageTask(final NotificationInlineImageResolver mResolver) {
            this.mResolver = mResolver;
        }
        
        protected Drawable doInBackground(final Uri... array) {
            final Uri obj = array[0];
            Drawable resolveImage;
            try {
                resolveImage = this.mResolver.resolveImage(obj);
            }
            catch (IOException | SecurityException ex) {
                final Object o2;
                final Object o = o2;
                final String access$000 = NotificationInlineImageCache.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("PreloadImageTask: Resolve failed from ");
                sb.append(obj);
                Log.d(access$000, sb.toString(), (Throwable)o);
                resolveImage = null;
            }
            return resolveImage;
        }
    }
}
