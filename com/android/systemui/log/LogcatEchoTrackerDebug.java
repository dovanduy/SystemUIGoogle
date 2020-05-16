// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

import android.provider.Settings$SettingNotFoundException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.TypeCastException;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings$Global;
import android.os.Looper;
import java.util.LinkedHashMap;
import android.content.ContentResolver;
import java.util.Map;

public final class LogcatEchoTrackerDebug implements LogcatEchoTracker
{
    public static final Factory Factory;
    private final Map<String, LogLevel> cachedBufferLevels;
    private final Map<String, LogLevel> cachedTagLevels;
    private final ContentResolver contentResolver;
    
    static {
        Factory = new Factory(null);
    }
    
    private LogcatEchoTrackerDebug(final ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        this.cachedBufferLevels = new LinkedHashMap<String, LogLevel>();
        this.cachedTagLevels = new LinkedHashMap<String, LogLevel>();
    }
    
    private final void attach(final Looper looper) {
        this.contentResolver.registerContentObserver(Settings$Global.getUriFor("systemui/buffer"), true, (ContentObserver)new LogcatEchoTrackerDebug$attach.LogcatEchoTrackerDebug$attach$1(this, looper, new Handler(looper)));
        this.contentResolver.registerContentObserver(Settings$Global.getUriFor("systemui/tag"), true, (ContentObserver)new LogcatEchoTrackerDebug$attach.LogcatEchoTrackerDebug$attach$2(this, looper, new Handler(looper)));
    }
    
    public static final LogcatEchoTrackerDebug create(final ContentResolver contentResolver, final Looper looper) {
        return LogcatEchoTrackerDebug.Factory.create(contentResolver, looper);
    }
    
    private final LogLevel getLogLevel(final String str, final String str2, final Map<String, LogLevel> map) {
        final LogLevel logLevel = map.get(str);
        LogLevel logLevel2;
        if (logLevel != null) {
            logLevel2 = logLevel;
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append('/');
            sb.append(str);
            final LogLevel setting = this.readSetting(sb.toString());
            map.put(str, setting);
            logLevel2 = setting;
        }
        return logLevel2;
    }
    
    private final LogLevel parseProp(String lowerCase) {
        if (lowerCase != null) {
            if (lowerCase == null) {
                throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
            }
            lowerCase = lowerCase.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(lowerCase, "(this as java.lang.String).toLowerCase()");
        }
        else {
            lowerCase = null;
        }
        if (lowerCase != null) {
            switch (lowerCase.hashCode()) {
                case 1124446108: {
                    if (lowerCase.equals("warning")) {
                        return LogLevel.WARNING;
                    }
                    break;
                }
                case 351107458: {
                    if (lowerCase.equals("verbose")) {
                        return LogLevel.VERBOSE;
                    }
                    break;
                }
                case 96784904: {
                    if (lowerCase.equals("error")) {
                        return LogLevel.ERROR;
                    }
                    break;
                }
                case 95458899: {
                    if (lowerCase.equals("debug")) {
                        return LogLevel.DEBUG;
                    }
                    break;
                }
                case 3641990: {
                    if (lowerCase.equals("warn")) {
                        return LogLevel.WARNING;
                    }
                    break;
                }
                case 3237038: {
                    if (lowerCase.equals("info")) {
                        return LogLevel.INFO;
                    }
                    break;
                }
                case 118057: {
                    if (lowerCase.equals("wtf")) {
                        return LogLevel.WTF;
                    }
                    break;
                }
                case 119: {
                    if (lowerCase.equals("w")) {
                        return LogLevel.WARNING;
                    }
                    break;
                }
                case 118: {
                    if (lowerCase.equals("v")) {
                        return LogLevel.VERBOSE;
                    }
                    break;
                }
                case 105: {
                    if (lowerCase.equals("i")) {
                        return LogLevel.INFO;
                    }
                    break;
                }
                case 101: {
                    if (lowerCase.equals("e")) {
                        return LogLevel.ERROR;
                    }
                    break;
                }
                case 100: {
                    if (lowerCase.equals("d")) {
                        return LogLevel.DEBUG;
                    }
                    break;
                }
                case -1408208058: {
                    if (lowerCase.equals("assert")) {
                        return LogLevel.WTF;
                    }
                    break;
                }
            }
        }
        return LogcatEchoTrackerDebugKt.access$getDEFAULT_LEVEL$p();
    }
    
    private final LogLevel readSetting(final String s) {
        LogLevel logLevel;
        try {
            logLevel = this.parseProp(Settings$Global.getString(this.contentResolver, s));
        }
        catch (Settings$SettingNotFoundException ex) {
            logLevel = LogcatEchoTrackerDebugKt.access$getDEFAULT_LEVEL$p();
        }
        return logLevel;
    }
    
    @Override
    public boolean isBufferLoggable(final String s, final LogLevel logLevel) {
        synchronized (this) {
            Intrinsics.checkParameterIsNotNull(s, "bufferName");
            Intrinsics.checkParameterIsNotNull(logLevel, "level");
            return logLevel.ordinal() >= this.getLogLevel(s, "systemui/buffer", this.cachedBufferLevels).ordinal();
        }
    }
    
    @Override
    public boolean isTagLoggable(final String s, final LogLevel logLevel) {
        synchronized (this) {
            Intrinsics.checkParameterIsNotNull(s, "tagName");
            Intrinsics.checkParameterIsNotNull(logLevel, "level");
            return logLevel.compareTo(this.getLogLevel(s, "systemui/tag", this.cachedTagLevels)) >= 0;
        }
    }
    
    public static final class Factory
    {
        private Factory() {
        }
        
        public final LogcatEchoTrackerDebug create(final ContentResolver contentResolver, final Looper looper) {
            Intrinsics.checkParameterIsNotNull(contentResolver, "contentResolver");
            Intrinsics.checkParameterIsNotNull(looper, "mainLooper");
            final LogcatEchoTrackerDebug logcatEchoTrackerDebug = new LogcatEchoTrackerDebug(contentResolver, null);
            logcatEchoTrackerDebug.attach(looper);
            return logcatEchoTrackerDebug;
        }
    }
}
