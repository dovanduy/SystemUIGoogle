// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import android.view.View;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import java.util.HashMap;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import androidx.mediarouter.R$dimen;
import android.util.TypedValue;
import androidx.mediarouter.R$bool;
import android.content.Context;

final class MediaRouteDialogHelper
{
    public static int getDialogHeight(final Context context) {
        if (!context.getResources().getBoolean(R$bool.is_tablet)) {
            return -1;
        }
        return -2;
    }
    
    public static int getDialogWidth(final Context context) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final boolean b = displayMetrics.widthPixels < displayMetrics.heightPixels;
        final TypedValue typedValue = new TypedValue();
        final Resources resources = context.getResources();
        int n;
        if (b) {
            n = R$dimen.mr_dialog_fixed_width_minor;
        }
        else {
            n = R$dimen.mr_dialog_fixed_width_major;
        }
        resources.getValue(n, typedValue, true);
        final int type = typedValue.type;
        float n2;
        if (type == 5) {
            n2 = typedValue.getDimension(displayMetrics);
        }
        else {
            if (type != 6) {
                return -2;
            }
            final int widthPixels = displayMetrics.widthPixels;
            n2 = typedValue.getFraction((float)widthPixels, (float)widthPixels);
        }
        return (int)n2;
    }
    
    public static int getDialogWidthForDynamicGroup(final Context context) {
        if (!context.getResources().getBoolean(R$bool.is_tablet)) {
            return -1;
        }
        return getDialogWidth(context);
    }
    
    public static <E> HashMap<E, BitmapDrawable> getItemBitmapMap(final Context context, final ListView listView, final ArrayAdapter<E> arrayAdapter) {
        final HashMap<Object, BitmapDrawable> hashMap = (HashMap<Object, BitmapDrawable>)new HashMap<E, BitmapDrawable>();
        final int firstVisiblePosition = listView.getFirstVisiblePosition();
        for (int i = 0; i < listView.getChildCount(); ++i) {
            hashMap.put(arrayAdapter.getItem(firstVisiblePosition + i), getViewBitmap(context, listView.getChildAt(i)));
        }
        return (HashMap<E, BitmapDrawable>)hashMap;
    }
    
    public static <E> HashMap<E, Rect> getItemBoundMap(final ListView listView, final ArrayAdapter<E> arrayAdapter) {
        final HashMap<E, Rect> hashMap = new HashMap<E, Rect>();
        final int firstVisiblePosition = listView.getFirstVisiblePosition();
        for (int i = 0; i < listView.getChildCount(); ++i) {
            final Object item = arrayAdapter.getItem(firstVisiblePosition + i);
            final View child = listView.getChildAt(i);
            hashMap.put((E)item, new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom()));
        }
        return hashMap;
    }
    
    public static <E> Set<E> getItemsAdded(final List<E> c, final List<E> c2) {
        final HashSet<E> set = new HashSet<E>((Collection<? extends E>)c2);
        set.removeAll(c);
        return set;
    }
    
    public static <E> Set<E> getItemsRemoved(final List<E> c, final List<E> c2) {
        final HashSet<E> set = new HashSet<E>((Collection<? extends E>)c);
        set.removeAll(c2);
        return set;
    }
    
    private static BitmapDrawable getViewBitmap(final Context context, final View view) {
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap$Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return new BitmapDrawable(context.getResources(), bitmap);
    }
    
    public static <E> boolean listUnorderedEquals(final List<E> c, final List<E> c2) {
        return new HashSet(c).equals(new HashSet(c2));
    }
}
