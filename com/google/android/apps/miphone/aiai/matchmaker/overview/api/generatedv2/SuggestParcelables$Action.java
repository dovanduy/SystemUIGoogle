// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class SuggestParcelables$Action
{
    @Nullable
    private String dEPRECATEDIconBitmapId;
    @Nullable
    private SuggestParcelables$IntentInfo dEPRECATEDIntentInfo;
    @Nullable
    private String displayName;
    @Nullable
    private String fullDisplayName;
    @Nullable
    private String id;
    @Nullable
    private String opaquePayload;
    @Nullable
    private SuggestParcelables$IntentInfo proxiedIntentInfo;
    
    private SuggestParcelables$Action() {
    }
    
    private SuggestParcelables$Action(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$Action create(final Bundle bundle) {
        return new SuggestParcelables$Action(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("id")) {
            this.id = bundle.getString("id");
        }
        if (bundle.containsKey("displayName")) {
            this.displayName = bundle.getString("displayName");
        }
        if (bundle.containsKey("dEPRECATEDIconBitmapId")) {
            this.dEPRECATEDIconBitmapId = bundle.getString("dEPRECATEDIconBitmapId");
        }
        if (bundle.containsKey("fullDisplayName")) {
            this.fullDisplayName = bundle.getString("fullDisplayName");
        }
        if (bundle.containsKey("dEPRECATEDIntentInfo")) {
            final Bundle bundle2 = bundle.getBundle("dEPRECATEDIntentInfo");
            if (bundle2 == null) {
                this.dEPRECATEDIntentInfo = null;
            }
            else {
                this.dEPRECATEDIntentInfo = SuggestParcelables$IntentInfo.create(bundle2);
            }
        }
        if (bundle.containsKey("proxiedIntentInfo")) {
            final Bundle bundle3 = bundle.getBundle("proxiedIntentInfo");
            if (bundle3 == null) {
                this.proxiedIntentInfo = null;
            }
            else {
                this.proxiedIntentInfo = SuggestParcelables$IntentInfo.create(bundle3);
            }
        }
        if (bundle.containsKey("opaquePayload")) {
            this.opaquePayload = bundle.getString("opaquePayload");
        }
    }
    
    @Nullable
    public String getDisplayName() {
        return this.displayName;
    }
    
    @Nullable
    public String getFullDisplayName() {
        return this.fullDisplayName;
    }
    
    @Nullable
    public String getId() {
        return this.id;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("id", this.id);
        bundle.putString("displayName", this.displayName);
        bundle.putString("dEPRECATEDIconBitmapId", this.dEPRECATEDIconBitmapId);
        bundle.putString("fullDisplayName", this.fullDisplayName);
        final SuggestParcelables$IntentInfo deprecatedIntentInfo = this.dEPRECATEDIntentInfo;
        if (deprecatedIntentInfo == null) {
            bundle.putBundle("dEPRECATEDIntentInfo", (Bundle)null);
        }
        else {
            bundle.putBundle("dEPRECATEDIntentInfo", deprecatedIntentInfo.writeToBundle());
        }
        final SuggestParcelables$IntentInfo proxiedIntentInfo = this.proxiedIntentInfo;
        if (proxiedIntentInfo == null) {
            bundle.putBundle("proxiedIntentInfo", (Bundle)null);
        }
        else {
            bundle.putBundle("proxiedIntentInfo", proxiedIntentInfo.writeToBundle());
        }
        bundle.putString("opaquePayload", this.opaquePayload);
        return bundle;
    }
}
