// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.view.ViewGroup;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import android.annotation.TargetApi;
import java.util.Iterator;
import java.util.Map;
import android.view.View$OnAttachStateChangeListener;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.view.View$OnApplyWindowInsetsListener;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.content.Context;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import android.animation.ValueAnimator;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.annotation.SuppressLint;
import android.view.WindowManager;
import android.view.Display;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import java.util.ArrayList;
import java.util.List;
import android.view.View$AccessibilityDelegate;
import android.view.KeyEvent;
import android.view.WindowInsets;
import android.view.ViewParent;
import android.os.Build$VERSION;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.R$id;
import android.view.View;
import java.util.WeakHashMap;
import android.graphics.Rect;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Field;

public class ViewCompat
{
    private static boolean sAccessibilityDelegateCheckFailed;
    private static Field sAccessibilityDelegateField;
    private static Field sMinHeightField;
    private static boolean sMinHeightFieldFetched;
    private static Field sMinWidthField;
    private static boolean sMinWidthFieldFetched;
    private static final AtomicInteger sNextGeneratedId;
    private static ThreadLocal<Rect> sThreadLocalRect;
    private static WeakHashMap<View, String> sTransitionNameMap;
    private static WeakHashMap<View, ViewPropertyAnimatorCompat> sViewPropertyAnimatorMap;
    
    static {
        sNextGeneratedId = new AtomicInteger(1);
        ViewCompat.sViewPropertyAnimatorMap = null;
        ViewCompat.sAccessibilityDelegateCheckFailed = false;
        new AccessibilityPaneVisibilityManager();
    }
    
    private static AccessibilityViewProperty<Boolean> accessibilityHeadingProperty() {
        return (AccessibilityViewProperty<Boolean>)new AccessibilityViewProperty<Boolean>(R$id.tag_accessibility_heading, Boolean.class, 28) {
            Boolean frameworkGet(final View view) {
                return view.isAccessibilityHeading();
            }
        };
    }
    
    private static void addAccessibilityAction(final View view, final AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat) {
        if (Build$VERSION.SDK_INT >= 21) {
            getOrCreateAccessibilityDelegateCompat(view);
            removeActionWithId(accessibilityActionCompat.getId(), view);
            getActionList(view).add(accessibilityActionCompat);
            notifyViewAccessibilityStateChangedIfNeeded(view, 0);
        }
    }
    
    public static ViewPropertyAnimatorCompat animate(final View view) {
        if (ViewCompat.sViewPropertyAnimatorMap == null) {
            ViewCompat.sViewPropertyAnimatorMap = new WeakHashMap<View, ViewPropertyAnimatorCompat>();
        }
        ViewPropertyAnimatorCompat value;
        if ((value = ViewCompat.sViewPropertyAnimatorMap.get(view)) == null) {
            value = new ViewPropertyAnimatorCompat(view);
            ViewCompat.sViewPropertyAnimatorMap.put(view, value);
        }
        return value;
    }
    
    private static void compatOffsetLeftAndRight(final View view, final int n) {
        view.offsetLeftAndRight(n);
        if (view.getVisibility() == 0) {
            tickleInvalidationFlag(view);
            final ViewParent parent = view.getParent();
            if (parent instanceof View) {
                tickleInvalidationFlag((View)parent);
            }
        }
    }
    
    private static void compatOffsetTopAndBottom(final View view, final int n) {
        view.offsetTopAndBottom(n);
        if (view.getVisibility() == 0) {
            tickleInvalidationFlag(view);
            final ViewParent parent = view.getParent();
            if (parent instanceof View) {
                tickleInvalidationFlag((View)parent);
            }
        }
    }
    
    public static WindowInsetsCompat computeSystemWindowInsets(final View view, final WindowInsetsCompat windowInsetsCompat, final Rect rect) {
        if (Build$VERSION.SDK_INT >= 21) {
            return WindowInsetsCompat.toWindowInsetsCompat(view.computeSystemWindowInsets(windowInsetsCompat.toWindowInsets(), rect));
        }
        return windowInsetsCompat;
    }
    
    public static WindowInsetsCompat dispatchApplyWindowInsets(final View view, final WindowInsetsCompat windowInsetsCompat) {
        if (Build$VERSION.SDK_INT >= 21) {
            final WindowInsets windowInsets = windowInsetsCompat.toWindowInsets();
            final WindowInsets dispatchApplyWindowInsets = view.dispatchApplyWindowInsets(windowInsets);
            WindowInsets windowInsets2 = windowInsets;
            if (!dispatchApplyWindowInsets.equals((Object)windowInsets)) {
                windowInsets2 = new WindowInsets(dispatchApplyWindowInsets);
            }
            return WindowInsetsCompat.toWindowInsetsCompat(windowInsets2);
        }
        return windowInsetsCompat;
    }
    
