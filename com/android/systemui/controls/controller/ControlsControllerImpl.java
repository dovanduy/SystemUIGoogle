// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import java.util.concurrent.TimeUnit;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.service.controls.actions.ControlAction;
import android.app.backup.BackupManager;
import kotlin.collections.SetsKt;
import kotlin.collections.CollectionsKt;
import java.util.Iterator;
import android.service.controls.Control$StatelessBuilder;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import android.content.ContentResolver;
import android.service.controls.Control;
import java.util.Set;
import com.android.systemui.controls.ControlStatus;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Handler;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import android.os.Environment;
import android.app.ActivityManager;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.dump.DumpManager;
import java.util.Optional;
import android.provider.Settings$Secure;
import com.android.systemui.controls.ui.ControlsUiController;
import android.database.ContentObserver;
import java.util.function.Consumer;
import java.util.List;
import android.content.BroadcastReceiver;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import android.os.UserHandle;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import java.io.File;
import android.net.Uri;
import com.android.systemui.Dumpable;

public final class ControlsControllerImpl implements Dumpable, ControlsController
{
    private static final Uri URI;
    private File auxiliaryFile;
    private AuxiliaryPersistenceWrapper auxiliaryPersistenceWrapper;
    private boolean available;
    private final ControlsBindingController bindingController;
    private final BroadcastDispatcher broadcastDispatcher;
    private final Context context;
    private UserHandle currentUser;
    private final DelayableExecutor executor;
    private File file;
    private final ControlsControllerImpl$listingCallback.ControlsControllerImpl$listingCallback$1 listingCallback;
    private final ControlsListingController listingController;
    private Runnable loadCanceller;
    private final ControlsFavoritePersistenceWrapper persistenceWrapper;
    private final BroadcastReceiver restoreFinishedReceiver;
    private final List<Consumer<Boolean>> seedingCallbacks;
    private boolean seedingInProgress;
    private final ContentObserver settingObserver;
    private final ControlsUiController uiController;
    private boolean userChanging;
    private final ControlsControllerImpl$userSwitchReceiver.ControlsControllerImpl$userSwitchReceiver$1 userSwitchReceiver;
    
    static {
        URI = Settings$Secure.getUriFor("controls_enabled");
    }
    
