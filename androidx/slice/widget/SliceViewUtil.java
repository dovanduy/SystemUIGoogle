// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.R$attr;
import android.widget.ProgressBar;
import java.util.Calendar;
import android.text.format.DateUtils;
import android.content.res.TypedArray;
import android.content.Context;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.graphics.drawable.IconCompat;
import android.graphics.drawable.Drawable;

public class SliceViewUtil
{
    public static IconCompat createIconFromDrawable(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return IconCompat.createWithBitmap(((BitmapDrawable)drawable).getBitmap());
        }
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return IconCompat.createWithBitmap(bitmap);
    }
    
    public static Bitmap getCircularBitmap(final Bitmap bitmap) {
        final Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle((float)(bitmap.getWidth() / 2), (float)(bitmap.getHeight() / 2), (float)(bitmap.getWidth() / 2), paint);
        paint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return bitmap2;
    }
    
    public static int getColorAccent(final Context context) {
        return getColorAttr(context, 16843829);
    }
    
    public static int getColorAttr(final Context context, int color) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[] { color });
        color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        return color;
    }
    
    public static Drawable getDrawable(final Context context, final int n) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[] { n });
        final Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }
    
    public static CharSequence getTimestampString(final Context context, final long n) {
        if (n >= System.currentTimeMillis() && !DateUtils.isToday(n)) {
            return DateUtils.formatDateTime(context, n, 8);
        }
        return DateUtils.getRelativeTimeSpanString(n, Calendar.getInstance().getTimeInMillis(), 60000L, 262144);
    }
    
    public static int resolveLayoutDirection(final int n) {
        int n2 = n;
        if (n != 2 && (n2 = n) != 3 && (n2 = n) != 1) {
            if (n == 0) {
                n2 = n;
            }
            else {
                n2 = -1;
            }
        }
        return n2;
    }
    
    public static void tintIndeterminateProgressBar(final Context context, final ProgressBar progressBar) {
        final int colorAttr = getColorAttr(context, R$attr.colorControlHighlight);
        final Drawable wrap = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        if (wrap != null && colorAttr != 0) {
            wrap.setColorFilter(colorAttr, PorterDuff$Mode.MULTIPLY);
            progressBar.setProgressDrawable(wrap);
        }
    }
}
