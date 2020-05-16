// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenrecord;

import android.os.RemoteException;
import android.media.projection.IMediaProjection$Stub;
import android.media.projection.IMediaProjectionManager$Stub;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.Handler;
import android.hardware.display.VirtualDisplay$Callback;
import android.view.WindowManager;
import android.provider.Settings$System;
import java.io.OutputStream;
import android.widget.Toast;
import java.nio.file.Files;
import android.provider.MediaStore$Video$Media;
import android.content.ContentValues;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.content.ContentResolver;
import android.app.Notification$Action;
import android.app.Notification$Style;
import android.graphics.Bitmap;
import android.app.Notification$BigPictureStyle;
import java.io.IOException;
import android.util.Log;
import android.os.CancellationSignal;
import android.util.Size;
import android.app.Notification$Action$Builder;
import android.graphics.drawable.Icon;
import android.content.Intent;
import android.app.Notification;
import android.net.Uri;
import android.content.res.Resources;
import android.app.PendingIntent;
import com.android.systemui.R$color;
import com.android.systemui.R$drawable;
import android.content.Context;
import android.os.Bundle;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import com.android.systemui.R$string;
import android.hardware.display.VirtualDisplay;
import java.io.File;
import android.app.Notification$Builder;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.view.Surface;
import android.media.MediaRecorder$OnInfoListener;
import android.app.Service;

public class RecordingService extends Service implements MediaRecorder$OnInfoListener
{
    private final RecordingController mController;
    private Surface mInputSurface;
    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private boolean mOriginalShowTaps;
    private Notification$Builder mRecordingNotificationBuilder;
    private boolean mShowTaps;
    private File mTempFile;
    private boolean mUseAudio;
    private VirtualDisplay mVirtualDisplay;
    
    public RecordingService(final RecordingController mController) {
        this.mController = mController;
    }
    
    private void createRecordingNotification() {
        final Resources resources = this.getResources();
        final NotificationChannel notificationChannel = new NotificationChannel("screen_record", (CharSequence)this.getString(R$string.screenrecord_name), 3);
        notificationChannel.setDescription(this.getString(R$string.screenrecord_channel_description));
        notificationChannel.enableVibration(true);
        final NotificationManager notificationManager = (NotificationManager)this.getSystemService("notification");
        notificationManager.createNotificationChannel(notificationChannel);
        final Bundle bundle = new Bundle();
        bundle.putString("android.substName", resources.getString(R$string.screenrecord_name));
        String contentTitle;
        if (this.mUseAudio) {
            contentTitle = resources.getString(R$string.screenrecord_ongoing_screen_and_audio);
        }
        else {
            contentTitle = resources.getString(R$string.screenrecord_ongoing_screen_only);
        }
        final Notification$Builder addExtras = new Notification$Builder((Context)this, "screen_record").setSmallIcon(R$drawable.ic_screenrecord).setContentTitle((CharSequence)contentTitle).setContentText((CharSequence)this.getResources().getString(R$string.screenrecord_stop_text)).setUsesChronometer(true).setColorized(true).setColor(this.getResources().getColor(R$color.GM2_red_700)).setOngoing(true).setContentIntent(PendingIntent.getService((Context)this, 2, getStopIntent((Context)this), 134217728)).addExtras(bundle);
        this.mRecordingNotificationBuilder = addExtras;
        notificationManager.notify(1, addExtras.build());
        this.startForeground(1, this.mRecordingNotificationBuilder.build());
    }
    
    private Notification createSaveNotification(final Uri uri) {
        final Intent setDataAndType = new Intent("android.intent.action.VIEW").setFlags(268435457).setDataAndType(uri, "video/mp4");
        final Notification$Action build = new Notification$Action$Builder(Icon.createWithResource((Context)this, R$drawable.ic_screenrecord), (CharSequence)this.getResources().getString(R$string.screenrecord_share_label), PendingIntent.getService((Context)this, 2, getShareIntent((Context)this, uri.toString()), 134217728)).build();
        final Notification$Action build2 = new Notification$Action$Builder(Icon.createWithResource((Context)this, R$drawable.ic_screenrecord), (CharSequence)this.getResources().getString(R$string.screenrecord_delete_label), PendingIntent.getService((Context)this, 2, getDeleteIntent((Context)this, uri.toString()), 134217728)).build();
        final Bundle bundle = new Bundle();
        bundle.putString("android.substName", this.getResources().getString(R$string.screenrecord_name));
        final Notification$Builder addExtras = new Notification$Builder((Context)this, "screen_record").setSmallIcon(R$drawable.ic_screenrecord).setContentTitle((CharSequence)this.getResources().getString(R$string.screenrecord_save_message)).setContentIntent(PendingIntent.getActivity((Context)this, 2, setDataAndType, 1)).addAction(build).addAction(build2).setAutoCancel(true).addExtras(bundle);
        Bitmap loadThumbnail;
        try {
            final ContentResolver contentResolver = this.getContentResolver();
            final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            loadThumbnail = contentResolver.loadThumbnail(uri, new Size(displayMetrics.widthPixels, displayMetrics.heightPixels / 2), (CancellationSignal)null);
        }
        catch (IOException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Error creating thumbnail: ");
            sb.append(ex.getMessage());
            Log.e("RecordingService", sb.toString());
            ex.printStackTrace();
            loadThumbnail = null;
        }
        if (loadThumbnail != null) {
            addExtras.setLargeIcon(loadThumbnail).setStyle((Notification$Style)new Notification$BigPictureStyle().bigPicture(loadThumbnail).bigLargeIcon((Bitmap)null));
        }
        return addExtras.build();
    }
    
