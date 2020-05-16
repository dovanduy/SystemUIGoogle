// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import com.android.systemui.R$color;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.app.Notification$Builder;
import android.app.Notification;
import com.android.internal.util.ContrastColorUtil;
import java.util.Iterator;
import java.util.List;
import android.graphics.Bitmap;
import android.content.Context;
import androidx.palette.graphics.Palette;

public class MediaNotificationProcessor
{
    private final Palette.Filter mBlackWhiteFilter;
    private final ImageGradientColorizer mColorizer;
    private final Context mContext;
    private final Context mPackageContext;
    
    public MediaNotificationProcessor(final Context context, final Context context2) {
        this(context, context2, new ImageGradientColorizer());
    }
    
    MediaNotificationProcessor(final Context mContext, final Context mPackageContext, final ImageGradientColorizer mColorizer) {
        this.mBlackWhiteFilter = (Palette.Filter)_$$Lambda$MediaNotificationProcessor$oWRwwE503YseXSqqQUwqkZxEskY.INSTANCE;
        this.mContext = mContext;
        this.mPackageContext = mPackageContext;
        this.mColorizer = mColorizer;
    }
    
    public static Palette.Swatch findBackgroundSwatch(final Bitmap bitmap) {
        return findBackgroundSwatch(generateArtworkPaletteBuilder(bitmap).generate());
    }
    
    public static Palette.Swatch findBackgroundSwatch(final Palette palette) {
        final Palette.Swatch dominantSwatch = palette.getDominantSwatch();
        if (dominantSwatch == null) {
            return new Palette.Swatch(-1, 100);
        }
        if (!isWhiteOrBlack(dominantSwatch.getHsl())) {
            return dominantSwatch;
        }
        final List<Palette.Swatch> swatches = palette.getSwatches();
        float n = -1.0f;
        Palette.Swatch swatch = null;
        for (final Palette.Swatch swatch2 : swatches) {
            if (swatch2 != dominantSwatch && swatch2.getPopulation() > n && !isWhiteOrBlack(swatch2.getHsl())) {
                n = (float)swatch2.getPopulation();
                swatch = swatch2;
            }
        }
        if (swatch == null) {
            return dominantSwatch;
        }
        if (dominantSwatch.getPopulation() / n > 2.5f) {
            return dominantSwatch;
        }
        return swatch;
    }
    
    public static Palette.Builder generateArtworkPaletteBuilder(final Bitmap bitmap) {
        final Palette.Builder from = Palette.from(bitmap);
        from.setRegion(0, 0, bitmap.getWidth() / 2, bitmap.getHeight());
        from.clearFilters();
        from.resizeBitmapArea(22500);
        return from;
    }
    
    private static boolean hasEnoughPopulation(final Palette.Swatch swatch) {
        return swatch != null && swatch.getPopulation() / 22500.0f > 0.002;
    }
    
    private static boolean isBlack(final float[] array) {
        return array[2] <= 0.08f;
    }
    
    private static boolean isWhite(final float[] array) {
        return array[2] >= 0.9f;
    }
    
    private static boolean isWhiteOrBlack(final float[] array) {
        return isBlack(array) || isWhite(array);
    }
    
    public static int selectForegroundColor(final int n, final Palette palette) {
        if (ContrastColorUtil.isColorLight(n)) {
            return selectForegroundColorForSwatches(palette.getDarkVibrantSwatch(), palette.getVibrantSwatch(), palette.getDarkMutedSwatch(), palette.getMutedSwatch(), palette.getDominantSwatch(), -16777216);
        }
        return selectForegroundColorForSwatches(palette.getLightVibrantSwatch(), palette.getVibrantSwatch(), palette.getLightMutedSwatch(), palette.getMutedSwatch(), palette.getDominantSwatch(), -1);
    }
    
    private static int selectForegroundColorForSwatches(Palette.Swatch swatch, Palette.Swatch swatch2, final Palette.Swatch swatch3, final Palette.Swatch swatch4, final Palette.Swatch swatch5, final int n) {
        swatch2 = (swatch = selectVibrantCandidate(swatch, swatch2));
        if (swatch2 == null) {
            swatch = selectMutedCandidate(swatch4, swatch3);
        }
        if (swatch != null) {
            if (swatch5 == swatch) {
                return swatch.getRgb();
            }
            if (swatch.getPopulation() / (float)swatch5.getPopulation() < 0.01f && swatch5.getHsl()[1] > 0.19f) {
                return swatch5.getRgb();
            }
            return swatch.getRgb();
        }
        else {
            if (hasEnoughPopulation(swatch5)) {
                return swatch5.getRgb();
            }
            return n;
        }
    }
    
