// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.os.Bundle;
import android.view.ViewConfiguration;
import android.os.SystemClock;
import com.android.systemui.shared.system.QuickStepContract;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.graphics.drawable.Icon;
import android.graphics.Canvas;
import android.view.InputEvent;
import com.android.systemui.bubbles.BubbleController;
import android.util.Log;
import android.view.KeyEvent;
import android.metrics.LogMaker;
import com.android.internal.annotations.VisibleForTesting;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.util.TypedValue;
import com.android.systemui.R$styleable;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.recents.OverviewProxyService;
import android.graphics.Paint;
import android.view.View$OnClickListener;
import com.android.internal.logging.MetricsLogger;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import com.android.systemui.statusbar.phone.ButtonInterface;
import android.widget.ImageView;

public class KeyButtonView extends ImageView implements ButtonInterface
{
    private static final String TAG;
    private AudioManager mAudioManager;
    private final Runnable mCheckLongPress;
    private int mCode;
    private int mContentDescriptionRes;
    private float mDarkIntensity;
    private long mDownTime;
    private boolean mGestureAborted;
    private boolean mHasOvalBg;
    private final InputManager mInputManager;
    private boolean mLongClicked;
    private final MetricsLogger mMetricsLogger;
    private View$OnClickListener mOnClickListener;
    private final Paint mOvalBgPaint;
    private final OverviewProxyService mOverviewProxyService;
    private final boolean mPlaySounds;
    private final KeyButtonRipple mRipple;
    private int mTouchDownX;
    private int mTouchDownY;
    
    static {
        TAG = KeyButtonView.class.getSimpleName();
    }
    
