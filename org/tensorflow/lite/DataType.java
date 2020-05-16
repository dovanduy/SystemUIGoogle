// 
// Decompiled by Procyon v0.5.36
// 

package org.tensorflow.lite;

public enum DataType
{
    FLOAT32(1), 
    INT32(2), 
    INT64(4), 
    STRING(5), 
    UINT8(3);
    
    private static final DataType[] values;
    private final int value;
    
    static {
        values = values();
    }
    
    private DataType(final int value) {
        this.value = value;
    }
    
    static DataType fromC(final int i) {
        for (final DataType dataType : DataType.values) {
            if (dataType.value == i) {
                return dataType;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("DataType error: DataType ");
        sb.append(i);
        sb.append(" is not recognized in Java (version ");
        sb.append(TensorFlowLite.runtimeVersion());
        sb.append(")");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public int byteSize() {
        final int n = DataType$1.$SwitchMap$org$tensorflow$lite$DataType[this.ordinal()];
        if (n == 1 || n == 2) {
            return 4;
        }
        if (n == 3) {
            return 1;
        }
        if (n == 4) {
            return 8;
        }
        if (n == 5) {
            return -1;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("DataType error: DataType ");
        sb.append(this);
        sb.append(" is not supported yet");
        throw new IllegalArgumentException(sb.toString());
    }
}
