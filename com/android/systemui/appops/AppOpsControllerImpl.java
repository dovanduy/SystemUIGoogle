// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.appops;

import android.os.Handler;
import android.app.AppOpsManager$OnOpActiveChangedListener;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Iterator;
import android.util.ArraySet;
import java.util.ArrayList;
import com.android.systemui.dump.DumpManager;
import android.os.Looper;
import android.content.Context;
import java.util.Set;
import android.util.ArrayMap;
import android.app.AppOpsManager;
import com.android.internal.annotations.GuardedBy;
import java.util.List;
import com.android.systemui.Dumpable;
import android.app.AppOpsManager$OnOpNotedListener;
import android.app.AppOpsManager$OnOpActiveChangedInternalListener;

public class AppOpsControllerImpl implements AppOpsController, AppOpsManager$OnOpActiveChangedInternalListener, AppOpsManager$OnOpNotedListener, Dumpable
{
    protected static final int[] OPS;
    @GuardedBy({ "mActiveItems" })
    private final List<AppOpItem> mActiveItems;
    private final AppOpsManager mAppOps;
    private H mBGHandler;
    private final List<Callback> mCallbacks;
    private final ArrayMap<Integer, Set<Callback>> mCallbacksByCode;
    private boolean mListening;
    @GuardedBy({ "mNotedItems" })
    private final List<AppOpItem> mNotedItems;
    
    static {
        OPS = new int[] { 26, 24, 27, 0, 1 };
    }
    
    public AppOpsControllerImpl(final Context context, final Looper looper, final DumpManager dumpManager) {
        final int[] ops = AppOpsControllerImpl.OPS;
        this.mCallbacks = new ArrayList<Callback>();
        this.mCallbacksByCode = (ArrayMap<Integer, Set<Callback>>)new ArrayMap();
        this.mActiveItems = new ArrayList<AppOpItem>();
        this.mNotedItems = new ArrayList<AppOpItem>();
        this.mAppOps = (AppOpsManager)context.getSystemService("appops");
        this.mBGHandler = new H(looper);
        for (int length = ops.length, i = 0; i < length; ++i) {
            this.mCallbacksByCode.put((Object)ops[i], (Object)new ArraySet());
        }
        dumpManager.registerDumpable("AppOpsControllerImpl", this);
    }
    
    private boolean addNoted(final int n, final int n2, final String s) {
        synchronized (this.mNotedItems) {
            final AppOpItem appOpItemLocked = this.getAppOpItemLocked(this.mNotedItems, n, n2, s);
            boolean b;
            AppOpItem appOpItem2;
            if (appOpItemLocked == null) {
                final AppOpItem appOpItem = new AppOpItem(n, n2, s, System.currentTimeMillis());
                this.mNotedItems.add(appOpItem);
                b = true;
                appOpItem2 = appOpItem;
            }
            else {
                b = false;
                appOpItem2 = appOpItemLocked;
            }
            // monitorexit(this.mNotedItems)
            this.mBGHandler.removeCallbacksAndMessages((Object)appOpItem2);
            this.mBGHandler.scheduleRemoval(appOpItem2, 5000L);
            return b;
        }
    }
    
