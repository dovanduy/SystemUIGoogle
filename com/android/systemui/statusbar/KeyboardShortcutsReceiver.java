// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class KeyboardShortcutsReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) {
        if ("com.android.intent.action.SHOW_KEYBOARD_SHORTCUTS".equals(intent.getAction())) {
            KeyboardShortcuts.show(context, -1);
        }
        else if ("com.android.intent.action.DISMISS_KEYBOARD_SHORTCUTS".equals(intent.getAction())) {
            KeyboardShortcuts.dismiss();
        }
    }
}
