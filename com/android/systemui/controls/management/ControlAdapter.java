// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import android.view.View;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import com.android.systemui.R$drawable;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import kotlin.NoWhenBranchMatchedException;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class ControlAdapter extends Adapter<Holder>
{
    private final float elevation;
    private ControlsModel model;
    private final GridLayoutManager.SpanSizeLookup spanSizeLookup;
    
    public ControlAdapter(final float elevation) {
        this.elevation = elevation;
        this.spanSizeLookup = (GridLayoutManager.SpanSizeLookup)new ControlAdapter$spanSizeLookup.ControlAdapter$spanSizeLookup$1(this);
    }
    
    public final void changeModel(final ControlsModel model) {
        Intrinsics.checkParameterIsNotNull(model, "model");
        this.model = model;
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    @Override
    public int getItemCount() {
        final ControlsModel model = this.model;
        if (model != null) {
            final List<ElementWrapper> elements = model.getElements();
            if (elements != null) {
                return elements.size();
            }
        }
        return 0;
    }
    
    @Override
    public int getItemViewType(int n) {
        final ControlsModel model = this.model;
        if (model != null) {
            final ElementWrapper elementWrapper = model.getElements().get(n);
            if (elementWrapper instanceof ZoneNameWrapper) {
                n = 0;
            }
            else {
                if (!(elementWrapper instanceof ControlWrapper)) {
                    throw new NoWhenBranchMatchedException();
                }
                n = 1;
            }
            return n;
        }
        throw new IllegalStateException("Getting item type for null model");
    }
    
    public final GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return this.spanSizeLookup;
    }
    
    public void onBindViewHolder(final Holder holder, final int n) {
        Intrinsics.checkParameterIsNotNull(holder, "holder");
        final ControlsModel model = this.model;
        if (model != null) {
            holder.bindData(model.getElements().get(n));
        }
    }
    
    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        final LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        Holder holder;
        if (i != 0) {
            if (i != 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Wrong viewType: ");
                sb.append(i);
                throw new IllegalStateException(sb.toString());
            }
            final View inflate = from.inflate(R$layout.controls_base_item, viewGroup, false);
            inflate.getLayoutParams().width = -1;
            inflate.setElevation(this.elevation);
            inflate.setBackground(viewGroup.getContext().getDrawable(R$drawable.control_background_ripple));
            Intrinsics.checkExpressionValueIsNotNull(inflate, "layoutInflater.inflate(R\u2026le)\n                    }");
            holder = new ControlHolder(inflate, (Function2<? super String, ? super Boolean, Unit>)new ControlAdapter$onCreateViewHolder.ControlAdapter$onCreateViewHolder$2(this));
        }
        else {
            final View inflate2 = from.inflate(R$layout.controls_zone_header, viewGroup, false);
            Intrinsics.checkExpressionValueIsNotNull(inflate2, "layoutInflater.inflate(R\u2026ne_header, parent, false)");
            holder = new ZoneHolder(inflate2);
        }
        return holder;
    }
}
