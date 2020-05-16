// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.builders.impl;

import androidx.core.util.Pair;
import androidx.core.graphics.drawable.IconCompat;
import java.util.ArrayList;
import androidx.slice.builders.SliceAction;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceQuery;
import java.util.Iterator;
import androidx.slice.Clock;
import androidx.slice.SliceSpec;
import androidx.slice.Slice;
import java.util.List;
import java.util.Set;

public class ListBuilderImpl extends TemplateBuilderImpl implements ListBuilder
{
    private boolean mFirstRowChecked;
    private boolean mFirstRowHasText;
    private boolean mIsError;
    private boolean mIsFirstRowTypeValid;
    private Set<String> mKeywords;
    private List<Slice> mSliceActions;
    private Slice mSliceHeader;
    
    public ListBuilderImpl(final Slice.Builder builder, final SliceSpec sliceSpec, final Clock clock) {
        super(builder, sliceSpec, clock);
    }
    
    private void checkRow(final boolean mIsFirstRowTypeValid, final boolean mFirstRowHasText) {
        if (!this.mFirstRowChecked) {
            this.mFirstRowChecked = true;
            this.mIsFirstRowTypeValid = mIsFirstRowTypeValid;
            this.mFirstRowHasText = mFirstRowHasText;
        }
    }
    
    @Override
    public void addRow(final androidx.slice.builders.ListBuilder.RowBuilder rowBuilder) {
        final RowBuilderImpl rowBuilderImpl = new RowBuilderImpl(this.createChildBuilder());
        rowBuilderImpl.fillFrom(rowBuilder);
        this.checkRow(true, rowBuilderImpl.hasText());
        this.addRow(rowBuilderImpl);
    }
    
    public void addRow(final RowBuilderImpl rowBuilderImpl) {
        this.checkRow(true, rowBuilderImpl.hasText());
        rowBuilderImpl.getBuilder().addHints("list_item");
        this.getBuilder().addSubSlice(rowBuilderImpl.build());
    }
    
    @Override
    public void apply(final Slice.Builder builder) {
        builder.addLong(this.getClock().currentTimeMillis(), "millis", "last_updated");
        final Slice mSliceHeader = this.mSliceHeader;
        if (mSliceHeader != null) {
            builder.addSubSlice(mSliceHeader);
        }
        if (this.mSliceActions != null) {
            final Slice.Builder builder2 = new Slice.Builder(builder);
            for (int i = 0; i < this.mSliceActions.size(); ++i) {
                builder2.addSubSlice(this.mSliceActions.get(i));
            }
            builder2.addHints("actions");
            builder.addSubSlice(builder2.build());
        }
        if (this.mIsError) {
            builder.addHints("error");
        }
        if (this.mKeywords != null) {
            final Slice.Builder builder3 = new Slice.Builder(this.getBuilder());
            final Iterator<String> iterator = this.mKeywords.iterator();
            while (iterator.hasNext()) {
                builder3.addText(iterator.next(), null, new String[0]);
            }
            final Slice.Builder builder4 = this.getBuilder();
            builder3.addHints("keywords");
            builder4.addSubSlice(builder3.build());
        }
    }
    
    @Override
    public Slice build() {
        final Slice build = super.build();
        final SliceItem find = SliceQuery.find(build, null, "partial", null);
        boolean b = true;
        final boolean b2 = find != null;
        if (SliceQuery.find(build, "slice", "list_item", null) != null) {
            b = false;
        }
        final String[] array = { "shortcut", "title" };
        final SliceItem find2 = SliceQuery.find(build, "action", array, null);
        final List<SliceItem> all = SliceQuery.findAll(build, "slice", array, null);
        if (!b2 && !b && find2 == null && (all == null || all.isEmpty())) {
            throw new IllegalStateException("A slice requires a primary action; ensure one of your builders has called #setPrimaryAction with a valid SliceAction.");
        }
        if (this.mFirstRowChecked && !this.mIsFirstRowTypeValid) {
            throw new IllegalStateException("A slice cannot have the first row be constructed from a GridRowBuilder, consider using #setHeader.");
        }
        if (this.mFirstRowChecked && !this.mFirstRowHasText) {
            throw new IllegalStateException("A slice requires the first row to have some text.");
        }
        return build;
    }
    
