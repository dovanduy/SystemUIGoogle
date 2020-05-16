// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
public class MediaBrowserCompat$MediaItem implements Parcelable
{
    public static final Parcelable$Creator<MediaBrowserCompat$MediaItem> CREATOR;
    private final MediaDescriptionCompat mDescription;
    private final int mFlags;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<MediaBrowserCompat$MediaItem>() {
            public MediaBrowserCompat$MediaItem createFromParcel(final Parcel parcel) {
                return new MediaBrowserCompat$MediaItem(parcel);
            }
            
            public MediaBrowserCompat$MediaItem[] newArray(final int n) {
                return new MediaBrowserCompat$MediaItem[n];
            }
        };
    }
    
    MediaBrowserCompat$MediaItem(final Parcel parcel) {
        this.mFlags = parcel.readInt();
        this.mDescription = (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
    }
    
    public int describeContents() {
        return 0;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MediaItem{");
        sb.append("mFlags=");
        sb.append(this.mFlags);
        sb.append(", mDescription=");
        sb.append(this.mDescription);
        sb.append('}');
        return sb.toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.mFlags);
        this.mDescription.writeToParcel(parcel, n);
    }
}
