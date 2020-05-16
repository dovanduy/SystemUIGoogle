// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import java.util.HashMap;
import java.util.Set;
import android.os.UserManager;
import com.android.systemui.plugins.qs.QSTileView;
import java.util.function.Consumer;
import android.service.quicksettings.IQSService;
import com.android.systemui.qs.external.TileLifecycleManager;
import android.os.UserHandle;
import android.content.Intent;
import android.service.quicksettings.Tile;
import com.android.systemui.qs.external.CustomTile;
import android.content.ComponentName;
import java.util.Iterator;
import android.util.ArraySet;
import java.util.Map;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.res.Resources;
import android.os.Build;
import java.util.Collection;
import java.util.Arrays;
import com.android.systemui.R$string;
import android.text.TextUtils;
import android.provider.Settings$Secure;
import android.app.ActivityManager;
import java.util.function.Predicate;
import com.android.systemui.plugins.Plugin;
import javax.inject.Provider;
import com.android.systemui.shared.plugins.PluginManager;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import com.android.systemui.plugins.qs.QSTile;
import java.util.LinkedHashMap;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Optional;
import com.android.systemui.qs.external.TileServices;
import java.util.ArrayList;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import java.util.List;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.qs.QSFactory;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.tuner.TunerService;

public class QSTileHost implements QSHost, Tunable, PluginListener<QSFactory>, Dumpable
{
    private static final boolean DEBUG;
    private AutoTileManager mAutoTiles;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final List<Callback> mCallbacks;
    private final Context mContext;
    private int mCurrentUser;
    private final DumpManager mDumpManager;
    private final StatusBarIconController mIconController;
    private final QSLogger mQSLogger;
    private final ArrayList<QSFactory> mQsFactories;
    private final TileServices mServices;
    private final Optional<StatusBar> mStatusBarOptional;
    protected final ArrayList<String> mTileSpecs;
    private final LinkedHashMap<String, QSTile> mTiles;
    private final TunerService mTunerService;
    private Context mUserContext;
    
    static {
        DEBUG = Log.isLoggable("QSTileHost", 3);
    }
    
    public QSTileHost(final Context context, final StatusBarIconController mIconController, final QSFactory e, final Handler handler, final Looper looper, final PluginManager pluginManager, final TunerService mTunerService, final Provider<AutoTileManager> provider, final DumpManager mDumpManager, final BroadcastDispatcher mBroadcastDispatcher, final Optional<StatusBar> mStatusBarOptional, final QSLogger mqsLogger) {
        this.mTiles = new LinkedHashMap<String, QSTile>();
        this.mTileSpecs = new ArrayList<String>();
        this.mCallbacks = new ArrayList<Callback>();
        this.mQsFactories = new ArrayList<QSFactory>();
        this.mIconController = mIconController;
        this.mContext = context;
        this.mUserContext = context;
        this.mTunerService = mTunerService;
        this.mDumpManager = mDumpManager;
        this.mQSLogger = mqsLogger;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mServices = new TileServices(this, looper, this.mBroadcastDispatcher);
        this.mStatusBarOptional = mStatusBarOptional;
        this.mQsFactories.add(e);
        pluginManager.addPluginListener((PluginListener<Plugin>)this, QSFactory.class, true);
        this.mDumpManager.registerDumpable("QSTileHost", this);
        handler.post((Runnable)new _$$Lambda$QSTileHost$8OyZkY1GXlSGEY9CusSz83dAxOw(this, mTunerService, provider));
    }
    
    private void changeTileSpecs(final Predicate<List<String>> predicate) {
        final List<String> loadTileSpecs = loadTileSpecs(this.mContext, Settings$Secure.getStringForUser(this.mContext.getContentResolver(), "sysui_qs_tiles", ActivityManager.getCurrentUser()));
        if (predicate.test(loadTileSpecs)) {
            Settings$Secure.putStringForUser(this.mContext.getContentResolver(), "sysui_qs_tiles", TextUtils.join((CharSequence)",", (Iterable)loadTileSpecs), ActivityManager.getCurrentUser());
        }
    }
    
