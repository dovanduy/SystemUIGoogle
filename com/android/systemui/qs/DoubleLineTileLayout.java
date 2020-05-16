// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.content.res.Resources;
import com.android.systemui.R$dimen;
import java.util.Iterator;
import android.view.View$MeasureSpec;
import com.android.systemui.plugins.qs.QSTileView;
import android.content.res.Configuration;
import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import java.util.ArrayList;
import android.view.ViewGroup;

public final class DoubleLineTileLayout extends ViewGroup implements QSTileLayout
{
    private boolean _listening;
    private int cellMarginHorizontal;
    private int cellMarginVertical;
    private final ArrayList<TileRecord> mRecords;
    private int smallTileSize;
    private int tilesToShow;
    
    public DoubleLineTileLayout(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
        this.mRecords = new ArrayList<TileRecord>();
        this.setFocusableInTouchMode(true);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.updateResources();
    }
    
    private final int calculateMaxColumns(final int n) {
        final int smallTileSize = this.smallTileSize;
        final int cellMarginHorizontal = this.cellMarginHorizontal;
        if (smallTileSize + cellMarginHorizontal == 0) {
            return 0;
        }
        return (n - smallTileSize) / (smallTileSize + cellMarginHorizontal) + 1;
    }
    
    private final int getLeftForColumn(final int n, final int n2, final boolean b) {
        int n3;
        if (b) {
            n3 = n2 / 2;
        }
        else {
            n3 = 0;
        }
        return n3 + n * (this.smallTileSize + n2);
    }
    
    private final int getTopBottomRow() {
        return this.smallTileSize + this.cellMarginVertical;
    }
    
    private final int getTwoLineHeight() {
        return this.smallTileSize * 2 + this.cellMarginVertical * 1;
    }
    
    public void addTile(final TileRecord e) {
        Intrinsics.checkParameterIsNotNull(e, "tile");
        this.mRecords.add(e);
        e.tile.setListening(this, this._listening);
        this.addTileView(e);
    }
    
    protected final void addTileView(final TileRecord tileRecord) {
        Intrinsics.checkParameterIsNotNull(tileRecord, "tile");
        this.addView((View)tileRecord.tileView);
    }
    
    public int getNumVisibleTiles() {
        return this.tilesToShow;
    }
    
    public int getOffsetTop(final TileRecord tileRecord) {
        return this.getTop();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        Intrinsics.checkParameterIsNotNull(configuration, "newConfig");
        super.onConfigurationChanged(configuration);
        this.updateResources();
        this.postInvalidate();
    }
    
    protected void onFinishInflate() {
        this.updateResources();
    }
    
    protected void onLayout(final boolean b, int n, int i, int topBottomRow, int min) {
        n = topBottomRow - n - this.getPaddingLeft() - this.getPaddingRight();
        min = Math.min(this.calculateMaxColumns(n), this.mRecords.size() / 2);
        if (min == 0) {
            return;
        }
        this.tilesToShow = min * 2;
        if (min <= 2) {
            n = (n - this.smallTileSize * min) / min;
        }
        else {
            n = (n - this.smallTileSize * min) / (min - 1);
        }
        int size;
        QSTileView tileView;
        int leftForColumn;
        int smallTileSize;
        for (size = this.mRecords.size(), i = 0; i < size; ++i) {
            tileView = this.mRecords.get(i).tileView;
            if (i >= this.tilesToShow) {
                Intrinsics.checkExpressionValueIsNotNull(tileView, "tileView");
                tileView.setVisibility(8);
            }
            else {
                Intrinsics.checkExpressionValueIsNotNull(tileView, "tileView");
                tileView.setVisibility(0);
                if (i > 0) {
                    tileView.updateAccessibilityOrder((View)this.mRecords.get(i - 1).tileView);
                }
                leftForColumn = this.getLeftForColumn(i % min, n, min <= 2);
                if (i < min) {
                    topBottomRow = 0;
                }
                else {
                    topBottomRow = this.getTopBottomRow();
                }
                smallTileSize = this.smallTileSize;
                tileView.layout(leftForColumn, topBottomRow, leftForColumn + smallTileSize, smallTileSize + topBottomRow);
            }
        }
    }
    
    protected void onMeasure(final int n, int twoLineHeight) {
        final Iterator<TileRecord> iterator = this.mRecords.iterator();
        while (iterator.hasNext()) {
            iterator.next().tileView.measure(TileLayout.exactly(this.smallTileSize), TileLayout.exactly(this.smallTileSize));
        }
        twoLineHeight = this.getTwoLineHeight();
        this.setMeasuredDimension(View$MeasureSpec.getSize(n), twoLineHeight);
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
        Intrinsics.checkParameterIsNotNull(o, "tile");
        this.mRecords.remove(o);
        o.tile.setListening(this, false);
        this.removeView((View)o.tileView);
    }
    
    public void setListening(final boolean listening) {
        if (this._listening == listening) {
            return;
        }
        this._listening = listening;
        final Iterator<TileRecord> iterator = this.mRecords.iterator();
        while (iterator.hasNext()) {
            iterator.next().tile.setListening(this, listening);
        }
    }
    
    public boolean updateResources() {
        final Context mContext = super.mContext;
        Intrinsics.checkExpressionValueIsNotNull(mContext, "mContext");
        final Resources resources = mContext.getResources();
        this.smallTileSize = resources.getDimensionPixelSize(R$dimen.qs_quick_tile_size);
        this.cellMarginHorizontal = resources.getDimensionPixelSize(R$dimen.qs_tile_margin_horizontal_two_line);
        this.cellMarginVertical = resources.getDimensionPixelSize(R$dimen.new_qs_vertical_margin);
        this.requestLayout();
        return false;
    }
}
