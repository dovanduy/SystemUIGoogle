// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.jvm.internal;

import java.lang.reflect.Field;
import kotlin.jvm.internal.Intrinsics;

public final class DebugMetadataKt
{
    private static final void checkDebugMetadataVersion(final int i, final int j) {
        if (j <= i) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Debug metadata version mismatch. Expected: ");
        sb.append(i);
        sb.append(", got ");
        sb.append(j);
        sb.append(". Please update the Kotlin standard library.");
        throw new IllegalStateException(sb.toString().toString());
    }
    
    private static final DebugMetadata getDebugMetadataAnnotation(final BaseContinuationImpl baseContinuationImpl) {
        return baseContinuationImpl.getClass().getAnnotation(DebugMetadata.class);
    }
    
    private static final int getLabel(final BaseContinuationImpl obj) {
        int intValue;
        try {
            final Field declaredField = obj.getClass().getDeclaredField("label");
            Intrinsics.checkExpressionValueIsNotNull(declaredField, "field");
            declaredField.setAccessible(true);
            Object value;
            if (!((value = declaredField.get(obj)) instanceof Integer)) {
                value = null;
            }
            final Integer n = (Integer)value;
            if (n != null) {
                intValue = n;
            }
            else {
                intValue = 0;
            }
            --intValue;
        }
        catch (Exception ex) {
            intValue = -1;
        }
        return intValue;
    }
    
    public static final StackTraceElement getStackTraceElement(final BaseContinuationImpl baseContinuationImpl) {
        Intrinsics.checkParameterIsNotNull(baseContinuationImpl, "$this$getStackTraceElementImpl");
        final DebugMetadata debugMetadataAnnotation = getDebugMetadataAnnotation(baseContinuationImpl);
        if (debugMetadataAnnotation != null) {
            checkDebugMetadataVersion(1, debugMetadataAnnotation.v());
            final int label = getLabel(baseContinuationImpl);
            int lineNumber;
            if (label < 0) {
                lineNumber = -1;
            }
            else {
                lineNumber = debugMetadataAnnotation.l()[label];
            }
            final String moduleName = ModuleNameRetriever.INSTANCE.getModuleName(baseContinuationImpl);
            String declaringClass;
            if (moduleName == null) {
                declaringClass = debugMetadataAnnotation.c();
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append(moduleName);
                sb.append('/');
                sb.append(debugMetadataAnnotation.c());
                declaringClass = sb.toString();
            }
            return new StackTraceElement(declaringClass, debugMetadataAnnotation.m(), debugMetadataAnnotation.f(), lineNumber);
        }
        return null;
    }
}
