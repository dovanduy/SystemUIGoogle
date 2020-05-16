// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.text.SimpleDateFormat;
import java.util.BitSet;
import android.util.Log;
import java.io.PrintWriter;
import android.content.Context;

public abstract class SignalController<T extends State, I extends IconGroup>
{
    protected static final boolean CHATTY;
    protected static final boolean DEBUG;
    private final CallbackHandler mCallbackHandler;
    protected final Context mContext;
    protected final T mCurrentState;
    private final State[] mHistory;
    private int mHistoryIndex;
    protected final T mLastState;
    protected final NetworkControllerImpl mNetworkController;
    protected final String mTag;
    protected final int mTransportType;
    
    static {
        DEBUG = NetworkControllerImpl.DEBUG;
        CHATTY = NetworkControllerImpl.CHATTY;
    }
    
    public SignalController(final String str, final Context mContext, int i, final CallbackHandler mCallbackHandler, final NetworkControllerImpl mNetworkController) {
        final StringBuilder sb = new StringBuilder();
        sb.append("NetworkController.");
        sb.append(str);
        this.mTag = sb.toString();
        this.mNetworkController = mNetworkController;
        this.mTransportType = i;
        this.mContext = mContext;
        this.mCallbackHandler = mCallbackHandler;
        this.mCurrentState = this.cleanState();
        this.mLastState = this.cleanState();
        this.mHistory = new State[64];
        for (i = 0; i < 64; ++i) {
            this.mHistory[i] = this.cleanState();
        }
    }
    
    protected abstract T cleanState();
    
