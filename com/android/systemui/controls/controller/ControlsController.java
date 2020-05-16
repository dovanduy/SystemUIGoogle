// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import com.android.systemui.controls.ControlStatus;
import kotlin.jvm.internal.Intrinsics;
import android.os.UserHandle;
import android.service.controls.Control;
import java.util.List;
import java.util.function.Consumer;
import android.service.controls.actions.ControlAction;
import android.content.ComponentName;
import com.android.systemui.controls.UserAwareController;

public interface ControlsController extends UserAwareController
{
    void action(final ComponentName p0, final ControlInfo p1, final ControlAction p2);
    
    void addFavorite(final ComponentName p0, final CharSequence p1, final ControlInfo p2);
    
    boolean addSeedingFavoritesCallback(final Consumer<Boolean> p0);
    
    int countFavoritesForComponent(final ComponentName p0);
    
    boolean getAvailable();
    
    List<StructureInfo> getFavorites();
    
    List<StructureInfo> getFavoritesForComponent(final ComponentName p0);
    
    void onActionResponse(final ComponentName p0, final String p1, final int p2);
    
    void refreshStatus(final ComponentName p0, final Control p1);
    
    void seedFavoritesForComponent(final ComponentName p0, final Consumer<Boolean> p1);
    
    void subscribeToFavorites(final StructureInfo p0);
    
    void unsubscribe();
    
    public static final class DefaultImpls
    {
        public static void changeUser(final ControlsController controlsController, final UserHandle userHandle) {
            Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
            UserAwareController.DefaultImpls.changeUser(controlsController, userHandle);
        }
    }
    
    public interface LoadData
    {
        List<ControlStatus> getAllControls();
        
        boolean getErrorOnLoad();
        
        List<String> getFavoritesIds();
    }
}
