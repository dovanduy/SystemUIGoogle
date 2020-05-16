// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.jvm.internal;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.FunctionBase;

public abstract class RestrictedSuspendLambda extends RestrictedContinuationImpl implements FunctionBase<Object>, Object
{
    private final int arity;
    
    public RestrictedSuspendLambda(final int arity, final Continuation<Object> continuation) {
        super(continuation);
        this.arity = arity;
    }
    
    @Override
    public int getArity() {
        return this.arity;
    }
    
    @Override
    public String toString() {
        String s;
        if (this.getCompletion() == null) {
            s = Reflection.renderLambdaToString(this);
            Intrinsics.checkExpressionValueIsNotNull(s, "Reflection.renderLambdaToString(this)");
        }
        else {
            s = super.toString();
        }
        return s;
    }
}
