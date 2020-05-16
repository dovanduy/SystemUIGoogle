// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.util.Iterator;

public class Joiner
{
    private final String separator;
    
    private Joiner(final String s) {
        Preconditions.checkNotNull(s);
        this.separator = s;
    }
    
    public static Joiner on(final String s) {
        return new Joiner(s);
    }
    
    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(final A a, final Iterator<?> iterator) throws IOException {
        Preconditions.checkNotNull(a);
        if (iterator.hasNext()) {
            a.append(this.toString(iterator.next()));
            while (iterator.hasNext()) {
                a.append(this.separator);
                a.append(this.toString(iterator.next()));
            }
        }
        return a;
    }
    
    @CanIgnoreReturnValue
    public final StringBuilder appendTo(final StringBuilder sb, final Iterable<?> iterable) {
        this.appendTo(sb, iterable.iterator());
        return sb;
    }
    
    @CanIgnoreReturnValue
    public final StringBuilder appendTo(final StringBuilder sb, final Iterator<?> iterator) {
        try {
            this.appendTo(sb, iterator);
            return sb;
        }
        catch (IOException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    CharSequence toString(final Object o) {
        Preconditions.checkNotNull(o);
        CharSequence string;
        if (o instanceof CharSequence) {
            string = (CharSequence)o;
        }
        else {
            string = o.toString();
        }
        return string;
    }
}
