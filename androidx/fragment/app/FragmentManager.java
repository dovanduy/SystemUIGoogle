// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import java.io.Writer;
import android.os.Parcelable;
import android.view.animation.Animation;
import androidx.lifecycle.ViewModelStore;
import android.view.LayoutInflater$Factory2;
import java.util.List;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.res.Configuration;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.activity.OnBackPressedDispatcherOwner;
import android.util.Log;
import androidx.fragment.R$id;
import java.util.Collection;
import android.os.Looper;
import androidx.lifecycle.LifecycleOwner;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.view.View;
import android.animation.AnimatorListenerAdapter;
import java.util.Set;
import java.util.Iterator;
import androidx.collection.ArraySet;
import android.view.ViewGroup;
import android.os.Bundle;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedCallback;
import androidx.core.os.CancellationSignal;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

public abstract class FragmentManager
{
    private static boolean DEBUG = false;
    static boolean USE_STATE_MANAGER = false;
    ArrayList<BackStackRecord> mBackStack;
    private ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    private final AtomicInteger mBackStackIndex;
    private FragmentContainer mContainer;
    private ArrayList<Fragment> mCreatedMenus;
    int mCurState;
    private SpecialEffectsControllerFactory mDefaultSpecialEffectsControllerFactory;
    private boolean mDestroyed;
    private Runnable mExecCommit;
    private boolean mExecutingActions;
    private ConcurrentHashMap<Fragment, HashSet<CancellationSignal>> mExitAnimationCancellationSignals;
    private FragmentFactory mFragmentFactory;
    private final FragmentStore mFragmentStore;
    private final FragmentTransition.Callback mFragmentTransitionCallback;
    private boolean mHavePendingDeferredStart;
    private FragmentHostCallback<?> mHost;
    private FragmentFactory mHostFragmentFactory;
    private final FragmentLayoutInflaterFactory mLayoutInflaterFactory;
    private final FragmentLifecycleCallbacksDispatcher mLifecycleCallbacksDispatcher;
    private boolean mNeedMenuInvalidate;
    private FragmentManagerViewModel mNonConfig;
    private final OnBackPressedCallback mOnBackPressedCallback;
    private OnBackPressedDispatcher mOnBackPressedDispatcher;
    private Fragment mParent;
    private final ArrayList<OpGenerator> mPendingActions;
    private ArrayList<StartEnterTransitionListener> mPostponedTransactions;
    Fragment mPrimaryNav;
    private SpecialEffectsControllerFactory mSpecialEffectsControllerFactory;
    private boolean mStateSaved;
    private boolean mStopped;
    private ArrayList<Fragment> mTmpAddedFragments;
    private ArrayList<Boolean> mTmpIsPop;
    private ArrayList<BackStackRecord> mTmpRecords;
    
