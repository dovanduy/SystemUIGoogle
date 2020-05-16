// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import java.io.IOException;
import android.util.Log;
import android.os.Bundle;
import android.app.PendingIntent;
import android.os.UserHandle;
import android.app.Notification$Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import android.os.Debug;
import java.util.ArrayList;
import com.google.android.collect.Lists;
import android.net.Uri;
import android.content.ClipData;
import android.content.ClipData$Item;
import android.os.SystemProperties;
import androidx.core.content.FileProvider;
import android.content.Intent;
import java.io.File;
import android.content.Context;

public class LeakReporter
{
    private final Context mContext;
    private final LeakDetector mLeakDetector;
    private final String mLeakReportEmail;
    
    public LeakReporter(final Context mContext, final LeakDetector mLeakDetector, final String mLeakReportEmail) {
        this.mContext = mContext;
        this.mLeakDetector = mLeakDetector;
        this.mLeakReportEmail = mLeakReportEmail;
    }
    
    private Intent getIntent(final File file, final File file2) {
        final Uri uriForFile = FileProvider.getUriForFile(this.mContext, "com.android.systemui.fileprovider", file2);
        final Uri uriForFile2 = FileProvider.getUriForFile(this.mContext, "com.android.systemui.fileprovider", file);
        final Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.addFlags(1);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.leakreport");
        intent.putExtra("android.intent.extra.SUBJECT", "SystemUI leak report");
        final StringBuilder sb = new StringBuilder("Build info: ");
        sb.append(SystemProperties.get("ro.build.description"));
        intent.putExtra("android.intent.extra.TEXT", sb.toString());
        final ClipData clipData = new ClipData((CharSequence)null, new String[] { "application/vnd.android.leakreport" }, new ClipData$Item((CharSequence)null, (String)null, (Intent)null, uriForFile));
        final ArrayList arrayList = Lists.newArrayList((Object[])new Uri[] { uriForFile });
        clipData.addItem(new ClipData$Item((CharSequence)null, (String)null, (Intent)null, uriForFile2));
        arrayList.add(uriForFile2);
        intent.setClipData(clipData);
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
        final String mLeakReportEmail = this.mLeakReportEmail;
        if (mLeakReportEmail != null) {
            intent.putExtra("android.intent.extra.EMAIL", new String[] { mLeakReportEmail });
        }
        return intent;
    }
    
    public void dumpLeak(final int i) {
        try {
            final File file = new File(this.mContext.getCacheDir(), "leak");
            file.mkdir();
            final File file2 = new File(file, "leak.hprof");
            Debug.dumpHprofData(file2.getAbsolutePath());
            final File file3 = new File(file, "leak.dump");
            Object out = new FileOutputStream(file3);
            try {
                final PrintWriter printWriter = new PrintWriter((OutputStream)out);
                printWriter.print("Build: ");
                printWriter.println(SystemProperties.get("ro.build.description"));
                printWriter.println();
                printWriter.flush();
                this.mLeakDetector.dump(((FileOutputStream)out).getFD(), printWriter, new String[0]);
                printWriter.close();
                ((FileOutputStream)out).close();
                out = this.mContext.getSystemService((Class)NotificationManager.class);
                final NotificationChannel notificationChannel = new NotificationChannel("leak", (CharSequence)"Leak Alerts", 4);
                notificationChannel.enableVibration(true);
                ((NotificationManager)out).createNotificationChannel(notificationChannel);
                ((NotificationManager)out).notify("LeakReporter", 0, new Notification$Builder(this.mContext, notificationChannel.getId()).setAutoCancel(true).setShowWhen(true).setContentTitle((CharSequence)"Memory Leak Detected").setContentText((CharSequence)String.format("SystemUI has detected %d leaked objects. Tap to send", i)).setSmallIcon(17303540).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, this.getIntent(file2, file3), 134217728, (Bundle)null, UserHandle.CURRENT)).build());
            }
            finally {
                try {
                    ((FileOutputStream)out).close();
                }
                finally {
                    final Throwable t;
                    final Throwable exception;
                    t.addSuppressed(exception);
                }
            }
        }
        catch (IOException ex) {
            Log.e("LeakReporter", "Couldn't dump heap for leak", (Throwable)ex);
        }
    }
}
