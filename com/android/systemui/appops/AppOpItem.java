// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.appops;

public class AppOpItem
{
    private int mCode;
    private String mPackageName;
    private String mState;
    private int mUid;
    
    public AppOpItem(final int n, final int n2, final String s, final long n3) {
        this.mCode = n;
        this.mUid = n2;
        this.mPackageName = s;
        final StringBuilder sb = new StringBuilder();
        sb.append("AppOpItem(");
        sb.append("Op code=");
        sb.append(n);
        sb.append(", ");
        sb.append("UID=");
        sb.append(n2);
        sb.append(", ");
        sb.append("Package name=");
        sb.append(s);
        sb.append(")");
        this.mState = sb.toString();
    }
    
    public int getCode() {
        return this.mCode;
    }
    
    public String getPackageName() {
        return this.mPackageName;
    }
    
    public int getUid() {
        return this.mUid;
    }
    
    @Override
    public String toString() {
        return this.mState;
    }
}
