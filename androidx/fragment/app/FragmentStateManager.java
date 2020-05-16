// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.os.Parcelable;
import androidx.lifecycle.LifecycleOwner;
import java.util.Iterator;
import android.app.Activity;
import androidx.lifecycle.ViewModelStoreOwner;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.fragment.R$id;
import android.content.res.Resources$NotFoundException;
import android.view.ViewGroup;
import android.util.SparseArray;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import android.os.Bundle;
import androidx.core.os.CancellationSignal;

class FragmentStateManager
{
    private final FragmentLifecycleCallbacksDispatcher mDispatcher;
    private CancellationSignal mEnterAnimationCancellationSignal;
    private CancellationSignal mExitAnimationCancellationSignal;
    private final Fragment mFragment;
    private int mFragmentManagerState;
    private final FragmentStore mFragmentStore;
    private boolean mMovingToState;
    
    FragmentStateManager(final FragmentLifecycleCallbacksDispatcher mDispatcher, final FragmentStore mFragmentStore, final Fragment mFragment) {
        this.mMovingToState = false;
        this.mFragmentManagerState = -1;
        this.mDispatcher = mDispatcher;
        this.mFragmentStore = mFragmentStore;
        this.mFragment = mFragment;
    }
    
    FragmentStateManager(final FragmentLifecycleCallbacksDispatcher mDispatcher, final FragmentStore mFragmentStore, final Fragment mFragment, final FragmentState fragmentState) {
        this.mMovingToState = false;
        this.mFragmentManagerState = -1;
        this.mDispatcher = mDispatcher;
        this.mFragmentStore = mFragmentStore;
        this.mFragment = mFragment;
        mFragment.mSavedViewState = null;
        mFragment.mBackStackNesting = 0;
        mFragment.mInLayout = false;
        mFragment.mAdded = false;
        final Fragment mTarget = mFragment.mTarget;
        String mWho;
        if (mTarget != null) {
            mWho = mTarget.mWho;
        }
        else {
            mWho = null;
        }
        mFragment.mTargetWho = mWho;
        final Fragment mFragment2 = this.mFragment;
        mFragment2.mTarget = null;
        final Bundle mSavedFragmentState = fragmentState.mSavedFragmentState;
        if (mSavedFragmentState != null) {
            mFragment2.mSavedFragmentState = mSavedFragmentState;
        }
        else {
            mFragment2.mSavedFragmentState = new Bundle();
        }
    }
    
    FragmentStateManager(final FragmentLifecycleCallbacksDispatcher mDispatcher, final FragmentStore mFragmentStore, final ClassLoader classLoader, final FragmentFactory fragmentFactory, final FragmentState fragmentState) {
        this.mMovingToState = false;
        this.mFragmentManagerState = -1;
        this.mDispatcher = mDispatcher;
        this.mFragmentStore = mFragmentStore;
        this.mFragment = fragmentFactory.instantiate(classLoader, fragmentState.mClassName);
        final Bundle mArguments = fragmentState.mArguments;
        if (mArguments != null) {
            mArguments.setClassLoader(classLoader);
        }
        this.mFragment.setArguments(fragmentState.mArguments);
        final Fragment mFragment = this.mFragment;
        mFragment.mWho = fragmentState.mWho;
        mFragment.mFromLayout = fragmentState.mFromLayout;
        mFragment.mRestored = true;
        mFragment.mFragmentId = fragmentState.mFragmentId;
        mFragment.mContainerId = fragmentState.mContainerId;
        mFragment.mTag = fragmentState.mTag;
        mFragment.mRetainInstance = fragmentState.mRetainInstance;
        mFragment.mRemoving = fragmentState.mRemoving;
        mFragment.mDetached = fragmentState.mDetached;
        mFragment.mHidden = fragmentState.mHidden;
        mFragment.mMaxState = Lifecycle.State.values()[fragmentState.mMaxLifecycleState];
        final Bundle mSavedFragmentState = fragmentState.mSavedFragmentState;
        if (mSavedFragmentState != null) {
            this.mFragment.mSavedFragmentState = mSavedFragmentState;
        }
        else {
            this.mFragment.mSavedFragmentState = new Bundle();
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Instantiated fragment ");
            sb.append(this.mFragment);
            Log.v("FragmentManager", sb.toString());
        }
    }
    
