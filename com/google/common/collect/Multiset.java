// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Set;
import java.util.Collection;

public interface Multiset<E> extends Collection<E>
{
    Set<E> elementSet();
}
