// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import java.util.Iterator;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

final class ExpandButton extends Preference
{
    private long mId;
    
    ExpandButton(final Context context, final List<Preference> summary, final long n) {
        super(context);
        this.initLayout();
        this.setSummary(summary);
        this.mId = n + 1000000L;
    }
    
    private void initLayout() {
        this.setLayoutResource(R$layout.expand_button);
        this.setIcon(R$drawable.ic_arrow_down_24dp);
        this.setTitle(R$string.expand_button_title);
        this.setOrder(999);
    }
    
    private void setSummary(final List<Preference> list) {
        final ArrayList<PreferenceGroup> list2 = new ArrayList<PreferenceGroup>();
        final Iterator<Preference> iterator = list.iterator();
        CharSequence string = null;
        while (iterator.hasNext()) {
            final Preference preference = iterator.next();
            final CharSequence title = preference.getTitle();
            final boolean b = preference instanceof PreferenceGroup;
            if (b && !TextUtils.isEmpty(title)) {
                list2.add((PreferenceGroup)preference);
            }
            if (list2.contains(preference.getParent())) {
                if (!b) {
                    continue;
                }
                list2.add((PreferenceGroup)preference);
            }
            else {
                if (TextUtils.isEmpty(title)) {
                    continue;
                }
                if (string == null) {
                    string = title;
                }
                else {
                    string = this.getContext().getString(R$string.summary_collapsed_preference_list, new Object[] { string, title });
                }
            }
        }
        this.setSummary(string);
    }
    
    @Override
    long getId() {
        return this.mId;
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(false);
    }
}
