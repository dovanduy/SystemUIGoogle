// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.content.res.TypedArray;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.view.accessibility.AccessibilityEvent;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.graphics.Rect;
import android.view.animation.AnimationUtils;
import android.provider.Settings$System;
import android.graphics.Typeface;
import android.graphics.Paint$Align;
import com.android.systemui.R$dimen;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import java.util.ArrayList;
import android.os.PowerManager;
import android.graphics.Paint;
import java.util.Stack;
import android.view.animation.Interpolator;
import android.view.View;

public class PasswordTextView extends View
{
    private static char DOT = '\u2022';
    private Interpolator mAppearInterpolator;
    private int mCharPadding;
    private Stack<CharState> mCharPool;
    private Interpolator mDisappearInterpolator;
    private int mDotSize;
    private final Paint mDrawPaint;
    private final int mGravity;
    private PowerManager mPM;
    private boolean mShowPassword;
    private String mText;
    private ArrayList<CharState> mTextChars;
    private final int mTextHeightRaw;
    private UserActivityListener mUserActivityListener;
    
    public PasswordTextView(final Context context) {
        this(context, null);
    }
    
    public PasswordTextView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PasswordTextView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PasswordTextView(final Context context, AttributeSet obtainStyledAttributes, int color, final int n) {
        super(context, obtainStyledAttributes, color, n);
        this.mTextChars = new ArrayList<CharState>();
        this.mText = "";
        this.mCharPool = new Stack<CharState>();
        this.mDrawPaint = new Paint();
        boolean mShowPassword = true;
        this.setFocusableInTouchMode(true);
        this.setFocusable(true);
        obtainStyledAttributes = (AttributeSet)context.obtainStyledAttributes(obtainStyledAttributes, R$styleable.PasswordTextView);
        try {
            this.mTextHeightRaw = ((TypedArray)obtainStyledAttributes).getInt(R$styleable.PasswordTextView_scaledTextSize, 0);
            this.mGravity = ((TypedArray)obtainStyledAttributes).getInt(R$styleable.PasswordTextView_android_gravity, 17);
            this.mDotSize = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(R$styleable.PasswordTextView_dotSize, this.getContext().getResources().getDimensionPixelSize(R$dimen.password_dot_size));
            this.mCharPadding = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(R$styleable.PasswordTextView_charPadding, this.getContext().getResources().getDimensionPixelSize(R$dimen.password_char_padding));
            color = ((TypedArray)obtainStyledAttributes).getColor(R$styleable.PasswordTextView_android_textColor, -1);
            this.mDrawPaint.setColor(color);
            ((TypedArray)obtainStyledAttributes).recycle();
            this.mDrawPaint.setFlags(129);
            this.mDrawPaint.setTextAlign(Paint$Align.CENTER);
            this.mDrawPaint.setTypeface(Typeface.create(context.getString(17039907), 0));
            if (Settings$System.getInt(super.mContext.getContentResolver(), "show_password", 1) != 1) {
                mShowPassword = false;
            }
            this.mShowPassword = mShowPassword;
            this.mAppearInterpolator = AnimationUtils.loadInterpolator(super.mContext, 17563662);
            this.mDisappearInterpolator = AnimationUtils.loadInterpolator(super.mContext, 17563663);
            AnimationUtils.loadInterpolator(super.mContext, 17563661);
            this.mPM = (PowerManager)super.mContext.getSystemService("power");
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private Rect getCharBounds() {
        this.mDrawPaint.setTextSize(this.mTextHeightRaw * this.getResources().getDisplayMetrics().scaledDensity);
        final Rect rect = new Rect();
        this.mDrawPaint.getTextBounds("0", 0, 1, rect);
        return rect;
    }
    
    private float getDrawingWidth() {
        final int size = this.mTextChars.size();
        final Rect charBounds = this.getCharBounds();
        final int right = charBounds.right;
        final int left = charBounds.left;
        int i = 0;
        int n = 0;
        while (i < size) {
            final CharState charState = this.mTextChars.get(i);
            int n2 = n;
            if (i != 0) {
                n2 = (int)(n + this.mCharPadding * charState.currentWidthFactor);
            }
            n = (int)(n2 + (right - left) * charState.currentWidthFactor);
            ++i;
        }
        return (float)n;
    }
    
    private CharSequence getTransformedText() {
        final int size = this.mTextChars.size();
        final StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            final CharState charState = this.mTextChars.get(i);
            if (charState.dotAnimator == null || charState.dotAnimationIsGrowing) {
                char c;
                if (charState.isCharVisibleForA11y()) {
                    c = charState.whichChar;
                }
                else {
                    c = PasswordTextView.DOT;
                }
                sb.append(c);
            }
        }
        return sb;
    }
    
    private CharState obtainCharState(final char c) {
        CharState charState;
        if (this.mCharPool.isEmpty()) {
            charState = new CharState();
        }
        else {
            charState = this.mCharPool.pop();
            charState.reset();
        }
        charState.whichChar = c;
        return charState;
    }
    
    private void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
        final UserActivityListener mUserActivityListener = this.mUserActivityListener;
        if (mUserActivityListener != null) {
            mUserActivityListener.onUserActivity();
        }
    }
    
