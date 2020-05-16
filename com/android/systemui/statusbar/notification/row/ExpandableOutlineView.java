// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.graphics.RectF;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import android.graphics.Path$Op;
import android.graphics.Canvas;
import android.content.res.Resources;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$bool;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.content.Context;
import java.util.function.Function;
import android.view.View;
import java.util.function.BiConsumer;
import com.android.systemui.R$id;
import android.view.ViewOutlineProvider;
import android.graphics.Rect;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import android.graphics.Path;
import com.android.systemui.statusbar.notification.AnimatableProperty;

public abstract class ExpandableOutlineView extends ExpandableView
{
    private static final AnimatableProperty BOTTOM_ROUNDNESS;
    private static final Path EMPTY_PATH;
    private static final AnimationProperties ROUNDNESS_PROPERTIES;
    private static final AnimatableProperty TOP_ROUNDNESS;
    private boolean mAlwaysRoundBothCorners;
    private int mBackgroundTop;
    private float mBottomRoundness;
    private final Path mClipPath;
    private float mCurrentBottomRoundness;
    private float mCurrentTopRoundness;
    private boolean mCustomOutline;
    private float mDistanceToTopRoundness;
    private float mOutlineAlpha;
    protected float mOutlineRadius;
    private final Rect mOutlineRect;
    private final ViewOutlineProvider mProvider;
    protected boolean mShouldTranslateContents;
    private Path mTmpPath;
    private boolean mTopAmountRounded;
    private float mTopRoundness;
    
    static {
        TOP_ROUNDNESS = AnimatableProperty.from("topRoundness", (BiConsumer<View, Float>)_$$Lambda$ExpandableOutlineView$lgIjKBD4iaJhFeEZ5izPzOddhds.INSTANCE, (Function<View, Float>)_$$Lambda$iDWyu_PNFZfGQr9kcCLSWoFYxpI.INSTANCE, R$id.top_roundess_animator_tag, R$id.top_roundess_animator_end_tag, R$id.top_roundess_animator_start_tag);
        BOTTOM_ROUNDNESS = AnimatableProperty.from("bottomRoundness", (BiConsumer<View, Float>)_$$Lambda$ExpandableOutlineView$ZLqiUGCQzNj3P4m8kfbTwbzfyaI.INSTANCE, (Function<View, Float>)_$$Lambda$RLFq7_ULx7AWbuaAJNsAxNrN1PI.INSTANCE, R$id.bottom_roundess_animator_tag, R$id.bottom_roundess_animator_end_tag, R$id.bottom_roundess_animator_start_tag);
        final AnimationProperties roundness_PROPERTIES = new AnimationProperties();
        roundness_PROPERTIES.setDuration(360L);
        ROUNDNESS_PROPERTIES = roundness_PROPERTIES;
        EMPTY_PATH = new Path();
    }
    
