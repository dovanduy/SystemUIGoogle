// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import android.provider.Settings$Secure;
import org.json.JSONException;
import android.util.Log;
import org.json.JSONObject;
import com.android.internal.annotations.VisibleForTesting;
import android.content.ContentResolver;

public class SettingsWrapper
{
    private final ContentResolver mContentResolver;
    private final Migration mMigration;
    
    SettingsWrapper(final ContentResolver contentResolver) {
        this(contentResolver, (Migration)new Migrator(contentResolver));
    }
    
    @VisibleForTesting
    SettingsWrapper(final ContentResolver mContentResolver, final Migration mMigration) {
        this.mContentResolver = mContentResolver;
        this.mMigration = mMigration;
    }
    
    @VisibleForTesting
    String decode(String string, final int n) {
        if (string == null) {
            return (String)string;
        }
        try {
            final JSONObject jsonObject = new JSONObject((String)string);
            try {
                string = jsonObject.getString("clock");
                return (String)string;
            }
            catch (JSONException string) {
                Log.e("ClockFaceSettings", "JSON object does not contain clock field.", (Throwable)string);
                return null;
            }
        }
        catch (JSONException ex) {
            Log.e("ClockFaceSettings", "Settings value is not valid JSON", (Throwable)ex);
            this.mMigration.migrate((String)string, n);
            return (String)string;
        }
    }
    
    String getDockedClockFace(final int n) {
        return Settings$Secure.getStringForUser(this.mContentResolver, "docked_clock_face", n);
    }
    
    String getLockScreenCustomClockFace(final int n) {
        return this.decode(Settings$Secure.getStringForUser(this.mContentResolver, "lock_screen_custom_clock_face", n), n);
    }
    
    interface Migration
    {
        void migrate(final String p0, final int p1);
    }
    
    private static final class Migrator implements Migration
    {
        private final ContentResolver mContentResolver;
        
        Migrator(final ContentResolver mContentResolver) {
            this.mContentResolver = mContentResolver;
        }
        
        @Override
        public void migrate(final String s, final int n) {
            try {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("clock", (Object)s);
                Settings$Secure.putStringForUser(this.mContentResolver, "lock_screen_custom_clock_face", jsonObject.toString(), n);
            }
            catch (JSONException ex) {
                Log.e("ClockFaceSettings", "Failed migrating settings value to JSON format", (Throwable)ex);
            }
        }
    }
}
