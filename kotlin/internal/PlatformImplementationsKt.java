// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.internal;

public final class PlatformImplementationsKt
{
    public static final PlatformImplementations IMPLEMENTATIONS;
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: istore_0       
        //     4: iload_0        
        //     5: ldc             65544
        //     7: if_icmplt       264
        //    10: ldc             "kotlin.internal.jdk8.JDK8PlatformImplementations"
        //    12: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //    15: invokevirtual   java/lang/Class.newInstance:()Ljava/lang/Object;
        //    18: astore_1       
        //    19: aload_1        
        //    20: ldc             "Class.forName(\"kotlin.in\u2026entations\").newInstance()"
        //    22: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //    25: aload_1        
        //    26: ifnull          41
        //    29: aload_1        
        //    30: checkcast       Lkotlin/internal/PlatformImplementations;
        //    33: astore_2       
        //    34: goto            526
        //    37: astore_2       
        //    38: goto            53
        //    41: new             Lkotlin/TypeCastException;
        //    44: astore_2       
        //    45: aload_2        
        //    46: ldc             "null cannot be cast to non-null type kotlin.internal.PlatformImplementations"
        //    48: invokespecial   kotlin/TypeCastException.<init>:(Ljava/lang/String;)V
        //    51: aload_2        
        //    52: athrow         
        //    53: aload_1        
        //    54: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //    57: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //    60: astore_3       
        //    61: ldc             Lkotlin/internal/PlatformImplementations;.class
        //    63: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //    66: astore_1       
        //    67: new             Ljava/lang/ClassCastException;
        //    70: astore          4
        //    72: new             Ljava/lang/StringBuilder;
        //    75: astore          5
        //    77: aload           5
        //    79: invokespecial   java/lang/StringBuilder.<init>:()V
        //    82: aload           5
        //    84: ldc             "Instance classloader: "
        //    86: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    89: pop            
        //    90: aload           5
        //    92: aload_3        
        //    93: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    96: pop            
        //    97: aload           5
        //    99: ldc             ", base type classloader: "
        //   101: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   104: pop            
        //   105: aload           5
        //   107: aload_1        
        //   108: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   111: pop            
        //   112: aload           4
        //   114: aload           5
        //   116: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   119: invokespecial   java/lang/ClassCastException.<init>:(Ljava/lang/String;)V
        //   122: aload           4
        //   124: aload_2        
        //   125: invokevirtual   java/lang/ClassCastException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   128: astore_2       
        //   129: aload_2        
        //   130: ldc             "ClassCastException(\"Inst\u2026baseTypeCL\").initCause(e)"
        //   132: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   135: aload_2        
        //   136: athrow         
        //   137: astore_2       
        //   138: ldc             "kotlin.internal.JRE8PlatformImplementations"
        //   140: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //   143: invokevirtual   java/lang/Class.newInstance:()Ljava/lang/Object;
        //   146: astore_1       
        //   147: aload_1        
        //   148: ldc             "Class.forName(\"kotlin.in\u2026entations\").newInstance()"
        //   150: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   153: aload_1        
        //   154: ifnull          169
        //   157: aload_1        
        //   158: checkcast       Lkotlin/internal/PlatformImplementations;
        //   161: astore_2       
        //   162: goto            526
        //   165: astore_2       
        //   166: goto            181
        //   169: new             Lkotlin/TypeCastException;
        //   172: astore_2       
        //   173: aload_2        
        //   174: ldc             "null cannot be cast to non-null type kotlin.internal.PlatformImplementations"
        //   176: invokespecial   kotlin/TypeCastException.<init>:(Ljava/lang/String;)V
        //   179: aload_2        
        //   180: athrow         
        //   181: aload_1        
        //   182: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //   185: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //   188: astore          4
        //   190: ldc             Lkotlin/internal/PlatformImplementations;.class
        //   192: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //   195: astore_1       
        //   196: new             Ljava/lang/ClassCastException;
        //   199: astore_3       
        //   200: new             Ljava/lang/StringBuilder;
        //   203: astore          5
        //   205: aload           5
        //   207: invokespecial   java/lang/StringBuilder.<init>:()V
        //   210: aload           5
        //   212: ldc             "Instance classloader: "
        //   214: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   217: pop            
        //   218: aload           5
        //   220: aload           4
        //   222: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   225: pop            
        //   226: aload           5
        //   228: ldc             ", base type classloader: "
        //   230: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   233: pop            
        //   234: aload           5
        //   236: aload_1        
        //   237: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   240: pop            
        //   241: aload_3        
        //   242: aload           5
        //   244: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   247: invokespecial   java/lang/ClassCastException.<init>:(Ljava/lang/String;)V
        //   250: aload_3        
        //   251: aload_2        
        //   252: invokevirtual   java/lang/ClassCastException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   255: astore_2       
        //   256: aload_2        
        //   257: ldc             "ClassCastException(\"Inst\u2026baseTypeCL\").initCause(e)"
        //   259: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   262: aload_2        
        //   263: athrow         
        //   264: iload_0        
        //   265: ldc             65543
        //   267: if_icmplt       518
        //   270: ldc             "kotlin.internal.jdk7.JDK7PlatformImplementations"
        //   272: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //   275: invokevirtual   java/lang/Class.newInstance:()Ljava/lang/Object;
        //   278: astore_1       
        //   279: aload_1        
        //   280: ldc             "Class.forName(\"kotlin.in\u2026entations\").newInstance()"
        //   282: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   285: aload_1        
        //   286: ifnull          301
        //   289: aload_1        
        //   290: checkcast       Lkotlin/internal/PlatformImplementations;
        //   293: astore_2       
        //   294: goto            526
        //   297: astore_2       
        //   298: goto            313
        //   301: new             Lkotlin/TypeCastException;
        //   304: astore_2       
        //   305: aload_2        
        //   306: ldc             "null cannot be cast to non-null type kotlin.internal.PlatformImplementations"
        //   308: invokespecial   kotlin/TypeCastException.<init>:(Ljava/lang/String;)V
        //   311: aload_2        
        //   312: athrow         
        //   313: aload_1        
        //   314: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //   317: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //   320: astore          5
        //   322: ldc             Lkotlin/internal/PlatformImplementations;.class
        //   324: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //   327: astore          4
        //   329: new             Ljava/lang/ClassCastException;
        //   332: astore_3       
        //   333: new             Ljava/lang/StringBuilder;
        //   336: astore_1       
        //   337: aload_1        
        //   338: invokespecial   java/lang/StringBuilder.<init>:()V
        //   341: aload_1        
        //   342: ldc             "Instance classloader: "
        //   344: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   347: pop            
        //   348: aload_1        
        //   349: aload           5
        //   351: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   354: pop            
        //   355: aload_1        
        //   356: ldc             ", base type classloader: "
        //   358: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   361: pop            
        //   362: aload_1        
        //   363: aload           4
        //   365: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   368: pop            
        //   369: aload_3        
        //   370: aload_1        
        //   371: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   374: invokespecial   java/lang/ClassCastException.<init>:(Ljava/lang/String;)V
        //   377: aload_3        
        //   378: aload_2        
        //   379: invokevirtual   java/lang/ClassCastException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   382: astore_2       
        //   383: aload_2        
        //   384: ldc             "ClassCastException(\"Inst\u2026baseTypeCL\").initCause(e)"
        //   386: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   389: aload_2        
        //   390: athrow         
        //   391: astore_2       
        //   392: ldc             "kotlin.internal.JRE7PlatformImplementations"
        //   394: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //   397: invokevirtual   java/lang/Class.newInstance:()Ljava/lang/Object;
        //   400: astore_1       
        //   401: aload_1        
        //   402: ldc             "Class.forName(\"kotlin.in\u2026entations\").newInstance()"
        //   404: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   407: aload_1        
        //   408: ifnull          423
        //   411: aload_1        
        //   412: checkcast       Lkotlin/internal/PlatformImplementations;
        //   415: astore_2       
        //   416: goto            526
        //   419: astore_2       
        //   420: goto            435
        //   423: new             Lkotlin/TypeCastException;
        //   426: astore_2       
        //   427: aload_2        
        //   428: ldc             "null cannot be cast to non-null type kotlin.internal.PlatformImplementations"
        //   430: invokespecial   kotlin/TypeCastException.<init>:(Ljava/lang/String;)V
        //   433: aload_2        
        //   434: athrow         
        //   435: aload_1        
        //   436: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //   439: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //   442: astore          4
        //   444: ldc             Lkotlin/internal/PlatformImplementations;.class
        //   446: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //   449: astore_1       
        //   450: new             Ljava/lang/ClassCastException;
        //   453: astore_3       
        //   454: new             Ljava/lang/StringBuilder;
        //   457: astore          5
        //   459: aload           5
        //   461: invokespecial   java/lang/StringBuilder.<init>:()V
        //   464: aload           5
        //   466: ldc             "Instance classloader: "
        //   468: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   471: pop            
        //   472: aload           5
        //   474: aload           4
        //   476: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   479: pop            
        //   480: aload           5
        //   482: ldc             ", base type classloader: "
        //   484: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   487: pop            
        //   488: aload           5
        //   490: aload_1        
        //   491: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   494: pop            
        //   495: aload_3        
        //   496: aload           5
        //   498: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   501: invokespecial   java/lang/ClassCastException.<init>:(Ljava/lang/String;)V
        //   504: aload_3        
        //   505: aload_2        
        //   506: invokevirtual   java/lang/ClassCastException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   509: astore_2       
        //   510: aload_2        
        //   511: ldc             "ClassCastException(\"Inst\u2026baseTypeCL\").initCause(e)"
        //   513: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   516: aload_2        
        //   517: athrow         
        //   518: new             Lkotlin/internal/PlatformImplementations;
        //   521: dup            
        //   522: invokespecial   kotlin/internal/PlatformImplementations.<init>:()V
        //   525: astore_2       
        //   526: aload_2        
        //   527: putstatic       kotlin/internal/PlatformImplementationsKt.IMPLEMENTATIONS:Lkotlin/internal/PlatformImplementations;
        //   530: return         
        //   531: astore_2       
        //   532: goto            264
        //   535: astore_2       
        //   536: goto            518
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                              
        //  -----  -----  -----  -----  ----------------------------------
        //  10     25     137    264    Ljava/lang/ClassNotFoundException;
        //  29     34     37     41     Ljava/lang/ClassCastException;
        //  29     34     137    264    Ljava/lang/ClassNotFoundException;
        //  41     53     37     41     Ljava/lang/ClassCastException;
        //  41     53     137    264    Ljava/lang/ClassNotFoundException;
        //  53     137    137    264    Ljava/lang/ClassNotFoundException;
        //  138    153    531    535    Ljava/lang/ClassNotFoundException;
        //  157    162    165    169    Ljava/lang/ClassCastException;
        //  157    162    531    535    Ljava/lang/ClassNotFoundException;
        //  169    181    165    169    Ljava/lang/ClassCastException;
        //  169    181    531    535    Ljava/lang/ClassNotFoundException;
        //  181    264    531    535    Ljava/lang/ClassNotFoundException;
        //  270    285    391    518    Ljava/lang/ClassNotFoundException;
        //  289    294    297    301    Ljava/lang/ClassCastException;
        //  289    294    391    518    Ljava/lang/ClassNotFoundException;
        //  301    313    297    301    Ljava/lang/ClassCastException;
        //  301    313    391    518    Ljava/lang/ClassNotFoundException;
        //  313    391    391    518    Ljava/lang/ClassNotFoundException;
        //  392    407    535    539    Ljava/lang/ClassNotFoundException;
        //  411    416    419    423    Ljava/lang/ClassCastException;
        //  411    416    535    539    Ljava/lang/ClassNotFoundException;
        //  423    435    419    423    Ljava/lang/ClassCastException;
        //  423    435    535    539    Ljava/lang/ClassNotFoundException;
        //  435    518    535    539    Ljava/lang/ClassNotFoundException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0169:
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
    
