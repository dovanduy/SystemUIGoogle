// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import java.util.Set;
import android.service.controls.Control;
import com.android.systemui.controls.controller.ControlInfo;
import kotlin.sequences.Sequence;
import android.text.TextUtils;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.SequencesKt;
import kotlin.collections.MapsKt;
import java.util.Map;
import android.util.ArrayMap;
import java.util.Iterator;
import java.util.Collection;
import kotlin.collections.CollectionsKt;
import java.util.ArrayList;
import java.util.HashSet;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.controls.ControlStatus;
import java.util.List;

public final class AllModel implements ControlsModel
{
    private final List<ControlStatus> controls;
    private final List<ElementWrapper> elements;
    private final CharSequence emptyZoneString;
    private final List<String> favoriteIds;
    
    public AllModel(final List<ControlStatus> controls, final List<String> list, final CharSequence emptyZoneString) {
        Intrinsics.checkParameterIsNotNull(controls, "controls");
        Intrinsics.checkParameterIsNotNull(list, "initialFavoriteIds");
        Intrinsics.checkParameterIsNotNull(emptyZoneString, "emptyZoneString");
        this.controls = controls;
        this.emptyZoneString = emptyZoneString;
        final HashSet<String> set = new HashSet<String>();
        final Iterator<ControlStatus> iterator = controls.iterator();
        while (iterator.hasNext()) {
            set.add(iterator.next().getControl().getControlId());
        }
        final ArrayList<String> list2 = new ArrayList<String>();
        for (final String next : list) {
            if (set.contains(next)) {
                list2.add(next);
            }
        }
        this.favoriteIds = (List<String>)CollectionsKt.toMutableList((Collection<?>)list2);
        this.elements = this.createWrappers(this.controls);
    }
    
    private final List<ElementWrapper> createWrappers(final List<ControlStatus> list) {
        final OrderedMap<CharSequence, Iterable<? extends T>> orderedMap = (OrderedMap<CharSequence, Iterable<? extends T>>)new OrderedMap<Object, List<ControlStatus>>((Map<Object, List<ControlStatus>>)new ArrayMap());
        for (final ControlStatus next : list) {
            CharSequence zone = next.getControl().getZone();
            if (zone == null) {
                zone = "";
            }
            List<ControlStatus> value;
            if ((value = orderedMap.get(zone)) == null) {
                value = new ArrayList<ControlStatus>();
                orderedMap.put(zone, value);
            }
            value.add(next);
        }
        final ArrayList<Object> list2 = (ArrayList<Object>)new ArrayList<ElementWrapper>();
        Sequence<? extends T> sequence = null;
        for (final CharSequence charSequence : orderedMap.getOrderedKeys()) {
            final Object value2 = MapsKt.getValue((Map<CharSequence, ?>)orderedMap, charSequence);
            Intrinsics.checkExpressionValueIsNotNull(value2, "map.getValue(zoneName)");
            final Sequence<Object> map = SequencesKt.map(CollectionsKt.asSequence((Iterable<?>)value2), (Function1<? super Object, ?>)AllModel$createWrappers$values.AllModel$createWrappers$values$1.INSTANCE);
            if (TextUtils.isEmpty(charSequence)) {
                sequence = (Sequence<? extends T>)map;
            }
            else {
                Intrinsics.checkExpressionValueIsNotNull(charSequence, "zoneName");
                list2.add(new ZoneNameWrapper(charSequence));
                CollectionsKt.addAll((Collection<? super Object>)list2, (Sequence<?>)map);
            }
        }
        if (sequence != null) {
            if (orderedMap.size() == 0) {
                list2.add(new ZoneNameWrapper(this.emptyZoneString));
            }
            CollectionsKt.addAll((Collection<? super Object>)list2, (Sequence<?>)sequence);
        }
        return (List<ElementWrapper>)list2;
    }
    
    @Override
    public void changeFavoriteStatus(final String s, final boolean favorite) {
        Intrinsics.checkParameterIsNotNull(s, "controlId");
        while (true) {
            for (final ControlWrapper next : this.getElements()) {
                final ControlWrapper controlWrapper = next;
                if (controlWrapper instanceof ControlWrapper && Intrinsics.areEqual(controlWrapper.getControlStatus().getControl().getControlId(), s)) {
                    final ControlWrapper controlWrapper2 = next;
                    if (controlWrapper2 != null) {
                        final ControlStatus controlStatus = controlWrapper2.getControlStatus();
                        if (controlStatus != null && favorite == controlStatus.getFavorite()) {
                            return;
                        }
                    }
                    if (favorite) {
                        this.favoriteIds.add(s);
                    }
                    else {
                        this.favoriteIds.remove(s);
                    }
                    if (controlWrapper2 != null) {
                        controlWrapper2.getControlStatus().setFavorite(favorite);
                    }
                    return;
                }
            }
            ControlWrapper next = null;
            continue;
        }
    }
    
