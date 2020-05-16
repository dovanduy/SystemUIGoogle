// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Collection;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class ContentParcelables$Selection
{
    @Nullable
    private String id;
    private SuggestParcelables$InteractionType interactionType;
    private boolean isSmartSelection;
    @Nullable
    private String opaquePayload;
    @Nullable
    private List<Integer> rectIndices;
    private int suggestedPresentationMode;
    
    private ContentParcelables$Selection() {
    }
    
    private ContentParcelables$Selection(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static ContentParcelables$Selection create(final Bundle bundle) {
        return new ContentParcelables$Selection(bundle);
    }
    
    private void readFromBundle(Bundle bundle) {
        if (bundle.containsKey("rectIndices")) {
            this.rectIndices = (List<Integer>)bundle.getIntegerArrayList("rectIndices");
        }
        if (bundle.containsKey("id")) {
            this.id = bundle.getString("id");
        }
        if (bundle.containsKey("isSmartSelection")) {
            this.isSmartSelection = bundle.getBoolean("isSmartSelection");
        }
        if (bundle.containsKey("suggestedPresentationMode")) {
            this.suggestedPresentationMode = bundle.getInt("suggestedPresentationMode");
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
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        if (this.rectIndices == null) {
            bundle.putIntegerArrayList("rectIndices", (ArrayList)null);
        }
        else {
            bundle.putIntegerArrayList("rectIndices", new ArrayList((Collection<? extends E>)this.rectIndices));
        }
        bundle.putString("id", this.id);
        bundle.putBoolean("isSmartSelection", this.isSmartSelection);
        bundle.putInt("suggestedPresentationMode", this.suggestedPresentationMode);
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
