// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tileimpl;

import android.util.SparseArray;
import android.os.Message;
import android.graphics.drawable.Drawable;
import com.android.systemui.Prefs;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.ActivityStarter;
import android.content.Intent;
import com.android.systemui.plugins.qs.DetailAdapter;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.plugins.qs.QSIconView;
import android.metrics.LogMaker;
import com.android.settingslib.RestrictedLockUtilsInternal;
import android.app.ActivityManager;
import java.util.Iterator;
import com.android.systemui.qs.PagedTileLayout;
import androidx.lifecycle.Lifecycle;
import com.android.settingslib.Utils;
import com.android.systemui.qs.tiles.QSSettingsControllerKt;
import com.android.systemui.Dependency;
import android.os.Looper;
import android.util.Log;
import android.os.Handler;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.tiles.QSSettingsPanel;
import com.android.systemui.qs.logging.QSLogger;
import com.android.internal.logging.MetricsLogger;
import android.util.ArraySet;
import androidx.lifecycle.LifecycleRegistry;
import com.android.systemui.qs.QSHost;
import com.android.settingslib.RestrictedLockUtils;
import android.content.Context;
import java.util.ArrayList;
import com.android.systemui.Dumpable;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.plugins.qs.QSTile;

public abstract class QSTileImpl<TState extends State> implements QSTile, LifecycleOwner, Dumpable
{
    protected static final Object ARG_SHOW_TRANSIENT_ENABLING;
    protected static final boolean DEBUG;
    protected final String TAG;
    private boolean mAnnounceNextStateChange;
    private final ArrayList<Callback> mCallbacks;
    protected final Context mContext;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    protected H mHandler;
    protected final QSHost mHost;
    private int mIsFullQs;
    private final LifecycleRegistry mLifecycle;
    private final ArraySet<Object> mListeners;
    private final MetricsLogger mMetricsLogger;
    private final QSLogger mQSLogger;
    protected final QSSettingsPanel mQSSettingsPanelOption;
    private boolean mShowingDetail;
    private final Object mStaleListener;
    protected TState mState;
    private final StatusBarStateController mStatusBarStateController;
    private String mTileSpec;
    private TState mTmpState;
    protected final Handler mUiHandler;
    
    static {
        DEBUG = Log.isLoggable("Tile", 3);
        ARG_SHOW_TRANSIENT_ENABLING = new Object();
    }
    
