// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.res.ColorStateList;
import java.util.TimerTask;
import android.graphics.drawable.Drawable;
import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.ViewStub;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.R$id;
import com.android.systemui.qs.QuickQSPanel;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.util.Utils;
import android.media.session.MediaSession$Token;
import android.text.format.DateUtils;
import android.metrics.LogMaker;
import com.android.internal.widget.MediaNotificationView;
import android.media.session.PlaybackState;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.internal.widget.MediaNotificationView$VisibilityChangeListener;
import com.android.internal.annotations.VisibleForTesting;
import android.widget.SeekBar$OnSeekBarChangeListener;
import java.util.Timer;
import android.widget.TextView;
import android.widget.SeekBar;
import com.android.internal.logging.MetricsLogger;
import android.media.MediaMetadata;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.media.session.MediaController;
import android.media.session.MediaController$Callback;
import android.os.Handler;
import android.content.Context;
import android.view.View$OnAttachStateChangeListener;
import android.view.View;

public class NotificationMediaTemplateViewWrapper extends NotificationTemplateViewWrapper
{
    private View mActions;
    private View$OnAttachStateChangeListener mAttachStateListener;
    private Context mContext;
    private long mDuration;
    private final Handler mHandler;
    private boolean mIsViewVisible;
    private MediaController$Callback mMediaCallback;
    private MediaController mMediaController;
    private NotificationMediaManager mMediaManager;
    private MediaMetadata mMediaMetadata;
    private MetricsLogger mMetricsLogger;
    protected final Runnable mOnUpdateTimerTick;
    private SeekBar mSeekBar;
    private TextView mSeekBarElapsedTime;
    private Timer mSeekBarTimer;
    private TextView mSeekBarTotalTime;
    private View mSeekBarView;
    @VisibleForTesting
    protected SeekBar$OnSeekBarChangeListener mSeekListener;
    private MediaNotificationView$VisibilityChangeListener mVisibilityListener;
    
