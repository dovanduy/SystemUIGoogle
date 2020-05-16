// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import android.media.MediaDescription$Builder;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.os.Build$VERSION;
import android.os.Parcel;
import android.net.Uri;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.media.MediaDescription;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
public final class MediaDescriptionCompat implements Parcelable
{
    public static final Parcelable$Creator<MediaDescriptionCompat> CREATOR;
    private final CharSequence mDescription;
    private MediaDescription mDescriptionFwk;
    private final Bundle mExtras;
    private final Bitmap mIcon;
    private final Uri mIconUri;
    private final String mMediaId;
    private final Uri mMediaUri;
    private final CharSequence mSubtitle;
    private final CharSequence mTitle;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<MediaDescriptionCompat>() {
            public MediaDescriptionCompat createFromParcel(final Parcel parcel) {
                if (Build$VERSION.SDK_INT < 21) {
                    return new MediaDescriptionCompat(parcel);
                }
                return MediaDescriptionCompat.fromMediaDescription(MediaDescription.CREATOR.createFromParcel(parcel));
            }
            
            public MediaDescriptionCompat[] newArray(final int n) {
                return new MediaDescriptionCompat[n];
            }
        };
    }
    
    MediaDescriptionCompat(final Parcel parcel) {
        this.mMediaId = parcel.readString();
        this.mTitle = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mSubtitle = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mDescription = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        final ClassLoader classLoader = MediaDescriptionCompat.class.getClassLoader();
        this.mIcon = (Bitmap)parcel.readParcelable(classLoader);
        this.mIconUri = (Uri)parcel.readParcelable(classLoader);
        this.mExtras = parcel.readBundle(classLoader);
        this.mMediaUri = (Uri)parcel.readParcelable(classLoader);
    }
    
    MediaDescriptionCompat(final String mMediaId, final CharSequence mTitle, final CharSequence mSubtitle, final CharSequence mDescription, final Bitmap mIcon, final Uri mIconUri, final Bundle mExtras, final Uri mMediaUri) {
        this.mMediaId = mMediaId;
        this.mTitle = mTitle;
        this.mSubtitle = mSubtitle;
        this.mDescription = mDescription;
        this.mIcon = mIcon;
        this.mIconUri = mIconUri;
        this.mExtras = mExtras;
        this.mMediaUri = mMediaUri;
    }
    
    public static MediaDescriptionCompat fromMediaDescription(final Object o) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final Bundle bundle = null;
        if (o != null && sdk_INT >= 21) {
            final Builder builder = new Builder();
            final MediaDescription mDescriptionFwk = (MediaDescription)o;
            builder.setMediaId(mDescriptionFwk.getMediaId());
            builder.setTitle(mDescriptionFwk.getTitle());
            builder.setSubtitle(mDescriptionFwk.getSubtitle());
            builder.setDescription(mDescriptionFwk.getDescription());
            builder.setIconBitmap(mDescriptionFwk.getIconBitmap());
            builder.setIconUri(mDescriptionFwk.getIconUri());
            final Bundle extras = mDescriptionFwk.getExtras();
            Bundle unparcelWithClassLoader;
            if ((unparcelWithClassLoader = extras) != null) {
                unparcelWithClassLoader = MediaSessionCompat.unparcelWithClassLoader(extras);
            }
            Uri mediaUri;
            if (unparcelWithClassLoader != null) {
                mediaUri = (Uri)unparcelWithClassLoader.getParcelable("android.support.v4.media.description.MEDIA_URI");
            }
            else {
                mediaUri = null;
            }
            if (mediaUri != null) {
                if (unparcelWithClassLoader.containsKey("android.support.v4.media.description.NULL_BUNDLE_FLAG") && unparcelWithClassLoader.size() == 2) {
                    unparcelWithClassLoader = bundle;
                }
                else {
                    unparcelWithClassLoader.remove("android.support.v4.media.description.MEDIA_URI");
                    unparcelWithClassLoader.remove("android.support.v4.media.description.NULL_BUNDLE_FLAG");
                }
            }
            builder.setExtras(unparcelWithClassLoader);
            if (mediaUri != null) {
                builder.setMediaUri(mediaUri);
            }
            else if (sdk_INT >= 23) {
                builder.setMediaUri(mDescriptionFwk.getMediaUri());
            }
            final MediaDescriptionCompat build = builder.build();
            build.mDescriptionFwk = mDescriptionFwk;
            return build;
        }
        return null;
    }
    
    public int describeContents() {
        return 0;
    }
    
    public Bitmap getIconBitmap() {
        return this.mIcon;
    }
    
    public Uri getIconUri() {
        return this.mIconUri;
    }
    
    public Object getMediaDescription() {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (this.mDescriptionFwk == null && sdk_INT >= 21) {
            final MediaDescription$Builder mediaDescription$Builder = new MediaDescription$Builder();
            mediaDescription$Builder.setMediaId(this.mMediaId);
            mediaDescription$Builder.setTitle(this.mTitle);
            mediaDescription$Builder.setSubtitle(this.mSubtitle);
            mediaDescription$Builder.setDescription(this.mDescription);
            mediaDescription$Builder.setIconBitmap(this.mIcon);
            mediaDescription$Builder.setIconUri(this.mIconUri);
            Bundle mExtras;
            final Bundle bundle = mExtras = this.mExtras;
            if (sdk_INT < 23) {
                mExtras = bundle;
                if (this.mMediaUri != null) {
                    if ((mExtras = bundle) == null) {
                        mExtras = new Bundle();
                        mExtras.putBoolean("android.support.v4.media.description.NULL_BUNDLE_FLAG", true);
                    }
                    mExtras.putParcelable("android.support.v4.media.description.MEDIA_URI", (Parcelable)this.mMediaUri);
                }
            }
            mediaDescription$Builder.setExtras(mExtras);
            if (sdk_INT >= 23) {
                mediaDescription$Builder.setMediaUri(this.mMediaUri);
            }
            return this.mDescriptionFwk = mediaDescription$Builder.build();
        }
        return this.mDescriptionFwk;
    }
    
    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }
    
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append((Object)this.mTitle);
        sb.append(", ");
        sb.append((Object)this.mSubtitle);
        sb.append(", ");
        sb.append((Object)this.mDescription);
        return sb.toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        if (Build$VERSION.SDK_INT < 21) {
            parcel.writeString(this.mMediaId);
            TextUtils.writeToParcel(this.mTitle, parcel, n);
            TextUtils.writeToParcel(this.mSubtitle, parcel, n);
            TextUtils.writeToParcel(this.mDescription, parcel, n);
            parcel.writeParcelable((Parcelable)this.mIcon, n);
            parcel.writeParcelable((Parcelable)this.mIconUri, n);
            parcel.writeBundle(this.mExtras);
            parcel.writeParcelable((Parcelable)this.mMediaUri, n);
        }
        else {
            ((MediaDescription)this.getMediaDescription()).writeToParcel(parcel, n);
        }
    }
    
    public static final class Builder
    {
        private CharSequence mDescription;
        private Bundle mExtras;
        private Bitmap mIcon;
        private Uri mIconUri;
        private String mMediaId;
        private Uri mMediaUri;
        private CharSequence mSubtitle;
        private CharSequence mTitle;
        
        public MediaDescriptionCompat build() {
            return new MediaDescriptionCompat(this.mMediaId, this.mTitle, this.mSubtitle, this.mDescription, this.mIcon, this.mIconUri, this.mExtras, this.mMediaUri);
        }
        
        public Builder setDescription(final CharSequence mDescription) {
            this.mDescription = mDescription;
            return this;
        }
        
        public Builder setExtras(final Bundle mExtras) {
            this.mExtras = mExtras;
            return this;
        }
        
        public Builder setIconBitmap(final Bitmap mIcon) {
            this.mIcon = mIcon;
            return this;
        }
        
        public Builder setIconUri(final Uri mIconUri) {
            this.mIconUri = mIconUri;
            return this;
        }
        
        public Builder setMediaId(final String mMediaId) {
            this.mMediaId = mMediaId;
            return this;
        }
        
        public Builder setMediaUri(final Uri mMediaUri) {
            this.mMediaUri = mMediaUri;
            return this;
        }
        
        public Builder setSubtitle(final CharSequence mSubtitle) {
            this.mSubtitle = mSubtitle;
            return this;
        }
        
        public Builder setTitle(final CharSequence mTitle) {
            this.mTitle = mTitle;
            return this;
        }
    }
}
