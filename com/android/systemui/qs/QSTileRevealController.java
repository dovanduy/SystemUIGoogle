// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import java.util.Iterator;
import com.android.systemui.plugins.qs.QSTile;
import java.util.Collection;
import com.android.systemui.Prefs;
import java.util.Collections;
import java.util.Set;
import android.util.ArraySet;
import android.os.Handler;
import android.content.Context;

public class QSTileRevealController
{
    private final Context mContext;
    private final Handler mHandler;
    private final PagedTileLayout mPagedTileLayout;
    private final QSPanel mQSPanel;
    private final Runnable mRevealQsTiles;
    private final ArraySet<String> mTilesToReveal;
    
    QSTileRevealController(final Context mContext, final QSPanel mqsPanel, final PagedTileLayout mPagedTileLayout) {
        this.mTilesToReveal = (ArraySet<String>)new ArraySet();
        this.mHandler = new Handler();
        this.mRevealQsTiles = new Runnable() {
            @Override
            public void run() {
                QSTileRevealController.this.mPagedTileLayout.startTileReveal((Set<String>)QSTileRevealController.this.mTilesToReveal, new _$$Lambda$QSTileRevealController$1$gTMt7U_W3YL6K0ko8X3nSQ3r95I(this));
            }
        };
        this.mContext = mContext;
        this.mQSPanel = mqsPanel;
        this.mPagedTileLayout = mPagedTileLayout;
    }
    
    private void addTileSpecsToRevealed(final ArraySet<String> set) {
        final ArraySet set2 = new ArraySet((Collection)Prefs.getStringSet(this.mContext, "QsTileSpecsRevealed", Collections.EMPTY_SET));
        set2.addAll((ArraySet)set);
        Prefs.putStringSet(this.mContext, "QsTileSpecsRevealed", (Set<String>)set2);
    }
    
    public void setExpansion(final float n) {
        if (n == 1.0f) {
            this.mHandler.postDelayed(this.mRevealQsTiles, 500L);
        }
        else {
            this.mHandler.removeCallbacks(this.mRevealQsTiles);
        }
    }
    
    public void updateRevealedTiles(final Collection<QSTile> collection) {
        final ArraySet set = new ArraySet();
        final Iterator<QSTile> iterator = collection.iterator();
        while (iterator.hasNext()) {
            set.add((Object)iterator.next().getTileSpec());
        }
        final Set<String> stringSet = Prefs.getStringSet(this.mContext, "QsTileSpecsRevealed", Collections.EMPTY_SET);
        if (!stringSet.isEmpty() && !this.mQSPanel.isShowingCustomize()) {
            set.removeAll((Collection)stringSet);
            this.mTilesToReveal.addAll(set);
        }
        else {
            this.addTileSpecsToRevealed((ArraySet<String>)set);
        }
    }
}
