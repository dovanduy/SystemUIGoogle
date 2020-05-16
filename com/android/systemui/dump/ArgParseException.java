// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import kotlin.jvm.internal.Intrinsics;

public final class ArgParseException extends Exception
{
    public ArgParseException(final String message) {
        Intrinsics.checkParameterIsNotNull(message, "message");
        super(message);
    }
}
