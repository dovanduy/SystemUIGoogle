// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.plugins.FalsingManager;

public class DozeFalsingManagerAdapter implements Part
{
    private final FalsingManager mFalsingManager;
    
    public DozeFalsingManagerAdapter(final FalsingManager mFalsingManager) {
        this.mFalsingManager = mFalsingManager;
    }
    
    private boolean isAodMode(final State state) {
        final int n = DozeFalsingManagerAdapter$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()];
        return n == 1 || n == 2 || n == 3;
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        this.mFalsingManager.setShowingAod(this.isAodMode(state2));
    }
}
