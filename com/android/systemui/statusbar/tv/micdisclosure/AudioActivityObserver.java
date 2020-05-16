// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.tv.micdisclosure;

import java.util.Set;
import android.content.Context;

abstract class AudioActivityObserver
{
    final Context mContext;
    final OnAudioActivityStateChangeListener mListener;
    
    AudioActivityObserver(final Context mContext, final OnAudioActivityStateChangeListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }
    
    abstract Set<String> getActivePackages();
    
    interface OnAudioActivityStateChangeListener
    {
        void onAudioActivityStateChange(final boolean p0, final String p1);
    }
}