    public void append(final char c) {
        final int size = this.mTextChars.size();
        final CharSequence transformedText = this.getTransformedText();
        final StringBuilder sb = new StringBuilder();
        sb.append(this.mText);
        sb.append(c);
        final String string = sb.toString();
        this.mText = string;
        final int length = string.length();
        CharState obtainCharState;
        if (length > size) {
            obtainCharState = this.obtainCharState(c);
            this.mTextChars.add(obtainCharState);
        }
        else {
            obtainCharState = this.mTextChars.get(length - 1);
            obtainCharState.whichChar = c;
        }
        obtainCharState.startAppearAnimation();
        if (length > 1) {
            final CharState charState = this.mTextChars.get(length - 2);
            if (charState.isDotSwapPending) {
                charState.swapToDotWhenAppearFinished();
            }
        }
        this.userActivity();
        this.sendAccessibilityEventTypeViewTextChanged(transformedText, transformedText.length(), 0, 1);
    }
    
    public void deleteLastChar() {
        int length = this.mText.length();
        final CharSequence transformedText = this.getTransformedText();
        if (length > 0) {
            final String mText = this.mText;
            --length;
            this.mText = mText.substring(0, length);
            this.mTextChars.get(length).startRemoveAnimation(0L, 0L);
            this.sendAccessibilityEventTypeViewTextChanged(transformedText, transformedText.length() - 1, 1, 0);
        }
        this.userActivity();
    }
    
