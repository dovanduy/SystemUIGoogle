// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class SuggestParcelables$IntentParam
{
    @Nullable
    private String contentUri;
    private float floatValue;
    private int intValue;
    @Nullable
    private SuggestParcelables$IntentInfo intentValue;
    private long longValue;
    @Nullable
    private String name;
    @Nullable
    private String strValue;
    private SuggestParcelables$IntentParamType type;
    
    private SuggestParcelables$IntentParam() {
    }
    
    private SuggestParcelables$IntentParam(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$IntentParam create(final Bundle bundle) {
        return new SuggestParcelables$IntentParam(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("name")) {
            this.name = bundle.getString("name");
        }
        if (bundle.containsKey("type")) {
            final Bundle bundle2 = bundle.getBundle("type");
            if (bundle2 == null) {
                this.type = null;
            }
            else {
                this.type = SuggestParcelables$IntentParamType.create(bundle2);
            }
            final SuggestParcelables$IntentParamType type = this.type;
        }
        if (bundle.containsKey("strValue")) {
            this.strValue = bundle.getString("strValue");
        }
        if (bundle.containsKey("intValue")) {
            this.intValue = bundle.getInt("intValue");
        }
        if (bundle.containsKey("floatValue")) {
            this.floatValue = bundle.getFloat("floatValue");
        }
        if (bundle.containsKey("longValue")) {
            this.longValue = bundle.getLong("longValue");
        }
        if (bundle.containsKey("intentValue")) {
            final Bundle bundle3 = bundle.getBundle("intentValue");
            if (bundle3 == null) {
                this.intentValue = null;
            }
            else {
                this.intentValue = SuggestParcelables$IntentInfo.create(bundle3);
            }
        }
        if (bundle.containsKey("contentUri")) {
            this.contentUri = bundle.getString("contentUri");
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("name", this.name);
        final SuggestParcelables$IntentParamType type = this.type;
        if (type == null) {
            bundle.putBundle("type", (Bundle)null);
        }
        else {
            bundle.putBundle("type", type.writeToBundle());
        }
        bundle.putString("strValue", this.strValue);
        bundle.putInt("intValue", this.intValue);
        bundle.putFloat("floatValue", this.floatValue);
        bundle.putLong("longValue", this.longValue);
        final SuggestParcelables$IntentInfo intentValue = this.intentValue;
        if (intentValue == null) {
            bundle.putBundle("intentValue", (Bundle)null);
        }
        else {
            bundle.putBundle("intentValue", intentValue.writeToBundle());
        }
        bundle.putString("contentUri", this.contentUri);
        return bundle;
    }
}
