// 
// Decompiled by Procyon v0.5.36
// 

package com.android.framework.protobuf.nano;

import java.io.IOException;

class FieldData implements Cloneable
{
    abstract int computeSerializedSize();
    
    abstract void writeTo(final CodedOutputByteBufferNano p0) throws IOException;
}
