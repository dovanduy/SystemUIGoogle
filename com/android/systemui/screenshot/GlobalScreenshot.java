// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import java.util.Iterator;
import android.app.PendingIntent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import android.util.Slog;
import android.os.UserHandle;
import android.app.ActivityOptions;
import java.util.concurrent.TimeUnit;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.content.Intent;
import dagger.Lazy;
import java.util.Optional;
import com.android.systemui.statusbar.phone.StatusBar;
import android.content.BroadcastReceiver;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View$OnTouchListener;
import android.graphics.Region$Op;
import android.graphics.Region;
import android.view.ViewTreeObserver$InternalInsetsInfo;
import android.graphics.Insets;
import android.view.SurfaceControl;
import android.view.ViewGroup$LayoutParams;
import com.android.systemui.R$string;
import android.os.PowerManager;
import android.util.MathUtils;
import android.app.PendingIntent$CanceledException;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;
import android.animation.TimeInterpolator;
import android.animation.AnimatorSet;
import android.os.RemoteException;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.drawable.Icon;
import com.android.systemui.R$drawable;
import android.widget.Toast;
import android.provider.DeviceConfig;
import android.app.ActivityManager;
import android.graphics.Paint;
import android.util.Log;
import android.app.Notification$Action;
import android.animation.ValueAnimator;
import java.util.List;
import android.net.Uri;
import java.util.function.Consumer;
import android.view.animation.AnimationUtils;
import com.android.systemui.R$dimen;
import android.view.View$OnClickListener;
import android.graphics.Rect;
import android.graphics.Outline;
import android.view.ViewOutlineProvider;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.os.Message;
import android.os.Looper;
import android.view.LayoutInflater;
import android.content.res.Resources;
import android.view.WindowManager;
import android.view.WindowManager$LayoutParams;
import android.view.View;
import android.os.Handler;
import android.animation.Animator;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.animation.Interpolator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;
import android.content.Context;
import android.media.MediaActionSound;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.view.ViewTreeObserver$OnComputeInternalInsetsListener;

public class GlobalScreenshot implements ViewTreeObserver$OnComputeInternalInsetsListener
{
    private final HorizontalScrollView mActionsContainer;
    private final LinearLayout mActionsView;
    private final ImageView mBackgroundProtection;
    private final ImageView mBackgroundView;
    private MediaActionSound mCameraSound;
    private final Context mContext;
    private float mCornerSizeX;
    private final FrameLayout mDismissButton;
    private float mDismissButtonSize;
    private final Display mDisplay;
    private final DisplayMetrics mDisplayMetrics;
    private final Interpolator mFastOutSlowIn;
    private final ScreenshotNotificationsController mNotificationsController;
    private AsyncTask<Void, Void, Void> mSaveInBgTask;
    private Bitmap mScreenBitmap;
    private Animator mScreenshotAnimation;
    private final ImageView mScreenshotFlash;
    private final Handler mScreenshotHandler;
    private float mScreenshotHeightPx;
    private final View mScreenshotLayout;
    private float mScreenshotOffsetXPx;
    private float mScreenshotOffsetYPx;
    private final ScreenshotSelectorView mScreenshotSelectorView;
    private final ImageView mScreenshotView;
    private final WindowManager$LayoutParams mWindowLayoutParams;
    private final WindowManager mWindowManager;
    
