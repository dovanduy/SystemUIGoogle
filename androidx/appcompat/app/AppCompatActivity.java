// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.content.res.Configuration;
import androidx.core.app.NavUtils;
import android.content.Intent;
import androidx.appcompat.widget.VectorEnabledTintResources;
import android.view.MenuInflater;
import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.Window;
import android.os.Build$VERSION;
import android.view.KeyEvent;
import android.content.res.Resources;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.FragmentActivity;

public class AppCompatActivity extends FragmentActivity implements AppCompatCallback, SupportParentable
{
    private AppCompatDelegate mDelegate;
    private Resources mResources;
    
    private boolean performMenuItemShortcut(final KeyEvent keyEvent) {
        if (Build$VERSION.SDK_INT < 26 && !keyEvent.isCtrlPressed() && !KeyEvent.metaStateHasNoModifiers(keyEvent.getMetaState()) && keyEvent.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyEvent.getKeyCode())) {
            final Window window = this.getWindow();
            if (window != null && window.getDecorView() != null && window.getDecorView().dispatchKeyShortcutEvent(keyEvent)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void addContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        this.getDelegate().addContentView(view, viewGroup$LayoutParams);
    }
    
    protected void attachBaseContext(final Context context) {
        super.attachBaseContext(this.getDelegate().attachBaseContext2(context));
    }
    
    public void closeOptionsMenu() {
        final ActionBar supportActionBar = this.getSupportActionBar();
        if (this.getWindow().hasFeature(0) && (supportActionBar == null || !supportActionBar.closeOptionsMenu())) {
            super.closeOptionsMenu();
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final int keyCode = keyEvent.getKeyCode();
        final ActionBar supportActionBar = this.getSupportActionBar();
        return (keyCode == 82 && supportActionBar != null && supportActionBar.onMenuKeyEvent(keyEvent)) || super.dispatchKeyEvent(keyEvent);
    }
    
    public <T extends View> T findViewById(final int n) {
        return this.getDelegate().findViewById(n);
    }
    
    public AppCompatDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = AppCompatDelegate.create(this, this);
        }
        return this.mDelegate;
    }
    
    public MenuInflater getMenuInflater() {
        return this.getDelegate().getMenuInflater();
    }
    
    public Resources getResources() {
        if (this.mResources == null && VectorEnabledTintResources.shouldBeUsed()) {
            this.mResources = new VectorEnabledTintResources((Context)this, super.getResources());
        }
        Resources resources;
        if ((resources = this.mResources) == null) {
            resources = super.getResources();
        }
        return resources;
    }
    
    public ActionBar getSupportActionBar() {
        return this.getDelegate().getSupportActionBar();
    }
    
    @Override
    public Intent getSupportParentActivityIntent() {
        return NavUtils.getParentActivityIntent(this);
    }
    
    public void invalidateOptionsMenu() {
        this.getDelegate().invalidateOptionsMenu();
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mResources != null) {
            this.mResources.updateConfiguration(configuration, super.getResources().getDisplayMetrics());
        }
        this.getDelegate().onConfigurationChanged(configuration);
    }
    
    public void onContentChanged() {
        this.onSupportContentChanged();
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        final AppCompatDelegate delegate = this.getDelegate();
        delegate.installViewFactory();
        delegate.onCreate(bundle);
        super.onCreate(bundle);
    }
    
    public void onCreateSupportNavigateUpTaskStack(final TaskStackBuilder taskStackBuilder) {
        taskStackBuilder.addParentStack(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getDelegate().onDestroy();
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        return this.performMenuItemShortcut(keyEvent) || super.onKeyDown(n, keyEvent);
    }
    
    @Override
    public final boolean onMenuItemSelected(final int n, final MenuItem menuItem) {
        if (super.onMenuItemSelected(n, menuItem)) {
            return true;
        }
        final ActionBar supportActionBar = this.getSupportActionBar();
        return menuItem.getItemId() == 16908332 && supportActionBar != null && (supportActionBar.getDisplayOptions() & 0x4) != 0x0 && this.onSupportNavigateUp();
    }
    
    public boolean onMenuOpened(final int n, final Menu menu) {
        return super.onMenuOpened(n, menu);
    }
    
    protected void onNightModeChanged(final int n) {
    }
    
    @Override
    public void onPanelClosed(final int n, final Menu menu) {
        super.onPanelClosed(n, menu);
    }
    
    protected void onPostCreate(final Bundle bundle) {
        super.onPostCreate(bundle);
        this.getDelegate().onPostCreate(bundle);
    }
    
    @Override
    protected void onPostResume() {
        super.onPostResume();
        this.getDelegate().onPostResume();
    }
    
    public void onPrepareSupportNavigateUpTaskStack(final TaskStackBuilder taskStackBuilder) {
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.getDelegate().onSaveInstanceState(bundle);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        this.getDelegate().onStart();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        this.getDelegate().onStop();
    }
    
    @Override
    public void onSupportActionModeFinished(final ActionMode actionMode) {
    }
    
    @Override
    public void onSupportActionModeStarted(final ActionMode actionMode) {
    }
    
    @Deprecated
    public void onSupportContentChanged() {
    }
    
    public boolean onSupportNavigateUp() {
        final Intent supportParentActivityIntent = this.getSupportParentActivityIntent();
        if (supportParentActivityIntent != null) {
            if (this.supportShouldUpRecreateTask(supportParentActivityIntent)) {
                final TaskStackBuilder create = TaskStackBuilder.create((Context)this);
                this.onCreateSupportNavigateUpTaskStack(create);
                this.onPrepareSupportNavigateUpTaskStack(create);
                create.startActivities();
                try {
                    ActivityCompat.finishAffinity(this);
                }
                catch (IllegalStateException ex) {
                    this.finish();
                }
            }
            else {
                this.supportNavigateUpTo(supportParentActivityIntent);
            }
            return true;
        }
        return false;
    }
    
    protected void onTitleChanged(final CharSequence title, final int n) {
        super.onTitleChanged(title, n);
        this.getDelegate().setTitle(title);
    }
    
    @Override
    public ActionMode onWindowStartingSupportActionMode(final ActionMode.Callback callback) {
        return null;
    }
    
    public void openOptionsMenu() {
        final ActionBar supportActionBar = this.getSupportActionBar();
        if (this.getWindow().hasFeature(0) && (supportActionBar == null || !supportActionBar.openOptionsMenu())) {
            super.openOptionsMenu();
        }
    }
    
    @Override
    public void setContentView(final int contentView) {
        this.getDelegate().setContentView(contentView);
    }
    
    @Override
    public void setContentView(final View contentView) {
        this.getDelegate().setContentView(contentView);
    }
    
    @Override
    public void setContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        this.getDelegate().setContentView(view, viewGroup$LayoutParams);
    }
    
    public void setTheme(final int n) {
        super.setTheme(n);
        this.getDelegate().setTheme(n);
    }
    
    @Override
    public void supportInvalidateOptionsMenu() {
        this.getDelegate().invalidateOptionsMenu();
    }
    
    public void supportNavigateUpTo(final Intent intent) {
        NavUtils.navigateUpTo(this, intent);
    }
    
    public boolean supportShouldUpRecreateTask(final Intent intent) {
        return NavUtils.shouldUpRecreateTask(this, intent);
    }
}
