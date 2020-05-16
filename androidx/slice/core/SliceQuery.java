// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.core;

import java.util.ArrayDeque;
import android.text.TextUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.ArrayList;
import java.util.List;
import androidx.slice.Slice;
import androidx.slice.SliceItem;

public class SliceQuery
{
    static boolean checkFormat(final SliceItem sliceItem, final String s) {
        return s == null || s.equals(sliceItem.getFormat());
    }
    
    static boolean checkSubtype(final SliceItem sliceItem, final String s) {
        return s == null || s.equals(sliceItem.getSubType());
    }
    
    public static SliceItem find(final Slice slice, final String s) {
        return find(slice, s, null, (String[])null);
    }
    
    public static SliceItem find(final Slice slice, final String s, final String s2, final String s3) {
        return find(slice, s, new String[] { s2 }, new String[] { s3 });
    }
    
    public static SliceItem find(final Slice slice, final String s, final String[] array, final String[] array2) {
        if (slice == null) {
            return null;
        }
        return findSliceItem(toQueue(slice), (Filter<SliceItem>)new Filter<SliceItem>() {
            public boolean filter(final SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, s) && SliceQuery.hasHints(sliceItem, array) && !SliceQuery.hasAnyHints(sliceItem, array2);
            }
        });
    }
    
    public static SliceItem find(final SliceItem sliceItem, final String s) {
        return find(sliceItem, s, null, (String[])null);
    }
    
    public static SliceItem find(final SliceItem sliceItem, final String s, final String s2, final String s3) {
        return find(sliceItem, s, new String[] { s2 }, new String[] { s3 });
    }
    
    public static SliceItem find(final SliceItem sliceItem, final String s, final String[] array, final String[] array2) {
        if (sliceItem == null) {
            return null;
        }
        return findSliceItem(toQueue(sliceItem), (Filter<SliceItem>)new Filter<SliceItem>() {
            public boolean filter(final SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, s) && SliceQuery.hasHints(sliceItem, array) && !SliceQuery.hasAnyHints(sliceItem, array2);
            }
        });
    }
    
    public static List<SliceItem> findAll(final Slice slice, final String s, final String[] array, final String[] array2) {
        final ArrayList<SliceItem> list = new ArrayList<SliceItem>();
        findAll(toQueue(slice), (Filter<SliceItem>)new Filter<SliceItem>() {
            public boolean filter(final SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, s) && SliceQuery.hasHints(sliceItem, array) && !SliceQuery.hasAnyHints(sliceItem, array2);
            }
        }, list);
        return list;
    }
    
    public static List<SliceItem> findAll(final SliceItem sliceItem, final String s) {
        return findAll(sliceItem, s, null, (String[])null);
    }
    
    public static List<SliceItem> findAll(final SliceItem sliceItem, final String s, final String s2, final String s3) {
        return findAll(sliceItem, s, new String[] { s2 }, new String[] { s3 });
    }
    
    public static List<SliceItem> findAll(final SliceItem sliceItem, final String s, final String[] array, final String[] array2) {
        final ArrayList<SliceItem> list = new ArrayList<SliceItem>();
        findAll(toQueue(sliceItem), (Filter<SliceItem>)new Filter<SliceItem>() {
            public boolean filter(final SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, s) && SliceQuery.hasHints(sliceItem, array) && !SliceQuery.hasAnyHints(sliceItem, array2);
            }
        }, list);
        return list;
    }
    
    private static void findAll(final Deque<SliceItem> c, final Filter<SliceItem> filter, final List<SliceItem> list) {
        while (!c.isEmpty()) {
            final SliceItem sliceItem = c.poll();
            if (filter.filter(sliceItem)) {
                list.add(sliceItem);
            }
            if ("slice".equals(sliceItem.getFormat()) || "action".equals(sliceItem.getFormat())) {
                Collections.addAll(c, sliceItem.getSlice().getItemArray());
            }
        }
    }
    
    private static SliceItem findSliceItem(final Deque<SliceItem> c, final Filter<SliceItem> filter) {
        while (!c.isEmpty()) {
            final SliceItem sliceItem = c.poll();
            if (filter.filter(sliceItem)) {
                return sliceItem;
            }
            if (!"slice".equals(sliceItem.getFormat()) && !"action".equals(sliceItem.getFormat())) {
                continue;
            }
            Collections.addAll(c, sliceItem.getSlice().getItemArray());
        }
        return null;
    }
    
    public static SliceItem findSubtype(final Slice slice, final String s, final String s2) {
        if (slice == null) {
            return null;
        }
        return findSliceItem(toQueue(slice), (Filter<SliceItem>)new Filter<SliceItem>() {
            public boolean filter(final SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, s) && SliceQuery.checkSubtype(sliceItem, s2);
            }
        });
    }
    
    public static SliceItem findSubtype(final SliceItem sliceItem, final String s, final String s2) {
        if (sliceItem == null) {
            return null;
        }
        return findSliceItem(toQueue(sliceItem), (Filter<SliceItem>)new Filter<SliceItem>() {
            public boolean filter(final SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, s) && SliceQuery.checkSubtype(sliceItem, s2);
            }
        });
    }
    
    public static SliceItem findTopLevelItem(final Slice slice, final String s, final String s2, final String[] array, final String[] array2) {
        final SliceItem[] itemArray = slice.getItemArray();
        for (int i = 0; i < itemArray.length; ++i) {
            final SliceItem sliceItem = itemArray[i];
            if (checkFormat(sliceItem, s) && checkSubtype(sliceItem, s2) && hasHints(sliceItem, array) && !hasAnyHints(sliceItem, array2)) {
                return sliceItem;
            }
        }
        return null;
    }
    
    public static boolean hasAnyHints(final SliceItem sliceItem, final String... array) {
        if (array == null) {
            return false;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            if (sliceItem.hasHint(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasHints(final SliceItem sliceItem, final String... array) {
        if (array == null) {
            return true;
        }
        for (final String s : array) {
            if (!TextUtils.isEmpty((CharSequence)s) && !sliceItem.hasHint(s)) {
                return false;
            }
        }
        return true;
    }
    
    private static Deque<SliceItem> toQueue(final Slice slice) {
        final ArrayDeque<Object> c = new ArrayDeque<Object>();
        Collections.addAll(c, slice.getItemArray());
        return (Deque<SliceItem>)c;
    }
    
    private static Deque<SliceItem> toQueue(final SliceItem sliceItem) {
        final ArrayDeque<SliceItem> arrayDeque = new ArrayDeque<SliceItem>();
        arrayDeque.add(sliceItem);
        return arrayDeque;
    }
    
    private interface Filter<T>
    {
        boolean filter(final T p0);
    }
}
