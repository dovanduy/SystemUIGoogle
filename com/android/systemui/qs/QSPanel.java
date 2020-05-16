// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.content.res.Resources;
import com.android.systemui.settings.ToggleSliderView;
import java.util.Collection;
import android.content.res.Configuration;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.plugins.qs.QSTileView;
import com.android.systemui.qs.external.CustomTile;
import android.content.ComponentName;
import android.app.Notification;
import com.android.settingslib.media.InfoMediaManager;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import com.android.systemui.R$dimen;
import android.util.Log;
import android.service.notification.StatusBarNotification;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession$Token;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import android.metrics.LogMaker;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.settings.ToggleSlider;
import com.android.systemui.R$id;
import android.widget.HorizontalScrollView;
import com.android.systemui.util.Utils;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import java.util.Iterator;
import java.util.List;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.util.AttributeSet;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.internal.logging.MetricsLogger;
import java.util.ArrayList;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.concurrent.Executor;
import com.android.systemui.dump.DumpManager;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.qs.customize.QSCustomizer;
import android.content.Context;
import android.view.View;
import com.android.systemui.settings.BrightnessController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.tuner.TunerService;
import android.widget.LinearLayout;

public class QSPanel extends LinearLayout implements Tunable, Callback, BrightnessMirrorListener, Dumpable
{
    private final DelayableExecutor mBackgroundExecutor;
    private BrightnessController mBrightnessController;
    private BrightnessMirrorController mBrightnessMirrorController;
    protected final View mBrightnessView;
    private String mCachedSpecs;
    private QSDetail.Callback mCallback;
    protected final Context mContext;
    private QSCustomizer mCustomizePanel;
    private Record mDetailRecord;
    private MediaDevice mDevice;
    private final LocalMediaManager.DeviceCallback mDeviceCallback;
    private View mDivider;
    private final DumpManager mDumpManager;
    protected boolean mExpanded;
    protected QSSecurityFooter mFooter;
    private PageIndicator mFooterPageIndicator;
    private final Executor mForegroundExecutor;
    private boolean mGridContentVisible;
    private final H mHandler;
    protected QSTileHost mHost;
    protected boolean mListening;
    private final LocalBluetoothManager mLocalBluetoothManager;
    private LocalMediaManager mLocalMediaManager;
    private final LinearLayout mMediaCarousel;
    private final ArrayList<QSMediaPlayer> mMediaPlayers;
    private final MetricsLogger mMetricsLogger;
    private final NotificationMediaManager mNotificationMediaManager;
    private final QSLogger mQSLogger;
    private final QSTileRevealController mQsTileRevealController;
    protected final ArrayList<TileRecord> mRecords;
    protected QSTileLayout mTileLayout;
    private boolean mUpdateCarousel;
    
