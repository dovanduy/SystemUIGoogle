// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.List;

public final class FeedbackParcelables$ActionFeedback
{
    @Nullable
    private List<FeedbackParcelables$ActionMenuItem> actionMenuItems;
    private int actionPresentationMode;
    @Nullable
    private List<SuggestParcelables$Action> actionShown;
    @Nullable
    private String interactionSessionId;
    private SuggestParcelables$InteractionType interactionType;
    @Nullable
    private SuggestParcelables$Action invokedAction;
    @Nullable
    private FeedbackParcelables$ActionMenuItem invokedActionMenuItem;
    @Nullable
    private String overviewSessionId;
    @Nullable
    private SuggestParcelables$Entity selectedEntity;
    @Nullable
    private FeedbackParcelables$SelectionDetail selection;
    @Nullable
    private String selectionSessionId;
    private FeedbackParcelables$SelectionFeedback.SelectionType selectionType;
    @Nullable
    private String taskSnapshotSessionId;
    private ActionInteraction userInteraction;
    @Nullable
    private String verticalTypeName;
    
    private FeedbackParcelables$ActionFeedback() {
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        final FeedbackParcelables$SelectionFeedback.SelectionType selectionType = this.selectionType;
        if (selectionType == null) {
            bundle.putBundle("selectionType", (Bundle)null);
        }
        else {
            bundle.putBundle("selectionType", selectionType.writeToBundle());
        }
        final SuggestParcelables$Entity selectedEntity = this.selectedEntity;
        if (selectedEntity == null) {
            bundle.putBundle("selectedEntity", (Bundle)null);
        }
        else {
            bundle.putBundle("selectedEntity", selectedEntity.writeToBundle());
        }
        if (this.actionShown == null) {
            bundle.putParcelableArrayList("actionShown", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.actionShown.size());
            for (final SuggestParcelables$Action suggestParcelables$Action : this.actionShown) {
                if (suggestParcelables$Action == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$Action.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("actionShown", (ArrayList)list);
        }
        final SuggestParcelables$Action invokedAction = this.invokedAction;
        if (invokedAction == null) {
            bundle.putBundle("invokedAction", (Bundle)null);
        }
        else {
            bundle.putBundle("invokedAction", invokedAction.writeToBundle());
        }
        final ActionInteraction userInteraction = this.userInteraction;
        if (userInteraction == null) {
            bundle.putBundle("userInteraction", (Bundle)null);
        }
        else {
            bundle.putBundle("userInteraction", userInteraction.writeToBundle());
        }
        bundle.putInt("actionPresentationMode", this.actionPresentationMode);
        final FeedbackParcelables$SelectionDetail selection = this.selection;
        if (selection == null) {
            bundle.putBundle("selection", (Bundle)null);
        }
        else {
            bundle.putBundle("selection", selection.writeToBundle());
        }
        bundle.putString("overviewSessionId", this.overviewSessionId);
        bundle.putString("taskSnapshotSessionId", this.taskSnapshotSessionId);
        bundle.putString("interactionSessionId", this.interactionSessionId);
        bundle.putString("selectionSessionId", this.selectionSessionId);
        bundle.putString("verticalTypeName", this.verticalTypeName);
        if (this.actionMenuItems == null) {
            bundle.putParcelableArrayList("actionMenuItems", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list2 = new ArrayList<Bundle>(this.actionMenuItems.size());
            for (final FeedbackParcelables$ActionMenuItem feedbackParcelables$ActionMenuItem : this.actionMenuItems) {
                if (feedbackParcelables$ActionMenuItem == null) {
                    list2.add(null);
                }
                else {
                    list2.add(feedbackParcelables$ActionMenuItem.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("actionMenuItems", (ArrayList)list2);
        }
        final FeedbackParcelables$ActionMenuItem invokedActionMenuItem = this.invokedActionMenuItem;
        if (invokedActionMenuItem == null) {
            bundle.putBundle("invokedActionMenuItem", (Bundle)null);
        }
        else {
            bundle.putBundle("invokedActionMenuItem", invokedActionMenuItem.writeToBundle());
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
    
    public enum ActionInteraction
    {
        ACTION_DISMISSED(3), 
        ACTION_INVOKED(2), 
        ACTION_MENU_SHOWN(4), 
        ACTION_SHOWN(1), 
        ACTION_UNKNOWN(0);
        
        public final int value;
        
        private ActionInteraction(final int value) {
            this.value = value;
        }
        
        public Bundle writeToBundle() {
            final Bundle bundle = new Bundle();
            bundle.putInt("value", this.value);
            return bundle;
        }
    }
}
