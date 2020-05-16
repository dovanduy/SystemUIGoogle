// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.util.AttributeSet;
import android.content.Context;
import android.annotation.SuppressLint;
import android.widget.Checkable;
import android.widget.ImageView;

@SuppressLint({ "AppCompatCustomView" })
class CheckableImageView extends ImageView implements Checkable
{
    private static final int[] CHECKED_STATE_SET;
    private boolean mChecked;
    
    static {
        CHECKED_STATE_SET = new int[] { 16842912 };
    }
    
    public CheckableImageView(final Context context) {
        this(context, null);
    }
    
    public CheckableImageView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public CheckableImageView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public boolean isChecked() {
        return this.mChecked;
    }
    
    public int[] onCreateDrawableState(final int n) {
        final int[] onCreateDrawableState = super.onCreateDrawableState(n + 1);
        if (this.isChecked()) {
            ImageView.mergeDrawableStates(onCreateDrawableState, CheckableImageView.CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }
    
    public void setChecked(final boolean mChecked) {
        if (this.mChecked != mChecked) {
            this.mChecked = mChecked;
            this.refreshDrawableState();
        }
    }
    
    public void toggle() {
        this.setChecked(this.mChecked ^ true);
    }
}
