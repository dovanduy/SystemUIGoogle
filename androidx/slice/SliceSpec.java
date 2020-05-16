// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import androidx.versionedparcelable.VersionedParcelable;

public final class SliceSpec implements VersionedParcelable
{
    int mRevision;
    String mType;
    
    public SliceSpec() {
        this.mRevision = 1;
    }
    
    public SliceSpec(final String mType, final int mRevision) {
        this.mRevision = 1;
        this.mType = mType;
        this.mRevision = mRevision;
    }
    
    public boolean canRender(final SliceSpec sliceSpec) {
        final boolean equals = this.mType.equals(sliceSpec.mType);
        boolean b = false;
        if (!equals) {
            return false;
        }
        if (this.mRevision >= sliceSpec.mRevision) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof SliceSpec;
        final boolean b2 = false;
        if (!b) {
            return false;
        }
        final SliceSpec sliceSpec = (SliceSpec)o;
        boolean b3 = b2;
        if (this.mType.equals(sliceSpec.mType)) {
            b3 = b2;
            if (this.mRevision == sliceSpec.mRevision) {
                b3 = true;
            }
        }
        return b3;
    }
    
    public int getRevision() {
        return this.mRevision;
    }
    
    public String getType() {
        return this.mType;
    }
    
    @Override
    public int hashCode() {
        return this.mType.hashCode() + this.mRevision;
    }
    
    @Override
    public String toString() {
        return String.format("SliceSpec{%s,%d}", this.mType, this.mRevision);
    }
}
