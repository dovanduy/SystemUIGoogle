// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import android.service.controls.Control;
import java.util.List;
import java.util.function.Consumer;
import android.service.controls.actions.ControlAction;
import android.content.ComponentName;
import com.android.systemui.controls.UserAwareController;

public interface ControlsBindingController extends UserAwareController
{
    void action(final ComponentName p0, final ControlInfo p1, final ControlAction p2);
    
    Runnable bindAndLoad(final ComponentName p0, final LoadCallback p1);
    
    void bindAndLoadSuggested(final ComponentName p0, final LoadCallback p1);
    
    void onComponentRemoved(final ComponentName p0);
    
    void subscribe(final StructureInfo p0);
    
    void unsubscribe();
    
    public interface LoadCallback extends Consumer<List<? extends Control>>
    {
        void error(final String p0);
    }
}
