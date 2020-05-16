// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.KeyEvent;
import androidx.leanback.R$style;
import android.util.AttributeSet;
import android.content.Context;

public class SearchEditText extends StreamingTextView
{
    OnKeyboardDismissListener mKeyboardDismissListener;
    
    public SearchEditText(final Context context) {
        this(context, null);
    }
    
    public SearchEditText(final Context context, final AttributeSet set) {
        this(context, set, R$style.TextAppearance_Leanback_SearchTextEdit);
    }
    
    public SearchEditText(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public boolean onKeyPreIme(final int n, final KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && this.mKeyboardDismissListener != null) {
            this.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    final OnKeyboardDismissListener mKeyboardDismissListener = SearchEditText.this.mKeyboardDismissListener;
                    if (mKeyboardDismissListener != null) {
                        mKeyboardDismissListener.onKeyboardDismiss();
                    }
                }
            });
        }
        return super.onKeyPreIme(n, keyEvent);
    }
    
    public void setOnKeyboardDismissListener(final OnKeyboardDismissListener mKeyboardDismissListener) {
        this.mKeyboardDismissListener = mKeyboardDismissListener;
    }
    
    public interface OnKeyboardDismissListener
    {
        void onKeyboardDismiss();
    }
}
