// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.chooser;

import android.os.IBinder;
import android.os.StrictMode;
import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;

public class ChooserHelper
{
    static void onChoose(final Activity activity) {
        final Intent intent = activity.getIntent();
        final Bundle extras = intent.getExtras();
        final Intent intent2 = (Intent)intent.getParcelableExtra("android.intent.extra.INTENT");
        final Bundle bundle = (Bundle)intent.getParcelableExtra("android.app.extra.OPTIONS");
        final IBinder binder = extras.getBinder("android.app.extra.PERMISSION_TOKEN");
        final boolean booleanExtra = intent.getBooleanExtra("android.app.extra.EXTRA_IGNORE_TARGET_SECURITY", false);
        final int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", -1);
        StrictMode.disableDeathOnFileUriExposure();
        try {
            activity.startActivityAsCaller(intent2, bundle, binder, booleanExtra, intExtra);
        }
        finally {
            StrictMode.enableDeathOnFileUriExposure();
        }
    }
}
