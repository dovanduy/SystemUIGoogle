// 
// Decompiled by Procyon v0.5.36
// 

package androidx.media;

import android.media.AudioAttributes;

public class AudioAttributesImplApi26 extends AudioAttributesImplApi21
{
    public AudioAttributesImplApi26() {
    }
    
    AudioAttributesImplApi26(final AudioAttributes audioAttributes) {
        super(audioAttributes, -1);
    }
    
    static class Builder extends AudioAttributesImplApi21.Builder
    {
        @Override
        public AudioAttributesImpl build() {
            return new AudioAttributesImplApi26(super.mFwkBuilder.build());
        }
    }
}
