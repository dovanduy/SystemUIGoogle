// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import java.util.Iterator;
import java.util.Arrays;
import android.content.IntentFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import android.os.Bundle;

public final class MediaRouteSelector
{
    public static final MediaRouteSelector EMPTY;
    private final Bundle mBundle;
    List<String> mControlCategories;
    
    static {
        EMPTY = new MediaRouteSelector(new Bundle(), null);
    }
    
    MediaRouteSelector(final Bundle mBundle, final List<String> mControlCategories) {
        this.mBundle = mBundle;
        this.mControlCategories = mControlCategories;
    }
    
    public static MediaRouteSelector fromBundle(final Bundle bundle) {
        MediaRouteSelector mediaRouteSelector = null;
        if (bundle != null) {
            mediaRouteSelector = new MediaRouteSelector(bundle, null);
        }
        return mediaRouteSelector;
    }
    
    public Bundle asBundle() {
        return this.mBundle;
    }
    
    public boolean contains(final MediaRouteSelector mediaRouteSelector) {
        if (mediaRouteSelector != null) {
            this.ensureControlCategories();
            mediaRouteSelector.ensureControlCategories();
            return this.mControlCategories.containsAll(mediaRouteSelector.mControlCategories);
        }
        return false;
    }
    
    void ensureControlCategories() {
        if (this.mControlCategories == null) {
            final ArrayList stringArrayList = this.mBundle.getStringArrayList("controlCategories");
            this.mControlCategories = (List<String>)stringArrayList;
            if (stringArrayList == null || stringArrayList.isEmpty()) {
                this.mControlCategories = Collections.emptyList();
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof MediaRouteSelector) {
            final MediaRouteSelector mediaRouteSelector = (MediaRouteSelector)o;
            this.ensureControlCategories();
            mediaRouteSelector.ensureControlCategories();
            return this.mControlCategories.equals(mediaRouteSelector.mControlCategories);
        }
        return false;
    }
    
    public List<String> getControlCategories() {
        this.ensureControlCategories();
        return this.mControlCategories;
    }
    
    @Override
    public int hashCode() {
        this.ensureControlCategories();
        return this.mControlCategories.hashCode();
    }
    
    public boolean isEmpty() {
        this.ensureControlCategories();
        return this.mControlCategories.isEmpty();
    }
    
    public boolean isValid() {
        this.ensureControlCategories();
        return !this.mControlCategories.contains(null);
    }
    
    public boolean matchesControlFilters(final List<IntentFilter> list) {
        if (list != null) {
            this.ensureControlCategories();
            final int size = this.mControlCategories.size();
            if (size != 0) {
                for (int size2 = list.size(), i = 0; i < size2; ++i) {
                    final IntentFilter intentFilter = list.get(i);
                    if (intentFilter != null) {
                        for (int j = 0; j < size; ++j) {
                            if (intentFilter.hasCategory((String)this.mControlCategories.get(j))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MediaRouteSelector{ ");
        sb.append("controlCategories=");
        sb.append(Arrays.toString(this.getControlCategories().toArray()));
        sb.append(" }");
        return sb.toString();
    }
    
    public static final class Builder
    {
        private ArrayList<String> mControlCategories;
        
        public Builder() {
        }
        
        public Builder(final MediaRouteSelector mediaRouteSelector) {
            if (mediaRouteSelector != null) {
                mediaRouteSelector.ensureControlCategories();
                if (!mediaRouteSelector.mControlCategories.isEmpty()) {
                    this.mControlCategories = new ArrayList<String>(mediaRouteSelector.mControlCategories);
                }
                return;
            }
            throw new IllegalArgumentException("selector must not be null");
        }
        
        public Builder addControlCategories(final Collection<String> collection) {
            if (collection != null) {
                if (!collection.isEmpty()) {
                    final Iterator<String> iterator = collection.iterator();
                    while (iterator.hasNext()) {
                        this.addControlCategory(iterator.next());
                    }
                }
                return this;
            }
            throw new IllegalArgumentException("categories must not be null");
        }
        
        public Builder addControlCategory(final String s) {
            if (s != null) {
                if (this.mControlCategories == null) {
                    this.mControlCategories = new ArrayList<String>();
                }
                if (!this.mControlCategories.contains(s)) {
                    this.mControlCategories.add(s);
                }
                return this;
            }
            throw new IllegalArgumentException("category must not be null");
        }
        
        public Builder addSelector(final MediaRouteSelector mediaRouteSelector) {
            if (mediaRouteSelector != null) {
                this.addControlCategories(mediaRouteSelector.getControlCategories());
                return this;
            }
            throw new IllegalArgumentException("selector must not be null");
        }
        
        public MediaRouteSelector build() {
            if (this.mControlCategories == null) {
                return MediaRouteSelector.EMPTY;
            }
            final Bundle bundle = new Bundle();
            bundle.putStringArrayList("controlCategories", (ArrayList)this.mControlCategories);
            return new MediaRouteSelector(bundle, this.mControlCategories);
        }
    }
}
