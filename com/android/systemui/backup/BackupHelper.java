// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.backup;

import android.util.Log;
import android.os.Environment;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import kotlin.TypeCastException;
import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;
import java.util.Map;
import android.app.backup.FileBackupHelper;
import android.os.UserHandle;
import android.content.Intent;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.Pair;
import kotlin.collections.MapsKt;
import kotlin.TuplesKt;
import android.content.Context;
import android.app.backup.BackupAgentHelper;

public final class BackupHelper extends BackupAgentHelper
{
    public static final Companion Companion;
    private static final Object controlsDataLock;
    
    static {
        Companion = new Companion(null);
        controlsDataLock = new Object();
    }
    
    public static final /* synthetic */ Object access$getControlsDataLock$cp() {
        return BackupHelper.controlsDataLock;
    }
    
    public void onCreate() {
        super.onCreate();
        this.addHelper("systemui.files_no_overwrite", (android.app.backup.BackupHelper)new NoOverwriteFileBackupHelper(BackupHelper.controlsDataLock, (Context)this, MapsKt.mapOf((Pair<? extends String, ? extends Function0<Unit>>)TuplesKt.to((K)"controls_favorites.xml", (V)BackupHelperKt.access$getPPControlsFile((Context)this)))));
    }
    
    public void onRestoreFinished() {
        super.onRestoreFinished();
        final Intent intent = new Intent("com.android.systemui.backup.RESTORE_FINISHED");
        intent.setPackage(this.getPackageName());
        intent.putExtra("android.intent.extra.USER_ID", this.getUserId());
        intent.setFlags(1073741824);
        this.sendBroadcastAsUser(intent, UserHandle.SYSTEM, "com.android.systemui.permission.SELF");
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final Object getControlsDataLock() {
            return BackupHelper.access$getControlsDataLock$cp();
        }
    }
    
    private static final class NoOverwriteFileBackupHelper extends FileBackupHelper
    {
        private final Context context;
        private final Map<String, Function0<Unit>> fileNamesAndPostProcess;
        private final Object lock;
        
        public NoOverwriteFileBackupHelper(final Object lock, final Context context, final Map<String, ? extends Function0<Unit>> fileNamesAndPostProcess) {
            Intrinsics.checkParameterIsNotNull(lock, "lock");
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(fileNamesAndPostProcess, "fileNamesAndPostProcess");
            final String[] array = fileNamesAndPostProcess.keySet().toArray(new String[0]);
            if (array != null) {
                final String[] original = array;
                super(context, (String[])Arrays.copyOf(original, original.length));
                this.lock = lock;
                this.context = context;
                this.fileNamesAndPostProcess = (Map<String, Function0<Unit>>)fileNamesAndPostProcess;
                return;
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
        }
        
        public void performBackup(final ParcelFileDescriptor parcelFileDescriptor, final BackupDataOutput backupDataOutput, final ParcelFileDescriptor parcelFileDescriptor2) {
            synchronized (this.lock) {
                super.performBackup(parcelFileDescriptor, backupDataOutput, parcelFileDescriptor2);
                final Unit instance = Unit.INSTANCE;
            }
        }
        
        public void restoreEntity(final BackupDataInputStream backupDataInputStream) {
            Intrinsics.checkParameterIsNotNull(backupDataInputStream, "data");
            if (Environment.buildPath(this.context.getFilesDir(), new String[] { backupDataInputStream.getKey() }).exists()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("File ");
                sb.append(backupDataInputStream.getKey());
                sb.append(" already exists. Skipping restore.");
                Log.w("BackupHelper", sb.toString());
                return;
            }
            synchronized (this.lock) {
                super.restoreEntity(backupDataInputStream);
                final Function0<Unit> function0 = this.fileNamesAndPostProcess.get(backupDataInputStream.getKey());
                if (function0 != null) {
                    final Unit unit = function0.invoke();
                }
            }
        }
    }
}
