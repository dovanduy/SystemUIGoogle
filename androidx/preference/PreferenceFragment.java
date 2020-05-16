// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.os.Bundle;
import android.view.View;
import android.os.Message;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.app.Fragment;

@Deprecated
public abstract class PreferenceFragment extends Fragment implements OnPreferenceTreeClickListener, OnDisplayPreferenceDialogListener, OnNavigateToScreenListener, TargetFragment
{
    private final DividerDecoration mDividerDecoration;
    private final Handler mHandler;
    private boolean mHavePrefs;
    private boolean mInitDone;
    private int mLayoutResId;
    RecyclerView mList;
    private PreferenceManager mPreferenceManager;
    private final Runnable mRequestFocus;
    private Runnable mSelectPreferenceRunnable;
    private Context mStyledContext;
    
    public PreferenceFragment() {
        this.mDividerDecoration = new DividerDecoration();
        this.mLayoutResId = R$layout.preference_list_fragment;
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                if (message.what == 1) {
                    PreferenceFragment.this.bindPreferences();
                }
            }
        };
        this.mRequestFocus = new Runnable() {
            @Override
            public void run() {
                final RecyclerView mList = PreferenceFragment.this.mList;
                mList.focusableViewAvailable((View)mList);
            }
        };
    }
    
    private void postBindPreferences() {
        if (this.mHandler.hasMessages(1)) {
            return;
        }
        this.mHandler.obtainMessage(1).sendToTarget();
    }
    
    private void requirePreferenceManager() {
        if (this.mPreferenceManager != null) {
            return;
        }
        throw new RuntimeException("This should be called after super.onCreate.");
    }
    
    private void unbindPreferences() {
        final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.onDetached();
        }
        this.onUnbindPreferences();
    }
    
    @Deprecated
    public void addPreferencesFromResource(final int n) {
        this.requirePreferenceManager();
        this.setPreferenceScreen(this.mPreferenceManager.inflateFromResource(this.mStyledContext, n, this.getPreferenceScreen()));
    }
    
    void bindPreferences() {
        final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        if (preferenceScreen != null) {
            this.getListView().setAdapter(this.onCreateAdapter(preferenceScreen));
            preferenceScreen.onAttached();
        }
        this.onBindPreferences();
    }
    
    @Deprecated
    public <T extends Preference> T findPreference(final CharSequence charSequence) {
        final PreferenceManager mPreferenceManager = this.mPreferenceManager;
        if (mPreferenceManager == null) {
            return null;
        }
        return (T)mPreferenceManager.findPreference(charSequence);
    }
    
    public Fragment getCallbackFragment() {
        return null;
    }
    
    @Deprecated
    public final RecyclerView getListView() {
        return this.mList;
    }
    
    @Deprecated
    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }
    
    @Deprecated
    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceManager.getPreferenceScreen();
    }
    
    protected void onBindPreferences() {
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final TypedValue typedValue = new TypedValue();
        this.getActivity().getTheme().resolveAttribute(R$attr.preferenceTheme, typedValue, true);
        int n;
        if ((n = typedValue.resourceId) == 0) {
            n = R$style.PreferenceThemeOverlay;
        }
        final ContextThemeWrapper mStyledContext = new ContextThemeWrapper((Context)this.getActivity(), n);
        this.mStyledContext = (Context)mStyledContext;
        (this.mPreferenceManager = new PreferenceManager((Context)mStyledContext)).setOnNavigateToScreenListener((PreferenceManager.OnNavigateToScreenListener)this);
        String string;
        if (this.getArguments() != null) {
            string = this.getArguments().getString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT");
        }
        else {
            string = null;
        }
        this.onCreatePreferences(bundle, string);
    }
    
    @Deprecated
    protected RecyclerView.Adapter onCreateAdapter(final PreferenceScreen preferenceScreen) {
        return new PreferenceGroupAdapter(preferenceScreen);
    }
    
    @Deprecated
    public RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager((Context)this.getActivity());
    }
    
    @Deprecated
    public abstract void onCreatePreferences(final Bundle p0, final String p1);
    
    @Deprecated
    public RecyclerView onCreateRecyclerView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        if (this.mStyledContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            final RecyclerView recyclerView = (RecyclerView)viewGroup.findViewById(R$id.recycler_view);
            if (recyclerView != null) {
                return recyclerView;
            }
        }
        final RecyclerView recyclerView2 = (RecyclerView)layoutInflater.inflate(R$layout.preference_recyclerview, viewGroup, false);
        recyclerView2.setLayoutManager(this.onCreateLayoutManager());
        recyclerView2.setAccessibilityDelegateCompat(new PreferenceRecyclerViewAccessibilityDelegate(recyclerView2));
        return recyclerView2;
    }
    
    public View onCreateView(LayoutInflater cloneInContext, final ViewGroup viewGroup, final Bundle bundle) {
        final Context mStyledContext = this.mStyledContext;
        final TypedArray obtainStyledAttributes = mStyledContext.obtainStyledAttributes((AttributeSet)null, R$styleable.PreferenceFragment, TypedArrayUtils.getAttr(mStyledContext, R$attr.preferenceFragmentStyle, 16844038), 0);
        this.mLayoutResId = obtainStyledAttributes.getResourceId(R$styleable.PreferenceFragment_android_layout, this.mLayoutResId);
        final Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.PreferenceFragment_android_divider);
        final int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.PreferenceFragment_android_dividerHeight, -1);
        final boolean boolean1 = obtainStyledAttributes.getBoolean(R$styleable.PreferenceFragment_allowDividerAfterLastItem, true);
        obtainStyledAttributes.recycle();
        cloneInContext = cloneInContext.cloneInContext(this.mStyledContext);
        final View inflate = cloneInContext.inflate(this.mLayoutResId, viewGroup, false);
        final View viewById = inflate.findViewById(16908351);
        if (!(viewById instanceof ViewGroup)) {
            throw new RuntimeException("Content has view with id attribute 'android.R.id.list_container' that is not a ViewGroup class");
        }
        final ViewGroup viewGroup2 = (ViewGroup)viewById;
        final RecyclerView onCreateRecyclerView = this.onCreateRecyclerView(cloneInContext, viewGroup2, bundle);
        if (onCreateRecyclerView != null) {
            (this.mList = onCreateRecyclerView).addItemDecoration((RecyclerView.ItemDecoration)this.mDividerDecoration);
            this.setDivider(drawable);
            if (dimensionPixelSize != -1) {
                this.setDividerHeight(dimensionPixelSize);
            }
            this.mDividerDecoration.setAllowDividerAfterLastItem(boolean1);
            if (this.mList.getParent() == null) {
                viewGroup2.addView((View)this.mList);
            }
            this.mHandler.post(this.mRequestFocus);
            return inflate;
        }
        throw new RuntimeException("Could not create RecyclerView");
    }
    
    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mHandler.removeMessages(1);
        if (this.mHavePrefs) {
            this.unbindPreferences();
        }
        this.mList = null;
        super.onDestroyView();
    }
    
    @Deprecated
    public void onDisplayPreferenceDialog(final Preference preference) {
        boolean onPreferenceDisplayDialog;
        final boolean b = onPreferenceDisplayDialog = (this.getCallbackFragment() instanceof OnPreferenceDisplayDialogCallback && ((OnPreferenceDisplayDialogCallback)this.getCallbackFragment()).onPreferenceDisplayDialog(this, preference));
        if (!b) {
            onPreferenceDisplayDialog = b;
            if (this.getActivity() instanceof OnPreferenceDisplayDialogCallback) {
                onPreferenceDisplayDialog = ((OnPreferenceDisplayDialogCallback)this.getActivity()).onPreferenceDisplayDialog(this, preference);
            }
        }
        if (onPreferenceDisplayDialog) {
            return;
        }
        if (this.getFragmentManager().findFragmentByTag("androidx.preference.PreferenceFragment.DIALOG") != null) {
            return;
        }
        PreferenceDialogFragment preferenceDialogFragment;
        if (preference instanceof EditTextPreference) {
            preferenceDialogFragment = EditTextPreferenceDialogFragment.newInstance(preference.getKey());
        }
        else if (preference instanceof ListPreference) {
            preferenceDialogFragment = ListPreferenceDialogFragment.newInstance(preference.getKey());
        }
        else {
            if (!(preference instanceof MultiSelectListPreference)) {
                throw new IllegalArgumentException("Tried to display dialog for unknown preference type. Did you forget to override onDisplayPreferenceDialog()?");
            }
            preferenceDialogFragment = MultiSelectListPreferenceDialogFragment.newInstance(preference.getKey());
        }
        preferenceDialogFragment.setTargetFragment((Fragment)this, 0);
        preferenceDialogFragment.show(this.getFragmentManager(), "androidx.preference.PreferenceFragment.DIALOG");
    }
    
    @Deprecated
    public void onNavigateToScreen(final PreferenceScreen preferenceScreen) {
        if ((!(this.getCallbackFragment() instanceof OnPreferenceStartScreenCallback) || !((OnPreferenceStartScreenCallback)this.getCallbackFragment()).onPreferenceStartScreen(this, preferenceScreen)) && this.getActivity() instanceof OnPreferenceStartScreenCallback) {
            ((OnPreferenceStartScreenCallback)this.getActivity()).onPreferenceStartScreen(this, preferenceScreen);
        }
    }
    
    @Deprecated
    public boolean onPreferenceTreeClick(final Preference preference) {
        final String fragment = preference.getFragment();
        int onPreferenceStartFragment = 0;
        int onPreferenceStartFragment2 = 0;
        if (fragment != null) {
            if (this.getCallbackFragment() instanceof OnPreferenceStartFragmentCallback) {
                onPreferenceStartFragment2 = (((OnPreferenceStartFragmentCallback)this.getCallbackFragment()).onPreferenceStartFragment(this, preference) ? 1 : 0);
            }
            onPreferenceStartFragment = onPreferenceStartFragment2;
            if (onPreferenceStartFragment2 == 0) {
                onPreferenceStartFragment = onPreferenceStartFragment2;
                if (this.getActivity() instanceof OnPreferenceStartFragmentCallback) {
                    onPreferenceStartFragment = (((OnPreferenceStartFragmentCallback)this.getActivity()).onPreferenceStartFragment(this, preference) ? 1 : 0);
                }
            }
        }
        return onPreferenceStartFragment != 0;
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        if (preferenceScreen != null) {
            final Bundle bundle2 = new Bundle();
            preferenceScreen.saveHierarchyState(bundle2);
            bundle.putBundle("android:preferences", bundle2);
        }
    }
    
    public void onStart() {
        super.onStart();
        this.mPreferenceManager.setOnPreferenceTreeClickListener((PreferenceManager.OnPreferenceTreeClickListener)this);
        this.mPreferenceManager.setOnDisplayPreferenceDialogListener((PreferenceManager.OnDisplayPreferenceDialogListener)this);
    }
    
    public void onStop() {
        super.onStop();
        this.mPreferenceManager.setOnPreferenceTreeClickListener(null);
        this.mPreferenceManager.setOnDisplayPreferenceDialogListener(null);
    }
    
    protected void onUnbindPreferences() {
    }
    
    public void onViewCreated(final View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (bundle != null) {
            bundle = bundle.getBundle("android:preferences");
            if (bundle != null) {
                final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.restoreHierarchyState(bundle);
                }
            }
        }
        if (this.mHavePrefs) {
            this.bindPreferences();
            final Runnable mSelectPreferenceRunnable = this.mSelectPreferenceRunnable;
            if (mSelectPreferenceRunnable != null) {
                mSelectPreferenceRunnable.run();
                this.mSelectPreferenceRunnable = null;
            }
        }
        this.mInitDone = true;
    }
    
    @Deprecated
    public void setDivider(final Drawable divider) {
        this.mDividerDecoration.setDivider(divider);
    }
    
    @Deprecated
    public void setDividerHeight(final int dividerHeight) {
        this.mDividerDecoration.setDividerHeight(dividerHeight);
    }
    
    @Deprecated
    public void setPreferenceScreen(final PreferenceScreen preferences) {
        if (this.mPreferenceManager.setPreferences(preferences) && preferences != null) {
            this.onUnbindPreferences();
            this.mHavePrefs = true;
            if (this.mInitDone) {
                this.postBindPreferences();
            }
        }
    }
    
    private class DividerDecoration extends ItemDecoration
    {
        private boolean mAllowDividerAfterLastItem;
        private Drawable mDivider;
        private int mDividerHeight;
        
        DividerDecoration() {
            this.mAllowDividerAfterLastItem = true;
        }
        
        private boolean shouldDrawDividerBelow(final View view, final RecyclerView recyclerView) {
            final RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(view);
            final boolean b = childViewHolder instanceof PreferenceViewHolder;
            final boolean b2 = false;
            if (!b || !((PreferenceViewHolder)childViewHolder).isDividerAllowedBelow()) {
                return false;
            }
            boolean mAllowDividerAfterLastItem = this.mAllowDividerAfterLastItem;
            final int indexOfChild = recyclerView.indexOfChild(view);
            if (indexOfChild < recyclerView.getChildCount() - 1) {
                final RecyclerView.ViewHolder childViewHolder2 = recyclerView.getChildViewHolder(recyclerView.getChildAt(indexOfChild + 1));
                mAllowDividerAfterLastItem = b2;
                if (childViewHolder2 instanceof PreferenceViewHolder) {
                    mAllowDividerAfterLastItem = b2;
                    if (((PreferenceViewHolder)childViewHolder2).isDividerAllowedAbove()) {
                        mAllowDividerAfterLastItem = true;
                    }
                }
            }
            return mAllowDividerAfterLastItem;
        }
        
        @Override
        public void getItemOffsets(final Rect rect, final View view, final RecyclerView recyclerView, final State state) {
            if (this.shouldDrawDividerBelow(view, recyclerView)) {
                rect.bottom = this.mDividerHeight;
            }
        }
        
        @Override
        public void onDrawOver(final Canvas canvas, final RecyclerView recyclerView, final State state) {
            if (this.mDivider == null) {
                return;
            }
            final int childCount = recyclerView.getChildCount();
            final int width = recyclerView.getWidth();
            for (int i = 0; i < childCount; ++i) {
                final View child = recyclerView.getChildAt(i);
                if (this.shouldDrawDividerBelow(child, recyclerView)) {
                    final int n = (int)child.getY() + child.getHeight();
                    this.mDivider.setBounds(0, n, width, this.mDividerHeight + n);
                    this.mDivider.draw(canvas);
                }
            }
        }
        
        public void setAllowDividerAfterLastItem(final boolean mAllowDividerAfterLastItem) {
            this.mAllowDividerAfterLastItem = mAllowDividerAfterLastItem;
        }
        
        public void setDivider(final Drawable mDivider) {
            if (mDivider != null) {
                this.mDividerHeight = mDivider.getIntrinsicHeight();
            }
            else {
                this.mDividerHeight = 0;
            }
            this.mDivider = mDivider;
            PreferenceFragment.this.mList.invalidateItemDecorations();
        }
        
        public void setDividerHeight(final int mDividerHeight) {
            this.mDividerHeight = mDividerHeight;
            PreferenceFragment.this.mList.invalidateItemDecorations();
        }
    }
    
    public interface OnPreferenceDisplayDialogCallback
    {
        boolean onPreferenceDisplayDialog(final PreferenceFragment p0, final Preference p1);
    }
    
    public interface OnPreferenceStartFragmentCallback
    {
        boolean onPreferenceStartFragment(final PreferenceFragment p0, final Preference p1);
    }
    
    public interface OnPreferenceStartScreenCallback
    {
        boolean onPreferenceStartScreen(final PreferenceFragment p0, final PreferenceScreen p1);
    }
}
