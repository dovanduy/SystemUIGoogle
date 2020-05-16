// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.internal.widget.ViewClippingUtil;
import com.android.internal.widget.MessagingPropertyAnimator;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import android.widget.ProgressBar;
import android.widget.ImageView;
import com.android.internal.widget.MessagingImageMessage;
import android.widget.TextView;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.R$id;
import android.view.View;
import android.view.animation.Interpolator;
import android.util.Pools$SimplePool;
import com.android.internal.widget.ViewClippingUtil$ClippingParameters;

public class TransformState
{
    private static ViewClippingUtil$ClippingParameters CLIPPING_PARAMETERS;
    private static final int TRANSFORMATION_START_SCLALE_X;
    private static final int TRANSFORMATION_START_SCLALE_Y;
    private static final int TRANSFORMATION_START_X;
    private static final int TRANSFORMATION_START_Y;
    private static Pools$SimplePool<TransformState> sInstancePool;
    protected Interpolator mDefaultInterpolator;
    private int[] mOwnPosition;
    private boolean mSameAsAny;
    protected TransformInfo mTransformInfo;
    private float mTransformationEndX;
    private float mTransformationEndY;
    protected View mTransformedView;
    
    static {
        TRANSFORMATION_START_X = R$id.transformation_start_x_tag;
        TRANSFORMATION_START_Y = R$id.transformation_start_y_tag;
        TRANSFORMATION_START_SCLALE_X = R$id.transformation_start_scale_x_tag;
        TRANSFORMATION_START_SCLALE_Y = R$id.transformation_start_scale_y_tag;
        TransformState.sInstancePool = (Pools$SimplePool<TransformState>)new Pools$SimplePool(40);
        TransformState.CLIPPING_PARAMETERS = (ViewClippingUtil$ClippingParameters)new ViewClippingUtil$ClippingParameters() {
            public void onClippingStateChanged(final View view, final boolean b) {
                if (view instanceof ExpandableNotificationRow) {
                    final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                    if (b) {
                        expandableNotificationRow.setClipToActualHeight(true);
                    }
                    else if (expandableNotificationRow.isChildInGroup()) {
                        expandableNotificationRow.setClipToActualHeight(false);
                    }
                }
            }
            
            public boolean shouldFinish(final View view) {
                return view instanceof ExpandableNotificationRow && (((ExpandableNotificationRow)view).isChildInGroup() ^ true);
            }
        };
    }
    
    public TransformState() {
        this.mOwnPosition = new int[2];
        this.mTransformationEndY = -1.0f;
        this.mTransformationEndX = -1.0f;
        this.mDefaultInterpolator = Interpolators.FAST_OUT_SLOW_IN;
    }
    
    public static TransformState createFrom(final View view, final TransformInfo transformInfo) {
        if (view instanceof TextView) {
            final TextViewTransformState obtain = TextViewTransformState.obtain();
            obtain.initFrom(view, transformInfo);
            return obtain;
        }
        if (view.getId() == 16908717) {
            final ActionListTransformState obtain2 = ActionListTransformState.obtain();
            obtain2.initFrom(view, transformInfo);
            return obtain2;
        }
        if (view.getId() == 16909226) {
            final MessagingLayoutTransformState obtain3 = MessagingLayoutTransformState.obtain();
            obtain3.initFrom(view, transformInfo);
            return obtain3;
        }
        if (view instanceof MessagingImageMessage) {
            final MessagingImageTransformState obtain4 = MessagingImageTransformState.obtain();
            obtain4.initFrom(view, transformInfo);
            return obtain4;
        }
        if (view instanceof ImageView) {
            final ImageTransformState obtain5 = ImageTransformState.obtain();
            obtain5.initFrom(view, transformInfo);
            if (view.getId() == 16909333) {
                obtain5.setIsSameAsAnyView(true);
            }
            return obtain5;
        }
        if (view instanceof ProgressBar) {
            final ProgressTransformState obtain6 = ProgressTransformState.obtain();
            obtain6.initFrom(view, transformInfo);
            return obtain6;
        }
        final TransformState obtain7 = obtain();
        obtain7.initFrom(view, transformInfo);
        return obtain7;
    }
    
    public static TransformState obtain() {
        final TransformState transformState = (TransformState)TransformState.sInstancePool.acquire();
        if (transformState != null) {
            return transformState;
        }
        return new TransformState();
    }
    
    private void setTransformationStartScaleX(final float f) {
        this.mTransformedView.setTag(TransformState.TRANSFORMATION_START_SCLALE_X, (Object)f);
    }
    
