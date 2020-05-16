// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class SuggestParcelables$Flag
{
    @Nullable
    private String name;
    @Nullable
    private String value;
    
    private SuggestParcelables$Flag() {
    }
    
    private SuggestParcelables$Flag(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$Flag create(final Bundle bundle) {
        return new SuggestParcelables$Flag(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("name")) {
            this.name = bundle.getString("name");
        }
        if (bundle.containsKey("value")) {
            this.value = bundle.getString("value");
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("name", this.name);
        bundle.putString("value", this.value);
        return bundle;
    }
}
