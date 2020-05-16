// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.slice.core.SliceActionImpl;
import android.util.Log;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import androidx.slice.core.SliceQuery;
import androidx.slice.core.SliceAction;
import androidx.slice.SliceItem;
import java.util.ArrayList;

public class RowContent extends SliceContent
{
    private ArrayList<SliceItem> mEndItems;
    private boolean mIsHeader;
    private int mLineCount;
    private SliceItem mPrimaryAction;
    private SliceItem mRange;
    private SliceItem mSelection;
    private boolean mShowActionDivider;
    private boolean mShowBottomDivider;
    private boolean mShowTitleItems;
    private SliceItem mStartItem;
    private SliceItem mSubtitleItem;
    private SliceItem mSummaryItem;
    private SliceItem mTitleItem;
    private ArrayList<SliceAction> mToggleItems;
    
    public RowContent(final SliceItem sliceItem, final int n) {
        super(sliceItem, n);
        this.mEndItems = new ArrayList<SliceItem>();
        this.mToggleItems = new ArrayList<SliceAction>();
        boolean b = false;
        this.mLineCount = 0;
        if (n == 0) {
            b = true;
        }
        this.populate(sliceItem, b);
    }
    
    private void determineStartAndPrimaryAction(final SliceItem mPrimaryAction) {
        final List<SliceItem> all = SliceQuery.findAll(mPrimaryAction, null, "title", null);
        if (all.size() > 0) {
            final String format = all.get(0).getFormat();
            if (("action".equals(format) && SliceQuery.find(all.get(0), "image") != null) || "slice".equals(format) || "long".equals(format) || "image".equals(format)) {
                this.mStartItem = all.get(0);
            }
        }
        final String[] array = { "shortcut", "title" };
        final List<SliceItem> all2 = SliceQuery.findAll(mPrimaryAction, "slice", array, null);
        all2.addAll(SliceQuery.findAll(mPrimaryAction, "action", array, null));
        if (all2.isEmpty() && "action".equals(mPrimaryAction.getFormat()) && mPrimaryAction.getSlice().getItems().size() == 1) {
            this.mPrimaryAction = mPrimaryAction;
        }
        else if (this.mStartItem != null && all2.size() > 1 && all2.get(0) == this.mStartItem) {
            this.mPrimaryAction = all2.get(1);
        }
        else if (all2.size() > 0) {
            this.mPrimaryAction = all2.get(0);
        }
    }
    
    private static ArrayList<SliceItem> filterInvalidItems(final SliceItem sliceItem) {
        final ArrayList<SliceItem> list = new ArrayList<SliceItem>();
        for (final SliceItem e : sliceItem.getSlice().getItems()) {
            if (isValidRowContent(sliceItem, e)) {
                list.add(e);
            }
        }
        return list;
    }
    
    private static boolean hasText(final SliceItem sliceItem) {
        return sliceItem != null && (sliceItem.hasHint("partial") || !TextUtils.isEmpty(sliceItem.getText()));
    }
    
