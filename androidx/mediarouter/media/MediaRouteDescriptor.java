// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import android.text.TextUtils;
import android.content.IntentSender;
import android.net.Uri;
import java.util.Collections;
import android.content.IntentFilter;
import java.util.List;
import android.os.Bundle;

public final class MediaRouteDescriptor
{
    final Bundle mBundle;
    List<IntentFilter> mControlFilters;
    List<String> mGroupMemberIds;
    
    MediaRouteDescriptor(final Bundle mBundle) {
        this.mBundle = mBundle;
    }
    
    public static MediaRouteDescriptor fromBundle(final Bundle bundle) {
        MediaRouteDescriptor mediaRouteDescriptor;
        if (bundle != null) {
            mediaRouteDescriptor = new MediaRouteDescriptor(bundle);
        }
        else {
            mediaRouteDescriptor = null;
        }
        return mediaRouteDescriptor;
    }
    
    public boolean canDisconnectAndKeepPlaying() {
        return this.mBundle.getBoolean("canDisconnect", false);
    }
    
    void ensureControlFilters() {
        if (this.mControlFilters == null && (this.mControlFilters = (List<IntentFilter>)this.mBundle.getParcelableArrayList("controlFilters")) == null) {
            this.mControlFilters = Collections.emptyList();
        }
    }
    
    void ensureGroupMemberIds() {
        if (this.mGroupMemberIds == null && (this.mGroupMemberIds = (List<String>)this.mBundle.getStringArrayList("groupMemberIds")) == null) {
            this.mGroupMemberIds = Collections.emptyList();
        }
    }
    
    public int getConnectionState() {
        return this.mBundle.getInt("connectionState", 0);
    }
    
    public List<IntentFilter> getControlFilters() {
        this.ensureControlFilters();
        return this.mControlFilters;
    }
    
    public String getDescription() {
        return this.mBundle.getString("status");
    }
    
    public int getDeviceType() {
        return this.mBundle.getInt("deviceType");
    }
    
    public Bundle getExtras() {
        return this.mBundle.getBundle("extras");
    }
    
    public List<String> getGroupMemberIds() {
        this.ensureGroupMemberIds();
        return this.mGroupMemberIds;
    }
    
    public Uri getIconUri() {
        final String string = this.mBundle.getString("iconUri");
        Uri parse;
        if (string == null) {
            parse = null;
        }
        else {
            parse = Uri.parse(string);
        }
        return parse;
    }
    
    public String getId() {
        return this.mBundle.getString("id");
    }
    
    public int getMaxClientVersion() {
        return this.mBundle.getInt("maxClientVersion", Integer.MAX_VALUE);
    }
    
    public int getMinClientVersion() {
        return this.mBundle.getInt("minClientVersion", 1);
    }
    
    public String getName() {
        return this.mBundle.getString("name");
    }
    
    public int getPlaybackStream() {
        return this.mBundle.getInt("playbackStream", -1);
    }
    
    public int getPlaybackType() {
        return this.mBundle.getInt("playbackType", 1);
    }
    
    public int getPresentationDisplayId() {
        return this.mBundle.getInt("presentationDisplayId", -1);
    }
    
    public IntentSender getSettingsActivity() {
        return (IntentSender)this.mBundle.getParcelable("settingsIntent");
    }
    
    public int getVolume() {
        return this.mBundle.getInt("volume");
    }
    
    public int getVolumeHandling() {
        return this.mBundle.getInt("volumeHandling", 0);
    }
    
    public int getVolumeMax() {
        return this.mBundle.getInt("volumeMax");
    }
    
    public boolean isEnabled() {
        return this.mBundle.getBoolean("enabled", true);
    }
    
