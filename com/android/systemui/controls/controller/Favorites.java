// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import kotlin.Pair;
import kotlin.ranges.RangesKt;
import android.service.controls.Control;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.collections.CollectionsKt;
import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;
import kotlin.collections.MapsKt;
import java.util.List;
import android.content.ComponentName;
import java.util.Map;

final class Favorites
{
    public static final Favorites INSTANCE;
    private static Map<ComponentName, ? extends List<StructureInfo>> favMap;
    
    static {
        INSTANCE = new Favorites();
        Favorites.favMap = MapsKt.emptyMap();
    }
    
    private Favorites() {
    }
    
    public final boolean addFavorite(final ComponentName componentName, final CharSequence charSequence, final ControlInfo controlInfo) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(charSequence, "structureName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "controlInfo");
        final List<ControlInfo> controlsForComponent = this.getControlsForComponent(componentName);
        boolean b = false;
        Label_0094: {
            if (!(controlsForComponent instanceof Collection) || !controlsForComponent.isEmpty()) {
                final Iterator<Object> iterator = controlsForComponent.iterator();
                while (iterator.hasNext()) {
                    if (Intrinsics.areEqual(iterator.next().getControlId(), controlInfo.getControlId())) {
                        b = true;
                        break Label_0094;
                    }
                }
            }
            b = false;
        }
        if (b) {
            return false;
        }
        final List list = (List)Favorites.favMap.get(componentName);
        StructureInfo structureInfo2 = null;
        Label_0200: {
            Label_0187: {
                if (list != null) {
                    while (true) {
                        for (final StructureInfo next : list) {
                            if (Intrinsics.areEqual(next.getStructure(), charSequence)) {
                                final StructureInfo structureInfo = next;
                                if (structureInfo != null) {
                                    structureInfo2 = structureInfo;
                                    break Label_0200;
                                }
                                break Label_0187;
                            }
                        }
                        StructureInfo next = null;
                        continue;
                    }
                }
            }
            structureInfo2 = new StructureInfo(componentName, charSequence, CollectionsKt.emptyList());
        }
        this.replaceControls(StructureInfo.copy$default(structureInfo2, null, null, CollectionsKt.plus(structureInfo2.getControls(), controlInfo), 3, null));
        return true;
    }
    
    public final void clear() {
        Favorites.favMap = MapsKt.emptyMap();
    }
    
    public final List<StructureInfo> getAllStructures() {
        final Map<ComponentName, ? extends List<StructureInfo>> favMap = Favorites.favMap;
        final ArrayList<Object> list = new ArrayList<Object>();
        final Iterator<Map.Entry<ComponentName, ? extends List<StructureInfo>>> iterator = favMap.entrySet().iterator();
        while (iterator.hasNext()) {
            CollectionsKt.addAll((Collection<? super Object>)list, (Iterable<?>)iterator.next().getValue());
        }
        return (List<StructureInfo>)list;
    }
    
    public final List<ControlInfo> getControlsForComponent(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        final List<StructureInfo> structuresForComponent = this.getStructuresForComponent(componentName);
        final ArrayList<Object> list = new ArrayList<Object>();
        final Iterator<Object> iterator = structuresForComponent.iterator();
        while (iterator.hasNext()) {
            CollectionsKt.addAll((Collection<? super Object>)list, (Iterable<?>)iterator.next().getControls());
        }
        return (List<ControlInfo>)list;
    }
    
    public final List<StructureInfo> getStructuresForComponent(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        List<StructureInfo> emptyList = (List<StructureInfo>)Favorites.favMap.get(componentName);
        if (emptyList == null) {
            emptyList = CollectionsKt.emptyList();
        }
        return emptyList;
    }
    
    public final void load(final List<StructureInfo> list) {
        Intrinsics.checkParameterIsNotNull(list, "structures");
        final LinkedHashMap<ComponentName, List<StructureInfo>> favMap = (LinkedHashMap<ComponentName, List<StructureInfo>>)new LinkedHashMap<Object, List<StructureInfo>>();
        for (final StructureInfo next : list) {
            final ComponentName componentName = next.getComponentName();
            List<StructureInfo> value;
            if ((value = favMap.get(componentName)) == null) {
                value = new ArrayList<StructureInfo>();
                favMap.put(componentName, value);
            }
            value.add(next);
        }
        Favorites.favMap = favMap;
    }
    
    public final void removeStructures(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        final Map<ComponentName, ? extends List<StructureInfo>> mutableMap = MapsKt.toMutableMap((Map<? extends ComponentName, ? extends List<StructureInfo>>)Favorites.favMap);
        mutableMap.remove(componentName);
        Favorites.favMap = mutableMap;
    }
    
