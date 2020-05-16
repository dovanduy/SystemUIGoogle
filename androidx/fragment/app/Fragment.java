// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Looper;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.ContextMenu$ContextMenuInfo;
import android.view.ContextMenu;
import android.view.animation.Animation;
import android.view.MenuItem;
import android.content.res.Configuration;
import android.app.Activity;
import android.content.Intent;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.LiveData;
import androidx.savedstate.SavedStateRegistry;
import android.content.res.Resources;
import androidx.core.view.LayoutInflaterCompat;
import androidx.core.app.SharedElementCallback;
import android.animation.Animator;
import androidx.loader.app.LoaderManager;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.lang.reflect.InvocationTargetException;
import android.content.Context;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleEventObserver;
import android.os.Build$VERSION;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.UUID;
import androidx.lifecycle.MutableLiveData;
import android.view.View;
import android.os.Parcelable;
import android.util.SparseArray;
import androidx.savedstate.SavedStateRegistryController;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import androidx.savedstate.SavedStateRegistryOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.LifecycleOwner;
import android.view.View$OnCreateContextMenuListener;
import android.content.ComponentCallbacks;

public class Fragment implements ComponentCallbacks, View$OnCreateContextMenuListener, LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner
{
    static final Object USE_DEFAULT_TRANSITION;
    boolean mAdded;
    AnimationInfo mAnimationInfo;
    Bundle mArguments;
    int mBackStackNesting;
    private boolean mCalled;
    FragmentManager mChildFragmentManager;
    ViewGroup mContainer;
    int mContainerId;
    private int mContentLayoutId;
    boolean mDeferStart;
    boolean mDetached;
    int mFragmentId;
    FragmentManager mFragmentManager;
    boolean mFromLayout;
    boolean mHasMenu;
    boolean mHidden;
    boolean mHiddenChanged;
    FragmentHostCallback<?> mHost;
    boolean mInLayout;
    boolean mIsCreated;
    boolean mIsNewlyAdded;
    private Boolean mIsPrimaryNavigationFragment;
    LayoutInflater mLayoutInflater;
    LifecycleRegistry mLifecycleRegistry;
    Lifecycle.State mMaxState;
    boolean mMenuVisible;
    Fragment mParentFragment;
    boolean mPerformedCreateView;
    float mPostponedAlpha;
    boolean mRemoving;
    boolean mRestored;
    boolean mRetainInstance;
    boolean mRetainInstanceChangedWhileDetached;
    Bundle mSavedFragmentState;
    SavedStateRegistryController mSavedStateRegistryController;
    Boolean mSavedUserVisibleHint;
    SparseArray<Parcelable> mSavedViewState;
    int mState;
    String mTag;
    Fragment mTarget;
    int mTargetRequestCode;
    String mTargetWho;
    boolean mUserVisibleHint;
    View mView;
    FragmentViewLifecycleOwner mViewLifecycleOwner;
    MutableLiveData<LifecycleOwner> mViewLifecycleOwnerLiveData;
    String mWho;
    
    static {
        USE_DEFAULT_TRANSITION = new Object();
    }
    
    public Fragment() {
        this.mState = -1;
        this.mWho = UUID.randomUUID().toString();
        this.mTargetWho = null;
        this.mIsPrimaryNavigationFragment = null;
        this.mChildFragmentManager = new FragmentManagerImpl();
        this.mMenuVisible = true;
        this.mUserVisibleHint = true;
        this.mMaxState = Lifecycle.State.RESUMED;
        this.mViewLifecycleOwnerLiveData = new MutableLiveData<LifecycleOwner>();
        new AtomicInteger();
        this.initLifecycle();
    }
    
    private AnimationInfo ensureAnimationInfo() {
        if (this.mAnimationInfo == null) {
            this.mAnimationInfo = new AnimationInfo();
        }
        return this.mAnimationInfo;
    }
    
