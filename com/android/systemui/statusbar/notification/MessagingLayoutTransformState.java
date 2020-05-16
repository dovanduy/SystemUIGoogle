// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.internal.widget.MessagingImageMessage;
import com.android.systemui.Interpolators;
import java.util.List;
import com.android.internal.widget.MessagingMessage;
import com.android.internal.widget.MessagingPropertyAnimator;
import android.view.ViewGroup$LayoutParams;
import com.android.internal.widget.MessagingLinearLayout$LayoutParams;
import java.util.Collection;
import java.util.ArrayList;
import com.android.systemui.statusbar.TransformableView;
import android.view.View;
import com.android.internal.widget.IMessagingLayout;
import com.android.internal.widget.MessagingLinearLayout;
import com.android.internal.widget.MessagingGroup;
import java.util.HashMap;
import android.util.Pools$SimplePool;

public class MessagingLayoutTransformState extends TransformState
{
    private static Pools$SimplePool<MessagingLayoutTransformState> sInstancePool;
    private HashMap<MessagingGroup, MessagingGroup> mGroupMap;
    private MessagingLinearLayout mMessageContainer;
    private IMessagingLayout mMessagingLayout;
    private float mRelativeTranslationOffset;
    
    static {
        MessagingLayoutTransformState.sInstancePool = (Pools$SimplePool<MessagingLayoutTransformState>)new Pools$SimplePool(40);
    }
    
    public MessagingLayoutTransformState() {
        this.mGroupMap = new HashMap<MessagingGroup, MessagingGroup>();
    }
    
    private void adaptGroupAppear(final MessagingGroup messagingGroup, float n, final float n2, final boolean b) {
        if (b) {
            n *= this.mRelativeTranslationOffset;
        }
        else {
            n = (1.0f - n) * this.mRelativeTranslationOffset;
        }
        float n3 = n;
        if (messagingGroup.getSenderView().getVisibility() != 8) {
            n3 = n * 0.5f;
        }
        messagingGroup.getMessageContainer().setTranslationY(n3);
        messagingGroup.getSenderView().setTranslationY(n3);
        messagingGroup.setTranslationY(n2 * 0.9f);
    }
    
    private void appear(final View view, final float n) {
        if (view != null) {
            if (view.getVisibility() != 8) {
                final TransformState from = TransformState.createFrom(view, super.mTransformInfo);
                from.appear(n, null);
                from.recycle();
            }
        }
    }
    
    private void appear(final MessagingGroup messagingGroup, final float n) {
        final MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
        for (int i = 0; i < messageContainer.getChildCount(); ++i) {
            final View child = messageContainer.getChildAt(i);
            if (!this.isGone(child)) {
                this.appear(child, n);
                this.setClippingDeactivated(child, true);
            }
        }
        this.appear(messagingGroup.getAvatar(), n);
        this.appear(messagingGroup.getSenderView(), n);
        this.appear((View)messagingGroup.getIsolatedMessage(), n);
        this.setClippingDeactivated(messagingGroup.getSenderView(), true);
        this.setClippingDeactivated(messagingGroup.getAvatar(), true);
    }
    
    private void disappear(final View view, final float n) {
        if (view != null) {
            if (view.getVisibility() != 8) {
                final TransformState from = TransformState.createFrom(view, super.mTransformInfo);
                from.disappear(n, null);
                from.recycle();
            }
        }
    }
    
    private void disappear(final MessagingGroup messagingGroup, final float n) {
        final MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
        for (int i = 0; i < messageContainer.getChildCount(); ++i) {
            final View child = messageContainer.getChildAt(i);
            if (!this.isGone(child)) {
                this.disappear(child, n);
                this.setClippingDeactivated(child, true);
            }
        }
        this.disappear(messagingGroup.getAvatar(), n);
        this.disappear(messagingGroup.getSenderView(), n);
        this.disappear((View)messagingGroup.getIsolatedMessage(), n);
        this.setClippingDeactivated(messagingGroup.getSenderView(), true);
        this.setClippingDeactivated(messagingGroup.getAvatar(), true);
    }
    
