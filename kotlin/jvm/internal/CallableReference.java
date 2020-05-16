// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import java.io.ObjectStreamException;
import kotlin.jvm.KotlinReflectionNotSupportedError;
import kotlin.reflect.KDeclarationContainer;
import java.io.Serializable;
import kotlin.reflect.KCallable;

public abstract class CallableReference implements KCallable, Serializable
{
    public static final Object NO_RECEIVER;
    protected final Object receiver;
    private transient KCallable reflected;
    
    static {
        NO_RECEIVER = NoReceiver.INSTANCE;
    }
    
    public CallableReference() {
        this(CallableReference.NO_RECEIVER);
    }
    
    protected CallableReference(final Object receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public Object call(final Object... array) {
        return this.getReflected().call(array);
    }
    
    public KCallable compute() {
        KCallable reflected;
        if ((reflected = this.reflected) == null) {
            reflected = this.computeReflected();
            this.reflected = reflected;
        }
        return reflected;
    }
    
    protected abstract KCallable computeReflected();
    
    public Object getBoundReceiver() {
        return this.receiver;
    }
    
    public String getName() {
        throw new AbstractMethodError();
    }
    
    public KDeclarationContainer getOwner() {
        throw new AbstractMethodError();
    }
    
    protected KCallable getReflected() {
        final KCallable compute = this.compute();
        if (compute != this) {
            return compute;
        }
        throw new KotlinReflectionNotSupportedError();
    }
    
    public String getSignature() {
        throw new AbstractMethodError();
    }
    
    private static class NoReceiver implements Serializable
    {
        private static final NoReceiver INSTANCE;
        
        static {
            INSTANCE = new NoReceiver();
        }
        
        private Object readResolve() throws ObjectStreamException {
            return NoReceiver.INSTANCE;
        }
    }
}
