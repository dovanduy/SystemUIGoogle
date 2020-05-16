// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.external;

import android.service.quicksettings.IQSTileService$Stub;
import java.util.Objects;
import android.content.pm.ServiceInfo;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.RemoteException;
import android.util.Log;
import java.util.Collection;
import android.util.ArraySet;
import android.os.Binder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.IQSService;
import android.os.UserHandle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import android.content.Intent;
import android.os.Handler;
import android.content.Context;
import android.os.IBinder;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.IBinder$DeathRecipient;
import android.content.ServiceConnection;
import android.service.quicksettings.IQSTileService;
import android.content.BroadcastReceiver;

public class TileLifecycleManager extends BroadcastReceiver implements IQSTileService, ServiceConnection, IBinder$DeathRecipient
{
    private int mBindRetryDelay;
    private int mBindTryCount;
    private boolean mBound;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private TileChangeListener mChangeListener;
    private IBinder mClickBinder;
    private final Context mContext;
    private final Handler mHandler;
    private final Intent mIntent;
    private boolean mIsBound;
    private boolean mListening;
    private final PackageManagerAdapter mPackageManagerAdapter;
    private AtomicBoolean mPackageReceiverRegistered;
    private Set<Integer> mQueuedMessages;
    private final IBinder mToken;
    private boolean mUnbindImmediate;
    private final UserHandle mUser;
    private AtomicBoolean mUserReceiverRegistered;
    private QSTileServiceWrapper mWrapper;
    
    public TileLifecycleManager(final Handler handler, final Context context, final IQSService iqsService, final Tile tile, final Intent intent, final UserHandle userHandle, final BroadcastDispatcher broadcastDispatcher) {
        this(handler, context, iqsService, tile, intent, userHandle, new PackageManagerAdapter(context), broadcastDispatcher);
    }
    
    TileLifecycleManager(final Handler mHandler, final Context mContext, final IQSService iqsService, final Tile tile, final Intent mIntent, final UserHandle mUser, final PackageManagerAdapter mPackageManagerAdapter, final BroadcastDispatcher mBroadcastDispatcher) {
        this.mToken = (IBinder)new Binder();
        this.mQueuedMessages = (Set<Integer>)new ArraySet();
        this.mBindRetryDelay = 1000;
        this.mPackageReceiverRegistered = new AtomicBoolean(false);
        this.mUserReceiverRegistered = new AtomicBoolean(false);
        this.mContext = mContext;
        this.mHandler = mHandler;
        (this.mIntent = mIntent).putExtra("service", iqsService.asBinder());
        this.mIntent.putExtra("token", this.mToken);
        this.mUser = mUser;
        this.mPackageManagerAdapter = mPackageManagerAdapter;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    private boolean checkComponentState() {
        if (this.isPackageAvailable() && this.isComponentAvailable()) {
            return true;
        }
        this.startPackageListening();
        return false;
    }
    
    private void handleDeath() {
        if (this.mWrapper == null) {
            return;
        }
        this.mWrapper = null;
        if (!this.mBound) {
            return;
        }
        if (this.checkComponentState()) {
            this.mHandler.postDelayed((Runnable)new Runnable() {
                @Override
                public void run() {
                    if (TileLifecycleManager.this.mBound) {
                        TileLifecycleManager.this.setBindService(true);
                    }
                }
            }, (long)this.mBindRetryDelay);
        }
    }
    
    private void handlePendingMessages() {
        synchronized (this.mQueuedMessages) {
            final ArraySet set = new ArraySet((Collection)this.mQueuedMessages);
            this.mQueuedMessages.clear();
            // monitorexit(this.mQueuedMessages)
            if (set.contains((Object)0)) {
                this.onTileAdded();
            }
            if (this.mListening) {
                this.onStartListening();
            }
            if (set.contains((Object)2)) {
                if (!this.mListening) {
                    Log.w("TileLifecycleManager", "Managed to get click on non-listening state...");
                }
                else {
                    this.onClick(this.mClickBinder);
                }
            }
            if (set.contains((Object)3)) {
                if (!this.mListening) {
                    Log.w("TileLifecycleManager", "Managed to get unlock on non-listening state...");
                }
                else {
                    this.onUnlockComplete();
                }
            }
            if (set.contains((Object)1)) {
                if (this.mListening) {
                    Log.w("TileLifecycleManager", "Managed to get remove in listening state...");
                    this.onStopListening();
                }
                this.onTileRemoved();
            }
            if (this.mUnbindImmediate) {
                this.setBindService(this.mUnbindImmediate = false);
            }
        }
    }
    
    private boolean isComponentAvailable() {
        this.mIntent.getComponent().getPackageName();
        boolean b = false;
        try {
            if (this.mPackageManagerAdapter.getServiceInfo(this.mIntent.getComponent(), 0, this.mUser.getIdentifier()) != null) {
                b = true;
            }
            return b;
        }
        catch (RemoteException ex) {
            return b;
        }
    }
    
    private boolean isPackageAvailable() {
        final String packageName = this.mIntent.getComponent().getPackageName();
        try {
            this.mPackageManagerAdapter.getPackageInfoAsUser(packageName, 0, this.mUser.getIdentifier());
            return true;
        }
        catch (PackageManager$NameNotFoundException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Package not available: ");
            sb.append(packageName);
            Log.d("TileLifecycleManager", sb.toString());
            return false;
        }
    }
    
    public static boolean isTileAdded(final Context context, final ComponentName componentName) {
        return context.getSharedPreferences("tiles_prefs", 0).getBoolean(componentName.flattenToString(), false);
    }
    
    private void queueMessage(final int i) {
        synchronized (this.mQueuedMessages) {
            this.mQueuedMessages.add(i);
        }
    }
    
    public static void setTileAdded(final Context context, final ComponentName componentName, final boolean b) {
        context.getSharedPreferences("tiles_prefs", 0).edit().putBoolean(componentName.flattenToString(), b).commit();
    }
    
    private void startPackageListening() {
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        try {
            this.mPackageReceiverRegistered.set(true);
            this.mContext.registerReceiverAsUser((BroadcastReceiver)this, this.mUser, intentFilter, (String)null, this.mHandler);
        }
        catch (Exception ex) {
            this.mPackageReceiverRegistered.set(false);
            Log.e("TileLifecycleManager", "Could not register package receiver", (Throwable)ex);
        }
        final IntentFilter intentFilter2 = new IntentFilter("android.intent.action.USER_UNLOCKED");
        try {
            this.mUserReceiverRegistered.set(true);
            this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter2, this.mHandler, this.mUser);
        }
        catch (Exception ex2) {
            this.mUserReceiverRegistered.set(false);
            Log.e("TileLifecycleManager", "Could not register unlock receiver", (Throwable)ex2);
        }
    }
    