    public static List<String> getDefaultSpecs(final Context context) {
        final ArrayList<String> list = new ArrayList<String>();
        final Resources resources = context.getResources();
        final String string = resources.getString(R$string.quick_settings_tiles_default);
        final String string2 = resources.getString(17039870);
        list.addAll(Arrays.asList(string.split(",")));
        list.addAll(Arrays.asList(string2.split(",")));
        if (Build.IS_DEBUGGABLE) {
            list.add("dbg:mem");
        }
        return list;
    }
    
    protected static List<String> loadTileSpecs(final Context context, String s) {
        final Resources resources = context.getResources();
        String string;
        if (TextUtils.isEmpty((CharSequence)s)) {
            s = (string = resources.getString(R$string.quick_settings_tiles));
            if (QSTileHost.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Loaded tile specs from config: ");
                sb.append(s);
                Log.d("QSTileHost", sb.toString());
                string = s;
            }
        }
        else {
            string = s;
            if (QSTileHost.DEBUG) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Loaded tile specs from setting: ");
                sb2.append(s);
                Log.d("QSTileHost", sb2.toString());
                string = s;
            }
        }
        final ArrayList<String> list = new ArrayList<String>();
        final ArraySet set = new ArraySet();
        final String[] split = string.split(",");
        final int length = split.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            final String trim = split[i].trim();
            int n2;
            if (trim.isEmpty()) {
                n2 = n;
            }
            else if (trim.equals("default")) {
                if ((n2 = n) == 0) {
                    for (final String e : getDefaultSpecs(context)) {
                        if (!((Set)set).contains(e)) {
                            list.add(e);
                            ((Set<String>)set).add(e);
                        }
                    }
                    n2 = 1;
                }
            }
            else {
                n2 = n;
                if (!((Set)set).contains(trim)) {
                    list.add(trim);
                    ((Set<String>)set).add(trim);
                    n2 = n;
                }
            }
            ++i;
            n = n2;
        }
        return list;
    }
    
    public void addCallback(final Callback callback) {
        this.mCallbacks.add(callback);
    }
    
    public void addTile(final ComponentName componentName) {
        final String spec = CustomTile.toSpec(componentName);
        if (!this.mTileSpecs.contains(spec)) {
            final ArrayList<String> list = new ArrayList<String>(this.mTileSpecs);
            list.add(0, spec);
            this.changeTiles(this.mTileSpecs, list);
        }
    }
    
    public void addTile(final String s) {
        this.changeTileSpecs(new _$$Lambda$QSTileHost$iiTl64od8Xx0qaz8exmdhzyHaWg(s));
    }
    
    public void changeTiles(final List<String> c, final List<String> obj) {
        final ArrayList<Object> list = new ArrayList<Object>(c);
        for (int size = list.size(), i = 0; i < size; ++i) {
            final String s = list.get(i);
            if (s.startsWith("custom(")) {
                if (!obj.contains(s)) {
                    final ComponentName componentFromSpec = CustomTile.getComponentFromSpec(s);
                    final TileLifecycleManager tileLifecycleManager = new TileLifecycleManager(new Handler(), this.mContext, (IQSService)this.mServices, new Tile(), new Intent().setComponent(componentFromSpec), new UserHandle(ActivityManager.getCurrentUser()), this.mBroadcastDispatcher);
                    tileLifecycleManager.onStopListening();
                    tileLifecycleManager.onTileRemoved();
                    TileLifecycleManager.setTileAdded(this.mContext, componentFromSpec, false);
                    tileLifecycleManager.flushMessagesAndUnbind();
                }
            }
        }
        if (QSTileHost.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("saveCurrentTiles ");
            sb.append(obj);
            Log.d("QSTileHost", sb.toString());
        }
        Settings$Secure.putStringForUser(this.getContext().getContentResolver(), "sysui_qs_tiles", TextUtils.join((CharSequence)",", (Iterable)obj), ActivityManager.getCurrentUser());
    }
    
    @Override
    public void collapsePanels() {
        this.mStatusBarOptional.ifPresent((Consumer<? super StatusBar>)_$$Lambda$4RRpk2g2DG1jxcebU4uq2xyjwbI.INSTANCE);
    }
    
    public QSTile createTile(final String s) {
        for (int i = 0; i < this.mQsFactories.size(); ++i) {
            final QSTile tile = this.mQsFactories.get(i).createTile(s);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }
    
    public QSTileView createTileView(final QSTile qsTile, final boolean b) {
        for (int i = 0; i < this.mQsFactories.size(); ++i) {
            final QSTileView tileView = this.mQsFactories.get(i).createTileView(qsTile, b);
            if (tileView != null) {
                return tileView;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Default factory didn't create view for ");
        sb.append(qsTile.getTileSpec());
        throw new RuntimeException(sb.toString());
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("QSTileHost:");
        this.mTiles.values().stream().filter((Predicate<? super QSTile>)_$$Lambda$QSTileHost$w0YHlhMwIm7qnoeEO7kRZCq47o8.INSTANCE).forEach(new _$$Lambda$QSTileHost$8dGA3dPDXgH8k_YhV5jUASLKyAo(fileDescriptor, printWriter, array));
    }
    
    public void forceCollapsePanels() {
        this.mStatusBarOptional.ifPresent((Consumer<? super StatusBar>)_$$Lambda$mg7HvLF2bK_625f51dPB__SLbws.INSTANCE);
    }
    
    @Override
    public Context getContext() {
        return this.mContext;
    }
    
    public StatusBarIconController getIconController() {
        return this.mIconController;
    }
    
    @Override
    public QSLogger getQSLogger() {
        return this.mQSLogger;
    }
    
    @Override
    public TileServices getTileServices() {
        return this.mServices;
    }
    
    public Collection<QSTile> getTiles() {
        return this.mTiles.values();
    }
    
    @Override
    public Context getUserContext() {
        return this.mUserContext;
    }
    
    @Override
    public int indexOf(final String o) {
        return this.mTileSpecs.indexOf(o);
    }
    
    @Override
    public void onPluginConnected(final QSFactory element, final Context context) {
        this.mQsFactories.add(0, element);
        final String value = this.mTunerService.getValue("sysui_qs_tiles");
        this.onTuningChanged("sysui_qs_tiles", "");
        this.onTuningChanged("sysui_qs_tiles", value);
    }
    
    @Override
    public void onPluginDisconnected(final QSFactory o) {
        this.mQsFactories.remove(o);
        final String value = this.mTunerService.getValue("sysui_qs_tiles");
        this.onTuningChanged("sysui_qs_tiles", "");
        this.onTuningChanged("sysui_qs_tiles", value);
    }
    
    @Override
    public void onTuningChanged(String string, String loadTileSpecs) {
        if (!"sysui_qs_tiles".equals(string)) {
            return;
        }
        Log.d("QSTileHost", "Recreating tiles");
        if ((string = loadTileSpecs) == null) {
            string = loadTileSpecs;
            if (UserManager.isDeviceInDemoMode(this.mContext)) {
                string = this.mContext.getResources().getString(R$string.quick_settings_tiles_retail_mode);
            }
        }
        loadTileSpecs = (String)loadTileSpecs(this.mContext, string);
        final int currentUser = ActivityManager.getCurrentUser();
        final int mCurrentUser = this.mCurrentUser;
        final int n = 0;
        if (currentUser != mCurrentUser) {
            this.mUserContext = this.mContext.createContextAsUser(UserHandle.of(currentUser), 0);
        }
        if (((List)loadTileSpecs).equals(this.mTileSpecs) && currentUser == this.mCurrentUser) {
            return;
        }
        this.mTiles.entrySet().stream().filter(new _$$Lambda$QSTileHost$tL3GWCpuev_DvXg1noj_yj7fk3Y((List)loadTileSpecs)).forEach(new _$$Lambda$QSTileHost$nV3a9GzHlwmibkt4wOBaCI5DZk8(this));
        string = (String)new LinkedHashMap();
        for (final String str : loadTileSpecs) {
            final QSTile qsTile = this.mTiles.get(str);
            if (qsTile != null) {
                final boolean b = qsTile instanceof CustomTile;
                if (!b || ((CustomTile)qsTile).getUser() == currentUser) {
                    if (qsTile.isAvailable()) {
                        if (QSTileHost.DEBUG) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Adding ");
                            sb.append(qsTile);
                            Log.d("QSTileHost", sb.toString());
                        }
                        qsTile.removeCallbacks();
                        if (!b && this.mCurrentUser != currentUser) {
                            qsTile.userSwitch(currentUser);
                        }
                        ((HashMap<String, CustomTile>)string).put(str, (CustomTile)qsTile);
                        this.mQSLogger.logTileAdded(str);
                        continue;
                    }
                    qsTile.destroy();
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Destroying not available tile: ");
                    sb2.append(str);
                    Log.d("QSTileHost", sb2.toString());
                    this.mQSLogger.logTileDestroyed(str, "Tile not available");
                    continue;
                }
            }
            if (qsTile != null) {
                qsTile.destroy();
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Destroying tile for wrong user: ");
                sb3.append(str);
                Log.d("QSTileHost", sb3.toString());
                this.mQSLogger.logTileDestroyed(str, "Tile for wrong user");
            }
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("Creating tile: ");
            sb4.append(str);
            Log.d("QSTileHost", sb4.toString());
            try {
                final QSTile tile = this.createTile(str);
                if (tile == null) {
                    continue;
                }
                tile.setTileSpec(str);
                if (tile.isAvailable()) {
                    ((HashMap<String, QSTile>)string).put(str, tile);
                    this.mQSLogger.logTileAdded(str);
                }
                else {
                    tile.destroy();
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append("Destroying not available tile: ");
                    sb5.append(str);
                    Log.d("QSTileHost", sb5.toString());
                    this.mQSLogger.logTileDestroyed(str, "Tile not available");
                }
            }
            finally {
                final StringBuilder sb6 = new StringBuilder();
                sb6.append("Error creating tile for spec: ");
                sb6.append(str);
                final Throwable t;
                Log.w("QSTileHost", sb6.toString(), t);
            }
        }
        this.mCurrentUser = currentUser;
        final ArrayList<String> list = new ArrayList<String>(this.mTileSpecs);
        this.mTileSpecs.clear();
        this.mTileSpecs.addAll((Collection<? extends String>)loadTileSpecs);
        this.mTiles.clear();
        this.mTiles.putAll((Map<?, ?>)string);
        int i = n;
        if (((HashMap)string).isEmpty()) {
            i = n;
            if (!((List)loadTileSpecs).isEmpty()) {
                Log.d("QSTileHost", "No valid tiles on tuning changed. Setting to default.");
                this.changeTiles(list, loadTileSpecs(this.mContext, ""));
                return;
            }
        }
        while (i < this.mCallbacks.size()) {
            this.mCallbacks.get(i).onTilesChanged();
            ++i;
        }
    }
    
    @Override
    public void openPanels() {
        this.mStatusBarOptional.ifPresent((Consumer<? super StatusBar>)_$$Lambda$dlfb7Xnz27iJwNxSQU2fGCzuI2E.INSTANCE);
    }
    
    public void removeCallback(final Callback callback) {
        this.mCallbacks.remove(callback);
    }
    
    public void removeTile(final ComponentName componentName) {
        final ArrayList<String> list = new ArrayList<String>(this.mTileSpecs);
        list.remove(CustomTile.toSpec(componentName));
        this.changeTiles(this.mTileSpecs, list);
    }
    
    @Override
    public void removeTile(final String s) {
        this.changeTileSpecs(new _$$Lambda$QSTileHost$lvnGvThFo7_HeGkbFqhwU9KCtaQ(s));
    }
    
    @Override
    public void unmarkTileAsAutoAdded(final String s) {
        final AutoTileManager mAutoTiles = this.mAutoTiles;
        if (mAutoTiles != null) {
            mAutoTiles.unmarkTileAsAutoAdded(s);
        }
    }
    
    @Override
    public void warn(final String s, final Throwable t) {
    }
}
