// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.internal.jdk7;

import kotlin.jvm.internal.Intrinsics;
import kotlin.internal.PlatformImplementations;

public class JDK7PlatformImplementations extends PlatformImplementations
{
    @Override
    public void addSuppressed(final Throwable t, final Throwable exception) {
        Intrinsics.checkParameterIsNotNull(t, "cause");
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        t.addSuppressed(exception);
    }
}
