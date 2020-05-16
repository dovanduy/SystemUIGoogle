// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import android.content.ClipData;
import android.content.ClipData$Item;
import android.content.ClipDescription;
import android.content.Intent;
import androidx.core.content.FileProvider;
import android.os.Debug;
import android.os.Process;
import android.os.Build;
import java.io.File;
import com.android.systemui.Dependency;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import android.util.Log;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import android.net.Uri;
import android.content.Context;

public class DumpTruck
{
    final StringBuilder body;
    private final Context context;
    private Uri hprofUri;
    private long rss;
    
    public DumpTruck(final Context context) {
        this.body = new StringBuilder();
        this.context = context;
    }
    
    private static boolean zipUp(String b, ArrayList<String> bufferedInputStream) {
        try {
            final ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(b));
            try {
                b = (String)(Object)new byte[1048576];
                for (final String s : bufferedInputStream) {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(s));
                    try {
                        zipOutputStream.putNextEntry(new ZipEntry(s));
                        while (true) {
                            final int read = bufferedInputStream.read((byte[])(Object)b, 0, 1048576);
                            if (read <= 0) {
                                break;
                            }
                            zipOutputStream.write((byte[])(Object)b, 0, read);
                        }
                        zipOutputStream.closeEntry();
                        bufferedInputStream.close();
                        continue;
                    }
                    finally {
                        try {
                            bufferedInputStream.close();
                        }
                        finally {
                            final Throwable exception;
                            ((Throwable)b).addSuppressed(exception);
                        }
                    }
                    break;
                }
                zipOutputStream.close();
                return true;
            }
            finally {
                try {
                    zipOutputStream.close();
                }
                finally {
                    final Throwable exception2;
                    ((Throwable)b).addSuppressed(exception2);
                }
            }
        }
        catch (IOException ex) {
            Log.e("DumpTruck", "error zipping up profile data", (Throwable)ex);
            return false;
        }
    }
    
    public DumpTruck captureHeaps(List<Long> iterator) {
        final GarbageMonitor garbageMonitor = Dependency.get(GarbageMonitor.class);
        final File file = new File(this.context.getCacheDir(), "leak");
        file.mkdirs();
        this.hprofUri = null;
        this.body.setLength(0);
        final StringBuilder body = this.body;
        body.append("Build: ");
        body.append(Build.DISPLAY);
        body.append("\n\nProcesses:\n");
        final ArrayList<String> list = new ArrayList<String>();
        final int myPid = Process.myPid();
        iterator = ((List<Long>)iterator).iterator();
        while (iterator.hasNext()) {
            final int intValue = iterator.next().intValue();
            final StringBuilder body2 = this.body;
            body2.append("  pid ");
            body2.append(intValue);
            if (garbageMonitor != null) {
                final GarbageMonitor.ProcessMemInfo memInfo = garbageMonitor.getMemInfo(intValue);
                if (memInfo != null) {
                    final StringBuilder body3 = this.body;
                    body3.append(":");
                    body3.append(" up=");
                    body3.append(memInfo.getUptime());
                    body3.append(" rss=");
                    body3.append(memInfo.currentRss);
                    this.rss = memInfo.currentRss;
                }
            }
            if (intValue == myPid) {
                final String path = new File(file, String.format("heap-%d.ahprof", intValue)).getPath();
                final StringBuilder sb = new StringBuilder();
                sb.append("Dumping memory info for process ");
                sb.append(intValue);
                sb.append(" to ");
                sb.append(path);
                Log.v("DumpTruck", sb.toString());
                try {
                    Debug.dumpHprofData(path);
                    list.add(path);
                    this.body.append(" (hprof attached)");
                }
                catch (IOException ex) {
                    Log.e("DumpTruck", "error dumping memory:", (Throwable)ex);
                    final StringBuilder body4 = this.body;
                    body4.append("\n** Could not dump heap: \n");
                    body4.append(ex.toString());
                    body4.append("\n");
                }
            }
            this.body.append("\n");
        }
        try {
            final String canonicalPath = new File(file, String.format("hprof-%d.zip", System.currentTimeMillis())).getCanonicalPath();
            if (zipUp(canonicalPath, list)) {
                this.hprofUri = FileProvider.getUriForFile(this.context, "com.android.systemui.fileprovider", new File(canonicalPath));
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Heap dump accessible at URI: ");
                sb2.append(this.hprofUri);
                Log.v("DumpTruck", sb2.toString());
            }
        }
        catch (IOException ex2) {
            Log.e("DumpTruck", "unable to zip up heapdumps", (Throwable)ex2);
            final StringBuilder body5 = this.body;
            body5.append("\n** Could not zip up files: \n");
            body5.append(ex2.toString());
            body5.append("\n");
        }
        return this;
    }
    
    public Intent createShareIntent() {
        final Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.addFlags(268435456);
        intent.addFlags(1);
        intent.putExtra("android.intent.extra.SUBJECT", String.format("SystemUI memory dump (rss=%dM)", this.rss / 1024L));
        intent.putExtra("android.intent.extra.TEXT", this.body.toString());
        if (this.hprofUri != null) {
            final ArrayList<Uri> list = new ArrayList<Uri>();
            list.add(this.hprofUri);
            intent.setType("application/zip");
            intent.putParcelableArrayListExtra("android.intent.extra.STREAM", (ArrayList)list);
            intent.setClipData(new ClipData(new ClipDescription((CharSequence)"content", new String[] { "text/plain" }), new ClipData$Item(this.hprofUri)));
            intent.addFlags(1);
        }
        return intent;
    }
}
