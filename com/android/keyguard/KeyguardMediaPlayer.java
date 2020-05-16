// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.app.Notification$Action;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.palette.graphics.Palette;
import android.app.Notification;
import android.media.session.MediaController;
import android.app.Notification$Builder;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.statusbar.notification.MediaNotificationProcessor;
import android.media.session.MediaSession$Token;
import android.app.PendingIntent$CanceledException;
import com.android.systemui.R$dimen;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.PendingIntent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import com.android.systemui.statusbar.notification.stack.MediaHeaderView;
import java.util.List;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;
import java.util.Objects;
import androidx.lifecycle.Observer;
import android.view.View$OnClickListener;
import android.widget.ImageButton;
import android.media.MediaMetadata;
import android.graphics.drawable.Icon;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.view.View;
import com.android.systemui.media.MediaControllerFactory;
import android.content.Context;
import com.android.systemui.R$id;
import java.util.concurrent.Executor;

public class KeyguardMediaPlayer
{
    private static final int[] ACTION_IDS;
    private final Executor mBackgroundExecutor;
    private KeyguardMediaObserver mObserver;
    private final KeyguardMediaViewModel mViewModel;
    
    static {
        ACTION_IDS = new int[] { R$id.action0, R$id.action1, R$id.action2 };
    }
    
    public KeyguardMediaPlayer(final Context context, final MediaControllerFactory mediaControllerFactory, final Executor mBackgroundExecutor) {
        this.mBackgroundExecutor = mBackgroundExecutor;
        this.mViewModel = new KeyguardMediaViewModel(context, mediaControllerFactory);
    }
    
    public void bindView(final View view) {
        if (this.mObserver == null) {
            this.mViewModel.loadDimens();
            this.mObserver = new KeyguardMediaObserver(view);
            int n = 0;
            while (true) {
                final int[] action_IDS = KeyguardMediaPlayer.ACTION_IDS;
                if (n >= action_IDS.length) {
                    break;
                }
                final ImageButton imageButton = (ImageButton)view.findViewById(action_IDS[n]);
                if (imageButton != null) {
                    imageButton.setOnClickListener((View$OnClickListener)new _$$Lambda$KeyguardMediaPlayer$5R9BqQzWgjcTTufsejFRCWr29fM(this, n));
                }
                ++n;
            }
            this.mViewModel.getKeyguardMedia().observeForever(this.mObserver);
            return;
        }
        throw new IllegalStateException("cannot bind views, already bound");
    }
    
    public void clearControls() {
        final Executor mBackgroundExecutor = this.mBackgroundExecutor;
        final KeyguardMediaViewModel mViewModel = this.mViewModel;
        Objects.requireNonNull(mViewModel);
        mBackgroundExecutor.execute(new _$$Lambda$JnbyUyXj5h6J_mQqew0NqL_4BCo(mViewModel));
    }
    
    public void unbindView() {
        if (this.mObserver != null) {
            this.mViewModel.getKeyguardMedia().removeObserver(this.mObserver);
            this.mObserver = null;
            return;
        }
        throw new IllegalStateException("cannot unbind views, nothing bound");
    }
    
    public void updateControls(final NotificationEntry notificationEntry, final Icon icon, final MediaMetadata mediaMetadata) {
        if (this.mObserver == null) {
            throw new IllegalStateException("cannot update controls, views not bound");
        }
        if (mediaMetadata == null) {
            Log.d("KeyguardMediaPlayer", "media metadata was null, closing media controls");
            this.clearControls();
            return;
        }
        this.mBackgroundExecutor.execute(new _$$Lambda$KeyguardMediaPlayer$BeGqj0hyOEKej5NXfP7LCdSMMXE(this, notificationEntry, icon, mediaMetadata));
    }
    
    private static final class KeyguardMediaObserver implements Observer<KeyguardMedia>
    {
        private final ImageView mAlbumView;
        private final ImageView mAppIconView;
        private final TextView mAppNameView;
        private final TextView mArtistView;
        private final List<ImageButton> mButtonViews;
        private final MediaHeaderView mMediaHeaderView;
        private final View mRootView;
        private final TextView mTitleView;
        