    private void initLifecycle() {
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mSavedStateRegistryController = SavedStateRegistryController.create(this);
        if (Build$VERSION.SDK_INT >= 19) {
            this.mLifecycleRegistry.addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_STOP) {
                        final View mView = Fragment.this.mView;
                        if (mView != null) {
                            mView.cancelPendingInputEvents();
                        }
                    }
                }
            });
        }
    }
    
    @Deprecated
    public static Fragment instantiate(final Context context, final String s, final Bundle arguments) {
        try {
            final Fragment fragment = (Fragment)FragmentFactory.loadFragmentClass(context.getClassLoader(), s).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            if (arguments != null) {
                arguments.setClassLoader(fragment.getClass().getClassLoader());
                fragment.setArguments(arguments);
            }
            return fragment;
        }
        catch (InvocationTargetException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unable to instantiate fragment ");
            sb.append(s);
            sb.append(": calling Fragment constructor caused an exception");
            throw new InstantiationException(sb.toString(), ex);
        }
        catch (NoSuchMethodException ex2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Unable to instantiate fragment ");
            sb2.append(s);
            sb2.append(": could not find Fragment constructor");
            throw new InstantiationException(sb2.toString(), ex2);
        }
        catch (IllegalAccessException ex3) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Unable to instantiate fragment ");
            sb3.append(s);
            sb3.append(": make sure class name exists, is public, and has an empty constructor that is public");
            throw new InstantiationException(sb3.toString(), ex3);
        }
        catch (java.lang.InstantiationException ex4) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("Unable to instantiate fragment ");
            sb4.append(s);
            sb4.append(": make sure class name exists, is public, and has an empty constructor that is public");
            throw new InstantiationException(sb4.toString(), ex4);
        }
    }
    
    void callStartTransitionListener() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        Object mStartEnterTransitionListener = null;
        if (mAnimationInfo != null) {
            mAnimationInfo.mEnterTransitionPostponed = false;
            mStartEnterTransitionListener = mAnimationInfo.mStartEnterTransitionListener;
            mAnimationInfo.mStartEnterTransitionListener = null;
        }
        if (mStartEnterTransitionListener != null) {
            ((OnStartEnterTransitionListener)mStartEnterTransitionListener).onStartEnterTransition();
        }
    }
    
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print(s);
        printWriter.print("mFragmentId=#");
        printWriter.print(Integer.toHexString(this.mFragmentId));
        printWriter.print(" mContainerId=#");
        printWriter.print(Integer.toHexString(this.mContainerId));
        printWriter.print(" mTag=");
        printWriter.println(this.mTag);
        printWriter.print(s);
        printWriter.print("mState=");
        printWriter.print(this.mState);
        printWriter.print(" mWho=");
        printWriter.print(this.mWho);
        printWriter.print(" mBackStackNesting=");
        printWriter.println(this.mBackStackNesting);
        printWriter.print(s);
        printWriter.print("mAdded=");
        printWriter.print(this.mAdded);
        printWriter.print(" mRemoving=");
        printWriter.print(this.mRemoving);
        printWriter.print(" mFromLayout=");
        printWriter.print(this.mFromLayout);
        printWriter.print(" mInLayout=");
        printWriter.println(this.mInLayout);
        printWriter.print(s);
        printWriter.print("mHidden=");
        printWriter.print(this.mHidden);
        printWriter.print(" mDetached=");
        printWriter.print(this.mDetached);
        printWriter.print(" mMenuVisible=");
        printWriter.print(this.mMenuVisible);
        printWriter.print(" mHasMenu=");
        printWriter.println(this.mHasMenu);
        printWriter.print(s);
        printWriter.print("mRetainInstance=");
        printWriter.print(this.mRetainInstance);
        printWriter.print(" mUserVisibleHint=");
        printWriter.println(this.mUserVisibleHint);
        if (this.mFragmentManager != null) {
            printWriter.print(s);
            printWriter.print("mFragmentManager=");
            printWriter.println(this.mFragmentManager);
        }
        if (this.mHost != null) {
            printWriter.print(s);
            printWriter.print("mHost=");
            printWriter.println(this.mHost);
        }
        if (this.mParentFragment != null) {
            printWriter.print(s);
            printWriter.print("mParentFragment=");
            printWriter.println(this.mParentFragment);
        }
        if (this.mArguments != null) {
            printWriter.print(s);
            printWriter.print("mArguments=");
            printWriter.println(this.mArguments);
        }
        if (this.mSavedFragmentState != null) {
            printWriter.print(s);
            printWriter.print("mSavedFragmentState=");
            printWriter.println(this.mSavedFragmentState);
        }
        if (this.mSavedViewState != null) {
            printWriter.print(s);
            printWriter.print("mSavedViewState=");
            printWriter.println(this.mSavedViewState);
        }
        final Fragment targetFragment = this.getTargetFragment();
        if (targetFragment != null) {
            printWriter.print(s);
            printWriter.print("mTarget=");
            printWriter.print(targetFragment);
            printWriter.print(" mTargetRequestCode=");
            printWriter.println(this.mTargetRequestCode);
        }
        if (this.getNextAnim() != 0) {
            printWriter.print(s);
            printWriter.print("mNextAnim=");
            printWriter.println(this.getNextAnim());
        }
        if (this.mContainer != null) {
            printWriter.print(s);
            printWriter.print("mContainer=");
            printWriter.println(this.mContainer);
        }
        if (this.mView != null) {
            printWriter.print(s);
            printWriter.print("mView=");
            printWriter.println(this.mView);
        }
        if (this.getAnimatingAway() != null) {
            printWriter.print(s);
            printWriter.print("mAnimatingAway=");
            printWriter.println(this.getAnimatingAway());
        }
        if (this.getContext() != null) {
            LoaderManager.getInstance(this).dump(s, fileDescriptor, printWriter, array);
        }
        printWriter.print(s);
        final StringBuilder sb = new StringBuilder();
        sb.append("Child ");
        sb.append(this.mChildFragmentManager);
        sb.append(":");
        printWriter.println(sb.toString());
        final FragmentManager mChildFragmentManager = this.mChildFragmentManager;
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("  ");
        mChildFragmentManager.dump(sb2.toString(), fileDescriptor, printWriter, array);
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    Fragment findFragmentByWho(final String s) {
        if (s.equals(this.mWho)) {
            return this;
        }
        return this.mChildFragmentManager.findFragmentByWho(s);
    }
    
    public final FragmentActivity getActivity() {
        final FragmentHostCallback<?> mHost = this.mHost;
        FragmentActivity fragmentActivity;
        if (mHost == null) {
            fragmentActivity = null;
        }
        else {
            fragmentActivity = (FragmentActivity)mHost.getActivity();
        }
        return fragmentActivity;
    }
    
    public boolean getAllowEnterTransitionOverlap() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo != null) {
            final Boolean mAllowEnterTransitionOverlap = mAnimationInfo.mAllowEnterTransitionOverlap;
            if (mAllowEnterTransitionOverlap != null) {
                return mAllowEnterTransitionOverlap;
            }
        }
        return true;
    }
    
    public boolean getAllowReturnTransitionOverlap() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo != null) {
            final Boolean mAllowReturnTransitionOverlap = mAnimationInfo.mAllowReturnTransitionOverlap;
            if (mAllowReturnTransitionOverlap != null) {
                return mAllowReturnTransitionOverlap;
            }
        }
        return true;
    }
    
    View getAnimatingAway() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        return mAnimationInfo.mAnimatingAway;
    }
    
    Animator getAnimator() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        return mAnimationInfo.mAnimator;
    }
    
    public final Bundle getArguments() {
        return this.mArguments;
    }
    
    public final FragmentManager getChildFragmentManager() {
        if (this.mHost != null) {
            return this.mChildFragmentManager;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" has not been attached yet.");
        throw new IllegalStateException(sb.toString());
    }
    
    public Context getContext() {
        final FragmentHostCallback<?> mHost = this.mHost;
        Context context;
        if (mHost == null) {
            context = null;
        }
        else {
            context = mHost.getContext();
        }
        return context;
    }
    
    public Object getEnterTransition() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        return mAnimationInfo.mEnterTransition;
    }
    
    SharedElementCallback getEnterTransitionCallback() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        return mAnimationInfo.mEnterTransitionCallback;
    }
    
    public Object getExitTransition() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        return mAnimationInfo.mExitTransition;
    }
    
    SharedElementCallback getExitTransitionCallback() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        return mAnimationInfo.mExitTransitionCallback;
    }
    
    public final Object getHost() {
        final FragmentHostCallback<?> mHost = this.mHost;
        Object onGetHost;
        if (mHost == null) {
            onGetHost = null;
        }
        else {
            onGetHost = mHost.onGetHost();
        }
        return onGetHost;
    }
    
    @Deprecated
    public LayoutInflater getLayoutInflater(final Bundle bundle) {
        final FragmentHostCallback<?> mHost = this.mHost;
        if (mHost != null) {
            final LayoutInflater onGetLayoutInflater = mHost.onGetLayoutInflater();
            LayoutInflaterCompat.setFactory2(onGetLayoutInflater, this.mChildFragmentManager.getLayoutInflaterFactory());
            return onGetLayoutInflater;
        }
        throw new IllegalStateException("onGetLayoutInflater() cannot be executed until the Fragment is attached to the FragmentManager.");
    }
    
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }
    
    int getNextAnim() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return 0;
        }
        return mAnimationInfo.mNextAnim;
    }
    
    int getNextTransition() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return 0;
        }
        return mAnimationInfo.mNextTransition;
    }
    
    public final Fragment getParentFragment() {
        return this.mParentFragment;
    }
    
    public final FragmentManager getParentFragmentManager() {
        final FragmentManager mFragmentManager = this.mFragmentManager;
        if (mFragmentManager != null) {
            return mFragmentManager;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" not associated with a fragment manager.");
        throw new IllegalStateException(sb.toString());
    }
    
    public Object getReenterTransition() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        Object o;
        if ((o = mAnimationInfo.mReenterTransition) == Fragment.USE_DEFAULT_TRANSITION) {
            o = this.getExitTransition();
        }
        return o;
    }
    
    public final Resources getResources() {
        return this.requireContext().getResources();
    }
    
    public Object getReturnTransition() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        Object o;
        if ((o = mAnimationInfo.mReturnTransition) == Fragment.USE_DEFAULT_TRANSITION) {
            o = this.getEnterTransition();
        }
        return o;
    }
    
    public final SavedStateRegistry getSavedStateRegistry() {
        return this.mSavedStateRegistryController.getSavedStateRegistry();
    }
    
    public Object getSharedElementEnterTransition() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        return mAnimationInfo.mSharedElementEnterTransition;
    }
    
    public Object getSharedElementReturnTransition() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo == null) {
            return null;
        }
        Object o;
        if ((o = mAnimationInfo.mSharedElementReturnTransition) == Fragment.USE_DEFAULT_TRANSITION) {
            o = this.getSharedElementEnterTransition();
        }
        return o;
    }
    
    public final Fragment getTargetFragment() {
        final Fragment mTarget = this.mTarget;
        if (mTarget != null) {
            return mTarget;
        }
        final FragmentManager mFragmentManager = this.mFragmentManager;
        if (mFragmentManager != null) {
            final String mTargetWho = this.mTargetWho;
            if (mTargetWho != null) {
                return mFragmentManager.findActiveFragment(mTargetWho);
            }
        }
        return null;
    }
    
    public View getView() {
        return this.mView;
    }
    
    public LiveData<LifecycleOwner> getViewLifecycleOwnerLiveData() {
        return this.mViewLifecycleOwnerLiveData;
    }
    
    public ViewModelStore getViewModelStore() {
        final FragmentManager mFragmentManager = this.mFragmentManager;
        if (mFragmentManager != null) {
            return mFragmentManager.getViewModelStore(this);
        }
        throw new IllegalStateException("Can't access ViewModels from detached fragment");
    }
    
    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    void initState() {
        this.initLifecycle();
        this.mWho = UUID.randomUUID().toString();
        this.mAdded = false;
        this.mRemoving = false;
        this.mFromLayout = false;
        this.mInLayout = false;
        this.mRestored = false;
        this.mBackStackNesting = 0;
        this.mFragmentManager = null;
        this.mChildFragmentManager = new FragmentManagerImpl();
        this.mHost = null;
        this.mFragmentId = 0;
        this.mContainerId = 0;
        this.mTag = null;
        this.mHidden = false;
        this.mDetached = false;
    }
    
    public final boolean isDetached() {
        return this.mDetached;
    }
    
    boolean isHideReplaced() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        return mAnimationInfo != null && mAnimationInfo.mIsHideReplaced;
    }
    
    final boolean isInBackStack() {
        return this.mBackStackNesting > 0;
    }
    
    boolean isPostponed() {
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        return mAnimationInfo != null && mAnimationInfo.mEnterTransitionPostponed;
    }
    
    public final boolean isRemoving() {
        return this.mRemoving;
    }
    
    final boolean isRemovingParent() {
        final Fragment parentFragment = this.getParentFragment();
        return parentFragment != null && (parentFragment.isRemoving() || parentFragment.isRemovingParent());
    }
    
    public final boolean isStateSaved() {
        final FragmentManager mFragmentManager = this.mFragmentManager;
        return mFragmentManager != null && mFragmentManager.isStateSaved();
    }
    
    void noteStateNotSaved() {
        this.mChildFragmentManager.noteStateNotSaved();
    }
    
    @Deprecated
    public void onActivityCreated(final Bundle bundle) {
        this.mCalled = true;
    }
    
    public void onActivityResult(final int n, final int n2, final Intent intent) {
    }
    
    @Deprecated
    public void onAttach(final Activity activity) {
        this.mCalled = true;
    }
    
    public void onAttach(final Context context) {
        this.mCalled = true;
        final FragmentHostCallback<?> mHost = this.mHost;
        Activity activity;
        if (mHost == null) {
            activity = null;
        }
        else {
            activity = mHost.getActivity();
        }
        if (activity != null) {
            this.mCalled = false;
            this.onAttach(activity);
        }
    }
    
    public void onAttachFragment(final Fragment fragment) {
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        this.mCalled = true;
    }
    
    public boolean onContextItemSelected(final MenuItem menuItem) {
        return false;
    }
    
    public void onCreate(final Bundle bundle) {
        this.mCalled = true;
        this.restoreChildFragmentState(bundle);
        if (!this.mChildFragmentManager.isStateAtLeast(1)) {
            this.mChildFragmentManager.dispatchCreate();
        }
    }
    
    public Animation onCreateAnimation(final int n, final boolean b, final int n2) {
        return null;
    }
    
    public Animator onCreateAnimator(final int n, final boolean b, final int n2) {
        return null;
    }
    
    public void onCreateContextMenu(final ContextMenu contextMenu, final View view, final ContextMenu$ContextMenuInfo contextMenu$ContextMenuInfo) {
        this.requireActivity().onCreateContextMenu(contextMenu, view, contextMenu$ContextMenuInfo);
    }
    
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        final int mContentLayoutId = this.mContentLayoutId;
        if (mContentLayoutId != 0) {
            return layoutInflater.inflate(mContentLayoutId, viewGroup, false);
        }
        return null;
    }
    
    public void onDestroy() {
        this.mCalled = true;
    }
    
    public void onDestroyOptionsMenu() {
    }
    
    public void onDestroyView() {
        this.mCalled = true;
    }
    
    public void onDetach() {
        this.mCalled = true;
    }
    
    public LayoutInflater onGetLayoutInflater(final Bundle bundle) {
        return this.getLayoutInflater(bundle);
    }
    
    public void onHiddenChanged(final boolean b) {
    }
    
    @Deprecated
    public void onInflate(final Activity activity, final AttributeSet set, final Bundle bundle) {
        this.mCalled = true;
    }
    
    public void onInflate(final Context context, final AttributeSet set, final Bundle bundle) {
        this.mCalled = true;
        final FragmentHostCallback<?> mHost = this.mHost;
        Activity activity;
        if (mHost == null) {
            activity = null;
        }
        else {
            activity = mHost.getActivity();
        }
        if (activity != null) {
            this.mCalled = false;
            this.onInflate(activity, set, bundle);
        }
    }
    
    public void onLowMemory() {
        this.mCalled = true;
    }
    
    public void onMultiWindowModeChanged(final boolean b) {
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        return false;
    }
    
    public void onOptionsMenuClosed(final Menu menu) {
    }
    
    public void onPause() {
        this.mCalled = true;
    }
    
    public void onPictureInPictureModeChanged(final boolean b) {
    }
    
    public void onPrepareOptionsMenu(final Menu menu) {
    }
    
    public void onPrimaryNavigationFragmentChanged(final boolean b) {
    }
    
    public void onRequestPermissionsResult(final int n, final String[] array, final int[] array2) {
    }
    
    public void onResume() {
        this.mCalled = true;
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
    }
    
    public void onStart() {
        this.mCalled = true;
    }
    
    public void onStop() {
        this.mCalled = true;
    }
    
    public void onViewCreated(final View view, final Bundle bundle) {
    }
    
    public void onViewStateRestored(final Bundle bundle) {
        this.mCalled = true;
    }
    
    void performActivityCreated(final Bundle bundle) {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mState = 2;
        this.mCalled = false;
        this.onActivityCreated(bundle);
        if (this.mCalled) {
            this.mChildFragmentManager.dispatchActivityCreated();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onActivityCreated()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void performAttach() {
        this.mChildFragmentManager.attachController(this.mHost, new FragmentContainer() {
            @Override
            public View onFindViewById(final int n) {
                final View mView = Fragment.this.mView;
                if (mView != null) {
                    return mView.findViewById(n);
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Fragment ");
                sb.append(this);
                sb.append(" does not have a view");
                throw new IllegalStateException(sb.toString());
            }
            
            @Override
            public boolean onHasView() {
                return Fragment.this.mView != null;
            }
        }, this);
        this.mState = 0;
        this.mCalled = false;
        this.onAttach(this.mHost.getContext());
        if (this.mCalled) {
            final Fragment mParentFragment = this.mParentFragment;
            if (mParentFragment == null) {
                this.mHost.onAttachFragment(this);
            }
            else {
                mParentFragment.onAttachFragment(this);
            }
            this.mChildFragmentManager.dispatchAttach();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onAttach()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void performConfigurationChanged(final Configuration configuration) {
        this.onConfigurationChanged(configuration);
        this.mChildFragmentManager.dispatchConfigurationChanged(configuration);
    }
    
    boolean performContextItemSelected(final MenuItem menuItem) {
        if (!this.mHidden) {
            if (this.onContextItemSelected(menuItem)) {
                return true;
            }
            if (this.mChildFragmentManager.dispatchContextItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }
    
    void performCreate(final Bundle bundle) {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mState = 1;
        this.mCalled = false;
        this.mSavedStateRegistryController.performRestore(bundle);
        this.onCreate(bundle);
        this.mIsCreated = true;
        if (this.mCalled) {
            this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onCreate()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    boolean performCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        final boolean mHidden = this.mHidden;
        boolean b = false;
        final boolean b2 = false;
        if (!mHidden) {
            boolean b3 = b2;
            if (this.mHasMenu) {
                b3 = b2;
                if (this.mMenuVisible) {
                    b3 = true;
                    this.onCreateOptionsMenu(menu, menuInflater);
                }
            }
            b = (b3 | this.mChildFragmentManager.dispatchCreateOptionsMenu(menu, menuInflater));
        }
        return b;
    }
    
    void performCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mPerformedCreateView = true;
        this.mViewLifecycleOwner = new FragmentViewLifecycleOwner();
        final View onCreateView = this.onCreateView(layoutInflater, viewGroup, bundle);
        this.mView = onCreateView;
        if (onCreateView != null) {
            this.mViewLifecycleOwner.initialize();
            ViewTreeLifecycleOwner.set(this.mView, this.mViewLifecycleOwner);
            this.mViewLifecycleOwnerLiveData.setValue(this.mViewLifecycleOwner);
        }
        else {
            if (this.mViewLifecycleOwner.isInitialized()) {
                throw new IllegalStateException("Called getViewLifecycleOwner() but onCreateView() returned null");
            }
            this.mViewLifecycleOwner = null;
        }
    }
    
    void performDestroy() {
        this.mChildFragmentManager.dispatchDestroy();
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        this.mState = 0;
        this.mCalled = false;
        this.mIsCreated = false;
        this.onDestroy();
        if (this.mCalled) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onDestroy()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void performDestroyView() {
        this.mChildFragmentManager.dispatchDestroyView();
        if (this.mView != null) {
            this.mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        }
        this.mState = 1;
        this.mCalled = false;
        this.onDestroyView();
        if (this.mCalled) {
            LoaderManager.getInstance(this).markForRedelivery();
            this.mPerformedCreateView = false;
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onDestroyView()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void performDetach() {
        this.mState = -1;
        this.mCalled = false;
        this.onDetach();
        this.mLayoutInflater = null;
        if (this.mCalled) {
            if (!this.mChildFragmentManager.isDestroyed()) {
                this.mChildFragmentManager.dispatchDestroy();
                this.mChildFragmentManager = new FragmentManagerImpl();
            }
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onDetach()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    LayoutInflater performGetLayoutInflater(final Bundle bundle) {
        return this.mLayoutInflater = this.onGetLayoutInflater(bundle);
    }
    
    void performLowMemory() {
        this.onLowMemory();
        this.mChildFragmentManager.dispatchLowMemory();
    }
    
    void performMultiWindowModeChanged(final boolean b) {
        this.onMultiWindowModeChanged(b);
        this.mChildFragmentManager.dispatchMultiWindowModeChanged(b);
    }
    
    boolean performOptionsItemSelected(final MenuItem menuItem) {
        if (!this.mHidden) {
            if (this.mHasMenu && this.mMenuVisible && this.onOptionsItemSelected(menuItem)) {
                return true;
            }
            if (this.mChildFragmentManager.dispatchOptionsItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }
    
    void performOptionsMenuClosed(final Menu menu) {
        if (!this.mHidden) {
            if (this.mHasMenu && this.mMenuVisible) {
                this.onOptionsMenuClosed(menu);
            }
            this.mChildFragmentManager.dispatchOptionsMenuClosed(menu);
        }
    }
    
    void performPause() {
        this.mChildFragmentManager.dispatchPause();
        if (this.mView != null) {
            this.mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        }
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        this.mState = 5;
        this.mCalled = false;
        this.onPause();
        if (this.mCalled) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onPause()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void performPictureInPictureModeChanged(final boolean b) {
        this.onPictureInPictureModeChanged(b);
        this.mChildFragmentManager.dispatchPictureInPictureModeChanged(b);
    }
    
    boolean performPrepareOptionsMenu(final Menu menu) {
        final boolean mHidden = this.mHidden;
        boolean b = false;
        final boolean b2 = false;
        if (!mHidden) {
            boolean b3 = b2;
            if (this.mHasMenu) {
                b3 = b2;
                if (this.mMenuVisible) {
                    b3 = true;
                    this.onPrepareOptionsMenu(menu);
                }
            }
            b = (b3 | this.mChildFragmentManager.dispatchPrepareOptionsMenu(menu));
        }
        return b;
    }
    
    void performPrimaryNavigationFragmentChanged() {
        final boolean primaryNavigation = this.mFragmentManager.isPrimaryNavigation(this);
        final Boolean mIsPrimaryNavigationFragment = this.mIsPrimaryNavigationFragment;
        if (mIsPrimaryNavigationFragment == null || mIsPrimaryNavigationFragment != primaryNavigation) {
            this.mIsPrimaryNavigationFragment = primaryNavigation;
            this.onPrimaryNavigationFragmentChanged(primaryNavigation);
            this.mChildFragmentManager.dispatchPrimaryNavigationFragmentChanged();
        }
    }
    
    void performResume() {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mChildFragmentManager.execPendingActions(true);
        this.mState = 6;
        this.mCalled = false;
        this.onResume();
        if (this.mCalled) {
            this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
            if (this.mView != null) {
                this.mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
            }
            this.mChildFragmentManager.dispatchResume();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onResume()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void performSaveInstanceState(final Bundle bundle) {
        this.onSaveInstanceState(bundle);
        this.mSavedStateRegistryController.performSave(bundle);
        final Parcelable saveAllState = this.mChildFragmentManager.saveAllState();
        if (saveAllState != null) {
            bundle.putParcelable("android:support:fragments", saveAllState);
        }
    }
    
    void performStart() {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mChildFragmentManager.execPendingActions(true);
        this.mState = 4;
        this.mCalled = false;
        this.onStart();
        if (this.mCalled) {
            this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            if (this.mView != null) {
                this.mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START);
            }
            this.mChildFragmentManager.dispatchStart();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onStart()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void performStop() {
        this.mChildFragmentManager.dispatchStop();
        if (this.mView != null) {
            this.mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        this.mState = 3;
        this.mCalled = false;
        this.onStop();
        if (this.mCalled) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onStop()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    public final FragmentActivity requireActivity() {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            return activity;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" not attached to an activity.");
        throw new IllegalStateException(sb.toString());
    }
    
    public final Context requireContext() {
        final Context context = this.getContext();
        if (context != null) {
            return context;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" not attached to a context.");
        throw new IllegalStateException(sb.toString());
    }
    
    public final View requireView() {
        final View view = this.getView();
        if (view != null) {
            return view;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not return a View from onCreateView() or this was called before onCreateView().");
        throw new IllegalStateException(sb.toString());
    }
    
    void restoreChildFragmentState(final Bundle bundle) {
        if (bundle != null) {
            final Parcelable parcelable = bundle.getParcelable("android:support:fragments");
            if (parcelable != null) {
                this.mChildFragmentManager.restoreSaveState(parcelable);
                this.mChildFragmentManager.dispatchCreate();
            }
        }
    }
    
    final void restoreViewState(final Bundle bundle) {
        final SparseArray<Parcelable> mSavedViewState = this.mSavedViewState;
        if (mSavedViewState != null) {
            this.mView.restoreHierarchyState((SparseArray)mSavedViewState);
            this.mSavedViewState = null;
        }
        this.mCalled = false;
        this.onViewStateRestored(bundle);
        if (this.mCalled) {
            if (this.mView != null) {
                this.mViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
            }
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(this);
        sb.append(" did not call through to super.onViewStateRestored()");
        throw new SuperNotCalledException(sb.toString());
    }
    
    void setAnimatingAway(final View mAnimatingAway) {
        this.ensureAnimationInfo().mAnimatingAway = mAnimatingAway;
    }
    
    void setAnimator(final Animator mAnimator) {
        this.ensureAnimationInfo().mAnimator = mAnimator;
    }
    
    public void setArguments(final Bundle mArguments) {
        if (this.mFragmentManager != null && this.isStateSaved()) {
            throw new IllegalStateException("Fragment already added and state has been saved");
        }
        this.mArguments = mArguments;
    }
    
    void setHideReplaced(final boolean mIsHideReplaced) {
        this.ensureAnimationInfo().mIsHideReplaced = mIsHideReplaced;
    }
    
    void setNextAnim(final int mNextAnim) {
        if (this.mAnimationInfo == null && mNextAnim == 0) {
            return;
        }
        this.ensureAnimationInfo().mNextAnim = mNextAnim;
    }
    
    void setNextTransition(final int mNextTransition) {
        if (this.mAnimationInfo == null && mNextTransition == 0) {
            return;
        }
        this.ensureAnimationInfo();
        this.mAnimationInfo.mNextTransition = mNextTransition;
    }
    
    void setOnStartEnterTransitionListener(final OnStartEnterTransitionListener mStartEnterTransitionListener) {
        this.ensureAnimationInfo();
        final OnStartEnterTransitionListener mStartEnterTransitionListener2 = this.mAnimationInfo.mStartEnterTransitionListener;
        if (mStartEnterTransitionListener == mStartEnterTransitionListener2) {
            return;
        }
        if (mStartEnterTransitionListener != null && mStartEnterTransitionListener2 != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Trying to set a replacement startPostponedEnterTransition on ");
            sb.append(this);
            throw new IllegalStateException(sb.toString());
        }
        final AnimationInfo mAnimationInfo = this.mAnimationInfo;
        if (mAnimationInfo.mEnterTransitionPostponed) {
            mAnimationInfo.mStartEnterTransitionListener = mStartEnterTransitionListener;
        }
        if (mStartEnterTransitionListener != null) {
            mStartEnterTransitionListener.startListening();
        }
    }
    
    public void startPostponedEnterTransition() {
        if (this.mAnimationInfo != null) {
            if (this.ensureAnimationInfo().mEnterTransitionPostponed) {
                if (this.mHost == null) {
                    this.ensureAnimationInfo().mEnterTransitionPostponed = false;
                }
                else if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
                    this.mHost.getHandler().postAtFrontOfQueue((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            Fragment.this.callStartTransitionListener();
                        }
                    });
                }
                else {
                    this.callStartTransitionListener();
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append(this.getClass().getSimpleName());
        sb.append("{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("}");
        sb.append(" (");
        sb.append(this.mWho);
        sb.append(")");
        if (this.mFragmentId != 0) {
            sb.append(" id=0x");
            sb.append(Integer.toHexString(this.mFragmentId));
        }
        if (this.mTag != null) {
            sb.append(" ");
            sb.append(this.mTag);
        }
        sb.append('}');
        return sb.toString();
    }
    
    static class AnimationInfo
    {
        Boolean mAllowEnterTransitionOverlap;
        Boolean mAllowReturnTransitionOverlap;
        View mAnimatingAway;
        Animator mAnimator;
        Object mEnterTransition;
        SharedElementCallback mEnterTransitionCallback;
        boolean mEnterTransitionPostponed;
        Object mExitTransition;
        SharedElementCallback mExitTransitionCallback;
        boolean mIsHideReplaced;
        int mNextAnim;
        int mNextTransition;
        Object mReenterTransition;
        Object mReturnTransition;
        Object mSharedElementEnterTransition;
        Object mSharedElementReturnTransition;
        OnStartEnterTransitionListener mStartEnterTransitionListener;
        
        AnimationInfo() {
            this.mEnterTransition = null;
            final Object use_DEFAULT_TRANSITION = Fragment.USE_DEFAULT_TRANSITION;
            this.mReturnTransition = use_DEFAULT_TRANSITION;
            this.mExitTransition = null;
            this.mReenterTransition = use_DEFAULT_TRANSITION;
            this.mSharedElementEnterTransition = null;
            this.mSharedElementReturnTransition = use_DEFAULT_TRANSITION;
            this.mEnterTransitionCallback = null;
            this.mExitTransitionCallback = null;
        }
    }
    
    public static class InstantiationException extends RuntimeException
    {
        public InstantiationException(final String message, final Exception cause) {
            super(message, cause);
        }
    }
    
    interface OnStartEnterTransitionListener
    {
        void onStartEnterTransition();
        
        void startListening();
    }
    
    @SuppressLint({ "BanParcelableUsage" })
    public static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        final Bundle mState;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel, final ClassLoader classLoader) {
            final Bundle bundle = parcel.readBundle();
            this.mState = bundle;
            if (classLoader != null && bundle != null) {
                bundle.setClassLoader(classLoader);
            }
        }
        
        public int describeContents() {
            return 0;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeBundle(this.mState);
        }
    }
}
