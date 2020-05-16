// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import android.content.ContentResolver;
import android.provider.Settings$Secure;
import com.android.systemui.SystemUI;
import com.android.systemui.R$string;
import com.android.systemui.qs.QSHost;
import com.android.systemui.plugins.ActivityStarter;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Canvas;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.R$drawable;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.android.systemui.plugins.qs.QSTile;
import android.os.Message;
import java.util.List;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Process;
import android.content.Intent;
import android.provider.Settings$Global;
import com.android.systemui.R$integer;
import android.app.ActivityManager;
import android.os.Looper;
import android.util.Log;
import android.os.SystemProperties;
import android.os.Build;
import java.util.ArrayList;
import android.os.Handler;
import android.util.LongSparseArray;
import android.content.Context;
import com.android.systemui.Dumpable;

public class GarbageMonitor implements Dumpable
{
    private static final boolean DEBUG;
    private static final boolean ENABLE_AM_HEAP_LIMIT;
    private static final boolean HEAP_TRACKING_ENABLED;
    private static final boolean LEAK_REPORTING_ENABLED;
    private final Context mContext;
    private final LongSparseArray<ProcessMemInfo> mData;
    private DumpTruck mDumpTruck;
    private final Handler mHandler;
    private long mHeapLimit;
    private final LeakReporter mLeakReporter;
    private final ArrayList<Long> mPids;
    private MemoryTile mQSTile;
    private final TrackedGarbage mTrackedGarbage;
    
    static {
        final boolean is_DEBUGGABLE = Build.IS_DEBUGGABLE;
        boolean leak_REPORTING_ENABLED = false;
        if (is_DEBUGGABLE) {
            leak_REPORTING_ENABLED = leak_REPORTING_ENABLED;
            if (SystemProperties.getBoolean("debug.enable_leak_reporting", false)) {
                leak_REPORTING_ENABLED = true;
            }
        }
        LEAK_REPORTING_ENABLED = leak_REPORTING_ENABLED;
        ENABLE_AM_HEAP_LIMIT = (HEAP_TRACKING_ENABLED = Build.IS_DEBUGGABLE);
        DEBUG = Log.isLoggable("GarbageMonitor", 3);
    }
    
    public GarbageMonitor(final Context context, final Looper looper, final LeakDetector leakDetector, final LeakReporter mLeakReporter) {
        this.mData = (LongSparseArray<ProcessMemInfo>)new LongSparseArray();
        this.mPids = new ArrayList<Long>();
        this.mContext = context.getApplicationContext();
        final ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
        this.mHandler = new BackgroundHeapCheckHandler(looper);
        this.mTrackedGarbage = leakDetector.getTrackedGarbage();
        this.mLeakReporter = mLeakReporter;
        this.mDumpTruck = new DumpTruck(this.mContext);
        if (GarbageMonitor.ENABLE_AM_HEAP_LIMIT) {
            this.mHeapLimit = Settings$Global.getInt(context.getContentResolver(), "systemui_am_heap_limit", this.mContext.getResources().getInteger(R$integer.watch_heap_limit));
        }
    }
    
    private Intent dumpHprofAndGetShareIntent() {
        final DumpTruck mDumpTruck = this.mDumpTruck;
        mDumpTruck.captureHeaps(this.getTrackedProcesses());
        return mDumpTruck.createShareIntent();
    }
    
    private static String formatBytes(long lng) {
        int n;
        for (n = 0; n < 5 && lng >= 1024L; lng /= 1024L, ++n) {}
        final StringBuilder sb = new StringBuilder();
        sb.append(lng);
        sb.append((new String[] { "B", "K", "M", "G", "T" })[n]);
        return sb.toString();
    }
    
    private boolean gcAndCheckGarbage() {
        if (this.mTrackedGarbage.countOldGarbage() > 5) {
            Runtime.getRuntime().gc();
            return true;
        }
        return false;
    }
    
    private void logPids() {
        if (GarbageMonitor.DEBUG) {
            final StringBuffer sb = new StringBuffer("Now tracking processes: ");
            for (int i = 0; i < this.mPids.size(); ++i) {
                this.mPids.get(i).intValue();
                sb.append(" ");
            }
            Log.v("GarbageMonitor", sb.toString());
        }
    }
    
    private void setTile(final MemoryTile mqsTile) {
        this.mQSTile = mqsTile;
        if (mqsTile != null) {
            mqsTile.update();
        }
    }
    
