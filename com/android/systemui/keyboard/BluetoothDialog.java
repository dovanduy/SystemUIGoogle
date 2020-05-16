// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyboard;

import android.content.Context;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public class BluetoothDialog extends SystemUIDialog
{
    public BluetoothDialog(final Context context) {
        super(context);
        this.getWindow().setType(2008);
        this.setShowForAllUsers(true);
    }
}