    private void stopPackageListening() {
        if (this.mUserReceiverRegistered.compareAndSet(true, false)) {
            this.mBroadcastDispatcher.unregisterReceiver(this);
        }
        if (this.mPackageReceiverRegistered.compareAndSet(true, false)) {
            this.mContext.unregisterReceiver((BroadcastReceiver)this);
        }
    }
    
    public IBinder asBinder() {
        final QSTileServiceWrapper mWrapper = this.mWrapper;
        IBinder binder;
        if (mWrapper != null) {
            binder = mWrapper.asBinder();
        }
        else {
            binder = null;
        }
        return binder;
    }
    
    public void binderDied() {
        this.handleDeath();
    }
    
    public void flushMessagesAndUnbind() {
        this.setBindService(this.mUnbindImmediate = true);
    }
    
    public ComponentName getComponent() {
        return this.mIntent.getComponent();
    }
    
    public IBinder getToken() {
        return this.mToken;
    }
    
    public void handleDestroy() {
        if (this.mPackageReceiverRegistered.get() || this.mUserReceiverRegistered.get()) {
            this.stopPackageListening();
        }
        this.mChangeListener = null;
    }
    
    public boolean hasPendingClick() {
        synchronized (this.mQueuedMessages) {
            return this.mQueuedMessages.contains(2);
        }
    }
    
    public boolean isActiveTile() {
        final boolean b = false;
        try {
            final ServiceInfo serviceInfo = this.mPackageManagerAdapter.getServiceInfo(this.mIntent.getComponent(), 8320);
            boolean b2 = b;
            if (serviceInfo.metaData != null) {
                final boolean boolean1 = serviceInfo.metaData.getBoolean("android.service.quicksettings.ACTIVE_TILE", false);
                b2 = b;
                if (boolean1) {
                    b2 = true;
                }
            }
            return b2;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return b;
        }
    }
    
