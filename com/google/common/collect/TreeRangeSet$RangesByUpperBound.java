// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.Comparator;
import java.util.NavigableMap;

final class TreeRangeSet$RangesByUpperBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>>
{
    private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
    private final Range<Cut<C>> upperBoundWindow;
    
    private TreeRangeSet$RangesByUpperBound(final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound, final Range<Cut<C>> upperBoundWindow) {
        this.rangesByLowerBound = rangesByLowerBound;
        this.upperBoundWindow = upperBoundWindow;
    }
    
    private NavigableMap<Cut<C>, Range<C>> subMap(final Range<Cut<C>> range) {
        if (range.isConnected(this.upperBoundWindow)) {
            return new TreeRangeSet$RangesByUpperBound<Object>((NavigableMap<Cut<?>, Range<?>>)this.rangesByLowerBound, (Range<Cut<?>>)range.intersection(this.upperBoundWindow));
        }
        return (NavigableMap<Cut<C>, Range<C>>)ImmutableSortedMap.of();
    }
    
    @Override
    public Comparator<? super Cut<C>> comparator() {
        return Ordering.natural();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.get(o) != null;
    }
    
    @Override
    Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
        Collection<? extends T> collection;
        if (this.upperBoundWindow.hasUpperBound()) {
            collection = this.rangesByLowerBound.headMap(this.upperBoundWindow.upperEndpoint(), false).descendingMap().values();
        }
        else {
            collection = this.rangesByLowerBound.descendingMap().values();
        }
        final PeekingIterator<Object> peekingIterator = Iterators.peekingIterator((Iterator<?>)collection.iterator());
        if (peekingIterator.hasNext() && this.upperBoundWindow.upperBound.isLessThan((Cut<C>)peekingIterator.peek().upperBound)) {
            peekingIterator.next();
        }
        return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
            @Override
            protected Entry<Cut<C>, Range<C>> computeNext() {
                if (!peekingIterator.hasNext()) {
                    return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
                final Range<C> range = peekingIterator.next();
                Entry<Cut<C>, Range<C>> immutableEntry;
                if (TreeRangeSet$RangesByUpperBound.this.upperBoundWindow.lowerBound.isLessThan((C)range.upperBound)) {
                    immutableEntry = Maps.immutableEntry((Cut<C>)range.upperBound, range);
                }
                else {
                    immutableEntry = (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
                return (Entry<Cut<C>, Range<C>>)immutableEntry;
            }
        };
    }
    
    @Override
    Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
        Iterator<Object> iterator;
        if (!this.upperBoundWindow.hasLowerBound()) {
            iterator = this.rangesByLowerBound.values().iterator();
        }
        else {
            final Map.Entry<Cut<C>, Range<C>> lowerEntry = this.rangesByLowerBound.lowerEntry(this.upperBoundWindow.lowerEndpoint());
            if (lowerEntry == null) {
                iterator = this.rangesByLowerBound.values().iterator();
            }
            else if (this.upperBoundWindow.lowerBound.isLessThan(lowerEntry.getValue().upperBound)) {
                iterator = this.rangesByLowerBound.tailMap(lowerEntry.getKey(), true).values().iterator();
            }
            else {
                iterator = this.rangesByLowerBound.tailMap(this.upperBoundWindow.lowerEndpoint(), true).values().iterator();
            }
        }
        return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
            @Override
            protected Entry<Cut<C>, Range<C>> computeNext() {
                if (!iterator.hasNext()) {
                    return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
                final Range<C> range = iterator.next();
                if (TreeRangeSet$RangesByUpperBound.this.upperBoundWindow.upperBound.isLessThan((C)range.upperBound)) {
                    return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
                return Maps.immutableEntry(range.upperBound, range);
            }
        };
    }
    
    @Override
    public Range<C> get(final Object o) {
        Label_0071: {
            if (!(o instanceof Cut)) {
                break Label_0071;
            }
            try {
                final Cut cut = (Cut)o;
                if (!this.upperBoundWindow.contains(cut)) {
                    return null;
                }
                final Map.Entry<Cut<C>, Range<C>> lowerEntry = this.rangesByLowerBound.lowerEntry(cut);
                if (lowerEntry != null && lowerEntry.getValue().upperBound.equals(cut)) {
                    return lowerEntry.getValue();
                }
                return null;
            }
            catch (ClassCastException ex) {
                return null;
            }
        }
    }
    
    @Override
    public NavigableMap<Cut<C>, Range<C>> headMap(final Cut<C> cut, final boolean b) {
        return this.subMap(Range.upTo(cut, BoundType.forBoolean(b)));
    }
    
    @Override
    public boolean isEmpty() {
        boolean empty;
        if (this.upperBoundWindow.equals(Range.all())) {
            empty = this.rangesByLowerBound.isEmpty();
        }
        else {
            empty = !this.entryIterator().hasNext();
        }
        return empty;
    }
    
    @Override
    public int size() {
        if (this.upperBoundWindow.equals(Range.all())) {
            return this.rangesByLowerBound.size();
        }
        return Iterators.size(this.entryIterator());
    }
    
    @Override
    public NavigableMap<Cut<C>, Range<C>> subMap(final Cut<C> cut, final boolean b, final Cut<C> cut2, final boolean b2) {
        return this.subMap(Range.range(cut, BoundType.forBoolean(b), cut2, BoundType.forBoolean(b2)));
    }
    
    @Override
    public NavigableMap<Cut<C>, Range<C>> tailMap(final Cut<C> cut, final boolean b) {
        return this.subMap(Range.downTo(cut, BoundType.forBoolean(b)));
    }
}
