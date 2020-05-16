// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import android.os.SystemClock;
import android.os.Trace;
import kotlin.jvm.functions.Function1;
import kotlin.collections.ArraysKt;
import java.util.List;
import kotlin.text.StringsKt;
import java.io.FileDescriptor;
import com.android.systemui.R$array;
import com.android.systemui.R$string;
import java.util.Iterator;
import java.io.PrintWriter;
import android.util.ArrayMap;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.Dumpable;
import android.content.Context;
import com.android.systemui.log.LogBuffer;
import java.util.Map;

public final class DumpManager
{
    private final Map<String, RegisteredDumpable<LogBuffer>> buffers;
    private final Context context;
    private final Map<String, RegisteredDumpable<Dumpable>> dumpables;
    
    public DumpManager(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.context = context;
        this.dumpables = (Map<String, RegisteredDumpable<Dumpable>>)new ArrayMap();
        this.buffers = (Map<String, RegisteredDumpable<LogBuffer>>)new ArrayMap();
    }
    
    private final boolean canAssignToNameLocked(final String s, final Object o) {
        final RegisteredDumpable<Dumpable> registeredDumpable = this.dumpables.get(s);
        if (registeredDumpable != null) {
            final Dumpable dumpable = registeredDumpable.getDumpable();
            if (dumpable != null) {
                final Object o2 = dumpable;
                return o2 == null || Intrinsics.areEqual(o, o2);
            }
        }
        final RegisteredDumpable<LogBuffer> registeredDumpable2 = this.buffers.get(s);
        Object o2;
        if (registeredDumpable2 != null) {
            o2 = registeredDumpable2.getDumpable();
        }
        else {
            o2 = null;
        }
        return o2 == null || Intrinsics.areEqual(o, o2);
    }
    
    private final void dumpBuffer(final RegisteredDumpable<LogBuffer> registeredDumpable, final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        printWriter.println();
        printWriter.println();
        final StringBuilder sb = new StringBuilder();
        sb.append("BUFFER ");
        sb.append(registeredDumpable.getName());
        sb.append(':');
        printWriter.println(sb.toString());
        printWriter.println("============================================================================");
        registeredDumpable.getDumpable().dump(printWriter, parsedArgs.getTailLength());
    }
    
    private final void dumpBuffersLocked(final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        final Iterator<RegisteredDumpable<LogBuffer>> iterator = this.buffers.values().iterator();
        while (iterator.hasNext()) {
            this.dumpBuffer(iterator.next(), printWriter, parsedArgs);
        }
    }
    
    private final void dumpConfig(final PrintWriter printWriter) {
        printWriter.println("SystemUiServiceComponents configuration:");
        printWriter.print("vendor component: ");
        printWriter.println(this.context.getResources().getString(R$string.config_systemUIVendorServiceComponent));
        this.dumpServiceList(printWriter, "global", R$array.config_systemUIServiceComponents);
        this.dumpServiceList(printWriter, "per-user", R$array.config_systemUIServiceComponentsPerUser);
    }
    
    private final void dumpCriticalLocked(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        this.dumpDumpablesLocked(fileDescriptor, printWriter, parsedArgs);
        this.dumpConfig(printWriter);
    }
    
    private final void dumpDumpable(final RegisteredDumpable<Dumpable> registeredDumpable, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        printWriter.println();
        final StringBuilder sb = new StringBuilder();
        sb.append(registeredDumpable.getName());
        sb.append(':');
        printWriter.println(sb.toString());
        printWriter.println("----------------------------------------------------------------------------");
        registeredDumpable.getDumpable().dump(fileDescriptor, printWriter, parsedArgs.getRawArgs());
    }
    
    private final void dumpDumpablesLocked(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        final Iterator<RegisteredDumpable<Dumpable>> iterator = this.dumpables.values().iterator();
        while (iterator.hasNext()) {
            this.dumpDumpable(iterator.next(), fileDescriptor, printWriter, parsedArgs);
        }
    }
    
    private final void dumpNormalLocked(final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        this.dumpBuffersLocked(printWriter, parsedArgs);
    }
    