    public KeyButtonView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyButtonView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, InputManager.getInstance());
    }
    
    @VisibleForTesting
    public KeyButtonView(final Context context, final AttributeSet set, final int n, final InputManager mInputManager) {
        super(context, set);
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mOvalBgPaint = new Paint(3);
        this.mHasOvalBg = false;
        this.mCheckLongPress = new Runnable() {
            @Override
            public void run() {
                if (KeyButtonView.this.isPressed()) {
                    if (KeyButtonView.this.isLongClickable()) {
                        KeyButtonView.this.performLongClick();
                        KeyButtonView.this.mLongClicked = true;
                    }
                    else {
                        KeyButtonView.this.sendEvent(0, 128);
                        KeyButtonView.this.sendAccessibilityEvent(2);
                        KeyButtonView.this.mLongClicked = true;
                    }
                }
            }
        };
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.KeyButtonView, n, 0);
        this.mCode = obtainStyledAttributes.getInteger(R$styleable.KeyButtonView_keyCode, 0);
        this.mPlaySounds = obtainStyledAttributes.getBoolean(R$styleable.KeyButtonView_playSound, true);
        final TypedValue typedValue = new TypedValue();
        if (obtainStyledAttributes.getValue(R$styleable.KeyButtonView_android_contentDescription, typedValue)) {
            this.mContentDescriptionRes = typedValue.resourceId;
        }
        obtainStyledAttributes.recycle();
        this.setClickable(true);
        this.mAudioManager = (AudioManager)context.getSystemService("audio");
        this.mRipple = new KeyButtonRipple(context, (View)this);
        this.mOverviewProxyService = Dependency.get(OverviewProxyService.class);
        this.mInputManager = mInputManager;
        this.setBackground((Drawable)this.mRipple);
        this.setWillNotDraw(false);
        this.forceHasOverlappingRendering(false);
    }
    
    private void sendEvent(int displayId, int n, final long n2) {
        this.mMetricsLogger.write(new LogMaker(931).setType(4).setSubtype(this.mCode).addTaggedData(933, (Object)displayId).addTaggedData(932, (Object)n));
        if (this.mCode == 4 && n != 128) {
            final String tag = KeyButtonView.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Back button event: ");
            sb.append(KeyEvent.actionToString(displayId));
            Log.i(tag, sb.toString());
            if (displayId == 1) {
                this.mOverviewProxyService.notifyBackAction((n & 0x20) == 0x0, -1, -1, true, false);
            }
        }
        int n3;
        if ((n & 0x80) != 0x0) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        final KeyEvent keyEvent = new KeyEvent(this.mDownTime, n2, displayId, this.mCode, n3, 0, -1, 0, n | 0x8 | 0x40, 257);
        if (this.getDisplay() != null) {
            displayId = this.getDisplay().getDisplayId();
        }
        else {
            displayId = -1;
        }
        final int expandedDisplayId = Dependency.get(BubbleController.class).getExpandedDisplayId(super.mContext);
        n = displayId;
        if (this.mCode == 4) {
            n = displayId;
            if (expandedDisplayId != -1) {
                n = expandedDisplayId;
            }
        }
        if (n != -1) {
            keyEvent.setDisplayId(n);
        }
        this.mInputManager.injectInputEvent((InputEvent)keyEvent, 0);
    }
    
    public void abortCurrentGesture() {
        this.setPressed(false);
        this.mRipple.abortDelayedRipple();
        this.mGestureAborted = true;
    }
    
    public void draw(final Canvas canvas) {
        if (this.mHasOvalBg) {
            canvas.save();
            canvas.translate((float)((this.getLeft() + this.getRight()) / 2), (float)((this.getTop() + this.getBottom()) / 2));
            final int n = Math.min(this.getWidth(), this.getHeight()) / 2;
            final float n2 = (float)(-n);
            final float n3 = (float)n;
            canvas.drawOval(n2, n2, n3, n3, this.mOvalBgPaint);
            canvas.restore();
        }
        super.draw(canvas);
    }
    
    public boolean isClickable() {
        return this.mCode != 0 || super.isClickable();
    }
    
    public void loadAsync(final Icon icon) {
        new AsyncTask<Icon, Void, Drawable>() {
            protected Drawable doInBackground(final Icon... array) {
                return array[0].loadDrawable(KeyButtonView.this.mContext);
            }
            
            protected void onPostExecute(final Drawable imageDrawable) {
                KeyButtonView.this.setImageDrawable(imageDrawable);
            }
        }.execute((Object[])new Icon[] { icon });
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final int mContentDescriptionRes = this.mContentDescriptionRes;
        if (mContentDescriptionRes != 0) {
            this.setContentDescription((CharSequence)super.mContext.getString(mContentDescriptionRes));
        }
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.mCode != 0) {
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(16, (CharSequence)null));
            if (this.isLongClickable()) {
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(32, (CharSequence)null));
            }
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final boolean shouldShowSwipeUpUI = this.mOverviewProxyService.shouldShowSwipeUpUI();
        final int action = motionEvent.getAction();
        if (action == 0) {
            this.mGestureAborted = false;
        }
        if (this.mGestureAborted) {
            this.setPressed(false);
            return false;
        }
        if (action != 0) {
            if (action != 1) {
                if (action != 2) {
                    if (action == 3) {
                        this.setPressed(false);
                        if (this.mCode != 0) {
                            this.sendEvent(1, 32);
                        }
                        this.removeCallbacks(this.mCheckLongPress);
                    }
                }
                else {
                    final int n = (int)motionEvent.getRawX();
                    final int n2 = (int)motionEvent.getRawY();
                    final float quickStepTouchSlopPx = QuickStepContract.getQuickStepTouchSlopPx(this.getContext());
                    if (Math.abs(n - this.mTouchDownX) > quickStepTouchSlopPx || Math.abs(n2 - this.mTouchDownY) > quickStepTouchSlopPx) {
                        this.setPressed(false);
                        this.removeCallbacks(this.mCheckLongPress);
                    }
                }
            }
            else {
                final boolean b = this.isPressed() && !this.mLongClicked;
                this.setPressed(false);
                final boolean b2 = SystemClock.uptimeMillis() - this.mDownTime > 150L;
                if (shouldShowSwipeUpUI) {
                    if (b) {
                        this.performHapticFeedback(1);
                        this.playSoundEffect(0);
                    }
                }
                else if (b2 && !this.mLongClicked) {
                    this.performHapticFeedback(8);
                }
                if (this.mCode != 0) {
                    if (b) {
                        this.sendEvent(1, 0);
                        this.sendAccessibilityEvent(1);
                    }
                    else {
                        this.sendEvent(1, 32);
                    }
                }
                else if (b) {
                    final View$OnClickListener mOnClickListener = this.mOnClickListener;
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick((View)this);
                        this.sendAccessibilityEvent(1);
                    }
                }
                this.removeCallbacks(this.mCheckLongPress);
            }
        }
        else {
            this.mDownTime = SystemClock.uptimeMillis();
            this.mLongClicked = false;
            this.setPressed(true);
            this.mTouchDownX = (int)motionEvent.getRawX();
            this.mTouchDownY = (int)motionEvent.getRawY();
            if (this.mCode != 0) {
                this.sendEvent(0, 0, this.mDownTime);
            }
            else {
                this.performHapticFeedback(1);
            }
            if (!shouldShowSwipeUpUI) {
                this.playSoundEffect(0);
            }
            this.removeCallbacks(this.mCheckLongPress);
            this.postDelayed(this.mCheckLongPress, (long)ViewConfiguration.getLongPressTimeout());
        }
        return true;
    }
    
    protected void onWindowVisibilityChanged(final int n) {
        super.onWindowVisibilityChanged(n);
        if (n != 0) {
            this.jumpDrawablesToCurrentState();
        }
    }
    
    public boolean performAccessibilityActionInternal(final int n, final Bundle bundle) {
        if (n == 16 && this.mCode != 0) {
            this.sendEvent(0, 0, SystemClock.uptimeMillis());
            this.sendEvent(1, 0);
            this.sendAccessibilityEvent(1);
            this.playSoundEffect(0);
            return true;
        }
        if (n == 32 && this.mCode != 0) {
            this.sendEvent(0, 128);
            this.sendEvent(1, 0);
            this.sendAccessibilityEvent(2);
            return true;
        }
        return super.performAccessibilityActionInternal(n, bundle);
    }
    
    public void playSoundEffect(final int n) {
        if (!this.mPlaySounds) {
            return;
        }
        this.mAudioManager.playSoundEffect(n, ActivityManager.getCurrentUser());
    }
    
    public void sendEvent(final int n, final int n2) {
        this.sendEvent(n, n2, SystemClock.uptimeMillis());
    }
    
    public void setCode(final int mCode) {
        this.mCode = mCode;
    }
    
    public void setDarkIntensity(final float darkIntensity) {
        this.mDarkIntensity = darkIntensity;
        final Drawable drawable = this.getDrawable();
        if (drawable != null) {
            ((KeyButtonDrawable)drawable).setDarkIntensity(darkIntensity);
            this.invalidate();
        }
        this.mRipple.setDarkIntensity(darkIntensity);
    }
    
    public void setDelayTouchFeedback(final boolean delayTouchFeedback) {
        this.mRipple.setDelayTouchFeedback(delayTouchFeedback);
    }
    
    public void setImageDrawable(final Drawable imageDrawable) {
        super.setImageDrawable(imageDrawable);
        if (imageDrawable == null) {
            return;
        }
        final KeyButtonDrawable keyButtonDrawable = (KeyButtonDrawable)imageDrawable;
        keyButtonDrawable.setDarkIntensity(this.mDarkIntensity);
        final boolean hasOvalBg = keyButtonDrawable.hasOvalBg();
        this.mHasOvalBg = hasOvalBg;
        if (hasOvalBg) {
            this.mOvalBgPaint.setColor(keyButtonDrawable.getDrawableBackgroundColor());
        }
        final KeyButtonRipple mRipple = this.mRipple;
        KeyButtonRipple.Type type;
        if (keyButtonDrawable.hasOvalBg()) {
            type = KeyButtonRipple.Type.OVAL;
        }
        else {
            type = KeyButtonRipple.Type.ROUNDED_RECT;
        }
        mRipple.setType(type);
    }
    
    public void setOnClickListener(final View$OnClickListener view$OnClickListener) {
        super.setOnClickListener(view$OnClickListener);
        this.mOnClickListener = view$OnClickListener;
    }
    
    public void setVertical(final boolean b) {
    }
}
