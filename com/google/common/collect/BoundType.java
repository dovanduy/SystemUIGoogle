// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

public enum BoundType
{
    CLOSED(true), 
    OPEN(false);
    
    final boolean inclusive;
    
    private BoundType(final boolean inclusive) {
        this.inclusive = inclusive;
    }
    
    static BoundType forBoolean(final boolean b) {
        BoundType boundType;
        if (b) {
            boundType = BoundType.CLOSED;
        }
        else {
            boundType = BoundType.OPEN;
        }
        return boundType;
    }
}