    private final void dumpParameterizedLocked(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        final String command = parsedArgs.getCommand();
        if (command != null) {
            switch (command.hashCode()) {
                case 842828580: {
                    if (command.equals("bugreport-critical")) {
                        this.dumpCriticalLocked(fileDescriptor, printWriter, parsedArgs);
                        return;
                    }
                    break;
                }
                case 227996723: {
                    if (command.equals("buffers")) {
                        this.dumpBuffersLocked(printWriter, parsedArgs);
                        return;
                    }
                    break;
                }
                case -1045369428: {
                    if (command.equals("bugreport-normal")) {
                        this.dumpNormalLocked(printWriter, parsedArgs);
                        return;
                    }
                    break;
                }
                case -1353714459: {
                    if (command.equals("dumpables")) {
                        this.dumpDumpablesLocked(fileDescriptor, printWriter, parsedArgs);
                        return;
                    }
                    break;
                }
            }
        }
        this.dumpTargetsLocked(parsedArgs.getNonFlagArgs(), fileDescriptor, printWriter, parsedArgs);
    }
    
    private final void dumpServiceList(final PrintWriter printWriter, final String s, int i) {
        final String[] stringArray = this.context.getResources().getStringArray(i);
        printWriter.print(s);
        printWriter.print(": ");
        if (stringArray == null) {
            printWriter.println("N/A");
            return;
        }
        printWriter.print(stringArray.length);
        printWriter.println(" services");
        for (i = 0; i < stringArray.length; ++i) {
            printWriter.print("  ");
            printWriter.print(i);
            printWriter.print(": ");
            printWriter.println(stringArray[i]);
        }
    }
    
    private final void dumpTarget(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        if (Intrinsics.areEqual(s, "config")) {
            this.dumpConfig(printWriter);
            return;
        }
        for (final RegisteredDumpable<Dumpable> registeredDumpable : this.dumpables.values()) {
            if (StringsKt.endsWith$default(registeredDumpable.getName(), s, false, 2, null)) {
                this.dumpDumpable(registeredDumpable, fileDescriptor, printWriter, parsedArgs);
                return;
            }
        }
        for (final RegisteredDumpable<LogBuffer> registeredDumpable2 : this.buffers.values()) {
            if (StringsKt.endsWith$default(registeredDumpable2.getName(), s, false, 2, null)) {
                this.dumpBuffer(registeredDumpable2, printWriter, parsedArgs);
                break;
            }
        }
    }
    
