// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.controls.ControlStatus;
import android.view.View$OnClickListener;
import android.content.ComponentName;
import android.content.res.ColorStateList;
import android.content.Context;
import com.android.systemui.controls.ui.RenderInfo;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import android.widget.CheckBox;

final class ControlHolder extends Holder
{
    private final CheckBox favorite;
    private final Function2<String, Boolean, Unit> favoriteCallback;
    private final ImageView icon;
    private final TextView removed;
    private final TextView subtitle;
    private final TextView title;
    
    public ControlHolder(View view, final Function2<? super String, ? super Boolean, Unit> favoriteCallback) {
        Intrinsics.checkParameterIsNotNull(view, "view");
        Intrinsics.checkParameterIsNotNull(favoriteCallback, "favoriteCallback");
        super(view, null);
        this.favoriteCallback = (Function2<String, Boolean, Unit>)favoriteCallback;
        view = super.itemView.requireViewById(R$id.icon);
        Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById(R.id.icon)");
        this.icon = (ImageView)view;
        view = super.itemView.requireViewById(R$id.title);
        Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById(R.id.title)");
        this.title = (TextView)view;
        view = super.itemView.requireViewById(R$id.subtitle);
        Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById(R.id.subtitle)");
        this.subtitle = (TextView)view;
        view = super.itemView.requireViewById(R$id.status);
        Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById(R.id.status)");
        this.removed = (TextView)view;
        view = super.itemView.requireViewById(R$id.favorite);
        final CheckBox favorite = (CheckBox)view;
        favorite.setVisibility(0);
        Intrinsics.checkExpressionValueIsNotNull(view, "itemView.requireViewById\u2026lity = View.VISIBLE\n    }");
        this.favorite = favorite;
    }
    
    private final void applyRenderInfo(final RenderInfo renderInfo) {
        final View itemView = super.itemView;
        Intrinsics.checkExpressionValueIsNotNull(itemView, "itemView");
        final Context context = itemView.getContext();
        final ColorStateList colorStateList = context.getResources().getColorStateList(renderInfo.getForeground(), context.getTheme());
        this.icon.setImageDrawable(renderInfo.getIcon());
        this.icon.setImageTintList(colorStateList);
    }
    
    private final RenderInfo getRenderInfo(final ComponentName componentName, final int n) {
        final RenderInfo.Companion companion = RenderInfo.Companion;
        final View itemView = super.itemView;
        Intrinsics.checkExpressionValueIsNotNull(itemView, "itemView");
        final Context context = itemView.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "itemView.context");
        return RenderInfo.Companion.lookup$default(companion, context, componentName, n, true, 0, 16, null);
    }
    
    @Override
    public void bindData(final ElementWrapper elementWrapper) {
        Intrinsics.checkParameterIsNotNull(elementWrapper, "wrapper");
        final ControlStatus controlStatus = ((ControlWrapper)elementWrapper).getControlStatus();
        final RenderInfo renderInfo = this.getRenderInfo(controlStatus.getComponent(), controlStatus.getControl().getDeviceType());
        this.title.setText(controlStatus.getControl().getTitle());
        this.subtitle.setText(controlStatus.getControl().getSubtitle());
        this.favorite.setChecked(controlStatus.getFavorite());
        final TextView removed = this.removed;
        String text;
        if (controlStatus.getRemoved()) {
            text = "Removed";
        }
        else {
            text = "";
        }
        removed.setText((CharSequence)text);
        super.itemView.setOnClickListener((View$OnClickListener)new ControlHolder$bindData.ControlHolder$bindData$1(this, controlStatus));
        this.applyRenderInfo(renderInfo);
    }
    
    public final Function2<String, Boolean, Unit> getFavoriteCallback() {
        return this.favoriteCallback;
    }
}
