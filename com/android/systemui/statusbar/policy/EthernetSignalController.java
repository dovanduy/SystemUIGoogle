// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.BitSet;
import android.content.Context;

public class EthernetSignalController extends SignalController<State, IconGroup>
{
    public EthernetSignalController(final Context context, final CallbackHandler callbackHandler, final NetworkControllerImpl networkControllerImpl) {
        super("EthernetSignalController", context, 3, callbackHandler, networkControllerImpl);
        final State mCurrentState = super.mCurrentState;
        final State mLastState = super.mLastState;
        final int[][] ethernet_ICONS = EthernetIcons.ETHERNET_ICONS;
        final int[] ethernet_CONNECTION_VALUES = AccessibilityContentDescriptions.ETHERNET_CONNECTION_VALUES;
        final IconGroup iconGroup = new IconGroup("Ethernet Icons", ethernet_ICONS, null, ethernet_CONNECTION_VALUES, 0, 0, 0, 0, ethernet_CONNECTION_VALUES[0]);
        mLastState.iconGroup = iconGroup;
        mCurrentState.iconGroup = iconGroup;
    }
    
    public State cleanState() {
        return new State();
    }
    
    @Override
    public void notifyListeners(final NetworkController.SignalCallback signalCallback) {
        signalCallback.setEthernetIndicators(new NetworkController.IconState(super.mCurrentState.connected, this.getCurrentIconId(), this.getTextIfExists(this.getContentDescription()).toString()));
    }
    
    @Override
    public void updateConnectivity(final BitSet set, final BitSet set2) {
        super.mCurrentState.connected = set.get(super.mTransportType);
        super.updateConnectivity(set, set2);
    }
}
