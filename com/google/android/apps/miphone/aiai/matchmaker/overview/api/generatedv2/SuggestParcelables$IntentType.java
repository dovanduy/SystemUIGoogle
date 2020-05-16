// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum SuggestParcelables$IntentType
{
    COPY_IMAGE(5), 
    COPY_TEXT(1), 
    DEFAULT(0), 
    LENS(3), 
    SAVE(4), 
    SHARE_IMAGE(2);
    
    public final int value;
    
    private SuggestParcelables$IntentType(final int value) {
        this.value = value;
    }
    
    public static SuggestParcelables$IntentType create(final int n) {
        if (n == 0) {
            return SuggestParcelables$IntentType.DEFAULT;
        }
        if (n == 1) {
            return SuggestParcelables$IntentType.COPY_TEXT;
        }
        if (n == 2) {
            return SuggestParcelables$IntentType.SHARE_IMAGE;
        }
        if (n == 3) {
            return SuggestParcelables$IntentType.LENS;
        }
        if (n == 4) {
            return SuggestParcelables$IntentType.SAVE;
        }
        if (n == 5) {
            return SuggestParcelables$IntentType.COPY_IMAGE;
        }
        return null;
    }
    
    public static SuggestParcelables$IntentType create(final Bundle bundle) {
        return create(bundle.getInt("value"));
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
