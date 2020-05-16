// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$FeedbackBatch;

public final class FeedbackBundle
{
    public final int bundleVersion;
    @Nullable
    public final FeedbackParcelables$FeedbackBatch feedbackBatch;
    
    private FeedbackBundle(@Nullable final FeedbackParcelables$FeedbackBatch feedbackBatch, final int bundleVersion) {
        this.feedbackBatch = feedbackBatch;
        this.bundleVersion = bundleVersion;
    }
    
    public static FeedbackBundle create(@Nullable final FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch, final int n) {
        return new FeedbackBundle(feedbackParcelables$FeedbackBatch, n);
    }
    
    private static Bundle createBundle(final Bundle bundle, final int n) {
        bundle.putInt("Version", n);
        bundle.putInt("BundleTypedVersion", 6);
        return bundle;
    }
    
    public Bundle createBundle() {
        final Bundle bundle = new Bundle();
        final FeedbackParcelables$FeedbackBatch feedbackBatch = this.feedbackBatch;
        if (feedbackBatch == null) {
            bundle.putBundle("FeedbackBatch", (Bundle)null);
        }
        else {
            bundle.putBundle("FeedbackBatch", feedbackBatch.writeToBundle());
        }
        createBundle(bundle, this.bundleVersion);
        return bundle;
    }
}
