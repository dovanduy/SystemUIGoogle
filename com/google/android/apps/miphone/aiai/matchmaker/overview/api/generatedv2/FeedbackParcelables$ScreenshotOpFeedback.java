// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public final class FeedbackParcelables$ScreenshotOpFeedback
{
    private long durationMs;
    private FeedbackParcelables$ScreenshotOp op;
    private FeedbackParcelables$ScreenshotOpStatus status;
    
    private FeedbackParcelables$ScreenshotOpFeedback() {
    }
    
    public static FeedbackParcelables$ScreenshotOpFeedback create() {
        return new FeedbackParcelables$ScreenshotOpFeedback();
    }
    
    public void setDurationMs(final long durationMs) {
        this.durationMs = durationMs;
    }
    
    public void setOp(final FeedbackParcelables$ScreenshotOp op) {
        this.op = op;
    }
    
    public void setStatus(final FeedbackParcelables$ScreenshotOpStatus status) {
        this.status = status;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        final FeedbackParcelables$ScreenshotOp op = this.op;
        if (op == null) {
            bundle.putBundle("op", (Bundle)null);
        }
        else {
            bundle.putBundle("op", op.writeToBundle());
        }
        final FeedbackParcelables$ScreenshotOpStatus status = this.status;
        if (status == null) {
            bundle.putBundle("status", (Bundle)null);
        }
        else {
            bundle.putBundle("status", status.writeToBundle());
        }
        bundle.putLong("durationMs", this.durationMs);
        return bundle;
    }
}
