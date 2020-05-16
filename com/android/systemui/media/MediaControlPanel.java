// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import java.util.Iterator;
import java.util.List;
import com.android.systemui.Dependency;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Icon;
import android.media.session.PlaybackState;
import com.android.systemui.R$drawable;
import android.view.View$OnClickListener;
import android.widget.ImageButton;
import com.android.settingslib.widget.AdaptiveIcon;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import com.android.systemui.R$dimen;
import com.android.systemui.util.Assert;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.plugins.ActivityStarter;
import android.app.PendingIntent;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import android.app.PendingIntent$CanceledException;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.content.Intent;
import android.view.LayoutInflater;
import android.media.MediaMetadata;
import android.util.Log;
import android.view.ViewGroup;
import android.media.session.MediaSession$Token;
import android.view.View$OnAttachStateChangeListener;
import android.media.session.MediaController$Callback;
import android.view.View;
import android.content.ComponentName;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.media.session.MediaController;
import android.content.Context;
import java.util.concurrent.Executor;

public class MediaControlPanel
{
    protected static final int[] NOTIF_ACTION_IDS;
    private final int[] mActionIds;
    private int mBackgroundColor;
    private final Executor mBackgroundExecutor;
    private Context mContext;
    private MediaController mController;
    private int mForegroundColor;
    private final Executor mForegroundExecutor;
    private boolean mIsRegistered;
    private final NotificationMediaManager.MediaListener mMediaListener;
    private final NotificationMediaManager mMediaManager;
    protected LinearLayout mMediaNotifView;
    protected ComponentName mRecvComponent;
    private View mSeamless;
    private final MediaController$Callback mSessionCallback;
    private final View$OnAttachStateChangeListener mStateListener;
    private MediaSession$Token mToken;
    
    static {
        NOTIF_ACTION_IDS = new int[] { 16908691, 16908692, 16908693, 16908694, 16908695 };
    }
    
