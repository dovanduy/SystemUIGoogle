// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import android.view.View;
import com.android.settingslib.R$id;
import androidx.preference.PreferenceViewHolder;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$layout;
import android.util.AttributeSet;
import android.content.Context;
import com.android.settingslib.R$string;
import com.android.settingslib.R$attr;
import android.widget.TextView;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.Drawable;
import androidx.preference.Preference;

public class AccessPointPreference extends Preference
{
    private static final int[] STATE_METERED;
    private static final int[] STATE_SECURED;
    private static final int[] WIFI_CONNECTION_STRENGTH;
    private AccessPoint mAccessPoint;
    private Drawable mBadge;
    private final int mBadgePadding;
    private CharSequence mContentDescription;
    private final StateListDrawable mFrictionSld;
    private int mLevel;
    private final Runnable mNotifyChanged;
    private boolean mShowDivider;
    private TextView mTitleView;
    
    static {
        STATE_SECURED = new int[] { R$attr.state_encrypted };
        STATE_METERED = new int[] { R$attr.state_metered };
        WIFI_CONNECTION_STRENGTH = new int[] { R$string.accessibility_no_wifi, R$string.accessibility_wifi_one_bar, R$string.accessibility_wifi_two_bars, R$string.accessibility_wifi_three_bars, R$string.accessibility_wifi_signal_full };
    }
    
    public AccessPointPreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.mNotifyChanged = new Runnable() {
            @Override
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        this.mFrictionSld = null;
        this.mBadgePadding = 0;
    }
    
    AccessPointPreference(final AccessPoint mAccessPoint, final Context context, final UserBadgeCache userBadgeCache, final int n, final boolean b, final StateListDrawable mFrictionSld, final int mLevel, final IconInjector iconInjector) {
        super(context);
        this.mNotifyChanged = new Runnable() {
            @Override
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        this.setLayoutResource(R$layout.preference_access_point);
        this.setWidgetLayoutResource(this.getWidgetLayoutResourceId());
        (this.mAccessPoint = mAccessPoint).setTag(this);
        this.mLevel = mLevel;
        this.mFrictionSld = mFrictionSld;
        this.mBadgePadding = context.getResources().getDimensionPixelSize(R$dimen.wifi_preference_badge_padding);
    }
    
    private void bindFrictionImage(final ImageView imageView) {
        if (imageView != null) {
            if (this.mFrictionSld != null) {
                if (this.mAccessPoint.getSecurity() != 0 && this.mAccessPoint.getSecurity() != 4) {
                    this.mFrictionSld.setState(AccessPointPreference.STATE_SECURED);
                }
                else if (this.mAccessPoint.isMetered()) {
                    this.mFrictionSld.setState(AccessPointPreference.STATE_METERED);
                }
                imageView.setImageDrawable(this.mFrictionSld.getCurrent());
            }
        }
    }
    
    static CharSequence buildContentDescription(final Context context, final Preference preference, final AccessPoint accessPoint) {
        final CharSequence title = preference.getTitle();
        final CharSequence summary = preference.getSummary();
        CharSequence concat = title;
        if (!TextUtils.isEmpty(summary)) {
            concat = TextUtils.concat(new CharSequence[] { title, ",", summary });
        }
        final int level = accessPoint.getLevel();
        CharSequence concat2 = concat;
        if (level >= 0) {
            final int[] wifi_CONNECTION_STRENGTH = AccessPointPreference.WIFI_CONNECTION_STRENGTH;
            concat2 = concat;
            if (level < wifi_CONNECTION_STRENGTH.length) {
                concat2 = TextUtils.concat(new CharSequence[] { concat, ",", context.getString(wifi_CONNECTION_STRENGTH[level]) });
            }
        }
        String s;
        if (accessPoint.getSecurity() == 0) {
            s = context.getString(R$string.accessibility_wifi_security_type_none);
        }
        else {
            s = context.getString(R$string.accessibility_wifi_security_type_secured);
        }
        return TextUtils.concat(new CharSequence[] { concat2, ",", s });
    }
    
    private void postNotifyChanged() {
        final TextView mTitleView = this.mTitleView;
        if (mTitleView != null) {
            mTitleView.post(this.mNotifyChanged);
        }
    }
    
    static void setTitle(final AccessPointPreference accessPointPreference, final AccessPoint accessPoint) {
        accessPointPreference.setTitle(accessPoint.getTitle());
    }
    
    protected int getWidgetLayoutResourceId() {
        return R$layout.access_point_friction_widget;
    }
    
    @Override
    protected void notifyChanged() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            this.postNotifyChanged();
        }
        else {
            super.notifyChanged();
        }
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mAccessPoint == null) {
            return;
        }
        final Drawable icon = this.getIcon();
        if (icon != null) {
            icon.setLevel(this.mLevel);
        }
        final TextView mTitleView = (TextView)preferenceViewHolder.findViewById(16908310);
        if ((this.mTitleView = mTitleView) != null) {
            mTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable)null, (Drawable)null, this.mBadge, (Drawable)null);
            this.mTitleView.setCompoundDrawablePadding(this.mBadgePadding);
        }
        preferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
        this.bindFrictionImage((ImageView)preferenceViewHolder.findViewById(R$id.friction_icon));
        final View viewById = preferenceViewHolder.findViewById(R$id.two_target_divider);
        int visibility;
        if (this.shouldShowDivider()) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        viewById.setVisibility(visibility);
    }
    
    public boolean shouldShowDivider() {
        return this.mShowDivider;
    }
    
    static class IconInjector
    {
    }
    
    public static class UserBadgeCache
    {
    }
}
