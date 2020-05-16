// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class SuggestParcelables$SetupInfo
{
    private SuggestParcelables$ErrorCode errorCode;
    @Nullable
    private String errorMesssage;
    @Nullable
    private List<SuggestParcelables$Flag> setupFlags;
    
    private SuggestParcelables$SetupInfo(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$SetupInfo create(final Bundle bundle) {
        return new SuggestParcelables$SetupInfo(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("errorCode")) {
            final Bundle bundle2 = bundle.getBundle("errorCode");
            if (bundle2 == null) {
                this.errorCode = null;
            }
            else {
                this.errorCode = SuggestParcelables$ErrorCode.create(bundle2);
            }
            final SuggestParcelables$ErrorCode errorCode = this.errorCode;
        }
        if (bundle.containsKey("errorMesssage")) {
            this.errorMesssage = bundle.getString("errorMesssage");
        }
        if (bundle.containsKey("setupFlags")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("setupFlags");
            if (parcelableArrayList == null) {
                this.setupFlags = null;
            }
            else {
                this.setupFlags = new ArrayList<SuggestParcelables$Flag>(parcelableArrayList.size());
                for (final Bundle bundle3 : parcelableArrayList) {
                    if (bundle3 == null) {
                        this.setupFlags.add(null);
                    }
                    else {
                        this.setupFlags.add(SuggestParcelables$Flag.create(bundle3));
                    }
                }
            }
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        final SuggestParcelables$ErrorCode errorCode = this.errorCode;
        if (errorCode == null) {
            bundle.putBundle("errorCode", (Bundle)null);
        }
        else {
            bundle.putBundle("errorCode", errorCode.writeToBundle());
        }
        bundle.putString("errorMesssage", this.errorMesssage);
        if (this.setupFlags == null) {
            bundle.putParcelableArrayList("setupFlags", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.setupFlags.size());
            for (final SuggestParcelables$Flag suggestParcelables$Flag : this.setupFlags) {
                if (suggestParcelables$Flag == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$Flag.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("setupFlags", (ArrayList)list);
        }
        return bundle;
    }
}