    @Override
    public void setHeader(final androidx.slice.builders.ListBuilder.HeaderBuilder headerBuilder) {
        this.mIsFirstRowTypeValid = true;
        this.mFirstRowHasText = true;
        this.mFirstRowChecked = true;
        final HeaderBuilderImpl headerBuilderImpl = new HeaderBuilderImpl(this);
        headerBuilderImpl.fillFrom(headerBuilder);
        this.mSliceHeader = headerBuilderImpl.build();
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
    
    public static class HeaderBuilderImpl extends TemplateBuilderImpl
    {
        private CharSequence mContentDescr;
        private SliceAction mPrimaryAction;
        private SliceItem mSubtitleItem;
        private SliceItem mSummaryItem;
        private SliceItem mTitleItem;
        
        HeaderBuilderImpl(final ListBuilderImpl listBuilderImpl) {
            super(listBuilderImpl.createChildBuilder(), null);
        }
        
        private void setContentDescription(final CharSequence mContentDescr) {
            this.mContentDescr = mContentDescr;
        }
        
        private void setLayoutDirection(final int n) {
            this.getBuilder().addInt(n, "layout_direction", new String[0]);
        }
        
        private void setPrimaryAction(final SliceAction mPrimaryAction) {
            this.mPrimaryAction = mPrimaryAction;
        }
        
        private void setSubtitle(final CharSequence charSequence, final boolean b) {
            final SliceItem mSubtitleItem = new SliceItem(charSequence, "text", null, new String[0]);
            this.mSubtitleItem = mSubtitleItem;
            if (b) {
                mSubtitleItem.addHint("partial");
            }
        }
        
        private void setSummary(final CharSequence charSequence, final boolean b) {
            final SliceItem mSummaryItem = new SliceItem(charSequence, "text", null, new String[] { "summary" });
            this.mSummaryItem = mSummaryItem;
            if (b) {
                mSummaryItem.addHint("partial");
            }
        }
        
        private void setTitle(final CharSequence charSequence, final boolean b) {
            final SliceItem mTitleItem = new SliceItem(charSequence, "text", null, new String[] { "title" });
            this.mTitleItem = mTitleItem;
            if (b) {
                mTitleItem.addHint("partial");
            }
        }
        
        @Override
        public void apply(final Slice.Builder primaryAction) {
            final SliceItem mTitleItem = this.mTitleItem;
            if (mTitleItem != null) {
                primaryAction.addItem(mTitleItem);
            }
            final SliceItem mSubtitleItem = this.mSubtitleItem;
            if (mSubtitleItem != null) {
                primaryAction.addItem(mSubtitleItem);
            }
            final SliceItem mSummaryItem = this.mSummaryItem;
            if (mSummaryItem != null) {
                primaryAction.addItem(mSummaryItem);
            }
            final CharSequence mContentDescr = this.mContentDescr;
            if (mContentDescr != null) {
                primaryAction.addText(mContentDescr, "content_description", new String[0]);
            }
            final SliceAction mPrimaryAction = this.mPrimaryAction;
            if (mPrimaryAction != null) {
                mPrimaryAction.setPrimaryAction(primaryAction);
            }
            if (this.mSubtitleItem == null && this.mTitleItem == null) {
                throw new IllegalStateException("Header requires a title or subtitle to be set.");
            }
        }
        
        void fillFrom(final androidx.slice.builders.ListBuilder.HeaderBuilder headerBuilder) {
            if (headerBuilder.getUri() != null) {
                this.setBuilder(new Slice.Builder(headerBuilder.getUri()));
            }
            this.setPrimaryAction(headerBuilder.getPrimaryAction());
            if (headerBuilder.getLayoutDirection() != -1) {
                this.setLayoutDirection(headerBuilder.getLayoutDirection());
            }
            if (headerBuilder.getTitle() != null || headerBuilder.isTitleLoading()) {
                this.setTitle(headerBuilder.getTitle(), headerBuilder.isTitleLoading());
            }
            if (headerBuilder.getSubtitle() != null || headerBuilder.isSubtitleLoading()) {
                this.setSubtitle(headerBuilder.getSubtitle(), headerBuilder.isSubtitleLoading());
            }
            if (headerBuilder.getSummary() != null || headerBuilder.isSummaryLoading()) {
                this.setSummary(headerBuilder.getSummary(), headerBuilder.isSummaryLoading());
            }
            if (headerBuilder.getContentDescription() != null) {
                this.setContentDescription(headerBuilder.getContentDescription());
            }
        }
    }
    
    public static class RowBuilderImpl extends TemplateBuilderImpl
    {
        private CharSequence mContentDescr;
        private ArrayList<Slice> mEndItems;
        private SliceAction mPrimaryAction;
        private Slice mStartItem;
        private SliceItem mSubtitleItem;
        private SliceItem mTitleItem;
        
        RowBuilderImpl(final Slice.Builder builder) {
            super(builder, null);
            this.mEndItems = new ArrayList<Slice>();
        }
        
        private void addEndItem(final IconCompat iconCompat, final int n, final boolean b) {
            final Slice.Builder builder = new Slice.Builder(this.getBuilder());
            builder.addIcon(iconCompat, null, this.parseImageMode(n, b));
            if (b) {
                builder.addHints("partial");
            }
            this.mEndItems.add(builder.build());
        }
        
        private void addEndItem(final SliceAction sliceAction, final boolean b) {
            final Slice.Builder builder = new Slice.Builder(this.getBuilder());
            if (b) {
                builder.addHints("partial");
            }
            this.mEndItems.add(sliceAction.buildSlice(builder));
        }
        
        private void setContentDescription(final CharSequence mContentDescr) {
            this.mContentDescr = mContentDescr;
        }
        
        private void setLayoutDirection(final int n) {
            this.getBuilder().addInt(n, "layout_direction", new String[0]);
        }
        
        private void setPrimaryAction(final SliceAction mPrimaryAction) {
            this.mPrimaryAction = mPrimaryAction;
        }
        
        private void setSubtitle(final CharSequence charSequence, final boolean b) {
            final SliceItem mSubtitleItem = new SliceItem(charSequence, "text", null, new String[0]);
            this.mSubtitleItem = mSubtitleItem;
            if (b) {
                mSubtitleItem.addHint("partial");
            }
        }
        
        private void setTitle(final CharSequence charSequence, final boolean b) {
            final SliceItem mTitleItem = new SliceItem(charSequence, "text", null, new String[] { "title" });
            this.mTitleItem = mTitleItem;
            if (b) {
                mTitleItem.addHint("partial");
            }
        }
        
        private void setTitleItem(final long n) {
            final Slice.Builder builder = new Slice.Builder(this.getBuilder());
            builder.addTimestamp(n, null, new String[0]);
            builder.addHints("title");
            this.mStartItem = builder.build();
        }
        
        private void setTitleItem(final IconCompat iconCompat, final int n, final boolean b) {
            final Slice.Builder builder = new Slice.Builder(this.getBuilder());
            builder.addIcon(iconCompat, null, this.parseImageMode(n, b));
            if (b) {
                builder.addHints("partial");
            }
            builder.addHints("title");
            this.mStartItem = builder.build();
        }
        
        private void setTitleItem(final SliceAction sliceAction, final boolean b) {
            final Slice.Builder builder = new Slice.Builder(this.getBuilder());
            builder.addHints("title");
            if (b) {
                builder.addHints("partial");
            }
            this.mStartItem = sliceAction.buildSlice(builder);
        }
        
        protected void addEndItem(final long n) {
            final ArrayList<Slice> mEndItems = this.mEndItems;
            final Slice.Builder builder = new Slice.Builder(this.getBuilder());
            builder.addTimestamp(n, null, new String[0]);
            mEndItems.add(builder.build());
        }
        
        @Override
        public void apply(final Slice.Builder primaryAction) {
            final Slice mStartItem = this.mStartItem;
            if (mStartItem != null) {
                primaryAction.addSubSlice(mStartItem);
            }
            final SliceItem mTitleItem = this.mTitleItem;
            if (mTitleItem != null) {
                primaryAction.addItem(mTitleItem);
            }
            final SliceItem mSubtitleItem = this.mSubtitleItem;
            if (mSubtitleItem != null) {
                primaryAction.addItem(mSubtitleItem);
            }
            for (int i = 0; i < this.mEndItems.size(); ++i) {
                primaryAction.addSubSlice(this.mEndItems.get(i));
            }
            final CharSequence mContentDescr = this.mContentDescr;
            if (mContentDescr != null) {
                primaryAction.addText(mContentDescr, "content_description", new String[0]);
            }
            final SliceAction mPrimaryAction = this.mPrimaryAction;
            if (mPrimaryAction != null) {
                mPrimaryAction.setPrimaryAction(primaryAction);
            }
        }
        
        void fillFrom(final androidx.slice.builders.ListBuilder.RowBuilder rowBuilder) {
            if (rowBuilder.getUri() != null) {
                this.setBuilder(new Slice.Builder(rowBuilder.getUri()));
            }
            this.setPrimaryAction(rowBuilder.getPrimaryAction());
            if (rowBuilder.getLayoutDirection() != -1) {
                this.setLayoutDirection(rowBuilder.getLayoutDirection());
            }
            if (rowBuilder.getTitleAction() == null && !rowBuilder.isTitleActionLoading()) {
                if (rowBuilder.getTitleIcon() == null && !rowBuilder.isTitleItemLoading()) {
                    if (rowBuilder.getTimeStamp() != -1L) {
                        this.setTitleItem(rowBuilder.getTimeStamp());
                    }
                }
                else {
                    this.setTitleItem(rowBuilder.getTitleIcon(), rowBuilder.getTitleImageMode(), rowBuilder.isTitleItemLoading());
                }
            }
            else {
                this.setTitleItem(rowBuilder.getTitleAction(), rowBuilder.isTitleActionLoading());
            }
            if (rowBuilder.getTitle() != null || rowBuilder.isTitleLoading()) {
                this.setTitle(rowBuilder.getTitle(), rowBuilder.isTitleLoading());
            }
            if (rowBuilder.getSubtitle() != null || rowBuilder.isSubtitleLoading()) {
                this.setSubtitle(rowBuilder.getSubtitle(), rowBuilder.isSubtitleLoading());
            }
            if (rowBuilder.getContentDescription() != null) {
                this.setContentDescription(rowBuilder.getContentDescription());
            }
            final List<Object> endItems = rowBuilder.getEndItems();
            final List<Integer> endTypes = rowBuilder.getEndTypes();
            final List<Boolean> endLoads = rowBuilder.getEndLoads();
            for (int i = 0; i < endItems.size(); ++i) {
                final int intValue = endTypes.get(i);
                if (intValue != 0) {
                    if (intValue != 1) {
                        if (intValue == 2) {
                            this.addEndItem(endItems.get(i), endLoads.get(i));
                        }
                    }
                    else {
                        final Pair pair = endItems.get(i);
                        this.addEndItem((IconCompat)pair.first, (int)pair.second, endLoads.get(i));
                    }
                }
                else {
                    this.addEndItem(endItems.get(i));
                }
            }
        }
        
        boolean hasText() {
            return this.mTitleItem != null || this.mSubtitleItem != null;
        }
    }
}
