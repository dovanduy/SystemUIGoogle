// 
// Decompiled by Procyon v0.5.36
// 

package com.android.framework.protobuf.nano;

import java.nio.charset.Charset;

public final class InternalNano
{
    static {
        Charset.forName("UTF-8");
        Charset.forName("ISO-8859-1");
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
