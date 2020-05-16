// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.app.ActivityManager$StackInfo;
import android.app.ActivityTaskManager;
import android.window.WindowContainerTransaction;
import java.io.PrintWriter;
import android.content.res.Resources;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.util.TypedValue;
import android.util.DisplayMetrics;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import android.graphics.Point;
import android.util.Size;
import android.content.ComponentName;
import android.graphics.Rect;
import android.view.DisplayInfo;
import android.content.Context;

public class PipBoundsHandler
{
    private static final String TAG = "PipBoundsHandler";
    private float mAspectRatio;
    private final Context mContext;
    private int mCurrentMinSize;
    private float mDefaultAspectRatio;
    private int mDefaultMinSize;
    private int mDefaultStackGravity;
    private final DisplayInfo mDisplayInfo;
    private int mImeHeight;
    private boolean mIsImeShowing;
    private boolean mIsShelfShowing;
    private final Rect mLastDestinationBounds;
    private ComponentName mLastPipComponentName;
    private float mMaxAspectRatio;
    private float mMinAspectRatio;
    private Size mOverrideMinimalSize;
    private Size mReentrySize;
    private float mReentrySnapFraction;
    private Point mScreenEdgeInsets;
    private int mShelfHeight;
    private final PipSnapAlgorithm mSnapAlgorithm;
    private final Rect mTmpInsets;
    private final IWindowManager mWindowManager;
    
    public PipBoundsHandler(final Context mContext, final PipSnapAlgorithm mSnapAlgorithm) {
        this.mDisplayInfo = new DisplayInfo();
        this.mTmpInsets = new Rect();
        this.mLastDestinationBounds = new Rect();
        this.mReentrySnapFraction = -1.0f;
        this.mContext = mContext;
        this.mSnapAlgorithm = mSnapAlgorithm;
        this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
        this.reloadResources();
        this.mAspectRatio = this.mDefaultAspectRatio;
    }
    
    private int dpToPx(final float n, final DisplayMetrics displayMetrics) {
        return (int)TypedValue.applyDimension(1, n, displayMetrics);
    }
    
    private Rect getDefaultBounds(float n, final Size size) {
        final Rect rect = new Rect();
        int mShelfHeight = 0;
        if (n != -1.0f && size != null) {
            rect.set(0, 0, size.getWidth(), size.getHeight());
            this.mSnapAlgorithm.applySnapFraction(rect, this.getMovementBounds(rect), n);
        }
        else {
            final Rect rect2 = new Rect();
            this.getInsetBounds(rect2);
            final PipSnapAlgorithm mSnapAlgorithm = this.mSnapAlgorithm;
            final float mDefaultAspectRatio = this.mDefaultAspectRatio;
            n = (float)this.mDefaultMinSize;
            final DisplayInfo mDisplayInfo = this.mDisplayInfo;
            final Size sizeForAspectRatio = mSnapAlgorithm.getSizeForAspectRatio(mDefaultAspectRatio, n, mDisplayInfo.logicalWidth, mDisplayInfo.logicalHeight);
            final int mDefaultStackGravity = this.mDefaultStackGravity;
            final int width = sizeForAspectRatio.getWidth();
            final int height = sizeForAspectRatio.getHeight();
            int mImeHeight;
            if (this.mIsImeShowing) {
                mImeHeight = this.mImeHeight;
            }
            else {
                mImeHeight = 0;
            }
            if (this.mIsShelfShowing) {
                mShelfHeight = this.mShelfHeight;
            }
            Gravity.apply(mDefaultStackGravity, width, height, rect2, 0, Math.max(mImeHeight, mShelfHeight), rect);
        }
        return rect;
    }
    
    private void getInsetBounds(final Rect rect) {
        try {
            this.mWindowManager.getStableInsets(this.mContext.getDisplayId(), this.mTmpInsets);
            rect.set(this.mTmpInsets.left + this.mScreenEdgeInsets.x, this.mTmpInsets.top + this.mScreenEdgeInsets.y, this.mDisplayInfo.logicalWidth - this.mTmpInsets.right - this.mScreenEdgeInsets.x, this.mDisplayInfo.logicalHeight - this.mTmpInsets.bottom - this.mScreenEdgeInsets.y);
        }
        catch (RemoteException ex) {
            Log.e(PipBoundsHandler.TAG, "Failed to get stable insets from WM", (Throwable)ex);
        }
    }
    
