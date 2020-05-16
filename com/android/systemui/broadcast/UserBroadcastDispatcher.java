// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.broadcast;

import android.os.Handler;
import android.os.UserHandle;
import android.content.BroadcastReceiver$PendingResult;
import android.content.Intent;
import java.util.Map;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.function.Predicate;
import kotlin.jvm.internal.Ref$BooleanRef;
import java.util.ArrayList;
import android.util.ArraySet;
import kotlin.sequences.Sequence;
import java.util.Collection;
import java.util.Iterator;
import kotlin.sequences.SequencesKt;
import kotlin.collections.CollectionsKt;
import java.util.LinkedHashSet;
import com.android.internal.util.Preconditions;
import android.content.IntentFilter;
import kotlin.jvm.internal.Intrinsics;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import android.content.Context;
import android.os.Looper;
import java.util.Set;
import android.util.ArrayMap;
import com.android.systemui.Dumpable;
import android.content.BroadcastReceiver;

public final class UserBroadcastDispatcher extends BroadcastReceiver implements Dumpable
{
    private final ArrayMap<String, Set<ReceiverData>> actionsToReceivers;
    private final UserBroadcastDispatcher$bgHandler.UserBroadcastDispatcher$bgHandler$1 bgHandler;
    private final Looper bgLooper;
    private final Context context;
    private final ArrayMap<BroadcastReceiver, Set<ReceiverData>> receiverToReceiverData;
    private final AtomicBoolean registered;
    private final int userId;
    
    static {
        new AtomicInteger(0);
    }
    
