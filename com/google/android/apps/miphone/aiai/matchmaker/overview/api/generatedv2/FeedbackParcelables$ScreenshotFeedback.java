// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$ScreenshotFeedback
{
    @Nullable
    private Object screenshotFeedback;
    @Nullable
    private String screenshotId;
    
    private FeedbackParcelables$ScreenshotFeedback() {
    }
    
    public static FeedbackParcelables$ScreenshotFeedback create() {
        return new FeedbackParcelables$ScreenshotFeedback();
    }
    
    public void setScreenshotFeedback(@Nullable final Object screenshotFeedback) {
        this.screenshotFeedback = screenshotFeedback;
    }
    
    public void setScreenshotId(@Nullable final String screenshotId) {
        this.screenshotId = screenshotId;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        if (this.screenshotFeedback instanceof FeedbackParcelables$ScreenshotOpFeedback) {
            bundle.putInt("screenshotFeedback#tag", 2);
            final FeedbackParcelables$ScreenshotOpFeedback feedbackParcelables$ScreenshotOpFeedback = (FeedbackParcelables$ScreenshotOpFeedback)this.screenshotFeedback;
            if (feedbackParcelables$ScreenshotOpFeedback == null) {
                bundle.putBundle("screenshotFeedback", (Bundle)null);
            }
            else {
                bundle.putBundle("screenshotFeedback", feedbackParcelables$ScreenshotOpFeedback.writeToBundle());
            }
        }
        if (this.screenshotFeedback instanceof FeedbackParcelables$ScreenshotActionFeedback) {
            bundle.putInt("screenshotFeedback#tag", 3);
            final FeedbackParcelables$ScreenshotActionFeedback feedbackParcelables$ScreenshotActionFeedback = (FeedbackParcelables$ScreenshotActionFeedback)this.screenshotFeedback;
            if (feedbackParcelables$ScreenshotActionFeedback == null) {
                bundle.putBundle("screenshotFeedback", (Bundle)null);
            }
            else {
                bundle.putBundle("screenshotFeedback", feedbackParcelables$ScreenshotActionFeedback.writeToBundle());
            }
        }
        bundle.putString("screenshotId", this.screenshotId);
        return bundle;
    }
}
