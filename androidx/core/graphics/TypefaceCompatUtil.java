// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import android.os.ParcelFileDescriptor;
import java.nio.MappedByteBuffer;
import android.content.ContentResolver;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Process;
import android.os.StrictMode$ThreadPolicy;
import android.util.Log;
import java.io.FileOutputStream;
import android.os.StrictMode;
import java.io.InputStream;
import java.io.File;
import java.nio.ByteBuffer;
import android.content.res.Resources;
import android.content.Context;
import java.io.IOException;
import java.io.Closeable;

public class TypefaceCompatUtil
{
    public static void closeQuietly(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        }
        catch (IOException ex) {}
    }
    
    public static ByteBuffer copyToDirectBuffer(Context tempFile, final Resources resources, final int n) {
        tempFile = (Context)getTempFile(tempFile);
        if (tempFile == null) {
            return null;
        }
        try {
            if (!copyToFile((File)tempFile, resources, n)) {
                return null;
            }
            return mmap((File)tempFile);
        }
        finally {
            ((File)tempFile).delete();
        }
    }
    
    public static boolean copyToFile(final File file, final Resources resources, final int n) {
        Closeable closeable;
        try {
            final InputStream openRawResource = resources.openRawResource(n);
            try {
                final boolean copyToFile = copyToFile(file, openRawResource);
                closeQuietly(openRawResource);
                return copyToFile;
            }
            finally {}
        }
        finally {
            closeable = null;
        }
        closeQuietly(closeable);
    }
    
    public static boolean copyToFile(final File file, final InputStream ex) {
        final StrictMode$ThreadPolicy allowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        final Closeable closeable = null;
        Closeable closeable2 = null;
        Closeable closeable3;
        try {
            try {
                closeable2 = closeable2;
                final FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                try {
                    final byte[] array = new byte[1024];
                    while (true) {
                        final int read = ((InputStream)ex).read(array);
                        if (read == -1) {
                            break;
                        }
                        fileOutputStream.write(array, 0, read);
                    }
                    closeQuietly(fileOutputStream);
                    StrictMode.setThreadPolicy(allowThreadDiskWrites);
                    return true;
                }
                catch (IOException ex) {}
                finally {
                    closeable2 = fileOutputStream;
                }
            }
            finally {}
        }
        catch (IOException ex) {
            closeable3 = closeable;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Error copying resource contents to temp file: ");
        sb.append(ex.getMessage());
        Log.e("TypefaceCompatUtil", sb.toString());
        closeQuietly(closeable3);
        StrictMode.setThreadPolicy(allowThreadDiskWrites);
        return false;
        closeQuietly(closeable2);
        StrictMode.setThreadPolicy(allowThreadDiskWrites);
    }
    
    public static File getTempFile(Context cacheDir) {
        cacheDir = (Context)cacheDir.getCacheDir();
        if (cacheDir == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(".font");
        sb.append(Process.myPid());
        sb.append("-");
        sb.append(Process.myTid());
        sb.append("-");
        final String string = sb.toString();
        int i = 0;
    Label_0115_Outer:
        while (true) {
            Label_0121: {
                if (i >= 100) {
                    break Label_0121;
                }
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(string);
                sb2.append(i);
                final File file = new File((File)cacheDir, sb2.toString());
                while (true) {
                    try {
                        if (file.createNewFile()) {
                            return file;
                        }
                        ++i;
                        continue Label_0115_Outer;
                        return null;
                    }
                    catch (IOException ex) {
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    public static ByteBuffer mmap(Context openFileDescriptor, CancellationSignal cancellationSignal, final Uri uri) {
        final ContentResolver contentResolver = openFileDescriptor.getContentResolver();
        try {
            openFileDescriptor = (Context)contentResolver.openFileDescriptor(uri, "r", cancellationSignal);
            if (openFileDescriptor == null) {
                if (openFileDescriptor != null) {
                    ((ParcelFileDescriptor)openFileDescriptor).close();
                }
                return null;
            }
            try {
                cancellationSignal = (CancellationSignal)new FileInputStream(((ParcelFileDescriptor)openFileDescriptor).getFileDescriptor());
                try {
                    final FileChannel channel = ((FileInputStream)cancellationSignal).getChannel();
                    final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
                    ((FileInputStream)cancellationSignal).close();
                    if (openFileDescriptor != null) {
                        ((ParcelFileDescriptor)openFileDescriptor).close();
                    }
                    return map;
                }
                finally {
                    try {
                        ((FileInputStream)cancellationSignal).close();
                    }
                    finally {
                        final Throwable exception;
                        ((Throwable)uri).addSuppressed(exception);
                    }
                }
            }
            finally {
                if (openFileDescriptor != null) {
                    try {
                        ((ParcelFileDescriptor)openFileDescriptor).close();
                    }
                    finally {
                        final Throwable exception2;
                        ((Throwable)cancellationSignal).addSuppressed(exception2);
                    }
                }
            }
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    private static ByteBuffer mmap(final File file) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            try {
                final FileChannel channel = fileInputStream.getChannel();
                final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
                fileInputStream.close();
                return map;
            }
            finally {
                try {
                    fileInputStream.close();
                }
                finally {
                    final Throwable exception;
                    ((Throwable)file).addSuppressed(exception);
                }
            }
        }
        catch (IOException ex) {
            return null;
        }
    }
}
