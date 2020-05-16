// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.tv.micdisclosure;

import android.app.AppOpsManager;
import android.util.ArraySet;
import android.content.Context;
import java.util.Set;
import android.app.AppOpsManager$OnOpActiveChangedListener;

class RecordAudioAppOpObserver extends AudioActivityObserver implements AppOpsManager$OnOpActiveChangedListener
{
    private final Set<String> mActiveAudioRecordingPackages;
    
    RecordAudioAppOpObserver(final Context context, final OnAudioActivityStateChangeListener onAudioActivityStateChangeListener) {
        super(context, onAudioActivityStateChangeListener);
        this.mActiveAudioRecordingPackages = (Set<String>)new ArraySet();
        ((AppOpsManager)super.mContext.getSystemService("appops")).startWatchingActive(new String[] { "android:record_audio" }, super.mContext.getMainExecutor(), (AppOpsManager$OnOpActiveChangedListener)this);
    }
    
    @Override
    Set<String> getActivePackages() {
        return this.mActiveAudioRecordingPackages;
    }
    
    public void onOpActiveChanged(final String s, final int n, final String s2, final boolean b) {
        if (b) {
            if (this.mActiveAudioRecordingPackages.add(s2)) {
                super.mListener.onAudioActivityStateChange(true, s2);
            }
        }
        else if (this.mActiveAudioRecordingPackages.remove(s2)) {
            super.mListener.onAudioActivityStateChange(false, s2);
        }
    }
}
