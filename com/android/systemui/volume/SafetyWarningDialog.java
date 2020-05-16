// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.view.KeyEvent;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.res.Resources$NotFoundException;
import android.util.Log;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.media.AudioManager;
import android.content.DialogInterface$OnClickListener;
import android.content.DialogInterface$OnDismissListener;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public abstract class SafetyWarningDialog extends SystemUIDialog implements DialogInterface$OnDismissListener, DialogInterface$OnClickListener
{
    private static final String TAG;
    private final AudioManager mAudioManager;
    private final Context mContext;
    private boolean mDisableOnVolumeUp;
    private boolean mNewVolumeUp;
    private final BroadcastReceiver mReceiver;
    private long mShowTime;
    
    static {
        TAG = Util.logTag(SafetyWarningDialog.class);
    }
    
    public SafetyWarningDialog(final Context mContext, final AudioManager mAudioManager) {
        super(mContext);
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                    if (D.BUG) {
                        Log.d(SafetyWarningDialog.TAG, "Received ACTION_CLOSE_SYSTEM_DIALOGS");
                    }
                    SafetyWarningDialog.this.cancel();
                    SafetyWarningDialog.this.cleanUp();
                }
            }
        };
        this.mContext = mContext;
        this.mAudioManager = mAudioManager;
        try {
            this.mDisableOnVolumeUp = mContext.getResources().getBoolean(17891512);
        }
        catch (Resources$NotFoundException ex) {
            this.mDisableOnVolumeUp = true;
        }
        this.getWindow().setType(2010);
        this.setShowForAllUsers(true);
        this.setMessage((CharSequence)this.mContext.getString(17041175));
        this.setButton(-1, (CharSequence)this.mContext.getString(17039379), (DialogInterface$OnClickListener)this);
        this.setButton(-2, (CharSequence)this.mContext.getString(17039369), (DialogInterface$OnClickListener)null);
        this.setOnDismissListener((DialogInterface$OnDismissListener)this);
        mContext.registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }
    
    protected abstract void cleanUp();
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        this.mAudioManager.disableSafeMediaVolume();
    }
    
    public void onDismiss(final DialogInterface dialogInterface) {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.cleanUp();
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (this.mDisableOnVolumeUp && n == 24 && keyEvent.getRepeatCount() == 0) {
            this.mNewVolumeUp = true;
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (n == 24 && this.mNewVolumeUp && System.currentTimeMillis() - this.mShowTime > 1000L) {
            if (D.BUG) {
                Log.d(SafetyWarningDialog.TAG, "Confirmed warning via VOLUME_UP");
            }
            this.mAudioManager.disableSafeMediaVolume();
            this.dismiss();
        }
        return super.onKeyUp(n, keyEvent);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        this.mShowTime = System.currentTimeMillis();
    }
}
