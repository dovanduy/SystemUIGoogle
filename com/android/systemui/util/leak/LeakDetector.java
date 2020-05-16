// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import java.util.function.Predicate;
import java.io.Writer;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Collection;
import com.android.internal.annotations.VisibleForTesting;
import android.os.Build;
import com.android.systemui.Dumpable;

public class LeakDetector implements Dumpable
{
    public static final boolean ENABLED;
    private final TrackedCollections mTrackedCollections;
    private final TrackedGarbage mTrackedGarbage;
    private final TrackedObjects mTrackedObjects;
    
    static {
        ENABLED = Build.IS_DEBUGGABLE;
    }
    
    @VisibleForTesting
    public LeakDetector(final TrackedCollections mTrackedCollections, final TrackedGarbage mTrackedGarbage, final TrackedObjects mTrackedObjects) {
        this.mTrackedCollections = mTrackedCollections;
        this.mTrackedGarbage = mTrackedGarbage;
        this.mTrackedObjects = mTrackedObjects;
    }
    
    public static LeakDetector create() {
        if (LeakDetector.ENABLED) {
            final TrackedCollections trackedCollections = new TrackedCollections();
            return new LeakDetector(trackedCollections, new TrackedGarbage(trackedCollections), new TrackedObjects(trackedCollections));
        }
        return new LeakDetector(null, null, null);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter((Writer)printWriter, "  ");
        indentingPrintWriter.println("SYSUI LEAK DETECTOR");
        indentingPrintWriter.increaseIndent();
        if (this.mTrackedCollections != null && this.mTrackedGarbage != null) {
            indentingPrintWriter.println("TrackedCollections:");
            indentingPrintWriter.increaseIndent();
            this.mTrackedCollections.dump((PrintWriter)indentingPrintWriter, (Predicate<Collection<?>>)_$$Lambda$LeakDetector$pWx7s0HACocvPZyQWmuD0rk2VO8.INSTANCE);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.println("TrackedObjects:");
            indentingPrintWriter.increaseIndent();
            this.mTrackedCollections.dump((PrintWriter)indentingPrintWriter, (Predicate<Collection<?>>)_$$Lambda$oUbBhMkDSLCrT89WHUZWOlE1TKs.INSTANCE);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.print("TrackedGarbage:");
            indentingPrintWriter.increaseIndent();
            this.mTrackedGarbage.dump((PrintWriter)indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
        }
        else {
            indentingPrintWriter.println("disabled");
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
    }
    
    TrackedGarbage getTrackedGarbage() {
        return this.mTrackedGarbage;
    }
    
    public <T> void trackCollection(final Collection<T> collection, final String s) {
        final TrackedCollections mTrackedCollections = this.mTrackedCollections;
        if (mTrackedCollections != null) {
            mTrackedCollections.track(collection, s);
        }
    }
    
    public void trackGarbage(final Object o) {
        final TrackedGarbage mTrackedGarbage = this.mTrackedGarbage;
        if (mTrackedGarbage != null) {
            mTrackedGarbage.track(o);
        }
    }
    
    public <T> void trackInstance(final T t) {
        final TrackedObjects mTrackedObjects = this.mTrackedObjects;
        if (mTrackedObjects != null) {
            mTrackedObjects.track(t);
        }
    }
}