    private Rect getMovementBounds(final Rect rect) {
        return this.getMovementBounds(rect, true);
    }
    
    private Rect getMovementBounds(final Rect rect, final boolean b) {
        final Rect rect2 = new Rect();
        this.getInsetBounds(rect2);
        final PipSnapAlgorithm mSnapAlgorithm = this.mSnapAlgorithm;
        int mImeHeight;
        if (b && this.mIsImeShowing) {
            mImeHeight = this.mImeHeight;
        }
        else {
            mImeHeight = 0;
        }
        mSnapAlgorithm.getMovementBounds(rect, rect2, rect2, mImeHeight);
        return rect2;
    }
    
    private boolean isValidPictureInPictureAspectRatio(final float n) {
        return Float.compare(this.mMinAspectRatio, n) <= 0 && Float.compare(n, this.mMaxAspectRatio) <= 0;
    }
    
    private void onResetReentryBoundsUnchecked() {
        this.mReentrySnapFraction = -1.0f;
        this.mReentrySize = null;
        this.mLastPipComponentName = null;
        this.mLastDestinationBounds.setEmpty();
    }
    
    private void reloadResources() {
        final Resources resources = this.mContext.getResources();
        this.mDefaultAspectRatio = resources.getFloat(17105071);
        this.mDefaultStackGravity = resources.getInteger(17694782);
        final int dimensionPixelSize = resources.getDimensionPixelSize(17105155);
        this.mDefaultMinSize = dimensionPixelSize;
        this.mCurrentMinSize = dimensionPixelSize;
        final String string = resources.getString(17039876);
        Size size;
        if (!string.isEmpty()) {
            size = Size.parseSize(string);
        }
        else {
            size = null;
        }
        Point mScreenEdgeInsets;
        if (size == null) {
            mScreenEdgeInsets = new Point();
        }
        else {
            mScreenEdgeInsets = new Point(this.dpToPx((float)size.getWidth(), resources.getDisplayMetrics()), this.dpToPx((float)size.getHeight(), resources.getDisplayMetrics()));
        }
        this.mScreenEdgeInsets = mScreenEdgeInsets;
        this.mMinAspectRatio = resources.getFloat(17105074);
        this.mMaxAspectRatio = resources.getFloat(17105073);
    }
    
    private void transformBoundsToAspectRatio(final Rect rect, final float n, final boolean b) {
        final float snapFraction = this.mSnapAlgorithm.getSnapFraction(rect, this.getMovementBounds(rect));
        Size size;
        if (b) {
            size = this.mSnapAlgorithm.getSizeForAspectRatio(new Size(rect.width(), rect.height()), n, (float)this.mCurrentMinSize);
        }
        else {
            final int mDefaultMinSize = this.mDefaultMinSize;
            final PipSnapAlgorithm mSnapAlgorithm = this.mSnapAlgorithm;
            final float n2 = (float)mDefaultMinSize;
            final DisplayInfo mDisplayInfo = this.mDisplayInfo;
            size = mSnapAlgorithm.getSizeForAspectRatio(n, n2, mDisplayInfo.logicalWidth, mDisplayInfo.logicalHeight);
        }
        final int n3 = (int)(rect.centerX() - size.getWidth() / 2.0f);
        final int n4 = (int)(rect.centerY() - size.getHeight() / 2.0f);
        rect.set(n3, n4, size.getWidth() + n3, size.getHeight() + n4);
        final Size mOverrideMinimalSize = this.mOverrideMinimalSize;
        if (mOverrideMinimalSize != null) {
            this.transformBoundsToMinimalSize(rect, n, mOverrideMinimalSize);
        }
        this.mSnapAlgorithm.applySnapFraction(rect, this.getMovementBounds(rect), snapFraction);
    }
    
    private void transformBoundsToMinimalSize(final Rect rect, final float n, Size size) {
        if (size == null) {
            return;
        }
        if (size.getWidth() / (float)size.getHeight() > n) {
            size = new Size(size.getWidth(), (int)(size.getWidth() / n));
        }
        else {
            size = new Size((int)(size.getHeight() * n), size.getHeight());
        }
        Gravity.apply(this.mDefaultStackGravity, size.getWidth(), size.getHeight(), new Rect(rect), rect);
    }
    
