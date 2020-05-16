// 
// Decompiled by Procyon v0.5.36
// 

package androidx.media;

import android.media.AudioAttributes;
import android.os.Build$VERSION;
import android.util.SparseIntArray;
import androidx.versionedparcelable.VersionedParcelable;

public class AudioAttributesCompat implements VersionedParcelable
{
    private static final SparseIntArray SUPPRESSIBLE_USAGES;
    static boolean sForceLegacyBehavior;
    public AudioAttributesImpl mImpl;
    
    static {
        (SUPPRESSIBLE_USAGES = new SparseIntArray()).put(5, 1);
        AudioAttributesCompat.SUPPRESSIBLE_USAGES.put(6, 2);
        AudioAttributesCompat.SUPPRESSIBLE_USAGES.put(7, 2);
        AudioAttributesCompat.SUPPRESSIBLE_USAGES.put(8, 1);
        AudioAttributesCompat.SUPPRESSIBLE_USAGES.put(9, 1);
        AudioAttributesCompat.SUPPRESSIBLE_USAGES.put(10, 1);
    }
    
    public AudioAttributesCompat() {
    }
    
    AudioAttributesCompat(final AudioAttributesImpl mImpl) {
        this.mImpl = mImpl;
    }
    
    static int toVolumeStreamType(final boolean b, int n, final int i) {
        final int n2 = 1;
        if ((n & 0x1) == 0x1) {
            if (b) {
                n = n2;
            }
            else {
                n = 7;
            }
            return n;
        }
        final int n3 = 0;
        final int n4 = 0;
        if ((n & 0x4) == 0x4) {
            if (b) {
                n = n4;
            }
            else {
                n = 6;
            }
            return n;
        }
        n = n3;
        switch (i) {
            default: {
                if (!b) {
                    return 3;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Unknown usage value ");
                sb.append(i);
                sb.append(" in audio attributes");
                throw new IllegalArgumentException(sb.toString());
            }
            case 13: {
                return 1;
            }
            case 11: {
                return 10;
            }
            case 6: {
                return 2;
            }
            case 5:
            case 7:
            case 8:
            case 9:
            case 10: {
                return 5;
            }
            case 4: {
                return 4;
            }
            case 3: {
                if (b) {
                    n = n3;
                    return n;
                }
                n = 8;
                return n;
            }
            case 2: {
                return n;
            }
            case 0:
            case 1:
            case 12:
            case 14:
            case 16: {
                return 3;
            }
        }
    }
    
    static String usageToString(final int i) {
        switch (i) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("unknown usage ");
                sb.append(i);
                return sb.toString();
            }
            case 16: {
                return "USAGE_ASSISTANT";
            }
            case 14: {
                return "USAGE_GAME";
            }
            case 13: {
                return "USAGE_ASSISTANCE_SONIFICATION";
            }
            case 12: {
                return "USAGE_ASSISTANCE_NAVIGATION_GUIDANCE";
            }
            case 11: {
                return "USAGE_ASSISTANCE_ACCESSIBILITY";
            }
            case 10: {
                return "USAGE_NOTIFICATION_EVENT";
            }
            case 9: {
                return "USAGE_NOTIFICATION_COMMUNICATION_DELAYED";
            }
            case 8: {
                return "USAGE_NOTIFICATION_COMMUNICATION_INSTANT";
            }
            case 7: {
                return "USAGE_NOTIFICATION_COMMUNICATION_REQUEST";
            }
            case 6: {
                return "USAGE_NOTIFICATION_RINGTONE";
            }
            case 5: {
                return "USAGE_NOTIFICATION";
            }
            case 4: {
                return "USAGE_ALARM";
            }
            case 3: {
                return "USAGE_VOICE_COMMUNICATION_SIGNALLING";
            }
            case 2: {
                return "USAGE_VOICE_COMMUNICATION";
            }
            case 1: {
                return "USAGE_MEDIA";
            }
            case 0: {
                return "USAGE_UNKNOWN";
            }
        }
    }
    
    public static AudioAttributesCompat wrap(final Object o) {
        if (AudioAttributesCompat.sForceLegacyBehavior) {
            return null;
        }
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 26) {
            return new AudioAttributesCompat(new AudioAttributesImplApi26((AudioAttributes)o));
        }
        if (sdk_INT >= 21) {
            return new AudioAttributesCompat(new AudioAttributesImplApi21((AudioAttributes)o));
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof AudioAttributesCompat;
        boolean b2 = false;
        if (!b) {
            return false;
        }
        final AudioAttributesCompat audioAttributesCompat = (AudioAttributesCompat)o;
        final AudioAttributesImpl mImpl = this.mImpl;
        if (mImpl == null) {
            if (audioAttributesCompat.mImpl == null) {
                b2 = true;
            }
            return b2;
        }
        return mImpl.equals(audioAttributesCompat.mImpl);
    }
    
    @Override
    public int hashCode() {
        return this.mImpl.hashCode();
    }
    
    @Override
    public String toString() {
        return this.mImpl.toString();
    }
    
    public static class Builder
    {
        final AudioAttributesImpl.Builder mBuilderImpl;
        
        public Builder() {
            if (AudioAttributesCompat.sForceLegacyBehavior) {
                this.mBuilderImpl = new AudioAttributesImplBase.Builder();
            }
            else {
                final int sdk_INT = Build$VERSION.SDK_INT;
                if (sdk_INT >= 26) {
                    this.mBuilderImpl = new AudioAttributesImplApi26.Builder();
                }
                else if (sdk_INT >= 21) {
                    this.mBuilderImpl = new AudioAttributesImplApi21.Builder();
                }
                else {
                    this.mBuilderImpl = new AudioAttributesImplBase.Builder();
                }
            }
        }
        
        public AudioAttributesCompat build() {
            return new AudioAttributesCompat(this.mBuilderImpl.build());
        }
        
        public Builder setLegacyStreamType(final int legacyStreamType) {
            this.mBuilderImpl.setLegacyStreamType(legacyStreamType);
            return this;
        }
    }
}
