// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import android.content.res.TypedArray;
import java.util.ArrayList;
import java.util.List;
import android.content.res.Resources;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$styleable;
import android.util.AttributeSet;
import android.content.Context;

public class SliceStyle
{
    private int mGridAllImagesHeight;
    private int mGridBigPicMaxHeight;
    private int mGridBigPicMinHeight;
    private int mGridBottomPadding;
    private int mGridImageTextHeight;
    private int mGridMaxHeight;
    private int mGridMinHeight;
    private int mGridSubtitleSize;
    private int mGridTitleSize;
    private int mGridTopPadding;
    private int mHeaderSubtitleSize;
    private int mHeaderTitleSize;
    private int mListLargeHeight;
    private int mListMinScrollHeight;
    private int mRowMaxHeight;
    private int mRowMinHeight;
    private int mRowRangeHeight;
    private int mRowSelectionHeight;
    private int mRowSingleTextWithRangeHeight;
    private int mRowSingleTextWithSelectionHeight;
    private RowStyle mRowStyle;
    private int mRowTextWithRangeHeight;
    private int mRowTextWithSelectionHeight;
    private int mSubtitleColor;
    private int mSubtitleSize;
    private int mTintColor;
    private int mTitleColor;
    private int mTitleSize;
    private int mVerticalGridTextPadding;
    private int mVerticalHeaderTextPadding;
    private int mVerticalTextPadding;
    
