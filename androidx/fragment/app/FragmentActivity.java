// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.view.Window;
import android.view.LayoutInflater;
import androidx.lifecycle.ViewModelStore;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import android.content.IntentSender$SendIntentException;
import android.content.IntentSender;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.Menu;
import android.os.Bundle;
import android.content.res.Configuration;
import android.app.Activity;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.content.Intent;
import androidx.loader.app.LoaderManager;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.util.Iterator;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.collection.SparseArrayCompat;
import androidx.lifecycle.LifecycleRegistry;
import androidx.activity.ComponentActivity;

public class FragmentActivity extends ComponentActivity
{
    boolean mCreated;
    final LifecycleRegistry mFragmentLifecycleRegistry;
    final FragmentController mFragments;
    int mNextCandidateRequestIndex;
    SparseArrayCompat<String> mPendingFragmentActivityResults;
    boolean mResumed;
    boolean mStartedActivityFromFragment;
    boolean mStartedIntentSenderFromFragment;
    boolean mStopped;
    
    public FragmentActivity() {
        this.mFragments = FragmentController.createController(new HostCallbacks());
        this.mFragmentLifecycleRegistry = new LifecycleRegistry(this);
        this.mStopped = true;
    }
    
    static void checkForValidRequestCode(final int n) {
        if ((n & 0xFFFF0000) == 0x0) {
            return;
        }
        throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
    }
    
    private void markFragmentsCreated() {
        while (markState(this.getSupportFragmentManager(), Lifecycle.State.CREATED)) {}
    }
    
