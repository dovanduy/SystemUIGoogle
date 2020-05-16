// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import android.view.View$OnClickListener;
import com.android.systemui.statusbar.CrossFadeHelper;
import android.util.ArraySet;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import com.android.systemui.statusbar.notification.TransformState;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import com.android.internal.annotations.VisibleForTesting;
import android.widget.TextView;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.util.ContrastColorUtil;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.NotificationHeaderView;
import android.app.Notification$DecoratedCustomViewStyle;
import com.android.internal.widget.ConversationLayout;
import android.content.Context;
import android.view.View;
import android.graphics.Rect;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.TransformableView;

public abstract class NotificationViewWrapper implements TransformableView
{
    protected int mBackgroundColor;
    protected final ExpandableNotificationRow mRow;
    private final Rect mTmpRect;
    protected final View mView;
    
    protected NotificationViewWrapper(final Context context, final View mView, final ExpandableNotificationRow mRow) {
        this.mTmpRect = new Rect();
        this.mBackgroundColor = 0;
        this.mView = mView;
        this.mRow = mRow;
        this.onReinflated();
    }
    
    public static NotificationViewWrapper wrap(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        if (view.getId() == 16909463) {
            if ("bigPicture".equals(view.getTag())) {
                return new NotificationBigPictureTemplateViewWrapper(context, view, expandableNotificationRow);
            }
            if ("bigText".equals(view.getTag())) {
                return new NotificationBigTextTemplateViewWrapper(context, view, expandableNotificationRow);
            }
            if ("media".equals(view.getTag()) || "bigMediaNarrow".equals(view.getTag())) {
                return new NotificationMediaTemplateViewWrapper(context, view, expandableNotificationRow);
            }
            if ("messaging".equals(view.getTag())) {
                return new NotificationMessagingTemplateViewWrapper(context, view, expandableNotificationRow);
            }
            if ("conversation".equals(view.getTag())) {
                return new NotificationConversationTemplateViewWrapper(context, view, expandableNotificationRow);
            }
            if (Notification$DecoratedCustomViewStyle.class.equals(expandableNotificationRow.getEntry().getSbn().getNotification().getNotificationStyle())) {
                return new NotificationDecoratedCustomViewWrapper(context, view, expandableNotificationRow);
            }
            return new NotificationTemplateViewWrapper(context, view, expandableNotificationRow);
        }
        else {
            if (view instanceof NotificationHeaderView) {
                return new NotificationHeaderViewWrapper(context, view, expandableNotificationRow);
            }
            return new NotificationCustomViewWrapper(context, view, expandableNotificationRow);
        }
    }
    
