// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import java.util.Arrays;
import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.VersionedParcel;

public final class SliceParcelizer
{
    public static Slice read(final VersionedParcel versionedParcel) {
        final Slice slice = new Slice();
        slice.mSpec = versionedParcel.readVersionedParcelable(slice.mSpec, 1);
        slice.mItems = versionedParcel.readArray(slice.mItems, 2);
        slice.mHints = versionedParcel.readArray(slice.mHints, 3);
        slice.mUri = versionedParcel.readString(slice.mUri, 4);
        slice.onPostParceling();
        return slice;
    }
    
    public static void write(final Slice slice, final VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, false);
        slice.onPreParceling(versionedParcel.isStream());
        final SliceSpec mSpec = slice.mSpec;
        if (mSpec != null) {
            versionedParcel.writeVersionedParcelable(mSpec, 1);
        }
        if (!Arrays.equals(Slice.NO_ITEMS, slice.mItems)) {
            versionedParcel.writeArray(slice.mItems, 2);
        }
        if (!Arrays.equals(Slice.NO_HINTS, slice.mHints)) {
            versionedParcel.writeArray(slice.mHints, 3);
        }
        final String mUri = slice.mUri;
        if (mUri != null) {
            versionedParcel.writeString(mUri, 4);
        }
    }
}
