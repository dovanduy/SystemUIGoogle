// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum FeedbackParcelables$ScreenshotOp
{
    OP_UNKNOWN(0), 
    REQUEST_SMART_ACTIONS(2), 
    RETRIEVE_SMART_ACTIONS(1), 
    WAIT_FOR_SMART_ACTIONS(3);
    
    public final int value;
    
    private FeedbackParcelables$ScreenshotOp(final int value) {
        this.value = value;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
