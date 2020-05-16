// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum SuggestParcelables$InteractionType
{
    CHIP(3), 
    COMPOSE(8), 
    GLEAM(2), 
    GLEAM_CHIP(4), 
    LONG_PRESS(1), 
    SCREENSHOT_NOTIFICATION(5), 
    SELECT_MODE(6), 
    SETUP(7), 
    UNKNOWN(0);
    
    public final int value;
    
    private SuggestParcelables$InteractionType(final int value) {
        this.value = value;
    }
    
    public static SuggestParcelables$InteractionType create(final int n) {
        if (n == 0) {
            return SuggestParcelables$InteractionType.UNKNOWN;
        }
        if (n == 1) {
            return SuggestParcelables$InteractionType.LONG_PRESS;
        }
        if (n == 2) {
            return SuggestParcelables$InteractionType.GLEAM;
        }
        if (n == 3) {
            return SuggestParcelables$InteractionType.CHIP;
        }
        if (n == 4) {
            return SuggestParcelables$InteractionType.GLEAM_CHIP;
        }
        if (n == 5) {
            return SuggestParcelables$InteractionType.SCREENSHOT_NOTIFICATION;
        }
        if (n == 6) {
            return SuggestParcelables$InteractionType.SELECT_MODE;
        }
        if (n == 7) {
            return SuggestParcelables$InteractionType.SETUP;
        }
        if (n == 8) {
            return SuggestParcelables$InteractionType.COMPOSE;
        }
        return null;
    }
    
    public static SuggestParcelables$InteractionType create(final Bundle bundle) {
        return create(bundle.getInt("value"));
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
