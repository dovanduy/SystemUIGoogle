// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import android.view.ViewGroup$LayoutParams;
import android.content.res.TypedArray;
import androidx.appcompat.R$styleable;
import android.util.AttributeSet;
import android.view.ViewGroup$MarginLayoutParams;
import androidx.appcompat.view.ActionMode;
import android.view.KeyEvent;
import android.content.res.Configuration;
import android.content.Context;

public abstract class ActionBar
{
    public boolean closeOptionsMenu() {
        return false;
    }
    
    public abstract boolean collapseActionView();
    
    public abstract void dispatchMenuVisibilityChanged(final boolean p0);
    
    public abstract int getDisplayOptions();
    
    public abstract Context getThemedContext();
    
    public boolean invalidateOptionsMenu() {
        return false;
    }
    
    public abstract void onConfigurationChanged(final Configuration p0);
    
    void onDestroy() {
    }
    
    public abstract boolean onKeyShortcut(final int p0, final KeyEvent p1);
    
    public boolean onMenuKeyEvent(final KeyEvent keyEvent) {
        return false;
    }
    
    public boolean openOptionsMenu() {
        return false;
    }
    
    public abstract void setDefaultDisplayHomeAsUpEnabled(final boolean p0);
    
    public abstract void setShowHideAnimationEnabled(final boolean p0);
    
    public abstract void setWindowTitle(final CharSequence p0);
    
    public abstract ActionMode startActionMode(final ActionMode.Callback p0);
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public int gravity;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.gravity = 0;
            this.gravity = 8388627;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.gravity = 0;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ActionBarLayout);
            this.gravity = obtainStyledAttributes.getInt(R$styleable.ActionBarLayout_android_layout_gravity, 0);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.gravity = 0;
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.gravity = 0;
            this.gravity = layoutParams.gravity;
        }
    }
    
    public interface OnMenuVisibilityListener
    {
        void onMenuVisibilityChanged(final boolean p0);
    }
}
