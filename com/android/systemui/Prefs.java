// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.SharedPreferences$OnSharedPreferenceChangeListener;
import java.util.Set;
import java.util.Map;
import android.content.SharedPreferences;
import android.content.Context;

public final class Prefs
{
    public static SharedPreferences get(final Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0);
    }
    
    public static Map<String, ?> getAll(final Context context) {
        return (Map<String, ?>)get(context).getAll();
    }
    
    public static boolean getBoolean(final Context context, final String s, final boolean b) {
        return get(context).getBoolean(s, b);
    }
    
    public static int getInt(final Context context, final String s, final int n) {
        return get(context).getInt(s, n);
    }
    
    public static Set<String> getStringSet(final Context context, final String s, final Set<String> set) {
        return (Set<String>)get(context).getStringSet(s, (Set)set);
    }
    
    public static void putBoolean(final Context context, final String s, final boolean b) {
        get(context).edit().putBoolean(s, b).apply();
    }
    
    public static void putInt(final Context context, final String s, final int n) {
        get(context).edit().putInt(s, n).apply();
    }
    
    public static void putStringSet(final Context context, final String s, final Set<String> set) {
        get(context).edit().putStringSet(s, (Set)set).apply();
    }
    
    public static void registerListener(final Context context, final SharedPreferences$OnSharedPreferenceChangeListener sharedPreferences$OnSharedPreferenceChangeListener) {
        get(context).registerOnSharedPreferenceChangeListener(sharedPreferences$OnSharedPreferenceChangeListener);
    }
    
    public static void remove(final Context context, final String s) {
        get(context).edit().remove(s).apply();
    }
    
    public static void unregisterListener(final Context context, final SharedPreferences$OnSharedPreferenceChangeListener sharedPreferences$OnSharedPreferenceChangeListener) {
        get(context).unregisterOnSharedPreferenceChangeListener(sharedPreferences$OnSharedPreferenceChangeListener);
    }
}
