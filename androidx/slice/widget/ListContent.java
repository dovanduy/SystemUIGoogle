// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import androidx.slice.SliceMetadata;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import androidx.slice.SliceItem;
import androidx.slice.Slice;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import androidx.slice.core.SliceAction;

public class ListContent extends SliceContent
{
    private RowContent mHeaderContent;
    private SliceAction mPrimaryAction;
    private ArrayList<SliceContent> mRowItems;
    private RowContent mSeeMoreContent;
    private List<SliceAction> mSliceActions;
    
    @Deprecated
    public ListContent(final Context context, final Slice slice) {
        super(slice);
        this.mRowItems = new ArrayList<SliceContent>();
        if (super.mSliceItem == null) {
            return;
        }
        this.populate(slice);
    }
    
    public ListContent(final Slice slice) {
        super(slice);
        this.mRowItems = new ArrayList<SliceContent>();
        if (super.mSliceItem == null) {
            return;
        }
        this.populate(slice);
    }
    
    private static SliceItem findHeaderItem(final Slice slice) {
        final SliceItem find = SliceQuery.find(slice, "slice", null, new String[] { "list_item", "shortcut", "actions", "keywords", "ttl", "last_updated", "horizontal", "selection_option" });
        if (find != null && isValidHeader(find)) {
            return find;
        }
        return null;
    }
    
    private SliceAction findPrimaryAction() {
        final RowContent mHeaderContent = this.mHeaderContent;
        final SliceAction sliceAction = null;
        SliceItem primaryAction;
        if (mHeaderContent != null) {
            primaryAction = mHeaderContent.getPrimaryAction();
        }
        else {
            primaryAction = null;
        }
        SliceItem find = primaryAction;
        if (primaryAction == null) {
            find = SliceQuery.find(super.mSliceItem, "action", new String[] { "shortcut", "title" }, null);
        }
        SliceItem find2;
        if ((find2 = find) == null) {
            find2 = SliceQuery.find(super.mSliceItem, "action", null, (String)null);
        }
        SliceAction sliceAction2 = sliceAction;
        if (find2 != null) {
            sliceAction2 = new SliceActionImpl(find2);
        }
        return sliceAction2;
    }
    
    public static int getListHeight(final List<SliceContent> list, final SliceStyle sliceStyle, final SliceViewPolicy sliceViewPolicy) {
        int i = 0;
        if (list == null) {
            return 0;
        }
        SliceContent sliceContent = null;
        if (!list.isEmpty()) {
            sliceContent = list.get(0);
        }
        if (list.size() == 1 && !sliceContent.getSliceItem().hasHint("horizontal")) {
            return sliceContent.getHeight(sliceStyle, sliceViewPolicy);
        }
        int n = 0;
        while (i < list.size()) {
            n += list.get(i).getHeight(sliceStyle, sliceViewPolicy);
            ++i;
        }
        return n;
    }
    
