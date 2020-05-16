// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder;

public class PipelineState
{
    private int mState;
    
    public PipelineState() {
        this.mState = 0;
    }
    
    public int getState() {
        return this.mState;
    }
    
    public void incrementTo(final int n) {
        if (this.mState == n - 1) {
            this.mState = n;
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Cannot increment from state ");
        sb.append(this.mState);
        sb.append(" to state ");
        sb.append(n);
        throw new IllegalStateException(sb.toString());
    }
    
    public boolean is(final int n) {
        return n == this.mState;
    }
    
    public void requireIsBefore(final int i) {
        if (this.mState < i) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Required state is <");
        sb.append(i);
        sb.append(" but actual state is ");
        sb.append(this.mState);
        throw new IllegalStateException(sb.toString());
    }
    
    public void requireState(final int i) {
        if (i == this.mState) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Required state is <");
        sb.append(i);
        sb.append(" but actual state is ");
        sb.append(this.mState);
        throw new IllegalStateException(sb.toString());
    }
    
    public void setState(final int mState) {
        this.mState = mState;
    }
}
