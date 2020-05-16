// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.RemoteException;
import android.net.NetworkPolicyManager$Listener;
import android.os.Looper;
import android.content.Context;
import android.net.NetworkPolicyManager;
import android.net.INetworkPolicyListener;
import java.util.ArrayList;
import android.os.Handler;

public class DataSaverControllerImpl implements DataSaverController
{
    private final Handler mHandler;
    private final ArrayList<Listener> mListeners;
    private final INetworkPolicyListener mPolicyListener;
    private final NetworkPolicyManager mPolicyManager;
    
    public DataSaverControllerImpl(final Context context) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mListeners = new ArrayList<Listener>();
        this.mPolicyListener = (INetworkPolicyListener)new NetworkPolicyManager$Listener() {
            public void onRestrictBackgroundChanged(final boolean b) {
                DataSaverControllerImpl.this.mHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        DataSaverControllerImpl.this.handleRestrictBackgroundChanged(b);
                    }
                });
            }
        };
        this.mPolicyManager = NetworkPolicyManager.from(context);
    }
    
    private void handleRestrictBackgroundChanged(final boolean b) {
        final ArrayList<Listener> mListeners = this.mListeners;
        // monitorenter(mListeners)
        int i = 0;
        try {
            while (i < this.mListeners.size()) {
                this.mListeners.get(i).onDataSaverChanged(b);
                ++i;
            }
        }
        finally {
        }
        // monitorexit(mListeners)
    }
    
    @Override
    public void addCallback(final Listener e) {
        synchronized (this.mListeners) {
            this.mListeners.add(e);
            if (this.mListeners.size() == 1) {
                this.mPolicyManager.registerListener(this.mPolicyListener);
            }
            // monitorexit(this.mListeners)
            e.onDataSaverChanged(this.isDataSaverEnabled());
        }
    }
    
    @Override
    public boolean isDataSaverEnabled() {
        return this.mPolicyManager.getRestrictBackground();
    }
    
    @Override
    public void removeCallback(final Listener o) {
        synchronized (this.mListeners) {
            this.mListeners.remove(o);
            if (this.mListeners.size() == 0) {
                this.mPolicyManager.unregisterListener(this.mPolicyListener);
            }
        }
    }
    
    @Override
    public void setDataSaverEnabled(final boolean restrictBackground) {
        this.mPolicyManager.setRestrictBackground(restrictBackground);
        try {
            this.mPolicyListener.onRestrictBackgroundChanged(restrictBackground);
        }
        catch (RemoteException ex) {}
    }
}