    public QSPanel(final Context mContext, final AttributeSet set, final DumpManager mDumpManager, final BroadcastDispatcher broadcastDispatcher, final QSLogger mqsLogger, final NotificationMediaManager mNotificationMediaManager, final Executor mForegroundExecutor, final DelayableExecutor mBackgroundExecutor, final LocalBluetoothManager mLocalBluetoothManager) {
        super(mContext, set);
        this.mRecords = new ArrayList<TileRecord>();
        this.mCachedSpecs = "";
        this.mHandler = new H();
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mMediaPlayers = new ArrayList<QSMediaPlayer>();
        this.mUpdateCarousel = false;
        this.mGridContentVisible = true;
        this.mDeviceCallback = new LocalMediaManager.DeviceCallback() {
            @Override
            public void onDeviceListUpdate(final List<MediaDevice> list) {
                if (QSPanel.this.mLocalMediaManager == null) {
                    return;
                }
                final MediaDevice currentConnectedDevice = QSPanel.this.mLocalMediaManager.getCurrentConnectedDevice();
                if (QSPanel.this.mDevice == null || !QSPanel.this.mDevice.equals(currentConnectedDevice)) {
                    QSPanel.this.mDevice = currentConnectedDevice;
                    final Iterator<QSMediaPlayer> iterator = QSPanel.this.mMediaPlayers.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().updateDevice(QSPanel.this.mDevice);
                    }
                }
            }
            
            @Override
            public void onSelectedDeviceStateChanged(final MediaDevice mediaDevice, final int n) {
                if (QSPanel.this.mDevice == null || !QSPanel.this.mDevice.equals(mediaDevice)) {
                    QSPanel.this.mDevice = mediaDevice;
                    final Iterator<QSMediaPlayer> iterator = QSPanel.this.mMediaPlayers.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().updateDevice(QSPanel.this.mDevice);
                    }
                }
            }
        };
        this.mContext = mContext;
        this.mQSLogger = mqsLogger;
        this.mDumpManager = mDumpManager;
        this.mNotificationMediaManager = mNotificationMediaManager;
        this.mForegroundExecutor = mForegroundExecutor;
        this.mBackgroundExecutor = mBackgroundExecutor;
        this.mLocalBluetoothManager = mLocalBluetoothManager;
        this.setOrientation(1);
        this.addView(this.mBrightnessView = LayoutInflater.from(this.mContext).inflate(R$layout.quick_settings_brightness_dialog, (ViewGroup)this, false));
        this.mTileLayout = (QSTileLayout)LayoutInflater.from(this.mContext).inflate(R$layout.qs_paged_tile_layout, (ViewGroup)this, false);
        this.mQSLogger.logAllTilesChangeListening(this.mListening, this.getDumpableTag(), this.mCachedSpecs);
        this.mTileLayout.setListening(this.mListening);
        this.addView((View)this.mTileLayout);
        this.mQsTileRevealController = new QSTileRevealController(this.mContext, this, (PagedTileLayout)this.mTileLayout);
        this.addDivider();
        if (Utils.useQsMediaPlayer(mContext)) {
            final HorizontalScrollView horizontalScrollView = (HorizontalScrollView)LayoutInflater.from(this.mContext).inflate(R$layout.media_carousel, (ViewGroup)this, false);
            this.mMediaCarousel = (LinearLayout)horizontalScrollView.findViewById(R$id.media_carousel);
            this.addView((View)horizontalScrollView, 0);
        }
        else {
            this.mMediaCarousel = null;
        }
        final QSSecurityFooter mFooter = new QSSecurityFooter(this, mContext);
        this.mFooter = mFooter;
        this.addView(mFooter.getView());
        this.updateResources();
        this.mBrightnessController = new BrightnessController(this.getContext(), (ToggleSlider)this.findViewById(R$id.brightness_slider), broadcastDispatcher);
    }
    
    private void fireScanStateChanged(final boolean b) {
        final QSDetail.Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onScanStateChanged(b);
        }
    }
    
    private void fireShowingDetail(final DetailAdapter detailAdapter, final int n, final int n2) {
        final QSDetail.Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onShowingDetail(detailAdapter, n, n2);
        }
    }
    
    private void fireToggleStateChanged(final boolean b) {
        final QSDetail.Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onToggleStateChanged(b);
        }
    }
    
    private QSTile getTile(final String s) {
        for (int i = 0; i < this.mRecords.size(); ++i) {
            if (s.equals(this.mRecords.get(i).tile.getTileSpec())) {
                return this.mRecords.get(i).tile;
            }
        }
        return this.mHost.createTile(s);
    }
    
    private String getTilesSpecs() {
        return this.mRecords.stream().map((Function<? super Object, ?>)_$$Lambda$QSPanel$BhHZgebPgkrbRfUrB_Ik6CkLFO8.INSTANCE).collect((Collector<? super Object, ?, String>)Collectors.joining(","));
    }
    
    private void handleShowDetailImpl(final Record record, final boolean b, final int n, final int n2) {
        final DetailAdapter detailAdapter = null;
        Record detailRecord;
        if (b) {
            detailRecord = record;
        }
        else {
            detailRecord = null;
        }
        this.setDetailRecord(detailRecord);
        DetailAdapter detailAdapter2 = detailAdapter;
        if (b) {
            detailAdapter2 = record.detailAdapter;
        }
        this.fireShowingDetail(detailAdapter2, n, n2);
    }
    
    private void handleShowDetailTile(final TileRecord tileRecord, final boolean detailListening) {
        if (this.mDetailRecord != null == detailListening && this.mDetailRecord == tileRecord) {
            return;
        }
        if (detailListening && (((Record)tileRecord).detailAdapter = tileRecord.tile.getDetailAdapter()) == null) {
            return;
        }
        tileRecord.tile.setDetailListening(detailListening);
        this.handleShowDetailImpl((Record)tileRecord, detailListening, tileRecord.tileView.getLeft() + tileRecord.tileView.getWidth() / 2, tileRecord.tileView.getDetailY() + this.mTileLayout.getOffsetTop(tileRecord) + this.getTop());
    }
    
    private void logTiles() {
        for (int i = 0; i < this.mRecords.size(); ++i) {
            final QSTile tile = this.mRecords.get(i).tile;
            this.mMetricsLogger.write(tile.populate(new LogMaker(tile.getMetricsCategory()).setType(1)));
        }
    }
    
    private void updatePageIndicator() {
        if (this.mTileLayout instanceof PagedTileLayout) {
            final PageIndicator mFooterPageIndicator = this.mFooterPageIndicator;
            if (mFooterPageIndicator != null) {
                mFooterPageIndicator.setVisibility(8);
                ((PagedTileLayout)this.mTileLayout).setPageIndicator(this.mFooterPageIndicator);
            }
        }
    }
    
    private void updateViewVisibilityForTuningValue(final View view, final String s) {
        int visibility;
        if (TunerService.parseIntegerSwitch(s, true)) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        view.setVisibility(visibility);
    }
    
    protected void addDivider() {
        final View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.qs_divider, (ViewGroup)this, false);
        (this.mDivider = inflate).setBackgroundColor(com.android.settingslib.Utils.applyAlpha(inflate.getAlpha(), QSTileImpl.getColorForState(this.mContext, 2)));
        this.addView(this.mDivider);
    }
    
    public void addMediaSession(final MediaSession$Token mediaSession$Token, final Icon icon, final int n, final int n2, final View view, final StatusBarNotification statusBarNotification) {
        if (!Utils.useQsMediaPlayer(this.mContext)) {
            Log.e("QSPanel", "Tried to add media session without player!");
            return;
        }
        if (mediaSession$Token == null) {
            Log.e("QSPanel", "Media session token was null!");
            return;
        }
        final String packageName = statusBarNotification.getPackageName();
        while (true) {
            for (QSMediaPlayer e : this.mMediaPlayers) {
                if (e.getMediaSessionToken().equals((Object)mediaSession$Token)) {
                    Log.d("QSPanel", "a player for this session already exists");
                }
                else {
                    if (!packageName.equals(e.getMediaPlayerPackage())) {
                        continue;
                    }
                    Log.d("QSPanel", "found an old session for this app");
                }
                final int n3 = (int)this.getResources().getDimension(R$dimen.qs_media_width);
                final int n4 = (int)this.getResources().getDimension(R$dimen.qs_media_padding);
                final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(n3, -1);
                linearLayout$LayoutParams.setMarginStart(n4);
                linearLayout$LayoutParams.setMarginEnd(n4);
                if (e == null) {
                    Log.d("QSPanel", "creating new player");
                    e = new QSMediaPlayer(this.mContext, (ViewGroup)this, this.mNotificationMediaManager, this.mForegroundExecutor, this.mBackgroundExecutor);
                    e.setListening(this.mListening);
                    if (e.isPlaying()) {
                        this.mMediaCarousel.addView(e.getView(), 0, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                    }
                    else {
                        this.mMediaCarousel.addView(e.getView(), (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                    }
                    this.mMediaPlayers.add(e);
                }
                else if (e.isPlaying()) {
                    this.mUpdateCarousel = true;
                }
                Log.d("QSPanel", "setting player session");
                e.setMediaSession(mediaSession$Token, icon, n, n2, view, statusBarNotification.getNotification(), this.mDevice);
                if (this.mMediaPlayers.size() > 0) {
                    ((View)this.mMediaCarousel.getParent()).setVisibility(0);
                    if (this.mLocalMediaManager == null) {
                        (this.mLocalMediaManager = new LocalMediaManager(this.mContext, this.mLocalBluetoothManager, new InfoMediaManager(this.mContext, null, null, this.mLocalBluetoothManager), null)).startScan();
                        this.mDevice = this.mLocalMediaManager.getCurrentConnectedDevice();
                        this.mLocalMediaManager.registerCallback(this.mDeviceCallback);
                    }
                }
                return;
            }
            QSMediaPlayer e = null;
            continue;
        }
    }
    
    protected TileRecord addTile(final QSTile tile, final boolean b) {
        final TileRecord e = new TileRecord();
        e.tile = tile;
        e.tileView = this.createTileView(tile, b);
        final QSTile.Callback callback = new QSTile.Callback() {
            @Override
            public void onAnnouncementRequested(final CharSequence charSequence) {
                if (charSequence != null) {
                    QSPanel.this.mHandler.obtainMessage(3, (Object)charSequence).sendToTarget();
                }
            }
            
            @Override
            public void onScanStateChanged(final boolean scanState) {
                e.scanState = scanState;
                final Record access$400 = QSPanel.this.mDetailRecord;
                final TileRecord val$r = e;
                if (access$400 == val$r) {
                    QSPanel.this.fireScanStateChanged(val$r.scanState);
                }
            }
            
            @Override
            public void onShowDetail(final boolean b) {
                if (QSPanel.this.shouldShowDetail()) {
                    QSPanel.this.showDetail(b, (Record)e);
                }
            }
            
            @Override
            public void onStateChanged(final State state) {
                QSPanel.this.drawTile(e, state);
            }
            
            @Override
            public void onToggleStateChanged(final boolean b) {
                if (QSPanel.this.mDetailRecord == e) {
                    QSPanel.this.fireToggleStateChanged(b);
                }
            }
        };
        e.tile.addCallback((QSTile.Callback)callback);
        e.callback = callback;
        e.tileView.init(e.tile);
        e.tile.refreshState();
        this.mRecords.add(e);
        this.mCachedSpecs = this.getTilesSpecs();
        final QSTileLayout mTileLayout = this.mTileLayout;
        if (mTileLayout != null) {
            mTileLayout.addTile(e);
        }
        return e;
    }
    
    public void clickTile(final ComponentName componentName) {
        final String spec = CustomTile.toSpec(componentName);
        for (int size = this.mRecords.size(), i = 0; i < size; ++i) {
            if (this.mRecords.get(i).tile.getTileSpec().equals(spec)) {
                this.mRecords.get(i).tile.click();
                break;
            }
        }
    }
    
    public void closeDetail() {
        final QSCustomizer mCustomizePanel = this.mCustomizePanel;
        if (mCustomizePanel != null && mCustomizePanel.isShown()) {
            this.mCustomizePanel.hide();
            return;
        }
        this.showDetail(false, this.mDetailRecord);
    }
    
    protected QSTileView createTileView(final QSTile qsTile, final boolean b) {
        return this.mHost.createTileView(qsTile, b);
    }
    
    protected void drawTile(final TileRecord tileRecord, final QSTile.State state) {
        tileRecord.tileView.onStateChanged(state);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(":");
        printWriter.println(sb.toString());
        printWriter.println("  Tile records:");
        for (final TileRecord tileRecord : this.mRecords) {
            if (tileRecord.tile instanceof Dumpable) {
                printWriter.print("    ");
                ((Dumpable)tileRecord.tile).dump(fileDescriptor, printWriter, array);
                printWriter.print("    ");
                printWriter.println(tileRecord.tileView.toString());
            }
        }
    }
    
    View getBrightnessView() {
        return this.mBrightnessView;
    }
    
    public View getDivider() {
        return this.mDivider;
    }
    
    protected String getDumpableTag() {
        return "QSPanel";
    }
    
    public QSSecurityFooter getFooter() {
        return this.mFooter;
    }
    
    public QSTileHost getHost() {
        return this.mHost;
    }
    
    protected View getMediaPanel() {
        return (View)this.mMediaCarousel;
    }
    
    public QSTileRevealController getQsTileRevealController() {
        return this.mQsTileRevealController;
    }
    
    QSTileLayout getTileLayout() {
        return this.mTileLayout;
    }
    
    QSTileView getTileView(final QSTile qsTile) {
        for (final TileRecord tileRecord : this.mRecords) {
            if (tileRecord.tile == qsTile) {
                return tileRecord.tileView;
            }
        }
        return null;
    }
    
    protected void handleShowDetail(final Record record, final boolean b) {
        if (record instanceof TileRecord) {
            this.handleShowDetailTile((TileRecord)record, b);
        }
        else {
            int x = 0;
            int y;
            if (record != null) {
                x = record.x;
                y = record.y;
            }
            else {
                y = 0;
            }
            this.handleShowDetailImpl(record, b, x, y);
        }
    }
    
    public boolean isExpanded() {
        return this.mExpanded;
    }
    
    public boolean isShowingCustomize() {
        final QSCustomizer mCustomizePanel = this.mCustomizePanel;
        return mCustomizePanel != null && mCustomizePanel.isCustomizing();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "qs_show_brightness");
        final QSTileHost mHost = this.mHost;
        if (mHost != null) {
            this.setTiles(mHost.getTiles());
        }
        final BrightnessMirrorController mBrightnessMirrorController = this.mBrightnessMirrorController;
        if (mBrightnessMirrorController != null) {
            mBrightnessMirrorController.addCallback((BrightnessMirrorController.BrightnessMirrorListener)this);
        }
        this.mDumpManager.registerDumpable(this.getDumpableTag(), this);
    }
    
    public void onBrightnessMirrorReinflated(final View view) {
        this.updateBrightnessMirror();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFooter.onConfigurationChanged();
        this.updateResources();
        this.updateBrightnessMirror();
    }
    
    protected void onDetachedFromWindow() {
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
        final QSTileHost mHost = this.mHost;
        if (mHost != null) {
            mHost.removeCallback(this);
        }
        final Iterator<TileRecord> iterator = this.mRecords.iterator();
        while (iterator.hasNext()) {
            iterator.next().tile.removeCallbacks();
        }
        final BrightnessMirrorController mBrightnessMirrorController = this.mBrightnessMirrorController;
        if (mBrightnessMirrorController != null) {
            mBrightnessMirrorController.removeCallback((BrightnessMirrorController.BrightnessMirrorListener)this);
        }
        this.mDumpManager.unregisterDumpable(this.getDumpableTag());
        final LocalMediaManager mLocalMediaManager = this.mLocalMediaManager;
        if (mLocalMediaManager != null) {
            mLocalMediaManager.stopScan();
            this.mLocalMediaManager.unregisterCallback(this.mDeviceCallback);
            this.mLocalMediaManager = null;
        }
        super.onDetachedFromWindow();
    }
    
    protected void onMeasure(int n, int n2) {
        super.onMeasure(n, n2);
        n = this.getPaddingBottom() + this.getPaddingTop();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i, n = n2) {
            final View child = this.getChildAt(i);
            n2 = n;
            if (child.getVisibility() != 8) {
                n2 = n + child.getMeasuredHeight();
            }
        }
        this.setMeasuredDimension(this.getMeasuredWidth(), n);
    }
    
    public void onTilesChanged() {
        this.setTiles(this.mHost.getTiles());
    }
    
    public void onTuningChanged(final String anObject, final String s) {
        if ("qs_show_brightness".equals(anObject)) {
            this.updateViewVisibilityForTuningValue(this.mBrightnessView, s);
        }
    }
    
    public void onVisibilityAggregated(final boolean b) {
        super.onVisibilityAggregated(b);
        if (!b && this.mUpdateCarousel) {
            for (final QSMediaPlayer qsMediaPlayer : this.mMediaPlayers) {
                if (qsMediaPlayer.isPlaying()) {
                    final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)qsMediaPlayer.getView().getLayoutParams();
                    this.mMediaCarousel.removeView(qsMediaPlayer.getView());
                    this.mMediaCarousel.addView(qsMediaPlayer.getView(), 0, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                    ((HorizontalScrollView)this.mMediaCarousel.getParent()).fullScroll(17);
                    this.mUpdateCarousel = false;
                    break;
                }
            }
        }
    }
    
    public void openDetails(final String s) {
        final QSTile tile = this.getTile(s);
        if (tile != null) {
            this.showDetailAdapter(true, tile.getDetailAdapter(), new int[] { this.getWidth() / 2, 0 });
        }
    }
    
    public void refreshAllTiles() {
        this.mBrightnessController.checkRestrictionAndSetEnabled();
        final Iterator<TileRecord> iterator = this.mRecords.iterator();
        while (iterator.hasNext()) {
            iterator.next().tile.refreshState();
        }
        this.mFooter.refreshState();
    }
    
    protected boolean removeMediaPlayer(final QSMediaPlayer o) {
        if (!this.mMediaPlayers.remove(o)) {
            return false;
        }
        this.mMediaCarousel.removeView(o.getView());
        if (this.mMediaPlayers.size() == 0) {
            ((View)this.mMediaCarousel.getParent()).setVisibility(8);
            final LocalMediaManager mLocalMediaManager = this.mLocalMediaManager;
            if (mLocalMediaManager != null) {
                mLocalMediaManager.stopScan();
                this.mLocalMediaManager.unregisterCallback(this.mDeviceCallback);
                this.mLocalMediaManager = null;
            }
        }
        return true;
    }
    
    public void setBrightnessListening(final boolean b) {
        if (b) {
            this.mBrightnessController.registerCallbacks();
        }
        else {
            this.mBrightnessController.unregisterCallbacks();
        }
    }
    
    public void setBrightnessMirror(final BrightnessMirrorController mBrightnessMirrorController) {
        final BrightnessMirrorController mBrightnessMirrorController2 = this.mBrightnessMirrorController;
        if (mBrightnessMirrorController2 != null) {
            mBrightnessMirrorController2.removeCallback((BrightnessMirrorController.BrightnessMirrorListener)this);
        }
        if ((this.mBrightnessMirrorController = mBrightnessMirrorController) != null) {
            mBrightnessMirrorController.addCallback((BrightnessMirrorController.BrightnessMirrorListener)this);
        }
        this.updateBrightnessMirror();
    }
    
    public void setCallback(final QSDetail.Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    protected void setDetailRecord(final Record mDetailRecord) {
        if (mDetailRecord == this.mDetailRecord) {
            return;
        }
        this.mDetailRecord = mDetailRecord;
        this.fireScanStateChanged(mDetailRecord instanceof TileRecord && ((TileRecord)mDetailRecord).scanState);
    }
    
    public void setExpanded(final boolean mExpanded) {
        if (this.mExpanded == mExpanded) {
            return;
        }
        this.mQSLogger.logPanelExpanded(mExpanded, this.getDumpableTag());
        if (!(this.mExpanded = mExpanded)) {
            final QSTileLayout mTileLayout = this.mTileLayout;
            if (mTileLayout instanceof PagedTileLayout) {
                ((PagedTileLayout)mTileLayout).setCurrentItem(0, false);
            }
        }
        this.mMetricsLogger.visibility(111, this.mExpanded);
        if (!this.mExpanded) {
            this.closeDetail();
        }
        else {
            this.logTiles();
        }
    }
    
    public void setFooterPageIndicator(final PageIndicator mFooterPageIndicator) {
        if (this.mTileLayout instanceof PagedTileLayout) {
            this.mFooterPageIndicator = mFooterPageIndicator;
            this.updatePageIndicator();
        }
    }
    
    void setGridContentVisibility(final boolean mGridContentVisible) {
        int visibility;
        if (mGridContentVisible) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        this.setVisibility(visibility);
        if (this.mGridContentVisible != mGridContentVisible) {
            this.mMetricsLogger.visibility(111, visibility);
        }
        this.mGridContentVisible = mGridContentVisible;
    }
    
    public void setHost(final QSTileHost qsTileHost, final QSCustomizer mCustomizePanel) {
        (this.mHost = qsTileHost).addCallback(this);
        this.setTiles(this.mHost.getTiles());
        this.mFooter.setHostEnvironment(qsTileHost);
        this.mCustomizePanel = mCustomizePanel;
        if (mCustomizePanel != null) {
            mCustomizePanel.setHost(this.mHost);
        }
    }
    
    public void setListening(final boolean b) {
        if (this.mListening == b) {
            return;
        }
        this.mListening = b;
        if (this.mTileLayout != null) {
            this.mQSLogger.logAllTilesChangeListening(b, this.getDumpableTag(), this.mCachedSpecs);
            this.mTileLayout.setListening(b);
        }
        if (this.mListening) {
            this.refreshAllTiles();
        }
        final Iterator<QSMediaPlayer> iterator = this.mMediaPlayers.iterator();
        while (iterator.hasNext()) {
            iterator.next().setListening(this.mListening);
        }
    }
    
    public void setListening(final boolean b, final boolean b2) {
        this.setListening(b && b2);
        this.getFooter().setListening(b);
        this.setBrightnessListening(b);
    }
    
    public void setMargins(final int n) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child != this.mTileLayout) {
                final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)child.getLayoutParams();
                linearLayout$LayoutParams.leftMargin = n;
                linearLayout$LayoutParams.rightMargin = n;
            }
        }
    }
    
    public void setPageListener(final PagedTileLayout.PageListener pageListener) {
        final QSTileLayout mTileLayout = this.mTileLayout;
        if (mTileLayout instanceof PagedTileLayout) {
            ((PagedTileLayout)mTileLayout).setPageListener(pageListener);
        }
    }
    
    public void setTiles(final Collection<QSTile> collection) {
        this.setTiles(collection, false);
    }
    
    public void setTiles(final Collection<QSTile> collection, final boolean b) {
        if (!b) {
            this.mQsTileRevealController.updateRevealedTiles(collection);
        }
        for (final TileRecord tileRecord : this.mRecords) {
            this.mTileLayout.removeTile(tileRecord);
            tileRecord.tile.removeCallback(tileRecord.callback);
        }
        this.mRecords.clear();
        this.mCachedSpecs = "";
        final Iterator<QSTile> iterator2 = collection.iterator();
        while (iterator2.hasNext()) {
            this.addTile(iterator2.next(), b);
        }
    }
    
    protected boolean shouldShowDetail() {
        return this.mExpanded;
    }
    
    protected void showDetail(final boolean b, final Record record) {
        this.mHandler.obtainMessage(1, (int)(b ? 1 : 0), 0, (Object)record).sendToTarget();
    }
    
    public void showDetailAdapter(final boolean b, final DetailAdapter detailAdapter, final int[] array) {
        final int n = array[0];
        final int n2 = array[1];
        ((View)this.getParent()).getLocationInWindow(array);
        final Record record = new Record();
        record.detailAdapter = detailAdapter;
        record.x = n - array[0];
        record.y = n2 - array[1];
        array[0] = n;
        array[1] = n2;
        this.showDetail(b, record);
    }
    
    public void showDeviceMonitoringDialog() {
        this.mFooter.showDeviceMonitoringDialog();
    }
    
    public void showEdit(final View view) {
        view.post((Runnable)new Runnable() {
            @Override
            public void run() {
                if (QSPanel.this.mCustomizePanel != null && !QSPanel.this.mCustomizePanel.isCustomizing()) {
                    final int[] locationOnScreen = view.getLocationOnScreen();
                    QSPanel.this.mCustomizePanel.show(locationOnScreen[0] + view.getWidth() / 2, locationOnScreen[1] + view.getHeight() / 2);
                }
            }
        });
    }
    
    public void updateBrightnessMirror() {
        if (this.mBrightnessMirrorController != null) {
            final ToggleSliderView toggleSliderView = (ToggleSliderView)this.findViewById(R$id.brightness_slider);
            toggleSliderView.setMirror((ToggleSliderView)this.mBrightnessMirrorController.getMirror().findViewById(R$id.brightness_slider));
            toggleSliderView.setMirrorController(this.mBrightnessMirrorController);
        }
    }
    
    public void updateResources() {
        final Resources resources = this.mContext.getResources();
        this.setPadding(0, resources.getDimensionPixelSize(R$dimen.qs_panel_padding_top), 0, resources.getDimensionPixelSize(R$dimen.qs_panel_padding_bottom));
        this.updatePageIndicator();
        if (this.mListening) {
            this.refreshAllTiles();
        }
        final QSTileLayout mTileLayout = this.mTileLayout;
        if (mTileLayout != null) {
            mTileLayout.updateResources();
        }
    }
    
    private class H extends Handler
    {
        public void handleMessage(final Message message) {
            final int what = message.what;
            boolean b = true;
            if (what == 1) {
                final QSPanel this$0 = QSPanel.this;
                final Record record = (Record)message.obj;
                if (message.arg1 == 0) {
                    b = false;
                }
                this$0.handleShowDetail(record, b);
            }
            else if (what == 3) {
                QSPanel.this.announceForAccessibility((CharSequence)message.obj);
            }
        }
    }
    
    public interface QSTileLayout
    {
        void addTile(final TileRecord p0);
        
        int getNumVisibleTiles();
        
        int getOffsetTop(final TileRecord p0);
        
        void removeTile(final TileRecord p0);
        
        default void restoreInstanceState(final Bundle bundle) {
        }
        
        default void saveInstanceState(final Bundle bundle) {
        }
        
        default void setExpansion(final float n) {
        }
        
        void setListening(final boolean p0);
        
        boolean updateResources();
    }
    
    protected static class Record
    {
        DetailAdapter detailAdapter;
        int x;
        int y;
    }
    
    public static final class TileRecord extends Record
    {
        public QSTile.Callback callback;
        public boolean scanState;
        public QSTile tile;
        public QSTileView tileView;
    }
}
