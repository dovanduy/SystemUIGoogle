// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.content.Context;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.animation.Animation;
import android.view.View;
import android.view.animation.Animation$AnimationListener;
import android.view.ViewGroup;
import androidx.core.os.CancellationSignal;
import java.util.HashSet;
import java.util.HashMap;

class DefaultSpecialEffectsController extends SpecialEffectsController
{
    private final HashMap<Operation, HashSet<CancellationSignal>> mRunningOperations;
    
    DefaultSpecialEffectsController(final ViewGroup viewGroup) {
        super(viewGroup);
        this.mRunningOperations = new HashMap<Operation, HashSet<CancellationSignal>>();
    }
    
    private void addCancellationSignal(final Operation key, final CancellationSignal e) {
        if (this.mRunningOperations.get(key) == null) {
            this.mRunningOperations.put(key, new HashSet<CancellationSignal>());
        }
        this.mRunningOperations.get(key).add(e);
    }
    
    private void startAnimation(final Operation operation, final CancellationSignal cancellationSignal) {
        final ViewGroup container = this.getContainer();
        final Context context = container.getContext();
        final Fragment fragment = operation.getFragment();
        final View mView = fragment.mView;
        final FragmentAnim.AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(context, fragment, operation.getType() == Type.ADD);
        if (loadAnimation == null) {
            this.removeCancellationSignal(operation, cancellationSignal);
            return;
        }
        container.startViewTransition(mView);
        if (loadAnimation.animation != null) {
            Object animation;
            if (operation.getType() == Type.ADD) {
                animation = loadAnimation.animation;
            }
            else {
                animation = new FragmentAnim.EndViewTransitionAnimation(loadAnimation.animation, container, mView);
            }
            ((Animation)animation).setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
                final /* synthetic */ DefaultSpecialEffectsController this$0;
                
                public void onAnimationEnd(final Animation animation) {
                    container.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            final Animation$AnimationListener this$1 = (Animation$AnimationListener)Animation$AnimationListener.this;
                            container.endViewTransition(mView);
                            final Animation$AnimationListener this$2 = (Animation$AnimationListener)Animation$AnimationListener.this;
                            this$2.this$0.removeCancellationSignal(operation, cancellationSignal);
                        }
                    });
                }
                
                public void onAnimationRepeat(final Animation animation) {
                }
                
                public void onAnimationStart(final Animation animation) {
                }
            });
            mView.startAnimation((Animation)animation);
        }
        else {
            loadAnimation.animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    container.endViewTransition(mView);
                    DefaultSpecialEffectsController.this.removeCancellationSignal(operation, cancellationSignal);
                }
            });
            loadAnimation.animator.setTarget((Object)mView);
            loadAnimation.animator.start();
        }
        cancellationSignal.setOnCancelListener((CancellationSignal.OnCancelListener)new CancellationSignal.OnCancelListener(this) {
            @Override
            public void onCancel() {
                mView.clearAnimation();
            }
        });
    }
    
    private void startTransitions(final List<TransitionInfo> list) {
        final Iterator<TransitionInfo> iterator = list.iterator();
        FragmentTransitionImpl fragmentTransitionImpl = null;
        while (iterator.hasNext()) {
            final TransitionInfo transitionInfo = iterator.next();
            final FragmentTransitionImpl handlingImpl = transitionInfo.getHandlingImpl();
            if (fragmentTransitionImpl == null) {
                fragmentTransitionImpl = handlingImpl;
            }
            else {
                if (handlingImpl == null) {
                    continue;
                }
                if (fragmentTransitionImpl == handlingImpl) {
                    continue;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Mixing framework transitions and AndroidX transitions is not allowed. Fragment ");
                sb.append(transitionInfo.getOperation().getFragment());
                sb.append(" returned Transition ");
                sb.append(transitionInfo.getTransition());
                sb.append(" which uses a different Transition  type than other Fragments.");
                throw new IllegalArgumentException(sb.toString());
            }
        }
        if (fragmentTransitionImpl == null) {
            for (final TransitionInfo transitionInfo2 : list) {
                this.removeCancellationSignal(transitionInfo2.getOperation(), transitionInfo2.getSignal());
            }
        }
        else {
            final Iterator<TransitionInfo> iterator3 = list.iterator();
            Object mergeTransitionsTogether2;
            Object mergeTransitionsTogether = mergeTransitionsTogether2 = null;
            while (iterator3.hasNext()) {
                final TransitionInfo transitionInfo3 = iterator3.next();
                final Object transition = transitionInfo3.getTransition();
                if (transition == null) {
                    this.removeCancellationSignal(transitionInfo3.getOperation(), transitionInfo3.getSignal());
                }
                else if (transitionInfo3.isOverlapAllowed()) {
                    mergeTransitionsTogether = fragmentTransitionImpl.mergeTransitionsTogether(mergeTransitionsTogether, transition, null);
                }
                else {
                    mergeTransitionsTogether2 = fragmentTransitionImpl.mergeTransitionsTogether(mergeTransitionsTogether2, transition, null);
                }
            }
            final Object mergeTransitionsInSequence = fragmentTransitionImpl.mergeTransitionsInSequence(mergeTransitionsTogether, mergeTransitionsTogether2, null);
            for (final TransitionInfo transitionInfo4 : list) {
                if (transitionInfo4.getTransition() != null) {
                    fragmentTransitionImpl.setListenerForTransitionEnd(transitionInfo4.getOperation().getFragment(), mergeTransitionsInSequence, transitionInfo4.getSignal(), new Runnable() {
                        @Override
                        public void run() {
                            DefaultSpecialEffectsController.this.removeCancellationSignal(transitionInfo4.getOperation(), transitionInfo4.getSignal());
                        }
                    });
                }
            }
            fragmentTransitionImpl.beginDelayedTransition(this.getContainer(), mergeTransitionsInSequence);
        }
    }
    
    void applyContainerChanges(final Operation operation) {
        final View mView = operation.getFragment().mView;
        if (operation.getType() == Type.ADD) {
            mView.setVisibility(0);
        }
        else {
            this.getContainer().removeView(mView);
        }
    }
    
    void cancelAllSpecialEffects(final Operation key) {
        final HashSet<CancellationSignal> set = this.mRunningOperations.remove(key);
        if (set != null) {
            final Iterator<CancellationSignal> iterator = set.iterator();
            while (iterator.hasNext()) {
                iterator.next().cancel();
            }
        }
    }
    
    @Override
    void executeOperations(final List<Operation> c) {
        final boolean empty = c.isEmpty();
        boolean b = true;
        if (empty || c.get(c.size() - 1).getType() != Type.REMOVE) {
            b = false;
        }
        final ArrayList<AnimationInfo> list = new ArrayList<AnimationInfo>();
        final ArrayList<TransitionInfo> list2 = new ArrayList<TransitionInfo>();
        final ArrayList<Operation> list3 = new ArrayList<Operation>(c);
        for (final Operation operation : c) {
            final CancellationSignal cancellationSignal = new CancellationSignal();
            this.addCancellationSignal(operation, cancellationSignal);
            list.add(new AnimationInfo(operation, cancellationSignal));
            final CancellationSignal cancellationSignal2 = new CancellationSignal();
            this.addCancellationSignal(operation, cancellationSignal2);
            list2.add(new TransitionInfo(operation, cancellationSignal2, b));
            operation.addCompletionListener(new Runnable() {
                @Override
                public void run() {
                    if (list3.contains(operation)) {
                        list3.remove(operation);
                        DefaultSpecialEffectsController.this.applyContainerChanges(operation);
                    }
                }
            });
            operation.getCancellationSignal().setOnCancelListener((CancellationSignal.OnCancelListener)new CancellationSignal.OnCancelListener() {
                @Override
                public void onCancel() {
                    DefaultSpecialEffectsController.this.cancelAllSpecialEffects(operation);
                }
            });
        }
        this.startTransitions(list2);
        for (final AnimationInfo animationInfo : list) {
            this.startAnimation(animationInfo.getOperation(), animationInfo.getSignal());
        }
        final Iterator<Object> iterator3 = list3.iterator();
        while (iterator3.hasNext()) {
            this.applyContainerChanges(iterator3.next());
        }
        list3.clear();
    }
    
    void removeCancellationSignal(final Operation operation, final CancellationSignal o) {
        final HashSet<CancellationSignal> set = this.mRunningOperations.get(operation);
        if (set != null && set.remove(o) && set.isEmpty()) {
            this.mRunningOperations.remove(operation);
            operation.complete();
        }
    }
    
    private static class AnimationInfo
    {
        private final Operation mOperation;
        private final CancellationSignal mSignal;
        
        AnimationInfo(final Operation mOperation, final CancellationSignal mSignal) {
            this.mOperation = mOperation;
            this.mSignal = mSignal;
        }
        
        Operation getOperation() {
            return this.mOperation;
        }
        
        CancellationSignal getSignal() {
            return this.mSignal;
        }
    }
    
    private static class TransitionInfo
    {
        private final Operation mOperation;
        private final boolean mOverlapAllowed;
        private final CancellationSignal mSignal;
        private final Object mTransition;
        
        TransitionInfo(final Operation mOperation, final CancellationSignal mSignal, final boolean b) {
            this.mOperation = mOperation;
            this.mSignal = mSignal;
            if (mOperation.getType() == Type.ADD) {
                Object mTransition;
                if (b) {
                    mTransition = mOperation.getFragment().getReenterTransition();
                }
                else {
                    mTransition = mOperation.getFragment().getEnterTransition();
                }
                this.mTransition = mTransition;
                boolean mOverlapAllowed;
                if (b) {
                    mOverlapAllowed = mOperation.getFragment().getAllowEnterTransitionOverlap();
                }
                else {
                    mOverlapAllowed = mOperation.getFragment().getAllowReturnTransitionOverlap();
                }
                this.mOverlapAllowed = mOverlapAllowed;
            }
            else {
                Object mTransition2;
                if (b) {
                    mTransition2 = mOperation.getFragment().getReturnTransition();
                }
                else {
                    mTransition2 = mOperation.getFragment().getExitTransition();
                }
                this.mTransition = mTransition2;
                this.mOverlapAllowed = true;
            }
        }
        
        FragmentTransitionImpl getHandlingImpl() {
            final Object mTransition = this.mTransition;
            if (mTransition == null) {
                return null;
            }
            final FragmentTransitionImpl platform_IMPL = FragmentTransition.PLATFORM_IMPL;
            if (platform_IMPL != null && platform_IMPL.canHandle(mTransition)) {
                return FragmentTransition.PLATFORM_IMPL;
            }
            final FragmentTransitionImpl support_IMPL = FragmentTransition.SUPPORT_IMPL;
            if (support_IMPL != null && support_IMPL.canHandle(this.mTransition)) {
                return FragmentTransition.SUPPORT_IMPL;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Transition ");
            sb.append(this.mTransition);
            sb.append(" for fragment ");
            sb.append(this.mOperation.getFragment());
            sb.append(" is not a valid framework Transition or AndroidX Transition");
            throw new IllegalArgumentException(sb.toString());
        }
        
        Operation getOperation() {
            return this.mOperation;
        }
        
        CancellationSignal getSignal() {
            return this.mSignal;
        }
        
        Object getTransition() {
            return this.mTransition;
        }
        
        boolean isOverlapAllowed() {
            return this.mOverlapAllowed;
        }
    }
}
