// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.text.method.MovementMethod;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import android.text.TextUtils;
import com.android.settingslib.R$drawable;
import androidx.core.content.res.TypedArrayUtils;
import com.android.settingslib.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.Preference;

public class FooterPreference extends Preference
{
    public FooterPreference(final Context context, final AttributeSet set) {
        super(context, set, TypedArrayUtils.getAttr(context, R$attr.footerPreferenceStyle, 16842894));
        this.init();
    }
    
    private void init() {
        if (this.getIcon() == null) {
            this.setIcon(R$drawable.ic_info_outline_24);
        }
        this.setOrder(2147483646);
        if (TextUtils.isEmpty((CharSequence)this.getKey())) {
            this.setKey("footer_preference");
        }
    }
    
    @Override
    public CharSequence getSummary() {
        return this.getTitle();
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final TextView textView = (TextView)preferenceViewHolder.itemView.findViewById(16908310);
        textView.setMovementMethod((MovementMethod)new LinkMovementMethod());
        textView.setClickable(false);
        textView.setLongClickable(false);
    }
    
    @Override
    public void setSummary(final int title) {
        this.setTitle(title);
    }
    
    @Override
    public void setSummary(final CharSequence title) {
        this.setTitle(title);
    }
}
