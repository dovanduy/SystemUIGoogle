// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import java.util.ArrayList;

public abstract class WirelessCharger
{
    public abstract void asyncIsDockPresent(final IsDockPresentCallback p0);
    
    public abstract void challenge(final byte p0, final byte[] p1, final ChallengeCallback p2);
    
    public abstract void getInformation(final GetInformationCallback p0);
    
    public abstract void keyExchange(final byte[] p0, final KeyExchangeCallback p1);
    
    public abstract void registerAlignInfo(final AlignInfoListener p0);
    
    public interface AlignInfoListener
    {
        void onAlignInfoChanged(final DockAlignInfo p0);
    }
    
    public interface ChallengeCallback
    {
        void onCallback(final int p0, final ArrayList<Byte> p1);
    }
    
    public interface GetInformationCallback
    {
        void onCallback(final int p0, final DockInfo p1);
    }
    
    public interface IsDockPresentCallback
    {
        void onCallback(final boolean p0, final byte p1, final byte p2, final boolean p3, final int p4);
    }
    
    public interface KeyExchangeCallback
    {
        void onCallback(final int p0, final byte p1, final ArrayList<Byte> p2);
    }
}
