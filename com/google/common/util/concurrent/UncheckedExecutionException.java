// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

public class UncheckedExecutionException extends RuntimeException
{
    private static final long serialVersionUID = 0L;
    
    protected UncheckedExecutionException() {
    }
    
    protected UncheckedExecutionException(final String message) {
        super(message);
    }
    
    public UncheckedExecutionException(final Throwable cause) {
        super(cause);
    }
}
