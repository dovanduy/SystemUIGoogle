// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.usb;

import android.os.Handler;
import android.content.IntentFilter;
import java.util.Iterator;
import android.app.Notification$Action;
import android.app.Notification;
import android.text.format.DateUtils;
import android.text.TextUtils;
import android.app.Notification$Extender;
import android.app.Notification$TvExtender;
import android.app.Notification$Style;
import android.app.Notification$BigTextStyle;
import com.android.systemui.util.NotificationChannels;
import android.app.Notification$Builder;
import android.os.StrictMode$VmPolicy;
import android.os.StrictMode;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.os.Bundle;
import android.content.Intent;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.os.UserHandle;
import android.os.storage.DiskInfo;
import android.content.Context;
import android.os.storage.StorageManager;
import android.app.NotificationManager;
import android.util.SparseArray;
import android.content.pm.PackageManager$MoveCallback;
import android.os.storage.StorageEventListener;
import android.content.BroadcastReceiver;
import com.android.systemui.SystemUI;

public class StorageNotification extends SystemUI
{
    private final BroadcastReceiver mFinishReceiver;
    private final StorageEventListener mListener;
    private final PackageManager$MoveCallback mMoveCallback;
    private final SparseArray<MoveInfo> mMoves;
    private NotificationManager mNotificationManager;
    private final BroadcastReceiver mSnoozeReceiver;
    private StorageManager mStorageManager;
    
