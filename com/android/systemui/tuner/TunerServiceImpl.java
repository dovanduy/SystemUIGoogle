// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.os.Looper;
import com.android.internal.util.ArrayUtils;
import android.content.Intent;
import android.provider.Settings$Global;
import android.text.TextUtils;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.database.ContentObserver;
import android.provider.Settings$Secure;
import java.util.Collection;
import android.util.ArraySet;
import java.util.Iterator;
import android.app.ActivityManager;
import android.content.pm.UserInfo;
import android.os.UserManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import com.android.systemui.settings.CurrentUserTracker;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import android.net.Uri;
import android.util.ArrayMap;
import com.android.systemui.util.leak.LeakDetector;
import android.content.Context;
import android.content.ContentResolver;

public class TunerServiceImpl extends TunerService
{
    private static final String[] RESET_BLACKLIST;
    private ContentResolver mContentResolver;
    private final Context mContext;
    private int mCurrentUser;
    private final LeakDetector mLeakDetector;
    private final ArrayMap<Uri, String> mListeningUris;
    private final Observer mObserver;
    private final ConcurrentHashMap<String, Set<Tunable>> mTunableLookup;
    private final HashSet<Tunable> mTunables;
    private CurrentUserTracker mUserTracker;
    
    static {
        RESET_BLACKLIST = new String[] { "sysui_qs_tiles", "doze_always_on" };
    }
    
    public TunerServiceImpl(final Context mContext, final Handler handler, final LeakDetector mLeakDetector, final BroadcastDispatcher broadcastDispatcher) {
        this.mObserver = new Observer();
        this.mListeningUris = (ArrayMap<Uri, String>)new ArrayMap();
        this.mTunableLookup = new ConcurrentHashMap<String, Set<Tunable>>();
        HashSet<Tunable> mTunables;
        if (LeakDetector.ENABLED) {
            mTunables = new HashSet<Tunable>();
        }
        else {
            mTunables = null;
        }
        this.mTunables = mTunables;
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
        this.mLeakDetector = mLeakDetector;
        final Iterator<UserInfo> iterator = (Iterator<UserInfo>)UserManager.get(this.mContext).getUsers().iterator();
        while (iterator.hasNext()) {
            this.mCurrentUser = iterator.next().getUserHandle().getIdentifier();
            if (this.getValue("sysui_tuner_version", 0) != 4) {
                this.upgradeTuner(this.getValue("sysui_tuner_version", 0), 4, handler);
            }
        }
        this.mCurrentUser = ActivityManager.getCurrentUser();
        (this.mUserTracker = new CurrentUserTracker(broadcastDispatcher) {
            @Override
            public void onUserSwitched(final int n) {
                TunerServiceImpl.this.mCurrentUser = n;
                TunerServiceImpl.this.reloadAll();
                TunerServiceImpl.this.reregisterAll();
            }
        }).startTracking();
    }
    
    private void addTunable(final Tunable e, final String key) {
        if (!this.mTunableLookup.containsKey(key)) {
            this.mTunableLookup.put(key, (Set<Tunable>)new ArraySet());
        }
        this.mTunableLookup.get(key).add(e);
        if (LeakDetector.ENABLED) {
            this.mTunables.add(e);
            this.mLeakDetector.trackCollection(this.mTunables, "TunerService.mTunables");
        }
        final Uri uri = Settings$Secure.getUriFor(key);
        if (!this.mListeningUris.containsKey((Object)uri)) {
            this.mListeningUris.put((Object)uri, (Object)key);
            this.mContentResolver.registerContentObserver(uri, false, (ContentObserver)this.mObserver, this.mCurrentUser);
        }
        e.onTuningChanged(key, DejankUtils.whitelistIpcs((Supplier<String>)new _$$Lambda$TunerServiceImpl$rxKdnA_ESs9ir91j7kkfizLlu6E(this, key)));
    }
    
    private void reloadAll() {
        for (final String key : this.mTunableLookup.keySet()) {
            final String stringForUser = Settings$Secure.getStringForUser(this.mContentResolver, key, this.mCurrentUser);
            final Iterator<Tunable> iterator2 = this.mTunableLookup.get(key).iterator();
            while (iterator2.hasNext()) {
                iterator2.next().onTuningChanged(key, stringForUser);
            }
        }
    }
    