    private Bundle saveBasicState() {
        final Bundle bundle = new Bundle();
        this.mFragment.performSaveInstanceState(bundle);
        this.mDispatcher.dispatchOnFragmentSaveInstanceState(this.mFragment, bundle, false);
        Bundle bundle2 = bundle;
        if (bundle.isEmpty()) {
            bundle2 = null;
        }
        if (this.mFragment.mView != null) {
            this.saveViewState();
        }
        Bundle bundle3 = bundle2;
        if (this.mFragment.mSavedViewState != null) {
            if ((bundle3 = bundle2) == null) {
                bundle3 = new Bundle();
            }
            bundle3.putSparseParcelableArray("android:view_state", (SparseArray)this.mFragment.mSavedViewState);
        }
        Bundle bundle4 = bundle3;
        if (!this.mFragment.mUserVisibleHint) {
            if ((bundle4 = bundle3) == null) {
                bundle4 = new Bundle();
            }
            bundle4.putBoolean("android:user_visible_hint", this.mFragment.mUserVisibleHint);
        }
        return bundle4;
    }
    
    void activityCreated() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("moveto ACTIVITY_CREATED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        final Fragment mFragment = this.mFragment;
        mFragment.performActivityCreated(mFragment.mSavedFragmentState);
        final FragmentLifecycleCallbacksDispatcher mDispatcher = this.mDispatcher;
        final Fragment mFragment2 = this.mFragment;
        mDispatcher.dispatchOnFragmentActivityCreated(mFragment2, mFragment2.mSavedFragmentState, false);
    }
    
