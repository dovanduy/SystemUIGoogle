// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.chooser;

import android.os.Bundle;
import android.app.Activity;

public final class ChooserActivity extends Activity
{
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        ChooserHelper.onChoose(this);
        this.finish();
    }
}
