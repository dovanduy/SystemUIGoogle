// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import java.lang.ref.WeakReference;
import com.android.systemui.util.Utils;
import android.os.Trace;
import android.graphics.drawable.Icon;
import android.content.ComponentName;
import android.media.session.MediaSession$Token;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.phone.ScrimState;
import android.graphics.drawable.BitmapDrawable;
import java.util.Collection;
import android.graphics.Bitmap;
import android.provider.DeviceConfig;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import java.util.Iterator;
import android.provider.DeviceConfig$Properties;
import android.util.ArraySet;
import com.android.systemui.Dependency;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.ScrimController;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import android.os.AsyncTask;
import java.util.Set;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import dagger.Lazy;
import android.media.session.MediaSessionManager;
import com.android.keyguard.KeyguardMediaPlayer;
import android.media.MediaMetadata;
import java.util.ArrayList;
import android.media.session.MediaController$Callback;
import android.media.session.MediaController;
import java.util.concurrent.Executor;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.content.Context;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import android.widget.ImageView;
import java.util.HashSet;
import com.android.systemui.Dumpable;

public class NotificationMediaManager implements Dumpable
{
    private static final HashSet<Integer> PAUSED_MEDIA_STATES;
    private BackDropView mBackdrop;
    private ImageView mBackdropBack;
    private ImageView mBackdropFront;
    private BiometricUnlockController mBiometricUnlockController;
    private final SysuiColorExtractor mColorExtractor;
    private final Context mContext;
    private final NotificationEntryManager mEntryManager;
    protected final Runnable mHideBackdropFront;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardStateController mKeyguardStateController;
    private LockscreenWallpaper mLockscreenWallpaper;
    private final Executor mMainExecutor;
    private final MediaArtworkProcessor mMediaArtworkProcessor;
    private MediaController mMediaController;
    private final MediaController$Callback mMediaListener;
    private final ArrayList<MediaListener> mMediaListeners;
    private MediaMetadata mMediaMetadata;
    private String mMediaNotificationKey;
    private final KeyguardMediaPlayer mMediaPlayer;
    private final MediaSessionManager mMediaSessionManager;
    private Lazy<NotificationShadeWindowController> mNotificationShadeWindowController;
    protected NotificationPresenter mPresenter;
    private final Set<AsyncTask<?, ?, ?>> mProcessArtworkTasks;
    private final DeviceConfig$OnPropertiesChangedListener mPropertiesChangedListener;
    private ScrimController mScrimController;
    private boolean mShowCompactMediaSeekbar;
    private final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    
    static {
        (PAUSED_MEDIA_STATES = new HashSet<Integer>()).add(0);
        NotificationMediaManager.PAUSED_MEDIA_STATES.add(1);
        NotificationMediaManager.PAUSED_MEDIA_STATES.add(2);
        NotificationMediaManager.PAUSED_MEDIA_STATES.add(7);
    }
    
