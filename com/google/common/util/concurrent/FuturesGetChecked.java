// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.concurrent.CopyOnWriteArraySet;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import com.google.common.base.Function;
import java.lang.reflect.Constructor;
import com.google.common.collect.Ordering;

final class FuturesGetChecked
{
    private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST;
    
    static {
        WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf((Function<Object, ? extends Comparable>)new Function<Constructor<?>, Boolean>() {
            @Override
            public Boolean apply(final Constructor<?> constructor) {
                return Arrays.asList((Class<?>[])constructor.getParameterTypes()).contains(String.class);
            }
        }).reverse();
    }
    
    static void checkExceptionClassValidity(final Class<? extends Exception> clazz) {
        Preconditions.checkArgument(isCheckedException(clazz), "Futures.getChecked exception type (%s) must not be a RuntimeException", clazz);
        Preconditions.checkArgument(hasConstructorUsableByGetChecked(clazz), "Futures.getChecked exception type (%s) must be an accessible class with an accessible constructor whose parameters (if any) must be of type String and/or Throwable", clazz);
    }
    
    static GetCheckedTypeValidator classValueValidator() {
        return (GetCheckedTypeValidator)ClassValueValidator.INSTANCE;
    }
    
    @CanIgnoreReturnValue
    static <V, X extends Exception> V getChecked(final GetCheckedTypeValidator getCheckedTypeValidator, final Future<V> future, final Class<X> clazz) throws X, Exception {
        getCheckedTypeValidator.validateClass(clazz);
        try {
            return future.get();
        }
        catch (ExecutionException ex) {
            wrapAndThrowExceptionOrError(ex.getCause(), clazz);
            throw null;
        }
        catch (InterruptedException ex2) {
            Thread.currentThread().interrupt();
            throw newWithCause(clazz, ex2);
        }
    }
    
    private static boolean hasConstructorUsableByGetChecked(final Class<? extends Exception> clazz) {
        try {
            newWithCause(clazz, new Exception());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    static boolean isCheckedException(final Class<? extends Exception> clazz) {
        return RuntimeException.class.isAssignableFrom(clazz) ^ true;
    }
    
    private static <X> X newFromConstructor(final Constructor<X> constructor, final Throwable t) {
        final Class<?>[] parameterTypes = (Class<?>[])constructor.getParameterTypes();
        final Object[] initargs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            final Class<?> clazz = parameterTypes[i];
            if (clazz.equals(String.class)) {
                initargs[i] = t.toString();
            }
            else {
                if (!clazz.equals(Throwable.class)) {
                    return null;
                }
                initargs[i] = t;
            }
        }
        try {
            return constructor.newInstance(initargs);
        }
        catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
    }
    
    private static <X extends Exception> X newWithCause(final Class<X> obj, final Throwable t) {
        final Iterator<Constructor<Exception>> iterator = preferringStrings((List<Constructor<Exception>>)Arrays.asList((Constructor<X>[])obj.getConstructors())).iterator();
        while (iterator.hasNext()) {
            final Exception ex = newFromConstructor((Constructor<X>)iterator.next(), t);
            if (ex != null) {
                if (ex.getCause() == null) {
                    ex.initCause(t);
                }
                return (X)ex;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("No appropriate constructor for exception of type ");
        sb.append(obj);
        sb.append(" in response to chained exception");
        throw new IllegalArgumentException(sb.toString(), t);
    }
    
    private static <X extends Exception> List<Constructor<X>> preferringStrings(final List<Constructor<X>> list) {
        return FuturesGetChecked.WITH_STRING_PARAM_FIRST.sortedCopy(list);
    }
    
    static GetCheckedTypeValidator weakSetValidator() {
        return (GetCheckedTypeValidator)WeakSetValidator.INSTANCE;
    }
    
    private static <X extends Exception> void wrapAndThrowExceptionOrError(final Throwable t, final Class<X> clazz) throws X, Exception {
        if (t instanceof Error) {
            throw new ExecutionError((Error)t);
        }
        if (t instanceof RuntimeException) {
            throw new UncheckedExecutionException(t);
        }
        throw newWithCause(clazz, t);
    }
    
    interface GetCheckedTypeValidator
    {
        void validateClass(final Class<? extends Exception> p0);
    }
    
    static class GetCheckedTypeValidatorHolder
    {
        static final String CLASS_VALUE_VALIDATOR_NAME;
        
        static {
            final StringBuilder sb = new StringBuilder();
            sb.append(GetCheckedTypeValidatorHolder.class.getName());
            sb.append("$ClassValueValidator");
            CLASS_VALUE_VALIDATOR_NAME = sb.toString();
            getBestValidator();
        }
        
        static GetCheckedTypeValidator getBestValidator() {
            try {
                return (GetCheckedTypeValidator)Class.forName(GetCheckedTypeValidatorHolder.CLASS_VALUE_VALIDATOR_NAME).getEnumConstants()[0];
            }
            finally {
                return FuturesGetChecked.weakSetValidator();
            }
        }
        
        enum ClassValueValidator implements GetCheckedTypeValidator
        {
            INSTANCE;
            
            private static final ClassValue<Boolean> isValidClass;
            
            static {
                isValidClass = new ClassValue<Boolean>() {};
            }
            
            @Override
            public void validateClass(final Class<? extends Exception> type) {
                ClassValueValidator.isValidClass.get(type);
            }
        }
        
        enum WeakSetValidator implements GetCheckedTypeValidator
        {
            INSTANCE;
            
            private static final Set<WeakReference<Class<? extends Exception>>> validClasses;
            
            static {
                validClasses = new CopyOnWriteArraySet<WeakReference<Class<? extends Exception>>>();
            }
            
            @Override
            public void validateClass(final Class<? extends Exception> referent) {
                final Iterator<WeakReference<Class<? extends Exception>>> iterator = WeakSetValidator.validClasses.iterator();
                while (iterator.hasNext()) {
                    if (referent.equals(iterator.next().get())) {
                        return;
                    }
                }
                FuturesGetChecked.checkExceptionClassValidity(referent);
                if (WeakSetValidator.validClasses.size() > 1000) {
                    WeakSetValidator.validClasses.clear();
                }
                WeakSetValidator.validClasses.add(new WeakReference<Class<? extends Exception>>(referent));
            }
        }
    }
}
