// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import com.android.systemui.R$string;
import android.view.View;
import java.util.concurrent.Executor;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.internal.annotations.VisibleForTesting;
import android.app.admin.DevicePolicyManager;
import android.graphics.Color;
import android.app.ActivityManager$TaskDescription;
import android.os.Parcelable;
import android.app.PendingIntent;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.app.KeyguardManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.app.Activity;

public class WorkLockActivity extends Activity
{
    private final BroadcastDispatcher mBroadcastDispatcher;
    private KeyguardManager mKgm;
    private final BroadcastReceiver mLockEventReceiver;
    
    public WorkLockActivity(final BroadcastDispatcher mBroadcastDispatcher) {
        this.mLockEventReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final int targetUserId = WorkLockActivity.this.getTargetUserId();
                if (intent.getIntExtra("android.intent.extra.user_handle", targetUserId) == targetUserId && !WorkLockActivity.this.getKeyguardManager().isDeviceLocked(targetUserId)) {
                    WorkLockActivity.this.finish();
                }
            }
        };
        this.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    private KeyguardManager getKeyguardManager() {
        if (this.mKgm == null) {
            this.mKgm = (KeyguardManager)this.getSystemService("keyguard");
        }
        return this.mKgm;
    }
    
    private void goToHomeScreen() {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        this.startActivity(intent);
    }
    
    private void showConfirmCredentialActivity() {
        if (!this.isFinishing()) {
            if (this.getKeyguardManager().isDeviceLocked(this.getTargetUserId())) {
                final Intent confirmDeviceCredentialIntent = this.getKeyguardManager().createConfirmDeviceCredentialIntent((CharSequence)null, (CharSequence)null, this.getTargetUserId(), true);
                if (confirmDeviceCredentialIntent == null) {
                    return;
                }
                final ActivityOptions basic = ActivityOptions.makeBasic();
                basic.setLaunchTaskId(this.getTaskId());
                final PendingIntent activity = PendingIntent.getActivity((Context)this, -1, this.getIntent(), 1409286144, basic.toBundle());
                if (activity != null) {
                    confirmDeviceCredentialIntent.putExtra("android.intent.extra.INTENT", (Parcelable)activity.getIntentSender());
                }
                final ActivityOptions basic2 = ActivityOptions.makeBasic();
                basic2.setLaunchTaskId(this.getTaskId());
                basic2.setTaskOverlay(true, true);
                this.startActivityForResult(confirmDeviceCredentialIntent, 1, basic2.toBundle());
            }
        }
    }
    
    @VisibleForTesting
    final int getPrimaryColor() {
        final ActivityManager$TaskDescription activityManager$TaskDescription = (ActivityManager$TaskDescription)this.getIntent().getExtra("com.android.systemui.keyguard.extra.TASK_DESCRIPTION");
        if (activityManager$TaskDescription != null && Color.alpha(activityManager$TaskDescription.getPrimaryColor()) == 255) {
            return activityManager$TaskDescription.getPrimaryColor();
        }
        return ((DevicePolicyManager)this.getSystemService("device_policy")).getOrganizationColorForUser(this.getTargetUserId());
    }
    
    @VisibleForTesting
    final int getTargetUserId() {
        return this.getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
    }
    
    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        if (n == 1 && n2 != -1) {
            this.goToHomeScreen();
        }
    }
    
    public void onBackPressed() {
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mBroadcastDispatcher.registerReceiver(this.mLockEventReceiver, new IntentFilter("android.intent.action.DEVICE_LOCKED_CHANGED"), null, UserHandle.ALL);
        if (!this.getKeyguardManager().isDeviceLocked(this.getTargetUserId())) {
            this.finish();
            return;
        }
        this.setOverlayWithDecorCaptionEnabled(true);
        final View contentView = new View((Context)this);
        contentView.setContentDescription((CharSequence)this.getString(R$string.accessibility_desc_work_lock));
        contentView.setBackgroundColor(this.getPrimaryColor());
        this.setContentView(contentView);
    }
    
    public void onDestroy() {
        this.unregisterBroadcastReceiver();
        super.onDestroy();
    }
    
    public void onWindowFocusChanged(final boolean b) {
        if (b) {
            this.showConfirmCredentialActivity();
        }
    }
    
    public void setTaskDescription(final ActivityManager$TaskDescription activityManager$TaskDescription) {
    }
    
    @VisibleForTesting
    protected void unregisterBroadcastReceiver() {
        this.mBroadcastDispatcher.unregisterReceiver(this.mLockEventReceiver);
    }
}