    public String getText() {
        return this.mText;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onDraw(final Canvas canvas) {
        float drawingWidth = this.getDrawingWidth();
        final int mGravity = this.mGravity;
        float n = 0.0f;
        Label_0078: {
            float n2;
            if ((mGravity & 0x7) == 0x3) {
                if ((mGravity & 0x800000) == 0x0 || this.getLayoutDirection() != 1) {
                    n = (float)this.getPaddingLeft();
                    break Label_0078;
                }
                n2 = (float)(this.getWidth() - this.getPaddingRight());
            }
            else {
                n2 = (float)(this.getWidth() / 2);
                drawingWidth /= 2.0f;
            }
            n = n2 - drawingWidth;
        }
        final int size = this.mTextChars.size();
        final Rect charBounds = this.getCharBounds();
        final int bottom = charBounds.bottom;
        final int top = charBounds.top;
        final float n3 = (float)((this.getHeight() - this.getPaddingBottom() - this.getPaddingTop()) / 2 + this.getPaddingTop());
        canvas.clipRect(this.getPaddingLeft(), this.getPaddingTop(), this.getWidth() - this.getPaddingRight(), this.getHeight() - this.getPaddingBottom());
        final float n4 = (float)(charBounds.right - charBounds.left);
        for (int i = 0; i < size; ++i) {
            n += this.mTextChars.get(i).draw(canvas, n, bottom - top, n3, n4);
        }
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName((CharSequence)EditText.class.getName());
        accessibilityEvent.setPassword(true);
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName((CharSequence)EditText.class.getName());
        accessibilityNodeInfo.setPassword(true);
        accessibilityNodeInfo.setText(this.getTransformedText());
        accessibilityNodeInfo.setEditable(true);
        accessibilityNodeInfo.setInputType(16);
    }
    
    public void reset(final boolean b, final boolean b2) {
        final CharSequence transformedText = this.getTransformedText();
        this.mText = "";
        final int size = this.mTextChars.size();
        final int n = size - 1;
        final int n2 = n / 2;
        for (int i = 0; i < size; ++i) {
            final CharState item = this.mTextChars.get(i);
            if (b) {
                int n3;
                if (i <= n2) {
                    n3 = i * 2;
                }
                else {
                    n3 = n - (i - n2 - 1) * 2;
                }
                item.startRemoveAnimation(Math.min(n3 * 40L, 200L), Math.min(40L * n, 200L) + 160L);
                item.removeDotSwapCallbacks();
            }
            else {
                this.mCharPool.push(item);
            }
        }
        if (!b) {
            this.mTextChars.clear();
        }
        if (b2) {
            this.sendAccessibilityEventTypeViewTextChanged(transformedText, 0, transformedText.length(), 0);
        }
    }
    
    void sendAccessibilityEventTypeViewTextChanged(CharSequence transformedText, final int fromIndex, final int removedCount, final int addedCount) {
        if (AccessibilityManager.getInstance(super.mContext).isEnabled() && (this.isFocused() || (this.isSelected() && this.isShown()))) {
            final AccessibilityEvent obtain = AccessibilityEvent.obtain(16);
            obtain.setFromIndex(fromIndex);
            obtain.setRemovedCount(removedCount);
            obtain.setAddedCount(addedCount);
            obtain.setBeforeText(transformedText);
            transformedText = this.getTransformedText();
            if (!TextUtils.isEmpty(transformedText)) {
                obtain.getText().add(transformedText);
            }
            obtain.setPassword(true);
            this.sendAccessibilityEventUnchecked(obtain);
        }
    }
    
    public void setUserActivityListener(final UserActivityListener mUserActivityListener) {
        this.mUserActivityListener = mUserActivityListener;
    }
    
    private class CharState
    {
        float currentDotSizeFactor;
        float currentTextSizeFactor;
        float currentTextTranslationY;
        float currentWidthFactor;
        boolean dotAnimationIsGrowing;
        Animator dotAnimator;
        Animator$AnimatorListener dotFinishListener;
        private ValueAnimator$AnimatorUpdateListener dotSizeUpdater;
        private Runnable dotSwapperRunnable;
        boolean isDotSwapPending;
        Animator$AnimatorListener removeEndListener;
        boolean textAnimationIsGrowing;
        ValueAnimator textAnimator;
        Animator$AnimatorListener textFinishListener;
        private ValueAnimator$AnimatorUpdateListener textSizeUpdater;
        ValueAnimator textTranslateAnimator;
        Animator$AnimatorListener textTranslateFinishListener;
        private ValueAnimator$AnimatorUpdateListener textTranslationUpdater;
        char whichChar;
        boolean widthAnimationIsGrowing;
        ValueAnimator widthAnimator;
        Animator$AnimatorListener widthFinishListener;
        private ValueAnimator$AnimatorUpdateListener widthUpdater;
        
        private CharState() {
            this.currentTextTranslationY = 1.0f;
            this.removeEndListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
                private boolean mCancelled;
                
                public void onAnimationCancel(final Animator animator) {
                    this.mCancelled = true;
                }
                
                public void onAnimationEnd(final Animator animator) {
                    if (!this.mCancelled) {
                        PasswordTextView.this.mTextChars.remove(CharState.this);
                        PasswordTextView.this.mCharPool.push(CharState.this);
                        CharState.this.reset();
                        final CharState this$1 = CharState.this;
                        this$1.cancelAnimator((Animator)this$1.textTranslateAnimator);
                        CharState.this.textTranslateAnimator = null;
                    }
                }
                
                public void onAnimationStart(final Animator animator) {
                    this.mCancelled = false;
                }
            };
            this.dotFinishListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    CharState.this.dotAnimator = null;
                }
            };
            this.textFinishListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    CharState.this.textAnimator = null;
                }
            };
            this.textTranslateFinishListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    CharState.this.textTranslateAnimator = null;
                }
            };
            this.widthFinishListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    CharState.this.widthAnimator = null;
                }
            };
            this.dotSizeUpdater = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    CharState.this.currentDotSizeFactor = (float)valueAnimator.getAnimatedValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.textSizeUpdater = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    final boolean charVisibleForA11y = CharState.this.isCharVisibleForA11y();
                    final CharState this$1 = CharState.this;
                    final float currentTextSizeFactor = this$1.currentTextSizeFactor;
                    this$1.currentTextSizeFactor = (float)valueAnimator.getAnimatedValue();
                    if (charVisibleForA11y != CharState.this.isCharVisibleForA11y()) {
                        final CharState this$2 = CharState.this;
                        this$2.currentTextSizeFactor = currentTextSizeFactor;
                        final CharSequence access$500 = PasswordTextView.this.getTransformedText();
                        CharState.this.currentTextSizeFactor = (float)valueAnimator.getAnimatedValue();
                        final int index = PasswordTextView.this.mTextChars.indexOf(CharState.this);
                        if (index >= 0) {
                            PasswordTextView.this.sendAccessibilityEventTypeViewTextChanged(access$500, index, 1, 1);
                        }
                    }
                    PasswordTextView.this.invalidate();
                }
            };
            this.textTranslationUpdater = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    CharState.this.currentTextTranslationY = (float)valueAnimator.getAnimatedValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.widthUpdater = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    CharState.this.currentWidthFactor = (float)valueAnimator.getAnimatedValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.dotSwapperRunnable = new Runnable() {
                @Override
                public void run() {
                    CharState.this.performSwap();
                    CharState.this.isDotSwapPending = false;
                }
            };
        }
        
        private void cancelAnimator(final Animator animator) {
            if (animator != null) {
                animator.cancel();
            }
        }
        
        private void performSwap() {
            this.startTextDisappearAnimation(0L);
            this.startDotAppearAnimation(30L);
        }
        
        private void postDotSwap(final long n) {
            this.removeDotSwapCallbacks();
            PasswordTextView.this.postDelayed(this.dotSwapperRunnable, n);
            this.isDotSwapPending = true;
        }
        
        private void removeDotSwapCallbacks() {
            PasswordTextView.this.removeCallbacks(this.dotSwapperRunnable);
            this.isDotSwapPending = false;
        }
        
        private void startDotAppearAnimation(final long n) {
            this.cancelAnimator(this.dotAnimator);
            if (!PasswordTextView.this.mShowPassword) {
                final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 1.5f });
                ofFloat.addUpdateListener(this.dotSizeUpdater);
                ofFloat.setInterpolator((TimeInterpolator)PasswordTextView.this.mAppearInterpolator);
                ofFloat.setDuration(160L);
                final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[] { 1.5f, 1.0f });
                ofFloat2.addUpdateListener(this.dotSizeUpdater);
                ofFloat2.setDuration(160L);
                ofFloat2.addListener(this.dotFinishListener);
                final AnimatorSet dotAnimator = new AnimatorSet();
                dotAnimator.playSequentially(new Animator[] { (Animator)ofFloat, (Animator)ofFloat2 });
                dotAnimator.setStartDelay(n);
                dotAnimator.start();
                this.dotAnimator = (Animator)dotAnimator;
            }
            else {
                final ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 1.0f });
                ofFloat3.addUpdateListener(this.dotSizeUpdater);
                ofFloat3.setDuration((long)((1.0f - this.currentDotSizeFactor) * 160.0f));
                ofFloat3.addListener(this.dotFinishListener);
                ofFloat3.setStartDelay(n);
                ofFloat3.start();
                this.dotAnimator = (Animator)ofFloat3;
            }
            this.dotAnimationIsGrowing = true;
        }
        
        private void startDotDisappearAnimation(final long startDelay) {
            this.cancelAnimator(this.dotAnimator);
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 0.0f });
            ofFloat.addUpdateListener(this.dotSizeUpdater);
            ofFloat.addListener(this.dotFinishListener);
            ofFloat.setInterpolator((TimeInterpolator)PasswordTextView.this.mDisappearInterpolator);
            ofFloat.setDuration((long)(Math.min(this.currentDotSizeFactor, 1.0f) * 160.0f));
            ofFloat.setStartDelay(startDelay);
            ofFloat.start();
            this.dotAnimator = (Animator)ofFloat;
            this.dotAnimationIsGrowing = false;
        }
        
        private void startTextAppearAnimation() {
            this.cancelAnimator((Animator)this.textAnimator);
            (this.textAnimator = ValueAnimator.ofFloat(new float[] { this.currentTextSizeFactor, 1.0f })).addUpdateListener(this.textSizeUpdater);
            this.textAnimator.addListener(this.textFinishListener);
            this.textAnimator.setInterpolator((TimeInterpolator)PasswordTextView.this.mAppearInterpolator);
            this.textAnimator.setDuration((long)((1.0f - this.currentTextSizeFactor) * 160.0f));
            this.textAnimator.start();
            this.textAnimationIsGrowing = true;
            if (this.textTranslateAnimator == null) {
                (this.textTranslateAnimator = ValueAnimator.ofFloat(new float[] { 1.0f, 0.0f })).addUpdateListener(this.textTranslationUpdater);
                this.textTranslateAnimator.addListener(this.textTranslateFinishListener);
                this.textTranslateAnimator.setInterpolator((TimeInterpolator)PasswordTextView.this.mAppearInterpolator);
                this.textTranslateAnimator.setDuration(160L);
                this.textTranslateAnimator.start();
            }
        }
        
        private void startTextDisappearAnimation(final long startDelay) {
            this.cancelAnimator((Animator)this.textAnimator);
            (this.textAnimator = ValueAnimator.ofFloat(new float[] { this.currentTextSizeFactor, 0.0f })).addUpdateListener(this.textSizeUpdater);
            this.textAnimator.addListener(this.textFinishListener);
            this.textAnimator.setInterpolator((TimeInterpolator)PasswordTextView.this.mDisappearInterpolator);
            this.textAnimator.setDuration((long)(this.currentTextSizeFactor * 160.0f));
            this.textAnimator.setStartDelay(startDelay);
            this.textAnimator.start();
            this.textAnimationIsGrowing = false;
        }
        
        private void startWidthAppearAnimation() {
            this.cancelAnimator((Animator)this.widthAnimator);
            (this.widthAnimator = ValueAnimator.ofFloat(new float[] { this.currentWidthFactor, 1.0f })).addUpdateListener(this.widthUpdater);
            this.widthAnimator.addListener(this.widthFinishListener);
            this.widthAnimator.setDuration((long)((1.0f - this.currentWidthFactor) * 160.0f));
            this.widthAnimator.start();
            this.widthAnimationIsGrowing = true;
        }
        
        private void startWidthDisappearAnimation(final long startDelay) {
            this.cancelAnimator((Animator)this.widthAnimator);
            (this.widthAnimator = ValueAnimator.ofFloat(new float[] { this.currentWidthFactor, 0.0f })).addUpdateListener(this.widthUpdater);
            this.widthAnimator.addListener(this.widthFinishListener);
            this.widthAnimator.addListener(this.removeEndListener);
            this.widthAnimator.setDuration((long)(this.currentWidthFactor * 160.0f));
            this.widthAnimator.setStartDelay(startDelay);
            this.widthAnimator.start();
            this.widthAnimationIsGrowing = false;
        }
        
        public float draw(final Canvas canvas, final float n, final int n2, final float n3, float n4) {
            final float currentTextSizeFactor = this.currentTextSizeFactor;
            boolean b = true;
            final boolean b2 = currentTextSizeFactor > 0.0f;
            if (this.currentDotSizeFactor <= 0.0f) {
                b = false;
            }
            n4 *= this.currentWidthFactor;
            if (b2) {
                final float n5 = (float)n2;
                final float n6 = n5 / 2.0f;
                final float currentTextSizeFactor2 = this.currentTextSizeFactor;
                final float currentTextTranslationY = this.currentTextTranslationY;
                canvas.save();
                canvas.translate(n4 / 2.0f + n, n6 * currentTextSizeFactor2 + n3 + n5 * currentTextTranslationY * 0.8f);
                final float currentTextSizeFactor3 = this.currentTextSizeFactor;
                canvas.scale(currentTextSizeFactor3, currentTextSizeFactor3);
                canvas.drawText(Character.toString(this.whichChar), 0.0f, 0.0f, PasswordTextView.this.mDrawPaint);
                canvas.restore();
            }
            if (b) {
                canvas.save();
                canvas.translate(n + n4 / 2.0f, n3);
                canvas.drawCircle(0.0f, 0.0f, PasswordTextView.this.mDotSize / 2 * this.currentDotSizeFactor, PasswordTextView.this.mDrawPaint);
                canvas.restore();
            }
            return n4 + PasswordTextView.this.mCharPadding * this.currentWidthFactor;
        }
        
        public boolean isCharVisibleForA11y() {
            final ValueAnimator textAnimator = this.textAnimator;
            final boolean b = true;
            final boolean b2 = textAnimator != null && this.textAnimationIsGrowing;
            boolean b3 = b;
            if (this.currentTextSizeFactor <= 0.0f) {
                b3 = (b2 && b);
            }
            return b3;
        }
        
        void reset() {
            this.whichChar = 0;
            this.currentTextSizeFactor = 0.0f;
            this.currentDotSizeFactor = 0.0f;
            this.currentWidthFactor = 0.0f;
            this.cancelAnimator((Animator)this.textAnimator);
            this.textAnimator = null;
            this.cancelAnimator(this.dotAnimator);
            this.dotAnimator = null;
            this.cancelAnimator((Animator)this.widthAnimator);
            this.widthAnimator = null;
            this.currentTextTranslationY = 1.0f;
            this.removeDotSwapCallbacks();
        }
        
        void startAppearAnimation() {
            final boolean access$700 = PasswordTextView.this.mShowPassword;
            final boolean b = true;
            final boolean b2 = !access$700 && (this.dotAnimator == null || !this.dotAnimationIsGrowing);
            final boolean b3 = PasswordTextView.this.mShowPassword && (this.textAnimator == null || !this.textAnimationIsGrowing);
            int n = b ? 1 : 0;
            if (this.widthAnimator != null) {
                if (!this.widthAnimationIsGrowing) {
                    n = (b ? 1 : 0);
                }
                else {
                    n = 0;
                }
            }
            if (b2) {
                this.startDotAppearAnimation(0L);
            }
            if (b3) {
                this.startTextAppearAnimation();
            }
            if (n != 0) {
                this.startWidthAppearAnimation();
            }
            if (PasswordTextView.this.mShowPassword) {
                this.postDotSwap(1300L);
            }
        }
        
        void startRemoveAnimation(final long n, final long n2) {
            final float currentDotSizeFactor = this.currentDotSizeFactor;
            final boolean b = true;
            final boolean b2 = (currentDotSizeFactor > 0.0f && this.dotAnimator == null) || (this.dotAnimator != null && this.dotAnimationIsGrowing);
            final boolean b3 = (this.currentTextSizeFactor > 0.0f && this.textAnimator == null) || (this.textAnimator != null && this.textAnimationIsGrowing);
            int n3 = 0;
            Label_0129: {
                if (this.currentWidthFactor > 0.0f) {
                    n3 = (b ? 1 : 0);
                    if (this.widthAnimator == null) {
                        break Label_0129;
                    }
                }
                if (this.widthAnimator != null && this.widthAnimationIsGrowing) {
                    n3 = (b ? 1 : 0);
                }
                else {
                    n3 = 0;
                }
            }
            if (b2) {
                this.startDotDisappearAnimation(n);
            }
            if (b3) {
                this.startTextDisappearAnimation(n);
            }
            if (n3 != 0) {
                this.startWidthDisappearAnimation(n2);
            }
        }
        
        void swapToDotWhenAppearFinished() {
            this.removeDotSwapCallbacks();
            final ValueAnimator textAnimator = this.textAnimator;
            if (textAnimator != null) {
                this.postDotSwap(textAnimator.getDuration() - this.textAnimator.getCurrentPlayTime() + 100L);
            }
            else {
                this.performSwap();
            }
        }
    }
    
    public interface UserActivityListener
    {
        void onUserActivity();
    }
}