    private void setTransformationStartScaleY(final float f) {
        this.mTransformedView.setTag(TransformState.TRANSFORMATION_START_SCLALE_Y, (Object)f);
    }
    
    private void transformViewTo(final TransformState transformState, int n, final ViewTransformationHelper.CustomTransformation customTransformation, float n2) {
        final View mTransformedView = this.mTransformedView;
        final boolean b = (n & 0x1) != 0x0;
        if ((n & 0x10) != 0x0) {
            n = 1;
        }
        else {
            n = 0;
        }
        final boolean transformScale = this.transformScale(transformState);
        if (n2 == 0.0f) {
            if (b) {
                float transformationStartX = this.getTransformationStartX();
                if (transformationStartX == -1.0f) {
                    transformationStartX = mTransformedView.getTranslationX();
                }
                this.setTransformationStartX(transformationStartX);
            }
            if (n != 0) {
                float transformationStartY = this.getTransformationStartY();
                if (transformationStartY == -1.0f) {
                    transformationStartY = mTransformedView.getTranslationY();
                }
                this.setTransformationStartY(transformationStartY);
            }
            transformState.getTransformedView();
            if (transformScale && transformState.getViewWidth() != this.getViewWidth()) {
                this.setTransformationStartScaleX(mTransformedView.getScaleX());
                mTransformedView.setPivotX(0.0f);
            }
            else {
                this.setTransformationStartScaleX(-1.0f);
            }
            if (transformScale && transformState.getViewHeight() != this.getViewHeight()) {
                this.setTransformationStartScaleY(mTransformedView.getScaleY());
                mTransformedView.setPivotY(0.0f);
            }
            else {
                this.setTransformationStartScaleY(-1.0f);
            }
            this.setClippingDeactivated(mTransformedView, true);
        }
        final float interpolation = this.mDefaultInterpolator.getInterpolation(n2);
        final int[] laidOutLocationOnScreen = transformState.getLaidOutLocationOnScreen();
        final int[] laidOutLocationOnScreen2 = this.getLaidOutLocationOnScreen();
        if (b) {
            float n3;
            float mTransformationEndX = n3 = (float)(laidOutLocationOnScreen[0] - laidOutLocationOnScreen2[0]);
            float n4 = 0.0f;
            Label_0318: {
                if (customTransformation != null) {
                    if (customTransformation.customTransformTarget(this, transformState)) {
                        mTransformationEndX = this.mTransformationEndX;
                    }
                    final Interpolator customInterpolator = customTransformation.getCustomInterpolator(1, false);
                    n3 = mTransformationEndX;
                    if (customInterpolator != null) {
                        final float interpolation2 = customInterpolator.getInterpolation(n2);
                        n3 = mTransformationEndX;
                        n4 = interpolation2;
                        break Label_0318;
                    }
                }
                n4 = interpolation;
            }
            mTransformedView.setTranslationX(NotificationUtils.interpolate(this.getTransformationStartX(), n3, n4));
        }
        if (n != 0) {
            float n5;
            float mTransformationEndY = n5 = (float)(laidOutLocationOnScreen[1] - laidOutLocationOnScreen2[1]);
            Label_0410: {
                if (customTransformation != null) {
                    if (customTransformation.customTransformTarget(this, transformState)) {
                        mTransformationEndY = this.mTransformationEndY;
                    }
                    final Interpolator customInterpolator2 = customTransformation.getCustomInterpolator(16, false);
                    n5 = mTransformationEndY;
                    if (customInterpolator2 != null) {
                        n2 = customInterpolator2.getInterpolation(n2);
                        break Label_0410;
                    }
                }
                n2 = interpolation;
                mTransformationEndY = n5;
            }
            mTransformedView.setTranslationY(NotificationUtils.interpolate(this.getTransformationStartY(), mTransformationEndY, n2));
        }
        if (transformScale) {
            transformState.getTransformedView();
            n2 = this.getTransformationStartScaleX();
            if (n2 != -1.0f) {
                mTransformedView.setScaleX(NotificationUtils.interpolate(n2, transformState.getViewWidth() / (float)this.getViewWidth(), interpolation));
            }
            n2 = this.getTransformationStartScaleY();
            if (n2 != -1.0f) {
                mTransformedView.setScaleY(NotificationUtils.interpolate(n2, transformState.getViewHeight() / (float)this.getViewHeight(), interpolation));
            }
        }
    }
    
