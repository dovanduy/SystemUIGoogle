// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import android.widget.Toast;
import com.android.systemui.R$string;
import java.util.function.Consumer;
import android.hardware.display.DisplayManager;
import android.graphics.Point;
import android.graphics.Rect;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.Dependency;
import com.android.systemui.shared.recents.IOverviewProxy;
import android.os.RemoteException;
import android.util.Log;
import android.app.ActivityManager$RunningTaskInfo;
import android.app.trust.TrustManager;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import com.android.systemui.shared.system.TaskStackChangeListener;
import android.os.Handler;
import com.android.systemui.stackdivider.Divider;
import java.util.Optional;
import android.content.Context;

public class OverviewProxyRecentsImpl implements RecentsImplementation
{
    private Context mContext;
    private final Optional<Divider> mDividerOptional;
    private Handler mHandler;
    private TaskStackChangeListener mListener;
    private OverviewProxyService mOverviewProxyService;
    private final Lazy<StatusBar> mStatusBarLazy;
    private TrustManager mTrustManager;
    
    public OverviewProxyRecentsImpl(final Optional<Lazy<StatusBar>> optional, final Optional<Divider> mDividerOptional) {
        this.mListener = new TaskStackChangeListener() {
            @Override
            public void onActivityRestartAttempt(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo, final boolean b, final boolean b2) {
                if (activityManager$RunningTaskInfo.configuration.windowConfiguration.getWindowingMode() != 3) {
                    return;
                }
                if (b) {
                    OverviewProxyRecentsImpl.this.showRecentApps(false);
                }
            }
        };
        this.mStatusBarLazy = optional.orElse(null);
        this.mDividerOptional = mDividerOptional;
    }
    
    @Override
    public void hideRecentApps(final boolean b, final boolean b2) {
        final IOverviewProxy proxy = this.mOverviewProxyService.getProxy();
        if (proxy != null) {
            try {
                proxy.onOverviewHidden(b, b2);
            }
            catch (RemoteException ex) {
                Log.e("OverviewProxyRecentsImpl", "Failed to send overview hide event to launcher.", (Throwable)ex);
            }
        }
    }
    
    @Override
    public void onStart(final Context mContext) {
        this.mContext = mContext;
        this.mHandler = new Handler();
        this.mTrustManager = (TrustManager)mContext.getSystemService("trust");
        this.mOverviewProxyService = Dependency.get(OverviewProxyService.class);
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mListener);
    }
    
    @Override
    public void showRecentApps(final boolean b) {
        final IOverviewProxy proxy = this.mOverviewProxyService.getProxy();
        if (proxy != null) {
            try {
                proxy.onOverviewShown(b);
            }
            catch (RemoteException ex) {
                Log.e("OverviewProxyRecentsImpl", "Failed to send overview show event to launcher.", (Throwable)ex);
            }
        }
    }
    
    @Override
    public boolean splitPrimaryTask(final int n, final Rect rect, int activityType) {
        final Point point = new Point();
        Rect rect2 = rect;
        if (rect == null) {
            ((DisplayManager)this.mContext.getSystemService((Class)DisplayManager.class)).getDisplay(0).getRealSize(point);
            rect2 = new Rect(0, 0, point.x, point.y);
        }
        final ActivityManager$RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
        if (runningTask != null) {
            activityType = runningTask.configuration.windowConfiguration.getActivityType();
        }
        else {
            activityType = 0;
        }
        final boolean screenPinningActive = ActivityManagerWrapper.getInstance().isScreenPinningActive();
        if (activityType != 2 && activityType != 3) {
            activityType = 0;
        }
        else {
            activityType = 1;
        }
        if (runningTask != null && activityType == 0 && !screenPinningActive) {
            if (runningTask.supportsSplitScreenMultiWindow) {
                if (ActivityManagerWrapper.getInstance().setTaskWindowingModeSplitScreenPrimary(runningTask.id, n, rect2)) {
                    this.mDividerOptional.ifPresent((Consumer<? super Divider>)_$$Lambda$fHPOCVoTSvBox_jGWtU7jxIAav4.INSTANCE);
                    this.mDividerOptional.ifPresent((Consumer<? super Divider>)_$$Lambda$SmHdjDaQkSsbiXXCyer_AyvUNnY.INSTANCE);
                    return true;
                }
            }
            else {
                Toast.makeText(this.mContext, R$string.dock_non_resizeble_failed_to_dock_text, 0).show();
            }
        }
        return false;
    }
    
    @Override
    public void toggleRecentApps() {
        if (this.mOverviewProxyService.getProxy() != null) {
            final _$$Lambda$OverviewProxyRecentsImpl$ZzsBj6p_GVl3rLvpPg_WKT0NW9E $$Lambda$OverviewProxyRecentsImpl$ZzsBj6p_GVl3rLvpPg_WKT0NW9E = new _$$Lambda$OverviewProxyRecentsImpl$ZzsBj6p_GVl3rLvpPg_WKT0NW9E(this);
            final Lazy<StatusBar> mStatusBarLazy = this.mStatusBarLazy;
            if (mStatusBarLazy != null && mStatusBarLazy.get().isKeyguardShowing()) {
                this.mStatusBarLazy.get().executeRunnableDismissingKeyguard(new _$$Lambda$OverviewProxyRecentsImpl$PUSBynP3ZsSZrPqXO1jJqSKnayU(this, $$Lambda$OverviewProxyRecentsImpl$ZzsBj6p_GVl3rLvpPg_WKT0NW9E), null, true, false, true);
            }
            else {
                $$Lambda$OverviewProxyRecentsImpl$ZzsBj6p_GVl3rLvpPg_WKT0NW9E.run();
            }
        }
    }
}
