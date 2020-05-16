// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.ArrayList;

public class InitController
{
    private final ArrayList<Runnable> mTasks;
    private boolean mTasksExecuted;
    
    public InitController() {
        this.mTasksExecuted = false;
        this.mTasks = new ArrayList<Runnable>();
    }
    
    public void addPostInitTask(final Runnable e) {
        if (!this.mTasksExecuted) {
            this.mTasks.add(e);
            return;
        }
        throw new IllegalStateException("post init tasks have already been executed!");
    }
    
    public void executePostInitTasks() {
        while (!this.mTasks.isEmpty()) {
            this.mTasks.remove(0).run();
        }
        this.mTasksExecuted = true;
    }
}
