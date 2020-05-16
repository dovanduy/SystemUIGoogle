// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.security.PrivilegedActionException;
import com.google.common.base.Throwables;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.internal.InternalFutures;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Logger;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;

public abstract class AbstractFuture<V> extends InternalFutureFailureAccess implements ListenableFuture<V>
{
    private static final AtomicHelper ATOMIC_HELPER;
    private static final boolean GENERATE_CANCELLATION_CAUSES;
    private static final Object NULL;
    private static final Logger log;
    private volatile Listener listeners;
    private volatile Object value;
    private volatile Waiter waiters;
    
    static {
        GENERATE_CANCELLATION_CAUSES = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
        log = Logger.getLogger(AbstractFuture.class.getName());
        SynchronizedHelper atomic_HELPER = null;
        try {
            final UnsafeAtomicHelper unsafeAtomicHelper = new UnsafeAtomicHelper();
        }
        finally {
            try {
                final SafeAtomicHelper safeAtomicHelper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Thread.class, "thread"), AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Waiter.class, "next"), (AtomicReferenceFieldUpdater<AbstractFuture, Waiter>)AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Waiter.class, "waiters"), (AtomicReferenceFieldUpdater<AbstractFuture, Listener>)AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Listener.class, "listeners"), (AtomicReferenceFieldUpdater<AbstractFuture, Object>)AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value"));
            }
            finally {
                atomic_HELPER = new SynchronizedHelper();
            }
        }
        ATOMIC_HELPER = (AtomicHelper)atomic_HELPER;
        final Throwable thrown;
        if (thrown != null) {
            final Throwable thrown2;
            AbstractFuture.log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", thrown2);
            AbstractFuture.log.log(Level.SEVERE, "SafeAtomicHelper is broken!", thrown);
        }
        NULL = new Object();
    }
    
    protected AbstractFuture() {
    }
    
    private void addDoneString(final StringBuilder sb) {
        try {
            final Object uninterruptibly = getUninterruptibly((Future<Object>)this);
            sb.append("SUCCESS, result=[");
            sb.append(this.userObjectToString(uninterruptibly));
            sb.append("]");
        }
        catch (RuntimeException ex) {
            sb.append("UNKNOWN, cause=[");
            sb.append(ex.getClass());
            sb.append(" thrown from get()]");
        }
        catch (CancellationException ex3) {
            sb.append("CANCELLED");
        }
        catch (ExecutionException ex2) {
            sb.append("FAILURE, cause=[");
            sb.append(ex2.getCause());
            sb.append("]");
        }
    }
    
    private static CancellationException cancellationExceptionWithCause(final String message, final Throwable cause) {
        final CancellationException ex = new CancellationException(message);
        ex.initCause(cause);
        return ex;
    }
    
    private Listener clearListeners(Listener next) {
        Listener listeners;
        do {
            listeners = this.listeners;
        } while (!AbstractFuture.ATOMIC_HELPER.casListeners(this, listeners, Listener.TOMBSTONE));
        Listener listener = next;
        next = listeners;
        Listener next2;
        while (true) {
            next2 = listener;
            listener = next;
            if (listener == null) {
                break;
            }
            next = listener.next;
            listener.next = next2;
        }
        return next2;
    }
    
    private static void complete(AbstractFuture<?> abstractFuture) {
        Listener listener = null;
    Label_0002:
        while (true) {
            abstractFuture.releaseWaiters();
            abstractFuture.afterDone();
            Listener next;
            for (Listener clearListeners = abstractFuture.clearListeners(listener); clearListeners != null; clearListeners = next) {
                next = clearListeners.next;
                final Runnable task = clearListeners.task;
                if (task instanceof SetFuture) {
                    final SetFuture setFuture = (SetFuture)task;
                    final AbstractFuture<V> owner = setFuture.owner;
                    if (owner.value == setFuture && AbstractFuture.ATOMIC_HELPER.casValue(owner, setFuture, getFutureValue(setFuture.future))) {
                        listener = next;
                        abstractFuture = owner;
                        continue Label_0002;
                    }
                }
                else {
                    executeListener(task, clearListeners.executor);
                }
            }
            break;
        }
    }
    
    private static void executeListener(final Runnable obj, final Executor obj2) {
        try {
            obj2.execute(obj);
        }
        catch (RuntimeException thrown) {
            final Logger log = AbstractFuture.log;
            final Level severe = Level.SEVERE;
            final StringBuilder sb = new StringBuilder();
            sb.append("RuntimeException while executing runnable ");
            sb.append(obj);
            sb.append(" with executor ");
            sb.append(obj2);
            log.log(severe, sb.toString(), thrown);
        }
    }
    
    private V getDoneValue(final Object o) throws ExecutionException {
        if (o instanceof Cancellation) {
            throw cancellationExceptionWithCause("Task was cancelled.", ((Cancellation)o).cause);
        }
        if (o instanceof Failure) {
            throw new ExecutionException(((Failure)o).exception);
        }
        if (o == AbstractFuture.NULL) {
            return null;
        }
        return (V)o;
    }
    
    private static Object getFutureValue(final ListenableFuture<?> obj) {
        if (obj instanceof Trusted) {
            Object o2;
            final Object o = o2 = ((AbstractFuture)obj).value;
            if (o instanceof Cancellation) {
                final Cancellation cancellation = (Cancellation)o;
                o2 = o;
                if (cancellation.wasInterrupted) {
                    if (cancellation.cause != null) {
                        o2 = new Cancellation(false, cancellation.cause);
                    }
                    else {
                        o2 = Cancellation.CAUSELESS_CANCELLED;
                    }
                }
            }
            return o2;
        }
        if (obj instanceof InternalFutureFailureAccess) {
            final Throwable tryInternalFastPathGetFailure = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)obj);
            if (tryInternalFastPathGetFailure != null) {
                return new Failure(tryInternalFastPathGetFailure);
            }
        }
        final boolean cancelled = obj.isCancelled();
        if ((AbstractFuture.GENERATE_CANCELLATION_CAUSES ^ true) & cancelled) {
            return Cancellation.CAUSELESS_CANCELLED;
        }
        try {
            final Object uninterruptibly = getUninterruptibly(obj);
            if (cancelled) {
                final StringBuilder sb = new StringBuilder();
                sb.append("get() did not throw CancellationException, despite reporting isCancelled() == true: ");
                sb.append(obj);
                return new Cancellation(false, new IllegalArgumentException(sb.toString()));
            }
            Object null;
            if ((null = uninterruptibly) == null) {
                null = AbstractFuture.NULL;
            }
            return null;
        }
        catch (CancellationException cause) {
            if (!cancelled) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("get() threw CancellationException, despite reporting isCancelled() == false: ");
                sb2.append(obj);
                return new Failure(new IllegalArgumentException(sb2.toString(), cause));
            }
            return new Cancellation(false, cause);
        }
        catch (ExecutionException cause2) {
            if (cancelled) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("get() did not throw CancellationException, despite reporting isCancelled() == true: ");
                sb3.append(obj);
                return new Cancellation(false, new IllegalArgumentException(sb3.toString(), cause2));
            }
            return new Failure(cause2.getCause());
        }
        finally {
            final Throwable t;
            return new Failure(t);
        }
    }
    
    private static <V> V getUninterruptibly(final Future<V> future) throws ExecutionException {
        boolean b = false;
        try {
            return future.get();
        }
        catch (InterruptedException ex) {
            b = true;
            return future.get();
        }
        finally {
            if (b) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void releaseWaiters() {
        Waiter waiter;
        do {
            waiter = this.waiters;
        } while (!AbstractFuture.ATOMIC_HELPER.casWaiters(this, waiter, Waiter.TOMBSTONE));
        while (waiter != null) {
            waiter.unpark();
            waiter = waiter.next;
        }
    }
    
    private void removeWaiter(Waiter waiters) {
        waiters.thread = null;
    Label_0005:
        while (true) {
            waiters = this.waiters;
            if (waiters == Waiter.TOMBSTONE) {
                return;
            }
            Waiter waiter = null;
            while (waiters != null) {
                final Waiter next = waiters.next;
                Waiter waiter2;
                if (waiters.thread != null) {
                    waiter2 = waiters;
                }
                else if (waiter != null) {
                    waiter.next = next;
                    waiter2 = waiter;
                    if (waiter.thread == null) {
                        continue Label_0005;
                    }
                }
                else {
                    waiter2 = waiter;
                    if (!AbstractFuture.ATOMIC_HELPER.casWaiters(this, waiters, next)) {
                        continue Label_0005;
                    }
                }
                waiters = next;
                waiter = waiter2;
            }
        }
    }
    
    private String userObjectToString(final Object obj) {
        if (obj == this) {
            return "this future";
        }
        return String.valueOf(obj);
    }
    
    @Override
    public void addListener(final Runnable runnable, final Executor executor) {
        Preconditions.checkNotNull(runnable, "Runnable was null.");
        Preconditions.checkNotNull(executor, "Executor was null.");
        if (!this.isDone()) {
            Listener next = this.listeners;
            if (next != Listener.TOMBSTONE) {
                final Listener listener = new Listener(runnable, executor);
                do {
                    listener.next = next;
                    if (AbstractFuture.ATOMIC_HELPER.casListeners(this, next, listener)) {
                        return;
                    }
                } while ((next = this.listeners) != Listener.TOMBSTONE);
            }
        }
        executeListener(runnable, executor);
    }
    
    @ForOverride
    protected void afterDone() {
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean cancel(final boolean b) {
        Object o = this.value;
        final boolean b2 = true;
        boolean b3;
        if (o == null | o instanceof SetFuture) {
            Cancellation cancellation;
            if (AbstractFuture.GENERATE_CANCELLATION_CAUSES) {
                cancellation = new Cancellation(b, new CancellationException("Future.cancel() was called."));
            }
            else if (b) {
                cancellation = Cancellation.CAUSELESS_INTERRUPTED;
            }
            else {
                cancellation = Cancellation.CAUSELESS_CANCELLED;
            }
            b3 = false;
            AbstractFuture<? extends V> abstractFuture = (AbstractFuture<? extends V>)this;
            while (true) {
                if (AbstractFuture.ATOMIC_HELPER.casValue(abstractFuture, o, cancellation)) {
                    if (b) {
                        abstractFuture.interruptTask();
                    }
                    complete(abstractFuture);
                    b3 = b2;
                    if (!(o instanceof SetFuture)) {
                        break;
                    }
                    final ListenableFuture<? extends V> future = ((SetFuture)o).future;
                    if (!(future instanceof Trusted)) {
                        future.cancel(b);
                        b3 = b2;
                        break;
                    }
                    abstractFuture = (AbstractFuture<? extends V>)future;
                    o = abstractFuture.value;
                    final boolean b4 = o == null;
                    b3 = b2;
                    if (!(b4 | o instanceof SetFuture)) {
                        break;
                    }
                    b3 = true;
                }
                else {
                    if (!((o = abstractFuture.value) instanceof SetFuture)) {
                        break;
                    }
                    continue;
                }
            }
        }
        else {
            b3 = false;
        }
        return b3;
    }
    
    @CanIgnoreReturnValue
    @Override
    public V get() throws InterruptedException, ExecutionException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        final Object value = this.value;
        if (value != null & (value instanceof SetFuture ^ true)) {
            return this.getDoneValue(value);
        }
        Waiter next = this.waiters;
        if (next != Waiter.TOMBSTONE) {
            final Waiter waiter = new Waiter();
            do {
                waiter.setNext(next);
                if (AbstractFuture.ATOMIC_HELPER.casWaiters(this, next, waiter)) {
                    Object value2;
                    do {
                        LockSupport.park(this);
                        if (Thread.interrupted()) {
                            this.removeWaiter(waiter);
                            throw new InterruptedException();
                        }
                        value2 = this.value;
                    } while (!(value2 != null & (value2 instanceof SetFuture ^ true)));
                    return this.getDoneValue(value2);
                }
            } while ((next = this.waiters) != Waiter.TOMBSTONE);
        }
        return this.getDoneValue(this.value);
    }
    
    @CanIgnoreReturnValue
    @Override
    public V get(long convert, final TimeUnit timeUnit) throws InterruptedException, TimeoutException, ExecutionException {
        long nanos = timeUnit.toNanos(convert);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        final Object value = this.value;
        if (value != null & (value instanceof SetFuture ^ true)) {
            return this.getDoneValue(value);
        }
        long n;
        if (nanos > 0L) {
            n = System.nanoTime() + nanos;
        }
        else {
            n = 0L;
        }
        long n2 = nanos;
        Label_0254: {
            if (nanos >= 1000L) {
                Waiter next = this.waiters;
                if (next != Waiter.TOMBSTONE) {
                    final Waiter waiter = new Waiter();
                    do {
                        waiter.setNext(next);
                        if (AbstractFuture.ATOMIC_HELPER.casWaiters(this, next, waiter)) {
                            do {
                                LockSupport.parkNanos(this, nanos);
                                if (Thread.interrupted()) {
                                    this.removeWaiter(waiter);
                                    throw new InterruptedException();
                                }
                                final Object value2 = this.value;
                                if (value2 != null & (value2 instanceof SetFuture ^ true)) {
                                    return this.getDoneValue(value2);
                                }
                                n2 = (nanos = n - System.nanoTime());
                            } while (n2 >= 1000L);
                            this.removeWaiter(waiter);
                            break Label_0254;
                        }
                    } while ((next = this.waiters) != Waiter.TOMBSTONE);
                }
                return this.getDoneValue(this.value);
            }
        }
        while (n2 > 0L) {
            final Object value3 = this.value;
            if (value3 != null & (value3 instanceof SetFuture ^ true)) {
                return this.getDoneValue(value3);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            n2 = n - System.nanoTime();
        }
        final String string = this.toString();
        final String lowerCase = timeUnit.toString().toLowerCase(Locale.ROOT);
        final StringBuilder sb = new StringBuilder();
        sb.append("Waited ");
        sb.append(convert);
        sb.append(" ");
        sb.append(timeUnit.toString().toLowerCase(Locale.ROOT));
        String s;
        final String str = s = sb.toString();
        if (n2 + 1000L < 0L) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(" (plus ");
            final String string2 = sb2.toString();
            final long sourceDuration = -n2;
            convert = timeUnit.convert(sourceDuration, TimeUnit.NANOSECONDS);
            final long lng = sourceDuration - timeUnit.toNanos(convert);
            final long n3 = lcmp(convert, 0L);
            final boolean b = n3 == 0 || lng > 1000L;
            String string3 = string2;
            if (n3 > 0) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(string2);
                sb3.append(convert);
                sb3.append(" ");
                sb3.append(lowerCase);
                String s2 = sb3.toString();
                if (b) {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append(s2);
                    sb4.append(",");
                    s2 = sb4.toString();
                }
                final StringBuilder sb5 = new StringBuilder();
                sb5.append(s2);
                sb5.append(" ");
                string3 = sb5.toString();
            }
            String string4 = string3;
            if (b) {
                final StringBuilder sb6 = new StringBuilder();
                sb6.append(string3);
                sb6.append(lng);
                sb6.append(" nanoseconds ");
                string4 = sb6.toString();
            }
            final StringBuilder sb7 = new StringBuilder();
            sb7.append(string4);
            sb7.append("delay)");
            s = sb7.toString();
        }
        if (this.isDone()) {
            final StringBuilder sb8 = new StringBuilder();
            sb8.append(s);
            sb8.append(" but future completed as timeout expired");
            throw new TimeoutException(sb8.toString());
        }
        final StringBuilder sb9 = new StringBuilder();
        sb9.append(s);
        sb9.append(" for ");
        sb9.append(string);
        throw new TimeoutException(sb9.toString());
    }
    
    protected void interruptTask() {
    }
    
    @Override
    public boolean isCancelled() {
        return this.value instanceof Cancellation;
    }
    
    @Override
    public boolean isDone() {
        final Object value = this.value;
        return (value instanceof SetFuture ^ true) & value != null;
    }
    
    final void maybePropagateCancellationTo(final Future<?> future) {
        if (future != null & this.isCancelled()) {
            future.cancel(this.wasInterrupted());
        }
    }
    
    protected String pendingToString() {
        final Object value = this.value;
        if (value instanceof SetFuture) {
            final StringBuilder sb = new StringBuilder();
            sb.append("setFuture=[");
            sb.append(this.userObjectToString(((SetFuture)value).future));
            sb.append("]");
            return sb.toString();
        }
        if (this instanceof ScheduledFuture) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("remaining delay=[");
            sb2.append(((ScheduledFuture)this).getDelay(TimeUnit.MILLISECONDS));
            sb2.append(" ms]");
            return sb2.toString();
        }
        return null;
    }
    
    @CanIgnoreReturnValue
    protected boolean set(final V v) {
        Object null = v;
        if (v == null) {
            null = AbstractFuture.NULL;
        }
        if (AbstractFuture.ATOMIC_HELPER.casValue(this, null, null)) {
            complete(this);
            return true;
        }
        return false;
    }
    
    @CanIgnoreReturnValue
    protected boolean setException(final Throwable t) {
        Preconditions.checkNotNull(t);
        if (AbstractFuture.ATOMIC_HELPER.casValue(this, null, new Failure(t))) {
            complete(this);
            return true;
        }
        return false;
    }
    
    @CanIgnoreReturnValue
    protected boolean setFuture(final ListenableFuture<? extends V> listenableFuture) {
        Preconditions.checkNotNull(listenableFuture);
        Object o;
        if ((o = this.value) == null) {
            if (listenableFuture.isDone()) {
                if (AbstractFuture.ATOMIC_HELPER.casValue(this, null, getFutureValue(listenableFuture))) {
                    complete(this);
                    return true;
                }
                return false;
            }
            else {
                final SetFuture setFuture = new SetFuture((AbstractFuture<V>)this, (ListenableFuture<? extends V>)listenableFuture);
                if (AbstractFuture.ATOMIC_HELPER.casValue(this, null, setFuture)) {
                    try {
                        listenableFuture.addListener(setFuture, DirectExecutor.INSTANCE);
                    }
                    finally {
                        Failure fallback_INSTANCE = null;
                        try {
                            final Throwable t;
                            final Failure failure = new Failure(t);
                        }
                        finally {
                            fallback_INSTANCE = Failure.FALLBACK_INSTANCE;
                        }
                        AbstractFuture.ATOMIC_HELPER.casValue(this, setFuture, fallback_INSTANCE);
                    }
                    return true;
                }
                o = this.value;
            }
        }
        if (o instanceof Cancellation) {
            listenableFuture.cancel(((Cancellation)o).wasInterrupted);
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("[status=");
        if (this.isCancelled()) {
            sb.append("CANCELLED");
        }
        else if (this.isDone()) {
            this.addDoneString(sb);
        }
        else {
            String str;
            try {
                str = this.pendingToString();
            }
            catch (RuntimeException ex) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Exception thrown from implementation: ");
                sb2.append(ex.getClass());
                str = sb2.toString();
            }
            if (str != null && !str.isEmpty()) {
                sb.append("PENDING, info=[");
                sb.append(str);
                sb.append("]");
            }
            else if (this.isDone()) {
                this.addDoneString(sb);
            }
            else {
                sb.append("PENDING");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    protected final Throwable tryInternalFastPathGetFailure() {
        if (this instanceof Trusted) {
            final Object value = this.value;
            if (value instanceof Failure) {
                return ((Failure)value).exception;
            }
        }
        return null;
    }
    
    protected final boolean wasInterrupted() {
        final Object value = this.value;
        return value instanceof Cancellation && ((Cancellation)value).wasInterrupted;
    }
    
    private abstract static class AtomicHelper
    {
        abstract boolean casListeners(final AbstractFuture<?> p0, final Listener p1, final Listener p2);
        
        abstract boolean casValue(final AbstractFuture<?> p0, final Object p1, final Object p2);
        
        abstract boolean casWaiters(final AbstractFuture<?> p0, final Waiter p1, final Waiter p2);
        
        abstract void putNext(final Waiter p0, final Waiter p1);
        
        abstract void putThread(final Waiter p0, final Thread p1);
    }
    
    private static final class Cancellation
    {
        static final Cancellation CAUSELESS_CANCELLED;
        static final Cancellation CAUSELESS_INTERRUPTED;
        final Throwable cause;
        final boolean wasInterrupted;
        
        static {
            if (AbstractFuture.GENERATE_CANCELLATION_CAUSES) {
                CAUSELESS_CANCELLED = null;
                CAUSELESS_INTERRUPTED = null;
            }
            else {
                CAUSELESS_CANCELLED = new Cancellation(false, null);
                CAUSELESS_INTERRUPTED = new Cancellation(true, null);
            }
        }
        
        Cancellation(final boolean wasInterrupted, final Throwable cause) {
            this.wasInterrupted = wasInterrupted;
            this.cause = cause;
        }
    }
    
    private static final class Failure
    {
        static final Failure FALLBACK_INSTANCE;
        final Throwable exception;
        
        static {
            FALLBACK_INSTANCE = new Failure(new Throwable() {
                @Override
                public Throwable fillInStackTrace() {
                    // monitorenter(this)
                    // monitorexit(this)
                    return this;
                }
            });
        }
        
        Failure(final Throwable t) {
            Preconditions.checkNotNull(t);
            this.exception = t;
        }
    }
    
    private static final class Listener
    {
        static final Listener TOMBSTONE;
        final Executor executor;
        Listener next;
        final Runnable task;
        
        static {
            TOMBSTONE = new Listener(null, null);
        }
        
        Listener(final Runnable task, final Executor executor) {
            this.task = task;
            this.executor = executor;
        }
    }
    
    private static final class SafeAtomicHelper extends AtomicHelper
    {
        final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater;
        
        SafeAtomicHelper(final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater, final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater, final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater, final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater, final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater) {
            this.waiterThreadUpdater = waiterThreadUpdater;
            this.waiterNextUpdater = waiterNextUpdater;
            this.waitersUpdater = waitersUpdater;
            this.listenersUpdater = listenersUpdater;
            this.valueUpdater = valueUpdater;
        }
        
        @Override
        boolean casListeners(final AbstractFuture<?> abstractFuture, final Listener listener, final Listener listener2) {
            return this.listenersUpdater.compareAndSet(abstractFuture, listener, listener2);
        }
        
        @Override
        boolean casValue(final AbstractFuture<?> abstractFuture, final Object o, final Object o2) {
            return this.valueUpdater.compareAndSet(abstractFuture, o, o2);
        }
        
        @Override
        boolean casWaiters(final AbstractFuture<?> abstractFuture, final Waiter waiter, final Waiter waiter2) {
            return this.waitersUpdater.compareAndSet(abstractFuture, waiter, waiter2);
        }
        
        @Override
        void putNext(final Waiter waiter, final Waiter waiter2) {
            this.waiterNextUpdater.lazySet(waiter, waiter2);
        }
        
        @Override
        void putThread(final Waiter waiter, final Thread thread) {
            this.waiterThreadUpdater.lazySet(waiter, thread);
        }
    }
    
    private static final class SetFuture<V> implements Runnable
    {
        final ListenableFuture<? extends V> future;
        final AbstractFuture<V> owner;
        
        SetFuture(final AbstractFuture<V> owner, final ListenableFuture<? extends V> future) {
            this.owner = owner;
            this.future = future;
        }
        
        @Override
        public void run() {
            if (((AbstractFuture<Object>)this.owner).value != this) {
                return;
            }
            if (AbstractFuture.ATOMIC_HELPER.casValue(this.owner, this, getFutureValue(this.future))) {
                complete(this.owner);
            }
        }
    }
    
    private static final class SynchronizedHelper extends AtomicHelper
    {
        @Override
        boolean casListeners(final AbstractFuture<?> abstractFuture, final Listener listener, final Listener listener2) {
            synchronized (abstractFuture) {
                if (((AbstractFuture<Object>)abstractFuture).listeners == listener) {
                    ((AbstractFuture<Object>)abstractFuture).listeners = listener2;
                    return true;
                }
                return false;
            }
        }
        
        @Override
        boolean casValue(final AbstractFuture<?> abstractFuture, final Object o, final Object o2) {
            synchronized (abstractFuture) {
                if (((AbstractFuture<Object>)abstractFuture).value == o) {
                    ((AbstractFuture<Object>)abstractFuture).value = o2;
                    return true;
                }
                return false;
            }
        }
        
        @Override
        boolean casWaiters(final AbstractFuture<?> abstractFuture, final Waiter waiter, final Waiter waiter2) {
            synchronized (abstractFuture) {
                if (((AbstractFuture<Object>)abstractFuture).waiters == waiter) {
                    ((AbstractFuture<Object>)abstractFuture).waiters = waiter2;
                    return true;
                }
                return false;
            }
        }
        
        @Override
        void putNext(final Waiter waiter, final Waiter next) {
            waiter.next = next;
        }
        
        @Override
        void putThread(final Waiter waiter, final Thread thread) {
            waiter.thread = thread;
        }
    }
    
    interface Trusted<V> extends ListenableFuture<V>
    {
    }
    
    abstract static class TrustedFuture<V> extends AbstractFuture<V> implements Trusted<V>
    {
        @Override
        public final void addListener(final Runnable runnable, final Executor executor) {
            super.addListener(runnable, executor);
        }
        
        @CanIgnoreReturnValue
        @Override
        public final boolean cancel(final boolean b) {
            return super.cancel(b);
        }
        
        @CanIgnoreReturnValue
        @Override
        public final V get() throws InterruptedException, ExecutionException {
            return super.get();
        }
        
        @CanIgnoreReturnValue
        @Override
        public final V get(final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(n, timeUnit);
        }
        
        @Override
        public final boolean isCancelled() {
            return super.isCancelled();
        }
        
        @Override
        public boolean isDone() {
            return super.isDone();
        }
    }
    
    private static final class UnsafeAtomicHelper extends AtomicHelper
    {
        static final long LISTENERS_OFFSET;
        static final Unsafe UNSAFE;
        static final long VALUE_OFFSET;
        static final long WAITERS_OFFSET;
        static final long WAITER_NEXT_OFFSET;
        static final long WAITER_THREAD_OFFSET;
        
        static {
            Label_0024: {
                try {
                    final Unsafe unsafe = Unsafe.getUnsafe();
                    break Label_0024;
                }
                catch (SecurityException ex2) {
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction = new(com.google.common.util.concurrent.AbstractFuture.UnsafeAtomicHelper.AbstractFuture$UnsafeAtomicHelper$1.class);
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction3;
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction2 = privilegedExceptionAction3 = privilegedExceptionAction;
                    new PrivilegedExceptionAction<Unsafe>() {
                        @Override
                        public Unsafe run() throws Exception {
                            for (final Field field : Unsafe.class.getDeclaredFields()) {
                                field.setAccessible(true);
                                final Object value = field.get(null);
                                if (Unsafe.class.isInstance(value)) {
                                    return Unsafe.class.cast(value);
                                }
                            }
                            throw new NoSuchFieldError("the Unsafe");
                        }
                    };
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction4 = privilegedExceptionAction2;
                    final Unsafe unsafe2 = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>)privilegedExceptionAction4);
                    final Unsafe unsafe;
                    final Unsafe unsafe3 = unsafe = unsafe2;
                }
                try {
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction = new(com.google.common.util.concurrent.AbstractFuture.UnsafeAtomicHelper.AbstractFuture$UnsafeAtomicHelper$1.class);
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction3;
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction2 = privilegedExceptionAction3 = privilegedExceptionAction;
                    new PrivilegedExceptionAction<Unsafe>() {
                        @Override
                        public Unsafe run() throws Exception {
                            for (final Field field : Unsafe.class.getDeclaredFields()) {
                                field.setAccessible(true);
                                final Object value = field.get(null);
                                if (Unsafe.class.isInstance(value)) {
                                    return Unsafe.class.cast(value);
                                }
                            }
                            throw new NoSuchFieldError("the Unsafe");
                        }
                    };
                    final PrivilegedExceptionAction<Unsafe> privilegedExceptionAction4 = privilegedExceptionAction2;
                    final Unsafe unsafe2 = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>)privilegedExceptionAction4);
                    final Unsafe unsafe = unsafe2;
                    try {
                        WAITERS_OFFSET = unsafe.objectFieldOffset(AbstractFuture.class.getDeclaredField("waiters"));
                        LISTENERS_OFFSET = unsafe.objectFieldOffset(AbstractFuture.class.getDeclaredField("listeners"));
                        VALUE_OFFSET = unsafe.objectFieldOffset(AbstractFuture.class.getDeclaredField("value"));
                        WAITER_THREAD_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("thread"));
                        WAITER_NEXT_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("next"));
                        UNSAFE = unsafe;
                    }
                    catch (Exception cause) {
                        Throwables.throwIfUnchecked(cause);
                        throw new RuntimeException(cause);
                    }
                }
                catch (PrivilegedActionException ex) {
                    throw new RuntimeException("Could not initialize intrinsics", ex.getCause());
                }
            }
        }
        
        @Override
        boolean casListeners(final AbstractFuture<?> o, final Listener expected, final Listener x) {
            return UnsafeAtomicHelper.UNSAFE.compareAndSwapObject(o, UnsafeAtomicHelper.LISTENERS_OFFSET, expected, x);
        }
        
        @Override
        boolean casValue(final AbstractFuture<?> o, final Object expected, final Object x) {
            return UnsafeAtomicHelper.UNSAFE.compareAndSwapObject(o, UnsafeAtomicHelper.VALUE_OFFSET, expected, x);
        }
        
        @Override
        boolean casWaiters(final AbstractFuture<?> o, final Waiter expected, final Waiter x) {
            return UnsafeAtomicHelper.UNSAFE.compareAndSwapObject(o, UnsafeAtomicHelper.WAITERS_OFFSET, expected, x);
        }
        
        @Override
        void putNext(final Waiter o, final Waiter x) {
            UnsafeAtomicHelper.UNSAFE.putObject(o, UnsafeAtomicHelper.WAITER_NEXT_OFFSET, x);
        }
        
        @Override
        void putThread(final Waiter o, final Thread x) {
            UnsafeAtomicHelper.UNSAFE.putObject(o, UnsafeAtomicHelper.WAITER_THREAD_OFFSET, x);
        }
    }
    
    private static final class Waiter
    {
        static final Waiter TOMBSTONE;
        volatile Waiter next;
        volatile Thread thread;
        
        static {
            TOMBSTONE = new Waiter(false);
        }
        
        Waiter() {
            AbstractFuture.ATOMIC_HELPER.putThread(this, Thread.currentThread());
        }
        
        Waiter(final boolean b) {
        }
        
        void setNext(final Waiter waiter) {
            AbstractFuture.ATOMIC_HELPER.putNext(this, waiter);
        }
        
        void unpark() {
            final Thread thread = this.thread;
            if (thread != null) {
                this.thread = null;
                LockSupport.unpark(thread);
            }
        }
    }
}
