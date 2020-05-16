// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.R$drawable;
import android.graphics.drawable.Drawable;
import android.widget.BaseAdapter;
import com.android.internal.util.UserIcons;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.R$bool;
import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.List;
import com.android.systemui.Prefs;
import com.android.systemui.R$dimen;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.telephony.TelephonyManager;
import android.provider.Settings$System;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import android.app.ActivityManager;
import com.android.systemui.SystemUISecondaryUserService;
import java.util.concurrent.Executor;
import android.content.IntentFilter;
import com.android.systemui.R$string;
import com.android.systemui.qs.tiles.UserDetailView;
import android.view.ViewGroup;
import android.view.View;
import android.content.ContentResolver;
import android.provider.Settings$Global;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import com.android.systemui.plugins.qs.DetailAdapter;
import android.os.UserManager;
import android.database.ContentObserver;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.telephony.PhoneStateListener;
import android.os.Handler;
import com.android.systemui.GuestResumeSessionReceiver;
import android.util.SparseBooleanArray;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.app.Dialog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.Dumpable;

public class UserSwitcherController implements Dumpable
{
    private final ActivityStarter mActivityStarter;
    private final ArrayList<WeakReference<BaseUserAdapter>> mAdapters;
    private Dialog mAddUserDialog;
    private boolean mAddUsersWhenLocked;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final KeyguardStateController.Callback mCallback;
    protected final Context mContext;
    private Dialog mExitGuestDialog;
    private SparseBooleanArray mForcePictureLoadForUserId;
    private final GuestResumeSessionReceiver mGuestResumeSessionReceiver;
    protected final Handler mHandler;
    private final KeyguardStateController mKeyguardStateController;
    private int mLastNonGuestUser;
    private boolean mPauseRefreshUsers;
    private final PhoneStateListener mPhoneStateListener;
    private BroadcastReceiver mReceiver;
    private boolean mResumeUserOnGuestLogout;
    private int mSecondaryUser;
    private Intent mSecondaryUserServiceIntent;
    private final ContentObserver mSettingsObserver;
    private boolean mSimpleUserSwitcher;
    private final Runnable mUnpauseRefreshUsers;
    protected final UserManager mUserManager;
    private ArrayList<UserRecord> mUsers;
    public final DetailAdapter userDetailAdapter;
    
