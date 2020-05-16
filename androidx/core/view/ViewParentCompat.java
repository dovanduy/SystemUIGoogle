// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.util.Log;
import android.os.Build$VERSION;
import android.view.View;
import android.view.ViewParent;

public final class ViewParentCompat
{
    public static boolean onNestedFling(final ViewParent obj, final View view, final float n, final float n2, final boolean b) {
        if (Build$VERSION.SDK_INT >= 21) {
            try {
                return obj.onNestedFling(view, n, n2, b);
            }
            catch (AbstractMethodError abstractMethodError) {
                final StringBuilder sb = new StringBuilder();
                sb.append("ViewParent ");
                sb.append(obj);
                sb.append(" does not implement interface method onNestedFling");
                Log.e("ViewParentCompat", sb.toString(), (Throwable)abstractMethodError);
                return false;
            }
        }
        if (obj instanceof NestedScrollingParent) {
            return ((NestedScrollingParent)obj).onNestedFling(view, n, n2, b);
        }
        return false;
    }
    
    public static boolean onNestedPreFling(final ViewParent obj, final View view, final float n, final float n2) {
        if (Build$VERSION.SDK_INT >= 21) {
            try {
                return obj.onNestedPreFling(view, n, n2);
            }
            catch (AbstractMethodError abstractMethodError) {
                final StringBuilder sb = new StringBuilder();
                sb.append("ViewParent ");
                sb.append(obj);
                sb.append(" does not implement interface method onNestedPreFling");
                Log.e("ViewParentCompat", sb.toString(), (Throwable)abstractMethodError);
                return false;
            }
        }
        if (obj instanceof NestedScrollingParent) {
            return ((NestedScrollingParent)obj).onNestedPreFling(view, n, n2);
        }
        return false;
    }
    
    public static void onNestedPreScroll(final ViewParent obj, final View view, final int n, final int n2, final int[] array, final int n3) {
        if (obj instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2)obj).onNestedPreScroll(view, n, n2, array, n3);
        }
        else if (n3 == 0) {
            if (Build$VERSION.SDK_INT >= 21) {
                try {
                    obj.onNestedPreScroll(view, n, n2, array);
                }
                catch (AbstractMethodError abstractMethodError) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("ViewParent ");
                    sb.append(obj);
                    sb.append(" does not implement interface method onNestedPreScroll");
                    Log.e("ViewParentCompat", sb.toString(), (Throwable)abstractMethodError);
                }
            }
            else if (obj instanceof NestedScrollingParent) {
                ((NestedScrollingParent)obj).onNestedPreScroll(view, n, n2, array);
            }
        }
    }
    
    public static void onNestedScroll(final ViewParent obj, final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        if (obj instanceof NestedScrollingParent3) {
            ((NestedScrollingParent3)obj).onNestedScroll(view, n, n2, n3, n4, n5, array);
        }
        else {
            array[0] += n3;
            array[1] += n4;
            if (obj instanceof NestedScrollingParent2) {
                ((NestedScrollingParent2)obj).onNestedScroll(view, n, n2, n3, n4, n5);
            }
            else if (n5 == 0) {
                if (Build$VERSION.SDK_INT >= 21) {
                    try {
                        obj.onNestedScroll(view, n, n2, n3, n4);
                    }
                    catch (AbstractMethodError abstractMethodError) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("ViewParent ");
                        sb.append(obj);
                        sb.append(" does not implement interface method onNestedScroll");
                        Log.e("ViewParentCompat", sb.toString(), (Throwable)abstractMethodError);
                    }
                }
                else if (obj instanceof NestedScrollingParent) {
                    ((NestedScrollingParent)obj).onNestedScroll(view, n, n2, n3, n4);
                }
            }
        }
    }
    
    public static void onNestedScrollAccepted(final ViewParent obj, final View view, final View view2, final int n, final int n2) {
        if (obj instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2)obj).onNestedScrollAccepted(view, view2, n, n2);
        }
        else if (n2 == 0) {
            if (Build$VERSION.SDK_INT >= 21) {
                try {
                    obj.onNestedScrollAccepted(view, view2, n);
                }
                catch (AbstractMethodError abstractMethodError) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("ViewParent ");
                    sb.append(obj);
                    sb.append(" does not implement interface method onNestedScrollAccepted");
                    Log.e("ViewParentCompat", sb.toString(), (Throwable)abstractMethodError);
                }
            }
            else if (obj instanceof NestedScrollingParent) {
                ((NestedScrollingParent)obj).onNestedScrollAccepted(view, view2, n);
            }
        }
    }
    
    public static boolean onStartNestedScroll(final ViewParent obj, final View view, final View view2, final int n, final int n2) {
        if (obj instanceof NestedScrollingParent2) {
            return ((NestedScrollingParent2)obj).onStartNestedScroll(view, view2, n, n2);
        }
        if (n2 == 0) {
            if (Build$VERSION.SDK_INT >= 21) {
                try {
                    return obj.onStartNestedScroll(view, view2, n);
                }
                catch (AbstractMethodError abstractMethodError) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("ViewParent ");
                    sb.append(obj);
                    sb.append(" does not implement interface method onStartNestedScroll");
                    Log.e("ViewParentCompat", sb.toString(), (Throwable)abstractMethodError);
                    return false;
                }
            }
            if (obj instanceof NestedScrollingParent) {
                return ((NestedScrollingParent)obj).onStartNestedScroll(view, view2, n);
            }
        }
        return false;
    }
    
    public static void onStopNestedScroll(final ViewParent obj, final View view, final int n) {
        if (obj instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2)obj).onStopNestedScroll(view, n);
        }
        else if (n == 0) {
            if (Build$VERSION.SDK_INT >= 21) {
                try {
                    obj.onStopNestedScroll(view);
                }
                catch (AbstractMethodError abstractMethodError) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("ViewParent ");
                    sb.append(obj);
                    sb.append(" does not implement interface method onStopNestedScroll");
                    Log.e("ViewParentCompat", sb.toString(), (Throwable)abstractMethodError);
                }
            }
            else if (obj instanceof NestedScrollingParent) {
                ((NestedScrollingParent)obj).onStopNestedScroll(view);
            }
        }
    }
}
