// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import androidx.core.content.ContextCompat;
import android.widget.TextView;
import android.util.TypedValue;
import android.os.Build$VERSION;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class PreferenceCategory extends PreferenceGroup
{
    public PreferenceCategory(final Context context) {
        this(context, null);
    }
    
    public PreferenceCategory(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.preferenceCategoryStyle, 16842892));
    }
    
    public PreferenceCategory(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PreferenceCategory(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    @Override
    public boolean isEnabled() {
        return false;
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 28) {
            preferenceViewHolder.itemView.setAccessibilityHeading(true);
        }
        else if (sdk_INT < 21) {
            final TypedValue typedValue = new TypedValue();
            if (!this.getContext().getTheme().resolveAttribute(androidx.appcompat.R$attr.colorAccent, typedValue, true)) {
                return;
            }
            final TextView textView = (TextView)preferenceViewHolder.findViewById(16908310);
            if (textView == null) {
                return;
            }
            if (textView.getCurrentTextColor() != ContextCompat.getColor(this.getContext(), R$color.preference_fallback_accent_color)) {
                return;
            }
            textView.setTextColor(typedValue.data);
        }
    }
    
    @Override
    public boolean shouldDisableDependents() {
        return super.isEnabled() ^ true;
    }
}
