// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.View;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import android.util.Log;
import android.metrics.LogMaker;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import java.util.Set;
import com.android.internal.logging.MetricsLogger;
import android.content.Context;

public class NotificationBlockingHelperManager
{
    private ExpandableNotificationRow mBlockingHelperRow;
    private final Context mContext;
    private boolean mIsShadeExpanded;
    private final MetricsLogger mMetricsLogger;
    private Set<String> mNonBlockablePkgs;
    private final NotificationEntryManager mNotificationEntryManager;
    private final NotificationGutsManager mNotificationGutsManager;
    
    public NotificationBlockingHelperManager(final Context mContext, final NotificationGutsManager mNotificationGutsManager, final NotificationEntryManager mNotificationEntryManager, final MetricsLogger mMetricsLogger) {
        this.mContext = mContext;
        this.mNotificationGutsManager = mNotificationGutsManager;
        this.mNotificationEntryManager = mNotificationEntryManager;
        this.mMetricsLogger = mMetricsLogger;
        Collections.addAll(this.mNonBlockablePkgs = new HashSet<String>(), this.mContext.getResources().getStringArray(17236059));
    }
    
    private LogMaker getLogMaker() {
        return this.mBlockingHelperRow.getEntry().getSbn().getLogMaker().setCategory(1621);
    }
    
    private String makeChannelKey(final String str, final String str2) {
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(":");
        sb.append(str2);
        return sb.toString();
    }
    
    boolean dismissCurrentBlockingHelper() {
        if (!this.isBlockingHelperRowNull()) {
            if (!this.mBlockingHelperRow.isBlockingHelperShowing()) {
                Log.e("BlockingHelper", "Manager.dismissCurrentBlockingHelper: Non-null row is not showing a blocking helper");
            }
            this.mBlockingHelperRow.setBlockingHelperShowing(false);
            if (this.mBlockingHelperRow.isAttachedToWindow()) {
                this.mNotificationEntryManager.updateNotifications("dismissCurrentBlockingHelper");
            }
            this.mBlockingHelperRow = null;
            return true;
        }
        return false;
    }
    
    boolean isBlockingHelperRowNull() {
        return this.mBlockingHelperRow == null;
    }
    
    public boolean isNonblockable(final String s, final String s2) {
        return this.mNonBlockablePkgs.contains(s) || this.mNonBlockablePkgs.contains(this.makeChannelKey(s, s2));
    }
    
    boolean perhapsShowBlockingHelper(final ExpandableNotificationRow mBlockingHelperRow, final NotificationMenuRowPlugin notificationMenuRowPlugin) {
        if (mBlockingHelperRow.getEntry().getUserSentiment() == -1) {
            if (this.mIsShadeExpanded && !mBlockingHelperRow.getIsNonblockable() && (!mBlockingHelperRow.isChildInGroup() || mBlockingHelperRow.isOnlyChildInGroup()) && mBlockingHelperRow.getNumUniqueChannels() <= 1) {
                this.dismissCurrentBlockingHelper();
                (this.mBlockingHelperRow = mBlockingHelperRow).setBlockingHelperShowing(true);
                this.mMetricsLogger.write(this.getLogMaker().setSubtype(3));
                this.mNotificationGutsManager.openGuts((View)this.mBlockingHelperRow, 0, 0, notificationMenuRowPlugin.getLongpressMenuItem(this.mContext));
                this.mMetricsLogger.count("blocking_helper_shown", 1);
                return true;
            }
        }
        return false;
    }
    
    void setBlockingHelperRowForTest(final ExpandableNotificationRow mBlockingHelperRow) {
        this.mBlockingHelperRow = mBlockingHelperRow;
    }
    
    void setNonBlockablePkgs(final String[] elements) {
        Collections.addAll(this.mNonBlockablePkgs = new HashSet<String>(), elements);
    }
    
    public void setNotificationShadeExpanded(final float n) {
        this.mIsShadeExpanded = (n > 0.0f);
    }
}