        KeyguardMediaObserver(final View mRootView) {
            this.mButtonViews = new ArrayList<ImageButton>();
            this.mRootView = mRootView;
            MediaHeaderView mMediaHeaderView;
            if (mRootView instanceof MediaHeaderView) {
                mMediaHeaderView = (MediaHeaderView)mRootView;
            }
            else {
                mMediaHeaderView = null;
            }
            this.mMediaHeaderView = mMediaHeaderView;
            this.mAlbumView = (ImageView)mRootView.findViewById(R$id.album_art);
            this.mAppIconView = (ImageView)mRootView.findViewById(R$id.icon);
            this.mAppNameView = (TextView)mRootView.findViewById(R$id.app_name);
            this.mTitleView = (TextView)mRootView.findViewById(R$id.header_title);
            this.mArtistView = (TextView)mRootView.findViewById(R$id.header_artist);
            for (int i = 0; i < KeyguardMediaPlayer.ACTION_IDS.length; ++i) {
                this.mButtonViews.add((ImageButton)mRootView.findViewById(KeyguardMediaPlayer.ACTION_IDS[i]));
            }
        }
        
        @Override
        public void onChanged(final KeyguardMedia keyguardMedia) {
            if (keyguardMedia == null) {
                this.mRootView.setVisibility(8);
                return;
            }
            this.mRootView.setVisibility(0);
            final MediaHeaderView mMediaHeaderView = this.mMediaHeaderView;
            if (mMediaHeaderView != null) {
                mMediaHeaderView.setBackgroundColor(keyguardMedia.getBackgroundColor());
            }
            final ImageView mAlbumView = this.mAlbumView;
            if (mAlbumView != null) {
                mAlbumView.setImageDrawable(keyguardMedia.getArtwork());
                final ImageView mAlbumView2 = this.mAlbumView;
                int visibility;
                if (keyguardMedia.getArtwork() == null) {
                    visibility = 8;
                }
                else {
                    visibility = 0;
                }
                mAlbumView2.setVisibility(visibility);
            }
            if (this.mAppIconView != null) {
                final Drawable appIcon = keyguardMedia.getAppIcon();
                appIcon.setTint(keyguardMedia.getForegroundColor());
                this.mAppIconView.setImageDrawable(appIcon);
            }
            if (this.mAppNameView != null) {
                this.mAppNameView.setText((CharSequence)keyguardMedia.getApp());
                this.mAppNameView.setTextColor(keyguardMedia.getForegroundColor());
            }
            final TextView mTitleView = this.mTitleView;
            if (mTitleView != null) {
                mTitleView.setText((CharSequence)keyguardMedia.getSong());
                this.mTitleView.setTextColor(keyguardMedia.getForegroundColor());
            }
            final TextView mArtistView = this.mArtistView;
            if (mArtistView != null) {
                mArtistView.setText((CharSequence)keyguardMedia.getArtist());
                this.mArtistView.setTextColor(keyguardMedia.getForegroundColor());
            }
            for (int i = 0; i < KeyguardMediaPlayer.ACTION_IDS.length; ++i) {
                final ImageButton imageButton = this.mButtonViews.get(i);
                if (imageButton != null) {
                    final Drawable imageDrawable = keyguardMedia.getActionIcons().get(i);
                    if (imageDrawable == null) {
                        imageButton.setVisibility(8);
                        imageButton.setImageDrawable((Drawable)null);
                    }
                    else {
                        imageButton.setVisibility(0);
                        imageButton.setImageDrawable(imageDrawable);
                        imageButton.setImageTintList(ColorStateList.valueOf(keyguardMedia.getForegroundColor()));
                    }
                }
            }
        }
    }
    
    private static final class KeyguardMediaViewModel
    {
        private List<PendingIntent> mActions;
        private final Object mActionsLock;
        private float mAlbumArtRadius;
        private int mAlbumArtSize;
        private final Context mContext;
        private final MutableLiveData<KeyguardMedia> mMedia;
        private final MediaControllerFactory mMediaControllerFactory;
        
        KeyguardMediaViewModel(final Context mContext, final MediaControllerFactory mMediaControllerFactory) {
            this.mMedia = new MutableLiveData<KeyguardMedia>();
            this.mActionsLock = new Object();
            this.mContext = mContext;
            this.mMediaControllerFactory = mMediaControllerFactory;
            this.loadDimens();
        }
        
        public void clearControls() {
            synchronized (this.mActionsLock) {
                this.mActions = null;
                // monitorexit(this.mActionsLock)
                this.mMedia.postValue(null);
            }
        }
        
        public LiveData<KeyguardMedia> getKeyguardMedia() {
            return this.mMedia;
        }
        
        void loadDimens() {
            this.mAlbumArtRadius = this.mContext.getResources().getDimension(R$dimen.qs_media_corner_radius);
            this.mAlbumArtSize = (int)this.mContext.getResources().getDimension(R$dimen.qs_media_album_size);
        }
        
