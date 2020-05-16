// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.inputmethod;

import com.android.internal.annotations.VisibleForTesting;
import android.text.TextUtils;
import java.util.Locale;
import android.content.Context;

public class InputMethodSubtypePreference extends SwitchWithNoTextPreference
{
    private final boolean mIsSystemLocale;
    
    @VisibleForTesting
    InputMethodSubtypePreference(final Context context, final String key, final CharSequence title, final Locale locale, final Locale obj) {
        super(context);
        this.setPersistent(false);
        this.setKey(key);
        this.setTitle(title);
        if (locale == null) {
            this.mIsSystemLocale = false;
        }
        else if (!(this.mIsSystemLocale = locale.equals(obj))) {
            TextUtils.equals((CharSequence)locale.getLanguage(), (CharSequence)obj.getLanguage());
        }
    }
}
