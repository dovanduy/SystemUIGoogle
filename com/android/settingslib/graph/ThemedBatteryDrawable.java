// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.graph;

import android.graphics.ColorFilter;
import android.graphics.Path$Op;
import android.graphics.Path$Direction;
import android.graphics.Canvas;
import android.util.PathParser;
import android.content.res.TypedArray;
import android.content.res.Resources;
import com.android.settingslib.R$array;
import com.android.settingslib.Utils;
import com.android.settingslib.R$color;
import android.graphics.Paint$Join;
import android.graphics.BlendMode;
import android.graphics.Paint$Style;
import android.graphics.Rect;
import kotlin.jvm.internal.Intrinsics;
import android.graphics.Matrix;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import android.graphics.RectF;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

public class ThemedBatteryDrawable extends Drawable
{
    private int batteryLevel;
    private final Path boltPath;
    private boolean charging;
    private int[] colorLevels;
    private final Context context;
    private boolean dualTone;
    private final Paint dualToneBackgroundFill;
    private final Paint errorPaint;
    private final Path errorPerimeterPath;
    private int fillColor;
    private final Paint fillColorStrokePaint;
    private final Paint fillColorStrokeProtection;
    private final Path fillMask;
    private final Paint fillPaint;
    private final RectF fillRect;
    private int intrinsicHeight;
    private int intrinsicWidth;
    private final Function0<Unit> invalidateRunnable;
    private boolean invertFillIcon;
    private int levelColor;
    private final Path levelPath;
    private final RectF levelRect;
    private final Path perimeterPath;
    private final Path plusPath;
    private boolean powerSaveEnabled;
    private final Matrix scaleMatrix;
    private final Path scaledBolt;
    private final Path scaledErrorPerimeter;
    private final Path scaledFill;
    private final Path scaledPerimeter;
    private final Path scaledPlus;
    private final Path unifiedPath;
    