    private static boolean markState(final FragmentManager fragmentManager, final Lifecycle.State currentState) {
        final Iterator<Fragment> iterator = (Iterator<Fragment>)fragmentManager.getFragments().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final Fragment fragment = iterator.next();
            if (fragment == null) {
                continue;
            }
            int n2 = n;
            if (fragment.getHost() != null) {
                n2 = (n | (markState(fragment.getChildFragmentManager(), currentState) ? 1 : 0));
            }
            n = n2;
            if (!fragment.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                continue;
            }
            fragment.mLifecycleRegistry.setCurrentState(currentState);
            n = (true ? 1 : 0);
        }
        return n != 0;
    }
    
    final View dispatchFragmentsOnCreateView(final View view, final String s, final Context context, final AttributeSet set) {
        return this.mFragments.onCreateView(view, s, context, set);
    }
    
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        super.dump(s, fileDescriptor, printWriter, array);
        printWriter.print(s);
        printWriter.print("Local FragmentActivity ");
        printWriter.print(Integer.toHexString(System.identityHashCode(this)));
        printWriter.println(" State:");
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        printWriter.print(string);
        printWriter.print("mCreated=");
        printWriter.print(this.mCreated);
        printWriter.print(" mResumed=");
        printWriter.print(this.mResumed);
        printWriter.print(" mStopped=");
        printWriter.print(this.mStopped);
        if (this.getApplication() != null) {
            LoaderManager.getInstance(this).dump(string, fileDescriptor, printWriter, array);
        }
        this.mFragments.getSupportFragmentManager().dump(s, fileDescriptor, printWriter, array);
    }
    
    public FragmentManager getSupportFragmentManager() {
        return this.mFragments.getSupportFragmentManager();
    }
    
    @Override
    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        this.mFragments.noteStateNotSaved();
        int n3 = n >> 16;
        if (n3 != 0) {
            --n3;
            final String str = this.mPendingFragmentActivityResults.get(n3);
            this.mPendingFragmentActivityResults.remove(n3);
            if (str == null) {
                Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
                return;
            }
            final Fragment fragmentByWho = this.mFragments.findFragmentByWho(str);
            if (fragmentByWho == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Activity result no fragment exists for who: ");
                sb.append(str);
                Log.w("FragmentActivity", sb.toString());
            }
            else {
                fragmentByWho.onActivityResult(n & 0xFFFF, n2, intent);
            }
        }
        else {
            final ActivityCompat.PermissionCompatDelegate permissionCompatDelegate = ActivityCompat.getPermissionCompatDelegate();
            if (permissionCompatDelegate != null && permissionCompatDelegate.onActivityResult(this, n, n2, intent)) {
                return;
            }
            super.onActivityResult(n, n2, intent);
        }
    }
    
    public void onAttachFragment(final Fragment fragment) {
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFragments.noteStateNotSaved();
        this.mFragments.dispatchConfigurationChanged(configuration);
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        this.mFragments.attachHost(null);
        if (bundle != null) {
            this.mFragments.restoreSaveState(bundle.getParcelable("android:support:fragments"));
            if (bundle.containsKey("android:support:next_request_index")) {
                this.mNextCandidateRequestIndex = bundle.getInt("android:support:next_request_index");
                final int[] intArray = bundle.getIntArray("android:support:request_indicies");
                final String[] stringArray = bundle.getStringArray("android:support:request_fragment_who");
                if (intArray != null && stringArray != null && intArray.length == stringArray.length) {
                    this.mPendingFragmentActivityResults = new SparseArrayCompat<String>(intArray.length);
                    for (int i = 0; i < intArray.length; ++i) {
                        this.mPendingFragmentActivityResults.put(intArray[i], stringArray[i]);
                    }
                }
                else {
                    Log.w("FragmentActivity", "Invalid requestCode mapping in savedInstanceState.");
                }
            }
        }
        if (this.mPendingFragmentActivityResults == null) {
            this.mPendingFragmentActivityResults = new SparseArrayCompat<String>();
            this.mNextCandidateRequestIndex = 0;
        }
        super.onCreate(bundle);
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        this.mFragments.dispatchCreate();
    }
    
    public boolean onCreatePanelMenu(final int n, final Menu menu) {
        if (n == 0) {
            return this.mFragments.dispatchCreateOptionsMenu(menu, this.getMenuInflater()) | super.onCreatePanelMenu(n, menu);
        }
        return super.onCreatePanelMenu(n, menu);
    }
    
    public View onCreateView(final View view, final String s, final Context context, final AttributeSet set) {
        final View dispatchFragmentsOnCreateView = this.dispatchFragmentsOnCreateView(view, s, context, set);
        if (dispatchFragmentsOnCreateView == null) {
            return super.onCreateView(view, s, context, set);
        }
        return dispatchFragmentsOnCreateView;
    }
    
    public View onCreateView(final String s, final Context context, final AttributeSet set) {
        final View dispatchFragmentsOnCreateView = this.dispatchFragmentsOnCreateView(null, s, context, set);
        if (dispatchFragmentsOnCreateView == null) {
            return super.onCreateView(s, context, set);
        }
        return dispatchFragmentsOnCreateView;
    }
    
    protected void onDestroy() {
        super.onDestroy();
        this.mFragments.dispatchDestroy();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }
    
    public void onLowMemory() {
        super.onLowMemory();
        this.mFragments.dispatchLowMemory();
    }
    
    public boolean onMenuItemSelected(final int n, final MenuItem menuItem) {
        if (super.onMenuItemSelected(n, menuItem)) {
            return true;
        }
        if (n != 0) {
            return n == 6 && this.mFragments.dispatchContextItemSelected(menuItem);
        }
        return this.mFragments.dispatchOptionsItemSelected(menuItem);
    }
    
    public void onMultiWindowModeChanged(final boolean b) {
        this.mFragments.dispatchMultiWindowModeChanged(b);
    }
    
    protected void onNewIntent(@SuppressLint({ "UnknownNullness" }) final Intent intent) {
        super.onNewIntent(intent);
        this.mFragments.noteStateNotSaved();
    }
    
    public void onPanelClosed(final int n, final Menu menu) {
        if (n == 0) {
            this.mFragments.dispatchOptionsMenuClosed(menu);
        }
        super.onPanelClosed(n, menu);
    }
    
    protected void onPause() {
        super.onPause();
        this.mResumed = false;
        this.mFragments.dispatchPause();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }
    
    public void onPictureInPictureModeChanged(final boolean b) {
        this.mFragments.dispatchPictureInPictureModeChanged(b);
    }
    
    protected void onPostResume() {
        super.onPostResume();
        this.onResumeFragments();
    }
    
    @Deprecated
    protected boolean onPrepareOptionsPanel(final View view, final Menu menu) {
        return super.onPreparePanel(0, view, menu);
    }
    
    public boolean onPreparePanel(final int n, final View view, final Menu menu) {
        if (n == 0) {
            return this.mFragments.dispatchPrepareOptionsMenu(menu) | this.onPrepareOptionsPanel(view, menu);
        }
        return super.onPreparePanel(n, view, menu);
    }
    
    @Override
    public void onRequestPermissionsResult(final int n, final String[] array, final int[] array2) {
        this.mFragments.noteStateNotSaved();
        super.onRequestPermissionsResult(n, array, array2);
        int n2 = n >> 16 & 0xFFFF;
        if (n2 != 0) {
            --n2;
            final String str = this.mPendingFragmentActivityResults.get(n2);
            this.mPendingFragmentActivityResults.remove(n2);
            if (str == null) {
                Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
                return;
            }
            final Fragment fragmentByWho = this.mFragments.findFragmentByWho(str);
            if (fragmentByWho == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Activity result no fragment exists for who: ");
                sb.append(str);
                Log.w("FragmentActivity", sb.toString());
            }
            else {
                fragmentByWho.onRequestPermissionsResult(n & 0xFFFF, array, array2);
            }
        }
    }
    
    protected void onResume() {
        super.onResume();
        this.mResumed = true;
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
    }
    
    protected void onResumeFragments() {
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        this.mFragments.dispatchResume();
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.markFragmentsCreated();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        final Parcelable saveAllState = this.mFragments.saveAllState();
        if (saveAllState != null) {
            bundle.putParcelable("android:support:fragments", saveAllState);
        }
        if (this.mPendingFragmentActivityResults.size() > 0) {
            bundle.putInt("android:support:next_request_index", this.mNextCandidateRequestIndex);
            final int[] array = new int[this.mPendingFragmentActivityResults.size()];
            final String[] array2 = new String[this.mPendingFragmentActivityResults.size()];
            for (int i = 0; i < this.mPendingFragmentActivityResults.size(); ++i) {
                array[i] = this.mPendingFragmentActivityResults.keyAt(i);
                array2[i] = this.mPendingFragmentActivityResults.valueAt(i);
            }
            bundle.putIntArray("android:support:request_indicies", array);
            bundle.putStringArray("android:support:request_fragment_who", array2);
        }
    }
    
    protected void onStart() {
        super.onStart();
        this.mStopped = false;
        if (!this.mCreated) {
            this.mCreated = true;
            this.mFragments.dispatchActivityCreated();
        }
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        this.mFragments.dispatchStart();
    }
    
    public void onStateNotSaved() {
        this.mFragments.noteStateNotSaved();
    }
    
    protected void onStop() {
        super.onStop();
        this.mStopped = true;
        this.markFragmentsCreated();
        this.mFragments.dispatchStop();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }
    
    public void startActivityForResult(@SuppressLint({ "UnknownNullness" }) final Intent intent, final int n) {
        if (!this.mStartedActivityFromFragment && n != -1) {
            checkForValidRequestCode(n);
        }
        super.startActivityForResult(intent, n);
    }
    
    public void startActivityForResult(@SuppressLint({ "UnknownNullness" }) final Intent intent, final int n, final Bundle bundle) {
        if (!this.mStartedActivityFromFragment && n != -1) {
            checkForValidRequestCode(n);
        }
        super.startActivityForResult(intent, n, bundle);
    }
    
    public void startIntentSenderForResult(@SuppressLint({ "UnknownNullness" }) final IntentSender intentSender, final int n, final Intent intent, final int n2, final int n3, final int n4) throws IntentSender$SendIntentException {
        if (!this.mStartedIntentSenderFromFragment && n != -1) {
            checkForValidRequestCode(n);
        }
        super.startIntentSenderForResult(intentSender, n, intent, n2, n3, n4);
    }
    
    public void startIntentSenderForResult(@SuppressLint({ "UnknownNullness" }) final IntentSender intentSender, final int n, final Intent intent, final int n2, final int n3, final int n4, final Bundle bundle) throws IntentSender$SendIntentException {
        if (!this.mStartedIntentSenderFromFragment && n != -1) {
            checkForValidRequestCode(n);
        }
        super.startIntentSenderForResult(intentSender, n, intent, n2, n3, n4, bundle);
    }
    
    @Deprecated
    public void supportInvalidateOptionsMenu() {
        this.invalidateOptionsMenu();
    }
    
    class HostCallbacks extends FragmentHostCallback<FragmentActivity> implements ViewModelStoreOwner, OnBackPressedDispatcherOwner
    {
        public HostCallbacks() {
            super(FragmentActivity.this);
        }
        
        @Override
        public Lifecycle getLifecycle() {
            return FragmentActivity.this.mFragmentLifecycleRegistry;
        }
        
        @Override
        public OnBackPressedDispatcher getOnBackPressedDispatcher() {
            return FragmentActivity.this.getOnBackPressedDispatcher();
        }
        
        @Override
        public ViewModelStore getViewModelStore() {
            return FragmentActivity.this.getViewModelStore();
        }
        
        public void onAttachFragment(final Fragment fragment) {
            FragmentActivity.this.onAttachFragment(fragment);
        }
        
        @Override
        public View onFindViewById(final int n) {
            return FragmentActivity.this.findViewById(n);
        }
        
        @Override
        public FragmentActivity onGetHost() {
            return FragmentActivity.this;
        }
        
        @Override
        public LayoutInflater onGetLayoutInflater() {
            return FragmentActivity.this.getLayoutInflater().cloneInContext((Context)FragmentActivity.this);
        }
        
        @Override
        public boolean onHasView() {
            final Window window = FragmentActivity.this.getWindow();
            return window != null && window.peekDecorView() != null;
        }
        
        @Override
        public boolean onShouldSaveFragmentState(final Fragment fragment) {
            return FragmentActivity.this.isFinishing() ^ true;
        }
        
        @Override
        public void onSupportInvalidateOptionsMenu() {
            FragmentActivity.this.supportInvalidateOptionsMenu();
        }
    }
}
