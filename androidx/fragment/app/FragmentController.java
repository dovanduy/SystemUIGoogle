// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import androidx.lifecycle.ViewModelStoreOwner;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.res.Configuration;
import androidx.core.util.Preconditions;

public class FragmentController
{
    private final FragmentHostCallback<?> mHost;
    
    private FragmentController(final FragmentHostCallback<?> mHost) {
        this.mHost = mHost;
    }
    
    public static FragmentController createController(final FragmentHostCallback<?> fragmentHostCallback) {
        Preconditions.checkNotNull(fragmentHostCallback, "callbacks == null");
        return new FragmentController(fragmentHostCallback);
    }
    
    public void attachHost(final Fragment fragment) {
        final FragmentHostCallback<?> mHost = this.mHost;
        mHost.mFragmentManager.attachController(mHost, mHost, fragment);
    }
    
    public void dispatchActivityCreated() {
        this.mHost.mFragmentManager.dispatchActivityCreated();
    }
    
    public void dispatchConfigurationChanged(final Configuration configuration) {
        this.mHost.mFragmentManager.dispatchConfigurationChanged(configuration);
    }
    
    public boolean dispatchContextItemSelected(final MenuItem menuItem) {
        return this.mHost.mFragmentManager.dispatchContextItemSelected(menuItem);
    }
    
    public void dispatchCreate() {
        this.mHost.mFragmentManager.dispatchCreate();
    }
    
    public boolean dispatchCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        return this.mHost.mFragmentManager.dispatchCreateOptionsMenu(menu, menuInflater);
    }
    
    public void dispatchDestroy() {
        this.mHost.mFragmentManager.dispatchDestroy();
    }
    
    public void dispatchLowMemory() {
        this.mHost.mFragmentManager.dispatchLowMemory();
    }
    
    public void dispatchMultiWindowModeChanged(final boolean b) {
        this.mHost.mFragmentManager.dispatchMultiWindowModeChanged(b);
    }
    
    public boolean dispatchOptionsItemSelected(final MenuItem menuItem) {
        return this.mHost.mFragmentManager.dispatchOptionsItemSelected(menuItem);
    }
    
    public void dispatchOptionsMenuClosed(final Menu menu) {
        this.mHost.mFragmentManager.dispatchOptionsMenuClosed(menu);
    }
    
    public void dispatchPause() {
        this.mHost.mFragmentManager.dispatchPause();
    }
    
    public void dispatchPictureInPictureModeChanged(final boolean b) {
        this.mHost.mFragmentManager.dispatchPictureInPictureModeChanged(b);
    }
    
    public boolean dispatchPrepareOptionsMenu(final Menu menu) {
        return this.mHost.mFragmentManager.dispatchPrepareOptionsMenu(menu);
    }
    
    public void dispatchResume() {
        this.mHost.mFragmentManager.dispatchResume();
    }
    
    public void dispatchStart() {
        this.mHost.mFragmentManager.dispatchStart();
    }
    
    public void dispatchStop() {
        this.mHost.mFragmentManager.dispatchStop();
    }
    
    public boolean execPendingActions() {
        return this.mHost.mFragmentManager.execPendingActions(true);
    }
    
    public Fragment findFragmentByWho(final String s) {
        return this.mHost.mFragmentManager.findFragmentByWho(s);
    }
    
    public FragmentManager getSupportFragmentManager() {
        return this.mHost.mFragmentManager;
    }
    
    public void noteStateNotSaved() {
        this.mHost.mFragmentManager.noteStateNotSaved();
    }
    
    public View onCreateView(final View view, final String s, final Context context, final AttributeSet set) {
        return this.mHost.mFragmentManager.getLayoutInflaterFactory().onCreateView(view, s, context, set);
    }
    
    public void restoreSaveState(final Parcelable parcelable) {
        final FragmentHostCallback<?> mHost = this.mHost;
        if (mHost instanceof ViewModelStoreOwner) {
            mHost.mFragmentManager.restoreSaveState(parcelable);
            return;
        }
        throw new IllegalStateException("Your FragmentHostCallback must implement ViewModelStoreOwner to call restoreSaveState(). Call restoreAllState()  if you're still using retainNestedNonConfig().");
    }
    
    public Parcelable saveAllState() {
        return this.mHost.mFragmentManager.saveAllState();
    }
}