    private void update() {
        Object o = this.mPids;
        // monitorenter(o)
        int i = 0;
        try {
            while (i < this.mPids.size()) {
                final int intValue = this.mPids.get(i).intValue();
                final long[] rss = Process.getRss(intValue);
                if (rss == null && rss.length == 0) {
                    if (GarbageMonitor.DEBUG) {
                        Log.e("GarbageMonitor", "update: Process.getRss() didn't provide any values.");
                        break;
                    }
                    break;
                }
                else {
                    final long n = rss[0];
                    final LongSparseArray<ProcessMemInfo> mData = this.mData;
                    final long n2 = intValue;
                    final ProcessMemInfo processMemInfo = (ProcessMemInfo)mData.get(n2);
                    processMemInfo.rss[processMemInfo.head] = (processMemInfo.currentRss = n);
                    processMemInfo.head = (processMemInfo.head + 1) % processMemInfo.rss.length;
                    if (n > processMemInfo.max) {
                        processMemInfo.max = n;
                    }
                    if (processMemInfo.currentRss == 0L) {
                        if (GarbageMonitor.DEBUG) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("update: pid ");
                            sb.append(intValue);
                            sb.append(" has rss=0, it probably died");
                            Log.v("GarbageMonitor", sb.toString());
                        }
                        this.mData.remove(n2);
                    }
                    ++i;
                }
            }
            for (int j = this.mPids.size() - 1; j >= 0; --j) {
                if (this.mData.get((long)this.mPids.get(j).intValue()) == null) {
                    this.mPids.remove(j);
                    this.logPids();
                }
            }
            // monitorexit(o)
            o = this.mQSTile;
            if (o != null) {
                ((MemoryTile)o).update();
            }
        }
        finally {
        }
        // monitorexit(o)
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("GarbageMonitor params:");
        printWriter.println(String.format("   mHeapLimit=%d KB", this.mHeapLimit));
        printWriter.println(String.format("   GARBAGE_INSPECTION_INTERVAL=%d (%.1f mins)", 900000L, 15.0f));
        printWriter.println(String.format("   HEAP_TRACK_INTERVAL=%d (%.1f mins)", 60000L, 1.0f));
        printWriter.println(String.format("   HEAP_TRACK_HISTORY_LEN=%d (%.1f hr total)", 720, 12.0f));
        printWriter.println("GarbageMonitor tracked processes:");
        final Iterator<Long> iterator = this.mPids.iterator();
        while (iterator.hasNext()) {
            final ProcessMemInfo processMemInfo = (ProcessMemInfo)this.mData.get((long)iterator.next());
            if (processMemInfo != null) {
                processMemInfo.dump(fileDescriptor, printWriter, array);
            }
        }
    }
    
    public ProcessMemInfo getMemInfo(final int n) {
        return (ProcessMemInfo)this.mData.get((long)n);
    }
    
    public List<Long> getTrackedProcesses() {
        return this.mPids;
    }
    
    void reinspectGarbageAfterGc() {
        final int countOldGarbage = this.mTrackedGarbage.countOldGarbage();
        if (countOldGarbage > 5) {
            this.mLeakReporter.dumpLeak(countOldGarbage);
        }
    }
    
    public void startHeapTracking() {
        this.startTrackingProcess(Process.myPid(), this.mContext.getPackageName(), System.currentTimeMillis());
        this.mHandler.sendEmptyMessage(3000);
    }
    
    public void startLeakMonitor() {
        if (this.mTrackedGarbage == null) {
            return;
        }
        this.mHandler.sendEmptyMessage(1000);
    }
    
    public void startTrackingProcess(final long n, final String s, final long n2) {
        synchronized (this.mPids) {
            if (this.mPids.contains(n)) {
                return;
            }
            this.mPids.add(n);
            this.logPids();
            this.mData.put(n, (Object)new ProcessMemInfo(n, s, n2));
        }
    }
    
    private class BackgroundHeapCheckHandler extends Handler
    {
        BackgroundHeapCheckHandler(final Looper obj) {
            super(obj);
            if (!Looper.getMainLooper().equals(obj)) {
                return;
            }
            throw new RuntimeException("BackgroundHeapCheckHandler may not run on the ui thread");
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1000) {
                if (what == 3000) {
                    GarbageMonitor.this.update();
                    this.removeMessages(3000);
                    this.sendEmptyMessageDelayed(3000, 60000L);
                }
            }
            else {
                if (GarbageMonitor.this.gcAndCheckGarbage()) {
                    this.postDelayed((Runnable)new _$$Lambda$XMHjUeThvUDRPlJmBo9djG71pM8(GarbageMonitor.this), 100L);
                }
                this.removeMessages(1000);
                this.sendEmptyMessageDelayed(1000, 900000L);
            }
        }
    }
    
    private static class MemoryGraphIcon extends Icon
    {
        long limit;
        long rss;
        
        @Override
        public Drawable getDrawable(final Context context) {
            final MemoryIconDrawable memoryIconDrawable = new MemoryIconDrawable(context);
            memoryIconDrawable.setRss(this.rss);
            memoryIconDrawable.setLimit(this.limit);
            return memoryIconDrawable;
        }
        
        public void setHeapLimit(final long limit) {
            this.limit = limit;
        }
        
        public void setRss(final long rss) {
            this.rss = rss;
        }
    }
    
    private static class MemoryIconDrawable extends Drawable
    {
        final Drawable baseIcon;
        final float dp;
        long limit;
        final Paint paint;
        long rss;
        
        MemoryIconDrawable(final Context context) {
            this.paint = new Paint();
            this.baseIcon = context.getDrawable(R$drawable.ic_memory).mutate();
            this.dp = context.getResources().getDisplayMetrics().density;
            this.paint.setColor(QSTileImpl.getColorForState(context, 2));
        }
        
        public void draw(final Canvas canvas) {
            this.baseIcon.draw(canvas);
            final long limit = this.limit;
            if (limit > 0L) {
                final long rss = this.rss;
                if (rss > 0L) {
                    final float min = Math.min(1.0f, rss / (float)limit);
                    final Rect bounds = this.getBounds();
                    final float n = (float)bounds.left;
                    final float dp = this.dp;
                    canvas.translate(n + dp * 8.0f, bounds.top + dp * 5.0f);
                    final float dp2 = this.dp;
                    canvas.drawRect(0.0f, dp2 * 14.0f * (1.0f - min), 8.0f * dp2 + 1.0f, dp2 * 14.0f + 1.0f, this.paint);
                }
            }
        }
        
        public int getIntrinsicHeight() {
            return this.baseIcon.getIntrinsicHeight();
        }
        
        public int getIntrinsicWidth() {
            return this.baseIcon.getIntrinsicWidth();
        }
        
        public int getOpacity() {
            return -3;
        }
        
        public void setAlpha(final int alpha) {
            this.baseIcon.setAlpha(alpha);
        }
        
        public void setBounds(final int n, final int n2, final int n3, final int n4) {
            super.setBounds(n, n2, n3, n4);
            this.baseIcon.setBounds(n, n2, n3, n4);
        }
        
        public void setColorFilter(final ColorFilter colorFilter) {
            this.baseIcon.setColorFilter(colorFilter);
            this.paint.setColorFilter(colorFilter);
        }
        
        public void setLimit(final long limit) {
            if (limit != this.limit) {
                this.limit = limit;
                this.invalidateSelf();
            }
        }
        
        public void setRss(final long rss) {
            if (rss != this.rss) {
                this.rss = rss;
                this.invalidateSelf();
            }
        }
        
        public void setTint(final int n) {
            super.setTint(n);
            this.baseIcon.setTint(n);
        }
        
        public void setTintList(final ColorStateList list) {
            super.setTintList(list);
            this.baseIcon.setTintList(list);
        }
        
        public void setTintMode(final PorterDuff$Mode porterDuff$Mode) {
            super.setTintMode(porterDuff$Mode);
            this.baseIcon.setTintMode(porterDuff$Mode);
        }
    }
    
    public static class MemoryTile extends QSTileImpl<State>
    {
        private boolean dumpInProgress;
        private final GarbageMonitor gm;
        private final ActivityStarter mActivityStarter;
        private ProcessMemInfo pmi;
        
        public MemoryTile(final QSHost qsHost, final GarbageMonitor gm, final ActivityStarter mActivityStarter) {
            super(qsHost);
            this.gm = gm;
            this.mActivityStarter = mActivityStarter;
        }
        
        @Override
        public Intent getLongClickIntent() {
            return new Intent();
        }
        
        @Override
        public int getMetricsCategory() {
            return 0;
        }
        
        @Override
        public CharSequence getTileLabel() {
            return this.getState().label;
        }
        
        @Override
        protected void handleClick() {
            if (this.dumpInProgress) {
                return;
            }
            this.dumpInProgress = true;
            this.refreshState();
            new Thread("HeapDumpThread") {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(500L);
                            MemoryTile.this.mHandler.post((Runnable)new _$$Lambda$GarbageMonitor$MemoryTile$1$cmBeuqKr1b9hrY1trlao7X6pfIc(this, MemoryTile.this.gm.dumpHprofAndGetShareIntent()));
                        }
                        catch (InterruptedException ex) {
                            continue;
                        }
                        break;
                    }
                }
            }.start();
        }
        
        public void handleSetListening(final boolean b) {
            super.handleSetListening(b);
            final GarbageMonitor gm = this.gm;
            if (gm != null) {
                MemoryTile memoryTile;
                if (b) {
                    memoryTile = this;
                }
                else {
                    memoryTile = null;
                }
                gm.setTile(memoryTile);
            }
            final ActivityManager activityManager = (ActivityManager)super.mContext.getSystemService((Class)ActivityManager.class);
            if (b && this.gm.mHeapLimit > 0L) {
                activityManager.setWatchHeapLimit(this.gm.mHeapLimit * 1024L);
            }
            else {
                activityManager.clearWatchHeapLimit();
            }
        }
        
        @Override
        protected void handleUpdateState(final State state, final Object o) {
            this.pmi = this.gm.getMemInfo(Process.myPid());
            final MemoryGraphIcon icon = new MemoryGraphIcon();
            icon.setHeapLimit(this.gm.mHeapLimit);
            int state2;
            if (this.dumpInProgress) {
                state2 = 0;
            }
            else {
                state2 = 2;
            }
            state.state = state2;
            String string;
            if (this.dumpInProgress) {
                string = "Dumping...";
            }
            else {
                string = super.mContext.getString(R$string.heap_dump_tile_name);
            }
            state.label = string;
            final ProcessMemInfo pmi = this.pmi;
            if (pmi != null) {
                icon.setRss(pmi.currentRss);
                state.secondaryLabel = String.format("rss: %s / %s", formatBytes(this.pmi.currentRss * 1024L), formatBytes(this.gm.mHeapLimit * 1024L));
            }
            else {
                icon.setRss(0L);
                state.secondaryLabel = null;
            }
            state.icon = icon;
        }
        
        @Override
        public State newTileState() {
            return new QSTile.State();
        }
        
        public void update() {
            this.refreshState();
        }
    }
    
    public static class ProcessMemInfo implements Dumpable
    {
        public long currentRss;
        public int head;
        public long max;
        public String name;
        public long pid;
        public long[] rss;
        public long startTime;
        
        public ProcessMemInfo(final long pid, final String name, final long startTime) {
            this.rss = new long[720];
            this.max = 1L;
            this.head = 0;
            this.pid = pid;
            this.name = name;
            this.startTime = startTime;
        }
        
        @Override
        public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
            printWriter.print("{ \"pid\": ");
            printWriter.print(this.pid);
            printWriter.print(", \"name\": \"");
            printWriter.print(this.name.replace('\"', '-'));
            printWriter.print("\", \"start\": ");
            printWriter.print(this.startTime);
            printWriter.print(", \"rss\": [");
            for (int i = 0; i < this.rss.length; ++i) {
                if (i > 0) {
                    printWriter.print(",");
                }
                final long[] rss = this.rss;
                printWriter.print(rss[(this.head + i) % rss.length]);
            }
            printWriter.println("] }");
        }
        
        public long getUptime() {
            return System.currentTimeMillis() - this.startTime;
        }
    }
    
    public static class Service extends SystemUI implements Dumpable
    {
        private final GarbageMonitor mGarbageMonitor;
        
        public Service(final Context context, final GarbageMonitor mGarbageMonitor) {
            super(context);
            this.mGarbageMonitor = mGarbageMonitor;
        }
        
        @Override
        public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
            final GarbageMonitor mGarbageMonitor = this.mGarbageMonitor;
            if (mGarbageMonitor != null) {
                mGarbageMonitor.dump(fileDescriptor, printWriter, array);
            }
        }
        
        @Override
        public void start() {
            final ContentResolver contentResolver = super.mContext.getContentResolver();
            boolean b = false;
            if (Settings$Secure.getInt(contentResolver, "sysui_force_enable_leak_reporting", 0) != 0) {
                b = true;
            }
            if (GarbageMonitor.LEAK_REPORTING_ENABLED || b) {
                this.mGarbageMonitor.startLeakMonitor();
            }
            if (GarbageMonitor.HEAP_TRACKING_ENABLED || b) {
                this.mGarbageMonitor.startHeapTracking();
            }
        }
    }
}
