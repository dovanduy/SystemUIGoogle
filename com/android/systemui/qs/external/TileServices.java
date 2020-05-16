// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.external;

import android.os.UserHandle;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.internal.statusbar.StatusBarIcon;
import android.graphics.drawable.Icon;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.service.quicksettings.Tile;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.Binder;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Looper;
import android.os.IBinder;
import android.content.ComponentName;
import android.util.ArrayMap;
import android.content.BroadcastReceiver;
import com.android.systemui.qs.QSTileHost;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import java.util.Comparator;
import android.service.quicksettings.IQSService$Stub;

public class TileServices extends IQSService$Stub
{
    private static final Comparator<TileServiceManager> SERVICE_SORT;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final Handler mHandler;
    private final QSTileHost mHost;
    private final Handler mMainHandler;
    private int mMaxBound;
    private final BroadcastReceiver mRequestListeningReceiver;
    private final ArrayMap<CustomTile, TileServiceManager> mServices;
    private final ArrayMap<ComponentName, CustomTile> mTiles;
    private final ArrayMap<IBinder, CustomTile> mTokenMap;
    
    static {
        SERVICE_SORT = new Comparator<TileServiceManager>() {
            @Override
            public int compare(final TileServiceManager tileServiceManager, final TileServiceManager tileServiceManager2) {
                return -Integer.compare(tileServiceManager.getBindPriority(), tileServiceManager2.getBindPriority());
            }
        };
    }
    
