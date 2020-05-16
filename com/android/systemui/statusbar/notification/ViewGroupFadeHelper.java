// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import kotlin.jvm.internal.TypeIntrinsics;
import java.util.Iterator;
import android.animation.Animator$AnimatorListener;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator;
import com.android.systemui.R$id;
import android.graphics.Paint;
import kotlin.jvm.internal.Intrinsics;
import java.util.LinkedHashSet;
import java.util.Set;
import android.view.ViewGroup;
import android.view.View;
import kotlin.jvm.functions.Function1;

public final class ViewGroupFadeHelper
{
    public static final Companion Companion;
    private static final Function1<View, Boolean> visibilityIncluder;
    
    static {
        Companion = new Companion(null);
        visibilityIncluder = (Function1)ViewGroupFadeHelper$Companion$visibilityIncluder.ViewGroupFadeHelper$Companion$visibilityIncluder$1.INSTANCE;
    }
    
    public static final /* synthetic */ Function1 access$getVisibilityIncluder$cp() {
        return ViewGroupFadeHelper.visibilityIncluder;
    }
    
    public static final void fadeOutAllChildrenExcept(final ViewGroup viewGroup, final View view, final long n, final Runnable runnable) {
        ViewGroupFadeHelper.Companion.fadeOutAllChildrenExcept(viewGroup, view, n, runnable);
    }
    
    public static final void reset(final ViewGroup viewGroup) {
        ViewGroupFadeHelper.Companion.reset(viewGroup);
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        private final Set<View> gatherViews(final ViewGroup viewGroup, View child, final Function1<? super View, Boolean> function1) {
            final LinkedHashSet<View> set = new LinkedHashSet<View>();
            final ViewGroup viewGroup2 = (ViewGroup)child.getParent();
            View view = child;
            ViewGroup viewGroup3 = viewGroup2;
            while (true) {
                final View view2 = view;
                view = (View)viewGroup3;
                if (view == null) {
                    break;
                }
                for (int i = 0; i < ((ViewGroup)view).getChildCount(); ++i) {
                    child = ((ViewGroup)view).getChildAt(i);
                    Intrinsics.checkExpressionValueIsNotNull(child, "child");
                    if (function1.invoke(child) && (Intrinsics.areEqual(view2, child) ^ true)) {
                        set.add(child);
                    }
                }
                if (Intrinsics.areEqual(view, viewGroup)) {
                    break;
                }
                viewGroup3 = (ViewGroup)((ViewGroup)view).getParent();
            }
            return set;
        }
        
        public final void fadeOutAllChildrenExcept(final ViewGroup viewGroup, final View view, final long duration, final Runnable runnable) {
            Intrinsics.checkParameterIsNotNull(viewGroup, "root");
            Intrinsics.checkParameterIsNotNull(view, "excludedView");
            final Set<View> gatherViews = this.gatherViews(viewGroup, view, ViewGroupFadeHelper.access$getVisibilityIncluder$cp());
            for (final View view2 : gatherViews) {
                if (view2.getHasOverlappingRendering() && view2.getLayerType() == 0) {
                    view2.setLayerType(2, (Paint)null);
                    view2.setTag(R$id.view_group_fade_helper_hardware_layer, (Object)Boolean.TRUE);
                }
            }
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 1.0f, 0.0f });
            Intrinsics.checkExpressionValueIsNotNull(ofFloat, "this");
            ofFloat.setDuration(duration);
            ofFloat.setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT);
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda.ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$1(duration, viewGroup, (Set)gatherViews, runnable));
            ofFloat.addListener((Animator$AnimatorListener)new ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda.ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$2(duration, viewGroup, (Set)gatherViews, runnable));
            ofFloat.start();
            viewGroup.setTag(R$id.view_group_fade_helper_modified_views, (Object)gatherViews);
            viewGroup.setTag(R$id.view_group_fade_helper_animator, (Object)ofFloat);
        }
        
        public final void reset(final ViewGroup viewGroup) {
            Intrinsics.checkParameterIsNotNull(viewGroup, "root");
            final Set mutableSet = TypeIntrinsics.asMutableSet(viewGroup.getTag(R$id.view_group_fade_helper_modified_views));
            final Animator animator = (Animator)viewGroup.getTag(R$id.view_group_fade_helper_animator);
            if (mutableSet != null) {
                if (animator != null) {
                    animator.cancel();
                    final Float n = (Float)viewGroup.getTag(R$id.view_group_fade_helper_previous_value_tag);
                    for (final View view : mutableSet) {
                        final Float n2 = (Float)view.getTag(R$id.view_group_fade_helper_restore_tag);
                        if (n2 == null) {
                            continue;
                        }
                        if (Intrinsics.areEqual(n, view.getAlpha())) {
                            view.setAlpha((float)n2);
                        }
                        if (Intrinsics.areEqual(view.getTag(R$id.view_group_fade_helper_hardware_layer), Boolean.TRUE)) {
                            view.setLayerType(0, (Paint)null);
                            view.setTag(R$id.view_group_fade_helper_hardware_layer, (Object)null);
                        }
                        view.setTag(R$id.view_group_fade_helper_restore_tag, (Object)null);
                    }
                    viewGroup.setTag(R$id.view_group_fade_helper_modified_views, (Object)null);
                    viewGroup.setTag(R$id.view_group_fade_helper_previous_value_tag, (Object)null);
                    viewGroup.setTag(R$id.view_group_fade_helper_animator, (Object)null);
                }
            }
        }
    }
}
