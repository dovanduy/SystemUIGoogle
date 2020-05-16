// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class SuggestParcelables$IntentInfo
{
    @Nullable
    private String action;
    @Nullable
    private String className;
    private int flags;
    @Nullable
    private List<SuggestParcelables$IntentParam> intentParams;
    private SuggestParcelables$IntentType intentType;
    @Nullable
    private String mimeType;
    @Nullable
    private String packageName;
    @Nullable
    private String uri;
    
    private SuggestParcelables$IntentInfo(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$IntentInfo create(final Bundle bundle) {
        return new SuggestParcelables$IntentInfo(bundle);
    }
    
    private void readFromBundle(Bundle bundle) {
        if (bundle.containsKey("intentParams")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("intentParams");
            if (parcelableArrayList == null) {
                this.intentParams = null;
            }
            else {
                this.intentParams = new ArrayList<SuggestParcelables$IntentParam>(parcelableArrayList.size());
                for (final Bundle bundle2 : parcelableArrayList) {
                    if (bundle2 == null) {
                        this.intentParams.add(null);
                    }
                    else {
                        this.intentParams.add(SuggestParcelables$IntentParam.create(bundle2));
                    }
                }
            }
        }
        if (bundle.containsKey("packageName")) {
            this.packageName = bundle.getString("packageName");
        }
        if (bundle.containsKey("className")) {
            this.className = bundle.getString("className");
        }
        if (bundle.containsKey("action")) {
            this.action = bundle.getString("action");
        }
        if (bundle.containsKey("uri")) {
            this.uri = bundle.getString("uri");
        }
        if (bundle.containsKey("mimeType")) {
            this.mimeType = bundle.getString("mimeType");
        }
        if (bundle.containsKey("flags")) {
            this.flags = bundle.getInt("flags");
        }
        if (bundle.containsKey("intentType")) {
            bundle = bundle.getBundle("intentType");
            if (bundle == null) {
                this.intentType = null;
            }
            else {
                this.intentType = SuggestParcelables$IntentType.create(bundle);
            }
            final SuggestParcelables$IntentType intentType = this.intentType;
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        if (this.intentParams == null) {
            bundle.putParcelableArrayList("intentParams", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.intentParams.size());
            for (final SuggestParcelables$IntentParam suggestParcelables$IntentParam : this.intentParams) {
                if (suggestParcelables$IntentParam == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$IntentParam.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("intentParams", (ArrayList)list);
        }
        bundle.putString("packageName", this.packageName);
        bundle.putString("className", this.className);
        bundle.putString("action", this.action);
        bundle.putString("uri", this.uri);
        bundle.putString("mimeType", this.mimeType);
        bundle.putInt("flags", this.flags);
        final SuggestParcelables$IntentType intentType = this.intentType;
        if (intentType == null) {
            bundle.putBundle("intentType", (Bundle)null);
        }
        else {
            bundle.putBundle("intentType", intentType.writeToBundle());
        }
        return bundle;
    }
}
