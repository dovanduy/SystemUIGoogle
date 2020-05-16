// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.slice.SliceUtils;
import androidx.slice.core.SliceQuery;
import java.util.List;
import androidx.slice.SliceItem;
import java.util.ArrayList;

public class GridContent extends SliceContent
{
    private boolean mAllImages;
    private ArrayList<CellContent> mGridContent;
    private boolean mHasImage;
    private boolean mIsLastIndex;
    private int mLargestImageMode;
    private int mMaxCellLineCount;
    private SliceItem mPrimaryAction;
    private SliceItem mSeeMoreItem;
    private SliceItem mTitleItem;
    
    public GridContent(final SliceItem sliceItem, final int n) {
        super(sliceItem, n);
        this.mGridContent = new ArrayList<CellContent>();
        this.mLargestImageMode = 5;
        this.populate(sliceItem);
    }
    
    private List<SliceItem> filterAndProcessItems(final List<SliceItem> list) {
        final ArrayList<SliceItem> list2 = new ArrayList<SliceItem>();
        for (int i = 0; i < list.size(); ++i) {
            final SliceItem mContentDescr = list.get(i);
            final SliceItem find = SliceQuery.find(mContentDescr, null, "see_more", null);
            final boolean b = true;
            final boolean b2 = find != null;
            int n = b ? 1 : 0;
            if (!b2) {
                if (mContentDescr.hasAnyHints("shortcut", "see_more", "keywords", "ttl", "last_updated")) {
                    n = (b ? 1 : 0);
                }
                else {
                    n = 0;
                }
            }
            if ("content_description".equals(mContentDescr.getSubType())) {
                super.mContentDescr = mContentDescr;
            }
            else if (n == 0) {
                list2.add(mContentDescr);
            }
        }
        return list2;
    }
    
    private boolean populate(final SliceItem sliceItem) {
        final SliceItem find = SliceQuery.find(sliceItem, null, "see_more", null);
        this.mSeeMoreItem = find;
        int i = 0;
        if (find != null && "slice".equals(find.getFormat())) {
            final List<SliceItem> items = this.mSeeMoreItem.getSlice().getItems();
            if (items != null && items.size() > 0) {
                this.mSeeMoreItem = items.get(0);
            }
        }
        this.mPrimaryAction = SliceQuery.find(sliceItem, "slice", new String[] { "shortcut", "title" }, new String[] { "actions" });
        this.mAllImages = true;
        if ("slice".equals(sliceItem.getFormat())) {
            for (List<SliceItem> filterAndProcessItems = this.filterAndProcessItems(sliceItem.getSlice().getItems()); i < filterAndProcessItems.size(); ++i) {
                final SliceItem sliceItem2 = filterAndProcessItems.get(i);
                if (!"content_description".equals(sliceItem2.getSubType())) {
                    this.processContent(new CellContent(sliceItem2));
                }
            }
        }
        else {
            this.processContent(new CellContent(sliceItem));
        }
        return this.isValid();
    }
    
    private void processContent(final CellContent e) {
        if (e.isValid()) {
            if (this.mTitleItem == null && e.getTitleItem() != null) {
                this.mTitleItem = e.getTitleItem();
            }
            this.mGridContent.add(e);
            if (!e.isImageOnly()) {
                this.mAllImages = false;
            }
            this.mMaxCellLineCount = Math.max(this.mMaxCellLineCount, e.getTextCount());
            this.mHasImage |= e.hasImage();
            final int mLargestImageMode = this.mLargestImageMode;
            int mLargestImageMode2;
            if (mLargestImageMode == 5) {
                mLargestImageMode2 = e.getImageMode();
            }
            else {
                mLargestImageMode2 = Math.max(mLargestImageMode, e.getImageMode());
            }
            this.mLargestImageMode = mLargestImageMode2;
        }
    }
    
    public SliceItem getContentIntent() {
        return this.mPrimaryAction;
    }
    
    public ArrayList<CellContent> getGridContent() {
        return this.mGridContent;
    }
    
    @Override
    public int getHeight(final SliceStyle sliceStyle, final SliceViewPolicy sliceViewPolicy) {
        return sliceStyle.getGridHeight(this, sliceViewPolicy);
    }
    
    public boolean getIsLastIndex() {
        return this.mIsLastIndex;
    }
    
    public int getLargestImageMode() {
        return this.mLargestImageMode;
    }
    
    public int getMaxCellLineCount() {
        return this.mMaxCellLineCount;
    }
    
    public SliceItem getSeeMoreItem() {
        return this.mSeeMoreItem;
    }
    
    public boolean hasImage() {
        return this.mHasImage;
    }
    
    public boolean isAllImages() {
        return this.mAllImages;
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && this.mGridContent.size() > 0;
    }
    
