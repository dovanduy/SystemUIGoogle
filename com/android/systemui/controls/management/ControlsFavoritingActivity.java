// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.settings.CurrentUserTracker;
import android.content.res.Resources;
import java.text.Collator;
import android.os.Bundle;
import android.content.res.Configuration;
import com.android.systemui.Prefs;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.controls.controller.ControlsController;
import java.util.function.Consumer;
import android.content.Context;
import com.android.systemui.R$string;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import android.view.View$OnLayoutChangeListener;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.view.ViewStub;
import com.android.systemui.R$layout;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import android.widget.Button;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.broadcast.BroadcastDispatcher;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.TextView;
import com.android.systemui.controls.TooltipManager;
import java.util.List;
import android.widget.ImageView;
import java.util.concurrent.Executor;
import android.view.View;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import android.content.ComponentName;
import java.util.Comparator;
import android.app.Activity;

public final class ControlsFavoritingActivity extends Activity
{
    private CharSequence appName;
    private Comparator<StructureContainer> comparator;
    private ComponentName component;
    private final ControlsControllerImpl controller;
    private final ControlsFavoritingActivity$currentUserTracker.ControlsFavoritingActivity$currentUserTracker$1 currentUserTracker;
    private View doneButton;
    private final Executor executor;
    private View iconFrame;
    private ImageView iconView;
    private List<StructureContainer> listOfStructures;
    private final ControlsFavoritingActivity$listingCallback.ControlsFavoritingActivity$listingCallback$1 listingCallback;
    private final ControlsListingController listingController;
    private TooltipManager mTooltipManager;
    private ManagementPageIndicator pageIndicator;
    private TextView statusText;
    private CharSequence structureExtra;
    private ViewPager2 structurePager;
    private TextView titleView;
    