    private void reloadSetting(final Uri uri) {
        final String key = (String)this.mListeningUris.get((Object)uri);
        final Set<Tunable> set = this.mTunableLookup.get(key);
        if (set == null) {
            return;
        }
        final String stringForUser = Settings$Secure.getStringForUser(this.mContentResolver, key, this.mCurrentUser);
        final Iterator<Tunable> iterator = set.iterator();
        while (iterator.hasNext()) {
            iterator.next().onTuningChanged(key, stringForUser);
        }
    }
    
    private void upgradeTuner(final int n, final int n2, final Handler handler) {
        if (n < 1) {
            final String value = this.getValue("icon_blacklist");
            if (value != null) {
                final ArraySet<String> iconBlacklist = StatusBarIconController.getIconBlacklist(this.mContext, value);
                iconBlacklist.add((Object)"rotate");
                iconBlacklist.add((Object)"headset");
                Settings$Secure.putStringForUser(this.mContentResolver, "icon_blacklist", TextUtils.join((CharSequence)",", (Iterable)iconBlacklist), this.mCurrentUser);
            }
        }
        if (n < 2) {
            TunerService.setTunerEnabled(this.mContext, false);
        }
        if (n < 4) {
            handler.postDelayed((Runnable)new _$$Lambda$TunerServiceImpl$SZ83wU1GcnmYXjZCH1hfw7pCVvY(this, this.mCurrentUser), 5000L);
        }
        this.setValue("sysui_tuner_version", n2);
    }
    
    @Override
    public void addTunable(final Tunable tunable, final String... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            this.addTunable(tunable, array[i]);
        }
    }
    
    @Override
    public void clearAll() {
        this.clearAllFromUser(this.mCurrentUser);
    }
    
    public void clearAllFromUser(final int n) {
        Settings$Global.putString(this.mContentResolver, "sysui_demo_allowed", (String)null);
        final Intent intent = new Intent("com.android.systemui.demo");
        intent.putExtra("command", "exit");
        this.mContext.sendBroadcast(intent);
        for (final String s : this.mTunableLookup.keySet()) {
            if (ArrayUtils.contains((Object[])TunerServiceImpl.RESET_BLACKLIST, (Object)s)) {
                continue;
            }
            Settings$Secure.putStringForUser(this.mContentResolver, s, (String)null, n);
        }
    }
    
    @Override
    public int getValue(final String s, final int n) {
        return Settings$Secure.getIntForUser(this.mContentResolver, s, n, this.mCurrentUser);
    }
    
    @Override
    public String getValue(final String s) {
        return Settings$Secure.getStringForUser(this.mContentResolver, s, this.mCurrentUser);
    }
    
    @Override
    public void removeTunable(final Tunable o) {
        final Iterator<Set<Tunable>> iterator = this.mTunableLookup.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove(o);
        }
        if (LeakDetector.ENABLED) {
            this.mTunables.remove(o);
        }
    }
    
    protected void reregisterAll() {
        if (this.mListeningUris.size() == 0) {
            return;
        }
        this.mContentResolver.unregisterContentObserver((ContentObserver)this.mObserver);
        final Iterator<Uri> iterator = this.mListeningUris.keySet().iterator();
        while (iterator.hasNext()) {
            this.mContentResolver.registerContentObserver((Uri)iterator.next(), false, (ContentObserver)this.mObserver, this.mCurrentUser);
        }
    }
    
    @Override
    public void setValue(final String s, final int n) {
        Settings$Secure.putIntForUser(this.mContentResolver, s, n, this.mCurrentUser);
    }
    
    @Override
    public void setValue(final String s, final String s2) {
        Settings$Secure.putStringForUser(this.mContentResolver, s, s2, this.mCurrentUser);
    }
    
    private class Observer extends ContentObserver
    {
        public Observer() {
            super(new Handler(Looper.getMainLooper()));
        }
        
        public void onChange(final boolean b, final Collection<Uri> collection, final int n, final int n2) {
            if (n2 == ActivityManager.getCurrentUser()) {
                final Iterator<Uri> iterator = collection.iterator();
                while (iterator.hasNext()) {
                    TunerServiceImpl.this.reloadSetting(iterator.next());
                }
            }
        }
    }
}
