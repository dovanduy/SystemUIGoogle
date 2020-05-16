// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Ascii;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.google.common.base.MoreObjects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.common.base.Preconditions;
import com.google.common.base.Equivalence;

public final class MapMaker
{
    int concurrencyLevel;
    int initialCapacity;
    Equivalence<Object> keyEquivalence;
    MapMakerInternalMap.Strength keyStrength;
    boolean useCustomMap;
    MapMakerInternalMap.Strength valueStrength;
    
    public MapMaker() {
        this.initialCapacity = -1;
        this.concurrencyLevel = -1;
    }
    
    @CanIgnoreReturnValue
    public MapMaker concurrencyLevel(final int concurrencyLevel) {
        final int concurrencyLevel2 = this.concurrencyLevel;
        final boolean b = true;
        Preconditions.checkState(concurrencyLevel2 == -1, "concurrency level was already set to %s", this.concurrencyLevel);
        Preconditions.checkArgument(concurrencyLevel > 0 && b);
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }
    
    int getConcurrencyLevel() {
        int concurrencyLevel;
        if ((concurrencyLevel = this.concurrencyLevel) == -1) {
            concurrencyLevel = 4;
        }
        return concurrencyLevel;
    }
    
    int getInitialCapacity() {
        int initialCapacity;
        if ((initialCapacity = this.initialCapacity) == -1) {
            initialCapacity = 16;
        }
        return initialCapacity;
    }
    
    Equivalence<Object> getKeyEquivalence() {
        return MoreObjects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
    }
    
    MapMakerInternalMap.Strength getKeyStrength() {
        return MoreObjects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
    }
    
    MapMakerInternalMap.Strength getValueStrength() {
        return MoreObjects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
    }
    
    @CanIgnoreReturnValue
    public MapMaker initialCapacity(final int initialCapacity) {
        final int initialCapacity2 = this.initialCapacity;
        final boolean b = true;
        Preconditions.checkState(initialCapacity2 == -1, "initial capacity was already set to %s", this.initialCapacity);
        Preconditions.checkArgument(initialCapacity >= 0 && b);
        this.initialCapacity = initialCapacity;
        return this;
    }
    
    @CanIgnoreReturnValue
    MapMaker keyEquivalence(final Equivalence<Object> equivalence) {
        Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", this.keyEquivalence);
        Preconditions.checkNotNull(equivalence);
        this.keyEquivalence = equivalence;
        this.useCustomMap = true;
        return this;
    }
    
    public <K, V> ConcurrentMap<K, V> makeMap() {
        if (!this.useCustomMap) {
            return new ConcurrentHashMap<K, V>(this.getInitialCapacity(), 0.75f, this.getConcurrencyLevel());
        }
        return (ConcurrentMap<K, V>)MapMakerInternalMap.create(this);
    }
    
    MapMaker setKeyStrength(final MapMakerInternalMap.Strength strength) {
        Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", this.keyStrength);
        Preconditions.checkNotNull(strength);
        this.keyStrength = strength;
        if (strength != MapMakerInternalMap.Strength.STRONG) {
            this.useCustomMap = true;
        }
        return this;
    }
    
    MapMaker setValueStrength(final MapMakerInternalMap.Strength strength) {
        Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", this.valueStrength);
        Preconditions.checkNotNull(strength);
        this.valueStrength = strength;
        if (strength != MapMakerInternalMap.Strength.STRONG) {
            this.useCustomMap = true;
        }
        return this;
    }
    
    @Override
    public String toString() {
        final MoreObjects.ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
        final int initialCapacity = this.initialCapacity;
        if (initialCapacity != -1) {
            stringHelper.add("initialCapacity", initialCapacity);
        }
        final int concurrencyLevel = this.concurrencyLevel;
        if (concurrencyLevel != -1) {
            stringHelper.add("concurrencyLevel", concurrencyLevel);
        }
        final MapMakerInternalMap.Strength keyStrength = this.keyStrength;
        if (keyStrength != null) {
            stringHelper.add("keyStrength", Ascii.toLowerCase(keyStrength.toString()));
        }
        final MapMakerInternalMap.Strength valueStrength = this.valueStrength;
        if (valueStrength != null) {
            stringHelper.add("valueStrength", Ascii.toLowerCase(valueStrength.toString()));
        }
        if (this.keyEquivalence != null) {
            stringHelper.addValue("keyEquivalence");
        }
        return stringHelper.toString();
    }
    
    @CanIgnoreReturnValue
    public MapMaker weakKeys() {
        this.setKeyStrength(MapMakerInternalMap.Strength.WEAK);
        return this;
    }
}
