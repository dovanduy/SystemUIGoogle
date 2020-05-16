// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.applications;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import java.util.Collections;
import android.os.Process;
import androidx.lifecycle.LifecycleObserver;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.content.Intent;
import android.os.SystemClock;
import android.app.usage.StorageStats;
import java.io.IOException;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.Message;
import android.content.pm.IPackageStatsObserver$Stub;
import android.os.Handler;
import com.android.settingslib.Utils;
import android.graphics.drawable.Drawable;
import java.io.File;
import android.util.Log;
import java.util.Collection;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.text.format.Formatter;
import java.util.HashSet;
import com.android.internal.util.ArrayUtils;
import android.content.pm.PackageStats;
import java.util.Iterator;
import android.content.pm.ModuleInfo;
import android.os.UserHandle;
import android.util.IconDrawableFactory;
import android.os.Looper;
import android.app.Application;
import java.text.Collator;
import java.util.regex.Pattern;
import android.os.UserManager;
import android.os.HandlerThread;
import android.app.usage.StorageStatsManager;
import android.content.pm.PackageManager;
import android.content.pm.IPackageManager;
import java.util.HashMap;
import android.util.SparseArray;
import java.util.UUID;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.util.List;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;

public class ApplicationsState
{
    public static final Comparator<AppEntry> ALPHA_COMPARATOR;
    public static final AppFilter FILTER_AUDIO;
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER;
    public static final AppFilter FILTER_GAMES;
    public static final AppFilter FILTER_MOVIES;
    public static final AppFilter FILTER_PHOTOS;
    static ApplicationsState sInstance;
    private static final Object sLock;
    final ArrayList<WeakReference<Session>> mActiveSessions;
    final int mAdminRetrieveFlags;
    final ArrayList<AppEntry> mAppEntries;
    List<ApplicationInfo> mApplications;
    final BackgroundHandler mBackgroundHandler;
    final Context mContext;
    String mCurComputingSizePkg;
    int mCurComputingSizeUserId;
    UUID mCurComputingSizeUuid;
    long mCurId;
    final SparseArray<HashMap<String, AppEntry>> mEntriesMap;
    boolean mHaveInstantApps;
    private InterestingConfigChanges mInterestingConfigChanges;
    final IPackageManager mIpm;
    final MainHandler mMainHandler;
    PackageIntentReceiver mPackageIntentReceiver;
    final PackageManager mPm;
    final ArrayList<Session> mRebuildingSessions;
    boolean mResumed;
    final int mRetrieveFlags;
    final ArrayList<Session> mSessions;
    boolean mSessionsChanged;
    final StorageStatsManager mStats;
    final HashMap<String, Boolean> mSystemModules;
    final HandlerThread mThread;
    final UserManager mUm;
    
    static {
        sLock = new Object();
        Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        ALPHA_COMPARATOR = new Comparator<AppEntry>() {
            private final Collator sCollator = Collator.getInstance();
            
            @Override
            public int compare(final AppEntry appEntry, final AppEntry appEntry2) {
                final int compare = this.sCollator.compare(appEntry.label, appEntry2.label);
                if (compare != 0) {
                    return compare;
                }
                final ApplicationInfo info = appEntry.info;
                if (info != null) {
                    final ApplicationInfo info2 = appEntry2.info;
                    if (info2 != null) {
                        final int compare2 = this.sCollator.compare(info.packageName, info2.packageName);
                        if (compare2 != 0) {
                            return compare2;
                        }
                    }
                }
                return appEntry.info.uid - appEntry2.info.uid;
            }
        };
        FILTER_DOWNLOADED_AND_LAUNCHER = (AppFilter)new AppFilter() {
            @Override
            public boolean filterApp(final AppEntry appEntry) {
                return !AppUtils.isInstant(appEntry.info) && (hasFlag(appEntry.info.flags, 128) || !hasFlag(appEntry.info.flags, 1) || appEntry.hasLauncherEntry || (hasFlag(appEntry.info.flags, 1) && appEntry.isHomeApp));
            }
            
            @Override
            public void init() {
            }
        };
        FILTER_GAMES = (AppFilter)new AppFilter() {
            @Override
            public boolean filterApp(final AppEntry appEntry) {
                synchronized (appEntry.info) {
                    return hasFlag(appEntry.info.flags, 33554432) || appEntry.info.category == 0;
                }
            }
            
            @Override
            public void init() {
            }
        };
        FILTER_AUDIO = (AppFilter)new AppFilter() {
            @Override
            public boolean filterApp(final AppEntry appEntry) {
                synchronized (appEntry) {
                    final int category = appEntry.info.category;
                    boolean b = true;
                    if (category != 1) {
                        b = false;
                    }
                    return b;
                }
            }
            
            @Override
            public void init() {
            }
        };
        FILTER_MOVIES = (AppFilter)new AppFilter() {
            @Override
            public boolean filterApp(final AppEntry appEntry) {
                synchronized (appEntry) {
                    return appEntry.info.category == 2;
                }
            }
            
            @Override
            public void init() {
            }
        };
        FILTER_PHOTOS = (AppFilter)new AppFilter() {
            @Override
            public boolean filterApp(final AppEntry appEntry) {
                synchronized (appEntry) {
                    return appEntry.info.category == 3;
                }
            }
            
            @Override
            public void init() {
            }
        };
    }
    