    private ArrayList<MessagingGroup> filterHiddenGroups(final ArrayList<MessagingGroup> c) {
        final ArrayList<MessagingGroup> list = new ArrayList<MessagingGroup>(c);
        int n;
        for (int i = 0; i < list.size(); i = n + 1) {
            n = i;
            if (this.isGone((View)list.get(i))) {
                list.remove(i);
                n = i - 1;
            }
        }
        return list;
    }
    
    private HashMap<MessagingGroup, MessagingGroup> findPairs(final ArrayList<MessagingGroup> list, final ArrayList<MessagingGroup> list2) {
        this.mGroupMap.clear();
        int i = list.size() - 1;
        int b = Integer.MAX_VALUE;
        while (i >= 0) {
            final MessagingGroup key = list.get(i);
            MessagingGroup value = null;
            int n = 0;
            int n2;
            for (int j = Math.min(list2.size(), b) - 1; j >= 0; --j, n = n2) {
                final MessagingGroup messagingGroup = list2.get(j);
                final int calculateGroupCompatibility = key.calculateGroupCompatibility(messagingGroup);
                if (calculateGroupCompatibility > (n2 = n)) {
                    b = j;
                    value = messagingGroup;
                    n2 = calculateGroupCompatibility;
                }
            }
            if (value != null) {
                this.mGroupMap.put(key, value);
            }
            --i;
        }
        return this.mGroupMap;
    }
    
    private boolean isGone(final View view) {
        if (view == null) {
            return true;
        }
        if (view.getVisibility() == 8) {
            return true;
        }
        if (view.getParent() == null) {
            return true;
        }
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        return layoutParams instanceof MessagingLinearLayout$LayoutParams && ((MessagingLinearLayout$LayoutParams)layoutParams).hide;
    }
    
    public static MessagingLayoutTransformState obtain() {
        final MessagingLayoutTransformState messagingLayoutTransformState = (MessagingLayoutTransformState)MessagingLayoutTransformState.sInstancePool.acquire();
        if (messagingLayoutTransformState != null) {
            return messagingLayoutTransformState;
        }
        return new MessagingLayoutTransformState();
    }
    
    private void resetTransformedView(final View view) {
        final TransformState from = TransformState.createFrom(view, super.mTransformInfo);
        from.resetTransformedView();
        from.recycle();
    }
    
    private void setVisible(final View view, final boolean b, final boolean b2) {
        if (!this.isGone(view)) {
            if (!MessagingPropertyAnimator.isAnimatingAlpha(view)) {
                final TransformState from = TransformState.createFrom(view, super.mTransformInfo);
                from.setVisible(b, b2);
                from.recycle();
            }
        }
    }
    
    private int transformGroups(final MessagingGroup messagingGroup, final MessagingGroup messagingGroup2, final float n, final boolean b) {
        final boolean b2 = messagingGroup2.getIsolatedMessage() == null && !super.mTransformInfo.isAnimating();
        this.transformView(n, b, messagingGroup.getSenderView(), messagingGroup2.getSenderView(), true, b2);
        final int transformView = this.transformView(n, b, messagingGroup.getAvatar(), messagingGroup2.getAvatar(), true, b2);
        final List messages = messagingGroup.getMessages();
        final List messages2 = messagingGroup2.getMessages();
        float translationY = 0.0f;
        for (int i = 0; i < messages.size(); ++i) {
            final View view = messages.get(messages.size() - 1 - i).getView();
            if (!this.isGone(view)) {
                final int n2 = messages2.size() - 1 - i;
                View view2 = null;
                Label_0194: {
                    if (n2 >= 0) {
                        view2 = messages2.get(n2).getView();
                        if (!this.isGone(view2)) {
                            break Label_0194;
                        }
                    }
                    view2 = null;
                }
                float max;
                if (view2 == null && translationY < 0.0f) {
                    final float n3 = max = Math.max(0.0f, Math.min(1.0f, (view.getTop() + view.getHeight() + translationY) / view.getHeight()));
                    if (b) {
                        max = 1.0f - n3;
                    }
                }
                else {
                    max = n;
                }
                final int transformView2 = this.transformView(max, b, view, view2, false, b2);
                final boolean b3 = messagingGroup2.getIsolatedMessage() == view2;
                if (max == 0.0f && (b3 || messagingGroup2.isSingleLine())) {
                    messagingGroup.setClippingDisabled(true);
                    this.mMessagingLayout.setMessagingClippingDisabled(true);
                }
                if (view2 == null) {
                    view.setTranslationY(translationY);
                    this.setClippingDeactivated(view, true);
                }
                else if (messagingGroup.getIsolatedMessage() != view) {
                    if (!b3) {
                        if (b) {
                            translationY = view2.getTranslationY() - transformView2;
                        }
                        else {
                            translationY = view.getTranslationY();
                        }
                    }
                }
            }
        }
        messagingGroup.updateClipRect();
        return transformView;
    }
    
