// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import android.view.Display;
import android.util.DisplayMetrics;
import android.content.Context;

public class DisplayUtils
{
    public static int convertDpToPx(final float n, final Context context) {
        final Display display = context.getDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        return (int)Math.ceil(n * displayMetrics.density);
    }
    
    public static int getCornerRadiusBottom(final Context context) {
        final int identifier = context.getResources().getIdentifier("rounded_corner_radius_bottom", "dimen", "android");
        int dimensionPixelSize;
        if (identifier > 0) {
            dimensionPixelSize = context.getResources().getDimensionPixelSize(identifier);
        }
        else {
            dimensionPixelSize = 0;
        }
        int cornerRadiusDefault = dimensionPixelSize;
        if (dimensionPixelSize == 0) {
            cornerRadiusDefault = getCornerRadiusDefault(context);
        }
        return cornerRadiusDefault;
    }
    
    private static int getCornerRadiusDefault(final Context context) {
        final int identifier = context.getResources().getIdentifier("rounded_corner_radius", "dimen", "android");
        int dimensionPixelSize;
        if (identifier > 0) {
            dimensionPixelSize = context.getResources().getDimensionPixelSize(identifier);
        }
        else {
            dimensionPixelSize = 0;
        }
        return dimensionPixelSize;
    }
    
    public static int getCornerRadiusTop(final Context context) {
        final int identifier = context.getResources().getIdentifier("rounded_corner_radius_top", "dimen", "android");
        int dimensionPixelSize;
        if (identifier > 0) {
            dimensionPixelSize = context.getResources().getDimensionPixelSize(identifier);
        }
        else {
            dimensionPixelSize = 0;
        }
        int cornerRadiusDefault = dimensionPixelSize;
        if (dimensionPixelSize == 0) {
            cornerRadiusDefault = getCornerRadiusDefault(context);
        }
        return cornerRadiusDefault;
    }
    
    public static int getHeight(final Context context) {
        final Display display = context.getDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        final int rotation = display.getRotation();
        if (rotation != 0 && rotation != 2) {
            return displayMetrics.widthPixels;
        }
        return displayMetrics.heightPixels;
    }
    
    public static int getWidth(final Context context) {
        final Display display = context.getDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        final int rotation = display.getRotation();
        if (rotation != 0 && rotation != 2) {
            return displayMetrics.heightPixels;
        }
        return displayMetrics.widthPixels;
    }
}
