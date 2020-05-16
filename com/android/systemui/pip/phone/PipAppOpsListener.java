// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.content.pm.ApplicationInfo;
import android.util.Pair;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.ComponentName;
import android.os.Handler;
import android.content.Context;
import android.app.AppOpsManager;
import android.app.AppOpsManager$OnOpChangedListener;
import android.app.IActivityManager;

public class PipAppOpsListener
{
    private IActivityManager mActivityManager;
    private AppOpsManager$OnOpChangedListener mAppOpsChangedListener;
    private AppOpsManager mAppOpsManager;
    private Callback mCallback;
    private Context mContext;
    private Handler mHandler;
    
    public PipAppOpsListener(final Context mContext, final IActivityManager mActivityManager, final Callback mCallback) {
        this.mAppOpsChangedListener = (AppOpsManager$OnOpChangedListener)new AppOpsManager$OnOpChangedListener() {
            public void onOpChanged(final String s, final String s2) {
                try {
                    final Pair<ComponentName, Integer> topPipActivity = PipUtils.getTopPipActivity(PipAppOpsListener.this.mContext, PipAppOpsListener.this.mActivityManager);
                    if (topPipActivity.first != null) {
                        final ApplicationInfo applicationInfoAsUser = PipAppOpsListener.this.mContext.getPackageManager().getApplicationInfoAsUser(s2, 0, (int)topPipActivity.second);
                        if (applicationInfoAsUser.packageName.equals(((ComponentName)topPipActivity.first).getPackageName()) && PipAppOpsListener.this.mAppOpsManager.checkOpNoThrow(67, applicationInfoAsUser.uid, s2) != 0) {
                            PipAppOpsListener.this.mHandler.post((Runnable)new _$$Lambda$PipAppOpsListener$1$UK38MrwiG74h0N6r_NQ6zq34Mqo(this));
                        }
                    }
                }
                catch (PackageManager$NameNotFoundException ex) {
                    PipAppOpsListener.this.unregisterAppOpsListener();
                }
            }
        };
        this.mContext = mContext;
        this.mHandler = new Handler(this.mContext.getMainLooper());
        this.mActivityManager = mActivityManager;
        this.mAppOpsManager = (AppOpsManager)mContext.getSystemService("appops");
        this.mCallback = mCallback;
    }
    
    private void registerAppOpsListener(final String s) {
        this.mAppOpsManager.startWatchingMode(67, s, this.mAppOpsChangedListener);
    }
    
    private void unregisterAppOpsListener() {
        this.mAppOpsManager.stopWatchingMode(this.mAppOpsChangedListener);
    }
    
    public void onActivityPinned(final String s) {
        this.registerAppOpsListener(s);
    }
    
    public void onActivityUnpinned() {
        this.unregisterAppOpsListener();
    }
    
    public interface Callback
    {
        void dismissPip();
    }
}
