// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.R$id;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import com.android.systemui.R$layout;
import android.view.ViewGroup;
import android.view.View$OnClickListener;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import androidx.lifecycle.Lifecycle;
import java.util.concurrent.Executor;
import android.content.res.Resources;
import kotlin.Unit;
import android.content.ComponentName;
import kotlin.jvm.functions.Function1;
import com.android.systemui.controls.ControlsServiceInfo;
import java.util.List;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.RecyclerView;

public final class AppAdapter extends Adapter<Holder>
{
    private final AppAdapter$callback.AppAdapter$callback$1 callback;
    private final FavoritesRenderer favoritesRenderer;
    private final LayoutInflater layoutInflater;
    private List<ControlsServiceInfo> listOfServices;
    private final Function1<ComponentName, Unit> onAppSelected;
    private final Resources resources;
    
    public AppAdapter(final Executor executor, final Executor executor2, final Lifecycle lifecycle, final ControlsListingController controlsListingController, final LayoutInflater layoutInflater, final Function1<? super ComponentName, Unit> onAppSelected, final FavoritesRenderer favoritesRenderer, final Resources resources) {
        Intrinsics.checkParameterIsNotNull(executor, "backgroundExecutor");
        Intrinsics.checkParameterIsNotNull(executor2, "uiExecutor");
        Intrinsics.checkParameterIsNotNull(lifecycle, "lifecycle");
        Intrinsics.checkParameterIsNotNull(controlsListingController, "controlsListingController");
        Intrinsics.checkParameterIsNotNull(layoutInflater, "layoutInflater");
        Intrinsics.checkParameterIsNotNull(onAppSelected, "onAppSelected");
        Intrinsics.checkParameterIsNotNull(favoritesRenderer, "favoritesRenderer");
        Intrinsics.checkParameterIsNotNull(resources, "resources");
        this.layoutInflater = layoutInflater;
        this.onAppSelected = (Function1<ComponentName, Unit>)onAppSelected;
        this.favoritesRenderer = favoritesRenderer;
        this.resources = resources;
        this.listOfServices = CollectionsKt.emptyList();
        controlsListingController.observe(lifecycle, (ControlsListingController.ControlsListingCallback)(this.callback = new AppAdapter$callback.AppAdapter$callback$1(this, executor, executor2)));
    }
    
    @Override
    public int getItemCount() {
        return this.listOfServices.size();
    }
    
    public void onBindViewHolder(final Holder holder, final int n) {
        Intrinsics.checkParameterIsNotNull(holder, "holder");
        holder.bindData(this.listOfServices.get(n));
        holder.itemView.setOnClickListener((View$OnClickListener)new AppAdapter$onBindViewHolder.AppAdapter$onBindViewHolder$1(this, n));
    }
    
    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        final View inflate = this.layoutInflater.inflate(R$layout.controls_app_item, viewGroup, false);
        Intrinsics.checkExpressionValueIsNotNull(inflate, "layoutInflater.inflate(R\u2026_app_item, parent, false)");
        return new Holder(inflate, this.favoritesRenderer);
    }
    
    public static final class Holder extends ViewHolder
    {
        private final FavoritesRenderer favRenderer;
        private final TextView favorites;
        private final ImageView icon;
        private final TextView title;
        
        public Holder(View view, final FavoritesRenderer favRenderer) {
            Intrinsics.checkParameterIsNotNull(view, "view");
            Intrinsics.checkParameterIsNotNull(favRenderer, "favRenderer");
            super(view);
            this.favRenderer = favRenderer;
            view = super.itemView.requireViewById(16908294);
            Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById\u2026droid.internal.R.id.icon)");
            this.icon = (ImageView)view;
            view = super.itemView.requireViewById(16908310);
            Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById\u2026roid.internal.R.id.title)");
            this.title = (TextView)view;
            view = super.itemView.requireViewById(R$id.favorites);
            Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById(R.id.favorites)");
            this.favorites = (TextView)view;
        }
        
        public final void bindData(final ControlsServiceInfo controlsServiceInfo) {
            Intrinsics.checkParameterIsNotNull(controlsServiceInfo, "data");
            this.icon.setImageDrawable(controlsServiceInfo.loadIcon());
            this.title.setText(controlsServiceInfo.loadLabel());
            final TextView favorites = this.favorites;
            final FavoritesRenderer favRenderer = this.favRenderer;
            final ComponentName componentName = controlsServiceInfo.componentName;
            Intrinsics.checkExpressionValueIsNotNull(componentName, "data.componentName");
            favorites.setText((CharSequence)favRenderer.renderFavoritesForComponent(componentName));
        }
    }
}
