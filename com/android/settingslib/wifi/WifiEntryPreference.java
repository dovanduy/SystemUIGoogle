// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import android.view.View;
import com.android.settingslib.R$drawable;
import android.widget.ImageButton;
import com.android.settingslib.R$id;
import androidx.preference.PreferenceViewHolder;
import android.text.TextUtils;
import com.android.settingslib.Utils;
import android.content.res.TypedArray;
import android.content.res.Resources$NotFoundException;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.android.settingslib.R$layout;
import android.content.Context;
import com.android.settingslib.R$string;
import com.android.settingslib.R$attr;
import android.graphics.drawable.StateListDrawable;
import android.view.View$OnClickListener;
import com.android.wifitrackerlib.WifiEntry;
import androidx.preference.Preference;

public class WifiEntryPreference extends Preference implements WifiEntryCallback, View$OnClickListener
{
    private static final int[] FRICTION_ATTRS;
    private static final int[] STATE_SECURED;
    private static final int[] WIFI_CONNECTION_STRENGTH;
    private CharSequence mContentDescription;
    private final StateListDrawable mFrictionSld;
    private final IconInjector mIconInjector;
    private int mLevel;
    private OnButtonClickListener mOnButtonClickListener;
    private WifiEntry mWifiEntry;
    
    static {
        STATE_SECURED = new int[] { R$attr.state_encrypted };
        FRICTION_ATTRS = new int[] { R$attr.wifi_friction };
        WIFI_CONNECTION_STRENGTH = new int[] { R$string.accessibility_no_wifi, R$string.accessibility_wifi_one_bar, R$string.accessibility_wifi_two_bars, R$string.accessibility_wifi_three_bars, R$string.accessibility_wifi_signal_full };
    }
    
    WifiEntryPreference(final Context context, final WifiEntry mWifiEntry, final IconInjector mIconInjector) {
        super(context);
        this.mLevel = -1;
        this.setLayoutResource(R$layout.preference_access_point);
        this.setWidgetLayoutResource(R$layout.access_point_friction_widget);
        this.mFrictionSld = this.getFrictionStateListDrawable();
        (this.mWifiEntry = mWifiEntry).setListener((WifiEntry.WifiEntryCallback)this);
        this.mIconInjector = mIconInjector;
        this.refresh();
    }
    
    private void bindFrictionImage(final ImageView imageView) {
        if (imageView != null) {
            if (this.mFrictionSld != null) {
                if (this.mWifiEntry.getSecurity() != 0 && this.mWifiEntry.getSecurity() != 4) {
                    this.mFrictionSld.setState(WifiEntryPreference.STATE_SECURED);
                }
                imageView.setImageDrawable(this.mFrictionSld.getCurrent());
            }
        }
    }
    
    private Drawable getDrawable(final int n) {
        Drawable drawable;
        try {
            drawable = this.getContext().getDrawable(n);
        }
        catch (Resources$NotFoundException ex) {
            drawable = null;
        }
        return drawable;
    }
    
    private StateListDrawable getFrictionStateListDrawable() {
        StateListDrawable stateListDrawable = null;
        TypedArray obtainStyledAttributes;
        try {
            obtainStyledAttributes = this.getContext().getTheme().obtainStyledAttributes(WifiEntryPreference.FRICTION_ATTRS);
        }
        catch (Resources$NotFoundException ex) {
            obtainStyledAttributes = null;
        }
        if (obtainStyledAttributes != null) {
            stateListDrawable = (StateListDrawable)obtainStyledAttributes.getDrawable(0);
        }
        return stateListDrawable;
    }
    
    private void updateIcon(final int n) {
        if (n == -1) {
            this.setIcon(null);
            return;
        }
        final Drawable icon = this.mIconInjector.getIcon(n);
        if (icon != null) {
            icon.setTintList(Utils.getColorAttr(this.getContext(), 16843817));
            this.setIcon(icon);
        }
        else {
            this.setIcon(null);
        }
    }
    
    CharSequence buildContentDescription() {
        final Context context = this.getContext();
        final CharSequence title = this.getTitle();
        final CharSequence summary = this.getSummary();
        CharSequence concat = title;
        if (!TextUtils.isEmpty(summary)) {
            concat = TextUtils.concat(new CharSequence[] { title, ",", summary });
        }
        final int level = this.mWifiEntry.getLevel();
        CharSequence concat2 = concat;
        if (level >= 0) {
            final int[] wifi_CONNECTION_STRENGTH = WifiEntryPreference.WIFI_CONNECTION_STRENGTH;
            concat2 = concat;
            if (level < wifi_CONNECTION_STRENGTH.length) {
                concat2 = TextUtils.concat(new CharSequence[] { concat, ",", context.getString(wifi_CONNECTION_STRENGTH[level]) });
            }
        }
        String s;
        if (this.mWifiEntry.getSecurity() == 0) {
            s = context.getString(R$string.accessibility_wifi_security_type_none);
        }
        else {
            s = context.getString(R$string.accessibility_wifi_security_type_secured);
        }
        return TextUtils.concat(new CharSequence[] { concat2, ",", s });
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        final Drawable icon = this.getIcon();
        if (icon != null) {
            icon.setLevel(this.mLevel);
        }
        preferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
        preferenceViewHolder.findViewById(R$id.two_target_divider).setVisibility(4);
        final ImageButton imageButton = (ImageButton)preferenceViewHolder.findViewById(R$id.icon_button);
        final ImageView imageView = (ImageView)preferenceViewHolder.findViewById(R$id.friction_icon);
        if (this.mWifiEntry.getHelpUriString() != null && this.mWifiEntry.getConnectedState() == 0) {
            final Drawable drawable = this.getDrawable(R$drawable.ic_help);
            drawable.setTintList(Utils.getColorAttr(this.getContext(), 16843817));
            ((ImageView)imageButton).setImageDrawable(drawable);
            imageButton.setVisibility(0);
            imageButton.setOnClickListener((View$OnClickListener)this);
            imageButton.setContentDescription(this.getContext().getText(R$string.help_label));
            if (imageView != null) {
                imageView.setVisibility(8);
            }
        }
        else {
            imageButton.setVisibility(8);
            if (imageView != null) {
                imageView.setVisibility(0);
                this.bindFrictionImage(imageView);
            }
        }
    }
    
    public void onClick(final View view) {
        if (view.getId() == R$id.icon_button) {
            final OnButtonClickListener mOnButtonClickListener = this.mOnButtonClickListener;
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onButtonClick(this);
            }
        }
    }
    
    @Override
    public void onUpdated() {
        this.refresh();
    }
    
    public void refresh() {
        this.setTitle(this.mWifiEntry.getTitle());
        final int level = this.mWifiEntry.getLevel();
        if (level != this.mLevel) {
            this.updateIcon(this.mLevel = level);
            this.notifyChanged();
        }
        this.setSummary(this.mWifiEntry.getSummary(false));
        this.mContentDescription = this.buildContentDescription();
    }
    
    static class IconInjector
    {
        public abstract Drawable getIcon(final int p0);
    }
    
    public interface OnButtonClickListener
    {
        void onButtonClick(final WifiEntryPreference p0);
    }
}
