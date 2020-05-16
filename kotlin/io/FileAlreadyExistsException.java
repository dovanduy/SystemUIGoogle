// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.io;

import kotlin.jvm.internal.Intrinsics;
import java.io.File;

public final class FileAlreadyExistsException extends FileSystemException
{
    public FileAlreadyExistsException(final File file, final File file2, final String s) {
        Intrinsics.checkParameterIsNotNull(file, "file");
        super(file, file2, s);
    }
}
