// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.settings.CurrentUserTracker;
import android.content.res.Resources;
import android.view.View;
import android.view.View$OnClickListener;
import android.widget.Button;
import com.android.systemui.R$string;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import kotlin.Unit;
import androidx.lifecycle.Lifecycle;
import kotlin.jvm.functions.Function1;
import android.content.Context;
import android.view.LayoutInflater;
import com.android.systemui.R$id;
import android.view.ViewStub;
import com.android.systemui.R$layout;
import android.os.Bundle;
import android.content.ComponentName;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.broadcast.BroadcastDispatcher;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.controls.controller.ControlsController;
import java.util.concurrent.Executor;
import com.android.systemui.util.LifecycleActivity;

public final class ControlsProviderSelectorActivity extends LifecycleActivity
{
    private final Executor backExecutor;
    private final ControlsController controlsController;
    private final ControlsProviderSelectorActivity$currentUserTracker.ControlsProviderSelectorActivity$currentUserTracker$1 currentUserTracker;
    private final Executor executor;
    private final ControlsListingController listingController;
    private RecyclerView recyclerView;
    
    public ControlsProviderSelectorActivity(final Executor executor, final Executor backExecutor, final ControlsListingController listingController, final ControlsController controlsController, final BroadcastDispatcher broadcastDispatcher) {
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        Intrinsics.checkParameterIsNotNull(backExecutor, "backExecutor");
        Intrinsics.checkParameterIsNotNull(listingController, "listingController");
        Intrinsics.checkParameterIsNotNull(controlsController, "controlsController");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher, "broadcastDispatcher");
        this.executor = executor;
        this.backExecutor = backExecutor;
        this.listingController = listingController;
        this.controlsController = controlsController;
        this.currentUserTracker = new ControlsProviderSelectorActivity$currentUserTracker.ControlsProviderSelectorActivity$currentUserTracker$1(this, broadcastDispatcher, broadcastDispatcher);
    }
    
    public final void launchFavoritingActivity(final ComponentName componentName) {
        this.backExecutor.execute((Runnable)new ControlsProviderSelectorActivity$launchFavoritingActivity.ControlsProviderSelectorActivity$launchFavoritingActivity$1(this, componentName));
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R$layout.controls_management);
        final ViewStub viewStub = (ViewStub)this.requireViewById(R$id.stub);
        viewStub.setLayoutResource(R$layout.controls_management_apps);
        viewStub.inflate();
        final View requireViewById = this.requireViewById(R$id.list);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById(R.id.list)");
        final RecyclerView recyclerView = (RecyclerView)requireViewById;
        this.recyclerView = recyclerView;
        if (recyclerView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recyclerView");
            throw null;
        }
        final Executor backExecutor = this.backExecutor;
        final Executor executor = this.executor;
        final com.android.settingslib.core.lifecycle.Lifecycle lifecycle = this.getLifecycle();
        final ControlsListingController listingController = this.listingController;
        final LayoutInflater from = LayoutInflater.from((Context)this);
        Intrinsics.checkExpressionValueIsNotNull(from, "LayoutInflater.from(this)");
        final ControlsProviderSelectorActivity$onCreate.ControlsProviderSelectorActivity$onCreate$2 controlsProviderSelectorActivity$onCreate$2 = new ControlsProviderSelectorActivity$onCreate.ControlsProviderSelectorActivity$onCreate$2(this);
        final Resources resources = this.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "resources");
        final FavoritesRenderer favoritesRenderer = new FavoritesRenderer(resources, (Function1<? super ComponentName, Integer>)new ControlsProviderSelectorActivity$onCreate.ControlsProviderSelectorActivity$onCreate$3(this.controlsController));
        final Resources resources2 = this.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources2, "resources");
        recyclerView.setAdapter((RecyclerView.Adapter)new AppAdapter(backExecutor, executor, lifecycle, listingController, from, (Function1<? super ComponentName, Unit>)controlsProviderSelectorActivity$onCreate$2, favoritesRenderer, resources2));
        final RecyclerView recyclerView2 = this.recyclerView;
        if (recyclerView2 != null) {
            recyclerView2.setLayoutManager((RecyclerView.LayoutManager)new LinearLayoutManager(this.getApplicationContext()));
            final View requireViewById2 = this.requireViewById(R$id.title);
            Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById<TextView>(R.id.title)");
            ((TextView)requireViewById2).setText(this.getResources().getText(R$string.controls_providers_title));
            ((Button)this.requireViewById(R$id.done)).setOnClickListener((View$OnClickListener)new ControlsProviderSelectorActivity$onCreate.ControlsProviderSelectorActivity$onCreate$4(this));
            ((CurrentUserTracker)this.currentUserTracker).startTracking();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("recyclerView");
        throw null;
    }
    
    @Override
    protected void onDestroy() {
        ((CurrentUserTracker)this.currentUserTracker).stopTracking();
        super.onDestroy();
    }
}
