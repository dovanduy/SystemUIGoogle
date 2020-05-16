// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.suggestions;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import android.service.settings.suggestions.Suggestion;
import java.util.List;
import androidx.lifecycle.LifecycleObserver;

public class SuggestionControllerMixinCompat implements Object, LifecycleObserver, Object<List<Suggestion>>
{
    private final SuggestionController mSuggestionController;
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mSuggestionController.start();
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mSuggestionController.stop();
    }
}
