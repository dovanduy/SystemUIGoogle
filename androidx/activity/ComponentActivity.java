// 
// Decompiled by Procyon v0.5.36
// 

package androidx.activity;

import androidx.lifecycle.ReportFragment;
import android.os.Bundle;
import android.content.Intent;
import androidx.savedstate.SavedStateRegistry;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import android.view.ViewGroup$LayoutParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.lifecycle.LifecycleObserver;
import android.view.View;
import android.view.Window;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import java.util.concurrent.atomic.AtomicInteger;
import android.text.TextUtils;
import android.os.Build$VERSION;
import androidx.lifecycle.ViewModelStore;
import androidx.savedstate.SavedStateRegistryController;
import androidx.lifecycle.LifecycleRegistry;
import androidx.activity.result.ActivityResultRegistry;
import androidx.savedstate.SavedStateRegistryOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.LifecycleOwner;

public class ComponentActivity extends androidx.core.app.ComponentActivity implements LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner, OnBackPressedDispatcherOwner
{
    private ActivityResultRegistry mActivityResultRegistry;
    private int mContentLayoutId;
    private final LifecycleRegistry mLifecycleRegistry;
    private final OnBackPressedDispatcher mOnBackPressedDispatcher;
    private final SavedStateRegistryController mSavedStateRegistryController;
    private ViewModelStore mViewModelStore;
    
