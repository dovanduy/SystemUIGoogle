// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.customize;

import android.content.pm.ServiceInfo;
import java.util.List;
import android.content.pm.PackageManager;
import com.android.systemui.qs.external.CustomTile;
import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import android.app.ActivityManager;
import android.content.Intent;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.Button;
import java.util.Iterator;
import com.android.systemui.plugins.qs.QSTile;
import android.os.Build;
import java.util.Collection;
import java.util.Arrays;
import android.provider.Settings$Secure;
import com.android.systemui.R$string;
import com.android.systemui.qs.QSTileHost;
import java.util.ArrayList;
import android.util.ArraySet;
import android.content.Context;
import java.util.concurrent.Executor;

public class TileQueryHelper
{
    private final Executor mBgExecutor;
    private final Context mContext;
    private boolean mFinished;
    private TileStateListener mListener;
    private final Executor mMainExecutor;
    private final ArraySet<String> mSpecs;
    private final ArrayList<TileInfo> mTiles;
    
    public TileQueryHelper(final Context mContext, final Executor mMainExecutor, final Executor mBgExecutor) {
        this.mTiles = new ArrayList<TileInfo>();
        this.mSpecs = (ArraySet<String>)new ArraySet();
        this.mContext = mContext;
        this.mMainExecutor = mMainExecutor;
        this.mBgExecutor = mBgExecutor;
    }
    
    private void addCurrentAndStockTiles(final QSTileHost qsTileHost) {
        final String string = this.mContext.getString(R$string.quick_settings_tiles_stock);
        String string2 = Settings$Secure.getString(this.mContext.getContentResolver(), "sysui_qs_tiles");
        final ArrayList<String> list = new ArrayList<String>();
        if (string2 != null) {
            list.addAll(Arrays.asList(string2.split(",")));
        }
        else {
            string2 = "";
        }
        for (final String s : string.split(",")) {
            if (!string2.contains(s)) {
                list.add(s);
            }
        }
        if (Build.IS_DEBUGGABLE && !string2.contains("dbg:mem")) {
            list.add("dbg:mem");
        }
        final ArrayList<QSTile> list2 = new ArrayList<QSTile>();
        for (final String tileSpec : list) {
            if (tileSpec.startsWith("custom(")) {
                continue;
            }
            final QSTile tile = qsTileHost.createTile(tileSpec);
            if (tile == null) {
                continue;
            }
            if (!tile.isAvailable()) {
                tile.destroy();
            }
            else {
                tile.setListening(this, true);
                tile.refreshState();
                tile.setListening(this, false);
                tile.setTileSpec(tileSpec);
                list2.add(tile);
            }
        }
        this.mBgExecutor.execute(new _$$Lambda$TileQueryHelper$sMzDfkcNEMwHLLe95kLdEn4WPkc(this, list2));
    }
    
    private void addPackageTiles(final QSTileHost qsTileHost) {
        this.mBgExecutor.execute(new _$$Lambda$TileQueryHelper$_7aqDrq4N73id_i9gI_WE72bklw(this, qsTileHost));
    }
    
    private void addTile(final String spec, final CharSequence charSequence, final QSTile.State state, final boolean isSystem) {
        if (this.mSpecs.contains((Object)spec)) {
            return;
        }
        final TileInfo e = new TileInfo();
        e.state = state;
        state.dualTarget = false;
        state.expandedAccessibilityClassName = Button.class.getName();
        e.spec = spec;
        final QSTile.State state2 = e.state;
        CharSequence secondaryLabel = null;
        Label_0076: {
            if (!isSystem) {
                secondaryLabel = charSequence;
                if (!TextUtils.equals(state.label, charSequence)) {
                    break Label_0076;
                }
            }
            secondaryLabel = null;
        }
        state2.secondaryLabel = secondaryLabel;
        e.isSystem = isSystem;
        this.mTiles.add(e);
        this.mSpecs.add((Object)spec);
    }
    
    private void createStateAndAddTile(final String s, final Drawable drawable, final CharSequence charSequence, final CharSequence charSequence2) {
        final QSTile.State state = new QSTile.State();
        state.state = 1;
        state.label = charSequence;
        state.contentDescription = charSequence;
        state.icon = new QSTileImpl.DrawableIcon(drawable);
        this.addTile(s, charSequence2, state, false);
    }
    
    private QSTile.State getState(final Collection<QSTile> collection, final String s) {
        for (final QSTile qsTile : collection) {
            if (s.equals(qsTile.getTileSpec())) {
                return qsTile.getState().copy();
            }
        }
        return null;
    }
    
    private void notifyTilesChanged(final boolean b) {
        this.mMainExecutor.execute(new _$$Lambda$TileQueryHelper$td1yVFso44MefBPUi6jpDHx3Yoc(this, new ArrayList((Collection<? extends E>)this.mTiles), b));
    }
    
    public boolean isFinished() {
        return this.mFinished;
    }
    
    public void queryTiles(final QSTileHost qsTileHost) {
        this.mTiles.clear();
        this.mSpecs.clear();
        this.mFinished = false;
        this.addCurrentAndStockTiles(qsTileHost);
        this.addPackageTiles(qsTileHost);
    }
    
    public void setListener(final TileStateListener mListener) {
        this.mListener = mListener;
    }
    
    public static class TileInfo
    {
        public boolean isSystem;
        public String spec;
        public QSTile.State state;
    }
    
    public interface TileStateListener
    {
        void onTilesChanged(final List<TileInfo> p0);
    }
}
