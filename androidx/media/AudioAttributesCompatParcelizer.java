// 
// Decompiled by Procyon v0.5.36
// 

package androidx.media;

import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.VersionedParcel;

public class AudioAttributesCompatParcelizer
{
    public static AudioAttributesCompat read(final VersionedParcel versionedParcel) {
        final AudioAttributesCompat audioAttributesCompat = new AudioAttributesCompat();
        audioAttributesCompat.mImpl = versionedParcel.readVersionedParcelable(audioAttributesCompat.mImpl, 1);
        return audioAttributesCompat;
    }
    
    public static void write(final AudioAttributesCompat audioAttributesCompat, final VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(false, false);
        versionedParcel.writeVersionedParcelable(audioAttributesCompat.mImpl, 1);
    }
}
