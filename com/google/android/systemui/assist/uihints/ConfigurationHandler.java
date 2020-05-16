// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent$CanceledException;
import android.util.Log;
import java.util.ArrayList;
import android.content.Intent;
import android.content.Context;

public class ConfigurationHandler implements ConfigInfoListener
{
    private final Context mContext;
    
    public ConfigurationHandler(final Context mContext) {
        this.mContext = mContext;
    }
    
    @Override
    public void onConfigInfo(final ConfigInfo configInfo) {
        if (configInfo.configurationCallback == null) {
            return;
        }
        final Intent intent = new Intent();
        final ArrayList<String> list = new ArrayList<String>();
        list.add("go_back");
        list.add("take_screenshot");
        list.add("half_listening_full");
        list.add("input_chips");
        list.add("actions_without_ui");
        intent.putCharSequenceArrayListExtra("flags", (ArrayList)list);
        intent.putExtra("version", 3);
        try {
            configInfo.configurationCallback.send(this.mContext, 0, intent);
        }
        catch (PendingIntent$CanceledException ex) {
            Log.e("ConfigurationHandler", "Pending intent canceled", (Throwable)ex);
        }
    }
}
