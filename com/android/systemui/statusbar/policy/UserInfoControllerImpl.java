// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.pm.UserInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.database.Cursor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.ContactsContract$Profile;
import com.android.internal.util.UserIcons;
import com.android.settingslib.drawable.UserIconDrawable;
import android.os.UserManager;
import com.android.systemui.R$dimen;
import com.android.systemui.R$style;
import java.util.Iterator;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.graphics.drawable.Drawable;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.ArrayList;

public class UserInfoControllerImpl implements UserInfoController
{
    private final ArrayList<OnUserInfoChangedListener> mCallbacks;
    private final Context mContext;
    private final BroadcastReceiver mProfileReceiver;
    private final BroadcastReceiver mReceiver;
    private String mUserAccount;
    private Drawable mUserDrawable;
    private AsyncTask<Void, Void, UserInfoQueryResult> mUserInfoTask;
    private String mUserName;
    
    public UserInfoControllerImpl(final Context mContext) {
        this.mCallbacks = new ArrayList<OnUserInfoChangedListener>();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                    UserInfoControllerImpl.this.reloadUserInfo();
                }
            }
        };
        this.mProfileReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (!"android.provider.Contacts.PROFILE_CHANGED".equals(action)) {
                    if (!"android.intent.action.USER_INFO_CHANGED".equals(action)) {
                        return;
                    }
                }
                try {
                    if (intent.getIntExtra("android.intent.extra.user_handle", this.getSendingUserId()) == ActivityManager.getService().getCurrentUser().id) {
                        UserInfoControllerImpl.this.reloadUserInfo();
                    }
                }
                catch (RemoteException ex) {
                    Log.e("UserInfoController", "Couldn't get current user id for profile change", (Throwable)ex);
                }
            }
        };
        this.mContext = mContext;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
        final IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.provider.Contacts.PROFILE_CHANGED");
        intentFilter2.addAction("android.intent.action.USER_INFO_CHANGED");
        this.mContext.registerReceiverAsUser(this.mProfileReceiver, UserHandle.ALL, intentFilter2, (String)null, (Handler)null);
    }
    
    private void notifyChanged() {
        final Iterator<OnUserInfoChangedListener> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onUserInfoChanged(this.mUserName, this.mUserDrawable, this.mUserAccount);
        }
    }
    
    private void queryForUserInformation() {
        try {
            final UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            final Context packageContextAsUser = this.mContext.createPackageContextAsUser("android", 0, new UserHandle(currentUser.id));
            final int id = currentUser.id;
            final boolean guest = currentUser.isGuest();
            final String name = currentUser.name;
            final boolean b = this.mContext.getThemeResId() != R$style.Theme_SystemUI_Light;
            final Resources resources = this.mContext.getResources();
            (this.mUserInfoTask = new AsyncTask<Void, Void, UserInfoQueryResult>() {
                final /* synthetic */ int val$avatarSize = Math.max(resources.getDimensionPixelSize(R$dimen.multi_user_avatar_expanded_size), resources.getDimensionPixelSize(R$dimen.multi_user_avatar_keyguard_size));
                
                protected UserInfoQueryResult doInBackground(final Void... array) {
                    final UserManager value = UserManager.get(UserInfoControllerImpl.this.mContext);
                    String s = name;
                    final Bitmap userIcon = value.getUserIcon(id);
                    Drawable defaultUserIcon;
                    if (userIcon != null) {
                        defaultUserIcon = new UserIconDrawable(this.val$avatarSize);
                        ((UserIconDrawable)defaultUserIcon).setIcon(userIcon);
                        ((UserIconDrawable)defaultUserIcon).setBadgeIfManagedUser(UserInfoControllerImpl.this.mContext, id);
                        ((UserIconDrawable)defaultUserIcon).bake();
                    }
                    else {
                        final Resources resources = packageContextAsUser.getResources();
                        int val$userId;
                        if (guest) {
                            val$userId = -10000;
                        }
                        else {
                            val$userId = id;
                        }
                        defaultUserIcon = UserIcons.getDefaultUserIcon(resources, val$userId, b);
                    }
                    String s2 = s;
                    if (value.getUsers().size() <= 1) {
                        final Cursor query = packageContextAsUser.getContentResolver().query(ContactsContract$Profile.CONTENT_URI, new String[] { "_id", "display_name" }, (String)null, (String[])null, (String)null);
                        s2 = s;
                        if (query != null) {
                            try {
                                if (query.moveToFirst()) {
                                    s = query.getString(query.getColumnIndex("display_name"));
                                }
                                query.close();
                                s2 = s;
                            }
                            finally {
                                query.close();
                            }
                        }
                    }
                    return new UserInfoQueryResult(s2, defaultUserIcon, value.getUserAccount(id));
                }
                
                protected void onPostExecute(final UserInfoQueryResult userInfoQueryResult) {
                    UserInfoControllerImpl.this.mUserName = userInfoQueryResult.getName();
                    UserInfoControllerImpl.this.mUserDrawable = userInfoQueryResult.getAvatar();
                    UserInfoControllerImpl.this.mUserAccount = userInfoQueryResult.getUserAccount();
                    UserInfoControllerImpl.this.mUserInfoTask = null;
                    UserInfoControllerImpl.this.notifyChanged();
                }
            }).execute((Object[])new Void[0]);
        }
        catch (RemoteException cause) {
            Log.e("UserInfoController", "Couldn't get user info", (Throwable)cause);
            throw new RuntimeException((Throwable)cause);
        }
        catch (PackageManager$NameNotFoundException cause2) {
            Log.e("UserInfoController", "Couldn't create user context", (Throwable)cause2);
            throw new RuntimeException((Throwable)cause2);
        }
    }
    
    @Override
    public void addCallback(final OnUserInfoChangedListener e) {
        this.mCallbacks.add(e);
        e.onUserInfoChanged(this.mUserName, this.mUserDrawable, this.mUserAccount);
    }
    
    public void onDensityOrFontScaleChanged() {
        this.reloadUserInfo();
    }
    
    @Override
    public void reloadUserInfo() {
        final AsyncTask<Void, Void, UserInfoQueryResult> mUserInfoTask = this.mUserInfoTask;
        if (mUserInfoTask != null) {
            mUserInfoTask.cancel(false);
            this.mUserInfoTask = null;
        }
        this.queryForUserInformation();
    }
    
    @Override
    public void removeCallback(final OnUserInfoChangedListener o) {
        this.mCallbacks.remove(o);
    }
    
    private static class UserInfoQueryResult
    {
        private Drawable mAvatar;
        private String mName;
        private String mUserAccount;
        
        public UserInfoQueryResult(final String mName, final Drawable mAvatar, final String mUserAccount) {
            this.mName = mName;
            this.mAvatar = mAvatar;
            this.mUserAccount = mUserAccount;
        }
        
        public Drawable getAvatar() {
            return this.mAvatar;
        }
        
        public String getName() {
            return this.mName;
        }
        
        public String getUserAccount() {
            return this.mUserAccount;
        }
    }
}
