// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import android.app.job.JobInfo$Builder;
import android.app.job.JobInfo;
import com.android.systemui.backup.BackupHelper;
import android.app.job.JobParameters;
import android.content.Context;
import java.util.concurrent.TimeUnit;
import android.app.job.JobService;
import java.util.Iterator;
import kotlin.Pair;
import java.util.ArrayList;
import android.content.ComponentName;
import android.app.backup.BackupManager;
import java.util.concurrent.Executor;
import java.io.File;
import com.android.internal.annotations.VisibleForTesting;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import java.util.List;

public final class AuxiliaryPersistenceWrapper
{
    private List<StructureInfo> favorites;
    private ControlsFavoritePersistenceWrapper persistenceWrapper;
    
    @VisibleForTesting
    public AuxiliaryPersistenceWrapper(final ControlsFavoritePersistenceWrapper persistenceWrapper) {
        Intrinsics.checkParameterIsNotNull(persistenceWrapper, "wrapper");
        this.persistenceWrapper = persistenceWrapper;
        this.favorites = CollectionsKt.emptyList();
        this.initialize();
    }
    
    public AuxiliaryPersistenceWrapper(final File file, final Executor executor) {
        Intrinsics.checkParameterIsNotNull(file, "file");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        this(new ControlsFavoritePersistenceWrapper(file, executor, null, 4, null));
    }
    
    public final void changeFile(final File file) {
        Intrinsics.checkParameterIsNotNull(file, "file");
        this.persistenceWrapper.changeFileAndBackupManager(file, null);
        this.initialize();
    }
    
    public final List<StructureInfo> getCachedFavoritesAndRemoveFor(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        if (!this.persistenceWrapper.getFileExists()) {
            return CollectionsKt.emptyList();
        }
        final List<StructureInfo> favorites = this.favorites;
        final ArrayList<StructureInfo> list = new ArrayList<StructureInfo>();
        final ArrayList<StructureInfo> list2 = new ArrayList<StructureInfo>();
        for (final StructureInfo next : favorites) {
            if (Intrinsics.areEqual(next.getComponentName(), componentName)) {
                list.add(next);
            }
            else {
                list2.add(next);
            }
        }
        final Pair pair = new Pair<ArrayList<StructureInfo>, ArrayList<StructureInfo>>(list, list2);
        final ArrayList<StructureInfo> list3 = pair.component1();
        final ArrayList<StructureInfo> favorites2 = pair.component2();
        this.favorites = favorites2;
        if (favorites2.isEmpty() ^ true) {
            this.persistenceWrapper.storeFavorites(favorites2);
        }
        else {
            this.persistenceWrapper.deleteFile();
        }
        return list3;
    }
    
    public final List<StructureInfo> getFavorites() {
        return this.favorites;
    }
    
    public final void initialize() {
        List<StructureInfo> favorites;
        if (this.persistenceWrapper.getFileExists()) {
            favorites = this.persistenceWrapper.readFavorites();
        }
        else {
            favorites = CollectionsKt.emptyList();
        }
        this.favorites = favorites;
    }
    
    public static final class DeletionJobService extends JobService
    {
        public static final Companion Companion;
        private static final int DELETE_FILE_JOB_ID = 1000;
        private static final long WEEK_IN_MILLIS;
        
        static {
            Companion = new Companion(null);
            WEEK_IN_MILLIS = TimeUnit.DAYS.toMillis(7L);
        }
        
        public static final /* synthetic */ int access$getDELETE_FILE_JOB_ID$cp() {
            return DeletionJobService.DELETE_FILE_JOB_ID;
        }
        
        public static final /* synthetic */ long access$getWEEK_IN_MILLIS$cp() {
            return DeletionJobService.WEEK_IN_MILLIS;
        }
        
        @VisibleForTesting
        public final void attachContext(final Context context) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            this.attachBaseContext(context);
        }
        
        public boolean onStartJob(final JobParameters jobParameters) {
            Intrinsics.checkParameterIsNotNull(jobParameters, "params");
            synchronized (BackupHelper.Companion.getControlsDataLock()) {
                this.getBaseContext().deleteFile("aux_controls_favorites.xml");
                return false;
            }
        }
        
        public boolean onStopJob(final JobParameters jobParameters) {
            return true;
        }
        
        public static final class Companion
        {
            private Companion() {
            }
            
            public final int getDELETE_FILE_JOB_ID$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
                return DeletionJobService.access$getDELETE_FILE_JOB_ID$cp();
            }
            
            public final JobInfo getJobForContext(final Context context) {
                Intrinsics.checkParameterIsNotNull(context, "context");
                final JobInfo build = new JobInfo$Builder(this.getDELETE_FILE_JOB_ID$frameworks__base__packages__SystemUI__android_common__SystemUI_core() + context.getUserId(), new ComponentName(context, (Class)DeletionJobService.class)).setMinimumLatency(DeletionJobService.access$getWEEK_IN_MILLIS$cp()).setPersisted(true).build();
                Intrinsics.checkExpressionValueIsNotNull(build, "JobInfo.Builder(jobId, c\u2026                 .build()");
                return build;
            }
        }
    }
}
