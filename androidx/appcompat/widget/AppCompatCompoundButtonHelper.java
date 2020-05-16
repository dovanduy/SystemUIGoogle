// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.util.AttributeSet;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;
import android.widget.CompoundButton;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;

class AppCompatCompoundButtonHelper
{
    private ColorStateList mButtonTintList;
    private PorterDuff$Mode mButtonTintMode;
    private boolean mHasButtonTint;
    private boolean mHasButtonTintMode;
    private boolean mSkipNextApply;
    private final CompoundButton mView;
    
    AppCompatCompoundButtonHelper(final CompoundButton mView) {
        this.mButtonTintList = null;
        this.mButtonTintMode = null;
        this.mHasButtonTint = false;
        this.mHasButtonTintMode = false;
        this.mView = mView;
    }
    
    void applyButtonTint() {
        final Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
        if (buttonDrawable != null && (this.mHasButtonTint || this.mHasButtonTintMode)) {
            final Drawable mutate = DrawableCompat.wrap(buttonDrawable).mutate();
            if (this.mHasButtonTint) {
                DrawableCompat.setTintList(mutate, this.mButtonTintList);
            }
            if (this.mHasButtonTintMode) {
                DrawableCompat.setTintMode(mutate, this.mButtonTintMode);
            }
            if (mutate.isStateful()) {
                mutate.setState(this.mView.getDrawableState());
            }
            this.mView.setButtonDrawable(mutate);
        }
    }
    
    int getCompoundPaddingLeft(final int n) {
        int n2 = n;
        if (Build$VERSION.SDK_INT < 17) {
            final Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
            n2 = n;
            if (buttonDrawable != null) {
                n2 = n + buttonDrawable.getIntrinsicWidth();
            }
        }
        return n2;
    }
    
