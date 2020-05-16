// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.List;

public final class SuggestParcelables$Entity
{
    @Nullable
    private List<SuggestParcelables$ActionGroup> actions;
    private float annotationScore;
    @Nullable
    private String annotationSourceName;
    @Nullable
    private String annotationTypeName;
    private int contentGroupIndex;
    private int endIndex;
    @Nullable
    private List<SuggestParcelables$EntitySpan> entitySpans;
    @Nullable
    private String id;
    private SuggestParcelables$InteractionType interactionType;
    private boolean isSmartSelection;
    private int numWords;
    @Nullable
    private String opaquePayload;
    @Nullable
    private String searchQueryHint;
    private int selectionIndex;
    private int startIndex;
    private int suggestedPresentationMode;
    @Nullable
    private String verticalTypeName;
    
    private SuggestParcelables$Entity() {
    }
    
    private SuggestParcelables$Entity(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$Entity create(final Bundle bundle) {
        return new SuggestParcelables$Entity(bundle);
    }
    
    private void readFromBundle(Bundle bundle) {
        if (bundle.containsKey("id")) {
            this.id = bundle.getString("id");
        }
        if (bundle.containsKey("actions")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("actions");
            if (parcelableArrayList == null) {
                this.actions = null;
            }
            else {
                this.actions = new ArrayList<SuggestParcelables$ActionGroup>(parcelableArrayList.size());
                for (final Bundle bundle2 : parcelableArrayList) {
                    if (bundle2 == null) {
                        this.actions.add(null);
                    }
                    else {
                        this.actions.add(SuggestParcelables$ActionGroup.create(bundle2));
                    }
                }
            }
        }
        if (bundle.containsKey("entitySpans")) {
            final ArrayList parcelableArrayList2 = bundle.getParcelableArrayList("entitySpans");
            if (parcelableArrayList2 == null) {
                this.entitySpans = null;
            }
            else {
                this.entitySpans = new ArrayList<SuggestParcelables$EntitySpan>(parcelableArrayList2.size());
                for (final Bundle bundle3 : parcelableArrayList2) {
                    if (bundle3 == null) {
                        this.entitySpans.add(null);
                    }
                    else {
                        this.entitySpans.add(SuggestParcelables$EntitySpan.create(bundle3));
                    }
                }
            }
        }
        if (bundle.containsKey("searchQueryHint")) {
            this.searchQueryHint = bundle.getString("searchQueryHint");
        }
        if (bundle.containsKey("annotationTypeName")) {
            this.annotationTypeName = bundle.getString("annotationTypeName");
        }
        if (bundle.containsKey("annotationSourceName")) {
            this.annotationSourceName = bundle.getString("annotationSourceName");
        }
        if (bundle.containsKey("verticalTypeName")) {
            this.verticalTypeName = bundle.getString("verticalTypeName");
        }
        if (bundle.containsKey("annotationScore")) {
            this.annotationScore = bundle.getFloat("annotationScore");
        }
        if (bundle.containsKey("contentGroupIndex")) {
            this.contentGroupIndex = bundle.getInt("contentGroupIndex");
        }
        if (bundle.containsKey("selectionIndex")) {
            this.selectionIndex = bundle.getInt("selectionIndex");
        }
        if (bundle.containsKey("isSmartSelection")) {
            this.isSmartSelection = bundle.getBoolean("isSmartSelection");
        }
        if (bundle.containsKey("suggestedPresentationMode")) {
            this.suggestedPresentationMode = bundle.getInt("suggestedPresentationMode");
        }
        if (bundle.containsKey("numWords")) {
            this.numWords = bundle.getInt("numWords");
        }
        if (bundle.containsKey("startIndex")) {
            this.startIndex = bundle.getInt("startIndex");
        }
        if (bundle.containsKey("endIndex")) {
            this.endIndex = bundle.getInt("endIndex");
        }
        if (bundle.containsKey("opaquePayload")) {
            this.opaquePayload = bundle.getString("opaquePayload");
        }
        if (bundle.containsKey("interactionType")) {
            bundle = bundle.getBundle("interactionType");
            if (bundle == null) {
                this.interactionType = null;
            }
            else {
                this.interactionType = SuggestParcelables$InteractionType.create(bundle);
            }
            final SuggestParcelables$InteractionType interactionType = this.interactionType;
        }
    }
    
    @Nullable
    public List<SuggestParcelables$ActionGroup> getActions() {
        return this.actions;
    }
    
    @Nullable
    public String getSearchQueryHint() {
        return this.searchQueryHint;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("id", this.id);
        if (this.actions == null) {
            bundle.putParcelableArrayList("actions", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.actions.size());
            for (final SuggestParcelables$ActionGroup suggestParcelables$ActionGroup : this.actions) {
                if (suggestParcelables$ActionGroup == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$ActionGroup.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("actions", (ArrayList)list);
        }
        if (this.entitySpans == null) {
            bundle.putParcelableArrayList("entitySpans", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list2 = new ArrayList<Bundle>(this.entitySpans.size());
            for (final SuggestParcelables$EntitySpan suggestParcelables$EntitySpan : this.entitySpans) {
                if (suggestParcelables$EntitySpan == null) {
                    list2.add(null);
                }
                else {
                    list2.add(suggestParcelables$EntitySpan.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("entitySpans", (ArrayList)list2);
        }
        bundle.putString("searchQueryHint", this.searchQueryHint);
        bundle.putString("annotationTypeName", this.annotationTypeName);
        bundle.putString("annotationSourceName", this.annotationSourceName);
        bundle.putString("verticalTypeName", this.verticalTypeName);
        bundle.putFloat("annotationScore", this.annotationScore);
        bundle.putInt("contentGroupIndex", this.contentGroupIndex);
        bundle.putInt("selectionIndex", this.selectionIndex);
        bundle.putBoolean("isSmartSelection", this.isSmartSelection);
        bundle.putInt("suggestedPresentationMode", this.suggestedPresentationMode);
        bundle.putInt("numWords", this.numWords);
        bundle.putInt("startIndex", this.startIndex);
        bundle.putInt("endIndex", this.endIndex);
        bundle.putString("opaquePayload", this.opaquePayload);
        final SuggestParcelables$InteractionType interactionType = this.interactionType;
        if (interactionType == null) {
            bundle.putBundle("interactionType", (Bundle)null);
        }
        else {
            bundle.putBundle("interactionType", interactionType.writeToBundle());
        }
        return bundle;
    }
}
