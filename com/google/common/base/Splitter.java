// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.util.Iterator;

public final class Splitter
{
    private final int limit;
    private final boolean omitEmptyStrings;
    private final Strategy strategy;
    private final CharMatcher trimmer;
    
    private Splitter(final Strategy strategy) {
        this(strategy, false, CharMatcher.none(), Integer.MAX_VALUE);
    }
    
    private Splitter(final Strategy strategy, final boolean omitEmptyStrings, final CharMatcher trimmer, final int limit) {
        this.strategy = strategy;
        this.omitEmptyStrings = omitEmptyStrings;
        this.trimmer = trimmer;
        this.limit = limit;
    }
    
    public static Splitter on(final char c) {
        return on(CharMatcher.is(c));
    }
    
    public static Splitter on(final CharMatcher charMatcher) {
        Preconditions.checkNotNull(charMatcher);
        return new Splitter((Strategy)new Strategy() {
            public SplittingIterator iterator(final Splitter splitter, final CharSequence charSequence) {
                return new SplittingIterator(splitter, charSequence) {
                    @Override
                    int separatorEnd(final int n) {
                        return n + 1;
                    }
                    
                    @Override
                    int separatorStart(final int n) {
                        return charMatcher.indexIn(super.toSplit, n);
                    }
                };
            }
        });
    }
    
    public static Splitter on(final String s) {
        Preconditions.checkArgument(s.length() != 0, "The separator may not be the empty string.");
        if (s.length() == 1) {
            return on(s.charAt(0));
        }
        return new Splitter((Strategy)new Strategy() {
            public SplittingIterator iterator(final Splitter splitter, final CharSequence charSequence) {
                return new SplittingIterator(splitter, charSequence) {
                    public int separatorEnd(final int n) {
                        return n + s.length();
                    }
                    
                    public int separatorStart(int i) {
                        final int length = s.length();
                        final int length2 = super.toSplit.length();
                    Label_0021:
                        while (i <= length2 - length) {
                            for (int j = 0; j < length; ++j) {
                                if (super.toSplit.charAt(j + i) != s.charAt(j)) {
                                    ++i;
                                    continue Label_0021;
                                }
                            }
                            return i;
                        }
                        return -1;
                    }
                };
            }
        });
    }
    
    private Iterator<String> splittingIterator(final CharSequence charSequence) {
        return this.strategy.iterator(this, charSequence);
    }
    
    public Splitter omitEmptyStrings() {
        return new Splitter(this.strategy, true, this.trimmer, this.limit);
    }
    
    public Iterable<String> split(final CharSequence charSequence) {
        Preconditions.checkNotNull(charSequence);
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return Splitter.this.splittingIterator(charSequence);
            }
            
            @Override
            public String toString() {
                final Joiner on = Joiner.on(", ");
                final StringBuilder sb = new StringBuilder();
                sb.append('[');
                on.appendTo(sb, this);
                sb.append(']');
                return sb.toString();
            }
        };
    }
    
    private abstract static class SplittingIterator extends AbstractIterator<String>
    {
        int limit;
        int offset;
        final boolean omitEmptyStrings;
        final CharSequence toSplit;
        final CharMatcher trimmer;
        
        protected SplittingIterator(final Splitter splitter, final CharSequence toSplit) {
            this.offset = 0;
            this.trimmer = splitter.trimmer;
            this.omitEmptyStrings = splitter.omitEmptyStrings;
            this.limit = splitter.limit;
            this.toSplit = toSplit;
        }
        
        @Override
        protected String computeNext() {
            int n = this.offset;
            while (true) {
                final int offset = this.offset;
                if (offset == -1) {
                    return this.endOfData();
                }
                int n2 = this.separatorStart(offset);
                if (n2 == -1) {
                    n2 = this.toSplit.length();
                    this.offset = -1;
                }
                else {
                    this.offset = this.separatorEnd(n2);
                }
                final int offset2 = this.offset;
                int i = n;
                if (offset2 == n) {
                    if ((this.offset = offset2 + 1) <= this.toSplit.length()) {
                        continue;
                    }
                    this.offset = -1;
                }
                else {
                    int n3;
                    while (i < (n3 = n2)) {
                        n3 = n2;
                        if (!this.trimmer.matches(this.toSplit.charAt(i))) {
                            break;
                        }
                        ++i;
                    }
                    while (n3 > i && this.trimmer.matches(this.toSplit.charAt(n3 - 1))) {
                        --n3;
                    }
                    if (!this.omitEmptyStrings || i != n3) {
                        final int limit = this.limit;
                        int n4;
                        if (limit == 1) {
                            int length = this.toSplit.length();
                            this.offset = -1;
                            while (true) {
                                n4 = length;
                                if (length <= i) {
                                    break;
                                }
                                n4 = length;
                                if (!this.trimmer.matches(this.toSplit.charAt(length - 1))) {
                                    break;
                                }
                                --length;
                            }
                        }
                        else {
                            this.limit = limit - 1;
                            n4 = n3;
                        }
                        return this.toSplit.subSequence(i, n4).toString();
                    }
                    n = this.offset;
                }
            }
        }
        
        abstract int separatorEnd(final int p0);
        
        abstract int separatorStart(final int p0);
    }
    
    private interface Strategy
    {
        Iterator<String> iterator(final Splitter p0, final CharSequence p1);
    }
}
