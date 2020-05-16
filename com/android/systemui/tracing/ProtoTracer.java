// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tracing;

import java.util.Iterator;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Queue;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import com.android.systemui.tracing.nano.SystemUiTraceProto;
import com.android.systemui.tracing.nano.SystemUiTraceEntryProto;
import com.android.systemui.tracing.nano.SystemUiTraceFileProto;
import com.google.protobuf.nano.MessageNano;
import com.android.systemui.shared.tracing.FrameProtoTracer;
import com.android.systemui.Dumpable;

public class ProtoTracer implements Dumpable, ProtoTraceParams<MessageNano, SystemUiTraceFileProto, SystemUiTraceEntryProto, SystemUiTraceProto>
{
    private final Context mContext;
    private final FrameProtoTracer<MessageNano, SystemUiTraceFileProto, SystemUiTraceEntryProto, SystemUiTraceProto> mProtoTracer;
    
    public ProtoTracer(final Context mContext, final DumpManager dumpManager) {
        this.mContext = mContext;
        this.mProtoTracer = new FrameProtoTracer<MessageNano, SystemUiTraceFileProto, SystemUiTraceEntryProto, SystemUiTraceProto>((FrameProtoTracer.ProtoTraceParams<MessageNano, SystemUiTraceFileProto, SystemUiTraceEntryProto, SystemUiTraceProto>)this);
        dumpManager.registerDumpable(ProtoTracer.class.getName(), this);
    }
    
    public void add(final ProtoTraceable<SystemUiTraceProto> protoTraceable) {
        this.mProtoTracer.add(protoTraceable);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("ProtoTracer:");
        printWriter.print("    ");
        final StringBuilder sb = new StringBuilder();
        sb.append("enabled: ");
        sb.append(this.mProtoTracer.isEnabled());
        printWriter.println(sb.toString());
        printWriter.print("    ");
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("usagePct: ");
        sb2.append(this.mProtoTracer.getBufferUsagePct());
        printWriter.println(sb2.toString());
        printWriter.print("    ");
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("file: ");
        sb3.append(this.getTraceFile());
        printWriter.println(sb3.toString());
    }
    
    public SystemUiTraceFileProto getEncapsulatingTraceProto() {
        return new SystemUiTraceFileProto();
    }
    
    public byte[] getProtoBytes(final MessageNano messageNano) {
        return MessageNano.toByteArray(messageNano);
    }
    
    public int getProtoSize(final MessageNano messageNano) {
        return messageNano.getCachedSize();
    }
    
    @Override
    public File getTraceFile() {
        return new File(this.mContext.getFilesDir(), "sysui_trace.pb");
    }
    
    public byte[] serializeEncapsulatingProto(final SystemUiTraceFileProto systemUiTraceFileProto, final Queue<SystemUiTraceEntryProto> queue) {
        systemUiTraceFileProto.magicNumber = 4851032422572317011L;
        systemUiTraceFileProto.entry = (SystemUiTraceEntryProto[])queue.toArray((Object[])new SystemUiTraceEntryProto[0]);
        return MessageNano.toByteArray(systemUiTraceFileProto);
    }
    
    public void start() {
        this.mProtoTracer.start();
    }
    
    public void stop() {
        this.mProtoTracer.stop();
    }
    
    public void update() {
        this.mProtoTracer.update();
    }
    
    public SystemUiTraceEntryProto updateBufferProto(SystemUiTraceEntryProto systemUiTraceEntryProto, final ArrayList<ProtoTraceable<SystemUiTraceProto>> list) {
        if (systemUiTraceEntryProto == null) {
            systemUiTraceEntryProto = new SystemUiTraceEntryProto();
        }
        systemUiTraceEntryProto.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        SystemUiTraceProto systemUi = systemUiTraceEntryProto.systemUi;
        if (systemUi == null) {
            systemUi = new SystemUiTraceProto();
        }
        systemUiTraceEntryProto.systemUi = systemUi;
        final Iterator<ProtoTraceable<SystemUiTraceProto>> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next().writeToProto(systemUiTraceEntryProto.systemUi);
        }
        return systemUiTraceEntryProto;
    }
}
