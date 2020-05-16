// 
// Decompiled by Procyon v0.5.36
// 

package androidx.media;

import android.os.Parcelable;
import android.media.AudioAttributes;
import androidx.versionedparcelable.VersionedParcel;

public class AudioAttributesImplApi26Parcelizer
{
    public static AudioAttributesImplApi26 read(final VersionedParcel versionedParcel) {
        final AudioAttributesImplApi26 audioAttributesImplApi26 = new AudioAttributesImplApi26();
        audioAttributesImplApi26.mAudioAttributes = versionedParcel.readParcelable(audioAttributesImplApi26.mAudioAttributes, 1);
        audioAttributesImplApi26.mLegacyStreamType = versionedParcel.readInt(audioAttributesImplApi26.mLegacyStreamType, 2);
        return audioAttributesImplApi26;
    }
    
    public static void write(final AudioAttributesImplApi26 audioAttributesImplApi26, final VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(false, false);
        versionedParcel.writeParcelable((Parcelable)audioAttributesImplApi26.mAudioAttributes, 1);
        versionedParcel.writeInt(audioAttributesImplApi26.mLegacyStreamType, 2);
    }
}
