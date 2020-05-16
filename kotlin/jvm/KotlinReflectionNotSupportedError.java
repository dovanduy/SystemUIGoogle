// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm;

public class KotlinReflectionNotSupportedError extends Error
{
    public KotlinReflectionNotSupportedError() {
        super("Kotlin reflection implementation is not found at runtime. Make sure you have kotlin-reflect.jar in the classpath");
    }
    
    public KotlinReflectionNotSupportedError(final String message) {
        super(message);
    }
}