    private int transformView(final float n, final boolean b, final View view, final View view2, final boolean b2, final boolean b3) {
        final TransformState from = TransformState.createFrom(view, super.mTransformInfo);
        if (b3) {
            from.setDefaultInterpolator(Interpolators.LINEAR);
        }
        int n2 = 0;
        from.setIsSameAsAnyView(b2 && !this.isGone(view2));
        if (b) {
            if (view2 != null) {
                final TransformState from2 = TransformState.createFrom(view2, super.mTransformInfo);
                if (!this.isGone(view2)) {
                    from.transformViewTo(from2, n);
                }
                else {
                    if (!this.isGone(view)) {
                        from.disappear(n, null);
                    }
                    from.transformViewVerticalTo(from2, n);
                }
                n2 = from.getLaidOutLocationOnScreen()[1] - from2.getLaidOutLocationOnScreen()[1];
                from2.recycle();
            }
            else {
                from.disappear(n, null);
            }
        }
        else if (view2 != null) {
            final TransformState from3 = TransformState.createFrom(view2, super.mTransformInfo);
            if (!this.isGone(view2)) {
                from.transformViewFrom(from3, n);
            }
            else {
                if (!this.isGone(view)) {
                    from.appear(n, null);
                }
                from.transformViewVerticalFrom(from3, n);
            }
            n2 = from.getLaidOutLocationOnScreen()[1] - from3.getLaidOutLocationOnScreen()[1];
            from3.recycle();
        }
        else {
            from.appear(n, null);
        }
        from.recycle();
        return n2;
    }
    
    private void transformViewInternal(final MessagingLayoutTransformState messagingLayoutTransformState, final float n, final boolean b) {
        this.ensureVisible();
        final ArrayList<MessagingGroup> filterHiddenGroups = this.filterHiddenGroups(this.mMessagingLayout.getMessagingGroups());
        final HashMap<MessagingGroup, MessagingGroup> pairs = this.findPairs(filterHiddenGroups, this.filterHiddenGroups(messagingLayoutTransformState.mMessagingLayout.getMessagingGroups()));
        int i = filterHiddenGroups.size() - 1;
        MessagingGroup messagingGroup = null;
        float n2 = 0.0f;
        while (i >= 0) {
            final MessagingGroup key = filterHiddenGroups.get(i);
            final MessagingGroup messagingGroup2 = pairs.get(key);
            MessagingGroup messagingGroup3 = messagingGroup;
            float translationY = n2;
            if (!this.isGone((View)key)) {
                if (messagingGroup2 != null) {
                    final int transformGroups = this.transformGroups(key, messagingGroup2, n, b);
                    messagingGroup3 = messagingGroup;
                    translationY = n2;
                    if (messagingGroup == null) {
                        if (b) {
                            translationY = messagingGroup2.getAvatar().getTranslationY() - transformGroups;
                        }
                        else {
                            translationY = key.getAvatar().getTranslationY();
                        }
                        messagingGroup3 = key;
                    }
                }
                else {
                    float max;
                    if (messagingGroup != null) {
                        this.adaptGroupAppear(key, n, n2, b);
                        final float n3 = key.getTop() + n2;
                        float n4;
                        float abs;
                        if (!super.mTransformInfo.isAnimating()) {
                            final float a = -key.getHeight() * 0.5f;
                            n4 = n3 - a;
                            abs = Math.abs(a);
                        }
                        else {
                            final float a2 = -key.getHeight() * 0.75f;
                            n4 = n3 - a2;
                            abs = Math.abs(a2) + key.getTop();
                        }
                        final float n5 = max = Math.max(0.0f, Math.min(1.0f, n4 / abs));
                        if (b) {
                            max = 1.0f - n5;
                        }
                    }
                    else {
                        max = n;
                    }
                    if (b) {
                        this.disappear(key, max);
                        messagingGroup3 = messagingGroup;
                        translationY = n2;
                    }
                    else {
                        this.appear(key, max);
                        translationY = n2;
                        messagingGroup3 = messagingGroup;
                    }
                }
            }
            --i;
            messagingGroup = messagingGroup3;
            n2 = translationY;
        }
    }
    