    public boolean isValid() {
        this.ensureControlFilters();
        return !TextUtils.isEmpty((CharSequence)this.getId()) && !TextUtils.isEmpty((CharSequence)this.getName()) && !this.mControlFilters.contains(null);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MediaRouteDescriptor{ ");
        sb.append("id=");
        sb.append(this.getId());
        sb.append(", groupMemberIds=");
        sb.append(this.getGroupMemberIds());
        sb.append(", name=");
        sb.append(this.getName());
        sb.append(", description=");
        sb.append(this.getDescription());
        sb.append(", iconUri=");
        sb.append(this.getIconUri());
        sb.append(", isEnabled=");
        sb.append(this.isEnabled());
        sb.append(", connectionState=");
        sb.append(this.getConnectionState());
        sb.append(", controlFilters=");
        sb.append(Arrays.toString(this.getControlFilters().toArray()));
        sb.append(", playbackType=");
        sb.append(this.getPlaybackType());
        sb.append(", playbackStream=");
        sb.append(this.getPlaybackStream());
        sb.append(", deviceType=");
        sb.append(this.getDeviceType());
        sb.append(", volume=");
        sb.append(this.getVolume());
        sb.append(", volumeMax=");
        sb.append(this.getVolumeMax());
        sb.append(", volumeHandling=");
        sb.append(this.getVolumeHandling());
        sb.append(", presentationDisplayId=");
        sb.append(this.getPresentationDisplayId());
        sb.append(", extras=");
        sb.append(this.getExtras());
        sb.append(", isValid=");
        sb.append(this.isValid());
        sb.append(", minClientVersion=");
        sb.append(this.getMinClientVersion());
        sb.append(", maxClientVersion=");
        sb.append(this.getMaxClientVersion());
        sb.append(" }");
        return sb.toString();
    }
    
    public static final class Builder
    {
        private final Bundle mBundle;
        private ArrayList<IntentFilter> mControlFilters;
        private ArrayList<String> mGroupMemberIds;
        
        public Builder(final MediaRouteDescriptor mediaRouteDescriptor) {
            if (mediaRouteDescriptor != null) {
                this.mBundle = new Bundle(mediaRouteDescriptor.mBundle);
                if (!mediaRouteDescriptor.getGroupMemberIds().isEmpty()) {
                    this.mGroupMemberIds = new ArrayList<String>(mediaRouteDescriptor.getGroupMemberIds());
                }
                if (!mediaRouteDescriptor.getControlFilters().isEmpty()) {
                    this.mControlFilters = new ArrayList<IntentFilter>(mediaRouteDescriptor.mControlFilters);
                }
                return;
            }
            throw new IllegalArgumentException("descriptor must not be null");
        }
        
        public Builder(final String id, final String name) {
            this.mBundle = new Bundle();
            this.setId(id);
            this.setName(name);
        }
        
        public Builder addControlFilter(final IntentFilter intentFilter) {
            if (intentFilter != null) {
                if (this.mControlFilters == null) {
                    this.mControlFilters = new ArrayList<IntentFilter>();
                }
                if (!this.mControlFilters.contains(intentFilter)) {
                    this.mControlFilters.add(intentFilter);
                }
                return this;
            }
            throw new IllegalArgumentException("filter must not be null");
        }
        
        public Builder addControlFilters(final Collection<IntentFilter> collection) {
            if (collection != null) {
                if (!collection.isEmpty()) {
                    final Iterator<IntentFilter> iterator = collection.iterator();
                    while (iterator.hasNext()) {
                        this.addControlFilter(iterator.next());
                    }
                }
                return this;
            }
            throw new IllegalArgumentException("filters must not be null");
        }
        
        public MediaRouteDescriptor build() {
            final ArrayList<IntentFilter> mControlFilters = this.mControlFilters;
            if (mControlFilters != null) {
                this.mBundle.putParcelableArrayList("controlFilters", (ArrayList)mControlFilters);
            }
            final ArrayList<String> mGroupMemberIds = this.mGroupMemberIds;
            if (mGroupMemberIds != null) {
                this.mBundle.putStringArrayList("groupMemberIds", (ArrayList)mGroupMemberIds);
            }
            return new MediaRouteDescriptor(this.mBundle);
        }
        
        public Builder setConnectionState(final int n) {
            this.mBundle.putInt("connectionState", n);
            return this;
        }
        
        public Builder setDescription(final String s) {
            this.mBundle.putString("status", s);
            return this;
        }
        
        public Builder setDeviceType(final int n) {
            this.mBundle.putInt("deviceType", n);
            return this;
        }
        
        public Builder setEnabled(final boolean b) {
            this.mBundle.putBoolean("enabled", b);
            return this;
        }
        
        public Builder setId(final String s) {
            this.mBundle.putString("id", s);
            return this;
        }
        
        public Builder setName(final String s) {
            this.mBundle.putString("name", s);
            return this;
        }
        
        public Builder setPlaybackStream(final int n) {
            this.mBundle.putInt("playbackStream", n);
            return this;
        }
        
        public Builder setPlaybackType(final int n) {
            this.mBundle.putInt("playbackType", n);
            return this;
        }
        
        public Builder setPresentationDisplayId(final int n) {
            this.mBundle.putInt("presentationDisplayId", n);
            return this;
        }
        
        public Builder setVolume(final int n) {
            this.mBundle.putInt("volume", n);
            return this;
        }
        
        public Builder setVolumeHandling(final int n) {
            this.mBundle.putInt("volumeHandling", n);
            return this;
        }
        
        public Builder setVolumeMax(final int n) {
            this.mBundle.putInt("volumeMax", n);
            return this;
        }
    }
}
