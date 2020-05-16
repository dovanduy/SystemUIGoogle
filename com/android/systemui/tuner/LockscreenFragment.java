// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import java.util.Map;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.R$id;
import android.widget.TextView;
import android.widget.ImageView;
import com.android.systemui.statusbar.phone.ExpandableIndicator;
import android.content.pm.LauncherActivityInfo;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View$OnClickListener;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.Drawable;
import com.android.systemui.statusbar.ScalingDrawableWrapper;
import android.util.TypedValue;
import android.content.Intent;
import com.android.systemui.plugins.IntentButtonProvider.IntentButton;
import com.android.systemui.plugins.IntentButtonProvider;
import java.util.function.Consumer;
import com.android.systemui.R$xml;
import android.os.Handler;
import com.android.systemui.Dependency;
import android.os.Bundle;
import com.android.systemui.R$string;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.Context;
import java.util.ArrayList;
import androidx.preference.PreferenceFragment;

public class LockscreenFragment extends PreferenceFragment
{
    private final ArrayList<TunerService.Tunable> mTunables;
    private TunerService mTunerService;
    
    public LockscreenFragment() {
        this.mTunables = new ArrayList<TunerService.Tunable>();
    }
    
    private void addTunable(final TunerService.Tunable e, final String... array) {
        this.mTunables.add(e);
        this.mTunerService.addTunable(e, array);
    }
    
    public static ActivityInfo getActivityinfo(final Context context, final String s) {
        final ComponentName unflattenFromString = ComponentName.unflattenFromString(s);
        try {
            return context.getPackageManager().getActivityInfo(unflattenFromString, 0);
        }
        catch (PackageManager$NameNotFoundException ex) {
            return null;
        }
    }
    
    public static ShortcutParser.Shortcut getShortcutInfo(final Context context, final String s) {
        return ShortcutParser.Shortcut.create(context, s);
    }
    
    private void setSummary(final Preference preference, String label) {
        if (label == null) {
            preference.setSummary(R$string.lockscreen_none);
            return;
        }
        final boolean contains = label.contains("::");
        final CharSequence charSequence = null;
        final String s = null;
        if (contains) {
            final ShortcutParser.Shortcut shortcutInfo = getShortcutInfo(this.getContext(), label);
            label = s;
            if (shortcutInfo != null) {
                label = shortcutInfo.label;
            }
            preference.setSummary(label);
        }
        else if (label.contains("/")) {
            final ActivityInfo activityinfo = getActivityinfo(this.getContext(), label);
            CharSequence loadLabel = charSequence;
            if (activityinfo != null) {
                loadLabel = activityinfo.loadLabel(this.getContext().getPackageManager());
            }
            preference.setSummary(loadLabel);
        }
        else {
            preference.setSummary(R$string.lockscreen_none);
        }
    }
    