    void attach() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("moveto ATTACHED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        final Fragment mFragment = this.mFragment;
        final Fragment mTarget = mFragment.mTarget;
        FragmentStateManager fragmentStateManager = null;
        if (mTarget != null) {
            fragmentStateManager = this.mFragmentStore.getFragmentStateManager(mTarget.mWho);
            if (fragmentStateManager == null) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Fragment ");
                sb2.append(this.mFragment);
                sb2.append(" declared target fragment ");
                sb2.append(this.mFragment.mTarget);
                sb2.append(" that does not belong to this FragmentManager!");
                throw new IllegalStateException(sb2.toString());
            }
            final Fragment mFragment2 = this.mFragment;
            mFragment2.mTargetWho = mFragment2.mTarget.mWho;
            mFragment2.mTarget = null;
        }
        else {
            final String mTargetWho = mFragment.mTargetWho;
            if (mTargetWho != null) {
                fragmentStateManager = this.mFragmentStore.getFragmentStateManager(mTargetWho);
                if (fragmentStateManager == null) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Fragment ");
                    sb3.append(this.mFragment);
                    sb3.append(" declared target fragment ");
                    sb3.append(this.mFragment.mTargetWho);
                    sb3.append(" that does not belong to this FragmentManager!");
                    throw new IllegalStateException(sb3.toString());
                }
            }
        }
        if (fragmentStateManager != null && (FragmentManager.USE_STATE_MANAGER || fragmentStateManager.getFragment().mState < 1)) {
            fragmentStateManager.moveToExpectedState();
        }
        final Fragment mFragment3 = this.mFragment;
        mFragment3.mHost = mFragment3.mFragmentManager.getHost();
        final Fragment mFragment4 = this.mFragment;
        mFragment4.mParentFragment = mFragment4.mFragmentManager.getParent();
        this.mDispatcher.dispatchOnFragmentPreAttached(this.mFragment, false);
        this.mFragment.performAttach();
        this.mDispatcher.dispatchOnFragmentAttached(this.mFragment, false);
    }
    
    int computeExpectedState() {
        final Fragment mFragment = this.mFragment;
        if (mFragment.mFragmentManager == null) {
            return mFragment.mState;
        }
        int a2;
        final int a = a2 = this.mFragmentManagerState;
        if (mFragment.mFromLayout) {
            if (mFragment.mInLayout) {
                a2 = Math.max(a, 1);
            }
            else if (a < 3) {
                a2 = Math.min(a, mFragment.mState);
            }
            else {
                a2 = Math.min(a, 1);
            }
        }
        int min = a2;
        if (!this.mFragment.mAdded) {
            min = Math.min(a2, 1);
        }
        Enum<SpecialEffectsController.Operation.Type> awaitingCompletionType;
        final Enum<SpecialEffectsController.Operation.Type> enum1 = awaitingCompletionType = null;
        if (FragmentManager.USE_STATE_MANAGER) {
            final Fragment mFragment2 = this.mFragment;
            final ViewGroup mContainer = mFragment2.mContainer;
            awaitingCompletionType = enum1;
            if (mContainer != null) {
                awaitingCompletionType = SpecialEffectsController.getOrCreateController(mContainer, mFragment2.getParentFragmentManager()).getAwaitingCompletionType(this);
            }
        }
        int a3;
        if (awaitingCompletionType == SpecialEffectsController.Operation.Type.ADD) {
            a3 = Math.min(min, 5);
        }
        else if (awaitingCompletionType == SpecialEffectsController.Operation.Type.REMOVE) {
            a3 = Math.max(min, 2);
        }
        else {
            final Fragment mFragment3 = this.mFragment;
            a3 = min;
            if (mFragment3.mRemoving) {
                if (mFragment3.isInBackStack()) {
                    a3 = Math.min(min, 1);
                }
                else {
                    a3 = Math.min(min, -1);
                }
            }
        }
        final Fragment mFragment4 = this.mFragment;
        int min2 = a3;
        if (mFragment4.mDeferStart) {
            min2 = a3;
            if (mFragment4.mState < 4) {
                min2 = Math.min(a3, 3);
            }
        }
        final int n = FragmentStateManager$1.$SwitchMap$androidx$lifecycle$Lifecycle$State[this.mFragment.mMaxState.ordinal()];
        int n2 = min2;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    n2 = Math.min(min2, -1);
                }
                else {
                    n2 = Math.min(min2, 1);
                }
            }
            else {
                n2 = Math.min(min2, 4);
            }
        }
        return n2;
    }
    
    void create() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("moveto CREATED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        final Fragment mFragment = this.mFragment;
        if (!mFragment.mIsCreated) {
            this.mDispatcher.dispatchOnFragmentPreCreated(mFragment, mFragment.mSavedFragmentState, false);
            final Fragment mFragment2 = this.mFragment;
            mFragment2.performCreate(mFragment2.mSavedFragmentState);
            final FragmentLifecycleCallbacksDispatcher mDispatcher = this.mDispatcher;
            final Fragment mFragment3 = this.mFragment;
            mDispatcher.dispatchOnFragmentCreated(mFragment3, mFragment3.mSavedFragmentState, false);
        }
        else {
            mFragment.restoreChildFragmentState(mFragment.mSavedFragmentState);
            this.mFragment.mState = 1;
        }
    }
    
    void createView() {
        if (this.mFragment.mFromLayout) {
            return;
        }
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("moveto CREATE_VIEW: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        final ViewGroup viewGroup = null;
        final Fragment mFragment = this.mFragment;
        ViewGroup mContainer = mFragment.mContainer;
        if (mContainer == null) {
            final int mContainerId = mFragment.mContainerId;
            mContainer = viewGroup;
            if (mContainerId != 0) {
                if (mContainerId == -1) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Cannot create fragment ");
                    sb2.append(this.mFragment);
                    sb2.append(" for a container view with no id");
                    throw new IllegalArgumentException(sb2.toString());
                }
                final ViewGroup viewGroup2 = (ViewGroup)mFragment.mFragmentManager.getContainer().onFindViewById(this.mFragment.mContainerId);
                if ((mContainer = viewGroup2) == null) {
                    final Fragment mFragment2 = this.mFragment;
                    if (!mFragment2.mRestored) {
                        String resourceName;
                        try {
                            resourceName = mFragment2.getResources().getResourceName(this.mFragment.mContainerId);
                        }
                        catch (Resources$NotFoundException ex) {
                            resourceName = "unknown";
                        }
                        final StringBuilder sb3 = new StringBuilder();
                        sb3.append("No view found for id 0x");
                        sb3.append(Integer.toHexString(this.mFragment.mContainerId));
                        sb3.append(" (");
                        sb3.append(resourceName);
                        sb3.append(") for fragment ");
                        sb3.append(this.mFragment);
                        throw new IllegalArgumentException(sb3.toString());
                    }
                    mContainer = viewGroup2;
                }
            }
        }
        final Fragment mFragment3 = this.mFragment;
        mFragment3.mContainer = mContainer;
        mFragment3.performCreateView(mFragment3.performGetLayoutInflater(mFragment3.mSavedFragmentState), mContainer, this.mFragment.mSavedFragmentState);
        final View mView = this.mFragment.mView;
        if (mView != null) {
            final boolean b = false;
            mView.setSaveFromParentEnabled(false);
            final Fragment mFragment4 = this.mFragment;
            mFragment4.mView.setTag(R$id.fragment_container_view_tag, (Object)mFragment4);
            if (mContainer != null) {
                mContainer.addView(this.mFragment.mView);
                if (FragmentManager.USE_STATE_MANAGER) {
                    this.mFragment.mView.setVisibility(4);
                }
            }
            final Fragment mFragment5 = this.mFragment;
            if (mFragment5.mHidden) {
                mFragment5.mView.setVisibility(8);
            }
            ViewCompat.requestApplyInsets(this.mFragment.mView);
            final Fragment mFragment6 = this.mFragment;
            mFragment6.onViewCreated(mFragment6.mView, mFragment6.mSavedFragmentState);
            final FragmentLifecycleCallbacksDispatcher mDispatcher = this.mDispatcher;
            final Fragment mFragment7 = this.mFragment;
            mDispatcher.dispatchOnFragmentViewCreated(mFragment7, mFragment7.mView, mFragment7.mSavedFragmentState, false);
            final Fragment mFragment8 = this.mFragment;
            boolean mIsNewlyAdded = b;
            if (mFragment8.mView.getVisibility() == 0) {
                mIsNewlyAdded = b;
                if (this.mFragment.mContainer != null) {
                    mIsNewlyAdded = true;
                }
            }
            mFragment8.mIsNewlyAdded = mIsNewlyAdded;
        }
    }
    
    void destroy() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("movefrom CREATED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        final Fragment mFragment = this.mFragment;
        final boolean mRemoving = mFragment.mRemoving;
        boolean cleared = true;
        final boolean b = mRemoving && !mFragment.isInBackStack();
        if (b || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
            final FragmentHostCallback<?> mHost = this.mFragment.mHost;
            if (mHost instanceof ViewModelStoreOwner) {
                cleared = this.mFragmentStore.getNonConfig().isCleared();
            }
            else if (mHost.getContext() instanceof Activity) {
                cleared = (true ^ ((Activity)mHost.getContext()).isChangingConfigurations());
            }
            if (b || cleared) {
                this.mFragmentStore.getNonConfig().clearNonConfigState(this.mFragment);
            }
            this.mFragment.performDestroy();
            this.mDispatcher.dispatchOnFragmentDestroyed(this.mFragment, false);
            for (final FragmentStateManager fragmentStateManager : this.mFragmentStore.getActiveFragmentStateManagers()) {
                if (fragmentStateManager != null) {
                    final Fragment fragment = fragmentStateManager.getFragment();
                    if (!this.mFragment.mWho.equals(fragment.mTargetWho)) {
                        continue;
                    }
                    fragment.mTarget = this.mFragment;
                    fragment.mTargetWho = null;
                }
            }
            final Fragment mFragment2 = this.mFragment;
            final String mTargetWho = mFragment2.mTargetWho;
            if (mTargetWho != null) {
                mFragment2.mTarget = this.mFragmentStore.findActiveFragment(mTargetWho);
            }
            this.mFragmentStore.makeInactive(this);
        }
        else {
            final String mTargetWho2 = this.mFragment.mTargetWho;
            if (mTargetWho2 != null) {
                final Fragment activeFragment = this.mFragmentStore.findActiveFragment(mTargetWho2);
                if (activeFragment != null && activeFragment.mRetainInstance) {
                    this.mFragment.mTarget = activeFragment;
                }
            }
            this.mFragment.mState = 0;
        }
    }
    
    void destroyFragmentView() {
        this.mFragment.performDestroyView();
        this.mDispatcher.dispatchOnFragmentViewDestroyed(this.mFragment, false);
        final Fragment mFragment = this.mFragment;
        mFragment.mContainer = null;
        mFragment.mView = null;
        mFragment.mViewLifecycleOwner = null;
        mFragment.mViewLifecycleOwnerLiveData.setValue(null);
        this.mFragment.mInLayout = false;
    }
    
    void detach() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("movefrom ATTACHED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performDetach();
        final FragmentLifecycleCallbacksDispatcher mDispatcher = this.mDispatcher;
        final Fragment mFragment = this.mFragment;
        final int n = 0;
        mDispatcher.dispatchOnFragmentDetached(mFragment, false);
        final Fragment mFragment2 = this.mFragment;
        mFragment2.mState = -1;
        mFragment2.mHost = null;
        mFragment2.mParentFragment = null;
        mFragment2.mFragmentManager = null;
        int n2 = n;
        if (mFragment2.mRemoving) {
            n2 = n;
            if (!mFragment2.isInBackStack()) {
                n2 = 1;
            }
        }
        if (n2 != 0 || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
            if (FragmentManager.isLoggingEnabled(3)) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("initState called for fragment: ");
                sb2.append(this.mFragment);
                Log.d("FragmentManager", sb2.toString());
            }
            this.mFragment.initState();
        }
    }
    
    void ensureInflatedView() {
        final Fragment mFragment = this.mFragment;
        if (mFragment.mFromLayout && mFragment.mInLayout && !mFragment.mPerformedCreateView) {
            if (FragmentManager.isLoggingEnabled(3)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("moveto CREATE_VIEW: ");
                sb.append(this.mFragment);
                Log.d("FragmentManager", sb.toString());
            }
            final Fragment mFragment2 = this.mFragment;
            mFragment2.performCreateView(mFragment2.performGetLayoutInflater(mFragment2.mSavedFragmentState), null, this.mFragment.mSavedFragmentState);
            final View mView = this.mFragment.mView;
            if (mView != null) {
                mView.setSaveFromParentEnabled(false);
                final Fragment mFragment3 = this.mFragment;
                mFragment3.mView.setTag(R$id.fragment_container_view_tag, (Object)mFragment3);
                final Fragment mFragment4 = this.mFragment;
                if (mFragment4.mHidden) {
                    mFragment4.mView.setVisibility(8);
                }
                final Fragment mFragment5 = this.mFragment;
                mFragment5.onViewCreated(mFragment5.mView, mFragment5.mSavedFragmentState);
                final FragmentLifecycleCallbacksDispatcher mDispatcher = this.mDispatcher;
                final Fragment mFragment6 = this.mFragment;
                mDispatcher.dispatchOnFragmentViewCreated(mFragment6, mFragment6.mView, mFragment6.mSavedFragmentState, false);
            }
        }
    }
    
    Fragment getFragment() {
        return this.mFragment;
    }
    
    void moveToExpectedState() {
        if (this.mMovingToState) {
            if (FragmentManager.isLoggingEnabled(2)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Ignoring re-entrant call to moveToExpectedState() for ");
                sb.append(this.getFragment());
                Log.v("FragmentManager", sb.toString());
            }
            return;
        }
        try {
            this.mMovingToState = true;
            while (true) {
                final int computeExpectedState = this.computeExpectedState();
                if (computeExpectedState == this.mFragment.mState) {
                    break;
                }
                if (computeExpectedState > this.mFragment.mState) {
                    final int mState = this.mFragment.mState;
                    if (this.mExitAnimationCancellationSignal != null) {
                        this.mExitAnimationCancellationSignal.cancel();
                    }
                    switch (mState + 1) {
                        default: {
                            continue;
                        }
                        case 6: {
                            this.resume();
                            continue;
                        }
                        case 5: {
                            this.mFragment.mState = 5;
                            continue;
                        }
                        case 4: {
                            this.start();
                            continue;
                        }
                        case 3: {
                            if (this.mFragment.mView != null && this.mFragment.mContainer != null) {
                                SpecialEffectsController.getOrCreateController(this.mFragment.mContainer, this.mFragment.getParentFragmentManager()).enqueueAdd(this, this.mEnterAnimationCancellationSignal = new CancellationSignal());
                            }
                            this.mFragment.mState = 3;
                            continue;
                        }
                        case 2: {
                            this.ensureInflatedView();
                            this.createView();
                            this.activityCreated();
                            this.restoreViewState();
                            continue;
                        }
                        case 1: {
                            this.create();
                            continue;
                        }
                        case 0: {
                            this.attach();
                            continue;
                        }
                    }
                }
                else {
                    final int mState2 = this.mFragment.mState;
                    if (this.mEnterAnimationCancellationSignal != null) {
                        this.mEnterAnimationCancellationSignal.cancel();
                    }
                    switch (mState2 - 1) {
                        default: {
                            continue;
                        }
                        case 5: {
                            this.pause();
                            continue;
                        }
                        case 4: {
                            this.mFragment.mState = 4;
                            continue;
                        }
                        case 3: {
                            this.stop();
                            continue;
                        }
                        case 2: {
                            if (FragmentManager.isLoggingEnabled(3)) {
                                final StringBuilder sb2 = new StringBuilder();
                                sb2.append("movefrom ACTIVITY_CREATED: ");
                                sb2.append(this.mFragment);
                                Log.d("FragmentManager", sb2.toString());
                            }
                            if (this.mFragment.mView != null && this.mFragment.mContainer != null) {
                                SpecialEffectsController.getOrCreateController(this.mFragment.mContainer, this.mFragment.getParentFragmentManager()).enqueueRemove(this, this.mExitAnimationCancellationSignal = new CancellationSignal());
                            }
                            this.mFragment.mState = 2;
                            continue;
                        }
                        case 1: {
                            this.destroyFragmentView();
                            continue;
                        }
                        case 0: {
                            this.destroy();
                            continue;
                        }
                        case -1: {
                            this.detach();
                            continue;
                        }
                    }
                }
            }
        }
        finally {
            this.mMovingToState = false;
        }
    }
    
    void pause() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("movefrom RESUMED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performPause();
        this.mDispatcher.dispatchOnFragmentPaused(this.mFragment, false);
    }
    
    void restoreState(final ClassLoader classLoader) {
        final Bundle mSavedFragmentState = this.mFragment.mSavedFragmentState;
        if (mSavedFragmentState == null) {
            return;
        }
        mSavedFragmentState.setClassLoader(classLoader);
        final Fragment mFragment = this.mFragment;
        mFragment.mSavedViewState = (SparseArray<Parcelable>)mFragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
        final Fragment mFragment2 = this.mFragment;
        mFragment2.mTargetWho = mFragment2.mSavedFragmentState.getString("android:target_state");
        final Fragment mFragment3 = this.mFragment;
        if (mFragment3.mTargetWho != null) {
            mFragment3.mTargetRequestCode = mFragment3.mSavedFragmentState.getInt("android:target_req_state", 0);
        }
        final Fragment mFragment4 = this.mFragment;
        final Boolean mSavedUserVisibleHint = mFragment4.mSavedUserVisibleHint;
        if (mSavedUserVisibleHint != null) {
            mFragment4.mUserVisibleHint = mSavedUserVisibleHint;
            this.mFragment.mSavedUserVisibleHint = null;
        }
        else {
            mFragment4.mUserVisibleHint = mFragment4.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
        }
        final Fragment mFragment5 = this.mFragment;
        if (!mFragment5.mUserVisibleHint) {
            mFragment5.mDeferStart = true;
        }
    }
    
    void restoreViewState() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("moveto RESTORE_VIEW_STATE: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        final Fragment mFragment = this.mFragment;
        if (mFragment.mView != null) {
            mFragment.restoreViewState(mFragment.mSavedFragmentState);
        }
        this.mFragment.mSavedFragmentState = null;
    }
    
    void resume() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("moveto RESUMED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performResume();
        this.mDispatcher.dispatchOnFragmentResumed(this.mFragment, false);
        final Fragment mFragment = this.mFragment;
        mFragment.mSavedFragmentState = null;
        mFragment.mSavedViewState = null;
    }
    
    FragmentState saveState() {
        final FragmentState fragmentState = new FragmentState(this.mFragment);
        if (this.mFragment.mState > -1 && fragmentState.mSavedFragmentState == null) {
            final Bundle saveBasicState = this.saveBasicState();
            fragmentState.mSavedFragmentState = saveBasicState;
            if (this.mFragment.mTargetWho != null) {
                if (saveBasicState == null) {
                    fragmentState.mSavedFragmentState = new Bundle();
                }
                fragmentState.mSavedFragmentState.putString("android:target_state", this.mFragment.mTargetWho);
                final int mTargetRequestCode = this.mFragment.mTargetRequestCode;
                if (mTargetRequestCode != 0) {
                    fragmentState.mSavedFragmentState.putInt("android:target_req_state", mTargetRequestCode);
                }
            }
        }
        else {
            fragmentState.mSavedFragmentState = this.mFragment.mSavedFragmentState;
        }
        return fragmentState;
    }
    
    void saveViewState() {
        if (this.mFragment.mView == null) {
            return;
        }
        final SparseArray mSavedViewState = new SparseArray();
        this.mFragment.mView.saveHierarchyState(mSavedViewState);
        if (mSavedViewState.size() > 0) {
            this.mFragment.mSavedViewState = (SparseArray<Parcelable>)mSavedViewState;
        }
    }
    
    void setFragmentManagerState(final int mFragmentManagerState) {
        this.mFragmentManagerState = mFragmentManagerState;
    }
    
    void start() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("moveto STARTED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performStart();
        this.mDispatcher.dispatchOnFragmentStarted(this.mFragment, false);
    }
    
    void stop() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("movefrom STARTED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performStop();
        this.mDispatcher.dispatchOnFragmentStopped(this.mFragment, false);
    }
}
