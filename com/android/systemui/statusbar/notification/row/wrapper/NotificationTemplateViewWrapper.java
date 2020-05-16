// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.wrapper;

import android.view.NotificationHeaderView;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.notification.ImageTransformState;
import android.service.notification.StatusBarNotification;
import android.view.View$OnAttachStateChangeListener;
import com.android.systemui.Dependency;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuffColorFilter;
import android.content.res.ColorStateList;
import android.widget.Button;
import android.app.PendingIntent$CancelListener;
import com.android.internal.util.ContrastColorUtil;
import android.graphics.Color;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.notification.row.HybridNotificationView;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.notification.TransformState;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.content.Context;
import com.android.systemui.UiOffloadThread;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.app.PendingIntent;
import android.util.ArraySet;
import android.view.View;
import com.android.internal.widget.NotificationActionListLayout;

public class NotificationTemplateViewWrapper extends NotificationHeaderViewWrapper
{
    private NotificationActionListLayout mActions;
    protected View mActionsContainer;
    private ArraySet<PendingIntent> mCancelledPendingIntents;
    private int mContentHeight;
    private final int mFullHeaderTranslation;
    private float mHeaderTranslation;
    private int mMinHeightHint;
    protected ImageView mPicture;
    private ProgressBar mProgressBar;
    private View mRemoteInputHistory;
    private ImageView mReplyAction;
    private TextView mText;
    private TextView mTitle;
    private UiOffloadThread mUiOffloadThread;
    
    protected NotificationTemplateViewWrapper(final Context context, final View view, final ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
        this.mCancelledPendingIntents = (ArraySet<PendingIntent>)new ArraySet();
        super.mTransformationHelper.setCustomTransformation((ViewTransformationHelper.CustomTransformation)new ViewTransformationHelper.CustomTransformation(this) {
            private float getTransformationY(final TransformState transformState, final TransformState transformState2) {
                return (transformState2.getLaidOutLocationOnScreen()[1] + transformState2.getTransformedView().getHeight() - transformState.getLaidOutLocationOnScreen()[1]) * 0.33f;
            }
            
            @Override
            public boolean customTransformTarget(final TransformState transformState, final TransformState transformState2) {
                transformState.setTransformationEndY(this.getTransformationY(transformState, transformState2));
                return true;
            }
            
            @Override
            public boolean initTransformation(final TransformState transformState, final TransformState transformState2) {
                transformState.setTransformationStartY(this.getTransformationY(transformState, transformState2));
                return true;
            }
            
            @Override
            public boolean transformFrom(final TransformState transformState, final TransformableView transformableView, final float n) {
                if (!(transformableView instanceof HybridNotificationView)) {
                    return false;
                }
                final TransformState currentState = transformableView.getCurrentState(1);
                CrossFadeHelper.fadeIn(transformState.getTransformedView(), n);
                if (currentState != null) {
                    transformState.transformViewVerticalFrom(currentState, this, n);
                    currentState.recycle();
                }
                return true;
            }
            
            @Override
            public boolean transformTo(final TransformState transformState, final TransformableView transformableView, final float n) {
                if (!(transformableView instanceof HybridNotificationView)) {
                    return false;
                }
                final TransformState currentState = transformableView.getCurrentState(1);
                CrossFadeHelper.fadeOut(transformState.getTransformedView(), n);
                if (currentState != null) {
                    transformState.transformViewVerticalTo(currentState, this, n);
                    currentState.recycle();
                }
                return true;
            }
        }, 2);
        this.mFullHeaderTranslation = context.getResources().getDimensionPixelSize(17105342) - context.getResources().getDimensionPixelSize(17105345);
    }
    
    private int blendColorWithBackground(final int n, final float n2) {
        return ContrastColorUtil.compositeColors(Color.argb((int)(n2 * 255.0f), Color.red(n), Color.green(n), Color.blue(n)), this.resolveBackgroundColor());
    }
    
