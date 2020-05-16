// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class TaskStackNotifier_Factory implements Factory<TaskStackNotifier>
{
    private static final TaskStackNotifier_Factory INSTANCE;
    
    static {
        INSTANCE = new TaskStackNotifier_Factory();
    }
    
    public static TaskStackNotifier_Factory create() {
        return TaskStackNotifier_Factory.INSTANCE;
    }
    
    public static TaskStackNotifier provideInstance() {
        return new TaskStackNotifier();
    }
    
    @Override
    public TaskStackNotifier get() {
        return provideInstance();
    }
}
