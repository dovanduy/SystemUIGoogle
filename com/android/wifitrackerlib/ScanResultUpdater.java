// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.List;

public class ScanResultUpdater
{
    public abstract List<ScanResult> getScanResults(final long p0) throws IllegalArgumentException;
    
    public abstract void update(final List<ScanResult> p0);
}
