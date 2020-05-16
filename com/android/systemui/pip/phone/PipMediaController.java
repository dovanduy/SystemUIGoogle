// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.os.Handler;
import android.content.ComponentName;
import java.util.function.Consumer;
import android.graphics.drawable.Drawable;
import java.util.Collections;
import android.media.session.MediaSession;
import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.UserInfoController;
import android.content.IntentFilter;
import java.util.List;
import android.media.session.PlaybackState;
import android.content.Intent;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.media.session.MediaSessionManager$OnActiveSessionsChangedListener;
import android.media.session.MediaController$Callback;
import android.content.BroadcastReceiver;
import android.app.RemoteAction;
import android.media.session.MediaSessionManager;
import android.media.session.MediaController;
import java.util.ArrayList;
import android.content.Context;
import android.app.IActivityManager;

public class PipMediaController
{
    private final IActivityManager mActivityManager;
    private final Context mContext;
    private ArrayList<ActionListener> mListeners;
    private MediaController mMediaController;
    private final MediaSessionManager mMediaSessionManager;
    private RemoteAction mNextAction;
    private RemoteAction mPauseAction;
    private RemoteAction mPlayAction;
    private BroadcastReceiver mPlayPauseActionReceiver;
    private final MediaController$Callback mPlaybackChangedListener;
    private RemoteAction mPrevAction;
    private final MediaSessionManager$OnActiveSessionsChangedListener mSessionsChangedListener;
    
