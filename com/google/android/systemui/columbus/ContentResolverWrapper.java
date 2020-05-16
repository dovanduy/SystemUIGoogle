// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.database.ContentObserver;
import android.net.Uri;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.content.ContentResolver;

public class ContentResolverWrapper
{
    private final ContentResolver contentResolver;
    
    public ContentResolverWrapper(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.contentResolver = context.getContentResolver();
    }
    
    public void registerContentObserver(final Uri uri, final boolean b, final ContentObserver contentObserver, final int n) {
        Intrinsics.checkParameterIsNotNull(uri, "uri");
        Intrinsics.checkParameterIsNotNull(contentObserver, "observer");
        this.contentResolver.registerContentObserver(uri, b, contentObserver, n);
    }
    
    public void unregisterContentObserver(final ContentObserver contentObserver) {
        Intrinsics.checkParameterIsNotNull(contentObserver, "observer");
        this.contentResolver.unregisterContentObserver(contentObserver);
    }
}