    private static Palette.Swatch selectMutedCandidate(final Palette.Swatch swatch, final Palette.Swatch swatch2) {
        final boolean hasEnoughPopulation = hasEnoughPopulation(swatch);
        final boolean hasEnoughPopulation2 = hasEnoughPopulation(swatch2);
        if (hasEnoughPopulation && hasEnoughPopulation2) {
            if (swatch.getHsl()[1] * (swatch.getPopulation() / (float)swatch2.getPopulation()) > swatch2.getHsl()[1]) {
                return swatch;
            }
            return swatch2;
        }
        else {
            if (hasEnoughPopulation) {
                return swatch;
            }
            if (hasEnoughPopulation2) {
                return swatch2;
            }
            return null;
        }
    }
    
    private static Palette.Swatch selectVibrantCandidate(final Palette.Swatch swatch, final Palette.Swatch swatch2) {
        final boolean hasEnoughPopulation = hasEnoughPopulation(swatch);
        final boolean hasEnoughPopulation2 = hasEnoughPopulation(swatch2);
        if (hasEnoughPopulation && hasEnoughPopulation2) {
            if (swatch.getPopulation() / (float)swatch2.getPopulation() < 1.0f) {
                return swatch2;
            }
            return swatch;
        }
        else {
            if (hasEnoughPopulation) {
                return swatch;
            }
            if (hasEnoughPopulation2) {
                return swatch2;
            }
            return null;
        }
    }
    
    public void processNotification(final Notification notification, final Notification$Builder notification$Builder) {
        final Icon largeIcon = notification.getLargeIcon();
        if (largeIcon != null) {
            boolean b = true;
            notification$Builder.setRebuildStyledRemoteViews(true);
            final Drawable loadDrawable = largeIcon.loadDrawable(this.mPackageContext);
            int n4;
            if (notification.isColorizedMedia()) {
                final int intrinsicWidth = loadDrawable.getIntrinsicWidth();
                final int intrinsicHeight = loadDrawable.getIntrinsicHeight();
                final int n = intrinsicWidth * intrinsicHeight;
                int n2 = intrinsicHeight;
                int n3 = intrinsicWidth;
                if (n > 22500) {
                    final double sqrt = Math.sqrt(22500.0f / n);
                    n3 = (int)(intrinsicWidth * sqrt);
                    n2 = (int)(sqrt * intrinsicHeight);
                }
                final Bitmap bitmap = Bitmap.createBitmap(n3, n2, Bitmap$Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap);
                loadDrawable.setBounds(0, 0, n3, n2);
                loadDrawable.draw(canvas);
                final Palette.Builder generateArtworkPaletteBuilder = generateArtworkPaletteBuilder(bitmap);
                final Palette.Swatch backgroundSwatch = findBackgroundSwatch(generateArtworkPaletteBuilder.generate());
                n4 = backgroundSwatch.getRgb();
                generateArtworkPaletteBuilder.setRegion((int)(bitmap.getWidth() * 0.4f), 0, bitmap.getWidth(), bitmap.getHeight());
                if (!isWhiteOrBlack(backgroundSwatch.getHsl())) {
                    generateArtworkPaletteBuilder.addFilter(new _$$Lambda$MediaNotificationProcessor$jNuRDwOMbOj8fwROH917lxaryoM(backgroundSwatch.getHsl()[0]));
                }
                generateArtworkPaletteBuilder.addFilter(this.mBlackWhiteFilter);
                notification$Builder.setColorPalette(n4, selectForegroundColor(n4, generateArtworkPaletteBuilder.generate()));
            }
            else {
                n4 = this.mContext.getColor(R$color.notification_material_background_color);
            }
            final ImageGradientColorizer mColorizer = this.mColorizer;
            if (this.mContext.getResources().getConfiguration().getLayoutDirection() != 1) {
                b = false;
            }
            notification$Builder.setLargeIcon(Icon.createWithBitmap(mColorizer.colorize(loadDrawable, n4, b)));
        }
    }
}
