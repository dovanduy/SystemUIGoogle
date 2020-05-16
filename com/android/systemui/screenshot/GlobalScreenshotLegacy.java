// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.view.MotionEvent;
import com.android.systemui.R$dimen;
import android.view.View$OnTouchListener;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.res.Resources;
import android.view.WindowManager;
import android.view.WindowManager$LayoutParams;
import android.view.View;
import android.util.DisplayMetrics;
import android.view.Display;
import android.content.Context;
import android.media.MediaActionSound;
import android.widget.ImageView;

@Deprecated
public class GlobalScreenshotLegacy
{
    private ImageView mBackgroundView;
    private float mBgPadding;
    private float mBgPaddingScale;
    private MediaActionSound mCameraSound;
    private Context mContext;
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    private final ScreenshotNotificationsController mNotificationsController;
    private ImageView mScreenshotFlash;
    private View mScreenshotLayout;
    private ScreenshotSelectorView mScreenshotSelectorView;
    private ImageView mScreenshotView;
    private WindowManager$LayoutParams mWindowLayoutParams;
    private WindowManager mWindowManager;
    
    public GlobalScreenshotLegacy(final Context mContext, final Resources resources, final LayoutInflater layoutInflater, final ScreenshotNotificationsController mNotificationsController) {
        this.mContext = mContext;
        this.mNotificationsController = mNotificationsController;
        final View inflate = layoutInflater.inflate(R$layout.global_screenshot_legacy, (ViewGroup)null);
        this.mScreenshotLayout = inflate;
        this.mBackgroundView = (ImageView)inflate.findViewById(R$id.global_screenshot_legacy_background);
        this.mScreenshotView = (ImageView)this.mScreenshotLayout.findViewById(R$id.global_screenshot_legacy);
        this.mScreenshotFlash = (ImageView)this.mScreenshotLayout.findViewById(R$id.global_screenshot_legacy_flash);
        this.mScreenshotSelectorView = (ScreenshotSelectorView)this.mScreenshotLayout.findViewById(R$id.global_screenshot_legacy_selector);
        this.mScreenshotLayout.setFocusable(true);
        this.mScreenshotSelectorView.setFocusable(true);
        this.mScreenshotSelectorView.setFocusableInTouchMode(true);
        this.mScreenshotLayout.setOnTouchListener((View$OnTouchListener)_$$Lambda$GlobalScreenshotLegacy$qwq1ocOBDT0FoPyQlDCaMU5A1wQ.INSTANCE);
        (this.mWindowLayoutParams = new WindowManager$LayoutParams(-1, -1, 0, 0, 2036, 525568, -3)).setTitle((CharSequence)"ScreenshotAnimation");
        final WindowManager$LayoutParams mWindowLayoutParams = this.mWindowLayoutParams;
        mWindowLayoutParams.layoutInDisplayCutoutMode = 3;
        mWindowLayoutParams.setFitInsetsTypes(0);
        final WindowManager mWindowManager = (WindowManager)mContext.getSystemService("window");
        this.mWindowManager = mWindowManager;
        this.mDisplay = mWindowManager.getDefaultDisplay();
        final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        this.mDisplayMetrics = mDisplayMetrics;
        this.mDisplay.getRealMetrics(mDisplayMetrics);
        final float mBgPadding = (float)resources.getDimensionPixelSize(R$dimen.global_screenshot_legacy_bg_padding);
        this.mBgPadding = mBgPadding;
        this.mBgPaddingScale = mBgPadding / this.mDisplayMetrics.widthPixels;
        (this.mCameraSound = new MediaActionSound()).load(0);
    }
    
    void stopScreenshot() {
        if (this.mScreenshotSelectorView.getSelectionRect() != null) {
            this.mWindowManager.removeView(this.mScreenshotLayout);
            this.mScreenshotSelectorView.stopSelection();
        }
    }
}
