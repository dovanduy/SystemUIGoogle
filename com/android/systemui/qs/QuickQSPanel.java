// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$MeasureSpec;
import com.android.systemui.plugins.qs.QSTileView;
import android.content.res.Configuration;
import com.android.systemui.R$id;
import android.graphics.Rect;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.R$integer;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.view.ViewGroup;
import com.android.systemui.R$dimen;
import com.android.systemui.util.Utils;
import android.view.View;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.concurrent.Executor;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.tuner.TunerService;
import android.widget.LinearLayout;

public class QuickQSPanel extends QSPanel
{
    private static int sDefaultMaxTiles = 6;
    private boolean mDisabledByPolicy;
    private boolean mHasMediaPlayer;
    private LinearLayout mHorizontalLinearLayout;
    private int mMaxTiles;
    private QuickQSMediaPlayer mMediaPlayer;
    private QSTileLayout mMediaTileLayout;
    private final Tunable mNumTiles;
    private QSTileLayout mRegularTileLayout;
    private boolean mUsingMediaPlayer;
    
    public QuickQSPanel(final Context context, final AttributeSet set, final DumpManager dumpManager, final BroadcastDispatcher broadcastDispatcher, final QSLogger qsLogger, final NotificationMediaManager notificationMediaManager, final Executor executor, final DelayableExecutor delayableExecutor, final LocalBluetoothManager localBluetoothManager) {
        super(context, set, dumpManager, broadcastDispatcher, qsLogger, notificationMediaManager, executor, delayableExecutor, localBluetoothManager);
        this.mNumTiles = new Tunable() {
            @Override
            public void onTuningChanged(final String s, final String s2) {
                QuickQSPanel.this.setMaxTiles(QuickQSPanel.parseNumTiles(s2));
            }
        };
        final QSSecurityFooter mFooter = super.mFooter;
        if (mFooter != null) {
            this.removeView(mFooter.getView());
        }
        if (super.mTileLayout != null) {
            for (int i = 0; i < super.mRecords.size(); ++i) {
                super.mTileLayout.removeTile(super.mRecords.get(i));
            }
            this.removeView((View)super.mTileLayout);
        }
        final boolean useQsMediaPlayer = Utils.useQsMediaPlayer(context);
        this.mUsingMediaPlayer = useQsMediaPlayer;
        if (useQsMediaPlayer) {
            (this.mHorizontalLinearLayout = new LinearLayout(super.mContext)).setOrientation(0);
            this.mHorizontalLinearLayout.setClipChildren(false);
            this.mHorizontalLinearLayout.setClipToPadding(false);
            final int n = (int)super.mContext.getResources().getDimension(R$dimen.qqs_media_spacing);
            this.mMediaPlayer = new QuickQSMediaPlayer(super.mContext, (ViewGroup)this.mHorizontalLinearLayout, notificationMediaManager, executor, delayableExecutor);
            final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(0, -1, 1.0f);
            linearLayout$LayoutParams.setMarginEnd(n);
            linearLayout$LayoutParams.setMarginStart(0);
            this.mHorizontalLinearLayout.addView(this.mMediaPlayer.getView(), (ViewGroup$LayoutParams)linearLayout$LayoutParams);
            final DoubleLineTileLayout doubleLineTileLayout = new DoubleLineTileLayout(context);
            super.mTileLayout = doubleLineTileLayout;
            this.mMediaTileLayout = doubleLineTileLayout;
            this.mRegularTileLayout = new HeaderTileLayout(context);
            final LinearLayout$LayoutParams linearLayout$LayoutParams2 = new LinearLayout$LayoutParams(0, -1, 1.0f);
            linearLayout$LayoutParams2.setMarginEnd(0);
            linearLayout$LayoutParams2.setMarginStart(n);
            this.mHorizontalLinearLayout.addView((View)super.mTileLayout, (ViewGroup$LayoutParams)linearLayout$LayoutParams2);
            QuickQSPanel.sDefaultMaxTiles = this.getResources().getInteger(R$integer.quick_qs_panel_max_columns);
            super.mTileLayout.setListening(super.mListening);
            this.addView((View)this.mHorizontalLinearLayout, 0);
            ((View)this.mRegularTileLayout).setVisibility(8);
            this.addView((View)this.mRegularTileLayout, 0);
            super.setPadding(0, 0, 0, 0);
        }
        else {
            QuickQSPanel.sDefaultMaxTiles = this.getResources().getInteger(R$integer.quick_qs_panel_max_columns);
            (super.mTileLayout = new HeaderTileLayout(context)).setListening(super.mListening);
            this.addView((View)super.mTileLayout, 0);
            super.setPadding(0, 0, 0, 0);
        }
    }
    
    public static int getDefaultMaxTiles() {
        return QuickQSPanel.sDefaultMaxTiles;
    }
    
