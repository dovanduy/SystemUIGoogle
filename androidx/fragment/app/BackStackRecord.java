// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import java.util.ArrayList;
import java.io.Writer;
import java.io.PrintWriter;
import android.util.Log;

final class BackStackRecord extends FragmentTransaction implements OpGenerator
{
    boolean mCommitted;
    int mIndex;
    final FragmentManager mManager;
    
    BackStackRecord(final FragmentManager mManager) {
        final FragmentFactory fragmentFactory = mManager.getFragmentFactory();
        ClassLoader classLoader;
        if (mManager.getHost() != null) {
            classLoader = mManager.getHost().getContext().getClassLoader();
        }
        else {
            classLoader = null;
        }
        super(fragmentFactory, classLoader);
        this.mIndex = -1;
        this.mManager = mManager;
    }
    
    private static boolean isFragmentPostponed(final Op op) {
        final Fragment mFragment = op.mFragment;
        return mFragment != null && mFragment.mAdded && mFragment.mView != null && !mFragment.mDetached && !mFragment.mHidden && mFragment.isPostponed();
    }
    
    void bumpBackStackNesting(final int i) {
        if (!super.mAddToBackStack) {
            return;
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Bump nesting in ");
            sb.append(this);
            sb.append(" by ");
            sb.append(i);
            Log.v("FragmentManager", sb.toString());
        }
        for (int size = super.mOps.size(), j = 0; j < size; ++j) {
            final Op op = super.mOps.get(j);
            final Fragment mFragment = op.mFragment;
            if (mFragment != null) {
                mFragment.mBackStackNesting += i;
                if (FragmentManager.isLoggingEnabled(2)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Bump nesting of ");
                    sb2.append(op.mFragment);
                    sb2.append(" to ");
                    sb2.append(op.mFragment.mBackStackNesting);
                    Log.v("FragmentManager", sb2.toString());
                }
            }
        }
    }
    
    @Override
    public int commit() {
        return this.commitInternal(false);
    }
    
    @Override
    public int commitAllowingStateLoss() {
        return this.commitInternal(true);
    }
    
    int commitInternal(final boolean b) {
        if (!this.mCommitted) {
            if (FragmentManager.isLoggingEnabled(2)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Commit: ");
                sb.append(this);
                Log.v("FragmentManager", sb.toString());
                final PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
                this.dump("  ", printWriter);
                printWriter.close();
            }
            this.mCommitted = true;
            if (super.mAddToBackStack) {
                this.mIndex = this.mManager.allocBackStackIndex();
            }
            else {
                this.mIndex = -1;
            }
            this.mManager.enqueueAction((FragmentManager.OpGenerator)this, b);
            return this.mIndex;
        }
        throw new IllegalStateException("commit already called");
    }
    
    @Override
    public void commitNowAllowingStateLoss() {
        this.disallowAddToBackStack();
        this.mManager.execSingleAction((FragmentManager.OpGenerator)this, true);
    }
    