    public TileServices(final QSTileHost mHost, final Looper looper, final BroadcastDispatcher mBroadcastDispatcher) {
        this.mServices = (ArrayMap<CustomTile, TileServiceManager>)new ArrayMap();
        this.mTiles = (ArrayMap<ComponentName, CustomTile>)new ArrayMap();
        this.mTokenMap = (ArrayMap<IBinder, CustomTile>)new ArrayMap();
        this.mMaxBound = 3;
        this.mRequestListeningReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.service.quicksettings.action.REQUEST_LISTENING".equals(intent.getAction())) {
                    TileServices.this.requestListening((ComponentName)intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME"));
                }
            }
        };
        this.mHost = mHost;
        this.mContext = mHost.getContext();
        (this.mBroadcastDispatcher = mBroadcastDispatcher).registerReceiver(this.mRequestListeningReceiver, new IntentFilter("android.service.quicksettings.action.REQUEST_LISTENING"));
        this.mHandler = new Handler(looper);
        this.mMainHandler = new Handler(Looper.getMainLooper());
    }
    
    private CustomTile getTileForComponent(final ComponentName componentName) {
        synchronized (this.mServices) {
            return (CustomTile)this.mTiles.get((Object)componentName);
        }
    }
    
    private CustomTile getTileForToken(final IBinder binder) {
        synchronized (this.mServices) {
            return (CustomTile)this.mTokenMap.get((Object)binder);
        }
    }
    
    private void requestListening(final ComponentName p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/android/systemui/qs/external/TileServices.mServices:Landroid/util/ArrayMap;
        //     4: astore_2       
        //     5: aload_2        
        //     6: monitorenter   
        //     7: aload_0        
        //     8: aload_1        
        //     9: invokespecial   com/android/systemui/qs/external/TileServices.getTileForComponent:(Landroid/content/ComponentName;)Lcom/android/systemui/qs/external/CustomTile;
        //    12: astore_3       
        //    13: aload_3        
        //    14: ifnonnull       51
        //    17: new             Ljava/lang/StringBuilder;
        //    20: astore_3       
        //    21: aload_3        
        //    22: invokespecial   java/lang/StringBuilder.<init>:()V
        //    25: aload_3        
        //    26: ldc             "Couldn't find tile for "
        //    28: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    31: pop            
        //    32: aload_3        
        //    33: aload_1        
        //    34: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    37: pop            
        //    38: ldc             "TileServices"
        //    40: aload_3        
        //    41: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    44: invokestatic    android/util/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //    47: pop            
        //    48: aload_2        
        //    49: monitorexit    
        //    50: return         
        //    51: aload_0        
        //    52: getfield        com/android/systemui/qs/external/TileServices.mServices:Landroid/util/ArrayMap;
        //    55: aload_3        
        //    56: invokevirtual   android/util/ArrayMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    59: checkcast       Lcom/android/systemui/qs/external/TileServiceManager;
        //    62: astore_1       
        //    63: aload_1        
        //    64: invokevirtual   com/android/systemui/qs/external/TileServiceManager.isActiveTile:()Z
        //    67: ifne            73
        //    70: aload_2        
        //    71: monitorexit    
        //    72: return         
        //    73: aload_1        
        //    74: iconst_1       
        //    75: invokevirtual   com/android/systemui/qs/external/TileServiceManager.setBindRequested:(Z)V
        //    78: aload_1        
        //    79: invokevirtual   com/android/systemui/qs/external/TileServiceManager.getTileService:()Landroid/service/quicksettings/IQSTileService;
        //    82: invokeinterface android/service/quicksettings/IQSTileService.onStartListening:()V
        //    87: aload_2        
        //    88: monitorexit    
        //    89: return         
        //    90: astore_1       
        //    91: aload_2        
        //    92: monitorexit    
        //    93: aload_1        
        //    94: athrow         
        //    95: astore_1       
        //    96: goto            87
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  7      13     90     95     Any
        //  17     50     90     95     Any
        //  51     72     90     95     Any
        //  73     78     90     95     Any
        //  78     87     95     99     Landroid/os/RemoteException;
        //  78     87     90     95     Any
        //  87     89     90     95     Any
        //  91     93     90     95     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0087:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void verifyCaller(final CustomTile customTile) {
        try {
            if (Binder.getCallingUid() == this.mContext.getPackageManager().getPackageUidAsUser(customTile.getComponent().getPackageName(), Binder.getCallingUserHandle().getIdentifier())) {
                return;
            }
            throw new SecurityException("Component outside caller's uid");
        }
        catch (PackageManager$NameNotFoundException cause) {
            throw new SecurityException((Throwable)cause);
        }
    }
    
    public void freeService(final CustomTile customTile, final TileServiceManager tileServiceManager) {
        synchronized (this.mServices) {
            tileServiceManager.setBindAllowed(false);
            tileServiceManager.handleDestroy();
            this.mServices.remove((Object)customTile);
            this.mTokenMap.remove((Object)tileServiceManager.getToken());
            this.mTiles.remove((Object)customTile.getComponent());
            this.mMainHandler.post((Runnable)new _$$Lambda$TileServices$m2qCzd8BVbBUzSnClFn7o_chF7k(this, customTile.getComponent().getClassName()));
        }
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    public QSTileHost getHost() {
        return this.mHost;
    }
    
    public Tile getTile(final IBinder binder) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken != null) {
            this.verifyCaller(tileForToken);
            return tileForToken.getQsTile();
        }
        return null;
    }
    
    public TileServiceManager getTileWrapper(final CustomTile customTile) {
        final ComponentName component = customTile.getComponent();
        final TileServiceManager onCreateTileService = this.onCreateTileService(component, customTile.getQsTile(), this.mBroadcastDispatcher);
        synchronized (this.mServices) {
            this.mServices.put((Object)customTile, (Object)onCreateTileService);
            this.mTiles.put((Object)component, (Object)customTile);
            this.mTokenMap.put((Object)onCreateTileService.getToken(), (Object)customTile);
            // monitorexit(this.mServices)
            onCreateTileService.startLifecycleManagerAndAddTile();
            return onCreateTileService;
        }
    }
    
    public boolean isLocked() {
        return Dependency.get(KeyguardStateController.class).isShowing();
    }
    
    public boolean isSecure() {
        final KeyguardStateController keyguardStateController = Dependency.get(KeyguardStateController.class);
        return keyguardStateController.isMethodSecure() && keyguardStateController.isShowing();
    }
    
    protected TileServiceManager onCreateTileService(final ComponentName componentName, final Tile tile, final BroadcastDispatcher broadcastDispatcher) {
        return new TileServiceManager(this, this.mHandler, componentName, tile, broadcastDispatcher);
    }
    
    public void onDialogHidden(final IBinder binder) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken != null) {
            this.verifyCaller(tileForToken);
            ((TileServiceManager)this.mServices.get((Object)tileForToken)).setShowingDialog(false);
            tileForToken.onDialogHidden();
        }
    }
    
    public void onShowDialog(final IBinder binder) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken != null) {
            this.verifyCaller(tileForToken);
            tileForToken.onDialogShown();
            this.mHost.forceCollapsePanels();
            ((TileServiceManager)this.mServices.get((Object)tileForToken)).setShowingDialog(true);
        }
    }
    
    public void onStartActivity(final IBinder binder) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken != null) {
            this.verifyCaller(tileForToken);
            this.mHost.forceCollapsePanels();
        }
    }
    
    public void onStartSuccessful(final IBinder binder) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken != null) {
            this.verifyCaller(tileForToken);
            synchronized (this.mServices) {
                final TileServiceManager tileServiceManager = (TileServiceManager)this.mServices.get((Object)tileForToken);
                if (tileServiceManager == null || !tileServiceManager.isLifecycleStarted()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("TileServiceManager not started for ");
                    sb.append(tileForToken.getComponent());
                    Log.e("TileServices", sb.toString(), (Throwable)new IllegalStateException());
                    return;
                }
                tileServiceManager.clearPendingBind();
                // monitorexit(this.mServices)
                tileForToken.refreshState();
            }
        }
    }
    
    public void recalculateBindAllowance() {
        synchronized (this.mServices) {
            final ArrayList<TileServiceManager> list = new ArrayList<TileServiceManager>(this.mServices.values());
            // monitorexit(this.mServices)
            final int size = list.size();
            if (size > this.mMaxBound) {
                final long currentTimeMillis = System.currentTimeMillis();
                for (int i = 0; i < size; ++i) {
                    ((TileServiceManager)list.get(i)).calculateBindPriority(currentTimeMillis);
                }
                Collections.sort((List<Object>)list, (Comparator<? super Object>)TileServices.SERVICE_SORT);
            }
            int index = 0;
            int j;
            while (true) {
                j = index;
                if (index >= this.mMaxBound || (j = index) >= size) {
                    break;
                }
                list.get(index).setBindAllowed(true);
                ++index;
            }
            while (j < size) {
                list.get(j).setBindAllowed(false);
                ++j;
            }
        }
    }
    
    public void startUnlockAndRun(final IBinder binder) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken != null) {
            this.verifyCaller(tileForToken);
            tileForToken.startUnlockAndRun();
        }
    }
    
    public void updateQsTile(final Tile tile, final IBinder binder) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken != null) {
            this.verifyCaller(tileForToken);
            synchronized (this.mServices) {
                final TileServiceManager tileServiceManager = (TileServiceManager)this.mServices.get((Object)tileForToken);
                if (tileServiceManager == null || !tileServiceManager.isLifecycleStarted()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("TileServiceManager not started for ");
                    sb.append(tileForToken.getComponent());
                    Log.e("TileServices", sb.toString(), (Throwable)new IllegalStateException());
                    return;
                }
                tileServiceManager.clearPendingBind();
                tileServiceManager.setLastUpdate(System.currentTimeMillis());
                // monitorexit(this.mServices)
                tileForToken.updateState(tile);
                tileForToken.refreshState();
            }
        }
    }
    
    public void updateStatusIcon(final IBinder binder, final Icon icon, final String s) {
        final CustomTile tileForToken = this.getTileForToken(binder);
        if (tileForToken == null) {
            return;
        }
        this.verifyCaller(tileForToken);
        try {
            final ComponentName component = tileForToken.getComponent();
            final String packageName = component.getPackageName();
            final UserHandle callingUserHandle = IQSService$Stub.getCallingUserHandle();
            if (this.mContext.getPackageManager().getPackageInfoAsUser(packageName, 0, callingUserHandle.getIdentifier()).applicationInfo.isSystemApp()) {
                StatusBarIcon statusBarIcon;
                if (icon != null) {
                    statusBarIcon = new StatusBarIcon(callingUserHandle, packageName, icon, 0, 0, (CharSequence)s);
                }
                else {
                    statusBarIcon = null;
                }
                this.mMainHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final StatusBarIconController iconController = TileServices.this.mHost.getIconController();
                        iconController.setIcon(component.getClassName(), statusBarIcon);
                        iconController.setExternalIcon(component.getClassName());
                    }
                });
            }
        }
        catch (PackageManager$NameNotFoundException ex) {}
    }
}
