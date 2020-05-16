// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.builders.impl;

import java.util.ArrayList;
import androidx.slice.SystemClock;
import androidx.slice.SliceSpec;
import androidx.slice.Slice;
import androidx.slice.Clock;

public abstract class TemplateBuilderImpl
{
    private Clock mClock;
    private Slice.Builder mSliceBuilder;
    private final SliceSpec mSpec;
    
    protected TemplateBuilderImpl(final Slice.Builder builder, final SliceSpec sliceSpec) {
        this(builder, sliceSpec, new SystemClock());
    }
    
    protected TemplateBuilderImpl(final Slice.Builder mSliceBuilder, final SliceSpec mSpec, final Clock mClock) {
        this.mSliceBuilder = mSliceBuilder;
        this.mSpec = mSpec;
        this.mClock = mClock;
    }
    
    public abstract void apply(final Slice.Builder p0);
    
    public Slice build() {
        this.mSliceBuilder.setSpec(this.mSpec);
        this.apply(this.mSliceBuilder);
        return this.mSliceBuilder.build();
    }
    
    public Slice.Builder createChildBuilder() {
        return new Slice.Builder(this.mSliceBuilder);
    }
    
    public Slice.Builder getBuilder() {
        return this.mSliceBuilder;
    }
    
    public Clock getClock() {
        return this.mClock;
    }
    
    protected ArrayList<String> parseImageMode(final int n, final boolean b) {
        final ArrayList<String> list = new ArrayList<String>();
        if (n != 0) {
            list.add("no_tint");
        }
        if (n == 2 || n == 4) {
            list.add("large");
        }
        if (n == 3 || n == 4) {
            list.add("raw");
        }
        if (b) {
            list.add("partial");
        }
        return list;
    }
    
    protected void setBuilder(final Slice.Builder mSliceBuilder) {
        this.mSliceBuilder = mSliceBuilder;
    }
}
