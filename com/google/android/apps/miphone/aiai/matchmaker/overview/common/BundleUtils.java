// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.common;

import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.EntitiesData;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.ParserParcelables$ParsedViewHierarchy;
import android.app.Notification$Action;
import java.util.ArrayList;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$FeedbackBatch;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.ContentParcelables$Contents;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.InteractionContextParcelables$InteractionContext;
import android.support.annotation.Nullable;
import android.os.Parcelable;
import android.os.Bundle;

public final class BundleUtils
{
    private final int bundleVersion;
    
    private BundleUtils(final int bundleVersion) {
        this.bundleVersion = bundleVersion;
    }
    
    public static BundleUtils createWithBackwardsCompatVersion() {
        return new BundleUtils(4);
    }
    
    public static <T extends Parcelable> T extractParcelable(final Bundle bundle, final String s, final Class<T> clazz) {
        bundle.setClassLoader(clazz.getClassLoader());
        return (T)bundle.getParcelable(s);
    }
    
    public Bundle createClassificationsRequest(final String s, final String s2, final int n, final long n2, @Nullable final Bundle bundle, @Nullable final InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, final ContentParcelables$Contents contentParcelables$Contents) {
        return ContentClassificationsBundle.create(s, s2, n, n2, bundle, contentParcelables$Contents, interactionContextParcelables$InteractionContext, this.bundleVersion).createBundle();
    }
    
    public Bundle createFeedbackRequest(@Nullable final FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch) {
        return FeedbackBundle.create(feedbackParcelables$FeedbackBatch, this.bundleVersion).createBundle();
    }
    
    public Bundle createScreenshotActionsResponse(final ArrayList<Notification$Action> list) {
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("ScreenshotNotificationActions", (ArrayList)list);
        return bundle;
    }
    
    public Bundle createSelectionsRequest(final String s, final String s2, final int n, final long n2, @Nullable final InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, @Nullable final Bundle bundle, @Nullable final ParserParcelables$ParsedViewHierarchy parserParcelables$ParsedViewHierarchy) {
        return ContentSelectionBundle.create(s, s2, n, n2, interactionContextParcelables$InteractionContext, bundle, parserParcelables$ParsedViewHierarchy, this.bundleVersion).createBundle();
    }
    
    public ContentParcelables$Contents extractContents(Bundle bundle) {
        bundle = bundle.getBundle("Contents");
        ContentParcelables$Contents contentParcelables$Contents;
        if (bundle == null) {
            contentParcelables$Contents = ContentParcelables$Contents.create();
        }
        else {
            contentParcelables$Contents = ContentParcelables$Contents.create(bundle);
        }
        return contentParcelables$Contents;
    }
    
    public EntitiesData extractEntitiesParcelable(final Bundle bundle) {
        return extractParcelable(bundle, "EntitiesData", EntitiesData.class);
    }
    
    public Bundle obtainContextImageBundle(final boolean b, final String s, final String s2, final long n) {
        final Bundle bundle = new Bundle();
        bundle.putInt("CONTEXT_IMAGE_BUNDLE_VERSION_KEY", 1);
        bundle.putBoolean("CONTEXT_IMAGE_PRIMARY_TASK_KEY", b);
        bundle.putString("CONTEXT_IMAGE_PACKAGE_NAME_KEY", s);
        bundle.putString("CONTEXT_IMAGE_ACTIVITY_NAME_KEY", s2);
        bundle.putLong("CONTEXT_IMAGE_CAPTURE_TIME_MS_KEY", n);
        return bundle;
    }
    
    public Bundle obtainScreenshotContextImageBundle(final boolean b, final String s, final String s2, final String s3, final long n) {
        final Bundle obtainContextImageBundle = this.obtainContextImageBundle(b, s2, s3, n);
        obtainContextImageBundle.putString("CONTEXT_IMAGE_BITMAP_FILE_NAME_KEY", s);
        return obtainContextImageBundle;
    }
}