    public StorageNotification(final Context context) {
        super(context);
        this.mMoves = (SparseArray<MoveInfo>)new SparseArray();
        this.mListener = new StorageEventListener() {
            public void onDiskDestroyed(final DiskInfo diskInfo) {
                StorageNotification.this.onDiskDestroyedInternal(diskInfo);
            }
            
            public void onDiskScanned(final DiskInfo diskInfo, final int n) {
                StorageNotification.this.onDiskScannedInternal(diskInfo, n);
            }
            
            public void onVolumeForgotten(final String s) {
                StorageNotification.this.mNotificationManager.cancelAsUser(s, 1397772886, UserHandle.ALL);
            }
            
            public void onVolumeRecordChanged(final VolumeRecord volumeRecord) {
                final VolumeInfo volumeByUuid = StorageNotification.this.mStorageManager.findVolumeByUuid(volumeRecord.getFsUuid());
                if (volumeByUuid != null && volumeByUuid.isMountedReadable()) {
                    StorageNotification.this.onVolumeStateChangedInternal(volumeByUuid);
                }
            }
            
            public void onVolumeStateChanged(final VolumeInfo volumeInfo, final int n, final int n2) {
                StorageNotification.this.onVolumeStateChangedInternal(volumeInfo);
            }
        };
        this.mSnoozeReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                StorageNotification.this.mStorageManager.setVolumeSnoozed(intent.getStringExtra("android.os.storage.extra.FS_UUID"), true);
            }
        };
        this.mFinishReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                StorageNotification.this.mNotificationManager.cancelAsUser((String)null, 1397575510, UserHandle.ALL);
            }
        };
        this.mMoveCallback = new PackageManager$MoveCallback() {
            public void onCreated(final int moveId, final Bundle bundle) {
                final MoveInfo moveInfo = new MoveInfo();
                moveInfo.moveId = moveId;
                if (bundle != null) {
                    moveInfo.packageName = bundle.getString("android.intent.extra.PACKAGE_NAME");
                    moveInfo.label = bundle.getString("android.intent.extra.TITLE");
                    moveInfo.volumeUuid = bundle.getString("android.os.storage.extra.FS_UUID");
                }
                StorageNotification.this.mMoves.put(moveId, (Object)moveInfo);
            }
            
            public void onStatusChanged(final int i, final int n, final long n2) {
                final MoveInfo moveInfo = (MoveInfo)StorageNotification.this.mMoves.get(i);
                if (moveInfo == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Ignoring unknown move ");
                    sb.append(i);
                    Log.w("StorageNotification", sb.toString());
                    return;
                }
                if (PackageManager.isMoveStatusFinished(n)) {
                    StorageNotification.this.onMoveFinished(moveInfo, n);
                }
                else {
                    StorageNotification.this.onMoveProgress(moveInfo, n, n2);
                }
            }
        };
    }
    
    private PendingIntent buildBrowsePendingIntent(final VolumeInfo volumeInfo) {
        final StrictMode$VmPolicy allowVmViolations = StrictMode.allowVmViolations();
        try {
            return PendingIntent.getActivityAsUser(super.mContext, volumeInfo.getId().hashCode(), volumeInfo.buildBrowseIntentForUser(volumeInfo.getMountUserId()), 268435456, (Bundle)null, UserHandle.CURRENT);
        }
        finally {
            StrictMode.setVmPolicy(allowVmViolations);
        }
    }
    
    private PendingIntent buildForgetPendingIntent(final VolumeRecord volumeRecord) {
        final Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$PrivateVolumeForgetActivity");
        intent.putExtra("android.os.storage.extra.FS_UUID", volumeRecord.getFsUuid());
        return PendingIntent.getActivityAsUser(super.mContext, volumeRecord.getFsUuid().hashCode(), intent, 268435456, (Bundle)null, UserHandle.CURRENT);
    }
    
    private PendingIntent buildInitPendingIntent(final DiskInfo diskInfo) {
        final Intent intent = new Intent();
        if (this.isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.NEW_STORAGE");
        }
        else {
            if (this.isAutomotive()) {
                return null;
            }
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardInit");
        }
        intent.putExtra("android.os.storage.extra.DISK_ID", diskInfo.getId());
        return PendingIntent.getActivityAsUser(super.mContext, diskInfo.getId().hashCode(), intent, 268435456, (Bundle)null, UserHandle.CURRENT);
    }
    
    private PendingIntent buildInitPendingIntent(final VolumeInfo volumeInfo) {
        final Intent intent = new Intent();
        if (this.isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.NEW_STORAGE");
        }
        else {
            if (this.isAutomotive()) {
                return null;
            }
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardInit");
        }
        intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
        return PendingIntent.getActivityAsUser(super.mContext, volumeInfo.getId().hashCode(), intent, 268435456, (Bundle)null, UserHandle.CURRENT);
    }
    
    private Notification$Builder buildNotificationBuilder(final VolumeInfo volumeInfo, final CharSequence contentTitle, final CharSequence contentText) {
        final Notification$Builder extend = new Notification$Builder(super.mContext, NotificationChannels.STORAGE).setSmallIcon(this.getSmallIcon(volumeInfo.getDisk(), volumeInfo.getState())).setColor(super.mContext.getColor(17170460)).setContentTitle(contentTitle).setContentText(contentText).setStyle((Notification$Style)new Notification$BigTextStyle().bigText(contentText)).setVisibility(1).setLocalOnly(true).extend((Notification$Extender)new Notification$TvExtender());
        SystemUI.overrideNotificationAppName(super.mContext, extend, false);
        return extend;
    }
    
    private PendingIntent buildSnoozeIntent(final String s) {
        final Intent intent = new Intent("com.android.systemui.action.SNOOZE_VOLUME");
        intent.putExtra("android.os.storage.extra.FS_UUID", s);
        return PendingIntent.getBroadcastAsUser(super.mContext, s.hashCode(), intent, 268435456, UserHandle.CURRENT);
    }
    
    private PendingIntent buildUnmountPendingIntent(final VolumeInfo volumeInfo) {
        final Intent intent = new Intent();
        if (this.isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.UNMOUNT_STORAGE");
            intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
            return PendingIntent.getActivityAsUser(super.mContext, volumeInfo.getId().hashCode(), intent, 268435456, (Bundle)null, UserHandle.CURRENT);
        }
        if (this.isAutomotive()) {
            intent.setClassName("com.android.car.settings", "com.android.car.settings.storage.StorageUnmountReceiver");
            intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
            return PendingIntent.getBroadcastAsUser(super.mContext, volumeInfo.getId().hashCode(), intent, 268435456, UserHandle.CURRENT);
        }
        intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageUnmountReceiver");
        intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
        return PendingIntent.getBroadcastAsUser(super.mContext, volumeInfo.getId().hashCode(), intent, 268435456, UserHandle.CURRENT);
    }
    
    private PendingIntent buildVolumeSettingsPendingIntent(final VolumeInfo volumeInfo) {
        final Intent intent = new Intent();
        if (this.isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("android.settings.INTERNAL_STORAGE_SETTINGS");
        }
        else {
            if (this.isAutomotive()) {
                return null;
            }
            final int type = volumeInfo.getType();
            if (type != 0) {
                if (type != 1) {
                    return null;
                }
                intent.setClassName("com.android.settings", "com.android.settings.Settings$PrivateVolumeSettingsActivity");
            }
            else {
                intent.setClassName("com.android.settings", "com.android.settings.Settings$PublicVolumeSettingsActivity");
            }
        }
        intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
        return PendingIntent.getActivityAsUser(super.mContext, volumeInfo.getId().hashCode(), intent, 268435456, (Bundle)null, UserHandle.CURRENT);
    }
    
    private PendingIntent buildWizardMigratePendingIntent(final MoveInfo moveInfo) {
        final Intent intent = new Intent();
        if (this.isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.MIGRATE_STORAGE");
        }
        else {
            if (this.isAutomotive()) {
                return null;
            }
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardMigrateProgress");
        }
        intent.putExtra("android.content.pm.extra.MOVE_ID", moveInfo.moveId);
        final VolumeInfo volumeByQualifiedUuid = this.mStorageManager.findVolumeByQualifiedUuid(moveInfo.volumeUuid);
        if (volumeByQualifiedUuid != null) {
            intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeByQualifiedUuid.getId());
        }
        return PendingIntent.getActivityAsUser(super.mContext, moveInfo.moveId, intent, 268435456, (Bundle)null, UserHandle.CURRENT);
    }
    
    private PendingIntent buildWizardMovePendingIntent(final MoveInfo moveInfo) {
        final Intent intent = new Intent();
        if (this.isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.MOVE_APP");
        }
        else {
            if (this.isAutomotive()) {
                return null;
            }
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardMoveProgress");
        }
        intent.putExtra("android.content.pm.extra.MOVE_ID", moveInfo.moveId);
        return PendingIntent.getActivityAsUser(super.mContext, moveInfo.moveId, intent, 268435456, (Bundle)null, UserHandle.CURRENT);
    }
    
    private PendingIntent buildWizardReadyPendingIntent(final DiskInfo diskInfo) {
        final Intent intent = new Intent();
        if (this.isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("android.settings.INTERNAL_STORAGE_SETTINGS");
        }
        else {
            if (this.isAutomotive()) {
                return null;
            }
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardReady");
        }
        intent.putExtra("android.os.storage.extra.DISK_ID", diskInfo.getId());
        return PendingIntent.getActivityAsUser(super.mContext, diskInfo.getId().hashCode(), intent, 268435456, (Bundle)null, UserHandle.CURRENT);
    }
    
    private int getSmallIcon(final DiskInfo diskInfo, final int n) {
        if (diskInfo.isSd()) {
            return 17302806;
        }
        if (diskInfo.isUsb()) {
            return 17302848;
        }
        return 17302806;
    }
    
    private boolean isAutomotive() {
        return super.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
    }
    
    private boolean isTv() {
        return super.mContext.getPackageManager().hasSystemFeature("android.software.leanback");
    }
    
    private void onDiskDestroyedInternal(final DiskInfo diskInfo) {
        this.mNotificationManager.cancelAsUser(diskInfo.getId(), 1396986699, UserHandle.ALL);
    }
    
    private void onDiskScannedInternal(final DiskInfo diskInfo, final int n) {
        if (n == 0 && diskInfo.size > 0L) {
            final String string = super.mContext.getString(17040141, new Object[] { diskInfo.getDescription() });
            final String string2 = super.mContext.getString(17040140, new Object[] { diskInfo.getDescription() });
            final Notification$Builder extend = new Notification$Builder(super.mContext, NotificationChannels.STORAGE).setSmallIcon(this.getSmallIcon(diskInfo, 6)).setColor(super.mContext.getColor(17170460)).setContentTitle((CharSequence)string).setContentText((CharSequence)string2).setContentIntent(this.buildInitPendingIntent(diskInfo)).setStyle((Notification$Style)new Notification$BigTextStyle().bigText((CharSequence)string2)).setVisibility(1).setLocalOnly(true).setCategory("err").extend((Notification$Extender)new Notification$TvExtender());
            SystemUI.overrideNotificationAppName(super.mContext, extend, false);
            this.mNotificationManager.notifyAsUser(diskInfo.getId(), 1396986699, extend.build(), UserHandle.ALL);
        }
        else {
            this.mNotificationManager.cancelAsUser(diskInfo.getId(), 1396986699, UserHandle.ALL);
        }
    }
    
    private void onMoveFinished(final MoveInfo moveInfo, final int n) {
        final String packageName = moveInfo.packageName;
        if (packageName != null) {
            this.mNotificationManager.cancelAsUser(packageName, 1397575510, UserHandle.ALL);
            return;
        }
        final VolumeInfo primaryStorageCurrentVolume = super.mContext.getPackageManager().getPrimaryStorageCurrentVolume();
        final String bestVolumeDescription = this.mStorageManager.getBestVolumeDescription(primaryStorageCurrentVolume);
        String contentTitle;
        String contentText;
        if (n == -100) {
            contentTitle = super.mContext.getString(17040116);
            contentText = super.mContext.getString(17040115, new Object[] { bestVolumeDescription });
        }
        else {
            contentTitle = super.mContext.getString(17040113);
            contentText = super.mContext.getString(17040112);
        }
        PendingIntent contentIntent;
        if (primaryStorageCurrentVolume != null && primaryStorageCurrentVolume.getDisk() != null) {
            contentIntent = this.buildWizardReadyPendingIntent(primaryStorageCurrentVolume.getDisk());
        }
        else if (primaryStorageCurrentVolume != null) {
            contentIntent = this.buildVolumeSettingsPendingIntent(primaryStorageCurrentVolume);
        }
        else {
            contentIntent = null;
        }
        final Notification$Builder setAutoCancel = new Notification$Builder(super.mContext, NotificationChannels.STORAGE).setSmallIcon(17302806).setColor(super.mContext.getColor(17170460)).setContentTitle((CharSequence)contentTitle).setContentText((CharSequence)contentText).setContentIntent(contentIntent).setStyle((Notification$Style)new Notification$BigTextStyle().bigText((CharSequence)contentText)).setVisibility(1).setLocalOnly(true).setCategory("sys").setAutoCancel(true);
        SystemUI.overrideNotificationAppName(super.mContext, setAutoCancel, false);
        this.mNotificationManager.notifyAsUser(moveInfo.packageName, 1397575510, setAutoCancel.build(), UserHandle.ALL);
    }
    
    private void onMoveProgress(final MoveInfo moveInfo, final int n, final long n2) {
        String contentTitle;
        if (!TextUtils.isEmpty((CharSequence)moveInfo.label)) {
            contentTitle = super.mContext.getString(17040114, new Object[] { moveInfo.label });
        }
        else {
            contentTitle = super.mContext.getString(17040117);
        }
        CharSequence formatDuration;
        if (n2 < 0L) {
            formatDuration = null;
        }
        else {
            formatDuration = DateUtils.formatDuration(n2);
        }
        PendingIntent contentIntent;
        if (moveInfo.packageName != null) {
            contentIntent = this.buildWizardMovePendingIntent(moveInfo);
        }
        else {
            contentIntent = this.buildWizardMigratePendingIntent(moveInfo);
        }
        final Notification$Builder setOngoing = new Notification$Builder(super.mContext, NotificationChannels.STORAGE).setSmallIcon(17302806).setColor(super.mContext.getColor(17170460)).setContentTitle((CharSequence)contentTitle).setContentText(formatDuration).setContentIntent(contentIntent).setStyle((Notification$Style)new Notification$BigTextStyle().bigText(formatDuration)).setVisibility(1).setLocalOnly(true).setCategory("progress").setProgress(100, n, false).setOngoing(true);
        SystemUI.overrideNotificationAppName(super.mContext, setOngoing, false);
        this.mNotificationManager.notifyAsUser(moveInfo.packageName, 1397575510, setOngoing.build(), UserHandle.ALL);
    }
    
    private void onPrivateVolumeStateChangedInternal(final VolumeInfo volumeInfo) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Notifying about private volume: ");
        sb.append(volumeInfo.toString());
        Log.d("StorageNotification", sb.toString());
        this.updateMissingPrivateVolumes();
    }
    
    private void onPublicVolumeStateChangedInternal(final VolumeInfo volumeInfo) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Notifying about public volume: ");
        sb.append(volumeInfo.toString());
        Log.d("StorageNotification", sb.toString());
        Notification notification = null;
        switch (volumeInfo.getState()) {
            default: {
                notification = null;
                break;
            }
            case 8: {
                notification = this.onVolumeBadRemoval(volumeInfo);
                break;
            }
            case 7: {
                notification = this.onVolumeRemoved(volumeInfo);
                break;
            }
            case 6: {
                notification = this.onVolumeUnmountable(volumeInfo);
                break;
            }
            case 5: {
                notification = this.onVolumeEjecting(volumeInfo);
                break;
            }
            case 4: {
                notification = this.onVolumeFormatting(volumeInfo);
                break;
            }
            case 2:
            case 3: {
                notification = this.onVolumeMounted(volumeInfo);
                break;
            }
            case 1: {
                notification = this.onVolumeChecking(volumeInfo);
                break;
            }
            case 0: {
                notification = this.onVolumeUnmounted(volumeInfo);
                break;
            }
        }
        if (notification != null) {
            this.mNotificationManager.notifyAsUser(volumeInfo.getId(), 1397773634, notification, UserHandle.of(volumeInfo.getMountUserId()));
        }
        else {
            this.mNotificationManager.cancelAsUser(volumeInfo.getId(), 1397773634, UserHandle.of(volumeInfo.getMountUserId()));
        }
    }
    
    private Notification onVolumeBadRemoval(final VolumeInfo volumeInfo) {
        if (!volumeInfo.isPrimary()) {
            return null;
        }
        final DiskInfo disk = volumeInfo.getDisk();
        return this.buildNotificationBuilder(volumeInfo, super.mContext.getString(17040105, new Object[] { disk.getDescription() }), super.mContext.getString(17040104, new Object[] { disk.getDescription() })).setCategory("err").build();
    }
    
    private Notification onVolumeChecking(final VolumeInfo volumeInfo) {
        final DiskInfo disk = volumeInfo.getDisk();
        return this.buildNotificationBuilder(volumeInfo, super.mContext.getString(17040108, new Object[] { disk.getDescription() }), super.mContext.getString(17040107, new Object[] { disk.getDescription() })).setCategory("progress").setOngoing(true).build();
    }
    
    private Notification onVolumeEjecting(final VolumeInfo volumeInfo) {
        final DiskInfo disk = volumeInfo.getDisk();
        return this.buildNotificationBuilder(volumeInfo, super.mContext.getString(17040139, new Object[] { disk.getDescription() }), super.mContext.getString(17040138, new Object[] { disk.getDescription() })).setCategory("progress").setOngoing(true).build();
    }
    
    private Notification onVolumeFormatting(final VolumeInfo volumeInfo) {
        return null;
    }
    
    private Notification onVolumeMounted(final VolumeInfo volumeInfo) {
        final VolumeRecord recordByUuid = this.mStorageManager.findRecordByUuid(volumeInfo.getFsUuid());
        final DiskInfo disk = volumeInfo.getDisk();
        if (recordByUuid.isSnoozed() && disk.isAdoptable()) {
            return null;
        }
        if (disk.isAdoptable() && !recordByUuid.isInited()) {
            final String description = disk.getDescription();
            final String string = super.mContext.getString(17040118, new Object[] { disk.getDescription() });
            final PendingIntent buildInitPendingIntent = this.buildInitPendingIntent(volumeInfo);
            return this.buildNotificationBuilder(volumeInfo, description, string).addAction(new Notification$Action(17302812, (CharSequence)super.mContext.getString(17040109), buildInitPendingIntent)).addAction(new Notification$Action(17302416, (CharSequence)super.mContext.getString(17040135), this.buildUnmountPendingIntent(volumeInfo))).setContentIntent(buildInitPendingIntent).setDeleteIntent(this.buildSnoozeIntent(volumeInfo.getFsUuid())).build();
        }
        final String description2 = disk.getDescription();
        final String string2 = super.mContext.getString(17040122, new Object[] { disk.getDescription() });
        final PendingIntent buildBrowsePendingIntent = this.buildBrowsePendingIntent(volumeInfo);
        final Notification$Builder setCategory = this.buildNotificationBuilder(volumeInfo, description2, string2).addAction(new Notification$Action(17302434, (CharSequence)super.mContext.getString(17040106), buildBrowsePendingIntent)).addAction(new Notification$Action(17302416, (CharSequence)super.mContext.getString(17040135), this.buildUnmountPendingIntent(volumeInfo))).setContentIntent(buildBrowsePendingIntent).setCategory("sys");
        if (disk.isAdoptable()) {
            setCategory.setDeleteIntent(this.buildSnoozeIntent(volumeInfo.getFsUuid()));
        }
        return setCategory.build();
    }
    
    private Notification onVolumeRemoved(final VolumeInfo volumeInfo) {
        if (!volumeInfo.isPrimary()) {
            return null;
        }
        final DiskInfo disk = volumeInfo.getDisk();
        return this.buildNotificationBuilder(volumeInfo, super.mContext.getString(17040121, new Object[] { disk.getDescription() }), super.mContext.getString(17040120, new Object[] { disk.getDescription() })).setCategory("err").build();
    }
    
    private void onVolumeStateChangedInternal(final VolumeInfo volumeInfo) {
        final int type = volumeInfo.getType();
        if (type != 0) {
            if (type == 1) {
                this.onPrivateVolumeStateChangedInternal(volumeInfo);
            }
        }
        else {
            this.onPublicVolumeStateChangedInternal(volumeInfo);
        }
    }
    
    private Notification onVolumeUnmountable(final VolumeInfo volumeInfo) {
        final DiskInfo disk = volumeInfo.getDisk();
        return this.buildNotificationBuilder(volumeInfo, super.mContext.getString(17040137, new Object[] { disk.getDescription() }), super.mContext.getString(17040136, new Object[] { disk.getDescription() })).setContentIntent(this.buildInitPendingIntent(volumeInfo)).setCategory("err").build();
    }
    
    private Notification onVolumeUnmounted(final VolumeInfo volumeInfo) {
        return null;
    }
    
    private void updateMissingPrivateVolumes() {
        if (!this.isTv()) {
            if (!this.isAutomotive()) {
                for (final VolumeRecord volumeRecord : this.mStorageManager.getVolumeRecords()) {
                    if (volumeRecord.getType() != 1) {
                        continue;
                    }
                    final String fsUuid = volumeRecord.getFsUuid();
                    final VolumeInfo volumeByUuid = this.mStorageManager.findVolumeByUuid(fsUuid);
                    if ((volumeByUuid != null && volumeByUuid.isMountedWritable()) || volumeRecord.isSnoozed()) {
                        this.mNotificationManager.cancelAsUser(fsUuid, 1397772886, UserHandle.ALL);
                    }
                    else {
                        final String string = super.mContext.getString(17040111, new Object[] { volumeRecord.getNickname() });
                        final String string2 = super.mContext.getString(17040110);
                        final Notification$Builder extend = new Notification$Builder(super.mContext, NotificationChannels.STORAGE).setSmallIcon(17302806).setColor(super.mContext.getColor(17170460)).setContentTitle((CharSequence)string).setContentText((CharSequence)string2).setContentIntent(this.buildForgetPendingIntent(volumeRecord)).setStyle((Notification$Style)new Notification$BigTextStyle().bigText((CharSequence)string2)).setVisibility(1).setLocalOnly(true).setCategory("sys").setDeleteIntent(this.buildSnoozeIntent(fsUuid)).extend((Notification$Extender)new Notification$TvExtender());
                        SystemUI.overrideNotificationAppName(super.mContext, extend, false);
                        this.mNotificationManager.notifyAsUser(fsUuid, 1397772886, extend.build(), UserHandle.ALL);
                    }
                }
            }
        }
    }
    
    @Override
    public void start() {
        this.mNotificationManager = (NotificationManager)super.mContext.getSystemService((Class)NotificationManager.class);
        (this.mStorageManager = (StorageManager)super.mContext.getSystemService((Class)StorageManager.class)).registerListener(this.mListener);
        super.mContext.registerReceiver(this.mSnoozeReceiver, new IntentFilter("com.android.systemui.action.SNOOZE_VOLUME"), "android.permission.MOUNT_UNMOUNT_FILESYSTEMS", (Handler)null);
        super.mContext.registerReceiver(this.mFinishReceiver, new IntentFilter("com.android.systemui.action.FINISH_WIZARD"), "android.permission.MOUNT_UNMOUNT_FILESYSTEMS", (Handler)null);
        for (final DiskInfo diskInfo : this.mStorageManager.getDisks()) {
            this.onDiskScannedInternal(diskInfo, diskInfo.volumeCount);
        }
        final Iterator<VolumeInfo> iterator2 = this.mStorageManager.getVolumes().iterator();
        while (iterator2.hasNext()) {
            this.onVolumeStateChangedInternal(iterator2.next());
        }
        super.mContext.getPackageManager().registerMoveCallback(this.mMoveCallback, new Handler());
        this.updateMissingPrivateVolumes();
    }
    
    private static class MoveInfo
    {
        public String label;
        public int moveId;
        public String packageName;
        public String volumeUuid;
    }
}
