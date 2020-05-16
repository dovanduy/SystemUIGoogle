// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.MenuItem;
import android.view.ContextMenu$ContextMenuInfo;
import android.view.ContextMenu;
import android.view.MenuItem$OnMenuItemClickListener;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.view.AbsSavedState;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.ViewCompat;
import android.view.View$OnCreateContextMenuListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;
import java.util.Set;
import androidx.appcompat.content.res.AppCompatResources;
import android.os.Parcelable;
import android.content.SharedPreferences$Editor;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.text.TextUtils;
import android.content.res.TypedArray;
import android.view.View;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import java.util.List;
import android.content.Context;
import android.view.View$OnClickListener;

public class Preference implements Comparable<Preference>
{
    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private boolean mBaseMethodCalled;
    private final View$OnClickListener mClickListener;
    private Context mContext;
    private boolean mCopyingEnabled;
    private Object mDefaultValue;
    private String mDependencyKey;
    private boolean mDependencyMet;
    private List<Preference> mDependents;
    private boolean mEnabled;
    private Bundle mExtras;
    private String mFragment;
    private boolean mHasId;
    private boolean mHasSingleLineTitleAttr;
    private Drawable mIcon;
    private int mIconResId;
    private boolean mIconSpaceReserved;
    private long mId;
    private Intent mIntent;
    private String mKey;
    private int mLayoutResId;
    private OnPreferenceChangeInternalListener mListener;
    private OnPreferenceChangeListener mOnChangeListener;
    private OnPreferenceClickListener mOnClickListener;
    private OnPreferenceCopyListener mOnCopyListener;
    private int mOrder;
    private boolean mParentDependencyMet;
    private PreferenceGroup mParentGroup;
    private boolean mPersistent;
    private PreferenceDataStore mPreferenceDataStore;
    private PreferenceManager mPreferenceManager;
    private boolean mRequiresKey;
    private boolean mSelectable;
    private boolean mShouldDisableView;
    private boolean mSingleLineTitle;
    private CharSequence mSummary;
    private SummaryProvider mSummaryProvider;
    private CharSequence mTitle;
    private int mViewId;
    private boolean mVisible;
    private int mWidgetLayoutResId;
    
    public Preference(final Context context) {
        this(context, null);
    }
    
