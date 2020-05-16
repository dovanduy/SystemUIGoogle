// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.function.Supplier;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.util.Assert;
import android.os.StrictMode$ThreadPolicy$Builder;
import android.os.Binder;
import android.os.StrictMode;
import android.os.RemoteException;
import com.android.settingslib.utils.ThreadUtils;
import android.os.IBinder;
import android.os.SystemProperties;
import android.os.Build;
import java.util.HashSet;
import android.os.Binder$ProxyTransactListener;
import java.util.ArrayList;
import android.os.Handler;
import android.view.Choreographer;
import java.util.Stack;

public class DejankUtils
{
    public static final boolean STRICT_MODE_ENABLED;
    private static final Runnable sAnimationCallbackRunnable;
    private static Stack<String> sBlockingIpcs;
    private static final Choreographer sChoreographer;
    private static final Handler sHandler;
    private static boolean sImmediate;
    private static final Object sLock;
    private static final ArrayList<Runnable> sPendingRunnables;
    private static final Binder$ProxyTransactListener sProxy;
    private static boolean sTemporarilyIgnoreStrictMode;
    private static final HashSet<String> sWhitelistedFrameworkClasses;
    
    static {
        final boolean is_ENG = Build.IS_ENG;
        boolean strict_MODE_ENABLED = false;
        if (is_ENG || SystemProperties.getBoolean("persist.sysui.strictmode", false)) {
            strict_MODE_ENABLED = true;
        }
        STRICT_MODE_ENABLED = strict_MODE_ENABLED;
        sChoreographer = Choreographer.getInstance();
        sHandler = new Handler();
        sPendingRunnables = new ArrayList<Runnable>();
        DejankUtils.sBlockingIpcs = new Stack<String>();
        sWhitelistedFrameworkClasses = new HashSet<String>();
        sLock = new Object();
        sProxy = (Binder$ProxyTransactListener)new Binder$ProxyTransactListener() {
            public void onTransactEnded(final Object o) {
            }
            
            public Object onTransactStarted(final IBinder binder, final int n) {
                return null;
            }
            
            public Object onTransactStarted(final IBinder binder, final int n, final int n2) {
                Object o = DejankUtils.sLock;
                // monitorenter(o)
                Label_0126: {
                    if ((n2 & 0x1) == 0x1) {
                        break Label_0126;
                    }
                    try {
                        if (!DejankUtils.sBlockingIpcs.empty() && ThreadUtils.isMainThread()) {
                            if (!DejankUtils.sTemporarilyIgnoreStrictMode) {
                                // monitorexit(o)
                                try {
                                    o = binder.getInterfaceDescriptor();
                                    synchronized (DejankUtils.sLock) {
                                        if (DejankUtils.sWhitelistedFrameworkClasses.contains(o)) {
                                            return null;
                                        }
                                    }
                                }
                                catch (RemoteException ex) {
                                    ex.printStackTrace();
                                }
                                final StringBuilder sb = new StringBuilder();
                                sb.append("IPC detected on critical path: ");
                                sb.append(DejankUtils.sBlockingIpcs.peek());
                                StrictMode.noteSlowCall(sb.toString());
                                return null;
                            }
                        }
                        return null;
                    }
                    finally {
                    }
                    // monitorexit(o)
                }
            }
        };
        if (DejankUtils.STRICT_MODE_ENABLED) {
            DejankUtils.sWhitelistedFrameworkClasses.add("android.view.IWindowSession");
            DejankUtils.sWhitelistedFrameworkClasses.add("com.android.internal.policy.IKeyguardStateCallback");
            DejankUtils.sWhitelistedFrameworkClasses.add("android.os.IPowerManager");
            DejankUtils.sWhitelistedFrameworkClasses.add("com.android.internal.statusbar.IStatusBarService");
            Binder.setProxyTransactListener(DejankUtils.sProxy);
            StrictMode.setThreadPolicy(new StrictMode$ThreadPolicy$Builder().detectCustomSlowCalls().penaltyFlashScreen().penaltyLog().build());
        }
        sAnimationCallbackRunnable = (Runnable)_$$Lambda$DejankUtils$SyBRIrRRZtwJZ1Fy9Pe5WnzuioU.INSTANCE;
    }
    
    public static void postAfterTraversal(final Runnable e) {
        if (DejankUtils.sImmediate) {
            e.run();
            return;
        }
        Assert.isMainThread();
        DejankUtils.sPendingRunnables.add(e);
        postAnimationCallback();
    }
    
    private static void postAnimationCallback() {
        DejankUtils.sChoreographer.postCallback(1, DejankUtils.sAnimationCallbackRunnable, (Object)null);
    }
    
    public static void removeCallbacks(final Runnable o) {
        Assert.isMainThread();
        DejankUtils.sPendingRunnables.remove(o);
        DejankUtils.sHandler.removeCallbacks(o);
    }
    
    @VisibleForTesting
    public static void setImmediate(final boolean sImmediate) {
        DejankUtils.sImmediate = sImmediate;
    }
    
    public static void startDetectingBlockingIpcs(final String item) {
        if (DejankUtils.STRICT_MODE_ENABLED) {
            synchronized (DejankUtils.sLock) {
                DejankUtils.sBlockingIpcs.push(item);
            }
        }
    }
    
    public static void stopDetectingBlockingIpcs(final String o) {
        if (DejankUtils.STRICT_MODE_ENABLED) {
            synchronized (DejankUtils.sLock) {
                DejankUtils.sBlockingIpcs.remove(o);
            }
        }
    }
    
    public static <T> T whitelistIpcs(final Supplier<T> supplier) {
        if (DejankUtils.STRICT_MODE_ENABLED && !DejankUtils.sTemporarilyIgnoreStrictMode) {
            final Object sLock = DejankUtils.sLock;
            synchronized (sLock) {
                DejankUtils.sTemporarilyIgnoreStrictMode = true;
                // monitorexit(sLock)
                try {
                    final T value = supplier.get();
                    synchronized (DejankUtils.sLock) {
                        DejankUtils.sTemporarilyIgnoreStrictMode = false;
                        return value;
                    }
                }
                finally {
                    synchronized (DejankUtils.sLock) {
                        DejankUtils.sTemporarilyIgnoreStrictMode = false;
                    }
                    // monitorexit(DejankUtils.sLock)
                }
            }
        }
        return supplier.get();
    }
    
    public static void whitelistIpcs(final Runnable runnable) {
        if (DejankUtils.STRICT_MODE_ENABLED && !DejankUtils.sTemporarilyIgnoreStrictMode) {
            final Object sLock = DejankUtils.sLock;
            synchronized (sLock) {
                DejankUtils.sTemporarilyIgnoreStrictMode = true;
                // monitorexit(sLock)
                try {
                    runnable.run();
                    synchronized (DejankUtils.sLock) {
                        DejankUtils.sTemporarilyIgnoreStrictMode = false;
                    }
                }
                finally {
                    synchronized (DejankUtils.sLock) {
                        DejankUtils.sTemporarilyIgnoreStrictMode = false;
                    }
                    // monitorexit(DejankUtils.sLock)
                }
            }
        }
        runnable.run();
    }
}
