// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$ActionGroupFeedback
{
    @Nullable
    private SuggestParcelables$ActionGroup actionGroup;
    private int actionGroupPresentationMode;
    @Nullable
    private List<SuggestParcelables$ActionGroup> actionGroupShown;
    @Nullable
    private SuggestParcelables$Entity selectedEntity;
    @Nullable
    private FeedbackParcelables$SelectionDetail selection;
    private FeedbackParcelables$SelectionFeedback.SelectionType selectionType;
    private ActionGroupInteraction userInteraction;
    
    private FeedbackParcelables$ActionGroupFeedback() {
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
        if (this.actionGroupShown == null) {
            bundle.putParcelableArrayList("actionGroupShown", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.actionGroupShown.size());
            for (final SuggestParcelables$ActionGroup suggestParcelables$ActionGroup : this.actionGroupShown) {
                if (suggestParcelables$ActionGroup == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$ActionGroup.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("actionGroupShown", (ArrayList)list);
        }
        final SuggestParcelables$ActionGroup actionGroup = this.actionGroup;
        if (actionGroup == null) {
            bundle.putBundle("actionGroup", (Bundle)null);
        }
        else {
            bundle.putBundle("actionGroup", actionGroup.writeToBundle());
        }
        final ActionGroupInteraction userInteraction = this.userInteraction;
        if (userInteraction == null) {
            bundle.putBundle("userInteraction", (Bundle)null);
        }
        else {
            bundle.putBundle("userInteraction", userInteraction.writeToBundle());
        }
        bundle.putInt("actionGroupPresentationMode", this.actionGroupPresentationMode);
        final FeedbackParcelables$SelectionDetail selection = this.selection;
        if (selection == null) {
            bundle.putBundle("selection", (Bundle)null);
        }
        else {
            bundle.putBundle("selection", selection.writeToBundle());
        }
        return bundle;
    }
    
    public enum ActionGroupInteraction
    {
        ACTION_GROUP_ACTION_UNKNOWN(0), 
        ACTION_GROUP_DISMISSED(2), 
        ACTION_GROUP_EXPANDED(3), 
        ACTION_GROUP_SHOWN(1);
        
        public final int value;
        
        private ActionGroupInteraction(final int value) {
            this.value = value;
        }
        
        public Bundle writeToBundle() {
            final Bundle bundle = new Bundle();
            bundle.putInt("value", this.value);
            return bundle;
        }
    }
}