    protected QSTileImpl(final QSHost mHost) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Tile.");
        sb.append(this.getClass().getSimpleName());
        this.TAG = sb.toString();
        this.mHandler = new H(Dependency.get(Dependency.BG_LOOPER));
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mListeners = (ArraySet<Object>)new ArraySet();
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mStatusBarStateController = Dependency.get(StatusBarStateController.class);
        this.mCallbacks = new ArrayList<Callback>();
        this.mStaleListener = new Object();
        this.mLifecycle = new LifecycleRegistry(this);
        this.mHost = mHost;
        this.mContext = mHost.getContext();
        this.mState = this.newTileState();
        this.mTmpState = this.newTileState();
        this.mQSSettingsPanelOption = QSSettingsControllerKt.getQSSettingsPanelOption();
        this.mQSLogger = mHost.getQSLogger();
    }
    
    public static int getColorForState(final Context context, final int i) {
        if (i == 0) {
            return Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16842808));
        }
        if (i == 1) {
            return Utils.getColorAttrDefaultColor(context, 16842808);
        }
        if (i != 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid state ");
            sb.append(i);
            Log.e("QSTile", sb.toString());
            return 0;
        }
        return Utils.getColorAttrDefaultColor(context, 16843827);
    }
    
    private void handleAddCallback(final Callback e) {
        this.mCallbacks.add(e);
        e.onStateChanged(this.mState);
    }
    
    private void handleRemoveCallback(final Callback o) {
        this.mCallbacks.remove(o);
    }
    
    private void handleRemoveCallbacks() {
        this.mCallbacks.clear();
    }
    
    private void handleScanStateChanged(final boolean b) {
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            this.mCallbacks.get(i).onScanStateChanged(b);
        }
    }
    
    private void handleSetListeningInternal(final Object o, final boolean b) {
        if (b) {
            if (this.mListeners.add(o) && this.mListeners.size() == 1) {
                if (QSTileImpl.DEBUG) {
                    Log.d(this.TAG, "handleSetListening true");
                }
                this.mLifecycle.markState(Lifecycle.State.RESUMED);
                this.handleSetListening(b);
                this.refreshState();
            }
        }
        else if (this.mListeners.remove(o) && this.mListeners.size() == 0) {
            if (QSTileImpl.DEBUG) {
                Log.d(this.TAG, "handleSetListening false");
            }
            this.mLifecycle.markState(Lifecycle.State.DESTROYED);
            this.handleSetListening(b);
        }
        this.updateIsFullQs();
    }
    
    private void handleShowDetail(final boolean mShowingDetail) {
        this.mShowingDetail = mShowingDetail;
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            this.mCallbacks.get(i).onShowDetail(mShowingDetail);
        }
    }
    
    private void handleStateChanged() {
        final boolean shouldAnnouncementBeDelayed = this.shouldAnnouncementBeDelayed();
        final int size = this.mCallbacks.size();
        final boolean b = false;
        if (size != 0) {
            for (int i = 0; i < this.mCallbacks.size(); ++i) {
                this.mCallbacks.get(i).onStateChanged(this.mState);
            }
            if (this.mAnnounceNextStateChange && !shouldAnnouncementBeDelayed) {
                final String composeChangeAnnouncement = this.composeChangeAnnouncement();
                if (composeChangeAnnouncement != null) {
                    this.mCallbacks.get(0).onAnnouncementRequested(composeChangeAnnouncement);
                }
            }
        }
        boolean mAnnounceNextStateChange = b;
        if (this.mAnnounceNextStateChange) {
            mAnnounceNextStateChange = b;
            if (shouldAnnouncementBeDelayed) {
                mAnnounceNextStateChange = true;
            }
        }
        this.mAnnounceNextStateChange = mAnnounceNextStateChange;
    }
    
    private void handleToggleStateChanged(final boolean b) {
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            this.mCallbacks.get(i).onToggleStateChanged(b);
        }
    }
    
    private void updateIsFullQs() {
        final Iterator iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            if (PagedTileLayout.TilePage.class.equals(iterator.next().getClass())) {
                this.mIsFullQs = 1;
                return;
            }
        }
        this.mIsFullQs = 0;
    }
    
    @Override
    public void addCallback(final Callback callback) {
        this.mHandler.obtainMessage(1, (Object)callback).sendToTarget();
    }
    
    protected void checkIfRestrictionEnforcedByAdminOnly(final State state, final String s) {
        final RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, s, ActivityManager.getCurrentUser());
        if (checkIfRestrictionEnforced != null && !RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, s, ActivityManager.getCurrentUser())) {
            state.disabledByPolicy = true;
            this.mEnforcedAdmin = checkIfRestrictionEnforced;
        }
        else {
            state.disabledByPolicy = false;
            this.mEnforcedAdmin = null;
        }
    }
    
    @Override
    public void click() {
        this.mMetricsLogger.write(this.populate(new LogMaker(925).setType(4).addTaggedData(1592, (Object)this.mStatusBarStateController.getState())));
        this.mQSLogger.logTileClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
        this.mHandler.sendEmptyMessage(2);
    }
    
    protected String composeChangeAnnouncement() {
        return null;
    }
    
    @Override
    public QSIconView createTileView(final Context context) {
        return new QSIconViewImpl(context);
    }
    
    @Override
    public void destroy() {
        this.mHandler.sendEmptyMessage(10);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(":");
        printWriter.println(sb.toString());
        printWriter.print("    ");
        printWriter.println(this.getState().toString());
    }
    
    public void fireScanStateChanged(final boolean b) {
        this.mHandler.obtainMessage(9, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    public void fireToggleStateChanged(final boolean b) {
        this.mHandler.obtainMessage(8, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    @Override
    public DetailAdapter getDetailAdapter() {
        return null;
    }
    
    public QSHost getHost() {
        return this.mHost;
    }
    
    @Override
    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }
    
    public abstract Intent getLongClickIntent();
    
    @Override
    public abstract int getMetricsCategory();
    
    protected long getStaleTimeout() {
        return 600000L;
    }
    
    @Override
    public TState getState() {
        return this.mState;
    }
    
    @Override
    public String getTileSpec() {
        return this.mTileSpec;
    }
    
    protected abstract void handleClick();
    
    protected void handleDestroy() {
        this.mQSLogger.logTileDestroyed(this.mTileSpec, "Handle destroy");
        if (this.mListeners.size() != 0) {
            this.handleSetListening(false);
        }
        this.mCallbacks.clear();
        this.mHandler.removeCallbacksAndMessages((Object)null);
    }
    
    protected void handleLongClick() {
        if (this.mQSSettingsPanelOption == QSSettingsPanel.USE_DETAIL) {
            this.showDetail(true);
            return;
        }
        Dependency.get(ActivityStarter.class).postStartActivityDismissingKeyguard(this.getLongClickIntent(), 0);
    }
    
    protected void handleRefreshState(final Object o) {
        this.handleUpdateState(this.mTmpState, o);
        if (((State)this.mTmpState).copyTo((State)this.mState)) {
            this.mQSLogger.logTileUpdated(this.mTileSpec, this.mState);
            this.handleStateChanged();
        }
        this.mHandler.removeMessages(14);
        this.mHandler.sendEmptyMessageDelayed(14, this.getStaleTimeout());
        this.setListening(this.mStaleListener, false);
    }
    
    protected void handleSecondaryClick() {
        this.handleClick();
    }
    
    protected void handleSetListening(final boolean b) {
        final String mTileSpec = this.mTileSpec;
        if (mTileSpec != null) {
            this.mQSLogger.logTileChangeListening(mTileSpec, b);
        }
    }
    
    @VisibleForTesting
    protected void handleStale() {
        this.setListening(this.mStaleListener, true);
    }
    
    protected abstract void handleUpdateState(final TState p0, final Object p1);
    
    protected void handleUserSwitch(final int n) {
        this.handleRefreshState(null);
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    protected boolean isShowingDetail() {
        return this.mShowingDetail;
    }
    
    @Override
    public void longClick() {
        this.mMetricsLogger.write(this.populate(new LogMaker(366).setType(4).addTaggedData(1592, (Object)this.mStatusBarStateController.getState())));
        this.mQSLogger.logTileLongClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
        this.mHandler.sendEmptyMessage(4);
        Prefs.putInt(this.mContext, "QsLongPressTooltipShownCount", 2);
    }
    
    public abstract TState newTileState();
    
    @Override
    public LogMaker populate(final LogMaker logMaker) {
        final State mState = this.mState;
        if (mState instanceof BooleanState) {
            logMaker.addTaggedData(928, (Object)(int)(((BooleanState)mState).value ? 1 : 0));
        }
        return logMaker.setSubtype(this.getMetricsCategory()).addTaggedData(1593, (Object)this.mIsFullQs).addTaggedData(927, (Object)this.mHost.indexOf(this.mTileSpec));
    }
    
    @Override
    public void refreshState() {
        this.refreshState(null);
    }
    
    protected final void refreshState(final Object o) {
        this.mHandler.obtainMessage(5, o).sendToTarget();
    }
    
    @Override
    public void removeCallback(final Callback callback) {
        this.mHandler.obtainMessage(12, (Object)callback).sendToTarget();
    }
    
    @Override
    public void removeCallbacks() {
        this.mHandler.sendEmptyMessage(11);
    }
    
    protected final void resetStates() {
        this.mState = this.newTileState();
        this.mTmpState = this.newTileState();
    }
    
    @Override
    public void secondaryClick() {
        this.mMetricsLogger.write(this.populate(new LogMaker(926).setType(4).addTaggedData(1592, (Object)this.mStatusBarStateController.getState())));
        this.mQSLogger.logTileSecondaryClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
        this.mHandler.sendEmptyMessage(3);
    }
    
    @Override
    public void setDetailListening(final boolean b) {
    }
    
    @Override
    public void setListening(final Object o, final boolean b) {
        this.mHandler.obtainMessage(13, (int)(b ? 1 : 0), 0, o).sendToTarget();
    }
    
    @Override
    public void setTileSpec(final String mTileSpec) {
        this.mTileSpec = mTileSpec;
    }
    
    protected boolean shouldAnnouncementBeDelayed() {
        return false;
    }
    
    public void showDetail(final boolean b) {
        this.mHandler.obtainMessage(6, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    @Override
    public void userSwitch(final int n) {
        this.mHandler.obtainMessage(7, n, 0).sendToTarget();
    }
    
    public static class DrawableIcon extends Icon
    {
        protected final Drawable mDrawable;
        protected final Drawable mInvisibleDrawable;
        
        public DrawableIcon(final Drawable mDrawable) {
            this.mDrawable = mDrawable;
            this.mInvisibleDrawable = mDrawable.getConstantState().newDrawable();
        }
        
        @Override
        public Drawable getDrawable(final Context context) {
            return this.mDrawable;
        }
        
        @Override
        public Drawable getInvisibleDrawable(final Context context) {
            return this.mInvisibleDrawable;
        }
        
        @Override
        public String toString() {
            return "DrawableIcon";
        }
    }
    
    protected final class H extends Handler
    {
        @VisibleForTesting
        protected H(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            String str;
            final String s = str = null;
            try {
                final int what = message.what;
                final boolean b = true;
                final boolean b2 = true;
                final boolean b3 = true;
                boolean b4 = true;
                if (what == 1) {
                    str = "handleAddCallback";
                    QSTileImpl.this.handleAddCallback((Callback)message.obj);
                }
                else {
                    str = s;
                    if (message.what == 11) {
                        str = "handleRemoveCallbacks";
                        QSTileImpl.this.handleRemoveCallbacks();
                    }
                    else {
                        str = s;
                        if (message.what == 12) {
                            str = "handleRemoveCallback";
                            QSTileImpl.this.handleRemoveCallback((Callback)message.obj);
                        }
                        else {
                            str = s;
                            if (message.what == 2) {
                                final String s2 = str = "handleClick";
                                if (QSTileImpl.this.mState.disabledByPolicy) {
                                    str = s2;
                                    final Intent showAdminSupportDetailsIntent = RestrictedLockUtils.getShowAdminSupportDetailsIntent(QSTileImpl.this.mContext, QSTileImpl.this.mEnforcedAdmin);
                                    str = s2;
                                    Dependency.get(ActivityStarter.class).postStartActivityDismissingKeyguard(showAdminSupportDetailsIntent, 0);
                                }
                                else {
                                    str = s2;
                                    QSTileImpl.this.handleClick();
                                }
                            }
                            else {
                                str = s;
                                if (message.what == 3) {
                                    str = "handleSecondaryClick";
                                    QSTileImpl.this.handleSecondaryClick();
                                }
                                else {
                                    str = s;
                                    if (message.what == 4) {
                                        str = "handleLongClick";
                                        QSTileImpl.this.handleLongClick();
                                    }
                                    else {
                                        str = s;
                                        if (message.what == 5) {
                                            str = "handleRefreshState";
                                            QSTileImpl.this.handleRefreshState(message.obj);
                                        }
                                        else {
                                            str = s;
                                            if (message.what == 6) {
                                                final String s3 = str = "handleShowDetail";
                                                final QSTileImpl this$0 = QSTileImpl.this;
                                                str = s3;
                                                if (message.arg1 == 0) {
                                                    b4 = false;
                                                }
                                                str = s3;
                                                this$0.handleShowDetail(b4);
                                            }
                                            else {
                                                str = s;
                                                if (message.what == 7) {
                                                    str = "handleUserSwitch";
                                                    QSTileImpl.this.handleUserSwitch(message.arg1);
                                                }
                                                else {
                                                    str = s;
                                                    if (message.what == 8) {
                                                        final String s4 = str = "handleToggleStateChanged";
                                                        final QSTileImpl this$2 = QSTileImpl.this;
                                                        str = s4;
                                                        final boolean b5 = message.arg1 != 0 && b;
                                                        str = s4;
                                                        this$2.handleToggleStateChanged(b5);
                                                    }
                                                    else {
                                                        str = s;
                                                        if (message.what == 9) {
                                                            final String s5 = str = "handleScanStateChanged";
                                                            final QSTileImpl this$3 = QSTileImpl.this;
                                                            str = s5;
                                                            final boolean b6 = message.arg1 != 0 && b2;
                                                            str = s5;
                                                            this$3.handleScanStateChanged(b6);
                                                        }
                                                        else {
                                                            str = s;
                                                            if (message.what == 10) {
                                                                str = "handleDestroy";
                                                                QSTileImpl.this.handleDestroy();
                                                            }
                                                            else {
                                                                str = s;
                                                                if (message.what == 13) {
                                                                    final String s6 = str = "handleSetListeningInternal";
                                                                    final QSTileImpl this$4 = QSTileImpl.this;
                                                                    str = s6;
                                                                    final Object obj = message.obj;
                                                                    str = s6;
                                                                    final boolean b7 = message.arg1 != 0 && b3;
                                                                    str = s6;
                                                                    this$4.handleSetListeningInternal(obj, b7);
                                                                }
                                                                else {
                                                                    str = s;
                                                                    if (message.what != 14) {
                                                                        str = s;
                                                                        str = s;
                                                                        str = s;
                                                                        final StringBuilder sb = new StringBuilder();
                                                                        str = s;
                                                                        sb.append("Unknown msg: ");
                                                                        str = s;
                                                                        sb.append(message.what);
                                                                        str = s;
                                                                        final IllegalArgumentException ex = new IllegalArgumentException(sb.toString());
                                                                        str = s;
                                                                        throw ex;
                                                                    }
                                                                    str = "handleStale";
                                                                    QSTileImpl.this.handleStale();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            finally {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Error in ");
                sb2.append(str);
                final String string = sb2.toString();
                final Throwable t;
                Log.w(QSTileImpl.this.TAG, string, t);
                QSTileImpl.this.mHost.warn(string, t);
            }
        }
    }
    
    public static class ResourceIcon extends Icon
    {
        private static final SparseArray<Icon> ICONS;
        protected final int mResId;
        
        static {
            ICONS = new SparseArray();
        }
        
        private ResourceIcon(final int mResId) {
            this.mResId = mResId;
        }
        
        public static Icon get(final int n) {
            synchronized (ResourceIcon.class) {
                Icon icon;
                if ((icon = (Icon)ResourceIcon.ICONS.get(n)) == null) {
                    icon = new ResourceIcon(n);
                    ResourceIcon.ICONS.put(n, (Object)icon);
                }
                return icon;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof ResourceIcon && ((ResourceIcon)o).mResId == this.mResId;
        }
        
        @Override
        public Drawable getDrawable(final Context context) {
            return context.getDrawable(this.mResId);
        }
        
        @Override
        public Drawable getInvisibleDrawable(final Context context) {
            return context.getDrawable(this.mResId);
        }
        
        @Override
        public String toString() {
            return String.format("ResourceIcon[resId=0x%08x]", this.mResId);
        }
    }
}
