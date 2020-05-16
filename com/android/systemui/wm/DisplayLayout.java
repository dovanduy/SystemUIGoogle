// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import android.provider.Settings$Global;
import android.os.SystemProperties;
import android.util.Size;
import android.content.res.Resources;
import android.graphics.Insets;
import android.util.RotationUtils;
import android.view.DisplayInfo;
import android.view.Display;
import android.content.Context;
import android.graphics.Rect;
import android.view.DisplayCutout;

public class DisplayLayout
{
    private DisplayCutout mCutout;
    private int mDensityDpi;
    private boolean mHasNavigationBar;
    private boolean mHasStatusBar;
    private int mHeight;
    private final Rect mNonDecorInsets;
    private int mRotation;
    private final Rect mStableInsets;
    private int mUiMode;
    private int mWidth;
    
    public DisplayLayout() {
        this.mNonDecorInsets = new Rect();
        this.mStableInsets = new Rect();
        this.mHasNavigationBar = false;
        this.mHasStatusBar = false;
    }
    
    public DisplayLayout(final Context context, final Display display) {
        this.mNonDecorInsets = new Rect();
        this.mStableInsets = new Rect();
        this.mHasNavigationBar = false;
        this.mHasStatusBar = false;
        final int displayId = display.getDisplayId();
        final DisplayInfo displayInfo = new DisplayInfo();
        display.getDisplayInfo(displayInfo);
        this.init(displayInfo, context.getResources(), hasNavigationBar(displayInfo, context, displayId), hasStatusBar(displayId));
    }
    
    public DisplayLayout(final DisplayLayout displayLayout) {
        this.mNonDecorInsets = new Rect();
        this.mStableInsets = new Rect();
        this.mHasNavigationBar = false;
        this.mHasStatusBar = false;
        this.set(displayLayout);
    }
    
    public static DisplayCutout calculateDisplayCutoutForRotation(DisplayCutout fromBoundsAndWaterfall, int n, int n2, final int n3) {
        if (fromBoundsAndWaterfall == null || fromBoundsAndWaterfall == DisplayCutout.NO_CUTOUT) {
            return null;
        }
        final Insets rotateInsets = RotationUtils.rotateInsets(fromBoundsAndWaterfall.getWaterfallInsets(), n);
        if (n == 0) {
            return computeSafeInsets(fromBoundsAndWaterfall, n2, n3);
        }
        final int n4 = 1;
        int i = 0;
        int n5 = n4;
        if (n != 1) {
            if (n == 3) {
                n5 = n4;
            }
            else {
                n5 = 0;
            }
        }
        final Rect[] boundingRectsAll = fromBoundsAndWaterfall.getBoundingRectsAll();
        final Rect[] array = new Rect[boundingRectsAll.length];
        final Rect rect = new Rect(0, 0, n2, n3);
        while (i < boundingRectsAll.length) {
            final Rect rect2 = new Rect(boundingRectsAll[i]);
            if (!rect2.isEmpty()) {
                rotateBounds(rect2, rect, n);
            }
            array[getBoundIndexFromRotation(i, n)] = rect2;
            ++i;
        }
        fromBoundsAndWaterfall = DisplayCutout.fromBoundsAndWaterfall(array, rotateInsets);
        if (n5 != 0) {
            n = n3;
        }
        else {
            n = n2;
        }
        if (n5 == 0) {
            n2 = n3;
        }
        return computeSafeInsets(fromBoundsAndWaterfall, n, n2);
    }
    
    static void computeNonDecorInsets(final Resources resources, int navigationBarPosition, int navigationBarSize, final int n, final DisplayCutout displayCutout, final int n2, final Rect rect, final boolean b) {
        rect.setEmpty();
        if (b) {
            navigationBarPosition = navigationBarPosition(resources, navigationBarSize, n, navigationBarPosition);
            navigationBarSize = getNavigationBarSize(resources, navigationBarPosition, navigationBarSize > n, n2);
            if (navigationBarPosition == 4) {
                rect.bottom = navigationBarSize;
            }
            else if (navigationBarPosition == 2) {
                rect.right = navigationBarSize;
            }
            else if (navigationBarPosition == 1) {
                rect.left = navigationBarSize;
            }
        }
        if (displayCutout != null) {
            rect.left += displayCutout.getSafeInsetLeft();
            rect.top += displayCutout.getSafeInsetTop();
            rect.right += displayCutout.getSafeInsetRight();
            rect.bottom += displayCutout.getSafeInsetBottom();
        }
    }
    
