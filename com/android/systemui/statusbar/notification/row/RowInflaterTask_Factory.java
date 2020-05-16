// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import dagger.internal.Factory;

public final class RowInflaterTask_Factory implements Factory<RowInflaterTask>
{
    private static final RowInflaterTask_Factory INSTANCE;
    
    static {
        INSTANCE = new RowInflaterTask_Factory();
    }
    
    public static RowInflaterTask_Factory create() {
        return RowInflaterTask_Factory.INSTANCE;
    }
    
    public static RowInflaterTask provideInstance() {
        return new RowInflaterTask();
    }
    
    @Override
    public RowInflaterTask get() {
        return provideInstance();
    }
}
