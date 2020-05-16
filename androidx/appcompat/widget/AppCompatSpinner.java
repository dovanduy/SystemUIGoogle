// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.view.View$BaseSavedState;
import android.widget.PopupWindow$OnDismissListener;
import androidx.core.view.ViewCompat;
import android.widget.AdapterView;
import android.widget.AdapterView$OnItemClickListener;
import android.database.DataSetObserver;
import android.widget.ThemedSpinnerAdapter;
import android.widget.ListView;
import android.util.Log;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface$OnClickListener;
import androidx.appcompat.content.res.AppCompatResources;
import android.widget.ListAdapter;
import android.widget.Adapter;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.os.Parcelable;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.os.Build$VERSION;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import android.view.View$MeasureSpec;
import android.graphics.drawable.Drawable;
import android.content.res.Resources$Theme;
import androidx.appcompat.R$attr;
import android.util.AttributeSet;
import android.graphics.Rect;
import android.widget.SpinnerAdapter;
import android.content.Context;
import androidx.core.view.TintableBackgroundView;
import android.widget.Spinner;

public class AppCompatSpinner extends Spinner implements TintableBackgroundView
{
    private static final int[] ATTRS_ANDROID_SPINNERMODE;
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    int mDropDownWidth;
    private ForwardingListener mForwardingListener;
    private SpinnerPopup mPopup;
    private final Context mPopupContext;
    private final boolean mPopupSet;
    private SpinnerAdapter mTempAdapter;
    final Rect mTempRect;
    
    static {
        ATTRS_ANDROID_SPINNERMODE = new int[] { 16843505 };
    }
    
    public AppCompatSpinner(final Context context, final AttributeSet set) {
        this(context, set, R$attr.spinnerStyle);
    }
    