    private static Intent getDeleteIntent(final Context context, final String s) {
        return new Intent(context, (Class)RecordingService.class).setAction("com.android.systemui.screenrecord.DELETE").putExtra("extra_path", s);
    }
    
    private static Intent getShareIntent(final Context context, final String s) {
        return new Intent(context, (Class)RecordingService.class).setAction("com.android.systemui.screenrecord.SHARE").putExtra("extra_path", s);
    }
    
    public static Intent getStartIntent(final Context context, final int n, final Intent intent, final boolean b, final boolean b2) {
        return new Intent(context, (Class)RecordingService.class).setAction("com.android.systemui.screenrecord.START").putExtra("extra_resultCode", n).putExtra("extra_data", (Parcelable)intent).putExtra("extra_useAudio", b).putExtra("extra_showTaps", b2);
    }
    
    public static Intent getStopIntent(final Context context) {
        return new Intent(context, (Class)RecordingService.class).setAction("com.android.systemui.screenrecord.STOP");
    }
    
    private void saveRecording(final NotificationManager notificationManager) {
        final String format = new SimpleDateFormat("'screen-'yyyyMMdd-HHmmss'.mp4'").format(new Date());
        final ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", format);
        contentValues.put("mime_type", "video/mp4");
        contentValues.put("date_added", Long.valueOf(System.currentTimeMillis()));
        contentValues.put("datetaken", Long.valueOf(System.currentTimeMillis()));
        final ContentResolver contentResolver = this.getContentResolver();
        final Uri insert = contentResolver.insert(MediaStore$Video$Media.getContentUri("external_primary"), contentValues);
        try {
            final OutputStream openOutputStream = contentResolver.openOutputStream(insert, "w");
            Files.copy(this.mTempFile.toPath(), openOutputStream);
            openOutputStream.close();
            notificationManager.notify(1, this.createSaveNotification(insert));
            this.mTempFile.delete();
        }
        catch (IOException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Error saving screen recording: ");
            sb.append(ex.getMessage());
            Log.e("RecordingService", sb.toString());
            Toast.makeText((Context)this, R$string.screenrecord_delete_error, 1).show();
        }
    }
    
    private void setTapsVisible(final boolean b) {
        Settings$System.putInt(this.getApplicationContext().getContentResolver(), "show_touches", (int)(b ? 1 : 0));
    }
    
