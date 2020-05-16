// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import android.graphics.fonts.FontFamily;
import java.io.InputStream;
import android.os.ParcelFileDescriptor;
import android.content.ContentResolver;
import androidx.core.provider.FontsContractCompat;
import android.os.CancellationSignal;
import android.graphics.fonts.Font;
import java.io.IOException;
import android.graphics.fonts.FontStyle;
import android.graphics.Typeface$CustomFallbackBuilder;
import android.graphics.fonts.FontFamily$Builder;
import android.graphics.fonts.Font$Builder;
import android.graphics.Typeface;
import android.content.res.Resources;
import androidx.core.content.res.FontResourcesParserCompat;
import android.content.Context;

public class TypefaceCompatApi29Impl extends TypefaceCompatBaseImpl
{
    @Override
    public Typeface createFromFontFamilyFilesResourceEntry(Context context, final FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, final Resources resources, final int n) {
        final FontResourcesParserCompat.FontFileResourceEntry[] entries = fontFamilyFilesResourceEntry.getEntries();
        final int length = entries.length;
        final int n2 = 0;
        context = null;
        int n3 = 0;
    Label_0126_Outer:
        while (true) {
            int slant = 1;
            Label_0132: {
                if (n3 >= length) {
                    break Label_0132;
                }
                final FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry = entries[n3];
            Block_8_Outer:
                while (true) {
                    try {
                        final Font$Builder setWeight = new Font$Builder(resources, fontFileResourceEntry.getResourceId()).setWeight(fontFileResourceEntry.getWeight());
                        if (!fontFileResourceEntry.isItalic()) {
                            slant = 0;
                        }
                        final Font build = setWeight.setSlant(slant).setTtcIndex(fontFileResourceEntry.getTtcIndex()).setFontVariationSettings(fontFileResourceEntry.getVariationSettings()).build();
                        if (context == null) {
                            context = (Context)new FontFamily$Builder(build);
                        }
                        else {
                            ((FontFamily$Builder)context).addFont(build);
                        }
                        ++n3;
                        continue Label_0126_Outer;
                        // iftrue(Label_0153:, n & 0x1 == 0x0)
                        // iftrue(Label_0172:, n & 0x2 == 0x0)
                        while (true) {
                            Label_0158: {
                                while (true) {
                                    n3 = 700;
                                    break Label_0158;
                                    Label_0138: {
                                        continue Block_8_Outer;
                                    }
                                }
                                Label_0172: {
                                    final int n4;
                                    return new Typeface$CustomFallbackBuilder(((FontFamily$Builder)context).build()).setStyle(new FontStyle(n3, n4)).build();
                                }
                                final int n4 = 1;
                                return new Typeface$CustomFallbackBuilder(((FontFamily$Builder)context).build()).setStyle(new FontStyle(n3, n4)).build();
                                Label_0153:
                                n3 = 400;
                            }
                            final int n4 = n2;
                            continue;
                        }
                        // iftrue(Label_0138:, context != null)
                        return null;
                    }
                    catch (IOException ex) {
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public Typeface createFromFontInfo(Context context, CancellationSignal style, final FontsContractCompat.FontInfo[] array, final int n) {
        final ContentResolver contentResolver = context.getContentResolver();
        final int length = array.length;
        final int n2 = 0;
        context = null;
        int n3 = 0;
    Label_0209_Outer:
        while (true) {
            int slant = 1;
            Label_0218: {
                if (n3 >= length) {
                    break Label_0218;
                }
                final FontsContractCompat.FontInfo fontInfo = array[n3];
                Object o = context;
                while (true) {
                    try {
                        final ParcelFileDescriptor openFileDescriptor = contentResolver.openFileDescriptor(fontInfo.getUri(), "r", style);
                        Label_0065: {
                            if (openFileDescriptor != null) {
                                try {
                                    o = new Font$Builder(openFileDescriptor);
                                    o = ((Font$Builder)o).setWeight(fontInfo.getWeight());
                                    if (!fontInfo.isItalic()) {
                                        slant = 0;
                                    }
                                    o = ((Font$Builder)o).setSlant(slant).setTtcIndex(fontInfo.getTtcIndex()).build();
                                    if (context == null) {
                                        o = (context = (Context)new FontFamily$Builder((Font)o));
                                    }
                                    else {
                                        ((FontFamily$Builder)context).addFont((Font)o);
                                    }
                                    o = context;
                                    if (openFileDescriptor != null) {
                                        break Label_0065;
                                    }
                                }
                                finally {
                                    if (openFileDescriptor != null) {
                                        try {
                                            openFileDescriptor.close();
                                        }
                                        finally {
                                            o = context;
                                            final Throwable t;
                                            t.addSuppressed((Throwable)openFileDescriptor);
                                        }
                                    }
                                    o = context;
                                }
                                break Label_0209;
                            }
                            o = context;
                            if (openFileDescriptor == null) {
                                break Label_0209;
                            }
                        }
                        o = context;
                        openFileDescriptor.close();
                        o = context;
                        ++n3;
                        context = (Context)o;
                        continue Label_0209_Outer;
                        // iftrue(Label_0224:, context != null)
                        return null;
                        Label_0239: {
                            n3 = 400;
                        }
                        // iftrue(Label_0239:, n & 0x1 == 0x0)
                        // iftrue(Label_0258:, n & 0x2 == 0x0)
                        int n4 = 0;
                    Label_0258:
                        while (true) {
                            Label_0244: {
                                break Label_0244;
                                Label_0224:
                                Block_7: {
                                    break Block_7;
                                    n4 = 1;
                                    break Label_0258;
                                }
                                n3 = 700;
                            }
                            n4 = n2;
                            continue;
                        }
                        style = (CancellationSignal)new FontStyle(n3, n4);
                        return new Typeface$CustomFallbackBuilder(((FontFamily$Builder)context).build()).setStyle((FontStyle)style).build();
                    }
                    catch (IOException ex) {
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    protected Typeface createFromInputStream(final Context context, final InputStream inputStream) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }
    
    @Override
    public Typeface createFromResourcesFontFile(final Context context, final Resources resources, int n, final String s, int n2) {
        try {
            final FontFamily build = new FontFamily$Builder(new Font$Builder(resources, n).build()).build();
            if ((n2 & 0x1) != 0x0) {
                n = 700;
            }
            else {
                n = 400;
            }
            if ((n2 & 0x2) != 0x0) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            return new Typeface$CustomFallbackBuilder(build).setStyle(new FontStyle(n, n2)).build();
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    @Override
    protected FontsContractCompat.FontInfo findBestInfo(final FontsContractCompat.FontInfo[] array, final int n) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }
}
