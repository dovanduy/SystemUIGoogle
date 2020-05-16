// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import kotlin.jvm.internal.Intrinsics;
import java.io.Serializable;

public final class Pair<A, B> implements Serializable
{
    private final A first;
    private final B second;
    
    public Pair(final A first, final B second) {
        this.first = first;
        this.second = second;
    }
    
    public final A component1() {
        return this.first;
    }
    
    public final B component2() {
        return this.second;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof Pair) {
                final Pair pair = (Pair)o;
                if (Intrinsics.areEqual(this.first, pair.first) && Intrinsics.areEqual(this.second, pair.second)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final A getFirst() {
        return this.first;
    }
    
    public final B getSecond() {
        return this.second;
    }
    
    @Override
    public int hashCode() {
        final A first = this.first;
        int hashCode = 0;
        int hashCode2;
        if (first != null) {
            hashCode2 = first.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final B second = this.second;
        if (second != null) {
            hashCode = second.hashCode();
        }
        return hashCode2 * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(this.first);
        sb.append(", ");
        sb.append(this.second);
        sb.append(')');
        return sb.toString();
    }
}
