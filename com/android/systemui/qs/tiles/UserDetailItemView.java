// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.R$id;
import com.android.systemui.FontSizeUtils;
import android.content.res.Configuration;
import com.android.systemui.R$dimen;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import com.android.internal.util.ArrayUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.res.TypedArray;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.R$layout;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.statusbar.phone.UserAvatarView;
import android.widget.LinearLayout;

public class UserDetailItemView extends LinearLayout
{
    protected static int layoutResId;
    private int mActivatedStyle;
    private UserAvatarView mAvatar;
    private TextView mName;
    private int mRegularStyle;
    private View mRestrictedPadlock;
    
    static {
        UserDetailItemView.layoutResId = R$layout.qs_user_detail_item;
    }
    
    public UserDetailItemView(final Context context) {
        this(context, null);
    }
    
    public UserDetailItemView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public UserDetailItemView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public UserDetailItemView(final Context context, final AttributeSet set, int i, int indexCount) {
        super(context, set, i, indexCount);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.UserDetailItemView, i, indexCount);
        int index;
        for (indexCount = obtainStyledAttributes.getIndexCount(), i = 0; i < indexCount; ++i) {
            index = obtainStyledAttributes.getIndex(i);
            if (index == R$styleable.UserDetailItemView_regularTextAppearance) {
                this.mRegularStyle = obtainStyledAttributes.getResourceId(index, 0);
            }
            else if (index == R$styleable.UserDetailItemView_activatedTextAppearance) {
                this.mActivatedStyle = obtainStyledAttributes.getResourceId(index, 0);
            }
        }
        obtainStyledAttributes.recycle();
    }
    
    public static UserDetailItemView convertOrInflate(final Context context, final View view, final ViewGroup viewGroup) {
        View inflate = view;
        if (!(view instanceof UserDetailItemView)) {
            inflate = LayoutInflater.from(context).inflate(UserDetailItemView.layoutResId, viewGroup, false);
        }
        return (UserDetailItemView)inflate;
    }
    
    private void updateTextStyle() {
        final boolean contains = ArrayUtils.contains(this.getDrawableState(), 16843518);
        final TextView mName = this.mName;
        int textAppearance;
        if (contains) {
            textAppearance = this.mActivatedStyle;
        }
        else {
            textAppearance = this.mRegularStyle;
        }
        mName.setTextAppearance(textAppearance);
    }
    
    public void bind(final String text, final Bitmap bitmap, final int n) {
        this.mName.setText((CharSequence)text);
        this.mAvatar.setAvatarWithBadge(bitmap, n);
    }
    
    public void bind(final String text, final Drawable drawable, final int n) {
        this.mName.setText((CharSequence)text);
        this.mAvatar.setDrawableWithBadge(drawable, n);
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.updateTextStyle();
    }
    
    protected int getFontSizeDimen() {
        return R$dimen.qs_detail_item_secondary_text_size;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FontSizeUtils.updateFontSize(this.mName, this.getFontSizeDimen());
    }
    
    protected void onFinishInflate() {
        this.mAvatar = (UserAvatarView)this.findViewById(R$id.user_picture);
        final TextView mName = (TextView)this.findViewById(R$id.user_name);
        this.mName = mName;
        if (this.mRegularStyle == 0) {
            this.mRegularStyle = mName.getExplicitStyle();
        }
        if (this.mActivatedStyle == 0) {
            this.mActivatedStyle = this.mName.getExplicitStyle();
        }
        this.updateTextStyle();
        this.mRestrictedPadlock = this.findViewById(R$id.restricted_padlock);
    }
    
    public void setAvatarEnabled(final boolean enabled) {
        this.mAvatar.setEnabled(enabled);
    }
    
    public void setDisabledByAdmin(final boolean b) {
        final View mRestrictedPadlock = this.mRestrictedPadlock;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mRestrictedPadlock.setVisibility(visibility);
        this.mName.setEnabled(b ^ true);
        this.mAvatar.setEnabled(b ^ true);
    }
    
    public void setEnabled(final boolean b) {
        this.mName.setEnabled(b);
        this.mAvatar.setEnabled(b);
    }
}
