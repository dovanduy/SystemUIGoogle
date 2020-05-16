// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.text.TextUtils;
import androidx.recyclerview.widget.DiffUtil;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import androidx.core.view.ViewCompat;
import androidx.appcompat.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;

public class PreferenceGroupAdapter extends Adapter<PreferenceViewHolder> implements OnPreferenceChangeInternalListener, Object
{
    private Handler mHandler;
    private PreferenceGroup mPreferenceGroup;
    private List<PreferenceResourceDescriptor> mPreferenceResourceDescriptors;
    private List<Preference> mPreferences;
    private Runnable mSyncRunnable;
    private List<Preference> mVisiblePreferences;
    
    public PreferenceGroupAdapter(PreferenceGroup mPreferenceGroup) {
        this.mSyncRunnable = new Runnable() {
            @Override
            public void run() {
                PreferenceGroupAdapter.this.updatePreferences();
            }
        };
        this.mPreferenceGroup = mPreferenceGroup;
        this.mHandler = new Handler();
        this.mPreferenceGroup.setOnPreferenceChangeInternalListener((Preference.OnPreferenceChangeInternalListener)this);
        this.mPreferences = new ArrayList<Preference>();
        this.mVisiblePreferences = new ArrayList<Preference>();
        this.mPreferenceResourceDescriptors = new ArrayList<PreferenceResourceDescriptor>();
        mPreferenceGroup = this.mPreferenceGroup;
        if (mPreferenceGroup instanceof PreferenceScreen) {
            ((RecyclerView.Adapter)this).setHasStableIds(((PreferenceScreen)mPreferenceGroup).shouldUseGeneratedIds());
        }
        else {
            ((RecyclerView.Adapter)this).setHasStableIds(true);
        }
        this.updatePreferences();
    }
    
