// 
// Decompiled by Procyon v0.5.36
// 

package androidx.media;

import android.media.AudioAttributes$Builder;
import android.media.AudioAttributes;

public class AudioAttributesImplApi21 implements AudioAttributesImpl
{
    public AudioAttributes mAudioAttributes;
    public int mLegacyStreamType;
    
    public AudioAttributesImplApi21() {
        this.mLegacyStreamType = -1;
    }
    
    AudioAttributesImplApi21(final AudioAttributes audioAttributes) {
        this(audioAttributes, -1);
    }
    
    AudioAttributesImplApi21(final AudioAttributes mAudioAttributes, final int mLegacyStreamType) {
        this.mLegacyStreamType = -1;
        this.mAudioAttributes = mAudioAttributes;
        this.mLegacyStreamType = mLegacyStreamType;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof AudioAttributesImplApi21 && this.mAudioAttributes.equals((Object)((AudioAttributesImplApi21)o).mAudioAttributes);
    }
    
    @Override
    public int hashCode() {
        return this.mAudioAttributes.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AudioAttributesCompat: audioattributes=");
        sb.append(this.mAudioAttributes);
        return sb.toString();
    }
    
    static class Builder implements AudioAttributesImpl.Builder
    {
        final AudioAttributes$Builder mFwkBuilder;
        
        Builder() {
            this.mFwkBuilder = new AudioAttributes$Builder();
        }
        
        @Override
        public AudioAttributesImpl build() {
            return new AudioAttributesImplApi21(this.mFwkBuilder.build());
        }
        
        public Builder setLegacyStreamType(final int legacyStreamType) {
            this.mFwkBuilder.setLegacyStreamType(legacyStreamType);
            return this;
        }
    }
}
