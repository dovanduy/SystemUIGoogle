// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano;

import java.nio.charset.Charset;

public final class InternalNano
{
    public static final Object LAZY_INIT_LOCK;
    static final Charset UTF_8;
    
    static {
        UTF_8 = Charset.forName("UTF-8");
        Charset.forName("ISO-8859-1");
        LAZY_INIT_LOCK = new Object();
    }
    
    public static void cloneUnknownFieldData(final ExtendableMessageNano extendableMessageNano, final ExtendableMessageNano extendableMessageNano2) {
        final FieldArray unknownFieldData = extendableMessageNano.unknownFieldData;
        if (unknownFieldData == null) {
            return;
        }
        unknownFieldData.clone();
        throw null;
    }
}
