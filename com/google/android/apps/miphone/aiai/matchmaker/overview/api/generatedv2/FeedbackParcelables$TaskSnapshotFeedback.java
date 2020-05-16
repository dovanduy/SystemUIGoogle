// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$TaskSnapshotFeedback
{
    @Nullable
    private String interactionSessionId;
    @Nullable
    private String overviewSessionId;
    @Nullable
    private String taskAppComponentName;
    @Nullable
    private String taskSnapshotSessionId;
    private TaskSnapshotInteraction userInteraction;
    
    private FeedbackParcelables$TaskSnapshotFeedback() {
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        final TaskSnapshotInteraction userInteraction = this.userInteraction;
        if (userInteraction == null) {
            bundle.putBundle("userInteraction", (Bundle)null);
        }
        else {
            bundle.putBundle("userInteraction", userInteraction.writeToBundle());
        }
        bundle.putString("overviewSessionId", this.overviewSessionId);
        bundle.putString("taskSnapshotSessionId", this.taskSnapshotSessionId);
        bundle.putString("taskAppComponentName", this.taskAppComponentName);
        bundle.putString("interactionSessionId", this.interactionSessionId);
        return bundle;
    }
    
    public enum TaskSnapshotInteraction
    {
        TASK_SNAPSHOT_CREATED(1), 
        TASK_SNAPSHOT_DISMISSED(6), 
        TASK_SNAPSHOT_GLEAMS_DISPLAYED(4), 
        TASK_SNAPSHOT_LONG_PRESSED(5), 
        TASK_SNAPSHOT_PROACTIVE_HINTS_DISPLAYED(3), 
        TASK_SNAPSHOT_SUGGEST_VIEW_DISPLAYED(2), 
        UNKNOWN_TASK_SNAPSHOT_ACTION(0);
        
        public final int value;
        
        private TaskSnapshotInteraction(final int value) {
            this.value = value;
        }
        
        public Bundle writeToBundle() {
            final Bundle bundle = new Bundle();
            bundle.putInt("value", this.value);
            return bundle;
        }
    }
}
