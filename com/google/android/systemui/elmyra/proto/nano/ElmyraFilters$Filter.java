// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;

public final class ElmyraFilters$Filter extends MessageNano
{
    private static volatile ElmyraFilters$Filter[] _emptyArray;
    private int parametersCase_;
    private Object parameters_;
    
    public ElmyraFilters$Filter() {
        this.parametersCase_ = 0;
        this.clear();
    }
    
    public static ElmyraFilters$Filter[] emptyArray() {
        if (ElmyraFilters$Filter._emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (ElmyraFilters$Filter._emptyArray == null) {
                    ElmyraFilters$Filter._emptyArray = new ElmyraFilters$Filter[0];
                }
            }
        }
        return ElmyraFilters$Filter._emptyArray;
    }
    
    public ElmyraFilters$Filter clear() {
        this.clearParameters();
        super.cachedSize = -1;
        return this;
    }
    
    public ElmyraFilters$Filter clearParameters() {
        this.parametersCase_ = 0;
        this.parameters_ = null;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize;
        final int n = computeSerializedSize = super.computeSerializedSize();
        if (this.parametersCase_ == 1) {
            computeSerializedSize = n + CodedOutputByteBufferNano.computeMessageSize(1, (MessageNano)this.parameters_);
        }
        int n2 = computeSerializedSize;
        if (this.parametersCase_ == 2) {
            n2 = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, (MessageNano)this.parameters_);
        }
        int n3 = n2;
        if (this.parametersCase_ == 3) {
            n3 = n2 + CodedOutputByteBufferNano.computeMessageSize(3, (MessageNano)this.parameters_);
        }
        int n4 = n3;
        if (this.parametersCase_ == 4) {
            n4 = n3 + CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano)this.parameters_);
        }
        return n4;
    }
    
    @Override
    public ElmyraFilters$Filter mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (tag != 18) {
                    if (tag != 26) {
                        if (tag != 34) {
                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        else {
                            if (this.parametersCase_ != 4) {
                                this.parameters_ = new ElmyraFilters$MedianFilter();
                            }
                            codedInputByteBufferNano.readMessage((MessageNano)this.parameters_);
                            this.parametersCase_ = 4;
                        }
                    }
                    else {
                        if (this.parametersCase_ != 3) {
                            this.parameters_ = new ElmyraFilters$LowpassFilter();
                        }
                        codedInputByteBufferNano.readMessage((MessageNano)this.parameters_);
                        this.parametersCase_ = 3;
                    }
                }
                else {
                    if (this.parametersCase_ != 2) {
                        this.parameters_ = new ElmyraFilters$HighpassFilter();
                    }
                    codedInputByteBufferNano.readMessage((MessageNano)this.parameters_);
                    this.parametersCase_ = 2;
                }
            }
            else {
                if (this.parametersCase_ != 1) {
                    this.parameters_ = new ElmyraFilters$FIRFilter();
                }
                codedInputByteBufferNano.readMessage((MessageNano)this.parameters_);
                this.parametersCase_ = 1;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.parametersCase_ == 1) {
            codedOutputByteBufferNano.writeMessage(1, (MessageNano)this.parameters_);
        }
        if (this.parametersCase_ == 2) {
            codedOutputByteBufferNano.writeMessage(2, (MessageNano)this.parameters_);
        }
        if (this.parametersCase_ == 3) {
            codedOutputByteBufferNano.writeMessage(3, (MessageNano)this.parameters_);
        }
        if (this.parametersCase_ == 4) {
            codedOutputByteBufferNano.writeMessage(4, (MessageNano)this.parameters_);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
