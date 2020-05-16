// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.os.ParcelFileDescriptor;
import android.content.ContentResolver;
import com.android.internal.annotations.VisibleForTesting;
import android.content.ComponentName;
import android.text.TextUtils;
import android.os.UserHandle;
import android.graphics.drawable.Icon;
import com.android.systemui.R$drawable;
import android.content.ClipData;
import android.content.ClipData$Item;
import android.content.ClipDescription;
import java.text.DateFormat;
import java.util.concurrent.CompletableFuture;
import android.content.res.Resources;
import android.graphics.Bitmap;
import com.android.systemui.R$string;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.Instant;
import android.os.Build;
import android.media.ExifInterface;
import android.os.CancellationSignal;
import java.io.OutputStream;
import android.graphics.Bitmap$CompressFormat;
import android.net.Uri;
import android.provider.MediaStore$Images$Media;
import java.io.File;
import android.os.Environment;
import android.content.ContentValues;
import android.os.UserManager;
import android.os.RemoteException;
import android.util.Slog;
import android.app.ActivityTaskManager;
import android.os.Bundle;
import java.util.Iterator;
import android.app.Notification$Action$Builder;
import android.app.PendingIntent;
import android.os.Parcelable;
import java.util.ArrayList;
import android.app.Notification$Action;
import java.util.List;
import android.content.Intent;
import android.os.Handler;
import com.android.systemui.SystemUIFactory;
import android.provider.DeviceConfig;
import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import android.content.Context;
import android.os.AsyncTask;

class SaveImageInBackgroundTask extends AsyncTask<Void, Void, Void>
{
    private final Context mContext;
    private final boolean mCreateDeleteAction;
    private final String mImageFileName;
    private final long mImageTime;
    private final GlobalScreenshot.SaveImageInBackgroundData mParams;
    private final Random mRandom;
    private final String mScreenshotId;
    private final boolean mSmartActionsEnabled;
    private final ScreenshotNotificationSmartActionsProvider mSmartActionsProvider;
    
