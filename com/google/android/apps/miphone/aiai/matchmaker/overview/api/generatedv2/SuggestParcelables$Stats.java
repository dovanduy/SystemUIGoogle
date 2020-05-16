// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public final class SuggestParcelables$Stats
{
    private long endTimestampMs;
    private long entityExtractionMs;
    private long ocrDetectionMs;
    private long ocrMs;
    private long startTimestampMs;
    
    private SuggestParcelables$Stats(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$Stats create(final Bundle bundle) {
        return new SuggestParcelables$Stats(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("startTimestampMs")) {
            this.startTimestampMs = bundle.getLong("startTimestampMs");
        }
        if (bundle.containsKey("endTimestampMs")) {
            this.endTimestampMs = bundle.getLong("endTimestampMs");
        }
        if (bundle.containsKey("ocrMs")) {
            this.ocrMs = bundle.getLong("ocrMs");
        }
        if (bundle.containsKey("ocrDetectionMs")) {
            this.ocrDetectionMs = bundle.getLong("ocrDetectionMs");
        }
        if (bundle.containsKey("entityExtractionMs")) {
            this.entityExtractionMs = bundle.getLong("entityExtractionMs");
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putLong("startTimestampMs", this.startTimestampMs);
        bundle.putLong("endTimestampMs", this.endTimestampMs);
        bundle.putLong("ocrMs", this.ocrMs);
        bundle.putLong("ocrDetectionMs", this.ocrDetectionMs);
        bundle.putLong("entityExtractionMs", this.entityExtractionMs);
        return bundle;
    }
}
