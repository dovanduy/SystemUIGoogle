// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.internal.colorextraction.ColorExtractor$GradientColors;
import android.view.ViewDebug;
import android.view.InsetsFlags;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.graphics.Rect;
import android.graphics.Color;
import com.android.systemui.R$color;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.content.Context;
import com.android.internal.view.AppearanceRegion;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.BatteryController;

public class LightBarController implements BatteryStateChangeCallback, Dumpable
{
    private int mAppearance;
    private AppearanceRegion[] mAppearanceRegions;
    private final BatteryController mBatteryController;
    private BiometricUnlockController mBiometricUnlockController;
    private boolean mDirectReplying;
    private boolean mForceDarkForScrim;
    private boolean mHasLightNavigationBar;
    private boolean mNavbarColorManagedByIme;
    private LightBarTransitionsController mNavigationBarController;
    private int mNavigationBarMode;
    private boolean mNavigationLight;
    private boolean mQsCustomizing;
    private final SysuiDarkIconDispatcher mStatusBarIconController;
    private int mStatusBarMode;
    
    public LightBarController(final Context context, final DarkIconDispatcher darkIconDispatcher, final BatteryController mBatteryController) {
        this.mAppearanceRegions = new AppearanceRegion[0];
        Color.valueOf(context.getColor(R$color.dark_mode_icon_color_single_tone));
        this.mStatusBarIconController = (SysuiDarkIconDispatcher)darkIconDispatcher;
        (this.mBatteryController = mBatteryController).addCallback((BatteryController.BatteryStateChangeCallback)this);
    }
    
    private boolean animateChange() {
        final BiometricUnlockController mBiometricUnlockController = this.mBiometricUnlockController;
        final boolean b = false;
        if (mBiometricUnlockController == null) {
            return false;
        }
        final int mode = mBiometricUnlockController.getMode();
        boolean b2 = b;
        if (mode != 2) {
            b2 = b;
            if (mode != 1) {
                b2 = true;
            }
        }
        return b2;
    }
    
    private static boolean isLight(int n, int n2, final int n3) {
        final boolean b = false;
        if (n2 != 0 && n2 != 6) {
            n2 = 0;
        }
        else {
            n2 = 1;
        }
        if ((n & n3) != 0x0) {
            n = 1;
        }
        else {
            n = 0;
        }
        boolean b2 = b;
        if (n2 != 0) {
            b2 = b;
            if (n != 0) {
                b2 = true;
            }
        }
        return b2;
    }
    
    private void reevaluate() {
        this.onStatusBarAppearanceChanged(this.mAppearanceRegions, true, this.mStatusBarMode, this.mNavbarColorManagedByIme);
        this.onNavigationBarAppearanceChanged(this.mAppearance, true, this.mNavigationBarMode, this.mNavbarColorManagedByIme);
    }
    
    private void updateNavigation() {
        final LightBarTransitionsController mNavigationBarController = this.mNavigationBarController;
        if (mNavigationBarController != null) {
            mNavigationBarController.setIconsDark(this.mNavigationLight, this.animateChange());
        }
    }
    