    public void abortTransformation() {
        final View mTransformedView = this.mTransformedView;
        final int transformation_START_X = TransformState.TRANSFORMATION_START_X;
        final Float value = -1.0f;
        mTransformedView.setTag(transformation_START_X, (Object)value);
        this.mTransformedView.setTag(TransformState.TRANSFORMATION_START_Y, (Object)value);
        this.mTransformedView.setTag(TransformState.TRANSFORMATION_START_SCLALE_X, (Object)value);
        this.mTransformedView.setTag(TransformState.TRANSFORMATION_START_SCLALE_Y, (Object)value);
    }
    
    public void appear(final float n, final TransformableView transformableView) {
        if (n == 0.0f) {
            this.prepareFadeIn();
        }
        CrossFadeHelper.fadeIn(this.mTransformedView, n);
    }
    
    public void disappear(final float n, final TransformableView transformableView) {
        CrossFadeHelper.fadeOut(this.mTransformedView, n);
    }
    
    public void ensureVisible() {
        if (this.mTransformedView.getVisibility() == 4 || this.mTransformedView.getAlpha() != 1.0f) {
            this.mTransformedView.setAlpha(1.0f);
            this.mTransformedView.setVisibility(0);
        }
    }
    
    public int[] getLaidOutLocationOnScreen() {
        final int[] locationOnScreen = this.getLocationOnScreen();
        locationOnScreen[0] -= (int)this.mTransformedView.getTranslationX();
        locationOnScreen[1] -= (int)this.mTransformedView.getTranslationY();
        return locationOnScreen;
    }
    
    public int[] getLocationOnScreen() {
        this.mTransformedView.getLocationOnScreen(this.mOwnPosition);
        final int[] mOwnPosition = this.mOwnPosition;
        mOwnPosition[0] -= (int)((1.0f - this.mTransformedView.getScaleX()) * this.mTransformedView.getPivotX());
        final int[] mOwnPosition2 = this.mOwnPosition;
        mOwnPosition2[1] -= (int)((1.0f - this.mTransformedView.getScaleY()) * this.mTransformedView.getPivotY());
        final int[] mOwnPosition3 = this.mOwnPosition;
        mOwnPosition3[1] -= MessagingPropertyAnimator.getTop(this.mTransformedView) - MessagingPropertyAnimator.getLayoutTop(this.mTransformedView);
        return this.mOwnPosition;
    }
    
    public float getTransformationStartScaleX() {
        final Object tag = this.mTransformedView.getTag(TransformState.TRANSFORMATION_START_SCLALE_X);
        float floatValue;
        if (tag == null) {
            floatValue = -1.0f;
        }
        else {
            floatValue = (float)tag;
        }
        return floatValue;
    }
    
    public float getTransformationStartScaleY() {
        final Object tag = this.mTransformedView.getTag(TransformState.TRANSFORMATION_START_SCLALE_Y);
        float floatValue;
        if (tag == null) {
            floatValue = -1.0f;
        }
        else {
            floatValue = (float)tag;
        }
        return floatValue;
    }
    
    public float getTransformationStartX() {
        final Object tag = this.mTransformedView.getTag(TransformState.TRANSFORMATION_START_X);
        float floatValue;
        if (tag == null) {
            floatValue = -1.0f;
        }
        else {
            floatValue = (float)tag;
        }
        return floatValue;
    }
    
    public float getTransformationStartY() {
        final Object tag = this.mTransformedView.getTag(TransformState.TRANSFORMATION_START_Y);
        float floatValue;
        if (tag == null) {
            floatValue = -1.0f;
        }
        else {
            floatValue = (float)tag;
        }
        return floatValue;
    }
    
    public View getTransformedView() {
        return this.mTransformedView;
    }
    
    protected int getViewHeight() {
        return this.mTransformedView.getHeight();
    }
    
    protected int getViewWidth() {
        return this.mTransformedView.getWidth();
    }
    
    public void initFrom(final View mTransformedView, final TransformInfo mTransformInfo) {
        this.mTransformedView = mTransformedView;
        this.mTransformInfo = mTransformInfo;
    }
    
    public void prepareFadeIn() {
        this.resetTransformedView();
    }
    
    public void recycle() {
        this.reset();
        if (this.getClass() == TransformState.class) {
            TransformState.sInstancePool.release((Object)this);
        }
    }
    
