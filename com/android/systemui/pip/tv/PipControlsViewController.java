// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv;

import java.util.Collection;
import com.android.systemui.R$string;
import com.android.systemui.R$drawable;
import android.graphics.drawable.Icon$OnDrawableLoadedListener;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.view.View$OnClickListener;
import android.media.session.PlaybackState;
import android.view.View;
import android.view.View$OnAttachStateChangeListener;
import android.media.session.MediaController$Callback;
import android.media.session.MediaController;
import android.view.LayoutInflater;
import android.os.Handler;
import android.view.View$OnFocusChangeListener;
import java.util.ArrayList;
import android.app.RemoteAction;
import java.util.List;

public class PipControlsViewController
{
    private static final String TAG = "PipControlsViewController";
    private List<RemoteAction> mCustomActions;
    private ArrayList<PipControlButtonView> mCustomButtonViews;
    private final View$OnFocusChangeListener mFocusChangeListener;
    private PipControlButtonView mFocusedChild;
    private final Handler mHandler;
    private final LayoutInflater mLayoutInflater;
    private Listener mListener;
    private MediaController mMediaController;
    private MediaController$Callback mMediaControllerCallback;
    private View$OnAttachStateChangeListener mOnAttachStateChangeListener;
    private final PipManager mPipManager;
    private final PipManager.MediaListener mPipMediaListener;
    private final PipControlButtonView mPlayPauseButtonView;
    private final PipControlsView mView;
    
