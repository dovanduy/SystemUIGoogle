// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.content.res.Resources;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import java.util.Iterator;
import com.android.systemui.plugins.qs.QSTileView;
import android.view.View;
import android.view.View$MeasureSpec;
import com.android.systemui.util.Utils;
import android.provider.Settings$System;
import android.util.AttributeSet;
import android.content.Context;
import java.util.ArrayList;
import android.view.ViewGroup;

public class TileLayout extends ViewGroup implements QSTileLayout
{
    protected int mCellHeight;
    protected int mCellMarginHorizontal;
    private int mCellMarginTop;
    protected int mCellMarginVertical;
    protected int mCellWidth;
    protected int mColumns;
    private final boolean mLessRows;
    private boolean mListening;
    protected int mMaxAllowedRows;
    protected final ArrayList<TileRecord> mRecords;
    protected int mRows;
    protected int mSidePadding;
    
    public TileLayout(final Context context) {
        this(context, null);
    }
    
    public TileLayout(final Context context, final AttributeSet set) {
        super(context, set);
        final boolean b = true;
        this.mRows = 1;
        this.mRecords = new ArrayList<TileRecord>();
        this.mMaxAllowedRows = 3;
        this.setFocusableInTouchMode(true);
        boolean mLessRows = b;
        if (Settings$System.getInt(context.getContentResolver(), "qs_less_rows", 0) == 0) {
            mLessRows = (Utils.useQsMediaPlayer(context) && b);
        }
        this.mLessRows = mLessRows;
        this.updateResources();
    }
    
    protected static int exactly(final int n) {
        return View$MeasureSpec.makeMeasureSpec(n, 1073741824);
    }
    
    private int getRowTop(final int n) {
        return n * (this.mCellHeight + this.mCellMarginVertical) + this.mCellMarginTop;
    }
    
    public void addTile(final TileRecord e) {
        this.mRecords.add(e);
        e.tile.setListening(this, this.mListening);
        this.addTileView(e);
    }
    
    protected void addTileView(final TileRecord tileRecord) {
        this.addView((View)tileRecord.tileView);
    }
    
    protected int getColumnStart(final int n) {
        final int paddingStart = this.getPaddingStart();
        final int mSidePadding = this.mSidePadding;
        final int mCellMarginHorizontal = this.mCellMarginHorizontal;
        return paddingStart + mSidePadding + mCellMarginHorizontal / 2 + n * (this.mCellWidth + mCellMarginHorizontal);
    }
    
    public int getNumVisibleTiles() {
        return this.mRecords.size();
    }
    