    public UserSwitcherController(final Context mContext, final KeyguardStateController mKeyguardStateController, final Handler mHandler, final ActivityStarter mActivityStarter, final BroadcastDispatcher mBroadcastDispatcher) {
        this.mAdapters = new ArrayList<WeakReference<BaseUserAdapter>>();
        this.mGuestResumeSessionReceiver = new GuestResumeSessionReceiver();
        this.mUsers = new ArrayList<UserRecord>();
        this.mLastNonGuestUser = 0;
        this.mResumeUserOnGuestLogout = true;
        this.mSecondaryUser = -10000;
        this.mForcePictureLoadForUserId = new SparseBooleanArray(2);
        this.mPhoneStateListener = new PhoneStateListener() {
            private int mCallState;
            
            public void onCallStateChanged(final int mCallState, final String s) {
                if (this.mCallState == mCallState) {
                    return;
                }
                this.mCallState = mCallState;
                UserSwitcherController.this.refreshUsers(-10000);
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final boolean equals = "android.intent.action.USER_SWITCHED".equals(intent.getAction());
                final boolean b = true;
                final int n = -10000;
                int n3;
                int intExtra2;
                if (equals) {
                    if (UserSwitcherController.this.mExitGuestDialog != null && UserSwitcherController.this.mExitGuestDialog.isShowing()) {
                        UserSwitcherController.this.mExitGuestDialog.cancel();
                        UserSwitcherController.this.mExitGuestDialog = null;
                    }
                    final int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1);
                    final UserInfo userInfo = UserSwitcherController.this.mUserManager.getUserInfo(intExtra);
                    int n2;
                    for (int size = UserSwitcherController.this.mUsers.size(), i = 0; i < size; i = n2 + 1) {
                        final UserRecord userRecord = UserSwitcherController.this.mUsers.get(i);
                        final UserInfo info = userRecord.info;
                        if (info == null) {
                            n2 = i;
                        }
                        else {
                            final boolean b2 = info.id == intExtra;
                            if (userRecord.isCurrent != b2) {
                                UserSwitcherController.this.mUsers.set(i, userRecord.copyWithIsCurrent(b2));
                            }
                            if (b2 && !userRecord.isGuest) {
                                UserSwitcherController.this.mLastNonGuestUser = userRecord.info.id;
                            }
                            if (userInfo != null) {
                                n2 = i;
                                if (userInfo.isAdmin()) {
                                    continue;
                                }
                            }
                            n2 = i;
                            if (userRecord.isRestricted) {
                                UserSwitcherController.this.mUsers.remove(i);
                                n2 = i - 1;
                            }
                        }
                    }
                    UserSwitcherController.this.notifyAdapters();
                    if (UserSwitcherController.this.mSecondaryUser != -10000) {
                        context.stopServiceAsUser(UserSwitcherController.this.mSecondaryUserServiceIntent, UserHandle.of(UserSwitcherController.this.mSecondaryUser));
                        UserSwitcherController.this.mSecondaryUser = -10000;
                    }
                    n3 = (b ? 1 : 0);
                    intExtra2 = n;
                    if (userInfo != null) {
                        n3 = (b ? 1 : 0);
                        intExtra2 = n;
                        if (userInfo.id != 0) {
                            context.startServiceAsUser(UserSwitcherController.this.mSecondaryUserServiceIntent, UserHandle.of(userInfo.id));
                            UserSwitcherController.this.mSecondaryUser = userInfo.id;
                            n3 = (b ? 1 : 0);
                            intExtra2 = n;
                        }
                    }
                }
                else {
                    if ("android.intent.action.USER_INFO_CHANGED".equals(intent.getAction())) {
                        intExtra2 = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                    }
                    else {
                        intExtra2 = n;
                        if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                            intExtra2 = n;
                            if (intent.getIntExtra("android.intent.extra.user_handle", -10000) != 0) {
                                return;
                            }
                        }
                    }
                    n3 = 0;
                }
                UserSwitcherController.this.refreshUsers(intExtra2);
                if (n3 != 0) {
                    UserSwitcherController.this.mUnpauseRefreshUsers.run();
                }
            }
        };
        this.mUnpauseRefreshUsers = new Runnable() {
            @Override
            public void run() {
                UserSwitcherController.this.mHandler.removeCallbacks((Runnable)this);
                UserSwitcherController.this.mPauseRefreshUsers = false;
                UserSwitcherController.this.refreshUsers(-10000);
            }
        };
        this.mSettingsObserver = new ContentObserver(new Handler()) {
            public void onChange(final boolean b) {
                final UserSwitcherController this$0 = UserSwitcherController.this;
                this$0.mSimpleUserSwitcher = this$0.shouldUseSimpleUserSwitcher();
                final UserSwitcherController this$2 = UserSwitcherController.this;
                final ContentResolver contentResolver = this$2.mContext.getContentResolver();
                boolean b2 = false;
                if (Settings$Global.getInt(contentResolver, "add_users_when_locked", 0) != 0) {
                    b2 = true;
                }
                this$2.mAddUsersWhenLocked = b2;
                UserSwitcherController.this.refreshUsers(-10000);
            }
        };
        this.userDetailAdapter = new DetailAdapter() {
            private final Intent USER_SETTINGS_INTENT = new Intent("android.settings.USER_SETTINGS");
            
            @Override
            public View createDetailView(final Context context, final View view, final ViewGroup viewGroup) {
                UserDetailView inflate;
                if (!(view instanceof UserDetailView)) {
                    inflate = UserDetailView.inflate(context, viewGroup, false);
                    inflate.createAndSetAdapter(UserSwitcherController.this);
                }
                else {
                    inflate = (UserDetailView)view;
                }
                inflate.refreshAdapter();
                return (View)inflate;
            }
            
            @Override
            public int getMetricsCategory() {
                return 125;
            }
            
            @Override
            public Intent getSettingsIntent() {
                return this.USER_SETTINGS_INTENT;
            }
            
            @Override
            public CharSequence getTitle() {
                return UserSwitcherController.this.mContext.getString(R$string.quick_settings_user_title);
            }
            
            @Override
            public Boolean getToggleState() {
                return null;
            }
            
            @Override
            public void setToggleState(final boolean b) {
            }
        };
        this.mCallback = new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardShowingChanged() {
                if (!UserSwitcherController.this.mKeyguardStateController.isShowing()) {
                    final UserSwitcherController this$0 = UserSwitcherController.this;
                    this$0.mHandler.post((Runnable)new _$$Lambda$UserSwitcherController$7$pQr4FiWnaYmK1LUVjgYn_vNV4vI(this$0));
                }
                else {
                    UserSwitcherController.this.notifyAdapters();
                }
            }
        };
        this.mContext = mContext;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        if (!UserManager.isGuestUserEphemeral()) {
            this.mGuestResumeSessionReceiver.register(this.mBroadcastDispatcher);
        }
        this.mKeyguardStateController = mKeyguardStateController;
        this.mHandler = mHandler;
        this.mActivityStarter = mActivityStarter;
        this.mUserManager = UserManager.get(mContext);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_STOPPED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter, null, UserHandle.SYSTEM);
        this.mSimpleUserSwitcher = this.shouldUseSimpleUserSwitcher();
        this.mSecondaryUserServiceIntent = new Intent(mContext, (Class)SystemUISecondaryUserService.class);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.SYSTEM, new IntentFilter(), "com.android.systemui.permission.SELF", (Handler)null);
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("lockscreenSimpleUserSwitcher"), true, this.mSettingsObserver);
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("add_users_when_locked"), true, this.mSettingsObserver);
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("allow_user_switching_when_system_user_locked"), true, this.mSettingsObserver);
        this.mSettingsObserver.onChange(false);
        mKeyguardStateController.addCallback(this.mCallback);
        this.listenForCallState();
        this.refreshUsers(-10000);
    }
    
    private void checkIfAddUserDisallowedByAdminOnly(final UserRecord userRecord) {
        final RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_add_user", ActivityManager.getCurrentUser());
        if (checkIfRestrictionEnforced != null && !RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_add_user", ActivityManager.getCurrentUser())) {
            userRecord.isDisabledByAdmin = true;
            userRecord.enforcedAdmin = checkIfRestrictionEnforced;
        }
        else {
            userRecord.isDisabledByAdmin = false;
            userRecord.enforcedAdmin = null;
        }
    }
    
    private void listenForCallState() {
        final TelephonyManager telephonyManager = (TelephonyManager)this.mContext.getSystemService("phone");
        if (telephonyManager != null) {
            telephonyManager.listen(this.mPhoneStateListener, 32);
        }
    }
    
    private void notifyAdapters() {
        for (int i = this.mAdapters.size() - 1; i >= 0; --i) {
            final BaseUserAdapter baseUserAdapter = this.mAdapters.get(i).get();
            if (baseUserAdapter != null) {
                baseUserAdapter.notifyDataSetChanged();
            }
            else {
                this.mAdapters.remove(i);
            }
        }
    }
    
    private void pauseRefreshUsers() {
        if (!this.mPauseRefreshUsers) {
            this.mHandler.postDelayed(this.mUnpauseRefreshUsers, 3000L);
            this.mPauseRefreshUsers = true;
        }
    }
    
    private void refreshUsers(int i) {
        if (i != -10000) {
            this.mForcePictureLoadForUserId.put(i, true);
        }
        if (this.mPauseRefreshUsers) {
            return;
        }
        final boolean value = this.mForcePictureLoadForUserId.get(-1);
        final SparseArray sparseArray = new SparseArray(this.mUsers.size());
        int size;
        UserRecord userRecord;
        UserInfo info;
        for (size = this.mUsers.size(), i = 0; i < size; ++i) {
            userRecord = this.mUsers.get(i);
            if (userRecord != null && userRecord.picture != null) {
                info = userRecord.info;
                if (info != null && !value) {
                    if (!this.mForcePictureLoadForUserId.get(info.id)) {
                        sparseArray.put(userRecord.info.id, (Object)userRecord.picture);
                    }
                }
            }
        }
        this.mForcePictureLoadForUserId.clear();
        new AsyncTask<SparseArray<Bitmap>, Void, ArrayList<UserRecord>>() {
            final /* synthetic */ boolean val$addUsersWhenLocked = UserSwitcherController.this.mAddUsersWhenLocked;
            
            protected ArrayList<UserRecord> doInBackground(final SparseArray<Bitmap>... array) {
                final boolean b = false;
                final SparseArray<Bitmap> sparseArray = array[0];
                final List users = UserSwitcherController.this.mUserManager.getUsers(true);
                Object e = null;
                if (users == null) {
                    return null;
                }
                final ArrayList list = new ArrayList<UserRecord>(users.size());
                final int currentUser = ActivityManager.getCurrentUser();
                final boolean b2 = UserSwitcherController.this.mUserManager.getUserSwitchability(UserHandle.of(ActivityManager.getCurrentUser())) == 0;
                final Iterator<UserInfo> iterator = users.iterator();
                UserInfo userInfo = null;
                while (iterator.hasNext()) {
                    final UserInfo userInfo2 = iterator.next();
                    final boolean b3 = currentUser == userInfo2.id;
                    if (b3) {
                        userInfo = userInfo2;
                    }
                    final boolean b4 = b2 || b3;
                    Object o = e;
                    if (userInfo2.isEnabled()) {
                        if (userInfo2.isGuest()) {
                            o = new UserRecord(userInfo2, null, true, b3, false, false, b2);
                        }
                        else {
                            o = e;
                            if (userInfo2.supportsSwitchToByUser()) {
                                Bitmap scaledBitmap;
                                if ((scaledBitmap = (Bitmap)sparseArray.get(userInfo2.id)) == null) {
                                    final Bitmap userIcon = UserSwitcherController.this.mUserManager.getUserIcon(userInfo2.id);
                                    if ((scaledBitmap = userIcon) != null) {
                                        final int dimensionPixelSize = UserSwitcherController.this.mContext.getResources().getDimensionPixelSize(R$dimen.max_avatar_size);
                                        scaledBitmap = Bitmap.createScaledBitmap(userIcon, dimensionPixelSize, dimensionPixelSize, true);
                                    }
                                }
                                list.add(new UserRecord(userInfo2, scaledBitmap, false, b3, false, false, b4));
                                o = e;
                            }
                        }
                    }
                    e = o;
                }
                if (list.size() > 1 || e != null) {
                    Prefs.putBoolean(UserSwitcherController.this.mContext, "HasSeenMultiUser", true);
                }
                final boolean b5 = UserSwitcherController.this.mUserManager.hasBaseUserRestriction("no_add_user", UserHandle.SYSTEM) ^ true;
                final boolean b6 = userInfo != null && (userInfo.isAdmin() || userInfo.id == 0) && b5;
                final boolean b7 = b5 && this.val$addUsersWhenLocked;
                final boolean b8 = (b6 || b7) && e == null;
                int n = 0;
                Label_0482: {
                    if (!b6) {
                        n = (b ? 1 : 0);
                        if (!b7) {
                            break Label_0482;
                        }
                    }
                    n = (b ? 1 : 0);
                    if (UserSwitcherController.this.mUserManager.canAddMoreUsers()) {
                        n = 1;
                    }
                }
                final boolean b9 = this.val$addUsersWhenLocked ^ true;
                if (e == null) {
                    if (b8) {
                        final UserRecord e2 = new UserRecord(null, null, true, false, false, b9, b2);
                        UserSwitcherController.this.checkIfAddUserDisallowedByAdminOnly(e2);
                        list.add(e2);
                    }
                }
                else {
                    list.add((UserRecord)e);
                }
                if (n != 0) {
                    final UserRecord e3 = new UserRecord(null, null, false, false, true, b9, b2);
                    UserSwitcherController.this.checkIfAddUserDisallowedByAdminOnly(e3);
                    list.add(e3);
                }
                return (ArrayList<UserRecord>)list;
            }
            
            protected void onPostExecute(final ArrayList<UserRecord> list) {
                if (list != null) {
                    UserSwitcherController.this.mUsers = list;
                    UserSwitcherController.this.notifyAdapters();
                }
            }
        }.execute((Object[])new SparseArray[] { sparseArray });
    }
    
    private boolean shouldUseSimpleUserSwitcher() {
        return Settings$Global.getInt(this.mContext.getContentResolver(), "lockscreenSimpleUserSwitcher", (int)(this.mContext.getResources().getBoolean(17891458) ? 1 : 0)) != 0;
    }
    
    private void showExitGuestDialog(final int n) {
        int id = 0;
        Label_0053: {
            if (this.mResumeUserOnGuestLogout) {
                final int mLastNonGuestUser = this.mLastNonGuestUser;
                if (mLastNonGuestUser != 0) {
                    final UserInfo userInfo = this.mUserManager.getUserInfo(mLastNonGuestUser);
                    if (userInfo != null && userInfo.isEnabled() && userInfo.supportsSwitchToByUser()) {
                        id = userInfo.id;
                        break Label_0053;
                    }
                }
            }
            id = 0;
        }
        this.showExitGuestDialog(n, id);
    }
    
    @VisibleForTesting
    public void addAdapter(final WeakReference<BaseUserAdapter> e) {
        this.mAdapters.add(e);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("UserSwitcherController state:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mLastNonGuestUser=");
        sb.append(this.mLastNonGuestUser);
        printWriter.println(sb.toString());
        printWriter.print("  mUsers.size=");
        printWriter.println(this.mUsers.size());
        for (int i = 0; i < this.mUsers.size(); ++i) {
            final UserRecord userRecord = this.mUsers.get(i);
            printWriter.print("    ");
            printWriter.println(userRecord.toString());
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("mSimpleUserSwitcher=");
        sb2.append(this.mSimpleUserSwitcher);
        printWriter.println(sb2.toString());
    }
    
    protected void exitGuest(final int n, final int n2) {
        this.switchToUserId(n2);
        this.mUserManager.removeUser(n);
    }
    
    public String getCurrentUserName(final Context context) {
        if (this.mUsers.isEmpty()) {
            return null;
        }
        final UserRecord userRecord = this.mUsers.get(0);
        if (userRecord != null) {
            final UserInfo info = userRecord.info;
            if (info != null) {
                if (userRecord.isGuest) {
                    return context.getString(com.android.settingslib.R$string.guest_nickname);
                }
                return info.name;
            }
        }
        return null;
    }
    
    public int getSwitchableUserCount() {
        final int size = this.mUsers.size();
        int i = 0;
        int n = 0;
        while (i < size) {
            final UserInfo info = this.mUsers.get(i).info;
            int n2 = n;
            if (info != null) {
                n2 = n;
                if (info.supportsSwitchToByUser()) {
                    n2 = n + 1;
                }
            }
            ++i;
            n = n2;
        }
        return n;
    }
    
    @VisibleForTesting
    public ArrayList<UserRecord> getUsers() {
        return this.mUsers;
    }
    
    public boolean isSimpleUserSwitcher() {
        return this.mSimpleUserSwitcher;
    }
    
    public void onDensityOrFontScaleChanged() {
        this.refreshUsers(-1);
    }
    
    public void showAddUserDialog() {
        final Dialog mAddUserDialog = this.mAddUserDialog;
        if (mAddUserDialog != null && mAddUserDialog.isShowing()) {
            this.mAddUserDialog.cancel();
        }
        (this.mAddUserDialog = (Dialog)new AddUserDialog(this.mContext)).show();
    }
    
    protected void showExitGuestDialog(final int n, final int n2) {
        final Dialog mExitGuestDialog = this.mExitGuestDialog;
        if (mExitGuestDialog != null && mExitGuestDialog.isShowing()) {
            this.mExitGuestDialog.cancel();
        }
        (this.mExitGuestDialog = (Dialog)new ExitGuestDialog(this.mContext, n, n2)).show();
    }
    
    public void startActivity(final Intent intent) {
        this.mActivityStarter.startActivity(intent, true);
    }
    
    public void switchTo(final UserRecord userRecord) {
        int n;
        if (userRecord.isGuest && userRecord.info == null) {
            final UserManager mUserManager = this.mUserManager;
            final Context mContext = this.mContext;
            final UserInfo guest = mUserManager.createGuest(mContext, mContext.getString(com.android.settingslib.R$string.guest_nickname));
            if (guest == null) {
                return;
            }
            n = guest.id;
        }
        else {
            if (userRecord.isAddUser) {
                this.showAddUserDialog();
                return;
            }
            n = userRecord.info.id;
        }
        final int currentUser = ActivityManager.getCurrentUser();
        if (currentUser == n) {
            if (userRecord.isGuest) {
                this.showExitGuestDialog(n);
            }
            return;
        }
        if (UserManager.isGuestUserEphemeral()) {
            final UserInfo userInfo = this.mUserManager.getUserInfo(currentUser);
            if (userInfo != null && userInfo.isGuest()) {
                this.showExitGuestDialog(currentUser, userRecord.resolveId());
                return;
            }
        }
        this.switchToUserId(n);
    }
    
    protected void switchToUserId(final int n) {
        try {
            this.pauseRefreshUsers();
            ActivityManager.getService().switchUser(n);
        }
        catch (RemoteException ex) {
            Log.e("UserSwitcherController", "Couldn't switch user.", (Throwable)ex);
        }
    }
    
    public boolean useFullscreenUserSwitcher() {
        final int intValue = DejankUtils.whitelistIpcs((Supplier<Integer>)new _$$Lambda$UserSwitcherController$gJeQLk7uUPWe8l2LAzLToqB_eJo(this));
        if (intValue != -1) {
            return intValue != 0;
        }
        return this.mContext.getResources().getBoolean(R$bool.config_enableFullscreenUserSwitcher);
    }
    
    private final class AddUserDialog extends SystemUIDialog implements DialogInterface$OnClickListener
    {
        public AddUserDialog(final Context context) {
            super(context);
            this.setTitle(R$string.user_add_user_title);
            this.setMessage((CharSequence)context.getString(R$string.user_add_user_message_short));
            this.setButton(-2, (CharSequence)context.getString(17039360), (DialogInterface$OnClickListener)this);
            this.setButton(-1, (CharSequence)context.getString(17039370), (DialogInterface$OnClickListener)this);
            SystemUIDialog.setWindowOnTop((Dialog)this);
        }
        
        public void onClick(final DialogInterface dialogInterface, int id) {
            if (id == -2) {
                this.cancel();
            }
            else {
                this.dismiss();
                if (ActivityManager.isUserAMonkey()) {
                    return;
                }
                final UserSwitcherController this$0 = UserSwitcherController.this;
                final UserInfo user = this$0.mUserManager.createUser(this$0.mContext.getString(R$string.user_new_user_name), 0);
                if (user == null) {
                    return;
                }
                id = user.id;
                UserSwitcherController.this.mUserManager.setUserIcon(id, UserIcons.convertToBitmap(UserIcons.getDefaultUserIcon(UserSwitcherController.this.mContext.getResources(), id, false)));
                UserSwitcherController.this.switchToUserId(id);
            }
        }
    }
    
    public abstract static class BaseUserAdapter extends BaseAdapter
    {
        final UserSwitcherController mController;
        private final KeyguardStateController mKeyguardStateController;
        
        protected BaseUserAdapter(final UserSwitcherController mController) {
            this.mController = mController;
            this.mKeyguardStateController = mController.mKeyguardStateController;
            mController.addAdapter(new WeakReference<BaseUserAdapter>(this));
        }
        
        protected static Drawable getIconDrawable(final Context context, final UserRecord userRecord) {
            int n;
            if (userRecord.isAddUser) {
                n = R$drawable.ic_add_circle;
            }
            else if (userRecord.isGuest) {
                n = R$drawable.ic_avatar_guest_user;
            }
            else {
                n = R$drawable.ic_avatar_user;
            }
            return context.getDrawable(n);
        }
        
        public int getCount() {
            final boolean showing = this.mKeyguardStateController.isShowing();
            final int n = 0;
            if (!showing || !this.mKeyguardStateController.isMethodSecure() || this.mKeyguardStateController.canDismissLockScreen()) {
                return this.mController.getUsers().size();
            }
            final int size = this.mController.getUsers().size();
            int n2 = 0;
            for (int index = n; index < size && !((UserRecord)this.mController.getUsers().get(index)).isRestricted; ++index) {
                ++n2;
            }
            return n2;
        }
        
        public UserRecord getItem(final int index) {
            return (UserRecord)this.mController.getUsers().get(index);
        }
        
        public long getItemId(final int n) {
            return n;
        }
        
        public String getName(final Context context, final UserRecord userRecord) {
            if (userRecord.isGuest) {
                if (userRecord.isCurrent) {
                    return context.getString(com.android.settingslib.R$string.guest_exit_guest);
                }
                int n;
                if (userRecord.info == null) {
                    n = com.android.settingslib.R$string.guest_new_guest;
                }
                else {
                    n = com.android.settingslib.R$string.guest_nickname;
                }
                return context.getString(n);
            }
            else {
                if (userRecord.isAddUser) {
                    return context.getString(R$string.user_add_user);
                }
                return userRecord.info.name;
            }
        }
        
        public void refresh() {
            this.mController.refreshUsers(-10000);
        }
        
        public void switchTo(final UserRecord userRecord) {
            this.mController.switchTo(userRecord);
        }
    }
    
    private final class ExitGuestDialog extends SystemUIDialog implements DialogInterface$OnClickListener
    {
        private final int mGuestId;
        private final int mTargetId;
        
        public ExitGuestDialog(final Context context, final int mGuestId, final int mTargetId) {
            super(context);
            this.setTitle(R$string.guest_exit_guest_dialog_title);
            this.setMessage((CharSequence)context.getString(R$string.guest_exit_guest_dialog_message));
            this.setButton(-2, (CharSequence)context.getString(17039360), (DialogInterface$OnClickListener)this);
            this.setButton(-1, (CharSequence)context.getString(R$string.guest_exit_guest_dialog_remove), (DialogInterface$OnClickListener)this);
            SystemUIDialog.setWindowOnTop((Dialog)this);
            this.setCanceledOnTouchOutside(false);
            this.mGuestId = mGuestId;
            this.mTargetId = mTargetId;
        }
        
        public void onClick(final DialogInterface dialogInterface, final int n) {
            if (n == -2) {
                this.cancel();
            }
            else {
                this.dismiss();
                UserSwitcherController.this.exitGuest(this.mGuestId, this.mTargetId);
            }
        }
    }
    
    public static final class UserRecord
    {
        public RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        public final UserInfo info;
        public final boolean isAddUser;
        public final boolean isCurrent;
        public boolean isDisabledByAdmin;
        public final boolean isGuest;
        public final boolean isRestricted;
        public boolean isSwitchToEnabled;
        public final Bitmap picture;
        
        public UserRecord(final UserInfo info, final Bitmap picture, final boolean isGuest, final boolean isCurrent, final boolean isAddUser, final boolean isRestricted, final boolean isSwitchToEnabled) {
            this.info = info;
            this.picture = picture;
            this.isGuest = isGuest;
            this.isCurrent = isCurrent;
            this.isAddUser = isAddUser;
            this.isRestricted = isRestricted;
            this.isSwitchToEnabled = isSwitchToEnabled;
        }
        
        public UserRecord copyWithIsCurrent(final boolean b) {
            return new UserRecord(this.info, this.picture, this.isGuest, b, this.isAddUser, this.isRestricted, this.isSwitchToEnabled);
        }
        
        public int resolveId() {
            if (!this.isGuest) {
                final UserInfo info = this.info;
                if (info != null) {
                    return info.id;
                }
            }
            return -10000;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("UserRecord(");
            if (this.info != null) {
                sb.append("name=\"");
                sb.append(this.info.name);
                sb.append("\" id=");
                sb.append(this.info.id);
            }
            else if (this.isGuest) {
                sb.append("<add guest placeholder>");
            }
            else if (this.isAddUser) {
                sb.append("<add user placeholder>");
            }
            if (this.isGuest) {
                sb.append(" <isGuest>");
            }
            if (this.isAddUser) {
                sb.append(" <isAddUser>");
            }
            if (this.isCurrent) {
                sb.append(" <isCurrent>");
            }
            if (this.picture != null) {
                sb.append(" <hasPicture>");
            }
            if (this.isRestricted) {
                sb.append(" <isRestricted>");
            }
            if (this.isDisabledByAdmin) {
                sb.append(" <isDisabledByAdmin>");
                sb.append(" enforcedAdmin=");
                sb.append(this.enforcedAdmin);
            }
            if (this.isSwitchToEnabled) {
                sb.append(" <isSwitchToEnabled>");
            }
            sb.append(')');
            return sb.toString();
        }
    }
}
