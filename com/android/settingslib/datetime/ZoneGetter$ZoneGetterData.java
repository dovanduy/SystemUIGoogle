// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.datetime;

import libcore.timezone.CountryTimeZones;
import libcore.timezone.TimeZoneFinder;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import libcore.timezone.CountryTimeZones$TimeZoneMapping;
import java.util.List;

public final class ZoneGetter$ZoneGetterData
{
    private static List<String> extractTimeZoneIds(final List<CountryTimeZones$TimeZoneMapping> list) {
        final ArrayList<String> list2 = new ArrayList<String>(list.size());
        final Iterator<CountryTimeZones$TimeZoneMapping> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(iterator.next().getTimeZoneId());
        }
        return (List<String>)Collections.unmodifiableList((List<?>)list2);
    }
    
    public List<String> lookupTimeZoneIdsByCountry(final String s) {
        final CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(s);
        if (lookupCountryTimeZones == null) {
            return null;
        }
        return extractTimeZoneIds(lookupCountryTimeZones.getTimeZoneMappings());
    }
}