    public static int getRowType(final SliceContent sliceContent, final boolean b, final List<SliceAction> list) {
        int n = 0;
        if (sliceContent != null) {
            if (sliceContent instanceof GridContent) {
                return 1;
            }
            final RowContent rowContent = (RowContent)sliceContent;
            final SliceItem primaryAction = rowContent.getPrimaryAction();
            SliceAction sliceAction = null;
            if (primaryAction != null) {
                sliceAction = new SliceActionImpl(primaryAction);
            }
            if (rowContent.getRange() != null) {
                int n2;
                if ("action".equals(rowContent.getRange().getFormat())) {
                    n2 = 4;
                }
                else {
                    n2 = 5;
                }
                return n2;
            }
            if (rowContent.getSelection() != null) {
                return 6;
            }
            if (sliceAction != null && sliceAction.isToggle()) {
                return 3;
            }
            if (b && list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    if (list.get(i).isToggle()) {
                        return 3;
                    }
                }
                return 0;
            }
            n = n;
            if (rowContent.getToggleItems().size() > 0) {
                n = 3;
            }
        }
        return n;
    }
    
    private static SliceItem getSeeMoreItem(final Slice slice) {
        final SliceItem topLevelItem = SliceQuery.findTopLevelItem(slice, null, null, new String[] { "see_more" }, null);
        if (topLevelItem != null && "slice".equals(topLevelItem.getFormat())) {
            final List<SliceItem> items = topLevelItem.getSlice().getItems();
            SliceItem sliceItem = topLevelItem;
            if (items.size() == 1) {
                sliceItem = topLevelItem;
                if ("action".equals(items.get(0).getFormat())) {
                    sliceItem = items.get(0);
                }
            }
            return sliceItem;
        }
        return null;
    }
    
    private static boolean isValidHeader(final SliceItem sliceItem) {
        final boolean equals = "slice".equals(sliceItem.getFormat());
        boolean b2;
        final boolean b = b2 = false;
        if (equals) {
            b2 = b;
            if (!sliceItem.hasAnyHints("actions", "keywords", "see_more")) {
                b2 = b;
                if (SliceQuery.find(sliceItem, "text", null, (String)null) != null) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    private void populate(final Slice slice) {
        if (slice == null) {
            return;
        }
        this.mSliceActions = SliceMetadata.getSliceActions(slice);
        final SliceItem headerItem = findHeaderItem(slice);
        if (headerItem != null) {
            final RowContent rowContent = new RowContent(headerItem, 0);
            this.mHeaderContent = rowContent;
            this.mRowItems.add(rowContent);
        }
        final SliceItem seeMoreItem = getSeeMoreItem(slice);
        if (seeMoreItem != null) {
            this.mSeeMoreContent = new RowContent(seeMoreItem, -1);
        }
        final List<SliceItem> items = slice.getItems();
        for (int i = 0; i < items.size(); ++i) {
            final SliceItem sliceItem = items.get(i);
            final String format = sliceItem.getFormat();
            if (!sliceItem.hasAnyHints("actions", "see_more", "keywords", "ttl", "last_updated") && ("action".equals(format) || "slice".equals(format))) {
                if (this.mHeaderContent == null && !sliceItem.hasHint("list_item")) {
                    final RowContent rowContent2 = new RowContent(sliceItem, 0);
                    this.mHeaderContent = rowContent2;
                    this.mRowItems.add(0, rowContent2);
                }
                else if (sliceItem.hasHint("list_item")) {
                    if (sliceItem.hasHint("horizontal")) {
                        this.mRowItems.add(new GridContent(sliceItem, i));
                    }
                    else {
                        this.mRowItems.add(new RowContent(sliceItem, i));
                    }
                }
            }
        }
        if (this.mHeaderContent == null && this.mRowItems.size() >= 1) {
            (this.mHeaderContent = this.mRowItems.get(0)).setIsHeader(true);
        }
        if (this.mRowItems.size() > 0) {
            final ArrayList<SliceContent> mRowItems = this.mRowItems;
            if (mRowItems.get(mRowItems.size() - 1) instanceof GridContent) {
                final ArrayList<SliceContent> mRowItems2 = this.mRowItems;
                ((GridContent)mRowItems2.get(mRowItems2.size() - 1)).setIsLastIndex(true);
            }
        }
        this.mPrimaryAction = this.findPrimaryAction();
    }
    
    public RowContent getHeader() {
        return this.mHeaderContent;
    }
    
    public int getHeaderTemplateType() {
        return getRowType(this.mHeaderContent, true, this.mSliceActions);
    }
    
    @Override
    public int getHeight(final SliceStyle sliceStyle, final SliceViewPolicy sliceViewPolicy) {
        return sliceStyle.getListHeight(this, sliceViewPolicy);
    }
    
    public ArrayList<SliceContent> getRowItems() {
        return this.mRowItems;
    }
    
    public ArrayList<SliceContent> getRowItems(final int n, final SliceStyle sliceStyle, final SliceViewPolicy sliceViewPolicy) {
        if (sliceViewPolicy.getMode() == 1) {
            return new ArrayList<SliceContent>(Arrays.asList(this.getHeader()));
        }
        if (!sliceViewPolicy.isScrollable() && n > 0) {
            return sliceStyle.getListItemsForNonScrollingList(this, n, sliceViewPolicy);
        }
        return this.getRowItems();
    }
    
    public SliceContent getSeeMoreItem() {
        return this.mSeeMoreContent;
    }
    
    @Override
    public SliceAction getShortcut(final Context context) {
        final SliceAction mPrimaryAction = this.mPrimaryAction;
        SliceAction shortcut;
        if (mPrimaryAction != null) {
            shortcut = mPrimaryAction;
        }
        else {
            shortcut = super.getShortcut(context);
        }
        return shortcut;
    }
    
    public List<SliceAction> getSliceActions() {
        return this.mSliceActions;
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && this.mRowItems.size() > 0;
    }
    
    public void showActionDividers(final boolean b) {
        for (final SliceContent sliceContent : this.mRowItems) {
            if (sliceContent instanceof RowContent) {
                ((RowContent)sliceContent).showActionDivider(b);
            }
        }
    }
    
    public void showHeaderDivider(final boolean b) {
        if (this.mHeaderContent != null && this.mRowItems.size() > 1) {
            this.mHeaderContent.showBottomDivider(b);
        }
    }
    
    public void showTitleItems(final boolean b) {
        final RowContent mHeaderContent = this.mHeaderContent;
        if (mHeaderContent != null) {
            mHeaderContent.showTitleItems(b);
        }
    }
}