    void loadFromAttributes(final AttributeSet p0, final int p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     4: invokevirtual   android/widget/CompoundButton.getContext:()Landroid/content/Context;
        //     7: aload_1        
        //     8: getstatic       androidx/appcompat/R$styleable.CompoundButton:[I
        //    11: iload_2        
        //    12: iconst_0       
        //    13: invokestatic    androidx/appcompat/widget/TintTypedArray.obtainStyledAttributes:(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroidx/appcompat/widget/TintTypedArray;
        //    16: astore_3       
        //    17: aload_0        
        //    18: getfield        androidx/appcompat/widget/AppCompatCompoundButtonHelper.mView:Landroid/widget/CompoundButton;
        //    21: astore          4
        //    23: aload           4
        //    25: aload           4
        //    27: invokevirtual   android/widget/CompoundButton.getContext:()Landroid/content/Context;
        //    30: getstatic       androidx/appcompat/R$styleable.CompoundButton:[I
        //    33: aload_1        
        //    34: aload_3        
        //    35: invokevirtual   androidx/appcompat/widget/TintTypedArray.getWrappedTypeArray:()Landroid/content/res/TypedArray;
        //    38: iload_2        
        //    39: iconst_0       
        //    40: invokestatic    androidx/core/view/ViewCompat.saveAttributeDataForStyleable:(Landroid/view/View;Landroid/content/Context;[ILandroid/util/AttributeSet;Landroid/content/res/TypedArray;II)V
        //    43: aload_3        
        //    44: getstatic       androidx/appcompat/R$styleable.CompoundButton_buttonCompat:I
        //    47: invokevirtual   androidx/appcompat/widget/TintTypedArray.hasValue:(I)Z
        //    50: ifeq            89
        //    53: aload_3        
        //    54: getstatic       androidx/appcompat/R$styleable.CompoundButton_buttonCompat:I
        //    57: iconst_0       
        //    58: invokevirtual   androidx/appcompat/widget/TintTypedArray.getResourceId:(II)I
        //    61: istore_2       
        //    62: iload_2        
        //    63: ifeq            89
        //    66: aload_0        
        //    67: getfield        androidx/appcompat/widget/AppCompatCompoundButtonHelper.mView:Landroid/widget/CompoundButton;
        //    70: aload_0        
        //    71: getfield        androidx/appcompat/widget/AppCompatCompoundButtonHelper.mView:Landroid/widget/CompoundButton;
        //    74: invokevirtual   android/widget/CompoundButton.getContext:()Landroid/content/Context;
        //    77: iload_2        
        //    78: invokestatic    androidx/appcompat/content/res/AppCompatResources.getDrawable:(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;
        //    81: invokevirtual   android/widget/CompoundButton.setButtonDrawable:(Landroid/graphics/drawable/Drawable;)V
        //    84: iconst_1       
        //    85: istore_2       
        //    86: goto            91
        //    89: iconst_0       
        //    90: istore_2       
        //    91: iload_2        
        //    92: ifne            136
        //    95: aload_3        
        //    96: getstatic       androidx/appcompat/R$styleable.CompoundButton_android_button:I
        //    99: invokevirtual   androidx/appcompat/widget/TintTypedArray.hasValue:(I)Z
        //   102: ifeq            136
        //   105: aload_3        
        //   106: getstatic       androidx/appcompat/R$styleable.CompoundButton_android_button:I
        //   109: iconst_0       
        //   110: invokevirtual   androidx/appcompat/widget/TintTypedArray.getResourceId:(II)I
        //   113: istore_2       
        //   114: iload_2        
        //   115: ifeq            136
        //   118: aload_0        
        //   119: getfield        androidx/appcompat/widget/AppCompatCompoundButtonHelper.mView:Landroid/widget/CompoundButton;
        //   122: aload_0        
        //   123: getfield        androidx/appcompat/widget/AppCompatCompoundButtonHelper.mView:Landroid/widget/CompoundButton;
        //   126: invokevirtual   android/widget/CompoundButton.getContext:()Landroid/content/Context;
        //   129: iload_2        
        //   130: invokestatic    androidx/appcompat/content/res/AppCompatResources.getDrawable:(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;
        //   133: invokevirtual   android/widget/CompoundButton.setButtonDrawable:(Landroid/graphics/drawable/Drawable;)V
        //   136: aload_3        
        //   137: getstatic       androidx/appcompat/R$styleable.CompoundButton_buttonTint:I
        //   140: invokevirtual   androidx/appcompat/widget/TintTypedArray.hasValue:(I)Z
        //   143: ifeq            160
        //   146: aload_0        
        //   147: getfield        androidx/appcompat/widget/AppCompatCompoundButtonHelper.mView:Landroid/widget/CompoundButton;
        //   150: aload_3        
        //   151: getstatic       androidx/appcompat/R$styleable.CompoundButton_buttonTint:I
        //   154: invokevirtual   androidx/appcompat/widget/TintTypedArray.getColorStateList:(I)Landroid/content/res/ColorStateList;
        //   157: invokestatic    androidx/core/widget/CompoundButtonCompat.setButtonTintList:(Landroid/widget/CompoundButton;Landroid/content/res/ColorStateList;)V
        //   160: aload_3        
        //   161: getstatic       androidx/appcompat/R$styleable.CompoundButton_buttonTintMode:I
        //   164: invokevirtual   androidx/appcompat/widget/TintTypedArray.hasValue:(I)Z
        //   167: ifeq            189
        //   170: aload_0        
        //   171: getfield        androidx/appcompat/widget/AppCompatCompoundButtonHelper.mView:Landroid/widget/CompoundButton;
        //   174: aload_3        
        //   175: getstatic       androidx/appcompat/R$styleable.CompoundButton_buttonTintMode:I
        //   178: iconst_m1      
        //   179: invokevirtual   androidx/appcompat/widget/TintTypedArray.getInt:(II)I
        //   182: aconst_null    
        //   183: invokestatic    androidx/appcompat/widget/DrawableUtils.parseTintMode:(ILandroid/graphics/PorterDuff$Mode;)Landroid/graphics/PorterDuff$Mode;
        //   186: invokestatic    androidx/core/widget/CompoundButtonCompat.setButtonTintMode:(Landroid/widget/CompoundButton;Landroid/graphics/PorterDuff$Mode;)V
        //   189: aload_3        
        //   190: invokevirtual   androidx/appcompat/widget/TintTypedArray.recycle:()V
        //   193: return         
        //   194: astore_1       
        //   195: aload_3        
        //   196: invokevirtual   androidx/appcompat/widget/TintTypedArray.recycle:()V
        //   199: aload_1        
        //   200: athrow         
        //   201: astore_1       
        //   202: goto            89
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                             
        //  -----  -----  -----  -----  -------------------------------------------------
        //  43     62     194    201    Any
        //  66     84     201    205    Landroid/content/res/Resources$NotFoundException;
        //  66     84     194    201    Any
        //  95     114    194    201    Any
        //  118    136    194    201    Any
        //  136    160    194    201    Any
        //  160    189    194    201    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0089:
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
    
    void onSetButtonDrawable() {
        if (this.mSkipNextApply) {
            this.mSkipNextApply = false;
            return;
        }
        this.mSkipNextApply = true;
        this.applyButtonTint();
    }
    
    void setSupportButtonTintList(final ColorStateList mButtonTintList) {
        this.mButtonTintList = mButtonTintList;
        this.mHasButtonTint = true;
        this.applyButtonTint();
    }
    
    void setSupportButtonTintMode(final PorterDuff$Mode mButtonTintMode) {
        this.mButtonTintMode = mButtonTintMode;
        this.mHasButtonTintMode = true;
        this.applyButtonTint();
    }
}
