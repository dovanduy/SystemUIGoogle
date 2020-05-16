// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui;

import android.support.annotation.VisibleForTesting;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$Feedback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils.Utils;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$FeedbackBatch;
import android.app.contentsuggestions.SelectionsRequest;
import android.app.contentsuggestions.ContentSuggestionsManager$SelectionsCallback;
import android.app.contentsuggestions.SelectionsRequest$Builder;
import android.support.annotation.Nullable;
import android.graphics.Bitmap;
import com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils.LogUtils;
import android.app.contentsuggestions.ContentClassification;
import android.app.contentsuggestions.ContentSuggestionsManager$ClassificationsCallback;
import android.app.contentsuggestions.ClassificationsRequest$Builder;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.contentsuggestions.ContentSelection;
import java.util.List;
import android.app.contentsuggestions.ContentSuggestionsManager;
import android.os.Handler;
import java.util.concurrent.Executor;
import android.content.Context;
import com.google.android.apps.miphone.aiai.matchmaker.overview.common.BundleUtils;

public class SuggestController
{
    public static Factory defaultFactory;
    private final BundleUtils bundleUtils;
    private final ContentSuggestionsServiceWrapper wrapper;
    
    static {
        SuggestController.defaultFactory = SuggestController$$Lambda$3.$instance;
    }
    
    protected SuggestController(final Context context, final Context context2, final Executor executor, final Handler handler) {
        this.wrapper = SuggestController.defaultFactory.create(context, executor, handler);
        this.bundleUtils = BundleUtils.createWithBackwardsCompatVersion();
    }
    
    public static SuggestController create(final Context context, final Context context2, final Executor executor, final Handler handler) {
        return new SuggestController(context, context2, executor, handler);
    }
    
    ContentSuggestionsServiceWrapper getWrapper() {
        return this.wrapper;
    }
    
    @VisibleForTesting
    void reportMetricsToService(final String s, final FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch, @Nullable final SuggestListener suggestListener) {
        final List<FeedbackParcelables$Feedback> feedback = feedbackParcelables$FeedbackBatch.getFeedback();
        Utils.checkNotNull(feedback);
        if (feedback.isEmpty()) {
            return;
        }
        this.wrapper.connectAndRunAsync(new SuggestController$$Lambda$2(this, feedbackParcelables$FeedbackBatch, s, suggestListener));
    }
    
    public interface Factory
    {
        ContentSuggestionsServiceWrapper create(final Context p0, final Executor p1, final Handler p2);
    }
}
