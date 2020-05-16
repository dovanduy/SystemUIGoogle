// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.qs.external.TileServices;
import com.android.systemui.qs.logging.QSLogger;
import android.content.Context;

public interface QSHost
{
    void collapsePanels();
    
    Context getContext();
    
    QSLogger getQSLogger();
    
    TileServices getTileServices();
    
    Context getUserContext();
    
    int indexOf(final String p0);
    
    void openPanels();
    
    void removeTile(final String p0);
    
    void unmarkTileAsAutoAdded(final String p0);
    
    void warn(final String p0, final Throwable p1);
    
    public interface Callback
    {
        void onTilesChanged();
    }
}