    private static boolean isValidRow(final SliceItem sliceItem) {
        if (sliceItem == null) {
            return false;
        }
        if ("slice".equals(sliceItem.getFormat()) || "action".equals(sliceItem.getFormat())) {
            final List<SliceItem> items = sliceItem.getSlice().getItems();
            if (sliceItem.hasHint("see_more") && items.isEmpty()) {
                return true;
            }
            for (int i = 0; i < items.size(); ++i) {
                if (isValidRowContent(sliceItem, items.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean isValidRowContent(final SliceItem sliceItem, final SliceItem sliceItem2) {
        final boolean hasAnyHints = sliceItem2.hasAnyHints("keywords", "ttl", "last_updated", "horizontal");
        boolean b2;
        final boolean b = b2 = false;
        if (!hasAnyHints) {
            b2 = b;
            if (!"content_description".equals(sliceItem2.getSubType())) {
                b2 = b;
                if (!"selection_option_key".equals(sliceItem2.getSubType())) {
                    if ("selection_option_value".equals(sliceItem2.getSubType())) {
                        b2 = b;
                    }
                    else {
                        final String format = sliceItem2.getFormat();
                        if (!"image".equals(format) && !"text".equals(format) && !"long".equals(format) && !"action".equals(format) && !"input".equals(format) && !"slice".equals(format)) {
                            b2 = b;
                            if (!"int".equals(format)) {
                                return b2;
                            }
                            b2 = b;
                            if (!"range".equals(sliceItem.getSubType())) {
                                return b2;
                            }
                        }
                        b2 = true;
                    }
                }
            }
        }
        return b2;
    }
    
    private boolean populate(SliceItem o, final boolean mIsHeader) {
        this.mIsHeader = mIsHeader;
        if (!isValidRow(o)) {
            Log.w("RowContent", "Provided SliceItem is invalid for RowContent");
            return false;
        }
        this.determineStartAndPrimaryAction(o);
        ArrayList<SliceItem> list = filterInvalidItems(o);
        boolean b;
        if (list.size() == 1 && ("action".equals(list.get(0).getFormat()) || "slice".equals(list.get(0).getFormat())) && !list.get(0).hasAnyHints("shortcut", "title") && isValidRow(list.get(0))) {
            o = list.get(0);
            list = filterInvalidItems(o);
            b = true;
        }
        else {
            b = false;
        }
        SliceItem sliceItem = o;
        ArrayList<SliceItem> filterInvalidItems = list;
        if ("range".equals(o.getSubType())) {
            if (SliceQuery.findSubtype(o, "action", "range") != null && !b) {
                list.remove(this.mStartItem);
                if (list.size() == 1) {
                    sliceItem = o;
                    filterInvalidItems = list;
                    if (isValidRow(list.get(0))) {
                        sliceItem = list.get(0);
                        filterInvalidItems = filterInvalidItems(sliceItem);
                        this.mRange = sliceItem;
                        filterInvalidItems.remove(this.getInputRangeThumb());
                    }
                }
                else {
                    final SliceItem subtype = SliceQuery.findSubtype(o, "action", "range");
                    this.mRange = subtype;
                    final ArrayList<SliceItem> filterInvalidItems2 = filterInvalidItems(subtype);
                    filterInvalidItems2.remove(this.getInputRangeThumb());
                    list.remove(this.mRange);
                    list.addAll(filterInvalidItems2);
                    sliceItem = o;
                    filterInvalidItems = list;
                }
            }
            else {
                this.mRange = o;
                filterInvalidItems = list;
                sliceItem = o;
            }
        }
        if ("selection".equals(sliceItem.getSubType())) {
            this.mSelection = sliceItem;
        }
        if (filterInvalidItems.size() > 0) {
            o = this.mStartItem;
            if (o != null) {
                filterInvalidItems.remove(o);
            }
            o = this.mPrimaryAction;
            if (o != null) {
                filterInvalidItems.remove(o);
            }
            final ArrayList<SliceItem> list2 = new ArrayList<SliceItem>();
            for (int i = 0; i < filterInvalidItems.size(); ++i) {
                final SliceItem sliceItem2 = filterInvalidItems.get(i);
                if ("text".equals(sliceItem2.getFormat())) {
                    final SliceItem mTitleItem = this.mTitleItem;
                    if ((mTitleItem == null || !mTitleItem.hasHint("title")) && sliceItem2.hasHint("title") && !sliceItem2.hasHint("summary")) {
                        this.mTitleItem = sliceItem2;
                    }
                    else if (this.mSubtitleItem == null && !sliceItem2.hasHint("summary")) {
                        this.mSubtitleItem = sliceItem2;
                    }
                    else if (this.mSummaryItem == null && sliceItem2.hasHint("summary")) {
                        this.mSummaryItem = sliceItem2;
                    }
                }
                else {
                    list2.add(sliceItem2);
                }
            }
            if (hasText(this.mTitleItem)) {
                ++this.mLineCount;
            }
            if (hasText(this.mSubtitleItem)) {
                ++this.mLineCount;
            }
            final SliceItem mStartItem = this.mStartItem;
            int n;
            if (mStartItem != null && "long".equals(mStartItem.getFormat())) {
                n = 1;
            }
            else {
                n = 0;
            }
            int j = 0;
            int n2 = n;
            while (j < list2.size()) {
                final SliceItem e = list2.get(j);
                final boolean b2 = SliceQuery.find(e, "action") != null;
                int n3;
                if ("long".equals(e.getFormat())) {
                    if ((n3 = n2) == 0) {
                        this.mEndItems.add(e);
                        n3 = 1;
                    }
                }
                else {
                    this.processContent(e, b2);
                    n3 = n2;
                }
                ++j;
                n2 = n3;
            }
        }
        return this.isValid();
    }
    
    private void processContent(final SliceItem e, final boolean b) {
        if (b) {
            final SliceActionImpl e2 = new SliceActionImpl(e);
            if (e2.isToggle()) {
                this.mToggleItems.add(e2);
            }
        }
        this.mEndItems.add(e);
    }
    
    public ArrayList<SliceItem> getEndItems() {
        return this.mEndItems;
    }
    
    @Override
    public int getHeight(final SliceStyle sliceStyle, final SliceViewPolicy sliceViewPolicy) {
        return sliceStyle.getRowHeight(this, sliceViewPolicy);
    }
    
    public SliceItem getInputRangeThumb() {
        final SliceItem mRange = this.mRange;
        if (mRange != null) {
            final List<SliceItem> items = mRange.getSlice().getItems();
            for (int i = 0; i < items.size(); ++i) {
                if ("image".equals(items.get(i).getFormat())) {
                    return items.get(i);
                }
            }
        }
        return null;
    }
    
    public boolean getIsHeader() {
        return this.mIsHeader;
    }
    
    public int getLineCount() {
        return this.mLineCount;
    }
    
    public SliceItem getPrimaryAction() {
        return this.mPrimaryAction;
    }
    
    public SliceItem getRange() {
        return this.mRange;
    }
    
    public SliceItem getSelection() {
        return this.mSelection;
    }
    
    public SliceItem getStartItem() {
        SliceItem mStartItem;
        if (this.mIsHeader && !this.mShowTitleItems) {
            mStartItem = null;
        }
        else {
            mStartItem = this.mStartItem;
        }
        return mStartItem;
    }
    
    public SliceItem getSubtitleItem() {
        return this.mSubtitleItem;
    }
    
    public SliceItem getSummaryItem() {
        SliceItem sliceItem;
        if ((sliceItem = this.mSummaryItem) == null) {
            sliceItem = this.mSubtitleItem;
        }
        return sliceItem;
    }
    
    public SliceItem getTitleItem() {
        return this.mTitleItem;
    }
    
    public ArrayList<SliceAction> getToggleItems() {
        return this.mToggleItems;
    }
    
    public boolean hasActionDivider() {
        return this.mShowActionDivider;
    }
    
    public boolean hasBottomDivider() {
        return this.mShowBottomDivider;
    }
    
    public boolean hasTitleItems() {
        return this.mShowTitleItems;
    }
    
    public boolean isDefaultSeeMore() {
        return "action".equals(super.mSliceItem.getFormat()) && super.mSliceItem.getSlice().hasHint("see_more") && super.mSliceItem.getSlice().getItems().isEmpty();
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && (this.mStartItem != null || this.mPrimaryAction != null || this.mTitleItem != null || this.mSubtitleItem != null || this.mEndItems.size() > 0 || this.mRange != null || this.mSelection != null || this.isDefaultSeeMore());
    }
    
    public void setIsHeader(final boolean mIsHeader) {
        this.mIsHeader = mIsHeader;
    }
    
    public void showActionDivider(final boolean mShowActionDivider) {
        this.mShowActionDivider = mShowActionDivider;
    }
    
    public void showBottomDivider(final boolean mShowBottomDivider) {
        this.mShowBottomDivider = mShowBottomDivider;
    }
    
    public void showTitleItems(final boolean mShowTitleItems) {
        this.mShowTitleItems = mShowTitleItems;
    }
}