    static boolean dispatchUnhandledKeyEventBeforeCallback(final View view, final KeyEvent keyEvent) {
        return Build$VERSION.SDK_INT < 28 && UnhandledKeyEventManager.at(view).dispatch(view, keyEvent);
    }
    
    static boolean dispatchUnhandledKeyEventBeforeHierarchy(final View view, final KeyEvent keyEvent) {
        return Build$VERSION.SDK_INT < 28 && UnhandledKeyEventManager.at(view).preDispatch(keyEvent);
    }
    
    public static int generateViewId() {
        if (Build$VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        }
        int value;
        int newValue;
        do {
            value = ViewCompat.sNextGeneratedId.get();
            if ((newValue = value + 1) > 16777215) {
                newValue = 1;
            }
        } while (!ViewCompat.sNextGeneratedId.compareAndSet(value, newValue));
        return value;
    }
    
    public static AccessibilityDelegateCompat getAccessibilityDelegate(final View view) {
        final View$AccessibilityDelegate accessibilityDelegateInternal = getAccessibilityDelegateInternal(view);
        if (accessibilityDelegateInternal == null) {
            return null;
        }
        if (accessibilityDelegateInternal instanceof AccessibilityDelegateCompat.AccessibilityDelegateAdapter) {
            return ((AccessibilityDelegateCompat.AccessibilityDelegateAdapter)accessibilityDelegateInternal).mCompat;
        }
        return new AccessibilityDelegateCompat(accessibilityDelegateInternal);
    }
    
    private static View$AccessibilityDelegate getAccessibilityDelegateInternal(final View view) {
        if (Build$VERSION.SDK_INT >= 29) {
            return view.getAccessibilityDelegate();
        }
        return getAccessibilityDelegateThroughReflection(view);
    }
    
    private static View$AccessibilityDelegate getAccessibilityDelegateThroughReflection(final View obj) {
        if (ViewCompat.sAccessibilityDelegateCheckFailed) {
            return null;
        }
        if (ViewCompat.sAccessibilityDelegateField == null) {
            try {
                (ViewCompat.sAccessibilityDelegateField = View.class.getDeclaredField("mAccessibilityDelegate")).setAccessible(true);
            }
            finally {
                ViewCompat.sAccessibilityDelegateCheckFailed = true;
                return null;
            }
        }
        try {
            final Object value = ViewCompat.sAccessibilityDelegateField.get(obj);
            if (value instanceof View$AccessibilityDelegate) {
                return (View$AccessibilityDelegate)value;
            }
            return null;
        }
        finally {
            ViewCompat.sAccessibilityDelegateCheckFailed = true;
            return null;
        }
    }
    
    public static int getAccessibilityLiveRegion(final View view) {
        if (Build$VERSION.SDK_INT >= 19) {
            return view.getAccessibilityLiveRegion();
        }
        return 0;
    }
    
    public static CharSequence getAccessibilityPaneTitle(final View view) {
        return paneTitleProperty().get(view);
    }
    
    private static List<AccessibilityNodeInfoCompat.AccessibilityActionCompat> getActionList(final View view) {
        ArrayList<AccessibilityNodeInfoCompat.AccessibilityActionCompat> list;
        if ((list = (ArrayList<AccessibilityNodeInfoCompat.AccessibilityActionCompat>)view.getTag(R$id.tag_accessibility_actions)) == null) {
            list = new ArrayList<AccessibilityNodeInfoCompat.AccessibilityActionCompat>();
            view.setTag(R$id.tag_accessibility_actions, (Object)list);
        }
        return list;
    }
    
    public static ColorStateList getBackgroundTintList(final View view) {
        if (Build$VERSION.SDK_INT >= 21) {
            return view.getBackgroundTintList();
        }
        ColorStateList supportBackgroundTintList;
        if (view instanceof TintableBackgroundView) {
            supportBackgroundTintList = ((TintableBackgroundView)view).getSupportBackgroundTintList();
        }
        else {
            supportBackgroundTintList = null;
        }
        return supportBackgroundTintList;
    }
    
    public static PorterDuff$Mode getBackgroundTintMode(final View view) {
        if (Build$VERSION.SDK_INT >= 21) {
            return view.getBackgroundTintMode();
        }
        PorterDuff$Mode supportBackgroundTintMode;
        if (view instanceof TintableBackgroundView) {
            supportBackgroundTintMode = ((TintableBackgroundView)view).getSupportBackgroundTintMode();
        }
        else {
            supportBackgroundTintMode = null;
        }
        return supportBackgroundTintMode;
    }
    
    public static Display getDisplay(final View view) {
        if (Build$VERSION.SDK_INT >= 17) {
            return view.getDisplay();
        }
        if (isAttachedToWindow(view)) {
            return ((WindowManager)view.getContext().getSystemService("window")).getDefaultDisplay();
        }
        return null;
    }
    
    public static float getElevation(final View view) {
        if (Build$VERSION.SDK_INT >= 21) {
            return view.getElevation();
        }
        return 0.0f;
    }
    
