// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.graphics.Color;
import android.util.Property;
import java.util.Iterator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.ViewGroup$LayoutParams;
import android.view.View$OnClickListener;
import android.widget.ImageView;
import android.widget.FrameLayout$LayoutParams;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator$AnimatorListener;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory$Options;
import android.util.AttributeSet;
import android.content.Context;
import java.util.HashSet;
import android.os.Handler;
import android.graphics.Point;
import java.util.Set;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

public class DessertCaseView extends FrameLayout
{
    private static final float[] ALPHA_MASK;
    private static final float[] MASK;
    private static final int NUM_PASTRIES;
    private static final int[] PASTRIES;
    private static final int[] RARE_PASTRIES;
    private static final int[] XRARE_PASTRIES;
    private static final int[] XXRARE_PASTRIES;
    float[] hsv;
    private int mCellSize;
    private View[] mCells;
    private int mColumns;
    private SparseArray<Drawable> mDrawables;
    private final Set<Point> mFreeList;
    private final Handler mHandler;
    private int mHeight;
    private final Runnable mJuggle;
    private int mRows;
    private boolean mStarted;
    private int mWidth;
    private final HashSet<View> tmpSet;
    
    static {
        PASTRIES = new int[] { R$drawable.dessert_kitkat, R$drawable.dessert_android };
        RARE_PASTRIES = new int[] { R$drawable.dessert_cupcake, R$drawable.dessert_donut, R$drawable.dessert_eclair, R$drawable.dessert_froyo, R$drawable.dessert_gingerbread, R$drawable.dessert_honeycomb, R$drawable.dessert_ics, R$drawable.dessert_jellybean };
        XRARE_PASTRIES = new int[] { R$drawable.dessert_petitfour, R$drawable.dessert_donutburger, R$drawable.dessert_flan, R$drawable.dessert_keylimepie };
        NUM_PASTRIES = DessertCaseView.PASTRIES.length + DessertCaseView.RARE_PASTRIES.length + DessertCaseView.XRARE_PASTRIES.length + (XXRARE_PASTRIES = new int[] { R$drawable.dessert_zombiegingerbread, R$drawable.dessert_dandroid, R$drawable.dessert_jandycane }).length;
        MASK = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f };
        ALPHA_MASK = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f };
    }
    
    public DessertCaseView(final Context context) {
        this(context, null);
    }
    
    public DessertCaseView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public DessertCaseView(final Context context, final AttributeSet set, int i) {
        super(context, set, i);
        this.mDrawables = (SparseArray<Drawable>)new SparseArray(DessertCaseView.NUM_PASTRIES);
        this.mFreeList = new HashSet<Point>();
        this.mHandler = new Handler();
        this.mJuggle = new Runnable() {
            @Override
            public void run() {
                final int childCount = DessertCaseView.this.getChildCount();
                for (int i = 0; i < 1; ++i) {
                    DessertCaseView.this.place(DessertCaseView.this.getChildAt((int)(Math.random() * childCount)), true);
                }
                DessertCaseView.this.fillFreeList();
                if (DessertCaseView.this.mStarted) {
                    DessertCaseView.this.mHandler.postDelayed(DessertCaseView.this.mJuggle, 2000L);
                }
            }
        };
        this.hsv = new float[] { 0.0f, 1.0f, 0.85f };
        this.tmpSet = new HashSet<View>();
        final Resources resources = this.getResources();
        this.mStarted = false;
        this.mCellSize = resources.getDimensionPixelSize(R$dimen.dessert_case_cell_size);
        final BitmapFactory$Options bitmapFactory$Options = new BitmapFactory$Options();
        if (this.mCellSize < 512) {
            bitmapFactory$Options.inSampleSize = 2;
        }
        bitmapFactory$Options.inMutable = true;
        Bitmap decodeResource = null;
        final int[] pastries = DessertCaseView.PASTRIES;
        final int[] rare_PASTRIES = DessertCaseView.RARE_PASTRIES;
        final int[] xrare_PASTRIES = DessertCaseView.XRARE_PASTRIES;
        final int[] xxrare_PASTRIES = DessertCaseView.XXRARE_PASTRIES;
        int[] array;
        int length;
        int j;
        int n;
        BitmapDrawable bitmapDrawable;
        int mCellSize;
        for (i = 0; i < 4; ++i) {
            array = (new int[][] { pastries, rare_PASTRIES, xrare_PASTRIES, xxrare_PASTRIES })[i];
            for (length = array.length, j = 0; j < length; ++j) {
                n = array[j];
                bitmapFactory$Options.inBitmap = decodeResource;
                decodeResource = BitmapFactory.decodeResource(resources, n, bitmapFactory$Options);
                bitmapDrawable = new BitmapDrawable(resources, convertToAlphaMask(decodeResource));
                bitmapDrawable.setColorFilter((ColorFilter)new ColorMatrixColorFilter(DessertCaseView.ALPHA_MASK));
                mCellSize = this.mCellSize;
                bitmapDrawable.setBounds(0, 0, mCellSize, mCellSize);
                this.mDrawables.append(n, (Object)bitmapDrawable);
            }
        }
    }
    
    private static Bitmap convertToAlphaMask(final Bitmap bitmap) {
        final Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap$Config.ALPHA_8);
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint();
        paint.setColorFilter((ColorFilter)new ColorMatrixColorFilter(DessertCaseView.MASK));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return bitmap2;
    }
    
    static float frand() {
        return (float)Math.random();
    }
    
    static float frand(final float n, final float n2) {
        return frand() * (n2 - n) + n;
    }
    
    private Point[] getOccupied(final View view) {
        final int intValue = (int)view.getTag(33554434);
        final Point point = (Point)view.getTag(33554433);
        if (point != null && intValue != 0) {
            final Point[] array = new Point[intValue * intValue];
            int n;
            for (int i = n = 0; i < intValue; ++i) {
                for (int j = 0; j < intValue; ++j, ++n) {
                    array[n] = new Point(point.x + i, point.y + j);
                }
            }
            return array;
        }
        return new Point[0];
    }
    
    static int irand(final int n, final int n2) {
        return (int)frand((float)n, (float)n2);
    }
    
    private final Animator$AnimatorListener makeHardwareLayerListener(final View view) {
        return (Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(final Animator animator) {
                view.setLayerType(0, (Paint)null);
            }
            
            public void onAnimationStart(final Animator animator) {
                view.setLayerType(2, (Paint)null);
                view.buildLayer();
            }
        };
    }
    
    public void fillFreeList() {
        this.fillFreeList(500);
    }
    
    public void fillFreeList(final int n) {
        synchronized (this) {
            final Context context = this.getContext();
            final FrameLayout$LayoutParams frameLayout$LayoutParams = new FrameLayout$LayoutParams(this.mCellSize, this.mCellSize);
            while (!this.mFreeList.isEmpty()) {
                final Point point = this.mFreeList.iterator().next();
                this.mFreeList.remove(point);
                if (this.mCells[point.y * this.mColumns + point.x] != null) {
                    continue;
                }
                final ImageView imageView = new ImageView(context);
                imageView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                    public void onClick(final View view) {
                        DessertCaseView.this.place((View)imageView, true);
                        DessertCaseView.this.postDelayed((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                DessertCaseView.this.fillFreeList();
                            }
                        }, 250L);
                    }
                });
                imageView.setBackgroundColor(this.random_color());
                final float frand = frand();
                Drawable drawable;
                if (frand < 5.0E-4f) {
                    drawable = (Drawable)this.mDrawables.get(this.pick(DessertCaseView.XXRARE_PASTRIES));
                }
                else if (frand < 0.005f) {
                    drawable = (Drawable)this.mDrawables.get(this.pick(DessertCaseView.XRARE_PASTRIES));
                }
                else if (frand < 0.5f) {
                    drawable = (Drawable)this.mDrawables.get(this.pick(DessertCaseView.RARE_PASTRIES));
                }
                else if (frand < 0.7f) {
                    drawable = (Drawable)this.mDrawables.get(this.pick(DessertCaseView.PASTRIES));
                }
                else {
                    drawable = null;
                }
                if (drawable != null) {
                    imageView.getOverlay().add(drawable);
                }
                final int mCellSize = this.mCellSize;
                frameLayout$LayoutParams.height = mCellSize;
                frameLayout$LayoutParams.width = mCellSize;
                this.addView((View)imageView, (ViewGroup$LayoutParams)frameLayout$LayoutParams);
                this.place((View)imageView, point, false);
                if (n <= 0) {
                    continue;
                }
                final float n2 = (float)(int)imageView.getTag(33554434);
                final float n3 = 0.5f * n2;
                imageView.setScaleX(n3);
                imageView.setScaleY(n3);
                imageView.setAlpha(0.0f);
                imageView.animate().withLayer().scaleX(n2).scaleY(n2).alpha(1.0f).setDuration((long)n);
            }
        }
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
    }
    
    protected void onSizeChanged(int i, int j, int mHeight, final int n) {
        synchronized (this) {
            super.onSizeChanged(i, j, mHeight, n);
            if (this.mWidth == i) {
                mHeight = this.mHeight;
                if (mHeight == j) {
                    return;
                }
            }
            final boolean mStarted = this.mStarted;
            if (mStarted) {
                this.stop();
            }
            this.mWidth = i;
            this.mHeight = j;
            this.mCells = null;
            this.removeAllViewsInLayout();
            this.mFreeList.clear();
            j = this.mHeight / this.mCellSize;
            this.mRows = j;
            i = this.mWidth / this.mCellSize;
            this.mColumns = i;
            this.mCells = new View[j * i];
            this.setScaleX(0.25f);
            this.setScaleY(0.25f);
            this.setTranslationX((this.mWidth - this.mCellSize * this.mColumns) * 0.5f * 0.25f);
            this.setTranslationY((this.mHeight - this.mCellSize * this.mRows) * 0.5f * 0.25f);
            for (i = 0; i < this.mRows; ++i) {
                for (j = 0; j < this.mColumns; ++j) {
                    this.mFreeList.add(new Point(j, i));
                }
            }
            if (mStarted) {
                this.start();
            }
        }
    }
    
    int pick(final int[] array) {
        return array[(int)(Math.random() * array.length)];
    }
    
    public void place(final View view, final Point point, final boolean b) {
        synchronized (this) {
            final int x = point.x;
            final int y = point.y;
            final float frand = frand();
            if (view.getTag(33554433) != null) {
                for (final Point point2 : this.getOccupied(view)) {
                    this.mFreeList.add(point2);
                    this.mCells[point2.y * this.mColumns + point2.x] = null;
                }
            }
            int j;
            if ((frand >= 0.01f) ? ((frand >= 0.1f) ? (frand < 0.33f && x != this.mColumns - 1 && y != this.mRows - 1) : (x < this.mColumns - 2 && y < this.mRows - 2)) : (x < this.mColumns - 3 && y < this.mRows - 3)) {
                j = 4;
            }
            else {
                j = 1;
            }
            view.setTag(33554433, (Object)point);
            view.setTag(33554434, (Object)j);
            this.tmpSet.clear();
            final Point[] occupied2 = this.getOccupied(view);
            for (final Point point3 : occupied2) {
                final View e = this.mCells[point3.y * this.mColumns + point3.x];
                if (e != null) {
                    this.tmpSet.add(e);
                }
            }
            for (final View view2 : this.tmpSet) {
                for (final Point point4 : this.getOccupied(view2)) {
                    this.mFreeList.add(point4);
                    this.mCells[point4.y * this.mColumns + point4.x] = null;
                }
                if (view2 != view) {
                    view2.setTag(33554433, (Object)null);
                    if (b) {
                        view2.animate().withLayer().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setDuration(500L).setInterpolator((TimeInterpolator)new AccelerateInterpolator()).setListener((Animator$AnimatorListener)new Animator$AnimatorListener() {
                            public void onAnimationCancel(final Animator animator) {
                            }
                            
                            public void onAnimationEnd(final Animator animator) {
                                DessertCaseView.this.removeView(view2);
                            }
                            
                            public void onAnimationRepeat(final Animator animator) {
                            }
                            
                            public void onAnimationStart(final Animator animator) {
                            }
                        }).start();
                    }
                    else {
                        this.removeView(view2);
                    }
                }
            }
            for (final Point point5 : occupied2) {
                this.mCells[point5.y * this.mColumns + point5.x] = view;
                this.mFreeList.remove(point5);
            }
            final float rotation = irand(0, 4) * 90.0f;
            if (b) {
                view.bringToFront();
                final AnimatorSet set = new AnimatorSet();
                final Property scale_X = View.SCALE_X;
                final float n2 = (float)j;
                set.playTogether(new Animator[] { (Animator)ObjectAnimator.ofFloat((Object)view, scale_X, new float[] { n2 }), (Animator)ObjectAnimator.ofFloat((Object)view, View.SCALE_Y, new float[] { n2 }) });
                set.setInterpolator((TimeInterpolator)new AnticipateOvershootInterpolator());
                set.setDuration(500L);
                final AnimatorSet set2 = new AnimatorSet();
                final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.ROTATION, new float[] { rotation });
                final Property x2 = View.X;
                final int mCellSize = this.mCellSize;
                --j;
                set2.playTogether(new Animator[] { (Animator)ofFloat, (Animator)ObjectAnimator.ofFloat((Object)view, x2, new float[] { (float)(x * mCellSize + this.mCellSize * j / 2) }), (Animator)ObjectAnimator.ofFloat((Object)view, View.Y, new float[] { (float)(y * this.mCellSize + j * this.mCellSize / 2) }) });
                set2.setInterpolator((TimeInterpolator)new DecelerateInterpolator());
                set2.setDuration(500L);
                set.addListener(this.makeHardwareLayerListener(view));
                set.start();
                set2.start();
            }
            else {
                final int mCellSize2 = this.mCellSize;
                final int n3 = j - 1;
                view.setX((float)(x * mCellSize2 + this.mCellSize * n3 / 2));
                view.setY((float)(y * this.mCellSize + n3 * this.mCellSize / 2));
                final float n4 = (float)j;
                view.setScaleX(n4);
                view.setScaleY(n4);
                view.setRotation(rotation);
            }
        }
    }
    
    public void place(final View view, final boolean b) {
        this.place(view, new Point(irand(0, this.mColumns), irand(0, this.mRows)), b);
    }
    
    int random_color() {
        this.hsv[0] = irand(0, 12) * 30.0f;
        return Color.HSVToColor(this.hsv);
    }
    
    public void start() {
        if (!this.mStarted) {
            this.mStarted = true;
            this.fillFreeList(2000);
        }
        this.mHandler.postDelayed(this.mJuggle, 5000L);
    }
    
    public void stop() {
        this.mStarted = false;
        this.mHandler.removeCallbacks(this.mJuggle);
    }
    
    public static class RescalingContainer extends FrameLayout
    {
        private DessertCaseView mView;
        
        public RescalingContainer(final Context context) {
            super(context);
            this.setSystemUiVisibility(5638);
        }
        
        protected void onLayout(final boolean b, int n, int n2, int n3, int n4) {
            final float n5 = (float)(n3 - n);
            final float n6 = (float)(n4 - n2);
            final DessertCaseView mView = this.mView;
            n3 = (int)(n5 / 0.25f / 2.0f);
            n4 = (int)(n6 / 0.25f / 2.0f);
            n += (int)(n5 * 0.5f);
            n2 += (int)(n6 * 0.5f);
            mView.layout(n - n3, n2 - n4, n + n3, n2 + n4);
        }
        
        public void setView(final DessertCaseView mView) {
            this.addView((View)mView);
            this.mView = mView;
        }
    }
}