    public PipControlsViewController(final PipControlsView mView, final PipManager mPipManager, final LayoutInflater mLayoutInflater, final Handler mHandler) {
        this.mCustomButtonViews = new ArrayList<PipControlButtonView>();
        this.mCustomActions = new ArrayList<RemoteAction>();
        this.mOnAttachStateChangeListener = (View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
                PipControlsViewController.this.updateMediaController();
                PipControlsViewController.this.mPipManager.addMediaListener(PipControlsViewController.this.mPipMediaListener);
            }
            
            public void onViewDetachedFromWindow(final View view) {
                PipControlsViewController.this.mPipManager.removeMediaListener(PipControlsViewController.this.mPipMediaListener);
            }
        };
        this.mMediaControllerCallback = new MediaController$Callback() {
            public void onPlaybackStateChanged(final PlaybackState playbackState) {
                PipControlsViewController.this.updateUserActions();
            }
        };
        this.mPipMediaListener = new _$$Lambda$PipControlsViewController$kZf8PkTX4QNE7Vc5zsES51WG3gA(this);
        this.mFocusChangeListener = (View$OnFocusChangeListener)new View$OnFocusChangeListener() {
            public void onFocusChange(final View view, final boolean b) {
                if (b) {
                    PipControlsViewController.this.mFocusedChild = (PipControlButtonView)view;
                }
                else if (PipControlsViewController.this.mFocusedChild == view) {
                    PipControlsViewController.this.mFocusedChild = null;
                }
            }
        };
        this.mView = mView;
        this.mPipManager = mPipManager;
        this.mLayoutInflater = mLayoutInflater;
        this.mHandler = mHandler;
        mView.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        if (this.mView.isAttachedToWindow()) {
            this.mOnAttachStateChangeListener.onViewAttachedToWindow((View)this.mView);
        }
        final PipControlButtonView fullButtonView = this.mView.getFullButtonView();
        ((View)fullButtonView).setOnFocusChangeListener(this.mFocusChangeListener);
        ((View)fullButtonView).setOnClickListener((View$OnClickListener)new _$$Lambda$PipControlsViewController$rQPZrjnmUU8eHBhj9PVaj2cMJVs(this));
        final PipControlButtonView closeButtonView = this.mView.getCloseButtonView();
        ((View)closeButtonView).setOnFocusChangeListener(this.mFocusChangeListener);
        ((View)closeButtonView).setOnClickListener((View$OnClickListener)new _$$Lambda$PipControlsViewController$rqMCDyXd4qEJoGxUNtb2wXHRu3A(this));
        (this.mPlayPauseButtonView = this.mView.getPlayPauseButtonView()).setOnFocusChangeListener(this.mFocusChangeListener);
        this.mPlayPauseButtonView.setOnClickListener((View$OnClickListener)new _$$Lambda$PipControlsViewController$q5wDnhpgYTIhYtkeUXKZ3KQDbyI(this));
    }
    
    private void updateMediaController() {
        final MediaController mediaController = this.mPipManager.getMediaController();
        final MediaController mMediaController = this.mMediaController;
        if (mMediaController == mediaController) {
            return;
        }
        if (mMediaController != null) {
            mMediaController.unregisterCallback(this.mMediaControllerCallback);
        }
        if ((this.mMediaController = mediaController) != null) {
            mediaController.registerCallback(this.mMediaControllerCallback);
        }
        this.updateUserActions();
    }
    
    private void updateUserActions() {
        final boolean empty = this.mCustomActions.isEmpty();
        int i = 0;
        final int n = 0;
        if (!empty) {
            while (this.mCustomButtonViews.size() < this.mCustomActions.size()) {
                final PipControlButtonView e = (PipControlButtonView)this.mLayoutInflater.inflate(R$layout.tv_pip_custom_control, (ViewGroup)this.mView, false);
                this.mView.addView((View)e);
                this.mCustomButtonViews.add(e);
            }
            int index = 0;
            int j;
            while (true) {
                j = n;
                if (index >= this.mCustomButtonViews.size()) {
                    break;
                }
                final PipControlButtonView pipControlButtonView = this.mCustomButtonViews.get(index);
                int visibility;
                if (index < this.mCustomActions.size()) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                pipControlButtonView.setVisibility(visibility);
                ++index;
            }
            while (j < this.mCustomActions.size()) {
                final RemoteAction remoteAction = this.mCustomActions.get(j);
                final PipControlButtonView pipControlButtonView2 = this.mCustomButtonViews.get(j);
                remoteAction.getIcon().loadDrawableAsync(this.mView.getContext(), (Icon$OnDrawableLoadedListener)new _$$Lambda$PipControlsViewController$kC7tvkXWtpNyYLWHDbM2CyhHzr4(pipControlButtonView2), this.mHandler);
                pipControlButtonView2.setText(remoteAction.getContentDescription());
                if (remoteAction.isEnabled()) {
                    pipControlButtonView2.setOnClickListener((View$OnClickListener)new _$$Lambda$PipControlsViewController$1reZdb40mM6nxQSPhdAKH0IMN6M(remoteAction));
                }
                pipControlButtonView2.setEnabled(remoteAction.isEnabled());
                float alpha;
                if (remoteAction.isEnabled()) {
                    alpha = 1.0f;
                }
                else {
                    alpha = 0.54f;
                }
                pipControlButtonView2.setAlpha(alpha);
                ++j;
            }
            this.mPlayPauseButtonView.setVisibility(8);
        }
        else {
            final int playbackState = this.mPipManager.getPlaybackState();
            if (playbackState == 2) {
                this.mPlayPauseButtonView.setVisibility(8);
            }
            else {
                this.mPlayPauseButtonView.setVisibility(0);
                if (playbackState == 0) {
                    this.mPlayPauseButtonView.setImageResource(R$drawable.ic_pause_white);
                    this.mPlayPauseButtonView.setText(R$string.pip_pause);
                }
                else {
                    this.mPlayPauseButtonView.setImageResource(R$drawable.ic_play_arrow_white);
                    this.mPlayPauseButtonView.setText(R$string.pip_play);
                }
            }
            while (i < this.mCustomButtonViews.size()) {
                this.mCustomButtonViews.get(i).setVisibility(8);
                ++i;
            }
        }
    }
    
    public PipControlsView getView() {
        return this.mView;
    }
    
    public void setActions(final List<RemoteAction> list) {
        this.mCustomActions.clear();
        this.mCustomActions.addAll(list);
        this.updateUserActions();
    }
    
    public interface Listener
    {
        void onClosed();
    }
}
