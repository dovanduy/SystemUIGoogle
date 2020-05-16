// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.functions.Function1;

public final class LogMessageImpl implements LogMessage
{
    public static final Factory Factory;
    private boolean bool1;
    private boolean bool2;
    private boolean bool3;
    private boolean bool4;
    private double double1;
    private int int1;
    private int int2;
    private LogLevel level;
    private long long1;
    private long long2;
    private Function1<? super LogMessage, String> printer;
    private String str1;
    private String str2;
    private String str3;
    private String tag;
    private long timestamp;
    
    static {
        Factory = new Factory(null);
    }
    
    public LogMessageImpl(final LogLevel level, final String tag, final long timestamp, final Function1<? super LogMessage, String> printer, final String str1, final String str2, final String str3, final int int1, final int int2, final long long1, final long long2, final double double1, final boolean bool1, final boolean bool2, final boolean bool3, final boolean bool4) {
        Intrinsics.checkParameterIsNotNull(level, "level");
        Intrinsics.checkParameterIsNotNull(tag, "tag");
        Intrinsics.checkParameterIsNotNull(printer, "printer");
        this.level = level;
        this.tag = tag;
        this.timestamp = timestamp;
        this.printer = printer;
        this.str1 = str1;
        this.str2 = str2;
        this.str3 = str3;
        this.int1 = int1;
        this.int2 = int2;
        this.long1 = long1;
        this.long2 = long2;
        this.double1 = double1;
        this.bool1 = bool1;
        this.bool2 = bool2;
        this.bool3 = bool3;
        this.bool4 = bool4;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof LogMessageImpl) {
                final LogMessageImpl logMessageImpl = (LogMessageImpl)o;
                if (Intrinsics.areEqual(this.getLevel(), logMessageImpl.getLevel()) && Intrinsics.areEqual(this.getTag(), logMessageImpl.getTag()) && this.getTimestamp() == logMessageImpl.getTimestamp() && Intrinsics.areEqual(this.getPrinter(), logMessageImpl.getPrinter()) && Intrinsics.areEqual(this.getStr1(), logMessageImpl.getStr1()) && Intrinsics.areEqual(this.getStr2(), logMessageImpl.getStr2()) && Intrinsics.areEqual(this.getStr3(), logMessageImpl.getStr3()) && this.getInt1() == logMessageImpl.getInt1() && this.getInt2() == logMessageImpl.getInt2() && this.getLong1() == logMessageImpl.getLong1() && this.getLong2() == logMessageImpl.getLong2() && Double.compare(this.getDouble1(), logMessageImpl.getDouble1()) == 0 && this.getBool1() == logMessageImpl.getBool1() && this.getBool2() == logMessageImpl.getBool2() && this.getBool3() == logMessageImpl.getBool3() && this.getBool4() == logMessageImpl.getBool4()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public boolean getBool1() {
        return this.bool1;
    }
    
    @Override
    public boolean getBool2() {
        return this.bool2;
    }
    
    @Override
    public boolean getBool3() {
        return this.bool3;
    }
    
    @Override
    public boolean getBool4() {
        return this.bool4;
    }
    
    public double getDouble1() {
        return this.double1;
    }
    
    @Override
    public int getInt1() {
        return this.int1;
    }
    
    @Override
    public int getInt2() {
        return this.int2;
    }
    
    @Override
    public LogLevel getLevel() {
        return this.level;
    }
    
    @Override
    public long getLong1() {
        return this.long1;
    }
    
    @Override
    public long getLong2() {
        return this.long2;
    }
    
    @Override
    public Function1<LogMessage, String> getPrinter() {
        return (Function1<LogMessage, String>)this.printer;
    }
    
    @Override
    public String getStr1() {
        return this.str1;
    }
    
    @Override
    public String getStr2() {
        return this.str2;
    }
    
    @Override
    public String getStr3() {
        return this.str3;
    }
    
    @Override
    public String getTag() {
        return this.tag;
    }
    
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public int hashCode() {
        final LogLevel level = this.getLevel();
        int hashCode = 0;
        int hashCode2;
        if (level != null) {
            hashCode2 = level.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final String tag = this.getTag();
        int hashCode3;
        if (tag != null) {
            hashCode3 = tag.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        final int hashCode4 = Long.hashCode(this.getTimestamp());
        final Function1<LogMessage, String> printer = this.getPrinter();
        int hashCode5;
        if (printer != null) {
            hashCode5 = printer.hashCode();
        }
        else {
            hashCode5 = 0;
        }
        final String str1 = this.getStr1();
        int hashCode6;
        if (str1 != null) {
            hashCode6 = str1.hashCode();
        }
        else {
            hashCode6 = 0;
        }
        final String str2 = this.getStr2();
        int hashCode7;
        if (str2 != null) {
            hashCode7 = str2.hashCode();
        }
        else {
            hashCode7 = 0;
        }
        final String str3 = this.getStr3();
        if (str3 != null) {
            hashCode = str3.hashCode();
        }
        final int hashCode8 = Integer.hashCode(this.getInt1());
        final int hashCode9 = Integer.hashCode(this.getInt2());
        final int hashCode10 = Long.hashCode(this.getLong1());
        final int hashCode11 = Long.hashCode(this.getLong2());
        final int hashCode12 = Double.hashCode(this.getDouble1());
        final int bool1 = this.getBool1() ? 1 : 0;
        int n = 1;
        int n2 = bool1;
        if (bool1 != 0) {
            n2 = 1;
        }
        int bool2;
        if ((bool2 = (this.getBool2() ? 1 : 0)) != 0) {
            bool2 = 1;
        }
        int bool3;
        if ((bool3 = (this.getBool3() ? 1 : 0)) != 0) {
            bool3 = 1;
        }
        final int bool4 = this.getBool4() ? 1 : 0;
        if (bool4 == 0) {
            n = bool4;
        }
        return ((((((((((((((hashCode2 * 31 + hashCode3) * 31 + hashCode4) * 31 + hashCode5) * 31 + hashCode6) * 31 + hashCode7) * 31 + hashCode) * 31 + hashCode8) * 31 + hashCode9) * 31 + hashCode10) * 31 + hashCode11) * 31 + hashCode12) * 31 + n2) * 31 + bool2) * 31 + bool3) * 31 + n;
    }
    
    public final void reset(final String tag, final LogLevel level, final long timestamp, final Function1<? super LogMessage, String> printer) {
        Intrinsics.checkParameterIsNotNull(tag, "tag");
        Intrinsics.checkParameterIsNotNull(level, "level");
        Intrinsics.checkParameterIsNotNull(printer, "renderer");
        this.setLevel(level);
        this.setTag(tag);
        this.setTimestamp(timestamp);
        this.setPrinter(printer);
        this.setStr1(null);
        this.setStr2(null);
        this.setStr3(null);
        this.setInt1(0);
        this.setInt2(0);
        this.setLong1(0L);
        this.setLong2(0L);
        this.setDouble1(0.0);
        this.setBool1(false);
        this.setBool2(false);
        this.setBool3(false);
        this.setBool4(false);
    }
    
    @Override
    public void setBool1(final boolean bool1) {
        this.bool1 = bool1;
    }
    
    @Override
    public void setBool2(final boolean bool2) {
        this.bool2 = bool2;
    }
    
    @Override
    public void setBool3(final boolean bool3) {
        this.bool3 = bool3;
    }
    
    @Override
    public void setBool4(final boolean bool4) {
        this.bool4 = bool4;
    }
    
    public void setDouble1(final double double1) {
        this.double1 = double1;
    }
    
    @Override
    public void setInt1(final int int1) {
        this.int1 = int1;
    }
    
    @Override
    public void setInt2(final int int2) {
        this.int2 = int2;
    }
    
    public void setLevel(final LogLevel level) {
        Intrinsics.checkParameterIsNotNull(level, "<set-?>");
        this.level = level;
    }
    
    @Override
    public void setLong1(final long long1) {
        this.long1 = long1;
    }
    
    @Override
    public void setLong2(final long long2) {
        this.long2 = long2;
    }
    
    public void setPrinter(final Function1<? super LogMessage, String> printer) {
        Intrinsics.checkParameterIsNotNull(printer, "<set-?>");
        this.printer = printer;
    }
    
    @Override
    public void setStr1(final String str1) {
        this.str1 = str1;
    }
    
    @Override
    public void setStr2(final String str2) {
        this.str2 = str2;
    }
    
    @Override
    public void setStr3(final String str3) {
        this.str3 = str3;
    }
    
    public void setTag(final String tag) {
        Intrinsics.checkParameterIsNotNull(tag, "<set-?>");
        this.tag = tag;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LogMessageImpl(level=");
        sb.append(this.getLevel());
        sb.append(", tag=");
        sb.append(this.getTag());
        sb.append(", timestamp=");
        sb.append(this.getTimestamp());
        sb.append(", printer=");
        sb.append(this.getPrinter());
        sb.append(", str1=");
        sb.append(this.getStr1());
        sb.append(", str2=");
        sb.append(this.getStr2());
        sb.append(", str3=");
        sb.append(this.getStr3());
        sb.append(", int1=");
        sb.append(this.getInt1());
        sb.append(", int2=");
        sb.append(this.getInt2());
        sb.append(", long1=");
        sb.append(this.getLong1());
        sb.append(", long2=");
        sb.append(this.getLong2());
        sb.append(", double1=");
        sb.append(this.getDouble1());
        sb.append(", bool1=");
        sb.append(this.getBool1());
        sb.append(", bool2=");
        sb.append(this.getBool2());
        sb.append(", bool3=");
        sb.append(this.getBool3());
        sb.append(", bool4=");
        sb.append(this.getBool4());
        sb.append(")");
        return sb.toString();
    }
    
    public static final class Factory
    {
        private Factory() {
        }
        
        public final LogMessageImpl create() {
            return new LogMessageImpl(LogLevel.DEBUG, "UnknownTag", 0L, LogMessageImplKt.access$getDEFAULT_RENDERER$p(), null, null, null, 0, 0, 0L, 0L, 0.0, false, false, false, false);
        }
    }
}
