// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum SuggestParcelables$ErrorCode
{
    ERROR_CODE_NO_SCREEN_CONTENT(3), 
    ERROR_CODE_NO_SUPPORTED_LOCALES(4), 
    ERROR_CODE_SUCCESS(0), 
    ERROR_CODE_TIMEOUT(2), 
    ERROR_CODE_UNKNOWN_ERROR(1);
    
    public final int value;
    
    private SuggestParcelables$ErrorCode(final int value) {
        this.value = value;
    }
    
    public static SuggestParcelables$ErrorCode create(final int n) {
        if (n == 0) {
            return SuggestParcelables$ErrorCode.ERROR_CODE_SUCCESS;
        }
        if (n == 1) {
            return SuggestParcelables$ErrorCode.ERROR_CODE_UNKNOWN_ERROR;
        }
        if (n == 2) {
            return SuggestParcelables$ErrorCode.ERROR_CODE_TIMEOUT;
        }
        if (n == 3) {
            return SuggestParcelables$ErrorCode.ERROR_CODE_NO_SCREEN_CONTENT;
        }
        if (n == 4) {
            return SuggestParcelables$ErrorCode.ERROR_CODE_NO_SUPPORTED_LOCALES;
        }
        return null;
    }
    
    public static SuggestParcelables$ErrorCode create(final Bundle bundle) {
        return create(bundle.getInt("value"));
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
