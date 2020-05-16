// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum SuggestParcelables$IntentParamType
{
    INTENT_PARAM_TYPE_CONTENT_URI(6), 
    INTENT_PARAM_TYPE_FLOAT(3), 
    INTENT_PARAM_TYPE_INT(2), 
    INTENT_PARAM_TYPE_INTENT(5), 
    INTENT_PARAM_TYPE_LONG(4), 
    INTENT_PARAM_TYPE_STRING(1), 
    INTENT_PARAM_TYPE_UNKNOWN(0);
    
    public final int value;
    
    private SuggestParcelables$IntentParamType(final int value) {
        this.value = value;
    }
    
    public static SuggestParcelables$IntentParamType create(final int n) {
        if (n == 0) {
            return SuggestParcelables$IntentParamType.INTENT_PARAM_TYPE_UNKNOWN;
        }
        if (n == 1) {
            return SuggestParcelables$IntentParamType.INTENT_PARAM_TYPE_STRING;
        }
        if (n == 2) {
            return SuggestParcelables$IntentParamType.INTENT_PARAM_TYPE_INT;
        }
        if (n == 3) {
            return SuggestParcelables$IntentParamType.INTENT_PARAM_TYPE_FLOAT;
        }
        if (n == 4) {
            return SuggestParcelables$IntentParamType.INTENT_PARAM_TYPE_LONG;
        }
        if (n == 5) {
            return SuggestParcelables$IntentParamType.INTENT_PARAM_TYPE_INTENT;
        }
        if (n == 6) {
            return SuggestParcelables$IntentParamType.INTENT_PARAM_TYPE_CONTENT_URI;
        }
        return null;
    }
    
    public static SuggestParcelables$IntentParamType create(final Bundle bundle) {
        return create(bundle.getInt("value"));
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
