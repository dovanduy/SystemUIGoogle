// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class FeedbackParcelables$ActionMenuItem
{
    @Nullable
    private SuggestParcelables$IntentInfo actionIntent;
    private ActionMenuItemDisplayMode displayMode;
    @Nullable
    private String displayName;
    @Nullable
    private String id;
    private int invokeRankIndex;
    
    private FeedbackParcelables$ActionMenuItem() {
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("id", this.id);
        bundle.putString("displayName", this.displayName);
        bundle.putInt("invokeRankIndex", this.invokeRankIndex);
        final ActionMenuItemDisplayMode displayMode = this.displayMode;
        if (displayMode == null) {
            bundle.putBundle("displayMode", (Bundle)null);
        }
        else {
            bundle.putBundle("displayMode", displayMode.writeToBundle());
        }
        final SuggestParcelables$IntentInfo actionIntent = this.actionIntent;
        if (actionIntent == null) {
            bundle.putBundle("actionIntent", (Bundle)null);
        }
        else {
            bundle.putBundle("actionIntent", actionIntent.writeToBundle());
        }
        return bundle;
    }
    
    public enum ActionMenuItemDisplayMode
    {
        ON_OVERFLOW_MENU(2), 
        ON_PRIMARY_MENU(1), 
        UNKNOWN_DISPLAY_MODE(0);
        
        public final int value;
        
        private ActionMenuItemDisplayMode(final int value) {
            this.value = value;
        }
        
        public Bundle writeToBundle() {
            final Bundle bundle = new Bundle();
            bundle.putInt("value", this.value);
            return bundle;
        }
    }
}
