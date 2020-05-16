// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.utils;

import android.content.Context;
import android.content.AsyncTaskLoader;

@Deprecated
public abstract class AsyncLoader<T> extends AsyncTaskLoader<T>
{
    private T mResult;
    
    public AsyncLoader(final Context context) {
        super(context);
    }
    
    public void deliverResult(final T mResult) {
        if (this.isReset()) {
            if (mResult != null) {
                this.onDiscardResult(mResult);
            }
            return;
        }
        final T mResult2 = this.mResult;
        this.mResult = mResult;
        if (this.isStarted()) {
            super.deliverResult((Object)mResult);
        }
        if (mResult2 != null && mResult2 != this.mResult) {
            this.onDiscardResult(mResult2);
        }
    }
    
    public void onCanceled(final T t) {
        super.onCanceled((Object)t);
        if (t != null) {
            this.onDiscardResult(t);
        }
    }
    
    protected abstract void onDiscardResult(final T p0);
    
    protected void onReset() {
        super.onReset();
        this.onStopLoading();
        final T mResult = this.mResult;
        if (mResult != null) {
            this.onDiscardResult(mResult);
        }
        this.mResult = null;
    }
    
    protected void onStartLoading() {
        final T mResult = this.mResult;
        if (mResult != null) {
            this.deliverResult(mResult);
        }
        if (this.takeContentChanged() || this.mResult == null) {
            this.forceLoad();
        }
    }
    
    protected void onStopLoading() {
        this.cancelLoad();
    }
}