    public ControlsFavoritingActivity(final Executor executor, final ControlsControllerImpl controller, final ControlsListingController listingController, final BroadcastDispatcher broadcastDispatcher) {
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        Intrinsics.checkParameterIsNotNull(controller, "controller");
        Intrinsics.checkParameterIsNotNull(listingController, "listingController");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher, "broadcastDispatcher");
        this.executor = executor;
        this.controller = controller;
        this.listingController = listingController;
        this.listOfStructures = CollectionsKt.emptyList();
        this.currentUserTracker = new ControlsFavoritingActivity$currentUserTracker.ControlsFavoritingActivity$currentUserTracker$1(this, broadcastDispatcher, broadcastDispatcher);
        this.listingCallback = new ControlsFavoritingActivity$listingCallback.ControlsFavoritingActivity$listingCallback$1(this);
    }
    
    private final void bindButtons() {
        final Button button = (Button)this.requireViewById(R$id.other_apps);
        button.setVisibility(0);
        button.setOnClickListener((View$OnClickListener)new ControlsFavoritingActivity$bindButtons$1.ControlsFavoritingActivity$bindButtons$1$1(button));
        final View requireViewById = this.requireViewById(R$id.done);
        final Button button2 = (Button)requireViewById;
        button2.setEnabled(false);
        button2.setOnClickListener((View$OnClickListener)new ControlsFavoritingActivity$bindButtons$$inlined$apply$lambda.ControlsFavoritingActivity$bindButtons$$inlined$apply$lambda$1(this));
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById<Button>(\u2026)\n            }\n        }");
        this.doneButton = requireViewById;
    }
    
    private final void bindViews() {
        this.setContentView(R$layout.controls_management);
        final ViewStub viewStub = (ViewStub)this.requireViewById(R$id.stub);
        viewStub.setLayoutResource(R$layout.controls_management_favorites);
        viewStub.inflate();
        final View requireViewById = this.requireViewById(R$id.status_message);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById(R.id.status_message)");
        this.statusText = (TextView)requireViewById;
        if (this.shouldShowTooltip()) {
            final TextView statusText = this.statusText;
            if (statusText == null) {
                Intrinsics.throwUninitializedPropertyAccessException("statusText");
                throw null;
            }
            final Context context = statusText.getContext();
            Intrinsics.checkExpressionValueIsNotNull(context, "statusText.context");
            final TooltipManager mTooltipManager = new TooltipManager(context, "ControlsStructureSwipeTooltipCount", 2, false, 8, null);
            Object layout;
            if ((this.mTooltipManager = mTooltipManager) != null) {
                layout = mTooltipManager.getLayout();
            }
            else {
                layout = null;
            }
            this.addContentView((View)layout, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2, 51));
        }
        final View requireViewById2 = this.requireViewById(R$id.structure_page_indicator);
        final ManagementPageIndicator pageIndicator = (ManagementPageIndicator)requireViewById2;
        pageIndicator.addOnLayoutChangeListener((View$OnLayoutChangeListener)new ControlsFavoritingActivity$bindViews$$inlined$apply$lambda.ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$1(this));
        pageIndicator.setVisibilityListener((Function1<? super Integer, Unit>)new ControlsFavoritingActivity$bindViews$$inlined$apply$lambda.ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$2(this));
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById<Manageme\u2026}\n            }\n        }");
        this.pageIndicator = pageIndicator;
        final View requireViewById3 = this.requireViewById(R$id.title);
        final TextView titleView = (TextView)requireViewById3;
        CharSequence text = this.appName;
        if (text == null) {
            text = titleView.getResources().getText(R$string.controls_favorite_default_title);
        }
        titleView.setText(text);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "requireViewById<TextView\u2026_default_title)\n        }");
        this.titleView = titleView;
        final View requireViewById4 = this.requireViewById(R$id.subtitle);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById4, "requireViewById<TextView>(R.id.subtitle)");
        ((TextView)requireViewById4).setText(this.getResources().getText(R$string.controls_favorite_subtitle));
        final View requireViewById5 = this.requireViewById(16908294);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById5, "requireViewById(com.android.internal.R.id.icon)");
        this.iconView = (ImageView)requireViewById5;
        final View requireViewById6 = this.requireViewById(R$id.icon_frame);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById6, "requireViewById(R.id.icon_frame)");
        this.iconFrame = requireViewById6;
        final View requireViewById7 = this.requireViewById(R$id.structure_pager);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById7, "requireViewById<ViewPager2>(R.id.structure_pager)");
        final ViewPager2 structurePager = (ViewPager2)requireViewById7;
        this.structurePager = structurePager;
        if (structurePager != null) {
            structurePager.registerOnPageChangeCallback((ViewPager2.OnPageChangeCallback)new ControlsFavoritingActivity$bindViews.ControlsFavoritingActivity$bindViews$4(this));
            this.bindButtons();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("structurePager");
        throw null;
    }
    
    private final void loadControls() {
        final ComponentName component = this.component;
        if (component != null) {
            final TextView statusText = this.statusText;
            if (statusText == null) {
                Intrinsics.throwUninitializedPropertyAccessException("statusText");
                throw null;
            }
            statusText.setText(this.getResources().getText(17040425));
            this.controller.loadForComponent(component, (Consumer<ControlsController.LoadData>)new ControlsFavoritingActivity$loadControls$$inlined$let$lambda.ControlsFavoritingActivity$loadControls$$inlined$let$lambda$1(this.getResources().getText(R$string.controls_favorite_other_zone_header), this));
        }
    }
    
    private final void setUpPager() {
        final ViewPager2 structurePager = this.structurePager;
        if (structurePager != null) {
            structurePager.setAdapter(new StructureAdapter(CollectionsKt.emptyList()));
            structurePager.registerOnPageChangeCallback((ViewPager2.OnPageChangeCallback)new ControlsFavoritingActivity$setUpPager$$inlined$apply$lambda.ControlsFavoritingActivity$setUpPager$$inlined$apply$lambda$1(this));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("structurePager");
        throw null;
    }
    
    private final boolean shouldShowTooltip() {
        final Context applicationContext = this.getApplicationContext();
        boolean b = false;
        if (Prefs.getInt(applicationContext, "ControlsStructureSwipeTooltipCount", 0) < 2) {
            b = true;
        }
        return b;
    }
    
    public void onBackPressed() {
        this.finish();
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        Intrinsics.checkParameterIsNotNull(configuration, "newConfig");
        super.onConfigurationChanged(configuration);
        final TooltipManager mTooltipManager = this.mTooltipManager;
        if (mTooltipManager != null) {
            mTooltipManager.hide(false);
        }
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Resources resources = this.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "resources");
        final Configuration configuration = resources.getConfiguration();
        Intrinsics.checkExpressionValueIsNotNull(configuration, "resources.configuration");
        final Collator instance = Collator.getInstance(configuration.getLocales().get(0));
        Intrinsics.checkExpressionValueIsNotNull(instance, "collator");
        this.comparator = (Comparator<StructureContainer>)new ControlsFavoritingActivity$onCreate$$inlined$compareBy.ControlsFavoritingActivity$onCreate$$inlined$compareBy$1((Comparator)instance);
        this.appName = this.getIntent().getCharSequenceExtra("extra_app_label");
        CharSequence charSequenceExtra = this.getIntent().getCharSequenceExtra("extra_structure");
        if (charSequenceExtra == null) {
            charSequenceExtra = "";
        }
        this.structureExtra = charSequenceExtra;
        this.component = (ComponentName)this.getIntent().getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        this.bindViews();
        this.setUpPager();
        this.loadControls();
        this.listingController.addCallback((ControlsListingController.ControlsListingCallback)this.listingCallback);
        ((CurrentUserTracker)this.currentUserTracker).startTracking();
    }
    
    protected void onDestroy() {
        ((CurrentUserTracker)this.currentUserTracker).stopTracking();
        this.listingController.removeCallback((ControlsListingController.ControlsListingCallback)this.listingCallback);
        this.controller.cancelLoad();
        super.onDestroy();
    }
    
    protected void onPause() {
        super.onPause();
        final TooltipManager mTooltipManager = this.mTooltipManager;
        if (mTooltipManager != null) {
            mTooltipManager.hide(false);
        }
    }
}
