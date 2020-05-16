// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui;

import android.support.annotation.Nullable;
import android.graphics.Bitmap;
import android.os.Bundle;

public interface ContentSuggestionsServiceWrapper
{
    void classifyContentSelections(final Bundle p0, final BundleCallback p1);
    
    void connectAndRunAsync(final Runnable p0);
    
    void notifyInteraction(final String p0, final Bundle p1);
    
    void processContextImage(final int p0, @Nullable final Bitmap p1, final Bundle p2);
    
    void suggestContentSelections(final int p0, final Bundle p1, final BundleCallback p2);
    
    public interface BundleCallback
    {
        void onResult(final Bundle p0);
    }
}
