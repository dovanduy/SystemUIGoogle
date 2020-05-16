// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import com.android.settingslib.drawable.UserIconDrawable;
import android.view.View;

public class UserAvatarView extends View
{
    private final UserIconDrawable mDrawable;
    
    public UserAvatarView(final Context context) {
        this(context, null);
    }
    
    public UserAvatarView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public UserAvatarView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public UserAvatarView(final Context context, final AttributeSet set, int i, int indexCount) {
        super(context, set, i, indexCount);
        this.mDrawable = new UserIconDrawable();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.UserAvatarView, i, indexCount);
        int index;
        for (indexCount = obtainStyledAttributes.getIndexCount(), i = 0; i < indexCount; ++i) {
            index = obtainStyledAttributes.getIndex(i);
            if (index == R$styleable.UserAvatarView_avatarPadding) {
                this.setAvatarPadding(obtainStyledAttributes.getDimension(index, 0.0f));
            }
            else if (index == R$styleable.UserAvatarView_frameWidth) {
                this.setFrameWidth(obtainStyledAttributes.getDimension(index, 0.0f));
            }
            else if (index == R$styleable.UserAvatarView_framePadding) {
                this.setFramePadding(obtainStyledAttributes.getDimension(index, 0.0f));
            }
            else if (index == R$styleable.UserAvatarView_frameColor) {
                this.setFrameColor(obtainStyledAttributes.getColorStateList(index));
            }
            else if (index == R$styleable.UserAvatarView_badgeDiameter) {
                this.setBadgeDiameter(obtainStyledAttributes.getDimension(index, 0.0f));
            }
            else if (index == R$styleable.UserAvatarView_badgeMargin) {
                this.setBadgeMargin(obtainStyledAttributes.getDimension(index, 0.0f));
            }
        }
        obtainStyledAttributes.recycle();
        this.setBackground((Drawable)this.mDrawable);
    }
    
    public void setActivated(final boolean activated) {
        super.setActivated(activated);
        this.mDrawable.invalidateSelf();
    }
    
    public void setAvatarPadding(final float padding) {
        this.mDrawable.setPadding(padding);
    }
    
    public void setAvatarWithBadge(final Bitmap icon, final int n) {
        this.mDrawable.setIcon(icon);
        this.mDrawable.setBadgeIfManagedUser(this.getContext(), n);
    }
    
    public void setBadgeDiameter(final float n) {
        this.mDrawable.setBadgeRadius(n * 0.5f);
    }
    
    public void setBadgeMargin(final float badgeMargin) {
        this.mDrawable.setBadgeMargin(badgeMargin);
    }
    
    public void setDrawableWithBadge(final Drawable iconDrawable, final int n) {
        if (!(iconDrawable instanceof UserIconDrawable)) {
            this.mDrawable.setIconDrawable(iconDrawable);
            this.mDrawable.setBadgeIfManagedUser(this.getContext(), n);
            return;
        }
        throw new RuntimeException("Recursively adding UserIconDrawable");
    }
    
    public void setFrameColor(final ColorStateList frameColor) {
        this.mDrawable.setFrameColor(frameColor);
    }
    
    public void setFramePadding(final float framePadding) {
        this.mDrawable.setFramePadding(framePadding);
    }
    
    public void setFrameWidth(final float frameWidth) {
        this.mDrawable.setFrameWidth(frameWidth);
    }
}
