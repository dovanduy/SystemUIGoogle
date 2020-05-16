// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$ScreenshotActionFeedback
{
    @Nullable
    private String actionType;
    private boolean isSmartActions;
    
    private FeedbackParcelables$ScreenshotActionFeedback() {
    }
    
    public static FeedbackParcelables$ScreenshotActionFeedback create() {
        return new FeedbackParcelables$ScreenshotActionFeedback();
    }
    
    public void setActionType(@Nullable final String actionType) {
        this.actionType = actionType;
    }
    
    public void setIsSmartActions(final boolean isSmartActions) {
        this.isSmartActions = isSmartActions;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("actionType", this.actionType);
        bundle.putBoolean("isSmartActions", this.isSmartActions);
        return bundle;
    }
}
