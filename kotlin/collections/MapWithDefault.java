// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.Map;

interface MapWithDefault<K, V> extends Map<K, V>, KMappedMarker
{
    Map<K, V> getMap();
    
    V getOrImplicitDefault(final K p0);
}
