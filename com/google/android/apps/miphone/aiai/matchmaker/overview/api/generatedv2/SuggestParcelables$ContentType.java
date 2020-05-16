// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum SuggestParcelables$ContentType
{
    CONTENT_TYPE_IMAGE(2), 
    CONTENT_TYPE_TEXT(1), 
    CONTENT_TYPE_UNKNOWN(0);
    
    public final int value;
    
    private SuggestParcelables$ContentType(final int value) {
        this.value = value;
    }
    
    public static SuggestParcelables$ContentType create(final int n) {
        if (n == 0) {
            return SuggestParcelables$ContentType.CONTENT_TYPE_UNKNOWN;
        }
        if (n == 1) {
            return SuggestParcelables$ContentType.CONTENT_TYPE_TEXT;
        }
        if (n == 2) {
            return SuggestParcelables$ContentType.CONTENT_TYPE_IMAGE;
        }
        return null;
    }
    
    public static SuggestParcelables$ContentType create(final Bundle bundle) {
        return create(bundle.getInt("value"));
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