    public ControlsControllerImpl(final Context context, final DelayableExecutor executor, final ControlsUiController uiController, final ControlsBindingController bindingController, final ControlsListingController listingController, final BroadcastDispatcher broadcastDispatcher, final Optional<ControlsFavoritePersistenceWrapper> optional, final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        Intrinsics.checkParameterIsNotNull(uiController, "uiController");
        Intrinsics.checkParameterIsNotNull(bindingController, "bindingController");
        Intrinsics.checkParameterIsNotNull(listingController, "listingController");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher, "broadcastDispatcher");
        Intrinsics.checkParameterIsNotNull(optional, "optionalWrapper");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.context = context;
        this.executor = executor;
        this.uiController = uiController;
        this.bindingController = bindingController;
        this.listingController = listingController;
        this.broadcastDispatcher = broadcastDispatcher;
        boolean available = true;
        this.userChanging = true;
        this.seedingCallbacks = new ArrayList<Consumer<Boolean>>();
        this.currentUser = UserHandle.of(ActivityManager.getCurrentUser());
        if (Settings$Secure.getIntForUser(this.getContentResolver(), "controls_enabled", 1, this.getCurrentUserId()) == 0) {
            available = false;
        }
        this.available = available;
        this.file = Environment.buildPath(this.context.getFilesDir(), new String[] { "controls_favorites.xml" });
        this.auxiliaryFile = Environment.buildPath(this.context.getFilesDir(), new String[] { "aux_controls_favorites.xml" });
        this.persistenceWrapper = optional.orElseGet((Supplier<? extends ControlsFavoritePersistenceWrapper>)new ControlsControllerImpl$persistenceWrapper.ControlsControllerImpl$persistenceWrapper$1(this));
        final File auxiliaryFile = this.auxiliaryFile;
        Intrinsics.checkExpressionValueIsNotNull(auxiliaryFile, "auxiliaryFile");
        this.auxiliaryPersistenceWrapper = new AuxiliaryPersistenceWrapper(auxiliaryFile, this.executor);
        this.userSwitchReceiver = new ControlsControllerImpl$userSwitchReceiver.ControlsControllerImpl$userSwitchReceiver$1(this);
        this.restoreFinishedReceiver = (BroadcastReceiver)new ControlsControllerImpl$restoreFinishedReceiver.ControlsControllerImpl$restoreFinishedReceiver$1(this);
        this.settingObserver = (ContentObserver)new ControlsControllerImpl$settingObserver.ControlsControllerImpl$settingObserver$1(this, (Handler)null);
        this.listingCallback = new ControlsControllerImpl$listingCallback.ControlsControllerImpl$listingCallback$1(this);
        final String name = ControlsControllerImpl.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        this.resetFavorites(this.getAvailable());
        this.userChanging = false;
        final BroadcastDispatcher broadcastDispatcher2 = this.broadcastDispatcher;
        final ControlsControllerImpl$userSwitchReceiver.ControlsControllerImpl$userSwitchReceiver$1 userSwitchReceiver = this.userSwitchReceiver;
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.USER_SWITCHED");
        final DelayableExecutor executor2 = this.executor;
        final UserHandle all = UserHandle.ALL;
        Intrinsics.checkExpressionValueIsNotNull(all, "UserHandle.ALL");
        broadcastDispatcher2.registerReceiver((BroadcastReceiver)userSwitchReceiver, intentFilter, executor2, all);
        this.context.registerReceiver(this.restoreFinishedReceiver, new IntentFilter("com.android.systemui.backup.RESTORE_FINISHED"), "com.android.systemui.permission.SELF", (Handler)null);
        this.getContentResolver().registerContentObserver(ControlsControllerImpl.URI, false, this.settingObserver, -1);
    }
    
    private final boolean confirmAvailability() {
        if (this.userChanging) {
            Log.w("ControlsControllerImpl", "Controls not available while user is changing");
            return false;
        }
        if (!this.getAvailable()) {
            Log.d("ControlsControllerImpl", "Controls not available");
            return false;
        }
        return true;
    }
    
    private final ControlStatus createRemovedStatus(final ComponentName componentName, final ControlInfo controlInfo, final CharSequence structure, final boolean b) {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(componentName.getPackageName());
        final Control build = new Control$StatelessBuilder(controlInfo.getControlId(), PendingIntent.getActivity(this.context, componentName.hashCode(), intent, 0)).setTitle(controlInfo.getControlTitle()).setSubtitle(controlInfo.getControlSubtitle()).setStructure(structure).setDeviceType(controlInfo.getDeviceType()).build();
        Intrinsics.checkExpressionValueIsNotNull(build, "control");
        return new ControlStatus(build, componentName, true, b);
    }
    
    private final void endSeedingCall(final boolean b) {
        this.seedingInProgress = false;
        final Iterator<Consumer<Boolean>> iterator = this.seedingCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().accept(b);
        }
        this.seedingCallbacks.clear();
    }
    
    private final Set<String> findRemoved(final Set<String> set, final List<Control> list) {
        final ArrayList<String> list2 = new ArrayList<String>(CollectionsKt.collectionSizeOrDefault((Iterable<?>)list, 10));
        final Iterator<? extends T> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(((Control)iterator.next()).getControlId());
        }
        return (Set<String>)SetsKt.minus((Set<?>)set, (Iterable<?>)list2);
    }
    
    private final ContentResolver getContentResolver() {
        final ContentResolver contentResolver = this.context.getContentResolver();
        Intrinsics.checkExpressionValueIsNotNull(contentResolver, "context.contentResolver");
        return contentResolver;
    }
    
    private final void resetFavorites(final boolean b) {
        Favorites.INSTANCE.clear();
        if (b) {
            Favorites.INSTANCE.load(this.persistenceWrapper.readFavorites());
            this.listingController.addCallback((ControlsListingController.ControlsListingCallback)this.listingCallback);
        }
    }
    
    private final void setValuesForUser(final UserHandle userHandle) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Changing to user: ");
        sb.append(userHandle);
        Log.d("ControlsControllerImpl", sb.toString());
        this.currentUser = userHandle;
        final Context contextAsUser = this.context.createContextAsUser(userHandle, 0);
        Intrinsics.checkExpressionValueIsNotNull(contextAsUser, "userContext");
        this.file = Environment.buildPath(contextAsUser.getFilesDir(), new String[] { "controls_favorites.xml" });
        this.auxiliaryFile = Environment.buildPath(contextAsUser.getFilesDir(), new String[] { "aux_controls_favorites.xml" });
        final ControlsFavoritePersistenceWrapper persistenceWrapper = this.persistenceWrapper;
        final File file = this.file;
        Intrinsics.checkExpressionValueIsNotNull(file, "file");
        persistenceWrapper.changeFileAndBackupManager(file, new BackupManager(contextAsUser));
        final AuxiliaryPersistenceWrapper auxiliaryPersistenceWrapper = this.auxiliaryPersistenceWrapper;
        final File auxiliaryFile = this.auxiliaryFile;
        Intrinsics.checkExpressionValueIsNotNull(auxiliaryFile, "auxiliaryFile");
        auxiliaryPersistenceWrapper.changeFile(auxiliaryFile);
        final ContentResolver contentResolver = this.getContentResolver();
        final int identifier = userHandle.getIdentifier();
        boolean available = true;
        if (Settings$Secure.getIntForUser(contentResolver, "controls_enabled", 1, identifier) == 0) {
            available = false;
        }
        this.available = available;
        this.resetFavorites(this.getAvailable());
        this.bindingController.changeUser(userHandle);
        this.listingController.changeUser(userHandle);
        this.userChanging = false;
    }
    
    @Override
    public void action(final ComponentName componentName, final ControlInfo controlInfo, final ControlAction controlAction) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "controlInfo");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        if (!this.confirmAvailability()) {
            return;
        }
        this.bindingController.action(componentName, controlInfo, controlAction);
    }
    
    @Override
    public void addFavorite(final ComponentName componentName, final CharSequence charSequence, final ControlInfo controlInfo) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(charSequence, "structureName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "controlInfo");
        if (!this.confirmAvailability()) {
            return;
        }
        this.executor.execute((Runnable)new ControlsControllerImpl$addFavorite.ControlsControllerImpl$addFavorite$1(this, componentName, charSequence, controlInfo));
    }
    
    @Override
    public boolean addSeedingFavoritesCallback(final Consumer<Boolean> consumer) {
        Intrinsics.checkParameterIsNotNull(consumer, "callback");
        if (!this.seedingInProgress) {
            return false;
        }
        this.executor.execute((Runnable)new ControlsControllerImpl$addSeedingFavoritesCallback.ControlsControllerImpl$addSeedingFavoritesCallback$1(this, (Consumer)consumer));
        return true;
    }
    
    public void cancelLoad() {
        final Runnable loadCanceller = this.loadCanceller;
        if (loadCanceller != null) {
            this.executor.execute(loadCanceller);
        }
    }
    
    @Override
    public void changeUser(final UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
        DefaultImpls.changeUser(this, userHandle);
    }
    
    @Override
    public int countFavoritesForComponent(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        return Favorites.INSTANCE.getControlsForComponent(componentName).size();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        printWriter.println("ControlsController state:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  Available: ");
        sb.append(this.getAvailable());
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  Changing users: ");
        sb2.append(this.userChanging);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  Current user: ");
        final UserHandle currentUser = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(currentUser, "currentUser");
        sb3.append(currentUser.getIdentifier());
        printWriter.println(sb3.toString());
        printWriter.println("  Favorites:");
        for (final StructureInfo obj : Favorites.INSTANCE.getAllStructures()) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("    ");
            sb4.append(obj);
            printWriter.println(sb4.toString());
            for (final ControlInfo obj2 : obj.getControls()) {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("      ");
                sb5.append(obj2);
                printWriter.println(sb5.toString());
            }
        }
        printWriter.println(this.bindingController.toString());
    }
    
    public final AuxiliaryPersistenceWrapper getAuxiliaryPersistenceWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
        return this.auxiliaryPersistenceWrapper;
    }
    
    @Override
    public boolean getAvailable() {
        return this.available;
    }
    
    @Override
    public int getCurrentUserId() {
        final UserHandle currentUser = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(currentUser, "currentUser");
        return currentUser.getIdentifier();
    }
    
    @Override
    public List<StructureInfo> getFavorites() {
        return Favorites.INSTANCE.getAllStructures();
    }
    
    @Override
    public List<StructureInfo> getFavoritesForComponent(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        return Favorites.INSTANCE.getStructuresForComponent(componentName);
    }
    
    public void loadForComponent(final ComponentName componentName, final Consumer<LoadData> consumer) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(consumer, "dataCallback");
        if (!this.confirmAvailability()) {
            if (this.userChanging) {
                this.loadCanceller = this.executor.executeDelayed((Runnable)new ControlsControllerImpl$loadForComponent.ControlsControllerImpl$loadForComponent$1(this, componentName, (Consumer)consumer), 500L, TimeUnit.MILLISECONDS);
            }
            else {
                consumer.accept(ControlsControllerKt.createLoadDataObject(CollectionsKt.emptyList(), CollectionsKt.emptyList(), true));
            }
            return;
        }
        this.loadCanceller = this.bindingController.bindAndLoad(componentName, (ControlsBindingController.LoadCallback)new ControlsControllerImpl$loadForComponent.ControlsControllerImpl$loadForComponent$2(this, componentName, (Consumer)consumer));
    }
    
    @Override
    public void onActionResponse(final ComponentName componentName, final String s, final int n) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(s, "controlId");
        if (!this.confirmAvailability()) {
            return;
        }
        this.uiController.onActionResponse(componentName, s, n);
    }
    
    @Override
    public void refreshStatus(final ComponentName componentName, final Control control) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(control, "control");
        if (!this.confirmAvailability()) {
            Log.d("ControlsControllerImpl", "Controls not available");
            return;
        }
        if (control.getStatus() == 1) {
            this.executor.execute((Runnable)new ControlsControllerImpl$refreshStatus.ControlsControllerImpl$refreshStatus$1(this, componentName, control));
        }
        this.uiController.onRefreshState(componentName, CollectionsKt.listOf(control));
    }
    
    public void replaceFavoritesForStructure(final StructureInfo structureInfo) {
        Intrinsics.checkParameterIsNotNull(structureInfo, "structureInfo");
        if (!this.confirmAvailability()) {
            return;
        }
        this.executor.execute((Runnable)new ControlsControllerImpl$replaceFavoritesForStructure.ControlsControllerImpl$replaceFavoritesForStructure$1(this, structureInfo));
    }
    
    @Override
    public void seedFavoritesForComponent(final ComponentName obj, final Consumer<Boolean> consumer) {
        Intrinsics.checkParameterIsNotNull(obj, "componentName");
        Intrinsics.checkParameterIsNotNull(consumer, "callback");
        final StringBuilder sb = new StringBuilder();
        sb.append("Beginning request to seed favorites for: ");
        sb.append(obj);
        Log.i("ControlsControllerImpl", sb.toString());
        if (!this.confirmAvailability()) {
            if (this.userChanging) {
                this.executor.executeDelayed((Runnable)new ControlsControllerImpl$seedFavoritesForComponent.ControlsControllerImpl$seedFavoritesForComponent$1(this, obj, (Consumer)consumer), 500L, TimeUnit.MILLISECONDS);
            }
            else {
                consumer.accept(Boolean.FALSE);
            }
            return;
        }
        this.seedingInProgress = true;
        this.bindingController.bindAndLoadSuggested(obj, (ControlsBindingController.LoadCallback)new ControlsControllerImpl$seedFavoritesForComponent.ControlsControllerImpl$seedFavoritesForComponent$2(this, obj, (Consumer)consumer));
    }
    
    @Override
    public void subscribeToFavorites(final StructureInfo structureInfo) {
        Intrinsics.checkParameterIsNotNull(structureInfo, "structureInfo");
        if (!this.confirmAvailability()) {
            return;
        }
        this.bindingController.subscribe(structureInfo);
    }
    
    @Override
    public void unsubscribe() {
        if (!this.confirmAvailability()) {
            return;
        }
        this.bindingController.unsubscribe();
    }
}
