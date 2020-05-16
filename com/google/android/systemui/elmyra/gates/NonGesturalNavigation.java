// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import com.android.systemui.Dependency;
import com.android.systemui.shared.system.QuickStepContract;
import android.content.Context;
import com.android.systemui.statusbar.phone.NavigationModeController;

public class NonGesturalNavigation extends Gate
{
    private boolean mCurrentModeIsGestural;
    private final NavigationModeController mModeController;
    private final NavigationModeController.ModeChangedListener mModeListener;
    
    public NonGesturalNavigation(final Context context) {
        super(context);
        this.mModeListener = new NavigationModeController.ModeChangedListener() {
            @Override
            public void onNavigationModeChanged(final int n) {
                NonGesturalNavigation.this.mCurrentModeIsGestural = QuickStepContract.isGesturalMode(n);
                NonGesturalNavigation.this.notifyListener();
            }
        };
        this.mModeController = Dependency.get(NavigationModeController.class);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.isNavigationGestural() ^ true;
    }
    
    public boolean isNavigationGestural() {
        return this.mCurrentModeIsGestural;
    }
    
    @Override
    protected void onActivate() {
        this.mCurrentModeIsGestural = QuickStepContract.isGesturalMode(this.mModeController.addListener(this.mModeListener));
    }
    
    @Override
    protected void onDeactivate() {
        this.mModeController.removeListener(this.mModeListener);
    }
}
