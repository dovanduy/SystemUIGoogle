// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.content.Context;
import android.media.AudioAttributes$Builder;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.media.AudioAttributes;

public class HapticClick implements FeedbackEffect
{
    private static final AudioAttributes SONIFICATION_AUDIO_ATTRIBUTES;
    private int mLastGestureStage;
    private final VibrationEffect mProgressVibrationEffect;
    private final VibrationEffect mResolveVibrationEffect;
    private final Vibrator mVibrator;
    
    static {
        SONIFICATION_AUDIO_ATTRIBUTES = new AudioAttributes$Builder().setContentType(4).setUsage(13).build();
    }
    
    public HapticClick(final Context p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   java/lang/Object.<init>:()V
        //     4: aload_0        
        //     5: aload_1        
        //     6: ldc             "vibrator"
        //     8: invokevirtual   android/content/Context.getSystemService:(Ljava/lang/String;)Ljava/lang/Object;
        //    11: checkcast       Landroid/os/Vibrator;
        //    14: putfield        com/google/android/systemui/elmyra/feedback/HapticClick.mVibrator:Landroid/os/Vibrator;
        //    17: aload_0        
        //    18: iconst_0       
        //    19: invokestatic    android/os/VibrationEffect.get:(I)Landroid/os/VibrationEffect;
        //    22: putfield        com/google/android/systemui/elmyra/feedback/HapticClick.mResolveVibrationEffect:Landroid/os/VibrationEffect;
        //    25: aload_0        
        //    26: iconst_5       
        //    27: invokestatic    android/os/VibrationEffect.get:(I)Landroid/os/VibrationEffect;
        //    30: putfield        com/google/android/systemui/elmyra/feedback/HapticClick.mProgressVibrationEffect:Landroid/os/VibrationEffect;
        //    33: aload_0        
        //    34: getfield        com/google/android/systemui/elmyra/feedback/HapticClick.mVibrator:Landroid/os/Vibrator;
        //    37: ifnull          94
        //    40: aload_1        
        //    41: invokevirtual   android/content/Context.getResources:()Landroid/content/res/Resources;
        //    44: getstatic       com/android/systemui/R$integer.elmyra_progress_always_on_vibration:I
        //    47: invokevirtual   android/content/res/Resources.getInteger:(I)I
        //    50: istore_2       
        //    51: aload_0        
        //    52: getfield        com/google/android/systemui/elmyra/feedback/HapticClick.mVibrator:Landroid/os/Vibrator;
        //    55: iload_2        
        //    56: aload_0        
        //    57: getfield        com/google/android/systemui/elmyra/feedback/HapticClick.mProgressVibrationEffect:Landroid/os/VibrationEffect;
        //    60: getstatic       com/google/android/systemui/elmyra/feedback/HapticClick.SONIFICATION_AUDIO_ATTRIBUTES:Landroid/media/AudioAttributes;
        //    63: invokevirtual   android/os/Vibrator.setAlwaysOnEffect:(ILandroid/os/VibrationEffect;Landroid/media/AudioAttributes;)Z
        //    66: pop            
        //    67: aload_1        
        //    68: invokevirtual   android/content/Context.getResources:()Landroid/content/res/Resources;
        //    71: getstatic       com/android/systemui/R$integer.elmyra_resolve_always_on_vibration:I
        //    74: invokevirtual   android/content/res/Resources.getInteger:(I)I
        //    77: istore_2       
        //    78: aload_0        
        //    79: getfield        com/google/android/systemui/elmyra/feedback/HapticClick.mVibrator:Landroid/os/Vibrator;
        //    82: iload_2        
        //    83: aload_0        
        //    84: getfield        com/google/android/systemui/elmyra/feedback/HapticClick.mResolveVibrationEffect:Landroid/os/VibrationEffect;
        //    87: getstatic       com/google/android/systemui/elmyra/feedback/HapticClick.SONIFICATION_AUDIO_ATTRIBUTES:Landroid/media/AudioAttributes;
        //    90: invokevirtual   android/os/Vibrator.setAlwaysOnEffect:(ILandroid/os/VibrationEffect;Landroid/media/AudioAttributes;)Z
        //    93: pop            
        //    94: return         
        //    95: astore_3       
        //    96: goto            67
        //    99: astore_1       
        //   100: goto            94
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                             
        //  -----  -----  -----  -----  -------------------------------------------------
        //  40     67     95     99     Landroid/content/res/Resources$NotFoundException;
        //  67     94     99     103    Landroid/content/res/Resources$NotFoundException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0067:
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
    
    @Override
    public void onProgress(final float n, final int mLastGestureStage) {
        if (this.mLastGestureStage != 2 && mLastGestureStage == 2) {
            final Vibrator mVibrator = this.mVibrator;
            if (mVibrator != null) {
                mVibrator.vibrate(this.mProgressVibrationEffect, HapticClick.SONIFICATION_AUDIO_ATTRIBUTES);
            }
        }
        this.mLastGestureStage = mLastGestureStage;
    }
    
    @Override
    public void onRelease() {
    }
    
    @Override
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        if (detectionProperties != null && detectionProperties.isHapticConsumed()) {
            return;
        }
        final Vibrator mVibrator = this.mVibrator;
        if (mVibrator != null) {
            mVibrator.vibrate(this.mResolveVibrationEffect, HapticClick.SONIFICATION_AUDIO_ATTRIBUTES);
        }
    }
}
