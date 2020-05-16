// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.content.IntentFilter;
import java.util.Iterator;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import android.util.Log;
import androidx.core.graphics.drawable.DrawableCompat;
import android.graphics.drawable.Drawable$Callback;
import android.graphics.drawable.AnimationDrawable;
import android.view.View$MeasureSpec;
import android.graphics.Canvas;
import androidx.appcompat.widget.TooltipCompat;
import android.text.TextUtils;
import androidx.mediarouter.R$string;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.content.ContextWrapper;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import androidx.core.view.ViewCompat;
import androidx.mediarouter.R$styleable;
import androidx.mediarouter.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import android.graphics.drawable.Drawable;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable$ConstantState;
import android.util.SparseArray;
import android.view.View;

public class MediaRouteButton extends View
{
    private static final int[] CHECKABLE_STATE_SET;
    private static final int[] CHECKED_STATE_SET;
    private static ConnectivityReceiver sConnectivityReceiver;
    static final SparseArray<Drawable$ConstantState> sRemoteIndicatorCache;
    private boolean mAlwaysVisible;
    private boolean mAttachedToWindow;
    private ColorStateList mButtonTint;
    private final MediaRouterCallback mCallback;
    private boolean mCheatSheetEnabled;
    private int mConnectionState;
    private MediaRouteDialogFactory mDialogFactory;
    private int mMinHeight;
    private int mMinWidth;
    private Drawable mRemoteIndicator;
    RemoteIndicatorLoader mRemoteIndicatorLoader;
    private int mRemoteIndicatorResIdToLoad;
    private final MediaRouter mRouter;
    private MediaRouteSelector mSelector;
    private boolean mUseDynamicGroup;
    private int mVisibility;
    
    static {
        sRemoteIndicatorCache = new SparseArray(2);
        CHECKED_STATE_SET = new int[] { 16842912 };
        CHECKABLE_STATE_SET = new int[] { 16842911 };
    }
    
    public MediaRouteButton(final Context context) {
        this(context, null);
    }
    
    public MediaRouteButton(final Context context, final AttributeSet set) {
        this(context, set, R$attr.mediaRouteButtonStyle);
    }
    
