// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public final class SuggestParcelables$ExtrasInfo
{
    private boolean containsBitmaps;
    private boolean containsPendingIntents;
    
    private SuggestParcelables$ExtrasInfo(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$ExtrasInfo create(final Bundle bundle) {
        return new SuggestParcelables$ExtrasInfo(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("containsPendingIntents")) {
            this.containsPendingIntents = bundle.getBoolean("containsPendingIntents");
        }
        if (bundle.containsKey("containsBitmaps")) {
            this.containsBitmaps = bundle.getBoolean("containsBitmaps");
        }
    }
    
    public boolean getContainsBitmaps() {
        return this.containsBitmaps;
    }
    
    public boolean getContainsPendingIntents() {
        return this.containsPendingIntents;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean("containsPendingIntents", this.containsPendingIntents);
        bundle.putBoolean("containsBitmaps", this.containsBitmaps);
        return bundle;
    }
}
