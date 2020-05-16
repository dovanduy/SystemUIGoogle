// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.external;

import java.util.function.Supplier;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;
import android.metrics.LogMaker;
import android.widget.Switch;
import android.util.Log;
import android.net.Uri;
import android.os.Parcelable;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.RemoteException;
import android.graphics.drawable.Drawable$ConstantState;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.PackageManager;
import java.util.Objects;
import android.view.WindowManagerGlobal;
import android.os.Binder;
import com.android.systemui.qs.QSHost;
import android.view.IWindowManager;
import android.content.Context;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.IQSTileService;
import android.graphics.drawable.Icon;
import android.content.ComponentName;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class CustomTile extends QSTileImpl<State> implements TileChangeListener
{
    private final ComponentName mComponent;
    private android.graphics.drawable.Icon mDefaultIcon;
    private CharSequence mDefaultLabel;
    private boolean mIsShowingDialog;
    private boolean mIsTokenGranted;
    private boolean mListening;
    private final IQSTileService mService;
    private final TileServiceManager mServiceManager;
    private final Tile mTile;
    private final IBinder mToken;
    private final int mUser;
    private final Context mUserContext;
    private final IWindowManager mWindowManager;
    
    private CustomTile(final QSHost qsHost, final String s, final Context mUserContext) {
        super(qsHost);
        this.mToken = (IBinder)new Binder();
        this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
        this.mComponent = ComponentName.unflattenFromString(s);
        this.mTile = new Tile();
        this.mUserContext = mUserContext;
        this.mUser = mUserContext.getUserId();
        this.updateDefaultTileAndIcon();
        final TileServiceManager tileWrapper = qsHost.getTileServices().getTileWrapper(this);
        this.mServiceManager = tileWrapper;
        if (tileWrapper.isToggleableTile()) {
            this.resetStates();
        }
        this.mService = this.mServiceManager.getTileService();
        this.mServiceManager.setTileChangeListener(this);
    }
    
    public static CustomTile create(final QSHost qsHost, String substring, final Context context) {
        if (substring == null || !substring.startsWith("custom(") || !substring.endsWith(")")) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Bad custom tile spec: ");
            sb.append(substring);
            throw new IllegalArgumentException(sb.toString());
        }
        substring = substring.substring(7, substring.length() - 1);
        if (!substring.isEmpty()) {
            return new CustomTile(qsHost, substring, context);
        }
        throw new IllegalArgumentException("Empty custom tile spec action");
    }
    
    public static ComponentName getComponentFromSpec(String substring) {
        substring = substring.substring(7, substring.length() - 1);
        if (!substring.isEmpty()) {
            return ComponentName.unflattenFromString(substring);
        }
        throw new IllegalArgumentException("Empty custom tile spec action");
    }
    
    private boolean iconEquals(final android.graphics.drawable.Icon icon, final android.graphics.drawable.Icon icon2) {
        if (icon == icon2) {
            return true;
        }
        if (icon != null) {
            if (icon2 != null) {
                if (icon.getType() == 2) {
                    if (icon2.getType() == 2) {
                        return icon.getResId() == icon2.getResId() && Objects.equals(icon.getResPackage(), icon2.getResPackage());
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isSystemApp(final PackageManager packageManager) throws PackageManager$NameNotFoundException {
        return packageManager.getApplicationInfo(this.mComponent.getPackageName(), 0).isSystemApp();
    }
    
    private Intent resolveIntent(Intent setClassName) {
        final ResolveInfo resolveActivityAsUser = super.mContext.getPackageManager().resolveActivityAsUser(setClassName, 0, ActivityManager.getCurrentUser());
        if (resolveActivityAsUser != null) {
            setClassName = new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES");
            final ActivityInfo activityInfo = resolveActivityAsUser.activityInfo;
            setClassName = setClassName.setClassName(activityInfo.packageName, activityInfo.name);
        }
        else {
            setClassName = null;
        }
        return setClassName;
    }
    
    public static String toSpec(final ComponentName componentName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("custom(");
        sb.append(componentName.flattenToShortString());
        sb.append(")");
        return sb.toString();
    }
    
    private void updateDefaultTileAndIcon() {
        try {
            final PackageManager packageManager = this.mUserContext.getPackageManager();
            int n = 786432;
            if (this.isSystemApp(packageManager)) {
                n = 786944;
            }
            final ServiceInfo serviceInfo = packageManager.getServiceInfo(this.mComponent, n);
            int n2;
            if (serviceInfo.icon != 0) {
                n2 = serviceInfo.icon;
            }
            else {
                n2 = serviceInfo.applicationInfo.icon;
            }
            final android.graphics.drawable.Icon icon = this.mTile.getIcon();
            final int n3 = 0;
            final boolean b = icon == null || this.iconEquals(this.mTile.getIcon(), this.mDefaultIcon);
            android.graphics.drawable.Icon withResource;
            if (n2 != 0) {
                withResource = android.graphics.drawable.Icon.createWithResource(this.mComponent.getPackageName(), n2);
            }
            else {
                withResource = null;
            }
            this.mDefaultIcon = withResource;
            if (b) {
                this.mTile.setIcon(withResource);
            }
            int n4 = 0;
            Label_0179: {
                if (this.mTile.getLabel() != null) {
                    n4 = n3;
                    if (!TextUtils.equals(this.mTile.getLabel(), this.mDefaultLabel)) {
                        break Label_0179;
                    }
                }
                n4 = 1;
            }
            final CharSequence loadLabel = serviceInfo.loadLabel(packageManager);
            this.mDefaultLabel = loadLabel;
            if (n4 != 0) {
                this.mTile.setLabel(loadLabel);
            }
        }
        catch (PackageManager$NameNotFoundException ex) {
            this.mDefaultIcon = null;
            this.mDefaultLabel = null;
        }
    }
    
    public ComponentName getComponent() {
        return this.mComponent;
    }
    
    @Override
    public Intent getLongClickIntent() {
        final Intent intent = new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES");
        intent.setPackage(this.mComponent.getPackageName());
        final Intent resolveIntent = this.resolveIntent(intent);
        if (resolveIntent != null) {
            resolveIntent.putExtra("android.intent.extra.COMPONENT_NAME", (Parcelable)this.mComponent);
            resolveIntent.putExtra("state", this.mTile.getState());
            return resolveIntent;
        }
        return new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", this.mComponent.getPackageName(), (String)null));
    }
    
    @Override
    public int getMetricsCategory() {
        return 268;
    }
    
    public Tile getQsTile() {
        this.updateDefaultTileAndIcon();
        return this.mTile;
    }
    
    @Override
    protected long getStaleTimeout() {
        return super.mHost.indexOf(this.getTileSpec()) * 60000L + 3600000L;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return this.getState().label;
    }
    
    public int getUser() {
        return this.mUser;
    }
    
    @Override
    protected void handleClick() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/android/systemui/qs/external/CustomTile.mTile:Landroid/service/quicksettings/Tile;
        //     4: invokevirtual   android/service/quicksettings/Tile.getState:()I
        //     7: ifne            11
        //    10: return         
        //    11: aload_0        
        //    12: getfield        com/android/systemui/qs/external/CustomTile.mWindowManager:Landroid/view/IWindowManager;
        //    15: aload_0        
        //    16: getfield        com/android/systemui/qs/external/CustomTile.mToken:Landroid/os/IBinder;
        //    19: sipush          2035
        //    22: iconst_0       
        //    23: invokeinterface android/view/IWindowManager.addWindowToken:(Landroid/os/IBinder;II)V
        //    28: aload_0        
        //    29: iconst_1       
        //    30: putfield        com/android/systemui/qs/external/CustomTile.mIsTokenGranted:Z
        //    33: aload_0        
        //    34: getfield        com/android/systemui/qs/external/CustomTile.mServiceManager:Lcom/android/systemui/qs/external/TileServiceManager;
        //    37: invokevirtual   com/android/systemui/qs/external/TileServiceManager.isActiveTile:()Z
        //    40: ifeq            60
        //    43: aload_0        
        //    44: getfield        com/android/systemui/qs/external/CustomTile.mServiceManager:Lcom/android/systemui/qs/external/TileServiceManager;
        //    47: iconst_1       
        //    48: invokevirtual   com/android/systemui/qs/external/TileServiceManager.setBindRequested:(Z)V
        //    51: aload_0        
        //    52: getfield        com/android/systemui/qs/external/CustomTile.mService:Landroid/service/quicksettings/IQSTileService;
        //    55: invokeinterface android/service/quicksettings/IQSTileService.onStartListening:()V
        //    60: aload_0        
        //    61: getfield        com/android/systemui/qs/external/CustomTile.mService:Landroid/service/quicksettings/IQSTileService;
        //    64: aload_0        
        //    65: getfield        com/android/systemui/qs/external/CustomTile.mToken:Landroid/os/IBinder;
        //    68: invokeinterface android/service/quicksettings/IQSTileService.onClick:(Landroid/os/IBinder;)V
        //    73: return         
        //    74: astore_1       
        //    75: goto            33
        //    78: astore_1       
        //    79: goto            73
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  11     33     74     78     Landroid/os/RemoteException;
        //  33     60     78     82     Landroid/os/RemoteException;
        //  60     73     78     82     Landroid/os/RemoteException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0033:
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
    
    @Override
    protected void handleDestroy() {
        super.handleDestroy();
        while (true) {
            if (!this.mIsTokenGranted) {
                break Label_0025;
            }
            try {
                this.mWindowManager.removeWindowToken(this.mToken, 0);
                super.mHost.getTileServices().freeService(this, this.mServiceManager);
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
    
    public void handleSetListening(final boolean p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: iload_1        
        //     2: invokespecial   com/android/systemui/qs/tileimpl/QSTileImpl.handleSetListening:(Z)V
        //     5: aload_0        
        //     6: getfield        com/android/systemui/qs/external/CustomTile.mListening:Z
        //     9: iload_1        
        //    10: if_icmpne       14
        //    13: return         
        //    14: aload_0        
        //    15: iload_1        
        //    16: putfield        com/android/systemui/qs/external/CustomTile.mListening:Z
        //    19: iload_1        
        //    20: ifeq            61
        //    23: aload_0        
        //    24: invokespecial   com/android/systemui/qs/external/CustomTile.updateDefaultTileAndIcon:()V
        //    27: aload_0        
        //    28: invokevirtual   com/android/systemui/qs/tileimpl/QSTileImpl.refreshState:()V
        //    31: aload_0        
        //    32: getfield        com/android/systemui/qs/external/CustomTile.mServiceManager:Lcom/android/systemui/qs/external/TileServiceManager;
        //    35: invokevirtual   com/android/systemui/qs/external/TileServiceManager.isActiveTile:()Z
        //    38: ifne            118
        //    41: aload_0        
        //    42: getfield        com/android/systemui/qs/external/CustomTile.mServiceManager:Lcom/android/systemui/qs/external/TileServiceManager;
        //    45: iconst_1       
        //    46: invokevirtual   com/android/systemui/qs/external/TileServiceManager.setBindRequested:(Z)V
        //    49: aload_0        
        //    50: getfield        com/android/systemui/qs/external/CustomTile.mService:Landroid/service/quicksettings/IQSTileService;
        //    53: invokeinterface android/service/quicksettings/IQSTileService.onStartListening:()V
        //    58: goto            118
        //    61: aload_0        
        //    62: getfield        com/android/systemui/qs/external/CustomTile.mService:Landroid/service/quicksettings/IQSTileService;
        //    65: invokeinterface android/service/quicksettings/IQSTileService.onStopListening:()V
        //    70: aload_0        
        //    71: getfield        com/android/systemui/qs/external/CustomTile.mIsTokenGranted:Z
        //    74: ifeq            105
        //    77: aload_0        
        //    78: getfield        com/android/systemui/qs/external/CustomTile.mIsShowingDialog:Z
        //    81: istore_1       
        //    82: iload_1        
        //    83: ifne            105
        //    86: aload_0        
        //    87: getfield        com/android/systemui/qs/external/CustomTile.mWindowManager:Landroid/view/IWindowManager;
        //    90: aload_0        
        //    91: getfield        com/android/systemui/qs/external/CustomTile.mToken:Landroid/os/IBinder;
        //    94: iconst_0       
        //    95: invokeinterface android/view/IWindowManager.removeWindowToken:(Landroid/os/IBinder;I)V
        //   100: aload_0        
        //   101: iconst_0       
        //   102: putfield        com/android/systemui/qs/external/CustomTile.mIsTokenGranted:Z
        //   105: aload_0        
        //   106: iconst_0       
        //   107: putfield        com/android/systemui/qs/external/CustomTile.mIsShowingDialog:Z
        //   110: aload_0        
        //   111: getfield        com/android/systemui/qs/external/CustomTile.mServiceManager:Lcom/android/systemui/qs/external/TileServiceManager;
        //   114: iconst_0       
        //   115: invokevirtual   com/android/systemui/qs/external/TileServiceManager.setBindRequested:(Z)V
        //   118: return         
        //   119: astore_2       
        //   120: goto            118
        //   123: astore_2       
        //   124: goto            100
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  23     58     119    123    Landroid/os/RemoteException;
        //  61     82     119    123    Landroid/os/RemoteException;
        //  86     100    123    127    Landroid/os/RemoteException;
        //  100    105    119    123    Landroid/os/RemoteException;
        //  105    118    119    123    Landroid/os/RemoteException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0100:
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
    
    @Override
    protected void handleUpdateState(final State state, final Object o) {
        int state2 = this.mTile.getState();
        final boolean hasPendingBind = this.mServiceManager.hasPendingBind();
        boolean value = false;
        if (hasPendingBind) {
            state2 = 0;
        }
        state.state = state2;
        Drawable drawable;
        try {
            drawable = this.mTile.getIcon().loadDrawable(this.mUserContext);
        }
        catch (Exception ex) {
            Log.w(super.TAG, "Invalid icon, forcing into unavailable state");
            state.state = 0;
            drawable = this.mDefaultIcon.loadDrawable(this.mUserContext);
        }
        state.iconSupplier = (Supplier<Icon>)new _$$Lambda$CustomTile$Oh_NzDEMM2yCWnVYbU2_DKTzaqo(drawable);
        state.label = this.mTile.getLabel();
        final CharSequence subtitle = this.mTile.getSubtitle();
        if (subtitle != null && subtitle.length() > 0) {
            state.secondaryLabel = subtitle;
        }
        else {
            state.secondaryLabel = null;
        }
        if (this.mTile.getContentDescription() != null) {
            state.contentDescription = this.mTile.getContentDescription();
        }
        else {
            state.contentDescription = state.label;
        }
        if (this.mTile.getStateDescription() != null) {
            state.stateDescription = this.mTile.getStateDescription();
        }
        else {
            state.stateDescription = null;
        }
        if (state instanceof BooleanState) {
            state.expandedAccessibilityClassName = Switch.class.getName();
            final BooleanState booleanState = (BooleanState)state;
            if (state.state == 2) {
                value = true;
            }
            booleanState.value = value;
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.mDefaultIcon != null;
    }
    
    @Override
    public State newTileState() {
        final TileServiceManager mServiceManager = this.mServiceManager;
        if (mServiceManager != null && mServiceManager.isToggleableTile()) {
            return new QSTile.BooleanState();
        }
        return new QSTile.State();
    }
    
    public void onDialogHidden() {
        this.mIsShowingDialog = false;
        try {
            this.mWindowManager.removeWindowToken(this.mToken, 0);
        }
        catch (RemoteException ex) {}
    }
    
    public void onDialogShown() {
        this.mIsShowingDialog = true;
    }
    
    @Override
    public void onTileChanged(final ComponentName componentName) {
        this.updateDefaultTileAndIcon();
    }
    
    @Override
    public LogMaker populate(final LogMaker logMaker) {
        return super.populate(logMaker).setComponentName(this.mComponent);
    }
    
    public void startUnlockAndRun() {
        Dependency.get(ActivityStarter.class).postQSRunnableDismissingKeyguard(new _$$Lambda$CustomTile$q1MKWZaaapZOjYFe9CyeyabLR0Q(this));
    }
    
    public void updateState(final Tile tile) {
        this.mTile.setIcon(tile.getIcon());
        this.mTile.setLabel(tile.getLabel());
        this.mTile.setSubtitle(tile.getSubtitle());
        this.mTile.setContentDescription(tile.getContentDescription());
        this.mTile.setStateDescription(tile.getStateDescription());
        this.mTile.setState(tile.getState());
    }
}