    private void updateStatus() {
        final int length = this.mAppearanceRegions.length;
        int n = -1;
        int n2;
        int n3;
        for (int i = n2 = 0; i < length; ++i, n2 = n3) {
            n3 = n2;
            if (isLight(this.mAppearanceRegions[i].getAppearance(), this.mStatusBarMode, 8)) {
                n3 = n2 + 1;
                n = i;
            }
        }
        if (n2 == length) {
            this.mStatusBarIconController.setIconsDarkArea(null);
            this.mStatusBarIconController.getTransitionsController().setIconsDark(true, this.animateChange());
        }
        else if (n2 == 0) {
            this.mStatusBarIconController.getTransitionsController().setIconsDark(false, this.animateChange());
        }
        else {
            this.mStatusBarIconController.setIconsDarkArea(this.mAppearanceRegions[n].getBounds());
            this.mStatusBarIconController.getTransitionsController().setIconsDark(true, this.animateChange());
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("LightBarController: ");
        printWriter.print(" mAppearance=");
        printWriter.println(ViewDebug.flagsToString((Class)InsetsFlags.class, "appearance", this.mAppearance));
        for (int length = this.mAppearanceRegions.length, i = 0; i < length; ++i) {
            final boolean light = isLight(this.mAppearanceRegions[i].getAppearance(), this.mStatusBarMode, 8);
            printWriter.print(" stack #");
            printWriter.print(i);
            printWriter.print(": ");
            printWriter.print(this.mAppearanceRegions[i].toString());
            printWriter.print(" isLight=");
            printWriter.println(light);
        }
        printWriter.print(" mNavigationLight=");
        printWriter.print(this.mNavigationLight);
        printWriter.print(" mHasLightNavigationBar=");
        printWriter.println(this.mHasLightNavigationBar);
        printWriter.print(" mStatusBarMode=");
        printWriter.print(this.mStatusBarMode);
        printWriter.print(" mNavigationBarMode=");
        printWriter.println(this.mNavigationBarMode);
        printWriter.print(" mForceDarkForScrim=");
        printWriter.print(this.mForceDarkForScrim);
        printWriter.print(" mQsCustomizing=");
        printWriter.print(this.mQsCustomizing);
        printWriter.print(" mDirectReplying=");
        printWriter.println(this.mDirectReplying);
        printWriter.print(" mNavbarColorManagedByIme=");
        printWriter.println(this.mNavbarColorManagedByIme);
        printWriter.println();
        final LightBarTransitionsController transitionsController = this.mStatusBarIconController.getTransitionsController();
        if (transitionsController != null) {
            printWriter.println(" StatusBarTransitionsController:");
            transitionsController.dump(fileDescriptor, printWriter, array);
            printWriter.println();
        }
        if (this.mNavigationBarController != null) {
            printWriter.println(" NavigationBarTransitionsController:");
            this.mNavigationBarController.dump(fileDescriptor, printWriter, array);
            printWriter.println();
        }
    }
    
    void onNavigationBarAppearanceChanged(final int mAppearance, final boolean b, final int mNavigationBarMode, final boolean mNavbarColorManagedByIme) {
        if ((((this.mAppearance ^ mAppearance) & 0x10) != 0x0 || b) && (this.mNavigationLight = ((this.mHasLightNavigationBar = isLight(mAppearance, mNavigationBarMode, 16)) && ((this.mDirectReplying && this.mNavbarColorManagedByIme) || !this.mForceDarkForScrim) && !this.mQsCustomizing)) != this.mNavigationLight) {
            this.updateNavigation();
        }
        this.mAppearance = mAppearance;
        this.mNavigationBarMode = mNavigationBarMode;
        this.mNavbarColorManagedByIme = mNavbarColorManagedByIme;
    }
    
    void onNavigationBarModeChanged(final int n) {
        this.mHasLightNavigationBar = isLight(this.mAppearance, n, 16);
    }
    
    @Override
    public void onPowerSaveChanged(final boolean b) {
        this.reevaluate();
    }
    
    void onStatusBarAppearanceChanged(final AppearanceRegion[] mAppearanceRegions, final boolean b, final int n, final boolean mNavbarColorManagedByIme) {
        final int length = mAppearanceRegions.length;
        final int length2 = this.mAppearanceRegions.length;
        int n2;
        boolean b2;
        for (n2 = 0, b2 = (length2 != length); n2 < length && !b2; b2 |= (mAppearanceRegions[n2].equals((Object)this.mAppearanceRegions[n2]) ^ true), ++n2) {}
        if (b2 || b) {
            this.mAppearanceRegions = mAppearanceRegions;
            this.onStatusBarModeChanged(n);
        }
        this.mNavbarColorManagedByIme = mNavbarColorManagedByIme;
    }
    
    void onStatusBarModeChanged(final int mStatusBarMode) {
        this.mStatusBarMode = mStatusBarMode;
        this.updateStatus();
    }
    
    public void setBiometricUnlockController(final BiometricUnlockController mBiometricUnlockController) {
        this.mBiometricUnlockController = mBiometricUnlockController;
    }
    
    public void setDirectReplying(final boolean mDirectReplying) {
        if (this.mDirectReplying == mDirectReplying) {
            return;
        }
        this.mDirectReplying = mDirectReplying;
        this.reevaluate();
    }
    
    public void setNavigationBar(final LightBarTransitionsController mNavigationBarController) {
        this.mNavigationBarController = mNavigationBarController;
        this.updateNavigation();
    }
    
    public void setQsCustomizing(final boolean mQsCustomizing) {
        if (this.mQsCustomizing == mQsCustomizing) {
            return;
        }
        this.mQsCustomizing = mQsCustomizing;
        this.reevaluate();
    }
    
    public void setScrimState(final ScrimState scrimState, final float n, final ColorExtractor$GradientColors colorExtractor$GradientColors) {
        final boolean mForceDarkForScrim = this.mForceDarkForScrim;
        final boolean mForceDarkForScrim2 = scrimState != ScrimState.BOUNCER && scrimState != ScrimState.BOUNCER_SCRIMMED && n >= 0.1f && !colorExtractor$GradientColors.supportsDarkText();
        this.mForceDarkForScrim = mForceDarkForScrim2;
        if (this.mHasLightNavigationBar && mForceDarkForScrim2 != mForceDarkForScrim) {
            this.reevaluate();
        }
    }
}