    @VisibleForTesting
    boolean childrenNeedInversion(int i, final ViewGroup viewGroup) {
        if (viewGroup == null) {
            return false;
        }
        int n2;
        final int n = n2 = this.getBackgroundColor((View)viewGroup);
        if (Color.alpha(n) != 255) {
            n2 = ColorUtils.setAlphaComponent(ContrastColorUtil.compositeColors(n, i), 255);
        }
        View child;
        for (i = 0; i < viewGroup.getChildCount(); ++i) {
            child = viewGroup.getChildAt(i);
            if (child instanceof TextView) {
                if (ColorUtils.calculateContrast(((TextView)child).getCurrentTextColor(), n2) < 3.0) {
                    return true;
                }
            }
            else if (child instanceof ViewGroup && this.childrenNeedInversion(n2, (ViewGroup)child)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean disallowSingleClick(final float n, final float n2) {
        return false;
    }
    
    protected int getBackgroundColor(final View view) {
        int color = 0;
        if (view == null) {
            return 0;
        }
        final Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            color = ((ColorDrawable)background).getColor();
        }
        return color;
    }
    
    @Override
    public TransformState getCurrentState(final int n) {
        return null;
    }
    
    public int getCustomBackgroundColor() {
        int mBackgroundColor;
        if (this.mRow.isSummaryWithChildren()) {
            mBackgroundColor = 0;
        }
        else {
            mBackgroundColor = this.mBackgroundColor;
        }
        return mBackgroundColor;
    }
    
    public int getExtraMeasureHeight() {
        return 0;
    }
    
    public int getHeaderTranslation(final boolean b) {
        return 0;
    }
    
    public int getMinLayoutHeight() {
        return 0;
    }
    
    public NotificationHeaderView getNotificationHeader() {
        return null;
    }
    
    public int getOriginalIconColor() {
        return 1;
    }
    
    public View getShelfTransformationTarget() {
        return null;
    }
    
    protected void invertViewLuminosity(final View view) {
        final Paint paint = new Paint();
        final ColorMatrix colorMatrix = new ColorMatrix();
        final ColorMatrix colorMatrix2 = new ColorMatrix();
        colorMatrix.setRGB2YUV();
        colorMatrix2.set(new float[] { -1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f });
        colorMatrix.postConcat(colorMatrix2);
        colorMatrix2.setYUV2RGB();
        colorMatrix.postConcat(colorMatrix2);
        paint.setColorFilter((ColorFilter)new ColorMatrixColorFilter(colorMatrix));
        view.setLayerType(2, paint);
    }
    
    public boolean isDimmable() {
        return true;
    }
    
    protected boolean isOnView(final View view, float n, float n2) {
        for (View view2 = (View)view.getParent(); view2 != null && !(view2 instanceof ExpandableNotificationRow); view2 = (View)view2.getParent()) {
            view2.getHitRect(this.mTmpRect);
            final Rect mTmpRect = this.mTmpRect;
            n -= mTmpRect.left;
            n2 -= mTmpRect.top;
        }
        view.getHitRect(this.mTmpRect);
        return this.mTmpRect.contains((int)n, (int)n2);
    }
    
    protected boolean needsInversion(int n, final View view) {
        if (view == null) {
            return false;
        }
        if ((this.mView.getResources().getConfiguration().uiMode & 0x30) != 0x20) {
            return false;
        }
        if (this.mRow.getEntry().targetSdk >= 29) {
            return false;
        }
        final int backgroundColor = this.getBackgroundColor(view);
        if (backgroundColor != 0) {
            n = backgroundColor;
        }
        int resolveBackgroundColor = n;
        if (n == 0) {
            resolveBackgroundColor = this.resolveBackgroundColor();
        }
        final float[] array2;
        final float[] array = array2 = new float[3];
        array2[0] = 0.0f;
        array2[2] = (array2[1] = 0.0f);
        ColorUtils.colorToHSL(resolveBackgroundColor, array);
        if (array[1] != 0.0f) {
            return false;
        }
        if (array[1] == 0.0f && array[2] > 0.5) {
            n = 1;
        }
        else {
            n = 0;
        }
        return n != 0 || (view instanceof ViewGroup && this.childrenNeedInversion(resolveBackgroundColor, (ViewGroup)view));
    }
    
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
    }
    
    public void onReinflated() {
        if (this.shouldClearBackgroundOnReapply()) {
            this.mBackgroundColor = 0;
        }
        final int backgroundColor = this.getBackgroundColor(this.mView);
        if (backgroundColor != 0) {
            this.mBackgroundColor = backgroundColor;
            this.mView.setBackground((Drawable)new ColorDrawable(0));
        }
    }
    
    protected int resolveBackgroundColor() {
        final int customBackgroundColor = this.getCustomBackgroundColor();
        if (customBackgroundColor != 0) {
            return customBackgroundColor;
        }
        return this.mView.getContext().getColor(17170887);
    }
    
    public void setContentHeight(final int n, final int n2) {
    }
    
    public void setHeaderVisibleAmount(final float n) {
    }
    
    public void setIsChildInGroup(final boolean b) {
    }
    
    public void setLegacy(final boolean b) {
    }
    
    public void setRecentlyAudiblyAlerted(final boolean b) {
    }
    
    public void setRemoteInputVisible(final boolean b) {
    }
    
    public void setRemoved() {
    }
    
    public void setShelfIconVisible(final boolean b) {
    }
    
    @Override
    public void setVisible(final boolean b) {
        this.mView.animate().cancel();
        final View mView = this.mView;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        mView.setVisibility(visibility);
    }
    
    protected boolean shouldClearBackgroundOnReapply() {
        return true;
    }
    
    public boolean shouldClipToRounding(final boolean b, final boolean b2) {
        return false;
    }
    
    public void showAppOpsIcons(final ArraySet<Integer> set) {
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView) {
        CrossFadeHelper.fadeIn(this.mView);
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView, final float n) {
        CrossFadeHelper.fadeIn(this.mView, n);
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final float n) {
        CrossFadeHelper.fadeOut(this.mView, n);
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final Runnable runnable) {
        CrossFadeHelper.fadeOut(this.mView, runnable);
    }
    
    public void updateExpandability(final boolean b, final View$OnClickListener view$OnClickListener) {
    }
}
