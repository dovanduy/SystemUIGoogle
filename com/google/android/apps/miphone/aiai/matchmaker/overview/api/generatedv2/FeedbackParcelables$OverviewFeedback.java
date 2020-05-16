// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Collection;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$OverviewFeedback
{
    private int numSelectionsInitialized;
    private int numSelectionsSuggested;
    private int overviewPresentationMode;
    @Nullable
    private String overviewSessionId;
    @Nullable
    private String primaryTaskAppComponentName;
    @Nullable
    private List<String> taskAppComponentNameList;
    @Nullable
    private String taskSnapshotSessionId;
    private OverviewInteraction userInteraction;
    
    private FeedbackParcelables$OverviewFeedback() {
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        final OverviewInteraction userInteraction = this.userInteraction;
        if (userInteraction == null) {
            bundle.putBundle("userInteraction", (Bundle)null);
        }
        else {
            bundle.putBundle("userInteraction", userInteraction.writeToBundle());
        }
        bundle.putInt("overviewPresentationMode", this.overviewPresentationMode);
        bundle.putInt("numSelectionsSuggested", this.numSelectionsSuggested);
        bundle.putInt("numSelectionsInitialized", this.numSelectionsInitialized);
        bundle.putString("overviewSessionId", this.overviewSessionId);
        bundle.putString("taskSnapshotSessionId", this.taskSnapshotSessionId);
        bundle.putString("primaryTaskAppComponentName", this.primaryTaskAppComponentName);
        if (this.taskAppComponentNameList == null) {
            bundle.putStringArrayList("taskAppComponentNameList", (ArrayList)null);
        }
        else {
            bundle.putStringArrayList("taskAppComponentNameList", new ArrayList((Collection<? extends E>)this.taskAppComponentNameList));
        }
        return bundle;
    }
    
    public enum OverviewInteraction
    {
        OVERVIEW_SCREEN_APP_CLOSED(6), 
        OVERVIEW_SCREEN_DISMISSED(2), 
        OVERVIEW_SCREEN_ENTER_ALL_APPS(11), 
        OVERVIEW_SCREEN_EXIT_APP_ENTERED(7), 
        OVERVIEW_SCREEN_EXIT_BACK_BUTTON(8), 
        OVERVIEW_SCREEN_EXIT_HOME_BUTTON(9), 
        OVERVIEW_SCREEN_EXIT_POWER_BUTTON(10), 
        OVERVIEW_SCREEN_QUICK_DISMISSED(3), 
        OVERVIEW_SCREEN_STARTED(1), 
        OVERVIEW_SCREEN_SWITCHED(4), 
        OVERVIEW_TASK_SNAPSHOT_DISPLAY(5), 
        UNKNOWN_OVERVIEW_ACTION(0);
        
        public final int value;
        
        private OverviewInteraction(final int value) {
            this.value = value;
        }
        
        public Bundle writeToBundle() {
            final Bundle bundle = new Bundle();
            bundle.putInt("value", this.value);
            return bundle;
        }
    }
}