    private static Rect getEmptyTempRect() {
        if (ViewCompat.sThreadLocalRect == null) {
            ViewCompat.sThreadLocalRect = new ThreadLocal<Rect>();
        }
        Rect value;
        if ((value = ViewCompat.sThreadLocalRect.get()) == null) {
            value = new Rect();
            ViewCompat.sThreadLocalRect.set(value);
        }
        value.setEmpty();
        return value;
    }
    
    public static boolean getFitsSystemWindows(final View view) {
        return Build$VERSION.SDK_INT >= 16 && view.getFitsSystemWindows();
    }
    
    public static int getImportantForAccessibility(final View view) {
        if (Build$VERSION.SDK_INT >= 16) {
            return view.getImportantForAccessibility();
        }
        return 0;
    }
    
    @SuppressLint({ "InlinedApi" })
    public static int getImportantForAutofill(final View view) {
        if (Build$VERSION.SDK_INT >= 26) {
            return view.getImportantForAutofill();
        }
        return 0;
    }
    
    public static int getLayoutDirection(final View view) {
        if (Build$VERSION.SDK_INT >= 17) {
            return view.getLayoutDirection();
        }
        return 0;
    }
    
    public static int getMinimumHeight(final View p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: bipush          16
        //     5: if_icmplt       13
        //     8: aload_0        
        //     9: invokevirtual   android/view/View.getMinimumHeight:()I
        //    12: ireturn        
        //    13: getstatic       androidx/core/view/ViewCompat.sMinHeightFieldFetched:Z
        //    16: ifne            41
        //    19: ldc             Landroid/view/View;.class
        //    21: ldc_w           "mMinHeight"
        //    24: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
        //    27: astore_1       
        //    28: aload_1        
        //    29: putstatic       androidx/core/view/ViewCompat.sMinHeightField:Ljava/lang/reflect/Field;
        //    32: aload_1        
        //    33: iconst_1       
        //    34: invokevirtual   java/lang/reflect/Field.setAccessible:(Z)V
        //    37: iconst_1       
        //    38: putstatic       androidx/core/view/ViewCompat.sMinHeightFieldFetched:Z
        //    41: getstatic       androidx/core/view/ViewCompat.sMinHeightField:Ljava/lang/reflect/Field;
        //    44: astore_1       
        //    45: aload_1        
        //    46: ifnull          63
        //    49: aload_1        
        //    50: aload_0        
        //    51: invokevirtual   java/lang/reflect/Field.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    54: checkcast       Ljava/lang/Integer;
        //    57: invokevirtual   java/lang/Integer.intValue:()I
        //    60: istore_2       
        //    61: iload_2        
        //    62: ireturn        
        //    63: iconst_0       
        //    64: ireturn        
        //    65: astore_1       
        //    66: goto            37
        //    69: astore_0       
        //    70: goto            63
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  19     37     65     69     Ljava/lang/NoSuchFieldException;
        //  49     61     69     73     Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0063:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
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
    
    public static int getMinimumWidth(final View p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: bipush          16
        //     5: if_icmplt       13
        //     8: aload_0        
        //     9: invokevirtual   android/view/View.getMinimumWidth:()I
        //    12: ireturn        
        //    13: getstatic       androidx/core/view/ViewCompat.sMinWidthFieldFetched:Z
        //    16: ifne            41
        //    19: ldc             Landroid/view/View;.class
        //    21: ldc_w           "mMinWidth"
        //    24: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
        //    27: astore_1       
        //    28: aload_1        
        //    29: putstatic       androidx/core/view/ViewCompat.sMinWidthField:Ljava/lang/reflect/Field;
        //    32: aload_1        
        //    33: iconst_1       
        //    34: invokevirtual   java/lang/reflect/Field.setAccessible:(Z)V
        //    37: iconst_1       
        //    38: putstatic       androidx/core/view/ViewCompat.sMinWidthFieldFetched:Z
        //    41: getstatic       androidx/core/view/ViewCompat.sMinWidthField:Ljava/lang/reflect/Field;
        //    44: astore_1       
        //    45: aload_1        
        //    46: ifnull          63
        //    49: aload_1        
        //    50: aload_0        
        //    51: invokevirtual   java/lang/reflect/Field.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    54: checkcast       Ljava/lang/Integer;
        //    57: invokevirtual   java/lang/Integer.intValue:()I
        //    60: istore_2       
        //    61: iload_2        
        //    62: ireturn        
        //    63: iconst_0       
        //    64: ireturn        
        //    65: astore_1       
        //    66: goto            37
        //    69: astore_0       
        //    70: goto            63
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  19     37     65     69     Ljava/lang/NoSuchFieldException;
        //  49     61     69     73     Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0063:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
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
    
    static AccessibilityDelegateCompat getOrCreateAccessibilityDelegateCompat(final View view) {
        AccessibilityDelegateCompat accessibilityDelegate;
        if ((accessibilityDelegate = getAccessibilityDelegate(view)) == null) {
            accessibilityDelegate = new AccessibilityDelegateCompat();
        }
        setAccessibilityDelegate(view, accessibilityDelegate);
        return accessibilityDelegate;
    }
    
    public static ViewParent getParentForAccessibility(final View view) {
        if (Build$VERSION.SDK_INT >= 16) {
            return view.getParentForAccessibility();
        }
        return view.getParent();
    }
    
    public static WindowInsetsCompat getRootWindowInsets(final View view) {
        if (Build$VERSION.SDK_INT >= 23) {
            return WindowInsetsCompat.toWindowInsetsCompat(Api23Impl.getRootWindowInsets(view));
        }
        return null;
    }
    
    public static String getTransitionName(final View key) {
        if (Build$VERSION.SDK_INT >= 21) {
            return key.getTransitionName();
        }
        final WeakHashMap<View, String> sTransitionNameMap = ViewCompat.sTransitionNameMap;
        if (sTransitionNameMap == null) {
            return null;
        }
        return sTransitionNameMap.get(key);
    }
    
    @Deprecated
    public static float getTranslationY(final View view) {
        return view.getTranslationY();
    }
    
    public static float getTranslationZ(final View view) {
        if (Build$VERSION.SDK_INT >= 21) {
            return view.getTranslationZ();
        }
        return 0.0f;
    }
    
    public static int getWindowSystemUiVisibility(final View view) {
        if (Build$VERSION.SDK_INT >= 16) {
            return view.getWindowSystemUiVisibility();
        }
        return 0;
    }
    
    public static float getZ(final View view) {
        if (Build$VERSION.SDK_INT >= 21) {
            return view.getZ();
        }
        return 0.0f;
    }
    
    public static boolean hasOnClickListeners(final View view) {
        return Build$VERSION.SDK_INT >= 15 && view.hasOnClickListeners();
    }
    
    public static boolean hasTransientState(final View view) {
        return Build$VERSION.SDK_INT >= 16 && view.hasTransientState();
    }
    
    public static boolean isAccessibilityHeading(final View view) {
        final Boolean b = accessibilityHeadingProperty().get(view);
        return b != null && b;
    }
    
    public static boolean isAttachedToWindow(final View view) {
        if (Build$VERSION.SDK_INT >= 19) {
            return view.isAttachedToWindow();
        }
        return view.getWindowToken() != null;
    }
    
    public static boolean isLaidOut(final View view) {
        if (Build$VERSION.SDK_INT >= 19) {
            return view.isLaidOut();
        }
        return view.getWidth() > 0 && view.getHeight() > 0;
    }
    
    public static boolean isNestedScrollingEnabled(final View view) {
        if (Build$VERSION.SDK_INT >= 21) {
            return view.isNestedScrollingEnabled();
        }
        return view instanceof NestedScrollingChild && ((NestedScrollingChild)view).isNestedScrollingEnabled();
    }
    
    public static boolean isScreenReaderFocusable(final View view) {
        final Boolean b = screenReaderFocusableProperty().get(view);
        return b != null && b;
    }
    
    static void notifyViewAccessibilityStateChangedIfNeeded(final View view, final int contentChangeTypes) {
        if (!((AccessibilityManager)view.getContext().getSystemService("accessibility")).isEnabled()) {
            return;
        }
        final boolean b = getAccessibilityPaneTitle(view) != null;
        if (getAccessibilityLiveRegion(view) == 0 && (!b || view.getVisibility() != 0)) {
            if (view.getParent() != null) {
                try {
                    view.getParent().notifySubtreeAccessibilityStateChanged(view, view, contentChangeTypes);
                }
                catch (AbstractMethodError abstractMethodError) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(view.getParent().getClass().getSimpleName());
                    sb.append(" does not fully implement ViewParent");
                    Log.e("ViewCompat", sb.toString(), (Throwable)abstractMethodError);
                }
            }
        }
        else {
            final AccessibilityEvent obtain = AccessibilityEvent.obtain();
            int eventType;
            if (b) {
                eventType = 32;
            }
            else {
                eventType = 2048;
            }
            obtain.setEventType(eventType);
            obtain.setContentChangeTypes(contentChangeTypes);
            view.sendAccessibilityEventUnchecked(obtain);
        }
    }
    
    public static void offsetLeftAndRight(final View view, final int n) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 23) {
            view.offsetLeftAndRight(n);
        }
        else if (sdk_INT >= 21) {
            final Rect emptyTempRect = getEmptyTempRect();
            boolean b = false;
            final ViewParent parent = view.getParent();
            if (parent instanceof View) {
                final View view2 = (View)parent;
                emptyTempRect.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                b = (emptyTempRect.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()) ^ true);
            }
            compatOffsetLeftAndRight(view, n);
            if (b && emptyTempRect.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                ((View)parent).invalidate(emptyTempRect);
            }
        }
        else {
            compatOffsetLeftAndRight(view, n);
        }
    }
    
    public static void offsetTopAndBottom(final View view, final int n) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 23) {
            view.offsetTopAndBottom(n);
        }
        else if (sdk_INT >= 21) {
            final Rect emptyTempRect = getEmptyTempRect();
            boolean b = false;
            final ViewParent parent = view.getParent();
            if (parent instanceof View) {
                final View view2 = (View)parent;
                emptyTempRect.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                b = (emptyTempRect.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()) ^ true);
            }
            compatOffsetTopAndBottom(view, n);
            if (b && emptyTempRect.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                ((View)parent).invalidate(emptyTempRect);
            }
        }
        else {
            compatOffsetTopAndBottom(view, n);
        }
    }
    
    public static WindowInsetsCompat onApplyWindowInsets(final View view, final WindowInsetsCompat windowInsetsCompat) {
        if (Build$VERSION.SDK_INT >= 21) {
            final WindowInsets windowInsets = windowInsetsCompat.toWindowInsets();
            final WindowInsets onApplyWindowInsets = view.onApplyWindowInsets(windowInsets);
            WindowInsets windowInsets2 = windowInsets;
            if (!onApplyWindowInsets.equals((Object)windowInsets)) {
                windowInsets2 = new WindowInsets(onApplyWindowInsets);
            }
            return WindowInsetsCompat.toWindowInsetsCompat(windowInsets2);
        }
        return windowInsetsCompat;
    }
    
    private static AccessibilityViewProperty<CharSequence> paneTitleProperty() {
        return (AccessibilityViewProperty<CharSequence>)new AccessibilityViewProperty<CharSequence>(R$id.tag_accessibility_pane_title, CharSequence.class, 8, 28) {
            CharSequence frameworkGet(final View view) {
                return view.getAccessibilityPaneTitle();
            }
        };
    }
    
    public static void postInvalidateOnAnimation(final View view) {
        if (Build$VERSION.SDK_INT >= 16) {
            view.postInvalidateOnAnimation();
        }
        else {
            view.postInvalidate();
        }
    }
    
    public static void postInvalidateOnAnimation(final View view, final int n, final int n2, final int n3, final int n4) {
        if (Build$VERSION.SDK_INT >= 16) {
            view.postInvalidateOnAnimation(n, n2, n3, n4);
        }
        else {
            view.postInvalidate(n, n2, n3, n4);
        }
    }
    
    public static void postOnAnimation(final View view, final Runnable runnable) {
        if (Build$VERSION.SDK_INT >= 16) {
            view.postOnAnimation(runnable);
        }
        else {
            view.postDelayed(runnable, ValueAnimator.getFrameDelay());
        }
    }
    
    public static void postOnAnimationDelayed(final View view, final Runnable runnable, final long n) {
        if (Build$VERSION.SDK_INT >= 16) {
            view.postOnAnimationDelayed(runnable, n);
        }
        else {
            view.postDelayed(runnable, ValueAnimator.getFrameDelay() + n);
        }
    }
    
    public static void removeAccessibilityAction(final View view, final int n) {
        if (Build$VERSION.SDK_INT >= 21) {
            removeActionWithId(n, view);
            notifyViewAccessibilityStateChangedIfNeeded(view, 0);
        }
    }
    
    private static void removeActionWithId(final int n, final View view) {
        final List<AccessibilityNodeInfoCompat.AccessibilityActionCompat> actionList = getActionList(view);
        for (int i = 0; i < actionList.size(); ++i) {
            if (actionList.get(i).getId() == n) {
                actionList.remove(i);
                break;
            }
        }
    }
    
    public static void replaceAccessibilityAction(final View view, final AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat, final CharSequence charSequence, final AccessibilityViewCommand accessibilityViewCommand) {
        if (accessibilityViewCommand == null && charSequence == null) {
            removeAccessibilityAction(view, accessibilityActionCompat.getId());
        }
        else {
            addAccessibilityAction(view, accessibilityActionCompat.createReplacementAction(charSequence, accessibilityViewCommand));
        }
    }
    
    public static void requestApplyInsets(final View view) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 20) {
            view.requestApplyInsets();
        }
        else if (sdk_INT >= 16) {
            view.requestFitSystemWindows();
        }
    }
    
    public static void saveAttributeDataForStyleable(final View view, @SuppressLint({ "ContextFirst" }) final Context context, final int[] array, final AttributeSet set, final TypedArray typedArray, final int n, final int n2) {
        if (Build$VERSION.SDK_INT >= 29) {
            ViewCompatApi29.saveAttributeDataForStyleable(view, context, array, set, typedArray, n, n2);
        }
    }
    
    private static AccessibilityViewProperty<Boolean> screenReaderFocusableProperty() {
        return (AccessibilityViewProperty<Boolean>)new AccessibilityViewProperty<Boolean>(R$id.tag_screen_reader_focusable, Boolean.class, 28) {
            Boolean frameworkGet(final View view) {
                return view.isScreenReaderFocusable();
            }
        };
    }
    
    public static void setAccessibilityDelegate(final View view, final AccessibilityDelegateCompat accessibilityDelegateCompat) {
        AccessibilityDelegateCompat accessibilityDelegateCompat2 = accessibilityDelegateCompat;
        if (accessibilityDelegateCompat == null) {
            accessibilityDelegateCompat2 = accessibilityDelegateCompat;
            if (getAccessibilityDelegateInternal(view) instanceof AccessibilityDelegateCompat.AccessibilityDelegateAdapter) {
                accessibilityDelegateCompat2 = new AccessibilityDelegateCompat();
            }
        }
        View$AccessibilityDelegate bridge;
        if (accessibilityDelegateCompat2 == null) {
            bridge = null;
        }
        else {
            bridge = accessibilityDelegateCompat2.getBridge();
        }
        view.setAccessibilityDelegate(bridge);
    }
    
    public static void setBackground(final View view, final Drawable drawable) {
        if (Build$VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        }
        else {
            view.setBackgroundDrawable(drawable);
        }
    }
    
    public static void setBackgroundTintList(final View view, final ColorStateList list) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 21) {
            view.setBackgroundTintList(list);
            if (sdk_INT == 21) {
                final Drawable background = view.getBackground();
                final boolean b = view.getBackgroundTintList() != null || view.getBackgroundTintMode() != null;
                if (background != null && b) {
                    if (background.isStateful()) {
                        background.setState(view.getDrawableState());
                    }
                    view.setBackground(background);
                }
            }
        }
        else if (view instanceof TintableBackgroundView) {
            ((TintableBackgroundView)view).setSupportBackgroundTintList(list);
        }
    }
    
    public static void setBackgroundTintMode(final View view, final PorterDuff$Mode porterDuff$Mode) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 21) {
            view.setBackgroundTintMode(porterDuff$Mode);
            if (sdk_INT == 21) {
                final Drawable background = view.getBackground();
                final boolean b = view.getBackgroundTintList() != null || view.getBackgroundTintMode() != null;
                if (background != null && b) {
                    if (background.isStateful()) {
                        background.setState(view.getDrawableState());
                    }
                    view.setBackground(background);
                }
            }
        }
        else if (view instanceof TintableBackgroundView) {
            ((TintableBackgroundView)view).setSupportBackgroundTintMode(porterDuff$Mode);
        }
    }
    
    public static void setElevation(final View view, final float elevation) {
        if (Build$VERSION.SDK_INT >= 21) {
            view.setElevation(elevation);
        }
    }
    
    public static void setImportantForAccessibility(final View view, final int importantForAccessibility) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 19) {
            view.setImportantForAccessibility(importantForAccessibility);
        }
        else if (sdk_INT >= 16) {
            int importantForAccessibility2;
            if ((importantForAccessibility2 = importantForAccessibility) == 4) {
                importantForAccessibility2 = 2;
            }
            view.setImportantForAccessibility(importantForAccessibility2);
        }
    }
    
    public static void setImportantForAutofill(final View view, final int importantForAutofill) {
        if (Build$VERSION.SDK_INT >= 26) {
            view.setImportantForAutofill(importantForAutofill);
        }
    }
    
    public static void setLayerPaint(final View view, final Paint layerPaint) {
        if (Build$VERSION.SDK_INT >= 17) {
            view.setLayerPaint(layerPaint);
        }
        else {
            view.setLayerType(view.getLayerType(), layerPaint);
            view.invalidate();
        }
    }
    
    public static void setOnApplyWindowInsetsListener(final View view, final OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
        if (Build$VERSION.SDK_INT >= 21) {
            if (onApplyWindowInsetsListener == null) {
                view.setOnApplyWindowInsetsListener((View$OnApplyWindowInsetsListener)null);
                return;
            }
            view.setOnApplyWindowInsetsListener((View$OnApplyWindowInsetsListener)new View$OnApplyWindowInsetsListener() {
                public WindowInsets onApplyWindowInsets(final View view, final WindowInsets windowInsets) {
                    return onApplyWindowInsetsListener.onApplyWindowInsets(view, WindowInsetsCompat.toWindowInsetsCompat(windowInsets)).toWindowInsets();
                }
            });
        }
    }
    
    public static void setScrollIndicators(final View view, final int n, final int n2) {
        if (Build$VERSION.SDK_INT >= 23) {
            view.setScrollIndicators(n, n2);
        }
    }
    
    public static void setTransitionName(final View key, final String s) {
        if (Build$VERSION.SDK_INT >= 21) {
            key.setTransitionName(s);
        }
        else {
            if (ViewCompat.sTransitionNameMap == null) {
                ViewCompat.sTransitionNameMap = new WeakHashMap<View, String>();
            }
            ViewCompat.sTransitionNameMap.put(key, s);
        }
    }
    
    public static void setTranslationZ(final View view, final float translationZ) {
        if (Build$VERSION.SDK_INT >= 21) {
            view.setTranslationZ(translationZ);
        }
    }
    
    public static void setZ(final View view, final float z) {
        if (Build$VERSION.SDK_INT >= 21) {
            view.setZ(z);
        }
    }
    
    public static void stopNestedScroll(final View view) {
        if (Build$VERSION.SDK_INT >= 21) {
            view.stopNestedScroll();
        }
        else if (view instanceof NestedScrollingChild) {
            ((NestedScrollingChild)view).stopNestedScroll();
        }
    }
    
    private static void tickleInvalidationFlag(final View view) {
        final float translationY = view.getTranslationY();
        view.setTranslationY(1.0f + translationY);
        view.setTranslationY(translationY);
    }
    
    static class AccessibilityPaneVisibilityManager implements ViewTreeObserver$OnGlobalLayoutListener, View$OnAttachStateChangeListener
    {
        private WeakHashMap<View, Boolean> mPanesToVisible;
        
        AccessibilityPaneVisibilityManager() {
            this.mPanesToVisible = new WeakHashMap<View, Boolean>();
        }
        
        private void checkPaneVisibility(final View key, final boolean b) {
            final boolean b2 = key.getVisibility() == 0;
            if (b != b2) {
                if (b2) {
                    ViewCompat.notifyViewAccessibilityStateChangedIfNeeded(key, 16);
                }
                this.mPanesToVisible.put(key, b2);
            }
        }
        
        private void registerForLayoutCallback(final View view) {
            view.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
        }
        
        public void onGlobalLayout() {
            for (final Map.Entry<View, Boolean> entry : this.mPanesToVisible.entrySet()) {
                this.checkPaneVisibility(entry.getKey(), entry.getValue());
            }
        }
        
        public void onViewAttachedToWindow(final View view) {
            this.registerForLayoutCallback(view);
        }
        
        public void onViewDetachedFromWindow(final View view) {
        }
    }
    
    abstract static class AccessibilityViewProperty<T>
    {
        private final int mFrameworkMinimumSdk;
        private final int mTagKey;
        private final Class<T> mType;
        
        AccessibilityViewProperty(final int n, final Class<T> clazz, final int n2) {
            this(n, clazz, 0, n2);
        }
        
        AccessibilityViewProperty(final int mTagKey, final Class<T> mType, final int n, final int mFrameworkMinimumSdk) {
            this.mTagKey = mTagKey;
            this.mType = mType;
            this.mFrameworkMinimumSdk = mFrameworkMinimumSdk;
        }
        
        private boolean extrasAvailable() {
            return Build$VERSION.SDK_INT >= 19;
        }
        
        private boolean frameworkAvailable() {
            return Build$VERSION.SDK_INT >= this.mFrameworkMinimumSdk;
        }
        
        abstract T frameworkGet(final View p0);
        
        T get(final View view) {
            if (this.frameworkAvailable()) {
                return this.frameworkGet(view);
            }
            if (this.extrasAvailable()) {
                final Object tag = view.getTag(this.mTagKey);
                if (this.mType.isInstance(tag)) {
                    return (T)tag;
                }
            }
            return null;
        }
    }
    
    @TargetApi(23)
    static class Api23Impl
    {
        public static WindowInsets getRootWindowInsets(final View view) {
            return view.getRootWindowInsets();
        }
    }
    
    public interface OnUnhandledKeyEventListenerCompat
    {
        boolean onUnhandledKeyEvent(final View p0, final KeyEvent p1);
    }
    
    static class UnhandledKeyEventManager
    {
        private static final ArrayList<WeakReference<View>> sViewsWithListeners;
        private SparseArray<WeakReference<View>> mCapturedKeys;
        private WeakReference<KeyEvent> mLastDispatchedPreViewKeyEvent;
        private WeakHashMap<View, Boolean> mViewsContainingListeners;
        
        static {
            sViewsWithListeners = new ArrayList<WeakReference<View>>();
        }
        
        UnhandledKeyEventManager() {
            this.mViewsContainingListeners = null;
            this.mCapturedKeys = null;
            this.mLastDispatchedPreViewKeyEvent = null;
        }
        
        static UnhandledKeyEventManager at(final View view) {
            UnhandledKeyEventManager unhandledKeyEventManager;
            if ((unhandledKeyEventManager = (UnhandledKeyEventManager)view.getTag(R$id.tag_unhandled_key_event_manager)) == null) {
                unhandledKeyEventManager = new UnhandledKeyEventManager();
                view.setTag(R$id.tag_unhandled_key_event_manager, (Object)unhandledKeyEventManager);
            }
            return unhandledKeyEventManager;
        }
        
        private View dispatchInOrder(final View key, final KeyEvent keyEvent) {
            final WeakHashMap<View, Boolean> mViewsContainingListeners = this.mViewsContainingListeners;
            if (mViewsContainingListeners != null) {
                if (mViewsContainingListeners.containsKey(key)) {
                    if (key instanceof ViewGroup) {
                        final ViewGroup viewGroup = (ViewGroup)key;
                        for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                            final View dispatchInOrder = this.dispatchInOrder(viewGroup.getChildAt(i), keyEvent);
                            if (dispatchInOrder != null) {
                                return dispatchInOrder;
                            }
                        }
                    }
                    if (this.onUnhandledKeyEvent(key, keyEvent)) {
                        return key;
                    }
                }
            }
            return null;
        }
        
        private SparseArray<WeakReference<View>> getCapturedKeys() {
            if (this.mCapturedKeys == null) {
                this.mCapturedKeys = (SparseArray<WeakReference<View>>)new SparseArray();
            }
            return this.mCapturedKeys;
        }
        
        private boolean onUnhandledKeyEvent(final View view, final KeyEvent keyEvent) {
            final ArrayList list = (ArrayList)view.getTag(R$id.tag_unhandled_key_listeners);
            if (list != null) {
                for (int i = list.size() - 1; i >= 0; --i) {
                    if (list.get(i).onUnhandledKeyEvent(view, keyEvent)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        private void recalcViewsWithUnhandled() {
            final Boolean true = Boolean.TRUE;
            final WeakHashMap<View, Boolean> mViewsContainingListeners = this.mViewsContainingListeners;
            if (mViewsContainingListeners != null) {
                mViewsContainingListeners.clear();
            }
            if (UnhandledKeyEventManager.sViewsWithListeners.isEmpty()) {
                return;
            }
            synchronized (UnhandledKeyEventManager.sViewsWithListeners) {
                if (this.mViewsContainingListeners == null) {
                    this.mViewsContainingListeners = new WeakHashMap<View, Boolean>();
                }
                for (int i = UnhandledKeyEventManager.sViewsWithListeners.size() - 1; i >= 0; --i) {
                    final View key = UnhandledKeyEventManager.sViewsWithListeners.get(i).get();
                    if (key == null) {
                        UnhandledKeyEventManager.sViewsWithListeners.remove(i);
                    }
                    else {
                        this.mViewsContainingListeners.put(key, true);
                        for (ViewParent viewParent = key.getParent(); viewParent instanceof View; viewParent = viewParent.getParent()) {
                            this.mViewsContainingListeners.put((View)viewParent, true);
                        }
                    }
                }
            }
        }
        
        boolean dispatch(View dispatchInOrder, final KeyEvent keyEvent) {
            if (keyEvent.getAction() == 0) {
                this.recalcViewsWithUnhandled();
            }
            dispatchInOrder = this.dispatchInOrder(dispatchInOrder, keyEvent);
            if (keyEvent.getAction() == 0) {
                final int keyCode = keyEvent.getKeyCode();
                if (dispatchInOrder != null && !KeyEvent.isModifierKey(keyCode)) {
                    this.getCapturedKeys().put(keyCode, (Object)new WeakReference(dispatchInOrder));
                }
            }
            return dispatchInOrder != null;
        }
        
        boolean preDispatch(final KeyEvent referent) {
            final WeakReference<KeyEvent> mLastDispatchedPreViewKeyEvent = this.mLastDispatchedPreViewKeyEvent;
            if (mLastDispatchedPreViewKeyEvent != null && mLastDispatchedPreViewKeyEvent.get() == referent) {
                return false;
            }
            this.mLastDispatchedPreViewKeyEvent = new WeakReference<KeyEvent>(referent);
            final WeakReference<View> weakReference = null;
            final SparseArray<WeakReference<View>> capturedKeys = this.getCapturedKeys();
            WeakReference<View> weakReference2 = weakReference;
            if (referent.getAction() == 1) {
                final int indexOfKey = capturedKeys.indexOfKey(referent.getKeyCode());
                weakReference2 = weakReference;
                if (indexOfKey >= 0) {
                    weakReference2 = (WeakReference<View>)capturedKeys.valueAt(indexOfKey);
                    capturedKeys.removeAt(indexOfKey);
                }
            }
            WeakReference<View> weakReference3;
            if ((weakReference3 = weakReference2) == null) {
                weakReference3 = (WeakReference<View>)capturedKeys.get(referent.getKeyCode());
            }
            if (weakReference3 != null) {
                final View view = weakReference3.get();
                if (view != null && ViewCompat.isAttachedToWindow(view)) {
                    this.onUnhandledKeyEvent(view, referent);
                }
                return true;
            }
            return false;
        }
    }
    
    private static class ViewCompatApi29
    {
        public static void saveAttributeDataForStyleable(final View view, final Context context, final int[] array, final AttributeSet set, final TypedArray typedArray, final int n, final int n2) {
            view.saveAttributeDataForStyleable(context, array, set, typedArray, n, n2);
        }
    }
}
