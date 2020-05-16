// 
// Decompiled by Procyon v0.5.36
// 

package androidx.asynclayoutinflater.view;

import android.util.Log;
import androidx.core.util.Pools$SynchronizedPool;
import java.util.concurrent.ArrayBlockingQueue;
import android.view.View;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.os.Message;
import android.content.Context;
import android.view.LayoutInflater;
import android.os.Handler$Callback;
import android.os.Handler;

public final class AsyncLayoutInflater
{
    Handler mHandler;
    private Handler$Callback mHandlerCallback;
    InflateThread mInflateThread;
    LayoutInflater mInflater;
    
    public AsyncLayoutInflater(final Context context) {
        this.mHandlerCallback = (Handler$Callback)new Handler$Callback() {
            public boolean handleMessage(final Message message) {
                final InflateRequest inflateRequest = (InflateRequest)message.obj;
                if (inflateRequest.view == null) {
                    inflateRequest.view = AsyncLayoutInflater.this.mInflater.inflate(inflateRequest.resid, inflateRequest.parent, false);
                }
                inflateRequest.callback.onInflateFinished(inflateRequest.view, inflateRequest.resid, inflateRequest.parent);
                AsyncLayoutInflater.this.mInflateThread.releaseRequest(inflateRequest);
                return true;
            }
        };
        this.mInflater = new BasicInflater(context);
        this.mHandler = new Handler(this.mHandlerCallback);
        this.mInflateThread = InflateThread.getInstance();
    }
    
    public void inflate(final int resid, final ViewGroup parent, final OnInflateFinishedListener callback) {
        if (callback != null) {
            final InflateRequest obtainRequest = this.mInflateThread.obtainRequest();
            obtainRequest.inflater = this;
            obtainRequest.resid = resid;
            obtainRequest.parent = parent;
            obtainRequest.callback = callback;
            this.mInflateThread.enqueue(obtainRequest);
            return;
        }
        throw new NullPointerException("callback argument may not be null!");
    }
    
    private static class BasicInflater extends LayoutInflater
    {
        private static final String[] sClassPrefixList;
        
        static {
            sClassPrefixList = new String[] { "android.widget.", "android.webkit.", "android.app." };
        }
        
        BasicInflater(final Context context) {
            super(context);
        }
        
        public LayoutInflater cloneInContext(final Context context) {
            return new BasicInflater(context);
        }
        
        protected View onCreateView(final String s, final AttributeSet set) throws ClassNotFoundException {
            final String[] sClassPrefixList = BasicInflater.sClassPrefixList;
            final int length = sClassPrefixList.length;
            int n = 0;
        Label_0042_Outer:
            while (true) {
                Label_0048: {
                    if (n >= length) {
                        break Label_0048;
                    }
                    final String s2 = sClassPrefixList[n];
                    while (true) {
                        try {
                            final View view = this.createView(s, s2, set);
                            if (view != null) {
                                return view;
                            }
                            ++n;
                            continue Label_0042_Outer;
                            return super.onCreateView(s, set);
                        }
                        catch (ClassNotFoundException ex) {
                            continue;
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private static class InflateRequest
    {
        OnInflateFinishedListener callback;
        AsyncLayoutInflater inflater;
        ViewGroup parent;
        int resid;
        View view;
        
        InflateRequest() {
        }
    }
    
    private static class InflateThread extends Thread
    {
        private static final InflateThread sInstance;
        private ArrayBlockingQueue<InflateRequest> mQueue;
        private Pools$SynchronizedPool<InflateRequest> mRequestPool;
        
        static {
            (sInstance = new InflateThread()).start();
        }
        
        private InflateThread() {
            this.mQueue = new ArrayBlockingQueue<InflateRequest>(10);
            this.mRequestPool = new Pools$SynchronizedPool<InflateRequest>(10);
        }
        
        public static InflateThread getInstance() {
            return InflateThread.sInstance;
        }
        
        public void enqueue(final InflateRequest e) {
            try {
                this.mQueue.put(e);
            }
            catch (InterruptedException cause) {
                throw new RuntimeException("Failed to enqueue async inflate request", cause);
            }
        }
        
        public InflateRequest obtainRequest() {
            InflateRequest inflateRequest;
            if ((inflateRequest = this.mRequestPool.acquire()) == null) {
                inflateRequest = new InflateRequest();
            }
            return inflateRequest;
        }
        
        public void releaseRequest(final InflateRequest inflateRequest) {
            inflateRequest.callback = null;
            inflateRequest.inflater = null;
            inflateRequest.parent = null;
            inflateRequest.resid = 0;
            inflateRequest.view = null;
            this.mRequestPool.release(inflateRequest);
        }
        
        @Override
        public void run() {
            while (true) {
                this.runInner();
            }
        }
        
        public void runInner() {
            try {
                final InflateRequest inflateRequest = this.mQueue.take();
                try {
                    inflateRequest.view = inflateRequest.inflater.mInflater.inflate(inflateRequest.resid, inflateRequest.parent, false);
                }
                catch (RuntimeException ex) {
                    Log.w("AsyncLayoutInflater", "Failed to inflate resource in the background! Retrying on the UI thread", (Throwable)ex);
                }
                Message.obtain(inflateRequest.inflater.mHandler, 0, (Object)inflateRequest).sendToTarget();
            }
            catch (InterruptedException ex2) {
                Log.w("AsyncLayoutInflater", (Throwable)ex2);
            }
        }
    }
    
    public interface OnInflateFinishedListener
    {
        void onInflateFinished(final View p0, final int p1, final ViewGroup p2);
    }
}
