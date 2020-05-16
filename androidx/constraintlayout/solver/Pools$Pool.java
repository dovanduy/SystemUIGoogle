// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver;

interface Pools$Pool<T>
{
    T acquire();
    
    boolean release(final T p0);
    
    void releaseAll(final T[] p0, final int p1);
}
