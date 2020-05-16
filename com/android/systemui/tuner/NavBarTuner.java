// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import java.util.function.Consumer;
import com.android.systemui.R$xml;
import android.os.Bundle;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.graphics.drawable.Icon;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.content.DialogInterface$OnClickListener;
import android.view.View;
import android.app.AlertDialog$Builder;
import android.content.DialogInterface;
import android.widget.EditText;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import com.android.systemui.Dependency;
import com.android.systemui.R$string;
import com.android.systemui.R$drawable;
import java.util.ArrayList;
import android.os.Handler;

public class NavBarTuner extends TunerPreferenceFragment
{
    private static final int[][] ICONS;
    private Handler mHandler;
    private final ArrayList<TunerService.Tunable> mTunables;
    
    static {
        ICONS = new int[][] { { R$drawable.ic_qs_circle, R$string.tuner_circle }, { R$drawable.ic_add, R$string.tuner_plus }, { R$drawable.ic_remove, R$string.tuner_minus }, { R$drawable.ic_left, R$string.tuner_left }, { R$drawable.ic_right, R$string.tuner_right }, { R$drawable.ic_menu, R$string.tuner_menu } };
    }
    
    public NavBarTuner() {
        this.mTunables = new ArrayList<TunerService.Tunable>();
    }
    
    private void addTunable(final TunerService.Tunable e, final String... array) {
        this.mTunables.add(e);
        Dependency.get(TunerService.class).addTunable(e, array);
    }
    
    private void bindButton(final String s, final String s2, final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("type_");
        sb.append(str);
        final ListPreference listPreference = this.findPreference(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("keycode_");
        sb2.append(str);
        final Preference preference = this.findPreference(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("icon_");
        sb3.append(str);
        final ListPreference listPreference2 = this.findPreference(sb3.toString());
        this.setupIcons(listPreference2);
        this.addTunable(new _$$Lambda$NavBarTuner$AtqwC3eDMLXM8PvQu0SrBbBcxZQ(this, s2, listPreference, listPreference2, preference), s);
        final _$$Lambda$NavBarTuner$5vkJoJwaFUhdGZ7Fp4qtkLVqooo $$Lambda$NavBarTuner$5vkJoJwaFUhdGZ7Fp4qtkLVqooo = new _$$Lambda$NavBarTuner$5vkJoJwaFUhdGZ7Fp4qtkLVqooo(this, s, listPreference, preference, listPreference2);
        listPreference.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener)$$Lambda$NavBarTuner$5vkJoJwaFUhdGZ7Fp4qtkLVqooo);
        listPreference2.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener)$$Lambda$NavBarTuner$5vkJoJwaFUhdGZ7Fp4qtkLVqooo);
        preference.setOnPreferenceClickListener((Preference.OnPreferenceClickListener)new _$$Lambda$NavBarTuner$VEefG8gxDDp8OSjE4w47bWNl4eQ(this, preference, s, listPreference, listPreference2));
    }
    
    private void bindLayout(final ListPreference listPreference) {
        this.addTunable(new _$$Lambda$NavBarTuner$nx5Q7aHowvZ9Bevy96_zeYYIxAY(this, listPreference), "sysui_nav_bar");
        listPreference.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener)_$$Lambda$NavBarTuner$xJajVHN9uODpq3muoNpXW6_uxwc.INSTANCE);
    }
    
    private void setValue(final String s, ListPreference str, final Preference preference, final ListPreference listPreference) {
        final ListPreference listPreference2 = str = (ListPreference)str.getValue();
        Label_0104: {
            if (!"key".equals(listPreference2)) {
                break Label_0104;
            }
            str = (ListPreference)listPreference.getValue();
            int int1 = 66;
            while (true) {
                try {
                    int1 = Integer.parseInt(preference.getSummary().toString());
                    final StringBuilder sb = new StringBuilder();
                    sb.append((String)listPreference2);
                    sb.append("(");
                    sb.append(int1);
                    sb.append(":");
                    sb.append((String)str);
                    sb.append(")");
                    str = (ListPreference)sb.toString();
                    Dependency.get(TunerService.class).setValue(s, (String)str);
                }
                catch (Exception ex) {
                    continue;
                }
                break;
            }
        }
    }
    
    private void setupIcons(final ListPreference listPreference) {
        final int[][] icons = NavBarTuner.ICONS;
        final CharSequence[] entries = new CharSequence[icons.length];
        final CharSequence[] entryValues = new CharSequence[icons.length];
        final int n = (int)TypedValue.applyDimension(1, 14.0f, this.getContext().getResources().getDisplayMetrics());
        for (int i = 0; i < icons.length; ++i) {
            final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            final Drawable loadDrawable = Icon.createWithResource(this.getContext().getPackageName(), icons[i][0]).loadDrawable(this.getContext());
            loadDrawable.setTint(-16777216);
            loadDrawable.setBounds(0, 0, n, n);
            spannableStringBuilder.append((CharSequence)"  ", (Object)new ImageSpan(loadDrawable, 1), 0);
            spannableStringBuilder.append((CharSequence)" ");
            spannableStringBuilder.append((CharSequence)this.getString(icons[i][1]));
            entries[i] = (CharSequence)spannableStringBuilder;
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getContext().getPackageName());
            sb.append("/");
            sb.append(icons[i][0]);
            entryValues[i] = sb.toString();
        }
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }
    
    private void updateSummary(final ListPreference listPreference) {
        final int[][] icons = NavBarTuner.ICONS;
        try {
            final int n = (int)TypedValue.applyDimension(1, 14.0f, this.getContext().getResources().getDisplayMetrics());
            final String s = listPreference.getValue().split("/")[0];
            final int int1 = Integer.parseInt(listPreference.getValue().split("/")[1]);
            final SpannableStringBuilder summary = new SpannableStringBuilder();
            final Drawable loadDrawable = Icon.createWithResource(s, int1).loadDrawable(this.getContext());
            loadDrawable.setTint(-16777216);
            loadDrawable.setBounds(0, 0, n, n);
            summary.append((CharSequence)"  ", (Object)new ImageSpan(loadDrawable, 1), 0);
            summary.append((CharSequence)" ");
            for (int i = 0; i < icons.length; ++i) {
                if (icons[i][0] == int1) {
                    summary.append((CharSequence)this.getString(icons[i][1]));
                }
            }
            listPreference.setSummary((CharSequence)summary);
        }
        catch (Exception ex) {
            Log.d("NavButton", "Problem with summary", (Throwable)ex);
            listPreference.setSummary(null);
        }
    }
    
    public void onActivityCreated(final Bundle bundle) {
        super.onActivityCreated(bundle);
        this.getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        this.mHandler = new Handler();
        super.onCreate(bundle);
    }
    
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        this.addPreferencesFromResource(R$xml.nav_bar_tuner);
        this.bindLayout(this.findPreference("layout"));
        this.bindButton("sysui_nav_bar_left", "space", "left");
        this.bindButton("sysui_nav_bar_right", "menu_ime", "right");
    }
    
    public void onDestroy() {
        super.onDestroy();
        this.mTunables.forEach((Consumer<? super TunerService.Tunable>)_$$Lambda$NavBarTuner$tsKQ8HfwaDSvc3iDCsgHsW954hc.INSTANCE);
    }
}
