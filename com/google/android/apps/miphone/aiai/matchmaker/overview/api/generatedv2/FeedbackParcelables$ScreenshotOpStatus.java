// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public enum FeedbackParcelables$ScreenshotOpStatus
{
    ERROR(2), 
    OP_STATUS_UNKNOWN(0), 
    SUCCESS(1), 
    TIMEOUT(3);
    
    public final int value;
    
    private FeedbackParcelables$ScreenshotOpStatus(final int value) {
        this.value = value;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", this.value);
        return bundle;
    }
}