    private void updateDisplayInfoIfNeeded() {
        final DisplayInfo mDisplayInfo = this.mDisplayInfo;
        final int rotation = mDisplayInfo.rotation;
        boolean b = true;
        Label_0059: {
            if (rotation != 0 && rotation != 2) {
                if (mDisplayInfo.logicalWidth < mDisplayInfo.logicalHeight) {
                    break Label_0059;
                }
            }
            else {
                final DisplayInfo mDisplayInfo2 = this.mDisplayInfo;
                if (mDisplayInfo2.logicalWidth > mDisplayInfo2.logicalHeight) {
                    break Label_0059;
                }
            }
            b = false;
        }
        if (b) {
            final DisplayInfo mDisplayInfo3 = this.mDisplayInfo;
            final int logicalWidth = mDisplayInfo3.logicalWidth;
            mDisplayInfo3.logicalWidth = mDisplayInfo3.logicalHeight;
            mDisplayInfo3.logicalHeight = logicalWidth;
        }
    }
    
    public void applySnapFraction(final Rect rect, final float n) {
        this.mSnapAlgorithm.applySnapFraction(rect, this.getMovementBounds(rect), n);
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append(PipBoundsHandler.TAG);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string);
        sb3.append("mLastPipComponentName=");
        sb3.append(this.mLastPipComponentName);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(string);
        sb4.append("mReentrySnapFraction=");
        sb4.append(this.mReentrySnapFraction);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(string);
        sb5.append("mReentrySize=");
        sb5.append(this.mReentrySize);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append(string);
        sb6.append("mDisplayInfo=");
        sb6.append(this.mDisplayInfo);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append(string);
        sb7.append("mDefaultAspectRatio=");
        sb7.append(this.mDefaultAspectRatio);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append(string);
        sb8.append("mMinAspectRatio=");
        sb8.append(this.mMinAspectRatio);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append(string);
        sb9.append("mMaxAspectRatio=");
        sb9.append(this.mMaxAspectRatio);
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append(string);
        sb10.append("mAspectRatio=");
        sb10.append(this.mAspectRatio);
        printWriter.println(sb10.toString());
        final StringBuilder sb11 = new StringBuilder();
        sb11.append(string);
        sb11.append("mDefaultStackGravity=");
        sb11.append(this.mDefaultStackGravity);
        printWriter.println(sb11.toString());
        final StringBuilder sb12 = new StringBuilder();
        sb12.append(string);
        sb12.append("mIsImeShowing=");
        sb12.append(this.mIsImeShowing);
        printWriter.println(sb12.toString());
        final StringBuilder sb13 = new StringBuilder();
        sb13.append(string);
        sb13.append("mImeHeight=");
        sb13.append(this.mImeHeight);
        printWriter.println(sb13.toString());
        final StringBuilder sb14 = new StringBuilder();
        sb14.append(string);
        sb14.append("mIsShelfShowing=");
        sb14.append(this.mIsShelfShowing);
        printWriter.println(sb14.toString());
        final StringBuilder sb15 = new StringBuilder();
        sb15.append(string);
        sb15.append("mShelfHeight=");
        sb15.append(this.mShelfHeight);
        printWriter.println(sb15.toString());
        this.mSnapAlgorithm.dump(printWriter, string);
    }
    
    float getDefaultAspectRatio() {
        return this.mDefaultAspectRatio;
    }
    
    Rect getDestinationBounds(final ComponentName mLastPipComponentName, final float mAspectRatio, Rect rect, final Size mOverrideMinimalSize) {
        if (!mLastPipComponentName.equals((Object)this.mLastPipComponentName)) {
            this.onResetReentryBoundsUnchecked();
            this.mLastPipComponentName = mLastPipComponentName;
        }
        Rect rect2;
        if (rect == null) {
            rect = (rect2 = new Rect(this.getDefaultBounds(this.mReentrySnapFraction, this.mReentrySize)));
            if (this.mReentrySnapFraction == -1.0f) {
                rect2 = rect;
                if (this.mReentrySize == null) {
                    this.mOverrideMinimalSize = mOverrideMinimalSize;
                    rect2 = rect;
                }
            }
        }
        else {
            rect2 = new Rect(rect);
        }
        if (this.isValidPictureInPictureAspectRatio(mAspectRatio)) {
            this.transformBoundsToAspectRatio(rect2, mAspectRatio, false);
        }
        this.mAspectRatio = mAspectRatio;
        this.mLastDestinationBounds.set(rect2);
        return rect2;
    }
    
    public Rect getDisplayBounds() {
        final DisplayInfo mDisplayInfo = this.mDisplayInfo;
        return new Rect(0, 0, mDisplayInfo.logicalWidth, mDisplayInfo.logicalHeight);
    }
    
    public Rect getLastDestinationBounds() {
        return this.mLastDestinationBounds;
    }
    
    public float getSnapFraction(final Rect rect) {
        return this.mSnapAlgorithm.getSnapFraction(rect, this.getMovementBounds(rect));
    }
    
    public void onAspectRatioChanged(final float mAspectRatio) {
        this.mAspectRatio = mAspectRatio;
    }
    
    public void onConfigurationChanged() {
        this.reloadResources();
    }
    
    public void onDisplayInfoChanged(final DisplayInfo displayInfo) {
        this.mDisplayInfo.copyFrom(displayInfo);
    }
    
    public boolean onDisplayRotationChanged(final Rect rect, final int n, final int n2, final int rotation, final WindowContainerTransaction windowContainerTransaction) {
        if (n == this.mDisplayInfo.displayId) {
            if (n2 != rotation) {
                try {
                    final ActivityManager$StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
                    if (stackInfo == null) {
                        return false;
                    }
                    final Rect rect2 = new Rect(this.mLastDestinationBounds);
                    final float snapFraction = this.getSnapFraction(rect2);
                    this.mDisplayInfo.rotation = rotation;
                    this.updateDisplayInfoIfNeeded();
                    this.mSnapAlgorithm.applySnapFraction(rect2, this.getMovementBounds(rect2, false), snapFraction);
                    rect.set(rect2);
                    this.mLastDestinationBounds.set(rect);
                    windowContainerTransaction.setBounds(stackInfo.stackToken, rect);
                    return true;
                }
                catch (RemoteException ex) {
                    Log.e(PipBoundsHandler.TAG, "Failed to get StackInfo for pinned stack", (Throwable)ex);
                }
            }
        }
        return false;
    }
    
    public void onImeVisibilityChanged(final boolean mIsImeShowing, final int mImeHeight) {
        this.mIsImeShowing = mIsImeShowing;
        this.mImeHeight = mImeHeight;
    }
    
    public void onMovementBoundsChanged(Rect defaultBounds, final Rect rect, final Rect rect2, final DisplayInfo displayInfo) {
        this.getInsetBounds(defaultBounds);
        defaultBounds = this.getDefaultBounds(-1.0f, null);
        rect.set(defaultBounds);
        if (rect2.isEmpty()) {
            rect2.set(defaultBounds);
        }
        if (this.isValidPictureInPictureAspectRatio(this.mAspectRatio)) {
            this.transformBoundsToAspectRatio(rect, this.mAspectRatio, false);
        }
        displayInfo.copyFrom(this.mDisplayInfo);
    }
    
    public void onResetReentryBounds(final ComponentName componentName) {
        if (componentName.equals((Object)this.mLastPipComponentName)) {
            this.onResetReentryBoundsUnchecked();
        }
    }
    
    public void onSaveReentryBounds(final ComponentName mLastPipComponentName, final Rect rect) {
        this.mReentrySnapFraction = this.getSnapFraction(rect);
        this.mReentrySize = new Size(rect.width(), rect.height());
        this.mLastPipComponentName = mLastPipComponentName;
    }
    
    public void setMinEdgeSize(final int mCurrentMinSize) {
        this.mCurrentMinSize = mCurrentMinSize;
    }
    
    public boolean setShelfHeight(final boolean mIsShelfShowing, final int mShelfHeight) {
        if ((mIsShelfShowing && mShelfHeight > 0) == this.mIsShelfShowing && mShelfHeight == this.mShelfHeight) {
            return false;
        }
        this.mIsShelfShowing = mIsShelfShowing;
        this.mShelfHeight = mShelfHeight;
        return true;
    }
    
    public void transformBoundsToAspectRatio(final Rect rect) {
        this.transformBoundsToAspectRatio(rect, this.mAspectRatio, true);
    }
}