    public NotificationMediaManager(final Context mContext, final Lazy<StatusBar> mStatusBarLazy, final Lazy<NotificationShadeWindowController> mNotificationShadeWindowController, final NotificationEntryManager mEntryManager, final MediaArtworkProcessor mMediaArtworkProcessor, final KeyguardBypassController mKeyguardBypassController, final KeyguardMediaPlayer mMediaPlayer, final Executor mMainExecutor, final DeviceConfigProxy deviceConfigProxy) {
        this.mStatusBarStateController = Dependency.get(StatusBarStateController.class);
        this.mColorExtractor = Dependency.get(SysuiColorExtractor.class);
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
        this.mProcessArtworkTasks = (Set<AsyncTask<?, ?, ?>>)new ArraySet();
        this.mPropertiesChangedListener = (DeviceConfig$OnPropertiesChangedListener)new DeviceConfig$OnPropertiesChangedListener() {
            public void onPropertiesChanged(final DeviceConfig$Properties deviceConfig$Properties) {
                for (final String anObject : deviceConfig$Properties.getKeyset()) {
                    if ("compact_media_notification_seekbar_enabled".equals(anObject)) {
                        NotificationMediaManager.this.mShowCompactMediaSeekbar = "true".equals(deviceConfig$Properties.getString(anObject, (String)null));
                    }
                }
            }
        };
        this.mMediaListener = new MediaController$Callback() {
            public void onMetadataChanged(final MediaMetadata mediaMetadata) {
                super.onMetadataChanged(mediaMetadata);
                NotificationMediaManager.this.mMediaArtworkProcessor.clearCache();
                NotificationMediaManager.this.mMediaMetadata = mediaMetadata;
                NotificationMediaManager.this.dispatchUpdateMediaMetaData(true, true);
            }
            
            public void onPlaybackStateChanged(final PlaybackState playbackState) {
                super.onPlaybackStateChanged(playbackState);
                if (playbackState != null) {
                    if (!NotificationMediaManager.this.isPlaybackActive(playbackState.getState())) {
                        NotificationMediaManager.this.clearCurrentMediaNotification();
                    }
                    NotificationMediaManager.this.findAndUpdateMediaNotifications();
                }
            }
        };
        this.mHideBackdropFront = new Runnable() {
            @Override
            public void run() {
                NotificationMediaManager.this.mBackdropFront.setVisibility(4);
                NotificationMediaManager.this.mBackdropFront.animate().cancel();
                NotificationMediaManager.this.mBackdropFront.setImageDrawable((Drawable)null);
            }
        };
        this.mContext = mContext;
        this.mMediaArtworkProcessor = mMediaArtworkProcessor;
        this.mKeyguardBypassController = mKeyguardBypassController;
        this.mMediaPlayer = mMediaPlayer;
        this.mMediaListeners = new ArrayList<MediaListener>();
        this.mMediaSessionManager = (MediaSessionManager)this.mContext.getSystemService("media_session");
        this.mStatusBarLazy = mStatusBarLazy;
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mEntryManager = mEntryManager;
        this.mMainExecutor = mMainExecutor;
        mEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                NotificationMediaManager.this.onNotificationRemoved(notificationEntry.getKey());
            }
            
            @Override
            public void onPendingEntryAdded(final NotificationEntry notificationEntry) {
                NotificationMediaManager.this.findAndUpdateMediaNotifications();
            }
            
