// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.io;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import kotlin.jvm.internal.Intrinsics;
import java.io.File;

class FilesKt__UtilsKt extends FilesKt__FileTreeWalkKt
{
    public static final File copyTo(File file, final File file2, final boolean b, final int n) {
        Intrinsics.checkParameterIsNotNull(file, "$this$copyTo");
        Intrinsics.checkParameterIsNotNull(file2, "target");
        if (file.exists()) {
            if (file2.exists()) {
                if (!b) {
                    throw new FileAlreadyExistsException(file, file2, "The destination file already exists.");
                }
                if (!file2.delete()) {
                    throw new FileAlreadyExistsException(file, file2, "Tried to overwrite the destination, but failed to delete it.");
                }
            }
            if (file.isDirectory()) {
                if (file2.mkdirs()) {
                    return file2;
                }
                throw new FileSystemException(file, file2, "Failed to create target directory.");
            }
            else {
                final File parentFile = file2.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
            }
            file = (File)new FileInputStream(file);
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    ByteStreamsKt.copyTo((InputStream)file, fileOutputStream, n);
                    CloseableKt.closeFinally(fileOutputStream, null);
                    CloseableKt.closeFinally((Closeable)file, null);
                    return file2;
                }
                finally {
                    try {}
                    finally {
                        final Throwable t;
                        CloseableKt.closeFinally(fileOutputStream, t);
                    }
                }
            }
            finally {
                try {}
                finally {
                    CloseableKt.closeFinally((Closeable)file, (Throwable)file2);
                }
            }
        }
        throw new NoSuchFileException(file, null, "The source file doesn't exist.", 2, null);
    }
}