    public MediaRouteButton(Context context, final AttributeSet set, int n) {
        super(MediaRouterThemeHelper.createThemedButtonContext(context), set, n);
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mDialogFactory = MediaRouteDialogFactory.getDefault();
        this.mVisibility = 0;
        context = this.getContext();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.MediaRouteButton, n, 0);
        ViewCompat.saveAttributeDataForStyleable(this, context, R$styleable.MediaRouteButton, set, obtainStyledAttributes, n, 0);
        if (this.isInEditMode()) {
            this.mRouter = null;
            this.mCallback = null;
            n = obtainStyledAttributes.getResourceId(R$styleable.MediaRouteButton_externalRouteEnabledDrawableStatic, 0);
            this.mRemoteIndicator = this.getResources().getDrawable(n);
            return;
        }
        this.mRouter = MediaRouter.getInstance(context);
        this.mCallback = new MediaRouterCallback();
        if (MediaRouteButton.sConnectivityReceiver == null) {
            MediaRouteButton.sConnectivityReceiver = new ConnectivityReceiver(context.getApplicationContext());
        }
        this.mButtonTint = obtainStyledAttributes.getColorStateList(R$styleable.MediaRouteButton_mediaRouteButtonTint);
        this.mMinWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.MediaRouteButton_android_minWidth, 0);
        this.mMinHeight = obtainStyledAttributes.getDimensionPixelSize(R$styleable.MediaRouteButton_android_minHeight, 0);
        final int resourceId = obtainStyledAttributes.getResourceId(R$styleable.MediaRouteButton_externalRouteEnabledDrawableStatic, 0);
        this.mRemoteIndicatorResIdToLoad = obtainStyledAttributes.getResourceId(R$styleable.MediaRouteButton_externalRouteEnabledDrawable, 0);
        obtainStyledAttributes.recycle();
        n = this.mRemoteIndicatorResIdToLoad;
        if (n != 0) {
            final Drawable$ConstantState drawable$ConstantState = (Drawable$ConstantState)MediaRouteButton.sRemoteIndicatorCache.get(n);
            if (drawable$ConstantState != null) {
                this.setRemoteIndicatorDrawable(drawable$ConstantState.newDrawable());
            }
        }
        if (this.mRemoteIndicator == null) {
            if (resourceId != 0) {
                final Drawable$ConstantState drawable$ConstantState2 = (Drawable$ConstantState)MediaRouteButton.sRemoteIndicatorCache.get(resourceId);
                if (drawable$ConstantState2 != null) {
                    this.setRemoteIndicatorDrawableInternal(drawable$ConstantState2.newDrawable());
                }
                else {
                    (this.mRemoteIndicatorLoader = new RemoteIndicatorLoader(resourceId, this.getContext())).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Object[])new Void[0]);
                }
            }
            else {
                this.loadRemoteIndicatorIfNeeded();
            }
        }
        this.updateContentDescription();
        this.setClickable(true);
    }
    
    private Activity getActivity() {
        for (Context context = this.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper)context).getBaseContext()) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
        }
        return null;
    }
    
    private FragmentManager getFragmentManager() {
        final Activity activity = this.getActivity();
        if (activity instanceof FragmentActivity) {
            return ((FragmentActivity)activity).getSupportFragmentManager();
        }
        return null;
    }
    
    private void loadRemoteIndicatorIfNeeded() {
        if (this.mRemoteIndicatorResIdToLoad > 0) {
            final RemoteIndicatorLoader mRemoteIndicatorLoader = this.mRemoteIndicatorLoader;
            if (mRemoteIndicatorLoader != null) {
                mRemoteIndicatorLoader.cancel(false);
            }
            final RemoteIndicatorLoader mRemoteIndicatorLoader2 = new RemoteIndicatorLoader(this.mRemoteIndicatorResIdToLoad, this.getContext());
            this.mRemoteIndicatorLoader = mRemoteIndicatorLoader2;
            this.mRemoteIndicatorResIdToLoad = 0;
            mRemoteIndicatorLoader2.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Object[])new Void[0]);
        }
    }
    
    private void updateContentDescription() {
        final int mConnectionState = this.mConnectionState;
        int n;
        if (mConnectionState != 1) {
            if (mConnectionState != 2) {
                n = R$string.mr_cast_button_disconnected;
            }
            else {
                n = R$string.mr_cast_button_connected;
            }
        }
        else {
            n = R$string.mr_cast_button_connecting;
        }
        String string = this.getContext().getString(n);
        this.setContentDescription((CharSequence)string);
        if (!this.mCheatSheetEnabled || TextUtils.isEmpty((CharSequence)string)) {
            string = null;
        }
        TooltipCompat.setTooltipText(this, string);
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mRemoteIndicator != null) {
            this.mRemoteIndicator.setState(this.getDrawableState());
            this.invalidate();
        }
    }
    
    public void enableDynamicGroup() {
        this.mUseDynamicGroup = true;
    }
    
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        final Drawable mRemoteIndicator = this.mRemoteIndicator;
        if (mRemoteIndicator != null) {
            mRemoteIndicator.jumpToCurrentState();
        }
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isInEditMode()) {
            return;
        }
        this.mAttachedToWindow = true;
        if (!this.mSelector.isEmpty()) {
            this.mRouter.addCallback(this.mSelector, (MediaRouter.Callback)this.mCallback);
        }
        this.refreshRoute();
        MediaRouteButton.sConnectivityReceiver.registerReceiver(this);
    }
    
    protected int[] onCreateDrawableState(int mConnectionState) {
        final int[] onCreateDrawableState = super.onCreateDrawableState(mConnectionState + 1);
        mConnectionState = this.mConnectionState;
        if (mConnectionState != 1) {
            if (mConnectionState == 2) {
                View.mergeDrawableStates(onCreateDrawableState, MediaRouteButton.CHECKED_STATE_SET);
            }
        }
        else {
            View.mergeDrawableStates(onCreateDrawableState, MediaRouteButton.CHECKABLE_STATE_SET);
        }
        return onCreateDrawableState;
    }
    
    public void onDetachedFromWindow() {
        if (!this.isInEditMode()) {
            this.mAttachedToWindow = false;
            if (!this.mSelector.isEmpty()) {
                this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
            }
            MediaRouteButton.sConnectivityReceiver.unregisterReceiver(this);
        }
        super.onDetachedFromWindow();
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mRemoteIndicator != null) {
            final int paddingLeft = this.getPaddingLeft();
            final int width = this.getWidth();
            final int paddingRight = this.getPaddingRight();
            final int paddingTop = this.getPaddingTop();
            final int height = this.getHeight();
            final int paddingBottom = this.getPaddingBottom();
            final int intrinsicWidth = this.mRemoteIndicator.getIntrinsicWidth();
            final int intrinsicHeight = this.mRemoteIndicator.getIntrinsicHeight();
            final int n = paddingLeft + (width - paddingRight - paddingLeft - intrinsicWidth) / 2;
            final int n2 = paddingTop + (height - paddingBottom - paddingTop - intrinsicHeight) / 2;
            this.mRemoteIndicator.setBounds(n, n2, intrinsicWidth + n, intrinsicHeight + n2);
            this.mRemoteIndicator.draw(canvas);
        }
    }
    
    protected void onMeasure(int min, int n) {
        final int size = View$MeasureSpec.getSize(min);
        final int size2 = View$MeasureSpec.getSize(n);
        final int mode = View$MeasureSpec.getMode(min);
        final int mode2 = View$MeasureSpec.getMode(n);
        n = this.mMinWidth;
        final Drawable mRemoteIndicator = this.mRemoteIndicator;
        final int n2 = 0;
        if (mRemoteIndicator != null) {
            min = mRemoteIndicator.getIntrinsicWidth() + this.getPaddingLeft() + this.getPaddingRight();
        }
        else {
            min = 0;
        }
        n = Math.max(n, min);
        final int mMinHeight = this.mMinHeight;
        final Drawable mRemoteIndicator2 = this.mRemoteIndicator;
        min = n2;
        if (mRemoteIndicator2 != null) {
            min = mRemoteIndicator2.getIntrinsicHeight() + this.getPaddingTop() + this.getPaddingBottom();
        }
        final int max = Math.max(mMinHeight, min);
        if (mode != Integer.MIN_VALUE) {
            min = size;
            if (mode != 1073741824) {
                min = n;
            }
        }
        else {
            min = Math.min(size, n);
        }
        if (mode2 != Integer.MIN_VALUE) {
            n = size2;
            if (mode2 != 1073741824) {
                n = max;
            }
        }
        else {
            n = Math.min(size2, max);
        }
        this.setMeasuredDimension(min, n);
    }
    
    public boolean performClick() {
        final boolean performClick = super.performClick();
        boolean b = false;
        if (!performClick) {
            this.playSoundEffect(0);
        }
        this.loadRemoteIndicatorIfNeeded();
        if (this.showDialog() || performClick) {
            b = true;
        }
        return b;
    }
    
    void refreshRoute() {
        final MediaRouter.RouteInfo selectedRoute = this.mRouter.getSelectedRoute();
        final boolean defaultOrBluetooth = selectedRoute.isDefaultOrBluetooth();
        boolean enabled = false;
        int connectionState;
        if (!defaultOrBluetooth && selectedRoute.matchesSelector(this.mSelector)) {
            connectionState = selectedRoute.getConnectionState();
        }
        else {
            connectionState = 0;
        }
        boolean b;
        if (this.mConnectionState != connectionState) {
            this.mConnectionState = connectionState;
            b = true;
        }
        else {
            b = false;
        }
        if (b) {
            this.updateContentDescription();
            this.refreshDrawableState();
        }
        if (connectionState == 1) {
            this.loadRemoteIndicatorIfNeeded();
        }
        if (this.mAttachedToWindow) {
            if (this.mAlwaysVisible || this.mRouter.isRouteAvailable(this.mSelector, 1)) {
                enabled = true;
            }
            this.setEnabled(enabled);
        }
        final Drawable mRemoteIndicator = this.mRemoteIndicator;
        if (mRemoteIndicator != null && mRemoteIndicator.getCurrent() instanceof AnimationDrawable) {
            final AnimationDrawable animationDrawable = (AnimationDrawable)this.mRemoteIndicator.getCurrent();
            if (this.mAttachedToWindow) {
                if ((b || connectionState == 1) && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
            }
            else if (connectionState == 2) {
                if (animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
                animationDrawable.selectDrawable(animationDrawable.getNumberOfFrames() - 1);
            }
        }
    }
    
    void refreshVisibility() {
        int mVisibility;
        if (this.mVisibility == 0 && !this.mAlwaysVisible && !MediaRouteButton.sConnectivityReceiver.isConnected()) {
            mVisibility = 4;
        }
        else {
            mVisibility = this.mVisibility;
        }
        super.setVisibility(mVisibility);
        final Drawable mRemoteIndicator = this.mRemoteIndicator;
        if (mRemoteIndicator != null) {
            mRemoteIndicator.setVisible(this.getVisibility() == 0, false);
        }
    }
    
    public void setAlwaysVisible(final boolean mAlwaysVisible) {
        if (mAlwaysVisible != this.mAlwaysVisible) {
            this.mAlwaysVisible = mAlwaysVisible;
            this.refreshVisibility();
            this.refreshRoute();
        }
    }
    
    void setCheatSheetEnabled(final boolean mCheatSheetEnabled) {
        if (mCheatSheetEnabled != this.mCheatSheetEnabled) {
            this.mCheatSheetEnabled = mCheatSheetEnabled;
            this.updateContentDescription();
        }
    }
    
    public void setDialogFactory(final MediaRouteDialogFactory mDialogFactory) {
        if (mDialogFactory != null) {
            this.mDialogFactory = mDialogFactory;
            return;
        }
        throw new IllegalArgumentException("factory must not be null");
    }
    
    public void setRemoteIndicatorDrawable(final Drawable remoteIndicatorDrawableInternal) {
        this.mRemoteIndicatorResIdToLoad = 0;
        this.setRemoteIndicatorDrawableInternal(remoteIndicatorDrawableInternal);
    }
    
    void setRemoteIndicatorDrawableInternal(Drawable mRemoteIndicator) {
        final RemoteIndicatorLoader mRemoteIndicatorLoader = this.mRemoteIndicatorLoader;
        if (mRemoteIndicatorLoader != null) {
            mRemoteIndicatorLoader.cancel(false);
        }
        final Drawable mRemoteIndicator2 = this.mRemoteIndicator;
        if (mRemoteIndicator2 != null) {
            mRemoteIndicator2.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mRemoteIndicator);
        }
        Drawable wrap;
        if ((wrap = mRemoteIndicator) != null) {
            wrap = mRemoteIndicator;
            if (this.mButtonTint != null) {
                wrap = DrawableCompat.wrap(mRemoteIndicator.mutate());
                DrawableCompat.setTintList(wrap, this.mButtonTint);
            }
            wrap.setCallback((Drawable$Callback)this);
            wrap.setState(this.getDrawableState());
            wrap.setVisible(this.getVisibility() == 0, false);
        }
        this.mRemoteIndicator = wrap;
        this.refreshDrawableState();
        if (this.mAttachedToWindow) {
            mRemoteIndicator = this.mRemoteIndicator;
            if (mRemoteIndicator != null && mRemoteIndicator.getCurrent() instanceof AnimationDrawable) {
                final AnimationDrawable animationDrawable = (AnimationDrawable)this.mRemoteIndicator.getCurrent();
                final int mConnectionState = this.mConnectionState;
                if (mConnectionState == 1) {
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                }
                else if (mConnectionState == 2) {
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    animationDrawable.selectDrawable(animationDrawable.getNumberOfFrames() - 1);
                }
            }
        }
    }
    
    public void setRouteSelector(final MediaRouteSelector mSelector) {
        if (mSelector != null) {
            if (!this.mSelector.equals(mSelector)) {
                if (this.mAttachedToWindow) {
                    if (!this.mSelector.isEmpty()) {
                        this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
                    }
                    if (!mSelector.isEmpty()) {
                        this.mRouter.addCallback(mSelector, (MediaRouter.Callback)this.mCallback);
                    }
                }
                this.mSelector = mSelector;
                this.refreshRoute();
            }
            return;
        }
        throw new IllegalArgumentException("selector must not be null");
    }
    
    public void setVisibility(final int mVisibility) {
        this.mVisibility = mVisibility;
        this.refreshVisibility();
    }
    
    public boolean showDialog() {
        if (!this.mAttachedToWindow) {
            return false;
        }
        final FragmentManager fragmentManager = this.getFragmentManager();
        if (fragmentManager != null) {
            final MediaRouter.RouteInfo selectedRoute = this.mRouter.getSelectedRoute();
            if (!selectedRoute.isDefaultOrBluetooth() && selectedRoute.matchesSelector(this.mSelector)) {
                if (fragmentManager.findFragmentByTag("android.support.v7.mediarouter:MediaRouteControllerDialogFragment") != null) {
                    Log.w("MediaRouteButton", "showDialog(): Route controller dialog already showing!");
                    return false;
                }
                final MediaRouteControllerDialogFragment onCreateControllerDialogFragment = this.mDialogFactory.onCreateControllerDialogFragment();
                onCreateControllerDialogFragment.setRouteSelector(this.mSelector);
                onCreateControllerDialogFragment.setUseDynamicGroup(this.mUseDynamicGroup);
                final FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
                beginTransaction.add(onCreateControllerDialogFragment, "android.support.v7.mediarouter:MediaRouteControllerDialogFragment");
                beginTransaction.commitAllowingStateLoss();
            }
            else {
                if (fragmentManager.findFragmentByTag("android.support.v7.mediarouter:MediaRouteChooserDialogFragment") != null) {
                    Log.w("MediaRouteButton", "showDialog(): Route chooser dialog already showing!");
                    return false;
                }
                final MediaRouteChooserDialogFragment onCreateChooserDialogFragment = this.mDialogFactory.onCreateChooserDialogFragment();
                onCreateChooserDialogFragment.setRouteSelector(this.mSelector);
                onCreateChooserDialogFragment.setUseDynamicGroup(this.mUseDynamicGroup);
                final FragmentTransaction beginTransaction2 = fragmentManager.beginTransaction();
                beginTransaction2.add(onCreateChooserDialogFragment, "android.support.v7.mediarouter:MediaRouteChooserDialogFragment");
                beginTransaction2.commitAllowingStateLoss();
            }
            return true;
        }
        throw new IllegalStateException("The activity must be a subclass of FragmentActivity");
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mRemoteIndicator;
    }
    
    private static final class ConnectivityReceiver extends BroadcastReceiver
    {
        private List<MediaRouteButton> mButtons;
        private final Context mContext;
        private boolean mIsConnected;
        
        ConnectivityReceiver(final Context mContext) {
            this.mIsConnected = true;
            this.mContext = mContext;
            this.mButtons = new ArrayList<MediaRouteButton>();
        }
        
        public boolean isConnected() {
            return this.mIsConnected;
        }
        
        public void onReceive(final Context context, final Intent intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                final boolean mIsConnected = intent.getBooleanExtra("noConnectivity", false) ^ true;
                if (this.mIsConnected != mIsConnected) {
                    this.mIsConnected = mIsConnected;
                    final Iterator<MediaRouteButton> iterator = this.mButtons.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().refreshVisibility();
                    }
                }
            }
        }
        
        public void registerReceiver(final MediaRouteButton mediaRouteButton) {
            if (this.mButtons.size() == 0) {
                final IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                this.mContext.registerReceiver((BroadcastReceiver)this, intentFilter);
            }
            this.mButtons.add(mediaRouteButton);
        }
        
        public void unregisterReceiver(final MediaRouteButton mediaRouteButton) {
            this.mButtons.remove(mediaRouteButton);
            if (this.mButtons.size() == 0) {
                this.mContext.unregisterReceiver((BroadcastReceiver)this);
            }
        }
    }
    
    private final class MediaRouterCallback extends Callback
    {
        MediaRouterCallback() {
        }
        
        @Override
        public void onProviderAdded(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
            MediaRouteButton.this.refreshRoute();
        }
        
        @Override
        public void onProviderChanged(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
            MediaRouteButton.this.refreshRoute();
        }
        
        @Override
        public void onProviderRemoved(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
            MediaRouteButton.this.refreshRoute();
        }
        
        @Override
        public void onRouteAdded(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteButton.this.refreshRoute();
        }
        
        @Override
        public void onRouteChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteButton.this.refreshRoute();
        }
        
        @Override
        public void onRouteRemoved(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteButton.this.refreshRoute();
        }
        
        @Override
        public void onRouteSelected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteButton.this.refreshRoute();
        }
        
        @Override
        public void onRouteUnselected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteButton.this.refreshRoute();
        }
    }
    
    private final class RemoteIndicatorLoader extends AsyncTask<Void, Void, Drawable>
    {
        private final Context mContext;
        private final int mResId;
        
        RemoteIndicatorLoader(final int mResId, final Context mContext) {
            this.mResId = mResId;
            this.mContext = mContext;
        }
        
        private void cacheAndReset(final Drawable drawable) {
            if (drawable != null) {
                MediaRouteButton.sRemoteIndicatorCache.put(this.mResId, (Object)drawable.getConstantState());
            }
            MediaRouteButton.this.mRemoteIndicatorLoader = null;
        }
        
        protected Drawable doInBackground(final Void... array) {
            if (MediaRouteButton.sRemoteIndicatorCache.get(this.mResId) == null) {
                return this.mContext.getResources().getDrawable(this.mResId);
            }
            return null;
        }
        
        protected void onCancelled(final Drawable drawable) {
            this.cacheAndReset(drawable);
        }
        
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                this.cacheAndReset(drawable);
            }
            else {
                final Drawable$ConstantState drawable$ConstantState = (Drawable$ConstantState)MediaRouteButton.sRemoteIndicatorCache.get(this.mResId);
                if (drawable$ConstantState != null) {
                    drawable = drawable$ConstantState.newDrawable();
                }
                MediaRouteButton.this.mRemoteIndicatorLoader = null;
            }
            MediaRouteButton.this.setRemoteIndicatorDrawableInternal(drawable);
        }
    }
}