    SaveImageInBackgroundTask(final Context mContext, final GlobalScreenshot.SaveImageInBackgroundData mParams) {
        this.mRandom = new Random();
        this.mContext = mContext;
        this.mParams = mParams;
        this.mImageTime = System.currentTimeMillis();
        this.mImageFileName = String.format("Screenshot_%s.png", new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(this.mImageTime)));
        this.mScreenshotId = String.format("Screenshot_%s", UUID.randomUUID());
        this.mCreateDeleteAction = mParams.createDeleteAction;
        final boolean boolean1 = DeviceConfig.getBoolean("systemui", "enable_screenshot_notification_smart_actions", true);
        this.mSmartActionsEnabled = boolean1;
        if (boolean1) {
            this.mSmartActionsProvider = SystemUIFactory.getInstance().createScreenshotNotificationSmartActionsProvider(mContext, AsyncTask.THREAD_POOL_EXECUTOR, new Handler());
        }
        else {
            this.mSmartActionsProvider = new ScreenshotNotificationSmartActionsProvider();
        }
    }
    
    private static void addIntentExtras(final String s, final Intent intent, final String s2, final boolean b) {
        intent.putExtra("android:screenshot_action_type", s2).putExtra("android:screenshot_id", s).putExtra("android:smart_actions_enabled", b);
    }
    
    private List<Notification$Action> buildSmartActions(final List<Notification$Action> list, final Context context) {
        final ArrayList<Notification$Action> list2 = new ArrayList<Notification$Action>();
        for (final Notification$Action notification$Action : list) {
            final Bundle extras = notification$Action.getExtras();
            final String string = extras.getString("action_type", "Smart Action");
            final Intent addFlags = new Intent(context, (Class)GlobalScreenshot.SmartActionsReceiver.class).putExtra("android:screenshot_action_intent", (Parcelable)notification$Action.actionIntent).addFlags(268435456);
            addIntentExtras(this.mScreenshotId, addFlags, string, this.mSmartActionsEnabled);
            list2.add(new Notification$Action$Builder(notification$Action.getIcon(), notification$Action.title, PendingIntent.getBroadcast(context, this.mRandom.nextInt(), addFlags, 268435456)).setContextual(true).addExtras(extras).build());
        }
        return list2;
    }
    
    private int getUserHandleOfForegroundApplication(final Context context) {
        try {
            return ActivityTaskManager.getService().getLastResumedActivityUserId();
        }
        catch (RemoteException ex) {
            Slog.w("SaveImageInBackgroundTask", "getUserHandleOfForegroundApplication: ", (Throwable)ex);
            return context.getUserId();
        }
    }
    
    private boolean isManagedProfile(final Context context) {
        return UserManager.get(context).getUserInfo(this.getUserHandleOfForegroundApplication(context)).isManagedProfile();
    }
    
    protected Void doInBackground(Void... insert) {
        if (this.isCancelled()) {
            return null;
        }
        Object contentResolver = this.mContext.getContentResolver();
        final Bitmap image = this.mParams.image;
        final Resources resources = this.mContext.getResources();
        try {
            final CompletableFuture<List<Notification$Action>> smartActionsFuture = ScreenshotSmartActions.getSmartActionsFuture(this.mScreenshotId, this.mImageFileName, image, this.mSmartActionsProvider, this.mSmartActionsEnabled, this.isManagedProfile(this.mContext));
            final ContentValues contentValues = new ContentValues();
            final StringBuilder sb = new StringBuilder();
            sb.append(Environment.DIRECTORY_PICTURES);
            sb.append(File.separator);
            sb.append(Environment.DIRECTORY_SCREENSHOTS);
            contentValues.put("relative_path", sb.toString());
            contentValues.put("_display_name", this.mImageFileName);
            contentValues.put("mime_type", "image/png");
            contentValues.put("date_added", Long.valueOf(this.mImageTime / 1000L));
            contentValues.put("date_modified", Long.valueOf(this.mImageTime / 1000L));
            contentValues.put("date_expires", Long.valueOf((this.mImageTime + 86400000L) / 1000L));
            contentValues.put("is_pending", Integer.valueOf(1));
            insert = (Void[])(Object)((ContentResolver)contentResolver).insert(MediaStore$Images$Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                Object o = ((ContentResolver)contentResolver).openOutputStream((Uri)(Object)insert);
                try {
                    if (image.compress(Bitmap$CompressFormat.PNG, 100, (OutputStream)o)) {
                        if (o != null) {
                            ((OutputStream)o).close();
                        }
                        o = ((ContentResolver)contentResolver).openFile((Uri)(Object)insert, "rw", (CancellationSignal)null);
                        try {
                            final ExifInterface exifInterface = new ExifInterface(((ParcelFileDescriptor)o).getFileDescriptor());
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append("Android ");
                            sb2.append(Build.DISPLAY);
                            exifInterface.setAttribute("Software", sb2.toString());
                            exifInterface.setAttribute("ImageWidth", Integer.toString(image.getWidth()));
                            exifInterface.setAttribute("ImageLength", Integer.toString(image.getHeight()));
                            final ZonedDateTime ofInstant = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.mImageTime), ZoneId.systemDefault());
                            exifInterface.setAttribute("DateTimeOriginal", DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").format(ofInstant));
                            exifInterface.setAttribute("SubSecTimeOriginal", DateTimeFormatter.ofPattern("SSS").format(ofInstant));
                            if (Objects.equals(ofInstant.getOffset(), ZoneOffset.UTC)) {
                                exifInterface.setAttribute("OffsetTimeOriginal", "+00:00");
                            }
                            else {
                                exifInterface.setAttribute("OffsetTimeOriginal", DateTimeFormatter.ofPattern("XXX").format(ofInstant));
                            }
                            exifInterface.saveAttributes();
                            if (o != null) {
                                ((ParcelFileDescriptor)o).close();
                            }
                            contentValues.clear();
                            contentValues.put("is_pending", Integer.valueOf(0));
                            contentValues.putNull("date_expires");
                            ((ContentResolver)contentResolver).update((Uri)(Object)insert, contentValues, (String)null, (String[])null);
                            final List<Notification$Action> populateNotificationActions = this.populateNotificationActions(this.mContext, resources, (Uri)(Object)insert);
                            contentResolver = new ArrayList<Notification$Action>();
                            if (this.mSmartActionsEnabled) {
                                ((List<Notification$Action>)contentResolver).addAll(this.buildSmartActions(ScreenshotSmartActions.getSmartActions(this.mScreenshotId, this.mImageFileName, smartActionsFuture, DeviceConfig.getInt("systemui", "screenshot_notification_smart_actions_timeout_ms", 1000), this.mSmartActionsProvider), this.mContext));
                            }
                            this.mParams.mActionsReadyListener.onActionsReady((Uri)(Object)insert, (List<Notification$Action>)contentResolver, populateNotificationActions);
                            this.mParams.imageUri = (Uri)(Object)insert;
                            this.mParams.image = null;
                            this.mParams.errorMsgResId = 0;
                            return null;
                        }
                        finally {
                            if (o != null) {
                                try {
                                    ((ParcelFileDescriptor)o).close();
                                }
                                finally {
                                    final Throwable t;
                                    final Throwable exception;
                                    t.addSuppressed(exception);
                                }
                            }
                        }
                    }
                    throw new IOException("Failed to compress");
                }
                finally {
                    if (o != null) {
                        try {
                            ((OutputStream)o).close();
                        }
                        finally {
                            final Throwable t2;
                            final Throwable exception2;
                            t2.addSuppressed(exception2);
                        }
                    }
                }
            }
            catch (Exception ex) {
                ((ContentResolver)contentResolver).delete((Uri)(Object)insert, (Bundle)null);
                throw ex;
            }
        }
        catch (Exception ex2) {
            Slog.e("SaveImageInBackgroundTask", "unable to save screenshot", (Throwable)ex2);
            this.mParams.clearImage();
            final GlobalScreenshot.SaveImageInBackgroundData mParams = this.mParams;
            mParams.errorMsgResId = R$string.screenshot_failed_to_save_text;
            mParams.mActionsReadyListener.onActionsReady(null, null, null);
        }
        return null;
    }
    
    protected void onCancelled(final Void void1) {
        this.mParams.mActionsReadyListener.onActionsReady(null, null, null);
        this.mParams.finisher.accept(null);
        this.mParams.clearImage();
    }
    
    protected void onPostExecute(final Void void1) {
        final GlobalScreenshot.SaveImageInBackgroundData mParams = this.mParams;
        mParams.finisher.accept(mParams.imageUri);
    }
    
    @VisibleForTesting
    List<Notification$Action> populateNotificationActions(final Context context, final Resources resources, final Uri data) {
        final String format = DateFormat.getDateTimeInstance().format(new Date(this.mImageTime));
        boolean b = true;
        final String format2 = String.format("Screenshot (%s)", format);
        final Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/png");
        intent.putExtra("android.intent.extra.STREAM", (Parcelable)data);
        intent.setClipData(new ClipData(new ClipDescription((CharSequence)"content", new String[] { "text/plain" }), new ClipData$Item(data)));
        intent.putExtra("android.intent.extra.SUBJECT", format2);
        intent.addFlags(1);
        final int userId = context.getUserId();
        final ArrayList<Notification$Action> list = new ArrayList<Notification$Action>();
        list.add(new Notification$Action$Builder(Icon.createWithResource(resources, R$drawable.ic_screenshot_share), (CharSequence)resources.getString(17041225), PendingIntent.getBroadcastAsUser(context, userId, new Intent(context, (Class)GlobalScreenshot.ActionProxyReceiver.class).putExtra("android:screenshot_action_intent", (Parcelable)Intent.createChooser(intent, (CharSequence)null, PendingIntent.getBroadcast(context, userId, new Intent(context, (Class)GlobalScreenshot.TargetChosenReceiver.class), 1342177280).getIntentSender()).addFlags(268468224).addFlags(1)).putExtra("android:screenshot_disallow_enter_pip", true).putExtra("android:screenshot_id", this.mScreenshotId).putExtra("android:smart_actions_enabled", this.mSmartActionsEnabled).setAction("android.intent.action.SEND").addFlags(268435456), 268435456, UserHandle.SYSTEM)).build());
        final String string = context.getString(R$string.config_screenshotEditor);
        final Intent intent2 = new Intent("android.intent.action.EDIT");
        if (!TextUtils.isEmpty((CharSequence)string)) {
            intent2.setComponent(ComponentName.unflattenFromString(string));
        }
        intent2.setType("image/png");
        intent2.setData(data);
        intent2.addFlags(1);
        intent2.addFlags(2);
        final Intent putExtra = new Intent(context, (Class)GlobalScreenshot.ActionProxyReceiver.class).putExtra("android:screenshot_action_intent", (Parcelable)intent2);
        if (intent2.getComponent() == null) {
            b = false;
        }
        list.add(new Notification$Action$Builder(Icon.createWithResource(resources, R$drawable.ic_screenshot_edit), (CharSequence)resources.getString(17041186), PendingIntent.getBroadcastAsUser(context, userId, putExtra.putExtra("android:screenshot_cancel_notification", b).putExtra("android:screenshot_id", this.mScreenshotId).putExtra("android:smart_actions_enabled", this.mSmartActionsEnabled).setAction("android.intent.action.EDIT").addFlags(268435456), 268435456, UserHandle.SYSTEM)).build());
        if (this.mCreateDeleteAction) {
            list.add(new Notification$Action$Builder(Icon.createWithResource(resources, R$drawable.ic_screenshot_delete), (CharSequence)resources.getString(17040043), PendingIntent.getBroadcast(context, userId, new Intent(context, (Class)GlobalScreenshot.DeleteScreenshotReceiver.class).putExtra("android:screenshot_uri_id", data.toString()).putExtra("android:screenshot_id", this.mScreenshotId).putExtra("android:smart_actions_enabled", this.mSmartActionsEnabled).addFlags(268435456), 1342177280)).build());
        }
        return list;
    }
}
