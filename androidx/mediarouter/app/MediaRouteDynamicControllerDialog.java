// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.widget.SeekBar;
import android.widget.RelativeLayout;
import android.widget.CheckBox;
import android.util.DisplayMetrics;
import android.content.res.Resources;
import androidx.mediarouter.R$dimen;
import android.util.TypedValue;
import android.widget.ProgressBar;
import androidx.mediarouter.media.MediaRouteProvider;
import android.view.ViewGroup;
import android.view.animation.Animation$AnimationListener;
import android.view.animation.Transformation;
import android.view.animation.Animation;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.mediarouter.R$integer;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;
import android.view.animation.Interpolator;
import android.widget.SeekBar$OnSeekBarChangeListener;
import java.io.IOException;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.InputStream;
import android.os.AsyncTask;
import android.os.SystemClock;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import android.text.TextUtils;
import android.os.Build$VERSION;
import androidx.core.util.ObjectsCompat;
import androidx.mediarouter.R$string;
import java.util.HashMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View$OnClickListener;
import androidx.mediarouter.R$id;
import android.app.Dialog;
import androidx.mediarouter.R$layout;
import android.os.Bundle;
import java.util.Iterator;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.ViewGroup$LayoutParams;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Element;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.os.Message;
import java.util.ArrayList;
import android.util.Log;
import java.util.Map;
import android.widget.TextView;
import android.widget.Button;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.support.v4.media.session.MediaControllerCompat;
import android.os.Handler;
import androidx.mediarouter.media.MediaRouter;
import java.util.List;
import android.support.v4.media.MediaDescriptionCompat;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.net.Uri;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatDialog;

public class MediaRouteDynamicControllerDialog extends AppCompatDialog
{
    static final boolean DEBUG;
    RecyclerAdapter mAdapter;
    int mArtIconBackgroundColor;
    Bitmap mArtIconBitmap;
    boolean mArtIconIsLoaded;
    Bitmap mArtIconLoadedBitmap;
    Uri mArtIconUri;
    ImageView mArtView;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private ImageButton mCloseButton;
    Context mContext;
    MediaControllerCallback mControllerCallback;
    private boolean mCreated;
    MediaDescriptionCompat mDescription;
    FetchArtTask mFetchArtTask;
    final List<MediaRouter.RouteInfo> mGroupableRoutes;
    final Handler mHandler;
    boolean mIsAnimatingVolumeSliderLayout;
    boolean mIsSelectingRoute;
    private long mLastUpdateTime;
    MediaControllerCompat mMediaController;
    final List<MediaRouter.RouteInfo> mMemberRoutes;
    private ImageView mMetadataBackground;
    private View mMetadataBlackScrim;
    RecyclerView mRecyclerView;
    MediaRouter.RouteInfo mRouteForVolumeUpdatingByUser;
    final MediaRouter mRouter;
    MediaRouter.RouteInfo mSelectedRoute;
    private MediaRouteSelector mSelector;
    private Button mStopCastingButton;
    private TextView mSubtitleView;
    private String mTitlePlaceholder;
    private TextView mTitleView;
    final List<MediaRouter.RouteInfo> mTransferableRoutes;
    final List<MediaRouter.RouteInfo> mUngroupableRoutes;
    Map<String, Integer> mUnmutedVolumeMap;
    private boolean mUpdateMetadataViewsDeferred;
    private boolean mUpdateRoutesViewDeferred;
    VolumeChangeListener mVolumeChangeListener;
    Map<String, MediaRouteVolumeSliderHolder> mVolumeSliderHolderMap;
    
    static {
        DEBUG = Log.isLoggable("MediaRouteCtrlDialog", 3);
    }
    
    public MediaRouteDynamicControllerDialog(final Context context) {
        this(context, 0);
    }
    