    private AppOpItem getAppOpItemLocked(final List<AppOpItem> list, final int n, final int n2, final String anObject) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final AppOpItem appOpItem = list.get(i);
            if (appOpItem.getCode() == n && appOpItem.getUid() == n2 && appOpItem.getPackageName().equals(anObject)) {
                return appOpItem;
            }
        }
        return null;
    }
    
    private void notifySuscribers(final int n, final int n2, final String s, final boolean b) {
        if (this.mCallbacksByCode.containsKey((Object)n)) {
            final Iterator<Callback> iterator = ((Set)this.mCallbacksByCode.get((Object)n)).iterator();
            while (iterator.hasNext()) {
                iterator.next().onActiveStateChanged(n, n2, s, b);
            }
        }
    }
    
    private void removeNoted(final int n, final int n2, final String s) {
        synchronized (this.mNotedItems) {
            final AppOpItem appOpItemLocked = this.getAppOpItemLocked(this.mNotedItems, n, n2, s);
            if (appOpItemLocked == null) {
                return;
            }
            this.mNotedItems.remove(appOpItemLocked);
            // monitorexit(this.mNotedItems)
            final List<AppOpItem> mActiveItems = this.mActiveItems;
            synchronized (this.mNotedItems) {
                final boolean b = this.getAppOpItemLocked(this.mActiveItems, n, n2, s) != null;
                // monitorexit(this.mNotedItems)
                if (!b) {
                    this.notifySuscribers(n, n2, s, false);
                }
            }
        }
    }
    
    private boolean updateActives(final int n, final int n2, final String s, final boolean b) {
        synchronized (this.mActiveItems) {
            final AppOpItem appOpItemLocked = this.getAppOpItemLocked(this.mActiveItems, n, n2, s);
            if (appOpItemLocked == null && b) {
                this.mActiveItems.add(new AppOpItem(n, n2, s, System.currentTimeMillis()));
                return true;
            }
            if (appOpItemLocked != null && !b) {
                this.mActiveItems.remove(appOpItemLocked);
                return true;
            }
            return false;
        }
    }
    
    @Override
    public void addCallback(final int[] array, final Callback callback) {
        final int length = array.length;
        int i = 0;
        boolean b = false;
        while (i < length) {
            if (this.mCallbacksByCode.containsKey((Object)array[i])) {
                ((Set)this.mCallbacksByCode.get((Object)array[i])).add(callback);
                b = true;
            }
            ++i;
        }
        if (b) {
            this.mCallbacks.add(callback);
        }
        if (!this.mCallbacks.isEmpty()) {
            this.setListening(true);
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("AppOpsController state:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  Listening: ");
        sb.append(this.mListening);
        printWriter.println(sb.toString());
        printWriter.println("  Active Items:");
        final int n = 0;
        for (int i = 0; i < this.mActiveItems.size(); ++i) {
            final AppOpItem appOpItem = this.mActiveItems.get(i);
            printWriter.print("    ");
            printWriter.println(appOpItem.toString());
        }
        printWriter.println("  Noted Items:");
        for (int j = n; j < this.mNotedItems.size(); ++j) {
            final AppOpItem appOpItem2 = this.mNotedItems.get(j);
            printWriter.print("    ");
            printWriter.println(appOpItem2.toString());
        }
    }
    
    public void onOpActiveChanged(final int n, final int n2, final String s, final boolean b) {
        if (!this.updateActives(n, n2, s, b)) {
            return;
        }
        synchronized (this.mNotedItems) {
            final boolean b2 = this.getAppOpItemLocked(this.mNotedItems, n, n2, s) != null;
            // monitorexit(this.mNotedItems)
            if (!b2) {
                this.mBGHandler.post((Runnable)new _$$Lambda$AppOpsControllerImpl$ytWudla0eUXQNol33KSx7VyQvYM(this, n, n2, s, b));
            }
        }
    }
    
    public void onOpNoted(final int n, final int n2, final String s, int n3) {
        if (n3 != 0) {
            return;
        }
        if (!this.addNoted(n, n2, s)) {
            return;
        }
        synchronized (this.mActiveItems) {
            if (this.getAppOpItemLocked(this.mActiveItems, n, n2, s) != null) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            // monitorexit(this.mActiveItems)
            if (n3 == 0) {
                this.mBGHandler.post((Runnable)new _$$Lambda$AppOpsControllerImpl$Ik_chvj1nqb8W_dVPetwy70ZXqg(this, n, n2, s));
            }
        }
    }
    
    @VisibleForTesting
    protected void setBGHandler(final H mbgHandler) {
        this.mBGHandler = mbgHandler;
    }
    
    @VisibleForTesting
    protected void setListening(final boolean mListening) {
        final int[] ops = AppOpsControllerImpl.OPS;
        this.mListening = mListening;
        if (mListening) {
            this.mAppOps.startWatchingActive(ops, (AppOpsManager$OnOpActiveChangedListener)this);
            this.mAppOps.startWatchingNoted(ops, (AppOpsManager$OnOpNotedListener)this);
            return;
        }
        this.mAppOps.stopWatchingActive((AppOpsManager$OnOpActiveChangedListener)this);
        this.mAppOps.stopWatchingNoted((AppOpsManager$OnOpNotedListener)this);
        this.mBGHandler.removeCallbacksAndMessages((Object)null);
        synchronized (this.mActiveItems) {
            this.mActiveItems.clear();
            // monitorexit(this.mActiveItems)
            synchronized (this.mNotedItems) {
                this.mNotedItems.clear();
            }
        }
    }
    
    protected class H extends Handler
    {
        H(final Looper looper) {
            super(looper);
        }
        
        public void scheduleRemoval(final AppOpItem appOpItem, final long n) {
            this.removeCallbacksAndMessages((Object)appOpItem);
            this.postDelayed((Runnable)new Runnable() {
                @Override
                public void run() {
                    AppOpsControllerImpl.this.removeNoted(appOpItem.getCode(), appOpItem.getUid(), appOpItem.getPackageName());
                }
            }, (Object)appOpItem, n);
        }
    }
}
