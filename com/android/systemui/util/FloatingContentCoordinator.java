// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import kotlin.Lazy;
import kotlin.jvm.functions.Function0;
import kotlin.LazyKt;
import kotlin.Pair;
import java.util.ArrayList;
import kotlin.jvm.internal.Ref$ObjectRef;
import java.util.List;
import java.util.Comparator;
import java.util.Collection;
import kotlin.jvm.internal.PropertyReference0;
import kotlin.reflect.KDeclarationContainer;
import kotlin.jvm.internal.PropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import android.util.Log;
import java.util.Iterator;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import java.util.LinkedHashMap;
import java.util.HashMap;
import android.graphics.Rect;
import java.util.Map;

public final class FloatingContentCoordinator
{
    public static final Companion Companion;
    private final Map<FloatingContent, Rect> allContentBounds;
    private boolean currentlyResolvingConflicts;
    
    static {
        Companion = new Companion(null);
    }
    
    public FloatingContentCoordinator() {
        this.allContentBounds = new HashMap<FloatingContent, Rect>();
    }
    
    private final void maybeMoveConflictingContent(FloatingContent floatingContent) {
        this.currentlyResolvingConflicts = true;
        final Rect value = this.allContentBounds.get(floatingContent);
        if (value != null) {
            final Rect rect = value;
            final Map<FloatingContent, Rect> allContentBounds = this.allContentBounds;
            final LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<Object, Object>();
            final Iterator<Map.Entry<FloatingContent, Rect>> iterator = allContentBounds.entrySet().iterator();
            while (true) {
                final boolean hasNext = iterator.hasNext();
                final boolean b = false;
                if (!hasNext) {
                    break;
                }
                final Map.Entry<FloatingContent, Rect> entry = iterator.next();
                final FloatingContent floatingContent2 = entry.getKey();
                final Rect rect2 = entry.getValue();
                int n = b ? 1 : 0;
                if (Intrinsics.areEqual(floatingContent2, floatingContent) ^ true) {
                    n = (b ? 1 : 0);
                    if (Rect.intersects(rect, rect2)) {
                        n = 1;
                    }
                }
                if (n == 0) {
                    continue;
                }
                linkedHashMap.put(entry.getKey(), entry.getValue());
            }
            for (final Map.Entry<FloatingContent, V> entry2 : linkedHashMap.entrySet()) {
                floatingContent = entry2.getKey();
                floatingContent.moveToBounds(floatingContent.calculateNewBoundsOnOverlap(rect, CollectionsKt.minus(CollectionsKt.minus(this.allContentBounds.values(), (Rect)entry2.getValue()), rect)));
                this.allContentBounds.put(floatingContent, floatingContent.getFloatingBoundsOnScreen());
            }
            this.currentlyResolvingConflicts = false;
            return;
        }
        Intrinsics.throwNpe();
        throw null;
    }
    
    private final void updateContentBounds() {
        for (final FloatingContent floatingContent : this.allContentBounds.keySet()) {
            this.allContentBounds.put(floatingContent, floatingContent.getFloatingBoundsOnScreen());
        }
    }
    
    public final void onContentAdded(final FloatingContent floatingContent) {
        Intrinsics.checkParameterIsNotNull(floatingContent, "newContent");
        this.updateContentBounds();
        this.allContentBounds.put(floatingContent, floatingContent.getFloatingBoundsOnScreen());
        this.maybeMoveConflictingContent(floatingContent);
    }
    
    public final void onContentMoved(final FloatingContent floatingContent) {
        Intrinsics.checkParameterIsNotNull(floatingContent, "content");
        if (this.currentlyResolvingConflicts) {
            return;
        }
        if (!this.allContentBounds.containsKey(floatingContent)) {
            Log.wtf("FloatingCoordinator", "Received onContentMoved call before onContentAdded! This should never happen.");
            return;
        }
        this.updateContentBounds();
        this.maybeMoveConflictingContent(floatingContent);
    }
    
    public final void onContentRemoved(final FloatingContent floatingContent) {
        Intrinsics.checkParameterIsNotNull(floatingContent, "removedContent");
        this.allContentBounds.remove(floatingContent);
    }
    
    public static final class Companion
    {
        static final /* synthetic */ KProperty[] $$delegatedProperties;
        
