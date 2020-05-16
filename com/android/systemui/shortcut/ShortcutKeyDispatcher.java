// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shortcut;

import android.os.RemoteException;
import com.android.internal.policy.IShortcutService;
import com.android.internal.policy.DividerSnapAlgorithm$SnapTarget;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.systemui.stackdivider.DividerView;
import android.graphics.Rect;
import android.view.WindowManagerGlobal;
import android.content.Context;
import android.view.IWindowManager;
import com.android.systemui.recents.Recents;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.SystemUI;

public class ShortcutKeyDispatcher extends SystemUI implements Callbacks
{
    private final Divider mDivider;
    private final Recents mRecents;
    private ShortcutKeyServiceProxy mShortcutKeyServiceProxy;
    private IWindowManager mWindowManagerService;
    
    public ShortcutKeyDispatcher(final Context context, final Divider mDivider, final Recents mRecents) {
        super(context);
        this.mShortcutKeyServiceProxy = new ShortcutKeyServiceProxy((ShortcutKeyServiceProxy.Callbacks)this);
        this.mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
        this.mDivider = mDivider;
        this.mRecents = mRecents;
    }
    
    private void handleDockKey(final long n) {
        final Divider mDivider = this.mDivider;
        int n2 = 0;
        if (mDivider != null && mDivider.inSplitMode()) {
            final DividerView view = this.mDivider.getView();
            final DividerSnapAlgorithm snapAlgorithm = view.getSnapAlgorithm();
            final DividerSnapAlgorithm$SnapTarget calculateNonDismissingSnapTarget = snapAlgorithm.calculateNonDismissingSnapTarget(view.getCurrentPosition());
            DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget;
            if (n == 281474976710727L) {
                dividerSnapAlgorithm$SnapTarget = snapAlgorithm.getPreviousTarget(calculateNonDismissingSnapTarget);
            }
            else {
                dividerSnapAlgorithm$SnapTarget = snapAlgorithm.getNextTarget(calculateNonDismissingSnapTarget);
            }
            view.startDragging(true, false);
            view.stopDragging(dividerSnapAlgorithm$SnapTarget.position, 0.0f, false, true);
        }
        else {
            final Recents mRecents = this.mRecents;
            if (n != 281474976710727L) {
                n2 = 1;
            }
            mRecents.splitPrimaryTask(n2, null, -1);
        }
    }
    
    @Override
    public void onShortcutKeyPressed(final long n) {
        final int orientation = super.mContext.getResources().getConfiguration().orientation;
        if ((n == 281474976710727L || n == 281474976710728L) && orientation == 2) {
            this.handleDockKey(n);
        }
    }
    
    public void registerShortcutKey(final long n) {
        try {
            this.mWindowManagerService.registerShortcutKey(n, (IShortcutService)this.mShortcutKeyServiceProxy);
        }
        catch (RemoteException ex) {}
    }
    
    @Override
    public void start() {
        this.registerShortcutKey(281474976710727L);
        this.registerShortcutKey(281474976710728L);
    }
}
