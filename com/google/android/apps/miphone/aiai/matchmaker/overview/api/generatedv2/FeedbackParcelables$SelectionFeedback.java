// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$SelectionFeedback
{
    @Nullable
    private String interactionSessionId;
    private SuggestParcelables$InteractionType interactionType;
    @Nullable
    private String overviewSessionId;
    @Nullable
    private ContentParcelables$Contents screenContents;
    @Nullable
    private SuggestParcelables$Entity selectedEntity;
    @Nullable
    private FeedbackParcelables$SelectionDetail selection;
    private int selectionPresentationMode;
    @Nullable
    private String selectionSessionId;
    @Nullable
    private String taskSnapshotSessionId;
    private SelectionType type;
    private SelectionInteraction userInteraction;
    
    private FeedbackParcelables$SelectionFeedback() {
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        final SelectionType type = this.type;
        if (type == null) {
            bundle.putBundle("type", (Bundle)null);
        }
        else {
            bundle.putBundle("type", type.writeToBundle());
        }
        final SuggestParcelables$Entity selectedEntity = this.selectedEntity;
        if (selectedEntity == null) {
            bundle.putBundle("selectedEntity", (Bundle)null);
        }
        else {
            bundle.putBundle("selectedEntity", selectedEntity.writeToBundle());
        }
        final FeedbackParcelables$SelectionDetail selection = this.selection;
        if (selection == null) {
            bundle.putBundle("selection", (Bundle)null);
        }
        else {
            bundle.putBundle("selection", selection.writeToBundle());
        }
        final SelectionInteraction userInteraction = this.userInteraction;
        if (userInteraction == null) {
            bundle.putBundle("userInteraction", (Bundle)null);
        }
        else {
            bundle.putBundle("userInteraction", userInteraction.writeToBundle());
        }
        bundle.putInt("selectionPresentationMode", this.selectionPresentationMode);
        bundle.putString("overviewSessionId", this.overviewSessionId);
        bundle.putString("taskSnapshotSessionId", this.taskSnapshotSessionId);
        bundle.putString("interactionSessionId", this.interactionSessionId);
        bundle.putString("selectionSessionId", this.selectionSessionId);
        final ContentParcelables$Contents screenContents = this.screenContents;
        if (screenContents == null) {
            bundle.putBundle("screenContents", (Bundle)null);
        }
        else {
            bundle.putBundle("screenContents", screenContents.writeToBundle());
        }
        final SuggestParcelables$InteractionType interactionType = this.interactionType;
        if (interactionType == null) {
            bundle.putBundle("interactionType", (Bundle)null);
        }
        else {
            bundle.putBundle("interactionType", interactionType.writeToBundle());
        }
        return bundle;
    }
    
    public enum SelectionInteraction
    {
        SELECTION_ACTION_UNKNOWN(0), 
        SELECTION_ADJUSTED(3), 
        SELECTION_CONFIRMED(4), 
        SELECTION_DISMISSED(2), 
        SELECTION_INITIATED(1), 
        SELECTION_SHOWN(7), 
        SELECTION_SUGGESTED(5), 
        SELECTION_SUGGESTION_VERIFIED(6);
        
        public final int value;
        
        private SelectionInteraction(final int value) {
            this.value = value;
        }
        
        public Bundle writeToBundle() {
            final Bundle bundle = new Bundle();
            bundle.putInt("value", this.value);
            return bundle;
        }
    }
    
    public enum SelectionType
    {
        IMAGE(2), 
        SELECTION_TYPE_UNKNOWN(0), 
        TEXT(1);
        
        public final int value;
        
        private SelectionType(final int value) {
            this.value = value;
        }
        
        public Bundle writeToBundle() {
            final Bundle bundle = new Bundle();
            bundle.putInt("value", this.value);
            return bundle;
        }
    }
}
