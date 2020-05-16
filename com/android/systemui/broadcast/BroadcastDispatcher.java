// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.broadcast;

import android.os.HandlerExecutor;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.annotations.VisibleForTesting;
import android.os.UserHandle;
import java.util.concurrent.Executor;
import android.content.BroadcastReceiver;
import android.text.TextUtils;
import android.content.IntentFilter;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.dump.DumpManager;
import android.os.Handler;
import android.util.SparseArray;
import android.content.Context;
import android.os.Looper;
import com.android.systemui.Dumpable;

public class BroadcastDispatcher implements Dumpable
{
    private final Looper bgLooper;
    private final Context context;
    private final BroadcastDispatcher$handler.BroadcastDispatcher$handler$1 handler;
    private final SparseArray<UserBroadcastDispatcher> receiversByUser;
    
    public BroadcastDispatcher(final Context context, final Handler handler, final Looper bgLooper, final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "mainHandler");
        Intrinsics.checkParameterIsNotNull(bgLooper, "bgLooper");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.context = context;
        this.bgLooper = bgLooper;
        this.receiversByUser = (SparseArray<UserBroadcastDispatcher>)new SparseArray(20);
        final String name = BroadcastDispatcher.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        this.handler = new BroadcastDispatcher$handler.BroadcastDispatcher$handler$1(this, this.bgLooper);
    }
    
    private final void checkFilter(final IntentFilter intentFilter) {
        final StringBuilder sb = new StringBuilder();
        if (intentFilter.countActions() == 0) {
            sb.append("Filter must contain at least one action. ");
        }
        if (intentFilter.countDataAuthorities() != 0) {
            sb.append("Filter cannot contain DataAuthorities. ");
        }
        if (intentFilter.countDataPaths() != 0) {
            sb.append("Filter cannot contain DataPaths. ");
        }
        if (intentFilter.countDataSchemes() != 0) {
            sb.append("Filter cannot contain DataSchemes. ");
        }
        if (intentFilter.countDataTypes() != 0) {
            sb.append("Filter cannot contain DataTypes. ");
        }
        if (intentFilter.getPriority() != 0) {
            sb.append("Filter cannot modify priority. ");
        }
        if (TextUtils.isEmpty((CharSequence)sb)) {
            return;
        }
        throw new IllegalArgumentException(sb.toString());
    }
    
    public static /* synthetic */ void registerReceiver$default(final BroadcastDispatcher broadcastDispatcher, final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter, Executor mainExecutor, UserHandle user, final int n, final Object o) {
        if (o == null) {
            if ((n & 0x4) != 0x0) {
                mainExecutor = broadcastDispatcher.context.getMainExecutor();
            }
            if ((n & 0x8) != 0x0) {
                user = broadcastDispatcher.context.getUser();
                Intrinsics.checkExpressionValueIsNotNull(user, "context.user");
            }
            broadcastDispatcher.registerReceiver(broadcastReceiver, intentFilter, mainExecutor, user);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: registerReceiver");
    }
    
    public static /* synthetic */ void registerReceiverWithHandler$default(final BroadcastDispatcher broadcastDispatcher, final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter, final Handler handler, UserHandle user, final int n, final Object o) {
        if (o == null) {
            if ((n & 0x8) != 0x0) {
                user = broadcastDispatcher.context.getUser();
                Intrinsics.checkExpressionValueIsNotNull(user, "context.user");
            }
            broadcastDispatcher.registerReceiverWithHandler(broadcastReceiver, intentFilter, handler, user);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: registerReceiverWithHandler");
    }
    
    @VisibleForTesting
    protected UserBroadcastDispatcher createUBRForUser(final int n) {
        return new UserBroadcastDispatcher(this.context, n, this.bgLooper);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        printWriter.println("Broadcast dispatcher:");
        for (int size = this.receiversByUser.size(), i = 0; i < size; ++i) {
            final StringBuilder sb = new StringBuilder();
            sb.append("  User ");
            sb.append(this.receiversByUser.keyAt(i));
            printWriter.println(sb.toString());
            ((UserBroadcastDispatcher)this.receiversByUser.valueAt(i)).dump(fileDescriptor, printWriter, array);
        }
    }
    
    public void registerReceiver(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter) {
        registerReceiver$default(this, broadcastReceiver, intentFilter, null, null, 12, null);
    }
    
    public void registerReceiver(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter, Executor mainExecutor, final UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        Intrinsics.checkParameterIsNotNull(intentFilter, "filter");
        Intrinsics.checkParameterIsNotNull(userHandle, "user");
        this.checkFilter(intentFilter);
        final BroadcastDispatcher$handler.BroadcastDispatcher$handler$1 handler = this.handler;
        if (mainExecutor == null) {
            mainExecutor = this.context.getMainExecutor();
            Intrinsics.checkExpressionValueIsNotNull(mainExecutor, "context.mainExecutor");
        }
        ((Handler)handler).obtainMessage(0, (Object)new ReceiverData(broadcastReceiver, intentFilter, mainExecutor, userHandle)).sendToTarget();
    }
    
    public void registerReceiverWithHandler(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter, final Handler handler) {
        registerReceiverWithHandler$default(this, broadcastReceiver, intentFilter, handler, null, 8, null);
    }
    
    public void registerReceiverWithHandler(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter, final Handler handler, final UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        Intrinsics.checkParameterIsNotNull(intentFilter, "filter");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        Intrinsics.checkParameterIsNotNull(userHandle, "user");
        this.registerReceiver(broadcastReceiver, intentFilter, (Executor)new HandlerExecutor(handler), userHandle);
    }
    
    public void unregisterReceiver(final BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        ((Handler)this.handler).obtainMessage(1, (Object)broadcastReceiver).sendToTarget();
    }
}