    public AppCompatSpinner(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, -1);
    }
    
    public AppCompatSpinner(final Context context, final AttributeSet set, final int n, final int n2) {
        this(context, set, n, n2, null);
    }
    
    public AppCompatSpinner(final Context p0, final AttributeSet p1, final int p2, final int p3, final Resources$Theme p4) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1        
        //     2: aload_2        
        //     3: iload_3        
        //     4: invokespecial   android/widget/Spinner.<init>:(Landroid/content/Context;Landroid/util/AttributeSet;I)V
        //     7: aload_0        
        //     8: new             Landroid/graphics/Rect;
        //    11: dup            
        //    12: invokespecial   android/graphics/Rect.<init>:()V
        //    15: putfield        androidx/appcompat/widget/AppCompatSpinner.mTempRect:Landroid/graphics/Rect;
        //    18: aload_0        
        //    19: aload_0        
        //    20: invokevirtual   android/widget/Spinner.getContext:()Landroid/content/Context;
        //    23: invokestatic    androidx/appcompat/widget/ThemeUtils.checkAppCompatTheme:(Landroid/view/View;Landroid/content/Context;)V
        //    26: aload_1        
        //    27: aload_2        
        //    28: getstatic       androidx/appcompat/R$styleable.Spinner:[I
        //    31: iload_3        
        //    32: iconst_0       
        //    33: invokestatic    androidx/appcompat/widget/TintTypedArray.obtainStyledAttributes:(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroidx/appcompat/widget/TintTypedArray;
        //    36: astore          6
        //    38: aload_0        
        //    39: new             Landroidx/appcompat/widget/AppCompatBackgroundHelper;
        //    42: dup            
        //    43: aload_0        
        //    44: invokespecial   androidx/appcompat/widget/AppCompatBackgroundHelper.<init>:(Landroid/view/View;)V
        //    47: putfield        androidx/appcompat/widget/AppCompatSpinner.mBackgroundTintHelper:Landroidx/appcompat/widget/AppCompatBackgroundHelper;
        //    50: aload           5
        //    52: ifnull          72
        //    55: aload_0        
        //    56: new             Landroidx/appcompat/view/ContextThemeWrapper;
        //    59: dup            
        //    60: aload_1        
        //    61: aload           5
        //    63: invokespecial   androidx/appcompat/view/ContextThemeWrapper.<init>:(Landroid/content/Context;Landroid/content/res/Resources$Theme;)V
        //    66: putfield        androidx/appcompat/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //    69: goto            110
        //    72: aload           6
        //    74: getstatic       androidx/appcompat/R$styleable.Spinner_popupTheme:I
        //    77: iconst_0       
        //    78: invokevirtual   androidx/appcompat/widget/TintTypedArray.getResourceId:(II)I
        //    81: istore          7
        //    83: iload           7
        //    85: ifeq            105
        //    88: aload_0        
        //    89: new             Landroidx/appcompat/view/ContextThemeWrapper;
        //    92: dup            
        //    93: aload_1        
        //    94: iload           7
        //    96: invokespecial   androidx/appcompat/view/ContextThemeWrapper.<init>:(Landroid/content/Context;I)V
        //    99: putfield        androidx/appcompat/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   102: goto            110
        //   105: aload_0        
        //   106: aload_1        
        //   107: putfield        androidx/appcompat/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   110: aconst_null    
        //   111: astore          8
        //   113: iload           4
        //   115: istore          9
        //   117: iload           4
        //   119: iconst_m1      
        //   120: if_icmpne       247
        //   123: aload_1        
        //   124: aload_2        
        //   125: getstatic       androidx/appcompat/widget/AppCompatSpinner.ATTRS_ANDROID_SPINNERMODE:[I
        //   128: iload_3        
        //   129: iconst_0       
        //   130: invokevirtual   android/content/Context.obtainStyledAttributes:(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;
        //   133: astore          5
        //   135: iload           4
        //   137: istore          7
        //   139: aload           5
        //   141: astore          8
        //   143: aload           5
        //   145: iconst_0       
        //   146: invokevirtual   android/content/res/TypedArray.hasValue:(I)Z
        //   149: ifeq            165
        //   152: aload           5
        //   154: astore          8
        //   156: aload           5
        //   158: iconst_0       
        //   159: iconst_0       
        //   160: invokevirtual   android/content/res/TypedArray.getInt:(II)I
        //   163: istore          7
        //   165: iload           7
        //   167: istore          9
        //   169: aload           5
        //   171: ifnull          247
        //   174: iload           7
        //   176: istore          4
        //   178: aload           5
        //   180: invokevirtual   android/content/res/TypedArray.recycle:()V
        //   183: iload           4
        //   185: istore          9
        //   187: goto            247
        //   190: astore          10
        //   192: goto            207
        //   195: astore_2       
        //   196: aload           8
        //   198: astore_1       
        //   199: goto            237
        //   202: astore          10
        //   204: aconst_null    
        //   205: astore          5
        //   207: aload           5
        //   209: astore          8
        //   211: ldc             "AppCompatSpinner"
        //   213: ldc             "Could not read android:spinnerMode"
        //   215: aload           10
        //   217: invokestatic    android/util/Log.i:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   220: pop            
        //   221: iload           4
        //   223: istore          9
        //   225: aload           5
        //   227: ifnull          247
        //   230: goto            178
        //   233: astore_2       
        //   234: aload           8
        //   236: astore_1       
        //   237: aload_1        
        //   238: ifnull          245
        //   241: aload_1        
        //   242: invokevirtual   android/content/res/TypedArray.recycle:()V
        //   245: aload_2        
        //   246: athrow         
        //   247: iload           9
        //   249: ifeq            361
        //   252: iload           9
        //   254: iconst_1       
        //   255: if_icmpeq       261
        //   258: goto            392
        //   261: new             Landroidx/appcompat/widget/AppCompatSpinner$DropdownPopup;
        //   264: dup            
        //   265: aload_0        
        //   266: aload_0        
        //   267: getfield        androidx/appcompat/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   270: aload_2        
        //   271: iload_3        
        //   272: invokespecial   androidx/appcompat/widget/AppCompatSpinner$DropdownPopup.<init>:(Landroidx/appcompat/widget/AppCompatSpinner;Landroid/content/Context;Landroid/util/AttributeSet;I)V
        //   275: astore          8
        //   277: aload_0        
        //   278: getfield        androidx/appcompat/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   281: aload_2        
        //   282: getstatic       androidx/appcompat/R$styleable.Spinner:[I
        //   285: iload_3        
        //   286: iconst_0       
        //   287: invokestatic    androidx/appcompat/widget/TintTypedArray.obtainStyledAttributes:(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroidx/appcompat/widget/TintTypedArray;
        //   290: astore          5
        //   292: aload_0        
        //   293: aload           5
        //   295: getstatic       androidx/appcompat/R$styleable.Spinner_android_dropDownWidth:I
        //   298: bipush          -2
        //   300: invokevirtual   androidx/appcompat/widget/TintTypedArray.getLayoutDimension:(II)I
        //   303: putfield        androidx/appcompat/widget/AppCompatSpinner.mDropDownWidth:I
        //   306: aload           8
        //   308: aload           5
        //   310: getstatic       androidx/appcompat/R$styleable.Spinner_android_popupBackground:I
        //   313: invokevirtual   androidx/appcompat/widget/TintTypedArray.getDrawable:(I)Landroid/graphics/drawable/Drawable;
        //   316: invokevirtual   androidx/appcompat/widget/ListPopupWindow.setBackgroundDrawable:(Landroid/graphics/drawable/Drawable;)V
        //   319: aload           8
        //   321: aload           6
        //   323: getstatic       androidx/appcompat/R$styleable.Spinner_android_prompt:I
        //   326: invokevirtual   androidx/appcompat/widget/TintTypedArray.getString:(I)Ljava/lang/String;
        //   329: invokevirtual   androidx/appcompat/widget/AppCompatSpinner$DropdownPopup.setPromptText:(Ljava/lang/CharSequence;)V
        //   332: aload           5
        //   334: invokevirtual   androidx/appcompat/widget/TintTypedArray.recycle:()V
        //   337: aload_0        
        //   338: aload           8
        //   340: putfield        androidx/appcompat/widget/AppCompatSpinner.mPopup:Landroidx/appcompat/widget/AppCompatSpinner$SpinnerPopup;
        //   343: aload_0        
        //   344: new             Landroidx/appcompat/widget/AppCompatSpinner$1;
        //   347: dup            
        //   348: aload_0        
        //   349: aload_0        
        //   350: aload           8
        //   352: invokespecial   androidx/appcompat/widget/AppCompatSpinner$1.<init>:(Landroidx/appcompat/widget/AppCompatSpinner;Landroid/view/View;Landroidx/appcompat/widget/AppCompatSpinner$DropdownPopup;)V
        //   355: putfield        androidx/appcompat/widget/AppCompatSpinner.mForwardingListener:Landroidx/appcompat/widget/ForwardingListener;
        //   358: goto            392
        //   361: new             Landroidx/appcompat/widget/AppCompatSpinner$DialogPopup;
        //   364: dup            
        //   365: aload_0        
        //   366: invokespecial   androidx/appcompat/widget/AppCompatSpinner$DialogPopup.<init>:(Landroidx/appcompat/widget/AppCompatSpinner;)V
        //   369: astore          5
        //   371: aload_0        
        //   372: aload           5
        //   374: putfield        androidx/appcompat/widget/AppCompatSpinner.mPopup:Landroidx/appcompat/widget/AppCompatSpinner$SpinnerPopup;
        //   377: aload           5
        //   379: aload           6
        //   381: getstatic       androidx/appcompat/R$styleable.Spinner_android_prompt:I
        //   384: invokevirtual   androidx/appcompat/widget/TintTypedArray.getString:(I)Ljava/lang/String;
        //   387: invokeinterface androidx/appcompat/widget/AppCompatSpinner$SpinnerPopup.setPromptText:(Ljava/lang/CharSequence;)V
        //   392: aload           6
        //   394: getstatic       androidx/appcompat/R$styleable.Spinner_android_entries:I
        //   397: invokevirtual   androidx/appcompat/widget/TintTypedArray.getTextArray:(I)[Ljava/lang/CharSequence;
        //   400: astore          5
        //   402: aload           5
        //   404: ifnull          432
        //   407: new             Landroid/widget/ArrayAdapter;
        //   410: dup            
        //   411: aload_1        
        //   412: ldc             17367048
        //   414: aload           5
        //   416: invokespecial   android/widget/ArrayAdapter.<init>:(Landroid/content/Context;I[Ljava/lang/Object;)V
        //   419: astore_1       
        //   420: aload_1        
        //   421: getstatic       androidx/appcompat/R$layout.support_simple_spinner_dropdown_item:I
        //   424: invokevirtual   android/widget/ArrayAdapter.setDropDownViewResource:(I)V
        //   427: aload_0        
        //   428: aload_1        
        //   429: invokevirtual   androidx/appcompat/widget/AppCompatSpinner.setAdapter:(Landroid/widget/SpinnerAdapter;)V
        //   432: aload           6
        //   434: invokevirtual   androidx/appcompat/widget/TintTypedArray.recycle:()V
        //   437: aload_0        
        //   438: iconst_1       
        //   439: putfield        androidx/appcompat/widget/AppCompatSpinner.mPopupSet:Z
        //   442: aload_0        
        //   443: getfield        androidx/appcompat/widget/AppCompatSpinner.mTempAdapter:Landroid/widget/SpinnerAdapter;
        //   446: astore_1       
        //   447: aload_1        
        //   448: ifnull          461
        //   451: aload_0        
        //   452: aload_1        
        //   453: invokevirtual   androidx/appcompat/widget/AppCompatSpinner.setAdapter:(Landroid/widget/SpinnerAdapter;)V
        //   456: aload_0        
        //   457: aconst_null    
        //   458: putfield        androidx/appcompat/widget/AppCompatSpinner.mTempAdapter:Landroid/widget/SpinnerAdapter;
        //   461: aload_0        
        //   462: getfield        androidx/appcompat/widget/AppCompatSpinner.mBackgroundTintHelper:Landroidx/appcompat/widget/AppCompatBackgroundHelper;
        //   465: aload_2        
        //   466: iload_3        
        //   467: invokevirtual   androidx/appcompat/widget/AppCompatBackgroundHelper.loadFromAttributes:(Landroid/util/AttributeSet;I)V
        //   470: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  123    135    202    207    Ljava/lang/Exception;
        //  123    135    195    202    Any
        //  143    152    190    195    Ljava/lang/Exception;
        //  143    152    233    237    Any
        //  156    165    190    195    Ljava/lang/Exception;
        //  156    165    233    237    Any
        //  211    221    233    237    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0165:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    int compatMeasureContentWidth(final SpinnerAdapter spinnerAdapter, final Drawable drawable) {
        int n = 0;
        if (spinnerAdapter == null) {
            return 0;
        }
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 0);
        final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), 0);
        final int max = Math.max(0, this.getSelectedItemPosition());
        final int min = Math.min(spinnerAdapter.getCount(), max + 15);
        int i = Math.max(0, max - (15 - (min - max)));
        View view = null;
        int max2 = 0;
        while (i < min) {
            final int itemViewType = spinnerAdapter.getItemViewType(i);
            int n2;
            if (itemViewType != (n2 = n)) {
                view = null;
                n2 = itemViewType;
            }
            view = spinnerAdapter.getView(i, view, (ViewGroup)this);
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup$LayoutParams(-2, -2));
            }
            view.measure(measureSpec, measureSpec2);
            max2 = Math.max(max2, view.getMeasuredWidth());
            ++i;
            n = n2;
        }
        int n3 = max2;
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            final Rect mTempRect = this.mTempRect;
            n3 = max2 + (mTempRect.left + mTempRect.right);
        }
        return n3;
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySupportBackgroundTint();
        }
    }
    
    public int getDropDownHorizontalOffset() {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            return mPopup.getHorizontalOffset();
        }
        if (Build$VERSION.SDK_INT >= 16) {
            return super.getDropDownHorizontalOffset();
        }
        return 0;
    }
    
    public int getDropDownVerticalOffset() {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            return mPopup.getVerticalOffset();
        }
        if (Build$VERSION.SDK_INT >= 16) {
            return super.getDropDownVerticalOffset();
        }
        return 0;
    }
    
    public int getDropDownWidth() {
        if (this.mPopup != null) {
            return this.mDropDownWidth;
        }
        if (Build$VERSION.SDK_INT >= 16) {
            return super.getDropDownWidth();
        }
        return 0;
    }
    
    final SpinnerPopup getInternalPopup() {
        return this.mPopup;
    }
    
    public Drawable getPopupBackground() {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            return mPopup.getBackground();
        }
        if (Build$VERSION.SDK_INT >= 16) {
            return super.getPopupBackground();
        }
        return null;
    }
    
    public Context getPopupContext() {
        return this.mPopupContext;
    }
    
    public CharSequence getPrompt() {
        final SpinnerPopup mPopup = this.mPopup;
        CharSequence charSequence;
        if (mPopup != null) {
            charSequence = mPopup.getHintText();
        }
        else {
            charSequence = super.getPrompt();
        }
        return charSequence;
    }
    
    public ColorStateList getSupportBackgroundTintList() {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        ColorStateList supportBackgroundTintList;
        if (mBackgroundTintHelper != null) {
            supportBackgroundTintList = mBackgroundTintHelper.getSupportBackgroundTintList();
        }
        else {
            supportBackgroundTintList = null;
        }
        return supportBackgroundTintList;
    }
    
    public PorterDuff$Mode getSupportBackgroundTintMode() {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        PorterDuff$Mode supportBackgroundTintMode;
        if (mBackgroundTintHelper != null) {
            supportBackgroundTintMode = mBackgroundTintHelper.getSupportBackgroundTintMode();
        }
        else {
            supportBackgroundTintMode = null;
        }
        return supportBackgroundTintMode;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null && mPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        if (this.mPopup != null && View$MeasureSpec.getMode(n) == Integer.MIN_VALUE) {
            this.setMeasuredDimension(Math.min(Math.max(this.getMeasuredWidth(), this.compatMeasureContentWidth(this.getAdapter(), this.getBackground())), View$MeasureSpec.getSize(n)), this.getMeasuredHeight());
        }
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.mShowDropdown) {
            final ViewTreeObserver viewTreeObserver = this.getViewTreeObserver();
            if (viewTreeObserver != null) {
                viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (!AppCompatSpinner.this.getInternalPopup().isShowing()) {
                            AppCompatSpinner.this.showPopup();
                        }
                        final ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
                        if (viewTreeObserver != null) {
                            if (Build$VERSION.SDK_INT >= 16) {
                                viewTreeObserver.removeOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                            }
                            else {
                                viewTreeObserver.removeGlobalOnLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                            }
                        }
                    }
                });
            }
        }
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        final SpinnerPopup mPopup = this.mPopup;
        savedState.mShowDropdown = (mPopup != null && mPopup.isShowing());
        return (Parcelable)savedState;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final ForwardingListener mForwardingListener = this.mForwardingListener;
        return (mForwardingListener != null && mForwardingListener.onTouch((View)this, motionEvent)) || super.onTouchEvent(motionEvent);
    }
    
    public boolean performClick() {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            if (!mPopup.isShowing()) {
                this.showPopup();
            }
            return true;
        }
        return super.performClick();
    }
    
    public void setAdapter(final SpinnerAdapter spinnerAdapter) {
        if (!this.mPopupSet) {
            this.mTempAdapter = spinnerAdapter;
            return;
        }
        super.setAdapter(spinnerAdapter);
        if (this.mPopup != null) {
            Context context;
            if ((context = this.mPopupContext) == null) {
                context = this.getContext();
            }
            this.mPopup.setAdapter((ListAdapter)new DropDownAdapter(spinnerAdapter, context.getTheme()));
        }
    }
    
    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        super.setBackgroundDrawable(backgroundDrawable);
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.onSetBackgroundDrawable(backgroundDrawable);
        }
    }
    
    public void setBackgroundResource(final int backgroundResource) {
        super.setBackgroundResource(backgroundResource);
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.onSetBackgroundResource(backgroundResource);
        }
    }
    
    public void setDropDownHorizontalOffset(final int dropDownHorizontalOffset) {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            mPopup.setHorizontalOriginalOffset(dropDownHorizontalOffset);
            this.mPopup.setHorizontalOffset(dropDownHorizontalOffset);
        }
        else if (Build$VERSION.SDK_INT >= 16) {
            super.setDropDownHorizontalOffset(dropDownHorizontalOffset);
        }
    }
    
    public void setDropDownVerticalOffset(final int n) {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            mPopup.setVerticalOffset(n);
        }
        else if (Build$VERSION.SDK_INT >= 16) {
            super.setDropDownVerticalOffset(n);
        }
    }
    
    public void setDropDownWidth(final int n) {
        if (this.mPopup != null) {
            this.mDropDownWidth = n;
        }
        else if (Build$VERSION.SDK_INT >= 16) {
            super.setDropDownWidth(n);
        }
    }
    
    public void setPopupBackgroundDrawable(final Drawable drawable) {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            mPopup.setBackgroundDrawable(drawable);
        }
        else if (Build$VERSION.SDK_INT >= 16) {
            super.setPopupBackgroundDrawable(drawable);
        }
    }
    
    public void setPopupBackgroundResource(final int n) {
        this.setPopupBackgroundDrawable(AppCompatResources.getDrawable(this.getPopupContext(), n));
    }
    
    public void setPrompt(final CharSequence charSequence) {
        final SpinnerPopup mPopup = this.mPopup;
        if (mPopup != null) {
            mPopup.setPromptText(charSequence);
        }
        else {
            super.setPrompt(charSequence);
        }
    }
    
    public void setSupportBackgroundTintList(final ColorStateList supportBackgroundTintList) {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.setSupportBackgroundTintList(supportBackgroundTintList);
        }
    }
    
    public void setSupportBackgroundTintMode(final PorterDuff$Mode supportBackgroundTintMode) {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.setSupportBackgroundTintMode(supportBackgroundTintMode);
        }
    }
    
    void showPopup() {
        if (Build$VERSION.SDK_INT >= 17) {
            this.mPopup.show(this.getTextDirection(), this.getTextAlignment());
        }
        else {
            this.mPopup.show(-1, -1);
        }
    }
    
    class DialogPopup implements SpinnerPopup, DialogInterface$OnClickListener
    {
        private ListAdapter mListAdapter;
        AlertDialog mPopup;
        private CharSequence mPrompt;
        
        @Override
        public void dismiss() {
            final AlertDialog mPopup = this.mPopup;
            if (mPopup != null) {
                mPopup.dismiss();
                this.mPopup = null;
            }
        }
        
        @Override
        public Drawable getBackground() {
            return null;
        }
        
        @Override
        public CharSequence getHintText() {
            return this.mPrompt;
        }
        
        @Override
        public int getHorizontalOffset() {
            return 0;
        }
        
        @Override
        public int getVerticalOffset() {
            return 0;
        }
        
        @Override
        public boolean isShowing() {
            final AlertDialog mPopup = this.mPopup;
            return mPopup != null && mPopup.isShowing();
        }
        
        public void onClick(final DialogInterface dialogInterface, final int selection) {
            AppCompatSpinner.this.setSelection(selection);
            if (AppCompatSpinner.this.getOnItemClickListener() != null) {
                AppCompatSpinner.this.performItemClick((View)null, selection, this.mListAdapter.getItemId(selection));
            }
            this.dismiss();
        }
        
        @Override
        public void setAdapter(final ListAdapter mListAdapter) {
            this.mListAdapter = mListAdapter;
        }
        
        @Override
        public void setBackgroundDrawable(final Drawable drawable) {
            Log.e("AppCompatSpinner", "Cannot set popup background for MODE_DIALOG, ignoring");
        }
        
        @Override
        public void setHorizontalOffset(final int n) {
            Log.e("AppCompatSpinner", "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }
        
        @Override
        public void setHorizontalOriginalOffset(final int n) {
            Log.e("AppCompatSpinner", "Cannot set horizontal (original) offset for MODE_DIALOG, ignoring");
        }
        
        @Override
        public void setPromptText(final CharSequence mPrompt) {
            this.mPrompt = mPrompt;
        }
        
        @Override
        public void setVerticalOffset(final int n) {
            Log.e("AppCompatSpinner", "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }
        
        @Override
        public void show(final int textDirection, final int textAlignment) {
            if (this.mListAdapter == null) {
                return;
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(AppCompatSpinner.this.getPopupContext());
            final CharSequence mPrompt = this.mPrompt;
            if (mPrompt != null) {
                builder.setTitle(mPrompt);
            }
            builder.setSingleChoiceItems(this.mListAdapter, AppCompatSpinner.this.getSelectedItemPosition(), (DialogInterface$OnClickListener)this);
            final AlertDialog create = builder.create();
            this.mPopup = create;
            final ListView listView = create.getListView();
            if (Build$VERSION.SDK_INT >= 17) {
                listView.setTextDirection(textDirection);
                listView.setTextAlignment(textAlignment);
            }
            this.mPopup.show();
        }
    }
    
    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter
    {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;
        
        public DropDownAdapter(final SpinnerAdapter mAdapter, final Resources$Theme resources$Theme) {
            this.mAdapter = mAdapter;
            if (mAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter)mAdapter;
            }
            if (resources$Theme != null) {
                if (Build$VERSION.SDK_INT >= 23 && mAdapter instanceof ThemedSpinnerAdapter) {
                    final ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter)mAdapter;
                    if (themedSpinnerAdapter.getDropDownViewTheme() != resources$Theme) {
                        themedSpinnerAdapter.setDropDownViewTheme(resources$Theme);
                    }
                }
                else if (mAdapter instanceof androidx.appcompat.widget.ThemedSpinnerAdapter) {
                    final androidx.appcompat.widget.ThemedSpinnerAdapter themedSpinnerAdapter2 = (androidx.appcompat.widget.ThemedSpinnerAdapter)mAdapter;
                    if (themedSpinnerAdapter2.getDropDownViewTheme() == null) {
                        themedSpinnerAdapter2.setDropDownViewTheme(resources$Theme);
                    }
                }
            }
        }
        
        public boolean areAllItemsEnabled() {
            final ListAdapter mListAdapter = this.mListAdapter;
            return mListAdapter == null || mListAdapter.areAllItemsEnabled();
        }
        
        public int getCount() {
            final SpinnerAdapter mAdapter = this.mAdapter;
            int count;
            if (mAdapter == null) {
                count = 0;
            }
            else {
                count = mAdapter.getCount();
            }
            return count;
        }
        
        public View getDropDownView(final int n, View dropDownView, final ViewGroup viewGroup) {
            final SpinnerAdapter mAdapter = this.mAdapter;
            if (mAdapter == null) {
                dropDownView = null;
            }
            else {
                dropDownView = mAdapter.getDropDownView(n, dropDownView, viewGroup);
            }
            return dropDownView;
        }
        
        public Object getItem(final int n) {
            final SpinnerAdapter mAdapter = this.mAdapter;
            Object item;
            if (mAdapter == null) {
                item = null;
            }
            else {
                item = mAdapter.getItem(n);
            }
            return item;
        }
        
        public long getItemId(final int n) {
            final SpinnerAdapter mAdapter = this.mAdapter;
            long itemId;
            if (mAdapter == null) {
                itemId = -1L;
            }
            else {
                itemId = mAdapter.getItemId(n);
            }
            return itemId;
        }
        
        public int getItemViewType(final int n) {
            return 0;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            return this.getDropDownView(n, view, viewGroup);
        }
        
        public int getViewTypeCount() {
            return 1;
        }
        
        public boolean hasStableIds() {
            final SpinnerAdapter mAdapter = this.mAdapter;
            return mAdapter != null && mAdapter.hasStableIds();
        }
        
        public boolean isEmpty() {
            return this.getCount() == 0;
        }
        
        public boolean isEnabled(final int n) {
            final ListAdapter mListAdapter = this.mListAdapter;
            return mListAdapter == null || mListAdapter.isEnabled(n);
        }
        
        public void registerDataSetObserver(final DataSetObserver dataSetObserver) {
            final SpinnerAdapter mAdapter = this.mAdapter;
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(dataSetObserver);
            }
        }
        
        public void unregisterDataSetObserver(final DataSetObserver dataSetObserver) {
            final SpinnerAdapter mAdapter = this.mAdapter;
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }
    
    class DropdownPopup extends ListPopupWindow implements SpinnerPopup
    {
        ListAdapter mAdapter;
        private CharSequence mHintText;
        private int mOriginalHorizontalOffset;
        private final Rect mVisibleRect;
        final /* synthetic */ AppCompatSpinner this$0;
        
        public DropdownPopup(final Context context, final AttributeSet set, final int n) {
            super(context, set, n);
            this.mVisibleRect = new Rect();
            this.setAnchorView((View)AppCompatSpinner.this);
            this.setModal(true);
            this.setPromptPosition(0);
            this.setOnItemClickListener((AdapterView$OnItemClickListener)new AdapterView$OnItemClickListener(AppCompatSpinner.this) {
                public void onItemClick(final AdapterView<?> adapterView, final View view, final int selection, final long n) {
                    AppCompatSpinner.this.setSelection(selection);
                    if (AppCompatSpinner.this.getOnItemClickListener() != null) {
                        final DropdownPopup this$1 = DropdownPopup.this;
                        this$1.this$0.performItemClick(view, selection, this$1.mAdapter.getItemId(selection));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }
        
        void computeContentWidth() {
            final Drawable background = this.getBackground();
            int right = 0;
            if (background != null) {
                background.getPadding(AppCompatSpinner.this.mTempRect);
                if (ViewUtils.isLayoutRtl((View)AppCompatSpinner.this)) {
                    right = AppCompatSpinner.this.mTempRect.right;
                }
                else {
                    right = -AppCompatSpinner.this.mTempRect.left;
                }
            }
            else {
                final Rect mTempRect = AppCompatSpinner.this.mTempRect;
                mTempRect.right = 0;
                mTempRect.left = 0;
            }
            final int paddingLeft = AppCompatSpinner.this.getPaddingLeft();
            final int paddingRight = AppCompatSpinner.this.getPaddingRight();
            final int width = AppCompatSpinner.this.getWidth();
            final AppCompatSpinner this$0 = AppCompatSpinner.this;
            final int mDropDownWidth = this$0.mDropDownWidth;
            if (mDropDownWidth == -2) {
                final int compatMeasureContentWidth = this$0.compatMeasureContentWidth((SpinnerAdapter)this.mAdapter, this.getBackground());
                final int widthPixels = AppCompatSpinner.this.getContext().getResources().getDisplayMetrics().widthPixels;
                final Rect mTempRect2 = AppCompatSpinner.this.mTempRect;
                final int n = widthPixels - mTempRect2.left - mTempRect2.right;
                int a;
                if ((a = compatMeasureContentWidth) > n) {
                    a = n;
                }
                this.setContentWidth(Math.max(a, width - paddingLeft - paddingRight));
            }
            else if (mDropDownWidth == -1) {
                this.setContentWidth(width - paddingLeft - paddingRight);
            }
            else {
                this.setContentWidth(mDropDownWidth);
            }
            int horizontalOffset;
            if (ViewUtils.isLayoutRtl((View)AppCompatSpinner.this)) {
                horizontalOffset = right + (width - paddingRight - this.getWidth() - this.getHorizontalOriginalOffset());
            }
            else {
                horizontalOffset = right + (paddingLeft + this.getHorizontalOriginalOffset());
            }
            this.setHorizontalOffset(horizontalOffset);
        }
        
        @Override
        public CharSequence getHintText() {
            return this.mHintText;
        }
        
        public int getHorizontalOriginalOffset() {
            return this.mOriginalHorizontalOffset;
        }
        
        boolean isVisibleToUser(final View view) {
            return ViewCompat.isAttachedToWindow(view) && view.getGlobalVisibleRect(this.mVisibleRect);
        }
        
        @Override
        public void setAdapter(final ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }
        
        @Override
        public void setHorizontalOriginalOffset(final int mOriginalHorizontalOffset) {
            this.mOriginalHorizontalOffset = mOriginalHorizontalOffset;
        }
        
        @Override
        public void setPromptText(final CharSequence mHintText) {
            this.mHintText = mHintText;
        }
        
        @Override
        public void show(final int textDirection, final int textAlignment) {
            final boolean showing = this.isShowing();
            this.computeContentWidth();
            this.setInputMethodMode(2);
            super.show();
            final ListView listView = this.getListView();
            listView.setChoiceMode(1);
            if (Build$VERSION.SDK_INT >= 17) {
                listView.setTextDirection(textDirection);
                listView.setTextAlignment(textAlignment);
            }
            this.setSelection(AppCompatSpinner.this.getSelectedItemPosition());
            if (showing) {
                return;
            }
            final ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
            if (viewTreeObserver != null) {
                final ViewTreeObserver$OnGlobalLayoutListener viewTreeObserver$OnGlobalLayoutListener = (ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        final DropdownPopup this$1 = DropdownPopup.this;
                        if (!this$1.isVisibleToUser((View)this$1.this$0)) {
                            DropdownPopup.this.dismiss();
                        }
                        else {
                            DropdownPopup.this.computeContentWidth();
                            ListPopupWindow.this.show();
                        }
                    }
                };
                viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)viewTreeObserver$OnGlobalLayoutListener);
                this.setOnDismissListener((PopupWindow$OnDismissListener)new PopupWindow$OnDismissListener() {
                    public void onDismiss() {
                        final ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
                        if (viewTreeObserver != null) {
                            viewTreeObserver.removeGlobalOnLayoutListener(viewTreeObserver$OnGlobalLayoutListener);
                        }
                    }
                });
            }
        }
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean mShowDropdown;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel) {
            super(parcel);
            this.mShowDropdown = (parcel.readByte() != 0);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeByte((byte)(byte)(this.mShowDropdown ? 1 : 0));
        }
    }
    
    interface SpinnerPopup
    {
        void dismiss();
        
        Drawable getBackground();
        
        CharSequence getHintText();
        
        int getHorizontalOffset();
        
        int getVerticalOffset();
        
        boolean isShowing();
        
        void setAdapter(final ListAdapter p0);
        
        void setBackgroundDrawable(final Drawable p0);
        
        void setHorizontalOffset(final int p0);
        
        void setHorizontalOriginalOffset(final int p0);
        
        void setPromptText(final CharSequence p0);
        
        void setVerticalOffset(final int p0);
        
        void show(final int p0, final int p1);
    }
}
