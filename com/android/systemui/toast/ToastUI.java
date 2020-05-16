// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.toast;

import android.view.View;
import android.os.UserHandle;
import android.util.Log;
import java.util.Objects;
import android.os.IBinder;
import com.android.internal.annotations.VisibleForTesting;
import android.content.res.Resources;
import android.view.accessibility.IAccessibilityManager$Stub;
import android.app.INotificationManager$Stub;
import android.os.ServiceManager;
import android.content.Context;
import android.widget.ToastPresenter;
import android.app.INotificationManager;
import android.app.ITransientNotificationCallback;
import android.view.accessibility.IAccessibilityManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class ToastUI extends SystemUI implements Callbacks
{
    private final IAccessibilityManager mAccessibilityManager;
    private ITransientNotificationCallback mCallback;
    private final CommandQueue mCommandQueue;
    private final int mGravity;
    private final INotificationManager mNotificationManager;
    private ToastPresenter mPresenter;
    private final int mY;
    
    public ToastUI(final Context context, final CommandQueue commandQueue) {
        this(context, commandQueue, INotificationManager$Stub.asInterface(ServiceManager.getService("notification")), IAccessibilityManager$Stub.asInterface(ServiceManager.getService("accessibility")));
    }
    
    @VisibleForTesting
    ToastUI(final Context context, final CommandQueue mCommandQueue, final INotificationManager mNotificationManager, final IAccessibilityManager mAccessibilityManager) {
        super(context);
        this.mCommandQueue = mCommandQueue;
        this.mNotificationManager = mNotificationManager;
        this.mAccessibilityManager = mAccessibilityManager;
        final Resources resources = super.mContext.getResources();
        this.mGravity = resources.getInteger(17694908);
        this.mY = resources.getDimensionPixelSize(17105527);
    }
    
    private void hideCurrentToast() {
        this.mPresenter.hide(this.mCallback);
        this.mPresenter = null;
    }
    
    @Override
    public void hideToast(final String s, final IBinder b) {
        final ToastPresenter mPresenter = this.mPresenter;
        if (mPresenter != null && Objects.equals(mPresenter.getPackageName(), s) && Objects.equals(this.mPresenter.getToken(), b)) {
            this.hideCurrentToast();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Attempt to hide non-current toast from package ");
        sb.append(s);
        Log.w("ToastUI", sb.toString());
    }
    
    @Override
    public void showToast(final int n, final String s, final IBinder binder, final CharSequence charSequence, final IBinder binder2, final int n2, final ITransientNotificationCallback mCallback) {
        if (this.mPresenter != null) {
            this.hideCurrentToast();
        }
        final Context contextAsUser = super.mContext.createContextAsUser(UserHandle.getUserHandleForUid(n), 0);
        final View textToastView = ToastPresenter.getTextToastView(contextAsUser, charSequence);
        this.mCallback = mCallback;
        (this.mPresenter = new ToastPresenter(contextAsUser, this.mAccessibilityManager, this.mNotificationManager, s)).show(textToastView, binder, binder2, n2, this.mGravity, 0, this.mY, 0.0f, 0.0f, this.mCallback);
    }
    
    @Override
    public void start() {
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
    }
}
