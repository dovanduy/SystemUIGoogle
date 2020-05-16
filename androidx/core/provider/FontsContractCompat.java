// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.provider;

import android.database.Cursor;
import androidx.core.util.Preconditions;
import androidx.core.graphics.TypefaceCompatUtil;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import android.content.pm.PackageManager;
import java.util.concurrent.Callable;
import android.os.Handler;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;
import android.content.ContentResolver;
import android.net.Uri;
import android.content.ContentUris;
import android.os.Build$VERSION;
import android.net.Uri$Builder;
import androidx.core.content.res.FontResourcesParserCompat;
import android.content.res.Resources;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.os.CancellationSignal;
import android.content.Context;
import java.util.Arrays;
import java.util.List;
import android.content.pm.Signature;
import android.graphics.Typeface;
import androidx.collection.LruCache;
import java.util.ArrayList;
import androidx.collection.SimpleArrayMap;
import java.util.Comparator;

public class FontsContractCompat
{
    private static final SelfDestructiveThread sBackgroundThread;
    private static final Comparator<byte[]> sByteArrayComparator;
    static final Object sLock;
    static final SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>>> sPendingReplies;
    static final LruCache<String, Typeface> sTypefaceCache;
    
    static {
        sTypefaceCache = new LruCache<String, Typeface>(16);
        sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
        sLock = new Object();
        sPendingReplies = new SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>>>();
        sByteArrayComparator = new Comparator<byte[]>() {
            @Override
            public int compare(final byte[] array, final byte[] array2) {
                if (array.length == array2.length) {
                    for (int i = 0; i < array.length; ++i) {
                        if (array[i] != array2[i]) {
                            final int length = array[i];
                            final int length2 = array2[i];
                            return length - length2;
                        }
                    }
                    return 0;
                }
                final int length = array.length;
                final int length2 = array2.length;
                return length - length2;
            }
        };
    }
    
    private static List<byte[]> convertToByteArrayList(final Signature[] array) {
        final ArrayList<byte[]> list = new ArrayList<byte[]>();
        for (int i = 0; i < array.length; ++i) {
            list.add(array[i].toByteArray());
        }
        return list;
    }
    
