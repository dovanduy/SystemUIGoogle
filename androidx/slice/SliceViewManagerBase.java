// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.content.ContentProviderClient;
import android.os.AsyncTask;
import androidx.slice.widget.SliceLiveData;
import android.database.ContentObserver;
import java.util.concurrent.Executor;
import android.os.Handler;
import android.os.Looper;
import android.net.Uri;
import android.util.Pair;
import android.util.ArrayMap;
import android.content.Context;

public abstract class SliceViewManagerBase extends SliceViewManager
{
    protected final Context mContext;
    private final ArrayMap<Pair<Uri, SliceCallback>, SliceListenerImpl> mListenerLookup;
    
    SliceViewManagerBase(final Context mContext) {
        this.mListenerLookup = (ArrayMap<Pair<Uri, SliceCallback>, SliceListenerImpl>)new ArrayMap();
        this.mContext = mContext;
    }
    
    private SliceListenerImpl getListener(final Uri uri, final SliceCallback sliceCallback, final SliceListenerImpl sliceListenerImpl) {
        final Pair pair = new Pair((Object)uri, (Object)sliceCallback);
        synchronized (this.mListenerLookup) {
            final SliceListenerImpl sliceListenerImpl2 = (SliceListenerImpl)this.mListenerLookup.put((Object)pair, (Object)sliceListenerImpl);
            if (sliceListenerImpl2 != null) {
                sliceListenerImpl2.stopListening();
            }
            return sliceListenerImpl;
        }
    }
    
    @Override
    public void registerSliceCallback(final Uri uri, final SliceCallback sliceCallback) {
        this.registerSliceCallback(uri, new Executor(this) {
            final /* synthetic */ Handler val$h = new Handler(Looper.getMainLooper());
            
            @Override
            public void execute(final Runnable runnable) {
                this.val$h.post(runnable);
            }
        }, sliceCallback);
    }
    
    public void registerSliceCallback(final Uri uri, final Executor executor, final SliceCallback sliceCallback) {
        final SliceListenerImpl sliceListenerImpl = new SliceListenerImpl(uri, executor, sliceCallback);
        this.getListener(uri, sliceCallback, sliceListenerImpl);
        sliceListenerImpl.startListening();
    }
    
    @Override
    public void unregisterSliceCallback(final Uri uri, final SliceCallback sliceCallback) {
        synchronized (this.mListenerLookup) {
            final SliceListenerImpl sliceListenerImpl = (SliceListenerImpl)this.mListenerLookup.remove((Object)new Pair((Object)uri, (Object)sliceCallback));
            if (sliceListenerImpl != null) {
                sliceListenerImpl.stopListening();
            }
        }
    }
    
    private class SliceListenerImpl
    {
        final SliceCallback mCallback;
        final Executor mExecutor;
        private final ContentObserver mObserver;
        private boolean mPinned;
        final Runnable mUpdateSlice;
        Uri mUri;
        final /* synthetic */ SliceViewManagerBase this$0;
        
        SliceListenerImpl(final Uri mUri, final Executor mExecutor, final SliceCallback mCallback) {
            this.mUpdateSlice = new Runnable() {
                @Override
                public void run() {
                    SliceListenerImpl.this.tryPin();
                    final SliceListenerImpl this$1 = SliceListenerImpl.this;
                    SliceListenerImpl.this.mExecutor.execute(new Runnable() {
                        final /* synthetic */ Slice val$s = Slice.bindSlice(this$1.this$0.mContext, this$1.mUri, SliceLiveData.SUPPORTED_SPECS);
                        
                        @Override
                        public void run() {
                            SliceListenerImpl.this.mCallback.onSliceUpdated(this.val$s);
                        }
                    });
                }
            };
            this.mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                public void onChange(final boolean b) {
                    AsyncTask.execute(SliceListenerImpl.this.mUpdateSlice);
                }
            };
            this.mUri = mUri;
            this.mExecutor = mExecutor;
            this.mCallback = mCallback;
        }
        
        void startListening() {
            final ContentProviderClient acquireContentProviderClient = SliceViewManagerBase.this.mContext.getContentResolver().acquireContentProviderClient(this.mUri);
            if (acquireContentProviderClient != null) {
                acquireContentProviderClient.release();
                SliceViewManagerBase.this.mContext.getContentResolver().registerContentObserver(this.mUri, true, this.mObserver);
                this.tryPin();
            }
        }
        
        void stopListening() {
            SliceViewManagerBase.this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
            if (this.mPinned) {
                SliceViewManagerBase.this.unpinSlice(this.mUri);
                this.mPinned = false;
            }
        }
        
        void tryPin() {
            if (this.mPinned) {
                return;
            }
            try {
                SliceViewManagerBase.this.pinSlice(this.mUri);
                this.mPinned = true;
            }
            catch (SecurityException ex) {}
        }
    }
}
