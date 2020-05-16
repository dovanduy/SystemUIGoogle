// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.content.res.TypedArray;
import com.android.systemui.R$drawable;
import com.android.systemui.R$array;
import com.android.systemui.R$id;
import android.view.LayoutInflater;
import android.view.View$OnHoverListener;
import com.android.systemui.R$styleable;
import android.view.View;
import com.android.systemui.R$layout;
import android.util.AttributeSet;
import android.content.Context;
import android.os.PowerManager;
import com.android.internal.widget.LockPatternUtils;
import android.view.View$OnClickListener;
import android.widget.TextView;
import android.view.ViewGroup;

public class NumPadKey extends ViewGroup
{
    static String[] sKlondike;
    private int mDigit;
    private final TextView mDigitText;
    private final TextView mKlondikeText;
    private View$OnClickListener mListener;
    private final LockPatternUtils mLockPatternUtils;
    private final PowerManager mPM;
    private PasswordTextView mTextView;
    private int mTextViewResId;
    
    public NumPadKey(final Context context) {
        this(context, null);
    }
    
    public NumPadKey(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public NumPadKey(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, R$layout.keyguard_num_pad_key);
    }
    
    protected NumPadKey(final Context context, final AttributeSet set, int mDigit, int length) {
        super(context, set, mDigit);
        this.mDigit = -1;
        this.mListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(View viewById) {
                if (NumPadKey.this.mTextView == null && NumPadKey.this.mTextViewResId > 0) {
                    viewById = NumPadKey.this.getRootView().findViewById(NumPadKey.this.mTextViewResId);
                    if (viewById != null && viewById instanceof PasswordTextView) {
                        NumPadKey.this.mTextView = (PasswordTextView)viewById;
                    }
                }
                if (NumPadKey.this.mTextView != null && NumPadKey.this.mTextView.isEnabled()) {
                    NumPadKey.this.mTextView.append(Character.forDigit(NumPadKey.this.mDigit, 10));
                }
                NumPadKey.this.userActivity();
            }
        };
        this.setFocusable(true);
        Object o = context.obtainStyledAttributes(set, R$styleable.NumPadKey);
        try {
            this.mDigit = ((TypedArray)o).getInt(R$styleable.NumPadKey_digit, this.mDigit);
            this.mTextViewResId = ((TypedArray)o).getResourceId(R$styleable.NumPadKey_textView, 0);
            ((TypedArray)o).recycle();
            this.setOnClickListener(this.mListener);
            this.setOnHoverListener((View$OnHoverListener)new LiftToActivateListener(context));
            this.mLockPatternUtils = new LockPatternUtils(context);
            this.mPM = (PowerManager)super.mContext.getSystemService("power");
            ((LayoutInflater)this.getContext().getSystemService("layout_inflater")).inflate(length, (ViewGroup)this, true);
            o = this.findViewById(R$id.digit_text);
            (this.mDigitText = (TextView)o).setText((CharSequence)Integer.toString(this.mDigit));
            this.mKlondikeText = (TextView)this.findViewById(R$id.klondike_text);
            if (this.mDigit >= 0) {
                if (NumPadKey.sKlondike == null) {
                    NumPadKey.sKlondike = this.getResources().getStringArray(R$array.lockscreen_num_pad_klondike);
                }
                o = NumPadKey.sKlondike;
                if (o != null) {
                    length = ((CharSequence)o).length;
                    mDigit = this.mDigit;
                    if (length > mDigit) {
                        o = o[mDigit];
                        if (((String)o).length() > 0) {
                            this.mKlondikeText.setText((CharSequence)o);
                        }
                        else {
                            this.mKlondikeText.setVisibility(4);
                        }
                    }
                }
            }
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, android.R$styleable.View);
            if (!obtainStyledAttributes.hasValueOrEmpty(13)) {
                this.setBackground(super.mContext.getDrawable(R$drawable.ripple_drawable_pin));
            }
            obtainStyledAttributes.recycle();
            this.setContentDescription((CharSequence)this.mDigitText.getText().toString());
        }
        finally {
            ((TypedArray)o).recycle();
        }
    }
    
    public void doHapticKeyClick() {
        if (this.mLockPatternUtils.isTactileFeedbackEnabled()) {
            this.performHapticFeedback(1, 3);
        }
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onLayout(final boolean b, int measuredHeight, int n, int n2, int n3) {
        final int measuredHeight2 = this.mDigitText.getMeasuredHeight();
        measuredHeight = this.mKlondikeText.getMeasuredHeight();
        n2 = this.getHeight() / 2 - (measuredHeight2 + measuredHeight) / 2;
        n = this.getWidth() / 2;
        n3 = n - this.mDigitText.getMeasuredWidth() / 2;
        final int n4 = measuredHeight2 + n2;
        final TextView mDigitText = this.mDigitText;
        mDigitText.layout(n3, n2, mDigitText.getMeasuredWidth() + n3, n4);
        n2 = (int)(n4 - measuredHeight * 0.35f);
        n -= this.mKlondikeText.getMeasuredWidth() / 2;
        final TextView mKlondikeText = this.mKlondikeText;
        mKlondikeText.layout(n, n2, mKlondikeText.getMeasuredWidth() + n, measuredHeight + n2);
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        this.measureChildren(n, n2);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.doHapticKeyClick();
        }
        return super.onTouchEvent(motionEvent);
    }
    
    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }
}
