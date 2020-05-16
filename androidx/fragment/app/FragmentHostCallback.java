// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.view.LayoutInflater;
import android.view.View;
import androidx.core.util.Preconditions;
import android.os.Handler;
import android.content.Context;
import android.app.Activity;

public abstract class FragmentHostCallback<E> extends FragmentContainer
{
    private final Activity mActivity;
    private final Context mContext;
    final FragmentManager mFragmentManager;
    private final Handler mHandler;
    
    FragmentHostCallback(final Activity mActivity, final Context context, final Handler handler, final int n) {
        this.mFragmentManager = new FragmentManagerImpl();
        this.mActivity = mActivity;
        Preconditions.checkNotNull(context, "context == null");
        this.mContext = context;
        Preconditions.checkNotNull(handler, "handler == null");
        this.mHandler = handler;
    }
    
    FragmentHostCallback(final FragmentActivity fragmentActivity) {
        this(fragmentActivity, (Context)fragmentActivity, new Handler(), 0);
    }
    
    Activity getActivity() {
        return this.mActivity;
    }
    
    Context getContext() {
        return this.mContext;
    }
    
    Handler getHandler() {
        return this.mHandler;
    }
    
    void onAttachFragment(final Fragment fragment) {
    }
    
    @Override
    public View onFindViewById(final int n) {
        return null;
    }
    
    public abstract E onGetHost();
    
    public LayoutInflater onGetLayoutInflater() {
        return LayoutInflater.from(this.mContext);
    }
    
    @Override
    public boolean onHasView() {
        return true;
    }
    
    public boolean onShouldSaveFragmentState(final Fragment fragment) {
        return true;
    }
    
    public void onSupportInvalidateOptionsMenu() {
    }
}
