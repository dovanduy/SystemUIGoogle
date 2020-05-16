// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.builders;

import androidx.slice.SystemClock;
import androidx.slice.Clock;
import androidx.slice.SliceManager;
import java.util.Collection;
import java.util.ArrayList;
import androidx.slice.SliceProvider;
import android.net.Uri;
import androidx.slice.SliceSpec;
import java.util.List;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import android.content.Context;
import androidx.slice.Slice;

public abstract class TemplateSliceBuilder
{
    private final Slice.Builder mBuilder;
    private final Context mContext;
    private final TemplateBuilderImpl mImpl;
    private List<SliceSpec> mSpecs;
    
    public TemplateSliceBuilder(final Context mContext, final Uri uri) {
        this.mBuilder = new Slice.Builder(uri);
        this.mContext = mContext;
        this.mSpecs = this.getSpecs(uri);
        final TemplateBuilderImpl selectImpl = this.selectImpl();
        this.mImpl = selectImpl;
        if (selectImpl != null) {
            this.setImpl(selectImpl);
            return;
        }
        throw new IllegalArgumentException("No valid specs found");
    }
    
    private List<SliceSpec> getSpecs(final Uri uri) {
        if (SliceProvider.getCurrentSpecs() != null) {
            return new ArrayList<SliceSpec>(SliceProvider.getCurrentSpecs());
        }
        return new ArrayList<SliceSpec>(SliceManager.getInstance(this.mContext).getPinnedSpecs(uri));
    }
    
    protected boolean checkCompatible(final SliceSpec sliceSpec) {
        for (int size = this.mSpecs.size(), i = 0; i < size; ++i) {
            if (this.mSpecs.get(i).canRender(sliceSpec)) {
                return true;
            }
        }
        return false;
    }
    
    protected Slice.Builder getBuilder() {
        return this.mBuilder;
    }
    
    protected Clock getClock() {
        if (SliceProvider.getClock() != null) {
            return SliceProvider.getClock();
        }
        return new SystemClock();
    }
    
    protected abstract TemplateBuilderImpl selectImpl();
    
    abstract void setImpl(final TemplateBuilderImpl p0);
}
