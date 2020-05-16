// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.external;

import android.os.IBinder;
import android.service.quicksettings.IQSTileService;
import android.util.Log;
import android.content.IntentFilter;
import java.util.Iterator;
import android.content.pm.ResolveInfo;
import java.util.Objects;
import android.content.Context;
import android.service.quicksettings.IQSService;
import android.os.UserHandle;
import android.app.ActivityManager;
import android.content.Intent;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.service.quicksettings.Tile;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.os.Handler;

public class TileServiceManager
{
    static final String PREFS_FILE = "CustomTileModes";
    private boolean mBindAllowed;
    private boolean mBindRequested;
    private boolean mBound;
    private final Handler mHandler;
    private boolean mJustBound;
    final Runnable mJustBoundOver;
    private long mLastUpdate;
    private boolean mPendingBind;
    private int mPriority;
    private final TileServices mServices;
    private boolean mShowingDialog;
    private boolean mStarted;
    private final TileLifecycleManager mStateManager;
    private final Runnable mUnbind;
    private final BroadcastReceiver mUninstallReceiver;
    
    TileServiceManager(final TileServices tileServices, final Handler handler, final ComponentName component, final Tile tile, final BroadcastDispatcher broadcastDispatcher) {
        this(tileServices, handler, new TileLifecycleManager(handler, tileServices.getContext(), (IQSService)tileServices, tile, new Intent().setComponent(component), new UserHandle(ActivityManager.getCurrentUser()), broadcastDispatcher));
    }
    
    TileServiceManager(final TileServices mServices, final Handler mHandler, final TileLifecycleManager mStateManager) {
        this.mPendingBind = true;
        this.mStarted = false;
        this.mUnbind = new Runnable() {
            @Override
            public void run() {
                if (TileServiceManager.this.mBound && !TileServiceManager.this.mBindRequested) {
                    TileServiceManager.this.unbindService();
                }
            }
        };
        this.mJustBoundOver = new Runnable() {
            @Override
            public void run() {
                TileServiceManager.this.mJustBound = false;
                TileServiceManager.this.mServices.recalculateBindAllowance();
            }
        };
        this.mUninstallReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, Intent intent) {
                if (!"android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                    return;
                }
                final String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                final ComponentName component = TileServiceManager.this.mStateManager.getComponent();
                if (!Objects.equals(encodedSchemeSpecificPart, component.getPackageName())) {
                    return;
                }
                if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    intent = new Intent("android.service.quicksettings.action.QS_TILE");
                    intent.setPackage(encodedSchemeSpecificPart);
                    for (final ResolveInfo resolveInfo : context.getPackageManager().queryIntentServicesAsUser(intent, 0, ActivityManager.getCurrentUser())) {
                        if (Objects.equals(resolveInfo.serviceInfo.packageName, component.getPackageName()) && Objects.equals(resolveInfo.serviceInfo.name, component.getClassName())) {
                            return;
                        }
                    }
                }
                TileServiceManager.this.mServices.getHost().removeTile(component);
            }
        };
        this.mServices = mServices;
        this.mHandler = mHandler;
        this.mStateManager = mStateManager;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        this.mServices.getContext().registerReceiverAsUser(this.mUninstallReceiver, new UserHandle(ActivityManager.getCurrentUser()), intentFilter, (String)null, this.mHandler);
    }
    
    private void bindService() {
        if (this.mBound) {
            Log.e("TileServiceManager", "Service already bound");
            return;
        }
        this.mPendingBind = true;
        this.mBound = true;
        this.mJustBound = true;
        this.mHandler.postDelayed(this.mJustBoundOver, 5000L);
        this.mStateManager.setBindService(true);
    }
    
    private void unbindService() {
        if (!this.mBound) {
            Log.e("TileServiceManager", "Service not bound");
            return;
        }
        this.mBound = false;
        this.mJustBound = false;
        this.mStateManager.setBindService(false);
    }
    
    public void calculateBindPriority(long n) {
        if (this.mStateManager.hasPendingClick()) {
            this.mPriority = Integer.MAX_VALUE;
        }
        else if (this.mShowingDialog) {
            this.mPriority = 2147483646;
        }
        else if (this.mJustBound) {
            this.mPriority = 2147483645;
        }
        else if (!this.mBindRequested) {
            this.mPriority = Integer.MIN_VALUE;
        }
        else {
            n -= this.mLastUpdate;
            if (n > 2147483644L) {
                this.mPriority = 2147483644;
            }
            else {
                this.mPriority = (int)n;
            }
        }
    }
    
    public void clearPendingBind() {
        this.mPendingBind = false;
    }
    
    public int getBindPriority() {
        return this.mPriority;
    }
    
    public IQSTileService getTileService() {
        return (IQSTileService)this.mStateManager;
    }
    
    public IBinder getToken() {
        return this.mStateManager.getToken();
    }
    
    public void handleDestroy() {
        this.setBindAllowed(false);
        this.mServices.getContext().unregisterReceiver(this.mUninstallReceiver);
        this.mStateManager.handleDestroy();
    }
    
    public boolean hasPendingBind() {
        return this.mPendingBind;
    }
    
    public boolean isActiveTile() {
        return this.mStateManager.isActiveTile();
    }
    
    boolean isLifecycleStarted() {
        return this.mStarted;
    }
    
    public boolean isToggleableTile() {
        return this.mStateManager.isToggleableTile();
    }
    
    public void setBindAllowed(final boolean mBindAllowed) {
        if (this.mBindAllowed == mBindAllowed) {
            return;
        }
        this.mBindAllowed = mBindAllowed;
        if (!mBindAllowed && this.mBound) {
            this.unbindService();
        }
        else if (this.mBindAllowed && this.mBindRequested && !this.mBound) {
            this.bindService();
        }
    }
    
    public void setBindRequested(final boolean mBindRequested) {
        if (this.mBindRequested == mBindRequested) {
            return;
        }
        this.mBindRequested = mBindRequested;
        if (this.mBindAllowed && mBindRequested && !this.mBound) {
            this.mHandler.removeCallbacks(this.mUnbind);
            this.bindService();
        }
        else {
            this.mServices.recalculateBindAllowance();
        }
        if (this.mBound && !this.mBindRequested) {
            this.mHandler.postDelayed(this.mUnbind, 30000L);
        }
    }
    
    public void setLastUpdate(final long mLastUpdate) {
        this.mLastUpdate = mLastUpdate;
        if (this.mBound && this.isActiveTile()) {
            this.mStateManager.onStopListening();
            this.setBindRequested(false);
        }
        this.mServices.recalculateBindAllowance();
    }
    
    public void setShowingDialog(final boolean mShowingDialog) {
        this.mShowingDialog = mShowingDialog;
    }
    
    public void setTileChangeListener(final TileLifecycleManager.TileChangeListener tileChangeListener) {
        this.mStateManager.setTileChangeListener(tileChangeListener);
    }
    
    void startLifecycleManagerAndAddTile() {
        this.mStarted = true;
        final ComponentName component = this.mStateManager.getComponent();
        final Context context = this.mServices.getContext();
        if (!TileLifecycleManager.isTileAdded(context, component)) {
            TileLifecycleManager.setTileAdded(context, component, true);
            this.mStateManager.onTileAdded();
            this.mStateManager.flushMessagesAndUnbind();
        }
    }
}
