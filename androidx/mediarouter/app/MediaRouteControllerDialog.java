// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.app.PendingIntent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.SystemClock;
import androidx.core.util.ObjectsCompat;
import java.io.IOException;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.InputStream;
import android.os.AsyncTask;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView$ScaleType;
import android.view.View$MeasureSpec;
import android.content.res.Resources;
import android.view.KeyEvent;
import androidx.mediarouter.R$integer;
import java.util.HashSet;
import android.widget.ListAdapter;
import java.util.ArrayList;
import android.widget.SeekBar$OnSeekBarChangeListener;
import android.app.PendingIntent$CanceledException;
import android.view.View$OnClickListener;
import androidx.mediarouter.R$layout;
import android.os.Bundle;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.R$id;
import java.util.Iterator;
import android.view.animation.TranslateAnimation;
import android.view.animation.AnimationSet;
import androidx.mediarouter.R$attr;
import androidx.mediarouter.R$string;
import android.text.TextUtils;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.ViewGroup$LayoutParams;
import java.util.HashMap;
import java.util.Collection;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation$AnimationListener;
import android.view.animation.Transformation;
import android.view.animation.Animation;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Rect;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import androidx.mediarouter.R$interpolator;
import android.os.Build$VERSION;
import androidx.mediarouter.R$dimen;
import java.util.concurrent.TimeUnit;
import android.util.Log;
import java.util.Map;
import android.widget.SeekBar;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.support.v4.media.session.MediaControllerCompat;
import java.util.Set;
import androidx.mediarouter.media.MediaRouter;
import java.util.List;
import android.widget.Button;
import android.widget.LinearLayout;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.net.Uri;
import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import androidx.appcompat.app.AlertDialog;

public class MediaRouteControllerDialog extends AlertDialog
{
    static final int CONNECTION_TIMEOUT_MILLIS;
    static final boolean DEBUG;
    private Interpolator mAccelerateDecelerateInterpolator;
    final AccessibilityManager mAccessibilityManager;
    int mArtIconBackgroundColor;
    Bitmap mArtIconBitmap;
    boolean mArtIconIsLoaded;
    Bitmap mArtIconLoadedBitmap;
    Uri mArtIconUri;
    private ImageView mArtView;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private ImageButton mCloseButton;
    Context mContext;
    MediaControllerCallback mControllerCallback;
    private boolean mCreated;
    private FrameLayout mCustomControlLayout;
    private View mCustomControlView;
    FrameLayout mDefaultControlLayout;
    MediaDescriptionCompat mDescription;
    private LinearLayout mDialogAreaLayout;
    private int mDialogContentWidth;
    private Button mDisconnectButton;
    private View mDividerView;
    private FrameLayout mExpandableAreaLayout;
    private Interpolator mFastOutSlowInInterpolator;
    FetchArtTask mFetchArtTask;
    private MediaRouteExpandCollapseButton mGroupExpandCollapseButton;
    int mGroupListAnimationDurationMs;
    Runnable mGroupListFadeInAnimation;
    private int mGroupListFadeInDurationMs;
    private int mGroupListFadeOutDurationMs;
    private List<MediaRouter.RouteInfo> mGroupMemberRoutes;
    Set<MediaRouter.RouteInfo> mGroupMemberRoutesAdded;
    Set<MediaRouter.RouteInfo> mGroupMemberRoutesAnimatingWithBitmap;
    private Set<MediaRouter.RouteInfo> mGroupMemberRoutesRemoved;
    boolean mHasPendingUpdate;
    private Interpolator mInterpolator;
    boolean mIsGroupExpanded;
    boolean mIsGroupListAnimating;
    boolean mIsGroupListAnimationPending;
    private Interpolator mLinearOutSlowInInterpolator;
    MediaControllerCompat mMediaController;
    private LinearLayout mMediaMainControlLayout;
    boolean mPendingUpdateAnimationNeeded;
    private ImageButton mPlaybackControlButton;
    private RelativeLayout mPlaybackControlLayout;
    final MediaRouter.RouteInfo mRoute;
    MediaRouter.RouteInfo mRouteInVolumeSliderTouched;
    private TextView mRouteNameTextView;
    final MediaRouter mRouter;
    PlaybackStateCompat mState;
    private Button mStopCastingButton;
    private TextView mSubtitleView;
    private TextView mTitleView;
    VolumeChangeListener mVolumeChangeListener;
    private boolean mVolumeControlEnabled;
    private LinearLayout mVolumeControlLayout;
    VolumeGroupAdapter mVolumeGroupAdapter;
    OverlayListView mVolumeGroupList;
    private int mVolumeGroupListItemHeight;
    private int mVolumeGroupListItemIconSize;
    private int mVolumeGroupListMaxHeight;
    private final int mVolumeGroupListPaddingTop;
    SeekBar mVolumeSlider;
    Map<MediaRouter.RouteInfo, SeekBar> mVolumeSliderMap;
    
    static {
        DEBUG = Log.isLoggable("MediaRouteCtrlDialog", 3);
        CONNECTION_TIMEOUT_MILLIS = (int)TimeUnit.SECONDS.toMillis(30L);
    }
    
    public MediaRouteControllerDialog(final Context context) {
        this(context, 0);
    }
    
