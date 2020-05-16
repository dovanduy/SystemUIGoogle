// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.pm.PackageManager;
import android.view.View;
import android.util.Slog;
import android.content.Context;
import android.content.ComponentName;
import android.app.Activity;

public class DessertCase extends Activity
{
    DessertCaseView mView;
    
    public void onPause() {
        super.onPause();
        this.mView.stop();
    }
    
    public void onResume() {
        super.onResume();
        this.mView.postDelayed((Runnable)new Runnable() {
            @Override
            public void run() {
                DessertCase.this.mView.start();
            }
        }, 1000L);
    }
    
    public void onStart() {
        super.onStart();
        final PackageManager packageManager = this.getPackageManager();
        final ComponentName componentName = new ComponentName((Context)this, (Class)DessertCaseDream.class);
        if (packageManager.getComponentEnabledSetting(componentName) != 1) {
            Slog.v("DessertCase", "ACHIEVEMENT UNLOCKED");
            packageManager.setComponentEnabledSetting(componentName, 1, 1);
        }
        this.mView = new DessertCaseView((Context)this);
        final DessertCaseView.RescalingContainer contentView = new DessertCaseView.RescalingContainer((Context)this);
        contentView.setView(this.mView);
        this.setContentView((View)contentView);
    }
}
