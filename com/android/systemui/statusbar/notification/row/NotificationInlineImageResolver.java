// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.internal.widget.LocalImageResolver;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import java.util.function.Consumer;
import java.io.IOException;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import java.util.Iterator;
import java.util.List;
import android.os.Parcelable;
import android.os.Bundle;
import com.android.internal.widget.MessagingMessage;
import android.app.Notification$MessagingStyle$Message;
import java.util.HashSet;
import android.app.Notification;
import android.app.ActivityManager;
import android.net.Uri;
import java.util.Set;
import com.android.internal.annotations.VisibleForTesting;
import android.content.Context;
import com.android.internal.widget.ImageResolver;

public class NotificationInlineImageResolver implements ImageResolver
{
    private static final String TAG;
    private final Context mContext;
    private final ImageCache mImageCache;
    @VisibleForTesting
    protected int mMaxImageHeight;
    @VisibleForTesting
    protected int mMaxImageWidth;
    private Set<Uri> mWantedUriSet;
    
    static {
        TAG = NotificationInlineImageResolver.class.getSimpleName();
    }
    
    public NotificationInlineImageResolver(final Context context, final ImageCache mImageCache) {
        this.mContext = context.getApplicationContext();
        this.mImageCache = mImageCache;
        if (mImageCache != null) {
            mImageCache.setImageResolver(this);
        }
        this.updateMaxImageSizes();
    }
    
    private boolean isLowRam() {
        return ActivityManager.isLowRamDeviceStatic();
    }
    
    private void retrieveWantedUriSet(final Notification notification) {
        final HashSet<Uri> mWantedUriSet = new HashSet<Uri>();
        final Bundle extras = notification.extras;
        if (extras == null) {
            return;
        }
        final Parcelable[] parcelableArray = extras.getParcelableArray("android.messages");
        final List<Notification$MessagingStyle$Message> list = null;
        List<Notification$MessagingStyle$Message> messagesFromBundleArray;
        if (parcelableArray == null) {
            messagesFromBundleArray = null;
        }
        else {
            messagesFromBundleArray = (List<Notification$MessagingStyle$Message>)Notification$MessagingStyle$Message.getMessagesFromBundleArray(parcelableArray);
        }
        if (messagesFromBundleArray != null) {
            for (final Notification$MessagingStyle$Message notification$MessagingStyle$Message : messagesFromBundleArray) {
                if (MessagingMessage.hasImage(notification$MessagingStyle$Message)) {
                    mWantedUriSet.add(notification$MessagingStyle$Message.getDataUri());
                }
            }
        }
        final Parcelable[] parcelableArray2 = extras.getParcelableArray("android.messages.historic");
        List<Notification$MessagingStyle$Message> messagesFromBundleArray2;
        if (parcelableArray2 == null) {
            messagesFromBundleArray2 = list;
        }
        else {
            messagesFromBundleArray2 = (List<Notification$MessagingStyle$Message>)Notification$MessagingStyle$Message.getMessagesFromBundleArray(parcelableArray2);
        }
        if (messagesFromBundleArray2 != null) {
            for (final Notification$MessagingStyle$Message notification$MessagingStyle$Message2 : messagesFromBundleArray2) {
                if (MessagingMessage.hasImage(notification$MessagingStyle$Message2)) {
                    mWantedUriSet.add(notification$MessagingStyle$Message2.getDataUri());
                }
            }
        }
        this.mWantedUriSet = mWantedUriSet;
    }
    
    @VisibleForTesting
    protected int getMaxImageHeight() {
        final Resources resources = this.mContext.getResources();
        int n;
        if (this.isLowRam()) {
            n = 17105347;
        }
        else {
            n = 17105346;
        }
        return resources.getDimensionPixelSize(n);
    }
    
    @VisibleForTesting
    protected int getMaxImageWidth() {
        final Resources resources = this.mContext.getResources();
        int n;
        if (this.isLowRam()) {
            n = 17105349;
        }
        else {
            n = 17105348;
        }
        return resources.getDimensionPixelSize(n);
    }
    
    Set<Uri> getWantedUriSet() {
        return this.mWantedUriSet;
    }
    
    public boolean hasCache() {
        return this.mImageCache != null && !ActivityManager.isLowRamDeviceStatic();
    }
    
    public Drawable loadImage(Uri obj) {
        try {
            if (this.hasCache()) {
                if (!this.mImageCache.hasEntry(obj)) {
                    this.mImageCache.preload(obj);
                }
                obj = (Uri)this.mImageCache.get(obj);
            }
            else {
                obj = (Uri)this.resolveImage(obj);
            }
        }
        catch (IOException | SecurityException ex) {
            final Object o2;
            final Object o = o2;
            final String tag = NotificationInlineImageResolver.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("loadImage: Can't load image from ");
            sb.append(obj);
            Log.d(tag, sb.toString(), (Throwable)o);
            obj = null;
        }
        return (Drawable)obj;
    }
    
    public void preloadImages(final Notification notification) {
        if (!this.hasCache()) {
            return;
        }
        this.retrieveWantedUriSet(notification);
        this.getWantedUriSet().forEach(new _$$Lambda$NotificationInlineImageResolver$9tt2CqLsWBYt2coRCrkS9VmF2EU(this));
    }
    
    public void purgeCache() {
        if (!this.hasCache()) {
            return;
        }
        this.mImageCache.purge();
    }
    
    Drawable resolveImage(final Uri uri) throws IOException {
        final BitmapDrawable resolveImageInternal = this.resolveImageInternal(uri);
        resolveImageInternal.setBitmap(Icon.scaleDownIfNecessary(resolveImageInternal.getBitmap(), this.mMaxImageWidth, this.mMaxImageHeight));
        return (Drawable)resolveImageInternal;
    }
    
    @VisibleForTesting
    protected BitmapDrawable resolveImageInternal(final Uri uri) throws IOException {
        return (BitmapDrawable)LocalImageResolver.resolveImage(uri, this.mContext);
    }
    
    public void updateMaxImageSizes() {
        this.mMaxImageWidth = this.getMaxImageWidth();
        this.mMaxImageHeight = this.getMaxImageHeight();
    }
    
    interface ImageCache
    {
        Drawable get(final Uri p0);
        
        boolean hasEntry(final Uri p0);
        
        void preload(final Uri p0);
        
        void purge();
        
        void setImageResolver(final NotificationInlineImageResolver p0);
    }
}
