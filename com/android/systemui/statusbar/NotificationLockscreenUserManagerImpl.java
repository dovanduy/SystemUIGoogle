// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.NotificationUtils;
import java.util.concurrent.Executor;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.provider.Settings$Global;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.Log;
import java.util.Iterator;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import android.content.ComponentName;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.UserManager;
import android.os.Handler;
import android.database.ContentObserver;
import android.util.SparseBooleanArray;
import com.android.internal.widget.LockPatternUtils;
import java.util.List;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.KeyguardManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.app.admin.DevicePolicyManager;
import android.util.SparseArray;
import android.content.pm.UserInfo;
import java.util.ArrayList;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.internal.statusbar.IStatusBarService;
import android.content.BroadcastReceiver;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.Dumpable;

public class NotificationLockscreenUserManagerImpl implements Dumpable, NotificationLockscreenUserManager, StateListener
{
    protected final BroadcastReceiver mAllUsersReceiver;
    private boolean mAllowLockscreenRemoteInput;
    private final IStatusBarService mBarService;
    protected final BroadcastReceiver mBaseBroadcastReceiver;
    private final BroadcastDispatcher mBroadcastDispatcher;
    protected final Context mContext;
    protected final ArrayList<UserInfo> mCurrentManagedProfiles;
    protected final SparseArray<UserInfo> mCurrentProfiles;
    protected int mCurrentUserId;
    private final DevicePolicyManager mDevicePolicyManager;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private NotificationEntryManager mEntryManager;
    protected KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    private final List<UserChangedListener> mListeners;
    private final Object mLock;
    private LockPatternUtils mLockPatternUtils;
    private final SparseBooleanArray mLockscreenPublicMode;
    protected ContentObserver mLockscreenSettingsObserver;
    private final Handler mMainHandler;
    protected NotificationPresenter mPresenter;
    protected ContentObserver mSettingsObserver;
    private boolean mShowLockscreenNotifications;
    private int mState;
    private final UserManager mUserManager;
    private final SparseBooleanArray mUsersAllowingNotifications;
    private final SparseBooleanArray mUsersAllowingPrivateNotifications;
    private final SparseBooleanArray mUsersWithSeperateWorkChallenge;
    
