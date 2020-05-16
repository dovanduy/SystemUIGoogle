// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import android.graphics.Bitmap;
import java.util.function.BiConsumer;
import android.provider.Settings$Secure;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.util.DisplayMetrics;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.util.ArrayMap;
import java.util.Objects;
import android.net.Uri;
import java.util.Collection;
import android.os.Looper;
import java.util.ArrayList;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.util.InjectionInflationController;
import android.content.Context;
import com.android.systemui.shared.plugins.PluginManager;
import android.os.Handler;
import java.util.Map;
import com.android.systemui.dock.DockManager;
import androidx.lifecycle.Observer;
import com.android.systemui.settings.CurrentUserObservable;
import android.content.ContentResolver;
import android.database.ContentObserver;
import com.android.systemui.plugins.ClockPlugin;
import java.util.function.Supplier;
import java.util.List;

public final class ClockManager
{
    private final List<Supplier<ClockPlugin>> mBuiltinClocks;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final CurrentUserObservable mCurrentUserObservable;
    private final Observer<Integer> mCurrentUserObserver;
    private final DockManager.DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    private final int mHeight;
    private boolean mIsDocked;
    private final Map<ClockChangedListener, AvailableClocks> mListeners;
    private final Handler mMainHandler;
    private final PluginManager mPluginManager;
    private final AvailableClocks mPreviewClocks;
    private final SettingsWrapper mSettingsWrapper;
    private final int mWidth;
    