    protected void reset() {
        this.mTransformedView = null;
        this.mTransformInfo = null;
        this.mSameAsAny = false;
        this.mTransformationEndX = -1.0f;
        this.mTransformationEndY = -1.0f;
        this.mDefaultInterpolator = Interpolators.FAST_OUT_SLOW_IN;
    }
    
    protected void resetTransformedView() {
        this.mTransformedView.setTranslationX(0.0f);
        this.mTransformedView.setTranslationY(0.0f);
        this.mTransformedView.setScaleX(1.0f);
        this.mTransformedView.setScaleY(1.0f);
        this.setClippingDeactivated(this.mTransformedView, false);
        this.abortTransformation();
    }
    
    protected boolean sameAs(final TransformState transformState) {
        return this.mSameAsAny;
    }
    
    protected void setClippingDeactivated(final View view, final boolean b) {
        ViewClippingUtil.setClippingDeactivated(view, b, TransformState.CLIPPING_PARAMETERS);
    }
    
    public void setDefaultInterpolator(final Interpolator mDefaultInterpolator) {
        this.mDefaultInterpolator = mDefaultInterpolator;
    }
    
    public void setIsSameAsAnyView(final boolean mSameAsAny) {
        this.mSameAsAny = mSameAsAny;
    }
    
    public void setTransformationEndY(final float mTransformationEndY) {
        this.mTransformationEndY = mTransformationEndY;
    }
    
    public void setTransformationStartX(final float f) {
        this.mTransformedView.setTag(TransformState.TRANSFORMATION_START_X, (Object)f);
    }
    
    public void setTransformationStartY(final float f) {
        this.mTransformedView.setTag(TransformState.TRANSFORMATION_START_Y, (Object)f);
    }
    
