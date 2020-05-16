// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import androidx.lifecycle.Lifecycle;
import java.lang.reflect.Modifier;
import android.view.ViewGroup;
import java.util.ArrayList;

public abstract class FragmentTransaction
{
    boolean mAddToBackStack;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;
    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    ArrayList<Runnable> mCommitRunnables;
    int mEnterAnim;
    int mExitAnim;
    String mName;
    ArrayList<Op> mOps;
    int mPopEnterAnim;
    int mPopExitAnim;
    boolean mReorderingAllowed;
    ArrayList<String> mSharedElementSourceNames;
    ArrayList<String> mSharedElementTargetNames;
    int mTransition;
    
    FragmentTransaction(final FragmentFactory fragmentFactory, final ClassLoader classLoader) {
        this.mOps = new ArrayList<Op>();
        this.mReorderingAllowed = false;
    }
    
    public FragmentTransaction add(final int n, final Fragment fragment, final String s) {
        this.doAddOp(n, fragment, s, 1);
        return this;
    }
    
    FragmentTransaction add(final ViewGroup mContainer, final Fragment fragment, final String s) {
        fragment.mContainer = mContainer;
        this.add(mContainer.getId(), fragment, s);
        return this;
    }
    
    public FragmentTransaction add(final Fragment fragment, final String s) {
        this.doAddOp(0, fragment, s, 1);
        return this;
    }
    
    void addOp(final Op e) {
        this.mOps.add(e);
        e.mEnterAnim = this.mEnterAnim;
        e.mExitAnim = this.mExitAnim;
        e.mPopEnterAnim = this.mPopEnterAnim;
        e.mPopExitAnim = this.mPopExitAnim;
    }
    
    public FragmentTransaction attach(final Fragment fragment) {
        this.addOp(new Op(7, fragment));
        return this;
    }
    
    public abstract int commit();
    
    public abstract int commitAllowingStateLoss();
    
    public abstract void commitNowAllowingStateLoss();
    
    public FragmentTransaction detach(final Fragment fragment) {
        this.addOp(new Op(6, fragment));
        return this;
    }
    
    public FragmentTransaction disallowAddToBackStack() {
        if (!this.mAddToBackStack) {
            return this;
        }
        throw new IllegalStateException("This transaction is already being added to the back stack");
    }
    
    void doAddOp(final int mContainerId, final Fragment obj, final String str, final int n) {
        final Class<? extends Fragment> class1 = obj.getClass();
        final int modifiers = class1.getModifiers();
        if (!class1.isAnonymousClass() && Modifier.isPublic(modifiers) && (!class1.isMemberClass() || Modifier.isStatic(modifiers))) {
            if (str != null) {
                final String mTag = obj.mTag;
                if (mTag != null && !str.equals(mTag)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Can't change tag of fragment ");
                    sb.append(obj);
                    sb.append(": was ");
                    sb.append(obj.mTag);
                    sb.append(" now ");
                    sb.append(str);
                    throw new IllegalStateException(sb.toString());
                }
                obj.mTag = str;
            }
            if (mContainerId != 0) {
                if (mContainerId == -1) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Can't add fragment ");
                    sb2.append(obj);
                    sb2.append(" with tag ");
                    sb2.append(str);
                    sb2.append(" to container view with no id");
                    throw new IllegalArgumentException(sb2.toString());
                }
                final int mFragmentId = obj.mFragmentId;
                if (mFragmentId != 0 && mFragmentId != mContainerId) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Can't change container ID of fragment ");
                    sb3.append(obj);
                    sb3.append(": was ");
                    sb3.append(obj.mFragmentId);
                    sb3.append(" now ");
                    sb3.append(mContainerId);
                    throw new IllegalStateException(sb3.toString());
                }
                obj.mFragmentId = mContainerId;
                obj.mContainerId = mContainerId;
            }
            this.addOp(new Op(n, obj));
            return;
        }
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("Fragment ");
        sb4.append(class1.getCanonicalName());
        sb4.append(" must be a public static class to be  properly recreated from instance state.");
        throw new IllegalStateException(sb4.toString());
    }
    
    public FragmentTransaction remove(final Fragment fragment) {
        this.addOp(new Op(3, fragment));
        return this;
    }
    
    public FragmentTransaction setReorderingAllowed(final boolean mReorderingAllowed) {
        this.mReorderingAllowed = mReorderingAllowed;
        return this;
    }
    
    static final class Op
    {
        int mCmd;
        Lifecycle.State mCurrentMaxState;
        int mEnterAnim;
        int mExitAnim;
        Fragment mFragment;
        Lifecycle.State mOldMaxState;
        int mPopEnterAnim;
        int mPopExitAnim;
        
        Op() {
        }
        
        Op(final int mCmd, final Fragment mFragment) {
            this.mCmd = mCmd;
            this.mFragment = mFragment;
            final Lifecycle.State resumed = Lifecycle.State.RESUMED;
            this.mOldMaxState = resumed;
            this.mCurrentMaxState = resumed;
        }
    }
}
