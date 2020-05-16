// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.widget.ImageView;
import com.android.systemui.R$id;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

final class ItemAdapter extends ArrayAdapter<SelectionItem>
{
    private final LayoutInflater layoutInflater;
    private final int resource;
    
    public ItemAdapter(final Context context, final int resource) {
        Intrinsics.checkParameterIsNotNull(context, "parentContext");
        super(context, resource);
        this.resource = resource;
        this.layoutInflater = LayoutInflater.from(this.getContext());
    }
    
    public View getView(final int n, View inflate, final ViewGroup viewGroup) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        final SelectionItem selectionItem = (SelectionItem)this.getItem(n);
        if (inflate == null) {
            inflate = this.layoutInflater.inflate(this.resource, viewGroup, false);
        }
        ((TextView)inflate.requireViewById(R$id.controls_spinner_item)).setText(selectionItem.getTitle());
        final ImageView imageView = (ImageView)inflate.requireViewById(R$id.app_icon);
        imageView.setContentDescription(selectionItem.getAppName());
        imageView.setImageDrawable(selectionItem.getIcon());
        Intrinsics.checkExpressionValueIsNotNull(inflate, "view");
        return inflate;
    }
}