    public UserBroadcastDispatcher(final Context context, final int userId, final Looper bgLooper) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(bgLooper, "bgLooper");
        this.context = context;
        this.userId = userId;
        this.bgLooper = bgLooper;
        this.bgHandler = new UserBroadcastDispatcher$bgHandler.UserBroadcastDispatcher$bgHandler$1(this, this.bgLooper);
        this.registered = new AtomicBoolean(false);
        this.actionsToReceivers = (ArrayMap<String, Set<ReceiverData>>)new ArrayMap();
        this.receiverToReceiverData = (ArrayMap<BroadcastReceiver, Set<ReceiverData>>)new ArrayMap();
    }
    
    public static final /* synthetic */ UserBroadcastDispatcher$bgHandler.UserBroadcastDispatcher$bgHandler$1 access$getBgHandler$p(final UserBroadcastDispatcher userBroadcastDispatcher) {
        return userBroadcastDispatcher.bgHandler;
    }
    
    public static final /* synthetic */ Context access$getContext$p(final UserBroadcastDispatcher userBroadcastDispatcher) {
        return userBroadcastDispatcher.context;
    }
    
    public static final /* synthetic */ AtomicBoolean access$getRegistered$p(final UserBroadcastDispatcher userBroadcastDispatcher) {
        return userBroadcastDispatcher.registered;
    }
    
    public static final /* synthetic */ int access$getUserId$p(final UserBroadcastDispatcher userBroadcastDispatcher) {
        return userBroadcastDispatcher.userId;
    }
    
    private final IntentFilter createFilter() {
        final Looper looper = ((Handler)this.bgHandler).getLooper();
        Intrinsics.checkExpressionValueIsNotNull(looper, "bgHandler.looper");
        Preconditions.checkState(looper.isCurrentThread(), "This method should only be called from BG thread");
        final LinkedHashSet<Object> set = new LinkedHashSet<Object>();
        final Collection values = this.receiverToReceiverData.values();
        Intrinsics.checkExpressionValueIsNotNull(values, "receiverToReceiverData.values");
        final Iterator<Object> iterator = CollectionsKt.flatten((Iterable<? extends Iterable<?>>)values).iterator();
        while (iterator.hasNext()) {
            final Iterator categoriesIterator = iterator.next().getFilter().categoriesIterator();
            if (categoriesIterator != null) {
                final Sequence<? extends T> sequence = SequencesKt.asSequence((Iterator<? extends T>)categoriesIterator);
                if (sequence == null) {
                    continue;
                }
                CollectionsKt.addAll((Collection<? super Object>)set, (Sequence<?>)sequence);
            }
        }
        final IntentFilter intentFilter = new IntentFilter();
        final Set keySet = this.actionsToReceivers.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet, "actionsToReceivers.keys");
        for (final String s : keySet) {
            if (s != null) {
                intentFilter.addAction(s);
            }
        }
        final Iterator<String> iterator3 = set.iterator();
        while (iterator3.hasNext()) {
            intentFilter.addCategory((String)iterator3.next());
        }
        return intentFilter;
    }
    
    private final void createFilterAndRegisterReceiverBG() {
        ((Handler)this.bgHandler).post((Runnable)new RegisterReceiverRunnable(this.createFilter()));
    }
    
    private final void handleRegisterReceiver(final ReceiverData receiverData) {
        final Looper looper = ((Handler)this.bgHandler).getLooper();
        Intrinsics.checkExpressionValueIsNotNull(looper, "bgHandler.looper");
        Preconditions.checkState(looper.isCurrentThread(), "This method should only be called from BG thread");
        final ArrayMap<BroadcastReceiver, Set<ReceiverData>> receiverToReceiverData = this.receiverToReceiverData;
        final BroadcastReceiver receiver = receiverData.getReceiver();
        Object value;
        if ((value = ((Map<BroadcastReceiver, Set<ReceiverData>>)receiverToReceiverData).get(receiver)) == null) {
            value = new ArraySet();
            ((Map<BroadcastReceiver, Set<ReceiverData>>)receiverToReceiverData).put(receiver, (Set<ReceiverData>)value);
        }
        ((Set<ReceiverData>)value).add(receiverData);
        boolean b = false;
        final Iterator actionsIterator = receiverData.getFilter().actionsIterator();
        Intrinsics.checkExpressionValueIsNotNull(actionsIterator, "receiverData.filter.actionsIterator()");
        while (actionsIterator.hasNext()) {
            final String s = actionsIterator.next();
            final ArrayMap<String, Set<ReceiverData>> actionsToReceivers = this.actionsToReceivers;
            Object value2;
            if ((value2 = ((Map<String, Object>)actionsToReceivers).get(s)) == null) {
                b = true;
                value2 = new ArraySet();
                ((Map<String, Object>)actionsToReceivers).put(s, value2);
            }
            ((Set<ReceiverData>)value2).add(receiverData);
        }
        if (b) {
            this.createFilterAndRegisterReceiverBG();
        }
    }
    
    private final void handleUnregisterReceiver(final BroadcastReceiver broadcastReceiver) {
        final Looper looper = ((Handler)this.bgHandler).getLooper();
        Intrinsics.checkExpressionValueIsNotNull(looper, "bgHandler.looper");
        Preconditions.checkState(looper.isCurrentThread(), "This method should only be called from BG thread");
        final Iterable<ReceiverData> value = ((Map<K, Iterable<ReceiverData>>)this.receiverToReceiverData).get(broadcastReceiver);
        if (value != null) {
            Intrinsics.checkExpressionValueIsNotNull(value, "receiverToReceiverData.g\u2026Else(receiver) { return }");
            final Iterable<ReceiverData> iterable = value;
            final ArrayList<Object> list = new ArrayList<Object>();
            final Iterator<ReceiverData> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                final Iterator actionsIterator = iterator.next().getFilter().actionsIterator();
                Intrinsics.checkExpressionValueIsNotNull(actionsIterator, "it.filter.actionsIterator()");
                CollectionsKt.addAll((Collection<? super Object>)list, (Iterable<?>)SequencesKt.asIterable(SequencesKt.asSequence((Iterator<?>)actionsIterator)));
            }
            final Set<Object> set = CollectionsKt.toSet((Iterable<?>)list);
            final Set set2 = (Set)this.receiverToReceiverData.remove((Object)broadcastReceiver);
            if (set2 != null) {
                set2.clear();
            }
            final Ref$BooleanRef ref$BooleanRef = new Ref$BooleanRef();
            ref$BooleanRef.element = false;
            for (final String s : set) {
                final Set set3 = (Set)this.actionsToReceivers.get((Object)s);
                if (set3 != null) {
                    set3.removeIf((Predicate)new UserBroadcastDispatcher$handleUnregisterReceiver$$inlined$forEach$lambda.UserBroadcastDispatcher$handleUnregisterReceiver$$inlined$forEach$lambda$1(this, broadcastReceiver, ref$BooleanRef));
                }
                final Set set4 = (Set)this.actionsToReceivers.get((Object)s);
                if (set4 != null && set4.isEmpty()) {
                    ref$BooleanRef.element = true;
                    this.actionsToReceivers.remove((Object)s);
                }
            }
            if (ref$BooleanRef.element) {
                this.createFilterAndRegisterReceiverBG();
            }
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        final StringBuilder sb = new StringBuilder();
        sb.append("  Registered=");
        sb.append(this.registered.get());
        printWriter.println(sb.toString());
        for (final Map.Entry<String, Set<ReceiverData>> entry : ((Map<String, Set<ReceiverData>>)this.actionsToReceivers).entrySet()) {
            final String str = entry.getKey();
            final Set<ReceiverData> set = entry.getValue();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("    ");
            sb2.append(str);
            sb2.append(':');
            printWriter.println(sb2.toString());
            Intrinsics.checkExpressionValueIsNotNull(set, "list");
            for (final ReceiverData receiverData : set) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("      ");
                sb3.append(receiverData.getReceiver());
                printWriter.println(sb3.toString());
            }
        }
    }
    
    public final boolean isReceiverReferenceHeld$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        final boolean containsKey = ((Map)this.receiverToReceiverData).containsKey(broadcastReceiver);
        boolean b = true;
        if (!containsKey) {
            final ArrayMap<String, Set<ReceiverData>> actionsToReceivers = this.actionsToReceivers;
            boolean b3 = false;
            Label_0182: {
                if (!((Map)actionsToReceivers).isEmpty()) {
                    final Iterator<Map.Entry<String, Set<ReceiverData>>> iterator = ((Map<String, Set<ReceiverData>>)actionsToReceivers).entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Set<ReceiverData> value = iterator.next().getValue();
                        Intrinsics.checkExpressionValueIsNotNull(value, "it.value");
                        final Set<ReceiverData> set = value;
                        boolean b2 = false;
                        Label_0174: {
                            if (!(set instanceof Collection) || !set.isEmpty()) {
                                final Iterator<Object> iterator2 = set.iterator();
                                while (iterator2.hasNext()) {
                                    if (Intrinsics.areEqual(iterator2.next().getReceiver(), broadcastReceiver)) {
                                        b2 = true;
                                        break Label_0174;
                                    }
                                }
                            }
                            b2 = false;
                        }
                        if (b2) {
                            b3 = true;
                            break Label_0182;
                        }
                    }
                }
                b3 = false;
            }
            b = (b3 && b);
        }
        return b;
    }
    
    public void onReceive(final Context context, final Intent intent) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        final UserBroadcastDispatcher$bgHandler.UserBroadcastDispatcher$bgHandler$1 bgHandler = this.bgHandler;
        final ArrayMap<String, Set<ReceiverData>> actionsToReceivers = this.actionsToReceivers;
        final BroadcastReceiver$PendingResult pendingResult = this.getPendingResult();
        Intrinsics.checkExpressionValueIsNotNull(pendingResult, "pendingResult");
        ((Handler)bgHandler).post((Runnable)new HandleBroadcastRunnable((Map<String, ? extends Set<ReceiverData>>)actionsToReceivers, context, intent, pendingResult, 0));
    }
    
    public final void registerReceiver(final ReceiverData receiverData) {
        Intrinsics.checkParameterIsNotNull(receiverData, "receiverData");
        ((Handler)this.bgHandler).obtainMessage(0, (Object)receiverData).sendToTarget();
    }
    
    public final void unregisterReceiver(final BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        ((Handler)this.bgHandler).obtainMessage(1, (Object)broadcastReceiver).sendToTarget();
    }
    
    private static final class HandleBroadcastRunnable implements Runnable
    {
        private final Map<String, Set<ReceiverData>> actionsToReceivers;
        private final Context context;
        private final Intent intent;
        private final BroadcastReceiver$PendingResult pendingResult;
        
        public HandleBroadcastRunnable(final Map<String, ? extends Set<ReceiverData>> actionsToReceivers, final Context context, final Intent intent, final BroadcastReceiver$PendingResult pendingResult, final int n) {
            Intrinsics.checkParameterIsNotNull(actionsToReceivers, "actionsToReceivers");
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(intent, "intent");
            Intrinsics.checkParameterIsNotNull(pendingResult, "pendingResult");
            this.actionsToReceivers = (Map<String, Set<ReceiverData>>)actionsToReceivers;
            this.context = context;
            this.intent = intent;
            this.pendingResult = pendingResult;
        }
        
        public final Context getContext() {
            return this.context;
        }
        
        public final Intent getIntent() {
            return this.intent;
        }
        
        public final BroadcastReceiver$PendingResult getPendingResult() {
            return this.pendingResult;
        }
        
        @Override
        public void run() {
            final Set<ReceiverData> set = this.actionsToReceivers.get(this.intent.getAction());
            if (set != null) {
                final ArrayList<ReceiverData> list = new ArrayList<ReceiverData>();
                for (final ReceiverData next : set) {
                    final ReceiverData receiverData = next;
                    if (receiverData.getFilter().hasAction(this.intent.getAction()) && receiverData.getFilter().matchCategories(this.intent.getCategories()) == null) {
                        list.add(next);
                    }
                }
                for (final ReceiverData receiverData2 : list) {
                    receiverData2.getExecutor().execute((Runnable)new UserBroadcastDispatcher$HandleBroadcastRunnable$run$$inlined$forEach$lambda.UserBroadcastDispatcher$HandleBroadcastRunnable$run$$inlined$forEach$lambda$1(receiverData2, this));
                }
            }
        }
    }
    
    private final class RegisterReceiverRunnable implements Runnable
    {
        private final IntentFilter intentFilter;
        
        public RegisterReceiverRunnable(final IntentFilter intentFilter) {
            Intrinsics.checkParameterIsNotNull(intentFilter, "intentFilter");
            this.intentFilter = intentFilter;
        }
        
        @Override
        public void run() {
            if (UserBroadcastDispatcher.access$getRegistered$p(UserBroadcastDispatcher.this).get()) {
                UserBroadcastDispatcher.access$getContext$p(UserBroadcastDispatcher.this).unregisterReceiver((BroadcastReceiver)UserBroadcastDispatcher.this);
                UserBroadcastDispatcher.access$getRegistered$p(UserBroadcastDispatcher.this).set(false);
            }
            if (this.intentFilter.countActions() > 0 && !UserBroadcastDispatcher.access$getRegistered$p(UserBroadcastDispatcher.this).get()) {
                final Context access$getContext$p = UserBroadcastDispatcher.access$getContext$p(UserBroadcastDispatcher.this);
                final UserBroadcastDispatcher this$0 = UserBroadcastDispatcher.this;
                access$getContext$p.registerReceiverAsUser((BroadcastReceiver)this$0, UserHandle.of(UserBroadcastDispatcher.access$getUserId$p(this$0)), this.intentFilter, (String)null, (Handler)UserBroadcastDispatcher.access$getBgHandler$p(UserBroadcastDispatcher.this));
                UserBroadcastDispatcher.access$getRegistered$p(UserBroadcastDispatcher.this).set(true);
            }
        }
    }
}
