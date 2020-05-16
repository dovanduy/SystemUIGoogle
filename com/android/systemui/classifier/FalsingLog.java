// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.app.Application;
import java.io.IOException;
import java.io.File;
import android.app.ActivityThread;
import java.util.Date;
import android.util.Log;
import java.util.Iterator;
import java.io.PrintWriter;
import java.util.Locale;
import android.os.SystemProperties;
import android.os.Build;
import java.util.ArrayDeque;
import java.text.SimpleDateFormat;

public class FalsingLog
{
    public static final boolean ENABLED;
    private static final boolean LOGCAT;
    private static final int MAX_SIZE;
    private static FalsingLog sInstance;
    private final SimpleDateFormat mFormat;
    private final ArrayDeque<String> mLog;
    
    static {
        ENABLED = SystemProperties.getBoolean("debug.falsing_log", Build.IS_DEBUGGABLE);
        LOGCAT = SystemProperties.getBoolean("debug.falsing_logcat", false);
        MAX_SIZE = SystemProperties.getInt("debug.falsing_log_size", 100);
    }
    
    private FalsingLog() {
        this.mLog = new ArrayDeque<String>(FalsingLog.MAX_SIZE);
        this.mFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.US);
    }
    
    public static void dump(final PrintWriter printWriter) {
        synchronized (FalsingLog.class) {
            printWriter.println("FALSING LOG:");
            if (!FalsingLog.ENABLED) {
                printWriter.println("Disabled, to enable: setprop debug.falsing_log 1");
                printWriter.println();
                return;
            }
            if (FalsingLog.sInstance != null && !FalsingLog.sInstance.mLog.isEmpty()) {
                final Iterator<String> iterator = FalsingLog.sInstance.mLog.iterator();
                while (iterator.hasNext()) {
                    printWriter.println(iterator.next());
                }
                printWriter.println();
                return;
            }
            printWriter.println("<empty>");
            printWriter.println();
        }
    }
    
    public static void e(final String str, final String str2) {
        if (FalsingLog.LOGCAT) {
            final StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("\t");
            sb.append(str2);
            Log.e("FalsingLog", sb.toString());
        }
        log("E", str, str2);
    }
    
    public static void i(final String str, final String str2) {
        if (FalsingLog.LOGCAT) {
            final StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("\t");
            sb.append(str2);
            Log.i("FalsingLog", sb.toString());
        }
        log("I", str, str2);
    }
    
    public static void log(String string, final String str, final String str2) {
        synchronized (FalsingLog.class) {
            if (!FalsingLog.ENABLED) {
                return;
            }
            if (FalsingLog.sInstance == null) {
                FalsingLog.sInstance = new FalsingLog();
            }
            if (FalsingLog.sInstance.mLog.size() >= FalsingLog.MAX_SIZE) {
                FalsingLog.sInstance.mLog.removeFirst();
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(FalsingLog.sInstance.mFormat.format(new Date()));
            sb.append(" ");
            sb.append(string);
            sb.append(" ");
            sb.append(str);
            sb.append(" ");
            sb.append(str2);
            string = sb.toString();
            FalsingLog.sInstance.mLog.add(string);
        }
    }
    
    public static void wLogcat(final String str, final String str2) {
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("\t");
        sb.append(str2);
        Log.w("FalsingLog", sb.toString());
        log("W", str, str2);
    }
    
    public static void wtf(final String str, final String str2, final Throwable t) {
        synchronized (FalsingLog.class) {
            if (!FalsingLog.ENABLED) {
                return;
            }
            e(str, str2);
            final Application currentApplication = ActivityThread.currentApplication();
            final String s = "";
            Object str3 = null;
            Label_0300: {
                if (Build.IS_DEBUGGABLE && currentApplication != null) {
                    final File dataDir = currentApplication.getDataDir();
                    final StringBuilder sb = new StringBuilder();
                    sb.append("falsing-");
                    sb.append(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
                    sb.append(".txt");
                    final File file = new File(dataDir, sb.toString());
                    str3 = null;
                    Label_0276: {
                        PrintWriter printWriter;
                        try {
                            printWriter = (PrintWriter)(str3 = new PrintWriter(file));
                            try {
                                final PrintWriter printWriter2 = printWriter;
                                dump(printWriter2);
                                str3 = printWriter;
                                final PrintWriter printWriter3 = printWriter;
                                printWriter3.close();
                                str3 = printWriter;
                                final StringBuilder sb2 = new(java.lang.StringBuilder.class);
                                final StringBuilder sb3 = sb2;
                                str3 = printWriter;
                                final StringBuilder sb4 = sb3;
                                new StringBuilder();
                                str3 = printWriter;
                                final StringBuilder sb5 = sb3;
                                final String s2 = "Log written to ";
                                sb5.append(s2);
                                str3 = printWriter;
                                final StringBuilder sb6 = sb3;
                                final File file2 = file;
                                final String s3 = file2.getAbsolutePath();
                                sb6.append(s3);
                                str3 = printWriter;
                                final StringBuilder sb7 = sb3;
                                final String s4 = sb7.toString();
                                final PrintWriter printWriter4 = printWriter;
                                printWriter4.close();
                                final String s5 = (String)(str3 = s4);
                            }
                            catch (IOException ex) {}
                        }
                        catch (IOException ex) {
                            printWriter = null;
                        }
                        finally {
                            break Label_0276;
                        }
                        try {
                            final PrintWriter printWriter2 = printWriter;
                            dump(printWriter2);
                            str3 = printWriter;
                            final PrintWriter printWriter3 = printWriter;
                            printWriter3.close();
                            str3 = printWriter;
                            final StringBuilder sb2 = new(java.lang.StringBuilder.class);
                            final StringBuilder sb3 = sb2;
                            str3 = printWriter;
                            final StringBuilder sb4 = sb3;
                            new StringBuilder();
                            str3 = printWriter;
                            final StringBuilder sb5 = sb3;
                            final String s2 = "Log written to ";
                            sb5.append(s2);
                            str3 = printWriter;
                            final StringBuilder sb6 = sb3;
                            final File file2 = file;
                            final String s3 = file2.getAbsolutePath();
                            sb6.append(s3);
                            str3 = printWriter;
                            final StringBuilder sb7 = sb3;
                            final String s4 = sb7.toString();
                            final PrintWriter printWriter4 = printWriter;
                            printWriter4.close();
                            str3 = s4;
                            break Label_0300;
                            str3 = printWriter;
                            final IOException ex;
                            Log.e("FalsingLog", "Unable to write falsing log", (Throwable)ex);
                            str3 = s;
                            // iftrue(Label_0300:, printWriter == null)
                            printWriter.close();
                            str3 = s;
                            break Label_0300;
                        }
                        finally {}
                    }
                    if (str3 != null) {
                        ((PrintWriter)str3).close();
                    }
                }
                else {
                    Log.e("FalsingLog", "Unable to write log, build must be debuggable.");
                    str3 = s;
                }
            }
            final StringBuilder sb8 = new StringBuilder();
            sb8.append(str);
            sb8.append(" ");
            sb8.append(str2);
            sb8.append("; ");
            sb8.append((String)str3);
            Log.wtf("FalsingLog", sb8.toString(), t);
        }
    }
}
