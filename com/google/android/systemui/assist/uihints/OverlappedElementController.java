// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.util.Log;
import com.android.systemui.statusbar.phone.KeyguardBottomAreaView;
import android.view.View;
import com.android.systemui.Dependency;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;

final class OverlappedElementController
{
    private float mAlpha;
    private final Lazy<StatusBar> mStatusBarLazy;
    
    OverlappedElementController(final Lazy<StatusBar> mStatusBarLazy) {
        this.mAlpha = 1.0f;
        this.mStatusBarLazy = mStatusBarLazy;
    }
    
    private void tellOverlappedElementsSetAlpha(final float n) {
        final StatusBar statusBar = this.mStatusBarLazy.get();
        Dependency.get(OverviewProxyService.class).notifyAssistantVisibilityChanged(1.0f - n);
        final View ambientIndicationContainer = statusBar.getAmbientIndicationContainer();
        if (ambientIndicationContainer != null) {
            ambientIndicationContainer.setAlpha(n);
        }
        final KeyguardBottomAreaView keyguardBottomAreaView = statusBar.getKeyguardBottomAreaView();
        if (keyguardBottomAreaView != null) {
            keyguardBottomAreaView.setAffordanceAlpha(n);
        }
    }
    
    public void setAlpha(final float mAlpha) {
        final float mAlpha2 = this.mAlpha;
        if (mAlpha2 == mAlpha) {
            return;
        }
        if (mAlpha2 == 1.0f && mAlpha < 1.0f) {
            Log.v("OverlappedElementController", "Overlapped elements becoming transparent.");
        }
        else if (this.mAlpha < 1.0f && mAlpha == 1.0f) {
            Log.v("OverlappedElementController", "Overlapped elements becoming opaque.");
        }
        this.tellOverlappedElementsSetAlpha(this.mAlpha = mAlpha);
    }
}
