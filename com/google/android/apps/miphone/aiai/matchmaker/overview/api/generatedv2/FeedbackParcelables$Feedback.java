// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$Feedback
{
    @Nullable
    private Object feedback;
    @Nullable
    private String id;
    @Nullable
    private InteractionContextParcelables$InteractionContext interactionContext;
    private FeedbackParcelables$SuggestionAction suggestionAction;
    private long timestampMs;
    
    private FeedbackParcelables$Feedback() {
    }
    
    public static FeedbackParcelables$Feedback create() {
        return new FeedbackParcelables$Feedback();
    }
    
    public void setFeedback(@Nullable final Object feedback) {
        this.feedback = feedback;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        if (this.feedback instanceof FeedbackParcelables$OverviewFeedback) {
            bundle.putInt("feedback#tag", 6);
            final FeedbackParcelables$OverviewFeedback feedbackParcelables$OverviewFeedback = (FeedbackParcelables$OverviewFeedback)this.feedback;
            if (feedbackParcelables$OverviewFeedback == null) {
                bundle.putBundle("feedback", (Bundle)null);
            }
            else {
                bundle.putBundle("feedback", feedbackParcelables$OverviewFeedback.writeToBundle());
            }
        }
        if (this.feedback instanceof FeedbackParcelables$SelectionFeedback) {
            bundle.putInt("feedback#tag", 7);
            final FeedbackParcelables$SelectionFeedback feedbackParcelables$SelectionFeedback = (FeedbackParcelables$SelectionFeedback)this.feedback;
            if (feedbackParcelables$SelectionFeedback == null) {
                bundle.putBundle("feedback", (Bundle)null);
            }
            else {
                bundle.putBundle("feedback", feedbackParcelables$SelectionFeedback.writeToBundle());
            }
        }
        if (this.feedback instanceof FeedbackParcelables$ActionFeedback) {
            bundle.putInt("feedback#tag", 8);
            final FeedbackParcelables$ActionFeedback feedbackParcelables$ActionFeedback = (FeedbackParcelables$ActionFeedback)this.feedback;
            if (feedbackParcelables$ActionFeedback == null) {
                bundle.putBundle("feedback", (Bundle)null);
            }
            else {
                bundle.putBundle("feedback", feedbackParcelables$ActionFeedback.writeToBundle());
            }
        }
        if (this.feedback instanceof FeedbackParcelables$ActionGroupFeedback) {
            bundle.putInt("feedback#tag", 9);
            final FeedbackParcelables$ActionGroupFeedback feedbackParcelables$ActionGroupFeedback = (FeedbackParcelables$ActionGroupFeedback)this.feedback;
            if (feedbackParcelables$ActionGroupFeedback == null) {
                bundle.putBundle("feedback", (Bundle)null);
            }
            else {
                bundle.putBundle("feedback", feedbackParcelables$ActionGroupFeedback.writeToBundle());
            }
        }
        if (this.feedback instanceof FeedbackParcelables$TaskSnapshotFeedback) {
            bundle.putInt("feedback#tag", 10);
            final FeedbackParcelables$TaskSnapshotFeedback feedbackParcelables$TaskSnapshotFeedback = (FeedbackParcelables$TaskSnapshotFeedback)this.feedback;
            if (feedbackParcelables$TaskSnapshotFeedback == null) {
                bundle.putBundle("feedback", (Bundle)null);
            }
            else {
                bundle.putBundle("feedback", feedbackParcelables$TaskSnapshotFeedback.writeToBundle());
            }
        }
        if (this.feedback instanceof FeedbackParcelables$ScreenshotFeedback) {
            bundle.putInt("feedback#tag", 11);
            final FeedbackParcelables$ScreenshotFeedback feedbackParcelables$ScreenshotFeedback = (FeedbackParcelables$ScreenshotFeedback)this.feedback;
            if (feedbackParcelables$ScreenshotFeedback == null) {
                bundle.putBundle("feedback", (Bundle)null);
            }
            else {
                bundle.putBundle("feedback", feedbackParcelables$ScreenshotFeedback.writeToBundle());
            }
        }
        bundle.putString("id", this.id);
        bundle.putLong("timestampMs", this.timestampMs);
        final FeedbackParcelables$SuggestionAction suggestionAction = this.suggestionAction;
        if (suggestionAction == null) {
            bundle.putBundle("suggestionAction", (Bundle)null);
        }
        else {
            bundle.putBundle("suggestionAction", suggestionAction.writeToBundle());
        }
        final InteractionContextParcelables$InteractionContext interactionContext = this.interactionContext;
        if (interactionContext == null) {
            bundle.putBundle("interactionContext", (Bundle)null);
        }
        else {
            bundle.putBundle("interactionContext", interactionContext.writeToBundle());
        }
        return bundle;
    }
}
