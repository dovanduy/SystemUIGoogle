// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.internal.widget.MessagingMessage;
import android.view.View;
import com.android.systemui.R$id;
import com.android.internal.widget.MessagingImageMessage;
import android.util.Pools$SimplePool;

public class MessagingImageTransformState extends ImageTransformState
{
    private static final int START_ACTUAL_HEIGHT;
    private static final int START_ACTUAL_WIDTH;
    private static Pools$SimplePool<MessagingImageTransformState> sInstancePool;
    private MessagingImageMessage mImageMessage;
    
    static {
        MessagingImageTransformState.sInstancePool = (Pools$SimplePool<MessagingImageTransformState>)new Pools$SimplePool(40);
        START_ACTUAL_WIDTH = R$id.transformation_start_actual_width;
        START_ACTUAL_HEIGHT = R$id.transformation_start_actual_height;
    }
    
    public static MessagingImageTransformState obtain() {
        final MessagingImageTransformState messagingImageTransformState = (MessagingImageTransformState)MessagingImageTransformState.sInstancePool.acquire();
        if (messagingImageTransformState != null) {
            return messagingImageTransformState;
        }
        return new MessagingImageTransformState();
    }
    
    public int getStartActualHeight() {
        final Object tag = super.mTransformedView.getTag(MessagingImageTransformState.START_ACTUAL_HEIGHT);
        int intValue;
        if (tag == null) {
            intValue = -1;
        }
        else {
            intValue = (int)tag;
        }
        return intValue;
    }
    
    public int getStartActualWidth() {
        final Object tag = super.mTransformedView.getTag(MessagingImageTransformState.START_ACTUAL_WIDTH);
        int intValue;
        if (tag == null) {
            intValue = -1;
        }
        else {
            intValue = (int)tag;
        }
        return intValue;
    }
    
    @Override
    public void initFrom(final View view, final TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        this.mImageMessage = (MessagingImageMessage)view;
    }
    
    @Override
    public void recycle() {
        super.recycle();
        MessagingImageTransformState.sInstancePool.release((Object)this);
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.mImageMessage = null;
    }
    
    @Override
    protected void resetTransformedView() {
        super.resetTransformedView();
        final MessagingImageMessage mImageMessage = this.mImageMessage;
        mImageMessage.setActualWidth(mImageMessage.getWidth());
        final MessagingImageMessage mImageMessage2 = this.mImageMessage;
        mImageMessage2.setActualHeight(mImageMessage2.getHeight());
    }
    
    @Override
    protected boolean sameAs(final TransformState transformState) {
        return super.sameAs(transformState) || (transformState instanceof MessagingImageTransformState && this.mImageMessage.sameAs((MessagingMessage)((MessagingImageTransformState)transformState).mImageMessage));
    }
    
    public void setStartActualHeight(final int i) {
        super.mTransformedView.setTag(MessagingImageTransformState.START_ACTUAL_HEIGHT, (Object)i);
    }
    
    public void setStartActualWidth(final int i) {
        super.mTransformedView.setTag(MessagingImageTransformState.START_ACTUAL_WIDTH, (Object)i);
    }
    
    @Override
    protected boolean transformScale(final TransformState transformState) {
        return false;
    }
    
    @Override
    protected void transformViewFrom(final TransformState transformState, final int n, final ViewTransformationHelper.CustomTransformation customTransformation, float n2) {
        super.transformViewFrom(transformState, n, customTransformation, n2);
        final float interpolation = super.mDefaultInterpolator.getInterpolation(n2);
        if (transformState instanceof MessagingImageTransformState && this.sameAs(transformState)) {
            final MessagingImageMessage mImageMessage = ((MessagingImageTransformState)transformState).mImageMessage;
            if (n2 == 0.0f) {
                this.setStartActualWidth(mImageMessage.getActualWidth());
                this.setStartActualHeight(mImageMessage.getActualHeight());
            }
            n2 = (float)this.getStartActualWidth();
            final MessagingImageMessage mImageMessage2 = this.mImageMessage;
            mImageMessage2.setActualWidth((int)NotificationUtils.interpolate(n2, (float)mImageMessage2.getWidth(), interpolation));
            n2 = (float)this.getStartActualHeight();
            final MessagingImageMessage mImageMessage3 = this.mImageMessage;
            mImageMessage3.setActualHeight((int)NotificationUtils.interpolate(n2, (float)mImageMessage3.getHeight(), interpolation));
        }
    }
}
