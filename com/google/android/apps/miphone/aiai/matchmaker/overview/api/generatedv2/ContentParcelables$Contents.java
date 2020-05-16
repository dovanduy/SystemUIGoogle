// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.List;

public final class ContentParcelables$Contents
{
    @Nullable
    private List<ContentParcelables$ContentGroup> contentGroups;
    @Nullable
    private SuggestParcelables$DebugInfo debugInfo;
    @Nullable
    private String id;
    @Nullable
    private String opaquePayload;
    private long screenSessionId;
    @Nullable
    private SuggestParcelables$SetupInfo setupInfo;
    @Nullable
    private SuggestParcelables$Stats stats;
    
    private ContentParcelables$Contents() {
    }
    
    private ContentParcelables$Contents(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static ContentParcelables$Contents create() {
        return new ContentParcelables$Contents();
    }
    
    public static ContentParcelables$Contents create(final Bundle bundle) {
        return new ContentParcelables$Contents(bundle);
    }
    
    private void readFromBundle(Bundle bundle) {
        if (bundle.containsKey("id")) {
            this.id = bundle.getString("id");
        }
        if (bundle.containsKey("screenSessionId")) {
            this.screenSessionId = bundle.getLong("screenSessionId");
        }
        if (bundle.containsKey("contentGroups")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("contentGroups");
            if (parcelableArrayList == null) {
                this.contentGroups = null;
            }
            else {
                this.contentGroups = new ArrayList<ContentParcelables$ContentGroup>(parcelableArrayList.size());
                for (final Bundle bundle2 : parcelableArrayList) {
                    if (bundle2 == null) {
                        this.contentGroups.add(null);
                    }
                    else {
                        this.contentGroups.add(ContentParcelables$ContentGroup.create(bundle2));
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
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("id", this.id);
        bundle.putLong("screenSessionId", this.screenSessionId);
        if (this.contentGroups == null) {
            bundle.putParcelableArrayList("contentGroups", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.contentGroups.size());
            for (final ContentParcelables$ContentGroup contentParcelables$ContentGroup : this.contentGroups) {
                if (contentParcelables$ContentGroup == null) {
                    list.add(null);
                }
                else {
                    list.add(contentParcelables$ContentGroup.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("contentGroups", (ArrayList)list);
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