    private ExpandButton createExpandButton(final PreferenceGroup preferenceGroup, final List<Preference> list) {
        final ExpandButton expandButton = new ExpandButton(preferenceGroup.getContext(), list, preferenceGroup.getId());
        expandButton.setOnPreferenceClickListener((Preference.OnPreferenceClickListener)new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                preferenceGroup.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
                PreferenceGroupAdapter.this.onPreferenceHierarchyChange(preference);
                final PreferenceGroup.OnExpandButtonClickListener onExpandButtonClickListener = preferenceGroup.getOnExpandButtonClickListener();
                if (onExpandButtonClickListener != null) {
                    onExpandButtonClickListener.onExpandButtonClick();
                }
                return true;
            }
        });
        return expandButton;
    }
    
    private List<Preference> createVisiblePreferencesList(final PreferenceGroup preferenceGroup) {
        final ArrayList<Preference> list = new ArrayList<Preference>();
        final ArrayList<ExpandButton> list2 = new ArrayList<ExpandButton>();
        final int preferenceCount = preferenceGroup.getPreferenceCount();
        int i = 0;
        int n = 0;
        while (i < preferenceCount) {
            final Preference preference = preferenceGroup.getPreference(i);
            if (preference.isVisible()) {
                if (this.isGroupExpandable(preferenceGroup) && n >= preferenceGroup.getInitialExpandedChildrenCount()) {
                    list2.add((ExpandButton)preference);
                }
                else {
                    list.add(preference);
                }
                if (!(preference instanceof PreferenceGroup)) {
                    ++n;
                }
                else {
                    final PreferenceGroup preferenceGroup2 = (PreferenceGroup)preference;
                    if (preferenceGroup2.isOnSameScreenAsChildren()) {
                        if (this.isGroupExpandable(preferenceGroup) && this.isGroupExpandable(preferenceGroup2)) {
                            throw new IllegalStateException("Nesting an expandable group inside of another expandable group is not supported!");
                        }
                        final Iterator<Preference> iterator = this.createVisiblePreferencesList(preferenceGroup2).iterator();
                        int n2 = n;
                        while (true) {
                            n = n2;
                            if (!iterator.hasNext()) {
                                break;
                            }
                            final Preference preference2 = iterator.next();
                            if (this.isGroupExpandable(preferenceGroup) && n2 >= preferenceGroup.getInitialExpandedChildrenCount()) {
                                list2.add((ExpandButton)preference2);
                            }
                            else {
                                list.add(preference2);
                            }
                            ++n2;
                        }
                    }
                }
            }
            ++i;
        }
        if (this.isGroupExpandable(preferenceGroup) && n > preferenceGroup.getInitialExpandedChildrenCount()) {
            list.add(this.createExpandButton(preferenceGroup, (List<Preference>)list2));
        }
        return list;
    }
    
    private void flattenPreferenceGroup(final List<Preference> list, final PreferenceGroup preferenceGroup) {
        preferenceGroup.sortPreferences();
        for (int preferenceCount = preferenceGroup.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            final Preference preference = preferenceGroup.getPreference(i);
            list.add(preference);
            final PreferenceResourceDescriptor preferenceResourceDescriptor = new PreferenceResourceDescriptor(preference);
            if (!this.mPreferenceResourceDescriptors.contains(preferenceResourceDescriptor)) {
                this.mPreferenceResourceDescriptors.add(preferenceResourceDescriptor);
            }
            if (preference instanceof PreferenceGroup) {
                final PreferenceGroup preferenceGroup2 = (PreferenceGroup)preference;
                if (preferenceGroup2.isOnSameScreenAsChildren()) {
                    this.flattenPreferenceGroup(list, preferenceGroup2);
                }
            }
            preference.setOnPreferenceChangeInternalListener((Preference.OnPreferenceChangeInternalListener)this);
        }
    }
    
    private boolean isGroupExpandable(final PreferenceGroup preferenceGroup) {
        return preferenceGroup.getInitialExpandedChildrenCount() != Integer.MAX_VALUE;
    }
    
    public Preference getItem(final int n) {
        if (n >= 0 && n < this.getItemCount()) {
            return this.mVisiblePreferences.get(n);
        }
        return null;
    }
    
    @Override
    public int getItemCount() {
        return this.mVisiblePreferences.size();
    }
    
    @Override
    public long getItemId(final int n) {
        if (!((RecyclerView.Adapter)this).hasStableIds()) {
            return -1L;
        }
        return this.getItem(n).getId();
    }
    
    @Override
    public int getItemViewType(int n) {
        final PreferenceResourceDescriptor preferenceResourceDescriptor = new PreferenceResourceDescriptor(this.getItem(n));
        n = this.mPreferenceResourceDescriptors.indexOf(preferenceResourceDescriptor);
        if (n != -1) {
            return n;
        }
        n = this.mPreferenceResourceDescriptors.size();
        this.mPreferenceResourceDescriptors.add(preferenceResourceDescriptor);
        return n;
    }
    
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder, final int n) {
        final Preference item = this.getItem(n);
        preferenceViewHolder.resetState();
        item.onBindViewHolder(preferenceViewHolder);
    }
    
    public PreferenceViewHolder onCreateViewHolder(final ViewGroup viewGroup, int mWidgetLayoutResId) {
        final PreferenceResourceDescriptor preferenceResourceDescriptor = this.mPreferenceResourceDescriptors.get(mWidgetLayoutResId);
        final LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        final TypedArray obtainStyledAttributes = viewGroup.getContext().obtainStyledAttributes((AttributeSet)null, R$styleable.BackgroundStyle);
        Drawable drawable;
        if ((drawable = obtainStyledAttributes.getDrawable(R$styleable.BackgroundStyle_android_selectableItemBackground)) == null) {
            drawable = AppCompatResources.getDrawable(viewGroup.getContext(), 17301602);
        }
        obtainStyledAttributes.recycle();
        final View inflate = from.inflate(preferenceResourceDescriptor.mLayoutResId, viewGroup, false);
        if (inflate.getBackground() == null) {
            ViewCompat.setBackground(inflate, drawable);
        }
        final ViewGroup viewGroup2 = (ViewGroup)inflate.findViewById(16908312);
        if (viewGroup2 != null) {
            mWidgetLayoutResId = preferenceResourceDescriptor.mWidgetLayoutResId;
            if (mWidgetLayoutResId != 0) {
                from.inflate(mWidgetLayoutResId, viewGroup2);
            }
            else {
                viewGroup2.setVisibility(8);
            }
        }
        return new PreferenceViewHolder(inflate);
    }
    
    @Override
    public void onPreferenceChange(final Preference preference) {
        final int index = this.mVisiblePreferences.indexOf(preference);
        if (index != -1) {
            ((RecyclerView.Adapter)this).notifyItemChanged(index, preference);
        }
    }
    
    @Override
    public void onPreferenceHierarchyChange(final Preference preference) {
        this.mHandler.removeCallbacks(this.mSyncRunnable);
        this.mHandler.post(this.mSyncRunnable);
    }
    
    @Override
    public void onPreferenceVisibilityChange(final Preference preference) {
        this.onPreferenceHierarchyChange(preference);
    }
    
    void updatePreferences() {
        final Iterator<Preference> iterator = this.mPreferences.iterator();
        while (iterator.hasNext()) {
            iterator.next().setOnPreferenceChangeInternalListener(null);
        }
        this.flattenPreferenceGroup(this.mPreferences = new ArrayList<Preference>(this.mPreferences.size()), this.mPreferenceGroup);
        final List<Preference> mVisiblePreferences = this.mVisiblePreferences;
        final List<Preference> visiblePreferencesList = this.createVisiblePreferencesList(this.mPreferenceGroup);
        this.mVisiblePreferences = visiblePreferencesList;
        final PreferenceManager preferenceManager = this.mPreferenceGroup.getPreferenceManager();
        if (preferenceManager != null && preferenceManager.getPreferenceComparisonCallback() != null) {
            DiffUtil.calculateDiff((DiffUtil.Callback)new DiffUtil.Callback(this) {
                final /* synthetic */ PreferenceManager.PreferenceComparisonCallback val$comparisonCallback = preferenceManager.getPreferenceComparisonCallback();
                
                @Override
                public boolean areContentsTheSame(final int n, final int n2) {
                    return this.val$comparisonCallback.arePreferenceContentsTheSame(mVisiblePreferences.get(n), visiblePreferencesList.get(n2));
                }
                
                @Override
                public boolean areItemsTheSame(final int n, final int n2) {
                    return this.val$comparisonCallback.arePreferenceItemsTheSame(mVisiblePreferences.get(n), visiblePreferencesList.get(n2));
                }
                
                @Override
                public int getNewListSize() {
                    return visiblePreferencesList.size();
                }
                
                @Override
                public int getOldListSize() {
                    return mVisiblePreferences.size();
                }
            }).dispatchUpdatesTo(this);
        }
        else {
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
        final Iterator<Preference> iterator2 = this.mPreferences.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().clearWasDetached();
        }
    }
    
    private static class PreferenceResourceDescriptor
    {
        String mClassName;
        int mLayoutResId;
        int mWidgetLayoutResId;
        
        PreferenceResourceDescriptor(final Preference preference) {
            this.mClassName = preference.getClass().getName();
            this.mLayoutResId = preference.getLayoutResource();
            this.mWidgetLayoutResId = preference.getWidgetLayoutResource();
        }
        
        @Override
        public boolean equals(final Object o) {
            final boolean b = o instanceof PreferenceResourceDescriptor;
            final boolean b2 = false;
            if (!b) {
                return false;
            }
            final PreferenceResourceDescriptor preferenceResourceDescriptor = (PreferenceResourceDescriptor)o;
            boolean b3 = b2;
            if (this.mLayoutResId == preferenceResourceDescriptor.mLayoutResId) {
                b3 = b2;
                if (this.mWidgetLayoutResId == preferenceResourceDescriptor.mWidgetLayoutResId) {
                    b3 = b2;
                    if (TextUtils.equals((CharSequence)this.mClassName, (CharSequence)preferenceResourceDescriptor.mClassName)) {
                        b3 = true;
                    }
                }
            }
            return b3;
        }
        
        @Override
        public int hashCode() {
            return ((527 + this.mLayoutResId) * 31 + this.mWidgetLayoutResId) * 31 + this.mClassName.hashCode();
        }
    }
}