    public ComponentActivity() {
        final int sdk_INT = Build$VERSION.SDK_INT;
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mSavedStateRegistryController = SavedStateRegistryController.create(this);
        this.mOnBackPressedDispatcher = new OnBackPressedDispatcher(new Runnable() {
            @Override
            public void run() {
                try {
                    ComponentActivity.access$001(ComponentActivity.this);
                }
                catch (IllegalStateException ex) {
                    if (!TextUtils.equals((CharSequence)ex.getMessage(), (CharSequence)"Can not perform this action after onSaveInstanceState")) {
                        throw ex;
                    }
                }
            }
        });
        new AtomicInteger();
        this.mActivityResultRegistry = new ActivityResultRegistry() {};
        if (this.getLifecycle() != null) {
            if (sdk_INT >= 19) {
                this.getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_STOP) {
                            final Window window = ComponentActivity.this.getWindow();
                            View peekDecorView;
                            if (window != null) {
                                peekDecorView = window.peekDecorView();
                            }
                            else {
                                peekDecorView = null;
                            }
                            if (peekDecorView != null) {
                                peekDecorView.cancelPendingInputEvents();
                            }
                        }
                    }
                });
            }
            this.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY && !ComponentActivity.this.isChangingConfigurations()) {
                        ComponentActivity.this.getViewModelStore().clear();
                    }
                }
            });
            if (19 <= sdk_INT && sdk_INT <= 23) {
                this.getLifecycle().addObserver(new ImmLeaksCleaner(this));
            }
            return;
        }
        throw new IllegalStateException("getLifecycle() returned null in ComponentActivity's constructor. Please make sure you are lazily constructing your Lifecycle in the first call to getLifecycle() rather than relying on field initialization.");
    }
    
    static /* synthetic */ void access$001(final ComponentActivity componentActivity) {
        componentActivity.onBackPressed();
    }
    
    public void addContentView(@SuppressLint({ "UnknownNullness", "MissingNullability" }) final View view, @SuppressLint({ "UnknownNullness", "MissingNullability" }) final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        ViewTreeLifecycleOwner.set(this.getWindow().getDecorView(), this);
        super.addContentView(view, viewGroup$LayoutParams);
    }
    
    public ActivityResultRegistry getActivityResultRegistry() {
        return this.mActivityResultRegistry;
    }
    
    @Override
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }
    
    @Override
    public final OnBackPressedDispatcher getOnBackPressedDispatcher() {
        return this.mOnBackPressedDispatcher;
    }
    
    @Override
    public final SavedStateRegistry getSavedStateRegistry() {
        return this.mSavedStateRegistryController.getSavedStateRegistry();
    }
    
    @Override
    public ViewModelStore getViewModelStore() {
        if (this.getApplication() != null) {
            if (this.mViewModelStore == null) {
                final NonConfigurationInstances nonConfigurationInstances = (NonConfigurationInstances)this.getLastNonConfigurationInstance();
                if (nonConfigurationInstances != null) {
                    this.mViewModelStore = nonConfigurationInstances.viewModelStore;
                }
                if (this.mViewModelStore == null) {
                    this.mViewModelStore = new ViewModelStore();
                }
            }
            return this.mViewModelStore;
        }
        throw new IllegalStateException("Your activity is not yet attached to the Application instance. You can't request ViewModel before onCreate call.");
    }
    
    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        if (!this.mActivityResultRegistry.dispatchResult(n, n2, intent)) {
            super.onActivityResult(n, n2, intent);
        }
    }
    
    public void onBackPressed() {
        this.mOnBackPressedDispatcher.onBackPressed();
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mSavedStateRegistryController.performRestore(bundle);
        this.mActivityResultRegistry.onRestoreInstanceState(bundle);
        ReportFragment.injectIfNeededIn(this);
        final int mContentLayoutId = this.mContentLayoutId;
        if (mContentLayoutId != 0) {
            this.setContentView(mContentLayoutId);
        }
    }
    
    public void onRequestPermissionsResult(final int n, final String[] array, final int[] array2) {
        if (!this.mActivityResultRegistry.dispatchResult(n, -1, new Intent().putExtra("androidx.activity.result.contract.extra.PERMISSIONS", array).putExtra("androidx.activity.result.contract.extra.PERMISSION_GRANT_RESULTS", array2)) && Build$VERSION.SDK_INT >= 23) {
            super.onRequestPermissionsResult(n, array, array2);
        }
    }
    
    @Deprecated
    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }
    
    public final Object onRetainNonConfigurationInstance() {
        final Object onRetainCustomNonConfigurationInstance = this.onRetainCustomNonConfigurationInstance();
        ViewModelStore viewModelStore2;
        final ViewModelStore viewModelStore = viewModelStore2 = this.mViewModelStore;
        if (viewModelStore == null) {
            final NonConfigurationInstances nonConfigurationInstances = (NonConfigurationInstances)this.getLastNonConfigurationInstance();
            viewModelStore2 = viewModelStore;
            if (nonConfigurationInstances != null) {
                viewModelStore2 = nonConfigurationInstances.viewModelStore;
            }
        }
        if (viewModelStore2 == null && onRetainCustomNonConfigurationInstance == null) {
            return null;
        }
        final NonConfigurationInstances nonConfigurationInstances2 = new NonConfigurationInstances();
        nonConfigurationInstances2.custom = onRetainCustomNonConfigurationInstance;
        nonConfigurationInstances2.viewModelStore = viewModelStore2;
        return nonConfigurationInstances2;
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        final Lifecycle lifecycle = this.getLifecycle();
        if (lifecycle instanceof LifecycleRegistry) {
            ((LifecycleRegistry)lifecycle).setCurrentState(Lifecycle.State.CREATED);
        }
        super.onSaveInstanceState(bundle);
        this.mSavedStateRegistryController.performSave(bundle);
        this.mActivityResultRegistry.onSaveInstanceState(bundle);
    }
    
    public void setContentView(final int contentView) {
        ViewTreeLifecycleOwner.set(this.getWindow().getDecorView(), this);
        super.setContentView(contentView);
    }
    
    public void setContentView(@SuppressLint({ "UnknownNullness", "MissingNullability" }) final View contentView) {
        ViewTreeLifecycleOwner.set(this.getWindow().getDecorView(), this);
        super.setContentView(contentView);
    }
    
    public void setContentView(@SuppressLint({ "UnknownNullness", "MissingNullability" }) final View view, @SuppressLint({ "UnknownNullness", "MissingNullability" }) final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        ViewTreeLifecycleOwner.set(this.getWindow().getDecorView(), this);
        super.setContentView(view, viewGroup$LayoutParams);
    }
    
    static final class NonConfigurationInstances
    {
        Object custom;
        ViewModelStore viewModelStore;
    }
}
