// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.os.Parcelable;
import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.VersionedParcel;

public final class SliceItemHolderParcelizer
{
    private static SliceItemHolder.SliceItemPool sBuilder;
    
    static {
        SliceItemHolderParcelizer.sBuilder = new SliceItemHolder.SliceItemPool();
    }
    
    public static SliceItemHolder read(final VersionedParcel versionedParcel) {
        final SliceItemHolder value = SliceItemHolderParcelizer.sBuilder.get();
        value.mVersionedParcelable = versionedParcel.readVersionedParcelable(value.mVersionedParcelable, 1);
        value.mParcelable = versionedParcel.readParcelable(value.mParcelable, 2);
        value.mStr = versionedParcel.readString(value.mStr, 3);
        value.mInt = versionedParcel.readInt(value.mInt, 4);
        value.mLong = versionedParcel.readLong(value.mLong, 5);
        return value;
    }
    
    public static void write(final SliceItemHolder sliceItemHolder, final VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, true);
        final VersionedParcelable mVersionedParcelable = sliceItemHolder.mVersionedParcelable;
        if (mVersionedParcelable != null) {
            versionedParcel.writeVersionedParcelable(mVersionedParcelable, 1);
        }
        final Parcelable mParcelable = sliceItemHolder.mParcelable;
        if (mParcelable != null) {
            versionedParcel.writeParcelable(mParcelable, 2);
        }
        final String mStr = sliceItemHolder.mStr;
        if (mStr != null) {
            versionedParcel.writeString(mStr, 3);
        }
        final int mInt = sliceItemHolder.mInt;
        if (mInt != 0) {
            versionedParcel.writeInt(mInt, 4);
        }
        final long mLong = sliceItemHolder.mLong;
        if (0L != mLong) {
            versionedParcel.writeLong(mLong, 5);
        }
    }
}
