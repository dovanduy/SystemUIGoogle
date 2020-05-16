// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import androidx.preference.PreferenceViewHolder;
import com.android.systemui.Dependency;
import android.os.Process;
import android.content.pm.LauncherApps;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import android.os.Bundle;
import java.util.List;
import android.content.pm.PackageManager$NameNotFoundException;
import java.util.function.Consumer;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import android.content.pm.LauncherActivityInfo;
import android.content.Context;
import java.util.ArrayList;
import androidx.preference.PreferenceFragment;

public class ShortcutPicker extends PreferenceFragment implements Tunable
{
    private String mKey;
    private SelectablePreference mNonePreference;
    private final ArrayList<SelectablePreference> mSelectablePreferences;
    private TunerService mTunerService;
    
    public ShortcutPicker() {
        this.mSelectablePreferences = new ArrayList<SelectablePreference>();
    }
    
    public void onActivityCreated(final Bundle bundle) {
        super.onActivityCreated(bundle);
        if ("sysui_keyguard_left".equals(this.mKey)) {
            this.getActivity().setTitle(R$string.lockscreen_shortcut_left);
        }
        else {
            this.getActivity().setTitle(R$string.lockscreen_shortcut_right);
        }
    }
    
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        final Context context = this.getPreferenceManager().getContext();
        final PreferenceScreen preferenceScreen = this.getPreferenceManager().createPreferenceScreen(context);
        preferenceScreen.setOrderingAsAdded(true);
        final PreferenceCategory preferenceCategory = new PreferenceCategory(context);
        preferenceCategory.setTitle(R$string.tuner_other_apps);
        final SelectablePreference selectablePreference = new SelectablePreference(context);
        this.mNonePreference = selectablePreference;
        this.mSelectablePreferences.add(selectablePreference);
        this.mNonePreference.setTitle(R$string.lockscreen_none);
        this.mNonePreference.setIcon(R$drawable.ic_remove_circle);
        preferenceScreen.addPreference(this.mNonePreference);
        final List activityList = ((LauncherApps)this.getContext().getSystemService((Class)LauncherApps.class)).getActivityList((String)null, Process.myUserHandle());
        preferenceScreen.addPreference(preferenceCategory);
        activityList.forEach(new _$$Lambda$ShortcutPicker$S1ImsFAQfzG0QWSEvA0DqvnEIeY(this, context, preferenceScreen, preferenceCategory));
        preferenceScreen.removePreference(preferenceCategory);
        for (int i = 0; i < preferenceCategory.getPreferenceCount(); ++i) {
            final Preference preference = preferenceCategory.getPreference(0);
            preferenceCategory.removePreference(preference);
            preference.setOrder(Integer.MAX_VALUE);
            preferenceScreen.addPreference(preference);
        }
        this.setPreferenceScreen(preferenceScreen);
        this.mKey = this.getArguments().getString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT");
        (this.mTunerService = Dependency.get(TunerService.class)).addTunable((TunerService.Tunable)this, this.mKey);
    }
    
    public void onDestroy() {
        super.onDestroy();
        this.mTunerService.removeTunable((TunerService.Tunable)this);
    }
    
    @Override
    public boolean onPreferenceTreeClick(final Preference preference) {
        this.mTunerService.setValue(this.mKey, preference.toString());
        this.getActivity().onBackPressed();
        return true;
    }
    
    @Override
    public void onTuningChanged(final String s, String s2) {
        if (s2 == null) {
            s2 = "";
        }
        this.mSelectablePreferences.forEach(new _$$Lambda$ShortcutPicker$i1fIZ726bN_ySXwulncRN12T1Qg(s2));
    }
    
    private static class AppPreference extends SelectablePreference
    {
        private boolean mBinding;
        private final LauncherActivityInfo mInfo;
        
        public AppPreference(final Context context, final LauncherActivityInfo mInfo) {
            super(context);
            this.mInfo = mInfo;
            this.setTitle(context.getString(R$string.tuner_launch_app, new Object[] { mInfo.getLabel() }));
            this.setSummary(context.getString(R$string.tuner_app, new Object[] { mInfo.getLabel() }));
        }
        
        @Override
        protected void notifyChanged() {
            if (this.mBinding) {
                return;
            }
            super.notifyChanged();
        }
        
        @Override
        public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
            this.mBinding = true;
            if (this.getIcon() == null) {
                this.setIcon(this.mInfo.getBadgedIcon(this.getContext().getResources().getConfiguration().densityDpi));
            }
            this.mBinding = false;
            super.onBindViewHolder(preferenceViewHolder);
        }
        
        @Override
        public String toString() {
            return this.mInfo.getComponentName().flattenToString();
        }
    }
    
    private static class ShortcutPreference extends SelectablePreference
    {
        private boolean mBinding;
        private final ShortcutParser.Shortcut mShortcut;
        
        public ShortcutPreference(final Context context, final ShortcutParser.Shortcut mShortcut, final CharSequence charSequence) {
            super(context);
            this.mShortcut = mShortcut;
            this.setTitle(mShortcut.label);
            this.setSummary(context.getString(R$string.tuner_app, new Object[] { charSequence }));
        }
        
        @Override
        protected void notifyChanged() {
            if (this.mBinding) {
                return;
            }
            super.notifyChanged();
        }
        
        @Override
        public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
            this.mBinding = true;
            if (this.getIcon() == null) {
                this.setIcon(this.mShortcut.icon.loadDrawable(this.getContext()));
            }
            this.mBinding = false;
            super.onBindViewHolder(preferenceViewHolder);
        }
        
        @Override
        public String toString() {
            return this.mShortcut.toString();
        }
    }
}
