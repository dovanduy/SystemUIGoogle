// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.view.View;
import android.content.Context;
import android.service.dreams.DreamService;

public class DessertCaseDream extends DreamService
{
    private DessertCaseView.RescalingContainer mContainer;
    private DessertCaseView mView;
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setInteractive(false);
        this.mView = new DessertCaseView((Context)this);
        (this.mContainer = new DessertCaseView.RescalingContainer((Context)this)).setView(this.mView);
        this.setContentView((View)this.mContainer);
    }
    
    public void onDreamingStarted() {
        super.onDreamingStarted();
        this.mView.postDelayed((Runnable)new Runnable() {
            @Override
            public void run() {
                DessertCaseDream.this.mView.start();
            }
        }, 1000L);
    }
    
    public void onDreamingStopped() {
        super.onDreamingStopped();
        this.mView.stop();
    }
}
