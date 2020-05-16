// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.R$plurals;
import kotlin.jvm.internal.Intrinsics;
import android.content.res.Resources;
import android.content.ComponentName;
import kotlin.jvm.functions.Function1;

public final class FavoritesRenderer
{
    private final Function1<ComponentName, Integer> favoriteFunction;
    private final Resources resources;
    
    public FavoritesRenderer(final Resources resources, final Function1<? super ComponentName, Integer> favoriteFunction) {
        Intrinsics.checkParameterIsNotNull(resources, "resources");
        Intrinsics.checkParameterIsNotNull(favoriteFunction, "favoriteFunction");
        this.resources = resources;
        this.favoriteFunction = (Function1<ComponentName, Integer>)favoriteFunction;
    }
    
    public final String renderFavoritesForComponent(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        final int intValue = this.favoriteFunction.invoke(componentName).intValue();
        if (intValue != 0) {
            final String quantityString = this.resources.getQuantityString(R$plurals.controls_number_of_favorites, intValue, new Object[] { intValue });
            Intrinsics.checkExpressionValueIsNotNull(quantityString, "resources.getQuantityStr\u2026r_of_favorites, qty, qty)");
            return quantityString;
        }
        return "";
    }
}
