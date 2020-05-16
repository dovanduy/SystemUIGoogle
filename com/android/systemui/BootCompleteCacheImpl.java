// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.function.Predicate;
import java.util.Iterator;
import kotlin.Unit;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.dump.DumpManager;
import com.android.internal.annotations.GuardedBy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BootCompleteCacheImpl implements BootCompleteCache, Dumpable
{
    private final AtomicBoolean bootComplete;
    @GuardedBy({ "listeners" })
    private final List<WeakReference<BootCompleteListener>> listeners;
    
    public BootCompleteCacheImpl(final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        dumpManager.registerDumpable("BootCompleteCacheImpl", this);
        this.listeners = new ArrayList<WeakReference<BootCompleteListener>>();
        this.bootComplete = new AtomicBoolean(false);
    }
    
    @Override
    public boolean addListener(final BootCompleteListener referent) {
        Intrinsics.checkParameterIsNotNull(referent, "listener");
        if (this.bootComplete.get()) {
            return true;
        }
        synchronized (this.listeners) {
            if (this.bootComplete.get()) {
                return true;
            }
            this.listeners.add(new WeakReference<BootCompleteListener>(referent));
            return false;
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        printWriter.println("BootCompleteCache state:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  boot complete: ");
        sb.append(this.isBootComplete());
        printWriter.println(sb.toString());
        if (!this.isBootComplete()) {
            printWriter.println("  listeners:");
            synchronized (this.listeners) {
                for (final WeakReference obj : this.listeners) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("    ");
                    sb2.append(obj);
                    printWriter.println(sb2.toString());
                }
                final Unit instance = Unit.INSTANCE;
            }
        }
    }
    
    @Override
    public boolean isBootComplete() {
        return this.bootComplete.get();
    }
    
    @Override
    public void removeListener(final BootCompleteListener bootCompleteListener) {
        Intrinsics.checkParameterIsNotNull(bootCompleteListener, "listener");
        if (this.bootComplete.get()) {
            return;
        }
        synchronized (this.listeners) {
            this.listeners.removeIf((Predicate<? super Object>)new BootCompleteCacheImpl$removeListener$$inlined$synchronized$lambda.BootCompleteCacheImpl$removeListener$$inlined$synchronized$lambda$1(this, bootCompleteListener));
            final Unit instance = Unit.INSTANCE;
        }
    }
    
    public final void setBootComplete() {
        if (this.bootComplete.compareAndSet(false, true)) {
            synchronized (this.listeners) {
                final Iterator<WeakReference<BootCompleteListener>> iterator = this.listeners.iterator();
                while (iterator.hasNext()) {
                    final BootCompleteListener bootCompleteListener = iterator.next().get();
                    if (bootCompleteListener != null) {
                        bootCompleteListener.onBootComplete();
                    }
                }
                this.listeners.clear();
                final Unit instance = Unit.INSTANCE;
            }
        }
    }
}
