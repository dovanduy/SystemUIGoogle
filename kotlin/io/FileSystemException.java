// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.io;

import kotlin.jvm.internal.Intrinsics;
import java.io.File;
import java.io.IOException;

public class FileSystemException extends IOException
{
    private final File file;
    private final File other;
    private final String reason;
    
    public FileSystemException(final File file, final File other, final String reason) {
        Intrinsics.checkParameterIsNotNull(file, "file");
        super(ExceptionsKt.access$constructMessage(file, other, reason));
        this.file = file;
        this.other = other;
        this.reason = reason;
    }
}
