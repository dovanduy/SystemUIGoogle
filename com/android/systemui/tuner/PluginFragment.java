// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.view.View$OnLongClickListener;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import androidx.preference.PreferenceViewHolder;
import android.net.Uri;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.content.ComponentName;
import com.android.systemui.R$layout;
import androidx.preference.SwitchPreference;
import com.android.systemui.plugins.PluginEnablerImpl;
import android.content.IntentFilter;
import android.os.Bundle;
import java.util.Iterator;
import java.util.Set;
import android.content.pm.PackageManager;
import java.util.function.Consumer;
import android.content.pm.ResolveInfo;
import com.android.systemui.Dependency;
import androidx.preference.Preference;
import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;
import android.content.pm.PackageInfo;
import androidx.preference.PreferenceScreen;
import com.android.systemui.shared.plugins.PluginManager;
import android.util.ArrayMap;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import com.android.systemui.shared.plugins.PluginPrefs;
import com.android.systemui.shared.plugins.PluginEnabler;
import androidx.preference.PreferenceFragment;

public class PluginFragment extends PreferenceFragment
{
    private PluginEnabler mPluginEnabler;
    private PluginPrefs mPluginPrefs;
    private final BroadcastReceiver mReceiver;
    
    public PluginFragment() {
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                PluginFragment.this.loadPrefs();
            }
        };
    }
    
    private void loadPrefs() {
        final PluginManager pluginManager = Dependency.get(PluginManager.class);
        final PreferenceScreen preferenceScreen = this.getPreferenceManager().createPreferenceScreen(this.getContext());
        preferenceScreen.setOrderingAsAdded(false);
        final Context context = this.getPreferenceManager().getContext();
        this.mPluginPrefs = new PluginPrefs(this.getContext());
        final PackageManager packageManager = this.getContext().getPackageManager();
        final Set<String> pluginList = this.mPluginPrefs.getPluginList();
        final ArrayMap arrayMap = new ArrayMap();
        for (final String s : pluginList) {
            final String name = this.toName(s);
            final Iterator iterator2 = packageManager.queryIntentServices(new Intent(s), 512).iterator();
            while (iterator2.hasNext()) {
                final String packageName = iterator2.next().serviceInfo.packageName;
                if (!arrayMap.containsKey((Object)packageName)) {
                    arrayMap.put((Object)packageName, (Object)new ArraySet());
                }
                ((ArraySet)arrayMap.get((Object)packageName)).add((Object)name);
            }
        }
        packageManager.getPackagesHoldingPermissions(new String[] { "com.android.systemui.permission.PLUGIN" }, 516).forEach(new _$$Lambda$PluginFragment$iW8kXrJfaof7fDZHqMxR_RNftYk(this, arrayMap, pluginManager, context, preferenceScreen));
        this.setPreferenceScreen(preferenceScreen);
    }
    
    private String toName(final String s) {
        final String replace = s.replace("com.android.systemui.action.PLUGIN_", "");
        final StringBuilder sb = new StringBuilder();
        for (final String s2 : replace.split("_")) {
            if (sb.length() != 0) {
                sb.append(' ');
            }
            sb.append(s2.substring(0, 1));
            sb.append(s2.substring(1).toLowerCase());
        }
        return sb.toString();
    }
    
    private String toString(final ArraySet<String> set) {
        final StringBuilder sb = new StringBuilder();
        for (final String str : set) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(str);
        }
        return sb.toString();
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        this.getContext().registerReceiver(this.mReceiver, intentFilter);
        this.getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
    }
    
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        this.mPluginEnabler = new PluginEnablerImpl(this.getContext());
        this.loadPrefs();
    }
    
    public void onDestroy() {
        super.onDestroy();
        this.getContext().unregisterReceiver(this.mReceiver);
    }
    
    private static class PluginPreference extends SwitchPreference
    {
        private final boolean mHasSettings;
        private final PackageInfo mInfo;
        private final PluginEnabler mPluginEnabler;
        
        public PluginPreference(final Context context, final PackageInfo mInfo, final PluginEnabler mPluginEnabler) {
            super(context);
            final PackageManager packageManager = context.getPackageManager();
            final Intent setPackage = new Intent("com.android.systemui.action.PLUGIN_SETTINGS").setPackage(mInfo.packageName);
            boolean mHasSettings = false;
            if (packageManager.resolveActivity(setPackage, 0) != null) {
                mHasSettings = true;
            }
            this.mHasSettings = mHasSettings;
            this.mInfo = mInfo;
            this.mPluginEnabler = mPluginEnabler;
            this.setTitle(mInfo.applicationInfo.loadLabel(packageManager));
            this.setChecked(this.isPluginEnabled());
            this.setWidgetLayoutResource(R$layout.tuner_widget_settings_switch);
        }
        
        private boolean isPluginEnabled() {
            for (int i = 0; i < this.mInfo.services.length; ++i) {
                final PackageInfo mInfo = this.mInfo;
                if (!this.mPluginEnabler.isEnabled(new ComponentName(mInfo.packageName, mInfo.services[i].name))) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            final View viewById = preferenceViewHolder.findViewById(R$id.settings);
            final boolean mHasSettings = this.mHasSettings;
            final int n = 0;
            int visibility;
            if (mHasSettings) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
            final View viewById2 = preferenceViewHolder.findViewById(R$id.divider);
            int visibility2;
            if (this.mHasSettings) {
                visibility2 = n;
            }
            else {
                visibility2 = 8;
            }
            viewById2.setVisibility(visibility2);
            preferenceViewHolder.findViewById(R$id.settings).setOnClickListener((View$OnClickListener)new _$$Lambda$PluginFragment$PluginPreference$Xt_y65tw1Tc7XykRWrNNbIDklTs(this));
            preferenceViewHolder.itemView.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$PluginFragment$PluginPreference$hyhKFHxbkbEXGxqXV7_N3Il_7XE(this));
        }
        
        @Override
        protected boolean persistBoolean(final boolean b) {
            int n = 0;
            boolean b2 = false;
            PackageInfo mInfo;
            while (true) {
                mInfo = this.mInfo;
                if (n >= mInfo.services.length) {
                    break;
                }
                final PackageInfo mInfo2 = this.mInfo;
                final ComponentName enabled = new ComponentName(mInfo2.packageName, mInfo2.services[n].name);
                if (this.mPluginEnabler.isEnabled(enabled) != b) {
                    if (b) {
                        this.mPluginEnabler.setEnabled(enabled);
                    }
                    else {
                        this.mPluginEnabler.setDisabled(enabled, 1);
                    }
                    b2 = true;
                }
                ++n;
            }
            if (b2) {
                final String packageName = mInfo.packageName;
                Uri fromParts = null;
                if (packageName != null) {
                    fromParts = Uri.fromParts("package", packageName, (String)null);
                }
                this.getContext().sendBroadcast(new Intent("com.android.systemui.action.PLUGIN_CHANGED", fromParts));
            }
            return true;
        }
    }
}
