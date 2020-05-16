// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.suggestions;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Loader;
import android.os.Bundle;
import android.content.Context;
import android.service.settings.suggestions.Suggestion;
import java.util.List;
import android.app.LoaderManager$LoaderCallbacks;
import androidx.lifecycle.LifecycleObserver;

@Deprecated
public class SuggestionControllerMixin implements Object, LifecycleObserver, LoaderManager$LoaderCallbacks<List<Suggestion>>
{
    private final Context mContext;
    private final SuggestionControllerHost mHost;
    private final SuggestionController mSuggestionController;
    
    public Loader<List<Suggestion>> onCreateLoader(final int i, final Bundle bundle) {
        if (i == 42) {
            return (Loader<List<Suggestion>>)new SuggestionLoader(this.mContext, this.mSuggestionController);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("This loader id is not supported ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void onLoadFinished(final Loader<List<Suggestion>> loader, final List<Suggestion> list) {
        this.mHost.onSuggestionReady(list);
    }
    
    public void onLoaderReset(final Loader<List<Suggestion>> loader) {
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mSuggestionController.start();
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mSuggestionController.stop();
    }
    
    public interface SuggestionControllerHost
    {
        void onSuggestionReady(final List<Suggestion> p0);
    }
}
