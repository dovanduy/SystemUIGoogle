// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.tracing;

import android.util.Log;
import android.os.Trace;
import java.util.Collection;
import android.view.Choreographer;
import java.util.function.Consumer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.io.File;
import java.util.ArrayList;
import com.android.internal.util.TraceBuffer$ProtoProvider;
import java.util.Queue;
import com.android.internal.util.TraceBuffer;
import android.view.Choreographer$FrameCallback;

public class FrameProtoTracer<P, S extends P, T extends P, R> implements Choreographer$FrameCallback
{
    private final TraceBuffer<P, S, T> mBuffer;
    private volatile boolean mEnabled;
    private final Object mLock;
    private final ProtoTraceParams<P, S, T, R> mParams;
    private final Queue<T> mPool;
    private final TraceBuffer$ProtoProvider<P, S, T> mProvider;
    private final ArrayList<ProtoTraceable<R>> mTmpTraceables;
    private final File mTraceFile;
    private final ArrayList<ProtoTraceable<R>> mTraceables;
    
    public FrameProtoTracer(final ProtoTraceParams<P, S, T, R> mParams) {
        this.mLock = new Object();
        this.mPool = new LinkedList<T>();
        this.mTraceables = new ArrayList<ProtoTraceable<R>>();
        this.mTmpTraceables = new ArrayList<ProtoTraceable<R>>();
        this.mProvider = (TraceBuffer$ProtoProvider<P, S, T>)new TraceBuffer$ProtoProvider<P, S, T>() {
            public byte[] getBytes(final P p) {
                return FrameProtoTracer.this.mParams.getProtoBytes(p);
            }
            
            public int getItemSize(final P p) {
                return FrameProtoTracer.this.mParams.getProtoSize(p);
            }
            
            public void write(final S n, final Queue<T> queue, final OutputStream outputStream) throws IOException {
                outputStream.write(FrameProtoTracer.this.mParams.serializeEncapsulatingProto(n, queue));
            }
        };
        this.mParams = mParams;
        this.mBuffer = (TraceBuffer<P, S, T>)new TraceBuffer(1048576, (TraceBuffer$ProtoProvider)this.mProvider, (Consumer)new Consumer<T>() {
            @Override
            public void accept(final T t) {
                FrameProtoTracer.this.onProtoDequeued(t);
            }
        });
        this.mTraceFile = mParams.getTraceFile();
        Choreographer.getMainThreadInstance();
    }
    
    private void logState() {
        synchronized (this.mLock) {
            this.mTmpTraceables.addAll(this.mTraceables);
            // monitorexit(this.mLock)
            this.mBuffer.add((Object)this.mParams.updateBufferProto(this.mPool.poll(), this.mTmpTraceables));
            this.mTmpTraceables.clear();
        }
    }
    
    private void onProtoDequeued(final T t) {
        this.mPool.add(t);
    }
    
    private void writeToFile() {
        try {
            try {
                Trace.beginSection("ProtoTracer.writeToFile");
                this.mBuffer.writeTraceToFile(this.mTraceFile, (Object)this.mParams.getEncapsulatingTraceProto());
            }
            finally {}
        }
        catch (IOException ex) {
            Log.e("FrameProtoTracer", "Unable to write buffer to file", (Throwable)ex);
        }
        Trace.endSection();
        return;
        Trace.endSection();
    }
    
    public void add(final ProtoTraceable<R> e) {
        synchronized (this.mLock) {
            this.mTraceables.add(e);
        }
    }
    
    public void doFrame(final long n) {
        this.logState();
    }
    
    public float getBufferUsagePct() {
        return this.mBuffer.getBufferSize() / 1048576.0f;
    }
    
    public boolean isEnabled() {
        return this.mEnabled;
    }
    
    public void start() {
        synchronized (this.mLock) {
            if (this.mEnabled) {
                return;
            }
            this.mBuffer.resetBuffer();
            this.mEnabled = true;
            // monitorexit(this.mLock)
            this.logState();
        }
    }
    
    public void stop() {
        synchronized (this.mLock) {
            if (!this.mEnabled) {
                return;
            }
            this.mEnabled = false;
            // monitorexit(this.mLock)
            this.writeToFile();
        }
    }
    
    public void update() {
        if (!this.mEnabled) {
            return;
        }
        this.logState();
    }
    
    public interface ProtoTraceParams<P, S, T, R>
    {
        S getEncapsulatingTraceProto();
        
        byte[] getProtoBytes(final P p0);
        
        int getProtoSize(final P p0);
        
        File getTraceFile();
        
        byte[] serializeEncapsulatingProto(final S p0, final Queue<T> p1);
        
        T updateBufferProto(final T p0, final ArrayList<ProtoTraceable<R>> p1);
    }
}