    public void setVisible(final boolean b, final boolean b2) {
        if (!b2 && this.mTransformedView.getVisibility() == 8) {
            return;
        }
        if (this.mTransformedView.getVisibility() != 8) {
            final View mTransformedView = this.mTransformedView;
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 4;
            }
            mTransformedView.setVisibility(visibility);
        }
        this.mTransformedView.animate().cancel();
        final View mTransformedView2 = this.mTransformedView;
        float alpha;
        if (b) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.0f;
        }
        mTransformedView2.setAlpha(alpha);
        this.resetTransformedView();
    }
    
    protected boolean transformScale(final TransformState transformState) {
        return this.sameAs(transformState);
    }
    
    public void transformViewFrom(final TransformState transformState, final float n) {
        this.mTransformedView.animate().cancel();
        if (this.sameAs(transformState)) {
            this.ensureVisible();
        }
        else {
            CrossFadeHelper.fadeIn(this.mTransformedView, n);
        }
        this.transformViewFullyFrom(transformState, n);
    }
    
    protected void transformViewFrom(final TransformState transformState, int n, final ViewTransformationHelper.CustomTransformation customTransformation, float n2) {
        final View mTransformedView = this.mTransformedView;
        final boolean b = (n & 0x1) != 0x0;
        if ((n & 0x10) != 0x0) {
            n = 1;
        }
        else {
            n = 0;
        }
        final int viewHeight = this.getViewHeight();
        final int viewHeight2 = transformState.getViewHeight();
        final boolean b2 = viewHeight2 != viewHeight && viewHeight2 != 0 && viewHeight != 0;
        final int viewWidth = this.getViewWidth();
        final int viewWidth2 = transformState.getViewWidth();
        final boolean b3 = viewWidth2 != viewWidth && viewWidth2 != 0 && viewWidth != 0;
        final boolean b4 = this.transformScale(transformState) && (b2 || b3);
        final float n3 = fcmpl(n2, 0.0f);
        if (n3 == 0 || (b && this.getTransformationStartX() == -1.0f) || (n != 0 && this.getTransformationStartY() == -1.0f) || (b4 && this.getTransformationStartScaleX() == -1.0f && b3) || (b4 && this.getTransformationStartScaleY() == -1.0f && b2)) {
            int[] array;
            if (n3 != 0) {
                array = transformState.getLaidOutLocationOnScreen();
            }
            else {
                array = transformState.getLocationOnScreen();
            }
            final int[] laidOutLocationOnScreen = this.getLaidOutLocationOnScreen();
            if (customTransformation == null || !customTransformation.initTransformation(this, transformState)) {
                if (b) {
                    this.setTransformationStartX((float)(array[0] - laidOutLocationOnScreen[0]));
                }
                if (n != 0) {
                    this.setTransformationStartY((float)(array[1] - laidOutLocationOnScreen[1]));
                }
                final View transformedView = transformState.getTransformedView();
                if (b4 && b3) {
                    this.setTransformationStartScaleX(viewWidth2 * transformedView.getScaleX() / viewWidth);
                    mTransformedView.setPivotX(0.0f);
                }
                else {
                    this.setTransformationStartScaleX(-1.0f);
                }
                if (b4 && b2) {
                    this.setTransformationStartScaleY(viewHeight2 * transformedView.getScaleY() / viewHeight);
                    mTransformedView.setPivotY(0.0f);
                }
                else {
                    this.setTransformationStartScaleY(-1.0f);
                }
            }
            if (!b) {
                this.setTransformationStartX(-1.0f);
            }
            if (n == 0) {
                this.setTransformationStartY(-1.0f);
            }
            if (!b4) {
                this.setTransformationStartScaleX(-1.0f);
                this.setTransformationStartScaleY(-1.0f);
            }
            this.setClippingDeactivated(mTransformedView, true);
        }
        final float interpolation = this.mDefaultInterpolator.getInterpolation(n2);
        if (b) {
            float interpolation2 = 0.0f;
            Label_0482: {
                if (customTransformation != null) {
                    final Interpolator customInterpolator = customTransformation.getCustomInterpolator(1, true);
                    if (customInterpolator != null) {
                        interpolation2 = customInterpolator.getInterpolation(n2);
                        break Label_0482;
                    }
                }
                interpolation2 = interpolation;
            }
            mTransformedView.setTranslationX(NotificationUtils.interpolate(this.getTransformationStartX(), 0.0f, interpolation2));
        }
        if (n != 0) {
            Label_0534: {
                if (customTransformation != null) {
                    final Interpolator customInterpolator2 = customTransformation.getCustomInterpolator(16, true);
                    if (customInterpolator2 != null) {
                        n2 = customInterpolator2.getInterpolation(n2);
                        break Label_0534;
                    }
                }
                n2 = interpolation;
            }
            mTransformedView.setTranslationY(NotificationUtils.interpolate(this.getTransformationStartY(), 0.0f, n2));
        }
        if (b4) {
            n2 = this.getTransformationStartScaleX();
            if (n2 != -1.0f) {
                mTransformedView.setScaleX(NotificationUtils.interpolate(n2, 1.0f, interpolation));
            }
            n2 = this.getTransformationStartScaleY();
            if (n2 != -1.0f) {
                mTransformedView.setScaleY(NotificationUtils.interpolate(n2, 1.0f, interpolation));
            }
        }
    }
    
    public void transformViewFullyFrom(final TransformState transformState, final float n) {
        this.transformViewFrom(transformState, 17, null, n);
    }
    
    public void transformViewFullyFrom(final TransformState transformState, final ViewTransformationHelper.CustomTransformation customTransformation, final float n) {
        this.transformViewFrom(transformState, 17, customTransformation, n);
    }
    
    public void transformViewFullyTo(final TransformState transformState, final float n) {
        this.transformViewTo(transformState, 17, null, n);
    }
    
    public void transformViewFullyTo(final TransformState transformState, final ViewTransformationHelper.CustomTransformation customTransformation, final float n) {
        this.transformViewTo(transformState, 17, customTransformation, n);
    }
    
    public boolean transformViewTo(final TransformState transformState, final float n) {
        this.mTransformedView.animate().cancel();
        if (this.sameAs(transformState)) {
            if (this.mTransformedView.getVisibility() == 0) {
                this.mTransformedView.setAlpha(0.0f);
                this.mTransformedView.setVisibility(4);
            }
            return false;
        }
        CrossFadeHelper.fadeOut(this.mTransformedView, n);
        this.transformViewFullyTo(transformState, n);
        return true;
    }
    
    public void transformViewVerticalFrom(final TransformState transformState, final float n) {
        this.transformViewFrom(transformState, 16, null, n);
    }
    
    public void transformViewVerticalFrom(final TransformState transformState, final ViewTransformationHelper.CustomTransformation customTransformation, final float n) {
        this.transformViewFrom(transformState, 16, customTransformation, n);
    }
    
    public void transformViewVerticalTo(final TransformState transformState, final float n) {
        this.transformViewTo(transformState, 16, null, n);
    }
    
    public void transformViewVerticalTo(final TransformState transformState, final ViewTransformationHelper.CustomTransformation customTransformation, final float n) {
        this.transformViewTo(transformState, 16, customTransformation, n);
    }
    
    public interface TransformInfo
    {
        boolean isAnimating();
    }
}
