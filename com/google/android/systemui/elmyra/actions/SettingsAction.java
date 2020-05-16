// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.android.systemui.R$string;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;

public class SettingsAction extends ServiceAction
{
    private final LaunchOpa mLaunchOpa;
    private final String mSettingsPackageName;
    private final StatusBar mStatusBar;
    
    private SettingsAction(final Context context, final StatusBar mStatusBar, final LaunchOpa mLaunchOpa) {
        super(context, null);
        this.mSettingsPackageName = context.getResources().getString(R$string.settings_app_package_name);
        this.mStatusBar = mStatusBar;
        this.mLaunchOpa = mLaunchOpa;
    }
    
    @Override
    protected boolean checkSupportedCaller() {
        return this.checkSupportedCaller(this.mSettingsPackageName);
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        super.onTrigger(detectionProperties);
    }
    
    @Override
    protected void triggerAction() {
        if (this.mLaunchOpa.isAvailable()) {
            this.mLaunchOpa.launchOpa();
        }
    }
    
    public static class Builder
    {
        private final Context mContext;
        private LaunchOpa mLaunchOpa;
        private final StatusBar mStatusBar;
        
        public Builder(final Context mContext, final StatusBar mStatusBar) {
            this.mContext = mContext;
            this.mStatusBar = mStatusBar;
        }
        
        public SettingsAction build() {
            return new SettingsAction(this.mContext, this.mStatusBar, this.mLaunchOpa, null);
        }
        
        public Builder setLaunchOpa(final LaunchOpa mLaunchOpa) {
            this.mLaunchOpa = mLaunchOpa;
            return this;
        }
    }
}
