// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum FeedbackParcelables$SuggestionAction
{
    SUGGESTION_ACTION_CLICKED(1), 
    SUGGESTION_ACTION_DISMISSED(2), 
    SUGGESTION_ACTION_EXPANDED(4), 
    SUGGESTION_ACTION_SHOWN(3), 
    SUGGESTION_ACTION_UNKNOWN(0);
    
    public final int value;
    
    private FeedbackParcelables$SuggestionAction(final int value) {
        this.value = value;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
