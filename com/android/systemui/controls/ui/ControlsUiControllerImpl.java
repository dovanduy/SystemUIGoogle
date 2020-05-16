// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import java.util.function.Consumer;
import android.service.controls.Control;
import android.util.Log;
import java.util.NoSuchElementException;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity;
import android.os.Parcelable;
import com.android.systemui.controls.management.ControlsFavoritingActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import android.util.TypedValue;
import com.android.systemui.R$integer;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import android.widget.ArrayAdapter;
import com.android.systemui.R$string;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.widget.Space;
import android.view.View;
import android.view.LayoutInflater;
import java.util.Iterator;
import android.view.View$OnClickListener;
import android.widget.ImageView;
import android.content.res.Resources$Theme;
import com.android.systemui.R$color;
import android.graphics.drawable.LayerDrawable;
import kotlin.TypeCastException;
import com.android.systemui.R$id;
import android.widget.TextView;
import java.util.Collection;
import com.android.systemui.R$layout;
import kotlin.jvm.internal.Ref$ObjectRef;
import android.graphics.drawable.Drawable;
import kotlin.ranges.RangesKt;
import kotlin.collections.MapsKt;
import kotlin.collections.CollectionsKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import java.util.LinkedHashMap;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.controls.controller.ControlInfo;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.widget.ListPopupWindow;
import android.view.ViewGroup;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.controller.ControlsController;
import dagger.Lazy;
import java.util.Map;
import android.content.Context;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.List;
import android.app.Dialog;
import com.android.systemui.controls.controller.StructureInfo;
import android.content.ComponentName;

public final class ControlsUiControllerImpl implements ControlsUiController
{
    private static final ComponentName EMPTY_COMPONENT;
    private static final StructureInfo EMPTY_STRUCTURE;
    private Dialog activeDialog;
    private List<StructureInfo> allStructures;
    private final DelayableExecutor bgExecutor;
    private final Context context;
    private final Map<ControlKey, ControlViewHolder> controlViewsById;
    private final Map<ControlKey, ControlWithState> controlsById;
    private final Lazy<ControlsController> controlsController;
    private final Lazy<ControlsListingController> controlsListingController;
    private boolean hidden;
    private List<SelectionItem> lastItems;
    private ControlsListingController.ControlsListingCallback listingCallback;
    private ViewGroup parent;
    private ListPopupWindow popup;
    private StructureInfo selectedStructure;
    private final SharedPreferences sharedPreferences;
    private final DelayableExecutor uiExecutor;
    
    static {
        EMPTY_STRUCTURE = new StructureInfo(EMPTY_COMPONENT = new ComponentName("", ""), "", new ArrayList<ControlInfo>());
    }
    
    public ControlsUiControllerImpl(final Lazy<ControlsController> controlsController, final Context context, final DelayableExecutor uiExecutor, final DelayableExecutor bgExecutor, final Lazy<ControlsListingController> controlsListingController, final SharedPreferences sharedPreferences) {
        Intrinsics.checkParameterIsNotNull(controlsController, "controlsController");
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(uiExecutor, "uiExecutor");
        Intrinsics.checkParameterIsNotNull(bgExecutor, "bgExecutor");
        Intrinsics.checkParameterIsNotNull(controlsListingController, "controlsListingController");
        Intrinsics.checkParameterIsNotNull(sharedPreferences, "sharedPreferences");
        this.controlsController = controlsController;
        this.context = context;
        this.uiExecutor = uiExecutor;
        this.bgExecutor = bgExecutor;
        this.controlsListingController = controlsListingController;
        this.sharedPreferences = sharedPreferences;
        this.selectedStructure = ControlsUiControllerImpl.EMPTY_STRUCTURE;
        this.controlsById = new LinkedHashMap<ControlKey, ControlWithState>();
        this.controlViewsById = new LinkedHashMap<ControlKey, ControlViewHolder>();
        this.hidden = true;
    }
    
    private final ControlsListingController.ControlsListingCallback createCallback(final Function1<? super List<SelectionItem>, Unit> function1) {
        return (ControlsListingController.ControlsListingCallback)new ControlsUiControllerImpl$createCallback.ControlsUiControllerImpl$createCallback$1(this, (Function1)function1);
    }
    
