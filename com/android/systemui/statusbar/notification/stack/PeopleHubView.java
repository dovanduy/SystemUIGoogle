// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.widget.ImageView;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.collections.CollectionsKt;
import kotlin.ranges.RangesKt;
import com.android.systemui.R$id;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;

public final class PeopleHubView extends StackScrollerDecorView implements SwipeableView
{
    private boolean canSwipe;
    private ViewGroup contents;
    private TextView label;
    
    public PeopleHubView(final Context context, final AttributeSet set) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(set, "attrs");
        super(context, set);
        this.canSwipe = true;
    }
    
    @Override
    protected void applyContentTransformation(final float alpha, final float translationY) {
        super.applyContentTransformation(alpha, translationY);
        final ViewGroup contents = this.contents;
        if (contents != null) {
            for (int childCount = contents.getChildCount(), i = 0; i < childCount; ++i) {
                final ViewGroup contents2 = this.contents;
                if (contents2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("contents");
                    throw null;
                }
                final View child = contents2.getChildAt(i);
                Intrinsics.checkExpressionValueIsNotNull(child, "view");
                child.setAlpha(alpha);
                child.setTranslationY(translationY);
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }
    
    @Override
    public NotificationMenuRowPlugin createMenu() {
        return null;
    }
    
    @Override
    protected View findContentView() {
        final ViewGroup contents = this.contents;
        if (contents != null) {
            return (View)contents;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }
    
    @Override
    protected View findSecondaryView() {
        return null;
    }
    
    @Override
    public boolean hasFinishedInitialization() {
        return true;
    }
    
    @Override
    public boolean needsClippingToShelf() {
        return true;
    }
    
    @Override
    protected void onFinishInflate() {
        final View requireViewById = this.requireViewById(R$id.people_list);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById(R.id.people_list)");
        this.contents = (ViewGroup)requireViewById;
        final View requireViewById2 = this.requireViewById(R$id.header_label);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById(R.id.header_label)");
        this.label = (TextView)requireViewById2;
        final ViewGroup contents = this.contents;
        if (contents != null) {
            CollectionsKt.asSequence((Iterable<?>)SequencesKt.toList((Sequence<?>)SequencesKt.mapNotNull((Sequence<?>)CollectionsKt.asSequence((Iterable<?>)RangesKt.until(0, contents.getChildCount())), (Function1<? super Object, ?>)new PeopleHubView$onFinishInflate.PeopleHubView$onFinishInflate$1(this))));
            super.onFinishInflate();
            this.setVisible(true, false);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }
    
    @Override
    public void resetTranslation() {
        this.setTranslationX(0.0f);
    }
    
    public final void setCanSwipe(final boolean canSwipe) {
        final boolean canSwipe2 = this.canSwipe;
        if (canSwipe2 != canSwipe) {
            if (canSwipe2) {
                this.resetTranslation();
            }
            this.canSwipe = canSwipe;
        }
    }
    
    public final void setTextColor(final int textColor) {
        final TextView label = this.label;
        if (label != null) {
            label.setTextColor(textColor);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("label");
        throw null;
    }
    
    @Override
    public void setTranslation(final float translation) {
        if (this.canSwipe) {
            super.setTranslation(translation);
        }
    }
    
    private final class PersonDataListenerImpl implements Object<Object>
    {
        private final ImageView avatarView;
        
        public PersonDataListenerImpl(final PeopleHubView peopleHubView, final ImageView avatarView) {
            Intrinsics.checkParameterIsNotNull(avatarView, "avatarView");
            this.avatarView = avatarView;
        }
    }
}
