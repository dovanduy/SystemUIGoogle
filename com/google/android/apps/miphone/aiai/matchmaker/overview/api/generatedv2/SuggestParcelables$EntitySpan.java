// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.List;

public final class SuggestParcelables$EntitySpan
{
    @Nullable
    private List<Integer> rectIndices;
    @Nullable
    private List<SuggestParcelables$ContentRect> rects;
    @Nullable
    private String selectionId;
    
    private SuggestParcelables$EntitySpan() {
    }
    
    private SuggestParcelables$EntitySpan(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static SuggestParcelables$EntitySpan create(final Bundle bundle) {
        return new SuggestParcelables$EntitySpan(bundle);
    }
    
    private void readFromBundle(final Bundle bundle) {
        if (bundle.containsKey("rects")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("rects");
            if (parcelableArrayList == null) {
                this.rects = null;
            }
            else {
                this.rects = new ArrayList<SuggestParcelables$ContentRect>(parcelableArrayList.size());
                for (final Bundle bundle2 : parcelableArrayList) {
                    if (bundle2 == null) {
                        this.rects.add(null);
                    }
                    else {
                        this.rects.add(SuggestParcelables$ContentRect.create(bundle2));
                    }
                }
            }
        }
        if (bundle.containsKey("selectionId")) {
            this.selectionId = bundle.getString("selectionId");
        }
        if (bundle.containsKey("rectIndices")) {
            this.rectIndices = (List<Integer>)bundle.getIntegerArrayList("rectIndices");
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        if (this.rects == null) {
            bundle.putParcelableArrayList("rects", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.rects.size());
            for (final SuggestParcelables$ContentRect suggestParcelables$ContentRect : this.rects) {
                if (suggestParcelables$ContentRect == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$ContentRect.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("rects", (ArrayList)list);
        }
        bundle.putString("selectionId", this.selectionId);
        if (this.rectIndices == null) {
            bundle.putIntegerArrayList("rectIndices", (ArrayList)null);
        }
        else {
            bundle.putIntegerArrayList("rectIndices", new ArrayList((Collection<? extends E>)this.rectIndices));
        }
        return bundle;
    }
}
