// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import kotlin.reflect.KProperty1;
import kotlin.reflect.KProperty0;
import kotlin.reflect.KDeclarationContainer;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;

public class Reflection
{
    private static final ReflectionFactory factory;
    
    static {
        ReflectionFactory factory2 = null;
        while (true) {
            try {
                factory2 = (ReflectionFactory)Class.forName("kotlin.reflect.jvm.internal.ReflectionFactoryImpl").newInstance();
                if (factory2 == null) {
                    factory2 = new ReflectionFactory();
                }
                factory = factory2;
            }
            catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                continue;
            }
            break;
        }
    }
    
    public static KFunction function(final FunctionReference functionReference) {
        Reflection.factory.function(functionReference);
        return functionReference;
    }
    
    public static KClass getOrCreateKotlinClass(final Class clazz) {
        return Reflection.factory.getOrCreateKotlinClass(clazz);
    }
    
    public static KDeclarationContainer getOrCreateKotlinPackage(final Class clazz, final String s) {
        return Reflection.factory.getOrCreateKotlinPackage(clazz, s);
    }
    
    public static KProperty0 property0(final PropertyReference0 propertyReference0) {
        Reflection.factory.property0(propertyReference0);
        return propertyReference0;
    }
    
    public static KProperty1 property1(final PropertyReference1 propertyReference1) {
        Reflection.factory.property1(propertyReference1);
        return propertyReference1;
    }
    
    public static String renderLambdaToString(final FunctionBase functionBase) {
        return Reflection.factory.renderLambdaToString(functionBase);
    }
    
    public static String renderLambdaToString(final Lambda lambda) {
        return Reflection.factory.renderLambdaToString(lambda);
    }
}