    public NotificationLockscreenUserManagerImpl(final Context mContext, final BroadcastDispatcher mBroadcastDispatcher, final DevicePolicyManager mDevicePolicyManager, final UserManager mUserManager, final IStatusBarService mBarService, final KeyguardManager mKeyguardManager, final StatusBarStateController statusBarStateController, final Handler mMainHandler, final DeviceProvisionedController mDeviceProvisionedController, final KeyguardStateController mKeyguardStateController) {
        this.mLock = new Object();
        this.mLockscreenPublicMode = new SparseBooleanArray();
        this.mUsersWithSeperateWorkChallenge = new SparseBooleanArray();
        this.mUsersAllowingPrivateNotifications = new SparseBooleanArray();
        this.mUsersAllowingNotifications = new SparseBooleanArray();
        this.mListeners = new ArrayList<UserChangedListener>();
        this.mState = 0;
        this.mAllUsersReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(intent.getAction()) && NotificationLockscreenUserManagerImpl.this.isCurrentProfile(this.getSendingUserId())) {
                    NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                    NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                    NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED");
                }
            }
        };
        this.mBaseBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context p0, final Intent p1) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: invokevirtual   android/content/Intent.getAction:()Ljava/lang/String;
                //     4: astore_1       
                //     5: aload_1        
                //     6: invokevirtual   java/lang/String.hashCode:()I
                //     9: istore_3       
                //    10: iconst_0       
                //    11: istore          4
                //    13: iload_3        
                //    14: lookupswitch {
                //          -1238404651: 145
                //          -864107122: 131
                //          -598152660: 117
                //          833559602: 103
                //          959232034: 89
                //          1121780209: 75
                //          default: 72
                //        }
                //    72: goto            159
                //    75: aload_1        
                //    76: ldc             "android.intent.action.USER_ADDED"
                //    78: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
                //    81: ifeq            159
                //    84: iconst_1       
                //    85: istore_3       
                //    86: goto            161
                //    89: aload_1        
                //    90: ldc             "android.intent.action.USER_SWITCHED"
                //    92: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
                //    95: ifeq            159
                //    98: iconst_0       
                //    99: istore_3       
                //   100: goto            161
                //   103: aload_1        
                //   104: ldc             "android.intent.action.USER_UNLOCKED"
                //   106: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
                //   109: ifeq            159
                //   112: iconst_4       
                //   113: istore_3       
                //   114: goto            161
                //   117: aload_1        
                //   118: ldc             "com.android.systemui.statusbar.work_challenge_unlocked_notification_action"
                //   120: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
                //   123: ifeq            159
                //   126: iconst_5       
                //   127: istore_3       
                //   128: goto            161
                //   131: aload_1        
                //   132: ldc             "android.intent.action.MANAGED_PROFILE_AVAILABLE"
                //   134: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
                //   137: ifeq            159
                //   140: iconst_2       
                //   141: istore_3       
                //   142: goto            161
                //   145: aload_1        
                //   146: ldc             "android.intent.action.MANAGED_PROFILE_UNAVAILABLE"
                //   148: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
                //   151: ifeq            159
                //   154: iconst_3       
                //   155: istore_3       
                //   156: goto            161
                //   159: iconst_m1      
                //   160: istore_3       
                //   161: iload_3        
                //   162: ifeq            329
                //   165: iload_3        
                //   166: iconst_1       
                //   167: if_icmpeq       319
                //   170: iload_3        
                //   171: iconst_2       
                //   172: if_icmpeq       319
                //   175: iload_3        
                //   176: iconst_3       
                //   177: if_icmpeq       319
                //   180: iload_3        
                //   181: iconst_4       
                //   182: if_icmpeq       305
                //   185: iload_3        
                //   186: iconst_5       
                //   187: if_icmpeq       193
                //   190: goto            484
                //   193: aload_2        
                //   194: ldc             "android.intent.extra.INTENT"
                //   196: invokevirtual   android/content/Intent.getParcelableExtra:(Ljava/lang/String;)Landroid/os/Parcelable;
                //   199: checkcast       Landroid/content/IntentSender;
                //   202: astore          5
                //   204: aload_2        
                //   205: ldc             "android.intent.extra.INDEX"
                //   207: invokevirtual   android/content/Intent.getStringExtra:(Ljava/lang/String;)Ljava/lang/String;
                //   210: astore_1       
                //   211: aload           5
                //   213: ifnull          232
                //   216: aload_0        
                //   217: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   220: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mContext:Landroid/content/Context;
                //   223: aload           5
                //   225: aconst_null    
                //   226: iconst_0       
                //   227: iconst_0       
                //   228: iconst_0       
                //   229: invokevirtual   android/content/Context.startIntentSender:(Landroid/content/IntentSender;Landroid/content/Intent;III)V
                //   232: aload_1        
                //   233: ifnull          484
                //   236: aload_0        
                //   237: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   240: invokestatic    com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.access$100:(Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;)Lcom/android/systemui/statusbar/notification/NotificationEntryManager;
                //   243: aload_1        
                //   244: invokevirtual   com/android/systemui/statusbar/notification/NotificationEntryManager.getActiveNotificationUnfiltered:(Ljava/lang/String;)Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;
                //   247: astore_2       
                //   248: aload_0        
                //   249: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   252: invokestatic    com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.access$100:(Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;)Lcom/android/systemui/statusbar/notification/NotificationEntryManager;
                //   255: invokevirtual   com/android/systemui/statusbar/notification/NotificationEntryManager.getActiveNotificationsCount:()I
                //   258: istore          6
                //   260: iload           4
                //   262: istore_3       
                //   263: aload_2        
                //   264: ifnull          275
                //   267: aload_2        
                //   268: invokevirtual   com/android/systemui/statusbar/notification/collection/NotificationEntry.getRanking:()Landroid/service/notification/NotificationListenerService$Ranking;
                //   271: invokevirtual   android/service/notification/NotificationListenerService$Ranking.getRank:()I
                //   274: istore_3       
                //   275: aload_1        
                //   276: iload_3        
                //   277: iload           6
                //   279: iconst_1       
                //   280: aload_2        
                //   281: invokestatic    com/android/systemui/statusbar/notification/logging/NotificationLogger.getNotificationLocation:(Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;)Lcom/android/internal/statusbar/NotificationVisibility$NotificationLocation;
                //   284: invokestatic    com/android/internal/statusbar/NotificationVisibility.obtain:(Ljava/lang/String;IIZLcom/android/internal/statusbar/NotificationVisibility$NotificationLocation;)Lcom/android/internal/statusbar/NotificationVisibility;
                //   287: astore_2       
                //   288: aload_0        
                //   289: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   292: invokestatic    com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.access$400:(Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;)Lcom/android/internal/statusbar/IStatusBarService;
                //   295: aload_1        
                //   296: aload_2        
                //   297: invokeinterface com/android/internal/statusbar/IStatusBarService.onNotificationClick:(Ljava/lang/String;Lcom/android/internal/statusbar/NotificationVisibility;)V
                //   302: goto            484
                //   305: ldc             Lcom/android/systemui/recents/OverviewProxyService;.class
                //   307: invokestatic    com/android/systemui/Dependency.get:(Ljava/lang/Class;)Ljava/lang/Object;
                //   310: checkcast       Lcom/android/systemui/recents/OverviewProxyService;
                //   313: invokevirtual   com/android/systemui/recents/OverviewProxyService.startConnectionToCurrentUser:()V
                //   316: goto            484
                //   319: aload_0        
                //   320: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   323: invokestatic    com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.access$200:(Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;)V
                //   326: goto            484
                //   329: aload_0        
                //   330: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   333: aload_2        
                //   334: ldc             "android.intent.extra.user_handle"
                //   336: iconst_m1      
                //   337: invokevirtual   android/content/Intent.getIntExtra:(Ljava/lang/String;I)I
                //   340: putfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentUserId:I
                //   343: aload_0        
                //   344: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   347: invokestatic    com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.access$200:(Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;)V
                //   350: new             Ljava/lang/StringBuilder;
                //   353: dup            
                //   354: invokespecial   java/lang/StringBuilder.<init>:()V
                //   357: astore_1       
                //   358: aload_1        
                //   359: ldc             "userId "
                //   361: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   364: pop            
                //   365: aload_1        
                //   366: aload_0        
                //   367: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   370: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentUserId:I
                //   373: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
                //   376: pop            
                //   377: aload_1        
                //   378: ldc             " is in the house"
                //   380: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   383: pop            
                //   384: ldc             "LockscreenUserManager"
                //   386: aload_1        
                //   387: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                //   390: invokestatic    android/util/Log.v:(Ljava/lang/String;Ljava/lang/String;)I
                //   393: pop            
                //   394: aload_0        
                //   395: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   398: invokevirtual   com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.updateLockscreenNotificationSetting:()V
                //   401: aload_0        
                //   402: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   405: invokevirtual   com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.updatePublicMode:()V
                //   408: aload_0        
                //   409: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   412: invokestatic    com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.access$100:(Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;)Lcom/android/systemui/statusbar/notification/NotificationEntryManager;
                //   415: ldc             "user switched"
                //   417: invokevirtual   com/android/systemui/statusbar/notification/NotificationEntryManager.reapplyFilterAndSort:(Ljava/lang/String;)V
                //   420: aload_0        
                //   421: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   424: astore_1       
                //   425: aload_1        
                //   426: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mPresenter:Lcom/android/systemui/statusbar/NotificationPresenter;
                //   429: aload_1        
                //   430: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentUserId:I
                //   433: invokeinterface com/android/systemui/statusbar/NotificationPresenter.onUserSwitched:(I)V
                //   438: aload_0        
                //   439: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   442: invokestatic    com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.access$300:(Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;)Ljava/util/List;
                //   445: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
                //   450: astore_1       
                //   451: aload_1        
                //   452: invokeinterface java/util/Iterator.hasNext:()Z
                //   457: ifeq            484
                //   460: aload_1        
                //   461: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
                //   466: checkcast       Lcom/android/systemui/statusbar/NotificationLockscreenUserManager$UserChangedListener;
                //   469: aload_0        
                //   470: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl$2.this$0:Lcom/android/systemui/statusbar/NotificationLockscreenUserManagerImpl;
                //   473: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentUserId:I
                //   476: invokeinterface com/android/systemui/statusbar/NotificationLockscreenUserManager$UserChangedListener.onUserChanged:(I)V
                //   481: goto            451
                //   484: return         
                //   485: astore_2       
                //   486: goto            232
                //   489: astore_1       
                //   490: goto            484
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                                              
                //  -----  -----  -----  -----  --------------------------------------------------
                //  216    232    485    489    Landroid/content/IntentSender$SendIntentException;
                //  288    302    489    493    Landroid/os/RemoteException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0305:
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
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1164)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:1009)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:554)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:392)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:294)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
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
        };
        this.mCurrentProfiles = (SparseArray<UserInfo>)new SparseArray();
        this.mCurrentManagedProfiles = new ArrayList<UserInfo>();
        this.mCurrentUserId = 0;
        this.mContext = mContext;
        this.mMainHandler = mMainHandler;
        this.mDevicePolicyManager = mDevicePolicyManager;
        this.mUserManager = mUserManager;
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        this.mBarService = mBarService;
        statusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.mLockPatternUtils = new LockPatternUtils(mContext);
        this.mKeyguardManager = mKeyguardManager;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mDeviceProvisionedController = mDeviceProvisionedController;
        this.mKeyguardStateController = mKeyguardStateController;
    }
    
    private boolean adminAllowsKeyguardFeature(final int n, final int n2) {
        boolean b = true;
        if (n == -1) {
            return true;
        }
        if ((this.mDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName)null, n) & n2) != 0x0) {
            b = false;
        }
        return b;
    }
    
    private NotificationEntryManager getEntryManager() {
        if (this.mEntryManager == null) {
            this.mEntryManager = Dependency.get(NotificationEntryManager.class);
        }
        return this.mEntryManager;
    }
    
    private boolean hideSilentNotificationsOnLockscreen() {
        return DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$NotificationLockscreenUserManagerImpl$ghZezzviwGt8pgH_T3DEzpSavw8(this));
    }
    
    private boolean packageHasVisibilityOverride(final String s) {
        final NotificationEntryManager entryManager = this.getEntryManager();
        boolean b = true;
        if (entryManager == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return true;
        }
        final NotificationEntry activeNotificationUnfiltered = this.getEntryManager().getActiveNotificationUnfiltered(s);
        if (activeNotificationUnfiltered == null || activeNotificationUnfiltered.getRanking().getVisibilityOverride() != 0) {
            b = false;
        }
        return b;
    }
    
    private void setLockscreenAllowRemoteInput(final boolean mAllowLockscreenRemoteInput) {
        this.mAllowLockscreenRemoteInput = mAllowLockscreenRemoteInput;
    }
    
    private void setShowLockscreenNotifications(final boolean mShowLockscreenNotifications) {
        this.mShowLockscreenNotifications = mShowLockscreenNotifications;
    }
    
    private boolean shouldTemporarilyHideNotifications(final int n) {
        int mCurrentUserId = n;
        if (n == -1) {
            mCurrentUserId = this.mCurrentUserId;
        }
        return Dependency.get(KeyguardUpdateMonitor.class).isUserInLockdown(mCurrentUserId);
    }
    
    private void updateCurrentProfilesCache() {
        synchronized (this.mLock) {
            this.mCurrentProfiles.clear();
            this.mCurrentManagedProfiles.clear();
            if (this.mUserManager != null) {
                for (final UserInfo e : this.mUserManager.getProfiles(this.mCurrentUserId)) {
                    this.mCurrentProfiles.put(e.id, (Object)e);
                    if ("android.os.usertype.profile.MANAGED".equals(e.userType)) {
                        this.mCurrentManagedProfiles.add(e);
                    }
                }
            }
            // monitorexit(this.mLock)
            this.mMainHandler.post((Runnable)new _$$Lambda$NotificationLockscreenUserManagerImpl$PLQsiLSkjaG6xwZdvFK_TGqwDWU(this));
        }
    }
    
    @Override
    public void addUserChangedListener(final UserChangedListener userChangedListener) {
        this.mListeners.add(userChangedListener);
    }
    
    public boolean allowsManagedPrivateNotificationsInPublic() {
        synchronized (this.mLock) {
            final Iterator<UserInfo> iterator = this.mCurrentManagedProfiles.iterator();
            while (iterator.hasNext()) {
                if (!this.userAllowsPrivateNotificationsInPublic(iterator.next().id)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    @Override
    public void dump(final FileDescriptor p0, final PrintWriter p1, final String[] p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ldc_w           "NotificationLockscreenUserManager state:"
        //     4: invokevirtual   java/io/PrintWriter.println:(Ljava/lang/String;)V
        //     7: aload_2        
        //     8: ldc_w           "  mCurrentUserId="
        //    11: invokevirtual   java/io/PrintWriter.print:(Ljava/lang/String;)V
        //    14: aload_2        
        //    15: aload_0        
        //    16: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentUserId:I
        //    19: invokevirtual   java/io/PrintWriter.println:(I)V
        //    22: aload_2        
        //    23: ldc_w           "  mShowLockscreenNotifications="
        //    26: invokevirtual   java/io/PrintWriter.print:(Ljava/lang/String;)V
        //    29: aload_2        
        //    30: aload_0        
        //    31: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mShowLockscreenNotifications:Z
        //    34: invokevirtual   java/io/PrintWriter.println:(Z)V
        //    37: aload_2        
        //    38: ldc_w           "  mAllowLockscreenRemoteInput="
        //    41: invokevirtual   java/io/PrintWriter.print:(Ljava/lang/String;)V
        //    44: aload_2        
        //    45: aload_0        
        //    46: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mAllowLockscreenRemoteInput:Z
        //    49: invokevirtual   java/io/PrintWriter.println:(Z)V
        //    52: aload_2        
        //    53: ldc_w           "  mCurrentProfiles="
        //    56: invokevirtual   java/io/PrintWriter.print:(Ljava/lang/String;)V
        //    59: aload_0        
        //    60: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mLock:Ljava/lang/Object;
        //    63: astore_1       
        //    64: aload_1        
        //    65: monitorenter   
        //    66: aload_0        
        //    67: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentProfiles:Landroid/util/SparseArray;
        //    70: invokevirtual   android/util/SparseArray.size:()I
        //    73: iconst_1       
        //    74: isub           
        //    75: istore          4
        //    77: iload           4
        //    79: iflt            144
        //    82: aload_0        
        //    83: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentProfiles:Landroid/util/SparseArray;
        //    86: iload           4
        //    88: invokevirtual   android/util/SparseArray.valueAt:(I)Ljava/lang/Object;
        //    91: checkcast       Landroid/content/pm/UserInfo;
        //    94: getfield        android/content/pm/UserInfo.id:I
        //    97: istore          5
        //    99: new             Ljava/lang/StringBuilder;
        //   102: astore_3       
        //   103: aload_3        
        //   104: invokespecial   java/lang/StringBuilder.<init>:()V
        //   107: aload_3        
        //   108: ldc_w           ""
        //   111: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   114: pop            
        //   115: aload_3        
        //   116: iload           5
        //   118: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   121: pop            
        //   122: aload_3        
        //   123: ldc_w           " "
        //   126: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   129: pop            
        //   130: aload_2        
        //   131: aload_3        
        //   132: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   135: invokevirtual   java/io/PrintWriter.print:(Ljava/lang/String;)V
        //   138: iinc            4, -1
        //   141: goto            77
        //   144: aload_1        
        //   145: monitorexit    
        //   146: aload_2        
        //   147: ldc_w           "  mCurrentManagedProfiles="
        //   150: invokevirtual   java/io/PrintWriter.print:(Ljava/lang/String;)V
        //   153: aload_0        
        //   154: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mLock:Ljava/lang/Object;
        //   157: astore_1       
        //   158: aload_1        
        //   159: monitorenter   
        //   160: aload_0        
        //   161: getfield        com/android/systemui/statusbar/NotificationLockscreenUserManagerImpl.mCurrentManagedProfiles:Ljava/util/ArrayList;
        //   164: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   167: astore          6
        //   169: aload           6
        //   171: invokeinterface java/util/Iterator.hasNext:()Z
        //   176: ifeq            236
        //   179: aload           6
        //   181: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   186: checkcast       Landroid/content/pm/UserInfo;
        //   189: astore          7
        //   191: new             Ljava/lang/StringBuilder;
        //   194: astore_3       
        //   195: aload_3        
        //   196: invokespecial   java/lang/StringBuilder.<init>:()V
        //   199: aload_3        
        //   200: ldc_w           ""
        //   203: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   206: pop            
        //   207: aload_3        
        //   208: aload           7
        //   210: getfield        android/content/pm/UserInfo.id:I
        //   213: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   216: pop            
        //   217: aload_3        
        //   218: ldc_w           " "
        //   221: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   224: pop            
        //   225: aload_2        
        //   226: aload_3        
        //   227: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   230: invokevirtual   java/io/PrintWriter.print:(Ljava/lang/String;)V
        //   233: goto            169
        //   236: aload_1        
        //   237: monitorexit    
        //   238: aload_2        
        //   239: invokevirtual   java/io/PrintWriter.println:()V
        //   242: return         
        //   243: astore_2       
        //   244: aload_1        
        //   245: monitorexit    
        //   246: aload_2        
        //   247: athrow         
        //   248: astore_2       
        //   249: aload_1        
        //   250: monitorexit    
        //   251: aload_2        
        //   252: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  66     77     248    253    Any
        //  82     138    248    253    Any
        //  144    146    248    253    Any
        //  160    169    243    248    Any
        //  169    233    243    248    Any
        //  236    238    243    248    Any
        //  244    246    243    248    Any
        //  249    251    248    253    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:188)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
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
    
    public SparseArray<UserInfo> getCurrentProfiles() {
        return this.mCurrentProfiles;
    }
    
    @Override
    public int getCurrentUserId() {
        return this.mCurrentUserId;
    }
    
    public boolean isAnyManagedProfilePublicMode() {
        synchronized (this.mLock) {
            for (int i = this.mCurrentManagedProfiles.size() - 1; i >= 0; --i) {
                if (this.isLockscreenPublicMode(this.mCurrentManagedProfiles.get(i).id)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public boolean isAnyProfilePublicMode() {
        synchronized (this.mLock) {
            for (int i = this.mCurrentProfiles.size() - 1; i >= 0; --i) {
                if (this.isLockscreenPublicMode(((UserInfo)this.mCurrentProfiles.valueAt(i)).id)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public boolean isCurrentProfile(final int n) {
        final Object mLock = this.mLock;
        // monitorenter(mLock)
        Label_0036: {
            if (n == -1) {
                break Label_0036;
            }
            Label_0038: {
                try {
                    if (this.mCurrentProfiles.get(n) != null) {
                        break Label_0036;
                    }
                    final boolean b = false;
                    break Label_0038;
                }
                finally {
                    // monitorexit(mLock)
                    // monitorexit(mLock)
                    return true;
                }
            }
        }
    }
    
    @Override
    public boolean isLockscreenPublicMode(final int n) {
        if (n == -1) {
            return this.mLockscreenPublicMode.get(this.mCurrentUserId, false);
        }
        return this.mLockscreenPublicMode.get(n, false);
    }
    
    @Override
    public boolean needsRedaction(final NotificationEntry notificationEntry) {
        final int userId = notificationEntry.getSbn().getUserId();
        final boolean userAllowsPrivateNotificationsInPublic = this.userAllowsPrivateNotificationsInPublic(this.mCurrentUserId);
        final boolean b = true;
        final boolean userAllowsPrivateNotificationsInPublic2 = this.userAllowsPrivateNotificationsInPublic(userId);
        final boolean b2 = (userAllowsPrivateNotificationsInPublic ^ true) || (userAllowsPrivateNotificationsInPublic2 ^ true);
        final boolean b3 = notificationEntry.getSbn().getNotification().visibility == 0;
        boolean b4 = b;
        if (!this.packageHasVisibilityOverride(notificationEntry.getSbn().getKey())) {
            b4 = (b3 && b2 && b);
        }
        return b4;
    }
    
    @Override
    public boolean needsSeparateWorkChallenge(final int n) {
        return this.mUsersWithSeperateWorkChallenge.get(n, false);
    }
    
    @Override
    public void onStateChanged(final int mState) {
        this.mState = mState;
        this.updatePublicMode();
    }
    
    public void setLockscreenPublicMode(final boolean b, final int n) {
        this.mLockscreenPublicMode.put(n, b);
    }
    
    @Override
    public void setUpWithPresenter(final NotificationPresenter mPresenter) {
        this.mPresenter = mPresenter;
        this.mLockscreenSettingsObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(final boolean b) {
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("LOCK_SCREEN_SHOW_NOTIFICATIONS, or LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS change");
            }
        };
        this.mSettingsObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(final boolean b) {
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                if (NotificationLockscreenUserManagerImpl.this.mDeviceProvisionedController.isDeviceProvisioned()) {
                    NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("LOCK_SCREEN_ALLOW_REMOTE_INPUT or ZEN_MODE change");
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("lock_screen_show_notifications"), false, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("lock_screen_allow_private_notifications"), true, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("zen_mode"), false, this.mSettingsObserver);
        this.mBroadcastDispatcher.registerReceiver(this.mAllUsersReceiver, new IntentFilter("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED"), null, UserHandle.ALL);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        this.mBroadcastDispatcher.registerReceiver(this.mBaseBroadcastReceiver, intentFilter, null, UserHandle.ALL);
        final IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
        this.mContext.registerReceiver(this.mBaseBroadcastReceiver, intentFilter2, "com.android.systemui.permission.SELF", (Handler)null);
        this.updateCurrentProfilesCache();
        this.mSettingsObserver.onChange(false);
    }
    
    @Override
    public boolean shouldAllowLockscreenRemoteInput() {
        return this.mAllowLockscreenRemoteInput;
    }
    
    @Override
    public boolean shouldHideNotifications(final int n) {
        if (!this.isLockscreenPublicMode(n) || this.userAllowsNotificationsInPublic(n)) {
            final int mCurrentUserId = this.mCurrentUserId;
            if ((n == mCurrentUserId || !this.shouldHideNotifications(mCurrentUserId)) && !this.shouldTemporarilyHideNotifications(n)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean shouldHideNotifications(final String s) {
        final NotificationEntryManager entryManager = this.getEntryManager();
        boolean b = true;
        if (entryManager == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return true;
        }
        final NotificationEntry activeNotificationUnfiltered = this.getEntryManager().getActiveNotificationUnfiltered(s);
        if (!this.isLockscreenPublicMode(this.mCurrentUserId) || activeNotificationUnfiltered == null || activeNotificationUnfiltered.getRanking().getVisibilityOverride() != -1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public boolean shouldShowLockscreenNotifications() {
        return this.mShowLockscreenNotifications;
    }
    
    @Override
    public boolean shouldShowOnKeyguard(final NotificationEntry notificationEntry) {
        final NotificationEntryManager entryManager = this.getEntryManager();
        final boolean b = false;
        if (entryManager == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return false;
        }
        boolean b2;
        if (NotificationUtils.useNewInterruptionModel(this.mContext) && this.hideSilentNotificationsOnLockscreen()) {
            b2 = (notificationEntry.getBucket() != 4);
        }
        else {
            b2 = (notificationEntry.getRanking().isAmbient() ^ true);
        }
        boolean b3 = b;
        if (this.mShowLockscreenNotifications) {
            b3 = b;
            if (b2) {
                b3 = true;
            }
        }
        return b3;
    }
    
    protected void updateLockscreenNotificationSetting() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        final int mCurrentUserId = this.mCurrentUserId;
        boolean showLockscreenNotifications = true;
        final boolean b = Settings$Secure.getIntForUser(contentResolver, "lock_screen_show_notifications", 1, mCurrentUserId) != 0;
        final boolean b2 = (this.mDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName)null, this.mCurrentUserId) & 0x4) == 0x0;
        if (!b || !b2) {
            showLockscreenNotifications = false;
        }
        this.setShowLockscreenNotifications(showLockscreenNotifications);
        this.setLockscreenAllowRemoteInput(false);
    }
    
    @Override
    public void updatePublicMode() {
        final boolean b = this.mState != 0 || this.mKeyguardStateController.isShowing();
        final boolean b2 = b && this.mKeyguardStateController.isMethodSecure();
        final SparseArray<UserInfo> currentProfiles = this.getCurrentProfiles();
        this.mUsersWithSeperateWorkChallenge.clear();
        for (int i = currentProfiles.size() - 1; i >= 0; --i) {
            final int id = ((UserInfo)currentProfiles.valueAt(i)).id;
            final boolean booleanValue = DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$NotificationLockscreenUserManagerImpl$R0Mmt5x5H5RiJ7r74XavfJAbwsU(this, id));
            boolean b3;
            if (!b2 && id != this.getCurrentUserId() && booleanValue && this.mLockPatternUtils.isSecure(id)) {
                b3 = (b || this.mKeyguardManager.isDeviceLocked(id));
            }
            else {
                b3 = b2;
            }
            this.setLockscreenPublicMode(b3, id);
            this.mUsersWithSeperateWorkChallenge.put(id, booleanValue);
        }
        this.getEntryManager().updateNotifications("NotificationLockscreenUserManager.updatePublicMode");
    }
    
    @Override
    public boolean userAllowsNotificationsInPublic(final int n) {
        final boolean currentProfile = this.isCurrentProfile(n);
        boolean b = true;
        if (currentProfile && n != this.mCurrentUserId) {
            return true;
        }
        if (this.mUsersAllowingNotifications.indexOfKey(n) < 0) {
            final boolean b2 = Settings$Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 0, n) != 0;
            final boolean adminAllowsKeyguardFeature = this.adminAllowsKeyguardFeature(n, 4);
            final boolean privateNotificationsAllowed = this.mKeyguardManager.getPrivateNotificationsAllowed();
            if (!b2 || !adminAllowsKeyguardFeature || !privateNotificationsAllowed) {
                b = false;
            }
            this.mUsersAllowingNotifications.append(n, b);
            return b;
        }
        return this.mUsersAllowingNotifications.get(n);
    }
    
    @Override
    public boolean userAllowsPrivateNotificationsInPublic(final int n) {
        boolean b = true;
        if (n == -1) {
            return true;
        }
        if (this.mUsersAllowingPrivateNotifications.indexOfKey(n) < 0) {
            final boolean b2 = Settings$Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 0, n) != 0;
            final boolean adminAllowsKeyguardFeature = this.adminAllowsKeyguardFeature(n, 8);
            if (!b2 || !adminAllowsKeyguardFeature) {
                b = false;
            }
            this.mUsersAllowingPrivateNotifications.append(n, b);
            return b;
        }
        return this.mUsersAllowingPrivateNotifications.get(n);
    }
}