    @Override
    public FragmentTransaction detach(final Fragment fragment) {
        final FragmentManager mFragmentManager = fragment.mFragmentManager;
        if (mFragmentManager != null && mFragmentManager != this.mManager) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot detach Fragment attached to a different FragmentManager. Fragment ");
            sb.append(fragment.toString());
            sb.append(" is already attached to a FragmentManager.");
            throw new IllegalStateException(sb.toString());
        }
        super.detach(fragment);
        return this;
    }
    
    @Override
    void doAddOp(final int n, final Fragment fragment, final String s, final int n2) {
        super.doAddOp(n, fragment, s, n2);
        fragment.mFragmentManager = this.mManager;
    }
    
    public void dump(final String s, final PrintWriter printWriter) {
        this.dump(s, printWriter, true);
    }
    
    public void dump(final String s, final PrintWriter printWriter, final boolean b) {
        if (b) {
            printWriter.print(s);
            printWriter.print("mName=");
            printWriter.print(super.mName);
            printWriter.print(" mIndex=");
            printWriter.print(this.mIndex);
            printWriter.print(" mCommitted=");
            printWriter.println(this.mCommitted);
            if (super.mTransition != 0) {
                printWriter.print(s);
                printWriter.print("mTransition=#");
                printWriter.print(Integer.toHexString(super.mTransition));
            }
            if (super.mEnterAnim != 0 || super.mExitAnim != 0) {
                printWriter.print(s);
                printWriter.print("mEnterAnim=#");
                printWriter.print(Integer.toHexString(super.mEnterAnim));
                printWriter.print(" mExitAnim=#");
                printWriter.println(Integer.toHexString(super.mExitAnim));
            }
            if (super.mPopEnterAnim != 0 || super.mPopExitAnim != 0) {
                printWriter.print(s);
                printWriter.print("mPopEnterAnim=#");
                printWriter.print(Integer.toHexString(super.mPopEnterAnim));
                printWriter.print(" mPopExitAnim=#");
                printWriter.println(Integer.toHexString(super.mPopExitAnim));
            }
            if (super.mBreadCrumbTitleRes != 0 || super.mBreadCrumbTitleText != null) {
                printWriter.print(s);
                printWriter.print("mBreadCrumbTitleRes=#");
                printWriter.print(Integer.toHexString(super.mBreadCrumbTitleRes));
                printWriter.print(" mBreadCrumbTitleText=");
                printWriter.println(super.mBreadCrumbTitleText);
            }
            if (super.mBreadCrumbShortTitleRes != 0 || super.mBreadCrumbShortTitleText != null) {
                printWriter.print(s);
                printWriter.print("mBreadCrumbShortTitleRes=#");
                printWriter.print(Integer.toHexString(super.mBreadCrumbShortTitleRes));
                printWriter.print(" mBreadCrumbShortTitleText=");
                printWriter.println(super.mBreadCrumbShortTitleText);
            }
        }
        if (!super.mOps.isEmpty()) {
            printWriter.print(s);
            printWriter.println("Operations:");
            for (int size = super.mOps.size(), i = 0; i < size; ++i) {
                final Op op = super.mOps.get(i);
                String string = null;
                switch (op.mCmd) {
                    default: {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("cmd=");
                        sb.append(op.mCmd);
                        string = sb.toString();
                        break;
                    }
                    case 10: {
                        string = "OP_SET_MAX_LIFECYCLE";
                        break;
                    }
                    case 9: {
                        string = "UNSET_PRIMARY_NAV";
                        break;
                    }
                    case 8: {
                        string = "SET_PRIMARY_NAV";
                        break;
                    }
                    case 7: {
                        string = "ATTACH";
                        break;
                    }
                    case 6: {
                        string = "DETACH";
                        break;
                    }
                    case 5: {
                        string = "SHOW";
                        break;
                    }
                    case 4: {
                        string = "HIDE";
                        break;
                    }
                    case 3: {
                        string = "REMOVE";
                        break;
                    }
                    case 2: {
                        string = "REPLACE";
                        break;
                    }
                    case 1: {
                        string = "ADD";
                        break;
                    }
                    case 0: {
                        string = "NULL";
                        break;
                    }
                }
                printWriter.print(s);
                printWriter.print("  Op #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.print(string);
                printWriter.print(" ");
                printWriter.println(op.mFragment);
                if (b) {
                    if (op.mEnterAnim != 0 || op.mExitAnim != 0) {
                        printWriter.print(s);
                        printWriter.print("enterAnim=#");
                        printWriter.print(Integer.toHexString(op.mEnterAnim));
                        printWriter.print(" exitAnim=#");
                        printWriter.println(Integer.toHexString(op.mExitAnim));
                    }
                    if (op.mPopEnterAnim != 0 || op.mPopExitAnim != 0) {
                        printWriter.print(s);
                        printWriter.print("popEnterAnim=#");
                        printWriter.print(Integer.toHexString(op.mPopEnterAnim));
                        printWriter.print(" popExitAnim=#");
                        printWriter.println(Integer.toHexString(op.mPopExitAnim));
                    }
                }
            }
        }
    }
    
    void executeOps() {
        for (int size = super.mOps.size(), i = 0; i < size; ++i) {
            final Op op = super.mOps.get(i);
            final Fragment mFragment = op.mFragment;
            if (mFragment != null) {
                mFragment.setNextTransition(super.mTransition);
            }
            switch (op.mCmd) {
                default: {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unknown cmd: ");
                    sb.append(op.mCmd);
                    throw new IllegalArgumentException(sb.toString());
                }
                case 10: {
                    this.mManager.setMaxLifecycle(mFragment, op.mCurrentMaxState);
                    break;
                }
                case 9: {
                    this.mManager.setPrimaryNavigationFragment(null);
                    break;
                }
                case 8: {
                    this.mManager.setPrimaryNavigationFragment(mFragment);
                    break;
                }
                case 7: {
                    mFragment.setNextAnim(op.mEnterAnim);
                    this.mManager.setExitAnimationOrder(mFragment, false);
                    this.mManager.attachFragment(mFragment);
                    break;
                }
                case 6: {
                    mFragment.setNextAnim(op.mExitAnim);
                    this.mManager.detachFragment(mFragment);
                    break;
                }
                case 5: {
                    mFragment.setNextAnim(op.mEnterAnim);
                    this.mManager.setExitAnimationOrder(mFragment, false);
                    this.mManager.showFragment(mFragment);
                    break;
                }
                case 4: {
                    mFragment.setNextAnim(op.mExitAnim);
                    this.mManager.hideFragment(mFragment);
                    break;
                }
                case 3: {
                    mFragment.setNextAnim(op.mExitAnim);
                    this.mManager.removeFragment(mFragment);
                    break;
                }
                case 1: {
                    mFragment.setNextAnim(op.mEnterAnim);
                    this.mManager.setExitAnimationOrder(mFragment, false);
                    this.mManager.addFragment(mFragment);
                    break;
                }
            }
            if (!super.mReorderingAllowed && op.mCmd != 1 && mFragment != null) {
                if (FragmentManager.USE_STATE_MANAGER) {
                    this.mManager.createOrGetFragmentStateManager(mFragment).moveToExpectedState();
                }
                else {
                    this.mManager.moveFragmentToExpectedState(mFragment);
                }
            }
        }
        if (!super.mReorderingAllowed) {
            final FragmentManager mManager = this.mManager;
            mManager.moveToState(mManager.mCurState, true);
        }
    }
    
    void executePopOps(final boolean b) {
        for (int i = super.mOps.size() - 1; i >= 0; --i) {
            final Op op = super.mOps.get(i);
            final Fragment mFragment = op.mFragment;
            if (mFragment != null) {
                mFragment.setNextTransition(FragmentManager.reverseTransit(super.mTransition));
            }
            switch (op.mCmd) {
                default: {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unknown cmd: ");
                    sb.append(op.mCmd);
                    throw new IllegalArgumentException(sb.toString());
                }
                case 10: {
                    this.mManager.setMaxLifecycle(mFragment, op.mOldMaxState);
                    break;
                }
                case 9: {
                    this.mManager.setPrimaryNavigationFragment(mFragment);
                    break;
                }
                case 8: {
                    this.mManager.setPrimaryNavigationFragment(null);
                    break;
                }
                case 7: {
                    mFragment.setNextAnim(op.mPopExitAnim);
                    this.mManager.setExitAnimationOrder(mFragment, true);
                    this.mManager.detachFragment(mFragment);
                    break;
                }
                case 6: {
                    mFragment.setNextAnim(op.mPopEnterAnim);
                    this.mManager.attachFragment(mFragment);
                    break;
                }
                case 5: {
                    mFragment.setNextAnim(op.mPopExitAnim);
                    this.mManager.setExitAnimationOrder(mFragment, true);
                    this.mManager.hideFragment(mFragment);
                    break;
                }
                case 4: {
                    mFragment.setNextAnim(op.mPopEnterAnim);
                    this.mManager.showFragment(mFragment);
                    break;
                }
                case 3: {
                    mFragment.setNextAnim(op.mPopEnterAnim);
                    this.mManager.addFragment(mFragment);
                    break;
                }
                case 1: {
                    mFragment.setNextAnim(op.mPopExitAnim);
                    this.mManager.setExitAnimationOrder(mFragment, true);
                    this.mManager.removeFragment(mFragment);
                    break;
                }
            }
            if (!super.mReorderingAllowed && op.mCmd != 3 && mFragment != null) {
                if (FragmentManager.USE_STATE_MANAGER) {
                    this.mManager.createOrGetFragmentStateManager(mFragment).moveToExpectedState();
                }
                else {
                    this.mManager.moveFragmentToExpectedState(mFragment);
                }
            }
        }
        if (!super.mReorderingAllowed && b) {
            final FragmentManager mManager = this.mManager;
            mManager.moveToState(mManager.mCurState, true);
        }
    }
    
    Fragment expandOps(final ArrayList<Fragment> list, Fragment mFragment) {
        int i = 0;
        Fragment fragment = mFragment;
        while (i < super.mOps.size()) {
            final Op op = super.mOps.get(i);
            final int mCmd = op.mCmd;
            int n = 0;
            Label_0441: {
                if (mCmd != 1) {
                    if (mCmd != 2) {
                        if (mCmd != 3 && mCmd != 6) {
                            if (mCmd != 7) {
                                if (mCmd != 8) {
                                    mFragment = fragment;
                                    n = i;
                                    break Label_0441;
                                }
                                super.mOps.add(i, new Op(9, fragment));
                                n = i + 1;
                                mFragment = op.mFragment;
                                break Label_0441;
                            }
                        }
                        else {
                            list.remove(op.mFragment);
                            final Fragment mFragment2 = op.mFragment;
                            mFragment = fragment;
                            n = i;
                            if (mFragment2 == fragment) {
                                super.mOps.add(i, new Op(9, mFragment2));
                                n = i + 1;
                                mFragment = null;
                            }
                            break Label_0441;
                        }
                    }
                    else {
                        final Fragment mFragment3 = op.mFragment;
                        final int mContainerId = mFragment3.mContainerId;
                        int j = list.size() - 1;
                        int n2 = 0;
                        n = i;
                        mFragment = fragment;
                        while (j >= 0) {
                            final Fragment o = list.get(j);
                            Fragment fragment2 = mFragment;
                            int index = n;
                            int n3 = n2;
                            if (o.mContainerId == mContainerId) {
                                if (o == mFragment3) {
                                    n3 = 1;
                                    fragment2 = mFragment;
                                    index = n;
                                }
                                else {
                                    fragment2 = mFragment;
                                    index = n;
                                    if (o == mFragment) {
                                        super.mOps.add(n, new Op(9, o));
                                        index = n + 1;
                                        fragment2 = null;
                                    }
                                    final Op element = new Op(3, o);
                                    element.mEnterAnim = op.mEnterAnim;
                                    element.mPopEnterAnim = op.mPopEnterAnim;
                                    element.mExitAnim = op.mExitAnim;
                                    element.mPopExitAnim = op.mPopExitAnim;
                                    super.mOps.add(index, element);
                                    list.remove(o);
                                    ++index;
                                    n3 = n2;
                                }
                            }
                            --j;
                            mFragment = fragment2;
                            n = index;
                            n2 = n3;
                        }
                        if (n2 != 0) {
                            super.mOps.remove(n);
                            --n;
                            break Label_0441;
                        }
                        op.mCmd = 1;
                        list.add(mFragment3);
                        break Label_0441;
                    }
                }
                list.add(op.mFragment);
                n = i;
                mFragment = fragment;
            }
            i = n + 1;
            fragment = mFragment;
        }
        return fragment;
    }
    
    @Override
    public boolean generateOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Run: ");
            sb.append(this);
            Log.v("FragmentManager", sb.toString());
        }
        list.add(this);
        list2.add(Boolean.FALSE);
        if (super.mAddToBackStack) {
            this.mManager.addBackStackState(this);
        }
        return true;
    }
    
    public String getName() {
        return super.mName;
    }
    
    boolean interactsWith(final int n) {
        for (int size = super.mOps.size(), i = 0; i < size; ++i) {
            final Fragment mFragment = ((Op)super.mOps.get(i)).mFragment;
            int mContainerId;
            if (mFragment != null) {
                mContainerId = mFragment.mContainerId;
            }
            else {
                mContainerId = 0;
            }
            if (mContainerId != 0 && mContainerId == n) {
                return true;
            }
        }
        return false;
    }
    
    boolean interactsWith(final ArrayList<BackStackRecord> list, final int n, final int n2) {
        if (n2 == n) {
            return false;
        }
        final int size = super.mOps.size();
        int n3 = -1;
        int n4;
        for (int i = 0; i < size; ++i, n3 = n4) {
            final Fragment mFragment = ((Op)super.mOps.get(i)).mFragment;
            int mContainerId;
            if (mFragment != null) {
                mContainerId = mFragment.mContainerId;
            }
            else {
                mContainerId = 0;
            }
            n4 = n3;
            if (mContainerId != 0 && mContainerId != (n4 = n3)) {
                for (int j = n; j < n2; ++j) {
                    final BackStackRecord backStackRecord = list.get(j);
                    for (int size2 = backStackRecord.mOps.size(), k = 0; k < size2; ++k) {
                        final Fragment mFragment2 = ((Op)backStackRecord.mOps.get(k)).mFragment;
                        int mContainerId2;
                        if (mFragment2 != null) {
                            mContainerId2 = mFragment2.mContainerId;
                        }
                        else {
                            mContainerId2 = 0;
                        }
                        if (mContainerId2 == mContainerId) {
                            return true;
                        }
                    }
                }
                n4 = mContainerId;
            }
        }
        return false;
    }
    
    boolean isPostponed() {
        for (int i = 0; i < super.mOps.size(); ++i) {
            if (isFragmentPostponed(super.mOps.get(i))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public FragmentTransaction remove(final Fragment fragment) {
        final FragmentManager mFragmentManager = fragment.mFragmentManager;
        if (mFragmentManager != null && mFragmentManager != this.mManager) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot remove Fragment attached to a different FragmentManager. Fragment ");
            sb.append(fragment.toString());
            sb.append(" is already attached to a FragmentManager.");
            throw new IllegalStateException(sb.toString());
        }
        super.remove(fragment);
        return this;
    }
    
    public void runOnCommitRunnables() {
        if (super.mCommitRunnables != null) {
            for (int i = 0; i < super.mCommitRunnables.size(); ++i) {
                super.mCommitRunnables.get(i).run();
            }
            super.mCommitRunnables = null;
        }
    }
    
    void setOnStartPostponedListener(final Fragment.OnStartEnterTransitionListener onStartEnterTransitionListener) {
        for (int i = 0; i < super.mOps.size(); ++i) {
            final Op op = super.mOps.get(i);
            if (isFragmentPostponed(op)) {
                op.mFragment.setOnStartEnterTransitionListener(onStartEnterTransitionListener);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("BackStackEntry{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.mIndex >= 0) {
            sb.append(" #");
            sb.append(this.mIndex);
        }
        if (super.mName != null) {
            sb.append(" ");
            sb.append(super.mName);
        }
        sb.append("}");
        return sb.toString();
    }
    
    Fragment trackAddedFragmentsInPop(final ArrayList<Fragment> list, Fragment mFragment) {
        for (int i = super.mOps.size() - 1; i >= 0; --i) {
            final Op op = super.mOps.get(i);
            final int mCmd = op.mCmd;
            Label_0127: {
                if (mCmd != 1) {
                    if (mCmd != 3) {
                        switch (mCmd) {
                            default: {
                                continue;
                            }
                            case 10: {
                                op.mCurrentMaxState = op.mOldMaxState;
                                continue;
                            }
                            case 9: {
                                mFragment = op.mFragment;
                                continue;
                            }
                            case 8: {
                                mFragment = null;
                                continue;
                            }
                            case 6: {
                                break;
                            }
                            case 7: {
                                break Label_0127;
                            }
                        }
                    }
                    list.add(op.mFragment);
                    continue;
                }
            }
            list.remove(op.mFragment);
        }
        return mFragment;
    }
}
