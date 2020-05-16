// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines;

public interface CoroutineContext
{
     <E extends Element> E get(final Key<E> p0);
    
    public interface Element extends CoroutineContext
    {
    }
    
    public interface Key<E extends Element>
    {
    }
}