    public ThemedBatteryDrawable(final Context context, int i) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.context = context;
        this.perimeterPath = new Path();
        this.scaledPerimeter = new Path();
        this.errorPerimeterPath = new Path();
        this.scaledErrorPerimeter = new Path();
        this.fillMask = new Path();
        this.scaledFill = new Path();
        this.fillRect = new RectF();
        this.levelRect = new RectF();
        this.levelPath = new Path();
        this.scaleMatrix = new Matrix();
        new Rect();
        this.unifiedPath = new Path();
        this.boltPath = new Path();
        this.scaledBolt = new Path();
        this.plusPath = new Path();
        this.scaledPlus = new Path();
        this.fillColor = -65281;
        this.levelColor = -65281;
        this.invalidateRunnable = (Function0<Unit>)new ThemedBatteryDrawable$invalidateRunnable.ThemedBatteryDrawable$invalidateRunnable$1(this);
        this.context.getResources().getInteger(17694765);
        final Paint fillColorStrokePaint = new Paint(1);
        fillColorStrokePaint.setColor(i);
        fillColorStrokePaint.setDither(true);
        fillColorStrokePaint.setStrokeWidth(5.0f);
        fillColorStrokePaint.setStyle(Paint$Style.STROKE);
        fillColorStrokePaint.setBlendMode(BlendMode.SRC);
        fillColorStrokePaint.setStrokeMiter(5.0f);
        fillColorStrokePaint.setStrokeJoin(Paint$Join.ROUND);
        this.fillColorStrokePaint = fillColorStrokePaint;
        final Paint fillColorStrokeProtection = new Paint(1);
        fillColorStrokeProtection.setDither(true);
        fillColorStrokeProtection.setStrokeWidth(5.0f);
        fillColorStrokeProtection.setStyle(Paint$Style.STROKE);
        fillColorStrokeProtection.setBlendMode(BlendMode.CLEAR);
        fillColorStrokeProtection.setStrokeMiter(5.0f);
        fillColorStrokeProtection.setStrokeJoin(Paint$Join.ROUND);
        this.fillColorStrokeProtection = fillColorStrokeProtection;
        final Paint fillPaint = new Paint(1);
        fillPaint.setColor(i);
        fillPaint.setAlpha(255);
        fillPaint.setDither(true);
        fillPaint.setStrokeWidth(0.0f);
        fillPaint.setStyle(Paint$Style.FILL_AND_STROKE);
        this.fillPaint = fillPaint;
        final Paint errorPaint = new Paint(1);
        errorPaint.setColor(Utils.getColorStateListDefaultColor(this.context, R$color.batterymeter_plus_color));
        errorPaint.setAlpha(255);
        errorPaint.setDither(true);
        errorPaint.setStrokeWidth(0.0f);
        errorPaint.setStyle(Paint$Style.FILL_AND_STROKE);
        errorPaint.setBlendMode(BlendMode.SRC);
        this.errorPaint = errorPaint;
        final Paint dualToneBackgroundFill = new Paint(1);
        dualToneBackgroundFill.setColor(i);
        dualToneBackgroundFill.setAlpha(255);
        dualToneBackgroundFill.setDither(true);
        dualToneBackgroundFill.setStrokeWidth(0.0f);
        dualToneBackgroundFill.setStyle(Paint$Style.FILL_AND_STROKE);
        this.dualToneBackgroundFill = dualToneBackgroundFill;
        final Resources resources = this.context.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
        final float density = resources.getDisplayMetrics().density;
        this.intrinsicHeight = (int)(20.0f * density);
        this.intrinsicWidth = (int)(density * 12.0f);
        final Resources resources2 = this.context.getResources();
        final TypedArray obtainTypedArray = resources2.obtainTypedArray(R$array.batterymeter_color_levels);
        final TypedArray obtainTypedArray2 = resources2.obtainTypedArray(R$array.batterymeter_color_values);
        final int length = obtainTypedArray.length();
        this.colorLevels = new int[length * 2];
        int[] colorLevels;
        int n;
        for (i = 0; i < length; ++i) {
            colorLevels = this.colorLevels;
            n = i * 2;
            colorLevels[n] = obtainTypedArray.getInt(i, 0);
            if (obtainTypedArray2.getType(i) == 2) {
                this.colorLevels[n + 1] = Utils.getColorAttrDefaultColor(this.context, obtainTypedArray2.getThemeAttributeId(i, 0));
            }
            else {
                this.colorLevels[n + 1] = obtainTypedArray2.getColor(i, 0);
            }
        }
        obtainTypedArray.recycle();
        obtainTypedArray2.recycle();
        this.loadPaths();
    }
    
    private final int batteryColorForLevel(int n) {
        if (!this.charging && !this.powerSaveEnabled) {
            n = this.getColorForLevel(n);
        }
        else {
            n = this.fillColor;
        }
        return n;
    }
    
    private final int getColorForLevel(final int n) {
        int n2 = 0;
        int fillColor = 0;
        while (true) {
            final int[] colorLevels = this.colorLevels;
            if (n2 >= colorLevels.length) {
                return fillColor;
            }
            final int n3 = colorLevels[n2];
            fillColor = colorLevels[n2 + 1];
            if (n <= n3) {
                if (n2 == colorLevels.length - 2) {
                    fillColor = this.fillColor;
                }
                return fillColor;
            }
            n2 += 2;
        }
    }
    
    private final void loadPaths() {
        this.perimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039838)));
        this.perimeterPath.computeBounds(new RectF(), true);
        this.errorPerimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039836)));
        this.errorPerimeterPath.computeBounds(new RectF(), true);
        this.fillMask.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039837)));
        this.fillMask.computeBounds(this.fillRect, true);
        this.boltPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039835)));
        this.plusPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039839)));
        this.dualTone = this.context.getResources().getBoolean(17891376);
    }
    
    private final void postInvalidate() {
        Object invalidateRunnable;
        final Function0<Unit> function0 = (Function0<Unit>)(invalidateRunnable = this.invalidateRunnable);
        if (function0 != null) {
            invalidateRunnable = new ThemedBatteryDrawable$sam$java_lang_Runnable$0(function0);
        }
        this.unscheduleSelf((Runnable)invalidateRunnable);
        final Function0<Unit> invalidateRunnable2 = this.invalidateRunnable;
        Runnable runnable;
        if ((runnable = (Runnable)invalidateRunnable2) != null) {
            runnable = new ThemedBatteryDrawable$sam$java_lang_Runnable$0(invalidateRunnable2);
        }
        this.scheduleSelf((Runnable)runnable, 0L);
    }
    
    private final void updateSize() {
        final Rect bounds = this.getBounds();
        Intrinsics.checkExpressionValueIsNotNull(bounds, "b");
        if (bounds.isEmpty()) {
            this.scaleMatrix.setScale(1.0f, 1.0f);
        }
        else {
            this.scaleMatrix.setScale(bounds.right / 12.0f, bounds.bottom / 20.0f);
        }
        this.perimeterPath.transform(this.scaleMatrix, this.scaledPerimeter);
        this.errorPerimeterPath.transform(this.scaleMatrix, this.scaledErrorPerimeter);
        this.fillMask.transform(this.scaleMatrix, this.scaledFill);
        this.scaledFill.computeBounds(this.fillRect, true);
        this.boltPath.transform(this.scaleMatrix, this.scaledBolt);
        this.plusPath.transform(this.scaleMatrix, this.scaledPlus);
        final float max = Math.max(bounds.right / 12.0f * 3.0f, 6.0f);
        this.fillColorStrokePaint.setStrokeWidth(max);
        this.fillColorStrokeProtection.setStrokeWidth(max);
    }
    
    public void draw(final Canvas canvas) {
        Intrinsics.checkParameterIsNotNull(canvas, "c");
        canvas.saveLayer((RectF)null, (Paint)null);
        this.unifiedPath.reset();
        this.levelPath.reset();
        this.levelRect.set(this.fillRect);
        final int batteryLevel = this.batteryLevel;
        final float n = batteryLevel / 100.0f;
        float top;
        if (batteryLevel >= 95) {
            top = this.fillRect.top;
        }
        else {
            final RectF fillRect = this.fillRect;
            top = fillRect.height() * (1 - n) + fillRect.top;
        }
        this.levelRect.top = (float)Math.floor(top);
        this.levelPath.addRect(this.levelRect, Path$Direction.CCW);
        this.unifiedPath.addPath(this.scaledPerimeter);
        if (!this.dualTone) {
            this.unifiedPath.op(this.levelPath, Path$Op.UNION);
        }
        this.fillPaint.setColor(this.levelColor);
        if (this.charging) {
            this.unifiedPath.op(this.scaledBolt, Path$Op.DIFFERENCE);
            if (!this.invertFillIcon) {
                canvas.drawPath(this.scaledBolt, this.fillPaint);
            }
        }
        if (this.dualTone) {
            canvas.drawPath(this.unifiedPath, this.dualToneBackgroundFill);
            canvas.save();
            canvas.clipRect(0.0f, this.getBounds().bottom - this.getBounds().height() * n, (float)this.getBounds().right, (float)this.getBounds().bottom);
            canvas.drawPath(this.unifiedPath, this.fillPaint);
            canvas.restore();
        }
        else {
            this.fillPaint.setColor(this.fillColor);
            canvas.drawPath(this.unifiedPath, this.fillPaint);
            this.fillPaint.setColor(this.levelColor);
            if (this.batteryLevel <= 15 && !this.charging) {
                canvas.save();
                canvas.clipPath(this.scaledFill);
                canvas.drawPath(this.levelPath, this.fillPaint);
                canvas.restore();
            }
        }
        if (this.charging) {
            canvas.clipOutPath(this.scaledBolt);
            if (this.invertFillIcon) {
                canvas.drawPath(this.scaledBolt, this.fillColorStrokePaint);
            }
            else {
                canvas.drawPath(this.scaledBolt, this.fillColorStrokeProtection);
            }
        }
        else if (this.powerSaveEnabled) {
            canvas.drawPath(this.scaledErrorPerimeter, this.errorPaint);
            canvas.drawPath(this.scaledPlus, this.errorPaint);
        }
        canvas.restore();
    }
    
    public int getIntrinsicHeight() {
        return this.intrinsicHeight;
    }
    
    public int getIntrinsicWidth() {
        return this.intrinsicWidth;
    }
    
    public int getOpacity() {
        return -1;
    }
    
    public final boolean getPowerSaveEnabled() {
        return this.powerSaveEnabled;
    }
    
    protected void onBoundsChange(final Rect rect) {
        super.onBoundsChange(rect);
        this.updateSize();
    }
    
    public void setAlpha(final int n) {
    }
    
    public void setBatteryLevel(final int batteryLevel) {
        this.invertFillIcon = (batteryLevel >= 67 || (batteryLevel > 33 && this.invertFillIcon));
        this.batteryLevel = batteryLevel;
        this.levelColor = this.batteryColorForLevel(batteryLevel);
        this.invalidateSelf();
    }
    
    public final void setCharging(final boolean charging) {
        this.charging = charging;
        this.postInvalidate();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.fillPaint.setColorFilter(colorFilter);
        this.fillColorStrokePaint.setColorFilter(colorFilter);
        this.dualToneBackgroundFill.setColorFilter(colorFilter);
    }
    
    public final void setColors(int n, final int color, final int n2) {
        if (!this.dualTone) {
            n = n2;
        }
        this.fillColor = n;
        this.fillPaint.setColor(n);
        this.fillColorStrokePaint.setColor(this.fillColor);
        this.dualToneBackgroundFill.setColor(color);
        this.levelColor = this.batteryColorForLevel(this.batteryLevel);
        this.invalidateSelf();
    }
    
    public final void setPowerSaveEnabled(final boolean powerSaveEnabled) {
        this.powerSaveEnabled = powerSaveEnabled;
        this.postInvalidate();
    }
}