            @Override
            public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                NotificationMediaManager.this.findAndUpdateMediaNotifications();
            }
        });
        this.mShowCompactMediaSeekbar = "true".equals(DeviceConfig.getProperty("systemui", "compact_media_notification_seekbar_enabled"));
        deviceConfigProxy.addOnPropertiesChangedListener("systemui", this.mContext.getMainExecutor(), this.mPropertiesChangedListener);
    }
    
    private void clearCurrentMediaNotificationSession() {
        this.mMediaArtworkProcessor.clearCache();
        this.mMediaMetadata = null;
        final MediaController mMediaController = this.mMediaController;
        if (mMediaController != null) {
            mMediaController.unregisterCallback(this.mMediaListener);
        }
        this.mMediaController = null;
    }
    
    private void dispatchUpdateMediaMetaData(final boolean b, final boolean b2) {
        final NotificationPresenter mPresenter = this.mPresenter;
        if (mPresenter != null) {
            mPresenter.updateMediaMetaData(b, b2);
        }
        final int mediaControllerPlaybackState = this.getMediaControllerPlaybackState(this.mMediaController);
        final ArrayList<MediaListener> list = new ArrayList<MediaListener>(this.mMediaListeners);
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).onMetadataOrStateChanged(this.mMediaMetadata, mediaControllerPlaybackState);
        }
    }
    
    private void finishUpdateMediaMetaData(boolean b, final boolean b2, final Bitmap bitmap) {
        Object o;
        if (bitmap != null) {
            o = new BitmapDrawable(this.mBackdropBack.getResources(), bitmap);
        }
        else {
            o = null;
        }
        final int n = 1;
        final boolean hasMediaArtwork = o != null;
        Object imageDrawable = o;
        boolean b3 = false;
        Label_0125: {
            if (o == null) {
                final LockscreenWallpaper mLockscreenWallpaper = this.mLockscreenWallpaper;
                Bitmap bitmap2;
                if (mLockscreenWallpaper != null) {
                    bitmap2 = mLockscreenWallpaper.getBitmap();
                }
                else {
                    bitmap2 = null;
                }
                imageDrawable = o;
                if (bitmap2 != null) {
                    final LockscreenWallpaper.WallpaperDrawable wallpaperDrawable = (LockscreenWallpaper.WallpaperDrawable)(imageDrawable = new LockscreenWallpaper.WallpaperDrawable(this.mBackdropBack.getResources(), bitmap2));
                    if (this.mStatusBarStateController.getState() == 1) {
                        b3 = true;
                        imageDrawable = wallpaperDrawable;
                        break Label_0125;
                    }
                }
            }
            b3 = false;
        }
        final NotificationShadeWindowController notificationShadeWindowController = this.mNotificationShadeWindowController.get();
        final boolean occluded = this.mStatusBarLazy.get().isOccluded();
        final boolean hasBackdrop = imageDrawable != null;
        this.mColorExtractor.setHasMediaArtwork(hasMediaArtwork);
        final ScrimController mScrimController = this.mScrimController;
        if (mScrimController != null) {
            mScrimController.setHasBackdrop(hasBackdrop);
        }
        if (hasBackdrop) {
            if (this.mStatusBarStateController.getState() != 0 || b3) {
                final BiometricUnlockController mBiometricUnlockController = this.mBiometricUnlockController;
                if (mBiometricUnlockController != null && mBiometricUnlockController.getMode() != 2 && !occluded) {
                    if (this.mBackdrop.getVisibility() != 0) {
                        this.mBackdrop.setVisibility(0);
                        if (b2) {
                            this.mBackdrop.setAlpha(0.0f);
                            this.mBackdrop.animate().alpha(1.0f);
                        }
                        else {
                            this.mBackdrop.animate().cancel();
                            this.mBackdrop.setAlpha(1.0f);
                        }
                        if (notificationShadeWindowController != null) {
                            notificationShadeWindowController.setBackdropShowing(true);
                        }
                        b = true;
                    }
                    if (!b) {
                        return;
                    }
                    if (this.mBackdropBack.getDrawable() != null) {
                        this.mBackdropFront.setImageDrawable(this.mBackdropBack.getDrawable().getConstantState().newDrawable(this.mBackdropFront.getResources()).mutate());
                        this.mBackdropFront.setAlpha(1.0f);
                        this.mBackdropFront.setVisibility(0);
                    }
                    else {
                        this.mBackdropFront.setVisibility(4);
                    }
                    this.mBackdropBack.setImageDrawable((Drawable)imageDrawable);
                    if (this.mBackdropFront.getVisibility() == 0) {
                        this.mBackdropFront.animate().setDuration(250L).alpha(0.0f).withEndAction(this.mHideBackdropFront);
                    }
                    return;
                }
            }
        }
        if (this.mBackdrop.getVisibility() != 8) {
            int n2;
            if (this.mStatusBarStateController.isDozing() && !ScrimState.AOD.getAnimateChange()) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
            final boolean bypassFadingAnimation = this.mKeyguardStateController.isBypassFadingAnimation();
            final BiometricUnlockController mBiometricUnlockController2 = this.mBiometricUnlockController;
            if ((((mBiometricUnlockController2 != null && mBiometricUnlockController2.getMode() == 2) || n2 != 0) && !bypassFadingAnimation) || occluded) {
                this.mBackdrop.setVisibility(8);
                this.mBackdropBack.setImageDrawable((Drawable)null);
                this.mMediaPlayer.clearControls();
                if (notificationShadeWindowController != null) {
                    notificationShadeWindowController.setBackdropShowing(false);
                }
            }
            else {
                if (notificationShadeWindowController != null) {
                    notificationShadeWindowController.setBackdropShowing(false);
                }
                this.mBackdrop.animate().alpha(0.0f).setInterpolator((TimeInterpolator)Interpolators.ACCELERATE_DECELERATE).setDuration(300L).setStartDelay(0L).withEndAction((Runnable)new _$$Lambda$NotificationMediaManager$5ApBYxWBRgBH6AkWUHgwLiCFqEk(this));
                if (this.mKeyguardStateController.isKeyguardFadingAway()) {
                    this.mBackdrop.animate().setDuration(this.mKeyguardStateController.getShortenedFadingAwayDuration()).setStartDelay(this.mKeyguardStateController.getKeyguardFadingAwayDelay()).setInterpolator((TimeInterpolator)Interpolators.LINEAR).start();
                }
            }
        }
    }
    
    private int getMediaControllerPlaybackState(final MediaController mediaController) {
        if (mediaController != null) {
            final PlaybackState playbackState = mediaController.getPlaybackState();
            if (playbackState != null) {
                return playbackState.getState();
            }
        }
        return 0;
    }
    
    private boolean isPlaybackActive(final int n) {
        boolean b = true;
        if (n == 1 || n == 7 || n == 0) {
            b = false;
        }
        return b;
    }
    
    public static boolean isPlayingState(final int i) {
        return NotificationMediaManager.PAUSED_MEDIA_STATES.contains(i) ^ true;
    }
    
    private Bitmap processArtwork(final Bitmap bitmap) {
        return this.mMediaArtworkProcessor.processArtwork(this.mContext, bitmap);
    }
    
    private void removeTask(final AsyncTask<?, ?, ?> asyncTask) {
        this.mProcessArtworkTasks.remove(asyncTask);
    }
    
    private boolean sameSessions(final MediaController mediaController, final MediaController mediaController2) {
        return mediaController == mediaController2 || (mediaController != null && mediaController.controlsSameSession(mediaController2));
    }
    
    public void addCallback(final MediaListener e) {
        this.mMediaListeners.add(e);
        e.onMetadataOrStateChanged(this.mMediaMetadata, this.getMediaControllerPlaybackState(this.mMediaController));
    }
    
    public void clearCurrentMediaNotification() {
        this.mMediaNotificationKey = null;
        this.clearCurrentMediaNotificationSession();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("    mMediaSessionManager=");
        printWriter.println(this.mMediaSessionManager);
        printWriter.print("    mMediaNotificationKey=");
        printWriter.println(this.mMediaNotificationKey);
        printWriter.print("    mMediaController=");
        printWriter.print(this.mMediaController);
        if (this.mMediaController != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" state=");
            sb.append(this.mMediaController.getPlaybackState());
            printWriter.print(sb.toString());
        }
        printWriter.println();
        printWriter.print("    mMediaMetadata=");
        printWriter.print(this.mMediaMetadata);
        if (this.mMediaMetadata != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(" title=");
            sb2.append((Object)this.mMediaMetadata.getText("android.media.metadata.TITLE"));
            printWriter.print(sb2.toString());
        }
        printWriter.println();
    }
    
    public void findAndUpdateMediaNotifications() {
        synchronized (this.mEntryManager) {
            final Collection<NotificationEntry> allNotifs = this.mEntryManager.getAllNotifs();
            while (true) {
                for (Object o : allNotifs) {
                    if (((NotificationEntry)o).isMediaNotification()) {
                        final MediaSession$Token mediaSession$Token = (MediaSession$Token)((NotificationEntry)o).getSbn().getNotification().extras.getParcelable("android.mediaSession");
                        if (mediaSession$Token == null) {
                            continue;
                        }
                        MediaController mediaController = new MediaController(this.mContext, mediaSession$Token);
                        if (3 == this.getMediaControllerPlaybackState(mediaController)) {
                            NotificationEntry notificationEntry = (NotificationEntry)o;
                            MediaController mMediaController = mediaController;
                            if (o == null) {
                                notificationEntry = (NotificationEntry)o;
                                mMediaController = mediaController;
                                if (this.mMediaSessionManager != null) {
                                    final Iterator iterator2 = this.mMediaSessionManager.getActiveSessionsForUser((ComponentName)null, -1).iterator();
                                    while (true) {
                                        notificationEntry = (NotificationEntry)o;
                                        mMediaController = mediaController;
                                        if (!iterator2.hasNext()) {
                                            break;
                                        }
                                        final MediaController mediaController2 = iterator2.next();
                                        if (3 != this.getMediaControllerPlaybackState(mediaController2)) {
                                            continue;
                                        }
                                        final String packageName = mediaController2.getPackageName();
                                        for (final NotificationEntry notificationEntry2 : allNotifs) {
                                            if (notificationEntry2.getSbn().getPackageName().equals(packageName)) {
                                                mediaController = mediaController2;
                                                o = notificationEntry2;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            boolean b;
                            if (mMediaController != null && !this.sameSessions(this.mMediaController, mMediaController)) {
                                this.clearCurrentMediaNotificationSession();
                                (this.mMediaController = mMediaController).registerCallback(this.mMediaListener);
                                this.mMediaMetadata = this.mMediaController.getMetadata();
                                b = true;
                            }
                            else {
                                b = false;
                            }
                            if (notificationEntry != null && !notificationEntry.getSbn().getKey().equals(this.mMediaNotificationKey)) {
                                this.mMediaNotificationKey = notificationEntry.getSbn().getKey();
                            }
                            // monitorexit(this.mEntryManager)
                            if (b) {
                                this.mEntryManager.updateNotifications("NotificationMediaManager - metaDataChanged");
                            }
                            this.dispatchUpdateMediaMetaData(b, true);
                            return;
                        }
                        continue;
                    }
                }
                MediaController mediaController;
                Object o = mediaController = null;
                continue;
            }
        }
    }
    
    public Icon getMediaIcon() {
        if (this.mMediaNotificationKey == null) {
            return null;
        }
        synchronized (this.mEntryManager) {
            final NotificationEntry activeNotificationUnfiltered = this.mEntryManager.getActiveNotificationUnfiltered(this.mMediaNotificationKey);
            if (activeNotificationUnfiltered != null && activeNotificationUnfiltered.getIcons().getShelfIcon() != null) {
                return activeNotificationUnfiltered.getIcons().getShelfIcon().getSourceIcon();
            }
            return null;
        }
    }
    
    public MediaMetadata getMediaMetadata() {
        return this.mMediaMetadata;
    }
    
    public String getMediaNotificationKey() {
        return this.mMediaNotificationKey;
    }
    
    public boolean getShowCompactMediaSeekbar() {
        return this.mShowCompactMediaSeekbar;
    }
    
    public void onNotificationRemoved(final String s) {
        if (s.equals(this.mMediaNotificationKey)) {
            this.clearCurrentMediaNotification();
            this.dispatchUpdateMediaMetaData(true, true);
        }
    }
    
    public void removeCallback(final MediaListener o) {
        this.mMediaListeners.remove(o);
    }
    
    public void setBiometricUnlockController(final BiometricUnlockController mBiometricUnlockController) {
        this.mBiometricUnlockController = mBiometricUnlockController;
    }
    
    public void setUpWithPresenter(final NotificationPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }
    
    public void setup(final BackDropView mBackdrop, final ImageView mBackdropFront, final ImageView mBackdropBack, final ScrimController mScrimController, final LockscreenWallpaper mLockscreenWallpaper) {
        this.mBackdrop = mBackdrop;
        this.mBackdropFront = mBackdropFront;
        this.mBackdropBack = mBackdropBack;
        this.mScrimController = mScrimController;
        this.mLockscreenWallpaper = mLockscreenWallpaper;
    }
    
    public void updateMediaMetaData(final boolean b, final boolean b2) {
        Trace.beginSection("StatusBar#updateMediaMetaData");
        if (this.mBackdrop == null) {
            Trace.endSection();
            return;
        }
        final BiometricUnlockController mBiometricUnlockController = this.mBiometricUnlockController;
        final boolean b3 = mBiometricUnlockController != null && mBiometricUnlockController.isWakeAndUnlock();
        if (!this.mKeyguardStateController.isLaunchTransitionFadingAway() && !b3) {
            final MediaMetadata mediaMetadata = this.getMediaMetadata();
            Bitmap bitmap;
            if (mediaMetadata != null && !this.mKeyguardBypassController.getBypassEnabled()) {
                if ((bitmap = mediaMetadata.getBitmap("android.media.metadata.ART")) == null) {
                    bitmap = mediaMetadata.getBitmap("android.media.metadata.ALBUM_ART");
                }
            }
            else {
                bitmap = null;
            }
            final NotificationEntry activeNotificationUnfiltered = this.mEntryManager.getActiveNotificationUnfiltered(this.mMediaNotificationKey);
            if (activeNotificationUnfiltered != null) {
                this.mMediaPlayer.updateControls(activeNotificationUnfiltered, this.getMediaIcon(), mediaMetadata);
            }
            else {
                this.mMediaPlayer.clearControls();
            }
            if (b) {
                final Iterator<AsyncTask<?, ?, ?>> iterator = this.mProcessArtworkTasks.iterator();
                while (iterator.hasNext()) {
                    iterator.next().cancel(true);
                }
                this.mProcessArtworkTasks.clear();
            }
            if (bitmap != null && !Utils.useQsMediaPlayer(this.mContext)) {
                this.mProcessArtworkTasks.add((AsyncTask<?, ?, ?>)new ProcessArtworkTask(this, b, b2).execute((Object[])new Bitmap[] { bitmap }));
            }
            else {
                this.finishUpdateMediaMetaData(b, b2, null);
            }
            Trace.endSection();
            return;
        }
        this.mBackdrop.setVisibility(4);
        this.mMediaPlayer.clearControls();
        Trace.endSection();
    }
    
    public interface MediaListener
    {
        void onMetadataOrStateChanged(final MediaMetadata p0, final int p1);
    }
    
    private static final class ProcessArtworkTask extends AsyncTask<Bitmap, Void, Bitmap>
    {
        private final boolean mAllowEnterAnimation;
        private final WeakReference<NotificationMediaManager> mManagerRef;
        private final boolean mMetaDataChanged;
        
        ProcessArtworkTask(final NotificationMediaManager referent, final boolean mMetaDataChanged, final boolean mAllowEnterAnimation) {
            this.mManagerRef = new WeakReference<NotificationMediaManager>(referent);
            this.mMetaDataChanged = mMetaDataChanged;
            this.mAllowEnterAnimation = mAllowEnterAnimation;
        }
        
        protected Bitmap doInBackground(final Bitmap... array) {
            final NotificationMediaManager notificationMediaManager = this.mManagerRef.get();
            if (notificationMediaManager != null && array.length != 0 && !this.isCancelled()) {
                return notificationMediaManager.processArtwork(array[0]);
            }
            return null;
        }
        
        protected void onCancelled(final Bitmap bitmap) {
            if (bitmap != null) {
                bitmap.recycle();
            }
            final NotificationMediaManager notificationMediaManager = this.mManagerRef.get();
            if (notificationMediaManager != null) {
                notificationMediaManager.removeTask(this);
            }
        }
        
        protected void onPostExecute(final Bitmap bitmap) {
            final NotificationMediaManager notificationMediaManager = this.mManagerRef.get();
            if (notificationMediaManager != null && !this.isCancelled()) {
                notificationMediaManager.removeTask(this);
                notificationMediaManager.finishUpdateMediaMetaData(this.mMetaDataChanged, this.mAllowEnterAnimation, bitmap);
            }
        }
    }
}