    public FragmentManager() {
        this.mPendingActions = new ArrayList<OpGenerator>();
        this.mFragmentStore = new FragmentStore();
        this.mLayoutInflaterFactory = new FragmentLayoutInflaterFactory(this);
        this.mOnBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager.this.handleOnBackPressed();
            }
        };
        this.mBackStackIndex = new AtomicInteger();
        this.mExitAnimationCancellationSignals = new ConcurrentHashMap<Fragment, HashSet<CancellationSignal>>();
        this.mFragmentTransitionCallback = new FragmentTransition.Callback() {
            @Override
            public void onComplete(final Fragment fragment, final CancellationSignal cancellationSignal) {
                if (!cancellationSignal.isCanceled()) {
                    FragmentManager.this.removeCancellationSignal(fragment, cancellationSignal);
                }
            }
            
            @Override
            public void onStart(final Fragment fragment, final CancellationSignal cancellationSignal) {
                FragmentManager.this.addCancellationSignal(fragment, cancellationSignal);
            }
        };
        this.mLifecycleCallbacksDispatcher = new FragmentLifecycleCallbacksDispatcher(this);
        this.mCurState = -1;
        this.mFragmentFactory = null;
        this.mHostFragmentFactory = new FragmentFactory() {
            @Override
            public Fragment instantiate(final ClassLoader classLoader, final String s) {
                return FragmentManager.this.getHost().instantiate(FragmentManager.this.getHost().getContext(), s, null);
            }
        };
        this.mSpecialEffectsControllerFactory = null;
        this.mDefaultSpecialEffectsControllerFactory = new SpecialEffectsControllerFactory() {
            @Override
            public SpecialEffectsController createController(final ViewGroup viewGroup) {
                return new DefaultSpecialEffectsController(viewGroup);
            }
        };
        this.mExecCommit = new Runnable() {
            @Override
            public void run() {
                FragmentManager.this.execPendingActions(true);
            }
        };
    }
    
    private void addAddedFragments(final ArraySet<Fragment> set) {
        final int mCurState = this.mCurState;
        if (mCurState < 1) {
            return;
        }
        final int min = Math.min(mCurState, 4);
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment.mState < min) {
                this.moveToState(fragment, min);
                if (fragment.mView == null || fragment.mHidden || !fragment.mIsNewlyAdded) {
                    continue;
                }
                set.add(fragment);
            }
        }
    }
    
    private void cancelExitAnimation(final Fragment fragment) {
        final HashSet<CancellationSignal> set = this.mExitAnimationCancellationSignals.get(fragment);
        if (set != null) {
            final Iterator<CancellationSignal> iterator = set.iterator();
            while (iterator.hasNext()) {
                iterator.next().cancel();
            }
            set.clear();
            this.destroyFragmentView(fragment);
            this.mExitAnimationCancellationSignals.remove(fragment);
        }
    }
    
    private void checkStateLoss() {
        if (!this.isStateSaved()) {
            return;
        }
        throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
    }
    
    private void cleanupExec() {
        this.mExecutingActions = false;
        this.mTmpIsPop.clear();
        this.mTmpRecords.clear();
    }
    
    private Set<SpecialEffectsController> collectAllSpecialEffectsController() {
        final HashSet<SpecialEffectsController> set = new HashSet<SpecialEffectsController>();
        final Iterator<FragmentStateManager> iterator = this.mFragmentStore.getActiveFragmentStateManagers().iterator();
        while (iterator.hasNext()) {
            final ViewGroup mContainer = iterator.next().getFragment().mContainer;
            if (mContainer != null) {
                set.add(SpecialEffectsController.getOrCreateController(mContainer, this.getSpecialEffectsControllerFactory()));
            }
        }
        return set;
    }
    
    private Set<SpecialEffectsController> collectChangedControllers(final ArrayList<BackStackRecord> list, int i, final int n) {
        final HashSet<SpecialEffectsController> set = new HashSet<SpecialEffectsController>();
        while (i < n) {
            final Iterator<FragmentTransaction.Op> iterator = list.get(i).mOps.iterator();
            while (iterator.hasNext()) {
                final Fragment mFragment = ((FragmentTransaction.Op)iterator.next()).mFragment;
                if (mFragment != null) {
                    final ViewGroup mContainer = mFragment.mContainer;
                    if (mContainer == null) {
                        continue;
                    }
                    set.add(SpecialEffectsController.getOrCreateController(mContainer, this));
                }
            }
            ++i;
        }
        return set;
    }
    
    private void completeShowHideFragment(final Fragment fragment) {
        Label_0192: {
            if (fragment.mView != null) {
                final FragmentAnim.AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(this.mHost.getContext(), fragment, fragment.mHidden ^ true);
                if (loadAnimation != null) {
                    final Animator animator = loadAnimation.animator;
                    if (animator != null) {
                        animator.setTarget((Object)fragment.mView);
                        if (fragment.mHidden) {
                            if (fragment.isHideReplaced()) {
                                fragment.setHideReplaced(false);
                            }
                            else {
                                final ViewGroup mContainer = fragment.mContainer;
                                final View mView = fragment.mView;
                                mContainer.startViewTransition(mView);
                                loadAnimation.animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                                    public void onAnimationEnd(final Animator animator) {
                                        mContainer.endViewTransition(mView);
                                        animator.removeListener((Animator$AnimatorListener)this);
                                        final Fragment val$fragment = fragment;
                                        final View mView = val$fragment.mView;
                                        if (mView != null && val$fragment.mHidden) {
                                            mView.setVisibility(8);
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            fragment.mView.setVisibility(0);
                        }
                        loadAnimation.animator.start();
                        break Label_0192;
                    }
                }
                if (loadAnimation != null) {
                    fragment.mView.startAnimation(loadAnimation.animation);
                    loadAnimation.animation.start();
                }
                int visibility;
                if (fragment.mHidden && !fragment.isHideReplaced()) {
                    visibility = 8;
                }
                else {
                    visibility = 0;
                }
                fragment.mView.setVisibility(visibility);
                if (fragment.isHideReplaced()) {
                    fragment.setHideReplaced(false);
                }
            }
        }
        if (fragment.mAdded && this.isMenuAvailable(fragment)) {
            this.mNeedMenuInvalidate = true;
        }
        fragment.mHiddenChanged = false;
        fragment.onHiddenChanged(fragment.mHidden);
    }
    
    private void destroyFragmentView(final Fragment fragment) {
        fragment.performDestroyView();
        this.mLifecycleCallbacksDispatcher.dispatchOnFragmentViewDestroyed(fragment, false);
        fragment.mContainer = null;
        fragment.mView = null;
        fragment.mViewLifecycleOwner = null;
        fragment.mViewLifecycleOwnerLiveData.setValue(null);
        fragment.mInLayout = false;
    }
    
    private void dispatchParentPrimaryNavigationFragmentChanged(final Fragment fragment) {
        if (fragment != null && fragment.equals(this.findActiveFragment(fragment.mWho))) {
            fragment.performPrimaryNavigationFragmentChanged();
        }
    }
    
    private void dispatchStateChange(final int n) {
        try {
            this.mExecutingActions = true;
            this.mFragmentStore.dispatchStateChange(n);
            this.moveToState(n, false);
            this.mExecutingActions = false;
            this.execPendingActions(true);
        }
        finally {
            this.mExecutingActions = false;
        }
    }
    
    private void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            this.mHavePendingDeferredStart = false;
            this.startPendingDeferredFragments();
        }
    }
    
    private void endAnimatingAwayFragments() {
        if (FragmentManager.USE_STATE_MANAGER) {
            final Iterator<SpecialEffectsController> iterator = this.collectAllSpecialEffectsController().iterator();
            while (iterator.hasNext()) {
                iterator.next().cancelAllOperations();
            }
        }
        else if (!this.mExitAnimationCancellationSignals.isEmpty()) {
            for (final Fragment fragment : this.mExitAnimationCancellationSignals.keySet()) {
                this.cancelExitAnimation(fragment);
                this.moveToState(fragment);
            }
        }
    }
    
    private void ensureExecReady(final boolean b) {
        if (this.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        }
        if (this.mHost != null) {
            if (Looper.myLooper() == this.mHost.getHandler().getLooper()) {
                if (!b) {
                    this.checkStateLoss();
                }
                if (this.mTmpRecords == null) {
                    this.mTmpRecords = new ArrayList<BackStackRecord>();
                    this.mTmpIsPop = new ArrayList<Boolean>();
                }
                this.mExecutingActions = true;
                try {
                    this.executePostponedTransaction(null, null);
                    return;
                }
                finally {
                    this.mExecutingActions = false;
                }
            }
            throw new IllegalStateException("Must be called from main thread of fragment host");
        }
        if (this.mDestroyed) {
            throw new IllegalStateException("FragmentManager has been destroyed");
        }
        throw new IllegalStateException("FragmentManager has not been attached to a host.");
    }
    
    private static void executeOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, int i, final int n) {
        while (i < n) {
            final BackStackRecord backStackRecord = list.get(i);
            final boolean booleanValue = list2.get(i);
            boolean b = true;
            if (booleanValue) {
                backStackRecord.bumpBackStackNesting(-1);
                if (i != n - 1) {
                    b = false;
                }
                backStackRecord.executePopOps(b);
            }
            else {
                backStackRecord.bumpBackStackNesting(1);
                backStackRecord.executeOps();
            }
            ++i;
        }
    }
    
    private void executeOpsTogether(final ArrayList<BackStackRecord> list, ArrayList<Boolean> list2, int i, final int n) {
        final boolean mReorderingAllowed = list.get(i).mReorderingAllowed;
        final ArrayList<Fragment> mTmpAddedFragments = this.mTmpAddedFragments;
        if (mTmpAddedFragments == null) {
            this.mTmpAddedFragments = new ArrayList<Fragment>();
        }
        else {
            mTmpAddedFragments.clear();
        }
        this.mTmpAddedFragments.addAll(this.mFragmentStore.getFragments());
        Fragment fragment = this.getPrimaryNavigationFragment();
        int n2 = 0;
        for (int j = i; j < n; ++j) {
            final BackStackRecord backStackRecord = list.get(j);
            if (!list2.get(j)) {
                fragment = backStackRecord.expandOps(this.mTmpAddedFragments, fragment);
            }
            else {
                fragment = backStackRecord.trackAddedFragmentsInPop(this.mTmpAddedFragments, fragment);
            }
            if (n2 == 0 && !backStackRecord.mAddToBackStack) {
                n2 = 0;
            }
            else {
                n2 = 1;
            }
        }
        this.mTmpAddedFragments.clear();
        if (!mReorderingAllowed && this.mCurState >= 1) {
            if (FragmentManager.USE_STATE_MANAGER) {
                for (int k = i; k < n; ++k) {
                    final Iterator<FragmentTransaction.Op> iterator = list.get(k).mOps.iterator();
                    while (iterator.hasNext()) {
                        final Fragment mFragment = ((FragmentTransaction.Op)iterator.next()).mFragment;
                        if (mFragment != null) {
                            this.mFragmentStore.makeActive(this.createOrGetFragmentStateManager(mFragment));
                        }
                    }
                }
            }
            else {
                FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, list, list2, i, n, false, this.mFragmentTransitionCallback);
            }
        }
        executeOps(list, list2, i, n);
        if (FragmentManager.USE_STATE_MANAGER) {
            if (mReorderingAllowed) {
                this.moveToState(this.mCurState, true);
            }
            final Iterator<SpecialEffectsController> iterator2 = this.collectChangedControllers(list, i, n).iterator();
            while (iterator2.hasNext()) {
                iterator2.next().executePendingOperations();
            }
        }
        else {
            int postponePostponableTransactions;
            if (mReorderingAllowed) {
                final ArraySet<Fragment> set = new ArraySet<Fragment>();
                this.addAddedFragments(set);
                postponePostponableTransactions = this.postponePostponableTransactions(list, list2, i, n, set);
                this.makeRemovedFragmentsInvisible(set);
            }
            else {
                postponePostponableTransactions = n;
            }
            final int n3 = 1;
            final ArrayList<Boolean> list3 = list2;
            if (postponePostponableTransactions != i && mReorderingAllowed) {
                if (this.mCurState >= n3) {
                    FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, list, list2, i, postponePostponableTransactions, true, this.mFragmentTransitionCallback);
                }
                list2 = list3;
                this.moveToState(this.mCurState, (boolean)(n3 != 0));
            }
            else {
                list2 = list3;
            }
        }
        while (i < n) {
            final BackStackRecord backStackRecord2 = list.get(i);
            if (list2.get(i) && backStackRecord2.mIndex >= 0) {
                backStackRecord2.mIndex = -1;
            }
            backStackRecord2.runOnCommitRunnables();
            ++i;
        }
        if (n2 != 0) {
            this.reportBackStackChanged();
        }
    }
    
    private void executePostponedTransaction(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        final ArrayList<StartEnterTransitionListener> mPostponedTransactions = this.mPostponedTransactions;
        int size;
        if (mPostponedTransactions == null) {
            size = 0;
        }
        else {
            size = mPostponedTransactions.size();
        }
        int n2;
        int n3;
        for (int i = 0, n = size; i < n; i = n2 + 1, n = n3) {
            final StartEnterTransitionListener startEnterTransitionListener = this.mPostponedTransactions.get(i);
            if (list != null && !startEnterTransitionListener.mIsBack) {
                final int index = list.indexOf(startEnterTransitionListener.mRecord);
                if (index != -1 && list2 != null && list2.get(index)) {
                    this.mPostponedTransactions.remove(i);
                    n2 = i - 1;
                    n3 = n - 1;
                    startEnterTransitionListener.cancelTransaction();
                    continue;
                }
            }
            if (!startEnterTransitionListener.isReady()) {
                n3 = n;
                n2 = i;
                if (list == null) {
                    continue;
                }
                n3 = n;
                n2 = i;
                if (!startEnterTransitionListener.mRecord.interactsWith(list, 0, list.size())) {
                    continue;
                }
            }
            this.mPostponedTransactions.remove(i);
            n2 = i - 1;
            n3 = n - 1;
            if (list != null && !startEnterTransitionListener.mIsBack) {
                final int index2 = list.indexOf(startEnterTransitionListener.mRecord);
                if (index2 != -1 && list2 != null && list2.get(index2)) {
                    startEnterTransitionListener.cancelTransaction();
                    continue;
                }
            }
            startEnterTransitionListener.completeTransaction();
        }
    }
    
    private void forcePostponedTransactions() {
        if (this.mPostponedTransactions != null) {
            while (!this.mPostponedTransactions.isEmpty()) {
                this.mPostponedTransactions.remove(0).completeTransaction();
            }
        }
    }
    
    private boolean generateOpsForPendingActions(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        synchronized (this.mPendingActions) {
            final boolean empty = this.mPendingActions.isEmpty();
            int i = 0;
            if (empty) {
                return false;
            }
            final int size = this.mPendingActions.size();
            boolean b = false;
            while (i < size) {
                b |= this.mPendingActions.get(i).generateOps(list, list2);
                ++i;
            }
            this.mPendingActions.clear();
            this.mHost.getHandler().removeCallbacks(this.mExecCommit);
            return b;
        }
    }
    
    private FragmentManagerViewModel getChildNonConfig(final Fragment fragment) {
        return this.mNonConfig.getChildNonConfig(fragment);
    }
    
    private ViewGroup getFragmentContainer(final Fragment fragment) {
        if (fragment.mContainerId <= 0) {
            return null;
        }
        if (this.mContainer.onHasView()) {
            final View onFindViewById = this.mContainer.onFindViewById(fragment.mContainerId);
            if (onFindViewById instanceof ViewGroup) {
                return (ViewGroup)onFindViewById;
            }
        }
        return null;
    }
    
    static Fragment getViewFragment(final View view) {
        final Object tag = view.getTag(R$id.fragment_container_view_tag);
        if (tag instanceof Fragment) {
            return (Fragment)tag;
        }
        return null;
    }
    
    static boolean isLoggingEnabled(final int n) {
        return FragmentManager.DEBUG || Log.isLoggable("FragmentManager", n);
    }
    
    private boolean isMenuAvailable(final Fragment fragment) {
        return (fragment.mHasMenu && fragment.mMenuVisible) || fragment.mChildFragmentManager.checkForMenus();
    }
    
    private void makeRemovedFragmentsInvisible(final ArraySet<Fragment> set) {
        for (int size = set.size(), i = 0; i < size; ++i) {
            final Fragment fragment = set.valueAt(i);
            if (!fragment.mAdded) {
                final View requireView = fragment.requireView();
                fragment.mPostponedAlpha = requireView.getAlpha();
                requireView.setAlpha(0.0f);
            }
        }
    }
    
    private boolean popBackStackImmediate(final String s, final int n, final int n2) {
        this.execPendingActions(false);
        this.ensureExecReady(true);
        final Fragment mPrimaryNav = this.mPrimaryNav;
        if (mPrimaryNav != null && n < 0 && s == null && mPrimaryNav.getChildFragmentManager().popBackStackImmediate()) {
            return true;
        }
        final boolean popBackStackState = this.popBackStackState(this.mTmpRecords, this.mTmpIsPop, s, n, n2);
        if (popBackStackState) {
            this.mExecutingActions = true;
            try {
                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
            }
            finally {
                this.cleanupExec();
            }
        }
        this.updateOnBackPressedCallbackEnabled();
        this.doPendingDeferredStart();
        this.mFragmentStore.burpActive();
        return popBackStackState;
    }
    
    private int postponePostponableTransactions(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int n, final int n2, final ArraySet<Fragment> set) {
        int i = n2 - 1;
        int n3 = n2;
        while (i >= n) {
            final BackStackRecord element = list.get(i);
            final boolean booleanValue = list2.get(i);
            final boolean b = element.isPostponed() && !element.interactsWith(list, i + 1, n2);
            int index = n3;
            if (b) {
                if (this.mPostponedTransactions == null) {
                    this.mPostponedTransactions = new ArrayList<StartEnterTransitionListener>();
                }
                final StartEnterTransitionListener startEnterTransitionListener = new StartEnterTransitionListener(element, booleanValue);
                this.mPostponedTransactions.add(startEnterTransitionListener);
                element.setOnStartPostponedListener(startEnterTransitionListener);
                if (booleanValue) {
                    element.executeOps();
                }
                else {
                    element.executePopOps(false);
                }
                index = n3 - 1;
                if (i != index) {
                    list.remove(i);
                    list.add(index, element);
                }
                this.addAddedFragments(set);
            }
            --i;
            n3 = index;
        }
        return n3;
    }
    
    private void removeRedundantOperationsAndExecute(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        if (list.isEmpty()) {
            return;
        }
        if (list.size() == list2.size()) {
            this.executePostponedTransaction(list, list2);
            final int size = list.size();
            int i = 0;
            int n = 0;
            while (i < size) {
                int n2 = i;
                int n3 = n;
                if (!list.get(i).mReorderingAllowed) {
                    if (n != i) {
                        this.executeOpsTogether(list, list2, n, i);
                    }
                    int n4 = n3 = i + 1;
                    if (list2.get(i)) {
                        while ((n3 = n4) < size) {
                            n3 = n4;
                            if (!list2.get(n4)) {
                                break;
                            }
                            n3 = n4;
                            if (list.get(n4).mReorderingAllowed) {
                                break;
                            }
                            ++n4;
                        }
                    }
                    this.executeOpsTogether(list, list2, i, n3);
                    n2 = n3 - 1;
                }
                i = n2 + 1;
                n = n3;
            }
            if (n != size) {
                this.executeOpsTogether(list, list2, n, size);
            }
            return;
        }
        throw new IllegalStateException("Internal error with the back stack records");
    }
    
    private void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); ++i) {
                this.mBackStackChangeListeners.get(i).onBackStackChanged();
            }
        }
    }
    
    static int reverseTransit(final int n) {
        int n2 = 8194;
        if (n != 4097) {
            if (n != 4099) {
                if (n != 8194) {
                    n2 = 0;
                }
                else {
                    n2 = 4097;
                }
            }
            else {
                n2 = 4099;
            }
        }
        return n2;
    }
    
    private void setVisibleRemovingFragment(final Fragment fragment) {
        final ViewGroup fragmentContainer = this.getFragmentContainer(fragment);
        if (fragmentContainer != null) {
            if (fragmentContainer.getTag(R$id.visible_removing_fragment_view_tag) == null) {
                fragmentContainer.setTag(R$id.visible_removing_fragment_view_tag, (Object)fragment);
            }
            ((Fragment)fragmentContainer.getTag(R$id.visible_removing_fragment_view_tag)).setNextAnim(fragment.getNextAnim());
        }
    }
    
    private void startPendingDeferredFragments() {
        final Iterator<FragmentStateManager> iterator = this.mFragmentStore.getActiveFragmentStateManagers().iterator();
        while (iterator.hasNext()) {
            this.performPendingDeferredStart(iterator.next());
        }
    }
    
    private void updateOnBackPressedCallbackEnabled() {
        synchronized (this.mPendingActions) {
            final boolean empty = this.mPendingActions.isEmpty();
            boolean enabled = true;
            if (!empty) {
                this.mOnBackPressedCallback.setEnabled(true);
                return;
            }
            // monitorexit(this.mPendingActions)
            final OnBackPressedCallback mOnBackPressedCallback = this.mOnBackPressedCallback;
            if (this.getBackStackEntryCount() <= 0 || !this.isPrimaryNavigation(this.mParent)) {
                enabled = false;
            }
            mOnBackPressedCallback.setEnabled(enabled);
        }
    }
    
    void addBackStackState(final BackStackRecord e) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList<BackStackRecord>();
        }
        this.mBackStack.add(e);
    }
    
    void addCancellationSignal(final Fragment key, final CancellationSignal e) {
        if (this.mExitAnimationCancellationSignals.get(key) == null) {
            this.mExitAnimationCancellationSignals.put(key, new HashSet<CancellationSignal>());
        }
        this.mExitAnimationCancellationSignals.get(key).add(e);
    }
    
    void addFragment(final Fragment obj) {
        if (isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("add: ");
            sb.append(obj);
            Log.v("FragmentManager", sb.toString());
        }
        this.mFragmentStore.makeActive(this.createOrGetFragmentStateManager(obj));
        if (!obj.mDetached) {
            this.mFragmentStore.addFragment(obj);
            obj.mRemoving = false;
            if (obj.mView == null) {
                obj.mHiddenChanged = false;
            }
            if (this.isMenuAvailable(obj)) {
                this.mNeedMenuInvalidate = true;
            }
        }
    }
    
    int allocBackStackIndex() {
        return this.mBackStackIndex.getAndIncrement();
    }
    
    void attachController(final FragmentHostCallback<?> mHost, final FragmentContainer mContainer, final Fragment mParent) {
        if (this.mHost == null) {
            this.mHost = mHost;
            this.mContainer = mContainer;
            if ((this.mParent = mParent) != null) {
                this.updateOnBackPressedCallbackEnabled();
            }
            if (mHost instanceof OnBackPressedDispatcherOwner) {
                Object o = mHost;
                this.mOnBackPressedDispatcher = ((OnBackPressedDispatcherOwner)o).getOnBackPressedDispatcher();
                if (mParent != null) {
                    o = mParent;
                }
                this.mOnBackPressedDispatcher.addCallback((LifecycleOwner)o, this.mOnBackPressedCallback);
            }
            if (mParent != null) {
                this.mNonConfig = mParent.mFragmentManager.getChildNonConfig(mParent);
            }
            else if (mHost instanceof ViewModelStoreOwner) {
                this.mNonConfig = FragmentManagerViewModel.getInstance(((ViewModelStoreOwner)mHost).getViewModelStore());
            }
            else {
                this.mNonConfig = new FragmentManagerViewModel(false);
            }
            this.mNonConfig.setIsStateSaved(this.isStateSaved());
            this.mFragmentStore.setNonConfig(this.mNonConfig);
            return;
        }
        throw new IllegalStateException("Already attached");
    }
    
    void attachFragment(final Fragment fragment) {
        if (isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("attach: ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (!fragment.mAdded) {
                this.mFragmentStore.addFragment(fragment);
                if (isLoggingEnabled(2)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("add from attach: ");
                    sb2.append(fragment);
                    Log.v("FragmentManager", sb2.toString());
                }
                if (this.isMenuAvailable(fragment)) {
                    this.mNeedMenuInvalidate = true;
                }
            }
        }
    }
    
    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }
    
    boolean checkForMenus() {
        final Iterator<Fragment> iterator = this.mFragmentStore.getActiveFragments().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final Fragment fragment = iterator.next();
            int menuAvailable = n;
            if (fragment != null) {
                menuAvailable = (this.isMenuAvailable(fragment) ? 1 : 0);
            }
            if ((n = menuAvailable) != 0) {
                return true;
            }
        }
        return false;
    }
    
    void completeExecute(final BackStackRecord e, final boolean b, final boolean b2, final boolean b3) {
        if (b) {
            e.executePopOps(b3);
        }
        else {
            e.executeOps();
        }
        final ArrayList<BackStackRecord> list = new ArrayList<BackStackRecord>(1);
        final ArrayList<Boolean> list2 = new ArrayList<Boolean>(1);
        list.add(e);
        list2.add(b);
        if (b2 && this.mCurState >= 1) {
            FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, list, list2, 0, 1, true, this.mFragmentTransitionCallback);
        }
        if (b3) {
            this.moveToState(this.mCurState, true);
        }
        for (final Fragment fragment : this.mFragmentStore.getActiveFragments()) {
            if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && e.interactsWith(fragment.mContainerId)) {
                final float mPostponedAlpha = fragment.mPostponedAlpha;
                if (mPostponedAlpha > 0.0f) {
                    fragment.mView.setAlpha(mPostponedAlpha);
                }
                if (b3) {
                    fragment.mPostponedAlpha = 0.0f;
                }
                else {
                    fragment.mPostponedAlpha = -1.0f;
                    fragment.mIsNewlyAdded = false;
                }
            }
        }
    }
    
    FragmentStateManager createOrGetFragmentStateManager(final Fragment fragment) {
        final FragmentStateManager fragmentStateManager = this.mFragmentStore.getFragmentStateManager(fragment.mWho);
        if (fragmentStateManager != null) {
            return fragmentStateManager;
        }
        final FragmentStateManager fragmentStateManager2 = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, fragment);
        fragmentStateManager2.restoreState(this.mHost.getContext().getClassLoader());
        fragmentStateManager2.setFragmentManagerState(this.mCurState);
        return fragmentStateManager2;
    }
    
    void detachFragment(final Fragment visibleRemovingFragment) {
        if (isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("detach: ");
            sb.append(visibleRemovingFragment);
            Log.v("FragmentManager", sb.toString());
        }
        if (!visibleRemovingFragment.mDetached) {
            visibleRemovingFragment.mDetached = true;
            if (visibleRemovingFragment.mAdded) {
                if (isLoggingEnabled(2)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("remove from detach: ");
                    sb2.append(visibleRemovingFragment);
                    Log.v("FragmentManager", sb2.toString());
                }
                this.mFragmentStore.removeFragment(visibleRemovingFragment);
                if (this.isMenuAvailable(visibleRemovingFragment)) {
                    this.mNeedMenuInvalidate = true;
                }
                this.setVisibleRemovingFragment(visibleRemovingFragment);
            }
        }
    }
    
    void dispatchActivityCreated() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        this.dispatchStateChange(3);
    }
    
    void dispatchAttach() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        this.dispatchStateChange(0);
    }
    
    void dispatchConfigurationChanged(final Configuration configuration) {
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performConfigurationChanged(configuration);
            }
        }
    }
    
    boolean dispatchContextItemSelected(final MenuItem menuItem) {
        if (this.mCurState < 1) {
            return false;
        }
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }
    
    void dispatchCreate() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        this.dispatchStateChange(1);
    }
    
    boolean dispatchCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        final int mCurState = this.mCurState;
        int i = 0;
        if (mCurState < 1) {
            return false;
        }
        ArrayList<Fragment> mCreatedMenus = null;
        final Iterator<Fragment> iterator = this.mFragmentStore.getFragments().iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final Fragment e = iterator.next();
            if (e != null && e.performCreateOptionsMenu(menu, menuInflater)) {
                ArrayList<Fragment> list;
                if ((list = mCreatedMenus) == null) {
                    list = new ArrayList<Fragment>();
                }
                list.add(e);
                b = true;
                mCreatedMenus = list;
            }
        }
        if (this.mCreatedMenus != null) {
            while (i < this.mCreatedMenus.size()) {
                final Fragment o = this.mCreatedMenus.get(i);
                if (mCreatedMenus == null || !mCreatedMenus.contains(o)) {
                    o.onDestroyOptionsMenu();
                }
                ++i;
            }
        }
        this.mCreatedMenus = mCreatedMenus;
        return b;
    }
    
    void dispatchDestroy() {
        this.execPendingActions(this.mDestroyed = true);
        this.endAnimatingAwayFragments();
        this.dispatchStateChange(-1);
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
        if (this.mOnBackPressedDispatcher != null) {
            this.mOnBackPressedCallback.remove();
            this.mOnBackPressedDispatcher = null;
        }
    }
    
    void dispatchDestroyView() {
        this.dispatchStateChange(1);
    }
    
    void dispatchLowMemory() {
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performLowMemory();
            }
        }
    }
    
    void dispatchMultiWindowModeChanged(final boolean b) {
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performMultiWindowModeChanged(b);
            }
        }
    }
    
    boolean dispatchOptionsItemSelected(final MenuItem menuItem) {
        if (this.mCurState < 1) {
            return false;
        }
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }
    
    void dispatchOptionsMenuClosed(final Menu menu) {
        if (this.mCurState < 1) {
            return;
        }
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performOptionsMenuClosed(menu);
            }
        }
    }
    
    void dispatchPause() {
        this.dispatchStateChange(4);
    }
    
    void dispatchPictureInPictureModeChanged(final boolean b) {
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performPictureInPictureModeChanged(b);
            }
        }
    }
    
    boolean dispatchPrepareOptionsMenu(final Menu menu) {
        final int mCurState = this.mCurState;
        boolean b = false;
        if (mCurState < 1) {
            return false;
        }
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                b = true;
            }
        }
        return b;
    }
    
    void dispatchPrimaryNavigationFragmentChanged() {
        this.updateOnBackPressedCallbackEnabled();
        this.dispatchParentPrimaryNavigationFragmentChanged(this.mPrimaryNav);
    }
    
    void dispatchResume() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        this.dispatchStateChange(6);
    }
    
    void dispatchStart() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        this.dispatchStateChange(4);
    }
    
    void dispatchStop() {
        this.mStopped = true;
        this.mNonConfig.setIsStateSaved(true);
        this.dispatchStateChange(3);
    }
    
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("    ");
        final String string = sb.toString();
        this.mFragmentStore.dump(s, fileDescriptor, printWriter, array);
        final ArrayList<Fragment> mCreatedMenus = this.mCreatedMenus;
        final int n = 0;
        if (mCreatedMenus != null) {
            final int size = mCreatedMenus.size();
            if (size > 0) {
                printWriter.print(s);
                printWriter.println("Fragments Created Menus:");
                for (int i = 0; i < size; ++i) {
                    final Fragment fragment = this.mCreatedMenus.get(i);
                    printWriter.print(s);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(fragment.toString());
                }
            }
        }
        final ArrayList<BackStackRecord> mBackStack = this.mBackStack;
        if (mBackStack != null) {
            final int size2 = mBackStack.size();
            if (size2 > 0) {
                printWriter.print(s);
                printWriter.println("Back Stack:");
                for (int j = 0; j < size2; ++j) {
                    final BackStackRecord backStackRecord = this.mBackStack.get(j);
                    printWriter.print(s);
                    printWriter.print("  #");
                    printWriter.print(j);
                    printWriter.print(": ");
                    printWriter.println(backStackRecord.toString());
                    backStackRecord.dump(string, printWriter);
                }
            }
        }
        printWriter.print(s);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Back Stack Index: ");
        sb2.append(this.mBackStackIndex.get());
        printWriter.println(sb2.toString());
        synchronized (this.mPendingActions) {
            final int size3 = this.mPendingActions.size();
            if (size3 > 0) {
                printWriter.print(s);
                printWriter.println("Pending Actions:");
                for (int k = n; k < size3; ++k) {
                    final OpGenerator x = this.mPendingActions.get(k);
                    printWriter.print(s);
                    printWriter.print("  #");
                    printWriter.print(k);
                    printWriter.print(": ");
                    printWriter.println(x);
                }
            }
            // monitorexit(this.mPendingActions)
            printWriter.print(s);
            printWriter.println("FragmentManager misc state:");
            printWriter.print(s);
            printWriter.print("  mHost=");
            printWriter.println(this.mHost);
            printWriter.print(s);
            printWriter.print("  mContainer=");
            printWriter.println(this.mContainer);
            if (this.mParent != null) {
                printWriter.print(s);
                printWriter.print("  mParent=");
                printWriter.println(this.mParent);
            }
            printWriter.print(s);
            printWriter.print("  mCurState=");
            printWriter.print(this.mCurState);
            printWriter.print(" mStateSaved=");
            printWriter.print(this.mStateSaved);
            printWriter.print(" mStopped=");
            printWriter.print(this.mStopped);
            printWriter.print(" mDestroyed=");
            printWriter.println(this.mDestroyed);
            if (this.mNeedMenuInvalidate) {
                printWriter.print(s);
                printWriter.print("  mNeedMenuInvalidate=");
                printWriter.println(this.mNeedMenuInvalidate);
            }
        }
    }
    
    void enqueueAction(final OpGenerator e, final boolean b) {
        if (!b) {
            if (this.mHost == null) {
                if (this.mDestroyed) {
                    throw new IllegalStateException("FragmentManager has been destroyed");
                }
                throw new IllegalStateException("FragmentManager has not been attached to a host.");
            }
            else {
                this.checkStateLoss();
            }
        }
        synchronized (this.mPendingActions) {
            if (this.mHost != null) {
                this.mPendingActions.add(e);
                this.scheduleCommit();
                return;
            }
            if (b) {
                return;
            }
            throw new IllegalStateException("Activity has been destroyed");
        }
    }
    
    boolean execPendingActions(boolean b) {
        this.ensureExecReady(b);
        b = false;
        while (this.generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                this.cleanupExec();
                b = true;
                continue;
            }
            finally {
                this.cleanupExec();
            }
            break;
        }
        this.updateOnBackPressedCallbackEnabled();
        this.doPendingDeferredStart();
        this.mFragmentStore.burpActive();
        return b;
    }
    
    void execSingleAction(final OpGenerator opGenerator, final boolean b) {
        if (b && (this.mHost == null || this.mDestroyed)) {
            return;
        }
        this.ensureExecReady(b);
        if (opGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
            }
            finally {
                this.cleanupExec();
            }
        }
        this.updateOnBackPressedCallbackEnabled();
        this.doPendingDeferredStart();
        this.mFragmentStore.burpActive();
    }
    
    public boolean executePendingTransactions() {
        final boolean execPendingActions = this.execPendingActions(true);
        this.forcePostponedTransactions();
        return execPendingActions;
    }
    
    Fragment findActiveFragment(final String s) {
        return this.mFragmentStore.findActiveFragment(s);
    }
    
    public Fragment findFragmentById(final int n) {
        return this.mFragmentStore.findFragmentById(n);
    }
    
    public Fragment findFragmentByTag(final String s) {
        return this.mFragmentStore.findFragmentByTag(s);
    }
    
    Fragment findFragmentByWho(final String s) {
        return this.mFragmentStore.findFragmentByWho(s);
    }
    
    public int getBackStackEntryCount() {
        final ArrayList<BackStackRecord> mBackStack = this.mBackStack;
        int size;
        if (mBackStack != null) {
            size = mBackStack.size();
        }
        else {
            size = 0;
        }
        return size;
    }
    
    FragmentContainer getContainer() {
        return this.mContainer;
    }
    
    public FragmentFactory getFragmentFactory() {
        final FragmentFactory mFragmentFactory = this.mFragmentFactory;
        if (mFragmentFactory != null) {
            return mFragmentFactory;
        }
        final Fragment mParent = this.mParent;
        if (mParent != null) {
            return mParent.mFragmentManager.getFragmentFactory();
        }
        return this.mHostFragmentFactory;
    }
    
    FragmentStore getFragmentStore() {
        return this.mFragmentStore;
    }
    
    public List<Fragment> getFragments() {
        return this.mFragmentStore.getFragments();
    }
    
    FragmentHostCallback<?> getHost() {
        return this.mHost;
    }
    
    LayoutInflater$Factory2 getLayoutInflaterFactory() {
        return (LayoutInflater$Factory2)this.mLayoutInflaterFactory;
    }
    
    FragmentLifecycleCallbacksDispatcher getLifecycleCallbacksDispatcher() {
        return this.mLifecycleCallbacksDispatcher;
    }
    
    Fragment getParent() {
        return this.mParent;
    }
    
    public Fragment getPrimaryNavigationFragment() {
        return this.mPrimaryNav;
    }
    
    SpecialEffectsControllerFactory getSpecialEffectsControllerFactory() {
        final SpecialEffectsControllerFactory mSpecialEffectsControllerFactory = this.mSpecialEffectsControllerFactory;
        if (mSpecialEffectsControllerFactory != null) {
            return mSpecialEffectsControllerFactory;
        }
        final Fragment mParent = this.mParent;
        if (mParent != null) {
            return mParent.mFragmentManager.getSpecialEffectsControllerFactory();
        }
        return this.mDefaultSpecialEffectsControllerFactory;
    }
    
    ViewModelStore getViewModelStore(final Fragment fragment) {
        return this.mNonConfig.getViewModelStore(fragment);
    }
    
    void handleOnBackPressed() {
        this.execPendingActions(true);
        if (this.mOnBackPressedCallback.isEnabled()) {
            this.popBackStackImmediate();
        }
        else {
            this.mOnBackPressedDispatcher.onBackPressed();
        }
    }
    
    void hideFragment(final Fragment fragment) {
        if (isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("hide: ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
        if (!fragment.mHidden) {
            fragment.mHidden = true;
            fragment.mHiddenChanged ^= true;
            this.setVisibleRemovingFragment(fragment);
        }
    }
    
    public boolean isDestroyed() {
        return this.mDestroyed;
    }
    
    boolean isPrimaryNavigation(final Fragment fragment) {
        boolean b = true;
        if (fragment == null) {
            return true;
        }
        final FragmentManager mFragmentManager = fragment.mFragmentManager;
        if (!fragment.equals(mFragmentManager.getPrimaryNavigationFragment()) || !this.isPrimaryNavigation(mFragmentManager.mParent)) {
            b = false;
        }
        return b;
    }
    
    boolean isStateAtLeast(final int n) {
        return this.mCurState >= n;
    }
    
    public boolean isStateSaved() {
        return this.mStateSaved || this.mStopped;
    }
    
    void moveFragmentToExpectedState(final Fragment obj) {
        if (!this.mFragmentStore.containsActiveFragment(obj.mWho)) {
            if (isLoggingEnabled(3)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Ignoring moving ");
                sb.append(obj);
                sb.append(" to state ");
                sb.append(this.mCurState);
                sb.append("since it is not added to ");
                sb.append(this);
                Log.d("FragmentManager", sb.toString());
            }
            return;
        }
        this.moveToState(obj);
        if (obj.mView != null) {
            final Fragment fragmentUnder = this.mFragmentStore.findFragmentUnder(obj);
            if (fragmentUnder != null) {
                final View mView = fragmentUnder.mView;
                final ViewGroup mContainer = obj.mContainer;
                final int indexOfChild = mContainer.indexOfChild(mView);
                final int indexOfChild2 = mContainer.indexOfChild(obj.mView);
                if (indexOfChild2 < indexOfChild) {
                    mContainer.removeViewAt(indexOfChild2);
                    mContainer.addView(obj.mView, indexOfChild);
                }
            }
            if (obj.mIsNewlyAdded && obj.mContainer != null) {
                final float mPostponedAlpha = obj.mPostponedAlpha;
                if (mPostponedAlpha > 0.0f) {
                    obj.mView.setAlpha(mPostponedAlpha);
                }
                obj.mPostponedAlpha = 0.0f;
                obj.mIsNewlyAdded = false;
                final FragmentAnim.AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(this.mHost.getContext(), obj, true);
                if (loadAnimation != null) {
                    final Animation animation = loadAnimation.animation;
                    if (animation != null) {
                        obj.mView.startAnimation(animation);
                    }
                    else {
                        loadAnimation.animator.setTarget((Object)obj.mView);
                        loadAnimation.animator.start();
                    }
                }
            }
        }
        if (obj.mHiddenChanged) {
            this.completeShowHideFragment(obj);
        }
    }
    
    void moveToState(int mCurState, final boolean b) {
        if (this.mHost == null && mCurState != -1) {
            throw new IllegalStateException("No activity");
        }
        if (!b && mCurState == this.mCurState) {
            return;
        }
        this.mCurState = mCurState;
        if (FragmentManager.USE_STATE_MANAGER) {
            this.mFragmentStore.moveToExpectedState();
        }
        else {
            final Iterator<Fragment> iterator = this.mFragmentStore.getFragments().iterator();
            while (iterator.hasNext()) {
                this.moveFragmentToExpectedState(iterator.next());
            }
            for (final FragmentStateManager fragmentStateManager : this.mFragmentStore.getActiveFragmentStateManagers()) {
                final Fragment fragment = fragmentStateManager.getFragment();
                if (!fragment.mIsNewlyAdded) {
                    this.moveFragmentToExpectedState(fragment);
                }
                if (fragment.mRemoving && !fragment.isInBackStack()) {
                    mCurState = 1;
                }
                else {
                    mCurState = 0;
                }
                if (mCurState != 0) {
                    this.mFragmentStore.makeInactive(fragmentStateManager);
                }
            }
        }
        this.startPendingDeferredFragments();
        if (this.mNeedMenuInvalidate) {
            final FragmentHostCallback<?> mHost = this.mHost;
            if (mHost != null && this.mCurState == 6) {
                mHost.onSupportInvalidateOptionsMenu();
                this.mNeedMenuInvalidate = false;
            }
        }
    }
    
    void moveToState(final Fragment fragment) {
        this.moveToState(fragment, this.mCurState);
    }
    
    void moveToState(final Fragment fragment, int min) {
        final FragmentStateManager fragmentStateManager = this.mFragmentStore.getFragmentStateManager(fragment.mWho);
        final int n = 1;
        FragmentStateManager fragmentStateManager2 = fragmentStateManager;
        if (fragmentStateManager == null) {
            fragmentStateManager2 = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, fragment);
            fragmentStateManager2.setFragmentManagerState(1);
        }
        min = Math.min(min, fragmentStateManager2.computeExpectedState());
        final int mState = fragment.mState;
        int n2 = 0;
        Label_0553: {
            if (mState <= min) {
                if (mState < min && !this.mExitAnimationCancellationSignals.isEmpty()) {
                    this.cancelExitAnimation(fragment);
                }
                final int mState2 = fragment.mState;
                Label_0189: {
                    Label_0179: {
                        Label_0149: {
                            if (mState2 != -1) {
                                if (mState2 != 0) {
                                    if (mState2 == 1) {
                                        break Label_0149;
                                    }
                                    if (mState2 == 3) {
                                        break Label_0179;
                                    }
                                    if (mState2 != 4) {
                                        n2 = min;
                                        break Label_0553;
                                    }
                                    break Label_0189;
                                }
                            }
                            else if (min > -1) {
                                fragmentStateManager2.attach();
                            }
                            if (min > 0) {
                                fragmentStateManager2.create();
                            }
                        }
                        if (min > -1) {
                            fragmentStateManager2.ensureInflatedView();
                        }
                        if (min > 1) {
                            fragmentStateManager2.createView();
                            fragmentStateManager2.activityCreated();
                            fragmentStateManager2.restoreViewState();
                        }
                    }
                    if (min > 3) {
                        fragmentStateManager2.start();
                    }
                }
                if ((n2 = min) > 4) {
                    fragmentStateManager2.resume();
                    n2 = min;
                }
            }
            else if (mState > (n2 = min)) {
                if (mState != 0) {
                    if (mState != 1) {
                        if (mState != 3) {
                            if (mState != 4) {
                                if (mState != 6) {
                                    n2 = min;
                                    break Label_0553;
                                }
                                if (min < 6) {
                                    fragmentStateManager2.pause();
                                }
                            }
                            if (min < 4) {
                                fragmentStateManager2.stop();
                            }
                        }
                        if (min < 3) {
                            if (isLoggingEnabled(3)) {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("movefrom ACTIVITY_CREATED: ");
                                sb.append(fragment);
                                Log.d("FragmentManager", sb.toString());
                            }
                            if (fragment.mView != null && this.mHost.onShouldSaveFragmentState(fragment) && fragment.mSavedViewState == null) {
                                fragmentStateManager2.saveViewState();
                            }
                            final FragmentAnim.AnimationOrAnimator animationOrAnimator = null;
                            final View mView = fragment.mView;
                            if (mView != null) {
                                final ViewGroup mContainer = fragment.mContainer;
                                if (mContainer != null) {
                                    mContainer.endViewTransition(mView);
                                    fragment.mView.clearAnimation();
                                    if (!fragment.isRemovingParent()) {
                                        FragmentAnim.AnimationOrAnimator loadAnimation = animationOrAnimator;
                                        if (this.mCurState > -1) {
                                            loadAnimation = animationOrAnimator;
                                            if (!this.mDestroyed) {
                                                loadAnimation = animationOrAnimator;
                                                if (fragment.mView.getVisibility() == 0) {
                                                    loadAnimation = animationOrAnimator;
                                                    if (fragment.mPostponedAlpha >= 0.0f) {
                                                        loadAnimation = FragmentAnim.loadAnimation(this.mHost.getContext(), fragment, false);
                                                    }
                                                }
                                            }
                                        }
                                        fragment.mPostponedAlpha = 0.0f;
                                        final ViewGroup mContainer2 = fragment.mContainer;
                                        final View mView2 = fragment.mView;
                                        if (loadAnimation != null) {
                                            FragmentAnim.animateRemoveFragment(fragment, loadAnimation, this.mFragmentTransitionCallback);
                                        }
                                        mContainer2.removeView(mView2);
                                        if (mContainer2 != fragment.mContainer) {
                                            return;
                                        }
                                    }
                                }
                            }
                            if (this.mExitAnimationCancellationSignals.get(fragment) == null) {
                                fragmentStateManager2.destroyFragmentView();
                            }
                        }
                    }
                    if (min < 1) {
                        if (this.mExitAnimationCancellationSignals.get(fragment) != null) {
                            min = n;
                        }
                        else {
                            fragmentStateManager2.destroy();
                        }
                    }
                }
                if (min < 0) {
                    fragmentStateManager2.detach();
                }
                n2 = min;
            }
        }
        if (fragment.mState != n2) {
            if (isLoggingEnabled(3)) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("moveToState: Fragment state for ");
                sb2.append(fragment);
                sb2.append(" not updated inline; expected state ");
                sb2.append(n2);
                sb2.append(" found ");
                sb2.append(fragment.mState);
                Log.d("FragmentManager", sb2.toString());
            }
            fragment.mState = n2;
        }
    }
    
    void noteStateNotSaved() {
        if (this.mHost == null) {
            return;
        }
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        for (final Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.noteStateNotSaved();
            }
        }
    }
    
    void performPendingDeferredStart(final FragmentStateManager fragmentStateManager) {
        final Fragment fragment = fragmentStateManager.getFragment();
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
                return;
            }
            fragment.mDeferStart = false;
            if (FragmentManager.USE_STATE_MANAGER) {
                fragmentStateManager.moveToExpectedState();
            }
            else {
                this.moveToState(fragment);
            }
        }
    }
    
    public void popBackStack(final int i, final int n) {
        if (i >= 0) {
            this.enqueueAction((OpGenerator)new PopBackStackState(null, i, n), false);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Bad id: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    public boolean popBackStackImmediate() {
        return this.popBackStackImmediate(null, -1, 0);
    }
    
    boolean popBackStackState(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final String s, int index, int i) {
        final Boolean true = Boolean.TRUE;
        final ArrayList<BackStackRecord> mBackStack = this.mBackStack;
        if (mBackStack == null) {
            return false;
        }
        if (s == null && index < 0 && (i & 0x1) == 0x0) {
            index = mBackStack.size() - 1;
            if (index < 0) {
                return false;
            }
            list.add(this.mBackStack.remove(index));
            list2.add(true);
        }
        else {
            if (s == null && index < 0) {
                index = -1;
            }
            else {
                int j;
                for (j = this.mBackStack.size() - 1; j >= 0; --j) {
                    final BackStackRecord backStackRecord = this.mBackStack.get(j);
                    if (s != null && s.equals(backStackRecord.getName())) {
                        break;
                    }
                    if (index >= 0 && index == backStackRecord.mIndex) {
                        break;
                    }
                }
                if (j < 0) {
                    return false;
                }
                int n = j;
                if ((i & 0x1) != 0x0) {
                    while (true) {
                        i = j - 1;
                        if ((n = i) < 0) {
                            break;
                        }
                        final BackStackRecord backStackRecord2 = this.mBackStack.get(i);
                        if (s != null) {
                            j = i;
                            if (s.equals(backStackRecord2.getName())) {
                                continue;
                            }
                        }
                        n = i;
                        if (index < 0) {
                            break;
                        }
                        n = i;
                        if (index != backStackRecord2.mIndex) {
                            break;
                        }
                        j = i;
                    }
                }
                index = n;
            }
            if (index == this.mBackStack.size() - 1) {
                return false;
            }
            for (i = this.mBackStack.size() - 1; i > index; --i) {
                list.add(this.mBackStack.remove(i));
                list2.add(true);
            }
        }
        return true;
    }
    
    void removeCancellationSignal(final Fragment fragment, final CancellationSignal o) {
        final HashSet<CancellationSignal> set = this.mExitAnimationCancellationSignals.get(fragment);
        if (set != null && set.remove(o) && set.isEmpty()) {
            this.mExitAnimationCancellationSignals.remove(fragment);
            if (fragment.mState < 4) {
                this.destroyFragmentView(fragment);
                this.moveToState(fragment);
            }
        }
    }
    
    void removeFragment(final Fragment fragment) {
        if (isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("remove: ");
            sb.append(fragment);
            sb.append(" nesting=");
            sb.append(fragment.mBackStackNesting);
            Log.v("FragmentManager", sb.toString());
        }
        final boolean inBackStack = fragment.isInBackStack();
        if (!fragment.mDetached || (inBackStack ^ true)) {
            this.mFragmentStore.removeFragment(fragment);
            if (this.isMenuAvailable(fragment)) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mRemoving = true;
            this.setVisibleRemovingFragment(fragment);
        }
    }
    
    void restoreSaveState(final Parcelable parcelable) {
        if (parcelable == null) {
            return;
        }
        final FragmentManagerState fragmentManagerState = (FragmentManagerState)parcelable;
        if (fragmentManagerState.mActive == null) {
            return;
        }
        this.mFragmentStore.resetActiveFragments();
        for (final FragmentState fragmentState : fragmentManagerState.mActive) {
            if (fragmentState != null) {
                final Fragment retainedFragmentByWho = this.mNonConfig.findRetainedFragmentByWho(fragmentState.mWho);
                FragmentStateManager fragmentStateManager;
                if (retainedFragmentByWho != null) {
                    if (isLoggingEnabled(2)) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("restoreSaveState: re-attaching retained ");
                        sb.append(retainedFragmentByWho);
                        Log.v("FragmentManager", sb.toString());
                    }
                    fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, retainedFragmentByWho, fragmentState);
                }
                else {
                    fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, this.mHost.getContext().getClassLoader(), this.getFragmentFactory(), fragmentState);
                }
                final Fragment fragment = fragmentStateManager.getFragment();
                fragment.mFragmentManager = this;
                if (isLoggingEnabled(2)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("restoreSaveState: active (");
                    sb2.append(fragment.mWho);
                    sb2.append("): ");
                    sb2.append(fragment);
                    Log.v("FragmentManager", sb2.toString());
                }
                fragmentStateManager.restoreState(this.mHost.getContext().getClassLoader());
                this.mFragmentStore.makeActive(fragmentStateManager);
                fragmentStateManager.setFragmentManagerState(this.mCurState);
            }
        }
        for (final Fragment obj : this.mNonConfig.getRetainedFragments()) {
            if (!this.mFragmentStore.containsActiveFragment(obj.mWho)) {
                if (isLoggingEnabled(2)) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Discarding retained Fragment ");
                    sb3.append(obj);
                    sb3.append(" that was not found in the set of active Fragments ");
                    sb3.append(fragmentManagerState.mActive);
                    Log.v("FragmentManager", sb3.toString());
                }
                this.mNonConfig.removeRetainedFragment(obj);
                obj.mFragmentManager = this;
                final FragmentStateManager fragmentStateManager2 = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, obj);
                fragmentStateManager2.setFragmentManagerState(1);
                fragmentStateManager2.moveToExpectedState();
                obj.mRemoving = true;
                fragmentStateManager2.moveToExpectedState();
            }
        }
        this.mFragmentStore.restoreAddedFragments(fragmentManagerState.mAdded);
        if (fragmentManagerState.mBackStack != null) {
            this.mBackStack = new ArrayList<BackStackRecord>(fragmentManagerState.mBackStack.length);
            int i = 0;
            while (true) {
                final BackStackState[] mBackStack = fragmentManagerState.mBackStack;
                if (i >= mBackStack.length) {
                    break;
                }
                final BackStackRecord instantiate = mBackStack[i].instantiate(this);
                if (isLoggingEnabled(2)) {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("restoreAllState: back stack #");
                    sb4.append(i);
                    sb4.append(" (index ");
                    sb4.append(instantiate.mIndex);
                    sb4.append("): ");
                    sb4.append(instantiate);
                    Log.v("FragmentManager", sb4.toString());
                    final PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
                    instantiate.dump("  ", printWriter, false);
                    printWriter.close();
                }
                this.mBackStack.add(instantiate);
                ++i;
            }
        }
        else {
            this.mBackStack = null;
        }
        this.mBackStackIndex.set(fragmentManagerState.mBackStackIndex);
        final String mPrimaryNavActiveWho = fragmentManagerState.mPrimaryNavActiveWho;
        if (mPrimaryNavActiveWho != null) {
            this.dispatchParentPrimaryNavigationFragmentChanged(this.mPrimaryNav = this.findActiveFragment(mPrimaryNavActiveWho));
        }
    }
    
    Parcelable saveAllState() {
        this.forcePostponedTransactions();
        this.endAnimatingAwayFragments();
        this.execPendingActions(true);
        this.mStateSaved = true;
        this.mNonConfig.setIsStateSaved(true);
        final ArrayList<FragmentState> saveActiveFragments = this.mFragmentStore.saveActiveFragments();
        final boolean empty = saveActiveFragments.isEmpty();
        final BackStackState[] array = null;
        if (empty) {
            if (isLoggingEnabled(2)) {
                Log.v("FragmentManager", "saveAllState: no fragments!");
            }
            return null;
        }
        final ArrayList<String> saveAddedFragments = this.mFragmentStore.saveAddedFragments();
        final ArrayList<BackStackRecord> mBackStack = this.mBackStack;
        BackStackState[] mBackStack2 = array;
        if (mBackStack != null) {
            final int size = mBackStack.size();
            mBackStack2 = array;
            if (size > 0) {
                final BackStackState[] array2 = new BackStackState[size];
                int index = 0;
                while (true) {
                    mBackStack2 = array2;
                    if (index >= size) {
                        break;
                    }
                    array2[index] = new BackStackState(this.mBackStack.get(index));
                    if (isLoggingEnabled(2)) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("saveAllState: adding back stack #");
                        sb.append(index);
                        sb.append(": ");
                        sb.append(this.mBackStack.get(index));
                        Log.v("FragmentManager", sb.toString());
                    }
                    ++index;
                }
            }
        }
        final FragmentManagerState fragmentManagerState = new FragmentManagerState();
        fragmentManagerState.mActive = saveActiveFragments;
        fragmentManagerState.mAdded = saveAddedFragments;
        fragmentManagerState.mBackStack = mBackStack2;
        fragmentManagerState.mBackStackIndex = this.mBackStackIndex.get();
        final Fragment mPrimaryNav = this.mPrimaryNav;
        if (mPrimaryNav != null) {
            fragmentManagerState.mPrimaryNavActiveWho = mPrimaryNav.mWho;
        }
        return (Parcelable)fragmentManagerState;
    }
    
    void scheduleCommit() {
        synchronized (this.mPendingActions) {
            final ArrayList<StartEnterTransitionListener> mPostponedTransactions = this.mPostponedTransactions;
            boolean b = false;
            final boolean b2 = mPostponedTransactions != null && !this.mPostponedTransactions.isEmpty();
            if (this.mPendingActions.size() == 1) {
                b = true;
            }
            if (b2 || b) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
                this.updateOnBackPressedCallbackEnabled();
            }
        }
    }
    
    void setExitAnimationOrder(final Fragment fragment, final boolean b) {
        final ViewGroup fragmentContainer = this.getFragmentContainer(fragment);
        if (fragmentContainer != null && fragmentContainer instanceof FragmentContainerView) {
            ((FragmentContainerView)fragmentContainer).setDrawDisappearingViewsLast(b ^ true);
        }
    }
    
    void setMaxLifecycle(final Fragment obj, final Lifecycle.State mMaxState) {
        if (obj.equals(this.findActiveFragment(obj.mWho)) && (obj.mHost == null || obj.mFragmentManager == this)) {
            obj.mMaxState = mMaxState;
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(obj);
        sb.append(" is not an active fragment of FragmentManager ");
        sb.append(this);
        throw new IllegalArgumentException(sb.toString());
    }
    
    void setPrimaryNavigationFragment(final Fragment fragment) {
        Label_0085: {
            if (fragment != null) {
                if (fragment.equals(this.findActiveFragment(fragment.mWho))) {
                    if (fragment.mHost == null) {
                        break Label_0085;
                    }
                    if (fragment.mFragmentManager == this) {
                        break Label_0085;
                    }
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Fragment ");
                sb.append(fragment);
                sb.append(" is not an active fragment of FragmentManager ");
                sb.append(this);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        final Fragment mPrimaryNav = this.mPrimaryNav;
        this.mPrimaryNav = fragment;
        this.dispatchParentPrimaryNavigationFragmentChanged(mPrimaryNav);
        this.dispatchParentPrimaryNavigationFragmentChanged(this.mPrimaryNav);
    }
    
    void showFragment(final Fragment obj) {
        if (isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("show: ");
            sb.append(obj);
            Log.v("FragmentManager", sb.toString());
        }
        if (obj.mHidden) {
            obj.mHidden = false;
            obj.mHiddenChanged ^= true;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        final Fragment mParent = this.mParent;
        if (mParent != null) {
            sb.append(mParent.getClass().getSimpleName());
            sb.append("{");
            sb.append(Integer.toHexString(System.identityHashCode(this.mParent)));
            sb.append("}");
        }
        else {
            final FragmentHostCallback<?> mHost = this.mHost;
            if (mHost != null) {
                sb.append(mHost.getClass().getSimpleName());
                sb.append("{");
                sb.append(Integer.toHexString(System.identityHashCode(this.mHost)));
                sb.append("}");
            }
            else {
                sb.append("null");
            }
        }
        sb.append("}}");
        return sb.toString();
    }
    
    public abstract static class FragmentLifecycleCallbacks
    {
        @Deprecated
        public abstract void onFragmentActivityCreated(final FragmentManager p0, final Fragment p1, final Bundle p2);
        
        public abstract void onFragmentAttached(final FragmentManager p0, final Fragment p1, final Context p2);
        
        public abstract void onFragmentCreated(final FragmentManager p0, final Fragment p1, final Bundle p2);
        
        public abstract void onFragmentDestroyed(final FragmentManager p0, final Fragment p1);
        
        public abstract void onFragmentDetached(final FragmentManager p0, final Fragment p1);
        
        public abstract void onFragmentPaused(final FragmentManager p0, final Fragment p1);
        
        public abstract void onFragmentPreAttached(final FragmentManager p0, final Fragment p1, final Context p2);
        
        public abstract void onFragmentPreCreated(final FragmentManager p0, final Fragment p1, final Bundle p2);
        
        public abstract void onFragmentResumed(final FragmentManager p0, final Fragment p1);
        
        public abstract void onFragmentSaveInstanceState(final FragmentManager p0, final Fragment p1, final Bundle p2);
        
        public abstract void onFragmentStarted(final FragmentManager p0, final Fragment p1);
        
        public abstract void onFragmentStopped(final FragmentManager p0, final Fragment p1);
        
        public abstract void onFragmentViewCreated(final FragmentManager p0, final Fragment p1, final View p2, final Bundle p3);
        
        public abstract void onFragmentViewDestroyed(final FragmentManager p0, final Fragment p1);
    }
    
    public interface OnBackStackChangedListener
    {
        void onBackStackChanged();
    }
    
    interface OpGenerator
    {
        boolean generateOps(final ArrayList<BackStackRecord> p0, final ArrayList<Boolean> p1);
    }
    
    private class PopBackStackState implements OpGenerator
    {
        final int mFlags;
        final int mId;
        final String mName;
        
        PopBackStackState(final String mName, final int mId, final int mFlags) {
            this.mName = mName;
            this.mId = mId;
            this.mFlags = mFlags;
        }
        
        @Override
        public boolean generateOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
            final Fragment mPrimaryNav = FragmentManager.this.mPrimaryNav;
            return (mPrimaryNav == null || this.mId >= 0 || this.mName != null || !mPrimaryNav.getChildFragmentManager().popBackStackImmediate()) && FragmentManager.this.popBackStackState(list, list2, this.mName, this.mId, this.mFlags);
        }
    }
    
    static class StartEnterTransitionListener implements OnStartEnterTransitionListener
    {
        final boolean mIsBack;
        private int mNumPostponed;
        final BackStackRecord mRecord;
        
        StartEnterTransitionListener(final BackStackRecord mRecord, final boolean mIsBack) {
            this.mIsBack = mIsBack;
            this.mRecord = mRecord;
        }
        
        void cancelTransaction() {
            final BackStackRecord mRecord = this.mRecord;
            mRecord.mManager.completeExecute(mRecord, this.mIsBack, false, false);
        }
        
        void completeTransaction() {
            final boolean b = this.mNumPostponed > 0;
            for (final Fragment fragment : this.mRecord.mManager.getFragments()) {
                fragment.setOnStartEnterTransitionListener(null);
                if (b && fragment.isPostponed()) {
                    fragment.startPostponedEnterTransition();
                }
            }
            final BackStackRecord mRecord = this.mRecord;
            mRecord.mManager.completeExecute(mRecord, this.mIsBack, b ^ true, true);
        }
        
        public boolean isReady() {
            return this.mNumPostponed == 0;
        }
        
        @Override
        public void onStartEnterTransition() {
            final int mNumPostponed = this.mNumPostponed - 1;
            this.mNumPostponed = mNumPostponed;
            if (mNumPostponed != 0) {
                return;
            }
            this.mRecord.mManager.scheduleCommit();
        }
        
        @Override
        public void startListening() {
            ++this.mNumPostponed;
        }
    }
}
