// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.colorextraction;

import com.android.keyguard.KeyguardUpdateMonitor;
import android.app.WallpaperColors;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.annotations.VisibleForTesting;
import android.os.Handler;
import android.app.WallpaperManager$OnColorsChangedListener;
import android.app.WallpaperManager;
import com.android.internal.colorextraction.types.ExtractionType;
import android.content.Context;
import com.android.internal.colorextraction.types.Tonal;
import com.android.internal.colorextraction.ColorExtractor$GradientColors;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.Dumpable;
import com.android.internal.colorextraction.ColorExtractor;

public class SysuiColorExtractor extends ColorExtractor implements Dumpable, ConfigurationListener
{
    private final ColorExtractor$GradientColors mBackdropColors;
    private boolean mHasMediaArtwork;
    private final ColorExtractor$GradientColors mNeutralColorsLock;
    private final Tonal mTonal;
    
    @VisibleForTesting
    public SysuiColorExtractor(final Context context, final ExtractionType extractionType, final ConfigurationController configurationController, final WallpaperManager wallpaperManager, final boolean b) {
        super(context, extractionType, b, wallpaperManager);
        Tonal mTonal;
        if (extractionType instanceof Tonal) {
            mTonal = (Tonal)extractionType;
        }
        else {
            mTonal = new Tonal(context);
        }
        this.mTonal = mTonal;
        this.mNeutralColorsLock = new ColorExtractor$GradientColors();
        configurationController.addCallback((ConfigurationController.ConfigurationListener)this);
        (this.mBackdropColors = new ColorExtractor$GradientColors()).setMainColor(-16777216);
        if (wallpaperManager.isWallpaperSupported()) {
            wallpaperManager.removeOnColorsChangedListener((WallpaperManager$OnColorsChangedListener)this);
            wallpaperManager.addOnColorsChangedListener((WallpaperManager$OnColorsChangedListener)this, (Handler)null, -1);
        }
    }
    
    public SysuiColorExtractor(final Context context, final ConfigurationController configurationController) {
        this(context, (ExtractionType)new Tonal(context), configurationController, (WallpaperManager)context.getSystemService((Class)WallpaperManager.class), false);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("SysuiColorExtractor:");
        printWriter.println("  Current wallpaper colors:");
        final StringBuilder sb = new StringBuilder();
        sb.append("    system: ");
        sb.append(super.mSystemColors);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("    lock: ");
        sb2.append(super.mLockColors);
        printWriter.println(sb2.toString());
        final ColorExtractor$GradientColors[] a = (ColorExtractor$GradientColors[])super.mGradientColors.get(1);
        final ColorExtractor$GradientColors[] a2 = (ColorExtractor$GradientColors[])super.mGradientColors.get(2);
        printWriter.println("  Gradients:");
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("    system: ");
        sb3.append(Arrays.toString(a));
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("    lock: ");
        sb4.append(Arrays.toString(a2));
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  Neutral colors: ");
        sb5.append(this.mNeutralColorsLock);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  Has media backdrop: ");
        sb6.append(this.mHasMediaArtwork);
        printWriter.println(sb6.toString());
    }
    
    protected void extractWallpaperColors() {
        super.extractWallpaperColors();
        final Tonal mTonal = this.mTonal;
        if (mTonal != null) {
            if (this.mNeutralColorsLock != null) {
                WallpaperColors wallpaperColors;
                if ((wallpaperColors = super.mLockColors) == null) {
                    wallpaperColors = super.mSystemColors;
                }
                mTonal.applyFallback(wallpaperColors, this.mNeutralColorsLock);
            }
        }
    }
    
    public ColorExtractor$GradientColors getColors(final int n, final int n2) {
        if (this.mHasMediaArtwork && (n & 0x2) != 0x0) {
            return this.mBackdropColors;
        }
        return super.getColors(n, n2);
    }
    
    public ColorExtractor$GradientColors getNeutralColors() {
        ColorExtractor$GradientColors colorExtractor$GradientColors;
        if (this.mHasMediaArtwork) {
            colorExtractor$GradientColors = this.mBackdropColors;
        }
        else {
            colorExtractor$GradientColors = this.mNeutralColorsLock;
        }
        return colorExtractor$GradientColors;
    }
    
    public void onColorsChanged(final WallpaperColors wallpaperColors, final int n, final int n2) {
        if (n2 != KeyguardUpdateMonitor.getCurrentUser()) {
            return;
        }
        if ((n & 0x2) != 0x0) {
            this.mTonal.applyFallback(wallpaperColors, this.mNeutralColorsLock);
        }
        super.onColorsChanged(wallpaperColors, n);
    }
    
    public void onUiModeChanged() {
        this.extractWallpaperColors();
        this.triggerColorsChanged(3);
    }
    
    public void setHasMediaArtwork(final boolean mHasMediaArtwork) {
        if (this.mHasMediaArtwork != mHasMediaArtwork) {
            this.mHasMediaArtwork = mHasMediaArtwork;
            this.triggerColorsChanged(2);
        }
    }
}
