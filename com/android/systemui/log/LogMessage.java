// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

import kotlin.jvm.functions.Function1;

public interface LogMessage
{
    boolean getBool1();
    
    boolean getBool2();
    
    boolean getBool3();
    
    boolean getBool4();
    
    int getInt1();
    
    int getInt2();
    
    LogLevel getLevel();
    
    long getLong1();
    
    long getLong2();
    
    Function1<LogMessage, String> getPrinter();
    
    String getStr1();
    
    String getStr2();
    
    String getStr3();
    
    String getTag();
    
    long getTimestamp();
    
    void setBool1(final boolean p0);
    
    void setBool2(final boolean p0);
    
    void setBool3(final boolean p0);
    
    void setBool4(final boolean p0);
    
    void setInt1(final int p0);
    
    void setInt2(final int p0);
    
    void setLong1(final long p0);
    
    void setLong2(final long p0);
    
    void setStr1(final String p0);
    
    void setStr2(final String p0);
    
    void setStr3(final String p0);
}
