// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;

public final class SuggestParcelables$OnScreenRect
{
    private float height;
    private float left;
    private float top;
    private float width;
    
    private SuggestParcelables$OnScreenRect(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$OnScreenRect create(final Bundle bundle) {
        return new SuggestParcelables$OnScreenRect(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("left")) {
            this.left = bundle.getFloat("left");
        }
        if (bundle.containsKey("top")) {
            this.top = bundle.getFloat("top");
        }
        if (bundle.containsKey("width")) {
            this.width = bundle.getFloat("width");
        }
        if (bundle.containsKey("height")) {
            this.height = bundle.getFloat("height");
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putFloat("left", this.left);
        bundle.putFloat("top", this.top);
        bundle.putFloat("width", this.width);
        bundle.putFloat("height", this.height);
        return bundle;
    }
}
