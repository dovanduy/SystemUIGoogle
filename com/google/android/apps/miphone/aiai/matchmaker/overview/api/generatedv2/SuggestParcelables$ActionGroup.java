// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.List;

public final class SuggestParcelables$ActionGroup
{
    @Nullable
    private List<SuggestParcelables$Action> alternateActions;
    @Nullable
    private String displayName;
    @Nullable
    private String id;
    private boolean isHiddenAction;
    @Nullable
    private SuggestParcelables$Action mainAction;
    @Nullable
    private String opaquePayload;
    
    private SuggestParcelables$ActionGroup() {
    }
    
    private SuggestParcelables$ActionGroup(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$ActionGroup create(final Bundle bundle) {
        return new SuggestParcelables$ActionGroup(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("id")) {
            this.id = bundle.getString("id");
        }
        if (bundle.containsKey("displayName")) {
            this.displayName = bundle.getString("displayName");
        }
        if (bundle.containsKey("mainAction")) {
            final Bundle bundle2 = bundle.getBundle("mainAction");
            if (bundle2 == null) {
                this.mainAction = null;
            }
            else {
                this.mainAction = SuggestParcelables$Action.create(bundle2);
            }
        }
        if (bundle.containsKey("alternateActions")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("alternateActions");
            if (parcelableArrayList == null) {
                this.alternateActions = null;
            }
            else {
                this.alternateActions = new ArrayList<SuggestParcelables$Action>(parcelableArrayList.size());
                for (final Bundle bundle3 : parcelableArrayList) {
                    if (bundle3 == null) {
                        this.alternateActions.add(null);
                    }
                    else {
                        this.alternateActions.add(SuggestParcelables$Action.create(bundle3));
                    }
                }
            }
        }
        if (bundle.containsKey("isHiddenAction")) {
            this.isHiddenAction = bundle.getBoolean("isHiddenAction");
        }
        if (bundle.containsKey("opaquePayload")) {
            this.opaquePayload = bundle.getString("opaquePayload");
        }
    }
    
    @Nullable
    public SuggestParcelables$Action getMainAction() {
        return this.mainAction;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("id", this.id);
        bundle.putString("displayName", this.displayName);
        final SuggestParcelables$Action mainAction = this.mainAction;
        if (mainAction == null) {
            bundle.putBundle("mainAction", (Bundle)null);
        }
        else {
            bundle.putBundle("mainAction", mainAction.writeToBundle());
        }
        if (this.alternateActions == null) {
            bundle.putParcelableArrayList("alternateActions", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.alternateActions.size());
            for (final SuggestParcelables$Action suggestParcelables$Action : this.alternateActions) {
                if (suggestParcelables$Action == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$Action.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("alternateActions", (ArrayList)list);
        }
        bundle.putBoolean("isHiddenAction", this.isHiddenAction);
        bundle.putString("opaquePayload", this.opaquePayload);
        return bundle;
    }
}