    private static final int getJavaVersion() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: invokestatic    java/lang/System.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //     5: astore_0       
        //     6: ldc             65542
        //     8: istore_1       
        //     9: aload_0        
        //    10: ifnull          151
        //    13: aload_0        
        //    14: bipush          46
        //    16: iconst_0       
        //    17: iconst_0       
        //    18: bipush          6
        //    20: aconst_null    
        //    21: invokestatic    kotlin/text/StringsKt.indexOf$default:(Ljava/lang/CharSequence;CIZILjava/lang/Object;)I
        //    24: istore_2       
        //    25: iload_2        
        //    26: ifge            41
        //    29: aload_0        
        //    30: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //    33: istore_3       
        //    34: iload_3        
        //    35: ldc             65536
        //    37: imul           
        //    38: istore_1       
        //    39: iload_1        
        //    40: ireturn        
        //    41: iload_2        
        //    42: iconst_1       
        //    43: iadd           
        //    44: istore          4
        //    46: aload_0        
        //    47: bipush          46
        //    49: iload           4
        //    51: iconst_0       
        //    52: iconst_4       
        //    53: aconst_null    
        //    54: invokestatic    kotlin/text/StringsKt.indexOf$default:(Ljava/lang/CharSequence;CIZILjava/lang/Object;)I
        //    57: istore          5
        //    59: iload           5
        //    61: istore_3       
        //    62: iload           5
        //    64: ifge            72
        //    67: aload_0        
        //    68: invokevirtual   java/lang/String.length:()I
        //    71: istore_3       
        //    72: aload_0        
        //    73: ifnull          141
        //    76: aload_0        
        //    77: iconst_0       
        //    78: iload_2        
        //    79: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //    82: astore          6
        //    84: aload           6
        //    86: ldc             "(this as java.lang.Strin\u2026ing(startIndex, endIndex)"
        //    88: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //    91: aload_0        
        //    92: ifnull          131
        //    95: aload_0        
        //    96: iload           4
        //    98: iload_3        
        //    99: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //   102: astore_0       
        //   103: aload_0        
        //   104: ldc             "(this as java.lang.Strin\u2026ing(startIndex, endIndex)"
        //   106: invokestatic    kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
        //   109: aload           6
        //   111: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //   114: istore          5
        //   116: aload_0        
        //   117: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //   120: istore_3       
        //   121: iload           5
        //   123: ldc             65536
        //   125: imul           
        //   126: iload_3        
        //   127: iadd           
        //   128: istore_1       
        //   129: iload_1        
        //   130: ireturn        
        //   131: new             Lkotlin/TypeCastException;
        //   134: dup            
        //   135: ldc             "null cannot be cast to non-null type java.lang.String"
        //   137: invokespecial   kotlin/TypeCastException.<init>:(Ljava/lang/String;)V
        //   140: athrow         
        //   141: new             Lkotlin/TypeCastException;
        //   144: dup            
        //   145: ldc             "null cannot be cast to non-null type java.lang.String"
        //   147: invokespecial   kotlin/TypeCastException.<init>:(Ljava/lang/String;)V
        //   150: athrow         
        //   151: ldc             65542
        //   153: ireturn        
        //   154: astore          6
        //   156: goto            39
        //   159: astore          6
        //   161: goto            129
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  29     34     154    159    Ljava/lang/NumberFormatException;
        //  109    121    159    164    Ljava/lang/NumberFormatException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0129:
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
}
