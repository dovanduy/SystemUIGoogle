// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import java.util.Arrays;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.Collections;
import java.util.List;

public final class MediaRouteProviderDescriptor
{
    final List<MediaRouteDescriptor> mRoutes;
    final boolean mSupportsDynamicGroupRoute;
    
    MediaRouteProviderDescriptor(final List<MediaRouteDescriptor> list, final boolean mSupportsDynamicGroupRoute) {
        List<MediaRouteDescriptor> emptyList = list;
        if (list == null) {
            emptyList = Collections.emptyList();
        }
        this.mRoutes = emptyList;
        this.mSupportsDynamicGroupRoute = mSupportsDynamicGroupRoute;
    }
    
    public static MediaRouteProviderDescriptor fromBundle(final Bundle bundle) {
        final List<MediaRouteDescriptor> list = null;
        if (bundle == null) {
            return null;
        }
        final ArrayList parcelableArrayList = bundle.getParcelableArrayList("routes");
        List<MediaRouteDescriptor> list2 = list;
        if (parcelableArrayList != null) {
            list2 = list;
            if (!parcelableArrayList.isEmpty()) {
                final int size = parcelableArrayList.size();
                list2 = new ArrayList<MediaRouteDescriptor>(size);
                for (int i = 0; i < size; ++i) {
                    list2.add(MediaRouteDescriptor.fromBundle(parcelableArrayList.get(i)));
                }
            }
        }
        return new MediaRouteProviderDescriptor(list2, bundle.getBoolean("supportsDynamicGroupRoute", false));
    }
    
    public List<MediaRouteDescriptor> getRoutes() {
        return this.mRoutes;
    }
    
    public boolean isValid() {
        for (int size = this.getRoutes().size(), i = 0; i < size; ++i) {
            final MediaRouteDescriptor mediaRouteDescriptor = this.mRoutes.get(i);
            if (mediaRouteDescriptor == null || !mediaRouteDescriptor.isValid()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean supportsDynamicGroupRoute() {
        return this.mSupportsDynamicGroupRoute;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MediaRouteProviderDescriptor{ ");
        sb.append("routes=");
        sb.append(Arrays.toString(this.getRoutes().toArray()));
        sb.append(", isValid=");
        sb.append(this.isValid());
        sb.append(" }");
        return sb.toString();
    }
    
    public static final class Builder
    {
        private List<MediaRouteDescriptor> mRoutes;
        private boolean mSupportsDynamicGroupRoute;
        
        public Builder() {
            this.mSupportsDynamicGroupRoute = false;
        }
        
        public Builder addRoute(final MediaRouteDescriptor mediaRouteDescriptor) {
            if (mediaRouteDescriptor != null) {
                final List<MediaRouteDescriptor> mRoutes = this.mRoutes;
                if (mRoutes == null) {
                    this.mRoutes = new ArrayList<MediaRouteDescriptor>();
                }
                else if (mRoutes.contains(mediaRouteDescriptor)) {
                    throw new IllegalArgumentException("route descriptor already added");
                }
                this.mRoutes.add(mediaRouteDescriptor);
                return this;
            }
            throw new IllegalArgumentException("route must not be null");
        }
        
        public MediaRouteProviderDescriptor build() {
            return new MediaRouteProviderDescriptor(this.mRoutes, this.mSupportsDynamicGroupRoute);
        }
    }
}
