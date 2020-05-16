// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.builders;

import androidx.core.util.Pair;
import java.util.ArrayList;
import androidx.core.graphics.drawable.IconCompat;
import java.util.List;
import androidx.slice.builders.impl.ListBuilderBasicImpl;
import androidx.slice.builders.impl.ListBuilderImpl;
import androidx.slice.SliceSpecs;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import androidx.slice.Slice;
import android.net.Uri;
import android.content.Context;

public class ListBuilder extends TemplateSliceBuilder
{
    private androidx.slice.builders.impl.ListBuilder mImpl;
    
    public ListBuilder(final Context context, final Uri uri, final long ttl) {
        super(context, uri);
        this.mImpl.setTtl(ttl);
    }
    
    public ListBuilder addRow(final RowBuilder rowBuilder) {
        this.mImpl.addRow(rowBuilder);
        return this;
    }
    
    public Slice build() {
        return ((TemplateBuilderImpl)this.mImpl).build();
    }
    
    @Override
    protected TemplateBuilderImpl selectImpl() {
        if (this.checkCompatible(SliceSpecs.LIST_V2)) {
            return new ListBuilderImpl(this.getBuilder(), SliceSpecs.LIST_V2, this.getClock());
        }
        if (this.checkCompatible(SliceSpecs.LIST)) {
            return new ListBuilderImpl(this.getBuilder(), SliceSpecs.LIST, this.getClock());
        }
        if (this.checkCompatible(SliceSpecs.BASIC)) {
            return new ListBuilderBasicImpl(this.getBuilder(), SliceSpecs.BASIC);
        }
        return null;
    }
    
    public ListBuilder setHeader(final HeaderBuilder header) {
        this.mImpl.setHeader(header);
        return this;
    }
    
    @Override
    void setImpl(final TemplateBuilderImpl templateBuilderImpl) {
        this.mImpl = (androidx.slice.builders.impl.ListBuilder)templateBuilderImpl;
    }
    
    public static class HeaderBuilder
    {
        private CharSequence mContentDescription;
        private int mLayoutDirection;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private boolean mSubtitleLoading;
        private CharSequence mSummary;
        private boolean mSummaryLoading;
        private CharSequence mTitle;
        private boolean mTitleLoading;
        private final Uri mUri;
        
        public HeaderBuilder(final Uri mUri) {
            this.mUri = mUri;
        }
        
        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }
        
        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }
        
        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }
        
        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }
        
        public CharSequence getSummary() {
            return this.mSummary;
        }
        
        public CharSequence getTitle() {
            return this.mTitle;
        }
        
        public Uri getUri() {
            return this.mUri;
        }
        
        public boolean isSubtitleLoading() {
            return this.mSubtitleLoading;
        }
        
        public boolean isSummaryLoading() {
            return this.mSummaryLoading;
        }
        
        public boolean isTitleLoading() {
            return this.mTitleLoading;
        }
        
        public HeaderBuilder setPrimaryAction(final SliceAction mPrimaryAction) {
            this.mPrimaryAction = mPrimaryAction;
            return this;
        }
        
        public HeaderBuilder setTitle(final CharSequence charSequence) {
            this.setTitle(charSequence, false);
            return this;
        }
        
        public HeaderBuilder setTitle(final CharSequence mTitle, final boolean mTitleLoading) {
            this.mTitle = mTitle;
            this.mTitleLoading = mTitleLoading;
            return this;
        }
    }
    
    public static class RowBuilder
    {
        private CharSequence mContentDescription;
        private List<Object> mEndItems;
        private List<Boolean> mEndLoads;
        private List<Integer> mEndTypes;
        private boolean mHasEndActionOrToggle;
        private int mLayoutDirection;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private boolean mSubtitleLoading;
        private long mTimeStamp;
        private CharSequence mTitle;
        private SliceAction mTitleAction;
        private boolean mTitleActionLoading;
        private IconCompat mTitleIcon;
        private int mTitleImageMode;
        private boolean mTitleItemLoading;
        private boolean mTitleLoading;
        private final Uri mUri;
        
        public RowBuilder(final Uri mUri) {
            this.mTimeStamp = -1L;
            this.mLayoutDirection = -1;
            this.mEndItems = new ArrayList<Object>();
            this.mEndTypes = new ArrayList<Integer>();
            this.mEndLoads = new ArrayList<Boolean>();
            this.mUri = mUri;
        }
        
        public RowBuilder addEndItem(final IconCompat iconCompat, final int n) {
            this.addEndItem(iconCompat, n, false);
            return this;
        }
        
        public RowBuilder addEndItem(final IconCompat iconCompat, final int i, final boolean b) {
            if (!this.mHasEndActionOrToggle) {
                this.mEndItems.add(new Pair(iconCompat, i));
                this.mEndTypes.add(1);
                this.mEndLoads.add(b);
                return this;
            }
            throw new IllegalArgumentException("Trying to add an icon to end items when anaction has already been added. End items cannot have a mixture of actions and icons.");
        }
        
        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }
        
        public List<Object> getEndItems() {
            return this.mEndItems;
        }
        
        public List<Boolean> getEndLoads() {
            return this.mEndLoads;
        }
        
        public List<Integer> getEndTypes() {
            return this.mEndTypes;
        }
        
        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }
        
        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }
        
        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }
        
        public long getTimeStamp() {
            return this.mTimeStamp;
        }
        
        public CharSequence getTitle() {
            return this.mTitle;
        }
        
        public SliceAction getTitleAction() {
            return this.mTitleAction;
        }
        
        public IconCompat getTitleIcon() {
            return this.mTitleIcon;
        }
        
        public int getTitleImageMode() {
            return this.mTitleImageMode;
        }
        
        public Uri getUri() {
            return this.mUri;
        }
        
        public boolean isSubtitleLoading() {
            return this.mSubtitleLoading;
        }
        
        public boolean isTitleActionLoading() {
            return this.mTitleActionLoading;
        }
        
        public boolean isTitleItemLoading() {
            return this.mTitleItemLoading;
        }
        
        public boolean isTitleLoading() {
            return this.mTitleLoading;
        }
        
        public RowBuilder setContentDescription(final CharSequence mContentDescription) {
            this.mContentDescription = mContentDescription;
            return this;
        }
        
        public RowBuilder setPrimaryAction(final SliceAction mPrimaryAction) {
            this.mPrimaryAction = mPrimaryAction;
            return this;
        }
        
        public RowBuilder setTitle(final CharSequence charSequence) {
            this.setTitle(charSequence, false);
            return this;
        }
        
        public RowBuilder setTitle(final CharSequence mTitle, final boolean mTitleLoading) {
            this.mTitle = mTitle;
            this.mTitleLoading = mTitleLoading;
            return this;
        }
    }
}
