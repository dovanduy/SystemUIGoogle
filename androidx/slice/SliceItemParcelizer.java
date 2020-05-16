// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import androidx.versionedparcelable.VersionedParcelable;
import java.util.Arrays;
import androidx.versionedparcelable.VersionedParcel;

public final class SliceItemParcelizer
{
    public static SliceItem read(final VersionedParcel versionedParcel) {
        final SliceItem sliceItem = new SliceItem();
        sliceItem.mHints = versionedParcel.readArray(sliceItem.mHints, 1);
        sliceItem.mFormat = versionedParcel.readString(sliceItem.mFormat, 2);
        sliceItem.mSubType = versionedParcel.readString(sliceItem.mSubType, 3);
        sliceItem.mHolder = versionedParcel.readVersionedParcelable(sliceItem.mHolder, 4);
        sliceItem.onPostParceling();
        return sliceItem;
    }
    
    public static void write(final SliceItem sliceItem, final VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, true);
        sliceItem.onPreParceling(versionedParcel.isStream());
        if (!Arrays.equals(Slice.NO_HINTS, sliceItem.mHints)) {
            versionedParcel.writeArray(sliceItem.mHints, 1);
        }
        if (!"text".equals(sliceItem.mFormat)) {
            versionedParcel.writeString(sliceItem.mFormat, 2);
        }
        final String mSubType = sliceItem.mSubType;
        if (mSubType != null) {
            versionedParcel.writeString(mSubType, 3);
        }
        versionedParcel.writeVersionedParcelable(sliceItem.mHolder, 4);
    }
}
