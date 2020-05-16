// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.io.PrintStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

final class UncaughtExceptionHandlers$Exiter implements UncaughtExceptionHandler
{
    private static final Logger logger;
    private final Runtime runtime;
    
    static {
        logger = Logger.getLogger(UncaughtExceptionHandlers$Exiter.class.getName());
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable thrown) {
        final Throwable t3;
        try {
            UncaughtExceptionHandlers$Exiter.logger.log(Level.SEVERE, String.format(Locale.ROOT, "Caught an exception in %s.  Shutting down.", thread), thrown);
            return;
        }
        finally {
            final PrintStream printStream = System.err;
            final Throwable t = thrown;
            final String s = t.getMessage();
            printStream.println(s);
            final PrintStream printStream2 = System.err;
            final Throwable t2 = t3;
            final String s2 = t2.getMessage();
            printStream2.println(s2);
        }
        try {
            final PrintStream printStream = System.err;
            final Throwable t = thrown;
            final String s = t.getMessage();
            printStream.println(s);
            final PrintStream printStream2 = System.err;
            final Throwable t2 = t3;
            final String s2 = t2.getMessage();
            printStream2.println(s2);
        }
        finally {
            this.runtime.exit(1);
        }
    }
}
