// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

public class SpreadBuilder
{
    private final ArrayList<Object> list;
    
    public SpreadBuilder(final int initialCapacity) {
        this.list = new ArrayList<Object>(initialCapacity);
    }
    
    public void add(final Object e) {
        this.list.add(e);
    }
    
    public void addSpread(Object next) {
        if (next == null) {
            return;
        }
        if (next instanceof Object[]) {
            final Object[] elements = (Object[])next;
            if (elements.length > 0) {
                final ArrayList<Object> list = this.list;
                list.ensureCapacity(list.size() + elements.length);
                Collections.addAll(this.list, elements);
            }
        }
        else if (next instanceof Collection) {
            this.list.addAll((Collection<?>)next);
        }
        else if (next instanceof Iterable) {
            final Iterator<Object> iterator = ((Iterable)next).iterator();
            while (iterator.hasNext()) {
                next = iterator.next();
                this.list.add(next);
            }
        }
        else {
            if (!(next instanceof Iterator)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Don't know how to spread ");
                sb.append(next.getClass());
                throw new UnsupportedOperationException(sb.toString());
            }
            final Iterator iterator2 = (Iterator)next;
            while (iterator2.hasNext()) {
                this.list.add(iterator2.next());
            }
        }
    }
    
    public int size() {
        return this.list.size();
    }
    
    public Object[] toArray(final Object[] a) {
        return this.list.toArray(a);
    }
}
