// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import android.util.Log;
import java.io.OutputStream;
import android.graphics.Bitmap$CompressFormat;
import android.os.ParcelFileDescriptor$AutoCloseOutputStream;
import android.database.MatrixCursor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.content.ContentProvider$PipeDataWriter;
import android.os.Bundle;
import android.text.TextUtils;
import java.io.FileNotFoundException;
import android.os.ParcelFileDescriptor;
import android.content.ContentValues;
import com.android.systemui.Dependency;
import android.net.Uri$Builder;
import android.net.Uri;
import com.android.internal.annotations.VisibleForTesting;
import java.util.List;
import java.util.function.Supplier;
import android.content.ContentProvider;

public final class ClockOptionsProvider extends ContentProvider
{
    private final Supplier<List<ClockInfo>> mClocksSupplier;
    
    public ClockOptionsProvider() {
        this((Supplier<List<ClockInfo>>)_$$Lambda$ClockOptionsProvider$VCF_r6VBqrtOSuPKYuOzo6kUuyg.INSTANCE);
    }
    
    @VisibleForTesting
    ClockOptionsProvider(final Supplier<List<ClockInfo>> mClocksSupplier) {
        this.mClocksSupplier = mClocksSupplier;
    }
    
    private Uri createPreviewUri(final ClockInfo clockInfo) {
        return new Uri$Builder().scheme("content").authority("com.android.keyguard.clock").appendPath("preview").appendPath(clockInfo.getId()).build();
    }
    
    private Uri createThumbnailUri(final ClockInfo clockInfo) {
        return new Uri$Builder().scheme("content").authority("com.android.keyguard.clock").appendPath("thumbnail").appendPath(clockInfo.getId()).build();
    }
    
    public int delete(final Uri uri, final String s, final String[] array) {
        return 0;
    }
    
    public String getType(final Uri uri) {
        final List pathSegments = uri.getPathSegments();
        if (pathSegments.size() > 0 && ("preview".equals(pathSegments.get(0)) || "thumbnail".equals(pathSegments.get(0)))) {
            return "image/png";
        }
        return "vnd.android.cursor.dir/clock_faces";
    }
    
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        return null;
    }
    
    public boolean onCreate() {
        return true;
    }
    
    public ParcelFileDescriptor openFile(final Uri uri, String s) throws FileNotFoundException {
        final List pathSegments = uri.getPathSegments();
        if (pathSegments.size() != 2 || (!"preview".equals(pathSegments.get(0)) && !"thumbnail".equals(pathSegments.get(0)))) {
            throw new FileNotFoundException("Invalid preview url");
        }
        s = pathSegments.get(1);
        if (!TextUtils.isEmpty((CharSequence)s)) {
            final List<ClockInfo> list = this.mClocksSupplier.get();
            int i = 0;
            while (true) {
                while (i < list.size()) {
                    if (s.equals(list.get(i).getId())) {
                        final ClockInfo clockInfo = list.get(i);
                        if (clockInfo != null) {
                            Bitmap bitmap;
                            if ("preview".equals(pathSegments.get(0))) {
                                bitmap = clockInfo.getPreview();
                            }
                            else {
                                bitmap = clockInfo.getThumbnail();
                            }
                            return this.openPipeHelper(uri, "image/png", (Bundle)null, (Object)bitmap, (ContentProvider$PipeDataWriter)new MyWriter());
                        }
                        throw new FileNotFoundException("Invalid preview url, id not found");
                    }
                    else {
                        ++i;
                    }
                }
                final ClockInfo clockInfo = null;
                continue;
            }
        }
        throw new FileNotFoundException("Invalid preview url, missing id");
    }
    
    public Cursor query(final Uri uri, final String[] array, final String s, final String[] array2, final String s2) {
        if (!"/list_options".equals(uri.getPath())) {
            return null;
        }
        final MatrixCursor matrixCursor = new MatrixCursor(new String[] { "name", "title", "id", "thumbnail", "preview" });
        final List<ClockInfo> list = this.mClocksSupplier.get();
        for (int i = 0; i < list.size(); ++i) {
            final ClockInfo clockInfo = list.get(i);
            matrixCursor.newRow().add("name", (Object)clockInfo.getName()).add("title", (Object)clockInfo.getTitle()).add("id", (Object)clockInfo.getId()).add("thumbnail", (Object)this.createThumbnailUri(clockInfo)).add("preview", (Object)this.createPreviewUri(clockInfo));
        }
        return (Cursor)matrixCursor;
    }
    
    public int update(final Uri uri, final ContentValues contentValues, final String s, final String[] array) {
        return 0;
    }
    
    private static class MyWriter implements ContentProvider$PipeDataWriter<Bitmap>
    {
        public void writeDataToPipe(final ParcelFileDescriptor parcelFileDescriptor, Uri uri, final String s, final Bundle bundle, final Bitmap bitmap) {
            try {
                uri = (Uri)new ParcelFileDescriptor$AutoCloseOutputStream(parcelFileDescriptor);
                try {
                    bitmap.compress(Bitmap$CompressFormat.PNG, 100, (OutputStream)uri);
                    ((ParcelFileDescriptor$AutoCloseOutputStream)uri).close();
                }
                finally {
                    try {
                        ((ParcelFileDescriptor$AutoCloseOutputStream)uri).close();
                    }
                    finally {
                        final Throwable exception;
                        ((Throwable)parcelFileDescriptor).addSuppressed(exception);
                    }
                }
            }
            catch (Exception ex) {
                Log.w("ClockOptionsProvider", "fail to write to pipe", (Throwable)ex);
            }
        }
    }
}
