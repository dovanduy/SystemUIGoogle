// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.widget.TextView;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ToggleButton;

public class AppCompatToggleButton extends ToggleButton
{
    private final AppCompatTextHelper mTextHelper;
    
    public AppCompatToggleButton(final Context context, final AttributeSet set) {
        this(context, set, 16842827);
    }
    
    public AppCompatToggleButton(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        ThemeUtils.checkAppCompatTheme((View)this, this.getContext());
        (this.mTextHelper = new AppCompatTextHelper((TextView)this)).loadFromAttributes(set, n);
    }
}
