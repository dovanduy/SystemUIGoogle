// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class SuggestParcelables$ContentRect
{
    private int contentGroupIndex;
    private SuggestParcelables$ContentType contentType;
    @Nullable
    private String contentUri;
    private int lineId;
    @Nullable
    private SuggestParcelables$OnScreenRect rect;
    @Nullable
    private String text;
    
    private SuggestParcelables$ContentRect() {
    }
    
    private SuggestParcelables$ContentRect(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$ContentRect create(final Bundle bundle) {
        return new SuggestParcelables$ContentRect(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("rect")) {
            final Bundle bundle2 = bundle.getBundle("rect");
            if (bundle2 == null) {
                this.rect = null;
            }
            else {
                this.rect = SuggestParcelables$OnScreenRect.create(bundle2);
            }
        }
        if (bundle.containsKey("text")) {
            this.text = bundle.getString("text");
        }
        if (bundle.containsKey("contentType")) {
            final Bundle bundle3 = bundle.getBundle("contentType");
            if (bundle3 == null) {
                this.contentType = null;
            }
            else {
                this.contentType = SuggestParcelables$ContentType.create(bundle3);
            }
            final SuggestParcelables$ContentType contentType = this.contentType;
        }
        if (bundle.containsKey("lineId")) {
            this.lineId = bundle.getInt("lineId");
        }
        if (bundle.containsKey("contentUri")) {
            this.contentUri = bundle.getString("contentUri");
        }
        if (bundle.containsKey("contentGroupIndex")) {
            this.contentGroupIndex = bundle.getInt("contentGroupIndex");
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        final SuggestParcelables$OnScreenRect rect = this.rect;
        if (rect == null) {
            bundle.putBundle("rect", (Bundle)null);
        }
        else {
            bundle.putBundle("rect", rect.writeToBundle());
        }
        bundle.putString("text", this.text);
        final SuggestParcelables$ContentType contentType = this.contentType;
        if (contentType == null) {
            bundle.putBundle("contentType", (Bundle)null);
        }
        else {
            bundle.putBundle("contentType", contentType.writeToBundle());
        }
        bundle.putInt("lineId", this.lineId);
        bundle.putString("contentUri", this.contentUri);
        bundle.putInt("contentGroupIndex", this.contentGroupIndex);
        return bundle;
    }
}