    public void dump(final PrintWriter printWriter) {
        final StringBuilder sb = new StringBuilder();
        sb.append("  - ");
        sb.append(this.mTag);
        sb.append(" -----");
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  Current State: ");
        sb2.append(this.mCurrentState);
        printWriter.println(sb2.toString());
        int i = 0;
        int n = 0;
        while (i < 64) {
            int n2 = n;
            if (this.mHistory[i].time != 0L) {
                n2 = n + 1;
            }
            ++i;
            n = n2;
        }
        for (int j = this.mHistoryIndex + 64 - 1; j >= this.mHistoryIndex + 64 - n; --j) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("  Previous State(");
            sb3.append(this.mHistoryIndex + 64 - j);
            sb3.append("): ");
            sb3.append(this.mHistory[j & 0x3F]);
            printWriter.println(sb3.toString());
        }
    }
    
    public int getContentDescription() {
        if (this.mCurrentState.connected) {
            return this.getIcons().mContentDesc[this.mCurrentState.level];
        }
        return this.getIcons().mDiscContentDesc;
    }
    
    public int getCurrentIconId() {
        final State mCurrentState = this.mCurrentState;
        if (mCurrentState.connected) {
            final int[][] mSbIcons = this.getIcons().mSbIcons;
            final State mCurrentState2 = this.mCurrentState;
            return mSbIcons[mCurrentState2.inetCondition][mCurrentState2.level];
        }
        if (mCurrentState.enabled) {
            return this.getIcons().mSbDiscState;
        }
        return this.getIcons().mSbNullState;
    }
    
    protected I getIcons() {
        return (I)this.mCurrentState.iconGroup;
    }
    
    public int getQsCurrentIconId() {
        final State mCurrentState = this.mCurrentState;
        if (mCurrentState.connected) {
            final int[][] mQsIcons = this.getIcons().mQsIcons;
            final State mCurrentState2 = this.mCurrentState;
            return mQsIcons[mCurrentState2.inetCondition][mCurrentState2.level];
        }
        if (mCurrentState.enabled) {
            return this.getIcons().mQsDiscState;
        }
        return this.getIcons().mQsNullState;
    }
    
    public T getState() {
        return this.mCurrentState;
    }
    
    CharSequence getTextIfExists(final int n) {
        CharSequence text;
        if (n != 0) {
            text = this.mContext.getText(n);
        }
        else {
            text = "";
        }
        return text;
    }
    
    public boolean isDirty() {
        if (!this.mLastState.equals(this.mCurrentState)) {
            if (SignalController.DEBUG) {
                final String mTag = this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Change in state from: ");
                sb.append(this.mLastState);
                sb.append("\n\tto: ");
                sb.append(this.mCurrentState);
                Log.d(mTag, sb.toString());
            }
            return true;
        }
        return false;
    }
    
    public final void notifyListeners() {
        this.notifyListeners(this.mCallbackHandler);
    }
    
    public abstract void notifyListeners(final NetworkController.SignalCallback p0);
    
    public void notifyListenersIfNecessary() {
        if (this.isDirty()) {
            this.saveLastState();
            this.notifyListeners();
        }
    }
    
    protected void recordLastState() {
        this.mHistory[this.mHistoryIndex++ & 0x3F].copyFrom(this.mLastState);
    }
    
    public void resetLastState() {
        this.mCurrentState.copyFrom(this.mLastState);
    }
    
    public void saveLastState() {
        this.recordLastState();
        this.mCurrentState.time = System.currentTimeMillis();
        this.mLastState.copyFrom(this.mCurrentState);
    }
    
    public void updateConnectivity(final BitSet set, final BitSet set2) {
        this.mCurrentState.inetCondition = (set2.get(this.mTransportType) ? 1 : 0);
        this.notifyListenersIfNecessary();
    }
    
    static class IconGroup
    {
        final int[] mContentDesc;
        final int mDiscContentDesc;
        final String mName;
        final int mQsDiscState;
        final int[][] mQsIcons;
        final int mQsNullState;
        final int mSbDiscState;
        final int[][] mSbIcons;
        final int mSbNullState;
        
        public IconGroup(final String mName, final int[][] mSbIcons, final int[][] mQsIcons, final int[] mContentDesc, final int mSbNullState, final int mQsNullState, final int mSbDiscState, final int mQsDiscState, final int mDiscContentDesc) {
            this.mName = mName;
            this.mSbIcons = mSbIcons;
            this.mQsIcons = mQsIcons;
            this.mContentDesc = mContentDesc;
            this.mSbNullState = mSbNullState;
            this.mQsNullState = mQsNullState;
            this.mSbDiscState = mSbDiscState;
            this.mQsDiscState = mQsDiscState;
            this.mDiscContentDesc = mDiscContentDesc;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("IconGroup(");
            sb.append(this.mName);
            sb.append(")");
            return sb.toString();
        }
    }
    
    static class State
    {
        private static SimpleDateFormat sSDF;
        boolean activityIn;
        boolean activityOut;
        boolean connected;
        boolean enabled;
        IconGroup iconGroup;
        int inetCondition;
        int level;
        int rssi;
        long time;
        
        static {
            State.sSDF = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        }
        
        public void copyFrom(final State state) {
            this.connected = state.connected;
            this.enabled = state.enabled;
            this.level = state.level;
            this.iconGroup = state.iconGroup;
            this.inetCondition = state.inetCondition;
            this.activityIn = state.activityIn;
            this.activityOut = state.activityOut;
            this.rssi = state.rssi;
            this.time = state.time;
        }
        
        @Override
        public boolean equals(final Object o) {
            final boolean equals = o.getClass().equals(this.getClass());
            final boolean b = false;
            if (!equals) {
                return false;
            }
            final State state = (State)o;
            boolean b2 = b;
            if (state.connected == this.connected) {
                b2 = b;
                if (state.enabled == this.enabled) {
                    b2 = b;
                    if (state.level == this.level) {
                        b2 = b;
                        if (state.inetCondition == this.inetCondition) {
                            b2 = b;
                            if (state.iconGroup == this.iconGroup) {
                                b2 = b;
                                if (state.activityIn == this.activityIn) {
                                    b2 = b;
                                    if (state.activityOut == this.activityOut) {
                                        b2 = b;
                                        if (state.rssi == this.rssi) {
                                            b2 = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return b2;
        }
        
        @Override
        public String toString() {
            if (this.time != 0L) {
                final StringBuilder sb = new StringBuilder();
                this.toString(sb);
                return sb.toString();
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Empty ");
            sb2.append(this.getClass().getSimpleName());
            return sb2.toString();
        }
        
        protected void toString(final StringBuilder sb) {
            sb.append("connected=");
            sb.append(this.connected);
            sb.append(',');
            sb.append("enabled=");
            sb.append(this.enabled);
            sb.append(',');
            sb.append("level=");
            sb.append(this.level);
            sb.append(',');
            sb.append("inetCondition=");
            sb.append(this.inetCondition);
            sb.append(',');
            sb.append("iconGroup=");
            sb.append(this.iconGroup);
            sb.append(',');
            sb.append("activityIn=");
            sb.append(this.activityIn);
            sb.append(',');
            sb.append("activityOut=");
            sb.append(this.activityOut);
            sb.append(',');
            sb.append("rssi=");
            sb.append(this.rssi);
            sb.append(',');
            sb.append("lastModified=");
            sb.append(State.sSDF.format(this.time));
        }
    }
}
