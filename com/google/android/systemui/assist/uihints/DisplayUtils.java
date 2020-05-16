// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.graphics.Point;
import android.view.WindowManager;
import android.util.TypedValue;
import android.view.Display;
import android.util.DisplayMetrics;
import android.content.Context;

public class DisplayUtils
{
    public static int convertDpToPx(final float n, final Context context) {
        final Display defaultDisplay = getDefaultDisplay(context);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(displayMetrics);
        return (int)Math.ceil(n * displayMetrics.density);
    }
    
    public static int convertSpToPx(final float n, final Context context) {
        return (int)TypedValue.applyDimension(2, n, context.getResources().getDisplayMetrics());
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
    
    private static Display getDefaultDisplay(final Context context) {
        return ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
    }
    
    public static int getHeight(final Context context) {
        final Display defaultDisplay = getDefaultDisplay(context);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(displayMetrics);
        final int rotation = defaultDisplay.getRotation();
        if (rotation != 0 && rotation != 2) {
            return displayMetrics.widthPixels;
        }
        return displayMetrics.heightPixels;
    }
    
    public static int getRotatedHeight(final Context context) {
        final Display defaultDisplay = getDefaultDisplay(context);
        final Point point = new Point();
        defaultDisplay.getRealSize(point);
        return point.y;
    }
    
    public static int getRotatedWidth(final Context context) {
        final Display defaultDisplay = getDefaultDisplay(context);
        final Point point = new Point();
        defaultDisplay.getRealSize(point);
        return point.x;
    }
    
    public static int getWidth(final Context context) {
        final Display defaultDisplay = getDefaultDisplay(context);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(displayMetrics);
        final int rotation = defaultDisplay.getRotation();
        if (rotation != 0 && rotation != 2) {
            return displayMetrics.heightPixels;
        }
        return displayMetrics.widthPixels;
    }
}
