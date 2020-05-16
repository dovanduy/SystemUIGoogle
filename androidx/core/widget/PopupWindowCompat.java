// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.widget;

import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.util.Log;
import android.os.Build$VERSION;
import android.widget.PopupWindow;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public final class PopupWindowCompat
{
    private static Field sOverlapAnchorField;
    private static boolean sOverlapAnchorFieldAttempted;
    private static Method sSetWindowLayoutTypeMethod;
    private static boolean sSetWindowLayoutTypeMethodAttempted;
    
    public static void setOverlapAnchor(final PopupWindow obj, final boolean b) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 23) {
            obj.setOverlapAnchor(b);
        }
        else if (sdk_INT >= 21) {
            if (!PopupWindowCompat.sOverlapAnchorFieldAttempted) {
                try {
                    (PopupWindowCompat.sOverlapAnchorField = PopupWindow.class.getDeclaredField("mOverlapAnchor")).setAccessible(true);
                }
                catch (NoSuchFieldException ex) {
                    Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", (Throwable)ex);
                }
                PopupWindowCompat.sOverlapAnchorFieldAttempted = true;
            }
            final Field sOverlapAnchorField = PopupWindowCompat.sOverlapAnchorField;
            if (sOverlapAnchorField != null) {
                try {
                    sOverlapAnchorField.set(obj, b);
                }
                catch (IllegalAccessException ex2) {
                    Log.i("PopupWindowCompatApi21", "Could not set overlap anchor field in PopupWindow", (Throwable)ex2);
                }
            }
        }
    }
    
    public static void setWindowLayoutType(final PopupWindow p0, final int p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: bipush          23
        //     5: if_icmplt       14
        //     8: aload_0        
        //     9: iload_1        
        //    10: invokevirtual   android/widget/PopupWindow.setWindowLayoutType:(I)V
        //    13: return         
        //    14: getstatic       androidx/core/widget/PopupWindowCompat.sSetWindowLayoutTypeMethodAttempted:Z
        //    17: ifne            51
        //    20: ldc             Landroid/widget/PopupWindow;.class
        //    22: ldc             "setWindowLayoutType"
        //    24: iconst_1       
        //    25: anewarray       Ljava/lang/Class;
        //    28: dup            
        //    29: iconst_0       
        //    30: getstatic       java/lang/Integer.TYPE:Ljava/lang/Class;
        //    33: aastore        
        //    34: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    37: astore_2       
        //    38: aload_2        
        //    39: putstatic       androidx/core/widget/PopupWindowCompat.sSetWindowLayoutTypeMethod:Ljava/lang/reflect/Method;
        //    42: aload_2        
        //    43: iconst_1       
        //    44: invokevirtual   java/lang/reflect/Method.setAccessible:(Z)V
        //    47: iconst_1       
        //    48: putstatic       androidx/core/widget/PopupWindowCompat.sSetWindowLayoutTypeMethodAttempted:Z
        //    51: getstatic       androidx/core/widget/PopupWindowCompat.sSetWindowLayoutTypeMethod:Ljava/lang/reflect/Method;
        //    54: astore_2       
        //    55: aload_2        
        //    56: ifnull          76
        //    59: aload_2        
        //    60: aload_0        
        //    61: iconst_1       
        //    62: anewarray       Ljava/lang/Object;
        //    65: dup            
        //    66: iconst_0       
        //    67: iload_1        
        //    68: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    71: aastore        
        //    72: invokevirtual   java/lang/reflect/Method.invoke:(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        //    75: pop            
        //    76: return         
        //    77: astore_2       
        //    78: goto            47
        //    81: astore_0       
        //    82: goto            76
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  20     47     77     81     Ljava/lang/Exception;
        //  59     76     81     85     Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0076:
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
    
    public static void showAsDropDown(final PopupWindow popupWindow, final View view, final int n, final int n2, final int n3) {
        if (Build$VERSION.SDK_INT >= 19) {
            popupWindow.showAsDropDown(view, n, n2, n3);
        }
        else {
            int n4 = n;
            if ((GravityCompat.getAbsoluteGravity(n3, ViewCompat.getLayoutDirection(view)) & 0x7) == 0x5) {
                n4 = n - (popupWindow.getWidth() - view.getWidth());
            }
            popupWindow.showAsDropDown(view, n4, n2);
        }
    }
}