    private final void dumpTargetsLocked(final List<String> list, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final ParsedArgs parsedArgs) {
        if (list.isEmpty()) {
            printWriter.println("Nothing to dump :(");
        }
        else {
            final Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                this.dumpTarget(iterator.next(), fileDescriptor, printWriter, parsedArgs);
            }
        }
    }
    
    private final ParsedArgs parseArgs(final String[] array) {
        final List<String> mutableList = ArraysKt.toMutableList(array);
        final ParsedArgs parsedArgs = new ParsedArgs(array, mutableList);
        final Iterator<String> iterator = mutableList.iterator();
        while (iterator.hasNext()) {
            final String str = iterator.next();
            if (StringsKt.startsWith$default(str, "-", false, 2, null)) {
                iterator.remove();
                final int hashCode = str.hashCode();
                Label_0174: {
                    if (hashCode != 1511) {
                        if (hashCode != 1056887741) {
                            if (hashCode != 1333422576 || !str.equals("--tail")) {
                                break Label_0174;
                            }
                        }
                        else {
                            if (str.equals("--dump-priority")) {
                                parsedArgs.setDumpPriority((String)this.readArgument(iterator, "--dump-priority", (Function1<? super String, ?>)DumpManager$parseArgs.DumpManager$parseArgs$1.INSTANCE));
                                continue;
                            }
                            break Label_0174;
                        }
                    }
                    else if (!str.equals("-t")) {
                        break Label_0174;
                    }
                    parsedArgs.setTailLength(((Number)this.readArgument(iterator, "--tail", (Function1<? super String, ?>)DumpManager$parseArgs.DumpManager$parseArgs$2.INSTANCE)).intValue());
                    continue;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Unknown flag: ");
                sb.append(str);
                throw new ArgParseException(sb.toString());
            }
        }
        if ((mutableList.isEmpty() ^ true) && ArraysKt.contains(DumpManagerKt.access$getCOMMANDS$p(), mutableList.get(0))) {
            parsedArgs.setCommand(mutableList.remove(0));
        }
        return parsedArgs;
    }
    
    private final <T> T readArgument(final Iterator<String> iterator, final String s, final Function1<? super String, ? extends T> function1) {
        if (iterator.hasNext()) {
            final String str = iterator.next();
            try {
                final T invoke = (T)function1.invoke(str);
                iterator.remove();
                return invoke;
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Invalid argument '");
                sb.append(str);
                sb.append("' for flag ");
                sb.append(s);
                throw new ArgParseException(sb.toString());
            }
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Missing argument for ");
        sb2.append(s);
        throw new ArgParseException(sb2.toString());
    }
    
    public final void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        synchronized (this) {
            Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
            Intrinsics.checkParameterIsNotNull(printWriter, "pw");
            Intrinsics.checkParameterIsNotNull(array, "args");
            Trace.beginSection("DumpManager#dump()");
            final long uptimeMillis = SystemClock.uptimeMillis();
            try {
                final ParsedArgs args = this.parseArgs(array);
                final String dumpPriority = args.getDumpPriority();
                Label_0128: {
                    if (dumpPriority != null) {
                        final int hashCode = dumpPriority.hashCode();
                        if (hashCode != -1986416409) {
                            if (hashCode == -1560189025) {
                                if (dumpPriority.equals("CRITICAL")) {
                                    this.dumpCriticalLocked(fileDescriptor, printWriter, args);
                                    break Label_0128;
                                }
                            }
                        }
                        else if (dumpPriority.equals("NORMAL")) {
                            this.dumpNormalLocked(printWriter, args);
                            break Label_0128;
                        }
                    }
                    this.dumpParameterizedLocked(fileDescriptor, printWriter, args);
                }
                printWriter.println();
                final StringBuilder sb = new StringBuilder();
                sb.append("Dump took ");
                sb.append(SystemClock.uptimeMillis() - uptimeMillis);
                sb.append("ms");
                printWriter.println(sb.toString());
                Trace.endSection();
            }
            catch (ArgParseException ex) {
                printWriter.println(ex.getMessage());
            }
        }
    }
    
    public final void registerBuffer(final String str, final LogBuffer logBuffer) {
        synchronized (this) {
            Intrinsics.checkParameterIsNotNull(str, "name");
            Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
            if (this.canAssignToNameLocked(str, logBuffer)) {
                this.buffers.put(str, new RegisteredDumpable<LogBuffer>(str, logBuffer));
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append('\'');
            sb.append(str);
            sb.append("' is already registered");
            throw new IllegalArgumentException(sb.toString());
        }
    }
    
    public final void registerDumpable(final String s, final Dumpable dumpable) {
        synchronized (this) {
            Intrinsics.checkParameterIsNotNull(s, "name");
            Intrinsics.checkParameterIsNotNull(dumpable, "module");
            if (ArraysKt.contains(DumpManagerKt.access$getRESERVED_NAMES$p(), s)) {
                final StringBuilder sb = new StringBuilder();
                sb.append('\'');
                sb.append(s);
                sb.append("' is reserved");
                throw new IllegalArgumentException(sb.toString());
            }
            if (this.canAssignToNameLocked(s, dumpable)) {
                this.dumpables.put(s, new RegisteredDumpable<Dumpable>(s, dumpable));
                return;
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append('\'');
            sb2.append(s);
            sb2.append("' is already registered");
            throw new IllegalArgumentException(sb2.toString());
        }
    }
    
    public final void unregisterDumpable(final String s) {
        synchronized (this) {
            Intrinsics.checkParameterIsNotNull(s, "name");
            this.dumpables.remove(s);
        }
    }
}
