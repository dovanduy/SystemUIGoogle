// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui;

import com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils.Utils;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$FeedbackBatch;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotOpFeedback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotOpStatus;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotOp;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotFeedback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotActionFeedback;
import java.util.ArrayList;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$Feedback;
import java.util.List;

public class FeedbackDataBuilder
{
    final List<FeedbackParcelables$Feedback> feedbacks;
    final String overviewSessionId;
    final int screenSessionId;
    
    private FeedbackDataBuilder(final String overviewSessionId) {
        this.feedbacks = new ArrayList<FeedbackParcelables$Feedback>();
        this.overviewSessionId = overviewSessionId;
        this.screenSessionId = 0;
    }
    
    private FeedbackParcelables$Feedback addFeedback() {
        final FeedbackParcelables$Feedback create = FeedbackParcelables$Feedback.create();
        this.feedbacks.add(create);
        return create;
    }
    
    static FeedbackDataBuilder newBuilder(final String s) {
        return new FeedbackDataBuilder(s);
    }
    
    FeedbackDataBuilder addScreenshotActionFeedback(final String screenshotId, final String actionType, final boolean isSmartActions) {
        final FeedbackParcelables$ScreenshotActionFeedback create = FeedbackParcelables$ScreenshotActionFeedback.create();
        create.setActionType(actionType);
        create.setIsSmartActions(isSmartActions);
        final FeedbackParcelables$ScreenshotFeedback create2 = FeedbackParcelables$ScreenshotFeedback.create();
        create2.setScreenshotId(screenshotId);
        create2.setScreenshotFeedback(create);
        this.addFeedback().setFeedback(create2);
        return this;
    }
    
    FeedbackDataBuilder addScreenshotOpFeedback(final String screenshotId, final FeedbackParcelables$ScreenshotOp op, final FeedbackParcelables$ScreenshotOpStatus status, final long durationMs) {
        final FeedbackParcelables$ScreenshotOpFeedback create = FeedbackParcelables$ScreenshotOpFeedback.create();
        create.setDurationMs(durationMs);
        create.setOp(op);
        create.setStatus(status);
        final FeedbackParcelables$ScreenshotFeedback create2 = FeedbackParcelables$ScreenshotFeedback.create();
        create2.setScreenshotId(screenshotId);
        create2.setScreenshotFeedback(create);
        this.addFeedback().setFeedback(create2);
        return this;
    }
    
    public FeedbackParcelables$FeedbackBatch build() {
        final FeedbackParcelables$FeedbackBatch create = FeedbackParcelables$FeedbackBatch.create();
        create.setScreenSessionId(this.screenSessionId);
        create.setOverviewSessionId(this.overviewSessionId);
        final List<FeedbackParcelables$Feedback> feedbacks = this.feedbacks;
        Utils.checkNotNull(feedbacks);
        create.setFeedback(feedbacks);
        return create;
    }
}