    public PipMediaController(final Context mContext, final IActivityManager mActivityManager, final BroadcastDispatcher broadcastDispatcher) {
        this.mPlayPauseActionReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (action.equals("com.android.systemui.pip.phone.PLAY")) {
                    PipMediaController.this.mMediaController.getTransportControls().play();
                }
                else if (action.equals("com.android.systemui.pip.phone.PAUSE")) {
                    PipMediaController.this.mMediaController.getTransportControls().pause();
                }
                else if (action.equals("com.android.systemui.pip.phone.NEXT")) {
                    PipMediaController.this.mMediaController.getTransportControls().skipToNext();
                }
                else if (action.equals("com.android.systemui.pip.phone.PREV")) {
                    PipMediaController.this.mMediaController.getTransportControls().skipToPrevious();
                }
            }
        };
        this.mPlaybackChangedListener = new MediaController$Callback() {
            public void onPlaybackStateChanged(final PlaybackState playbackState) {
                PipMediaController.this.notifyActionsChanged();
            }
        };
        this.mSessionsChangedListener = (MediaSessionManager$OnActiveSessionsChangedListener)new MediaSessionManager$OnActiveSessionsChangedListener() {
            public void onActiveSessionsChanged(final List<MediaController> list) {
                PipMediaController.this.resolveActiveMediaController(list);
            }
        };
        this.mListeners = new ArrayList<ActionListener>();
        this.mContext = mContext;
        this.mActivityManager = mActivityManager;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.systemui.pip.phone.PLAY");
        intentFilter.addAction("com.android.systemui.pip.phone.PAUSE");
        intentFilter.addAction("com.android.systemui.pip.phone.NEXT");
        intentFilter.addAction("com.android.systemui.pip.phone.PREV");
        broadcastDispatcher.registerReceiver(this.mPlayPauseActionReceiver, intentFilter);
        this.createMediaActions();
        this.mMediaSessionManager = (MediaSessionManager)mContext.getSystemService("media_session");
        Dependency.get(UserInfoController.class).addCallback((UserInfoController.OnUserInfoChangedListener)new _$$Lambda$PipMediaController$neOVZxIcmRkhimcM6huwsIEiXEw(this));
    }
    
    private void createMediaActions() {
        final String string = this.mContext.getString(R$string.pip_pause);
        this.mPauseAction = new RemoteAction(Icon.createWithResource(this.mContext, R$drawable.ic_pause_white), (CharSequence)string, (CharSequence)string, PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.systemui.pip.phone.PAUSE"), 134217728));
        final String string2 = this.mContext.getString(R$string.pip_play);
        this.mPlayAction = new RemoteAction(Icon.createWithResource(this.mContext, R$drawable.ic_play_arrow_white), (CharSequence)string2, (CharSequence)string2, PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.systemui.pip.phone.PLAY"), 134217728));
        final String string3 = this.mContext.getString(R$string.pip_skip_to_next);
        this.mNextAction = new RemoteAction(Icon.createWithResource(this.mContext, R$drawable.ic_skip_next_white), (CharSequence)string3, (CharSequence)string3, PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.systemui.pip.phone.NEXT"), 134217728));
        final String string4 = this.mContext.getString(R$string.pip_skip_to_prev);
        this.mPrevAction = new RemoteAction(Icon.createWithResource(this.mContext, R$drawable.ic_skip_previous_white), (CharSequence)string4, (CharSequence)string4, PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.systemui.pip.phone.PREV"), 134217728));
    }
    
    private List<RemoteAction> getMediaActions() {
        final MediaController mMediaController = this.mMediaController;
        if (mMediaController != null && mMediaController.getPlaybackState() != null) {
            final ArrayList<RemoteAction> list = new ArrayList<RemoteAction>();
            final boolean activeState = MediaSession.isActiveState(this.mMediaController.getPlaybackState().getState());
            final long actions = this.mMediaController.getPlaybackState().getActions();
            final RemoteAction mPrevAction = this.mPrevAction;
            final boolean b = true;
            mPrevAction.setEnabled((0x10L & actions) != 0x0L);
            list.add(this.mPrevAction);
            if (!activeState && (0x4L & actions) != 0x0L) {
                list.add(this.mPlayAction);
            }
            else if (activeState && (0x2L & actions) != 0x0L) {
                list.add(this.mPauseAction);
            }
            this.mNextAction.setEnabled((actions & 0x20L) != 0x0L && b);
            list.add(this.mNextAction);
            return list;
        }
        return (List<RemoteAction>)Collections.EMPTY_LIST;
    }
    
    private void notifyActionsChanged() {
        if (!this.mListeners.isEmpty()) {
            this.mListeners.forEach(new _$$Lambda$PipMediaController$PGZH9Rcf3EMC5cibv13aaStfc2E(this.getMediaActions()));
        }
    }
    
    private void registerSessionListenerForCurrentUser() {
        this.mMediaSessionManager.removeOnActiveSessionsChangedListener(this.mSessionsChangedListener);
        this.mMediaSessionManager.addOnActiveSessionsChangedListener(this.mSessionsChangedListener, (ComponentName)null, -2, (Handler)null);
    }
    
    private void resolveActiveMediaController(final List<MediaController> list) {
        if (list != null) {
            final ComponentName componentName = (ComponentName)PipUtils.getTopPipActivity(this.mContext, this.mActivityManager).first;
            if (componentName != null) {
                for (int i = 0; i < list.size(); ++i) {
                    final MediaController activeMediaController = list.get(i);
                    if (activeMediaController.getPackageName().equals(componentName.getPackageName())) {
                        this.setActiveMediaController(activeMediaController);
                        return;
                    }
                }
            }
        }
        this.setActiveMediaController(null);
    }
    
    private void setActiveMediaController(final MediaController mMediaController) {
        final MediaController mMediaController2 = this.mMediaController;
        if (mMediaController != mMediaController2) {
            if (mMediaController2 != null) {
                mMediaController2.unregisterCallback(this.mPlaybackChangedListener);
            }
            if ((this.mMediaController = mMediaController) != null) {
                mMediaController.registerCallback(this.mPlaybackChangedListener);
            }
            this.notifyActionsChanged();
        }
    }
    
    public void addListener(final ActionListener actionListener) {
        if (!this.mListeners.contains(actionListener)) {
            this.mListeners.add(actionListener);
            actionListener.onMediaActionsChanged(this.getMediaActions());
        }
    }
    
    public void onActivityPinned() {
        this.resolveActiveMediaController(this.mMediaSessionManager.getActiveSessionsForUser((ComponentName)null, -2));
    }
    
    public void removeListener(final ActionListener o) {
        o.onMediaActionsChanged(Collections.EMPTY_LIST);
        this.mListeners.remove(o);
    }
    
    public interface ActionListener
    {
        void onMediaActionsChanged(final List<RemoteAction> p0);
    }
}
