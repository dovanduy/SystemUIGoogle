// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import com.android.systemui.shared.system.QuickStepContract;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.systemui.statusbar.phone.NavigationModeController;
import dagger.Lazy;

public final class NonGesturalNavigation extends Gate
{
    private boolean currentModeIsGestural;
    private final Lazy<NavigationModeController> modeController;
    private final NavigationModeController.ModeChangedListener modeListener;
    
    public NonGesturalNavigation(final Context context, final Lazy<NavigationModeController> modeController) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(modeController, "modeController");
        super(context);
        this.modeController = modeController;
        this.modeListener = (NavigationModeController.ModeChangedListener)new NonGesturalNavigation$modeListener.NonGesturalNavigation$modeListener$1(this);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.isNavigationGestural() ^ true;
    }
    
    public final boolean isNavigationGestural() {
        return this.currentModeIsGestural;
    }
    
    @Override
    protected void onActivate() {
        this.currentModeIsGestural = QuickStepContract.isGesturalMode(this.modeController.get().addListener(this.modeListener));
    }
    
    @Override
    protected void onDeactivate() {
        this.modeController.get().removeListener(this.modeListener);
    }
}