    public boolean isToggleableTile() {
        final boolean b = false;
        try {
            final ServiceInfo serviceInfo = this.mPackageManagerAdapter.getServiceInfo(this.mIntent.getComponent(), 8320);
            boolean b2 = b;
            if (serviceInfo.metaData != null) {
                final boolean boolean1 = serviceInfo.metaData.getBoolean("android.service.quicksettings.TOGGLEABLE_TILE", false);
                b2 = b;
                if (boolean1) {
                    b2 = true;
                }
            }
            return b2;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return b;
        }
    }
    
    public void onClick(final IBinder mClickBinder) {
        final QSTileServiceWrapper mWrapper = this.mWrapper;
        if (mWrapper == null || !mWrapper.onClick(mClickBinder)) {
            this.mClickBinder = mClickBinder;
            this.queueMessage(2);
            this.handleDeath();
        }
    }
    
    public void onReceive(final Context context, final Intent intent) {
        if (!"android.intent.action.USER_UNLOCKED".equals(intent.getAction()) && !Objects.equals(intent.getData().getEncodedSchemeSpecificPart(), this.mIntent.getComponent().getPackageName())) {
            return;
        }
        if ("android.intent.action.PACKAGE_CHANGED".equals(intent.getAction())) {
            final TileChangeListener mChangeListener = this.mChangeListener;
            if (mChangeListener != null) {
                mChangeListener.onTileChanged(this.mIntent.getComponent());
            }
        }
        this.stopPackageListening();
        if (this.mBound) {
            this.setBindService(true);
        }
    }
    
    public void onServiceConnected(ComponentName mWrapper, final IBinder binder) {
        this.mBindTryCount = 0;
        mWrapper = (ComponentName)new QSTileServiceWrapper(IQSTileService$Stub.asInterface(binder));
        while (true) {
            try {
                binder.linkToDeath((IBinder$DeathRecipient)this, 0);
                this.mWrapper = (QSTileServiceWrapper)mWrapper;
                this.handlePendingMessages();
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
    
    public void onServiceDisconnected(final ComponentName componentName) {
        this.handleDeath();
    }
    
    public void onStartListening() {
        this.mListening = true;
        final QSTileServiceWrapper mWrapper = this.mWrapper;
        if (mWrapper != null && !mWrapper.onStartListening()) {
            this.handleDeath();
        }
    }
    
    public void onStopListening() {
        this.mListening = false;
        final QSTileServiceWrapper mWrapper = this.mWrapper;
        if (mWrapper != null && !mWrapper.onStopListening()) {
            this.handleDeath();
        }
    }
    
    public void onTileAdded() {
        final QSTileServiceWrapper mWrapper = this.mWrapper;
        if (mWrapper == null || !mWrapper.onTileAdded()) {
            this.queueMessage(0);
            this.handleDeath();
        }
    }
    
    public void onTileRemoved() {
        final QSTileServiceWrapper mWrapper = this.mWrapper;
        if (mWrapper == null || !mWrapper.onTileRemoved()) {
            this.queueMessage(1);
            this.handleDeath();
        }
    }
    
    public void onUnlockComplete() {
        final QSTileServiceWrapper mWrapper = this.mWrapper;
        if (mWrapper == null || !mWrapper.onUnlockComplete()) {
            this.queueMessage(3);
            this.handleDeath();
        }
    }
    
    public void setBindService(final boolean mBound) {
        if (this.mBound && this.mUnbindImmediate) {
            this.mUnbindImmediate = false;
            return;
        }
        if (this.mBound = mBound) {
            if (this.mBindTryCount == 5) {
                this.startPackageListening();
                return;
            }
            if (!this.checkComponentState()) {
                return;
            }
            ++this.mBindTryCount;
            try {
                this.mIsBound = this.mContext.bindServiceAsUser(this.mIntent, (ServiceConnection)this, 34603041, this.mUser);
            }
            catch (SecurityException ex) {
                Log.e("TileLifecycleManager", "Failed to bind to service", (Throwable)ex);
                this.mIsBound = false;
            }
        }
        else {
            this.mBindTryCount = 0;
            this.mWrapper = null;
            if (this.mIsBound) {
                this.mContext.unbindService((ServiceConnection)this);
                this.mIsBound = false;
            }
        }
    }
    
    public void setTileChangeListener(final TileChangeListener mChangeListener) {
        this.mChangeListener = mChangeListener;
    }
    
    public interface TileChangeListener
    {
        void onTileChanged(final ComponentName p0);
    }
}