    private static boolean equalsByteArrayList(final List<byte[]> list, final List<byte[]> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
            if (!Arrays.equals(list.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static FontFamilyResult fetchFonts(final Context context, final CancellationSignal cancellationSignal, final FontRequest fontRequest) throws PackageManager$NameNotFoundException {
        final ProviderInfo provider = getProvider(context.getPackageManager(), fontRequest, context.getResources());
        if (provider == null) {
            return new FontFamilyResult(1, null);
        }
        return new FontFamilyResult(0, getFontFromProvider(context, fontRequest, provider.authority, cancellationSignal));
    }
    
    private static List<List<byte[]>> getCertificates(final FontRequest fontRequest, final Resources resources) {
        if (fontRequest.getCertificates() != null) {
            return fontRequest.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(resources, fontRequest.getCertificatesArrayResId());
    }
    
    static FontInfo[] getFontFromProvider(final Context context, final FontRequest fontRequest, String s, final CancellationSignal cancellationSignal) {
        final ArrayList<Object> list = new ArrayList<Object>();
        final Uri build = new Uri$Builder().scheme("content").authority(s).build();
        final Uri build2 = new Uri$Builder().scheme("content").authority(s).appendPath("file").build();
        final String s2 = s = null;
        try {
            Object o;
            if (Build$VERSION.SDK_INT > 16) {
                s = s2;
                final ContentResolver contentResolver = context.getContentResolver();
                s = s2;
                final String query = fontRequest.getQuery();
                s = s2;
                o = contentResolver.query(build, new String[] { "_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code" }, "query = ?", new String[] { query }, (String)null, cancellationSignal);
            }
            else {
                s = s2;
                final ContentResolver contentResolver2 = context.getContentResolver();
                s = s2;
                final String query2 = fontRequest.getQuery();
                s = s2;
                o = contentResolver2.query(build, new String[] { "_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code" }, "query = ?", new String[] { query2 }, (String)null);
            }
            ArrayList<Object> list2 = list;
            if (o != null) {
                list2 = list;
                s = (String)o;
                if (((Cursor)o).getCount() > 0) {
                    s = (String)o;
                    final int columnIndex = ((Cursor)o).getColumnIndex("result_code");
                    s = (String)o;
                    s = (String)o;
                    final ArrayList<Object> list3 = new ArrayList<Object>();
                    s = (String)o;
                    final int columnIndex2 = ((Cursor)o).getColumnIndex("_id");
                    s = (String)o;
                    final int columnIndex3 = ((Cursor)o).getColumnIndex("file_id");
                    s = (String)o;
                    final int columnIndex4 = ((Cursor)o).getColumnIndex("font_ttc_index");
                    s = (String)o;
                    final int columnIndex5 = ((Cursor)o).getColumnIndex("font_weight");
                    s = (String)o;
                    final int columnIndex6 = ((Cursor)o).getColumnIndex("font_italic");
                    while (true) {
                        s = (String)o;
                        if (!((Cursor)o).moveToNext()) {
                            break;
                        }
                        int int1;
                        if (columnIndex != -1) {
                            s = (String)o;
                            int1 = ((Cursor)o).getInt(columnIndex);
                        }
                        else {
                            int1 = 0;
                        }
                        int int2;
                        if (columnIndex4 != -1) {
                            s = (String)o;
                            int2 = ((Cursor)o).getInt(columnIndex4);
                        }
                        else {
                            int2 = 0;
                        }
                        Uri uri;
                        if (columnIndex3 == -1) {
                            s = (String)o;
                            uri = ContentUris.withAppendedId(build, ((Cursor)o).getLong(columnIndex2));
                        }
                        else {
                            s = (String)o;
                            uri = ContentUris.withAppendedId(build2, ((Cursor)o).getLong(columnIndex3));
                        }
                        int int3;
                        if (columnIndex5 != -1) {
                            s = (String)o;
                            int3 = ((Cursor)o).getInt(columnIndex5);
                        }
                        else {
                            int3 = 400;
                        }
                        boolean b = false;
                        Label_0490: {
                            if (columnIndex6 != -1) {
                                s = (String)o;
                                if (((Cursor)o).getInt(columnIndex6) == 1) {
                                    b = true;
                                    break Label_0490;
                                }
                            }
                            b = false;
                        }
                        s = (String)o;
                        s = (String)o;
                        final FontInfo e = new FontInfo(uri, int2, int3, b, int1);
                        s = (String)o;
                        list3.add(e);
                    }
                    list2 = list3;
                }
            }
            if (o != null) {
                ((Cursor)o).close();
            }
            return list2.toArray(new FontInfo[0]);
        }
        finally {
            if (s != null) {
                ((Cursor)s).close();
            }
        }
    }
    
    static TypefaceResult getFontInternal(final Context context, final FontRequest fontRequest, final int n) {
        try {
            final FontFamilyResult fetchFonts = fetchFonts(context, null, fontRequest);
            final int statusCode = fetchFonts.getStatusCode();
            int n2 = -3;
            if (statusCode == 0) {
                final Typeface fromFontInfo = TypefaceCompat.createFromFontInfo(context, null, fetchFonts.getFonts(), n);
                if (fromFontInfo != null) {
                    n2 = 0;
                }
                return new TypefaceResult(fromFontInfo, n2);
            }
            if (fetchFonts.getStatusCode() == 1) {
                n2 = -2;
            }
            return new TypefaceResult(null, n2);
        }
        catch (PackageManager$NameNotFoundException ex) {
            return new TypefaceResult(null, -1);
        }
    }
    
    public static Typeface getFontSync(Context mTypeface, final FontRequest fontRequest, final ResourcesCompat.FontCallback fontCallback, final Handler handler, final boolean b, int mResult, final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append(fontRequest.getIdentifier());
        sb.append("-");
        sb.append(i);
        final String string = sb.toString();
        final Typeface typeface = FontsContractCompat.sTypefaceCache.get(string);
        if (typeface != null) {
            if (fontCallback != null) {
                fontCallback.onFontRetrieved(typeface);
            }
            return typeface;
        }
        if (b && mResult == -1) {
            final TypefaceResult fontInternal = getFontInternal(mTypeface, fontRequest, i);
            if (fontCallback != null) {
                mResult = fontInternal.mResult;
                if (mResult == 0) {
                    fontCallback.callbackSuccessAsync(fontInternal.mTypeface, handler);
                }
                else {
                    fontCallback.callbackFailAsync(mResult, handler);
                }
            }
            return fontInternal.mTypeface;
        }
        final Callable<TypefaceResult> callable = new Callable<TypefaceResult>() {
            @Override
            public TypefaceResult call() throws Exception {
                final TypefaceResult fontInternal = FontsContractCompat.getFontInternal(mTypeface, fontRequest, i);
                final Typeface mTypeface = fontInternal.mTypeface;
                if (mTypeface != null) {
                    FontsContractCompat.sTypefaceCache.put(string, mTypeface);
                }
                return fontInternal;
            }
        };
        mTypeface = null;
        Label_0173: {
            if (!b) {
                break Label_0173;
            }
            try {
                mTypeface = (Context)FontsContractCompat.sBackgroundThread.postAndWait((Callable<TypefaceResult>)callable, mResult).mTypeface;
                return (Typeface)mTypeface;
                // iftrue(Label_0182:, fontCallback != null)
                while (true) {
                    Block_9: {
                        break Block_9;
                        Label_0182: {
                            mTypeface = (Context)new SelfDestructiveThread.ReplyCallback<TypefaceResult>() {
                                public void onReply(final TypefaceResult typefaceResult) {
                                    if (typefaceResult == null) {
                                        fontCallback.callbackFailAsync(1, handler);
                                    }
                                    else {
                                        final int mResult = typefaceResult.mResult;
                                        if (mResult == 0) {
                                            fontCallback.callbackSuccessAsync(typefaceResult.mTypeface, handler);
                                        }
                                        else {
                                            fontCallback.callbackFailAsync(mResult, handler);
                                        }
                                    }
                                }
                            };
                        }
                        synchronized (FontsContractCompat.sLock) {
                            final ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>> list = FontsContractCompat.sPendingReplies.get(string);
                            if (list != null) {
                                if (mTypeface != null) {
                                    list.add((SelfDestructiveThread.ReplyCallback<TypefaceResult>)mTypeface);
                                }
                                return null;
                            }
                            if (mTypeface != null) {
                                final ArrayList<Context> list2 = new ArrayList<Context>();
                                list2.add(mTypeface);
                                FontsContractCompat.sPendingReplies.put(string, (ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>>)list2);
                            }
                            // monitorexit(FontsContractCompat.sLock)
                            FontsContractCompat.sBackgroundThread.postAndReply((Callable<T>)callable, (SelfDestructiveThread.ReplyCallback<T>)new SelfDestructiveThread.ReplyCallback<TypefaceResult>() {
                                public void onReply(final TypefaceResult typefaceResult) {
                                    synchronized (FontsContractCompat.sLock) {
                                        final ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>> list = FontsContractCompat.sPendingReplies.get(string);
                                        if (list == null) {
                                            return;
                                        }
                                        FontsContractCompat.sPendingReplies.remove(string);
                                        // monitorexit(FontsContractCompat.sLock)
                                        for (int i = 0; i < list.size(); ++i) {
                                            list.get(i).onReply(typefaceResult);
                                        }
                                    }
                                }
                            });
                            return null;
                        }
                    }
                    mTypeface = null;
                    continue;
                }
            }
            catch (InterruptedException ex) {
                return (Typeface)mTypeface;
            }
        }
    }
    
    public static ProviderInfo getProvider(final PackageManager packageManager, final FontRequest fontRequest, final Resources resources) throws PackageManager$NameNotFoundException {
        final String providerAuthority = fontRequest.getProviderAuthority();
        int i = 0;
        final ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(providerAuthority, 0);
        if (resolveContentProvider == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No package found for authority: ");
            sb.append(providerAuthority);
            throw new PackageManager$NameNotFoundException(sb.toString());
        }
        if (resolveContentProvider.packageName.equals(fontRequest.getProviderPackage())) {
            final List<byte[]> convertToByteArrayList = convertToByteArrayList(packageManager.getPackageInfo(resolveContentProvider.packageName, 64).signatures);
            Collections.sort((List<Object>)convertToByteArrayList, (Comparator<? super Object>)FontsContractCompat.sByteArrayComparator);
            for (List<List<byte[]>> certificates = getCertificates(fontRequest, resources); i < certificates.size(); ++i) {
                final ArrayList list = new ArrayList<byte[]>((Collection<? extends T>)certificates.get(i));
                Collections.sort((List<E>)list, (Comparator<? super E>)FontsContractCompat.sByteArrayComparator);
                if (equalsByteArrayList(convertToByteArrayList, (List<byte[]>)list)) {
                    return resolveContentProvider;
                }
            }
            return null;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Found content provider ");
        sb2.append(providerAuthority);
        sb2.append(", but package was not ");
        sb2.append(fontRequest.getProviderPackage());
        throw new PackageManager$NameNotFoundException(sb2.toString());
    }
    
    public static Map<Uri, ByteBuffer> prepareFontData(final Context context, final FontInfo[] array, final CancellationSignal cancellationSignal) {
        final HashMap<Uri, ByteBuffer> m = new HashMap<Uri, ByteBuffer>();
        for (final FontInfo fontInfo : array) {
            if (fontInfo.getResultCode() == 0) {
                final Uri uri = fontInfo.getUri();
                if (!m.containsKey(uri)) {
                    m.put(uri, TypefaceCompatUtil.mmap(context, cancellationSignal, uri));
                }
            }
        }
        return (Map<Uri, ByteBuffer>)Collections.unmodifiableMap((Map<?, ?>)m);
    }
    
    public static class FontFamilyResult
    {
        private final FontInfo[] mFonts;
        private final int mStatusCode;
        
        public FontFamilyResult(final int mStatusCode, final FontInfo[] mFonts) {
            this.mStatusCode = mStatusCode;
            this.mFonts = mFonts;
        }
        
        public FontInfo[] getFonts() {
            return this.mFonts;
        }
        
        public int getStatusCode() {
            return this.mStatusCode;
        }
    }
    
    public static class FontInfo
    {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;
        
        public FontInfo(final Uri uri, final int mTtcIndex, final int mWeight, final boolean mItalic, final int mResultCode) {
            Preconditions.checkNotNull(uri);
            this.mUri = uri;
            this.mTtcIndex = mTtcIndex;
            this.mWeight = mWeight;
            this.mItalic = mItalic;
            this.mResultCode = mResultCode;
        }
        
        public int getResultCode() {
            return this.mResultCode;
        }
        
        public int getTtcIndex() {
            return this.mTtcIndex;
        }
        
        public Uri getUri() {
            return this.mUri;
        }
        
        public int getWeight() {
            return this.mWeight;
        }
        
        public boolean isItalic() {
            return this.mItalic;
        }
    }
    
    private static final class TypefaceResult
    {
        final int mResult;
        final Typeface mTypeface;
        
        TypefaceResult(final Typeface mTypeface, final int mResult) {
            this.mTypeface = mTypeface;
            this.mResult = mResult;
        }
    }
}