    public static int parseNumTiles(final String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            return QuickQSPanel.sDefaultMaxTiles;
        }
    }
    
    @Override
    protected void addDivider() {
    }
    
    @Override
    protected void drawTile(final TileRecord tileRecord, final QSTile.State state) {
        QSTile.State state2 = state;
        if (state instanceof QSTile.SignalState) {
            state2 = new QSTile.SignalState();
            state.copyTo(state2);
            ((QSTile.SignalState)state2).activityIn = false;
            ((QSTile.SignalState)state2).activityOut = false;
        }
        super.drawTile(tileRecord, state2);
    }
    
    @Override
    protected String getDumpableTag() {
        return "QuickQSPanel";
    }
    
    public QuickQSMediaPlayer getMediaPlayer() {
        return this.mMediaPlayer;
    }
    
    public int getNumQuickTiles() {
        return this.mMaxTiles;
    }
    
    public boolean hasMediaPlayer() {
        return this.mHasMediaPlayer;
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(TunerService.class).addTunable(this.mNumTiles, "sysui_qqs_count");
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(TunerService.class).removeTunable(this.mNumTiles);
    }
    
    @Override
    public void onTuningChanged(final String anObject, final String s) {
        if ("qs_show_brightness".equals(anObject)) {
            super.onTuningChanged(anObject, "0");
        }
    }
    
    void setDisabledByPolicy(final boolean mDisabledByPolicy) {
        if (mDisabledByPolicy != this.mDisabledByPolicy) {
            this.mDisabledByPolicy = mDisabledByPolicy;
            int visibility;
            if (mDisabledByPolicy) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            this.setVisibility(visibility);
        }
    }
    
    @Override
    public void setHost(final QSTileHost qsTileHost, final QSCustomizer qsCustomizer) {
        super.setHost(qsTileHost, qsCustomizer);
        this.setTiles(super.mHost.getTiles());
    }
    
    public void setMaxTiles(final int mMaxTiles) {
        this.mMaxTiles = mMaxTiles;
        final QSTileHost mHost = super.mHost;
        if (mHost != null) {
            this.setTiles(mHost.getTiles());
        }
    }
    
    public void setPadding(final int n, final int n2, final int n3, final int n4) {
    }
    
    public void setQSPanelAndHeader(final QSPanel qsPanel, final View view) {
    }
    
    @Override
    public void setTiles(final Collection<QSTile> collection) {
        final ArrayList<QSTile> list = new ArrayList<QSTile>();
        final Iterator<QSTile> iterator = collection.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
            if (list.size() == this.mMaxTiles) {
                break;
            }
        }
        super.setTiles(list, true);
    }
    
    public void setVisibility(int visibility) {
        if (this.mDisabledByPolicy) {
            if (this.getVisibility() == 8) {
                return;
            }
            visibility = 8;
        }
        super.setVisibility(visibility);
    }
    
    boolean switchTileLayout() {
        if (!this.mUsingMediaPlayer) {
            return false;
        }
        final boolean hasMediaSession = this.mMediaPlayer.hasMediaSession();
        this.mHasMediaPlayer = hasMediaSession;
        if (hasMediaSession && this.mHorizontalLinearLayout.getVisibility() == 8) {
            this.mHorizontalLinearLayout.setVisibility(0);
            ((View)this.mRegularTileLayout).setVisibility(8);
            super.mTileLayout.setListening(false);
            for (final TileRecord tileRecord : super.mRecords) {
                super.mTileLayout.removeTile(tileRecord);
                tileRecord.tile.removeCallback(tileRecord.callback);
            }
            super.mTileLayout = this.mMediaTileLayout;
            final QSTileHost mHost = super.mHost;
            if (mHost != null) {
                this.setTiles(mHost.getTiles());
            }
            super.mTileLayout.setListening(super.mListening);
            return true;
        }
        if (!this.mHasMediaPlayer && this.mHorizontalLinearLayout.getVisibility() == 0) {
            this.mHorizontalLinearLayout.setVisibility(8);
            ((View)this.mRegularTileLayout).setVisibility(0);
            super.mTileLayout.setListening(false);
            for (final TileRecord tileRecord2 : super.mRecords) {
                super.mTileLayout.removeTile(tileRecord2);
                tileRecord2.tile.removeCallback(tileRecord2.callback);
            }
            super.mTileLayout = this.mRegularTileLayout;
            final QSTileHost mHost2 = super.mHost;
            if (mHost2 != null) {
                this.setTiles(mHost2.getTiles());
            }
            super.mTileLayout.setListening(super.mListening);
            return true;
        }
        return false;
    }
    
    private static class HeaderTileLayout extends TileLayout
    {
        private Rect mClippingBounds;
        
        public HeaderTileLayout(final Context context) {
            super(context);
            this.mClippingBounds = new Rect();
            this.setClipChildren(false);
            this.setClipToPadding(false);
            final LinearLayout$LayoutParams layoutParams = new LinearLayout$LayoutParams(-1, -1);
            layoutParams.gravity = 1;
            this.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
        
        private boolean calculateColumns() {
            final int mColumns = super.mColumns;
            final int size = super.mRecords.size();
            boolean b = false;
            if (size == 0) {
                super.mColumns = 0;
                return true;
            }
            final int n = this.getMeasuredWidth() - this.getPaddingStart() - this.getPaddingEnd();
            final int mCellMarginHorizontal = (n - super.mCellWidth * size) / Math.max(1, size - 1);
            if (mCellMarginHorizontal > 0) {
                super.mCellMarginHorizontal = mCellMarginHorizontal;
                super.mColumns = size;
            }
            else {
                final int mCellWidth = super.mCellWidth;
                int min;
                if (mCellWidth == 0) {
                    min = 1;
                }
                else {
                    min = Math.min(size, n / mCellWidth);
                }
                super.mColumns = min;
                if (min == 1) {
                    super.mCellMarginHorizontal = (n - super.mCellWidth) / 2;
                }
                else {
                    super.mCellMarginHorizontal = (n - super.mCellWidth * min) / (min - 1);
                }
            }
            if (super.mColumns != mColumns) {
                b = true;
            }
            return b;
        }
        
        private ViewGroup$LayoutParams generateTileLayoutParams() {
            return new ViewGroup$LayoutParams(super.mCellWidth, super.mCellHeight);
        }
        
        private void setAccessibilityOrder() {
            final ArrayList<TileRecord> mRecords = (ArrayList<TileRecord>)super.mRecords;
            if (mRecords != null && mRecords.size() > 0) {
                final Iterator<TileRecord> iterator = super.mRecords.iterator();
                Object updateAccessibilityOrder = this;
                while (iterator.hasNext()) {
                    final TileRecord tileRecord = iterator.next();
                    if (tileRecord.tileView.getVisibility() == 8) {
                        continue;
                    }
                    updateAccessibilityOrder = tileRecord.tileView.updateAccessibilityOrder((View)updateAccessibilityOrder);
                }
                final ArrayList<TileRecord> mRecords2 = (ArrayList<TileRecord>)super.mRecords;
                mRecords2.get(mRecords2.size() - 1).tileView.setAccessibilityTraversalBefore(R$id.expand_indicator);
            }
        }
        
        @Override
        protected void addTileView(final TileRecord tileRecord) {
            this.addView((View)tileRecord.tileView, this.getChildCount(), this.generateTileLayoutParams());
        }
        
        @Override
        protected int getColumnStart(final int n) {
            if (super.mColumns == 1) {
                return this.getPaddingStart() + super.mCellMarginHorizontal;
            }
            return this.getPaddingStart() + n * (super.mCellWidth + super.mCellMarginHorizontal);
        }
        
        @Override
        public int getNumVisibleTiles() {
            return super.mColumns;
        }
        
        protected void onConfigurationChanged(final Configuration configuration) {
            super.onConfigurationChanged(configuration);
            this.updateResources();
        }
        
        public void onFinishInflate() {
            this.updateResources();
        }
        
        @Override
        protected void onLayout(final boolean b, int i, int visibility, final int n, final int n2) {
            this.mClippingBounds.set(0, 0, n - i, 10000);
            this.setClipBounds(this.mClippingBounds);
            this.calculateColumns();
            QSTileView tileView;
            for (i = 0; i < super.mRecords.size(); ++i) {
                tileView = super.mRecords.get(i).tileView;
                if (i < super.mColumns) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                tileView.setVisibility(visibility);
            }
            this.setAccessibilityOrder();
            this.layoutTileRecords(super.mColumns);
        }
        
        @Override
        protected void onMeasure(final int n, int mCellHeight) {
            for (final TileRecord tileRecord : super.mRecords) {
                if (tileRecord.tileView.getVisibility() == 8) {
                    continue;
                }
                tileRecord.tileView.measure(TileLayout.exactly(super.mCellWidth), TileLayout.exactly(super.mCellHeight));
            }
            if ((mCellHeight = super.mCellHeight) < 0) {
                mCellHeight = 0;
            }
            this.setMeasuredDimension(View$MeasureSpec.getSize(n), mCellHeight);
        }
        
        @Override
        public boolean updateResources() {
            final int dimensionPixelSize = super.mContext.getResources().getDimensionPixelSize(R$dimen.qs_quick_tile_size);
            super.mCellWidth = dimensionPixelSize;
            super.mCellHeight = dimensionPixelSize;
            return false;
        }
    }
}
