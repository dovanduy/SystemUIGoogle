// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.common;

import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.ParserParcelables$ParsedViewHierarchy;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.InteractionContextParcelables$InteractionContext;
import android.support.annotation.Nullable;
import android.os.Bundle;

public final class ContentSelectionBundle
{
    public final String activityName;
    @Nullable
    public final Bundle assistBundle;
    public final int bundleVersion;
    public final long captureTimestampMs;
    @Nullable
    public final InteractionContextParcelables$InteractionContext interactionContext;
    public final String packageName;
    @Nullable
    public final ParserParcelables$ParsedViewHierarchy parsedViewHierarchy;
    public final int taskId;
    
    private ContentSelectionBundle(final String packageName, final String activityName, final int taskId, final long captureTimestampMs, @Nullable final InteractionContextParcelables$InteractionContext interactionContext, @Nullable final Bundle assistBundle, @Nullable final ParserParcelables$ParsedViewHierarchy parsedViewHierarchy, final int bundleVersion) {
        this.packageName = packageName;
        this.activityName = activityName;
        this.taskId = taskId;
        this.captureTimestampMs = captureTimestampMs;
        this.interactionContext = interactionContext;
        this.assistBundle = assistBundle;
        this.parsedViewHierarchy = parsedViewHierarchy;
        this.bundleVersion = bundleVersion;
    }
    
    public static ContentSelectionBundle create(final String s, final String s2, final int n, final long n2, @Nullable final InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, @Nullable final Bundle bundle, @Nullable final ParserParcelables$ParsedViewHierarchy parserParcelables$ParsedViewHierarchy, final int n3) {
        return new ContentSelectionBundle(s, s2, n, n2, interactionContextParcelables$InteractionContext, bundle, parserParcelables$ParsedViewHierarchy, n3);
    }
    
    public Bundle createBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("PackageName", this.packageName);
        bundle.putString("ActivityName", this.activityName);
        bundle.putInt("TaskId", this.taskId);
        bundle.putLong("CaptureTimestampMs", this.captureTimestampMs);
        final InteractionContextParcelables$InteractionContext interactionContext = this.interactionContext;
        if (interactionContext == null) {
            bundle.putBundle("InteractionContext", (Bundle)null);
        }
        else {
            bundle.putBundle("InteractionContext", interactionContext.writeToBundle());
        }
        bundle.putBundle("AssistBundle", this.assistBundle);
        final ParserParcelables$ParsedViewHierarchy parsedViewHierarchy = this.parsedViewHierarchy;
        if (parsedViewHierarchy == null) {
            bundle.putBundle("ParsedViewHierarchy", (Bundle)null);
        }
        else {
            bundle.putBundle("ParsedViewHierarchy", parsedViewHierarchy.writeToBundle());
        }
        bundle.putInt("Version", this.bundleVersion);
        bundle.putInt("BundleTypedVersion", 3);
        return bundle;
    }
}