    public ExpandableOutlineView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mOutlineRect = new Rect();
        this.mClipPath = new Path();
        this.mOutlineAlpha = -1.0f;
        this.mTmpPath = new Path();
        this.mDistanceToTopRoundness = -1.0f;
        this.setOutlineProvider(this.mProvider = new ViewOutlineProvider() {
            public void getOutline(final View view, final Outline outline) {
                if (!ExpandableOutlineView.this.mCustomOutline && ExpandableOutlineView.this.mCurrentTopRoundness == 0.0f && ExpandableOutlineView.this.mCurrentBottomRoundness == 0.0f && !ExpandableOutlineView.this.mAlwaysRoundBothCorners && !ExpandableOutlineView.this.mTopAmountRounded) {
                    final ExpandableOutlineView this$0 = ExpandableOutlineView.this;
                    int n;
                    if (this$0.mShouldTranslateContents) {
                        n = (int)this$0.getTranslation();
                    }
                    else {
                        n = 0;
                    }
                    final int max = Math.max(n, 0);
                    final ExpandableOutlineView this$2 = ExpandableOutlineView.this;
                    final int b = this$2.mClipTopAmount + this$2.mBackgroundTop;
                    outline.setRect(max, b, ExpandableOutlineView.this.getWidth() + Math.min(n, 0), Math.max(ExpandableOutlineView.this.getActualHeight() - ExpandableOutlineView.this.mClipBottomAmount, b));
                }
                else {
                    final Path clipPath = ExpandableOutlineView.this.getClipPath(false);
                    if (clipPath != null) {
                        outline.setPath(clipPath);
                    }
                }
                outline.setAlpha(ExpandableOutlineView.this.mOutlineAlpha);
            }
        });
        this.initDimens();
    }
    
    public static void getRoundedRectPath(final int n, final int n2, final int n3, final int n4, float b, float b2, final Path path) {
        path.reset();
        final float n5 = (float)((n3 - n) / 2);
        final float min = Math.min(n5, b);
        final float min2 = Math.min(n5, b2);
        if (b > 0.0f) {
            final float n6 = (float)n;
            final float n7 = (float)n2;
            b += n7;
            path.moveTo(n6, b);
            path.quadTo(n6, n7, n6 + min, n7);
            final float n8 = (float)n3;
            path.lineTo(n8 - min, n7);
            path.quadTo(n8, n7, n8, b);
        }
        else {
            final float n9 = (float)n;
            b = (float)n2;
            path.moveTo(n9, b);
            path.lineTo((float)n3, b);
        }
        if (b2 > 0.0f) {
            final float n10 = (float)n3;
            b = (float)n4;
            b2 = b - b2;
            path.lineTo(n10, b2);
            path.quadTo(n10, b, n10 - min2, b);
            final float n11 = (float)n;
            path.lineTo(min2 + n11, b);
            path.quadTo(n11, b, n11, b2);
        }
        else {
            b = (float)n3;
            b2 = (float)n4;
            path.lineTo(b, b2);
            path.lineTo((float)n, b2);
        }
        path.close();
    }
    
    private void initDimens() {
        final Resources resources = this.getResources();
        this.mShouldTranslateContents = resources.getBoolean(R$bool.config_translateNotificationContentsOnSwipe);
        this.mOutlineRadius = resources.getDimension(R$dimen.notification_shadow_radius);
        if (!(this.mAlwaysRoundBothCorners = resources.getBoolean(R$bool.config_clipNotificationsToOutline))) {
            this.mOutlineRadius = (float)resources.getDimensionPixelSize(Utils.getThemeAttr(super.mContext, 16844145));
        }
        this.setClipToOutline(this.mAlwaysRoundBothCorners);
    }
    
    private void setBottomRoundnessInternal(final float mCurrentBottomRoundness) {
        this.mCurrentBottomRoundness = mCurrentBottomRoundness;
        this.applyRoundness();
    }
    
    private void setTopRoundnessInternal(final float mCurrentTopRoundness) {
        this.mCurrentTopRoundness = mCurrentTopRoundness;
        this.applyRoundness();
    }
    
    protected void applyRoundness() {
        this.invalidateOutline();
        this.invalidate();
    }
    
    protected boolean childNeedsClipping(final View view) {
        return false;
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        canvas.save();
        Path mClipPath;
        if (this.mTopAmountRounded && this.topAmountNeedsClipping()) {
            final int n2 = (int)(-super.mExtraWidthForClipping / 2.0f);
            final int n3 = (int)(super.mClipTopAmount - this.mDistanceToTopRoundness);
            getRoundedRectPath(n2, n3, (int)(super.mExtraWidthForClipping + n2) + this.getWidth(), (int)Math.max((float)super.mMinimumHeightForClipping, Math.max((float)(this.getActualHeight() - super.mClipBottomAmount), n3 + this.mOutlineRadius)), this.mOutlineRadius, 0.0f, this.mClipPath);
            mClipPath = this.mClipPath;
        }
        else {
            mClipPath = null;
        }
        final boolean childNeedsClipping = this.childNeedsClipping(view);
        int n4 = 0;
        if (childNeedsClipping) {
            Path path;
            if ((path = this.getCustomClipPath(view)) == null) {
                path = this.getClipPath(false);
            }
            n4 = n4;
            if (path != null) {
                if (mClipPath != null) {
                    path.op(mClipPath, Path$Op.INTERSECT);
                }
                canvas.clipPath(path);
                n4 = 1;
            }
        }
        if (n4 == 0 && mClipPath != null) {
            canvas.clipPath(mClipPath);
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restore();
        return drawChild;
    }
    
    protected Path getClipPath(final boolean b) {
        float n;
        if (this.mAlwaysRoundBothCorners) {
            n = this.mOutlineRadius;
        }
        else {
            n = this.getCurrentBackgroundRadiusTop();
        }
        int left;
        int right;
        int top;
        int bottom;
        if (!this.mCustomOutline) {
            int n2;
            if (this.mShouldTranslateContents && !b) {
                n2 = (int)this.getTranslation();
            }
            else {
                n2 = 0;
            }
            final int n3 = (int)(super.mExtraWidthForClipping / 2.0f);
            left = Math.max(n2, 0) - n3;
            final int n4 = super.mClipTopAmount + this.mBackgroundTop;
            right = this.getWidth() + n3 + Math.min(n2, 0);
            final int max = Math.max(super.mMinimumHeightForClipping, Math.max(this.getActualHeight() - super.mClipBottomAmount, (int)(n4 + n)));
            top = n4;
            bottom = max;
        }
        else {
            final Rect mOutlineRect = this.mOutlineRect;
            left = mOutlineRect.left;
            top = mOutlineRect.top;
            right = mOutlineRect.right;
            bottom = mOutlineRect.bottom;
        }
        final int n5 = bottom - top;
        if (n5 == 0) {
            return ExpandableOutlineView.EMPTY_PATH;
        }
        float n6;
        if (this.mAlwaysRoundBothCorners) {
            n6 = this.mOutlineRadius;
        }
        else {
            n6 = this.getCurrentBackgroundRadiusBottom();
        }
        final float n7 = n + n6;
        final float n8 = (float)n5;
        float n9 = n;
        float n10 = n6;
        if (n7 > n8) {
            final float n11 = n7 - n8;
            final float mCurrentTopRoundness = this.mCurrentTopRoundness;
            final float mCurrentBottomRoundness = this.mCurrentBottomRoundness;
            n9 = n - n11 * mCurrentTopRoundness / (mCurrentTopRoundness + mCurrentBottomRoundness);
            n10 = n6 - n11 * mCurrentBottomRoundness / (mCurrentTopRoundness + mCurrentBottomRoundness);
        }
        getRoundedRectPath(left, top, right, bottom, n9, n10, this.mTmpPath);
        return this.mTmpPath;
    }
    
    protected float getCurrentBackgroundRadiusBottom() {
        return this.mCurrentBottomRoundness * this.mOutlineRadius;
    }
    
    public float getCurrentBackgroundRadiusTop() {
        if (this.mTopAmountRounded) {
            return this.mOutlineRadius;
        }
        return this.mCurrentTopRoundness * this.mOutlineRadius;
    }
    
    public float getCurrentBottomRoundness() {
        return this.mCurrentBottomRoundness;
    }
    
    public float getCurrentTopRoundness() {
        return this.mCurrentTopRoundness;
    }
    
    public Path getCustomClipPath(final View view) {
        return null;
    }
    
    @Override
    public float getOutlineAlpha() {
        return this.mOutlineAlpha;
    }
    
    @Override
    public int getOutlineTranslation() {
        int left;
        if (this.mCustomOutline) {
            left = this.mOutlineRect.left;
        }
        else {
            left = (int)this.getTranslation();
        }
        return left;
    }
    
    protected boolean isClippingNeeded() {
        return this.mAlwaysRoundBothCorners || this.mCustomOutline || this.getTranslation() != 0.0f;
    }
    
    protected boolean needsOutline() {
        final boolean childInGroup = this.isChildInGroup();
        boolean b = false;
        final boolean b2 = false;
        if (childInGroup) {
            boolean b3 = b2;
            if (this.isGroupExpanded()) {
                b3 = b2;
                if (!this.isGroupExpansionChanging()) {
                    b3 = true;
                }
            }
            return b3;
        }
        if (this.isSummaryWithChildren()) {
            if (!this.isGroupExpanded() || this.isGroupExpansionChanging()) {
                b = true;
            }
            return b;
        }
        return true;
    }
    
    public void onDensityOrFontScaleChanged() {
        this.initDimens();
        this.applyRoundness();
    }
    
    @Override
    public void setActualHeight(final int n, final boolean b) {
        final int actualHeight = this.getActualHeight();
        super.setActualHeight(n, b);
        if (actualHeight != n) {
            this.applyRoundness();
        }
    }
    
    public boolean setBottomRoundness(final float mBottomRoundness, final boolean b) {
        if (this.mBottomRoundness != mBottomRoundness) {
            this.mBottomRoundness = mBottomRoundness;
            PropertyAnimator.setProperty(this, ExpandableOutlineView.BOTTOM_ROUNDNESS, mBottomRoundness, ExpandableOutlineView.ROUNDNESS_PROPERTIES, b);
            return true;
        }
        return false;
    }
    
    @Override
    public void setClipBottomAmount(final int clipBottomAmount) {
        final int clipBottomAmount2 = this.getClipBottomAmount();
        super.setClipBottomAmount(clipBottomAmount);
        if (clipBottomAmount2 != clipBottomAmount) {
            this.applyRoundness();
        }
    }
    
    @Override
    public void setClipTopAmount(final int clipTopAmount) {
        final int clipTopAmount2 = this.getClipTopAmount();
        super.setClipTopAmount(clipTopAmount);
        if (clipTopAmount2 != clipTopAmount) {
            this.applyRoundness();
        }
    }
    
    @Override
    public void setDistanceToTopRoundness(final float n) {
        super.setDistanceToTopRoundness(n);
        if (n != this.mDistanceToTopRoundness) {
            this.mTopAmountRounded = (n >= 0.0f);
            this.mDistanceToTopRoundness = n;
            this.applyRoundness();
        }
    }
    
    @Override
    public void setExtraWidthForClipping(final float extraWidthForClipping) {
        super.setExtraWidthForClipping(extraWidthForClipping);
        this.invalidate();
    }
    
    @Override
    public void setMinimumHeightForClipping(final int minimumHeightForClipping) {
        super.setMinimumHeightForClipping(minimumHeightForClipping);
        this.invalidate();
    }
    
    protected void setOutlineAlpha(final float mOutlineAlpha) {
        if (mOutlineAlpha != this.mOutlineAlpha) {
            this.mOutlineAlpha = mOutlineAlpha;
            this.applyRoundness();
        }
    }
    
    protected void setOutlineRect(final float a, final float a2, final float n, final float n2) {
        this.mCustomOutline = true;
        this.mOutlineRect.set((int)a, (int)a2, (int)n, (int)n2);
        final Rect mOutlineRect = this.mOutlineRect;
        mOutlineRect.bottom = (int)Math.max(a2, (float)mOutlineRect.bottom);
        final Rect mOutlineRect2 = this.mOutlineRect;
        mOutlineRect2.right = (int)Math.max(a, (float)mOutlineRect2.right);
        this.applyRoundness();
    }
    
    protected void setOutlineRect(final RectF rectF) {
        if (rectF != null) {
            this.setOutlineRect(rectF.left, rectF.top, rectF.right, rectF.bottom);
        }
        else {
            this.mCustomOutline = false;
            this.applyRoundness();
        }
    }
    
    public boolean setTopRoundness(final float mTopRoundness, final boolean b) {
        if (this.mTopRoundness != mTopRoundness) {
            this.mTopRoundness = mTopRoundness;
            PropertyAnimator.setProperty(this, ExpandableOutlineView.TOP_ROUNDNESS, mTopRoundness, ExpandableOutlineView.ROUNDNESS_PROPERTIES, b);
            return true;
        }
        return false;
    }
    
    public boolean topAmountNeedsClipping() {
        return true;
    }
    
    public void updateOutline() {
        if (this.mCustomOutline) {
            return;
        }
        ViewOutlineProvider mProvider;
        if (this.needsOutline()) {
            mProvider = this.mProvider;
        }
        else {
            mProvider = null;
        }
        this.setOutlineProvider(mProvider);
    }
}
