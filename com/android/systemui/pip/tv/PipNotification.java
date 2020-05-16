// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv;

import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import android.media.MediaMetadata;
import android.app.Notification$Style;
import android.app.Notification$BigPictureStyle;
import android.text.TextUtils;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.app.Notification$Extender;
import android.app.Notification$TvExtender;
import com.android.systemui.util.NotificationChannels;
import android.util.Log;
import android.content.Intent;
import android.media.session.PlaybackState;
import android.content.pm.ParceledListSlice;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.content.Context;
import android.app.NotificationManager;
import android.app.Notification$Builder;
import android.media.session.MediaController$Callback;
import android.media.session.MediaController;
import android.content.BroadcastReceiver;
import android.graphics.Bitmap;

public class PipNotification
{
    private static final boolean DEBUG;
    private static final String NOTIFICATION_TAG = "PipNotification";
    private Bitmap mArt;
    private int mDefaultIconResId;
    private String mDefaultTitle;
    private final BroadcastReceiver mEventReceiver;
    private MediaController mMediaController;
    private MediaController$Callback mMediaControllerCallback;
    private final Notification$Builder mNotificationBuilder;
    private final NotificationManager mNotificationManager;
    private boolean mNotified;
    private PipManager.Listener mPipListener;
    private final PipManager mPipManager;
    private final PipManager.MediaListener mPipMediaListener;
    private String mTitle;
    
    static {
        DEBUG = PipManager.DEBUG;
    }
    
    public PipNotification(final Context context, final BroadcastDispatcher broadcastDispatcher, final PipManager mPipManager) {
        this.mPipListener = new PipManager.Listener() {
            @Override
            public void onMoveToFullscreen() {
                PipNotification.this.dismissPipNotification();
            }
            
            @Override
            public void onPipActivityClosed() {
                PipNotification.this.dismissPipNotification();
            }
            
            @Override
            public void onPipEntered() {
                PipNotification.this.updateMediaControllerMetadata();
                PipNotification.this.notifyPipNotification();
            }
            
            @Override
            public void onPipMenuActionsChanged(final ParceledListSlice parceledListSlice) {
            }
            
            @Override
            public void onPipResizeAboutToStart() {
            }
            
            @Override
            public void onShowPipMenu() {
            }
        };
        this.mMediaControllerCallback = new MediaController$Callback() {
            public void onPlaybackStateChanged(final PlaybackState playbackState) {
                if (PipNotification.this.updateMediaControllerMetadata() && PipNotification.this.mNotified) {
                    PipNotification.this.notifyPipNotification();
                }
            }
        };
        this.mPipMediaListener = new PipManager.MediaListener() {
            @Override
            public void onMediaControllerChanged() {
                final MediaController mediaController = PipNotification.this.mPipManager.getMediaController();
                if (PipNotification.this.mMediaController == mediaController) {
                    return;
                }
                if (PipNotification.this.mMediaController != null) {
                    PipNotification.this.mMediaController.unregisterCallback(PipNotification.this.mMediaControllerCallback);
                }
                PipNotification.this.mMediaController = mediaController;
                if (PipNotification.this.mMediaController != null) {
                    PipNotification.this.mMediaController.registerCallback(PipNotification.this.mMediaControllerCallback);
                }
                if (PipNotification.this.updateMediaControllerMetadata() && PipNotification.this.mNotified) {
                    PipNotification.this.notifyPipNotification();
                }
            }
        };
        this.mEventReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (PipNotification.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Received ");
                    sb.append(intent.getAction());
                    sb.append(" from the notification UI");
                    Log.d("PipNotification", sb.toString());
                }
                final String action = intent.getAction();
                int n = -1;
                final int hashCode = action.hashCode();
                if (hashCode != -1402086132) {
                    if (hashCode == 1201988555) {
                        if (action.equals("PipNotification.menu")) {
                            n = 0;
                        }
                    }
                }
                else if (action.equals("PipNotification.close")) {
                    n = 1;
                }
                if (n != 0) {
                    if (n == 1) {
                        PipNotification.this.mPipManager.closePip();
                    }
                }
                else {
                    PipNotification.this.mPipManager.showPictureInPictureMenu();
                }
            }
        };
        this.mNotificationManager = (NotificationManager)context.getSystemService("notification");
        this.mNotificationBuilder = new Notification$Builder(context, NotificationChannels.TVPIP).setLocalOnly(true).setOngoing(false).setCategory("sys").extend((Notification$Extender)new Notification$TvExtender().setContentIntent(createPendingIntent(context, "PipNotification.menu")).setDeleteIntent(createPendingIntent(context, "PipNotification.close")));
        (this.mPipManager = mPipManager).addListener(this.mPipListener);
        this.mPipManager.addMediaListener(this.mPipMediaListener);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PipNotification.menu");
        intentFilter.addAction("PipNotification.close");
        broadcastDispatcher.registerReceiver(this.mEventReceiver, intentFilter);
        this.onConfigurationChanged(context);
    }
    
    private static PendingIntent createPendingIntent(final Context context, final String s) {
        return PendingIntent.getBroadcast(context, 0, new Intent(s), 268435456);
    }
    
    private void dismissPipNotification() {
        this.mNotified = false;
        this.mNotificationManager.cancel(PipNotification.NOTIFICATION_TAG, 1100);
    }
    
    private void notifyPipNotification() {
        this.mNotified = true;
        final Notification$Builder setSmallIcon = this.mNotificationBuilder.setShowWhen(true).setWhen(System.currentTimeMillis()).setSmallIcon(this.mDefaultIconResId);
        String contentTitle;
        if (!TextUtils.isEmpty((CharSequence)this.mTitle)) {
            contentTitle = this.mTitle;
        }
        else {
            contentTitle = this.mDefaultTitle;
        }
        setSmallIcon.setContentTitle((CharSequence)contentTitle);
        if (this.mArt != null) {
            this.mNotificationBuilder.setStyle((Notification$Style)new Notification$BigPictureStyle().bigPicture(this.mArt));
        }
        else {
            this.mNotificationBuilder.setStyle((Notification$Style)null);
        }
        this.mNotificationManager.notify(PipNotification.NOTIFICATION_TAG, 1100, this.mNotificationBuilder.build());
    }
    
    private boolean updateMediaControllerMetadata() {
        final MediaController mediaController = this.mPipManager.getMediaController();
        String mTitle = null;
        Bitmap mArt = null;
        Label_0082: {
            if (mediaController != null) {
                final MediaMetadata metadata = this.mPipManager.getMediaController().getMetadata();
                if (metadata != null) {
                    if (TextUtils.isEmpty((CharSequence)(mTitle = metadata.getString("android.media.metadata.DISPLAY_TITLE")))) {
                        mTitle = metadata.getString("android.media.metadata.TITLE");
                    }
                    mArt = metadata.getBitmap("android.media.metadata.ALBUM_ART");
                    if (mArt == null) {
                        mArt = metadata.getBitmap("android.media.metadata.ART");
                    }
                    break Label_0082;
                }
            }
            mArt = null;
        }
        if (TextUtils.equals((CharSequence)mTitle, (CharSequence)this.mTitle) && mArt == this.mArt) {
            return false;
        }
        this.mTitle = mTitle;
        this.mArt = mArt;
        return true;
    }
    
    void onConfigurationChanged(final Context context) {
        this.mDefaultTitle = context.getResources().getString(R$string.pip_notification_unknown_title);
        this.mDefaultIconResId = R$drawable.pip_icon;
        if (this.mNotified) {
            this.notifyPipNotification();
        }
    }
}