    private ApplicationsState(Application mEntriesMap, final IPackageManager mIpm) {
        this.mSessions = new ArrayList<Session>();
        this.mRebuildingSessions = new ArrayList<Session>();
        this.mInterestingConfigChanges = new InterestingConfigChanges();
        this.mEntriesMap = (SparseArray<HashMap<String, AppEntry>>)new SparseArray();
        this.mAppEntries = new ArrayList<AppEntry>();
        this.mApplications = new ArrayList<ApplicationInfo>();
        this.mCurId = 1L;
        this.mSystemModules = new HashMap<String, Boolean>();
        this.mActiveSessions = new ArrayList<WeakReference<Session>>();
        this.mMainHandler = new MainHandler(Looper.getMainLooper());
        this.mContext = (Context)mEntriesMap;
        this.mPm = ((Context)mEntriesMap).getPackageManager();
        IconDrawableFactory.newInstance(this.mContext);
        this.mIpm = mIpm;
        this.mUm = (UserManager)this.mContext.getSystemService((Class)UserManager.class);
        this.mStats = (StorageStatsManager)this.mContext.getSystemService((Class)StorageStatsManager.class);
        final int[] profileIdsWithDisabled = this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId());
        for (int length = profileIdsWithDisabled.length, i = 0; i < length; ++i) {
            this.mEntriesMap.put(profileIdsWithDisabled[i], (Object)new HashMap());
        }
        (this.mThread = new HandlerThread("ApplicationsState.Loader")).start();
        this.mBackgroundHandler = new BackgroundHandler(this.mThread.getLooper());
        this.mAdminRetrieveFlags = 4227584;
        this.mRetrieveFlags = 33280;
        for (final ModuleInfo moduleInfo : this.mPm.getInstalledModules(0)) {
            this.mSystemModules.put(moduleInfo.getPackageName(), moduleInfo.isHidden());
        }
        mEntriesMap = (Application)this.mEntriesMap;
        // monitorenter(mEntriesMap)
        while (true) {
            try {
                try {
                    this.mEntriesMap.wait(1L);
                }
                finally {
                }
                // monitorexit(mEntriesMap)
                // monitorexit(mEntriesMap)
            }
            catch (InterruptedException ex) {
                continue;
            }
            break;
        }
    }
    
    private void addUser(final int n) {
        if (ArrayUtils.contains(this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId()), n)) {
            synchronized (this.mEntriesMap) {
                this.mEntriesMap.put(n, (Object)new HashMap());
                if (this.mResumed) {
                    this.doPauseLocked();
                    this.doResumeIfNeededLocked();
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }
    
    private static boolean anyAppIsRemoved(final List<ApplicationInfo> list, final List<ApplicationInfo> list2) {
        if (list.size() == 0) {
            return false;
        }
        if (list2.size() < list.size()) {
            return true;
        }
        final HashMap<Object, HashSet<String>> hashMap = new HashMap<Object, HashSet<String>>();
        for (final ApplicationInfo applicationInfo : list2) {
            final String value = String.valueOf(UserHandle.getUserId(applicationInfo.uid));
            HashSet<String> value2;
            if ((value2 = hashMap.get(value)) == null) {
                value2 = new HashSet<String>();
                hashMap.put(value, value2);
            }
            if (hasFlag(applicationInfo.flags, 8388608)) {
                value2.add(applicationInfo.packageName);
            }
        }
        for (final ApplicationInfo applicationInfo2 : list) {
            if (!hasFlag(applicationInfo2.flags, 8388608)) {
                continue;
            }
            final HashSet<String> set = hashMap.get(String.valueOf(UserHandle.getUserId(applicationInfo2.uid)));
            if (set == null || !set.remove(applicationInfo2.packageName)) {
                return true;
            }
        }
        return false;
    }
    
    private AppEntry getEntryLocked(final ApplicationInfo info) {
        final int userId = UserHandle.getUserId(info.uid);
        final AppEntry appEntry = ((HashMap)this.mEntriesMap.get(userId)).get(info.packageName);
        AppEntry appEntry2;
        if (appEntry == null) {
            if (this.isHiddenModule(info.packageName)) {
                return null;
            }
            final Context mContext = this.mContext;
            final long mCurId = this.mCurId;
            this.mCurId = 1L + mCurId;
            appEntry2 = new AppEntry(mContext, info, mCurId);
            ((HashMap)this.mEntriesMap.get(userId)).put(info.packageName, appEntry2);
            this.mAppEntries.add(appEntry2);
        }
        else {
            appEntry2 = appEntry;
            if (appEntry.info != info) {
                appEntry.info = info;
                appEntry2 = appEntry;
            }
        }
        return appEntry2;
    }
    
    static ApplicationsState getInstance(final Application application, final IPackageManager packageManager) {
        synchronized (ApplicationsState.sLock) {
            if (ApplicationsState.sInstance == null) {
                ApplicationsState.sInstance = new ApplicationsState(application, packageManager);
            }
            return ApplicationsState.sInstance;
        }
    }
    
    private String getSizeStr(final long n) {
        if (n >= 0L) {
            return Formatter.formatFileSize(this.mContext, n);
        }
        return null;
    }
    
    private long getTotalExternalSize(final PackageStats packageStats) {
        if (packageStats != null) {
            return packageStats.externalCodeSize + packageStats.externalDataSize + packageStats.externalCacheSize + packageStats.externalMediaSize + packageStats.externalObbSize;
        }
        return -2L;
    }
    
    private long getTotalInternalSize(final PackageStats packageStats) {
        if (packageStats != null) {
            return packageStats.codeSize + packageStats.dataSize - packageStats.cacheSize;
        }
        return -2L;
    }
    
    private static boolean hasFlag(final int n, final int n2) {
        return (n & n2) != 0x0;
    }
    
    private void removeUser(final int n) {
        synchronized (this.mEntriesMap) {
            final HashMap hashMap = (HashMap)this.mEntriesMap.get(n);
            if (hashMap != null) {
                for (final AppEntry o : hashMap.values()) {
                    this.mAppEntries.remove(o);
                    this.mApplications.remove(o.info);
                }
                this.mEntriesMap.remove(n);
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }
    
    void addPackage(final String s, final int n) {
        try {
            synchronized (this.mEntriesMap) {
                if (!this.mResumed) {
                    return;
                }
                if (this.indexOfApplicationInfoLocked(s, n) >= 0) {
                    return;
                }
                final IPackageManager mIpm = this.mIpm;
                int n2;
                if (this.mUm.isUserAdmin(n)) {
                    n2 = this.mAdminRetrieveFlags;
                }
                else {
                    n2 = this.mRetrieveFlags;
                }
                final ApplicationInfo applicationInfo = mIpm.getApplicationInfo(s, n2, n);
                if (applicationInfo == null) {
                    return;
                }
                if (!applicationInfo.enabled && applicationInfo.enabledSetting != 3) {
                    return;
                }
                if (AppUtils.isInstant(applicationInfo)) {
                    this.mHaveInstantApps = true;
                }
                this.mApplications.add(applicationInfo);
                if (!this.mBackgroundHandler.hasMessages(2)) {
                    this.mBackgroundHandler.sendEmptyMessage(2);
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
        catch (RemoteException ex) {}
    }
    
    void clearEntries() {
        for (int i = 0; i < this.mEntriesMap.size(); ++i) {
            ((HashMap)this.mEntriesMap.valueAt(i)).clear();
        }
        this.mAppEntries.clear();
    }
    
    void doPauseIfNeededLocked() {
        if (!this.mResumed) {
            return;
        }
        for (int i = 0; i < this.mSessions.size(); ++i) {
            if (this.mSessions.get(i).mResumed) {
                return;
            }
        }
        this.doPauseLocked();
    }
    
    void doPauseLocked() {
        this.mResumed = false;
        final PackageIntentReceiver mPackageIntentReceiver = this.mPackageIntentReceiver;
        if (mPackageIntentReceiver != null) {
            mPackageIntentReceiver.unregisterReceiver();
            this.mPackageIntentReceiver = null;
        }
    }
    
    void doResumeIfNeededLocked() {
        if (this.mResumed) {
            return;
        }
        this.mResumed = true;
        if (this.mPackageIntentReceiver == null) {
            (this.mPackageIntentReceiver = new PackageIntentReceiver()).registerReceiver();
        }
        final List<ApplicationInfo> mApplications = this.mApplications;
        this.mApplications = new ArrayList<ApplicationInfo>();
        for (final UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
            try {
                if (this.mEntriesMap.indexOfKey(userInfo.id) < 0) {
                    this.mEntriesMap.put(userInfo.id, (Object)new HashMap());
                }
                final IPackageManager mIpm = this.mIpm;
                int n;
                if (userInfo.isAdmin()) {
                    n = this.mAdminRetrieveFlags;
                }
                else {
                    n = this.mRetrieveFlags;
                }
                this.mApplications.addAll(mIpm.getInstalledApplications(n, userInfo.id).getList());
            }
            catch (Exception ex) {
                Log.e("ApplicationsState", "Error during doResumeIfNeededLocked", (Throwable)ex);
            }
        }
        final boolean applyNewConfig = this.mInterestingConfigChanges.applyNewConfig(this.mContext.getResources());
        final int n2 = 0;
        if (applyNewConfig) {
            this.clearEntries();
        }
        else {
            for (int i = 0; i < this.mAppEntries.size(); ++i) {
                this.mAppEntries.get(i).sizeStale = true;
            }
        }
        this.mHaveInstantApps = false;
        int n3;
        for (int j = n2; j < this.mApplications.size(); j = n3 + 1) {
            final ApplicationInfo info = this.mApplications.get(j);
            if (!info.enabled && info.enabledSetting != 3) {
                this.mApplications.remove(j);
                n3 = j - 1;
            }
            else if (this.isHiddenModule(info.packageName)) {
                this.mApplications.remove(j);
                n3 = j - 1;
            }
            else {
                if (!this.mHaveInstantApps && AppUtils.isInstant(info)) {
                    this.mHaveInstantApps = true;
                }
                final AppEntry appEntry = ((HashMap)this.mEntriesMap.get(UserHandle.getUserId(info.uid))).get(info.packageName);
                n3 = j;
                if (appEntry != null) {
                    appEntry.info = info;
                    n3 = j;
                }
            }
        }
        if (anyAppIsRemoved(mApplications, this.mApplications)) {
            this.clearEntries();
        }
        this.mCurComputingSizePkg = null;
        if (!this.mBackgroundHandler.hasMessages(2)) {
            this.mBackgroundHandler.sendEmptyMessage(2);
        }
    }
    
    int indexOfApplicationInfoLocked(final String anObject, final int n) {
        for (int i = this.mApplications.size() - 1; i >= 0; --i) {
            final ApplicationInfo applicationInfo = this.mApplications.get(i);
            if (applicationInfo.packageName.equals(anObject) && UserHandle.getUserId(applicationInfo.uid) == n) {
                return i;
            }
        }
        return -1;
    }
    
    public void invalidatePackage(final String s, final int n) {
        this.removePackage(s, n);
        this.addPackage(s, n);
    }
    
    boolean isHiddenModule(final String key) {
        final Boolean b = this.mSystemModules.get(key);
        return b != null && b;
    }
    
    void rebuildActiveSessions() {
        synchronized (this.mEntriesMap) {
            if (!this.mSessionsChanged) {
                return;
            }
            this.mActiveSessions.clear();
            for (int i = 0; i < this.mSessions.size(); ++i) {
                final Session referent = this.mSessions.get(i);
                if (referent.mResumed) {
                    this.mActiveSessions.add(new WeakReference<Session>(referent));
                }
            }
        }
    }
    
    public void removePackage(final String s, final int n) {
        synchronized (this.mEntriesMap) {
            final int indexOfApplicationInfoLocked = this.indexOfApplicationInfoLocked(s, n);
            if (indexOfApplicationInfoLocked >= 0) {
                final AppEntry o = ((HashMap)this.mEntriesMap.get(n)).get(s);
                if (o != null) {
                    ((HashMap)this.mEntriesMap.get(n)).remove(s);
                    this.mAppEntries.remove(o);
                }
                final ApplicationInfo applicationInfo = this.mApplications.get(indexOfApplicationInfoLocked);
                this.mApplications.remove(indexOfApplicationInfoLocked);
                if (!applicationInfo.enabled) {
                    final Iterator<ApplicationInfo> iterator = this.mApplications.iterator();
                    while (iterator.hasNext() && iterator.next().enabled) {}
                }
                if (AppUtils.isInstant(applicationInfo)) {
                    this.mHaveInstantApps = false;
                    final Iterator<ApplicationInfo> iterator2 = this.mApplications.iterator();
                    while (iterator2.hasNext()) {
                        if (AppUtils.isInstant(iterator2.next())) {
                            this.mHaveInstantApps = true;
                            break;
                        }
                    }
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }
    
    void setInterestingConfigChanges(final InterestingConfigChanges mInterestingConfigChanges) {
        this.mInterestingConfigChanges = mInterestingConfigChanges;
    }
    
    public static class AppEntry extends SizeInfo
    {
        public final File apkFile;
        public long externalSize;
        public String externalSizeStr;
        public boolean hasLauncherEntry;
        public Drawable icon;
        public ApplicationInfo info;
        public long internalSize;
        public String internalSizeStr;
        public boolean isHomeApp;
        public String label;
        public boolean launcherEntryEnabled;
        public boolean mounted;
        public long size;
        public long sizeLoadStart;
        public boolean sizeStale;
        public String sizeStr;
        
        public AppEntry(final Context context, final ApplicationInfo info, final long n) {
            this.apkFile = new File(info.sourceDir);
            this.info = info;
            this.size = -1L;
            this.sizeStale = true;
            this.ensureLabel(context);
        }
        
        boolean ensureIconLocked(final Context context) {
            if (this.icon == null) {
                if (this.apkFile.exists()) {
                    this.icon = Utils.getBadgedIcon(context, this.info);
                    return true;
                }
                this.mounted = false;
                this.icon = context.getDrawable(17303645);
            }
            else if (!this.mounted && this.apkFile.exists()) {
                this.mounted = true;
                this.icon = Utils.getBadgedIcon(context, this.info);
                return true;
            }
            return false;
        }
        
        public void ensureLabel(final Context context) {
            if (this.label == null || !this.mounted) {
                if (!this.apkFile.exists()) {
                    this.mounted = false;
                    this.label = this.info.packageName;
                }
                else {
                    this.mounted = true;
                    final CharSequence loadLabel = this.info.loadLabel(context.getPackageManager());
                    String label;
                    if (loadLabel != null) {
                        label = loadLabel.toString();
                    }
                    else {
                        label = this.info.packageName;
                    }
                    this.label = label;
                }
            }
        }
    }
    
    public interface AppFilter
    {
        boolean filterApp(final AppEntry p0);
        
        void init();
        
        default void init(final Context context) {
            this.init();
        }
    }
    
    private class BackgroundHandler extends Handler
    {
        boolean mRunning;
        final IPackageStatsObserver$Stub mStatsObserver;
        
        BackgroundHandler(final Looper looper) {
            super(looper);
            this.mStatsObserver = new IPackageStatsObserver$Stub() {
                public void onGetStatsCompleted(final PackageStats packageStats, final boolean b) {
                    if (!b) {
                        return;
                    }
                    synchronized (ApplicationsState.this.mEntriesMap) {
                        final HashMap hashMap = (HashMap)ApplicationsState.this.mEntriesMap.get(packageStats.userHandle);
                        if (hashMap == null) {
                            return;
                        }
                        Object obtainMessage = hashMap.get(packageStats.packageName);
                        if (obtainMessage != null) {
                            // monitorenter(obtainMessage)
                            boolean b2 = false;
                            try {
                                ((AppEntry)obtainMessage).sizeStale = false;
                                ((AppEntry)obtainMessage).sizeLoadStart = 0L;
                                final long externalCodeSize = packageStats.externalCodeSize + packageStats.externalObbSize;
                                final long externalDataSize = packageStats.externalDataSize + packageStats.externalMediaSize;
                                final long size = externalCodeSize + externalDataSize + ApplicationsState.this.getTotalInternalSize(packageStats);
                                if (((AppEntry)obtainMessage).size != size || ((SizeInfo)obtainMessage).cacheSize != packageStats.cacheSize || ((SizeInfo)obtainMessage).codeSize != packageStats.codeSize || ((SizeInfo)obtainMessage).dataSize != packageStats.dataSize || ((SizeInfo)obtainMessage).externalCodeSize != externalCodeSize || ((SizeInfo)obtainMessage).externalDataSize != externalDataSize || ((SizeInfo)obtainMessage).externalCacheSize != packageStats.externalCacheSize) {
                                    ((AppEntry)obtainMessage).size = size;
                                    ((SizeInfo)obtainMessage).cacheSize = packageStats.cacheSize;
                                    ((SizeInfo)obtainMessage).codeSize = packageStats.codeSize;
                                    ((SizeInfo)obtainMessage).dataSize = packageStats.dataSize;
                                    ((SizeInfo)obtainMessage).externalCodeSize = externalCodeSize;
                                    ((SizeInfo)obtainMessage).externalDataSize = externalDataSize;
                                    ((SizeInfo)obtainMessage).externalCacheSize = packageStats.externalCacheSize;
                                    ((AppEntry)obtainMessage).sizeStr = ApplicationsState.this.getSizeStr(size);
                                    final long access$400 = ApplicationsState.this.getTotalInternalSize(packageStats);
                                    ((AppEntry)obtainMessage).internalSize = access$400;
                                    ((AppEntry)obtainMessage).internalSizeStr = ApplicationsState.this.getSizeStr(access$400);
                                    final long access$401 = ApplicationsState.this.getTotalExternalSize(packageStats);
                                    ((AppEntry)obtainMessage).externalSize = access$401;
                                    ((AppEntry)obtainMessage).externalSizeStr = ApplicationsState.this.getSizeStr(access$401);
                                    b2 = true;
                                }
                                // monitorexit(obtainMessage)
                                if (b2) {
                                    obtainMessage = ApplicationsState.this.mMainHandler.obtainMessage(4, (Object)packageStats.packageName);
                                    ApplicationsState.this.mMainHandler.sendMessage((Message)obtainMessage);
                                }
                            }
                            finally {
                            }
                            // monitorexit(obtainMessage)
                        }
                        if (ApplicationsState.this.mCurComputingSizePkg != null && ApplicationsState.this.mCurComputingSizePkg.equals(packageStats.packageName) && ApplicationsState.this.mCurComputingSizeUserId == packageStats.userHandle) {
                            ApplicationsState.this.mCurComputingSizePkg = null;
                            BackgroundHandler.this.sendEmptyMessage(7);
                        }
                    }
                }
            };
        }
        
        private int getCombinedSessionFlags(final List<Session> list) {
            final SparseArray<HashMap<String, AppEntry>> mEntriesMap = ApplicationsState.this.mEntriesMap;
            // monitorenter(mEntriesMap)
            int n = 0;
            try {
                final Iterator<Session> iterator = list.iterator();
                while (iterator.hasNext()) {
                    n |= iterator.next().mFlags;
                }
                return n;
            }
            finally {
            }
            // monitorexit(mEntriesMap)
        }
        
        public void handleMessage(Message message) {
            Object o = ApplicationsState.this.mRebuildingSessions;
            synchronized (o) {
                ArrayList<Session> list;
                if (ApplicationsState.this.mRebuildingSessions.size() > 0) {
                    list = new ArrayList<Session>((Collection<? extends Session>)ApplicationsState.this.mRebuildingSessions);
                    ApplicationsState.this.mRebuildingSessions.clear();
                }
                else {
                    list = null;
                }
                // monitorexit(o)
                if (list != null) {
                    final Iterator<Session> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().handleRebuildList();
                    }
                }
                final int combinedSessionFlags = this.getCombinedSessionFlags(ApplicationsState.this.mSessions);
                final int what = message.what;
                int index = 0;
                Label_0476: {
                    switch (what) {
                        case 7: {
                            if (hasFlag(combinedSessionFlags, 4)) {
                                synchronized (ApplicationsState.this.mEntriesMap) {
                                    if (ApplicationsState.this.mCurComputingSizePkg != null) {
                                        return;
                                    }
                                    final long uptimeMillis = SystemClock.uptimeMillis();
                                    for (int i = 0; i < ApplicationsState.this.mAppEntries.size(); ++i) {
                                        o = ApplicationsState.this.mAppEntries.get(i);
                                        if (hasFlag(((AppEntry)o).info.flags, 8388608) && (((AppEntry)o).size == -1L || ((AppEntry)o).sizeStale)) {
                                            if (((AppEntry)o).sizeLoadStart == 0L || ((AppEntry)o).sizeLoadStart < uptimeMillis - 20000L) {
                                                if (!this.mRunning) {
                                                    this.mRunning = true;
                                                    ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, (Object)1));
                                                }
                                                ((AppEntry)o).sizeLoadStart = uptimeMillis;
                                                ApplicationsState.this.mCurComputingSizeUuid = ((AppEntry)o).info.storageUuid;
                                                ApplicationsState.this.mCurComputingSizePkg = ((AppEntry)o).info.packageName;
                                                ApplicationsState.this.mCurComputingSizeUserId = UserHandle.getUserId(((AppEntry)o).info.uid);
                                                o = ApplicationsState.this.mBackgroundHandler;
                                                ((Handler)o).post((Runnable)new _$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc(this));
                                            }
                                            return;
                                        }
                                    }
                                    if (!ApplicationsState.this.mMainHandler.hasMessages(5)) {
                                        ApplicationsState.this.mMainHandler.sendEmptyMessage(5);
                                        this.mRunning = false;
                                        ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, (Object)0));
                                    }
                                    break;
                                }
                                break Label_0476;
                            }
                            break;
                        }
                        case 6: {
                            if (hasFlag(combinedSessionFlags, 2)) {
                                message = (Message)ApplicationsState.this.mEntriesMap;
                                // monitorenter(message)
                                int n = 0;
                                try {
                                    while (index < ApplicationsState.this.mAppEntries.size() && n < 2) {
                                        final AppEntry appEntry = ApplicationsState.this.mAppEntries.get(index);
                                        // monitorenter(appEntry)
                                        Label_0622: {
                                            if (appEntry.icon != null) {
                                                final int n2 = n;
                                                if (appEntry.mounted) {
                                                    break Label_0622;
                                                }
                                            }
                                            int n2 = n;
                                            try {
                                                if (appEntry.ensureIconLocked(ApplicationsState.this.mContext)) {
                                                    if (!this.mRunning) {
                                                        this.mRunning = true;
                                                        ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, (Object)1));
                                                    }
                                                    n2 = n + 1;
                                                }
                                                // monitorexit(appEntry)
                                                ++index;
                                                n = n2;
                                                continue;
                                            }
                                            finally {
                                            }
                                            // monitorexit(appEntry)
                                        }
                                        break;
                                    }
                                    // monitorexit(message)
                                    if (n > 0 && !ApplicationsState.this.mMainHandler.hasMessages(3)) {
                                        ApplicationsState.this.mMainHandler.sendEmptyMessage(3);
                                    }
                                    if (n >= 2) {
                                        this.sendEmptyMessage(6);
                                        break;
                                    }
                                }
                                finally {
                                }
                                // monitorexit(message)
                            }
                            this.sendEmptyMessage(7);
                            break;
                        }
                        case 4:
                        case 5: {
                            if ((what == 4 && hasFlag(combinedSessionFlags, 8)) || (message.what == 5 && hasFlag(combinedSessionFlags, 16))) {
                                o = new Intent("android.intent.action.MAIN", (Uri)null);
                                String s;
                                if (message.what == 4) {
                                    s = "android.intent.category.LAUNCHER";
                                }
                                else {
                                    s = "android.intent.category.LEANBACK_LAUNCHER";
                                }
                                ((Intent)o).addCategory(s);
                                int j = 0;
                                while (j < ApplicationsState.this.mEntriesMap.size()) {
                                    final int key = ApplicationsState.this.mEntriesMap.keyAt(j);
                                    final List queryIntentActivitiesAsUser = ApplicationsState.this.mPm.queryIntentActivitiesAsUser((Intent)o, 786944, key);
                                    synchronized (ApplicationsState.this.mEntriesMap) {
                                        final HashMap hashMap = (HashMap)ApplicationsState.this.mEntriesMap.valueAt(j);
                                        for (int size = queryIntentActivitiesAsUser.size(), k = 0; k < size; ++k) {
                                            final ResolveInfo resolveInfo = queryIntentActivitiesAsUser.get(k);
                                            final String packageName = resolveInfo.activityInfo.packageName;
                                            final AppEntry appEntry2 = hashMap.get(packageName);
                                            if (appEntry2 != null) {
                                                appEntry2.hasLauncherEntry = true;
                                                appEntry2.launcherEntryEnabled |= resolveInfo.activityInfo.enabled;
                                            }
                                            else {
                                                final StringBuilder sb = new StringBuilder();
                                                sb.append("Cannot find pkg: ");
                                                sb.append(packageName);
                                                sb.append(" on user ");
                                                sb.append(key);
                                                Log.w("ApplicationsState", sb.toString());
                                            }
                                        }
                                        // monitorexit(this.this$0.mEntriesMap)
                                        ++j;
                                        continue;
                                    }
                                    break;
                                }
                                if (!ApplicationsState.this.mMainHandler.hasMessages(7)) {
                                    ApplicationsState.this.mMainHandler.sendEmptyMessage(7);
                                }
                            }
                            if (message.what == 4) {
                                this.sendEmptyMessage(5);
                                break;
                            }
                            this.sendEmptyMessage(6);
                            break;
                        }
                        case 3: {
                            if (hasFlag(combinedSessionFlags, 1)) {
                                o = new ArrayList();
                                ApplicationsState.this.mPm.getHomeActivities((List)o);
                                synchronized (ApplicationsState.this.mEntriesMap) {
                                    for (int size2 = ApplicationsState.this.mEntriesMap.size(), l = 0; l < size2; ++l) {
                                        final HashMap hashMap2 = (HashMap)ApplicationsState.this.mEntriesMap.valueAt(l);
                                        final Iterator<ResolveInfo> iterator2 = ((List<ResolveInfo>)o).iterator();
                                        while (iterator2.hasNext()) {
                                            final AppEntry appEntry3 = hashMap2.get(iterator2.next().activityInfo.packageName);
                                            if (appEntry3 != null) {
                                                appEntry3.isHomeApp = true;
                                            }
                                        }
                                    }
                                }
                            }
                            this.sendEmptyMessage(4);
                            break;
                        }
                        case 2: {
                            message = (Message)ApplicationsState.this.mEntriesMap;
                            // monitorenter(message)
                            int n3 = 0;
                            int n4 = 0;
                            try {
                                while (n4 < ApplicationsState.this.mApplications.size() && n3 < 6) {
                                    if (!this.mRunning) {
                                        this.mRunning = true;
                                        ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, (Object)1));
                                    }
                                    final ApplicationInfo applicationInfo = ApplicationsState.this.mApplications.get(n4);
                                    final int userId = UserHandle.getUserId(applicationInfo.uid);
                                    int n5 = n3;
                                    if (((HashMap)ApplicationsState.this.mEntriesMap.get(userId)).get(applicationInfo.packageName) == null) {
                                        n5 = n3 + 1;
                                        ApplicationsState.this.getEntryLocked(applicationInfo);
                                    }
                                    if (userId != 0) {
                                        if (ApplicationsState.this.mEntriesMap.indexOfKey(0) >= 0) {
                                            o = ((HashMap)ApplicationsState.this.mEntriesMap.get(0)).get(applicationInfo.packageName);
                                            if (o != null && !hasFlag(((AppEntry)o).info.flags, 8388608)) {
                                                ((HashMap)ApplicationsState.this.mEntriesMap.get(0)).remove(applicationInfo.packageName);
                                                ApplicationsState.this.mAppEntries.remove(o);
                                            }
                                        }
                                    }
                                    ++n4;
                                    n3 = n5;
                                }
                                // monitorexit(message)
                                if (n3 >= 6) {
                                    this.sendEmptyMessage(2);
                                }
                                else {
                                    if (!ApplicationsState.this.mMainHandler.hasMessages(8)) {
                                        ApplicationsState.this.mMainHandler.sendEmptyMessage(8);
                                    }
                                    this.sendEmptyMessage(3);
                                }
                            }
                            finally {}
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public interface Callbacks
    {
        void onAllSizesComputed();
        
        void onLauncherInfoChanged();
        
        void onLoadEntriesCompleted();
        
        void onPackageIconChanged();
        
        void onPackageListChanged();
        
        void onPackageSizeChanged(final String p0);
        
        void onRebuildComplete(final ArrayList<AppEntry> p0);
        
        void onRunningStateChanged(final boolean p0);
    }
    
    class MainHandler extends Handler
    {
        public MainHandler(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            ApplicationsState.this.rebuildActiveSessions();
            switch (message.what) {
                case 8: {
                    final Iterator<WeakReference<Session>> iterator = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator.hasNext()) {
                        final Session session = iterator.next().get();
                        if (session != null) {
                            session.mCallbacks.onLoadEntriesCompleted();
                        }
                    }
                    break;
                }
                case 7: {
                    final Iterator<WeakReference<Session>> iterator2 = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator2.hasNext()) {
                        final Session session2 = iterator2.next().get();
                        if (session2 != null) {
                            session2.mCallbacks.onLauncherInfoChanged();
                        }
                    }
                    break;
                }
                case 6: {
                    final Iterator<WeakReference<Session>> iterator3 = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator3.hasNext()) {
                        final Session session3 = iterator3.next().get();
                        if (session3 != null) {
                            session3.mCallbacks.onRunningStateChanged(message.arg1 != 0);
                        }
                    }
                    break;
                }
                case 5: {
                    final Iterator<WeakReference<Session>> iterator4 = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator4.hasNext()) {
                        final Session session4 = iterator4.next().get();
                        if (session4 != null) {
                            session4.mCallbacks.onAllSizesComputed();
                        }
                    }
                    break;
                }
                case 4: {
                    final Iterator<WeakReference<Session>> iterator5 = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator5.hasNext()) {
                        final Session session5 = iterator5.next().get();
                        if (session5 != null) {
                            session5.mCallbacks.onPackageSizeChanged((String)message.obj);
                        }
                    }
                    break;
                }
                case 3: {
                    final Iterator<WeakReference<Session>> iterator6 = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator6.hasNext()) {
                        final Session session6 = iterator6.next().get();
                        if (session6 != null) {
                            session6.mCallbacks.onPackageIconChanged();
                        }
                    }
                    break;
                }
                case 2: {
                    final Iterator<WeakReference<Session>> iterator7 = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator7.hasNext()) {
                        final Session session7 = iterator7.next().get();
                        if (session7 != null) {
                            session7.mCallbacks.onPackageListChanged();
                        }
                    }
                    break;
                }
                case 1: {
                    final Session session8 = (Session)message.obj;
                    final Iterator<WeakReference<Session>> iterator8 = ApplicationsState.this.mActiveSessions.iterator();
                    while (iterator8.hasNext()) {
                        final Session session9 = iterator8.next().get();
                        if (session9 != null && session9 == session8) {
                            session8.mCallbacks.onRebuildComplete(session8.mLastAppList);
                        }
                    }
                    break;
                }
            }
        }
    }
    
    private class PackageIntentReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            final boolean equals = "android.intent.action.PACKAGE_ADDED".equals(action);
            final int n = 0;
            final int n2 = 0;
            int i = 0;
            if (equals) {
                final String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    final ApplicationsState this$0 = ApplicationsState.this;
                    this$0.addPackage(encodedSchemeSpecificPart, this$0.mEntriesMap.keyAt(i));
                    ++i;
                }
            }
            else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                final String encodedSchemeSpecificPart2 = intent.getData().getEncodedSchemeSpecificPart();
                for (int j = n; j < ApplicationsState.this.mEntriesMap.size(); ++j) {
                    final ApplicationsState this$2 = ApplicationsState.this;
                    this$2.removePackage(encodedSchemeSpecificPart2, this$2.mEntriesMap.keyAt(j));
                }
            }
            else if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                final String encodedSchemeSpecificPart3 = intent.getData().getEncodedSchemeSpecificPart();
                for (int k = n2; k < ApplicationsState.this.mEntriesMap.size(); ++k) {
                    final ApplicationsState this$3 = ApplicationsState.this;
                    this$3.invalidatePackage(encodedSchemeSpecificPart3, this$3.mEntriesMap.keyAt(k));
                }
            }
            else if (!"android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action) && !"android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                if ("android.intent.action.USER_ADDED".equals(action)) {
                    ApplicationsState.this.addUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
                }
                else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    ApplicationsState.this.removeUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
                }
            }
            else {
                final String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                if (stringArrayExtra != null) {
                    if (stringArrayExtra.length != 0) {
                        if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action)) {
                            for (final String s : stringArrayExtra) {
                                for (int n3 = 0; n3 < ApplicationsState.this.mEntriesMap.size(); ++n3) {
                                    final ApplicationsState this$4 = ApplicationsState.this;
                                    this$4.invalidatePackage(s, this$4.mEntriesMap.keyAt(n3));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        void registerReceiver() {
            final IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addDataScheme("package");
            ApplicationsState.this.mContext.registerReceiver((BroadcastReceiver)this, intentFilter);
            final IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            ApplicationsState.this.mContext.registerReceiver((BroadcastReceiver)this, intentFilter2);
            final IntentFilter intentFilter3 = new IntentFilter();
            intentFilter3.addAction("android.intent.action.USER_ADDED");
            intentFilter3.addAction("android.intent.action.USER_REMOVED");
            ApplicationsState.this.mContext.registerReceiver((BroadcastReceiver)this, intentFilter3);
        }
        
        void unregisterReceiver() {
            ApplicationsState.this.mContext.unregisterReceiver((BroadcastReceiver)this);
        }
    }
    
    public class Session implements LifecycleObserver
    {
        final Callbacks mCallbacks;
        private int mFlags;
        private final boolean mHasLifecycle;
        ArrayList<AppEntry> mLastAppList;
        boolean mRebuildAsync;
        Comparator<AppEntry> mRebuildComparator;
        AppFilter mRebuildFilter;
        boolean mRebuildForeground;
        boolean mRebuildRequested;
        final Object mRebuildSync;
        boolean mResumed;
        final /* synthetic */ ApplicationsState this$0;
        
        void handleRebuildList() {
            if (!this.mResumed) {
                return;
            }
            Object o = this.mRebuildSync;
            synchronized (o) {
                if (!this.mRebuildRequested) {
                    return;
                }
                final AppFilter mRebuildFilter = this.mRebuildFilter;
                final Comparator<AppEntry> mRebuildComparator = this.mRebuildComparator;
                this.mRebuildRequested = false;
                this.mRebuildFilter = null;
                this.mRebuildComparator = null;
                if (this.mRebuildForeground) {
                    Process.setThreadPriority(-2);
                    this.mRebuildForeground = false;
                }
                // monitorexit(o)
                if (mRebuildFilter != null) {
                    mRebuildFilter.init(this.this$0.mContext);
                }
                o = this.this$0.mEntriesMap;
                synchronized (o) {
                    final ArrayList<AppEntry> list = new ArrayList<AppEntry>((Collection<? extends AppEntry>)this.this$0.mAppEntries);
                    // monitorexit(o)
                    o = new ArrayList<Object>();
                Label_0202_Outer:
                    for (final AppEntry e : list) {
                        if (e != null && (mRebuildFilter == null || mRebuildFilter.filterApp(e))) {
                            final SparseArray<HashMap<String, AppEntry>> mEntriesMap = this.this$0.mEntriesMap;
                            // monitorenter(mEntriesMap)
                            while (true) {
                                if (mRebuildComparator != null) {
                                    try {
                                        e.ensureLabel(this.this$0.mContext);
                                        ((ArrayList<AppEntry>)o).add(e);
                                        continue Label_0202_Outer;
                                    }
                                    finally {
                                    }
                                    // monitorexit(mEntriesMap)
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                    if (mRebuildComparator != null) {
                        synchronized (this.this$0.mEntriesMap) {
                            Collections.sort((List<Object>)o, (Comparator<? super Object>)mRebuildComparator);
                        }
                    }
                    synchronized (this.mRebuildSync) {
                        if (!this.mRebuildRequested) {
                            this.mLastAppList = (ArrayList<AppEntry>)o;
                            if (!this.mRebuildAsync) {
                                this.mRebuildSync.notifyAll();
                            }
                            else if (!this.this$0.mMainHandler.hasMessages(1, (Object)this)) {
                                this.this$0.mMainHandler.sendMessage(this.this$0.mMainHandler.obtainMessage(1, (Object)this));
                            }
                        }
                        // monitorexit(this.mRebuildSync)
                        Process.setThreadPriority(10);
                    }
                }
            }
        }
        
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            if (!this.mHasLifecycle) {
                this.onPause();
            }
            synchronized (this.this$0.mEntriesMap) {
                this.this$0.mSessions.remove(this);
            }
        }
        
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
            synchronized (this.this$0.mEntriesMap) {
                if (this.mResumed) {
                    this.mResumed = false;
                    this.this$0.mSessionsChanged = true;
                    this.this$0.mBackgroundHandler.removeMessages(1, (Object)this);
                    this.this$0.doPauseIfNeededLocked();
                }
            }
        }
        
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            synchronized (this.this$0.mEntriesMap) {
                if (!this.mResumed) {
                    this.mResumed = true;
                    this.this$0.mSessionsChanged = true;
                    this.this$0.doPauseLocked();
                    this.this$0.doResumeIfNeededLocked();
                }
            }
        }
    }
    
    public static class SizeInfo
    {
        public long cacheSize;
        public long codeSize;
        public long dataSize;
        public long externalCacheSize;
        public long externalCodeSize;
        public long externalDataSize;
    }
}