    public GlobalScreenshot(final Context mContext, final Resources resources, final LayoutInflater layoutInflater, final ScreenshotNotificationsController mNotificationsController) {
        this.mScreenshotHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(final Message message) {
                if (message.what == 2) {
                    GlobalScreenshot.this.clearScreenshot("timeout");
                }
            }
        };
        this.mContext = mContext;
        this.mNotificationsController = mNotificationsController;
        final View inflate = layoutInflater.inflate(R$layout.global_screenshot, (ViewGroup)null);
        this.mScreenshotLayout = inflate;
        this.mBackgroundView = (ImageView)inflate.findViewById(R$id.global_screenshot_background);
        (this.mScreenshotView = (ImageView)this.mScreenshotLayout.findViewById(R$id.global_screenshot)).setClipToOutline(true);
        this.mScreenshotView.setOutlineProvider((ViewOutlineProvider)new ViewOutlineProvider(this) {
            public void getOutline(final View view, final Outline outline) {
                outline.setRoundRect(new Rect(0, 0, view.getWidth(), view.getHeight()), view.getWidth() * 0.05f);
            }
        });
        this.mActionsContainer = (HorizontalScrollView)this.mScreenshotLayout.findViewById(R$id.global_screenshot_actions_container);
        this.mActionsView = (LinearLayout)this.mScreenshotLayout.findViewById(R$id.global_screenshot_actions);
        this.mBackgroundProtection = (ImageView)this.mScreenshotLayout.findViewById(R$id.global_screenshot_actions_background);
        (this.mDismissButton = (FrameLayout)this.mScreenshotLayout.findViewById(R$id.global_screenshot_dismiss_button)).setOnClickListener((View$OnClickListener)new _$$Lambda$GlobalScreenshot$cGB4E4NIBz60_oFnRLLYn4RWLrk(this));
        this.mScreenshotFlash = (ImageView)this.mScreenshotLayout.findViewById(R$id.global_screenshot_flash);
        this.mScreenshotSelectorView = (ScreenshotSelectorView)this.mScreenshotLayout.findViewById(R$id.global_screenshot_selector);
        this.mScreenshotLayout.setFocusable(true);
        this.mScreenshotSelectorView.setFocusable(true);
        this.mScreenshotSelectorView.setFocusableInTouchMode(true);
        this.mScreenshotView.setPivotX(0.0f);
        this.mScreenshotView.setPivotY(0.0f);
        (this.mWindowLayoutParams = new WindowManager$LayoutParams(-1, -1, 0, 0, 2036, 787744, -3)).setTitle((CharSequence)"ScreenshotAnimation");
        final WindowManager$LayoutParams mWindowLayoutParams = this.mWindowLayoutParams;
        mWindowLayoutParams.layoutInDisplayCutoutMode = 3;
        mWindowLayoutParams.setFitInsetsTypes(0);
        final WindowManager mWindowManager = (WindowManager)mContext.getSystemService("window");
        this.mWindowManager = mWindowManager;
        this.mDisplay = mWindowManager.getDefaultDisplay();
        final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        this.mDisplayMetrics = mDisplayMetrics;
        this.mDisplay.getRealMetrics(mDisplayMetrics);
        this.mScreenshotOffsetXPx = (float)resources.getDimensionPixelSize(R$dimen.screenshot_offset_x);
        this.mScreenshotOffsetYPx = (float)resources.getDimensionPixelSize(R$dimen.screenshot_offset_y);
        this.mScreenshotHeightPx = (float)resources.getDimensionPixelSize(R$dimen.screenshot_action_container_offset_y);
        this.mDismissButtonSize = (float)resources.getDimensionPixelSize(R$dimen.screenshot_dismiss_button_tappable_size);
        this.mCornerSizeX = (float)resources.getDimensionPixelSize(R$dimen.global_screenshot_x_scale);
        this.mFastOutSlowIn = AnimationUtils.loadInterpolator(this.mContext, 17563661);
        (this.mCameraSound = new MediaActionSound()).load(0);
    }
    
    private void clearScreenshot(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("clearing screenshot: ");
        sb.append(str);
        Log.e("GlobalScreenshot", sb.toString());
        if (this.mScreenshotLayout.isAttachedToWindow()) {
            this.mWindowManager.removeView(this.mScreenshotLayout);
        }
        this.mScreenshotHandler.removeMessages(2);
        this.mScreenshotLayout.getViewTreeObserver().removeOnComputeInternalInsetsListener((ViewTreeObserver$OnComputeInternalInsetsListener)this);
        this.mScreenshotView.setImageBitmap((Bitmap)null);
        this.mActionsContainer.setVisibility(8);
        this.mBackgroundView.setVisibility(8);
        this.mBackgroundProtection.setAlpha(0.0f);
        this.mDismissButton.setVisibility(8);
        this.mScreenshotView.setVisibility(8);
        this.mScreenshotView.setLayerType(0, (Paint)null);
    }
    
    private ValueAnimator createScreenshotActionsShadeAnimation(List<Notification$Action> o, List<Notification$Action> text) {
        final LayoutInflater from = LayoutInflater.from(this.mContext);
        this.mActionsView.removeAllViews();
        this.mActionsContainer.setScrollX(0);
        this.mScreenshotLayout.invalidate();
        this.mScreenshotLayout.requestLayout();
        this.mScreenshotLayout.getViewTreeObserver().dispatchOnGlobalLayout();
        while (true) {
            try {
                ActivityManager.getService().resumeAppSwitches();
                o = ((List<Notification$Action>)o).iterator();
                while (((Iterator)o).hasNext()) {
                    final Notification$Action notification$Action = ((Iterator<Notification$Action>)o).next();
                    final ScreenshotActionChip screenshotActionChip = (ScreenshotActionChip)from.inflate(R$layout.global_screenshot_action_chip, (ViewGroup)this.mActionsView, false);
                    screenshotActionChip.setText(notification$Action.title);
                    screenshotActionChip.setIcon(notification$Action.getIcon(), false);
                    screenshotActionChip.setPendingIntent(notification$Action.actionIntent, new _$$Lambda$GlobalScreenshot$chbTZdxF_jYB_2SoU2G2rY4Fx9E(this));
                    this.mActionsView.addView((View)screenshotActionChip);
                }
                o = ((List<Notification$Action>)text).iterator();
                while (((Iterator)o).hasNext()) {
                    final Notification$Action notification$Action2 = ((Iterator<Notification$Action>)o).next();
                    text = from.inflate(R$layout.global_screenshot_action_chip, (ViewGroup)this.mActionsView, false);
                    ((ScreenshotActionChip)text).setText(notification$Action2.title);
                    ((ScreenshotActionChip)text).setIcon(notification$Action2.getIcon(), true);
                    ((ScreenshotActionChip)text).setPendingIntent(notification$Action2.actionIntent, new _$$Lambda$GlobalScreenshot$U4njDgWbzNiU3QazaXsrOcSmMmA(this));
                    if (notification$Action2.actionIntent.getIntent().getAction().equals("android.intent.action.EDIT")) {
                        this.mScreenshotView.setOnClickListener((View$OnClickListener)new _$$Lambda$GlobalScreenshot$z6maCZLJ6BrEV4UDgyQ4rGUcb5k(this, notification$Action2));
                    }
                    this.mActionsView.addView((View)text);
                }
                if (DeviceConfig.getBoolean("systemui", "enable_screenshot_scrolling", false)) {
                    o = from.inflate(R$layout.global_screenshot_action_chip, (ViewGroup)this.mActionsView, false);
                    text = Toast.makeText(this.mContext, (CharSequence)"Not implemented", 0);
                    ((ScreenshotActionChip)o).setText("Extend");
                    ((ScreenshotActionChip)o).setIcon(Icon.createWithResource(this.mContext, R$drawable.ic_arrow_downward), true);
                    ((LinearLayout)o).setOnClickListener((View$OnClickListener)new _$$Lambda$GlobalScreenshot$p_p0kARe6i3l_RSpE_f1SKjMTmc((Toast)text));
                    this.mActionsView.addView((View)o);
                }
                o = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
                this.mActionsContainer.setY((float)this.mDisplayMetrics.heightPixels);
                this.mActionsContainer.setVisibility(0);
                this.mActionsContainer.measure(0, 0);
                ((ValueAnimator)o).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlobalScreenshot$KMz6ny0rUUp74nZGRj0qEzbvzaQ(this, this.mActionsContainer.getMeasuredHeight() + this.mScreenshotHeightPx));
                return (ValueAnimator)o;
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
    
    private AnimatorSet createScreenshotDropInAnimation(final int n, final int n2, final Rect rect) {
        final float mCornerSizeX = this.mCornerSizeX;
        final float n3 = (float)n;
        final float n4 = mCornerSizeX / n3;
        final AnimatorSet set = new AnimatorSet();
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.setDuration(133L);
        ofFloat.setInterpolator((TimeInterpolator)this.mFastOutSlowIn);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlobalScreenshot$vQdnfhc_MtKvi__D1jmFz2TIufY(this));
        final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[] { 1.0f, 0.0f });
        ofFloat2.setDuration(217L);
        ofFloat2.setInterpolator((TimeInterpolator)this.mFastOutSlowIn);
        ofFloat2.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlobalScreenshot$07y5e12dY3g0s8IdUzfJmDmqVdc(this));
        final PointF pointF = new PointF((float)rect.centerX(), (float)rect.centerY());
        final PointF pointF2 = new PointF(this.mScreenshotOffsetXPx + n3 * n4 / 2.0f, this.mDisplayMetrics.heightPixels - this.mScreenshotOffsetYPx - n2 * n4 / 2.0f);
        final ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat3.setDuration(500L);
        ofFloat3.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlobalScreenshot$iMnnKeE2VoCJit2kgHvxMtO653Y(this, 0.468f, n4, 0.468f, pointF, pointF2, n, n2));
        ofFloat3.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationStart(final Animator animator) {
                super.onAnimationStart(animator);
                GlobalScreenshot.this.mScreenshotView.setVisibility(0);
            }
        });
        this.mScreenshotFlash.setAlpha(0.0f);
        this.mScreenshotFlash.setVisibility(0);
        set.play((Animator)ofFloat2).after((Animator)ofFloat);
        set.play((Animator)ofFloat2).with((Animator)ofFloat3);
        set.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                super.onAnimationEnd(animator);
                GlobalScreenshot.this.mScreenshotView.setScaleX(n4);
                GlobalScreenshot.this.mScreenshotView.setScaleY(n4);
                GlobalScreenshot.this.mScreenshotView.setX(pointF2.x - n * n4 / 2.0f);
                GlobalScreenshot.this.mScreenshotView.setY(pointF2.y - n2 * n4 / 2.0f);
                final Rect rect = new Rect();
                GlobalScreenshot.this.mScreenshotView.getBoundsOnScreen(rect);
                GlobalScreenshot.this.mDismissButton.setX(rect.right - GlobalScreenshot.this.mDismissButtonSize / 2.0f);
                GlobalScreenshot.this.mDismissButton.setY(rect.top - GlobalScreenshot.this.mDismissButtonSize / 2.0f);
                GlobalScreenshot.this.mDismissButton.setVisibility(0);
            }
        });
        return set;
    }
    
    private void saveScreenshotInWorkerThread(final Consumer<Uri> finisher, final ActionsReadyListener mActionsReadyListener) {
        final SaveImageInBackgroundData saveImageInBackgroundData = new SaveImageInBackgroundData();
        saveImageInBackgroundData.image = this.mScreenBitmap;
        saveImageInBackgroundData.finisher = finisher;
        saveImageInBackgroundData.mActionsReadyListener = mActionsReadyListener;
        saveImageInBackgroundData.createDeleteAction = false;
        final AsyncTask<Void, Void, Void> mSaveInBgTask = this.mSaveInBgTask;
        if (mSaveInBgTask != null) {
            mSaveInBgTask.cancel(false);
        }
        this.mSaveInBgTask = (AsyncTask<Void, Void, Void>)new SaveImageInBackgroundTask(this.mContext, saveImageInBackgroundData).execute((Object[])new Void[0]);
    }
    
    private void startAnimation(final Consumer<Uri> consumer, final int n, final int n2, final Rect rect) {
        if (((PowerManager)this.mContext.getSystemService("power")).isPowerSaveMode()) {
            Toast.makeText(this.mContext, R$string.screenshot_saved_title, 0).show();
        }
        this.mScreenshotView.setImageBitmap(this.mScreenBitmap);
        this.mScreenshotAnimation = (Animator)this.createScreenshotDropInAnimation(n, n2, rect);
        this.saveScreenshotInWorkerThread(consumer, (ActionsReadyListener)new ActionsReadyListener() {
            @Override
            void onActionsReady(final Uri uri, final List<Notification$Action> list, final List<Notification$Action> list2) {
                if (uri == null) {
                    GlobalScreenshot.this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text);
                }
                else {
                    GlobalScreenshot.this.mScreenshotHandler.post((Runnable)new _$$Lambda$GlobalScreenshot$4$L884GhqhZpIihPJorVLyNT95uvA(this, list, list2));
                }
                GlobalScreenshot.this.mScreenshotHandler.removeMessages(2);
                GlobalScreenshot.this.mScreenshotHandler.sendMessageDelayed(GlobalScreenshot.this.mScreenshotHandler.obtainMessage(2), 6000L);
            }
        });
        this.mScreenshotHandler.post((Runnable)new _$$Lambda$GlobalScreenshot$0Vi2lyvwMUf5ppDDQu1u1pcC09g(this));
    }
    
    private void takeScreenshot(final Bitmap mScreenBitmap, final Consumer<Uri> consumer, final Rect rect) {
        this.mScreenBitmap = mScreenBitmap;
        if (mScreenBitmap == null) {
            this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text);
            consumer.accept(null);
            return;
        }
        mScreenBitmap.setHasAlpha(false);
        this.mScreenBitmap.prepareToDraw();
        this.mWindowManager.addView(this.mScreenshotLayout, (ViewGroup$LayoutParams)this.mWindowLayoutParams);
        this.mScreenshotLayout.getViewTreeObserver().addOnComputeInternalInsetsListener((ViewTreeObserver$OnComputeInternalInsetsListener)this);
        this.startAnimation(consumer, rect.width(), rect.height(), rect);
    }
    
    private void takeScreenshot(final Consumer<Uri> consumer, final Rect rect) {
        this.clearScreenshot("new screenshot requested");
        final int rotation = this.mDisplay.getRotation();
        final int width = rect.width();
        final int height = rect.height();
        final DisplayMetrics mDisplayMetrics = this.mDisplayMetrics;
        this.takeScreenshot(SurfaceControl.screenshot(rect, width, height, rotation), consumer, new Rect(0, 0, mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels));
    }
    
    void handleImageAsScreenshot(final Bitmap bitmap, final Rect rect, final Insets insets, final int n, final Consumer<Uri> consumer) {
        this.clearScreenshot("new screenshot requested");
        this.takeScreenshot(bitmap, consumer, rect);
    }
    
    public void onComputeInternalInsets(final ViewTreeObserver$InternalInsetsInfo viewTreeObserver$InternalInsetsInfo) {
        viewTreeObserver$InternalInsetsInfo.setTouchableInsets(3);
        final Region region = new Region();
        final Rect rect = new Rect();
        this.mScreenshotView.getBoundsOnScreen(rect);
        region.op(rect, Region$Op.UNION);
        final Rect rect2 = new Rect();
        this.mActionsContainer.getBoundsOnScreen(rect2);
        region.op(rect2, Region$Op.UNION);
        final Rect rect3 = new Rect();
        this.mDismissButton.getBoundsOnScreen(rect3);
        region.op(rect3, Region$Op.UNION);
        viewTreeObserver$InternalInsetsInfo.touchableRegion.set(region);
    }
    
    void stopScreenshot() {
        if (this.mScreenshotSelectorView.getSelectionRect() != null) {
            this.mWindowManager.removeView(this.mScreenshotLayout);
            this.mScreenshotSelectorView.stopSelection();
        }
    }
    
    void takeScreenshot(final Consumer<Uri> consumer) {
        this.mDisplay.getRealMetrics(this.mDisplayMetrics);
        final DisplayMetrics mDisplayMetrics = this.mDisplayMetrics;
        this.takeScreenshot(consumer, new Rect(0, 0, mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels));
    }
    
    @SuppressLint({ "ClickableViewAccessibility" })
    void takeScreenshotPartial(final Consumer<Uri> consumer) {
        this.mWindowManager.addView(this.mScreenshotLayout, (ViewGroup$LayoutParams)this.mWindowLayoutParams);
        this.mScreenshotSelectorView.setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                final ScreenshotSelectorView screenshotSelectorView = (ScreenshotSelectorView)view;
                final int action = motionEvent.getAction();
                if (action == 0) {
                    screenshotSelectorView.startSelection((int)motionEvent.getX(), (int)motionEvent.getY());
                    return true;
                }
                if (action == 1) {
                    screenshotSelectorView.setVisibility(8);
                    GlobalScreenshot.this.mWindowManager.removeView(GlobalScreenshot.this.mScreenshotLayout);
                    final Rect selectionRect = screenshotSelectorView.getSelectionRect();
                    if (selectionRect != null && selectionRect.width() != 0 && selectionRect.height() != 0) {
                        GlobalScreenshot.this.mScreenshotLayout.post((Runnable)new _$$Lambda$GlobalScreenshot$3$V4OeQi86gFHnhnLnOjIlIahyyyg(this, consumer, selectionRect));
                    }
                    screenshotSelectorView.stopSelection();
                    return true;
                }
                if (action != 2) {
                    return false;
                }
                screenshotSelectorView.updateSelection((int)motionEvent.getX(), (int)motionEvent.getY());
                return true;
            }
        });
        this.mScreenshotLayout.post((Runnable)new _$$Lambda$GlobalScreenshot$roIJK7m6E06Y4aRUfU9rrK2aTJc(this));
    }
    
    public static class ActionProxyReceiver extends BroadcastReceiver
    {
        private final StatusBar mStatusBar;
        
        public ActionProxyReceiver(final Optional<Lazy<StatusBar>> optional) {
            final StatusBar statusBar = null;
            final Lazy<StatusBar> lazy = optional.orElse(null);
            StatusBar mStatusBar = statusBar;
            if (lazy != null) {
                mStatusBar = lazy.get();
            }
            this.mStatusBar = mStatusBar;
        }
        
        public void onReceive(final Context context, final Intent intent) {
            final _$$Lambda$GlobalScreenshot$ActionProxyReceiver$tBhjeKzNYNKU1TanWTPaMXUfmOc $$Lambda$GlobalScreenshot$ActionProxyReceiver$tBhjeKzNYNKU1TanWTPaMXUfmOc = new _$$Lambda$GlobalScreenshot$ActionProxyReceiver$tBhjeKzNYNKU1TanWTPaMXUfmOc(intent, context);
            final StatusBar mStatusBar = this.mStatusBar;
            if (mStatusBar != null) {
                mStatusBar.executeRunnableDismissingKeyguard($$Lambda$GlobalScreenshot$ActionProxyReceiver$tBhjeKzNYNKU1TanWTPaMXUfmOc, null, true, true, true);
            }
            else {
                $$Lambda$GlobalScreenshot$ActionProxyReceiver$tBhjeKzNYNKU1TanWTPaMXUfmOc.run();
            }
            if (intent.getBooleanExtra("android:smart_actions_enabled", false)) {
                String s;
                if ("android.intent.action.EDIT".equals(intent.getAction())) {
                    s = "Edit";
                }
                else {
                    s = "Share";
                }
                ScreenshotSmartActions.notifyScreenshotAction(context, intent.getStringExtra("android:screenshot_id"), s, false);
            }
        }
    }
    
    abstract static class ActionsReadyListener
    {
        abstract void onActionsReady(final Uri p0, final List<Notification$Action> p1, final List<Notification$Action> p2);
    }
    
    public static class DeleteScreenshotReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            if (!intent.hasExtra("android:screenshot_uri_id")) {
                return;
            }
            ScreenshotNotificationsController.cancelScreenshotNotification(context);
            new DeleteImageInBackgroundTask(context).execute((Object[])new Uri[] { Uri.parse(intent.getStringExtra("android:screenshot_uri_id")) });
            if (intent.getBooleanExtra("android:smart_actions_enabled", false)) {
                ScreenshotSmartActions.notifyScreenshotAction(context, intent.getStringExtra("android:screenshot_id"), "Delete", false);
            }
        }
    }
    
    static class SaveImageInBackgroundData
    {
        public boolean createDeleteAction;
        public int errorMsgResId;
        public Consumer<Uri> finisher;
        public Bitmap image;
        public Uri imageUri;
        public ActionsReadyListener mActionsReadyListener;
        
        void clearImage() {
            this.image = null;
            this.imageUri = null;
        }
    }
    
    public static class SmartActionsReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            final Intent intent2 = ((PendingIntent)intent.getParcelableExtra("android:screenshot_action_intent")).getIntent();
            final String stringExtra = intent.getStringExtra("android:screenshot_action_type");
            final StringBuilder sb = new StringBuilder();
            sb.append("Executing smart action [");
            sb.append(stringExtra);
            sb.append("]:");
            sb.append(intent2);
            Slog.d("GlobalScreenshot", sb.toString());
            context.startActivityAsUser(intent2, ActivityOptions.makeBasic().toBundle(), UserHandle.CURRENT);
            ScreenshotSmartActions.notifyScreenshotAction(context, intent.getStringExtra("android:screenshot_id"), stringExtra, true);
        }
    }
    
    public static class TargetChosenReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            ScreenshotNotificationsController.cancelScreenshotNotification(context);
        }
    }
}
