// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.plugin;

import java.util.ArrayList;
import android.provider.Settings$Secure;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import android.hardware.location.NanoAppState;
import java.util.concurrent.TimeUnit;
import android.util.Log;
import android.hardware.location.NanoAppMessage;
import android.hardware.location.ContextHubManager;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.ContextHubClientCallback;
import android.hardware.location.ContextHubClient;
import android.content.Context;
import android.util.Pair;
import java.util.List;
import com.android.systemui.plugins.annotations.Requires;
import com.android.systemui.plugins.SensorManagerPlugin;

@Requires(target = SensorManagerPlugin.class, version = 1)
public class ElmyraSensorPlugin implements SensorManagerPlugin
{
    private List<Pair<Sensor, SensorEventListener>> mClients;
    private Context mContext;
    private ContextHubClient mContextHubClient;
    private final ContextHubClientCallback mContextHubClientCallback;
    private ContextHubInfo mContextHubInfo;
    private ContextHubManager mContextHubManager;
    private Thread mInitThread;
    private boolean mListening;
    private boolean mNanoAppFound;
    
    public ElmyraSensorPlugin() {
        this.mContextHubClientCallback = new ContextHubClientCallback() {
            public void onMessageFromNanoApp(final ContextHubClient contextHubClient, final NanoAppMessage nanoAppMessage) {
                if (nanoAppMessage.getNanoAppId() != 5147455389092024334L) {
                    return;
                }
                if (!ElmyraSensorPlugin.this.mNanoAppFound) {
                    Log.wtf("ElmyraSensorPlugin", "onMessageFromNanoApp(): nanoapp not found");
                    return;
                }
                if (nanoAppMessage.getMessageType() == 304) {
                    ElmyraSensorPlugin.this.onGrabDetected();
                }
            }
        };
    }
    
    private void awaitInit() {
        try {
            this.mInitThread.join();
        }
        catch (InterruptedException ex) {
            Log.e("ElmyraSensorPlugin", "Interrupted while waiting for init", (Throwable)ex);
        }
    }
    
    private boolean findNanoApp() {
        if (this.mNanoAppFound) {
            return true;
        }
        final List contextHubs = this.mContextHubManager.getContextHubs();
        if (contextHubs.size() == 0) {
            Log.e("ElmyraSensorPlugin", "No context hubs found");
            return false;
        }
        this.mContextHubInfo = contextHubs.get(0);
        try {
            final Iterator iterator = ((List)this.mContextHubManager.queryNanoApps((ContextHubInfo)contextHubs.get(0)).waitForResponse(5L, TimeUnit.SECONDS).getContents()).iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getNanoAppId() == 5147455389092024334L) {
                    this.mNanoAppFound = true;
                    break;
                }
            }
            return true;
        }
        catch (TimeoutException ex) {
            Log.e("ElmyraSensorPlugin", "Timed out looking for nanoapp");
            return false;
        }
        catch (InterruptedException ex2) {
            Log.e("ElmyraSensorPlugin", "Interrupted while looking for nanoapp");
            return false;
        }
    }
    
    private void onGrabDetected() {
        for (int i = 0; i < this.mClients.size(); ++i) {
            final Pair<Sensor, SensorEventListener> pair = this.mClients.get(i);
            ((SensorEventListener)pair.second).onSensorChanged(new SensorEvent((Sensor)pair.first, 1));
        }
    }
    
    private void sendMessageToNanoApp(final int n, final byte[] array) {
        if (!this.mListening && n != 205) {
            Log.w("ElmyraSensorPlugin", String.format("Attempted to send message %d to inactive recognizer", n));
            return;
        }
        final int sendMessageToNanoApp = this.mContextHubClient.sendMessageToNanoApp(NanoAppMessage.createMessageToNanoApp(5147455389092024334L, n, array));
        if (sendMessageToNanoApp != 0) {
            Log.e("ElmyraSensorPlugin", String.format("Unable to send message %d to nanoapp, error code %d", n, sendMessageToNanoApp));
        }
    }
    
    private void startListening() {
        this.awaitInit();
        if (!this.mNanoAppFound && !this.findNanoApp()) {
            return;
        }
        if (this.mListening) {
            return;
        }
        this.mContextHubClient = this.mContextHubManager.createClient(this.mContextHubInfo, this.mContextHubClientCallback);
        this.sendMessageToNanoApp(205, new byte[0]);
        this.mListening = true;
    }
    
    private void stopListening() {
        this.awaitInit();
        if (!this.mNanoAppFound) {
            final boolean nanoApp = this.findNanoApp();
            final StringBuilder sb = new StringBuilder();
            sb.append("stopListening(): nanoapp not found, refind = ");
            sb.append(nanoApp);
            Log.e("ElmyraSensorPlugin", sb.toString());
            if (!nanoApp) {
                return;
            }
        }
        if (!this.mListening) {
            return;
        }
        this.sendMessageToNanoApp(206, new byte[0]);
        this.mContextHubClient.close();
        this.mListening = false;
    }
    
    private void updateChreListener() {
        if (Settings$Secure.getInt(this.mContext.getContentResolver(), "com.google.android.systemui.elmyra.plugin.ENABLE", 0) == 0) {
            return;
        }
        if (this.mClients.isEmpty() && this.mListening) {
            this.stopListening();
        }
        else if (!this.mClients.isEmpty() && !this.mListening) {
            this.startListening();
        }
    }
    
    @Override
    public void onCreate(final Context context, final Context mContext) {
        this.mContext = mContext;
        this.mClients = new ArrayList<Pair<Sensor, SensorEventListener>>();
        this.mContextHubManager = (ContextHubManager)mContext.getSystemService((Class)ContextHubManager.class);
        this.mInitThread = new Thread("InitElmyraSensorPlugin") {
            @Override
            public void run() {
                final ElmyraSensorPlugin this$0 = ElmyraSensorPlugin.this;
                this$0.mNanoAppFound = this$0.findNanoApp();
            }
        };
    }
    
    @Override
    public void registerListener(final Sensor sensor, final SensorEventListener sensorEventListener) {
        if (sensor.getType() == 1) {
            this.mClients.add((Pair<Sensor, SensorEventListener>)new Pair((Object)sensor, (Object)sensorEventListener));
            this.updateChreListener();
        }
    }
    
    @Override
    public void unregisterListener(final Sensor obj, final SensorEventListener obj2) {
        if (obj.getType() == 1) {
            for (int i = this.mClients.size() - 1; i >= 0; --i) {
                final Pair<Sensor, SensorEventListener> pair = this.mClients.get(i);
                if (((Sensor)pair.first).equals(obj) && ((SensorEventListener)pair.second).equals(obj2)) {
                    this.mClients.remove(pair);
                }
            }
            this.updateChreListener();
        }
    }
}