        public void onActionClick(final int n) {
            synchronized (this.mActionsLock) {
                PendingIntent pendingIntent;
                if (this.mActions != null && n < this.mActions.size()) {
                    pendingIntent = this.mActions.get(n);
                }
                else {
                    pendingIntent = null;
                }
                // monitorexit(this.mActionsLock)
                if (pendingIntent != null) {
                    try {
                        pendingIntent.send();
                    }
                    catch (PendingIntent$CanceledException ex) {
                        Log.d("KeyguardMediaPlayer", "failed to send action intent", (Throwable)ex);
                    }
                }
            }
        }
        
        public void updateControls(NotificationEntry mActionsLock, final Icon icon, final MediaMetadata mediaMetadata) {
            final MediaSession$Token mediaSession$Token = (MediaSession$Token)mActionsLock.getSbn().getNotification().extras.getParcelable("android.mediaSession");
            MediaController create;
            if (mediaSession$Token != null) {
                create = this.mMediaControllerFactory.create(mediaSession$Token);
            }
            else {
                create = null;
            }
            if (create != null && create.getPlaybackState() == null) {
                this.clearControls();
                return;
            }
            final Notification notification = mActionsLock.getSbn().getNotification();
            int n = notification.color;
            int n2;
            if (mActionsLock.getRow() == null) {
                n2 = -1;
            }
            else {
                n2 = mActionsLock.getRow().getCurrentBackgroundTint();
            }
            Bitmap bitmap;
            if ((bitmap = mediaMetadata.getBitmap("android.media.metadata.ART")) == null) {
                bitmap = mediaMetadata.getBitmap("android.media.metadata.ALBUM_ART");
            }
            if (bitmap != null) {
                final Palette generate = MediaNotificationProcessor.generateArtworkPaletteBuilder(bitmap).generate();
                n2 = MediaNotificationProcessor.findBackgroundSwatch(generate).getRgb();
                n = MediaNotificationProcessor.selectForegroundColor(n2, generate);
            }
            final boolean b = ContrastColorUtil.isColorLight(n2) ^ true;
            final int ensureTextContrast = ContrastColorUtil.ensureTextContrast(ContrastColorUtil.resolveContrastColor(this.mContext, n, n2, b), n2, b);
            int i = 0;
            RoundedBitmapDrawable create2;
            if (bitmap != null) {
                final Bitmap copy = bitmap.copy(Bitmap$Config.ARGB_8888, true);
                final int mAlbumArtSize = this.mAlbumArtSize;
                create2 = RoundedBitmapDrawableFactory.create(this.mContext.getResources(), Bitmap.createScaledBitmap(copy, mAlbumArtSize, mAlbumArtSize, false));
                create2.setCornerRadius(this.mAlbumArtRadius);
            }
            else {
                create2 = null;
            }
            final String loadHeaderAppName = Notification$Builder.recoverBuilder(this.mContext, notification).loadHeaderAppName();
            final Drawable loadDrawable = icon.loadDrawable(this.mContext);
            final String string = mediaMetadata.getString("android.media.metadata.TITLE");
            final String string2 = mediaMetadata.getString("android.media.metadata.ARTIST");
            final ArrayList<Drawable> list = new ArrayList<Drawable>();
            final ArrayList<PendingIntent> mActions = new ArrayList<PendingIntent>();
            final Notification$Action[] actions = notification.actions;
            final int[] intArray = notification.extras.getIntArray("android.compactActions");
            final Context packageContext = mActionsLock.getSbn().getPackageContext(this.mContext);
            while (i < KeyguardMediaPlayer.ACTION_IDS.length) {
                if (intArray != null && actions != null && i < intArray.length && intArray[i] < actions.length) {
                    final int n3 = intArray[i];
                    list.add(actions[n3].getIcon().loadDrawable(packageContext));
                    mActions.add(actions[n3].actionIntent);
                }
                else {
                    list.add(null);
                    mActions.add(null);
                }
                ++i;
            }
            mActionsLock = (NotificationEntry)this.mActionsLock;
            synchronized (mActionsLock) {
                this.mActions = mActions;
                // monitorexit(mActionsLock)
                mActionsLock = (NotificationEntry)new KeyguardMedia(ensureTextContrast, n2, loadHeaderAppName, loadDrawable, string2, string, create2, list);
                this.mMedia.postValue((KeyguardMedia)mActionsLock);
            }
        }
    }
}
