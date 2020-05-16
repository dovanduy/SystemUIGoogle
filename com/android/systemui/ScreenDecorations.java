// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.graphics.Paint$Style;
import android.graphics.Canvas;
import java.util.Collection;
import android.graphics.Matrix;
import java.util.ArrayList;
import android.graphics.RectF;
import android.graphics.Paint;
import android.view.DisplayInfo;
import android.animation.ValueAnimator;
import android.os.HandlerThread;
import android.content.res.Configuration;
import android.view.WindowManager$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.graphics.drawable.VectorDrawable;
import com.android.internal.util.Preconditions;
import android.widget.ImageView;
import android.content.res.ColorStateList;
import android.view.ViewTreeObserver;
import android.view.DisplayCutout;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import java.util.concurrent.Executor;
import java.util.Objects;
import java.util.Iterator;
import android.graphics.Region$Op;
import android.graphics.Region;
import java.util.List;
import android.view.ViewTreeObserver$OnPreDrawListener;
import android.view.View$OnLayoutChangeListener;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.app.ActivityManager;
import android.content.Intent;
import android.util.Log;
import android.graphics.Rect;
import android.graphics.Path;
import android.content.Context;
import android.os.SystemProperties;
import android.view.WindowManager;
import android.view.View;
import android.content.BroadcastReceiver;
import android.os.Handler;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager$DisplayListener;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.tuner.TunerService;

public class ScreenDecorations extends SystemUI implements Tunable
{
    private static final boolean DEBUG_COLOR;
    private static final boolean DEBUG_SCREENSHOT_ROUNDED_CORNERS;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private CameraAvailabilityListener mCameraListener;
    private CameraAvailabilityListener.CameraTransitionCallback mCameraTransitionCallback;
    private SecureSetting mColorInversionSetting;
    private DisplayCutoutView[] mCutoutViews;
    private float mDensity;
    private DisplayManager$DisplayListener mDisplayListener;
    private DisplayManager mDisplayManager;
    private Handler mHandler;
    private final BroadcastReceiver mIntentReceiver;
    protected boolean mIsRegistered;
    private boolean mIsRoundedCornerMultipleRadius;
    private final Handler mMainHandler;
    protected View[] mOverlays;
    private boolean mPendingRotationChange;
    private int mRotation;
    protected int mRoundedDefault;
    protected int mRoundedDefaultBottom;
    protected int mRoundedDefaultTop;
    private final TunerService mTunerService;
    private WindowManager mWindowManager;
    
    static {
        DEBUG_COLOR = (DEBUG_SCREENSHOT_ROUNDED_CORNERS = SystemProperties.getBoolean("debug.screenshot_rounded_corners", false));
    }
    
