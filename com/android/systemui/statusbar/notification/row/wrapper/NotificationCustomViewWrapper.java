// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import com.android.internal.graphics.ColorUtils;
import com.android.systemui.R$color;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.View;
import android.content.Context;

public class NotificationCustomViewWrapper extends NotificationViewWrapper
{
    private boolean mIsLegacy;
    private int mLegacyColor;
    
    protected NotificationCustomViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
        this.mLegacyColor = expandableNotificationRow.getContext().getColor(R$color.notification_legacy_background_color);
    }
    
    @Override
    public int getCustomBackgroundColor() {
        final int customBackgroundColor = super.getCustomBackgroundColor();
        if (customBackgroundColor == 0 && this.mIsLegacy) {
            return this.mLegacyColor;
        }
        return customBackgroundColor;
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        super.onContentUpdated(expandableNotificationRow);
        if (this.needsInversion(super.mBackgroundColor, super.mView)) {
            this.invertViewLuminosity(super.mView);
            final float[] array2;
            final float[] array = array2 = new float[3];
            array2[0] = 0.0f;
            array2[2] = (array2[1] = 0.0f);
            ColorUtils.colorToHSL(super.mBackgroundColor, array);
            if (super.mBackgroundColor != 0 && array[2] > 0.5) {
                array[2] = 1.0f - array[2];
                super.mBackgroundColor = ColorUtils.HSLToColor(array);
            }
        }
    }
    
    @Override
    public void setLegacy(final boolean b) {
        super.setLegacy(b);
        this.mIsLegacy = b;
    }
    
    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        final View mView = super.mView;
        float alpha;
        if (visible) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.0f;
        }
        mView.setAlpha(alpha);
    }
    
    @Override
    protected boolean shouldClearBackgroundOnReapply() {
        return false;
    }
    
    @Override
    public boolean shouldClipToRounding(final boolean b, final boolean b2) {
        return true;
    }
}
