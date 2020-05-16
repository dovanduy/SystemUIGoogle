// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.List;

public final class FeedbackParcelables$FeedbackBatch
{
    @Nullable
    private List<FeedbackParcelables$Feedback> feedback;
    @Nullable
    private String overviewSessionId;
    private long screenSessionId;
    
    private FeedbackParcelables$FeedbackBatch() {
    }
    
    public static FeedbackParcelables$FeedbackBatch create() {
        return new FeedbackParcelables$FeedbackBatch();
    }
    
    @Nullable
    public List<FeedbackParcelables$Feedback> getFeedback() {
        return this.feedback;
    }
    
    public void setFeedback(@Nullable final List<FeedbackParcelables$Feedback> feedback) {
        this.feedback = feedback;
    }
    
    public void setOverviewSessionId(@Nullable final String overviewSessionId) {
        this.overviewSessionId = overviewSessionId;
    }
    
    public void setScreenSessionId(final long screenSessionId) {
        this.screenSessionId = screenSessionId;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        if (this.feedback == null) {
            bundle.putParcelableArrayList("feedback", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.feedback.size());
            for (final FeedbackParcelables$Feedback feedbackParcelables$Feedback : this.feedback) {
                if (feedbackParcelables$Feedback == null) {
                    list.add(null);
                }
                else {
                    list.add(feedbackParcelables$Feedback.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("feedback", (ArrayList)list);
        }
        bundle.putLong("screenSessionId", this.screenSessionId);
        bundle.putString("overviewSessionId", this.overviewSessionId);
        return bundle;
    }
}