    @Override
    public void initFrom(final View view, final TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        final View mTransformedView = super.mTransformedView;
        if (mTransformedView instanceof MessagingLinearLayout) {
            final MessagingLinearLayout mMessageContainer = (MessagingLinearLayout)mTransformedView;
            this.mMessageContainer = mMessageContainer;
            this.mMessagingLayout = mMessageContainer.getMessagingLayout();
            this.mRelativeTranslationOffset = view.getContext().getResources().getDisplayMetrics().density * 8.0f;
        }
    }
    
    @Override
    public void prepareFadeIn() {
        super.prepareFadeIn();
        this.setVisible(true, false);
    }
    
    @Override
    public void recycle() {
        super.recycle();
        this.mGroupMap.clear();
        MessagingLayoutTransformState.sInstancePool.release((Object)this);
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.mMessageContainer = null;
        this.mMessagingLayout = null;
    }
    
    @Override
    protected void resetTransformedView() {
        super.resetTransformedView();
        final ArrayList messagingGroups = this.mMessagingLayout.getMessagingGroups();
        for (int i = 0; i < messagingGroups.size(); ++i) {
            final MessagingGroup messagingGroup = messagingGroups.get(i);
            if (!this.isGone((View)messagingGroup)) {
                final MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
                for (int j = 0; j < messageContainer.getChildCount(); ++j) {
                    final View child = messageContainer.getChildAt(j);
                    if (!this.isGone(child)) {
                        this.resetTransformedView(child);
                        this.setClippingDeactivated(child, false);
                    }
                }
                this.resetTransformedView(messagingGroup.getAvatar());
                this.resetTransformedView(messagingGroup.getSenderView());
                final MessagingImageMessage isolatedMessage = messagingGroup.getIsolatedMessage();
                if (isolatedMessage != null) {
                    this.resetTransformedView((View)isolatedMessage);
                }
                this.setClippingDeactivated(messagingGroup.getAvatar(), false);
                this.setClippingDeactivated(messagingGroup.getSenderView(), false);
                messagingGroup.setTranslationY(0.0f);
                messagingGroup.getMessageContainer().setTranslationY(0.0f);
                messagingGroup.getSenderView().setTranslationY(0.0f);
            }
            messagingGroup.setClippingDisabled(false);
            messagingGroup.updateClipRect();
        }
        this.mMessagingLayout.setMessagingClippingDisabled(false);
    }
    
    @Override
    public void setVisible(final boolean b, final boolean b2) {
        super.setVisible(b, b2);
        this.resetTransformedView();
        final ArrayList messagingGroups = this.mMessagingLayout.getMessagingGroups();
        for (int i = 0; i < messagingGroups.size(); ++i) {
            final MessagingGroup messagingGroup = messagingGroups.get(i);
            if (!this.isGone((View)messagingGroup)) {
                final MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
                for (int j = 0; j < messageContainer.getChildCount(); ++j) {
                    this.setVisible(messageContainer.getChildAt(j), b, b2);
                }
                this.setVisible(messagingGroup.getAvatar(), b, b2);
                this.setVisible(messagingGroup.getSenderView(), b, b2);
                final MessagingImageMessage isolatedMessage = messagingGroup.getIsolatedMessage();
                if (isolatedMessage != null) {
                    this.setVisible((View)isolatedMessage, b, b2);
                }
            }
        }
    }
    
    @Override
    public void transformViewFrom(final TransformState transformState, final float n) {
        if (transformState instanceof MessagingLayoutTransformState) {
            this.transformViewInternal((MessagingLayoutTransformState)transformState, n, false);
        }
        else {
            super.transformViewFrom(transformState, n);
        }
    }
    
    @Override
    public boolean transformViewTo(final TransformState transformState, final float n) {
        if (transformState instanceof MessagingLayoutTransformState) {
            this.transformViewInternal((MessagingLayoutTransformState)transformState, n, true);
            return true;
        }
        return super.transformViewTo(transformState, n);
    }
}
