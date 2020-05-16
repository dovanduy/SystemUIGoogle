// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.theme;

import android.net.Uri;
import java.util.Collection;
import android.database.ContentObserver;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.android.systemui.R$string;
import android.os.AsyncTask;
import android.content.om.OverlayManager;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import android.content.pm.UserInfo;
import com.google.android.collect.Sets;
import android.os.UserHandle;
import org.json.JSONException;
import android.util.Log;
import org.json.JSONObject;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.provider.Settings$Secure;
import android.app.ActivityManager;
import android.content.Context;
import android.os.UserManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import com.android.systemui.SystemUI;

public class ThemeOverlayController extends SystemUI
{
    private final Handler mBgHandler;
    private BroadcastDispatcher mBroadcastDispatcher;
    private ThemeOverlayManager mThemeManager;
    private UserManager mUserManager;
    
    public ThemeOverlayController(final Context context, final BroadcastDispatcher mBroadcastDispatcher, final Handler mBgHandler) {
        super(context);
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mBgHandler = mBgHandler;
    }
    
    private void updateThemeOverlays() {
        final int currentUser = ActivityManager.getCurrentUser();
        final String stringForUser = Settings$Secure.getStringForUser(super.mContext.getContentResolver(), "theme_customization_overlay_packages", currentUser);
        final ArrayMap arrayMap = new ArrayMap();
        if (!TextUtils.isEmpty((CharSequence)stringForUser)) {
            try {
                final JSONObject jsonObject = new JSONObject(stringForUser);
                for (final String s : ThemeOverlayManager.THEME_CATEGORIES) {
                    if (jsonObject.has(s)) {
                        ((Map<String, String>)arrayMap).put(s, jsonObject.getString(s));
                    }
                }
            }
            catch (JSONException ex) {
                Log.i("ThemeOverlayController", "Failed to parse THEME_CUSTOMIZATION_OVERLAY_PACKAGES.", (Throwable)ex);
            }
        }
        final HashSet hashSet = Sets.newHashSet((Object[])new UserHandle[] { UserHandle.of(currentUser) });
        for (final UserInfo userInfo : this.mUserManager.getEnabledProfiles(currentUser)) {
            if (userInfo.isManagedProfile()) {
                hashSet.add(userInfo.getUserHandle());
            }
        }
        this.mThemeManager.applyCurrentUserOverlays((Map<String, String>)arrayMap, hashSet);
    }
    
    @Override
    public void start() {
        this.mUserManager = (UserManager)super.mContext.getSystemService((Class)UserManager.class);
        this.mThemeManager = new ThemeOverlayManager((OverlayManager)super.mContext.getSystemService((Class)OverlayManager.class), AsyncTask.THREAD_POOL_EXECUTOR, super.mContext.getString(R$string.launcher_overlayable_package), super.mContext.getString(R$string.themepicker_overlayable_package));
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                ThemeOverlayController.this.updateThemeOverlays();
            }
        }, intentFilter, this.mBgHandler, UserHandle.ALL);
        super.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("theme_customization_overlay_packages"), false, (ContentObserver)new ContentObserver(this.mBgHandler) {
            public void onChange(final boolean b, final Collection<Uri> collection, final int n, final int n2) {
                if (ActivityManager.getCurrentUser() == n2) {
                    ThemeOverlayController.this.updateThemeOverlays();
                }
            }
        }, -1);
    }
}