    private final void createDropDown(final List<SelectionItem> list) {
        for (final SelectionItem selectionItem : list) {
            RenderInfo.Companion.registerComponentIcon(selectionItem.getComponentName(), selectionItem.getIcon());
        }
        final LinkedHashMap<Object, SelectionItem> linkedHashMap = new LinkedHashMap<Object, SelectionItem>(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault((Iterable<?>)list, 10)), 16));
        for (final SelectionItem next : list) {
            linkedHashMap.put(next.getComponentName(), next);
        }
        final List<StructureInfo> allStructures = this.allStructures;
        if (allStructures == null) {
            Intrinsics.throwUninitializedPropertyAccessException("allStructures");
            throw null;
        }
        final ArrayList<SelectionItem> list2 = new ArrayList<SelectionItem>();
        for (final StructureInfo structureInfo : allStructures) {
            final SelectionItem selectionItem2 = linkedHashMap.get(structureInfo.getComponentName());
            SelectionItem copy$default;
            if (selectionItem2 != null) {
                copy$default = SelectionItem.copy$default(selectionItem2, null, structureInfo.getStructure(), null, null, 13, null);
            }
            else {
                copy$default = null;
            }
            if (copy$default != null) {
                list2.add(copy$default);
            }
        }
        final SelectionItem selectionItem3 = this.findSelectionItem(this.selectedStructure, list2);
        SelectionItem selectionItem4;
        if (selectionItem3 != null) {
            selectionItem4 = selectionItem3;
        }
        else {
            selectionItem4 = list.get(0);
        }
        final Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        final ItemAdapter element = new ItemAdapter(this.context, R$layout.controls_spinner_item);
        element.addAll((Collection)list2);
        ref$ObjectRef.element = (T)element;
        final ViewGroup parent = this.parent;
        if (parent == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        final TextView textView = (TextView)parent.requireViewById(R$id.app_or_structure_spinner);
        textView.setText(selectionItem4.getTitle());
        final Drawable background = textView.getBackground();
        if (background == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        }
        final Drawable drawable = ((LayerDrawable)background).getDrawable(1);
        final Context context = textView.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "context");
        drawable.setTint(context.getResources().getColor(R$color.control_spinner_dropdown, (Resources$Theme)null));
        final ViewGroup parent2 = this.parent;
        if (parent2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        final ImageView imageView = (ImageView)parent2.requireViewById(R$id.app_icon);
        imageView.setContentDescription(selectionItem4.getTitle());
        imageView.setImageDrawable(selectionItem4.getIcon());
        if (list2.size() == 1) {
            textView.setBackground((Drawable)null);
            return;
        }
        final ViewGroup parent3 = this.parent;
        if (parent3 != null) {
            final ViewGroup viewGroup = (ViewGroup)parent3.requireViewById(R$id.controls_header);
            viewGroup.setOnClickListener((View$OnClickListener)new ControlsUiControllerImpl$createDropDown.ControlsUiControllerImpl$createDropDown$3(this, viewGroup, ref$ObjectRef));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }
    
    private final void createListView() {
        final LayoutInflater from = LayoutInflater.from(this.context);
        final int controls_with_favorites = R$layout.controls_with_favorites;
        final ViewGroup parent = this.parent;
        if (parent == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        from.inflate(controls_with_favorites, parent, true);
        final int maxColumns = this.findMaxColumns();
        final ViewGroup parent2 = this.parent;
        if (parent2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        final View requireViewById = parent2.requireViewById(R$id.global_actions_controls_list);
        if (requireViewById != null) {
            final ViewGroup viewGroup = (ViewGroup)requireViewById;
            Intrinsics.checkExpressionValueIsNotNull(from, "inflater");
            ViewGroup row = this.createRow(from, viewGroup);
            for (final ControlInfo controlInfo : this.selectedStructure.getControls()) {
                ViewGroup row2 = row;
                if (row.getChildCount() == maxColumns) {
                    row2 = this.createRow(from, viewGroup);
                }
                final View inflate = from.inflate(R$layout.controls_base_item, row2, false);
                if (inflate == null) {
                    throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
                }
                final ViewGroup viewGroup2 = (ViewGroup)inflate;
                row2.addView((View)viewGroup2);
                final ControlsController value = this.controlsController.get();
                Intrinsics.checkExpressionValueIsNotNull(value, "controlsController.get()");
                final ControlViewHolder controlViewHolder = new ControlViewHolder(viewGroup2, value, this.uiExecutor, this.bgExecutor);
                final ControlKey controlKey = new ControlKey(this.selectedStructure.getComponentName(), controlInfo.getControlId());
                controlViewHolder.bindData((ControlWithState)MapsKt.getValue((Map<ControlKey, ?>)this.controlsById, controlKey));
                this.controlViewsById.put(controlKey, controlViewHolder);
                row = row2;
            }
            for (int i = this.selectedStructure.getControls().size() % maxColumns; i > 0; --i) {
                row.addView((View)new Space(this.context), (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(0, 0, 1.0f));
            }
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
    }
    
    private final void createMenu() {
        final String string = this.context.getResources().getString(R$string.controls_menu_add);
        final Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        ref$ObjectRef.element = (T)new ArrayAdapter(this.context, R$layout.controls_more_item, (Object[])new String[] { string });
        final ViewGroup parent = this.parent;
        if (parent != null) {
            final ImageView imageView = (ImageView)parent.requireViewById(R$id.controls_more);
            imageView.setOnClickListener((View$OnClickListener)new ControlsUiControllerImpl$createMenu.ControlsUiControllerImpl$createMenu$1(this, imageView, ref$ObjectRef));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }
    
    private final ListPopupWindow createPopup() {
        final ListPopupWindow listPopupWindow = new ListPopupWindow((Context)new ContextThemeWrapper(this.context, R$style.Control_ListPopupWindow));
        listPopupWindow.setWindowLayoutType(2020);
        listPopupWindow.setModal(true);
        return listPopupWindow;
    }
    
    private final ViewGroup createRow(final LayoutInflater layoutInflater, final ViewGroup viewGroup) {
        final View inflate = layoutInflater.inflate(R$layout.controls_row, viewGroup, false);
        if (inflate != null) {
            final ViewGroup viewGroup2 = (ViewGroup)inflate;
            viewGroup.addView((View)viewGroup2);
            return viewGroup2;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
    }
    
    private final int findMaxColumns() {
        final Resources resources = this.context.getResources();
        final int integer = resources.getInteger(R$integer.controls_max_columns);
        final int integer2 = resources.getInteger(R$integer.controls_max_columns_adjust_below_width_dp);
        final TypedValue typedValue = new TypedValue();
        final int controls_max_columns_adjust_above_font_scale = R$dimen.controls_max_columns_adjust_above_font_scale;
        boolean b = true;
        resources.getValue(controls_max_columns_adjust_above_font_scale, typedValue, true);
        final float float1 = typedValue.getFloat();
        Intrinsics.checkExpressionValueIsNotNull(resources, "res");
        final Configuration configuration = resources.getConfiguration();
        if (configuration.orientation != 1) {
            b = false;
        }
        int n = integer;
        if (b) {
            final int screenWidthDp = configuration.screenWidthDp;
            n = integer;
            if (screenWidthDp != 0) {
                n = integer;
                if (screenWidthDp <= integer2) {
                    n = integer;
                    if (configuration.fontScale >= float1) {
                        n = integer - 1;
                    }
                }
            }
        }
        return n;
    }
    
    private final SelectionItem findSelectionItem(final StructureInfo structureInfo, final List<SelectionItem> list) {
        for (final SelectionItem next : list) {
            final SelectionItem selectionItem = next;
            if (Intrinsics.areEqual(selectionItem.getComponentName(), structureInfo.getComponentName()) && Intrinsics.areEqual(selectionItem.getStructure(), structureInfo.getStructure())) {
                return next;
            }
        }
        return null;
    }
    
    private final StructureInfo loadPreference(final List<StructureInfo> list) {
        if (list.isEmpty()) {
            return ControlsUiControllerImpl.EMPTY_STRUCTURE;
        }
        final SharedPreferences sharedPreferences = this.sharedPreferences;
        final StructureInfo structureInfo = null;
        final String string = sharedPreferences.getString("controls_component", (String)null);
        ComponentName componentName = null;
        Label_0051: {
            if (string != null) {
                componentName = ComponentName.unflattenFromString(string);
                if (componentName != null) {
                    break Label_0051;
                }
            }
            componentName = ControlsUiControllerImpl.EMPTY_COMPONENT;
        }
        final String string2 = this.sharedPreferences.getString("controls_structure", "");
        final Iterator<Object> iterator = list.iterator();
        StructureInfo structureInfo2;
        StructureInfo next;
        do {
            next = structureInfo;
            if (!iterator.hasNext()) {
                break;
            }
            next = iterator.next();
            structureInfo2 = next;
        } while (!Intrinsics.areEqual(componentName, structureInfo2.getComponentName()) || !Intrinsics.areEqual(string2, structureInfo2.getStructure()));
        final StructureInfo structureInfo3 = next;
        StructureInfo structureInfo4;
        if (structureInfo3 != null) {
            structureInfo4 = structureInfo3;
        }
        else {
            structureInfo4 = list.get(0);
        }
        return structureInfo4;
    }
    
    private final void reload(final ViewGroup viewGroup) {
        if (this.hidden) {
            return;
        }
        this.show(viewGroup);
    }
    
    private final void showControlsView(final List<SelectionItem> list) {
        final ViewGroup parent = this.parent;
        if (parent != null) {
            parent.removeAllViews();
            this.controlViewsById.clear();
            this.createListView();
            this.createDropDown(list);
            this.createMenu();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }
    
    private final void showInitialSetupView(final List<SelectionItem> list) {
        final ViewGroup parent = this.parent;
        if (parent == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        parent.removeAllViews();
        final LayoutInflater from = LayoutInflater.from(this.context);
        final int controls_no_favorites = R$layout.controls_no_favorites;
        final ViewGroup parent2 = this.parent;
        if (parent2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        from.inflate(controls_no_favorites, parent2, true);
        final ViewGroup parent3 = this.parent;
        if (parent3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        final View requireViewById = parent3.requireViewById(R$id.controls_no_favorites_group);
        if (requireViewById == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
        }
        final ViewGroup viewGroup = (ViewGroup)requireViewById;
        viewGroup.setOnClickListener((View$OnClickListener)new ControlsUiControllerImpl$showInitialSetupView.ControlsUiControllerImpl$showInitialSetupView$1(this));
        final ViewGroup parent4 = this.parent;
        if (parent4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        ((TextView)parent4.requireViewById(R$id.controls_subtitle)).setText((CharSequence)this.context.getResources().getString(R$string.quick_controls_subtitle));
        final ViewGroup parent5 = this.parent;
        if (parent5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        final View requireViewById2 = parent5.requireViewById(R$id.controls_icon_row);
        if (requireViewById2 != null) {
            final ViewGroup viewGroup2 = (ViewGroup)requireViewById2;
            for (final SelectionItem selectionItem : list) {
                final View inflate = from.inflate(R$layout.controls_icon, viewGroup, false);
                if (inflate == null) {
                    throw new TypeCastException("null cannot be cast to non-null type android.widget.ImageView");
                }
                final ImageView imageView = (ImageView)inflate;
                imageView.setContentDescription(selectionItem.getTitle());
                imageView.setImageDrawable(selectionItem.getIcon());
                viewGroup2.addView((View)imageView);
            }
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
    }
    
    private final void showSeedingView(final List<SelectionItem> list) {
        final ViewGroup parent = this.parent;
        if (parent == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        parent.removeAllViews();
        final LayoutInflater from = LayoutInflater.from(this.context);
        final int controls_no_favorites = R$layout.controls_no_favorites;
        final ViewGroup parent2 = this.parent;
        if (parent2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        from.inflate(controls_no_favorites, parent2, true);
        final ViewGroup parent3 = this.parent;
        if (parent3 != null) {
            ((TextView)parent3.requireViewById(R$id.controls_subtitle)).setText((CharSequence)this.context.getResources().getString(R$string.controls_seeding_in_progress));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }
    
    private final void startActivity(final Context context, final Intent intent) {
        context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        context.startActivity(intent);
    }
    
    private final void startFavoritingActivity(final Context context, final StructureInfo structureInfo) {
        final Intent intent = new Intent(context, (Class)ControlsFavoritingActivity.class);
        intent.putExtra("extra_app_label", this.controlsListingController.get().getAppLabel(structureInfo.getComponentName()));
        intent.putExtra("extra_structure", structureInfo.getStructure());
        intent.putExtra("android.intent.extra.COMPONENT_NAME", (Parcelable)structureInfo.getComponentName());
        intent.addFlags(335544320);
        this.startActivity(context, intent);
    }
    
    private final void startProviderSelectorActivity(final Context context) {
        final Intent intent = new Intent(context, (Class)ControlsProviderSelectorActivity.class);
        intent.addFlags(335544320);
        this.startActivity(context, intent);
    }
    
    private final void switchAppOrStructure(final SelectionItem selectionItem) {
        final List<StructureInfo> allStructures = this.allStructures;
        if (allStructures != null) {
            for (final StructureInfo selectedStructure : allStructures) {
                if (Intrinsics.areEqual(selectedStructure.getStructure(), selectionItem.getStructure()) && Intrinsics.areEqual(selectedStructure.getComponentName(), selectionItem.getComponentName())) {
                    if (Intrinsics.areEqual(selectedStructure, this.selectedStructure) ^ true) {
                        this.updatePreferences(this.selectedStructure = selectedStructure);
                        final ControlsListingController controlsListingController = this.controlsListingController.get();
                        final ControlsListingController.ControlsListingCallback listingCallback = this.listingCallback;
                        if (listingCallback == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
                            throw null;
                        }
                        controlsListingController.removeCallback(listingCallback);
                        final ViewGroup parent = this.parent;
                        if (parent == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("parent");
                            throw null;
                        }
                        this.reload(parent);
                    }
                    return;
                }
            }
            throw new NoSuchElementException("Collection contains no element matching the predicate.");
        }
        Intrinsics.throwUninitializedPropertyAccessException("allStructures");
        throw null;
    }
    
    private final void updatePreferences(final StructureInfo structureInfo) {
        this.sharedPreferences.edit().putString("controls_component", structureInfo.getComponentName().flattenToString()).putString("controls_structure", structureInfo.getStructure().toString()).commit();
    }
    
    @Override
    public boolean getAvailable() {
        return this.controlsController.get().getAvailable();
    }
    
    public final DelayableExecutor getBgExecutor() {
        return this.bgExecutor;
    }
    
    public final Context getContext() {
        return this.context;
    }
    
    public final DelayableExecutor getUiExecutor() {
        return this.uiExecutor;
    }
    
    @Override
    public void hide() {
        Log.d("ControlsUiController", "hide()");
        this.hidden = true;
        final ListPopupWindow popup = this.popup;
        if (popup != null) {
            popup.dismiss();
        }
        final Dialog activeDialog = this.activeDialog;
        if (activeDialog != null) {
            activeDialog.dismiss();
        }
        this.controlsController.get().unsubscribe();
        final ViewGroup parent = this.parent;
        if (parent == null) {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        parent.removeAllViews();
        this.controlsById.clear();
        this.controlViewsById.clear();
        final ControlsListingController controlsListingController = this.controlsListingController.get();
        final ControlsListingController.ControlsListingCallback listingCallback = this.listingCallback;
        if (listingCallback != null) {
            controlsListingController.removeCallback(listingCallback);
            RenderInfo.Companion.clearCache();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
        throw null;
    }
    
    @Override
    public void onActionResponse(final ComponentName componentName, final String s, final int n) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(s, "controlId");
        this.uiExecutor.execute((Runnable)new ControlsUiControllerImpl$onActionResponse.ControlsUiControllerImpl$onActionResponse$1(this, new ControlKey(componentName, s), n));
    }
    
    @Override
    public void onRefreshState(final ComponentName componentName, final List<Control> list) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(list, "controls");
        Log.d("ControlsUiController", "onRefreshState()");
        for (final Control control : list) {
            final Map<ControlKey, ControlWithState> controlsById = this.controlsById;
            final String controlId = control.getControlId();
            Intrinsics.checkExpressionValueIsNotNull(controlId, "c.getControlId()");
            final ControlWithState controlWithState = controlsById.get(new ControlKey(componentName, controlId));
            if (controlWithState != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onRefreshState() for id: ");
                sb.append(control.getControlId());
                Log.d("ControlsUiController", sb.toString());
                final ControlWithState controlWithState2 = new ControlWithState(componentName, controlWithState.getCi(), control);
                final String controlId2 = control.getControlId();
                Intrinsics.checkExpressionValueIsNotNull(controlId2, "c.getControlId()");
                final ControlKey controlKey = new ControlKey(componentName, controlId2);
                this.controlsById.put(controlKey, controlWithState2);
                this.uiExecutor.execute((Runnable)new ControlsUiControllerImpl$onRefreshState$$inlined$forEach$lambda.ControlsUiControllerImpl$onRefreshState$$inlined$forEach$lambda$1(controlKey, controlWithState2, control, this, componentName));
            }
        }
    }
    
    @Override
    public void show(final ViewGroup parent) {
        Intrinsics.checkParameterIsNotNull(parent, "parent");
        Log.d("ControlsUiController", "show()");
        this.parent = parent;
        this.hidden = false;
        final List<StructureInfo> favorites = this.controlsController.get().getFavorites();
        this.allStructures = favorites;
        if (favorites == null) {
            Intrinsics.throwUninitializedPropertyAccessException("allStructures");
            throw null;
        }
        this.selectedStructure = this.loadPreference(favorites);
        Label_0352: {
            if (this.controlsController.get().addSeedingFavoritesCallback((Consumer<Boolean>)new ControlsUiControllerImpl$show$cb.ControlsUiControllerImpl$show$cb$1(this, parent))) {
                this.listingCallback = this.createCallback((Function1<? super List<SelectionItem>, Unit>)new ControlsUiControllerImpl$show.ControlsUiControllerImpl$show$1(this));
            }
            else {
                if (this.selectedStructure.getControls().isEmpty()) {
                    final List<StructureInfo> allStructures = this.allStructures;
                    if (allStructures == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("allStructures");
                        throw null;
                    }
                    if (allStructures.size() <= 1) {
                        this.listingCallback = this.createCallback((Function1<? super List<SelectionItem>, Unit>)new ControlsUiControllerImpl$show.ControlsUiControllerImpl$show$2(this));
                        break Label_0352;
                    }
                }
                final List<ControlInfo> controls = this.selectedStructure.getControls();
                final ArrayList list = new ArrayList<Object>(CollectionsKt.collectionSizeOrDefault((Iterable<?>)controls, 10));
                final Iterator<Object> iterator = controls.iterator();
                while (iterator.hasNext()) {
                    list.add(new ControlWithState(this.selectedStructure.getComponentName(), iterator.next(), null));
                }
                final Map<ControlKey, ControlWithState> controlsById = this.controlsById;
                for (final ControlWithState next : list) {
                    controlsById.put(new ControlKey(this.selectedStructure.getComponentName(), next.getCi().getControlId()), next);
                }
                this.listingCallback = this.createCallback((Function1<? super List<SelectionItem>, Unit>)new ControlsUiControllerImpl$show.ControlsUiControllerImpl$show$5(this));
                this.controlsController.get().subscribeToFavorites(this.selectedStructure);
            }
        }
        final ControlsListingController controlsListingController = this.controlsListingController.get();
        final ControlsListingController.ControlsListingCallback listingCallback = this.listingCallback;
        if (listingCallback != null) {
            controlsListingController.addCallback(listingCallback);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
        throw null;
    }
}
