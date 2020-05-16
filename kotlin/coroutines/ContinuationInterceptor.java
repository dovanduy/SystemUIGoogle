// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines;

public interface ContinuationInterceptor extends Element
{
    public static final Key Key = ContinuationInterceptor.Key.$$INSTANCE;
    
    void releaseInterceptedContinuation(final Continuation<?> p0);
    
    public static final class Key implements CoroutineContext.Key<ContinuationInterceptor>
    {
        static final /* synthetic */ Key $$INSTANCE;
        
        static {
            $$INSTANCE = new Key();
        }
        
        private Key() {
        }
    }
}