    @Override
    public List<ElementWrapper> getElements() {
        return this.elements;
    }
    
    @Override
    public List<ControlInfo.Builder> getFavorites() {
        final List<String> favoriteIds = this.favoriteIds;
        final ArrayList<ControlInfo.Builder> list = new ArrayList<ControlInfo.Builder>();
    Label_0020:
        for (final String s : favoriteIds) {
            final Iterator<ControlStatus> iterator2 = (Iterator<ControlStatus>)this.controls.iterator();
            while (true) {
                ControlStatus next;
                do {
                    final boolean hasNext = iterator2.hasNext();
                    ControlInfo.Builder builder = null;
                    if (hasNext) {
                        next = iterator2.next();
                    }
                    else {
                        next = null;
                        final ControlStatus controlStatus = next;
                        Control control;
                        if (controlStatus != null) {
                            control = controlStatus.getControl();
                        }
                        else {
                            control = null;
                        }
                        if (control != null) {
                            builder = new ControlInfo.Builder();
                            final String controlId = control.getControlId();
                            Intrinsics.checkExpressionValueIsNotNull(controlId, "it.controlId");
                            builder.setControlId(controlId);
                            final CharSequence title = control.getTitle();
                            Intrinsics.checkExpressionValueIsNotNull(title, "it.title");
                            builder.setControlTitle(title);
                            final CharSequence subtitle = control.getSubtitle();
                            Intrinsics.checkExpressionValueIsNotNull(subtitle, "it.subtitle");
                            builder.setControlSubtitle(subtitle);
                            builder.setDeviceType(control.getDeviceType());
                        }
                        if (builder != null) {
                            list.add(builder);
                            continue Label_0020;
                        }
                        continue Label_0020;
                    }
                } while (!Intrinsics.areEqual(next.getControl().getControlId(), s));
                continue;
            }
        }
        return list;
    }
    
    private static final class OrderedMap<K, V> implements Map<K, V>, Object
    {
        private final Map<K, V> map;
        private final List<K> orderedKeys;
        
        public OrderedMap(final Map<K, V> map) {
            Intrinsics.checkParameterIsNotNull(map, "map");
            this.map = map;
            this.orderedKeys = new ArrayList<K>();
        }
        
        @Override
        public void clear() {
            this.orderedKeys.clear();
            this.map.clear();
        }
        
        @Override
        public boolean containsKey(final Object o) {
            return this.map.containsKey(o);
        }
        
        @Override
        public boolean containsValue(final Object o) {
            return this.map.containsValue(o);
        }
        
        @Override
        public final /* bridge */ Set<Entry<K, V>> entrySet() {
            return this.getEntries();
        }
        
        @Override
        public V get(final Object o) {
            return this.map.get(o);
        }
        
        public Set<Entry<K, V>> getEntries() {
            return this.map.entrySet();
        }
        
        public Set<K> getKeys() {
            return this.map.keySet();
        }
        
        public final List<K> getOrderedKeys() {
            return this.orderedKeys;
        }
        
        public int getSize() {
            return this.map.size();
        }
        
        public Collection<V> getValues() {
            return this.map.values();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public final /* bridge */ Set<K> keySet() {
            return this.getKeys();
        }
        
        @Override
        public V put(final K k, final V v) {
            if (!this.map.containsKey(k)) {
                this.orderedKeys.add(k);
            }
            return this.map.put(k, v);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> map) {
            Intrinsics.checkParameterIsNotNull(map, "from");
            this.map.putAll(map);
        }
        
        @Override
        public V remove(final Object o) {
            final V remove = this.map.remove(o);
            if (remove != null) {
                this.orderedKeys.remove(o);
            }
            return remove;
        }
        
        @Override
        public final /* bridge */ int size() {
            return this.getSize();
        }
        
        @Override
        public final /* bridge */ Collection<V> values() {
            return this.getValues();
        }
    }
}
