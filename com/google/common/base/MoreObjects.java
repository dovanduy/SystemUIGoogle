// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.util.Arrays;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public final class MoreObjects
{
    public static <T> T firstNonNull(final T t, final T t2) {
        if (t != null) {
            return t;
        }
        if (t2 != null) {
            return t2;
        }
        throw new NullPointerException("Both parameters are null");
    }
    
    public static ToStringHelper toStringHelper(final Object o) {
        return new ToStringHelper(o.getClass().getSimpleName());
    }
    
    public static final class ToStringHelper
    {
        private final String className;
        private final ValueHolder holderHead;
        private ValueHolder holderTail;
        private boolean omitNullValues;
        
        private ToStringHelper(final String s) {
            final ValueHolder valueHolder = new ValueHolder();
            this.holderHead = valueHolder;
            this.holderTail = valueHolder;
            this.omitNullValues = false;
            Preconditions.checkNotNull(s);
            this.className = s;
        }
        
        private ValueHolder addHolder() {
            final ValueHolder valueHolder = new ValueHolder();
            this.holderTail.next = valueHolder;
            return this.holderTail = valueHolder;
        }
        
        private ToStringHelper addHolder(final Object value) {
            this.addHolder().value = value;
            return this;
        }
        
        private ToStringHelper addHolder(final String s, final Object value) {
            final ValueHolder addHolder = this.addHolder();
            addHolder.value = value;
            Preconditions.checkNotNull(s);
            addHolder.name = s;
            return this;
        }
        
        @CanIgnoreReturnValue
        public ToStringHelper add(final String s, final int i) {
            this.addHolder(s, String.valueOf(i));
            return this;
        }
        
        @CanIgnoreReturnValue
        public ToStringHelper add(final String s, final Object o) {
            this.addHolder(s, o);
            return this;
        }
        
        @CanIgnoreReturnValue
        public ToStringHelper addValue(final Object o) {
            this.addHolder(o);
            return this;
        }
        
        @Override
        public String toString() {
            final boolean omitNullValues = this.omitNullValues;
            final StringBuilder sb = new StringBuilder(32);
            sb.append(this.className);
            sb.append('{');
            ValueHolder valueHolder = this.holderHead.next;
            String str = "";
            while (valueHolder != null) {
                final Object value = valueHolder.value;
                String s = null;
                Label_0157: {
                    if (omitNullValues) {
                        s = str;
                        if (value == null) {
                            break Label_0157;
                        }
                    }
                    sb.append(str);
                    final String name = valueHolder.name;
                    if (name != null) {
                        sb.append(name);
                        sb.append('=');
                    }
                    if (value != null && value.getClass().isArray()) {
                        final String deepToString = Arrays.deepToString(new Object[] { value });
                        sb.append(deepToString, 1, deepToString.length() - 1);
                    }
                    else {
                        sb.append(value);
                    }
                    s = ", ";
                }
                valueHolder = valueHolder.next;
                str = s;
            }
            sb.append('}');
            return sb.toString();
        }
        
        private static final class ValueHolder
        {
            String name;
            ValueHolder next;
            Object value;
        }
    }
}
