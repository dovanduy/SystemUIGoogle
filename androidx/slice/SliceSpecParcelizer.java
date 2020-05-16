// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import androidx.versionedparcelable.VersionedParcel;

public final class SliceSpecParcelizer
{
    public static SliceSpec read(final VersionedParcel versionedParcel) {
        final SliceSpec sliceSpec = new SliceSpec();
        sliceSpec.mType = versionedParcel.readString(sliceSpec.mType, 1);
        sliceSpec.mRevision = versionedParcel.readInt(sliceSpec.mRevision, 2);
        return sliceSpec;
    }
    
    public static void write(final SliceSpec sliceSpec, final VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, false);
        versionedParcel.writeString(sliceSpec.mType, 1);
        final int mRevision = sliceSpec.mRevision;
        if (mRevision == 0) {
            versionedParcel.writeInt(mRevision, 2);
        }
    }
}
