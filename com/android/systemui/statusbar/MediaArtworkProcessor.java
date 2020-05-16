// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import androidx.palette.graphics.Palette;
import android.view.Display;
import android.util.Log;
import com.android.internal.graphics.ColorUtils;
import android.graphics.Canvas;
import com.android.systemui.statusbar.notification.MediaNotificationProcessor;
import android.renderscript.Allocation;
import android.renderscript.Allocation$MipmapControl;
import android.graphics.Bitmap$Config;
import android.util.MathUtils;
import android.graphics.Rect;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Bitmap;

public final class MediaArtworkProcessor
{
    private Bitmap mArtworkCache;
    private final Point mTmpSize;
    
    public MediaArtworkProcessor() {
        this.mTmpSize = new Point();
    }
    
    public final void clearCache() {
        final Bitmap mArtworkCache = this.mArtworkCache;
        if (mArtworkCache != null) {
            mArtworkCache.recycle();
        }
        this.mArtworkCache = null;
    }
    
    public final Bitmap processArtwork(Context ex, final Bitmap bitmap) {
        Intrinsics.checkParameterIsNotNull(ex, "context");
        Intrinsics.checkParameterIsNotNull(bitmap, "artwork");
        final Bitmap mArtworkCache = this.mArtworkCache;
        if (mArtworkCache != null) {
            return mArtworkCache;
        }
        final RenderScript create = RenderScript.create((Context)ex);
        final ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        final Allocation allocation = null;
        final Allocation allocation2 = null;
        Object o3 = null;
        Allocation allocation3 = null;
        final IllegalArgumentException ex3;
        Label_0482: {
            Label_0471: {
                try {
                    final Display display = ((Context)ex).getDisplay();
                    if (display != null) {
                        display.getSize(this.mTmpSize);
                    }
                    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    MathUtils.fitRect(rect, Math.max(this.mTmpSize.x / 6, this.mTmpSize.y / 6));
                    Object o2;
                    Object fromBitmap;
                    final Object o = fromBitmap = (o2 = Bitmap.createScaledBitmap(bitmap, rect.width(), rect.height(), (boolean)(1 != 0)));
                    try {
                        Intrinsics.checkExpressionValueIsNotNull(o, "inBitmap");
                        ex = (IllegalArgumentException)o;
                        o2 = o;
                        fromBitmap = o;
                        if (((Bitmap)o).getConfig() != Bitmap$Config.ARGB_8888) {
                            o2 = o;
                            fromBitmap = o;
                            ((Bitmap)o).copy(Bitmap$Config.ARGB_8888, false);
                            try {
                                ((Bitmap)o).recycle();
                            }
                            catch (IllegalArgumentException ex4) {
                                break Label_0471;
                            }
                            finally {
                                ex = null;
                                fromBitmap = allocation;
                                break Label_0482;
                            }
                        }
                        o2 = ex;
                        fromBitmap = ex;
                        Intrinsics.checkExpressionValueIsNotNull(ex, "inBitmap");
                        o2 = ex;
                        fromBitmap = ex;
                        final Bitmap bitmap2 = Bitmap.createBitmap(((Bitmap)ex).getWidth(), ((Bitmap)ex).getHeight(), Bitmap$Config.ARGB_8888);
                        o2 = ex;
                        fromBitmap = ex;
                        final Allocation fromBitmap2 = Allocation.createFromBitmap(create, (Bitmap)ex, Allocation$MipmapControl.MIPMAP_NONE, 2);
                        try {
                            fromBitmap = Allocation.createFromBitmap(create, bitmap2);
                            try {
                                create2.setRadius(25.0f);
                                create2.setInput(fromBitmap2);
                                create2.forEach((Allocation)fromBitmap);
                                ((Allocation)fromBitmap).copyTo(bitmap2);
                                o2 = MediaNotificationProcessor.findBackgroundSwatch(bitmap);
                                final Canvas canvas = new Canvas(bitmap2);
                                Intrinsics.checkExpressionValueIsNotNull(o2, "swatch");
                                canvas.drawColor(ColorUtils.setAlphaComponent(((Palette.Swatch)o2).getRgb(), 178));
                                if (fromBitmap2 != null) {
                                    fromBitmap2.destroy();
                                }
                                if (fromBitmap != null) {
                                    ((Allocation)fromBitmap).destroy();
                                }
                                create2.destroy();
                                ((Bitmap)ex).recycle();
                                return bitmap2;
                            }
                            catch (IllegalArgumentException o2) {
                                fromBitmap = ex;
                            }
                        }
                        catch (IllegalArgumentException o2) {
                            fromBitmap = ex;
                        }
                    }
                    catch (IllegalArgumentException swatch) {}
                    finally {
                        fromBitmap = allocation2;
                    }
                    break Label_0482;
                }
                catch (IllegalArgumentException ex) {
                    o3 = null;
                }
                finally {
                    o3 = (ex = null);
                    allocation3 = allocation;
                    break Label_0482;
                }
            }
            allocation3 = null;
            final IllegalArgumentException ex2 = null;
            ex3 = ex;
            ex = ex2;
            try {
                Log.e("MediaArtworkProcessor", "error while processing artwork", (Throwable)ex3);
                if (allocation3 != null) {
                    allocation3.destroy();
                }
                if (ex != null) {
                    ((Allocation)ex).destroy();
                }
                create2.destroy();
                if (o3 != null) {
                    ((Bitmap)o3).recycle();
                }
                return null;
            }
            finally {}
        }
        if (allocation3 != null) {
            allocation3.destroy();
        }
        if (ex != null) {
            ((Allocation)ex).destroy();
        }
        create2.destroy();
        if (o3 != null) {
            ((Bitmap)o3).recycle();
        }
        throw ex3;
    }
}