    private void startRecording() {
        try {
            final File cacheDir = this.getCacheDir();
            cacheDir.mkdirs();
            this.mTempFile = File.createTempFile("temp", ".mp4", cacheDir);
            final StringBuilder sb = new StringBuilder();
            sb.append("Writing video output to: ");
            sb.append(this.mTempFile.getAbsolutePath());
            Log.d("RecordingService", sb.toString());
            final ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
            boolean mOriginalShowTaps = false;
            if (1 == Settings$System.getInt(contentResolver, "show_touches", 0)) {
                mOriginalShowTaps = true;
            }
            this.mOriginalShowTaps = mOriginalShowTaps;
            this.setTapsVisible(this.mShowTaps);
            final MediaRecorder mMediaRecorder = new MediaRecorder();
            this.mMediaRecorder = mMediaRecorder;
            if (this.mUseAudio) {
                mMediaRecorder.setAudioSource(1);
            }
            this.mMediaRecorder.setVideoSource(2);
            this.mMediaRecorder.setOutputFormat(2);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager)this.getSystemService("window")).getDefaultDisplay().getRealMetrics(displayMetrics);
            final int widthPixels = displayMetrics.widthPixels;
            final int heightPixels = displayMetrics.heightPixels;
            this.mMediaRecorder.setVideoEncoder(2);
            this.mMediaRecorder.setVideoSize(widthPixels, heightPixels);
            this.mMediaRecorder.setVideoFrameRate(30);
            this.mMediaRecorder.setVideoEncodingBitRate(10000000);
            this.mMediaRecorder.setMaxDuration(3600000);
            this.mMediaRecorder.setMaxFileSize(5000000000L);
            if (this.mUseAudio) {
                this.mMediaRecorder.setAudioEncoder(1);
                this.mMediaRecorder.setAudioChannels(1);
                this.mMediaRecorder.setAudioEncodingBitRate(16);
                this.mMediaRecorder.setAudioSamplingRate(44100);
            }
            this.mMediaRecorder.setOutputFile(this.mTempFile);
            this.mMediaRecorder.prepare();
            final Surface surface = this.mMediaRecorder.getSurface();
            this.mInputSurface = surface;
            this.mVirtualDisplay = this.mMediaProjection.createVirtualDisplay("Recording Display", widthPixels, heightPixels, displayMetrics.densityDpi, 16, surface, (VirtualDisplay$Callback)null, (Handler)null);
            this.mMediaRecorder.setOnInfoListener((MediaRecorder$OnInfoListener)this);
            this.mMediaRecorder.start();
            this.mController.updateState(true);
            this.createRecordingNotification();
        }
        catch (IOException cause) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Error starting screen recording: ");
            sb2.append(cause.getMessage());
            Log.e("RecordingService", sb2.toString());
            cause.printStackTrace();
            throw new RuntimeException(cause);
        }
    }
    
    private void stopRecording() {
        this.setTapsVisible(this.mOriginalShowTaps);
        this.mMediaRecorder.stop();
        this.mMediaRecorder.release();
        this.mMediaRecorder = null;
        this.mMediaProjection.stop();
        this.mMediaProjection = null;
        this.mInputSurface.release();
        this.mVirtualDisplay.release();
        this.stopSelf();
        this.mController.updateState(false);
    }
    
    public IBinder onBind(final Intent intent) {
        return null;
    }
    
    public void onCreate() {
        super.onCreate();
    }
    
    public void onInfo(final MediaRecorder mediaRecorder, final int i, final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Media recorder info: ");
        sb.append(i);
        Log.d("RecordingService", sb.toString());
        this.onStartCommand(getStopIntent((Context)this), 0, 0);
    }
    
    public int onStartCommand(Intent putExtra, int n, final int n2) {
        if (putExtra == null) {
            return 2;
        }
        final String action = putExtra.getAction();
        final StringBuilder sb = new StringBuilder();
        sb.append("onStartCommand ");
        sb.append(action);
        Log.d("RecordingService", sb.toString());
        final NotificationManager notificationManager = (NotificationManager)this.getSystemService("notification");
        n = -1;
        switch (action.hashCode()) {
            case -470086188: {
                if (action.equals("com.android.systemui.screenrecord.STOP")) {
                    n = 1;
                    break;
                }
                break;
            }
            case -1224647939: {
                if (action.equals("com.android.systemui.screenrecord.DELETE")) {
                    n = 3;
                    break;
                }
                break;
            }
            case -1687783248: {
                if (action.equals("com.android.systemui.screenrecord.START")) {
                    n = 0;
                    break;
                }
                break;
            }
            case -1688140755: {
                if (action.equals("com.android.systemui.screenrecord.SHARE")) {
                    n = 2;
                    break;
                }
                break;
            }
        }
        if (n != 0) {
            if (n == 1) {
                this.stopRecording();
                this.saveRecording(notificationManager);
                return 1;
            }
            if (n == 2) {
                putExtra = new Intent("android.intent.action.SEND").setType("video/mp4").putExtra("android.intent.extra.STREAM", (Parcelable)Uri.parse(putExtra.getStringExtra("extra_path")));
                final String string = this.getResources().getString(R$string.screenrecord_share_label);
                this.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                notificationManager.cancel(1);
                this.startActivity(Intent.createChooser(putExtra, (CharSequence)string).setFlags(268435456));
                return 1;
            }
            if (n != 3) {
                return 1;
            }
            this.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            final ContentResolver contentResolver = this.getContentResolver();
            final Uri parse = Uri.parse(putExtra.getStringExtra("extra_path"));
            contentResolver.delete(parse, (String)null, (String[])null);
            Toast.makeText((Context)this, R$string.screenrecord_delete_description, 1).show();
            notificationManager.cancel(1);
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Deleted recording ");
            sb2.append(parse);
            Log.d("RecordingService", sb2.toString());
            return 1;
        }
        this.mUseAudio = putExtra.getBooleanExtra("extra_useAudio", false);
        this.mShowTaps = putExtra.getBooleanExtra("extra_showTaps", false);
        try {
            final IBinder binder = IMediaProjectionManager$Stub.asInterface(ServiceManager.getService("media_projection")).createProjection(this.getUserId(), this.getPackageName(), 0, false).asBinder();
            if (binder == null) {
                Log.e("RecordingService", "Projection was null");
                Toast.makeText((Context)this, R$string.screenrecord_start_error, 1).show();
                return 2;
            }
            this.mMediaProjection = new MediaProjection(this.getApplicationContext(), IMediaProjection$Stub.asInterface(binder));
            this.startRecording();
            return 1;
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
            Toast.makeText((Context)this, R$string.screenrecord_start_error, 1).show();
            return 2;
        }
    }
}
