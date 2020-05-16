// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import androidx.recyclerview.widget.GridLayoutManager;
import android.content.Context;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import android.view.View;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import kotlin.jvm.internal.Intrinsics;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public final class StructureAdapter extends Adapter<StructureHolder>
{
    private final List<StructureContainer> models;
    
    public StructureAdapter(final List<StructureContainer> models) {
        Intrinsics.checkParameterIsNotNull(models, "models");
        this.models = models;
    }
    
    @Override
    public int getItemCount() {
        return this.models.size();
    }
    
    public void onBindViewHolder(final StructureHolder structureHolder, final int n) {
        Intrinsics.checkParameterIsNotNull(structureHolder, "holder");
        structureHolder.bind(this.models.get(n).getModel());
    }
    
    public StructureHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        final View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.controls_structure_page, viewGroup, false);
        Intrinsics.checkExpressionValueIsNotNull(inflate, "layoutInflater.inflate(R\u2026ture_page, parent, false)");
        return new StructureHolder(inflate);
    }
    
    public static final class StructureHolder extends ViewHolder
    {
        private final ControlAdapter controlAdapter;
        private final RecyclerView recyclerView;
        
        public StructureHolder(View view) {
            Intrinsics.checkParameterIsNotNull(view, "view");
            super(view);
            view = super.itemView.requireViewById(R$id.listAll);
            Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById\u2026cyclerView>(R.id.listAll)");
            this.recyclerView = (RecyclerView)view;
            view = super.itemView;
            Intrinsics.checkExpressionValueIsNotNull(view, "itemView");
            final Context context = view.getContext();
            Intrinsics.checkExpressionValueIsNotNull(context, "itemView.context");
            this.controlAdapter = new ControlAdapter(context.getResources().getFloat(R$dimen.control_card_elevation));
            this.setUpRecyclerView();
        }
        
        private final void setUpRecyclerView() {
            final View itemView = super.itemView;
            Intrinsics.checkExpressionValueIsNotNull(itemView, "itemView");
            final Context context = itemView.getContext();
            Intrinsics.checkExpressionValueIsNotNull(context, "itemView.context");
            final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.controls_card_margin);
            final MarginItemDecorator marginItemDecorator = new MarginItemDecorator(dimensionPixelSize, dimensionPixelSize);
            final RecyclerView recyclerView = this.recyclerView;
            recyclerView.setAdapter((RecyclerView.Adapter)this.controlAdapter);
            final GridLayoutManager layoutManager = new GridLayoutManager(this.recyclerView.getContext(), 2);
            layoutManager.setSpanSizeLookup(this.controlAdapter.getSpanSizeLookup());
            recyclerView.setLayoutManager((RecyclerView.LayoutManager)layoutManager);
            recyclerView.addItemDecoration((RecyclerView.ItemDecoration)marginItemDecorator);
        }
        
        public final void bind(final ControlsModel controlsModel) {
            Intrinsics.checkParameterIsNotNull(controlsModel, "model");
            this.controlAdapter.changeModel(controlsModel);
        }
    }
}
