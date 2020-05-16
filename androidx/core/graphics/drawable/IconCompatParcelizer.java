// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics.drawable;

import android.os.Parcelable;
import android.content.res.ColorStateList;
import androidx.versionedparcelable.VersionedParcel;

public class IconCompatParcelizer
{
    public static IconCompat read(final VersionedParcel versionedParcel) {
        final IconCompat iconCompat = new IconCompat();
        iconCompat.mType = versionedParcel.readInt(iconCompat.mType, 1);
        iconCompat.mData = versionedParcel.readByteArray(iconCompat.mData, 2);
        iconCompat.mParcelable = versionedParcel.readParcelable(iconCompat.mParcelable, 3);
        iconCompat.mInt1 = versionedParcel.readInt(iconCompat.mInt1, 4);
        iconCompat.mInt2 = versionedParcel.readInt(iconCompat.mInt2, 5);
        iconCompat.mTintList = versionedParcel.readParcelable(iconCompat.mTintList, 6);
        iconCompat.mTintModeStr = versionedParcel.readString(iconCompat.mTintModeStr, 7);
        iconCompat.onPostParceling();
        return iconCompat;
    }
    
    public static void write(final IconCompat iconCompat, final VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, true);
        iconCompat.onPreParceling(versionedParcel.isStream());
        final int mType = iconCompat.mType;
        if (-1 != mType) {
            versionedParcel.writeInt(mType, 1);
        }
        final byte[] mData = iconCompat.mData;
        if (mData != null) {
            versionedParcel.writeByteArray(mData, 2);
        }
        final Parcelable mParcelable = iconCompat.mParcelable;
        if (mParcelable != null) {
            versionedParcel.writeParcelable(mParcelable, 3);
        }
        final int mInt1 = iconCompat.mInt1;
        if (mInt1 != 0) {
            versionedParcel.writeInt(mInt1, 4);
        }
        final int mInt2 = iconCompat.mInt2;
        if (mInt2 != 0) {
            versionedParcel.writeInt(mInt2, 5);
        }
        final ColorStateList mTintList = iconCompat.mTintList;
        if (mTintList != null) {
            versionedParcel.writeParcelable((Parcelable)mTintList, 6);
        }
        final String mTintModeStr = iconCompat.mTintModeStr;
        if (mTintModeStr != null) {
            versionedParcel.writeString(mTintModeStr, 7);
        }
    }
}
