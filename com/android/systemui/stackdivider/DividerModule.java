// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import com.android.systemui.TransactionPool;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.os.Handler;
import com.android.systemui.wm.DisplayImeController;
import com.android.systemui.wm.SystemWindows;
import com.android.systemui.wm.DisplayController;
import com.android.systemui.recents.Recents;
import dagger.Lazy;
import java.util.Optional;
import android.content.Context;

public class DividerModule
{
    static Divider provideDivider(final Context context, final Optional<Lazy<Recents>> optional, final DisplayController displayController, final SystemWindows systemWindows, final DisplayImeController displayImeController, final Handler handler, final KeyguardStateController keyguardStateController, final TransactionPool transactionPool) {
        return new Divider(context, optional, displayController, systemWindows, displayImeController, handler, keyguardStateController, transactionPool);
    }
}