    public MediaRouteControllerDialog(Context themedDialogContext, final int n) {
        themedDialogContext = MediaRouterThemeHelper.createThemedDialogContext(themedDialogContext, n, true);
        super(themedDialogContext, MediaRouterThemeHelper.createThemedDialogStyle(themedDialogContext));
        this.mVolumeControlEnabled = true;
        this.mGroupListFadeInAnimation = new Runnable() {
            @Override
            public void run() {
                MediaRouteControllerDialog.this.startGroupListFadeInAnimation();
            }
        };
        this.mContext = this.getContext();
        this.mControllerCallback = new MediaControllerCallback();
        this.mRouter = MediaRouter.getInstance(this.mContext);
        this.mCallback = new MediaRouterCallback();
        this.mRoute = this.mRouter.getSelectedRoute();
        this.setMediaSession(this.mRouter.getMediaSessionToken());
        this.mVolumeGroupListPaddingTop = this.mContext.getResources().getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_padding_top);
        this.mAccessibilityManager = (AccessibilityManager)this.mContext.getSystemService("accessibility");
        if (Build$VERSION.SDK_INT >= 21) {
            this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(themedDialogContext, R$interpolator.mr_linear_out_slow_in);
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(themedDialogContext, R$interpolator.mr_fast_out_slow_in);
        }
        this.mAccelerateDecelerateInterpolator = (Interpolator)new AccelerateDecelerateInterpolator();
    }
    
    private void animateGroupListItems(final Map<MediaRouter.RouteInfo, Rect> map, final Map<MediaRouter.RouteInfo, BitmapDrawable> map2) {
        this.mVolumeGroupList.setEnabled(false);
        this.mVolumeGroupList.requestLayout();
        this.mIsGroupListAnimating = true;
        this.mVolumeGroupList.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mVolumeGroupList.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                MediaRouteControllerDialog.this.animateGroupListItemsInternal(map, map2);
            }
        });
    }
    
    private void animateLayoutHeight(final View view, final int n) {
        final Animation animation = new Animation(this) {
            final /* synthetic */ int val$startValue = getLayoutHeight(view);
            
            protected void applyTransformation(final float n, final Transformation transformation) {
                final int val$startValue = this.val$startValue;
                MediaRouteControllerDialog.setLayoutHeight(view, val$startValue - (int)((val$startValue - n) * n));
            }
        };
        animation.setDuration((long)this.mGroupListAnimationDurationMs);
        if (Build$VERSION.SDK_INT >= 21) {
            animation.setInterpolator(this.mInterpolator);
        }
        view.startAnimation((Animation)animation);
    }
    
    private boolean canShowPlaybackControlLayout() {
        return this.mCustomControlView == null && (this.mDescription != null || this.mState != null);
    }
    
    private void fadeInAddedRoutes() {
        final Animation$AnimationListener animationListener = (Animation$AnimationListener)new Animation$AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                MediaRouteControllerDialog.this.finishAnimation(true);
            }
            
            public void onAnimationRepeat(final Animation animation) {
            }
            
            public void onAnimationStart(final Animation animation) {
            }
        };
        final int firstVisiblePosition = this.mVolumeGroupList.getFirstVisiblePosition();
        int i = 0;
        int n = 0;
        while (i < this.mVolumeGroupList.getChildCount()) {
            final View child = this.mVolumeGroupList.getChildAt(i);
            final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)this.mVolumeGroupAdapter.getItem(firstVisiblePosition + i);
            int n2 = n;
            if (this.mGroupMemberRoutesAdded.contains(routeInfo)) {
                final AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                ((Animation)alphaAnimation).setDuration((long)this.mGroupListFadeInDurationMs);
                ((Animation)alphaAnimation).setFillEnabled(true);
                ((Animation)alphaAnimation).setFillAfter(true);
                if ((n2 = n) == 0) {
                    ((Animation)alphaAnimation).setAnimationListener((Animation$AnimationListener)animationListener);
                    n2 = 1;
                }
                child.clearAnimation();
                child.startAnimation((Animation)alphaAnimation);
            }
            ++i;
            n = n2;
        }
    }
    
    private static int getLayoutHeight(final View view) {
        return view.getLayoutParams().height;
    }
    
    private int getMainControllerHeight(final boolean b) {
        int n = 0;
        if (b || this.mVolumeControlLayout.getVisibility() == 0) {
            int n3;
            final int n2 = n3 = 0 + (this.mMediaMainControlLayout.getPaddingTop() + this.mMediaMainControlLayout.getPaddingBottom());
            if (b) {
                n3 = n2 + this.mPlaybackControlLayout.getMeasuredHeight();
            }
            int n4 = n3;
            if (this.mVolumeControlLayout.getVisibility() == 0) {
                n4 = n3 + this.mVolumeControlLayout.getMeasuredHeight();
            }
            n = n4;
            if (b) {
                n = n4;
                if (this.mVolumeControlLayout.getVisibility() == 0) {
                    n = n4 + this.mDividerView.getMeasuredHeight();
                }
            }
        }
        return n;
    }
    
    static boolean isBitmapRecycled(final Bitmap bitmap) {
        return bitmap != null && bitmap.isRecycled();
    }
    
    private boolean isIconChanged() {
        final MediaDescriptionCompat mDescription = this.mDescription;
        Uri iconUri = null;
        Bitmap iconBitmap;
        if (mDescription == null) {
            iconBitmap = null;
        }
        else {
            iconBitmap = mDescription.getIconBitmap();
        }
        final MediaDescriptionCompat mDescription2 = this.mDescription;
        if (mDescription2 != null) {
            iconUri = mDescription2.getIconUri();
        }
        final FetchArtTask mFetchArtTask = this.mFetchArtTask;
        Bitmap bitmap;
        if (mFetchArtTask == null) {
            bitmap = this.mArtIconBitmap;
        }
        else {
            bitmap = mFetchArtTask.getIconBitmap();
        }
        final FetchArtTask mFetchArtTask2 = this.mFetchArtTask;
        Uri uri;
        if (mFetchArtTask2 == null) {
            uri = this.mArtIconUri;
        }
        else {
            uri = mFetchArtTask2.getIconUri();
        }
        return bitmap != iconBitmap || (bitmap == null && !uriEquals(uri, iconUri));
    }
    
    private void rebuildVolumeGroupList(final boolean b) {
        final List<MediaRouter.RouteInfo> memberRoutes = this.mRoute.getMemberRoutes();
        if (memberRoutes.isEmpty()) {
            this.mGroupMemberRoutes.clear();
            this.mVolumeGroupAdapter.notifyDataSetChanged();
        }
        else if (MediaRouteDialogHelper.listUnorderedEquals(this.mGroupMemberRoutes, memberRoutes)) {
            this.mVolumeGroupAdapter.notifyDataSetChanged();
        }
        else {
            HashMap<MediaRouter.RouteInfo, Rect> itemBoundMap;
            if (b) {
                itemBoundMap = MediaRouteDialogHelper.getItemBoundMap(this.mVolumeGroupList, (android.widget.ArrayAdapter<MediaRouter.RouteInfo>)this.mVolumeGroupAdapter);
            }
            else {
                itemBoundMap = null;
            }
            HashMap<MediaRouter.RouteInfo, BitmapDrawable> itemBitmapMap;
            if (b) {
                itemBitmapMap = MediaRouteDialogHelper.getItemBitmapMap(this.mContext, this.mVolumeGroupList, (android.widget.ArrayAdapter<MediaRouter.RouteInfo>)this.mVolumeGroupAdapter);
            }
            else {
                itemBitmapMap = null;
            }
            this.mGroupMemberRoutesAdded = MediaRouteDialogHelper.getItemsAdded(this.mGroupMemberRoutes, memberRoutes);
            this.mGroupMemberRoutesRemoved = MediaRouteDialogHelper.getItemsRemoved(this.mGroupMemberRoutes, memberRoutes);
            this.mGroupMemberRoutes.addAll(0, this.mGroupMemberRoutesAdded);
            this.mGroupMemberRoutes.removeAll(this.mGroupMemberRoutesRemoved);
            this.mVolumeGroupAdapter.notifyDataSetChanged();
            if (b && this.mIsGroupExpanded && this.mGroupMemberRoutesAdded.size() + this.mGroupMemberRoutesRemoved.size() > 0) {
                this.animateGroupListItems(itemBoundMap, itemBitmapMap);
            }
            else {
                this.mGroupMemberRoutesAdded = null;
                this.mGroupMemberRoutesRemoved = null;
            }
        }
    }
    
    static void setLayoutHeight(final View view, final int height) {
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }
    
    private void setMediaSession(final MediaSessionCompat.Token token) {
        final MediaControllerCompat mMediaController = this.mMediaController;
        final MediaDescriptionCompat mediaDescriptionCompat = null;
        if (mMediaController != null) {
            mMediaController.unregisterCallback((MediaControllerCompat.Callback)this.mControllerCallback);
            this.mMediaController = null;
        }
        if (token == null) {
            return;
        }
        if (!this.mAttachedToWindow) {
            return;
        }
        (this.mMediaController = new MediaControllerCompat(this.mContext, token)).registerCallback((MediaControllerCompat.Callback)this.mControllerCallback);
        final MediaMetadataCompat metadata = this.mMediaController.getMetadata();
        MediaDescriptionCompat description;
        if (metadata == null) {
            description = mediaDescriptionCompat;
        }
        else {
            description = metadata.getDescription();
        }
        this.mDescription = description;
        this.mState = this.mMediaController.getPlaybackState();
        this.updateArtIconIfNeeded();
        this.update(false);
    }
    
    private void updateMediaControlVisibility(final boolean b) {
        final View mDividerView = this.mDividerView;
        final int visibility = this.mVolumeControlLayout.getVisibility();
        final int n = 0;
        int visibility2;
        if (visibility == 0 && b) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mDividerView.setVisibility(visibility2);
        final LinearLayout mMediaMainControlLayout = this.mMediaMainControlLayout;
        int visibility3 = n;
        if (this.mVolumeControlLayout.getVisibility() == 8) {
            visibility3 = n;
            if (!b) {
                visibility3 = 8;
            }
        }
        mMediaMainControlLayout.setVisibility(visibility3);
    }
    
    private void updatePlaybackControlLayout() {
        if (this.canShowPlaybackControlLayout()) {
            final MediaDescriptionCompat mDescription = this.mDescription;
            CharSequence subtitle = null;
            CharSequence title;
            if (mDescription == null) {
                title = null;
            }
            else {
                title = mDescription.getTitle();
            }
            final boolean empty = TextUtils.isEmpty(title);
            int n = 1;
            final boolean b = empty ^ true;
            final MediaDescriptionCompat mDescription2 = this.mDescription;
            if (mDescription2 != null) {
                subtitle = mDescription2.getSubtitle();
            }
            final boolean b2 = TextUtils.isEmpty(subtitle) ^ true;
            final int presentationDisplayId = this.mRoute.getPresentationDisplayId();
            int visibility = 0;
            int n2 = 0;
            int n4 = 0;
            Label_0217: {
                Label_0100: {
                    if (presentationDisplayId != -1) {
                        this.mTitleView.setText(R$string.mr_controller_casting_screen);
                    }
                    else {
                        final PlaybackStateCompat mState = this.mState;
                        if (mState != null && mState.getState() != 0) {
                            if (!b && !b2) {
                                this.mTitleView.setText(R$string.mr_controller_no_info_available);
                            }
                            else {
                                boolean b3;
                                if (b) {
                                    this.mTitleView.setText(title);
                                    b3 = true;
                                }
                                else {
                                    b3 = false;
                                }
                                n2 = (b3 ? 1 : 0);
                                if (b2) {
                                    this.mSubtitleView.setText(subtitle);
                                    final int n3 = 1;
                                    n2 = (b3 ? 1 : 0);
                                    n4 = n3;
                                    break Label_0217;
                                }
                                break Label_0100;
                            }
                        }
                        else {
                            this.mTitleView.setText(R$string.mr_controller_no_media_selected);
                        }
                    }
                    n2 = 1;
                }
                n4 = 0;
            }
            final TextView mTitleView = this.mTitleView;
            int visibility2;
            if (n2 != 0) {
                visibility2 = 0;
            }
            else {
                visibility2 = 8;
            }
            mTitleView.setVisibility(visibility2);
            final TextView mSubtitleView = this.mSubtitleView;
            int visibility3;
            if (n4 != 0) {
                visibility3 = 0;
            }
            else {
                visibility3 = 8;
            }
            mSubtitleView.setVisibility(visibility3);
            final PlaybackStateCompat mState2 = this.mState;
            if (mState2 != null) {
                final boolean b4 = mState2.getState() == 6 || this.mState.getState() == 3;
                final Context context = this.mPlaybackControlButton.getContext();
                int n5;
                int n6;
                if (b4 && this.isPauseActionSupported()) {
                    n5 = R$attr.mediaRoutePauseDrawable;
                    n6 = R$string.mr_controller_pause;
                }
                else if (b4 && this.isStopActionSupported()) {
                    n5 = R$attr.mediaRouteStopDrawable;
                    n6 = R$string.mr_controller_stop;
                }
                else if (!b4 && this.isPlayActionSupported()) {
                    n5 = R$attr.mediaRoutePlayDrawable;
                    n6 = R$string.mr_controller_play;
                }
                else {
                    n5 = 0;
                    n6 = (n = n5);
                }
                final ImageButton mPlaybackControlButton = this.mPlaybackControlButton;
                if (n == 0) {
                    visibility = 8;
                }
                mPlaybackControlButton.setVisibility(visibility);
                if (n != 0) {
                    this.mPlaybackControlButton.setImageResource(MediaRouterThemeHelper.getThemeResource(context, n5));
                    this.mPlaybackControlButton.setContentDescription(context.getResources().getText(n6));
                }
            }
        }
    }
    
    private void updateVolumeControlLayout() {
        final boolean volumeControlAvailable = this.isVolumeControlAvailable(this.mRoute);
        int visibility = 8;
        if (volumeControlAvailable) {
            if (this.mVolumeControlLayout.getVisibility() == 8) {
                this.mVolumeControlLayout.setVisibility(0);
                this.mVolumeSlider.setMax(this.mRoute.getVolumeMax());
                this.mVolumeSlider.setProgress(this.mRoute.getVolume());
                final MediaRouteExpandCollapseButton mGroupExpandCollapseButton = this.mGroupExpandCollapseButton;
                if (this.mRoute.isGroup()) {
                    visibility = 0;
                }
                mGroupExpandCollapseButton.setVisibility(visibility);
            }
        }
        else {
            this.mVolumeControlLayout.setVisibility(8);
        }
    }
    
    private static boolean uriEquals(final Uri uri, final Uri uri2) {
        return (uri != null && uri.equals((Object)uri2)) || (uri == null && uri2 == null);
    }
    
    void animateGroupListItemsInternal(final Map<MediaRouter.RouteInfo, Rect> map, final Map<MediaRouter.RouteInfo, BitmapDrawable> map2) {
        final Set<MediaRouter.RouteInfo> mGroupMemberRoutesAdded = this.mGroupMemberRoutesAdded;
        if (mGroupMemberRoutesAdded != null) {
            if (this.mGroupMemberRoutesRemoved != null) {
                final int n = mGroupMemberRoutesAdded.size() - this.mGroupMemberRoutesRemoved.size();
                final Animation$AnimationListener animationListener = (Animation$AnimationListener)new Animation$AnimationListener() {
                    public void onAnimationEnd(final Animation animation) {
                    }
                    
                    public void onAnimationRepeat(final Animation animation) {
                    }
                    
                    public void onAnimationStart(final Animation animation) {
                        MediaRouteControllerDialog.this.mVolumeGroupList.startAnimationAll();
                        final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
                        this$0.mVolumeGroupList.postDelayed(this$0.mGroupListFadeInAnimation, (long)this$0.mGroupListAnimationDurationMs);
                    }
                };
                final int firstVisiblePosition = this.mVolumeGroupList.getFirstVisiblePosition();
                int i = 0;
                int n2 = 0;
                while (i < this.mVolumeGroupList.getChildCount()) {
                    final View child = this.mVolumeGroupList.getChildAt(i);
                    final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)this.mVolumeGroupAdapter.getItem(firstVisiblePosition + i);
                    final Rect rect = map.get(routeInfo);
                    final int top = child.getTop();
                    int top2;
                    if (rect != null) {
                        top2 = rect.top;
                    }
                    else {
                        top2 = this.mVolumeGroupListItemHeight * n + top;
                    }
                    final AnimationSet set = new AnimationSet(true);
                    final Set<MediaRouter.RouteInfo> mGroupMemberRoutesAdded2 = this.mGroupMemberRoutesAdded;
                    int n3 = top2;
                    if (mGroupMemberRoutesAdded2 != null) {
                        n3 = top2;
                        if (mGroupMemberRoutesAdded2.contains(routeInfo)) {
                            final AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 0.0f);
                            ((Animation)alphaAnimation).setDuration((long)this.mGroupListFadeInDurationMs);
                            set.addAnimation((Animation)alphaAnimation);
                            n3 = top;
                        }
                    }
                    final TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float)(n3 - top), 0.0f);
                    ((Animation)translateAnimation).setDuration((long)this.mGroupListAnimationDurationMs);
                    set.addAnimation((Animation)translateAnimation);
                    set.setFillAfter(true);
                    set.setFillEnabled(true);
                    set.setInterpolator(this.mInterpolator);
                    int n4;
                    if ((n4 = n2) == 0) {
                        set.setAnimationListener((Animation$AnimationListener)animationListener);
                        n4 = 1;
                    }
                    child.clearAnimation();
                    child.startAnimation((Animation)set);
                    map.remove(routeInfo);
                    map2.remove(routeInfo);
                    ++i;
                    n2 = n4;
                }
                for (final Map.Entry<MediaRouter.RouteInfo, BitmapDrawable> entry : map2.entrySet()) {
                    final MediaRouter.RouteInfo routeInfo2 = entry.getKey();
                    final BitmapDrawable bitmapDrawable = entry.getValue();
                    final Rect rect2 = map.get(routeInfo2);
                    OverlayListView.OverlayObject overlayObject;
                    if (this.mGroupMemberRoutesRemoved.contains(routeInfo2)) {
                        overlayObject = new OverlayListView.OverlayObject(bitmapDrawable, rect2);
                        overlayObject.setAlphaAnimation(1.0f, 0.0f);
                        overlayObject.setDuration(this.mGroupListFadeOutDurationMs);
                        overlayObject.setInterpolator(this.mInterpolator);
                    }
                    else {
                        final int mVolumeGroupListItemHeight = this.mVolumeGroupListItemHeight;
                        overlayObject = new OverlayListView.OverlayObject(bitmapDrawable, rect2);
                        overlayObject.setTranslateYAnimation(mVolumeGroupListItemHeight * n);
                        overlayObject.setDuration(this.mGroupListAnimationDurationMs);
                        overlayObject.setInterpolator(this.mInterpolator);
                        overlayObject.setAnimationEndListener((OverlayListView.OverlayObject.OnAnimationEndListener)new OverlayListView.OverlayObject.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                MediaRouteControllerDialog.this.mGroupMemberRoutesAnimatingWithBitmap.remove(routeInfo2);
                                MediaRouteControllerDialog.this.mVolumeGroupAdapter.notifyDataSetChanged();
                            }
                        });
                        this.mGroupMemberRoutesAnimatingWithBitmap.add(routeInfo2);
                    }
                    this.mVolumeGroupList.addOverlayObject(overlayObject);
                }
            }
        }
    }
    
    void clearGroupListAnimation(final boolean b) {
        final int firstVisiblePosition = this.mVolumeGroupList.getFirstVisiblePosition();
        for (int i = 0; i < this.mVolumeGroupList.getChildCount(); ++i) {
            final View child = this.mVolumeGroupList.getChildAt(i);
            final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)this.mVolumeGroupAdapter.getItem(firstVisiblePosition + i);
            if (b) {
                final Set<MediaRouter.RouteInfo> mGroupMemberRoutesAdded = this.mGroupMemberRoutesAdded;
                if (mGroupMemberRoutesAdded != null && mGroupMemberRoutesAdded.contains(routeInfo)) {
                    continue;
                }
            }
            ((LinearLayout)child.findViewById(R$id.volume_item_container)).setVisibility(0);
            final AnimationSet set = new AnimationSet(true);
            final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
            ((Animation)alphaAnimation).setDuration(0L);
            set.addAnimation((Animation)alphaAnimation);
            ((Animation)new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f)).setDuration(0L);
            set.setFillAfter(true);
            set.setFillEnabled(true);
            child.clearAnimation();
            child.startAnimation((Animation)set);
        }
        this.mVolumeGroupList.stopAnimationAll();
        if (!b) {
            this.finishAnimation(false);
        }
    }
    
    void clearLoadedBitmap() {
        this.mArtIconIsLoaded = false;
        this.mArtIconLoadedBitmap = null;
        this.mArtIconBackgroundColor = 0;
    }
    
    void finishAnimation(final boolean b) {
        this.mGroupMemberRoutesAdded = null;
        this.mGroupMemberRoutesRemoved = null;
        this.mIsGroupListAnimating = false;
        if (this.mIsGroupListAnimationPending) {
            this.mIsGroupListAnimationPending = false;
            this.updateLayoutHeight(b);
        }
        this.mVolumeGroupList.setEnabled(true);
    }
    
    int getDesiredArtHeight(final int n, final int n2) {
        float n3;
        float n4;
        if (n >= n2) {
            n3 = this.mDialogContentWidth * (float)n2;
            n4 = (float)n;
        }
        else {
            n3 = this.mDialogContentWidth * 9.0f;
            n4 = 16.0f;
        }
        return (int)(n3 / n4 + 0.5f);
    }
    
    boolean isPauseActionSupported() {
        return (this.mState.getActions() & 0x202L) != 0x0L;
    }
    
    boolean isPlayActionSupported() {
        return (this.mState.getActions() & 0x204L) != 0x0L;
    }
    
    boolean isStopActionSupported() {
        return (this.mState.getActions() & 0x1L) != 0x0L;
    }
    
    boolean isVolumeControlAvailable(final MediaRouter.RouteInfo routeInfo) {
        final boolean mVolumeControlEnabled = this.mVolumeControlEnabled;
        boolean b = true;
        if (!mVolumeControlEnabled || routeInfo.getVolumeHandling() != 1) {
            b = false;
        }
        return b;
    }
    
    void loadInterpolator() {
        if (Build$VERSION.SDK_INT >= 21) {
            Interpolator mInterpolator;
            if (this.mIsGroupExpanded) {
                mInterpolator = this.mLinearOutSlowInInterpolator;
            }
            else {
                mInterpolator = this.mFastOutSlowInInterpolator;
            }
            this.mInterpolator = mInterpolator;
        }
        else {
            this.mInterpolator = this.mAccelerateDecelerateInterpolator;
        }
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(MediaRouteSelector.EMPTY, (MediaRouter.Callback)this.mCallback, 2);
        this.setMediaSession(this.mRouter.getMediaSessionToken());
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setBackgroundDrawableResource(17170445);
        this.setContentView(R$layout.mr_controller_material_dialog_b);
        this.findViewById(16908315).setVisibility(8);
        final ClickListener clickListener = new ClickListener();
        (this.mExpandableAreaLayout = this.findViewById(R$id.mr_expandable_area)).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                MediaRouteControllerDialog.this.dismiss();
            }
        });
        (this.mDialogAreaLayout = this.findViewById(R$id.mr_dialog_area)).setOnClickListener((View$OnClickListener)new View$OnClickListener(this) {
            public void onClick(final View view) {
            }
        });
        final int buttonTextColor = MediaRouterThemeHelper.getButtonTextColor(this.mContext);
        (this.mDisconnectButton = this.findViewById(16908314)).setText(R$string.mr_controller_disconnect);
        this.mDisconnectButton.setTextColor(buttonTextColor);
        this.mDisconnectButton.setOnClickListener((View$OnClickListener)clickListener);
        (this.mStopCastingButton = this.findViewById(16908313)).setText(R$string.mr_controller_stop_casting);
        this.mStopCastingButton.setTextColor(buttonTextColor);
        this.mStopCastingButton.setOnClickListener((View$OnClickListener)clickListener);
        this.mRouteNameTextView = this.findViewById(R$id.mr_name);
        (this.mCloseButton = this.findViewById(R$id.mr_close)).setOnClickListener((View$OnClickListener)clickListener);
        this.mCustomControlLayout = this.findViewById(R$id.mr_custom_control);
        this.mDefaultControlLayout = this.findViewById(R$id.mr_default_control);
        final View$OnClickListener view$OnClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(View sessionActivity) {
                final MediaControllerCompat mMediaController = MediaRouteControllerDialog.this.mMediaController;
                if (mMediaController != null) {
                    sessionActivity = (View)mMediaController.getSessionActivity();
                    if (sessionActivity != null) {
                        try {
                            ((PendingIntent)sessionActivity).send();
                            MediaRouteControllerDialog.this.dismiss();
                        }
                        catch (PendingIntent$CanceledException ex) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append(sessionActivity);
                            sb.append(" was not sent, it had been canceled.");
                            Log.e("MediaRouteCtrlDialog", sb.toString());
                        }
                    }
                }
            }
        };
        (this.mArtView = this.findViewById(R$id.mr_art)).setOnClickListener((View$OnClickListener)view$OnClickListener);
        this.findViewById(R$id.mr_control_title_container).setOnClickListener((View$OnClickListener)view$OnClickListener);
        this.mMediaMainControlLayout = this.findViewById(R$id.mr_media_main_control);
        this.mDividerView = this.findViewById(R$id.mr_control_divider);
        this.mPlaybackControlLayout = this.findViewById(R$id.mr_playback_control);
        this.mTitleView = this.findViewById(R$id.mr_control_title);
        this.mSubtitleView = this.findViewById(R$id.mr_control_subtitle);
        (this.mPlaybackControlButton = this.findViewById(R$id.mr_control_playback_ctrl)).setOnClickListener((View$OnClickListener)clickListener);
        (this.mVolumeControlLayout = this.findViewById(R$id.mr_volume_control)).setVisibility(8);
        (this.mVolumeSlider = this.findViewById(R$id.mr_volume_slider)).setTag((Object)this.mRoute);
        final VolumeChangeListener volumeChangeListener = new VolumeChangeListener();
        this.mVolumeChangeListener = volumeChangeListener;
        this.mVolumeSlider.setOnSeekBarChangeListener((SeekBar$OnSeekBarChangeListener)volumeChangeListener);
        this.mVolumeGroupList = this.findViewById(R$id.mr_volume_group_list);
        this.mGroupMemberRoutes = new ArrayList<MediaRouter.RouteInfo>();
        final VolumeGroupAdapter volumeGroupAdapter = new VolumeGroupAdapter(this.mVolumeGroupList.getContext(), this.mGroupMemberRoutes);
        this.mVolumeGroupAdapter = volumeGroupAdapter;
        this.mVolumeGroupList.setAdapter((ListAdapter)volumeGroupAdapter);
        this.mGroupMemberRoutesAnimatingWithBitmap = new HashSet<MediaRouter.RouteInfo>();
        MediaRouterThemeHelper.setMediaControlsBackgroundColor(this.mContext, (View)this.mMediaMainControlLayout, (View)this.mVolumeGroupList, this.mRoute.isGroup());
        MediaRouterThemeHelper.setVolumeSliderColor(this.mContext, (MediaRouteVolumeSlider)this.mVolumeSlider, (View)this.mMediaMainControlLayout);
        (this.mVolumeSliderMap = new HashMap<MediaRouter.RouteInfo, SeekBar>()).put(this.mRoute, this.mVolumeSlider);
        (this.mGroupExpandCollapseButton = this.findViewById(R$id.mr_group_expand_collapse)).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
                final boolean mIsGroupExpanded = this$0.mIsGroupExpanded ^ true;
                this$0.mIsGroupExpanded = mIsGroupExpanded;
                if (mIsGroupExpanded) {
                    this$0.mVolumeGroupList.setVisibility(0);
                }
                MediaRouteControllerDialog.this.loadInterpolator();
                MediaRouteControllerDialog.this.updateLayoutHeight(true);
            }
        });
        this.loadInterpolator();
        this.mGroupListAnimationDurationMs = this.mContext.getResources().getInteger(R$integer.mr_controller_volume_group_list_animation_duration_ms);
        this.mGroupListFadeInDurationMs = this.mContext.getResources().getInteger(R$integer.mr_controller_volume_group_list_fade_in_duration_ms);
        this.mGroupListFadeOutDurationMs = this.mContext.getResources().getInteger(R$integer.mr_controller_volume_group_list_fade_out_duration_ms);
        final View onCreateMediaControlView = this.onCreateMediaControlView(bundle);
        this.mCustomControlView = onCreateMediaControlView;
        if (onCreateMediaControlView != null) {
            this.mCustomControlLayout.addView(onCreateMediaControlView);
            this.mCustomControlLayout.setVisibility(0);
        }
        this.mCreated = true;
        this.updateLayout();
    }
    
    public View onCreateMediaControlView(final Bundle bundle) {
        return null;
    }
    
    public void onDetachedFromWindow() {
        this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
        this.setMediaSession(null);
        this.mAttachedToWindow = false;
        super.onDetachedFromWindow();
    }
    
    @Override
    public boolean onKeyDown(int n, final KeyEvent keyEvent) {
        if (n != 25 && n != 24) {
            return super.onKeyDown(n, keyEvent);
        }
        final MediaRouter.RouteInfo mRoute = this.mRoute;
        if (n == 25) {
            n = -1;
        }
        else {
            n = 1;
        }
        mRoute.requestUpdateVolume(n);
        return true;
    }
    
    @Override
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        return n == 25 || n == 24 || super.onKeyUp(n, keyEvent);
    }
    
    void startGroupListFadeInAnimation() {
        this.clearGroupListAnimation(true);
        this.mVolumeGroupList.requestLayout();
        this.mVolumeGroupList.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mVolumeGroupList.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                MediaRouteControllerDialog.this.startGroupListFadeInAnimationInternal();
            }
        });
    }
    
    void startGroupListFadeInAnimationInternal() {
        final Set<MediaRouter.RouteInfo> mGroupMemberRoutesAdded = this.mGroupMemberRoutesAdded;
        if (mGroupMemberRoutesAdded != null && mGroupMemberRoutesAdded.size()) {
            this.fadeInAddedRoutes();
        }
        else {
            this.finishAnimation(true);
        }
    }
    
    void update(final boolean b) {
        if (this.mRouteInVolumeSliderTouched != null) {
            this.mHasPendingUpdate = true;
            this.mPendingUpdateAnimationNeeded |= b;
            return;
        }
        int visibility = 0;
        this.mHasPendingUpdate = false;
        this.mPendingUpdateAnimationNeeded = false;
        if (!this.mRoute.isSelected() || this.mRoute.isDefaultOrBluetooth()) {
            this.dismiss();
            return;
        }
        if (!this.mCreated) {
            return;
        }
        this.mRouteNameTextView.setText((CharSequence)this.mRoute.getName());
        final Button mDisconnectButton = this.mDisconnectButton;
        if (!this.mRoute.canDisconnect()) {
            visibility = 8;
        }
        mDisconnectButton.setVisibility(visibility);
        if (this.mCustomControlView == null && this.mArtIconIsLoaded) {
            if (isBitmapRecycled(this.mArtIconLoadedBitmap)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Can't set artwork image with recycled bitmap: ");
                sb.append(this.mArtIconLoadedBitmap);
                Log.w("MediaRouteCtrlDialog", sb.toString());
            }
            else {
                this.mArtView.setImageBitmap(this.mArtIconLoadedBitmap);
                this.mArtView.setBackgroundColor(this.mArtIconBackgroundColor);
            }
            this.clearLoadedBitmap();
        }
        this.updateVolumeControlLayout();
        this.updatePlaybackControlLayout();
        this.updateLayoutHeight(b);
    }
    
    void updateArtIconIfNeeded() {
        if (this.mCustomControlView == null) {
            if (this.isIconChanged()) {
                final FetchArtTask mFetchArtTask = this.mFetchArtTask;
                if (mFetchArtTask != null) {
                    mFetchArtTask.cancel(true);
                }
                (this.mFetchArtTask = new FetchArtTask()).execute((Object[])new Void[0]);
            }
        }
    }
    
    void updateLayout() {
        final int dialogWidth = MediaRouteDialogHelper.getDialogWidth(this.mContext);
        this.getWindow().setLayout(dialogWidth, -2);
        final View decorView = this.getWindow().getDecorView();
        this.mDialogContentWidth = dialogWidth - decorView.getPaddingLeft() - decorView.getPaddingRight();
        final Resources resources = this.mContext.getResources();
        this.mVolumeGroupListItemIconSize = resources.getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_item_icon_size);
        this.mVolumeGroupListItemHeight = resources.getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_item_height);
        this.mVolumeGroupListMaxHeight = resources.getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_max_height);
        this.mArtIconBitmap = null;
        this.mArtIconUri = null;
        this.updateArtIconIfNeeded();
        this.update(false);
    }
    
    void updateLayoutHeight(final boolean b) {
        this.mDefaultControlLayout.requestLayout();
        this.mDefaultControlLayout.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mDefaultControlLayout.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
                if (this$0.mIsGroupListAnimating) {
                    this$0.mIsGroupListAnimationPending = true;
                }
                else {
                    this$0.updateLayoutHeightInternal(b);
                }
            }
        });
    }
    
    void updateLayoutHeightInternal(final boolean b) {
        final int layoutHeight = getLayoutHeight((View)this.mMediaMainControlLayout);
        setLayoutHeight((View)this.mMediaMainControlLayout, -1);
        this.updateMediaControlVisibility(this.canShowPlaybackControlLayout());
        final View decorView = this.getWindow().getDecorView();
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getWindow().getAttributes().width, 1073741824);
        final boolean b2 = false;
        decorView.measure(measureSpec, 0);
        setLayoutHeight((View)this.mMediaMainControlLayout, layoutHeight);
        int desiredArtHeight = 0;
        Label_0167: {
            if (this.mCustomControlView == null && this.mArtView.getDrawable() instanceof BitmapDrawable) {
                final Bitmap bitmap = ((BitmapDrawable)this.mArtView.getDrawable()).getBitmap();
                if (bitmap != null) {
                    desiredArtHeight = this.getDesiredArtHeight(bitmap.getWidth(), bitmap.getHeight());
                    final ImageView mArtView = this.mArtView;
                    ImageView$ScaleType scaleType;
                    if (bitmap.getWidth() >= bitmap.getHeight()) {
                        scaleType = ImageView$ScaleType.FIT_XY;
                    }
                    else {
                        scaleType = ImageView$ScaleType.FIT_CENTER;
                    }
                    mArtView.setScaleType(scaleType);
                    break Label_0167;
                }
            }
            desiredArtHeight = 0;
        }
        final int mainControllerHeight = this.getMainControllerHeight(this.canShowPlaybackControlLayout());
        final int size = this.mGroupMemberRoutes.size();
        int n;
        if (this.mRoute.isGroup()) {
            n = this.mVolumeGroupListItemHeight * this.mRoute.getMemberRoutes().size();
        }
        else {
            n = 0;
        }
        int a = n;
        if (size > 0) {
            a = n + this.mVolumeGroupListPaddingTop;
        }
        int min = Math.min(a, this.mVolumeGroupListMaxHeight);
        if (!this.mIsGroupExpanded) {
            min = 0;
        }
        int n2 = Math.max(desiredArtHeight, min) + mainControllerHeight;
        final Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        final int n3 = rect.height() - (this.mDialogAreaLayout.getMeasuredHeight() - this.mDefaultControlLayout.getMeasuredHeight());
        if (this.mCustomControlView == null && desiredArtHeight > 0 && n2 <= n3) {
            this.mArtView.setVisibility(0);
            setLayoutHeight((View)this.mArtView, desiredArtHeight);
        }
        else {
            if (getLayoutHeight((View)this.mVolumeGroupList) + this.mMediaMainControlLayout.getMeasuredHeight() >= this.mDefaultControlLayout.getMeasuredHeight()) {
                this.mArtView.setVisibility(8);
            }
            n2 = min + mainControllerHeight;
            desiredArtHeight = 0;
        }
        if (this.canShowPlaybackControlLayout() && n2 <= n3) {
            this.mPlaybackControlLayout.setVisibility(0);
        }
        else {
            this.mPlaybackControlLayout.setVisibility(8);
        }
        this.updateMediaControlVisibility(this.mPlaybackControlLayout.getVisibility() == 0);
        boolean b3 = b2;
        if (this.mPlaybackControlLayout.getVisibility() == 0) {
            b3 = true;
        }
        final int mainControllerHeight2 = this.getMainControllerHeight(b3);
        int n4 = Math.max(desiredArtHeight, min) + mainControllerHeight2;
        if (n4 > n3) {
            min -= n4 - n3;
            n4 = n3;
        }
        this.mMediaMainControlLayout.clearAnimation();
        this.mVolumeGroupList.clearAnimation();
        this.mDefaultControlLayout.clearAnimation();
        if (b) {
            this.animateLayoutHeight((View)this.mMediaMainControlLayout, mainControllerHeight2);
            this.animateLayoutHeight((View)this.mVolumeGroupList, min);
            this.animateLayoutHeight((View)this.mDefaultControlLayout, n4);
        }
        else {
            setLayoutHeight((View)this.mMediaMainControlLayout, mainControllerHeight2);
            setLayoutHeight((View)this.mVolumeGroupList, min);
            setLayoutHeight((View)this.mDefaultControlLayout, n4);
        }
        setLayoutHeight((View)this.mExpandableAreaLayout, rect.height());
        this.rebuildVolumeGroupList(b);
    }
    
    void updateVolumeGroupItemHeight(final View view) {
        setLayoutHeight(view.findViewById(R$id.volume_item_container), this.mVolumeGroupListItemHeight);
        final View viewById = view.findViewById(R$id.mr_volume_item_icon);
        final ViewGroup$LayoutParams layoutParams = viewById.getLayoutParams();
        final int mVolumeGroupListItemIconSize = this.mVolumeGroupListItemIconSize;
        layoutParams.width = mVolumeGroupListItemIconSize;
        layoutParams.height = mVolumeGroupListItemIconSize;
        viewById.setLayoutParams(layoutParams);
    }
    
    private final class ClickListener implements View$OnClickListener
    {
        ClickListener() {
        }
        
        public void onClick(final View view) {
            final int id = view.getId();
            int n = 1;
            boolean b = true;
            if (id != 16908313 && id != 16908314) {
                if (id == R$id.mr_control_playback_ctrl) {
                    final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
                    if (this$0.mMediaController != null) {
                        final PlaybackStateCompat mState = this$0.mState;
                        if (mState != null) {
                            final int state = mState.getState();
                            final int n2 = 0;
                            if (state != 3) {
                                b = false;
                            }
                            int n3;
                            if (b && MediaRouteControllerDialog.this.isPauseActionSupported()) {
                                MediaRouteControllerDialog.this.mMediaController.getTransportControls().pause();
                                n3 = R$string.mr_controller_pause;
                            }
                            else if (b && MediaRouteControllerDialog.this.isStopActionSupported()) {
                                MediaRouteControllerDialog.this.mMediaController.getTransportControls().stop();
                                n3 = R$string.mr_controller_stop;
                            }
                            else {
                                n3 = n2;
                                if (!b) {
                                    n3 = n2;
                                    if (MediaRouteControllerDialog.this.isPlayActionSupported()) {
                                        MediaRouteControllerDialog.this.mMediaController.getTransportControls().play();
                                        n3 = R$string.mr_controller_play;
                                    }
                                }
                            }
                            final AccessibilityManager mAccessibilityManager = MediaRouteControllerDialog.this.mAccessibilityManager;
                            if (mAccessibilityManager != null && mAccessibilityManager.isEnabled() && n3 != 0) {
                                final AccessibilityEvent obtain = AccessibilityEvent.obtain(16384);
                                obtain.setPackageName((CharSequence)MediaRouteControllerDialog.this.mContext.getPackageName());
                                obtain.setClassName((CharSequence)ClickListener.class.getName());
                                obtain.getText().add(MediaRouteControllerDialog.this.mContext.getString(n3));
                                MediaRouteControllerDialog.this.mAccessibilityManager.sendAccessibilityEvent(obtain);
                            }
                        }
                    }
                }
                else if (id == R$id.mr_close) {
                    MediaRouteControllerDialog.this.dismiss();
                }
            }
            else {
                if (MediaRouteControllerDialog.this.mRoute.isSelected()) {
                    final MediaRouter mRouter = MediaRouteControllerDialog.this.mRouter;
                    if (id == 16908313) {
                        n = 2;
                    }
                    mRouter.unselect(n);
                }
                MediaRouteControllerDialog.this.dismiss();
            }
        }
    }
    
    private class FetchArtTask extends AsyncTask<Void, Void, Bitmap>
    {
        private int mBackgroundColor;
        private final Bitmap mIconBitmap;
        private final Uri mIconUri;
        private long mStartTimeMillis;
        
        FetchArtTask() {
            final MediaDescriptionCompat mDescription = MediaRouteControllerDialog.this.mDescription;
            final Uri uri = null;
            Bitmap iconBitmap;
            if (mDescription == null) {
                iconBitmap = null;
            }
            else {
                iconBitmap = mDescription.getIconBitmap();
            }
            Bitmap mIconBitmap = iconBitmap;
            if (MediaRouteControllerDialog.isBitmapRecycled(iconBitmap)) {
                Log.w("MediaRouteCtrlDialog", "Can't fetch the given art bitmap because it's already recycled.");
                mIconBitmap = null;
            }
            this.mIconBitmap = mIconBitmap;
            final MediaDescriptionCompat mDescription2 = MediaRouteControllerDialog.this.mDescription;
            Uri iconUri;
            if (mDescription2 == null) {
                iconUri = uri;
            }
            else {
                iconUri = mDescription2.getIconUri();
            }
            this.mIconUri = iconUri;
        }
        
        private InputStream openInputStreamByScheme(final Uri uri) throws IOException {
            final String lowerCase = uri.getScheme().toLowerCase();
            InputStream in;
            if (!"android.resource".equals(lowerCase) && !"content".equals(lowerCase) && !"file".equals(lowerCase)) {
                final URLConnection openConnection = new URL(uri.toString()).openConnection();
                openConnection.setConnectTimeout(MediaRouteControllerDialog.CONNECTION_TIMEOUT_MILLIS);
                openConnection.setReadTimeout(MediaRouteControllerDialog.CONNECTION_TIMEOUT_MILLIS);
                in = openConnection.getInputStream();
            }
            else {
                in = MediaRouteControllerDialog.this.mContext.getContentResolver().openInputStream(uri);
            }
            InputStream inputStream;
            if (in == null) {
                inputStream = null;
            }
            else {
                inputStream = new BufferedInputStream(in);
            }
            return inputStream;
        }
        
        protected Bitmap doInBackground(final Void... p0) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.mIconBitmap:Landroid/graphics/Bitmap;
            //     4: astore_1       
            //     5: iconst_0       
            //     6: istore_2       
            //     7: aconst_null    
            //     8: astore_3       
            //     9: aload_1        
            //    10: ifnull          16
            //    13: goto            561
            //    16: aload_0        
            //    17: getfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //    20: astore_1       
            //    21: aload_1        
            //    22: ifnull          559
            //    25: aload_0        
            //    26: aload_1        
            //    27: invokespecial   androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.openInputStreamByScheme:(Landroid/net/Uri;)Ljava/io/InputStream;
            //    30: astore          4
            //    32: aload           4
            //    34: ifnonnull       118
            //    37: aload           4
            //    39: astore_3       
            //    40: aload           4
            //    42: astore_1       
            //    43: new             Ljava/lang/StringBuilder;
            //    46: astore          5
            //    48: aload           4
            //    50: astore_3       
            //    51: aload           4
            //    53: astore_1       
            //    54: aload           5
            //    56: invokespecial   java/lang/StringBuilder.<init>:()V
            //    59: aload           4
            //    61: astore_3       
            //    62: aload           4
            //    64: astore_1       
            //    65: aload           5
            //    67: ldc             "Unable to open: "
            //    69: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    72: pop            
            //    73: aload           4
            //    75: astore_3       
            //    76: aload           4
            //    78: astore_1       
            //    79: aload           5
            //    81: aload_0        
            //    82: getfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //    85: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //    88: pop            
            //    89: aload           4
            //    91: astore_3       
            //    92: aload           4
            //    94: astore_1       
            //    95: ldc             "MediaRouteCtrlDialog"
            //    97: aload           5
            //    99: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   102: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //   105: pop            
            //   106: aload           4
            //   108: ifnull          116
            //   111: aload           4
            //   113: invokevirtual   java/io/InputStream.close:()V
            //   116: aconst_null    
            //   117: areturn        
            //   118: aload           4
            //   120: astore_3       
            //   121: aload           4
            //   123: astore_1       
            //   124: new             Landroid/graphics/BitmapFactory$Options;
            //   127: astore          6
            //   129: aload           4
            //   131: astore_3       
            //   132: aload           4
            //   134: astore_1       
            //   135: aload           6
            //   137: invokespecial   android/graphics/BitmapFactory$Options.<init>:()V
            //   140: aload           4
            //   142: astore_3       
            //   143: aload           4
            //   145: astore_1       
            //   146: aload           6
            //   148: iconst_1       
            //   149: putfield        android/graphics/BitmapFactory$Options.inJustDecodeBounds:Z
            //   152: aload           4
            //   154: astore_3       
            //   155: aload           4
            //   157: astore_1       
            //   158: aload           4
            //   160: aconst_null    
            //   161: aload           6
            //   163: invokestatic    android/graphics/BitmapFactory.decodeStream:(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
            //   166: pop            
            //   167: aload           4
            //   169: astore_3       
            //   170: aload           4
            //   172: astore_1       
            //   173: aload           6
            //   175: getfield        android/graphics/BitmapFactory$Options.outWidth:I
            //   178: ifeq            455
            //   181: aload           4
            //   183: astore_3       
            //   184: aload           4
            //   186: astore_1       
            //   187: aload           6
            //   189: getfield        android/graphics/BitmapFactory$Options.outHeight:I
            //   192: istore          7
            //   194: iload           7
            //   196: ifne            202
            //   199: goto            455
            //   202: aload           4
            //   204: astore_1       
            //   205: aload           4
            //   207: invokevirtual   java/io/InputStream.reset:()V
            //   210: goto            331
            //   213: astore_1       
            //   214: aload           4
            //   216: astore_3       
            //   217: aload           4
            //   219: astore_1       
            //   220: aload           4
            //   222: invokevirtual   java/io/InputStream.close:()V
            //   225: aload           4
            //   227: astore_3       
            //   228: aload           4
            //   230: astore_1       
            //   231: aload_0        
            //   232: aload_0        
            //   233: getfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //   236: invokespecial   androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.openInputStreamByScheme:(Landroid/net/Uri;)Ljava/io/InputStream;
            //   239: astore          5
            //   241: aload           5
            //   243: astore          4
            //   245: aload           5
            //   247: ifnonnull       331
            //   250: aload           5
            //   252: astore_3       
            //   253: aload           5
            //   255: astore_1       
            //   256: new             Ljava/lang/StringBuilder;
            //   259: astore          4
            //   261: aload           5
            //   263: astore_3       
            //   264: aload           5
            //   266: astore_1       
            //   267: aload           4
            //   269: invokespecial   java/lang/StringBuilder.<init>:()V
            //   272: aload           5
            //   274: astore_3       
            //   275: aload           5
            //   277: astore_1       
            //   278: aload           4
            //   280: ldc             "Unable to open: "
            //   282: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   285: pop            
            //   286: aload           5
            //   288: astore_3       
            //   289: aload           5
            //   291: astore_1       
            //   292: aload           4
            //   294: aload_0        
            //   295: getfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //   298: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   301: pop            
            //   302: aload           5
            //   304: astore_3       
            //   305: aload           5
            //   307: astore_1       
            //   308: ldc             "MediaRouteCtrlDialog"
            //   310: aload           4
            //   312: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   315: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //   318: pop            
            //   319: aload           5
            //   321: ifnull          329
            //   324: aload           5
            //   326: invokevirtual   java/io/InputStream.close:()V
            //   329: aconst_null    
            //   330: areturn        
            //   331: aload           4
            //   333: astore_3       
            //   334: aload           4
            //   336: astore_1       
            //   337: aload           6
            //   339: iconst_0       
            //   340: putfield        android/graphics/BitmapFactory$Options.inJustDecodeBounds:Z
            //   343: aload           4
            //   345: astore_3       
            //   346: aload           4
            //   348: astore_1       
            //   349: aload_0        
            //   350: getfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.this$0:Landroidx/mediarouter/app/MediaRouteControllerDialog;
            //   353: aload           6
            //   355: getfield        android/graphics/BitmapFactory$Options.outWidth:I
            //   358: aload           6
            //   360: getfield        android/graphics/BitmapFactory$Options.outHeight:I
            //   363: invokevirtual   androidx/mediarouter/app/MediaRouteControllerDialog.getDesiredArtHeight:(II)I
            //   366: istore          7
            //   368: aload           4
            //   370: astore_3       
            //   371: aload           4
            //   373: astore_1       
            //   374: aload           6
            //   376: iconst_1       
            //   377: aload           6
            //   379: getfield        android/graphics/BitmapFactory$Options.outHeight:I
            //   382: iload           7
            //   384: idiv           
            //   385: invokestatic    java/lang/Integer.highestOneBit:(I)I
            //   388: invokestatic    java/lang/Math.max:(II)I
            //   391: putfield        android/graphics/BitmapFactory$Options.inSampleSize:I
            //   394: aload           4
            //   396: astore_3       
            //   397: aload           4
            //   399: astore_1       
            //   400: aload_0        
            //   401: invokevirtual   android/os/AsyncTask.isCancelled:()Z
            //   404: istore          8
            //   406: iload           8
            //   408: ifeq            423
            //   411: aload           4
            //   413: ifnull          421
            //   416: aload           4
            //   418: invokevirtual   java/io/InputStream.close:()V
            //   421: aconst_null    
            //   422: areturn        
            //   423: aload           4
            //   425: astore_3       
            //   426: aload           4
            //   428: astore_1       
            //   429: aload           4
            //   431: aconst_null    
            //   432: aload           6
            //   434: invokestatic    android/graphics/BitmapFactory.decodeStream:(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
            //   437: astore          5
            //   439: aload           4
            //   441: ifnull          449
            //   444: aload           4
            //   446: invokevirtual   java/io/InputStream.close:()V
            //   449: aload           5
            //   451: astore_1       
            //   452: goto            561
            //   455: aload           4
            //   457: ifnull          465
            //   460: aload           4
            //   462: invokevirtual   java/io/InputStream.close:()V
            //   465: aconst_null    
            //   466: areturn        
            //   467: astore          4
            //   469: goto            480
            //   472: astore_1       
            //   473: goto            549
            //   476: astore          4
            //   478: aconst_null    
            //   479: astore_3       
            //   480: aload_3        
            //   481: astore_1       
            //   482: new             Ljava/lang/StringBuilder;
            //   485: astore          5
            //   487: aload_3        
            //   488: astore_1       
            //   489: aload           5
            //   491: invokespecial   java/lang/StringBuilder.<init>:()V
            //   494: aload_3        
            //   495: astore_1       
            //   496: aload           5
            //   498: ldc             "Unable to open: "
            //   500: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   503: pop            
            //   504: aload_3        
            //   505: astore_1       
            //   506: aload           5
            //   508: aload_0        
            //   509: getfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //   512: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   515: pop            
            //   516: aload_3        
            //   517: astore_1       
            //   518: ldc             "MediaRouteCtrlDialog"
            //   520: aload           5
            //   522: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   525: aload           4
            //   527: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   530: pop            
            //   531: aload_3        
            //   532: ifnull          559
            //   535: aload_3        
            //   536: invokevirtual   java/io/InputStream.close:()V
            //   539: goto            559
            //   542: astore          4
            //   544: aload_1        
            //   545: astore_3       
            //   546: aload           4
            //   548: astore_1       
            //   549: aload_3        
            //   550: ifnull          557
            //   553: aload_3        
            //   554: invokevirtual   java/io/InputStream.close:()V
            //   557: aload_1        
            //   558: athrow         
            //   559: aconst_null    
            //   560: astore_1       
            //   561: aload_1        
            //   562: invokestatic    androidx/mediarouter/app/MediaRouteControllerDialog.isBitmapRecycled:(Landroid/graphics/Bitmap;)Z
            //   565: ifeq            601
            //   568: new             Ljava/lang/StringBuilder;
            //   571: dup            
            //   572: invokespecial   java/lang/StringBuilder.<init>:()V
            //   575: astore_3       
            //   576: aload_3        
            //   577: ldc             "Can't use recycled bitmap: "
            //   579: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   582: pop            
            //   583: aload_3        
            //   584: aload_1        
            //   585: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   588: pop            
            //   589: ldc             "MediaRouteCtrlDialog"
            //   591: aload_3        
            //   592: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   595: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //   598: pop            
            //   599: aconst_null    
            //   600: areturn        
            //   601: aload_1        
            //   602: ifnull          673
            //   605: aload_1        
            //   606: invokevirtual   android/graphics/Bitmap.getWidth:()I
            //   609: aload_1        
            //   610: invokevirtual   android/graphics/Bitmap.getHeight:()I
            //   613: if_icmpge       673
            //   616: new             Landroidx/palette/graphics/Palette$Builder;
            //   619: dup            
            //   620: aload_1        
            //   621: invokespecial   androidx/palette/graphics/Palette$Builder.<init>:(Landroid/graphics/Bitmap;)V
            //   624: astore_3       
            //   625: aload_3        
            //   626: iconst_1       
            //   627: invokevirtual   androidx/palette/graphics/Palette$Builder.maximumColorCount:(I)Landroidx/palette/graphics/Palette$Builder;
            //   630: pop            
            //   631: aload_3        
            //   632: invokevirtual   androidx/palette/graphics/Palette$Builder.generate:()Landroidx/palette/graphics/Palette;
            //   635: astore_3       
            //   636: aload_3        
            //   637: invokevirtual   androidx/palette/graphics/Palette.getSwatches:()Ljava/util/List;
            //   640: invokeinterface java/util/List.isEmpty:()Z
            //   645: ifeq            651
            //   648: goto            668
            //   651: aload_3        
            //   652: invokevirtual   androidx/palette/graphics/Palette.getSwatches:()Ljava/util/List;
            //   655: iconst_0       
            //   656: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
            //   661: checkcast       Landroidx/palette/graphics/Palette$Swatch;
            //   664: invokevirtual   androidx/palette/graphics/Palette$Swatch.getRgb:()I
            //   667: istore_2       
            //   668: aload_0        
            //   669: iload_2        
            //   670: putfield        androidx/mediarouter/app/MediaRouteControllerDialog$FetchArtTask.mBackgroundColor:I
            //   673: aload_1        
            //   674: areturn        
            //   675: astore_1       
            //   676: goto            116
            //   679: astore_1       
            //   680: goto            329
            //   683: astore_1       
            //   684: goto            421
            //   687: astore_1       
            //   688: goto            449
            //   691: astore_1       
            //   692: goto            465
            //   695: astore_1       
            //   696: goto            559
            //   699: astore_3       
            //   700: goto            557
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  25     32     476    480    Ljava/io/IOException;
            //  25     32     472    476    Any
            //  43     48     467    472    Ljava/io/IOException;
            //  43     48     542    549    Any
            //  54     59     467    472    Ljava/io/IOException;
            //  54     59     542    549    Any
            //  65     73     467    472    Ljava/io/IOException;
            //  65     73     542    549    Any
            //  79     89     467    472    Ljava/io/IOException;
            //  79     89     542    549    Any
            //  95     106    467    472    Ljava/io/IOException;
            //  95     106    542    549    Any
            //  111    116    675    679    Ljava/io/IOException;
            //  124    129    467    472    Ljava/io/IOException;
            //  124    129    542    549    Any
            //  135    140    467    472    Ljava/io/IOException;
            //  135    140    542    549    Any
            //  146    152    467    472    Ljava/io/IOException;
            //  146    152    542    549    Any
            //  158    167    467    472    Ljava/io/IOException;
            //  158    167    542    549    Any
            //  173    181    467    472    Ljava/io/IOException;
            //  173    181    542    549    Any
            //  187    194    467    472    Ljava/io/IOException;
            //  187    194    542    549    Any
            //  205    210    213    331    Ljava/io/IOException;
            //  205    210    542    549    Any
            //  220    225    467    472    Ljava/io/IOException;
            //  220    225    542    549    Any
            //  231    241    467    472    Ljava/io/IOException;
            //  231    241    542    549    Any
            //  256    261    467    472    Ljava/io/IOException;
            //  256    261    542    549    Any
            //  267    272    467    472    Ljava/io/IOException;
            //  267    272    542    549    Any
            //  278    286    467    472    Ljava/io/IOException;
            //  278    286    542    549    Any
            //  292    302    467    472    Ljava/io/IOException;
            //  292    302    542    549    Any
            //  308    319    467    472    Ljava/io/IOException;
            //  308    319    542    549    Any
            //  324    329    679    683    Ljava/io/IOException;
            //  337    343    467    472    Ljava/io/IOException;
            //  337    343    542    549    Any
            //  349    368    467    472    Ljava/io/IOException;
            //  349    368    542    549    Any
            //  374    394    467    472    Ljava/io/IOException;
            //  374    394    542    549    Any
            //  400    406    467    472    Ljava/io/IOException;
            //  400    406    542    549    Any
            //  416    421    683    687    Ljava/io/IOException;
            //  429    439    467    472    Ljava/io/IOException;
            //  429    439    542    549    Any
            //  444    449    687    691    Ljava/io/IOException;
            //  460    465    691    695    Ljava/io/IOException;
            //  482    487    542    549    Any
            //  489    494    542    549    Any
            //  496    504    542    549    Any
            //  506    516    542    549    Any
            //  518    531    542    549    Any
            //  535    539    695    699    Ljava/io/IOException;
            //  553    557    699    703    Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IndexOutOfBoundsException: Index 374 out of bounds for length 374
            //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
            //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
            //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
            //     at java.base/java.util.Objects.checkIndex(Objects.java:372)
            //     at java.base/java.util.ArrayList.get(ArrayList.java:458)
            //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3321)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:113)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        public Bitmap getIconBitmap() {
            return this.mIconBitmap;
        }
        
        public Uri getIconUri() {
            return this.mIconUri;
        }
        
        protected void onPostExecute(final Bitmap mArtIconLoadedBitmap) {
            final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
            this$0.mFetchArtTask = null;
            if (!ObjectsCompat.equals(this$0.mArtIconBitmap, this.mIconBitmap) || !ObjectsCompat.equals(MediaRouteControllerDialog.this.mArtIconUri, this.mIconUri)) {
                final MediaRouteControllerDialog this$2 = MediaRouteControllerDialog.this;
                this$2.mArtIconBitmap = this.mIconBitmap;
                this$2.mArtIconLoadedBitmap = mArtIconLoadedBitmap;
                this$2.mArtIconUri = this.mIconUri;
                this$2.mArtIconBackgroundColor = this.mBackgroundColor;
                boolean b = true;
                this$2.mArtIconIsLoaded = true;
                final long uptimeMillis = SystemClock.uptimeMillis();
                final long mStartTimeMillis = this.mStartTimeMillis;
                final MediaRouteControllerDialog this$3 = MediaRouteControllerDialog.this;
                if (uptimeMillis - mStartTimeMillis <= 120L) {
                    b = false;
                }
                this$3.update(b);
            }
        }
        
        protected void onPreExecute() {
            this.mStartTimeMillis = SystemClock.uptimeMillis();
            MediaRouteControllerDialog.this.clearLoadedBitmap();
        }
    }
    
    private final class MediaControllerCallback extends Callback
    {
        MediaControllerCallback() {
        }
        
        @Override
        public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) {
            final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
            MediaDescriptionCompat description;
            if (mediaMetadataCompat == null) {
                description = null;
            }
            else {
                description = mediaMetadataCompat.getDescription();
            }
            this$0.mDescription = description;
            MediaRouteControllerDialog.this.updateArtIconIfNeeded();
            MediaRouteControllerDialog.this.update(false);
        }
        
        @Override
        public void onPlaybackStateChanged(final PlaybackStateCompat mState) {
            final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
            this$0.mState = mState;
            this$0.update(false);
        }
        
        @Override
        public void onSessionDestroyed() {
            final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
            final MediaControllerCompat mMediaController = this$0.mMediaController;
            if (mMediaController != null) {
                mMediaController.unregisterCallback((MediaControllerCompat.Callback)this$0.mControllerCallback);
                MediaRouteControllerDialog.this.mMediaController = null;
            }
        }
    }
    
    private final class MediaRouterCallback extends Callback
    {
        MediaRouterCallback() {
        }
        
        @Override
        public void onRouteChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteControllerDialog.this.update(true);
        }
        
        @Override
        public void onRouteUnselected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteControllerDialog.this.update(false);
        }
        
        @Override
        public void onRouteVolumeChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            final SeekBar seekBar = MediaRouteControllerDialog.this.mVolumeSliderMap.get(routeInfo);
            final int volume = routeInfo.getVolume();
            if (MediaRouteControllerDialog.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onRouteVolumeChanged(), route.getVolume:");
                sb.append(volume);
                Log.d("MediaRouteCtrlDialog", sb.toString());
            }
            if (seekBar != null && MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched != routeInfo) {
                seekBar.setProgress(volume);
            }
        }
    }
    
    private class VolumeChangeListener implements SeekBar$OnSeekBarChangeListener
    {
        private final Runnable mStopTrackingTouch;
        
        VolumeChangeListener() {
            this.mStopTrackingTouch = new Runnable() {
                @Override
                public void run() {
                    final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
                    if (this$0.mRouteInVolumeSliderTouched != null) {
                        this$0.mRouteInVolumeSliderTouched = null;
                        if (this$0.mHasPendingUpdate) {
                            this$0.update(this$0.mPendingUpdateAnimationNeeded);
                        }
                    }
                }
            };
        }
        
        public void onProgressChanged(final SeekBar seekBar, final int i, final boolean b) {
            if (b) {
                final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)seekBar.getTag();
                if (MediaRouteControllerDialog.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onProgressChanged(): calling MediaRouter.RouteInfo.requestSetVolume(");
                    sb.append(i);
                    sb.append(")");
                    Log.d("MediaRouteCtrlDialog", sb.toString());
                }
                routeInfo.requestSetVolume(i);
            }
        }
        
        public void onStartTrackingTouch(final SeekBar seekBar) {
            final MediaRouteControllerDialog this$0 = MediaRouteControllerDialog.this;
            if (this$0.mRouteInVolumeSliderTouched != null) {
                this$0.mVolumeSlider.removeCallbacks(this.mStopTrackingTouch);
            }
            MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched = (MediaRouter.RouteInfo)seekBar.getTag();
        }
        
        public void onStopTrackingTouch(final SeekBar seekBar) {
            MediaRouteControllerDialog.this.mVolumeSlider.postDelayed(this.mStopTrackingTouch, 500L);
        }
    }
    
    private class VolumeGroupAdapter extends ArrayAdapter<MediaRouter.RouteInfo>
    {
        final float mDisabledAlpha;
        
        public VolumeGroupAdapter(final Context context, final List<MediaRouter.RouteInfo> list) {
            super(context, 0, (List)list);
            this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(context);
        }
        
        public View getView(int n, View inflate, final ViewGroup viewGroup) {
            final int n2 = 0;
            if (inflate == null) {
                inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.mr_controller_volume_item, viewGroup, false);
            }
            else {
                MediaRouteControllerDialog.this.updateVolumeGroupItemHeight(inflate);
            }
            final MediaRouter.RouteInfo tag = (MediaRouter.RouteInfo)this.getItem(n);
            if (tag != null) {
                final boolean enabled = tag.isEnabled();
                final TextView textView = (TextView)inflate.findViewById(R$id.mr_name);
                textView.setEnabled(enabled);
                textView.setText((CharSequence)tag.getName());
                final MediaRouteVolumeSlider mediaRouteVolumeSlider = (MediaRouteVolumeSlider)inflate.findViewById(R$id.mr_volume_slider);
                MediaRouterThemeHelper.setVolumeSliderColor(viewGroup.getContext(), mediaRouteVolumeSlider, (View)MediaRouteControllerDialog.this.mVolumeGroupList);
                mediaRouteVolumeSlider.setTag((Object)tag);
                MediaRouteControllerDialog.this.mVolumeSliderMap.put(tag, mediaRouteVolumeSlider);
                mediaRouteVolumeSlider.setHideThumb(enabled ^ true);
                mediaRouteVolumeSlider.setEnabled(enabled);
                if (enabled) {
                    if (MediaRouteControllerDialog.this.isVolumeControlAvailable(tag)) {
                        mediaRouteVolumeSlider.setMax(tag.getVolumeMax());
                        mediaRouteVolumeSlider.setProgress(tag.getVolume());
                        mediaRouteVolumeSlider.setOnSeekBarChangeListener((SeekBar$OnSeekBarChangeListener)MediaRouteControllerDialog.this.mVolumeChangeListener);
                    }
                    else {
                        mediaRouteVolumeSlider.setMax(100);
                        mediaRouteVolumeSlider.setProgress(100);
                        mediaRouteVolumeSlider.setEnabled(false);
                    }
                }
                final ImageView imageView = (ImageView)inflate.findViewById(R$id.mr_volume_item_icon);
                if (enabled) {
                    n = 255;
                }
                else {
                    n = (int)(this.mDisabledAlpha * 255.0f);
                }
                imageView.setAlpha(n);
                final LinearLayout linearLayout = (LinearLayout)inflate.findViewById(R$id.volume_item_container);
                n = n2;
                if (MediaRouteControllerDialog.this.mGroupMemberRoutesAnimatingWithBitmap.contains(tag)) {
                    n = 4;
                }
                linearLayout.setVisibility(n);
                final Set<MediaRouter.RouteInfo> mGroupMemberRoutesAdded = MediaRouteControllerDialog.this.mGroupMemberRoutesAdded;
                if (mGroupMemberRoutesAdded != null && mGroupMemberRoutesAdded.contains(tag)) {
                    final AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 0.0f);
                    ((Animation)alphaAnimation).setDuration(0L);
                    ((Animation)alphaAnimation).setFillEnabled(true);
                    ((Animation)alphaAnimation).setFillAfter(true);
                    inflate.clearAnimation();
                    inflate.startAnimation((Animation)alphaAnimation);
                }
            }
            return inflate;
        }
        
        public boolean isEnabled(final int n) {
            return false;
        }
    }
}