    public ScreenDecorations(final Context context, final Handler mMainHandler, final BroadcastDispatcher mBroadcastDispatcher, final TunerService mTunerService) {
        super(context);
        this.mCameraTransitionCallback = new CameraAvailabilityListener.CameraTransitionCallback() {
            @Override
            public void onApplyCameraProtection(final Path path, final Rect rect) {
                if (ScreenDecorations.this.mCutoutViews == null) {
                    Log.w("ScreenDecorations", "DisplayCutoutView do not initialized");
                    return;
                }
                for (final DisplayCutoutView displayCutoutView : ScreenDecorations.this.mCutoutViews) {
                    if (displayCutoutView != null) {
                        displayCutoutView.setProtection(path, rect);
                        displayCutoutView.setShowProtection(true);
                    }
                }
            }
            
            @Override
            public void onHideCameraProtection() {
                if (ScreenDecorations.this.mCutoutViews == null) {
                    Log.w("ScreenDecorations", "DisplayCutoutView do not initialized");
                    return;
                }
                for (final DisplayCutoutView displayCutoutView : ScreenDecorations.this.mCutoutViews) {
                    if (displayCutoutView != null) {
                        displayCutoutView.setShowProtection(false);
                    }
                }
            }
        };
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals("android.intent.action.USER_SWITCHED")) {
                    ScreenDecorations.this.mColorInversionSetting.setUserId(intent.getIntExtra("android.intent.extra.user_handle", ActivityManager.getCurrentUser()));
                    final ScreenDecorations this$0 = ScreenDecorations.this;
                    this$0.updateColorInversion(this$0.mColorInversionSetting.getValue());
                }
            }
        };
        this.mMainHandler = mMainHandler;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mTunerService = mTunerService;
    }
    
    private void createOverlay(final int n) {
        if (this.mOverlays == null) {
            this.mOverlays = new View[4];
        }
        if (this.mCutoutViews == null) {
            this.mCutoutViews = new DisplayCutoutView[4];
        }
        final View[] mOverlays = this.mOverlays;
        if (mOverlays[n] != null) {
            return;
        }
        mOverlays[n] = LayoutInflater.from(super.mContext).inflate(R$layout.rounded_corners, (ViewGroup)null);
        this.mCutoutViews[n] = new DisplayCutoutView(super.mContext, n, this);
        ((ViewGroup)this.mOverlays[n]).addView((View)this.mCutoutViews[n]);
        this.mOverlays[n].setSystemUiVisibility(256);
        this.mOverlays[n].setAlpha(0.0f);
        this.mOverlays[n].setForceDarkAllowed(false);
        this.updateView(n);
        this.mWindowManager.addView(this.mOverlays[n], (ViewGroup$LayoutParams)this.getWindowLayoutParams(n));
        this.mOverlays[n].addOnLayoutChangeListener((View$OnLayoutChangeListener)new View$OnLayoutChangeListener() {
            public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
                ScreenDecorations.this.mOverlays[n].removeOnLayoutChangeListener((View$OnLayoutChangeListener)this);
                ScreenDecorations.this.mOverlays[n].animate().alpha(1.0f).setDuration(1000L).start();
            }
        });
        this.mOverlays[n].getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)new ValidatingPreDrawListener(this.mOverlays[n]));
    }
    
    private static int getBoundPositionFromRotation(int n, int n2) {
        n2 = (n -= n2);
        if (n2 < 0) {
            n = n2 + 4;
        }
        return n;
    }
    
    private int getHeightLayoutParamByPos(int boundPositionFromRotation) {
        boundPositionFromRotation = getBoundPositionFromRotation(boundPositionFromRotation, this.mRotation);
        if (boundPositionFromRotation != 1 && boundPositionFromRotation != 3) {
            boundPositionFromRotation = -1;
        }
        else {
            boundPositionFromRotation = -2;
        }
        return boundPositionFromRotation;
    }
    
    private int getOverlayWindowGravity(final int i) {
        final int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        if (boundPositionFromRotation == 0) {
            return 3;
        }
        if (boundPositionFromRotation == 1) {
            return 48;
        }
        if (boundPositionFromRotation == 2) {
            return 5;
        }
        if (boundPositionFromRotation == 3) {
            return 80;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("unknown bound position: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private int getRoundedCornerGravity(int n, final boolean b) {
        final int boundPositionFromRotation = getBoundPositionFromRotation(n, this.mRotation);
        n = 51;
        final int n2 = 83;
        if (boundPositionFromRotation == 0) {
            if (!b) {
                n = 83;
            }
            return n;
        }
        final int n3 = 53;
        if (boundPositionFromRotation == 1) {
            if (!b) {
                n = 53;
            }
            return n;
        }
        if (boundPositionFromRotation == 2) {
            if (b) {
                n = n3;
            }
            else {
                n = 85;
            }
            return n;
        }
        if (boundPositionFromRotation == 3) {
            if (b) {
                n = n2;
            }
            else {
                n = 85;
            }
            return n;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Incorrect position: ");
        sb.append(boundPositionFromRotation);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private int getRoundedCornerRotation(final int i) {
        if (i == 51) {
            return 0;
        }
        if (i == 53) {
            return 90;
        }
        if (i == 83) {
            return 270;
        }
        if (i == 85) {
            return 180;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unsupported gravity: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private int getWidthLayoutParamByPos(int boundPositionFromRotation) {
        boundPositionFromRotation = getBoundPositionFromRotation(boundPositionFromRotation, this.mRotation);
        if (boundPositionFromRotation != 1 && boundPositionFromRotation != 3) {
            boundPositionFromRotation = -2;
        }
        else {
            boundPositionFromRotation = -1;
        }
        return boundPositionFromRotation;
    }
    
    private static String getWindowTitleByPos(final int i) {
        if (i == 0) {
            return "ScreenDecorOverlayLeft";
        }
        if (i == 1) {
            return "ScreenDecorOverlay";
        }
        if (i == 2) {
            return "ScreenDecorOverlayRight";
        }
        if (i == 3) {
            return "ScreenDecorOverlayBottom";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("unknown bound position: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private boolean hasRoundedCorners() {
        return this.mRoundedDefault > 0 || this.mRoundedDefaultBottom > 0 || this.mRoundedDefaultTop > 0 || this.mIsRoundedCornerMultipleRadius;
    }
    
    public static Region rectsToRegion(final List<Rect> list) {
        final Region obtain = Region.obtain();
        if (list != null) {
            for (final Rect rect : list) {
                if (rect != null && !rect.isEmpty()) {
                    obtain.op(rect, Region$Op.UNION);
                }
            }
        }
        return obtain;
    }
    
    private void removeAllOverlays() {
        if (this.mOverlays == null) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            if (this.mOverlays[i] != null) {
                this.removeOverlay(i);
            }
        }
        this.mOverlays = null;
    }
    
    private void removeOverlay(final int n) {
        final View[] mOverlays = this.mOverlays;
        if (mOverlays != null) {
            if (mOverlays[n] != null) {
                this.mWindowManager.removeViewImmediate(mOverlays[n]);
                this.mOverlays[n] = null;
            }
        }
    }
    
    private void setupCameraListener() {
        if (super.mContext.getResources().getBoolean(R$bool.config_enableDisplayCutoutProtection)) {
            final CameraAvailabilityListener.Factory factory = CameraAvailabilityListener.Factory;
            final Context mContext = super.mContext;
            final Handler mHandler = this.mHandler;
            Objects.requireNonNull(mHandler);
            (this.mCameraListener = factory.build(mContext, new _$$Lambda$LfzJt661qZfn2w_6SYHFbD3aMy0(mHandler))).addTransitionCallback(this.mCameraTransitionCallback);
            this.mCameraListener.startListening();
        }
    }
    
    private void setupDecorations() {
        if (!this.hasRoundedCorners() && !this.shouldDrawCutout()) {
            this.removeAllOverlays();
        }
        else {
            final DisplayCutout cutout = this.getCutout();
            Rect[] boundingRectsAll;
            if (cutout == null) {
                boundingRectsAll = null;
            }
            else {
                boundingRectsAll = cutout.getBoundingRectsAll();
            }
            for (int i = 0; i < 4; ++i) {
                final int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
                if ((boundingRectsAll != null && !boundingRectsAll[boundPositionFromRotation].isEmpty()) || this.shouldShowRoundedCorner(i)) {
                    this.createOverlay(i);
                }
                else {
                    this.removeOverlay(i);
                }
            }
        }
        if (this.hasOverlays()) {
            if (this.mIsRegistered) {
                return;
            }
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            this.mDisplayManager.getDisplay(0).getMetrics(displayMetrics);
            this.mDensity = displayMetrics.density;
            this.mMainHandler.post((Runnable)new _$$Lambda$ScreenDecorations$ItnW8ZEHeCqCHue6f8abcXewifU(this));
            if (this.mColorInversionSetting == null) {
                this.mColorInversionSetting = new SecureSetting(super.mContext, this.mHandler, "accessibility_display_inversion_enabled") {
                    @Override
                    protected void handleValueChanged(final int n, final boolean b) {
                        ScreenDecorations.this.updateColorInversion(n);
                    }
                };
            }
            this.mColorInversionSetting.setListening(true);
            this.mColorInversionSetting.onChange(false);
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            this.mBroadcastDispatcher.registerReceiverWithHandler(this.mIntentReceiver, intentFilter, this.mHandler);
            this.mIsRegistered = true;
        }
        else {
            this.mMainHandler.post((Runnable)new _$$Lambda$ScreenDecorations$CTk_RNSSvwUoNV8CfAa6W3y0c0A(this));
            final SecureSetting mColorInversionSetting = this.mColorInversionSetting;
            if (mColorInversionSetting != null) {
                mColorInversionSetting.setListening(false);
            }
            this.mBroadcastDispatcher.unregisterReceiver(this.mIntentReceiver);
            this.mIsRegistered = false;
        }
    }
    
    private boolean shouldDrawCutout() {
        return shouldDrawCutout(super.mContext);
    }
    
    static boolean shouldDrawCutout(final Context context) {
        return context.getResources().getBoolean(17891460);
    }
    
    private boolean shouldShowRoundedCorner(final int n) {
        final boolean hasRoundedCorners = this.hasRoundedCorners();
        final boolean b = false;
        boolean b2 = false;
        if (!hasRoundedCorners) {
            return false;
        }
        final DisplayCutout cutout = this.getCutout();
        final boolean b3 = cutout == null || cutout.isBoundsEmpty();
        final int boundPositionFromRotation = getBoundPositionFromRotation(1, this.mRotation);
        final int boundPositionFromRotation2 = getBoundPositionFromRotation(3, this.mRotation);
        if (!b3 && cutout.getBoundingRectsAll()[boundPositionFromRotation].isEmpty() && cutout.getBoundingRectsAll()[boundPositionFromRotation2].isEmpty()) {
            if (n == 0 || n == 2) {
                b2 = true;
            }
            return b2;
        }
        if (n != 1) {
            final boolean b4 = b;
            if (n != 3) {
                return b4;
            }
        }
        return true;
    }
    
    private void startOnScreenDecorationsThread() {
        this.mRotation = super.mContext.getDisplay().getRotation();
        this.mWindowManager = (WindowManager)super.mContext.getSystemService((Class)WindowManager.class);
        this.mDisplayManager = (DisplayManager)super.mContext.getSystemService((Class)DisplayManager.class);
        this.mIsRoundedCornerMultipleRadius = super.mContext.getResources().getBoolean(R$bool.config_roundedCornerMultipleRadius);
        this.updateRoundedCornerRadii();
        this.setupDecorations();
        this.setupCameraListener();
        final DisplayManager$DisplayListener mDisplayListener = (DisplayManager$DisplayListener)new DisplayManager$DisplayListener() {
            public void onDisplayAdded(final int n) {
            }
            
            public void onDisplayChanged(int i) {
                final int rotation = ScreenDecorations.this.mContext.getDisplay().getRotation();
                final ScreenDecorations this$0 = ScreenDecorations.this;
                if (this$0.mOverlays != null && this$0.mRotation != rotation) {
                    ScreenDecorations.this.mPendingRotationChange = true;
                    View[] mOverlays;
                    ViewTreeObserver viewTreeObserver;
                    ScreenDecorations this$2;
                    for (i = 0; i < 4; ++i) {
                        mOverlays = ScreenDecorations.this.mOverlays;
                        if (mOverlays[i] != null) {
                            viewTreeObserver = mOverlays[i].getViewTreeObserver();
                            this$2 = ScreenDecorations.this;
                            viewTreeObserver.addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)new RestartingPreDrawListener(this$2.mOverlays[i], i, rotation));
                        }
                    }
                }
                ScreenDecorations.this.updateOrientation();
            }
            
            public void onDisplayRemoved(final int n) {
            }
        };
        this.mDisplayListener = (DisplayManager$DisplayListener)mDisplayListener;
        this.mDisplayManager.registerDisplayListener((DisplayManager$DisplayListener)mDisplayListener, this.mHandler);
        this.updateOrientation();
    }
    
    private void updateColorInversion(int color) {
        if (color != 0) {
            color = -1;
        }
        else {
            color = -16777216;
        }
        if (ScreenDecorations.DEBUG_COLOR) {
            color = -65536;
        }
        final ColorStateList value = ColorStateList.valueOf(color);
        if (this.mOverlays == null) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            final View[] mOverlays = this.mOverlays;
            if (mOverlays[i] != null) {
                for (int childCount = ((ViewGroup)mOverlays[i]).getChildCount(), j = 0; j < childCount; ++j) {
                    final View child = ((ViewGroup)this.mOverlays[i]).getChildAt(j);
                    if (child instanceof ImageView) {
                        ((ImageView)child).setImageTintList(value);
                    }
                    else if (child instanceof DisplayCutoutView) {
                        ((DisplayCutoutView)child).setColor(color);
                    }
                }
            }
        }
    }
    
    private void updateLayoutParams() {
        if (this.mOverlays == null) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            final View[] mOverlays = this.mOverlays;
            if (mOverlays[i] != null) {
                this.mWindowManager.updateViewLayout(mOverlays[i], (ViewGroup$LayoutParams)this.getWindowLayoutParams(i));
            }
        }
    }
    
    private void updateOrientation() {
        final Thread thread = this.mHandler.getLooper().getThread();
        final Thread currentThread = Thread.currentThread();
        int i = 0;
        final boolean b = thread == currentThread;
        final StringBuilder sb = new StringBuilder();
        sb.append("must call on ");
        sb.append(this.mHandler.getLooper().getThread());
        sb.append(", but was ");
        sb.append(Thread.currentThread());
        Preconditions.checkState(b, sb.toString());
        if (this.mPendingRotationChange) {
            return;
        }
        final int rotation = super.mContext.getDisplay().getRotation();
        if (rotation != this.mRotation) {
            this.mRotation = rotation;
            if (this.mOverlays != null) {
                this.updateLayoutParams();
                while (i < 4) {
                    if (this.mOverlays[i] != null) {
                        this.updateView(i);
                    }
                    ++i;
                }
            }
        }
    }
    
    private void updateRoundedCornerRadii() {
        final int dimensionPixelSize = super.mContext.getResources().getDimensionPixelSize(17105445);
        final int dimensionPixelSize2 = super.mContext.getResources().getDimensionPixelSize(17105449);
        final int dimensionPixelSize3 = super.mContext.getResources().getDimensionPixelSize(17105447);
        if (this.mRoundedDefault != dimensionPixelSize || this.mRoundedDefaultBottom != dimensionPixelSize3 || this.mRoundedDefaultTop != dimensionPixelSize2) {
            if (this.mIsRoundedCornerMultipleRadius) {
                final VectorDrawable vectorDrawable = (VectorDrawable)super.mContext.getDrawable(R$drawable.rounded);
                final int max = Math.max(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
                this.mRoundedDefault = max;
                this.mRoundedDefaultBottom = max;
                this.mRoundedDefaultTop = max;
            }
            else {
                this.mRoundedDefault = dimensionPixelSize;
                this.mRoundedDefaultTop = dimensionPixelSize2;
                this.mRoundedDefaultBottom = dimensionPixelSize3;
            }
            this.onTuningChanged("sysui_rounded_size", null);
        }
    }
    
    private void updateRoundedCornerSize(int n, int i, final int n2) {
        if (this.mOverlays == null) {
            return;
        }
        int n3;
        if ((n3 = i) == 0) {
            n3 = n;
        }
        if (n2 != 0) {
            n = n2;
        }
        View[] mOverlays;
        for (i = 0; i < 4; ++i) {
            mOverlays = this.mOverlays;
            if (mOverlays[i] != null) {
                if (i != 0 && i != 2) {
                    if (i == 1) {
                        this.setSize(mOverlays[i].findViewById(R$id.left), n3);
                        this.setSize(this.mOverlays[i].findViewById(R$id.right), n3);
                    }
                    else if (i == 3) {
                        this.setSize(mOverlays[i].findViewById(R$id.left), n);
                        this.setSize(this.mOverlays[i].findViewById(R$id.right), n);
                    }
                }
                else if (this.mRotation == 3) {
                    this.setSize(this.mOverlays[i].findViewById(R$id.left), n);
                    this.setSize(this.mOverlays[i].findViewById(R$id.right), n3);
                }
                else {
                    this.setSize(this.mOverlays[i].findViewById(R$id.left), n3);
                    this.setSize(this.mOverlays[i].findViewById(R$id.right), n);
                }
            }
        }
    }
    
    private void updateRoundedCornerView(int roundedCornerGravity, final int n) {
        final View viewById = this.mOverlays[roundedCornerGravity].findViewById(n);
        if (viewById == null) {
            return;
        }
        viewById.setVisibility(8);
        if (this.shouldShowRoundedCorner(roundedCornerGravity)) {
            roundedCornerGravity = this.getRoundedCornerGravity(roundedCornerGravity, n == R$id.left);
            ((FrameLayout$LayoutParams)viewById.getLayoutParams()).gravity = roundedCornerGravity;
            viewById.setRotation((float)this.getRoundedCornerRotation(roundedCornerGravity));
            viewById.setVisibility(0);
        }
    }
    
    private void updateView(final int n) {
        final View[] mOverlays = this.mOverlays;
        if (mOverlays != null) {
            if (mOverlays[n] != null) {
                this.updateRoundedCornerView(n, R$id.left);
                this.updateRoundedCornerView(n, R$id.right);
                this.updateRoundedCornerSize(this.mRoundedDefault, this.mRoundedDefaultTop, this.mRoundedDefaultBottom);
                final DisplayCutoutView[] mCutoutViews = this.mCutoutViews;
                if (mCutoutViews != null && mCutoutViews[n] != null) {
                    mCutoutViews[n].setRotation(this.mRotation);
                }
            }
        }
    }
    
    DisplayCutout getCutout() {
        return super.mContext.getDisplay().getCutout();
    }
    
    WindowManager$LayoutParams getWindowLayoutParams(final int n) {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(this.getWidthLayoutParamByPos(n), this.getHeightLayoutParamByPos(n), 2024, 545259816, -3);
        final int privateFlags = windowManager$LayoutParams.privateFlags | 0x50;
        windowManager$LayoutParams.privateFlags = privateFlags;
        if (!ScreenDecorations.DEBUG_SCREENSHOT_ROUNDED_CORNERS) {
            windowManager$LayoutParams.privateFlags = (privateFlags | 0x100000);
        }
        windowManager$LayoutParams.setTitle((CharSequence)getWindowTitleByPos(n));
        windowManager$LayoutParams.gravity = this.getOverlayWindowGravity(n);
        windowManager$LayoutParams.layoutInDisplayCutoutMode = 3;
        windowManager$LayoutParams.setFitInsetsTypes(0);
        windowManager$LayoutParams.privateFlags |= 0x1000000;
        return windowManager$LayoutParams;
    }
    
    boolean hasOverlays() {
        if (this.mOverlays == null) {
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            if (this.mOverlays[i] != null) {
                return true;
            }
        }
        this.mOverlays = null;
        return false;
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        this.mHandler.post((Runnable)new _$$Lambda$ScreenDecorations$OAK9dGZ7oEZcFQ_E6ZLqmPf58XA(this));
    }
    
    @Override
    public void onTuningChanged(final String s, final String s2) {
        this.mHandler.post((Runnable)new _$$Lambda$ScreenDecorations$dMR9XiqMPUNaTuEQSFDim2iw7AM(this, s, s2));
    }
    
    protected void setSize(final View view, final int n) {
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = n;
        layoutParams.height = n;
        view.setLayoutParams(layoutParams);
    }
    
    @Override
    public void start() {
        (this.mHandler = this.startHandlerThread()).post((Runnable)new _$$Lambda$ScreenDecorations$IfAux2ksmJXT9o9i38WaSEQXJTQ(this));
    }
    
    Handler startHandlerThread() {
        final HandlerThread handlerThread = new HandlerThread("ScreenDecorations");
        handlerThread.start();
        return handlerThread.getThreadHandler();
    }
    
    public static class DisplayCutoutView extends View implements DisplayManager$DisplayListener, RegionInterceptableView
    {
        private final Path mBoundingPath;
        private final Rect mBoundingRect;
        private final List<Rect> mBounds;
        private ValueAnimator mCameraProtectionAnimator;
        private float mCameraProtectionProgress;
        private int mColor;
        private final ScreenDecorations mDecorations;
        private final DisplayInfo mInfo;
        private int mInitialPosition;
        private final int[] mLocation;
        private final Paint mPaint;
        private int mPosition;
        private Path mProtectionPath;
        private Path mProtectionPathOrig;
        private RectF mProtectionRect;
        private RectF mProtectionRectOrig;
        private int mRotation;
        private boolean mShowProtection;
        private Rect mTotalBounds;
        
        public DisplayCutoutView(final Context context, final int mInitialPosition, final ScreenDecorations mDecorations) {
            super(context);
            this.mInfo = new DisplayInfo();
            this.mPaint = new Paint();
            this.mBounds = new ArrayList<Rect>();
            this.mBoundingRect = new Rect();
            this.mBoundingPath = new Path();
            this.mTotalBounds = new Rect();
            this.mShowProtection = false;
            this.mLocation = new int[2];
            this.mColor = -16777216;
            this.mCameraProtectionProgress = 0.5f;
            this.mInitialPosition = mInitialPosition;
            this.mDecorations = mDecorations;
            this.setId(R$id.display_cutout);
        }
        
        public static void boundsFromDirection(final DisplayCutout displayCutout, final int n, final Rect rect) {
            if (n != 3) {
                if (n != 5) {
                    if (n != 48) {
                        if (n != 80) {
                            rect.setEmpty();
                        }
                        else {
                            rect.set(displayCutout.getBoundingRectBottom());
                        }
                    }
                    else {
                        rect.set(displayCutout.getBoundingRectTop());
                    }
                }
                else {
                    rect.set(displayCutout.getBoundingRectRight());
                }
            }
            else {
                rect.set(displayCutout.getBoundingRectLeft());
            }
        }
        
        private int getGravity(final DisplayCutout displayCutout) {
            final int mPosition = this.mPosition;
            if (mPosition == 0) {
                if (!displayCutout.getBoundingRectLeft().isEmpty()) {
                    return 3;
                }
            }
            else if (mPosition == 1) {
                if (!displayCutout.getBoundingRectTop().isEmpty()) {
                    return 48;
                }
            }
            else if (mPosition == 3) {
                if (!displayCutout.getBoundingRectBottom().isEmpty()) {
                    return 80;
                }
            }
            else if (mPosition == 2 && !displayCutout.getBoundingRectRight().isEmpty()) {
                return 5;
            }
            return 0;
        }
        
        private boolean hasCutout() {
            final DisplayCutout displayCutout = this.mInfo.displayCutout;
            if (displayCutout == null) {
                return false;
            }
            final int mPosition = this.mPosition;
            if (mPosition == 0) {
                return displayCutout.getBoundingRectLeft().isEmpty() ^ true;
            }
            if (mPosition == 1) {
                return displayCutout.getBoundingRectTop().isEmpty() ^ true;
            }
            if (mPosition == 3) {
                return displayCutout.getBoundingRectBottom().isEmpty() ^ true;
            }
            return mPosition == 2 && (displayCutout.getBoundingRectRight().isEmpty() ^ true);
        }
        
        private void localBounds(final Rect rect) {
            final DisplayCutout displayCutout = this.mInfo.displayCutout;
            boundsFromDirection(displayCutout, this.getGravity(displayCutout), rect);
        }
        
        private static void transformPhysicalToLogicalCoordinates(final int i, final int n, final int n2, final Matrix matrix) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 3) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Unknown rotation: ");
                            sb.append(i);
                            throw new IllegalArgumentException(sb.toString());
                        }
                        matrix.setRotate(90.0f);
                        matrix.postTranslate((float)n2, 0.0f);
                    }
                    else {
                        matrix.setRotate(180.0f);
                        matrix.postTranslate((float)n, (float)n2);
                    }
                }
                else {
                    matrix.setRotate(270.0f);
                    matrix.postTranslate(0.0f, (float)n);
                }
            }
            else {
                matrix.reset();
            }
        }
        
        private void update() {
            if (this.isAttachedToWindow()) {
                if (!this.mDecorations.mPendingRotationChange) {
                    this.mPosition = getBoundPositionFromRotation(this.mInitialPosition, this.mRotation);
                    this.requestLayout();
                    this.getDisplay().getDisplayInfo(this.mInfo);
                    this.mBounds.clear();
                    this.mBoundingRect.setEmpty();
                    this.mBoundingPath.reset();
                    int visibility;
                    if (ScreenDecorations.shouldDrawCutout(this.getContext()) && this.hasCutout()) {
                        this.mBounds.addAll(this.mInfo.displayCutout.getBoundingRects());
                        this.localBounds(this.mBoundingRect);
                        this.updateGravity();
                        this.updateBoundingPath();
                        this.invalidate();
                        visibility = 0;
                    }
                    else {
                        visibility = 8;
                    }
                    if (visibility != this.getVisibility()) {
                        this.setVisibility(visibility);
                    }
                }
            }
        }
        
        private void updateBoundingPath() {
            final DisplayInfo mInfo = this.mInfo;
            final int logicalWidth = mInfo.logicalWidth;
            int logicalHeight = mInfo.logicalHeight;
            final int rotation = mInfo.rotation;
            int n2;
            final int n = n2 = 1;
            if (rotation != 1) {
                if (rotation == 3) {
                    n2 = n;
                }
                else {
                    n2 = 0;
                }
            }
            int n3;
            if (n2 != 0) {
                n3 = logicalHeight;
            }
            else {
                n3 = logicalWidth;
            }
            if (n2 != 0) {
                logicalHeight = logicalWidth;
            }
            this.mBoundingPath.set(DisplayCutout.pathFromResources(this.getResources(), n3, logicalHeight));
            final Matrix matrix = new Matrix();
            transformPhysicalToLogicalCoordinates(this.mInfo.rotation, n3, logicalHeight, matrix);
            this.mBoundingPath.transform(matrix);
            final Path mProtectionPathOrig = this.mProtectionPathOrig;
            if (mProtectionPathOrig != null) {
                this.mProtectionPath.set(mProtectionPathOrig);
                this.mProtectionPath.transform(matrix);
                matrix.mapRect(this.mProtectionRect, this.mProtectionRectOrig);
            }
        }
        
        private void updateGravity() {
            final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
            if (layoutParams instanceof FrameLayout$LayoutParams) {
                final FrameLayout$LayoutParams layoutParams2 = (FrameLayout$LayoutParams)layoutParams;
                final int gravity = this.getGravity(this.mInfo.displayCutout);
                if (layoutParams2.gravity != gravity) {
                    layoutParams2.gravity = gravity;
                    this.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                }
            }
        }
        
        public Region getInterceptRegion() {
            if (this.mInfo.displayCutout == null) {
                return null;
            }
            final View rootView = this.getRootView();
            final Region rectsToRegion = ScreenDecorations.rectsToRegion(this.mInfo.displayCutout.getBoundingRects());
            rootView.getLocationOnScreen(this.mLocation);
            final int[] mLocation = this.mLocation;
            rectsToRegion.translate(-mLocation[0], -mLocation[1]);
            rectsToRegion.op(rootView.getLeft(), rootView.getTop(), rootView.getRight(), rootView.getBottom(), Region$Op.INTERSECT);
            return rectsToRegion;
        }
        
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ((DisplayManager)super.mContext.getSystemService((Class)DisplayManager.class)).registerDisplayListener((DisplayManager$DisplayListener)this, this.getHandler());
            this.update();
        }
        
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ((DisplayManager)super.mContext.getSystemService((Class)DisplayManager.class)).unregisterDisplayListener((DisplayManager$DisplayListener)this);
        }
        
        public void onDisplayAdded(final int n) {
        }
        
        public void onDisplayChanged(final int n) {
            if (n == this.getDisplay().getDisplayId()) {
                this.update();
            }
        }
        
        public void onDisplayRemoved(final int n) {
        }
        
        protected void onDraw(final Canvas canvas) {
            super.onDraw(canvas);
            this.getLocationOnScreen(this.mLocation);
            final int[] mLocation = this.mLocation;
            canvas.translate((float)(-mLocation[0]), (float)(-mLocation[1]));
            if (!this.mBoundingPath.isEmpty()) {
                this.mPaint.setColor(this.mColor);
                this.mPaint.setStyle(Paint$Style.FILL);
                this.mPaint.setAntiAlias(true);
                canvas.drawPath(this.mBoundingPath, this.mPaint);
            }
            if (this.mCameraProtectionProgress > 0.5f && !this.mProtectionRect.isEmpty()) {
                final float mCameraProtectionProgress = this.mCameraProtectionProgress;
                canvas.scale(mCameraProtectionProgress, mCameraProtectionProgress, this.mProtectionRect.centerX(), this.mProtectionRect.centerY());
                canvas.drawPath(this.mProtectionPath, this.mPaint);
            }
        }
        
        protected void onMeasure(final int n, final int n2) {
            if (this.mBounds.isEmpty()) {
                super.onMeasure(n, n2);
                return;
            }
            if (this.mShowProtection) {
                this.mTotalBounds.union(this.mBoundingRect);
                final Rect mTotalBounds = this.mTotalBounds;
                final RectF mProtectionRect = this.mProtectionRect;
                mTotalBounds.union((int)mProtectionRect.left, (int)mProtectionRect.top, (int)mProtectionRect.right, (int)mProtectionRect.bottom);
                this.setMeasuredDimension(View.resolveSizeAndState(this.mTotalBounds.width(), n, 0), View.resolveSizeAndState(this.mTotalBounds.height(), n2, 0));
            }
            else {
                this.setMeasuredDimension(View.resolveSizeAndState(this.mBoundingRect.width(), n, 0), View.resolveSizeAndState(this.mBoundingRect.height(), n2, 0));
            }
        }
        
        public void setColor(final int mColor) {
            this.mColor = mColor;
            this.invalidate();
        }
        
        void setProtection(final Path path, final Rect rect) {
            if (this.mProtectionPathOrig == null) {
                this.mProtectionPathOrig = new Path();
                this.mProtectionPath = new Path();
            }
            this.mProtectionPathOrig.set(path);
            if (this.mProtectionRectOrig == null) {
                this.mProtectionRectOrig = new RectF();
                this.mProtectionRect = new RectF();
            }
            this.mProtectionRectOrig.set(rect);
        }
        
        public void setRotation(final int mRotation) {
            this.mRotation = mRotation;
            this.update();
        }
        
        void setShowProtection(final boolean mShowProtection) {
            if (this.mShowProtection == mShowProtection) {
                return;
            }
            this.mShowProtection = mShowProtection;
            this.updateBoundingPath();
            if (this.mShowProtection) {
                this.requestLayout();
            }
            final ValueAnimator mCameraProtectionAnimator = this.mCameraProtectionAnimator;
            if (mCameraProtectionAnimator != null) {
                mCameraProtectionAnimator.cancel();
            }
            final float mCameraProtectionProgress = this.mCameraProtectionProgress;
            float n;
            if (this.mShowProtection) {
                n = 1.0f;
            }
            else {
                n = 0.5f;
            }
            (this.mCameraProtectionAnimator = ValueAnimator.ofFloat(new float[] { mCameraProtectionProgress, n }).setDuration(750L)).setInterpolator((TimeInterpolator)Interpolators.DECELERATE_QUINT);
            this.mCameraProtectionAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ScreenDecorations$DisplayCutoutView$f2Iwcv56BgbHyCi4FYuzR2s0HB4(this));
            this.mCameraProtectionAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    DisplayCutoutView.this.mCameraProtectionAnimator = null;
                    if (!DisplayCutoutView.this.mShowProtection) {
                        DisplayCutoutView.this.requestLayout();
                    }
                }
            });
            this.mCameraProtectionAnimator.start();
        }
        
        public boolean shouldInterceptTouch() {
            return this.mInfo.displayCutout != null && this.getVisibility() == 0;
        }
    }
    
    private class RestartingPreDrawListener implements ViewTreeObserver$OnPreDrawListener
    {
        private final int mTargetRotation;
        private final View mView;
        
        private RestartingPreDrawListener(final View mView, final int n, final int mTargetRotation) {
            this.mView = mView;
            this.mTargetRotation = mTargetRotation;
        }
        
        public boolean onPreDraw() {
            this.mView.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
            if (this.mTargetRotation == ScreenDecorations.this.mRotation) {
                return true;
            }
            ScreenDecorations.this.mPendingRotationChange = false;
            ScreenDecorations.this.updateOrientation();
            this.mView.invalidate();
            return false;
        }
    }
    
    private class ValidatingPreDrawListener implements ViewTreeObserver$OnPreDrawListener
    {
        private final View mView;
        
        public ValidatingPreDrawListener(final View mView) {
            this.mView = mView;
        }
        
        public boolean onPreDraw() {
            if (ScreenDecorations.this.mContext.getDisplay().getRotation() != ScreenDecorations.this.mRotation && !ScreenDecorations.this.mPendingRotationChange) {
                this.mView.invalidate();
                return false;
            }
            return true;
        }
    }
}
