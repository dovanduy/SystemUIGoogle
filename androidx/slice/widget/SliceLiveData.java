// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import android.util.Log;
import android.os.AsyncTask;
import androidx.slice.SliceViewManager;
import android.content.Intent;
import androidx.slice.Slice;
import androidx.lifecycle.LiveData;
import android.net.Uri;
import android.content.Context;
import java.util.Collection;
import androidx.collection.ArraySet;
import java.util.Arrays;
import androidx.slice.SliceSpecs;
import java.util.Set;
import androidx.slice.SliceSpec;

public final class SliceLiveData
{
    public static final SliceSpec OLD_BASIC;
    public static final SliceSpec OLD_LIST;
    public static final Set<SliceSpec> SUPPORTED_SPECS;
    
    static {
        OLD_BASIC = new SliceSpec("androidx.app.slice.BASIC", 1);
        OLD_LIST = new SliceSpec("androidx.app.slice.LIST", 1);
        SUPPORTED_SPECS = new ArraySet<SliceSpec>(Arrays.asList(SliceSpecs.BASIC, SliceSpecs.LIST, SliceSpecs.LIST_V2, SliceLiveData.OLD_BASIC, SliceLiveData.OLD_LIST));
    }
    
    public static LiveData<Slice> fromUri(final Context context, final Uri uri) {
        return new SliceLiveDataImpl(context.getApplicationContext(), uri, null);
    }
    
    public interface OnErrorListener
    {
        void onSliceError(final int p0, final Throwable p1);
    }
    
    private static class SliceLiveDataImpl extends LiveData<Slice>
    {
        final Intent mIntent;
        final OnErrorListener mListener;
        final SliceViewManager.SliceCallback mSliceCallback;
        final SliceViewManager mSliceViewManager;
        private final Runnable mUpdateSlice;
        Uri mUri;
        
        SliceLiveDataImpl(final Context context, final Uri mUri, final OnErrorListener mListener) {
            this.mUpdateSlice = new Runnable() {
                @Override
                public void run() {
                    try {
                        Slice slice;
                        if (SliceLiveDataImpl.this.mUri != null) {
                            slice = SliceLiveDataImpl.this.mSliceViewManager.bindSlice(SliceLiveDataImpl.this.mUri);
                        }
                        else {
                            slice = SliceLiveDataImpl.this.mSliceViewManager.bindSlice(SliceLiveDataImpl.this.mIntent);
                        }
                        if (slice == null) {
                            SliceLiveDataImpl.this.onSliceError(2, null);
                            return;
                        }
                        if (SliceLiveDataImpl.this.mUri == null) {
                            SliceLiveDataImpl.this.mUri = slice.getUri();
                            SliceLiveDataImpl.this.mSliceViewManager.registerSliceCallback(SliceLiveDataImpl.this.mUri, SliceLiveDataImpl.this.mSliceCallback);
                        }
                        LiveData.this.postValue(slice);
                    }
                    catch (Exception ex) {
                        SliceLiveDataImpl.this.onSliceError(0, ex);
                    }
                    catch (IllegalArgumentException ex2) {
                        SliceLiveDataImpl.this.onSliceError(3, ex2);
                    }
                }
            };
            this.mSliceCallback = new _$$Lambda$SliceLiveData$SliceLiveDataImpl$R4N7L73501Pav2ashjY94Bexi9s(this);
            this.mSliceViewManager = SliceViewManager.getInstance(context);
            this.mUri = mUri;
            this.mIntent = null;
            this.mListener = mListener;
        }
        
        @Override
        protected void onActive() {
            AsyncTask.execute(this.mUpdateSlice);
            final Uri mUri = this.mUri;
            if (mUri != null) {
                this.mSliceViewManager.registerSliceCallback(mUri, this.mSliceCallback);
            }
        }
        
        @Override
        protected void onInactive() {
            final Uri mUri = this.mUri;
            if (mUri != null) {
                this.mSliceViewManager.unregisterSliceCallback(mUri, this.mSliceCallback);
            }
        }
        
        void onSliceError(final int i, final Throwable t) {
            final Uri mUri = this.mUri;
            if (mUri != null) {
                this.mSliceViewManager.unregisterSliceCallback(mUri, this.mSliceCallback);
            }
            final OnErrorListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onSliceError(i, t);
                return;
            }
            if (t != null) {
                Log.e("SliceLiveData", "Error binding slice", t);
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Error binding slice, error code: ");
                sb.append(i);
                Log.e("SliceLiveData", sb.toString());
            }
        }
    }
}
