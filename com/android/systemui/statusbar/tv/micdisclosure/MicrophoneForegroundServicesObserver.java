// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.tv.micdisclosure;

import java.util.Set;
import java.util.Iterator;
import java.util.List;
import android.app.ActivityManager$RunningAppProcessInfo;
import android.os.RemoteException;
import android.util.Log;
import android.app.ActivityManager;
import android.app.IProcessObserver$Stub;
import android.util.ArrayMap;
import android.content.Context;
import android.app.IProcessObserver;
import android.util.SparseArray;
import java.util.Map;
import android.app.IActivityManager;

class MicrophoneForegroundServicesObserver extends AudioActivityObserver
{
    private final IActivityManager mActivityManager;
    private final Map<String, Integer> mPackageToProcessCount;
    private final SparseArray<String[]> mPidToPackages;
    private final IProcessObserver mProcessObserver;
    
    MicrophoneForegroundServicesObserver(final Context context, final OnAudioActivityStateChangeListener onAudioActivityStateChangeListener) {
        super(context, onAudioActivityStateChangeListener);
        this.mPidToPackages = (SparseArray<String[]>)new SparseArray();
        this.mPackageToProcessCount = (Map<String, Integer>)new ArrayMap();
        this.mProcessObserver = (IProcessObserver)new IProcessObserver$Stub() {
            public void onForegroundActivitiesChanged(final int n, final int n2, final boolean b) {
            }
            
            public void onForegroundServicesChanged(final int n, final int n2, final int n3) {
                MicrophoneForegroundServicesObserver.this.mContext.getMainExecutor().execute(new _$$Lambda$MicrophoneForegroundServicesObserver$1$cK1ViNLGM7E8_70_FKaWjuX21p8(this, n, n3));
            }
            
            public void onProcessDied(final int n, final int n2) {
                MicrophoneForegroundServicesObserver.this.mContext.getMainExecutor().execute(new _$$Lambda$MicrophoneForegroundServicesObserver$1$vI3qHF0MmDL0fzSttt8297q0_Xk(this, n));
            }
        };
        final IActivityManager service = ActivityManager.getService();
        this.mActivityManager = service;
        try {
            service.registerProcessObserver(this.mProcessObserver);
        }
        catch (RemoteException ex) {
            Log.e("MicrophoneForegroundServicesObserver", "Couldn't register process observer", (Throwable)ex);
        }
    }
    
    private String[] getPackageNames(final int i) {
        try {
            final List runningAppProcesses = this.mActivityManager.getRunningAppProcesses();
            if (runningAppProcesses == null) {
                Log.wtf("MicrophoneForegroundServicesObserver", "No running apps reported");
            }
            for (final ActivityManager$RunningAppProcessInfo activityManager$RunningAppProcessInfo : runningAppProcesses) {
                if (activityManager$RunningAppProcessInfo.pid == i) {
                    return activityManager$RunningAppProcessInfo.pkgList;
                }
            }
            return null;
        }
        catch (RemoteException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Couldn't get package name for pid=");
            sb.append(i);
            Log.d("MicrophoneForegroundServicesObserver", sb.toString());
            return null;
        }
    }
    
    private void notifyPackageStateChanged(final String s, final boolean b) {
        super.mListener.onAudioActivityStateChange(b, s);
    }
    
    private void onProcessDied(int i) {
        final String[] array = (String[])this.mPidToPackages.removeReturnOld(i);
        if (array == null) {
            return;
        }
        String s;
        int intValue;
        StringBuilder sb;
        for (i = array.length - 1; i >= 0; --i) {
            s = array[i];
            intValue = this.mPackageToProcessCount.getOrDefault(s, 0);
            if (intValue <= 0) {
                sb = new StringBuilder();
                sb.append("Bookkeeping error, process count for ");
                sb.append(s);
                sb.append(" is ");
                sb.append(intValue);
                Log.e("MicrophoneForegroundServicesObserver", sb.toString());
            }
            else if (--intValue > 0) {
                this.mPackageToProcessCount.put(s, intValue);
            }
            else {
                this.mPackageToProcessCount.remove(s);
                this.notifyPackageStateChanged(s, false);
            }
        }
    }
    
    private void onProcessForegroundServicesChanged(int intValue, final boolean b) {
        Object packageNames;
        if (b) {
            if (this.mPidToPackages.contains(intValue)) {
                packageNames = null;
            }
            else {
                packageNames = this.getPackageNames(intValue);
                this.mPidToPackages.append(intValue, packageNames);
            }
        }
        else {
            packageNames = this.mPidToPackages.removeReturnOld(intValue);
        }
        if (packageNames == null) {
            return;
        }
        for (int i = packageNames.length - 1; i >= 0; --i) {
            final String key = packageNames[i];
            final Map<String, Integer> mPackageToProcessCount = this.mPackageToProcessCount;
            boolean b2 = false;
            intValue = mPackageToProcessCount.getOrDefault(key, 0);
            Label_0147: {
                if (b) {
                    final int n = intValue + 1;
                    if ((intValue = n) != 1) {
                        break Label_0147;
                    }
                    intValue = n;
                }
                else {
                    final int n2 = intValue - 1;
                    if ((intValue = n2) != 0) {
                        break Label_0147;
                    }
                    intValue = n2;
                }
                b2 = true;
            }
            if (intValue > 0) {
                this.mPackageToProcessCount.put(key, intValue);
            }
            else {
                this.mPackageToProcessCount.remove(key);
            }
            if (b2) {
                this.notifyPackageStateChanged(key, b);
            }
        }
    }
    
    @Override
    Set<String> getActivePackages() {
        return this.mPackageToProcessCount.keySet();
    }
}
