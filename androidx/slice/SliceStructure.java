// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import java.util.Iterator;
import android.net.Uri;

public class SliceStructure
{
    private final String mStructure;
    private final Uri mUri;
    
    public SliceStructure(final Slice slice) {
        final StringBuilder sb = new StringBuilder();
        getStructure(slice, sb);
        this.mStructure = sb.toString();
        this.mUri = slice.getUri();
    }
    
    public SliceStructure(final SliceItem sliceItem) {
        final StringBuilder sb = new StringBuilder();
        getStructure(sliceItem, sb);
        this.mStructure = sb.toString();
        if (!"action".equals(sliceItem.getFormat()) && !"slice".equals(sliceItem.getFormat())) {
            this.mUri = null;
        }
        else {
            this.mUri = sliceItem.getSlice().getUri();
        }
    }
    
    private static void getStructure(final Slice slice, final StringBuilder sb) {
        sb.append("s{");
        final Iterator<SliceItem> iterator = slice.getItems().iterator();
        while (iterator.hasNext()) {
            getStructure(iterator.next(), sb);
        }
        sb.append("}");
    }
    
    private static void getStructure(final SliceItem sliceItem, final StringBuilder sb) {
        final String format = sliceItem.getFormat();
        int n = 0;
        Label_0203: {
            switch (format.hashCode()) {
                case 109526418: {
                    if (format.equals("slice")) {
                        n = 0;
                        break Label_0203;
                    }
                    break;
                }
                case 100358090: {
                    if (format.equals("input")) {
                        n = 6;
                        break Label_0203;
                    }
                    break;
                }
                case 100313435: {
                    if (format.equals("image")) {
                        n = 3;
                        break Label_0203;
                    }
                    break;
                }
                case 3556653: {
                    if (format.equals("text")) {
                        n = 2;
                        break Label_0203;
                    }
                    break;
                }
                case 3327612: {
                    if (format.equals("long")) {
                        n = 5;
                        break Label_0203;
                    }
                    break;
                }
                case 104431: {
                    if (format.equals("int")) {
                        n = 4;
                        break Label_0203;
                    }
                    break;
                }
                case -1377881982: {
                    if (format.equals("bundle")) {
                        n = 7;
                        break Label_0203;
                    }
                    break;
                }
                case -1422950858: {
                    if (format.equals("action")) {
                        n = 1;
                        break Label_0203;
                    }
                    break;
                }
            }
            n = -1;
        }
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n == 3) {
                        sb.append('i');
                    }
                }
                else {
                    sb.append('t');
                }
            }
            else {
                sb.append('a');
                if ("range".equals(sliceItem.getSubType())) {
                    sb.append('r');
                }
                getStructure(sliceItem.getSlice(), sb);
            }
        }
        else {
            getStructure(sliceItem.getSlice(), sb);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof SliceStructure && this.mStructure.equals(((SliceStructure)o).mStructure);
    }
    
    public Uri getUri() {
        return this.mUri;
    }
    
    @Override
    public int hashCode() {
        return this.mStructure.hashCode();
    }
}