    public MediaControlPanel(final Context mContext, final ViewGroup viewGroup, final NotificationMediaManager mMediaManager, final int n, final int[] mActionIds, final Executor mForegroundExecutor, final Executor mBackgroundExecutor) {
        this.mIsRegistered = false;
        this.mSessionCallback = new MediaController$Callback() {
            public void onSessionDestroyed() {
                Log.d("MediaControlPanel", "session destroyed");
                MediaControlPanel.this.mController.unregisterCallback(MediaControlPanel.this.mSessionCallback);
                MediaControlPanel.this.clearControls();
                MediaControlPanel.this.makeInactive();
            }
        };
        this.mMediaListener = new NotificationMediaManager.MediaListener() {
            @Override
            public void onMetadataOrStateChanged(final MediaMetadata mediaMetadata, final int n) {
                if (n == 0) {
                    MediaControlPanel.this.clearControls();
                    MediaControlPanel.this.makeInactive();
                }
            }
        };
        this.mStateListener = (View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
                MediaControlPanel.this.makeActive();
            }
            
            public void onViewDetachedFromWindow(final View view) {
                MediaControlPanel.this.makeInactive();
            }
        };
        this.mContext = mContext;
        (this.mMediaNotifView = (LinearLayout)LayoutInflater.from(mContext).inflate(n, viewGroup, false)).addOnAttachStateChangeListener(this.mStateListener);
        this.mMediaManager = mMediaManager;
        this.mActionIds = mActionIds;
        this.mForegroundExecutor = mForegroundExecutor;
        this.mBackgroundExecutor = mBackgroundExecutor;
    }
    
    private void makeActive() {
        Assert.isMainThread();
        if (!this.mIsRegistered) {
            this.mMediaManager.addCallback(this.mMediaListener);
            this.mIsRegistered = true;
        }
    }
    
    private void makeInactive() {
        Assert.isMainThread();
        if (this.mIsRegistered) {
            this.mMediaManager.removeCallback(this.mMediaListener);
            this.mIsRegistered = false;
        }
    }
    
    private void processAlbumArt(final MediaMetadata mediaMetadata, final ImageView imageView) {
        final Bitmap bitmap = mediaMetadata.getBitmap("android.media.metadata.ALBUM_ART");
        final float dimension = this.mContext.getResources().getDimension(R$dimen.qs_media_corner_radius);
        RoundedBitmapDrawable create;
        if (bitmap != null) {
            final Bitmap copy = bitmap.copy(Bitmap$Config.ARGB_8888, true);
            final int n = (int)this.mContext.getResources().getDimension(R$dimen.qs_media_album_size);
            create = RoundedBitmapDrawableFactory.create(this.mContext.getResources(), Bitmap.createScaledBitmap(copy, n, n, false));
            create.setCornerRadius(dimension);
        }
        else {
            Log.e("MediaControlPanel", "No album art available");
            create = null;
        }
        this.mForegroundExecutor.execute(new _$$Lambda$MediaControlPanel$bLTVCcRKzZHd95e2hghzZttYiHU(create, imageView));
    }
    
    private void updateChipInternal(final MediaDevice mediaDevice) {
        final ColorStateList value = ColorStateList.valueOf(this.mForegroundColor);
        final GradientDrawable gradientDrawable = (GradientDrawable)((RippleDrawable)((LinearLayout)this.mSeamless).getBackground()).getDrawable(0);
        gradientDrawable.setStroke(2, this.mForegroundColor);
        gradientDrawable.setColor(this.mBackgroundColor);
        final ImageView imageView = (ImageView)this.mSeamless.findViewById(R$id.media_seamless_image);
        final TextView textView = (TextView)this.mSeamless.findViewById(R$id.media_seamless_text);
        textView.setTextColor(value);
        if (mediaDevice != null) {
            final Drawable icon = mediaDevice.getIcon();
            imageView.setVisibility(0);
            imageView.setImageTintList(value);
            if (icon instanceof AdaptiveIcon) {
                final AdaptiveIcon imageDrawable = (AdaptiveIcon)icon;
                imageDrawable.setBackgroundColor(this.mBackgroundColor);
                imageView.setImageDrawable((Drawable)imageDrawable);
            }
            else {
                imageView.setImageDrawable(icon);
            }
            textView.setText((CharSequence)mediaDevice.getName());
        }
        else {
            imageView.setVisibility(8);
            textView.setText(17040123);
        }
    }
    
    public void clearControls() {
        int n = 0;
        int[] mActionIds;
        while (true) {
            mActionIds = this.mActionIds;
            if (n >= mActionIds.length) {
                break;
            }
            final ImageButton imageButton = (ImageButton)this.mMediaNotifView.findViewById(mActionIds[n]);
            if (imageButton != null) {
                imageButton.setVisibility(8);
            }
            ++n;
        }
        final ImageButton imageButton2 = (ImageButton)this.mMediaNotifView.findViewById(mActionIds[0]);
        imageButton2.setOnClickListener((View$OnClickListener)new _$$Lambda$MediaControlPanel$g9KC9Jr_tMNhtW8frZgw0KV_RLM(this));
        imageButton2.setImageDrawable(this.mContext.getResources().getDrawable(R$drawable.lb_ic_play));
        imageButton2.setImageTintList(ColorStateList.valueOf(this.mForegroundColor));
        imageButton2.setVisibility(0);
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    public MediaController getController() {
        return this.mController;
    }
    
    public String getMediaPlayerPackage() {
        return this.mController.getPackageName();
    }
    
    public MediaSession$Token getMediaSessionToken() {
        return this.mToken;
    }
    
    public View getView() {
        return (View)this.mMediaNotifView;
    }
    
    public boolean hasMediaSession() {
        final MediaController mController = this.mController;
        return mController != null && mController.getPlaybackState() != null;
    }
    
    public boolean isPlaying() {
        return this.isPlaying(this.mController);
    }
    
    protected boolean isPlaying(final MediaController mediaController) {
        boolean b = false;
        if (mediaController == null) {
            return false;
        }
        final PlaybackState playbackState = mediaController.getPlaybackState();
        if (playbackState == null) {
            return false;
        }
        if (playbackState.getState() == 3) {
            b = true;
        }
        return b;
    }
    
    public void setMediaSession(final MediaSession$Token mToken, final Icon icon, final int mForegroundColor, final int mBackgroundColor, final PendingIntent pendingIntent, final String text, final MediaDevice mediaDevice) {
        this.mToken = mToken;
        this.mForegroundColor = mForegroundColor;
        this.mBackgroundColor = mBackgroundColor;
        final MediaController mController = new MediaController(this.mContext, this.mToken);
        this.mController = mController;
        final MediaMetadata metadata = mController.getMetadata();
        final List queryBroadcastReceiversAsUser = this.mContext.getPackageManager().queryBroadcastReceiversAsUser(new Intent("android.intent.action.MEDIA_BUTTON"), 0, this.mContext.getUser());
        if (queryBroadcastReceiversAsUser != null) {
            for (final ResolveInfo resolveInfo : queryBroadcastReceiversAsUser) {
                if (resolveInfo.activityInfo.packageName.equals(this.mController.getPackageName())) {
                    this.mRecvComponent = resolveInfo.getComponentInfo().getComponentName();
                }
            }
        }
        this.mController.registerCallback(this.mSessionCallback);
        if (metadata == null) {
            Log.e("MediaControlPanel", "Media metadata was null");
            return;
        }
        final ImageView imageView = (ImageView)this.mMediaNotifView.findViewById(R$id.album_art);
        if (imageView != null) {
            this.mBackgroundExecutor.execute(new _$$Lambda$MediaControlPanel$3QmV2Zuee0X_jPSRRuQWUVgcKz0(this, metadata, imageView));
        }
        this.mMediaNotifView.setBackgroundTintList(ColorStateList.valueOf(this.mBackgroundColor));
        if (pendingIntent != null) {
            this.mMediaNotifView.setOnClickListener((View$OnClickListener)new _$$Lambda$MediaControlPanel$1KSHAHXoLJ2jQmQa_Hn74FQaPmI(this, pendingIntent));
        }
        final ImageView imageView2 = (ImageView)this.mMediaNotifView.findViewById(R$id.icon);
        final Drawable loadDrawable = icon.loadDrawable(this.mContext);
        loadDrawable.setTint(this.mForegroundColor);
        imageView2.setImageDrawable(loadDrawable);
        final TextView textView = (TextView)this.mMediaNotifView.findViewById(R$id.header_title);
        textView.setText((CharSequence)metadata.getString("android.media.metadata.TITLE"));
        textView.setTextColor(this.mForegroundColor);
        final TextView textView2 = (TextView)this.mMediaNotifView.findViewById(R$id.app_name);
        if (textView2 != null) {
            textView2.setText((CharSequence)text);
            textView2.setTextColor(this.mForegroundColor);
        }
        final TextView textView3 = (TextView)this.mMediaNotifView.findViewById(R$id.header_artist);
        if (textView3 != null) {
            textView3.setText((CharSequence)metadata.getString("android.media.metadata.ARTIST"));
            textView3.setTextColor(this.mForegroundColor);
        }
        final View viewById = this.mMediaNotifView.findViewById(R$id.media_seamless);
        if ((this.mSeamless = viewById) != null) {
            viewById.setVisibility(0);
            this.updateDevice(mediaDevice);
            this.mSeamless.setOnClickListener((View$OnClickListener)new _$$Lambda$MediaControlPanel$_8VkfoVAgTZoKdDaOAHAL3lxHrw(this, Dependency.get(ActivityStarter.class)));
        }
        this.makeActive();
    }
    
    public void updateDevice(final MediaDevice mediaDevice) {
        if (this.mSeamless == null) {
            return;
        }
        this.mForegroundExecutor.execute(new _$$Lambda$MediaControlPanel$3B_a8KGVd3RIVRiTzrxVgWFjYNI(this, mediaDevice));
    }
}
