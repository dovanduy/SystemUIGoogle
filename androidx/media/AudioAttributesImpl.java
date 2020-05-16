// 
// Decompiled by Procyon v0.5.36
// 

package androidx.media;

import androidx.versionedparcelable.VersionedParcelable;

public interface AudioAttributesImpl extends VersionedParcelable
{
    public interface Builder
    {
        AudioAttributesImpl build();
        
        Builder setLegacyStreamType(final int p0);
    }
}
