// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.List;

public final class ContentParcelables$ContentGroup
{
    @Nullable
    private List<SuggestParcelables$ContentRect> contentRects;
    @Nullable
    private List<ContentParcelables$Selection> selections;
    
    private ContentParcelables$ContentGroup() {
    }
    
    private ContentParcelables$ContentGroup(final Bundle bundle) {
        this.readFromBundle(bundle);
    }
    
    public static ContentParcelables$ContentGroup create(final Bundle bundle) {
        return new ContentParcelables$ContentGroup(bundle);
    }
    
    private void readFromBundle(Bundle bundle) {
        if (bundle.containsKey("contentRects")) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("contentRects");
            if (parcelableArrayList == null) {
                this.contentRects = null;
            }
            else {
                this.contentRects = new ArrayList<SuggestParcelables$ContentRect>(parcelableArrayList.size());
                for (final Bundle bundle2 : parcelableArrayList) {
                    if (bundle2 == null) {
                        this.contentRects.add(null);
                    }
                    else {
                        this.contentRects.add(SuggestParcelables$ContentRect.create(bundle2));
                    }
                }
            }
        }
        if (bundle.containsKey("selections")) {
            final ArrayList parcelableArrayList2 = bundle.getParcelableArrayList("selections");
            if (parcelableArrayList2 == null) {
                this.selections = null;
            }
            else {
                this.selections = new ArrayList<ContentParcelables$Selection>(parcelableArrayList2.size());
                final Iterator<Bundle> iterator2 = parcelableArrayList2.iterator();
                while (iterator2.hasNext()) {
                    bundle = iterator2.next();
                    if (bundle == null) {
                        this.selections.add(null);
                    }
                    else {
                        this.selections.add(ContentParcelables$Selection.create(bundle));
                    }
                }
            }
        }
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        if (this.contentRects == null) {
            bundle.putParcelableArrayList("contentRects", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.contentRects.size());
            for (final SuggestParcelables$ContentRect suggestParcelables$ContentRect : this.contentRects) {
                if (suggestParcelables$ContentRect == null) {
                    list.add(null);
                }
                else {
                    list.add(suggestParcelables$ContentRect.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("contentRects", (ArrayList)list);
        }
        if (this.selections == null) {
            bundle.putParcelableArrayList("selections", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list2 = new ArrayList<Bundle>(this.selections.size());
            for (final ContentParcelables$Selection contentParcelables$Selection : this.selections) {
                if (contentParcelables$Selection == null) {
                    list2.add(null);
                }
                else {
                    list2.add(contentParcelables$Selection.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("selections", (ArrayList)list2);
        }
        return bundle;
    }
}