    public int getOffsetTop(final TileRecord tileRecord) {
        return this.getTop();
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void layoutTileRecords(int a) {
        final boolean b = this.getLayoutDirection() == 1;
        final int min = Math.min(a, this.mRows * this.mColumns);
        int i = 0;
        int n2;
        int n = n2 = i;
        while (i < min) {
            a = n;
            int n3 = n2;
            if (n == this.mColumns) {
                n3 = n2 + 1;
                a = 0;
            }
            final TileRecord tileRecord = this.mRecords.get(i);
            final int rowTop = this.getRowTop(n3);
            int n4;
            if (b) {
                n4 = this.mColumns - a - 1;
            }
            else {
                n4 = a;
            }
            final int columnStart = this.getColumnStart(n4);
            final int mCellWidth = this.mCellWidth;
            final QSTileView tileView = tileRecord.tileView;
            tileView.layout(columnStart, rowTop, mCellWidth + columnStart, tileView.getMeasuredHeight() + rowTop);
            ++i;
            n = a + 1;
            n2 = n3;
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        this.layoutTileRecords(this.mRecords.size());
    }
    
    protected void onMeasure(int paddingEnd, int n) {
        final int size = this.mRecords.size();
        final int size2 = View$MeasureSpec.getSize(paddingEnd);
        final int paddingStart = this.getPaddingStart();
        paddingEnd = this.getPaddingEnd();
        if (View$MeasureSpec.getMode(n) == 0) {
            n = this.mColumns;
            this.mRows = (size + n - 1) / n;
        }
        final int mSidePadding = this.mSidePadding;
        n = this.mCellMarginHorizontal;
        final int mColumns = this.mColumns;
        this.mCellWidth = (size2 - paddingStart - paddingEnd - mSidePadding * 2 - n * mColumns) / mColumns;
        final Iterator<TileRecord> iterator = this.mRecords.iterator();
        Object updateAccessibilityOrder = this;
        while (iterator.hasNext()) {
            final TileRecord tileRecord = iterator.next();
            if (tileRecord.tileView.getVisibility() == 8) {
                continue;
            }
            tileRecord.tileView.measure(exactly(this.mCellWidth), exactly(this.mCellHeight));
            updateAccessibilityOrder = tileRecord.tileView.updateAccessibilityOrder((View)updateAccessibilityOrder);
        }
        final int mCellHeight = this.mCellHeight;
        final int mCellMarginVertical = this.mCellMarginVertical;
        final int mRows = this.mRows;
        n = 0;
        if (mRows != 0) {
            paddingEnd = this.mCellMarginTop - mCellMarginVertical;
        }
        else {
            paddingEnd = 0;
        }
        paddingEnd += (mCellHeight + mCellMarginVertical) * mRows;
        if (paddingEnd < 0) {
            paddingEnd = n;
        }
        this.setMeasuredDimension(size2, paddingEnd);
    }
    
    public void removeAllViews() {
        final Iterator<TileRecord> iterator = this.mRecords.iterator();
        while (iterator.hasNext()) {
            iterator.next().tile.setListening(this, false);
        }
        this.mRecords.clear();
        super.removeAllViews();
    }
    
    public void removeTile(final TileRecord o) {
        this.mRecords.remove(o);
        o.tile.setListening(this, false);
        this.removeView((View)o.tileView);
    }
    
    public void setListening(final boolean mListening) {
        if (this.mListening == mListening) {
            return;
        }
        this.mListening = mListening;
        final Iterator<TileRecord> iterator = this.mRecords.iterator();
        while (iterator.hasNext()) {
            iterator.next().tile.setListening(this, this.mListening);
        }
    }
    
    public boolean updateMaxRows(int mRows, final int n) {
        final int size = View$MeasureSpec.getSize(mRows);
        final int mCellMarginTop = this.mCellMarginTop;
        final int mCellMarginVertical = this.mCellMarginVertical;
        mRows = this.mRows;
        final int mRows2 = (size - mCellMarginTop + mCellMarginVertical) / (this.mCellHeight + mCellMarginVertical);
        this.mRows = mRows2;
        final int mMaxAllowedRows = this.mMaxAllowedRows;
        boolean b = true;
        if (mRows2 >= mMaxAllowedRows) {
            this.mRows = mMaxAllowedRows;
        }
        else if (mRows2 <= 1) {
            this.mRows = 1;
        }
        final int mRows3 = this.mRows;
        final int mColumns = this.mColumns;
        if (mRows3 > (n + mColumns - 1) / mColumns) {
            this.mRows = (n + mColumns - 1) / mColumns;
        }
        if (mRows == this.mRows) {
            b = false;
        }
        return b;
    }
    
    public boolean updateResources() {
        final Resources resources = super.mContext.getResources();
        final int max = Math.max(1, resources.getInteger(R$integer.quick_settings_num_columns));
        this.mCellHeight = super.mContext.getResources().getDimensionPixelSize(R$dimen.qs_tile_height);
        this.mCellMarginHorizontal = resources.getDimensionPixelSize(R$dimen.qs_tile_margin_horizontal);
        this.mCellMarginVertical = resources.getDimensionPixelSize(R$dimen.qs_tile_margin_vertical);
        this.mCellMarginTop = resources.getDimensionPixelSize(R$dimen.qs_tile_margin_top);
        this.mSidePadding = resources.getDimensionPixelOffset(R$dimen.qs_tile_layout_margin_side);
        final int max2 = Math.max(1, this.getResources().getInteger(R$integer.quick_settings_max_rows));
        this.mMaxAllowedRows = max2;
        if (this.mLessRows) {
            this.mMaxAllowedRows = Math.max(1, max2 - 1);
        }
        if (this.mColumns != max) {
            this.mColumns = max;
            this.requestLayout();
            return true;
        }
        return false;
    }
}
