// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.graphics.Bitmap;
import android.os.Build$VERSION;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.Parcel;
import android.media.MediaMetadata;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
public final class MediaMetadataCompat implements Parcelable
{
    public static final Parcelable$Creator<MediaMetadataCompat> CREATOR;
    static final ArrayMap<String, Integer> METADATA_KEYS_TYPE;
    private static final String[] PREFERRED_BITMAP_ORDER;
    private static final String[] PREFERRED_DESCRIPTION_ORDER;
    private static final String[] PREFERRED_URI_ORDER;
    final Bundle mBundle;
    private MediaDescriptionCompat mDescription;
    private MediaMetadata mMetadataFwk;
    
    static {
        final ArrayMap<String, Integer> arrayMap = (ArrayMap<String, Integer>)(METADATA_KEYS_TYPE = new ArrayMap<String, Integer>());
        final Integer value = 1;
        arrayMap.put("android.media.metadata.TITLE", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.ARTIST", value);
        final ArrayMap<String, Integer> metadata_KEYS_TYPE = MediaMetadataCompat.METADATA_KEYS_TYPE;
        final Integer value2 = 0;
        metadata_KEYS_TYPE.put("android.media.metadata.DURATION", value2);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.AUTHOR", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.WRITER", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.COMPOSER", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.COMPILATION", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DATE", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.YEAR", value2);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.GENRE", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.TRACK_NUMBER", value2);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.NUM_TRACKS", value2);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DISC_NUMBER", value2);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ARTIST", value);
        final ArrayMap<String, Integer> metadata_KEYS_TYPE2 = MediaMetadataCompat.METADATA_KEYS_TYPE;
        final Integer value3 = 2;
        metadata_KEYS_TYPE2.put("android.media.metadata.ART", value3);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.ART_URI", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART", value3);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART_URI", value);
        final ArrayMap<String, Integer> metadata_KEYS_TYPE3 = MediaMetadataCompat.METADATA_KEYS_TYPE;
        final Integer value4 = 3;
        metadata_KEYS_TYPE3.put("android.media.metadata.USER_RATING", value4);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.RATING", value4);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_TITLE", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_SUBTITLE", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_DESCRIPTION", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON", value3);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON_URI", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.MEDIA_ID", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.BT_FOLDER_TYPE", value2);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.MEDIA_URI", value);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.ADVERTISEMENT", value2);
        MediaMetadataCompat.METADATA_KEYS_TYPE.put("android.media.metadata.DOWNLOAD_STATUS", value2);
        PREFERRED_DESCRIPTION_ORDER = new String[] { "android.media.metadata.TITLE", "android.media.metadata.ARTIST", "android.media.metadata.ALBUM", "android.media.metadata.ALBUM_ARTIST", "android.media.metadata.WRITER", "android.media.metadata.AUTHOR", "android.media.metadata.COMPOSER" };
        PREFERRED_BITMAP_ORDER = new String[] { "android.media.metadata.DISPLAY_ICON", "android.media.metadata.ART", "android.media.metadata.ALBUM_ART" };
        PREFERRED_URI_ORDER = new String[] { "android.media.metadata.DISPLAY_ICON_URI", "android.media.metadata.ART_URI", "android.media.metadata.ALBUM_ART_URI" };
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<MediaMetadataCompat>() {
            public MediaMetadataCompat createFromParcel(final Parcel parcel) {
                return new MediaMetadataCompat(parcel);
            }
            
            public MediaMetadataCompat[] newArray(final int n) {
                return new MediaMetadataCompat[n];
            }
        };
    }
    
    MediaMetadataCompat(final Parcel parcel) {
        this.mBundle = parcel.readBundle(MediaSessionCompat.class.getClassLoader());
    }
    
    public static MediaMetadataCompat fromMediaMetadata(final Object o) {
        if (o != null && Build$VERSION.SDK_INT >= 21) {
            final Parcel obtain = Parcel.obtain();
            final MediaMetadata mMetadataFwk = (MediaMetadata)o;
            mMetadataFwk.writeToParcel(obtain, 0);
            obtain.setDataPosition(0);
            final MediaMetadataCompat mediaMetadataCompat = (MediaMetadataCompat)MediaMetadataCompat.CREATOR.createFromParcel(obtain);
            obtain.recycle();
            mediaMetadataCompat.mMetadataFwk = mMetadataFwk;
            return mediaMetadataCompat;
        }
        return null;
    }
    
    public int describeContents() {
        return 0;
    }
    
    public Bitmap getBitmap(final String s) {
        Bitmap bitmap;
        try {
            bitmap = (Bitmap)this.mBundle.getParcelable(s);
        }
        catch (Exception ex) {
            Log.w("MediaMetadata", "Failed to retrieve a key as Bitmap.", (Throwable)ex);
            bitmap = null;
        }
        return bitmap;
    }
    
    public MediaDescriptionCompat getDescription() {
        final MediaDescriptionCompat mDescription = this.mDescription;
        if (mDescription != null) {
            return mDescription;
        }
        final String string = this.getString("android.media.metadata.MEDIA_ID");
        final CharSequence[] array = new CharSequence[3];
        final CharSequence text = this.getText("android.media.metadata.DISPLAY_TITLE");
        if (!TextUtils.isEmpty(text)) {
            array[0] = text;
            array[1] = this.getText("android.media.metadata.DISPLAY_SUBTITLE");
            array[2] = this.getText("android.media.metadata.DISPLAY_DESCRIPTION");
        }
        else {
            int n;
            int n2;
            for (int i = n = 0; i < 3; i = n2) {
                final String[] preferred_DESCRIPTION_ORDER = MediaMetadataCompat.PREFERRED_DESCRIPTION_ORDER;
                if (n >= preferred_DESCRIPTION_ORDER.length) {
                    break;
                }
                final CharSequence text2 = this.getText(preferred_DESCRIPTION_ORDER[n]);
                n2 = i;
                if (!TextUtils.isEmpty(text2)) {
                    array[i] = text2;
                    n2 = i + 1;
                }
                ++n;
            }
        }
        int n3 = 0;
        Uri parse;
        Bitmap bitmap;
        while (true) {
            final String[] preferred_BITMAP_ORDER = MediaMetadataCompat.PREFERRED_BITMAP_ORDER;
            final int length = preferred_BITMAP_ORDER.length;
            parse = null;
            if (n3 >= length) {
                bitmap = null;
                break;
            }
            bitmap = this.getBitmap(preferred_BITMAP_ORDER[n3]);
            if (bitmap != null) {
                break;
            }
            ++n3;
        }
        int n4 = 0;
        Uri parse2;
        while (true) {
            final String[] preferred_URI_ORDER = MediaMetadataCompat.PREFERRED_URI_ORDER;
            if (n4 >= preferred_URI_ORDER.length) {
                parse2 = null;
                break;
            }
            final String string2 = this.getString(preferred_URI_ORDER[n4]);
            if (!TextUtils.isEmpty((CharSequence)string2)) {
                parse2 = Uri.parse(string2);
                break;
            }
            ++n4;
        }
        final String string3 = this.getString("android.media.metadata.MEDIA_URI");
        if (!TextUtils.isEmpty((CharSequence)string3)) {
            parse = Uri.parse(string3);
        }
        final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
        builder.setMediaId(string);
        builder.setTitle(array[0]);
        builder.setSubtitle(array[1]);
        builder.setDescription(array[2]);
        builder.setIconBitmap(bitmap);
        builder.setIconUri(parse2);
        builder.setMediaUri(parse);
        final Bundle extras = new Bundle();
        if (this.mBundle.containsKey("android.media.metadata.BT_FOLDER_TYPE")) {
            extras.putLong("android.media.extra.BT_FOLDER_TYPE", this.getLong("android.media.metadata.BT_FOLDER_TYPE"));
        }
        if (this.mBundle.containsKey("android.media.metadata.DOWNLOAD_STATUS")) {
            extras.putLong("android.media.extra.DOWNLOAD_STATUS", this.getLong("android.media.metadata.DOWNLOAD_STATUS"));
        }
        if (!extras.isEmpty()) {
            builder.setExtras(extras);
        }
        return this.mDescription = builder.build();
    }
    
    public long getLong(final String s) {
        return this.mBundle.getLong(s, 0L);
    }
    
    public String getString(final String s) {
        final CharSequence charSequence = this.mBundle.getCharSequence(s);
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }
    
    public CharSequence getText(final String s) {
        return this.mBundle.getCharSequence(s);
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeBundle(this.mBundle);
    }
}
