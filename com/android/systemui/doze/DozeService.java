// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.os.SystemClock;
import android.os.PowerManager;
import android.content.Context;
import com.android.systemui.plugins.Plugin;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.Log;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.DozeServicePlugin;
import android.service.dreams.DreamService;

public class DozeService extends DreamService implements Service, RequestDoze, PluginListener<DozeServicePlugin>
{
    static final boolean DEBUG;
    private final DozeFactory mDozeFactory;
    private DozeMachine mDozeMachine;
    private DozeServicePlugin mDozePlugin;
    private PluginManager mPluginManager;
    
    static {
        DEBUG = Log.isLoggable("DozeService", 3);
    }
    
    public DozeService(final DozeFactory mDozeFactory, final PluginManager mPluginManager) {
        this.setDebug(DozeService.DEBUG);
        this.mDozeFactory = mDozeFactory;
        this.mPluginManager = mPluginManager;
    }
    
    protected void dumpOnHandler(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        super.dumpOnHandler(fileDescriptor, printWriter, array);
        final DozeMachine mDozeMachine = this.mDozeMachine;
        if (mDozeMachine != null) {
            mDozeMachine.dump(printWriter);
        }
    }
    
    public void onCreate() {
        super.onCreate();
        this.setWindowless(true);
        this.mPluginManager.addPluginListener((PluginListener<Plugin>)this, DozeServicePlugin.class, false);
        this.mDozeMachine = this.mDozeFactory.assembleMachine(this);
    }
    
    public void onDestroy() {
        final PluginManager mPluginManager = this.mPluginManager;
        if (mPluginManager != null) {
            mPluginManager.removePluginListener(this);
        }
        super.onDestroy();
        this.mDozeMachine = null;
    }
    
    public void onDreamingStarted() {
        super.onDreamingStarted();
        this.mDozeMachine.requestState(State.INITIALIZED);
        this.startDozing();
        final DozeServicePlugin mDozePlugin = this.mDozePlugin;
        if (mDozePlugin != null) {
            mDozePlugin.onDreamingStarted();
        }
    }
    
    public void onDreamingStopped() {
        super.onDreamingStopped();
        this.mDozeMachine.requestState(State.FINISH);
        final DozeServicePlugin mDozePlugin = this.mDozePlugin;
        if (mDozePlugin != null) {
            mDozePlugin.onDreamingStopped();
        }
    }
    
    public void onPluginConnected(final DozeServicePlugin mDozePlugin, final Context context) {
        (this.mDozePlugin = mDozePlugin).setDozeRequester((DozeServicePlugin.RequestDoze)this);
    }
    
    public void onPluginDisconnected(DozeServicePlugin mDozePlugin) {
        mDozePlugin = this.mDozePlugin;
        if (mDozePlugin != null) {
            mDozePlugin.onDreamingStopped();
            this.mDozePlugin = null;
        }
    }
    
    public void onRequestHideDoze() {
        final DozeMachine mDozeMachine = this.mDozeMachine;
        if (mDozeMachine != null) {
            mDozeMachine.requestState(State.DOZE);
        }
    }
    
    public void onRequestShowDoze() {
        final DozeMachine mDozeMachine = this.mDozeMachine;
        if (mDozeMachine != null) {
            mDozeMachine.requestState(State.DOZE_AOD);
        }
    }
    
    public void requestWakeUp() {
        ((PowerManager)this.getSystemService((Class)PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:NODOZE");
    }
}