    private static Rect computeSafeInsets(final Size obj, final DisplayCutout obj2) {
        if (obj.getWidth() != obj.getHeight()) {
            return new Rect(Math.max(obj2.getWaterfallInsets().left, findCutoutInsetForSide(obj, obj2.getBoundingRectLeft(), 3)), Math.max(obj2.getWaterfallInsets().top, findCutoutInsetForSide(obj, obj2.getBoundingRectTop(), 48)), Math.max(obj2.getWaterfallInsets().right, findCutoutInsetForSide(obj, obj2.getBoundingRectRight(), 5)), Math.max(obj2.getWaterfallInsets().bottom, findCutoutInsetForSide(obj, obj2.getBoundingRectBottom(), 80)));
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("not implemented: display=");
        sb.append(obj);
        sb.append(" cutout=");
        sb.append(obj2);
        throw new UnsupportedOperationException(sb.toString());
    }
    
    public static DisplayCutout computeSafeInsets(final DisplayCutout displayCutout, final int n, final int n2) {
        if (displayCutout == DisplayCutout.NO_CUTOUT) {
            return null;
        }
        return displayCutout.replaceSafeInsets(computeSafeInsets(new Size(n, n2), displayCutout));
    }
    
    private static void convertNonDecorInsetsToStableInsets(final Resources resources, final Rect rect, int statusBarHeight, final int n, final boolean b) {
        if (!b) {
            return;
        }
        statusBarHeight = getStatusBarHeight(statusBarHeight > n, resources);
        rect.top = Math.max(rect.top, statusBarHeight);
    }
    
    private static int findCutoutInsetForSide(final Size size, final Rect rect, final int i) {
        if (rect.isEmpty()) {
            return 0;
        }
        if (i == 3) {
            return Math.max(0, rect.right);
        }
        if (i == 5) {
            return Math.max(0, size.getWidth() - rect.left);
        }
        if (i == 48) {
            return Math.max(0, rect.bottom);
        }
        if (i == 80) {
            return Math.max(0, size.getHeight() - rect.top);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("unknown gravity: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static int getBoundIndexFromRotation(int n, int n2) {
        n2 = (n -= n2);
        if (n2 < 0) {
            n = n2 + 4;
        }
        return n;
    }
    
    public static int getNavigationBarSize(final Resources resources, int n, final boolean b, int n2) {
        if ((n2 & 0xF) == 0x3) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 != 0) {
            if (n == 4) {
                if (b) {
                    n = 17105327;
                }
                else {
                    n = 17105325;
                }
                return resources.getDimensionPixelSize(n);
            }
            return resources.getDimensionPixelSize(17105330);
        }
        else {
            if (n == 4) {
                if (b) {
                    n = 17105326;
                }
                else {
                    n = 17105324;
                }
                return resources.getDimensionPixelSize(n);
            }
            return resources.getDimensionPixelSize(17105329);
        }
    }
    
    static int getStatusBarHeight(final boolean b, final Resources resources) {
        int n;
        if (b) {
            n = resources.getDimensionPixelSize(17105472);
        }
        else {
            n = resources.getDimensionPixelSize(17105473);
        }
        return n;
    }
    
    static boolean hasNavigationBar(final DisplayInfo displayInfo, final Context context, int n) {
        final boolean b = true;
        if (n == 0) {
            final String value = SystemProperties.get("qemu.hw.mainkeys");
            return !"1".equals(value) && ("0".equals(value) || context.getResources().getBoolean(17891523));
        }
        if (displayInfo.type == 5 && displayInfo.ownerUid != 1000) {
            n = 1;
        }
        else {
            n = 0;
        }
        final boolean b2 = Settings$Global.getInt(context.getContentResolver(), "force_desktop_mode_on_external_displays", 0) != 0;
        boolean b3 = b;
        if ((displayInfo.flags & 0x40) == 0x0) {
            b3 = (b2 && n == 0 && b);
        }
        return b3;
    }
    
    static boolean hasStatusBar(final int n) {
        return n == 0;
    }
    
    private void init(final DisplayInfo displayInfo, final Resources resources, final boolean mHasNavigationBar, final boolean mHasStatusBar) {
        this.mUiMode = resources.getConfiguration().uiMode;
        this.mWidth = displayInfo.logicalWidth;
        this.mHeight = displayInfo.logicalHeight;
        this.mRotation = displayInfo.rotation;
        this.mCutout = displayInfo.displayCutout;
        this.mDensityDpi = displayInfo.logicalDensityDpi;
        this.mHasNavigationBar = mHasNavigationBar;
        this.mHasStatusBar = mHasStatusBar;
        this.recalcInsets(resources);
    }
    
    public static int navigationBarPosition(final Resources resources, final int n, final int n2, final int n3) {
        if (n == n2 || !resources.getBoolean(17891490) || n <= n2) {
            return 4;
        }
        if (n3 == 1) {
            return 2;
        }
        return 1;
    }
    
    private void recalcInsets(final Resources resources) {
        computeNonDecorInsets(resources, this.mRotation, this.mWidth, this.mHeight, this.mCutout, this.mUiMode, this.mNonDecorInsets, this.mHasNavigationBar);
        this.mStableInsets.set(this.mNonDecorInsets);
        final boolean mHasStatusBar = this.mHasStatusBar;
        if (mHasStatusBar) {
            convertNonDecorInsetsToStableInsets(resources, this.mStableInsets, this.mWidth, this.mHeight, mHasStatusBar);
        }
    }
    
    public static void rotateBounds(final Rect rect, final Rect rect2, int left) {
        final int n = (left % 4 + 4) % 4;
        left = rect.left;
        if (n == 1) {
            rect.left = rect.top;
            rect.top = rect2.right - rect.right;
            rect.right = rect.bottom;
            rect.bottom = rect2.right - left;
            return;
        }
        if (n == 2) {
            final int right = rect2.right;
            rect.left = right - rect.right;
            rect.right = right - left;
            return;
        }
        if (n != 3) {
            return;
        }
        rect.left = rect2.bottom - rect.bottom;
        rect.bottom = rect.right;
        rect.right = rect2.bottom - rect.top;
        rect.top = left;
    }
    
    public float density() {
        return this.mDensityDpi * 0.00625f;
    }
    
    public int getOrientation() {
        int n;
        if (this.mWidth > this.mHeight) {
            n = 2;
        }
        else {
            n = 1;
        }
        return n;
    }
    
    public void getStableBounds(final Rect rect) {
        rect.set(0, 0, this.mWidth, this.mHeight);
        rect.inset(this.mStableInsets);
    }
    
    public int height() {
        return this.mHeight;
    }
    
    public boolean isLandscape() {
        return this.mWidth > this.mHeight;
    }
    
    public Rect nonDecorInsets() {
        return this.mNonDecorInsets;
    }
    
    public void rotateTo(final Resources resources, final int mRotation) {
        final int n = (mRotation - this.mRotation + 4) % 4;
        final boolean b = n % 2 != 0;
        final int mWidth = this.mWidth;
        final int mHeight = this.mHeight;
        this.mRotation = mRotation;
        if (b) {
            this.mWidth = mHeight;
            this.mHeight = mWidth;
        }
        final DisplayCutout mCutout = this.mCutout;
        if (mCutout != null && !mCutout.isEmpty()) {
            this.mCutout = calculateDisplayCutoutForRotation(this.mCutout, n, mWidth, mHeight);
        }
        this.recalcInsets(resources);
    }
    
    public int rotation() {
        return this.mRotation;
    }
    
    public void set(final DisplayLayout displayLayout) {
        this.mUiMode = displayLayout.mUiMode;
        this.mWidth = displayLayout.mWidth;
        this.mHeight = displayLayout.mHeight;
        this.mCutout = displayLayout.mCutout;
        this.mRotation = displayLayout.mRotation;
        this.mDensityDpi = displayLayout.mDensityDpi;
        this.mHasNavigationBar = displayLayout.mHasNavigationBar;
        this.mHasStatusBar = displayLayout.mHasStatusBar;
        this.mNonDecorInsets.set(displayLayout.mNonDecorInsets);
        this.mStableInsets.set(displayLayout.mStableInsets);
    }
    
    public Rect stableInsets() {
        return this.mStableInsets;
    }
    
    public int width() {
        return this.mWidth;
    }
}
