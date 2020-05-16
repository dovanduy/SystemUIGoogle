// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import java.util.ArrayList;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.android.systemui.R$string;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;

public class CameraAction extends ServiceAction
{
    private final String mCameraPackageName;
    private final StatusBar mStatusBar;
    
    private CameraAction(final Context context, final StatusBar mStatusBar, final List<FeedbackEffect> list) {
        super(context, list);
        this.mCameraPackageName = context.getResources().getString(R$string.google_camera_app_package_name);
        this.mStatusBar = mStatusBar;
    }
    
    @Override
    protected boolean checkSupportedCaller() {
        return this.checkSupportedCaller(this.mCameraPackageName);
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        super.onTrigger(detectionProperties);
    }
    
    public static class Builder
    {
        private final Context mContext;
        List<FeedbackEffect> mFeedbackEffects;
        private final StatusBar mStatusBar;
        
        public Builder(final Context mContext, final StatusBar mStatusBar) {
            this.mFeedbackEffects = new ArrayList<FeedbackEffect>();
            this.mContext = mContext;
            this.mStatusBar = mStatusBar;
        }
        
        public Builder addFeedbackEffect(final FeedbackEffect feedbackEffect) {
            this.mFeedbackEffects.add(feedbackEffect);
            return this;
        }
        
        public CameraAction build() {
            return new CameraAction(this.mContext, this.mStatusBar, this.mFeedbackEffects, null);
        }
    }
}
