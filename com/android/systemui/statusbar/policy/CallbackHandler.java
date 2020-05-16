// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.telephony.SubscriptionInfo;
import java.util.List;
import android.os.Message;
import java.util.Iterator;
import com.android.internal.annotations.VisibleForTesting;
import android.os.Looper;
import java.util.ArrayList;
import android.os.Handler;

public class CallbackHandler extends Handler implements EmergencyListener, SignalCallback
{
    private final ArrayList<EmergencyListener> mEmergencyListeners;
    private final ArrayList<SignalCallback> mSignalCallbacks;
    
    public CallbackHandler() {
        super(Looper.getMainLooper());
        this.mEmergencyListeners = new ArrayList<EmergencyListener>();
        this.mSignalCallbacks = new ArrayList<SignalCallback>();
    }
    
    @VisibleForTesting
    CallbackHandler(final Looper looper) {
        super(looper);
        this.mEmergencyListeners = new ArrayList<EmergencyListener>();
        this.mSignalCallbacks = new ArrayList<SignalCallback>();
    }
    
    public void handleMessage(final Message message) {
        switch (message.what) {
            case 7: {
                if (message.arg1 != 0) {
                    this.mSignalCallbacks.add((SignalCallback)message.obj);
                    break;
                }
                this.mSignalCallbacks.remove(message.obj);
                break;
            }
            case 6: {
                if (message.arg1 != 0) {
                    this.mEmergencyListeners.add((EmergencyListener)message.obj);
                    break;
                }
                this.mEmergencyListeners.remove(message.obj);
                break;
            }
            case 5: {
                final Iterator<SignalCallback> iterator = this.mSignalCallbacks.iterator();
                while (iterator.hasNext()) {
                    iterator.next().setMobileDataEnabled(message.arg1 != 0);
                }
                break;
            }
            case 4: {
                final Iterator<SignalCallback> iterator2 = this.mSignalCallbacks.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().setIsAirplaneMode((IconState)message.obj);
                }
                break;
            }
            case 3: {
                final Iterator<SignalCallback> iterator3 = this.mSignalCallbacks.iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().setEthernetIndicators((IconState)message.obj);
                }
                break;
            }
            case 2: {
                final Iterator<SignalCallback> iterator4 = this.mSignalCallbacks.iterator();
                while (iterator4.hasNext()) {
                    iterator4.next().setNoSims(message.arg1 != 0, message.arg2 != 0);
                }
                break;
            }
            case 1: {
                final Iterator<SignalCallback> iterator5 = this.mSignalCallbacks.iterator();
                while (iterator5.hasNext()) {
                    iterator5.next().setSubs((List<SubscriptionInfo>)message.obj);
                }
                break;
            }
            case 0: {
                final Iterator<EmergencyListener> iterator6 = this.mEmergencyListeners.iterator();
                while (iterator6.hasNext()) {
                    iterator6.next().setEmergencyCallsOnly(message.arg1 != 0);
                }
                break;
            }
        }
    }
    
    public void setEmergencyCallsOnly(final boolean b) {
        this.obtainMessage(0, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    public void setEthernetIndicators(final IconState iconState) {
        this.obtainMessage(3, (Object)iconState).sendToTarget();
    }
    
    public void setIsAirplaneMode(final IconState iconState) {
        this.obtainMessage(4, (Object)iconState).sendToTarget();
    }
    
    public void setListening(final SignalCallback signalCallback, final boolean b) {
        this.obtainMessage(7, (int)(b ? 1 : 0), 0, (Object)signalCallback).sendToTarget();
    }
    
    public void setMobileDataEnabled(final boolean b) {
        this.obtainMessage(5, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    public void setMobileDataIndicators(final IconState iconState, final IconState iconState2, final int n, final int n2, final boolean b, final boolean b2, final CharSequence charSequence, final CharSequence charSequence2, final CharSequence charSequence3, final boolean b3, final int n3, final boolean b4) {
        this.post((Runnable)new _$$Lambda$CallbackHandler$uMnAccxpYS4aQwu2V03dAeAi978(this, iconState, iconState2, n, n2, b, b2, charSequence, charSequence2, charSequence3, b3, n3, b4));
    }
    
    public void setNoSims(final boolean b, final boolean b2) {
        this.obtainMessage(2, (int)(b ? 1 : 0), (int)(b2 ? 1 : 0)).sendToTarget();
    }
    
    public void setSubs(final List<SubscriptionInfo> list) {
        this.obtainMessage(1, (Object)list).sendToTarget();
    }
    
    public void setWifiIndicators(final boolean b, final IconState iconState, final IconState iconState2, final boolean b2, final boolean b3, final String s, final boolean b4, final String s2) {
        this.post((Runnable)new _$$Lambda$CallbackHandler$BL9Oe1XlhjuRCIkE3XITv_5klDM(this, b, iconState, iconState2, b2, b3, s, b4, s2));
    }
}
