// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.model;

import com.android.systemui.shared.system.QuickStepContract;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.function.Consumer;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.android.systemui.Dumpable;

public class SysUiState implements Dumpable
{
    private static final String TAG = "SysUiState";
    private final List<SysUiStateCallback> mCallbacks;
    private int mFlags;
    private int mFlagsToClear;
    private int mFlagsToSet;
    
    public SysUiState() {
        this.mCallbacks = new ArrayList<SysUiStateCallback>();
        this.mFlagsToSet = 0;
        this.mFlagsToClear = 0;
    }
    
    private void notifyAndSetSystemUiStateChanged(final int n, final int i) {
        final String tag = SysUiState.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("SysUiState changed: old=");
        sb.append(i);
        sb.append(" new=");
        sb.append(n);
        Log.d(tag, sb.toString());
        if (n != i) {
            this.mCallbacks.forEach(new _$$Lambda$SysUiState$t3XYZGveGuajejWRdEzQbzm_n4M(n));
            this.mFlags = n;
        }
    }
    
    private void updateFlags(int mFlags) {
        if (mFlags != 0) {
            final String tag = SysUiState.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Ignoring flag update for display: ");
            sb.append(mFlags);
            Log.w(tag, sb.toString(), new Throwable());
            return;
        }
        mFlags = this.mFlags;
        this.notifyAndSetSystemUiStateChanged((this.mFlagsToSet | mFlags) & this.mFlagsToClear, mFlags);
    }
    
    public void addCallback(final SysUiStateCallback sysUiStateCallback) {
        this.mCallbacks.add(sysUiStateCallback);
        sysUiStateCallback.onSystemUiStateChanged(this.mFlags);
    }
    
    public void commitUpdate(final int n) {
        this.updateFlags(n);
        this.mFlagsToSet = 0;
        this.mFlagsToClear = 0;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("SysUiState state:");
        printWriter.print("  mSysUiStateFlags=");
        printWriter.println(this.mFlags);
        final StringBuilder sb = new StringBuilder();
        sb.append("    ");
        sb.append(QuickStepContract.getSystemUiStateString(this.mFlags));
        printWriter.println(sb.toString());
        printWriter.print("    backGestureDisabled=");
        printWriter.println(QuickStepContract.isBackGestureDisabled(this.mFlags));
        printWriter.print("    assistantGestureDisabled=");
        printWriter.println(QuickStepContract.isAssistantGestureDisabled(this.mFlags));
    }
    
    public void removeCallback(final SysUiStateCallback sysUiStateCallback) {
        this.mCallbacks.remove(sysUiStateCallback);
    }
    
    public SysUiState setFlag(final int n, final boolean b) {
        if (b) {
            this.mFlagsToSet |= n;
        }
        else {
            this.mFlagsToClear |= n;
        }
        return this;
    }
    
    public interface SysUiStateCallback
    {
        void onSystemUiStateChanged(final int p0);
    }
}