    ClockManager(final Context context, final InjectionInflationController injectionInflationController, final PluginManager mPluginManager, final SysuiColorExtractor sysuiColorExtractor, final ContentResolver mContentResolver, final CurrentUserObservable mCurrentUserObservable, final SettingsWrapper mSettingsWrapper, final DockManager mDockManager) {
        this.mBuiltinClocks = new ArrayList<Supplier<ClockPlugin>>();
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mContentObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(final boolean b, final Collection<Uri> collection, final int n, final int i) {
                if (Objects.equals(i, ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue())) {
                    ClockManager.this.reload();
                }
            }
        };
        this.mCurrentUserObserver = (Observer<Integer>)new _$$Lambda$ClockManager$hg7TNpAa_jeQQKjwxI39ao59w9U(this);
        this.mDockEventListener = new DockManager.DockEventListener() {
            @Override
            public void onEvent(final int n) {
                final ClockManager this$0 = ClockManager.this;
                boolean b = true;
                if (n != 1) {
                    b = (n == 2 && b);
                }
                this$0.mIsDocked = b;
                ClockManager.this.reload();
            }
        };
        this.mListeners = (Map<ClockChangedListener, AvailableClocks>)new ArrayMap();
        this.mPluginManager = mPluginManager;
        this.mContentResolver = mContentResolver;
        this.mSettingsWrapper = mSettingsWrapper;
        this.mCurrentUserObservable = mCurrentUserObservable;
        this.mDockManager = mDockManager;
        this.mPreviewClocks = new AvailableClocks();
        final Resources resources = context.getResources();
        this.addBuiltinClock(new _$$Lambda$ClockManager$qcpjSm9nfcenHjNSU7lKV_TGsX4(resources, injectionInflationController.injectable(LayoutInflater.from(context)), sysuiColorExtractor));
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        this.mWidth = displayMetrics.widthPixels;
        this.mHeight = displayMetrics.heightPixels;
    }
    
    public ClockManager(final Context context, final InjectionInflationController injectionInflationController, final PluginManager pluginManager, final SysuiColorExtractor sysuiColorExtractor, final DockManager dockManager, final BroadcastDispatcher broadcastDispatcher) {
        this(context, injectionInflationController, pluginManager, sysuiColorExtractor, context.getContentResolver(), new CurrentUserObservable(broadcastDispatcher), new SettingsWrapper(context.getContentResolver()), dockManager);
    }
    
    private void register() {
        this.mPluginManager.addPluginListener((PluginListener<Plugin>)this.mPreviewClocks, ClockPlugin.class, true);
        this.mContentResolver.registerContentObserver(Settings$Secure.getUriFor("lock_screen_custom_clock_face"), false, this.mContentObserver, -1);
        this.mContentResolver.registerContentObserver(Settings$Secure.getUriFor("docked_clock_face"), false, this.mContentObserver, -1);
        this.mCurrentUserObservable.getCurrentUser().observeForever(this.mCurrentUserObserver);
        final DockManager mDockManager = this.mDockManager;
        if (mDockManager != null) {
            mDockManager.addListener(this.mDockEventListener);
        }
    }
    
    private void reload() {
        this.mPreviewClocks.reloadCurrentClock();
        this.mListeners.forEach(new _$$Lambda$ClockManager$qgNV_V_ndKBDwD0H6bkgmOPGFf8(this));
    }
    
    private void unregister() {
        this.mPluginManager.removePluginListener(this.mPreviewClocks);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
        this.mCurrentUserObservable.getCurrentUser().removeObserver(this.mCurrentUserObserver);
        final DockManager mDockManager = this.mDockManager;
        if (mDockManager != null) {
            mDockManager.removeListener(this.mDockEventListener);
        }
    }
    
    void addBuiltinClock(final Supplier<ClockPlugin> supplier) {
        this.mPreviewClocks.addClockPlugin(supplier.get());
        this.mBuiltinClocks.add(supplier);
    }
    
    public void addOnClockChangedListener(final ClockChangedListener clockChangedListener) {
        if (this.mListeners.isEmpty()) {
            this.register();
        }
        final AvailableClocks availableClocks = new AvailableClocks();
        for (int i = 0; i < this.mBuiltinClocks.size(); ++i) {
            availableClocks.addClockPlugin(this.mBuiltinClocks.get(i).get());
        }
        this.mListeners.put(clockChangedListener, availableClocks);
        this.mPluginManager.addPluginListener((PluginListener<Plugin>)availableClocks, ClockPlugin.class, true);
        this.reload();
    }
    
    List<ClockInfo> getClockInfos() {
        return this.mPreviewClocks.getInfo();
    }
    
    ContentObserver getContentObserver() {
        return this.mContentObserver;
    }
    
    boolean isDocked() {
        return this.mIsDocked;
    }
    
    public void removeOnClockChangedListener(final ClockChangedListener clockChangedListener) {
        this.mPluginManager.removePluginListener(this.mListeners.remove(clockChangedListener));
        if (this.mListeners.isEmpty()) {
            this.unregister();
        }
    }
    
    private final class AvailableClocks implements PluginListener<ClockPlugin>
    {
        private final List<ClockInfo> mClockInfo;
        private final Map<String, ClockPlugin> mClocks;
        private ClockPlugin mCurrentClock;
        
        private AvailableClocks() {
            this.mClocks = (Map<String, ClockPlugin>)new ArrayMap();
            this.mClockInfo = new ArrayList<ClockInfo>();
        }
        
        private ClockPlugin getClockPlugin() {
            ClockPlugin clockPlugin2 = null;
            Label_0068: {
                if (ClockManager.this.isDocked()) {
                    final String dockedClockFace = ClockManager.this.mSettingsWrapper.getDockedClockFace(ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue());
                    if (dockedClockFace != null) {
                        final ClockPlugin clockPlugin = this.mClocks.get(dockedClockFace);
                        if ((clockPlugin2 = clockPlugin) != null) {
                            return clockPlugin;
                        }
                        break Label_0068;
                    }
                }
                clockPlugin2 = null;
            }
            final String lockScreenCustomClockFace = ClockManager.this.mSettingsWrapper.getLockScreenCustomClockFace(ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue());
            if (lockScreenCustomClockFace != null) {
                clockPlugin2 = this.mClocks.get(lockScreenCustomClockFace);
            }
            return clockPlugin2;
        }
        
        private void reloadIfNeeded(final ClockPlugin clockPlugin) {
            final ClockPlugin mCurrentClock = this.mCurrentClock;
            boolean b = true;
            final boolean b2 = clockPlugin == mCurrentClock;
            this.reloadCurrentClock();
            if (clockPlugin != this.mCurrentClock) {
                b = false;
            }
            if (b2 || b) {
                ClockManager.this.reload();
            }
        }
        
        private void removeClockPlugin(final ClockPlugin clockPlugin) {
            final String name = clockPlugin.getClass().getName();
            this.mClocks.remove(name);
            for (int i = 0; i < this.mClockInfo.size(); ++i) {
                if (name.equals(this.mClockInfo.get(i).getId())) {
                    this.mClockInfo.remove(i);
                    break;
                }
            }
        }
        
        void addClockPlugin(final ClockPlugin clockPlugin) {
            final String name = clockPlugin.getClass().getName();
            this.mClocks.put(clockPlugin.getClass().getName(), clockPlugin);
            final List<ClockInfo> mClockInfo = this.mClockInfo;
            final ClockInfo.Builder builder = ClockInfo.builder();
            builder.setName(clockPlugin.getName());
            Objects.requireNonNull(clockPlugin);
            builder.setTitle(new _$$Lambda$NtEGOukxaFxn97YVYx86DAEBmms(clockPlugin));
            builder.setId(name);
            Objects.requireNonNull(clockPlugin);
            builder.setThumbnail(new _$$Lambda$d3U4w_CuqsezzeLGogc1fLHnUj0(clockPlugin));
            builder.setPreview(new _$$Lambda$ClockManager$AvailableClocks$3xFQeynnnUMh38fqZ7v9xTaqzmA(this, clockPlugin));
            mClockInfo.add(builder.build());
        }
        
        ClockPlugin getCurrentClock() {
            return this.mCurrentClock;
        }
        
        List<ClockInfo> getInfo() {
            return this.mClockInfo;
        }
        
        @Override
        public void onPluginConnected(final ClockPlugin clockPlugin, final Context context) {
            this.addClockPlugin(clockPlugin);
            this.reloadIfNeeded(clockPlugin);
        }
        
        @Override
        public void onPluginDisconnected(final ClockPlugin clockPlugin) {
            this.removeClockPlugin(clockPlugin);
            this.reloadIfNeeded(clockPlugin);
        }
        
        void reloadCurrentClock() {
            this.mCurrentClock = this.getClockPlugin();
        }
    }
    
    public interface ClockChangedListener
    {
        void onClockChanged(final ClockPlugin p0);
    }
}