        static {
            final PropertyReference0Impl propertyReference0Impl = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(Companion.class), "newContentBoundsAbove", "<v#0>");
            Reflection.property0(propertyReference0Impl);
            final PropertyReference0Impl propertyReference0Impl2 = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(Companion.class), "newContentBoundsBelow", "<v#1>");
            Reflection.property0(propertyReference0Impl2);
            final PropertyReference0Impl propertyReference0Impl3 = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(Companion.class), "positionAboveInBounds", "<v#2>");
            Reflection.property0(propertyReference0Impl3);
            final PropertyReference0Impl propertyReference0Impl4 = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(Companion.class), "positionBelowInBounds", "<v#3>");
            Reflection.property0(propertyReference0Impl4);
            $$delegatedProperties = new KProperty[] { propertyReference0Impl, propertyReference0Impl2, propertyReference0Impl3, propertyReference0Impl4 };
        }
        
        private Companion() {
        }
        
        private final boolean rectsIntersectVertically(final Rect rect, final Rect rect2) {
            final int left = rect.left;
            if (left < rect2.left || left > rect2.right) {
                final int right = rect.right;
                if (right > rect2.right || right < rect2.left) {
                    return false;
                }
            }
            return true;
        }
        
        public final Rect findAreaForContentAboveOrBelow(final Rect rect, final Collection<Rect> collection, final boolean b) {
            Intrinsics.checkParameterIsNotNull(rect, "contentRect");
            Intrinsics.checkParameterIsNotNull(collection, "exclusionRects");
            final List<Rect> sortedWith = CollectionsKt.sortedWith((Iterable<? extends Rect>)collection, (Comparator<? super Rect>)new FloatingContentCoordinator$Companion$findAreaForContentAboveOrBelow$$inlined$sortedBy.FloatingContentCoordinator$Companion$findAreaForContentAboveOrBelow$$inlined$sortedBy$1(b));
            final Rect rect2 = new Rect(rect);
            for (final Rect rect3 : sortedWith) {
                if (!Rect.intersects(rect2, rect3)) {
                    break;
                }
                int height;
                if (b) {
                    height = -rect.height();
                }
                else {
                    height = rect3.height();
                }
                rect2.offsetTo(rect2.left, rect3.top + height);
            }
            return rect2;
        }
        
        public final Rect findAreaForContentVertically(final Rect rect, final Rect rect2, final Collection<Rect> collection, final Rect rect3) {
            Intrinsics.checkParameterIsNotNull(rect, "contentRect");
            Intrinsics.checkParameterIsNotNull(rect2, "newlyOverlappingRect");
            Intrinsics.checkParameterIsNotNull(collection, "exclusionRects");
            Intrinsics.checkParameterIsNotNull(rect3, "allowedBounds");
            final int centerY = rect2.centerY();
            final int centerY2 = rect.centerY();
            final boolean b = true;
            final boolean b2 = centerY < centerY2;
            final Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            final Ref$ObjectRef ref$ObjectRef2 = new Ref$ObjectRef();
            final ArrayList<Rect> list = new ArrayList<Rect>();
            for (final Rect next : collection) {
                if (FloatingContentCoordinator.Companion.rectsIntersectVertically(next, rect)) {
                    list.add(next);
                }
            }
            final ArrayList<Rect> list2 = new ArrayList<Rect>();
            final ArrayList<Rect> list3 = new ArrayList<Rect>();
            for (final Rect next2 : list) {
                if (next2.top < rect.top) {
                    list2.add(next2);
                }
                else {
                    list3.add(next2);
                }
            }
            final Pair pair = new Pair<ArrayList<Rect>, ArrayList<Rect>>(list2, list3);
            ref$ObjectRef.element = (T)pair.component1();
            ref$ObjectRef2.element = (T)pair.component2();
            final Lazy<Object> lazy = LazyKt.lazy((Function0<?>)new FloatingContentCoordinator$Companion$findAreaForContentVertically$newContentBoundsAbove.FloatingContentCoordinator$Companion$findAreaForContentVertically$newContentBoundsAbove$2(rect, ref$ObjectRef, rect2));
            final KProperty kProperty = Companion.$$delegatedProperties[0];
            final Lazy<Object> lazy2 = LazyKt.lazy((Function0<?>)new FloatingContentCoordinator$Companion$findAreaForContentVertically$newContentBoundsBelow.FloatingContentCoordinator$Companion$findAreaForContentVertically$newContentBoundsBelow$2(rect, ref$ObjectRef2, rect2));
            final KProperty kProperty2 = Companion.$$delegatedProperties[1];
            final Lazy<Object> lazy3 = LazyKt.lazy((Function0<?>)new FloatingContentCoordinator$Companion$findAreaForContentVertically$positionAboveInBounds.FloatingContentCoordinator$Companion$findAreaForContentVertically$positionAboveInBounds$2(rect3, (Lazy)lazy, kProperty));
            final KProperty kProperty3 = Companion.$$delegatedProperties[2];
            final Lazy<Object> lazy4 = LazyKt.lazy((Function0<?>)new FloatingContentCoordinator$Companion$findAreaForContentVertically$positionBelowInBounds.FloatingContentCoordinator$Companion$findAreaForContentVertically$positionBelowInBounds$2(rect3, (Lazy)lazy2, kProperty2));
            final KProperty kProperty4 = Companion.$$delegatedProperties[3];
            int n = 0;
            Label_0415: {
                if (b2) {
                    n = (b ? 1 : 0);
                    if (lazy4.getValue()) {
                        break Label_0415;
                    }
                }
                if (!b2 && !lazy3.getValue()) {
                    n = (b ? 1 : 0);
                }
                else {
                    n = 0;
                }
            }
            Rect rect4;
            if (n != 0) {
                rect4 = lazy2.getValue();
            }
            else {
                rect4 = lazy.getValue();
            }
            return rect4;
        }
    }
    
    public interface FloatingContent
    {
        default Rect calculateNewBoundsOnOverlap(final Rect rect, final List<Rect> list) {
            Intrinsics.checkParameterIsNotNull(rect, "overlappingContentBounds");
            Intrinsics.checkParameterIsNotNull(list, "otherContentBounds");
            return FloatingContentCoordinator.Companion.findAreaForContentVertically(this.getFloatingBoundsOnScreen(), rect, list, this.getAllowedFloatingBoundsRegion());
        }
        
        Rect getAllowedFloatingBoundsRegion();
        
        Rect getFloatingBoundsOnScreen();
        
        void moveToBounds(final Rect p0);
    }
}