    protected NotificationMediaTemplateViewWrapper(final Context mContext, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(mContext, view, expandableNotificationRow);
        this.mHandler = Dependency.get(Dependency.MAIN_HANDLER);
        this.mDuration = 0L;
        this.mSeekListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
                if (NotificationMediaTemplateViewWrapper.this.mMediaController != null) {
                    NotificationMediaTemplateViewWrapper.this.mMediaController.getTransportControls().seekTo((long)NotificationMediaTemplateViewWrapper.this.mSeekBar.getProgress());
                    NotificationMediaTemplateViewWrapper.this.mMetricsLogger.write(NotificationMediaTemplateViewWrapper.this.newLog(6));
                }
            }
        };
        this.mVisibilityListener = (MediaNotificationView$VisibilityChangeListener)new MediaNotificationView$VisibilityChangeListener() {
            public void onAggregatedVisibilityChanged(final boolean b) {
                NotificationMediaTemplateViewWrapper.this.mIsViewVisible = b;
                if (b && NotificationMediaTemplateViewWrapper.this.mMediaController != null) {
                    final PlaybackState playbackState = NotificationMediaTemplateViewWrapper.this.mMediaController.getPlaybackState();
                    if (playbackState != null && playbackState.getState() == 3 && NotificationMediaTemplateViewWrapper.this.mSeekBarTimer == null && NotificationMediaTemplateViewWrapper.this.mSeekBarView != null && NotificationMediaTemplateViewWrapper.this.mSeekBarView.getVisibility() != 8) {
                        NotificationMediaTemplateViewWrapper.this.startTimer();
                    }
                }
                else {
                    NotificationMediaTemplateViewWrapper.this.clearTimer();
                }
            }
        };
        this.mAttachStateListener = (View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
            }
            
            public void onViewDetachedFromWindow(final View view) {
                NotificationMediaTemplateViewWrapper.this.mIsViewVisible = false;
            }
        };
        this.mMediaCallback = new MediaController$Callback() {
            public void onMetadataChanged(final MediaMetadata mediaMetadata) {
                if (NotificationMediaTemplateViewWrapper.this.mMediaMetadata == null || !NotificationMediaTemplateViewWrapper.this.mMediaMetadata.equals((Object)mediaMetadata)) {
                    NotificationMediaTemplateViewWrapper.this.mMediaMetadata = mediaMetadata;
                    NotificationMediaTemplateViewWrapper.this.updateDuration();
                }
            }
            
            public void onPlaybackStateChanged(final PlaybackState playbackState) {
                if (playbackState == null) {
                    return;
                }
                if (playbackState.getState() != 3) {
                    NotificationMediaTemplateViewWrapper.this.updatePlaybackUi(playbackState);
                    NotificationMediaTemplateViewWrapper.this.clearTimer();
                }
                else if (NotificationMediaTemplateViewWrapper.this.mSeekBarTimer == null && NotificationMediaTemplateViewWrapper.this.mSeekBarView != null && NotificationMediaTemplateViewWrapper.this.mSeekBarView.getVisibility() != 8) {
                    NotificationMediaTemplateViewWrapper.this.startTimer();
                }
            }
            
            public void onSessionDestroyed() {
                NotificationMediaTemplateViewWrapper.this.clearTimer();
                NotificationMediaTemplateViewWrapper.this.mMediaController.unregisterCallback((MediaController$Callback)this);
                final NotificationMediaTemplateViewWrapper this$0 = NotificationMediaTemplateViewWrapper.this;
                final View mView = this$0.mView;
                if (mView instanceof MediaNotificationView) {
                    ((MediaNotificationView)mView).removeVisibilityListener(this$0.mVisibilityListener);
                    final NotificationMediaTemplateViewWrapper this$2 = NotificationMediaTemplateViewWrapper.this;
                    this$2.mView.removeOnAttachStateChangeListener(this$2.mAttachStateListener);
                }
            }
        };
        this.mOnUpdateTimerTick = new Runnable() {
            @Override
            public void run() {
                if (NotificationMediaTemplateViewWrapper.this.mMediaController != null && NotificationMediaTemplateViewWrapper.this.mSeekBar != null) {
                    final PlaybackState playbackState = NotificationMediaTemplateViewWrapper.this.mMediaController.getPlaybackState();
                    if (playbackState != null) {
                        NotificationMediaTemplateViewWrapper.this.updatePlaybackUi(playbackState);
                    }
                    else {
                        NotificationMediaTemplateViewWrapper.this.clearTimer();
                    }
                }
                else {
                    NotificationMediaTemplateViewWrapper.this.clearTimer();
                }
            }
        };
        this.mContext = mContext;
        this.mMediaManager = Dependency.get(NotificationMediaManager.class);
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
    }
    
    private boolean canSeekMedia(final PlaybackState playbackState) {
        boolean b = false;
        if (playbackState == null) {
            return false;
        }
        if ((playbackState.getActions() & 0x100L) != 0x0L) {
            b = true;
        }
        return b;
    }
    
    private void clearTimer() {
        final Timer mSeekBarTimer = this.mSeekBarTimer;
        if (mSeekBarTimer != null) {
            mSeekBarTimer.cancel();
            this.mSeekBarTimer.purge();
            this.mSeekBarTimer = null;
        }
    }
    
    private String millisecondsToTimeString(final long n) {
        return DateUtils.formatElapsedTime(n / 1000L);
    }
    
    private LogMaker newLog(final int type) {
        return new LogMaker(1743).setType(type).setPackageName(super.mRow.getEntry().getSbn().getPackageName());
    }
    
    private LogMaker newLog(final int type, final int subtype) {
        return new LogMaker(1743).setType(type).setSubtype(subtype).setPackageName(super.mRow.getEntry().getSbn().getPackageName());
    }
    
    private void resolveViews() {
        this.mActions = super.mView.findViewById(16909139);
        this.mIsViewVisible = super.mView.isShown();
        final MediaSession$Token mediaSession$Token = (MediaSession$Token)super.mRow.getEntry().getSbn().getNotification().extras.getParcelable("android.mediaSession");
        if (Utils.useQsMediaPlayer(this.mContext) && mediaSession$Token != null) {
            final int[] intArray = super.mRow.getEntry().getSbn().getNotification().extras.getIntArray("android.compactActions");
            final int originalIconColor = this.getNotificationHeader().getOriginalIconColor();
            final NotificationShadeWindowController notificationShadeWindowController = Dependency.get(NotificationShadeWindowController.class);
            final QuickQSPanel quickQSPanel = (QuickQSPanel)notificationShadeWindowController.getNotificationShadeView().findViewById(R$id.quick_qs_panel);
            final StatusBarNotification sbn = super.mRow.getEntry().getSbn();
            final Notification notification = sbn.getNotification();
            quickQSPanel.getMediaPlayer().setMediaSession(mediaSession$Token, notification.getSmallIcon(), originalIconColor, super.mBackgroundColor, this.mActions, intArray, notification.contentIntent);
            ((QSPanel)notificationShadeWindowController.getNotificationShadeView().findViewById(R$id.quick_settings_panel)).addMediaSession(mediaSession$Token, notification.getSmallIcon(), originalIconColor, super.mBackgroundColor, this.mActions, sbn);
        }
        final boolean showCompactMediaSeekbar = this.mMediaManager.getShowCompactMediaSeekbar();
        if (mediaSession$Token != null && (!"media".equals(super.mView.getTag()) || showCompactMediaSeekbar)) {
            final MediaController mMediaController = this.mMediaController;
            int n;
            if (mMediaController != null && mMediaController.getSessionToken().equals((Object)mediaSession$Token)) {
                n = 0;
            }
            else {
                final MediaController mMediaController2 = this.mMediaController;
                if (mMediaController2 != null) {
                    mMediaController2.unregisterCallback(this.mMediaCallback);
                }
                this.mMediaController = new MediaController(this.mContext, mediaSession$Token);
                n = 1;
            }
            final MediaMetadata metadata = this.mMediaController.getMetadata();
            this.mMediaMetadata = metadata;
            if (metadata != null) {
                if (metadata.getLong("android.media.metadata.DURATION") <= 0L) {
                    final View mSeekBarView = this.mSeekBarView;
                    if (mSeekBarView != null && mSeekBarView.getVisibility() != 8) {
                        this.mSeekBarView.setVisibility(8);
                        this.mMetricsLogger.write(this.newLog(2));
                        this.clearTimer();
                    }
                    else if (this.mSeekBarView == null && n != 0) {
                        this.mMetricsLogger.write(this.newLog(2));
                    }
                    return;
                }
                final View mSeekBarView2 = this.mSeekBarView;
                if (mSeekBarView2 != null && mSeekBarView2.getVisibility() == 8) {
                    this.mSeekBarView.setVisibility(0);
                    this.mMetricsLogger.write(this.newLog(1));
                    this.updateDuration();
                    this.startTimer();
                }
            }
            final ViewStub viewStub = (ViewStub)super.mView.findViewById(16909224);
            if (viewStub instanceof ViewStub) {
                viewStub.setLayoutInflater(LayoutInflater.from(viewStub.getContext()));
                viewStub.setLayoutResource(17367198);
                this.mSeekBarView = viewStub.inflate();
                this.mMetricsLogger.write(this.newLog(1));
                (this.mSeekBar = (SeekBar)this.mSeekBarView.findViewById(16909222)).setOnSeekBarChangeListener(this.mSeekListener);
                this.mSeekBarElapsedTime = (TextView)this.mSeekBarView.findViewById(16909220);
                this.mSeekBarTotalTime = (TextView)this.mSeekBarView.findViewById(16909225);
                n = 1;
            }
            if (n != 0) {
                final View mView = super.mView;
                if (mView instanceof MediaNotificationView) {
                    ((MediaNotificationView)mView).addVisibilityListener(this.mVisibilityListener);
                    super.mView.addOnAttachStateChangeListener(this.mAttachStateListener);
                }
                if (this.mSeekBarTimer == null) {
                    final MediaController mMediaController3 = this.mMediaController;
                    if (mMediaController3 != null && this.canSeekMedia(mMediaController3.getPlaybackState())) {
                        this.mMetricsLogger.write(this.newLog(3, 1));
                    }
                    else {
                        this.setScrubberVisible(false);
                    }
                    this.updateDuration();
                    this.startTimer();
                    this.mMediaController.registerCallback(this.mMediaCallback);
                }
            }
            this.updateSeekBarTint(this.mSeekBarView);
            return;
        }
        final View mSeekBarView3 = this.mSeekBarView;
        if (mSeekBarView3 != null) {
            mSeekBarView3.setVisibility(8);
        }
    }
    
    private void setScrubberVisible(final boolean enabled) {
        final SeekBar mSeekBar = this.mSeekBar;
        if (mSeekBar != null) {
            if (mSeekBar.isEnabled() != enabled) {
                final Drawable thumb = this.mSeekBar.getThumb();
                int alpha;
                if (enabled) {
                    alpha = 255;
                }
                else {
                    alpha = 0;
                }
                thumb.setAlpha(alpha);
                this.mSeekBar.setEnabled(enabled);
                this.mMetricsLogger.write(this.newLog(3, (int)(enabled ? 1 : 0)));
            }
        }
    }
    
    private void startTimer() {
        this.clearTimer();
        if (this.mIsViewVisible) {
            (this.mSeekBarTimer = new Timer(true)).schedule(new TimerTask() {
                @Override
                public void run() {
                    NotificationMediaTemplateViewWrapper.this.mHandler.post(NotificationMediaTemplateViewWrapper.this.mOnUpdateTimerTick);
                }
            }, 0L, 1000L);
        }
    }
    
    private void updateDuration() {
        final MediaMetadata mMediaMetadata = this.mMediaMetadata;
        if (mMediaMetadata != null && this.mSeekBar != null) {
            final long long1 = mMediaMetadata.getLong("android.media.metadata.DURATION");
            if (this.mDuration != long1) {
                this.mDuration = long1;
                this.mSeekBar.setMax((int)long1);
                this.mSeekBarTotalTime.setText((CharSequence)this.millisecondsToTimeString(long1));
            }
        }
    }
    
    private void updatePlaybackUi(final PlaybackState playbackState) {
        if (this.mSeekBar != null) {
            if (this.mSeekBarElapsedTime != null) {
                final long position = playbackState.getPosition();
                this.mSeekBar.setProgress((int)position);
                this.mSeekBarElapsedTime.setText((CharSequence)this.millisecondsToTimeString(position));
                this.setScrubberVisible(this.canSeekMedia(playbackState));
            }
        }
    }
    
    private void updateSeekBarTint(final View view) {
        if (view == null) {
            return;
        }
        if (this.getNotificationHeader() == null) {
            return;
        }
        final int originalIconColor = this.getNotificationHeader().getOriginalIconColor();
        this.mSeekBarElapsedTime.setTextColor(originalIconColor);
        this.mSeekBarTotalTime.setTextColor(originalIconColor);
        this.mSeekBarTotalTime.setShadowLayer(1.5f, 1.5f, 1.5f, super.mBackgroundColor);
        final ColorStateList value = ColorStateList.valueOf(originalIconColor);
        this.mSeekBar.setThumbTintList(value);
        final ColorStateList withAlpha = value.withAlpha(192);
        this.mSeekBar.setProgressTintList(withAlpha);
        this.mSeekBar.setProgressBackgroundTintList(withAlpha.withAlpha(128));
    }
    
    @Override
    public boolean isDimmable() {
        return this.getCustomBackgroundColor() == 0;
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        this.resolveViews();
        super.onContentUpdated(expandableNotificationRow);
    }
    
    @Override
    public void setRemoved() {
        this.clearTimer();
        final MediaController mMediaController = this.mMediaController;
        if (mMediaController != null) {
            mMediaController.unregisterCallback(this.mMediaCallback);
        }
        final View mView = super.mView;
        if (mView instanceof MediaNotificationView) {
            ((MediaNotificationView)mView).removeVisibilityListener(this.mVisibilityListener);
            super.mView.removeOnAttachStateChangeListener(this.mAttachStateListener);
        }
    }
    
    @Override
    public boolean shouldClipToRounding(final boolean b, final boolean b2) {
        return true;
    }
    
    @Override
    protected void updateTransformedTypes() {
        super.updateTransformedTypes();
        final View mActions = this.mActions;
        if (mActions != null) {
            super.mTransformationHelper.addTransformedView(5, mActions);
        }
    }
}
