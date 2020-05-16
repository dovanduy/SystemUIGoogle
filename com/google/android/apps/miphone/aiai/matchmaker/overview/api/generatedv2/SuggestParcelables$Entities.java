// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class SuggestParcelables$Entities
{
    @Nullable
    private SuggestParcelables$DebugInfo debugInfo;
    @Nullable
    private List<SuggestParcelables$Entity> entities;
    @Nullable
    private SuggestParcelables$ExtrasInfo extrasInfo;
    @Nullable
    private String id;
    @Nullable
    private String opaquePayload;
    @Nullable
    private SuggestParcelables$SetupInfo setupInfo;
    @Nullable
    private SuggestParcelables$Stats stats;
    private boolean success;
    
    private SuggestParcelables$Entities() {
    }
    
    private SuggestParcelables$Entities(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$Entities create() {
        return new SuggestParcelables$Entities();
    }
    
    public static SuggestParcelables$Entities create(final Bundle bundle) {
        return new SuggestParcelables$Entities(bundle);
    }
    
    private void readFromBundle(Bundle bundle) {
        if (bundle.containsKey("id")) {
            this.id = bundle.getString("id");
        }
        if (bundle.containsKey("success")) {
            this.success = bundle.getBoolean("success");
        }
        if (bundle.containsKey("entities")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("entities");
            if (parcelableArrayList == null) {
                this.entities = null;
            }
            else {
                this.entities = new ArrayList<SuggestParcelables$Entity>(parcelableArrayList.size());
                for (final Bundle bundle2 : parcelableArrayList) {
                    if (bundle2 == null) {
                        this.entities.add(null);
                    }
                    else {
                        this.entities.add(SuggestParcelables$Entity.create(bundle2));
                    }
                }
            }
        }
        if (bundle.containsKey("stats")) {
            final Bundle bundle3 = bundle.getBundle("stats");
            if (bundle3 == null) {
                this.stats = null;
            }
            else {
                this.stats = SuggestParcelables$Stats.create(bundle3);
            }
        }
        if (bundle.containsKey("debugInfo")) {
            final Bundle bundle4 = bundle.getBundle("debugInfo");
            if (bundle4 == null) {
                this.debugInfo = null;
            }
            else {
                this.debugInfo = SuggestParcelables$DebugInfo.create(bundle4);
            }
        }
        if (bundle.containsKey("extrasInfo")) {
            final Bundle bundle5 = bundle.getBundle("extrasInfo");
            if (bundle5 == null) {
                this.extrasInfo = null;
            }
            else {
                this.extrasInfo = SuggestParcelables$ExtrasInfo.create(bundle5);
            }
        }
        if (bundle.containsKey("opaquePayload")) {
            this.opaquePayload = bundle.getString("opaquePayload");
        }
        if (bundle.containsKey("setupInfo")) {
            bundle = bundle.getBundle("setupInfo");
            if (bundle == null) {
                this.setupInfo = null;
            }
            else {
                this.setupInfo = SuggestParcelables$SetupInfo.create(bundle);
            }
        }
    }
    
    @Nullable
    public List<SuggestParcelables$Entity> getEntities() {
        return this.entities;
    }
    
    @Nullable
    public SuggestParcelables$ExtrasInfo getExtrasInfo() {
        return this.extrasInfo;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("id", this.id);
        bundle.putBoolean("success", this.success);
        if (this.entities == null) {
            bundle.putParcelableArrayList("entities", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.entities.size());
            for (final SuggestParcelables$Entity suggestParcelables$Entity : this.entities) {
                if (suggestParcelables$Entity == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$Entity.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("entities", (ArrayList)list);
        }
        final SuggestParcelables$Stats stats = this.stats;
        if (stats == null) {
            bundle.putBundle("stats", (Bundle)null);
        }
        else {
            bundle.putBundle("stats", stats.writeToBundle());
        }
        final SuggestParcelables$DebugInfo debugInfo = this.debugInfo;
        if (debugInfo == null) {
            bundle.putBundle("debugInfo", (Bundle)null);
        }
        else {
            bundle.putBundle("debugInfo", debugInfo.writeToBundle());
        }
        final SuggestParcelables$ExtrasInfo extrasInfo = this.extrasInfo;
        if (extrasInfo == null) {
            bundle.putBundle("extrasInfo", (Bundle)null);
        }
        else {
            bundle.putBundle("extrasInfo", extrasInfo.writeToBundle());
        }
        bundle.putString("opaquePayload", this.opaquePayload);
        final SuggestParcelables$SetupInfo setupInfo = this.setupInfo;
        if (setupInfo == null) {
            bundle.putBundle("setupInfo", (Bundle)null);
        }
        else {
            bundle.putBundle("setupInfo", setupInfo.writeToBundle());
        }
        return bundle;
    }
}
