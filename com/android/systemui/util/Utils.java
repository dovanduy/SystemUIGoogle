// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.view.View;
import android.view.View$OnAttachStateChangeListener;
import com.android.systemui.statusbar.CommandQueue;
import android.content.ContentResolver;
import android.provider.Settings$System;
import java.util.function.Consumer;
import java.util.List;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.android.systemui.shared.system.QuickStepContract;
import android.content.Context;

public class Utils
{
    public static boolean isGesturalModeOnDefaultDisplay(final Context context, final int n) {
        return context.getDisplayId() == 0 && QuickStepContract.isGesturalMode(n);
    }
    
    public static boolean isHeadlessRemoteDisplayProvider(final PackageManager packageManager, final String package1) {
        if (packageManager.checkPermission("android.permission.REMOTE_DISPLAY_PROVIDER", package1) != 0) {
            return false;
        }
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(package1);
        return packageManager.queryIntentActivities(intent, 0).isEmpty();
    }
    
    public static <T> void safeForeach(final List<T> list, final Consumer<T> consumer) {
        for (int i = list.size() - 1; i >= 0; --i) {
            final T value = list.get(i);
            if (value != null) {
                consumer.accept(value);
            }
        }
    }
    
    public static boolean useQsMediaPlayer(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$System.getInt(contentResolver, "qs_media_player", 0) > 0) {
            b = true;
        }
        return b;
    }
    
    public static class DisableStateTracker implements Callbacks, View$OnAttachStateChangeListener
    {
        private final CommandQueue mCommandQueue;
        private boolean mDisabled;
        private final int mMask1;
        private final int mMask2;
        private View mView;
        
        public DisableStateTracker(final int mMask1, final int mMask2, final CommandQueue mCommandQueue) {
            this.mMask1 = mMask1;
            this.mMask2 = mMask2;
            this.mCommandQueue = mCommandQueue;
        }
        
        @Override
        public void disable(int visibility, final int n, final int n2, final boolean b) {
            if (visibility != this.mView.getDisplay().getDisplayId()) {
                return;
            }
            final int mMask1 = this.mMask1;
            visibility = 0;
            final boolean mDisabled = (mMask1 & n) != 0x0 || (this.mMask2 & n2) != 0x0;
            if (mDisabled == this.mDisabled) {
                return;
            }
            this.mDisabled = mDisabled;
            final View mView = this.mView;
            if (mDisabled) {
                visibility = 8;
            }
            mView.setVisibility(visibility);
        }
        
        public void onViewAttachedToWindow(final View mView) {
            this.mView = mView;
            this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
        }
        
        public void onViewDetachedFromWindow(final View view) {
            this.mCommandQueue.removeCallback((CommandQueue.Callbacks)this);
            this.mView = null;
        }
    }
}