    public void setIsLastIndex(final boolean mIsLastIndex) {
        this.mIsLastIndex = mIsLastIndex;
    }
    
    public static class CellContent
    {
        private ArrayList<SliceItem> mCellItems;
        private SliceItem mContentDescr;
        private SliceItem mContentIntent;
        private boolean mHasImage;
        private int mImageMode;
        private int mTextCount;
        private SliceItem mTitleItem;
        
        public CellContent(final SliceItem sliceItem) {
            this.mCellItems = new ArrayList<SliceItem>();
            this.mImageMode = -1;
            this.populate(sliceItem);
        }
        
        private boolean isValidCellContent(final SliceItem sliceItem) {
            final String format = sliceItem.getFormat();
            final boolean equals = "content_description".equals(sliceItem.getSubType());
            final boolean b = false;
            final boolean b2 = equals || sliceItem.hasAnyHints("keywords", "ttl", "last_updated");
            boolean b3 = b;
            if (!b2) {
                if (!"text".equals(format) && !"long".equals(format)) {
                    b3 = b;
                    if (!"image".equals(format)) {
                        return b3;
                    }
                }
                b3 = true;
            }
            return b3;
        }
        
        public ArrayList<SliceItem> getCellItems() {
            return this.mCellItems;
        }
        
        public CharSequence getContentDescription() {
            final SliceItem mContentDescr = this.mContentDescr;
            CharSequence text;
            if (mContentDescr != null) {
                text = mContentDescr.getText();
            }
            else {
                text = null;
            }
            return text;
        }
        
        public SliceItem getContentIntent() {
            return this.mContentIntent;
        }
        
        public int getImageMode() {
            return this.mImageMode;
        }
        
        public int getTextCount() {
            return this.mTextCount;
        }
        
        public SliceItem getTitleItem() {
            return this.mTitleItem;
        }
        
        public boolean hasImage() {
            return this.mHasImage;
        }
        
        public boolean isImageOnly() {
            final int size = this.mCellItems.size();
            boolean b = false;
            if (size == 1) {
                b = b;
                if ("image".equals(this.mCellItems.get(0).getFormat())) {
                    b = true;
                }
            }
            return b;
        }
        
        public boolean isValid() {
            return this.mCellItems.size() > 0 && this.mCellItems.size() <= 3;
        }
        
        public boolean populate(SliceItem sliceItem) {
            final String format = sliceItem.getFormat();
            if (!sliceItem.hasHint("shortcut") && ("slice".equals(format) || "action".equals(format))) {
                final List<SliceItem> items = sliceItem.getSlice().getItems();
                final int size = items.size();
                int i = 0;
                List<SliceItem> items2 = items;
                Label_0137: {
                    if (size == 1) {
                        if (!"action".equals(items.get(0).getFormat())) {
                            items2 = items;
                            if (!"slice".equals(items.get(0).getFormat())) {
                                break Label_0137;
                            }
                        }
                        this.mContentIntent = items.get(0);
                        items2 = items.get(0).getSlice().getItems();
                    }
                }
                if ("action".equals(format)) {
                    this.mContentIntent = sliceItem;
                }
                this.mTextCount = 0;
                int n = 0;
                while (i < items2.size()) {
                    sliceItem = items2.get(i);
                    final String format2 = sliceItem.getFormat();
                    int n2 = 0;
                    Label_0359: {
                        if ("content_description".equals(sliceItem.getSubType())) {
                            this.mContentDescr = sliceItem;
                            n2 = n;
                        }
                        else if (this.mTextCount < 2 && ("text".equals(format2) || "long".equals(format2))) {
                            ++this.mTextCount;
                            this.mCellItems.add(sliceItem);
                            final SliceItem mTitleItem = this.mTitleItem;
                            if (mTitleItem != null) {
                                n2 = n;
                                if (mTitleItem.hasHint("title")) {
                                    break Label_0359;
                                }
                                n2 = n;
                                if (!sliceItem.hasHint("title")) {
                                    break Label_0359;
                                }
                            }
                            this.mTitleItem = sliceItem;
                            n2 = n;
                        }
                        else if ((n2 = n) < 1) {
                            n2 = n;
                            if ("image".equals(sliceItem.getFormat())) {
                                this.mImageMode = SliceUtils.parseImageMode(sliceItem);
                                n2 = n + 1;
                                this.mHasImage = true;
                                this.mCellItems.add(sliceItem);
                            }
                        }
                    }
                    ++i;
                    n = n2;
                }
            }
            else if (this.isValidCellContent(sliceItem)) {
                this.mCellItems.add(sliceItem);
            }
            return this.isValid();
        }
    }
}
