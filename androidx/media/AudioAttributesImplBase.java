// 
// Decompiled by Procyon v0.5.36
// 

package androidx.media;

import java.util.Arrays;

public class AudioAttributesImplBase implements AudioAttributesImpl
{
    public int mContentType;
    public int mFlags;
    public int mLegacyStream;
    public int mUsage;
    
    public AudioAttributesImplBase() {
        this.mUsage = 0;
        this.mContentType = 0;
        this.mFlags = 0;
        this.mLegacyStream = -1;
    }
    
    AudioAttributesImplBase(final int mContentType, final int mFlags, final int mUsage, final int mLegacyStream) {
        this.mUsage = 0;
        this.mContentType = 0;
        this.mFlags = 0;
        this.mLegacyStream = -1;
        this.mContentType = mContentType;
        this.mFlags = mFlags;
        this.mUsage = mUsage;
        this.mLegacyStream = mLegacyStream;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof AudioAttributesImplBase;
        final boolean b2 = false;
        if (!b) {
            return false;
        }
        final AudioAttributesImplBase audioAttributesImplBase = (AudioAttributesImplBase)o;
        boolean b3 = b2;
        if (this.mContentType == audioAttributesImplBase.getContentType()) {
            b3 = b2;
            if (this.mFlags == audioAttributesImplBase.getFlags()) {
                b3 = b2;
                if (this.mUsage == audioAttributesImplBase.getUsage()) {
                    b3 = b2;
                    if (this.mLegacyStream == audioAttributesImplBase.mLegacyStream) {
                        b3 = true;
                    }
                }
            }
        }
        return b3;
    }
    
    public int getContentType() {
        return this.mContentType;
    }
    
    public int getFlags() {
        final int mFlags = this.mFlags;
        final int legacyStreamType = this.getLegacyStreamType();
        int n;
        if (legacyStreamType == 6) {
            n = (mFlags | 0x4);
        }
        else {
            n = mFlags;
            if (legacyStreamType == 7) {
                n = (mFlags | 0x1);
            }
        }
        return n & 0x111;
    }
    
    public int getLegacyStreamType() {
        final int mLegacyStream = this.mLegacyStream;
        if (mLegacyStream != -1) {
            return mLegacyStream;
        }
        return AudioAttributesCompat.toVolumeStreamType(false, this.mFlags, this.mUsage);
    }
    
    public int getUsage() {
        return this.mUsage;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { this.mContentType, this.mFlags, this.mUsage, this.mLegacyStream });
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AudioAttributesCompat:");
        if (this.mLegacyStream != -1) {
            sb.append(" stream=");
            sb.append(this.mLegacyStream);
            sb.append(" derived");
        }
        sb.append(" usage=");
        sb.append(AudioAttributesCompat.usageToString(this.mUsage));
        sb.append(" content=");
        sb.append(this.mContentType);
        sb.append(" flags=0x");
        sb.append(Integer.toHexString(this.mFlags).toUpperCase());
        return sb.toString();
    }
    
    static class Builder implements AudioAttributesImpl.Builder
    {
        private int mContentType;
        private int mFlags;
        private int mLegacyStream;
        private int mUsage;
        
        Builder() {
            this.mUsage = 0;
            this.mContentType = 0;
            this.mFlags = 0;
            this.mLegacyStream = -1;
        }
        
        @Override
        public AudioAttributesImpl build() {
            return new AudioAttributesImplBase(this.mContentType, this.mFlags, this.mUsage, this.mLegacyStream);
        }
        
        public Builder setLegacyStreamType(final int mLegacyStream) {
            if (mLegacyStream != 10) {
                this.mLegacyStream = mLegacyStream;
                return this;
            }
            throw new IllegalArgumentException("STREAM_ACCESSIBILITY is not a legacy stream type that was used for audio playback");
        }
    }
}
