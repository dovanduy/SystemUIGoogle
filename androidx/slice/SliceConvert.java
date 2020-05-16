// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.content.res.Resources$NotFoundException;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import android.content.Context;
import java.util.Iterator;
import androidx.collection.ArraySet;
import java.util.Set;
import java.util.List;
import android.app.slice.Slice$Builder;

public class SliceConvert
{
    public static android.app.slice.Slice unwrap(final Slice slice) {
        if (slice != null && slice.getUri() != null) {
            final Slice$Builder slice$Builder = new Slice$Builder(slice.getUri(), unwrap(slice.getSpec()));
            slice$Builder.addHints((List)slice.getHints());
            for (final SliceItem sliceItem : slice.getItemArray()) {
                final String format = sliceItem.getFormat();
                switch (format) {
                    case "long": {
                        slice$Builder.addLong(sliceItem.getLong(), sliceItem.getSubType(), (List)sliceItem.getHints());
                        break;
                    }
                    case "int": {
                        slice$Builder.addInt(sliceItem.getInt(), sliceItem.getSubType(), (List)sliceItem.getHints());
                        break;
                    }
                    case "text": {
                        slice$Builder.addText(sliceItem.getText(), sliceItem.getSubType(), (List)sliceItem.getHints());
                        break;
                    }
                    case "action": {
                        slice$Builder.addAction(sliceItem.getAction(), unwrap(sliceItem.getSlice()), sliceItem.getSubType());
                        break;
                    }
                    case "input": {
                        slice$Builder.addRemoteInput(sliceItem.getRemoteInput(), sliceItem.getSubType(), (List)sliceItem.getHints());
                        break;
                    }
                    case "image": {
                        slice$Builder.addIcon(sliceItem.getIcon().toIcon(), sliceItem.getSubType(), (List)sliceItem.getHints());
                        break;
                    }
                    case "slice": {
                        slice$Builder.addSubSlice(unwrap(sliceItem.getSlice()), sliceItem.getSubType());
                        break;
                    }
                }
            }
            return slice$Builder.build();
        }
        return null;
    }
    
    private static android.app.slice.SliceSpec unwrap(final SliceSpec sliceSpec) {
        if (sliceSpec == null) {
            return null;
        }
        return new android.app.slice.SliceSpec(sliceSpec.getType(), sliceSpec.getRevision());
    }
    
    static Set<android.app.slice.SliceSpec> unwrap(final Set<SliceSpec> set) {
        final ArraySet<android.app.slice.SliceSpec> set2 = new ArraySet<android.app.slice.SliceSpec>();
        if (set != null) {
            final Iterator<SliceSpec> iterator = set.iterator();
            while (iterator.hasNext()) {
                set2.add(unwrap(iterator.next()));
            }
        }
        return set2;
    }
    
    public static Slice wrap(android.app.slice.Slice iterator, final Context context) {
        if (iterator != null && iterator.getUri() != null) {
            final Slice.Builder builder = new Slice.Builder(iterator.getUri());
            builder.addHints(iterator.getHints());
            builder.setSpec(wrap(iterator.getSpec()));
            iterator = (android.app.slice.Slice)iterator.getItems().iterator();
            while (((Iterator)iterator).hasNext()) {
                final android.app.slice.SliceItem sliceItem = ((Iterator<android.app.slice.SliceItem>)iterator).next();
                final String format = sliceItem.getFormat();
                switch (format) {
                    default: {
                        continue;
                    }
                    case "long": {
                        builder.addLong(sliceItem.getLong(), sliceItem.getSubType(), sliceItem.getHints());
                        continue;
                    }
                    case "int": {
                        builder.addInt(sliceItem.getInt(), sliceItem.getSubType(), sliceItem.getHints());
                        continue;
                    }
                    case "text": {
                        builder.addText(sliceItem.getText(), sliceItem.getSubType(), sliceItem.getHints());
                        continue;
                    }
                    case "action": {
                        builder.addAction(sliceItem.getAction(), wrap(sliceItem.getSlice(), context), sliceItem.getSubType());
                        continue;
                    }
                    case "input": {
                        builder.addRemoteInput(sliceItem.getRemoteInput(), sliceItem.getSubType(), sliceItem.getHints());
                        continue;
                    }
                    case "image": {
                        try {
                            builder.addIcon(IconCompat.createFromIcon(context, sliceItem.getIcon()), sliceItem.getSubType(), sliceItem.getHints());
                        }
                        catch (Resources$NotFoundException ex) {
                            Log.w("SliceConvert", "The icon resource isn't available.", (Throwable)ex);
                        }
                        catch (IllegalArgumentException ex2) {
                            Log.w("SliceConvert", "The icon resource isn't available.", (Throwable)ex2);
                        }
                        continue;
                    }
                    case "slice": {
                        builder.addSubSlice(wrap(sliceItem.getSlice(), context), sliceItem.getSubType());
                        continue;
                    }
                }
            }
            return builder.build();
        }
        return null;
    }
    
    private static SliceSpec wrap(final android.app.slice.SliceSpec sliceSpec) {
        if (sliceSpec == null) {
            return null;
        }
        return new SliceSpec(sliceSpec.getType(), sliceSpec.getRevision());
    }
    
    public static Set<SliceSpec> wrap(final Set<android.app.slice.SliceSpec> set) {
        final ArraySet<SliceSpec> set2 = new ArraySet<SliceSpec>();
        if (set != null) {
            final Iterator<android.app.slice.SliceSpec> iterator = set.iterator();
            while (iterator.hasNext()) {
                set2.add(wrap(iterator.next()));
            }
        }
        return set2;
    }
}
