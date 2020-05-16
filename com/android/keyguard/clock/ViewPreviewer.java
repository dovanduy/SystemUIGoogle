// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import android.util.Log;
import java.util.concurrent.FutureTask;
import android.view.View$MeasureSpec;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import java.util.concurrent.Callable;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.view.View;
import android.os.Looper;
import android.os.Handler;

final class ViewPreviewer
{
    private final Handler mMainHandler;
    
    ViewPreviewer() {
        this.mMainHandler = new Handler(Looper.getMainLooper());
    }
    
    private void dispatchVisibilityAggregated(final View view, final boolean b) {
        final int visibility = view.getVisibility();
        final boolean b2 = true;
        final int n = 0;
        final boolean b3 = visibility == 0;
        if (b3 || !b) {
            view.onVisibilityAggregated(b);
        }
        if (view instanceof ViewGroup) {
            final boolean b4 = b3 && b && b2;
            final ViewGroup viewGroup = (ViewGroup)view;
            for (int childCount = viewGroup.getChildCount(), i = n; i < childCount; ++i) {
                this.dispatchVisibilityAggregated(viewGroup.getChildAt(i), b4);
            }
        }
    }
    
    Bitmap createPreview(final View view, final int n, final int n2) {
        if (view == null) {
            return null;
        }
        final FutureTask<Bitmap> futureTask = new FutureTask<Bitmap>(new Callable<Bitmap>() {
            @Override
            public Bitmap call() {
                final Bitmap bitmap = Bitmap.createBitmap(n, n2, Bitmap$Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(-16777216);
                ViewPreviewer.this.dispatchVisibilityAggregated(view, true);
                view.measure(View$MeasureSpec.makeMeasureSpec(n, 1073741824), View$MeasureSpec.makeMeasureSpec(n2, 1073741824));
                view.layout(0, 0, n, n2);
                view.draw(canvas);
                return bitmap;
            }
        });
        if (Looper.myLooper() == Looper.getMainLooper()) {
            futureTask.run();
        }
        else {
            this.mMainHandler.post((Runnable)futureTask);
        }
        try {
            return futureTask.get();
        }
        catch (Exception ex) {
            Log.e("ViewPreviewer", "Error completing task", (Throwable)ex);
            return null;
        }
    }
}