    public SliceStyle(final Context context, AttributeSet obtainStyledAttributes, int mTintColor, final int n) {
        this.mTintColor = -1;
        obtainStyledAttributes = (AttributeSet)context.getTheme().obtainStyledAttributes(obtainStyledAttributes, R$styleable.SliceView, mTintColor, n);
        try {
            mTintColor = ((TypedArray)obtainStyledAttributes).getColor(R$styleable.SliceView_tintColor, -1);
            if (mTintColor == -1) {
                mTintColor = this.mTintColor;
            }
            this.mTintColor = mTintColor;
            this.mTitleColor = ((TypedArray)obtainStyledAttributes).getColor(R$styleable.SliceView_titleColor, 0);
            this.mSubtitleColor = ((TypedArray)obtainStyledAttributes).getColor(R$styleable.SliceView_subtitleColor, 0);
            this.mHeaderTitleSize = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_headerTitleSize, 0.0f);
            this.mHeaderSubtitleSize = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_headerSubtitleSize, 0.0f);
            this.mVerticalHeaderTextPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_headerTextVerticalPadding, 0.0f);
            this.mTitleSize = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_titleSize, 0.0f);
            this.mSubtitleSize = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_subtitleSize, 0.0f);
            this.mVerticalTextPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_textVerticalPadding, 0.0f);
            this.mGridTitleSize = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_gridTitleSize, 0.0f);
            this.mGridSubtitleSize = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_gridSubtitleSize, 0.0f);
            mTintColor = context.getResources().getDimensionPixelSize(R$dimen.abc_slice_grid_text_inner_padding);
            this.mVerticalGridTextPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_gridTextVerticalPadding, (float)mTintColor);
            this.mGridTopPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_gridTopPadding, 0.0f);
            this.mGridBottomPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.SliceView_gridBottomPadding, 0.0f);
            mTintColor = ((TypedArray)obtainStyledAttributes).getResourceId(R$styleable.SliceView_rowStyle, 0);
            if (mTintColor != 0) {
                this.mRowStyle = new RowStyle(context, mTintColor);
            }
            ((TypedArray)obtainStyledAttributes).recycle();
            final Resources resources = context.getResources();
            this.mRowMaxHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_max_height);
            this.mRowTextWithRangeHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_range_multi_text_height);
            this.mRowSingleTextWithRangeHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_range_single_text_height);
            this.mRowMinHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_min_height);
            this.mRowRangeHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_range_height);
            this.mRowSelectionHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_selection_height);
            this.mRowTextWithSelectionHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_selection_multi_text_height);
            this.mRowSingleTextWithSelectionHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_selection_single_text_height);
            this.mGridBigPicMinHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_big_pic_min_height);
            this.mGridBigPicMaxHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_big_pic_max_height);
            this.mGridAllImagesHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_only_height);
            this.mGridImageTextHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_text_height);
            this.mGridMinHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_min_height);
            this.mGridMaxHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_max_height);
            this.mListMinScrollHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_min_height);
            this.mListLargeHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_large_height);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    public int getGridBottomPadding() {
        return this.mGridBottomPadding;
    }
    
    public int getGridHeight(final GridContent gridContent, final SliceViewPolicy sliceViewPolicy) {
        final int mode = sliceViewPolicy.getMode();
        final int n = 0;
        final boolean b = true;
        final boolean b2 = mode == 1;
        if (!gridContent.isValid()) {
            return 0;
        }
        final int largestImageMode = gridContent.getLargestImageMode();
        int n2;
        if (gridContent.isAllImages()) {
            if (gridContent.getGridContent().size() == 1) {
                if (b2) {
                    n2 = this.mGridBigPicMinHeight;
                }
                else {
                    n2 = this.mGridBigPicMaxHeight;
                }
            }
            else if (largestImageMode == 0) {
                n2 = this.mGridMinHeight;
            }
            else {
                n2 = this.mGridAllImagesHeight;
            }
        }
        else {
            final boolean b3 = gridContent.getMaxCellLineCount() > 1;
            final boolean hasImage = gridContent.hasImage();
            int n3 = b ? 1 : 0;
            if (largestImageMode != 0) {
                if (largestImageMode == 5) {
                    n3 = (b ? 1 : 0);
                }
                else {
                    n3 = 0;
                }
            }
            if (b3 && !b2) {
                if (hasImage) {
                    n2 = this.mGridMaxHeight;
                }
                else {
                    n2 = this.mGridMinHeight;
                }
            }
            else if (n3 != 0) {
                n2 = this.mGridMinHeight;
            }
            else {
                n2 = this.mGridImageTextHeight;
            }
        }
        int mGridTopPadding;
        if (gridContent.isAllImages() && gridContent.getRowIndex() == 0) {
            mGridTopPadding = this.mGridTopPadding;
        }
        else {
            mGridTopPadding = 0;
        }
        int mGridBottomPadding = n;
        if (gridContent.isAllImages()) {
            mGridBottomPadding = n;
            if (gridContent.getIsLastIndex()) {
                mGridBottomPadding = this.mGridBottomPadding;
            }
        }
        return n2 + mGridTopPadding + mGridBottomPadding;
    }
    
    public int getGridSubtitleSize() {
        return this.mGridSubtitleSize;
    }
    
    public int getGridTitleSize() {
        return this.mGridTitleSize;
    }
    
    public int getGridTopPadding() {
        return this.mGridTopPadding;
    }
    
    public int getHeaderSubtitleSize() {
        return this.mHeaderSubtitleSize;
    }
    
    public int getHeaderTitleSize() {
        return this.mHeaderTitleSize;
    }
    
    public int getListHeight(final ListContent listContent, final SliceViewPolicy sliceViewPolicy) {
        final int mode = sliceViewPolicy.getMode();
        boolean b = true;
        if (mode == 1) {
            return listContent.getHeader().getHeight(this, sliceViewPolicy);
        }
        final int maxHeight = sliceViewPolicy.getMaxHeight();
        final boolean scrollable = sliceViewPolicy.isScrollable();
        final int listItemsHeight = this.getListItemsHeight(listContent.getRowItems(), sliceViewPolicy);
        int max;
        if ((max = maxHeight) > 0) {
            max = Math.max(listContent.getHeader().getHeight(this, sliceViewPolicy), maxHeight);
        }
        int mListLargeHeight;
        if (max > 0) {
            mListLargeHeight = max;
        }
        else {
            mListLargeHeight = this.mListLargeHeight;
        }
        if (listItemsHeight - mListLargeHeight < this.mListMinScrollHeight) {
            b = false;
        }
        int min;
        if (b) {
            min = mListLargeHeight;
        }
        else if (max <= 0) {
            min = listItemsHeight;
        }
        else {
            min = Math.min(mListLargeHeight, listItemsHeight);
        }
        int listItemsHeight2 = min;
        if (!scrollable) {
            listItemsHeight2 = this.getListItemsHeight(this.getListItemsForNonScrollingList(listContent, min, sliceViewPolicy), sliceViewPolicy);
        }
        return listItemsHeight2;
    }
    
    public ArrayList<SliceContent> getListItemsForNonScrollingList(final ListContent listContent, final int n, final SliceViewPolicy sliceViewPolicy) {
        final ArrayList<SliceContent> list = new ArrayList<SliceContent>();
        if (listContent.getRowItems() != null) {
            if (listContent.getRowItems().size() != 0) {
                int n2;
                if (listContent.getRowItems() != null) {
                    n2 = 2;
                }
                else {
                    n2 = 1;
                }
                int n3;
                if (listContent.getSeeMoreItem() != null) {
                    n3 = listContent.getSeeMoreItem().getHeight(this, sliceViewPolicy) + 0;
                }
                else {
                    n3 = 0;
                }
                final int size = listContent.getRowItems().size();
                for (int i = 0; i < size; ++i) {
                    final int height = listContent.getRowItems().get(i).getHeight(this, sliceViewPolicy);
                    if (n > 0 && n3 + height > n) {
                        break;
                    }
                    n3 += height;
                    list.add(listContent.getRowItems().get(i));
                }
                if (listContent.getSeeMoreItem() != null && list.size() >= n2 && list.size() != size) {
                    list.add(listContent.getSeeMoreItem());
                }
                if (list.size() == 0) {
                    list.add(listContent.getRowItems().get(0));
                }
            }
        }
        return list;
    }
    
    public int getListItemsHeight(final List<SliceContent> list, final SliceViewPolicy sliceViewPolicy) {
        int i = 0;
        if (list == null) {
            return 0;
        }
        SliceContent sliceContent = null;
        if (!list.isEmpty()) {
            sliceContent = list.get(0);
        }
        if (list.size() == 1 && !sliceContent.getSliceItem().hasHint("horizontal")) {
            return sliceContent.getHeight(this, sliceViewPolicy);
        }
        int n = 0;
        while (i < list.size()) {
            n += list.get(i).getHeight(this, sliceViewPolicy);
            ++i;
        }
        return n;
    }
    
    public int getRowHeight(final RowContent rowContent, final SliceViewPolicy sliceViewPolicy) {
        int n;
        if (sliceViewPolicy.getMaxSmallHeight() > 0) {
            n = sliceViewPolicy.getMaxSmallHeight();
        }
        else {
            n = this.mRowMaxHeight;
        }
        if (rowContent.getRange() == null && rowContent.getSelection() == null && sliceViewPolicy.getMode() != 2) {
            return n;
        }
        int n2;
        int n3;
        if (rowContent.getRange() != null && rowContent.getStartItem() == null) {
            if (rowContent.getLineCount() > 1) {
                n2 = this.mRowTextWithRangeHeight;
            }
            else {
                n2 = this.mRowSingleTextWithRangeHeight;
            }
            n3 = this.mRowRangeHeight;
        }
        else {
            if (rowContent.getSelection() == null) {
                int mRowMinHeight = n;
                if (rowContent.getLineCount() <= 1) {
                    if (rowContent.getIsHeader()) {
                        mRowMinHeight = n;
                    }
                    else {
                        mRowMinHeight = this.mRowMinHeight;
                    }
                }
                return mRowMinHeight;
            }
            if (rowContent.getLineCount() > 1) {
                n2 = this.mRowTextWithSelectionHeight;
            }
            else {
                n2 = this.mRowSingleTextWithSelectionHeight;
            }
            n3 = this.mRowSelectionHeight;
        }
        return n2 + n3;
    }
    
    public int getRowMaxHeight() {
        return this.mRowMaxHeight;
    }
    
    public int getRowRangeHeight() {
        return this.mRowRangeHeight;
    }
    
    public int getRowSelectionHeight() {
        return this.mRowSelectionHeight;
    }
    
    public RowStyle getRowStyle() {
        return this.mRowStyle;
    }
    
    public int getSubtitleColor() {
        return this.mSubtitleColor;
    }
    
    public int getSubtitleSize() {
        return this.mSubtitleSize;
    }
    
    public int getTintColor() {
        return this.mTintColor;
    }
    
    public int getTitleColor() {
        return this.mTitleColor;
    }
    
    public int getTitleSize() {
        return this.mTitleSize;
    }
    
    public int getVerticalGridTextPadding() {
        return this.mVerticalGridTextPadding;
    }
    
    public int getVerticalHeaderTextPadding() {
        return this.mVerticalHeaderTextPadding;
    }
    
    public int getVerticalTextPadding() {
        return this.mVerticalTextPadding;
    }
}
