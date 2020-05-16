// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui;

import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$FeedbackBatch;

public interface SuggestListener
{
    void onFeedbackBatchSent(final String p0, final FeedbackParcelables$FeedbackBatch p1);
}
