// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.common;

import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.InteractionContextParcelables$InteractionContext;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.ContentParcelables$Contents;
import android.support.annotation.Nullable;
import android.os.Bundle;

public final class ContentClassificationsBundle
{
    public final String activityName;
    @Nullable
    public final Bundle assistBundle;
    public final int bundleVersion;
    public final long captureTimestampMs;
    public final ContentParcelables$Contents contents;
    @Nullable
    public final InteractionContextParcelables$InteractionContext interactionContext;
    public final String packageName;
    public final int taskId;
    
    private ContentClassificationsBundle(final String packageName, final String activityName, final int taskId, final long captureTimestampMs, @Nullable final Bundle assistBundle, final ContentParcelables$Contents contents, @Nullable final InteractionContextParcelables$InteractionContext interactionContext, final int bundleVersion) {
        this.packageName = packageName;
        this.activityName = activityName;
        this.taskId = taskId;
        this.captureTimestampMs = captureTimestampMs;
        this.assistBundle = assistBundle;
        this.contents = contents;
        this.interactionContext = interactionContext;
        this.bundleVersion = bundleVersion;
    }
    
    public static ContentClassificationsBundle create(final String s, final String s2, final int n, final long n2, @Nullable final Bundle bundle, final ContentParcelables$Contents contentParcelables$Contents, @Nullable final InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, final int n3) {
        return new ContentClassificationsBundle(s, s2, n, n2, bundle, contentParcelables$Contents, interactionContextParcelables$InteractionContext, n3);
    }
    
    public Bundle createBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("PackageName", this.packageName);
        bundle.putString("ActivityName", this.activityName);
        bundle.putInt("TaskId", this.taskId);
        bundle.putLong("CaptureTimestampMs", this.captureTimestampMs);
        bundle.putBundle("AssistBundle", this.assistBundle);
        final ContentParcelables$Contents contents = this.contents;
        if (contents == null) {
            bundle.putBundle("Contents", (Bundle)null);
        }
        else {
            bundle.putBundle("Contents", contents.writeToBundle());
        }
        final InteractionContextParcelables$InteractionContext interactionContext = this.interactionContext;
        if (interactionContext == null) {
            bundle.putBundle("InteractionContext", (Bundle)null);
        }
        else {
            bundle.putBundle("InteractionContext", interactionContext.writeToBundle());
        }
        bundle.putInt("Version", this.bundleVersion);
        bundle.putInt("BundleTypedVersion", 3);
        return bundle;
    }
}