    private void setupGroup(final String s, final String s2) {
        this.addTunable(new _$$Lambda$LockscreenFragment$Obd464MAoJT5uRv3BJuc47igR_Y(this, this.findPreference(s2), this.findPreference(s)), s);
    }
    
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        this.mTunerService = Dependency.get(TunerService.class);
        new Handler();
        this.addPreferencesFromResource(R$xml.lockscreen_settings);
        this.setupGroup("sysui_keyguard_left", "sysui_keyguard_left_unlock");
        this.setupGroup("sysui_keyguard_right", "sysui_keyguard_right_unlock");
    }
    
    public void onDestroy() {
        super.onDestroy();
        this.mTunables.forEach(new _$$Lambda$LockscreenFragment$Lo7jOQgOiEZ4M1LxVUxyoD69g0s(this));
    }
    
    private static class ActivityButton implements IntentButton
    {
        private final IconState mIconState;
        private final Intent mIntent;
        
        public ActivityButton(final Context context, final ActivityInfo activityInfo) {
            this.mIntent = new Intent().setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
            final IconState mIconState = new IconState();
            this.mIconState = mIconState;
            mIconState.isVisible = true;
            mIconState.drawable = activityInfo.loadIcon(context.getPackageManager()).mutate();
            this.mIconState.contentDescription = activityInfo.loadLabel(context.getPackageManager());
            final int n = (int)TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
            final IconState mIconState2 = this.mIconState;
            final Drawable drawable = this.mIconState.drawable;
            mIconState2.drawable = (Drawable)new ScalingDrawableWrapper(drawable, n / (float)drawable.getIntrinsicWidth());
            this.mIconState.tint = false;
        }
        
        @Override
        public IconState getIcon() {
            return this.mIconState;
        }
        
        @Override
        public Intent getIntent() {
            return this.mIntent;
        }
    }
    
    public static class Adapter extends RecyclerView.Adapter<Holder>
    {
        private final Consumer<Item> mCallback;
        private ArrayList<Item> mItems;
        
        public void addItem(final Item o, final Item element) {
            int index = this.mItems.indexOf(o);
            final ArrayList<Item> mItems = this.mItems;
            ++index;
            mItems.add(index, element);
            ((RecyclerView.Adapter)this).notifyItemInserted(index);
        }
        
        @Override
        public int getItemCount() {
            return this.mItems.size();
        }
        
        public void onBindViewHolder(final Holder holder, final int index) {
            final Item item = this.mItems.get(index);
            holder.icon.setImageDrawable(item.getDrawable());
            holder.title.setText((CharSequence)item.getLabel());
            holder.itemView.setOnClickListener((View$OnClickListener)new _$$Lambda$LockscreenFragment$Adapter$VuIE2eL9_LHOyBflZw_Px7xwF04(this, holder));
            final Boolean expando = item.getExpando();
            if (expando != null) {
                holder.expand.setVisibility(0);
                holder.expand.setExpanded(expando);
                holder.expand.setOnClickListener((View$OnClickListener)new _$$Lambda$LockscreenFragment$Adapter$fS6IuUEavDgpMOkDZLNh46UcUNQ(this, holder));
            }
            else {
                holder.expand.setVisibility(8);
            }
        }
        
        public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
            return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.tuner_shortcut_item, viewGroup, false));
        }
        
        public void remItem(final Item item) {
            final int index = this.mItems.indexOf(item);
            this.mItems.remove(item);
            ((RecyclerView.Adapter)this).notifyItemRemoved(index);
        }
    }
    
    private static class App extends Item
    {
        private final ArrayList<Item> mChildren;
        private final Context mContext;
        private boolean mExpanded;
        private final LauncherActivityInfo mInfo;
        
        @Override
        public Drawable getDrawable() {
            return this.mInfo.getBadgedIcon(this.mContext.getResources().getConfiguration().densityDpi);
        }
        
        @Override
        public Boolean getExpando() {
            Boolean value;
            if (this.mChildren.size() != 0) {
                value = this.mExpanded;
            }
            else {
                value = null;
            }
            return value;
        }
        
        @Override
        public String getLabel() {
            return this.mInfo.getLabel().toString();
        }
        
        @Override
        public void toggleExpando(final Adapter adapter) {
            final boolean mExpanded = this.mExpanded ^ true;
            this.mExpanded = mExpanded;
            if (mExpanded) {
                this.mChildren.forEach(new _$$Lambda$LockscreenFragment$App$ETExpSuIeTllbJ9AB_3DTGOAJgk(this, adapter));
            }
            else {
                this.mChildren.forEach(new _$$Lambda$LockscreenFragment$App$KymmDZF_Q8mj0Qr5uc4akrkgskU(adapter));
            }
        }
    }
    
    public static class Holder extends ViewHolder
    {
        public final ExpandableIndicator expand;
        public final ImageView icon;
        public final TextView title;
        
        public Holder(final View view) {
            super(view);
            this.icon = (ImageView)view.findViewById(16908294);
            this.title = (TextView)view.findViewById(16908310);
            this.expand = (ExpandableIndicator)view.findViewById(R$id.expand);
        }
    }
    
    private abstract static class Item
    {
        public abstract Drawable getDrawable();
        
        public abstract Boolean getExpando();
        
        public abstract String getLabel();
        
        public void toggleExpando(final Adapter adapter) {
        }
    }
    
    public static class LockButtonFactory implements TunerFactory<IntentButton>
    {
        private final Context mContext;
        private final String mKey;
        
        public LockButtonFactory(final Context mContext, final String mKey) {
            this.mContext = mContext;
            this.mKey = mKey;
        }
        
        public IntentButton create(final Map<String, String> map) {
            final String s = map.get(this.mKey);
            if (!TextUtils.isEmpty((CharSequence)s)) {
                if (s.contains("::")) {
                    final ShortcutParser.Shortcut shortcutInfo = LockscreenFragment.getShortcutInfo(this.mContext, s);
                    if (shortcutInfo != null) {
                        return new ShortcutButton(this.mContext, shortcutInfo);
                    }
                }
                else if (s.contains("/")) {
                    final ActivityInfo activityinfo = LockscreenFragment.getActivityinfo(this.mContext, s);
                    if (activityinfo != null) {
                        return new ActivityButton(this.mContext, activityinfo);
                    }
                }
            }
            return null;
        }
        
        @Override
        public String[] keys() {
            return new String[] { this.mKey };
        }
    }
    
    private static class ShortcutButton implements IntentButton
    {
        private final IconState mIconState;
        private final ShortcutParser.Shortcut mShortcut;
        
        public ShortcutButton(final Context context, final ShortcutParser.Shortcut mShortcut) {
            this.mShortcut = mShortcut;
            final IconState mIconState = new IconState();
            this.mIconState = mIconState;
            mIconState.isVisible = true;
            mIconState.drawable = mShortcut.icon.loadDrawable(context).mutate();
            this.mIconState.contentDescription = this.mShortcut.label;
            final int n = (int)TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
            final IconState mIconState2 = this.mIconState;
            final Drawable drawable = this.mIconState.drawable;
            mIconState2.drawable = (Drawable)new ScalingDrawableWrapper(drawable, n / (float)drawable.getIntrinsicWidth());
            this.mIconState.tint = false;
        }
        
        @Override
        public IconState getIcon() {
            return this.mIconState;
        }
        
        @Override
        public Intent getIntent() {
            return this.mShortcut.intent;
        }
    }
    
    private static class StaticShortcut extends Item
    {
        private final Context mContext;
        private final ShortcutParser.Shortcut mShortcut;
        
        @Override
        public Drawable getDrawable() {
            return this.mShortcut.icon.loadDrawable(this.mContext);
        }
        
        @Override
        public Boolean getExpando() {
            return null;
        }
        
        @Override
        public String getLabel() {
            return this.mShortcut.label;
        }
    }
}
