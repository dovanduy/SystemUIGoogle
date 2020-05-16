// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

public class ExecutionError extends Error
{
    private static final long serialVersionUID = 0L;
    
    protected ExecutionError() {
    }
    
    public ExecutionError(final Error cause) {
        super(cause);
    }
    
    protected ExecutionError(final String message) {
        super(message);
    }
}
