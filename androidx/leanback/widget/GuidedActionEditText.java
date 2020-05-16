// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.ColorFilter;
import android.graphics.Canvas;
import androidx.core.widget.TextViewCompat;
import android.view.ActionMode$Callback;
import android.view.MotionEvent;
import android.view.KeyEvent;
import java.io.Serializable;
import android.widget.TextView;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.Rect;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.annotation.SuppressLint;
import android.widget.EditText;

@SuppressLint({ "AppCompatCustomView" })
public class GuidedActionEditText extends EditText
{
    private GuidedActionAutofillSupport$OnAutofillListener mAutofillListener;
    private ImeKeyMonitor$ImeKeyListener mKeyListener;
    private final Drawable mNoPaddingDrawable;
    private final Drawable mSavedBackground;
    
    public GuidedActionEditText(final Context context) {
        this(context, null);
    }
    
    public GuidedActionEditText(final Context context, final AttributeSet set) {
        this(context, set, 16842862);
    }
    
    public GuidedActionEditText(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mSavedBackground = this.getBackground();
        this.setBackground(this.mNoPaddingDrawable = new NoPaddingDrawable());
    }
    
    public void autofill(final AutofillValue autofillValue) {
        super.autofill(autofillValue);
        final GuidedActionAutofillSupport$OnAutofillListener mAutofillListener = this.mAutofillListener;
        if (mAutofillListener != null) {
            mAutofillListener.onAutofill((View)this);
        }
    }
    
    public int getAutofillType() {
        return 1;
    }
    
    protected void onFocusChanged(final boolean b, final int n, final Rect rect) {
        super.onFocusChanged(b, n, rect);
        if (b) {
            this.setBackground(this.mSavedBackground);
        }
        else {
            this.setBackground(this.mNoPaddingDrawable);
        }
        if (!b) {
            this.setFocusable(false);
        }
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        Serializable s;
        if (this.isFocused()) {
            s = EditText.class;
        }
        else {
            s = TextView.class;
        }
        accessibilityNodeInfo.setClassName((CharSequence)((Class)s).getName());
    }
    
    public boolean onKeyPreIme(final int n, final KeyEvent keyEvent) {
        final ImeKeyMonitor$ImeKeyListener mKeyListener = this.mKeyListener;
        boolean onKeyPreIme;
        if (!(onKeyPreIme = (mKeyListener != null && mKeyListener.onKeyPreIme(this, n, keyEvent)))) {
            onKeyPreIme = super.onKeyPreIme(n, keyEvent);
        }
        return onKeyPreIme;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return (!this.isInTouchMode() || this.isFocusableInTouchMode() || this.isTextSelectable()) && super.onTouchEvent(motionEvent);
    }
    
    public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback((TextView)this, actionMode$Callback));
    }
    
    static final class NoPaddingDrawable extends Drawable
    {
        public void draw(final Canvas canvas) {
        }
        
        public int getOpacity() {
            return -2;
        }
        
        public boolean getPadding(final Rect rect) {
            rect.set(0, 0, 0, 0);
            return true;
        }
        
        public void setAlpha(final int n) {
        }
        
        public void setColorFilter(final ColorFilter colorFilter) {
        }
    }
}
