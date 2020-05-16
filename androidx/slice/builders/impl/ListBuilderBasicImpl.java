// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.builders.impl;

import java.util.Iterator;
import androidx.slice.SliceItem;
import androidx.slice.SliceSpec;
import androidx.slice.Slice;
import androidx.slice.builders.SliceAction;
import java.util.Set;
import androidx.core.graphics.drawable.IconCompat;

public class ListBuilderBasicImpl extends TemplateBuilderImpl implements ListBuilder
{
    private IconCompat mIconCompat;
    boolean mIsError;
    private Set<String> mKeywords;
    private SliceAction mSliceAction;
    private CharSequence mSubtitle;
    private CharSequence mTitle;
    
    public ListBuilderBasicImpl(final Slice.Builder builder, final SliceSpec sliceSpec) {
        super(builder, sliceSpec);
    }
    
    @Override
    public void addRow(final androidx.slice.builders.ListBuilder.RowBuilder rowBuilder) {
        if (this.mTitle == null && rowBuilder.getTitle() != null) {
            this.mTitle = rowBuilder.getTitle();
        }
        if (this.mSubtitle == null && rowBuilder.getSubtitle() != null) {
            this.mSubtitle = rowBuilder.getSubtitle();
        }
        if (this.mSliceAction == null && rowBuilder.getPrimaryAction() != null) {
            this.mSliceAction = rowBuilder.getPrimaryAction();
        }
        if (this.mSliceAction == null && rowBuilder.getTitleAction() != null) {
            this.mSliceAction = rowBuilder.getTitleAction();
        }
        if (this.mIconCompat == null && rowBuilder.getTitleIcon() != null) {
            this.mIconCompat = rowBuilder.getTitleIcon();
        }
    }
    
    @Override
    public void apply(final Slice.Builder builder) {
        if (this.mIsError) {
            builder.addHints("error");
        }
        if (this.mKeywords != null) {
            final Slice.Builder builder2 = new Slice.Builder(this.getBuilder());
            final Iterator<String> iterator = this.mKeywords.iterator();
            while (iterator.hasNext()) {
                builder2.addText(iterator.next(), null, new String[0]);
            }
            builder2.addHints("keywords");
            builder.addSubSlice(builder2.build());
        }
        final Slice.Builder primaryAction = new Slice.Builder(this.getBuilder());
        final SliceAction mSliceAction = this.mSliceAction;
        if (mSliceAction != null) {
            if (this.mTitle == null && mSliceAction.getTitle() != null) {
                this.mTitle = this.mSliceAction.getTitle();
            }
            if (this.mIconCompat == null && this.mSliceAction.getIcon() != null) {
                this.mIconCompat = this.mSliceAction.getIcon();
            }
            this.mSliceAction.setPrimaryAction(primaryAction);
        }
        final CharSequence mTitle = this.mTitle;
        if (mTitle != null) {
            primaryAction.addItem(new SliceItem(mTitle, "text", null, new String[] { "title" }));
        }
        final CharSequence mSubtitle = this.mSubtitle;
        if (mSubtitle != null) {
            primaryAction.addItem(new SliceItem(mSubtitle, "text", null, new String[0]));
        }
        final IconCompat mIconCompat = this.mIconCompat;
        if (mIconCompat != null) {
            builder.addIcon(mIconCompat, null, "title");
        }
        builder.addSubSlice(primaryAction.build());
    }
    
    @Override
    public void setHeader(final androidx.slice.builders.ListBuilder.HeaderBuilder headerBuilder) {
        if (headerBuilder.getTitle() != null) {
            this.mTitle = headerBuilder.getTitle();
        }
        if (headerBuilder.getSubtitle() != null) {
            this.mSubtitle = headerBuilder.getSubtitle();
        }
        if (headerBuilder.getPrimaryAction() != null) {
            this.mSliceAction = headerBuilder.getPrimaryAction();
        }
    }
    
    @Override
    public void setTtl(long n) {
        final long n2 = -1L;
        if (n == -1L) {
            n = n2;
        }
        else {
            n += this.getClock().currentTimeMillis();
        }
        this.getBuilder().addTimestamp(n, "millis", "ttl");
    }
}