    public final void replaceControls(final StructureInfo structureInfo) {
        Intrinsics.checkParameterIsNotNull(structureInfo, "updatedStructure");
        final Map<ComponentName, ArrayList<StructureInfo>> mutableMap = MapsKt.toMutableMap((Map<? extends ComponentName, ? extends ArrayList<StructureInfo>>)Favorites.favMap);
        final ArrayList<StructureInfo> list = new ArrayList<StructureInfo>();
        final ComponentName componentName = structureInfo.getComponentName();
        final Iterator<Object> iterator = this.getStructuresForComponent(componentName).iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final StructureInfo structureInfo2 = iterator.next();
            int n2 = n;
            StructureInfo structureInfo3 = structureInfo2;
            if (Intrinsics.areEqual(structureInfo2.getStructure(), structureInfo.getStructure())) {
                n2 = 1;
                structureInfo3 = structureInfo;
            }
            n = n2;
            if (!structureInfo3.getControls().isEmpty()) {
                list.add(structureInfo3);
                n = n2;
            }
        }
        if (n == 0 && !structureInfo.getControls().isEmpty()) {
            list.add(structureInfo);
        }
        mutableMap.put(componentName, list);
        Favorites.favMap = mutableMap;
    }
    
    public final boolean updateControls(final ComponentName componentName, final List<Control> list) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(list, "controls");
        final LinkedHashMap<Object, Control> linkedHashMap = new LinkedHashMap<Object, Control>(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault((Iterable<?>)list, 10)), 16));
        for (final T next : list) {
            linkedHashMap.put(((Control)next).getControlId(), (Control)next);
        }
        final LinkedHashMap<Object, Object> linkedHashMap2 = new LinkedHashMap<Object, Object>();
        final Iterator<StructureInfo> iterator2 = (Iterator<StructureInfo>)this.getStructuresForComponent(componentName).iterator();
        int n = 0;
        while (iterator2.hasNext()) {
            final StructureInfo structureInfo = iterator2.next();
            final Iterator<Object> iterator3 = structureInfo.getControls().iterator();
            int n2 = n;
            while (true) {
                n = n2;
                if (!iterator3.hasNext()) {
                    break;
                }
                final ControlInfo controlInfo = iterator3.next();
                final Control control = linkedHashMap.get(controlInfo.getControlId());
                Pair pair;
                if (control != null) {
                    ControlInfo copy$default = null;
                    Label_0285: {
                        if (!(Intrinsics.areEqual(control.getTitle(), controlInfo.getControlTitle()) ^ true) && !(Intrinsics.areEqual(control.getSubtitle(), controlInfo.getControlSubtitle()) ^ true)) {
                            copy$default = controlInfo;
                            if (control.getDeviceType() == controlInfo.getDeviceType()) {
                                break Label_0285;
                            }
                        }
                        final CharSequence title = control.getTitle();
                        Intrinsics.checkExpressionValueIsNotNull(title, "updatedControl.title");
                        final CharSequence subtitle = control.getSubtitle();
                        Intrinsics.checkExpressionValueIsNotNull(subtitle, "updatedControl.subtitle");
                        copy$default = ControlInfo.copy$default(controlInfo, null, title, subtitle, control.getDeviceType(), 1, null);
                        n2 = 1;
                    }
                    CharSequence structure = control.getStructure();
                    if (structure == null) {
                        structure = "";
                    }
                    if (Intrinsics.areEqual(structureInfo.getStructure(), structure) ^ true) {
                        n2 = 1;
                    }
                    pair = new Pair<CharSequence, ControlInfo>(structure, copy$default);
                }
                else {
                    pair = new Pair<CharSequence, ControlInfo>(structureInfo.getStructure(), controlInfo);
                }
                final CharSequence charSequence = pair.component1();
                final ControlInfo controlInfo2 = pair.component2();
                List<ControlInfo> value;
                if ((value = linkedHashMap2.get(charSequence)) == null) {
                    value = new ArrayList<ControlInfo>();
                    linkedHashMap2.put(charSequence, value);
                }
                value.add(controlInfo2);
            }
        }
        if (n == 0) {
            return false;
        }
        final ArrayList list2 = new ArrayList<StructureInfo>(linkedHashMap2.size());
        for (final Map.Entry<CharSequence, List<ControlInfo>> entry : linkedHashMap2.entrySet()) {
            list2.add(new StructureInfo(componentName, entry.getKey(), entry.getValue()));
        }
        final Map<ComponentName, ArrayList<StructureInfo>> mutableMap = MapsKt.toMutableMap((Map<? extends ComponentName, ? extends ArrayList<StructureInfo>>)Favorites.favMap);
        mutableMap.put(componentName, (ArrayList<StructureInfo>)list2);
        Favorites.favMap = mutableMap;
        return true;
    }
}
