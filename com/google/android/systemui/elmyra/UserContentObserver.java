// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import android.util.Log;
import android.app.IUserSwitchObserver;
import android.app.ActivityManager;
import android.os.RemoteException;
import android.os.Handler;
import android.app.SynchronousUserSwitchObserver;
import android.content.Context;
import android.net.Uri;
import java.util.function.Consumer;
import android.database.ContentObserver;

public class UserContentObserver extends ContentObserver
{
    private final Consumer<Uri> mCallback;
    private final Context mContext;
    private final Uri mSettingsUri;
    private final SynchronousUserSwitchObserver mUserSwitchCallback;
    
    public UserContentObserver(final Context context, final Uri uri, final Consumer<Uri> consumer) {
        this(context, uri, consumer, true);
    }
    
    public UserContentObserver(final Context mContext, final Uri mSettingsUri, final Consumer<Uri> mCallback, final boolean b) {
        super(new Handler(mContext.getMainLooper()));
        this.mUserSwitchCallback = new SynchronousUserSwitchObserver() {
            public void onUserSwitching(final int n) throws RemoteException {
                UserContentObserver.this.updateContentObserver();
                UserContentObserver.this.mCallback.accept(UserContentObserver.this.mSettingsUri);
            }
        };
        this.mContext = mContext;
        this.mSettingsUri = mSettingsUri;
        this.mCallback = mCallback;
        if (b) {
            this.activate();
        }
    }
    
    private void updateContentObserver() {
        this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this);
        this.mContext.getContentResolver().registerContentObserver(this.mSettingsUri, false, (ContentObserver)this, -2);
    }
    
    public void activate() {
        this.updateContentObserver();
        try {
            ActivityManager.getService().registerUserSwitchObserver((IUserSwitchObserver)this.mUserSwitchCallback, "Elmyra/UserContentObserver");
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/UserContentObserver", "Failed to register user switch observer", (Throwable)ex);
        }
    }
    
    public void deactivate() {
        this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this);
        try {
            ActivityManager.getService().unregisterUserSwitchObserver((IUserSwitchObserver)this.mUserSwitchCallback);
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/UserContentObserver", "Failed to unregister user switch observer", (Throwable)ex);
        }
    }
    
    public void onChange(final boolean b, final Uri uri) {
        this.mCallback.accept(uri);
    }
}