    public Preference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.preferenceStyle, 16842894));
    }
    
    public Preference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public Preference(final Context mContext, final AttributeSet set, int n, final int n2) {
        this.mOrder = Integer.MAX_VALUE;
        this.mViewId = 0;
        this.mEnabled = true;
        this.mSelectable = true;
        this.mPersistent = true;
        this.mDependencyMet = true;
        this.mParentDependencyMet = true;
        this.mVisible = true;
        this.mAllowDividerAbove = true;
        this.mAllowDividerBelow = true;
        this.mSingleLineTitle = true;
        this.mShouldDisableView = true;
        this.mLayoutResId = R$layout.preference;
        this.mClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                Preference.this.performClick(view);
            }
        };
        this.mContext = mContext;
        final TypedArray obtainStyledAttributes = mContext.obtainStyledAttributes(set, R$styleable.Preference, n, n2);
        this.mIconResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R$styleable.Preference_icon, R$styleable.Preference_android_icon, 0);
        this.mKey = TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.Preference_key, R$styleable.Preference_android_key);
        this.mTitle = TypedArrayUtils.getText(obtainStyledAttributes, R$styleable.Preference_title, R$styleable.Preference_android_title);
        this.mSummary = TypedArrayUtils.getText(obtainStyledAttributes, R$styleable.Preference_summary, R$styleable.Preference_android_summary);
        this.mOrder = TypedArrayUtils.getInt(obtainStyledAttributes, R$styleable.Preference_order, R$styleable.Preference_android_order, Integer.MAX_VALUE);
        this.mFragment = TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.Preference_fragment, R$styleable.Preference_android_fragment);
        this.mLayoutResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R$styleable.Preference_layout, R$styleable.Preference_android_layout, R$layout.preference);
        this.mWidgetLayoutResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R$styleable.Preference_widgetLayout, R$styleable.Preference_android_widgetLayout, 0);
        this.mEnabled = TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.Preference_enabled, R$styleable.Preference_android_enabled, true);
        this.mSelectable = TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.Preference_selectable, R$styleable.Preference_android_selectable, true);
        this.mPersistent = TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.Preference_persistent, R$styleable.Preference_android_persistent, true);
        this.mDependencyKey = TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.Preference_dependency, R$styleable.Preference_android_dependency);
        n = R$styleable.Preference_allowDividerAbove;
        this.mAllowDividerAbove = TypedArrayUtils.getBoolean(obtainStyledAttributes, n, n, this.mSelectable);
        n = R$styleable.Preference_allowDividerBelow;
        this.mAllowDividerBelow = TypedArrayUtils.getBoolean(obtainStyledAttributes, n, n, this.mSelectable);
        if (obtainStyledAttributes.hasValue(R$styleable.Preference_defaultValue)) {
            this.mDefaultValue = this.onGetDefaultValue(obtainStyledAttributes, R$styleable.Preference_defaultValue);
        }
        else if (obtainStyledAttributes.hasValue(R$styleable.Preference_android_defaultValue)) {
            this.mDefaultValue = this.onGetDefaultValue(obtainStyledAttributes, R$styleable.Preference_android_defaultValue);
        }
        this.mShouldDisableView = TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.Preference_shouldDisableView, R$styleable.Preference_android_shouldDisableView, true);
        final boolean hasValue = obtainStyledAttributes.hasValue(R$styleable.Preference_singleLineTitle);
        this.mHasSingleLineTitleAttr = hasValue;
        if (hasValue) {
            this.mSingleLineTitle = TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.Preference_singleLineTitle, R$styleable.Preference_android_singleLineTitle, true);
        }
        this.mIconSpaceReserved = TypedArrayUtils.getBoolean(obtainStyledAttributes, R$styleable.Preference_iconSpaceReserved, R$styleable.Preference_android_iconSpaceReserved, false);
        n = R$styleable.Preference_isPreferenceVisible;
        this.mVisible = TypedArrayUtils.getBoolean(obtainStyledAttributes, n, n, true);
        n = R$styleable.Preference_enableCopying;
        this.mCopyingEnabled = TypedArrayUtils.getBoolean(obtainStyledAttributes, n, n, false);
        obtainStyledAttributes.recycle();
    }
    
    private void dispatchSetInitialValue() {
        if (this.getPreferenceDataStore() != null) {
            this.onSetInitialValue(true, this.mDefaultValue);
            return;
        }
        if (this.shouldPersist() && this.getSharedPreferences().contains(this.mKey)) {
            this.onSetInitialValue(true, null);
        }
        else {
            final Object mDefaultValue = this.mDefaultValue;
            if (mDefaultValue != null) {
                this.onSetInitialValue(false, mDefaultValue);
            }
        }
    }
    
    private void registerDependency() {
        if (TextUtils.isEmpty((CharSequence)this.mDependencyKey)) {
            return;
        }
        final Preference preferenceInHierarchy = this.findPreferenceInHierarchy(this.mDependencyKey);
        if (preferenceInHierarchy != null) {
            preferenceInHierarchy.registerDependent(this);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Dependency \"");
        sb.append(this.mDependencyKey);
        sb.append("\" not found for preference \"");
        sb.append(this.mKey);
        sb.append("\" (title: \"");
        sb.append((Object)this.mTitle);
        sb.append("\"");
        throw new IllegalStateException(sb.toString());
    }
    
    private void registerDependent(final Preference preference) {
        if (this.mDependents == null) {
            this.mDependents = new ArrayList<Preference>();
        }
        this.mDependents.add(preference);
        preference.onDependencyChanged(this, this.shouldDisableDependents());
    }
    
    private void setEnabledStateOnViews(final View view, final boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                this.setEnabledStateOnViews(viewGroup.getChildAt(i), enabled);
            }
        }
    }
    
    private void tryCommit(final SharedPreferences$Editor sharedPreferences$Editor) {
        if (this.mPreferenceManager.shouldCommit()) {
            sharedPreferences$Editor.apply();
        }
    }
    
    private void unregisterDependency() {
        final String mDependencyKey = this.mDependencyKey;
        if (mDependencyKey != null) {
            final Preference preferenceInHierarchy = this.findPreferenceInHierarchy(mDependencyKey);
            if (preferenceInHierarchy != null) {
                preferenceInHierarchy.unregisterDependent(this);
            }
        }
    }
    
    private void unregisterDependent(final Preference preference) {
        final List<Preference> mDependents = this.mDependents;
        if (mDependents != null) {
            mDependents.remove(preference);
        }
    }
    
    void assignParent(final PreferenceGroup mParentGroup) {
        if (mParentGroup != null && this.mParentGroup != null) {
            throw new IllegalStateException("This preference already has a parent. You must remove the existing parent before assigning a new one.");
        }
        this.mParentGroup = mParentGroup;
    }
    
    public boolean callChangeListener(final Object o) {
        final OnPreferenceChangeListener mOnChangeListener = this.mOnChangeListener;
        return mOnChangeListener == null || mOnChangeListener.onPreferenceChange(this, o);
    }
    
    final void clearWasDetached() {
    }
    
    @Override
    public int compareTo(final Preference preference) {
        final int mOrder = this.mOrder;
        final int mOrder2 = preference.mOrder;
        if (mOrder != mOrder2) {
            return mOrder - mOrder2;
        }
        final CharSequence mTitle = this.mTitle;
        final CharSequence mTitle2 = preference.mTitle;
        if (mTitle == mTitle2) {
            return 0;
        }
        if (mTitle == null) {
            return 1;
        }
        if (mTitle2 == null) {
            return -1;
        }
        return mTitle.toString().compareToIgnoreCase(preference.mTitle.toString());
    }
    
    void dispatchRestoreInstanceState(final Bundle bundle) {
        if (this.hasKey()) {
            final Parcelable parcelable = bundle.getParcelable(this.mKey);
            if (parcelable != null) {
                this.mBaseMethodCalled = false;
                this.onRestoreInstanceState(parcelable);
                if (!this.mBaseMethodCalled) {
                    throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
                }
            }
        }
    }
    
    void dispatchSaveInstanceState(final Bundle bundle) {
        if (this.hasKey()) {
            this.mBaseMethodCalled = false;
            final Parcelable onSaveInstanceState = this.onSaveInstanceState();
            if (!this.mBaseMethodCalled) {
                throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
            }
            if (onSaveInstanceState != null) {
                bundle.putParcelable(this.mKey, onSaveInstanceState);
            }
        }
    }
    
    protected <T extends Preference> T findPreferenceInHierarchy(final String s) {
        final PreferenceManager mPreferenceManager = this.mPreferenceManager;
        if (mPreferenceManager == null) {
            return null;
        }
        return (T)mPreferenceManager.findPreference(s);
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    public Bundle getExtras() {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        return this.mExtras;
    }
    
    StringBuilder getFilterableStringBuilder() {
        final StringBuilder sb = new StringBuilder();
        final CharSequence title = this.getTitle();
        if (!TextUtils.isEmpty(title)) {
            sb.append(title);
            sb.append(' ');
        }
        final CharSequence summary = this.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            sb.append(summary);
            sb.append(' ');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb;
    }
    
    public String getFragment() {
        return this.mFragment;
    }
    
    public Drawable getIcon() {
        if (this.mIcon == null) {
            final int mIconResId = this.mIconResId;
            if (mIconResId != 0) {
                this.mIcon = AppCompatResources.getDrawable(this.mContext, mIconResId);
            }
        }
        return this.mIcon;
    }
    
    long getId() {
        return this.mId;
    }
    
    public Intent getIntent() {
        return this.mIntent;
    }
    
    public String getKey() {
        return this.mKey;
    }
    
    public final int getLayoutResource() {
        return this.mLayoutResId;
    }
    
    public int getOrder() {
        return this.mOrder;
    }
    
    public PreferenceGroup getParent() {
        return this.mParentGroup;
    }
    
    protected boolean getPersistedBoolean(final boolean b) {
        if (!this.shouldPersist()) {
            return b;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            return preferenceDataStore.getBoolean(this.mKey, b);
        }
        return this.mPreferenceManager.getSharedPreferences().getBoolean(this.mKey, b);
    }
    
    protected int getPersistedInt(final int n) {
        if (!this.shouldPersist()) {
            return n;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            return preferenceDataStore.getInt(this.mKey, n);
        }
        return this.mPreferenceManager.getSharedPreferences().getInt(this.mKey, n);
    }
    
    protected String getPersistedString(final String s) {
        if (!this.shouldPersist()) {
            return s;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            return preferenceDataStore.getString(this.mKey, s);
        }
        return this.mPreferenceManager.getSharedPreferences().getString(this.mKey, s);
    }
    
    public Set<String> getPersistedStringSet(final Set<String> set) {
        if (!this.shouldPersist()) {
            return set;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            return preferenceDataStore.getStringSet(this.mKey, set);
        }
        return (Set<String>)this.mPreferenceManager.getSharedPreferences().getStringSet(this.mKey, (Set)set);
    }
    
    public PreferenceDataStore getPreferenceDataStore() {
        final PreferenceDataStore mPreferenceDataStore = this.mPreferenceDataStore;
        if (mPreferenceDataStore != null) {
            return mPreferenceDataStore;
        }
        final PreferenceManager mPreferenceManager = this.mPreferenceManager;
        if (mPreferenceManager != null) {
            return mPreferenceManager.getPreferenceDataStore();
        }
        return null;
    }
    
    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }
    
    public SharedPreferences getSharedPreferences() {
        if (this.mPreferenceManager != null && this.getPreferenceDataStore() == null) {
            return this.mPreferenceManager.getSharedPreferences();
        }
        return null;
    }
    
    public CharSequence getSummary() {
        if (this.getSummaryProvider() != null) {
            return this.getSummaryProvider().provideSummary(this);
        }
        return this.mSummary;
    }
    
    public final SummaryProvider getSummaryProvider() {
        return this.mSummaryProvider;
    }
    
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    public final int getWidgetLayoutResource() {
        return this.mWidgetLayoutResId;
    }
    
    public boolean hasKey() {
        return TextUtils.isEmpty((CharSequence)this.mKey) ^ true;
    }
    
    public boolean isCopyingEnabled() {
        return this.mCopyingEnabled;
    }
    
    public boolean isEnabled() {
        return this.mEnabled && this.mDependencyMet && this.mParentDependencyMet;
    }
    
    public boolean isPersistent() {
        return this.mPersistent;
    }
    
    public boolean isSelectable() {
        return this.mSelectable;
    }
    
    public final boolean isVisible() {
        return this.mVisible;
    }
    
    protected void notifyChanged() {
        final OnPreferenceChangeInternalListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onPreferenceChange(this);
        }
    }
    
    public void notifyDependencyChange(final boolean b) {
        final List<Preference> mDependents = this.mDependents;
        if (mDependents == null) {
            return;
        }
        for (int size = mDependents.size(), i = 0; i < size; ++i) {
            mDependents.get(i).onDependencyChanged(this, b);
        }
    }
    
    protected void notifyHierarchyChanged() {
        final OnPreferenceChangeInternalListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onPreferenceHierarchyChange(this);
        }
    }
    
    public void onAttached() {
        this.registerDependency();
    }
    
    protected void onAttachedToHierarchy(final PreferenceManager mPreferenceManager) {
        this.mPreferenceManager = mPreferenceManager;
        if (!this.mHasId) {
            this.mId = mPreferenceManager.getNextId();
        }
        this.dispatchSetInitialValue();
    }
    
    protected void onAttachedToHierarchy(final PreferenceManager preferenceManager, final long mId) {
        this.mId = mId;
        this.mHasId = true;
        try {
            this.onAttachedToHierarchy(preferenceManager);
        }
        finally {
            this.mHasId = false;
        }
    }
    
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        final View itemView = preferenceViewHolder.itemView;
        itemView.setOnClickListener(this.mClickListener);
        itemView.setId(this.mViewId);
        final TextView textView = (TextView)preferenceViewHolder.findViewById(16908304);
        final int n = 8;
        Integer value = null;
        Label_0086: {
            if (textView != null) {
                final CharSequence summary = this.getSummary();
                if (!TextUtils.isEmpty(summary)) {
                    textView.setText(summary);
                    textView.setVisibility(0);
                    value = textView.getCurrentTextColor();
                    break Label_0086;
                }
                textView.setVisibility(8);
            }
            value = null;
        }
        final TextView textView2 = (TextView)preferenceViewHolder.findViewById(16908310);
        if (textView2 != null) {
            final CharSequence title = this.getTitle();
            if (!TextUtils.isEmpty(title)) {
                textView2.setText(title);
                textView2.setVisibility(0);
                if (this.mHasSingleLineTitleAttr) {
                    textView2.setSingleLine(this.mSingleLineTitle);
                }
                if (!this.isSelectable() && this.isEnabled() && value != null) {
                    textView2.setTextColor((int)value);
                }
            }
            else {
                textView2.setVisibility(8);
            }
        }
        final ImageView imageView = (ImageView)preferenceViewHolder.findViewById(16908294);
        if (imageView != null) {
            if (this.mIconResId != 0 || this.mIcon != null) {
                if (this.mIcon == null) {
                    this.mIcon = AppCompatResources.getDrawable(this.mContext, this.mIconResId);
                }
                final Drawable mIcon = this.mIcon;
                if (mIcon != null) {
                    imageView.setImageDrawable(mIcon);
                }
            }
            if (this.mIcon != null) {
                imageView.setVisibility(0);
            }
            else {
                int visibility;
                if (this.mIconSpaceReserved) {
                    visibility = 4;
                }
                else {
                    visibility = 8;
                }
                imageView.setVisibility(visibility);
            }
        }
        View view;
        if ((view = preferenceViewHolder.findViewById(R$id.icon_frame)) == null) {
            view = preferenceViewHolder.findViewById(16908350);
        }
        if (view != null) {
            if (this.mIcon != null) {
                view.setVisibility(0);
            }
            else {
                int visibility2 = n;
                if (this.mIconSpaceReserved) {
                    visibility2 = 4;
                }
                view.setVisibility(visibility2);
            }
        }
        if (this.mShouldDisableView) {
            this.setEnabledStateOnViews(itemView, this.isEnabled());
        }
        else {
            this.setEnabledStateOnViews(itemView, true);
        }
        final boolean selectable = this.isSelectable();
        itemView.setFocusable(selectable);
        itemView.setClickable(selectable);
        preferenceViewHolder.setDividerAllowedAbove(this.mAllowDividerAbove);
        preferenceViewHolder.setDividerAllowedBelow(this.mAllowDividerBelow);
        final boolean copyingEnabled = this.isCopyingEnabled();
        if (copyingEnabled && this.mOnCopyListener == null) {
            this.mOnCopyListener = new OnPreferenceCopyListener(this);
        }
        Object mOnCopyListener;
        if (copyingEnabled) {
            mOnCopyListener = this.mOnCopyListener;
        }
        else {
            mOnCopyListener = null;
        }
        itemView.setOnCreateContextMenuListener((View$OnCreateContextMenuListener)mOnCopyListener);
        itemView.setLongClickable(copyingEnabled);
        if (copyingEnabled && !selectable) {
            ViewCompat.setBackground(itemView, null);
        }
    }
    
    protected void onClick() {
    }
    
    public void onDependencyChanged(final Preference preference, final boolean b) {
        if (this.mDependencyMet == b) {
            this.mDependencyMet = (b ^ true);
            this.notifyDependencyChange(this.shouldDisableDependents());
            this.notifyChanged();
        }
    }
    
    public void onDetached() {
        this.unregisterDependency();
    }
    
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return null;
    }
    
    @Deprecated
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
    }
    
    public void onParentChanged(final Preference preference, final boolean b) {
        if (this.mParentDependencyMet == b) {
            this.mParentDependencyMet = (b ^ true);
            this.notifyDependencyChange(this.shouldDisableDependents());
            this.notifyChanged();
        }
    }
    
    protected void onPrepareForRemoval() {
        this.unregisterDependency();
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        this.mBaseMethodCalled = true;
        if (parcelable != AbsSavedState.EMPTY_STATE && parcelable != null) {
            throw new IllegalArgumentException("Wrong state class -- expecting Preference State");
        }
    }
    
    protected Parcelable onSaveInstanceState() {
        this.mBaseMethodCalled = true;
        return (Parcelable)AbsSavedState.EMPTY_STATE;
    }
    
    protected void onSetInitialValue(final Object o) {
    }
    
    @Deprecated
    protected void onSetInitialValue(final boolean b, final Object o) {
        this.onSetInitialValue(o);
    }
    
    public void performClick() {
        if (this.isEnabled()) {
            if (this.isSelectable()) {
                this.onClick();
                final OnPreferenceClickListener mOnClickListener = this.mOnClickListener;
                if (mOnClickListener != null && mOnClickListener.onPreferenceClick(this)) {
                    return;
                }
                final PreferenceManager preferenceManager = this.getPreferenceManager();
                if (preferenceManager != null) {
                    final PreferenceManager.OnPreferenceTreeClickListener onPreferenceTreeClickListener = preferenceManager.getOnPreferenceTreeClickListener();
                    if (onPreferenceTreeClickListener != null && onPreferenceTreeClickListener.onPreferenceTreeClick(this)) {
                        return;
                    }
                }
                if (this.mIntent != null) {
                    this.getContext().startActivity(this.mIntent);
                }
            }
        }
    }
    
    protected void performClick(final View view) {
        this.performClick();
    }
    
    protected boolean persistBoolean(final boolean b) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (b == this.getPersistedBoolean(b ^ true)) {
            return true;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.putBoolean(this.mKey, b);
        }
        else {
            final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
            editor.putBoolean(this.mKey, b);
            this.tryCommit(editor);
        }
        return true;
    }
    
    protected boolean persistInt(final int n) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (n == this.getPersistedInt(n)) {
            return true;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.putInt(this.mKey, n);
        }
        else {
            final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
            editor.putInt(this.mKey, n);
            this.tryCommit(editor);
        }
        return true;
    }
    
    protected boolean persistString(final String s) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (TextUtils.equals((CharSequence)s, (CharSequence)this.getPersistedString(null))) {
            return true;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.putString(this.mKey, s);
        }
        else {
            final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
            editor.putString(this.mKey, s);
            this.tryCommit(editor);
        }
        return true;
    }
    
    public boolean persistStringSet(final Set<String> set) {
        if (!this.shouldPersist()) {
            return false;
        }
        if (set.equals(this.getPersistedStringSet(null))) {
            return true;
        }
        final PreferenceDataStore preferenceDataStore = this.getPreferenceDataStore();
        if (preferenceDataStore != null) {
            preferenceDataStore.putStringSet(this.mKey, set);
        }
        else {
            final SharedPreferences$Editor editor = this.mPreferenceManager.getEditor();
            editor.putStringSet(this.mKey, (Set)set);
            this.tryCommit(editor);
        }
        return true;
    }
    
    void requireKey() {
        if (!TextUtils.isEmpty((CharSequence)this.mKey)) {
            this.mRequiresKey = true;
            return;
        }
        throw new IllegalStateException("Preference does not have a key assigned.");
    }
    
    public void restoreHierarchyState(final Bundle bundle) {
        this.dispatchRestoreInstanceState(bundle);
    }
    
    public void saveHierarchyState(final Bundle bundle) {
        this.dispatchSaveInstanceState(bundle);
    }
    
    public void setEnabled(final boolean mEnabled) {
        if (this.mEnabled != mEnabled) {
            this.mEnabled = mEnabled;
            this.notifyDependencyChange(this.shouldDisableDependents());
            this.notifyChanged();
        }
    }
    
    public void setIcon(final int mIconResId) {
        this.setIcon(AppCompatResources.getDrawable(this.mContext, mIconResId));
        this.mIconResId = mIconResId;
    }
    
    public void setIcon(final Drawable mIcon) {
        if (this.mIcon != mIcon) {
            this.mIcon = mIcon;
            this.mIconResId = 0;
            this.notifyChanged();
        }
    }
    
    public void setIconSpaceReserved(final boolean mIconSpaceReserved) {
        if (this.mIconSpaceReserved != mIconSpaceReserved) {
            this.mIconSpaceReserved = mIconSpaceReserved;
            this.notifyChanged();
        }
    }
    
    public void setIntent(final Intent mIntent) {
        this.mIntent = mIntent;
    }
    
    public void setKey(final String mKey) {
        this.mKey = mKey;
        if (this.mRequiresKey && !this.hasKey()) {
            this.requireKey();
        }
    }
    
    public void setLayoutResource(final int mLayoutResId) {
        this.mLayoutResId = mLayoutResId;
    }
    
    final void setOnPreferenceChangeInternalListener(final OnPreferenceChangeInternalListener mListener) {
        this.mListener = mListener;
    }
    
    public void setOnPreferenceChangeListener(final OnPreferenceChangeListener mOnChangeListener) {
        this.mOnChangeListener = mOnChangeListener;
    }
    
    public void setOnPreferenceClickListener(final OnPreferenceClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
    
    public void setOrder(final int mOrder) {
        if (mOrder != this.mOrder) {
            this.mOrder = mOrder;
            this.notifyHierarchyChanged();
        }
    }
    
    public void setPersistent(final boolean mPersistent) {
        this.mPersistent = mPersistent;
    }
    
    public void setSelectable(final boolean mSelectable) {
        if (this.mSelectable != mSelectable) {
            this.mSelectable = mSelectable;
            this.notifyChanged();
        }
    }
    
    public void setShouldDisableView(final boolean mShouldDisableView) {
        if (this.mShouldDisableView != mShouldDisableView) {
            this.mShouldDisableView = mShouldDisableView;
            this.notifyChanged();
        }
    }
    
    public void setSummary(final int n) {
        this.setSummary(this.mContext.getString(n));
    }
    
    public void setSummary(final CharSequence mSummary) {
        if (this.getSummaryProvider() == null) {
            if (!TextUtils.equals(this.mSummary, mSummary)) {
                this.mSummary = mSummary;
                this.notifyChanged();
            }
            return;
        }
        throw new IllegalStateException("Preference already has a SummaryProvider set.");
    }
    
    public final void setSummaryProvider(final SummaryProvider mSummaryProvider) {
        this.mSummaryProvider = mSummaryProvider;
        this.notifyChanged();
    }
    
    public void setTitle(final int n) {
        this.setTitle(this.mContext.getString(n));
    }
    
    public void setTitle(final CharSequence mTitle) {
        if (!TextUtils.equals(mTitle, this.mTitle)) {
            this.mTitle = mTitle;
            this.notifyChanged();
        }
    }
    
    public final void setVisible(final boolean mVisible) {
        if (this.mVisible != mVisible) {
            this.mVisible = mVisible;
            final OnPreferenceChangeInternalListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onPreferenceVisibilityChange(this);
            }
        }
    }
    
    public void setWidgetLayoutResource(final int mWidgetLayoutResId) {
        this.mWidgetLayoutResId = mWidgetLayoutResId;
    }
    
    public boolean shouldDisableDependents() {
        return this.isEnabled() ^ true;
    }
    
    protected boolean shouldPersist() {
        return this.mPreferenceManager != null && this.isPersistent() && this.hasKey();
    }
    
    @Override
    public String toString() {
        return this.getFilterableStringBuilder().toString();
    }
    
    public static class BaseSavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<BaseSavedState> CREATOR;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<BaseSavedState>() {
                public BaseSavedState createFromParcel(final Parcel parcel) {
                    return new BaseSavedState(parcel);
                }
                
                public BaseSavedState[] newArray(final int n) {
                    return new BaseSavedState[n];
                }
            };
        }
        
        public BaseSavedState(final Parcel parcel) {
            super(parcel);
        }
        
        public BaseSavedState(final Parcelable parcelable) {
            super(parcelable);
        }
    }
    
    interface OnPreferenceChangeInternalListener
    {
        void onPreferenceChange(final Preference p0);
        
        void onPreferenceHierarchyChange(final Preference p0);
        
        void onPreferenceVisibilityChange(final Preference p0);
    }
    
    public interface OnPreferenceChangeListener
    {
        boolean onPreferenceChange(final Preference p0, final Object p1);
    }
    
    public interface OnPreferenceClickListener
    {
        boolean onPreferenceClick(final Preference p0);
    }
    
    private static class OnPreferenceCopyListener implements View$OnCreateContextMenuListener, MenuItem$OnMenuItemClickListener
    {
        private final Preference mPreference;
        
        OnPreferenceCopyListener(final Preference mPreference) {
            this.mPreference = mPreference;
        }
        
        public void onCreateContextMenu(final ContextMenu contextMenu, final View view, final ContextMenu$ContextMenuInfo contextMenu$ContextMenuInfo) {
            final CharSequence summary = this.mPreference.getSummary();
            if (this.mPreference.isCopyingEnabled()) {
                if (!TextUtils.isEmpty(summary)) {
                    contextMenu.setHeaderTitle(summary);
                    contextMenu.add(0, 0, 0, R$string.copy).setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)this);
                }
            }
        }
        
        public boolean onMenuItemClick(final MenuItem menuItem) {
            final ClipboardManager clipboardManager = (ClipboardManager)this.mPreference.getContext().getSystemService("clipboard");
            final CharSequence summary = this.mPreference.getSummary();
            clipboardManager.setPrimaryClip(ClipData.newPlainText((CharSequence)"Preference", summary));
            Toast.makeText(this.mPreference.getContext(), (CharSequence)this.mPreference.getContext().getString(R$string.preference_copied, new Object[] { summary }), 0).show();
            return true;
        }
    }
    
    public interface SummaryProvider<T extends Preference>
    {
        CharSequence provideSummary(final T p0);
    }
}