    private void performOnPendingIntentCancellation(final View view, final Runnable runnable) {
        final PendingIntent pendingIntent = (PendingIntent)view.getTag(16909266);
        if (pendingIntent == null) {
            return;
        }
        if (this.mCancelledPendingIntents.contains((Object)pendingIntent)) {
            runnable.run();
        }
        else {
            final _$$Lambda$NotificationTemplateViewWrapper$JW7SqyfmhP6HCTJ8F1p53b90n6s $$Lambda$NotificationTemplateViewWrapper$JW7SqyfmhP6HCTJ8F1p53b90n6s = new _$$Lambda$NotificationTemplateViewWrapper$JW7SqyfmhP6HCTJ8F1p53b90n6s(this, pendingIntent, runnable);
            if (this.mUiOffloadThread == null) {
                this.mUiOffloadThread = Dependency.get(UiOffloadThread.class);
            }
            if (view.isAttachedToWindow()) {
                this.mUiOffloadThread.execute(new _$$Lambda$NotificationTemplateViewWrapper$qLtzjAQEVXJmd7CTS0Q7hmNVWkU(pendingIntent, (PendingIntent$CancelListener)$$Lambda$NotificationTemplateViewWrapper$JW7SqyfmhP6HCTJ8F1p53b90n6s));
            }
            view.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(final View view) {
                    NotificationTemplateViewWrapper.this.mUiOffloadThread.execute(new _$$Lambda$NotificationTemplateViewWrapper$2$GihuSx3OPFqk_7UFX7W5ZofmkRI(pendingIntent, $$Lambda$NotificationTemplateViewWrapper$JW7SqyfmhP6HCTJ8F1p53b90n6s));
                }
                
                public void onViewDetachedFromWindow(final View view) {
                    NotificationTemplateViewWrapper.this.mUiOffloadThread.execute(new _$$Lambda$NotificationTemplateViewWrapper$2$YHJcr04bTyX63VZ5BMhNHsutz1Y(pendingIntent, $$Lambda$NotificationTemplateViewWrapper$JW7SqyfmhP6HCTJ8F1p53b90n6s));
                }
            });
        }
    }
    
    private void resolveTemplateViews(final StatusBarNotification statusBarNotification) {
        final ImageView mPicture = (ImageView)super.mView.findViewById(16909351);
        this.mPicture = mPicture;
        if (mPicture != null) {
            mPicture.setTag(ImageTransformState.ICON_TAG, (Object)statusBarNotification.getNotification().getLargeIcon());
        }
        this.mTitle = (TextView)super.mView.findViewById(16908310);
        this.mText = (TextView)super.mView.findViewById(16909487);
        final View viewById = super.mView.findViewById(16908301);
        if (viewById instanceof ProgressBar) {
            this.mProgressBar = (ProgressBar)viewById;
        }
        else {
            this.mProgressBar = null;
        }
        this.mActionsContainer = super.mView.findViewById(16908717);
        this.mActions = (NotificationActionListLayout)super.mView.findViewById(16908716);
        this.mReplyAction = (ImageView)super.mView.findViewById(16909333);
        this.mRemoteInputHistory = super.mView.findViewById(16909213);
        this.updatePendingIntentCancellations();
    }
    
    private void updateActionOffset() {
        if (this.mActionsContainer != null) {
            this.mActionsContainer.setTranslationY((float)(Math.max(this.mContentHeight, this.mMinHeightHint) - super.mView.getHeight() - this.getHeaderTranslation(false)));
        }
    }
    
    private void updatePendingIntentCancellations() {
        final NotificationActionListLayout mActions = this.mActions;
        if (mActions != null) {
            for (int childCount = mActions.getChildCount(), i = 0; i < childCount; ++i) {
                final Button button = (Button)this.mActions.getChildAt(i);
                this.performOnPendingIntentCancellation((View)button, new _$$Lambda$NotificationTemplateViewWrapper$JRq0wlJLDK40PaCOgvvnny6lB0w(this, button));
            }
        }
        final ImageView mReplyAction = this.mReplyAction;
        if (mReplyAction != null) {
            mReplyAction.setEnabled(true);
            this.performOnPendingIntentCancellation((View)this.mReplyAction, new _$$Lambda$NotificationTemplateViewWrapper$Znytf0R_oPxyrIENjI1T5rfvZf4(this));
        }
    }
    
    @Override
    public boolean disallowSingleClick(final float n, final float n2) {
        final ImageView mReplyAction = this.mReplyAction;
        return (mReplyAction != null && mReplyAction.getVisibility() == 0 && (this.isOnView((View)this.mReplyAction, n, n2) || this.isOnView((View)this.mPicture, n, n2))) || super.disallowSingleClick(n, n2);
    }
    
    @Override
    public int getExtraMeasureHeight() {
        final NotificationActionListLayout mActions = this.mActions;
        int extraMeasureHeight;
        if (mActions != null) {
            extraMeasureHeight = mActions.getExtraMeasureHeight();
        }
        else {
            extraMeasureHeight = 0;
        }
        final View mRemoteInputHistory = this.mRemoteInputHistory;
        int n = extraMeasureHeight;
        if (mRemoteInputHistory != null) {
            n = extraMeasureHeight;
            if (mRemoteInputHistory.getVisibility() != 8) {
                n = extraMeasureHeight + super.mRow.getContext().getResources().getDimensionPixelSize(R$dimen.remote_input_history_extra_height);
            }
        }
        return n + super.getExtraMeasureHeight();
    }
    
    @Override
    public int getHeaderTranslation(final boolean b) {
        int mFullHeaderTranslation;
        if (b) {
            mFullHeaderTranslation = this.mFullHeaderTranslation;
        }
        else {
            mFullHeaderTranslation = (int)this.mHeaderTranslation;
        }
        return mFullHeaderTranslation;
    }
    
    @Override
    public void onContentUpdated(final ExpandableNotificationRow expandableNotificationRow) {
        this.resolveTemplateViews(expandableNotificationRow.getEntry().getSbn());
        super.onContentUpdated(expandableNotificationRow);
        if (expandableNotificationRow.getHeaderVisibleAmount() != 1.0f) {
            this.setHeaderVisibleAmount(expandableNotificationRow.getHeaderVisibleAmount());
        }
    }
    
    @Override
    public void setContentHeight(final int mContentHeight, final int mMinHeightHint) {
        super.setContentHeight(mContentHeight, mMinHeightHint);
        this.mContentHeight = mContentHeight;
        this.mMinHeightHint = mMinHeightHint;
        this.updateActionOffset();
    }
    
    @Override
    public void setHeaderVisibleAmount(float n) {
        super.setHeaderVisibleAmount(n);
        final NotificationHeaderView mNotificationHeader = super.mNotificationHeader;
        if (mNotificationHeader != null) {
            mNotificationHeader.setAlpha(n);
            n = (1.0f - n) * this.mFullHeaderTranslation;
        }
        else {
            n = 0.0f;
        }
        this.mHeaderTranslation = n;
        super.mView.setTranslationY(n);
    }
    
    @Override
    public boolean shouldClipToRounding(final boolean b, final boolean b2) {
        final boolean shouldClipToRounding = super.shouldClipToRounding(b, b2);
        boolean b3 = true;
        if (shouldClipToRounding) {
            return true;
        }
        if (b2) {
            final View mActionsContainer = this.mActionsContainer;
            if (mActionsContainer != null && mActionsContainer.getVisibility() != 8) {
                return b3;
            }
        }
        b3 = false;
        return b3;
    }
    
    @Override
    protected void updateTransformedTypes() {
        super.updateTransformedTypes();
        final TextView mTitle = this.mTitle;
        if (mTitle != null) {
            super.mTransformationHelper.addTransformedView(1, (View)mTitle);
        }
        final TextView mText = this.mText;
        if (mText != null) {
            super.mTransformationHelper.addTransformedView(2, (View)mText);
        }
        final ImageView mPicture = this.mPicture;
        if (mPicture != null) {
            super.mTransformationHelper.addTransformedView(3, (View)mPicture);
        }
        final ProgressBar mProgressBar = this.mProgressBar;
        if (mProgressBar != null) {
            super.mTransformationHelper.addTransformedView(4, (View)mProgressBar);
        }
    }
}