    public MediaRouteDynamicControllerDialog(Context mContext, final int n) {
        mContext = MediaRouterThemeHelper.createThemedDialogContext(mContext, n, false);
        super(mContext, MediaRouterThemeHelper.createThemedDialogStyle(mContext));
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mMemberRoutes = new ArrayList<MediaRouter.RouteInfo>();
        this.mGroupableRoutes = new ArrayList<MediaRouter.RouteInfo>();
        this.mTransferableRoutes = new ArrayList<MediaRouter.RouteInfo>();
        this.mUngroupableRoutes = new ArrayList<MediaRouter.RouteInfo>();
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                final int what = message.what;
                if (what != 1) {
                    if (what == 2) {
                        final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
                        if (this$0.mRouteForVolumeUpdatingByUser != null) {
                            this$0.mRouteForVolumeUpdatingByUser = null;
                            this$0.updateViewsIfNeeded();
                        }
                    }
                }
                else {
                    MediaRouteDynamicControllerDialog.this.updateRoutesView();
                }
            }
        };
        mContext = this.getContext();
        this.mContext = mContext;
        this.mRouter = MediaRouter.getInstance(mContext);
        this.mCallback = new MediaRouterCallback();
        this.mSelectedRoute = this.mRouter.getSelectedRoute();
        this.mControllerCallback = new MediaControllerCallback();
        this.setMediaSession(this.mRouter.getMediaSessionToken());
    }
    
    private static Bitmap blurBitmap(final Bitmap bitmap, final float radius, final Context context) {
        final RenderScript create = RenderScript.create(context);
        final Allocation fromBitmap = Allocation.createFromBitmap(create, bitmap);
        final Allocation typed = Allocation.createTyped(create, fromBitmap.getType());
        final ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        create2.setRadius(radius);
        create2.setInput(fromBitmap);
        create2.forEach(typed);
        typed.copyTo(bitmap);
        fromBitmap.destroy();
        typed.destroy();
        create2.destroy();
        create.destroy();
        return bitmap;
    }
    
    static boolean isBitmapRecycled(final Bitmap bitmap) {
        return bitmap != null && bitmap.isRecycled();
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
        this.reloadIconIfNeeded();
        this.updateMetadataViews();
    }
    
    private boolean shouldDeferUpdateViews() {
        return this.mRouteForVolumeUpdatingByUser != null || this.mIsSelectingRoute || this.mIsAnimatingVolumeSliderLayout || (this.mCreated ^ true);
    }
    
    void clearLoadedBitmap() {
        this.mArtIconIsLoaded = false;
        this.mArtIconLoadedBitmap = null;
        this.mArtIconBackgroundColor = 0;
    }
    
    List<MediaRouter.RouteInfo> getCurrentGroupableRoutes() {
        final ArrayList<MediaRouter.RouteInfo> list = new ArrayList<MediaRouter.RouteInfo>();
        if (this.mSelectedRoute.getDynamicGroupState() != null) {
            for (final MediaRouter.RouteInfo routeInfo : this.mSelectedRoute.getProvider().getRoutes()) {
                final MediaRouter.RouteInfo.DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                if (dynamicGroupState != null && dynamicGroupState.isGroupable()) {
                    list.add(routeInfo);
                }
            }
        }
        return list;
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(this.mSelector, (MediaRouter.Callback)this.mCallback, 1);
        this.updateRoutes();
        this.setMediaSession(this.mRouter.getMediaSessionToken());
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R$layout.mr_cast_dialog);
        MediaRouterThemeHelper.setDialogBackgroundColor(this.mContext, this);
        (this.mCloseButton = this.findViewById(R$id.mr_cast_close_button)).setColorFilter(-1);
        this.mCloseButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                MediaRouteDynamicControllerDialog.this.dismiss();
            }
        });
        (this.mStopCastingButton = this.findViewById(R$id.mr_cast_stop_button)).setTextColor(-1);
        this.mStopCastingButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                if (MediaRouteDynamicControllerDialog.this.mSelectedRoute.isSelected()) {
                    MediaRouteDynamicControllerDialog.this.mRouter.unselect(2);
                }
                MediaRouteDynamicControllerDialog.this.dismiss();
            }
        });
        this.mAdapter = new RecyclerAdapter();
        (this.mRecyclerView = this.findViewById(R$id.mr_cast_list)).setAdapter((RecyclerView.Adapter)this.mAdapter);
        this.mRecyclerView.setLayoutManager((RecyclerView.LayoutManager)new LinearLayoutManager(this.mContext));
        this.mVolumeChangeListener = new VolumeChangeListener();
        this.mVolumeSliderHolderMap = new HashMap<String, MediaRouteVolumeSliderHolder>();
        this.mUnmutedVolumeMap = new HashMap<String, Integer>();
        this.mMetadataBackground = this.findViewById(R$id.mr_cast_meta_background);
        this.mMetadataBlackScrim = this.findViewById(R$id.mr_cast_meta_black_scrim);
        this.mArtView = this.findViewById(R$id.mr_cast_meta_art);
        (this.mTitleView = this.findViewById(R$id.mr_cast_meta_title)).setTextColor(-1);
        (this.mSubtitleView = this.findViewById(R$id.mr_cast_meta_subtitle)).setTextColor(-1);
        this.mTitlePlaceholder = this.mContext.getResources().getString(R$string.mr_cast_dialog_title_view_placeholder);
        this.mCreated = true;
        this.updateLayout();
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttachedToWindow = false;
        this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
        this.mHandler.removeCallbacksAndMessages((Object)null);
        this.setMediaSession(null);
    }
    
    public boolean onFilterRoute(final MediaRouter.RouteInfo routeInfo) {
        return !routeInfo.isDefaultOrBluetooth() && routeInfo.isEnabled() && routeInfo.matchesSelector(this.mSelector) && this.mSelectedRoute != routeInfo;
    }
    
    public void onFilterRoutes(final List<MediaRouter.RouteInfo> list) {
        for (int i = list.size() - 1; i >= 0; --i) {
            if (!this.onFilterRoute(list.get(i))) {
                list.remove(i);
            }
        }
    }
    
    void reloadIconIfNeeded() {
        final MediaDescriptionCompat mDescription = this.mDescription;
        Object iconUri = null;
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
        if (bitmap == iconBitmap && (bitmap != null || ObjectsCompat.equals(uri, iconUri))) {
            return;
        }
        final FetchArtTask mFetchArtTask3 = this.mFetchArtTask;
        if (mFetchArtTask3 != null) {
            mFetchArtTask3.cancel(true);
        }
        (this.mFetchArtTask = new FetchArtTask()).execute((Object[])new Void[0]);
    }
    
    public void setRouteSelector(final MediaRouteSelector mSelector) {
        if (mSelector != null) {
            if (!this.mSelector.equals(mSelector)) {
                this.mSelector = mSelector;
                if (this.mAttachedToWindow) {
                    this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
                    this.mRouter.addCallback(mSelector, (MediaRouter.Callback)this.mCallback, 1);
                    this.updateRoutes();
                }
            }
            return;
        }
        throw new IllegalArgumentException("selector must not be null");
    }
    
    void updateLayout() {
        this.getWindow().setLayout(MediaRouteDialogHelper.getDialogWidthForDynamicGroup(this.mContext), MediaRouteDialogHelper.getDialogHeight(this.mContext));
        this.mArtIconBitmap = null;
        this.mArtIconUri = null;
        this.reloadIconIfNeeded();
        this.updateMetadataViews();
        this.updateRoutesView();
    }
    
    void updateMetadataViews() {
        if (this.shouldDeferUpdateViews()) {
            this.mUpdateMetadataViewsDeferred = true;
            return;
        }
        this.mUpdateMetadataViewsDeferred = false;
        if (!this.mSelectedRoute.isSelected() || this.mSelectedRoute.isDefaultOrBluetooth()) {
            this.dismiss();
        }
        final boolean mArtIconIsLoaded = this.mArtIconIsLoaded;
        CharSequence subtitle = null;
        if (mArtIconIsLoaded && !isBitmapRecycled(this.mArtIconLoadedBitmap) && this.mArtIconLoadedBitmap != null) {
            this.mArtView.setVisibility(0);
            this.mArtView.setImageBitmap(this.mArtIconLoadedBitmap);
            this.mArtView.setBackgroundColor(this.mArtIconBackgroundColor);
            this.mMetadataBlackScrim.setVisibility(0);
            if (Build$VERSION.SDK_INT >= 17) {
                final Bitmap mArtIconLoadedBitmap = this.mArtIconLoadedBitmap;
                blurBitmap(mArtIconLoadedBitmap, 10.0f, this.mContext);
                this.mMetadataBackground.setImageBitmap(mArtIconLoadedBitmap);
            }
            else {
                this.mMetadataBackground.setImageBitmap(Bitmap.createBitmap(this.mArtIconLoadedBitmap));
            }
        }
        else {
            if (isBitmapRecycled(this.mArtIconLoadedBitmap)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Can't set artwork image with recycled bitmap: ");
                sb.append(this.mArtIconLoadedBitmap);
                Log.w("MediaRouteCtrlDialog", sb.toString());
            }
            this.mArtView.setVisibility(8);
            this.mMetadataBlackScrim.setVisibility(8);
            this.mMetadataBackground.setImageBitmap((Bitmap)null);
        }
        this.clearLoadedBitmap();
        final MediaDescriptionCompat mDescription = this.mDescription;
        CharSequence title;
        if (mDescription == null) {
            title = null;
        }
        else {
            title = mDescription.getTitle();
        }
        final boolean empty = TextUtils.isEmpty(title);
        final MediaDescriptionCompat mDescription2 = this.mDescription;
        if (mDescription2 != null) {
            subtitle = mDescription2.getSubtitle();
        }
        final boolean empty2 = TextUtils.isEmpty(subtitle);
        if (empty ^ true) {
            this.mTitleView.setText(title);
        }
        else {
            this.mTitleView.setText((CharSequence)this.mTitlePlaceholder);
        }
        if (true ^ empty2) {
            this.mSubtitleView.setText(subtitle);
            this.mSubtitleView.setVisibility(0);
        }
        else {
            this.mSubtitleView.setVisibility(8);
        }
    }
    
    void updateRoutes() {
        this.mMemberRoutes.clear();
        this.mGroupableRoutes.clear();
        this.mTransferableRoutes.clear();
        this.mMemberRoutes.addAll((Collection<? extends MediaRouter.RouteInfo>)this.mSelectedRoute.getMemberRoutes());
        if (this.mSelectedRoute.getDynamicGroupState() != null) {
            for (final MediaRouter.RouteInfo routeInfo : this.mSelectedRoute.getProvider().getRoutes()) {
                final MediaRouter.RouteInfo.DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                if (dynamicGroupState == null) {
                    continue;
                }
                if (dynamicGroupState.isGroupable()) {
                    this.mGroupableRoutes.add(routeInfo);
                }
                if (!dynamicGroupState.isTransferable()) {
                    continue;
                }
                this.mTransferableRoutes.add(routeInfo);
            }
        }
        this.onFilterRoutes(this.mGroupableRoutes);
        this.onFilterRoutes(this.mTransferableRoutes);
        Collections.sort(this.mMemberRoutes, RouteComparator.sInstance);
        Collections.sort(this.mGroupableRoutes, RouteComparator.sInstance);
        Collections.sort(this.mTransferableRoutes, RouteComparator.sInstance);
        this.mAdapter.updateItems();
    }
    
    void updateRoutesView() {
        if (this.mAttachedToWindow) {
            if (SystemClock.uptimeMillis() - this.mLastUpdateTime >= 300L) {
                if (this.shouldDeferUpdateViews()) {
                    this.mUpdateRoutesViewDeferred = true;
                    return;
                }
                this.mUpdateRoutesViewDeferred = false;
                if (!this.mSelectedRoute.isSelected() || this.mSelectedRoute.isDefaultOrBluetooth()) {
                    this.dismiss();
                }
                this.mLastUpdateTime = SystemClock.uptimeMillis();
                this.mAdapter.notifyAdapterDataSetChanged();
            }
            else {
                this.mHandler.removeMessages(1);
                this.mHandler.sendEmptyMessageAtTime(1, this.mLastUpdateTime + 300L);
            }
        }
    }
    
    void updateViewsIfNeeded() {
        if (this.mUpdateRoutesViewDeferred) {
            this.updateRoutesView();
        }
        if (this.mUpdateMetadataViewsDeferred) {
            this.updateMetadataViews();
        }
    }
    
    private class FetchArtTask extends AsyncTask<Void, Void, Bitmap>
    {
        private int mBackgroundColor;
        private final Bitmap mIconBitmap;
        private final Uri mIconUri;
        
        FetchArtTask() {
            final MediaDescriptionCompat mDescription = MediaRouteDynamicControllerDialog.this.mDescription;
            final Uri uri = null;
            Bitmap iconBitmap;
            if (mDescription == null) {
                iconBitmap = null;
            }
            else {
                iconBitmap = mDescription.getIconBitmap();
            }
            Bitmap mIconBitmap = iconBitmap;
            if (MediaRouteDynamicControllerDialog.isBitmapRecycled(iconBitmap)) {
                Log.w("MediaRouteCtrlDialog", "Can't fetch the given art bitmap because it's already recycled.");
                mIconBitmap = null;
            }
            this.mIconBitmap = mIconBitmap;
            final MediaDescriptionCompat mDescription2 = MediaRouteDynamicControllerDialog.this.mDescription;
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
                openConnection.setConnectTimeout(30000);
                openConnection.setReadTimeout(30000);
                in = openConnection.getInputStream();
            }
            else {
                in = MediaRouteDynamicControllerDialog.this.mContext.getContentResolver().openInputStream(uri);
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
            //     1: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.mIconBitmap:Landroid/graphics/Bitmap;
            //     4: astore_1       
            //     5: iconst_0       
            //     6: istore_2       
            //     7: aconst_null    
            //     8: astore_3       
            //     9: aload_1        
            //    10: ifnull          16
            //    13: goto            560
            //    16: aload_0        
            //    17: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //    20: astore_1       
            //    21: aload_1        
            //    22: ifnull          558
            //    25: aload_0        
            //    26: aload_1        
            //    27: invokespecial   androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.openInputStreamByScheme:(Landroid/net/Uri;)Ljava/io/InputStream;
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
            //    82: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
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
            //   178: ifeq            454
            //   181: aload           4
            //   183: astore_3       
            //   184: aload           4
            //   186: astore_1       
            //   187: aload           6
            //   189: getfield        android/graphics/BitmapFactory$Options.outHeight:I
            //   192: istore          7
            //   194: iload           7
            //   196: ifne            202
            //   199: goto            454
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
            //   233: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //   236: invokespecial   androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.openInputStreamByScheme:(Landroid/net/Uri;)Ljava/io/InputStream;
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
            //   295: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
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
            //   350: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.this$0:Landroidx/mediarouter/app/MediaRouteDynamicControllerDialog;
            //   353: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog.mContext:Landroid/content/Context;
            //   356: invokevirtual   android/content/Context.getResources:()Landroid/content/res/Resources;
            //   359: getstatic       androidx/mediarouter/R$dimen.mr_cast_meta_art_size:I
            //   362: invokevirtual   android/content/res/Resources.getDimensionPixelSize:(I)I
            //   365: istore          7
            //   367: aload           4
            //   369: astore_3       
            //   370: aload           4
            //   372: astore_1       
            //   373: aload           6
            //   375: iconst_1       
            //   376: aload           6
            //   378: getfield        android/graphics/BitmapFactory$Options.outHeight:I
            //   381: iload           7
            //   383: idiv           
            //   384: invokestatic    java/lang/Integer.highestOneBit:(I)I
            //   387: invokestatic    java/lang/Math.max:(II)I
            //   390: putfield        android/graphics/BitmapFactory$Options.inSampleSize:I
            //   393: aload           4
            //   395: astore_3       
            //   396: aload           4
            //   398: astore_1       
            //   399: aload_0        
            //   400: invokevirtual   android/os/AsyncTask.isCancelled:()Z
            //   403: istore          8
            //   405: iload           8
            //   407: ifeq            422
            //   410: aload           4
            //   412: ifnull          420
            //   415: aload           4
            //   417: invokevirtual   java/io/InputStream.close:()V
            //   420: aconst_null    
            //   421: areturn        
            //   422: aload           4
            //   424: astore_3       
            //   425: aload           4
            //   427: astore_1       
            //   428: aload           4
            //   430: aconst_null    
            //   431: aload           6
            //   433: invokestatic    android/graphics/BitmapFactory.decodeStream:(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
            //   436: astore          5
            //   438: aload           4
            //   440: ifnull          448
            //   443: aload           4
            //   445: invokevirtual   java/io/InputStream.close:()V
            //   448: aload           5
            //   450: astore_1       
            //   451: goto            560
            //   454: aload           4
            //   456: ifnull          464
            //   459: aload           4
            //   461: invokevirtual   java/io/InputStream.close:()V
            //   464: aconst_null    
            //   465: areturn        
            //   466: astore          4
            //   468: goto            479
            //   471: astore_1       
            //   472: goto            548
            //   475: astore          4
            //   477: aconst_null    
            //   478: astore_3       
            //   479: aload_3        
            //   480: astore_1       
            //   481: new             Ljava/lang/StringBuilder;
            //   484: astore          5
            //   486: aload_3        
            //   487: astore_1       
            //   488: aload           5
            //   490: invokespecial   java/lang/StringBuilder.<init>:()V
            //   493: aload_3        
            //   494: astore_1       
            //   495: aload           5
            //   497: ldc             "Unable to open: "
            //   499: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   502: pop            
            //   503: aload_3        
            //   504: astore_1       
            //   505: aload           5
            //   507: aload_0        
            //   508: getfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.mIconUri:Landroid/net/Uri;
            //   511: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   514: pop            
            //   515: aload_3        
            //   516: astore_1       
            //   517: ldc             "MediaRouteCtrlDialog"
            //   519: aload           5
            //   521: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   524: aload           4
            //   526: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   529: pop            
            //   530: aload_3        
            //   531: ifnull          558
            //   534: aload_3        
            //   535: invokevirtual   java/io/InputStream.close:()V
            //   538: goto            558
            //   541: astore          4
            //   543: aload_1        
            //   544: astore_3       
            //   545: aload           4
            //   547: astore_1       
            //   548: aload_3        
            //   549: ifnull          556
            //   552: aload_3        
            //   553: invokevirtual   java/io/InputStream.close:()V
            //   556: aload_1        
            //   557: athrow         
            //   558: aconst_null    
            //   559: astore_1       
            //   560: aload_1        
            //   561: invokestatic    androidx/mediarouter/app/MediaRouteDynamicControllerDialog.isBitmapRecycled:(Landroid/graphics/Bitmap;)Z
            //   564: ifeq            600
            //   567: new             Ljava/lang/StringBuilder;
            //   570: dup            
            //   571: invokespecial   java/lang/StringBuilder.<init>:()V
            //   574: astore_3       
            //   575: aload_3        
            //   576: ldc             "Can't use recycled bitmap: "
            //   578: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   581: pop            
            //   582: aload_3        
            //   583: aload_1        
            //   584: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   587: pop            
            //   588: ldc             "MediaRouteCtrlDialog"
            //   590: aload_3        
            //   591: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   594: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //   597: pop            
            //   598: aconst_null    
            //   599: areturn        
            //   600: aload_1        
            //   601: ifnull          672
            //   604: aload_1        
            //   605: invokevirtual   android/graphics/Bitmap.getWidth:()I
            //   608: aload_1        
            //   609: invokevirtual   android/graphics/Bitmap.getHeight:()I
            //   612: if_icmpge       672
            //   615: new             Landroidx/palette/graphics/Palette$Builder;
            //   618: dup            
            //   619: aload_1        
            //   620: invokespecial   androidx/palette/graphics/Palette$Builder.<init>:(Landroid/graphics/Bitmap;)V
            //   623: astore_3       
            //   624: aload_3        
            //   625: iconst_1       
            //   626: invokevirtual   androidx/palette/graphics/Palette$Builder.maximumColorCount:(I)Landroidx/palette/graphics/Palette$Builder;
            //   629: pop            
            //   630: aload_3        
            //   631: invokevirtual   androidx/palette/graphics/Palette$Builder.generate:()Landroidx/palette/graphics/Palette;
            //   634: astore_3       
            //   635: aload_3        
            //   636: invokevirtual   androidx/palette/graphics/Palette.getSwatches:()Ljava/util/List;
            //   639: invokeinterface java/util/List.isEmpty:()Z
            //   644: ifeq            650
            //   647: goto            667
            //   650: aload_3        
            //   651: invokevirtual   androidx/palette/graphics/Palette.getSwatches:()Ljava/util/List;
            //   654: iconst_0       
            //   655: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
            //   660: checkcast       Landroidx/palette/graphics/Palette$Swatch;
            //   663: invokevirtual   androidx/palette/graphics/Palette$Swatch.getRgb:()I
            //   666: istore_2       
            //   667: aload_0        
            //   668: iload_2        
            //   669: putfield        androidx/mediarouter/app/MediaRouteDynamicControllerDialog$FetchArtTask.mBackgroundColor:I
            //   672: aload_1        
            //   673: areturn        
            //   674: astore_1       
            //   675: goto            116
            //   678: astore_1       
            //   679: goto            329
            //   682: astore_1       
            //   683: goto            420
            //   686: astore_1       
            //   687: goto            448
            //   690: astore_1       
            //   691: goto            464
            //   694: astore_1       
            //   695: goto            558
            //   698: astore_3       
            //   699: goto            556
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  25     32     475    479    Ljava/io/IOException;
            //  25     32     471    475    Any
            //  43     48     466    471    Ljava/io/IOException;
            //  43     48     541    548    Any
            //  54     59     466    471    Ljava/io/IOException;
            //  54     59     541    548    Any
            //  65     73     466    471    Ljava/io/IOException;
            //  65     73     541    548    Any
            //  79     89     466    471    Ljava/io/IOException;
            //  79     89     541    548    Any
            //  95     106    466    471    Ljava/io/IOException;
            //  95     106    541    548    Any
            //  111    116    674    678    Ljava/io/IOException;
            //  124    129    466    471    Ljava/io/IOException;
            //  124    129    541    548    Any
            //  135    140    466    471    Ljava/io/IOException;
            //  135    140    541    548    Any
            //  146    152    466    471    Ljava/io/IOException;
            //  146    152    541    548    Any
            //  158    167    466    471    Ljava/io/IOException;
            //  158    167    541    548    Any
            //  173    181    466    471    Ljava/io/IOException;
            //  173    181    541    548    Any
            //  187    194    466    471    Ljava/io/IOException;
            //  187    194    541    548    Any
            //  205    210    213    331    Ljava/io/IOException;
            //  205    210    541    548    Any
            //  220    225    466    471    Ljava/io/IOException;
            //  220    225    541    548    Any
            //  231    241    466    471    Ljava/io/IOException;
            //  231    241    541    548    Any
            //  256    261    466    471    Ljava/io/IOException;
            //  256    261    541    548    Any
            //  267    272    466    471    Ljava/io/IOException;
            //  267    272    541    548    Any
            //  278    286    466    471    Ljava/io/IOException;
            //  278    286    541    548    Any
            //  292    302    466    471    Ljava/io/IOException;
            //  292    302    541    548    Any
            //  308    319    466    471    Ljava/io/IOException;
            //  308    319    541    548    Any
            //  324    329    678    682    Ljava/io/IOException;
            //  337    343    466    471    Ljava/io/IOException;
            //  337    343    541    548    Any
            //  349    367    466    471    Ljava/io/IOException;
            //  349    367    541    548    Any
            //  373    393    466    471    Ljava/io/IOException;
            //  373    393    541    548    Any
            //  399    405    466    471    Ljava/io/IOException;
            //  399    405    541    548    Any
            //  415    420    682    686    Ljava/io/IOException;
            //  428    438    466    471    Ljava/io/IOException;
            //  428    438    541    548    Any
            //  443    448    686    690    Ljava/io/IOException;
            //  459    464    690    694    Ljava/io/IOException;
            //  481    486    541    548    Any
            //  488    493    541    548    Any
            //  495    503    541    548    Any
            //  505    515    541    548    Any
            //  517    530    541    548    Any
            //  534    538    694    698    Ljava/io/IOException;
            //  552    556    698    702    Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IndexOutOfBoundsException: Index 373 out of bounds for length 373
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
        
        Bitmap getIconBitmap() {
            return this.mIconBitmap;
        }
        
        Uri getIconUri() {
            return this.mIconUri;
        }
        
        protected void onPostExecute(final Bitmap mArtIconLoadedBitmap) {
            final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
            this$0.mFetchArtTask = null;
            if (!ObjectsCompat.equals(this$0.mArtIconBitmap, this.mIconBitmap) || !ObjectsCompat.equals(MediaRouteDynamicControllerDialog.this.mArtIconUri, this.mIconUri)) {
                final MediaRouteDynamicControllerDialog this$2 = MediaRouteDynamicControllerDialog.this;
                this$2.mArtIconBitmap = this.mIconBitmap;
                this$2.mArtIconLoadedBitmap = mArtIconLoadedBitmap;
                this$2.mArtIconUri = this.mIconUri;
                this$2.mArtIconBackgroundColor = this.mBackgroundColor;
                this$2.mArtIconIsLoaded = true;
                this$2.updateMetadataViews();
            }
        }
        
        protected void onPreExecute() {
            MediaRouteDynamicControllerDialog.this.clearLoadedBitmap();
        }
    }
    
    private final class MediaControllerCallback extends Callback
    {
        MediaControllerCallback() {
        }
        
        @Override
        public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) {
            final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
            MediaDescriptionCompat description;
            if (mediaMetadataCompat == null) {
                description = null;
            }
            else {
                description = mediaMetadataCompat.getDescription();
            }
            this$0.mDescription = description;
            MediaRouteDynamicControllerDialog.this.reloadIconIfNeeded();
            MediaRouteDynamicControllerDialog.this.updateMetadataViews();
        }
        
        @Override
        public void onSessionDestroyed() {
            final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
            final MediaControllerCompat mMediaController = this$0.mMediaController;
            if (mMediaController != null) {
                mMediaController.unregisterCallback((MediaControllerCompat.Callback)this$0.mControllerCallback);
                MediaRouteDynamicControllerDialog.this.mMediaController = null;
            }
        }
    }
    
    private abstract class MediaRouteVolumeSliderHolder extends ViewHolder
    {
        final ImageButton mMuteButton;
        MediaRouter.RouteInfo mRoute;
        final MediaRouteVolumeSlider mVolumeSlider;
        final /* synthetic */ MediaRouteDynamicControllerDialog this$0;
        
        MediaRouteVolumeSliderHolder(final View view, final ImageButton mMuteButton, final MediaRouteVolumeSlider mVolumeSlider) {
            super(view);
            this.mMuteButton = mMuteButton;
            this.mVolumeSlider = mVolumeSlider;
            this.mMuteButton.setImageDrawable(MediaRouterThemeHelper.getMuteButtonDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext));
            MediaRouterThemeHelper.setVolumeSliderColor(MediaRouteDynamicControllerDialog.this.mContext, this.mVolumeSlider);
        }
        
        void bindRouteVolumeSliderHolder(final MediaRouter.RouteInfo mRoute) {
            this.mRoute = mRoute;
            final int volume = mRoute.getVolume();
            this.mMuteButton.setActivated(volume == 0);
            this.mMuteButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
                    if (this$0.mRouteForVolumeUpdatingByUser != null) {
                        this$0.mHandler.removeMessages(2);
                    }
                    final MediaRouteVolumeSliderHolder this$2 = MediaRouteVolumeSliderHolder.this;
                    this$2.this$0.mRouteForVolumeUpdatingByUser = this$2.mRoute;
                    final boolean mute = view.isActivated() ^ true;
                    int unmutedVolume;
                    if (mute) {
                        unmutedVolume = 0;
                    }
                    else {
                        unmutedVolume = MediaRouteVolumeSliderHolder.this.getUnmutedVolume();
                    }
                    MediaRouteVolumeSliderHolder.this.setMute(mute);
                    MediaRouteVolumeSliderHolder.this.mVolumeSlider.setProgress(unmutedVolume);
                    MediaRouteVolumeSliderHolder.this.mRoute.requestSetVolume(unmutedVolume);
                    MediaRouteDynamicControllerDialog.this.mHandler.sendEmptyMessageDelayed(2, 500L);
                }
            });
            this.mVolumeSlider.setTag((Object)this.mRoute);
            this.mVolumeSlider.setMax(mRoute.getVolumeMax());
            this.mVolumeSlider.setProgress(volume);
            this.mVolumeSlider.setOnSeekBarChangeListener((SeekBar$OnSeekBarChangeListener)MediaRouteDynamicControllerDialog.this.mVolumeChangeListener);
        }
        
        int getUnmutedVolume() {
            final Integer n = MediaRouteDynamicControllerDialog.this.mUnmutedVolumeMap.get(this.mRoute.getId());
            int max = 1;
            if (n != null) {
                max = Math.max(1, n);
            }
            return max;
        }
        
        void setMute(final boolean activated) {
            if (this.mMuteButton.isActivated() == activated) {
                return;
            }
            this.mMuteButton.setActivated(activated);
            if (activated) {
                MediaRouteDynamicControllerDialog.this.mUnmutedVolumeMap.put(this.mRoute.getId(), this.mVolumeSlider.getProgress());
            }
            else {
                MediaRouteDynamicControllerDialog.this.mUnmutedVolumeMap.remove(this.mRoute.getId());
            }
        }
        
        void updateVolume() {
            final int volume = this.mRoute.getVolume();
            this.setMute(volume == 0);
            this.mVolumeSlider.setProgress(volume);
        }
    }
    
    private final class MediaRouterCallback extends Callback
    {
        MediaRouterCallback() {
        }
        
        @Override
        public void onRouteAdded(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteDynamicControllerDialog.this.updateRoutesView();
        }
        
        @Override
        public void onRouteChanged(final MediaRouter mediaRouter, RouteInfo routeInfo) {
            boolean b = false;
            Label_0113: {
                if (routeInfo == MediaRouteDynamicControllerDialog.this.mSelectedRoute && routeInfo.getDynamicGroupState() != null) {
                    final Iterator<RouteInfo> iterator = routeInfo.getProvider().getRoutes().iterator();
                    while (iterator.hasNext()) {
                        routeInfo = iterator.next();
                        if (MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes().contains(routeInfo)) {
                            continue;
                        }
                        final MediaRouter.RouteInfo.DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                        if (dynamicGroupState != null && dynamicGroupState.isGroupable() && !MediaRouteDynamicControllerDialog.this.mGroupableRoutes.contains(routeInfo)) {
                            b = true;
                            break Label_0113;
                        }
                    }
                }
                b = false;
            }
            if (b) {
                MediaRouteDynamicControllerDialog.this.updateViewsIfNeeded();
                MediaRouteDynamicControllerDialog.this.updateRoutes();
            }
            else {
                MediaRouteDynamicControllerDialog.this.updateRoutesView();
            }
        }
        
        @Override
        public void onRouteRemoved(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteDynamicControllerDialog.this.updateRoutesView();
        }
        
        @Override
        public void onRouteSelected(final MediaRouter mediaRouter, final RouteInfo mSelectedRoute) {
            final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
            this$0.mSelectedRoute = mSelectedRoute;
            this$0.mIsSelectingRoute = false;
            this$0.updateViewsIfNeeded();
            MediaRouteDynamicControllerDialog.this.updateRoutes();
        }
        
        @Override
        public void onRouteUnselected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteDynamicControllerDialog.this.updateRoutesView();
        }
        
        @Override
        public void onRouteVolumeChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            final int volume = routeInfo.getVolume();
            if (MediaRouteDynamicControllerDialog.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onRouteVolumeChanged(), route.getVolume:");
                sb.append(volume);
                Log.d("MediaRouteCtrlDialog", sb.toString());
            }
            final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
            if (this$0.mRouteForVolumeUpdatingByUser != routeInfo) {
                final MediaRouteVolumeSliderHolder mediaRouteVolumeSliderHolder = this$0.mVolumeSliderHolderMap.get(routeInfo.getId());
                if (mediaRouteVolumeSliderHolder != null) {
                    mediaRouteVolumeSliderHolder.updateVolume();
                }
            }
        }
    }
    
    private final class RecyclerAdapter extends Adapter<ViewHolder>
    {
        private final Interpolator mAccelerateDecelerateInterpolator;
        private final Drawable mDefaultIcon;
        private Item mGroupVolumeItem;
        private final LayoutInflater mInflater;
        private final ArrayList<Item> mItems;
        private final int mLayoutAnimationDurationMs;
        private final Drawable mSpeakerGroupIcon;
        private final Drawable mSpeakerIcon;
        private final Drawable mTvIcon;
        final /* synthetic */ MediaRouteDynamicControllerDialog this$0;
        
        RecyclerAdapter() {
            this.mItems = new ArrayList<Item>();
            this.mInflater = LayoutInflater.from(MediaRouteDynamicControllerDialog.this.mContext);
            this.mDefaultIcon = MediaRouterThemeHelper.getDefaultDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mTvIcon = MediaRouterThemeHelper.getTvDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mSpeakerIcon = MediaRouterThemeHelper.getSpeakerDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mSpeakerGroupIcon = MediaRouterThemeHelper.getSpeakerGroupDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mLayoutAnimationDurationMs = MediaRouteDynamicControllerDialog.this.mContext.getResources().getInteger(R$integer.mr_cast_volume_slider_layout_animation_duration_ms);
            this.mAccelerateDecelerateInterpolator = (Interpolator)new AccelerateDecelerateInterpolator();
            this.updateItems();
        }
        
        private Drawable getDefaultIconDrawable(final MediaRouter.RouteInfo routeInfo) {
            final int deviceType = routeInfo.getDeviceType();
            if (deviceType == 1) {
                return this.mTvIcon;
            }
            if (deviceType == 2) {
                return this.mSpeakerIcon;
            }
            if (routeInfo.isGroup()) {
                return this.mSpeakerGroupIcon;
            }
            return this.mDefaultIcon;
        }
        
        void animateLayoutHeight(final View view, final int n) {
            final Animation animation = new Animation(this) {
                final /* synthetic */ int val$startValue = view.getLayoutParams().height;
                
                protected void applyTransformation(final float n, final Transformation transformation) {
                    final int val$endValue = n;
                    final int val$startValue = this.val$startValue;
                    MediaRouteDynamicControllerDialog.setLayoutHeight(view, val$startValue + (int)((val$endValue - val$startValue) * n));
                }
            };
            animation.setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
                public void onAnimationEnd(final Animation animation) {
                    final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
                    this$0.mIsAnimatingVolumeSliderLayout = false;
                    this$0.updateViewsIfNeeded();
                }
                
                public void onAnimationRepeat(final Animation animation) {
                }
                
                public void onAnimationStart(final Animation animation) {
                    MediaRouteDynamicControllerDialog.this.mIsAnimatingVolumeSliderLayout = true;
                }
            });
            animation.setDuration((long)this.mLayoutAnimationDurationMs);
            animation.setInterpolator(this.mAccelerateDecelerateInterpolator);
            view.startAnimation((Animation)animation);
        }
        
        Drawable getIconDrawable(final MediaRouter.RouteInfo routeInfo) {
            final Uri iconUri = routeInfo.getIconUri();
            if (iconUri != null) {
                try {
                    final Drawable fromStream = Drawable.createFromStream(MediaRouteDynamicControllerDialog.this.mContext.getContentResolver().openInputStream(iconUri), (String)null);
                    if (fromStream != null) {
                        return fromStream;
                    }
                }
                catch (IOException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to load ");
                    sb.append(iconUri);
                    Log.w("MediaRouteCtrlDialog", sb.toString(), (Throwable)ex);
                }
            }
            return this.getDefaultIconDrawable(routeInfo);
        }
        
        public Item getItem(final int n) {
            if (n == 0) {
                return this.mGroupVolumeItem;
            }
            return this.mItems.get(n - 1);
        }
        
        @Override
        public int getItemCount() {
            return this.mItems.size() + 1;
        }
        
        @Override
        public int getItemViewType(final int n) {
            return this.getItem(n).getType();
        }
        
        boolean isGroupVolumeNeeded() {
            final int size = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes().size();
            boolean b = true;
            if (size <= 1) {
                b = false;
            }
            return b;
        }
        
        void mayUpdateGroupVolume(final MediaRouter.RouteInfo routeInfo, final boolean b) {
            final List<MediaRouter.RouteInfo> memberRoutes = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes();
            final int size = memberRoutes.size();
            final boolean b2 = true;
            final int max = Math.max(1, size);
            final boolean group = routeInfo.isGroup();
            int n = -1;
            int n3;
            if (group) {
                final Iterator<MediaRouter.RouteInfo> iterator = routeInfo.getMemberRoutes().iterator();
                int n2 = max;
                while (true) {
                    n3 = n2;
                    if (!iterator.hasNext()) {
                        break;
                    }
                    if (memberRoutes.contains(iterator.next()) == b) {
                        continue;
                    }
                    int n4;
                    if (b) {
                        n4 = 1;
                    }
                    else {
                        n4 = -1;
                    }
                    n2 += n4;
                }
            }
            else {
                if (b) {
                    n = 1;
                }
                n3 = max + n;
            }
            final boolean groupVolumeNeeded = this.isGroupVolumeNeeded();
            int expandedHeight = 0;
            final boolean b3 = n3 >= 2 && b2;
            if (groupVolumeNeeded != b3) {
                final RecyclerView.ViewHolder viewHolderForAdapterPosition = MediaRouteDynamicControllerDialog.this.mRecyclerView.findViewHolderForAdapterPosition(0);
                if (viewHolderForAdapterPosition instanceof GroupVolumeViewHolder) {
                    final GroupVolumeViewHolder groupVolumeViewHolder = (GroupVolumeViewHolder)viewHolderForAdapterPosition;
                    final View itemView = groupVolumeViewHolder.itemView;
                    if (b3) {
                        expandedHeight = groupVolumeViewHolder.getExpandedHeight();
                    }
                    this.animateLayoutHeight(itemView, expandedHeight);
                }
            }
        }
        
        void notifyAdapterDataSetChanged() {
            MediaRouteDynamicControllerDialog.this.mUngroupableRoutes.clear();
            final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
            this$0.mUngroupableRoutes.addAll(MediaRouteDialogHelper.getItemsRemoved(this$0.mGroupableRoutes, this$0.getCurrentGroupableRoutes()));
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
        
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
            final int itemViewType = this.getItemViewType(n);
            final Item item = this.getItem(n);
            if (itemViewType != 1) {
                if (itemViewType != 2) {
                    if (itemViewType != 3) {
                        if (itemViewType != 4) {
                            Log.w("MediaRouteCtrlDialog", "Cannot bind item to ViewHolder because of wrong view type");
                        }
                        else {
                            ((GroupViewHolder)viewHolder).bindGroupViewHolder(item);
                        }
                    }
                    else {
                        MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.put(((MediaRouter.RouteInfo)item.getData()).getId(), (MediaRouteVolumeSliderHolder)viewHolder);
                        ((RouteViewHolder)viewHolder).bindRouteViewHolder(item);
                    }
                }
                else {
                    ((HeaderViewHolder)viewHolder).bindHeaderViewHolder(item);
                }
            }
            else {
                MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.put(((MediaRouter.RouteInfo)item.getData()).getId(), (MediaRouteVolumeSliderHolder)viewHolder);
                ((GroupVolumeViewHolder)viewHolder).bindGroupVolumeViewHolder(item);
            }
        }
        
        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
            if (n == 1) {
                return new GroupVolumeViewHolder(this.mInflater.inflate(R$layout.mr_cast_group_volume_item, viewGroup, false));
            }
            if (n == 2) {
                return new HeaderViewHolder(this.mInflater.inflate(R$layout.mr_cast_header_item, viewGroup, false));
            }
            if (n == 3) {
                return new RouteViewHolder(this.mInflater.inflate(R$layout.mr_cast_route_item, viewGroup, false));
            }
            if (n != 4) {
                Log.w("MediaRouteCtrlDialog", "Cannot create ViewHolder because of wrong view type");
                return null;
            }
            return new GroupViewHolder(this.mInflater.inflate(R$layout.mr_cast_group_item, viewGroup, false));
        }
        
        @Override
        public void onViewRecycled(final ViewHolder viewHolder) {
            super.onViewRecycled(viewHolder);
            MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.values().remove(viewHolder);
        }
        
        void updateItems() {
            this.mItems.clear();
            this.mGroupVolumeItem = new Item(MediaRouteDynamicControllerDialog.this.mSelectedRoute, 1);
            if (!MediaRouteDynamicControllerDialog.this.mMemberRoutes.isEmpty()) {
                final Iterator<MediaRouter.RouteInfo> iterator = MediaRouteDynamicControllerDialog.this.mMemberRoutes.iterator();
                while (iterator.hasNext()) {
                    this.mItems.add(new Item(iterator.next(), 3));
                }
            }
            else {
                this.mItems.add(new Item(MediaRouteDynamicControllerDialog.this.mSelectedRoute, 3));
            }
            final boolean empty = MediaRouteDynamicControllerDialog.this.mGroupableRoutes.isEmpty();
            final int n = 0;
            if (!empty) {
                final Iterator<MediaRouter.RouteInfo> iterator2 = MediaRouteDynamicControllerDialog.this.mGroupableRoutes.iterator();
                int n2 = 0;
                while (iterator2.hasNext()) {
                    final MediaRouter.RouteInfo routeInfo = iterator2.next();
                    if (!MediaRouteDynamicControllerDialog.this.mMemberRoutes.contains(routeInfo)) {
                        int n3;
                        if ((n3 = n2) == 0) {
                            final MediaRouteProvider.DynamicGroupRouteController dynamicGroupController = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupController();
                            String groupableSelectionTitle;
                            if (dynamicGroupController != null) {
                                groupableSelectionTitle = dynamicGroupController.getGroupableSelectionTitle();
                            }
                            else {
                                groupableSelectionTitle = null;
                            }
                            String string = groupableSelectionTitle;
                            if (TextUtils.isEmpty((CharSequence)groupableSelectionTitle)) {
                                string = MediaRouteDynamicControllerDialog.this.mContext.getString(R$string.mr_dialog_groupable_header);
                            }
                            this.mItems.add(new Item(string, 2));
                            n3 = 1;
                        }
                        this.mItems.add(new Item(routeInfo, 3));
                        n2 = n3;
                    }
                }
            }
            if (!MediaRouteDynamicControllerDialog.this.mTransferableRoutes.isEmpty()) {
                final Iterator<MediaRouter.RouteInfo> iterator3 = MediaRouteDynamicControllerDialog.this.mTransferableRoutes.iterator();
                int n4 = n;
                while (iterator3.hasNext()) {
                    final MediaRouter.RouteInfo routeInfo2 = iterator3.next();
                    final MediaRouter.RouteInfo mSelectedRoute = MediaRouteDynamicControllerDialog.this.mSelectedRoute;
                    if (mSelectedRoute != routeInfo2) {
                        int n5;
                        if ((n5 = n4) == 0) {
                            final MediaRouteProvider.DynamicGroupRouteController dynamicGroupController2 = mSelectedRoute.getDynamicGroupController();
                            String transferableSectionTitle;
                            if (dynamicGroupController2 != null) {
                                transferableSectionTitle = dynamicGroupController2.getTransferableSectionTitle();
                            }
                            else {
                                transferableSectionTitle = null;
                            }
                            String string2 = transferableSectionTitle;
                            if (TextUtils.isEmpty((CharSequence)transferableSectionTitle)) {
                                string2 = MediaRouteDynamicControllerDialog.this.mContext.getString(R$string.mr_dialog_transferable_header);
                            }
                            this.mItems.add(new Item(string2, 2));
                            n5 = 1;
                        }
                        this.mItems.add(new Item(routeInfo2, 4));
                        n4 = n5;
                    }
                }
            }
            this.notifyAdapterDataSetChanged();
        }
        
        private class GroupViewHolder extends ViewHolder
        {
            final float mDisabledAlpha;
            final ImageView mImageView;
            final View mItemView;
            final ProgressBar mProgressBar;
            MediaRouter.RouteInfo mRoute;
            final TextView mTextView;
            final /* synthetic */ RecyclerAdapter this$1;
            
            GroupViewHolder(final View mItemView) {
                super(mItemView);
                this.mItemView = mItemView;
                this.mImageView = (ImageView)mItemView.findViewById(R$id.mr_cast_group_icon);
                this.mProgressBar = (ProgressBar)mItemView.findViewById(R$id.mr_cast_group_progress_bar);
                this.mTextView = (TextView)mItemView.findViewById(R$id.mr_cast_group_name);
                this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(RecyclerAdapter.this.this$0.mContext);
                MediaRouterThemeHelper.setIndeterminateProgressBarColor(RecyclerAdapter.this.this$0.mContext, this.mProgressBar);
            }
            
            private boolean isEnabled(final MediaRouter.RouteInfo routeInfo) {
                if (MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupState() != null) {
                    final List<MediaRouter.RouteInfo> memberRoutes = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes();
                    if (memberRoutes.size() == 1 && memberRoutes.get(0) == routeInfo) {
                        return false;
                    }
                }
                return true;
            }
            
            void bindGroupViewHolder(final Item item) {
                final MediaRouter.RouteInfo mRoute = (MediaRouter.RouteInfo)item.getData();
                this.mRoute = mRoute;
                this.mImageView.setVisibility(0);
                this.mProgressBar.setVisibility(4);
                final boolean enabled = this.isEnabled(mRoute);
                final View mItemView = this.mItemView;
                float mDisabledAlpha;
                if (enabled) {
                    mDisabledAlpha = 1.0f;
                }
                else {
                    mDisabledAlpha = this.mDisabledAlpha;
                }
                mItemView.setAlpha(mDisabledAlpha);
                this.mItemView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                    public void onClick(final View view) {
                        final GroupViewHolder this$2 = GroupViewHolder.this;
                        this$2.this$1.this$0.mIsSelectingRoute = true;
                        this$2.mRoute.select();
                        GroupViewHolder.this.mImageView.setVisibility(4);
                        GroupViewHolder.this.mProgressBar.setVisibility(0);
                    }
                });
                this.mImageView.setImageDrawable(RecyclerAdapter.this.getIconDrawable(mRoute));
                this.mTextView.setText((CharSequence)mRoute.getName());
            }
        }
        
        private class GroupVolumeViewHolder extends MediaRouteVolumeSliderHolder
        {
            private final int mExpandedHeight;
            private final TextView mTextView;
            
            GroupVolumeViewHolder(final View view) {
                RecyclerAdapter.this.this$0.super(view, (ImageButton)view.findViewById(R$id.mr_cast_mute_button), (MediaRouteVolumeSlider)view.findViewById(R$id.mr_cast_volume_slider));
                this.mTextView = (TextView)view.findViewById(R$id.mr_group_volume_route_name);
                final Resources resources = RecyclerAdapter.this.this$0.mContext.getResources();
                final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                final TypedValue typedValue = new TypedValue();
                resources.getValue(R$dimen.mr_dynamic_volume_group_list_item_height, typedValue, true);
                this.mExpandedHeight = (int)typedValue.getDimension(displayMetrics);
            }
            
            void bindGroupVolumeViewHolder(final Item item) {
                final View itemView = super.itemView;
                int mExpandedHeight;
                if (RecyclerAdapter.this.isGroupVolumeNeeded()) {
                    mExpandedHeight = this.mExpandedHeight;
                }
                else {
                    mExpandedHeight = 0;
                }
                MediaRouteDynamicControllerDialog.setLayoutHeight(itemView, mExpandedHeight);
                final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)item.getData();
                super.bindRouteVolumeSliderHolder(routeInfo);
                this.mTextView.setText((CharSequence)routeInfo.getName());
            }
            
            int getExpandedHeight() {
                return this.mExpandedHeight;
            }
        }
        
        private class HeaderViewHolder extends ViewHolder
        {
            private final TextView mTextView;
            
            HeaderViewHolder(final RecyclerAdapter recyclerAdapter, final View view) {
                super(view);
                this.mTextView = (TextView)view.findViewById(R$id.mr_cast_header_name);
            }
            
            void bindHeaderViewHolder(final Item item) {
                this.mTextView.setText((CharSequence)item.getData().toString());
            }
        }
        
        private class Item
        {
            private final Object mData;
            private final int mType;
            
            Item(final RecyclerAdapter recyclerAdapter, final Object mData, final int mType) {
                this.mData = mData;
                this.mType = mType;
            }
            
            public Object getData() {
                return this.mData;
            }
            
            public int getType() {
                return this.mType;
            }
        }
        
        private class RouteViewHolder extends MediaRouteVolumeSliderHolder
        {
            final CheckBox mCheckBox;
            final int mCollapsedLayoutHeight;
            final float mDisabledAlpha;
            final int mExpandedLayoutHeight;
            final ImageView mImageView;
            final View mItemView;
            final ProgressBar mProgressBar;
            final TextView mTextView;
            final View$OnClickListener mViewClickListener;
            final RelativeLayout mVolumeSliderLayout;
            final /* synthetic */ RecyclerAdapter this$1;
            
            RouteViewHolder(final View mItemView) {
                RecyclerAdapter.this.this$0.super(mItemView, (ImageButton)mItemView.findViewById(R$id.mr_cast_mute_button), (MediaRouteVolumeSlider)mItemView.findViewById(R$id.mr_cast_volume_slider));
                this.mViewClickListener = (View$OnClickListener)new View$OnClickListener() {
                    public void onClick(final View view) {
                        final RouteViewHolder this$2 = RouteViewHolder.this;
                        final boolean b = this$2.isSelected(this$2.mRoute) ^ true;
                        final boolean group = RouteViewHolder.this.mRoute.isGroup();
                        if (b) {
                            final RouteViewHolder this$3 = RouteViewHolder.this;
                            this$3.this$1.this$0.mRouter.addMemberToDynamicGroup(this$3.mRoute);
                        }
                        else {
                            final RouteViewHolder this$4 = RouteViewHolder.this;
                            this$4.this$1.this$0.mRouter.removeMemberFromDynamicGroup(this$4.mRoute);
                        }
                        RouteViewHolder.this.showSelectingProgress(b, group ^ true);
                        if (group) {
                            final List<MediaRouter.RouteInfo> memberRoutes = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes();
                            for (final MediaRouter.RouteInfo routeInfo : RouteViewHolder.this.mRoute.getMemberRoutes()) {
                                if (memberRoutes.contains(routeInfo) != b) {
                                    final MediaRouteVolumeSliderHolder mediaRouteVolumeSliderHolder = MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.get(routeInfo.getId());
                                    if (!(mediaRouteVolumeSliderHolder instanceof RouteViewHolder)) {
                                        continue;
                                    }
                                    ((RouteViewHolder)mediaRouteVolumeSliderHolder).showSelectingProgress(b, true);
                                }
                            }
                        }
                        final RouteViewHolder this$5 = RouteViewHolder.this;
                        this$5.this$1.mayUpdateGroupVolume(this$5.mRoute, b);
                    }
                };
                this.mItemView = mItemView;
                this.mImageView = (ImageView)mItemView.findViewById(R$id.mr_cast_route_icon);
                this.mProgressBar = (ProgressBar)mItemView.findViewById(R$id.mr_cast_route_progress_bar);
                this.mTextView = (TextView)mItemView.findViewById(R$id.mr_cast_route_name);
                this.mVolumeSliderLayout = (RelativeLayout)mItemView.findViewById(R$id.mr_cast_volume_layout);
                (this.mCheckBox = (CheckBox)mItemView.findViewById(R$id.mr_cast_checkbox)).setButtonDrawable(MediaRouterThemeHelper.getCheckBoxDrawableIcon(RecyclerAdapter.this.this$0.mContext));
                MediaRouterThemeHelper.setIndeterminateProgressBarColor(RecyclerAdapter.this.this$0.mContext, this.mProgressBar);
                this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(RecyclerAdapter.this.this$0.mContext);
                final Resources resources = RecyclerAdapter.this.this$0.mContext.getResources();
                final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                final TypedValue typedValue = new TypedValue();
                resources.getValue(R$dimen.mr_dynamic_dialog_row_height, typedValue, true);
                this.mExpandedLayoutHeight = (int)typedValue.getDimension(displayMetrics);
                this.mCollapsedLayoutHeight = 0;
            }
            
            private boolean isEnabled(final MediaRouter.RouteInfo routeInfo) {
                final boolean contains = MediaRouteDynamicControllerDialog.this.mUngroupableRoutes.contains(routeInfo);
                final boolean b = false;
                if (contains) {
                    return false;
                }
                if (this.isSelected(routeInfo) && MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes().size() < 2) {
                    return false;
                }
                if (this.isSelected(routeInfo) && MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupState() != null) {
                    final MediaRouter.RouteInfo.DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                    boolean b2 = b;
                    if (dynamicGroupState != null) {
                        b2 = b;
                        if (dynamicGroupState.isUnselectable()) {
                            b2 = true;
                        }
                    }
                    return b2;
                }
                return true;
            }
            
            void bindRouteViewHolder(final Item item) {
                MediaRouter.RouteInfo routeInfo2;
                final MediaRouter.RouteInfo routeInfo = routeInfo2 = (MediaRouter.RouteInfo)item.getData();
                if (routeInfo == MediaRouteDynamicControllerDialog.this.mSelectedRoute) {
                    routeInfo2 = routeInfo;
                    if (routeInfo.getMemberRoutes().size() > 0) {
                        final Iterator<MediaRouter.RouteInfo> iterator = routeInfo.getMemberRoutes().iterator();
                        do {
                            routeInfo2 = routeInfo;
                            if (!iterator.hasNext()) {
                                break;
                            }
                            routeInfo2 = iterator.next();
                        } while (MediaRouteDynamicControllerDialog.this.mGroupableRoutes.contains(routeInfo2));
                    }
                }
                ((MediaRouteVolumeSliderHolder)this).bindRouteVolumeSliderHolder(routeInfo2);
                this.mImageView.setImageDrawable(RecyclerAdapter.this.getIconDrawable(routeInfo2));
                this.mTextView.setText((CharSequence)routeInfo2.getName());
                final MediaRouter.RouteInfo.DynamicGroupState dynamicGroupState = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupState();
                final float n = 1.0f;
                final boolean b = false;
                if (dynamicGroupState != null) {
                    this.mCheckBox.setVisibility(0);
                    final boolean selected = this.isSelected(routeInfo2);
                    final boolean enabled = this.isEnabled(routeInfo2);
                    this.mCheckBox.setChecked(selected);
                    this.mProgressBar.setVisibility(4);
                    this.mImageView.setVisibility(0);
                    this.mItemView.setEnabled(enabled);
                    this.mCheckBox.setEnabled(enabled);
                    super.mMuteButton.setEnabled(enabled || selected);
                    final MediaRouteVolumeSlider mVolumeSlider = super.mVolumeSlider;
                    boolean enabled2 = false;
                    Label_0263: {
                        if (!enabled) {
                            enabled2 = b;
                            if (!selected) {
                                break Label_0263;
                            }
                        }
                        enabled2 = true;
                    }
                    mVolumeSlider.setEnabled(enabled2);
                    this.mItemView.setOnClickListener(this.mViewClickListener);
                    this.mCheckBox.setOnClickListener(this.mViewClickListener);
                    final RelativeLayout mVolumeSliderLayout = this.mVolumeSliderLayout;
                    int n2;
                    if (selected && !super.mRoute.isGroup()) {
                        n2 = this.mExpandedLayoutHeight;
                    }
                    else {
                        n2 = this.mCollapsedLayoutHeight;
                    }
                    MediaRouteDynamicControllerDialog.setLayoutHeight((View)mVolumeSliderLayout, n2);
                    final View mItemView = this.mItemView;
                    float mDisabledAlpha;
                    if (!enabled && !selected) {
                        mDisabledAlpha = this.mDisabledAlpha;
                    }
                    else {
                        mDisabledAlpha = 1.0f;
                    }
                    mItemView.setAlpha(mDisabledAlpha);
                    final CheckBox mCheckBox = this.mCheckBox;
                    float mDisabledAlpha2 = n;
                    if (!enabled) {
                        if (!selected) {
                            mDisabledAlpha2 = n;
                        }
                        else {
                            mDisabledAlpha2 = this.mDisabledAlpha;
                        }
                    }
                    mCheckBox.setAlpha(mDisabledAlpha2);
                }
                else {
                    this.mCheckBox.setVisibility(8);
                    this.mProgressBar.setVisibility(4);
                    this.mImageView.setVisibility(0);
                    MediaRouteDynamicControllerDialog.setLayoutHeight((View)this.mVolumeSliderLayout, this.mExpandedLayoutHeight);
                    this.mItemView.setAlpha(1.0f);
                }
            }
            
            boolean isSelected(final MediaRouter.RouteInfo routeInfo) {
                final boolean selected = routeInfo.isSelected();
                boolean b = true;
                if (selected) {
                    return true;
                }
                final MediaRouter.RouteInfo.DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                if (dynamicGroupState == null || dynamicGroupState.getSelectionState() != 3) {
                    b = false;
                }
                return b;
            }
            
            void showSelectingProgress(final boolean checked, final boolean b) {
                this.mCheckBox.setEnabled(false);
                this.mItemView.setEnabled(false);
                this.mCheckBox.setChecked(checked);
                if (checked) {
                    this.mImageView.setVisibility(4);
                    this.mProgressBar.setVisibility(0);
                }
                if (b) {
                    final RecyclerAdapter this$1 = RecyclerAdapter.this;
                    final RelativeLayout mVolumeSliderLayout = this.mVolumeSliderLayout;
                    int n;
                    if (checked) {
                        n = this.mExpandedLayoutHeight;
                    }
                    else {
                        n = this.mCollapsedLayoutHeight;
                    }
                    this$1.animateLayoutHeight((View)mVolumeSliderLayout, n);
                }
            }
        }
    }
    
    static final class RouteComparator implements Comparator<MediaRouter.RouteInfo>
    {
        static final RouteComparator sInstance;
        
        static {
            sInstance = new RouteComparator();
        }
        
        @Override
        public int compare(final MediaRouter.RouteInfo routeInfo, final MediaRouter.RouteInfo routeInfo2) {
            return routeInfo.getName().compareToIgnoreCase(routeInfo2.getName());
        }
    }
    
    private class VolumeChangeListener implements SeekBar$OnSeekBarChangeListener
    {
        VolumeChangeListener() {
        }
        
        public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
            if (b) {
                final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)seekBar.getTag();
                final MediaRouteVolumeSliderHolder mediaRouteVolumeSliderHolder = MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.get(routeInfo.getId());
                if (mediaRouteVolumeSliderHolder != null) {
                    mediaRouteVolumeSliderHolder.setMute(n == 0);
                }
                routeInfo.requestSetVolume(n);
            }
        }
        
        public void onStartTrackingTouch(final SeekBar seekBar) {
            final MediaRouteDynamicControllerDialog this$0 = MediaRouteDynamicControllerDialog.this;
            if (this$0.mRouteForVolumeUpdatingByUser != null) {
                this$0.mHandler.removeMessages(2);
            }
            MediaRouteDynamicControllerDialog.this.mRouteForVolumeUpdatingByUser = (MediaRouter.RouteInfo)seekBar.getTag();
        }
        
        public void onStopTrackingTouch(final SeekBar seekBar) {
            MediaRouteDynamicControllerDialog.this.mHandler.sendEmptyMessageDelayed(2, 500L);
        }
    }
}
