// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.content.SharedPreferences;
import android.content.SharedPreferences$Editor;
import android.content.Context;

public class PreferenceManager
{
    private Context mContext;
    private SharedPreferences$Editor mEditor;
    private long mNextId;
    private boolean mNoCommit;
    private OnDisplayPreferenceDialogListener mOnDisplayPreferenceDialogListener;
    private OnNavigateToScreenListener mOnNavigateToScreenListener;
    private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
    private PreferenceComparisonCallback mPreferenceComparisonCallback;
    private PreferenceDataStore mPreferenceDataStore;
    private PreferenceScreen mPreferenceScreen;
    private SharedPreferences mSharedPreferences;
    private int mSharedPreferencesMode;
    private String mSharedPreferencesName;
    private int mStorage;
    
    public PreferenceManager(final Context mContext) {
        this.mNextId = 0L;
        this.mStorage = 0;
        this.mContext = mContext;
        this.setSharedPreferencesName(getDefaultSharedPreferencesName(mContext));
    }
    
    public static SharedPreferences getDefaultSharedPreferences(final Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode());
    }
    
    private static int getDefaultSharedPreferencesMode() {
        return 0;
    }
    
    private static String getDefaultSharedPreferencesName(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append(context.getPackageName());
        sb.append("_preferences");
        return sb.toString();
    }
    
    private void setNoCommit(final boolean mNoCommit) {
        if (!mNoCommit) {
            final SharedPreferences$Editor mEditor = this.mEditor;
            if (mEditor != null) {
                mEditor.apply();
            }
        }
        this.mNoCommit = mNoCommit;
    }
    
    public PreferenceScreen createPreferenceScreen(final Context context) {
        final PreferenceScreen preferenceScreen = new PreferenceScreen(context, null);
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }
    
    public <T extends Preference> T findPreference(final CharSequence charSequence) {
        final PreferenceScreen mPreferenceScreen = this.mPreferenceScreen;
        if (mPreferenceScreen == null) {
            return null;
        }
        return (T)mPreferenceScreen.findPreference(charSequence);
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    SharedPreferences$Editor getEditor() {
        if (this.mPreferenceDataStore != null) {
            return null;
        }
        if (this.mNoCommit) {
            if (this.mEditor == null) {
                this.mEditor = this.getSharedPreferences().edit();
            }
            return this.mEditor;
        }
        return this.getSharedPreferences().edit();
    }
    
    long getNextId() {
        synchronized (this) {
            final long mNextId = this.mNextId;
            this.mNextId = 1L + mNextId;
            return mNextId;
        }
    }
    
    public OnNavigateToScreenListener getOnNavigateToScreenListener() {
        return this.mOnNavigateToScreenListener;
    }
    
    public OnPreferenceTreeClickListener getOnPreferenceTreeClickListener() {
        return this.mOnPreferenceTreeClickListener;
    }
    
    public PreferenceComparisonCallback getPreferenceComparisonCallback() {
        return this.mPreferenceComparisonCallback;
    }
    
    public PreferenceDataStore getPreferenceDataStore() {
        return this.mPreferenceDataStore;
    }
    
    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceScreen;
    }
    
    public SharedPreferences getSharedPreferences() {
        if (this.getPreferenceDataStore() != null) {
            return null;
        }
        if (this.mSharedPreferences == null) {
            Context context;
            if (this.mStorage != 1) {
                context = this.mContext;
            }
            else {
                context = ContextCompat.createDeviceProtectedStorageContext(this.mContext);
            }
            this.mSharedPreferences = context.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
        }
        return this.mSharedPreferences;
    }
    
    public PreferenceScreen inflateFromResource(final Context context, final int n, final PreferenceScreen preferenceScreen) {
        this.setNoCommit(true);
        final PreferenceScreen preferenceScreen2 = (PreferenceScreen)new PreferenceInflater(context, this).inflate(n, preferenceScreen);
        preferenceScreen2.onAttachedToHierarchy(this);
        this.setNoCommit(false);
        return preferenceScreen2;
    }
    
    public void setOnDisplayPreferenceDialogListener(final OnDisplayPreferenceDialogListener mOnDisplayPreferenceDialogListener) {
        this.mOnDisplayPreferenceDialogListener = mOnDisplayPreferenceDialogListener;
    }
    
    public void setOnNavigateToScreenListener(final OnNavigateToScreenListener mOnNavigateToScreenListener) {
        this.mOnNavigateToScreenListener = mOnNavigateToScreenListener;
    }
    
    public void setOnPreferenceTreeClickListener(final OnPreferenceTreeClickListener mOnPreferenceTreeClickListener) {
        this.mOnPreferenceTreeClickListener = mOnPreferenceTreeClickListener;
    }
    
    public boolean setPreferences(final PreferenceScreen mPreferenceScreen) {
        final PreferenceScreen mPreferenceScreen2 = this.mPreferenceScreen;
        if (mPreferenceScreen != mPreferenceScreen2) {
            if (mPreferenceScreen2 != null) {
                mPreferenceScreen2.onDetached();
            }
            this.mPreferenceScreen = mPreferenceScreen;
            return true;
        }
        return false;
    }
    
    public void setSharedPreferencesName(final String mSharedPreferencesName) {
        this.mSharedPreferencesName = mSharedPreferencesName;
        this.mSharedPreferences = null;
    }
    
    boolean shouldCommit() {
        return this.mNoCommit ^ true;
    }
    
    public void showDialog(final Preference preference) {
        final OnDisplayPreferenceDialogListener mOnDisplayPreferenceDialogListener = this.mOnDisplayPreferenceDialogListener;
        if (mOnDisplayPreferenceDialogListener != null) {
            mOnDisplayPreferenceDialogListener.onDisplayPreferenceDialog(preference);
        }
    }
    
    public interface OnDisplayPreferenceDialogListener
    {
        void onDisplayPreferenceDialog(final Preference p0);
    }
    
    public interface OnNavigateToScreenListener
    {
        void onNavigateToScreen(final PreferenceScreen p0);
    }
    
    public interface OnPreferenceTreeClickListener
    {
        boolean onPreferenceTreeClick(final Preference p0);
    }
    
    public abstract static class PreferenceComparisonCallback
    {
        public abstract boolean arePreferenceContentsTheSame(final Preference p0, final Preference p1);
        
        public abstract boolean arePreferenceItemsTheSame(final Preference p0, final Preference p1);
    }
}
