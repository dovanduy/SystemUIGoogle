// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics.drawable;

import java.io.OutputStream;
import android.graphics.Bitmap$CompressFormat;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import androidx.core.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import java.lang.reflect.InvocationTargetException;
import android.os.Build$VERSION;
import android.content.res.Resources;
import android.net.Uri;
import android.graphics.Shader;
import android.graphics.Matrix;
import android.graphics.BitmapShader;
import android.graphics.Shader$TileMode;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import android.content.res.Resources$NotFoundException;
import androidx.core.util.Preconditions;
import android.graphics.drawable.Icon;
import android.content.Context;
import android.util.Log;
import android.os.Bundle;
import android.content.res.ColorStateList;
import android.os.Parcelable;
import android.graphics.PorterDuff$Mode;
import androidx.versionedparcelable.CustomVersionedParcelable;

public class IconCompat extends CustomVersionedParcelable
{
    static final PorterDuff$Mode DEFAULT_TINT_MODE;
    public byte[] mData;
    public int mInt1;
    public int mInt2;
    Object mObj1;
    public Parcelable mParcelable;
    public ColorStateList mTintList;
    PorterDuff$Mode mTintMode;
    public String mTintModeStr;
    public int mType;
    
    static {
        DEFAULT_TINT_MODE = PorterDuff$Mode.SRC_IN;
    }
    
    public IconCompat() {
        this.mType = -1;
        this.mData = null;
        this.mParcelable = null;
        this.mInt1 = 0;
        this.mInt2 = 0;
        this.mTintList = null;
        this.mTintMode = IconCompat.DEFAULT_TINT_MODE;
        this.mTintModeStr = null;
    }
    
    private IconCompat(final int mType) {
        this.mType = -1;
        this.mData = null;
        this.mParcelable = null;
        this.mInt1 = 0;
        this.mInt2 = 0;
        this.mTintList = null;
        this.mTintMode = IconCompat.DEFAULT_TINT_MODE;
        this.mTintModeStr = null;
        this.mType = mType;
    }
    
    public static IconCompat createFromBundle(final Bundle bundle) {
        final int int1 = bundle.getInt("type");
        final IconCompat iconCompat = new IconCompat(int1);
        iconCompat.mInt1 = bundle.getInt("int1");
        iconCompat.mInt2 = bundle.getInt("int2");
        if (bundle.containsKey("tint_list")) {
            iconCompat.mTintList = (ColorStateList)bundle.getParcelable("tint_list");
        }
        if (bundle.containsKey("tint_mode")) {
            iconCompat.mTintMode = PorterDuff$Mode.valueOf(bundle.getString("tint_mode"));
        }
        switch (int1) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unknown type ");
                sb.append(int1);
                Log.w("IconCompat", sb.toString());
                return null;
            }
            case 3: {
                iconCompat.mObj1 = bundle.getByteArray("obj");
                break;
            }
            case 2:
            case 4:
            case 6: {
                iconCompat.mObj1 = bundle.getString("obj");
                break;
            }
            case -1:
            case 1:
            case 5: {
                iconCompat.mObj1 = bundle.getParcelable("obj");
                break;
            }
        }
        return iconCompat;
    }
    
    public static IconCompat createFromIcon(final Context context, final Icon mObj1) {
        Preconditions.checkNotNull(mObj1);
        final int type = getType(mObj1);
        if (type != 2) {
            if (type == 4) {
                return createWithContentUri(getUri(mObj1));
            }
            if (type != 6) {
                final IconCompat iconCompat = new IconCompat(-1);
                iconCompat.mObj1 = mObj1;
                return iconCompat;
            }
            return createWithAdaptiveBitmapContentUri(getUri(mObj1));
        }
        else {
            final String resPackage = getResPackage(mObj1);
            try {
                return createWithResource(getResources(context, resPackage), resPackage, getResId(mObj1));
            }
            catch (Resources$NotFoundException ex) {
                throw new IllegalArgumentException("Icon resource cannot be found");
            }
        }
    }
    
    static Bitmap createLegacyIconFromAdaptiveIcon(final Bitmap bitmap, final boolean b) {
        final int n = (int)(Math.min(bitmap.getWidth(), bitmap.getHeight()) * 0.6666667f);
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n, Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap2);
        final Paint paint = new Paint(3);
        final float n2 = (float)n;
        final float n3 = 0.5f * n2;
        final float n4 = 0.9166667f * n3;
        if (b) {
            final float n5 = 0.010416667f * n2;
            paint.setColor(0);
            paint.setShadowLayer(n5, 0.0f, n2 * 0.020833334f, 1023410176);
            canvas.drawCircle(n3, n3, n4, paint);
            paint.setShadowLayer(n5, 0.0f, 0.0f, 503316480);
            canvas.drawCircle(n3, n3, n4, paint);
            paint.clearShadowLayer();
        }
        paint.setColor(-16777216);
        final Shader$TileMode clamp = Shader$TileMode.CLAMP;
        final BitmapShader shader = new BitmapShader(bitmap, clamp, clamp);
        final Matrix localMatrix = new Matrix();
        localMatrix.setTranslate((float)(-(bitmap.getWidth() - n) / 2), (float)(-(bitmap.getHeight() - n) / 2));
        shader.setLocalMatrix(localMatrix);
        paint.setShader((Shader)shader);
        canvas.drawCircle(n3, n3, n4, paint);
        canvas.setBitmap((Bitmap)null);
        return bitmap2;
    }
    
    public static IconCompat createWithAdaptiveBitmapContentUri(final Uri uri) {
        if (uri != null) {
            return createWithAdaptiveBitmapContentUri(uri.toString());
        }
        throw new IllegalArgumentException("Uri must not be null.");
    }
    
    public static IconCompat createWithAdaptiveBitmapContentUri(final String mObj1) {
        if (mObj1 != null) {
            final IconCompat iconCompat = new IconCompat(6);
            iconCompat.mObj1 = mObj1;
            return iconCompat;
        }
        throw new IllegalArgumentException("Uri must not be null.");
    }
    
    public static IconCompat createWithBitmap(final Bitmap mObj1) {
        if (mObj1 != null) {
            final IconCompat iconCompat = new IconCompat(1);
            iconCompat.mObj1 = mObj1;
            return iconCompat;
        }
        throw new IllegalArgumentException("Bitmap must not be null.");
    }
    
    public static IconCompat createWithContentUri(final Uri uri) {
        if (uri != null) {
            return createWithContentUri(uri.toString());
        }
        throw new IllegalArgumentException("Uri must not be null.");
    }
    
    public static IconCompat createWithContentUri(final String mObj1) {
        if (mObj1 != null) {
            final IconCompat iconCompat = new IconCompat(4);
            iconCompat.mObj1 = mObj1;
            return iconCompat;
        }
        throw new IllegalArgumentException("Uri must not be null.");
    }
    
    public static IconCompat createWithResource(final Context context, final int n) {
        if (context != null) {
            return createWithResource(context.getResources(), context.getPackageName(), n);
        }
        throw new IllegalArgumentException("Context must not be null.");
    }
    
    public static IconCompat createWithResource(final Resources resources, final String mObj1, final int mInt1) {
        if (mObj1 == null) {
            throw new IllegalArgumentException("Package must not be null.");
        }
        if (mInt1 != 0) {
            final IconCompat iconCompat = new IconCompat(2);
            iconCompat.mInt1 = mInt1;
            if (resources != null) {
                try {
                    iconCompat.mObj1 = resources.getResourceName(mInt1);
                    return iconCompat;
                }
                catch (Resources$NotFoundException ex) {
                    throw new IllegalArgumentException("Icon resource cannot be found");
                }
            }
            iconCompat.mObj1 = mObj1;
            return iconCompat;
        }
        throw new IllegalArgumentException("Drawable resource ID must not be 0");
    }
    
    private static int getResId(final Icon obj) {
        if (Build$VERSION.SDK_INT >= 28) {
            return obj.getResId();
        }
        try {
            return (int)obj.getClass().getMethod("getResId", (Class<?>[])new Class[0]).invoke(obj, new Object[0]);
        }
        catch (NoSuchMethodException ex) {
            Log.e("IconCompat", "Unable to get icon resource", (Throwable)ex);
            return 0;
        }
        catch (InvocationTargetException ex2) {
            Log.e("IconCompat", "Unable to get icon resource", (Throwable)ex2);
            return 0;
        }
        catch (IllegalAccessException ex3) {
            Log.e("IconCompat", "Unable to get icon resource", (Throwable)ex3);
            return 0;
        }
    }
    
    private static String getResPackage(final Icon obj) {
        if (Build$VERSION.SDK_INT >= 28) {
            return obj.getResPackage();
        }
        try {
            return (String)obj.getClass().getMethod("getResPackage", (Class<?>[])new Class[0]).invoke(obj, new Object[0]);
        }
        catch (NoSuchMethodException ex) {
            Log.e("IconCompat", "Unable to get icon package", (Throwable)ex);
            return null;
        }
        catch (InvocationTargetException ex2) {
            Log.e("IconCompat", "Unable to get icon package", (Throwable)ex2);
            return null;
        }
        catch (IllegalAccessException ex3) {
            Log.e("IconCompat", "Unable to get icon package", (Throwable)ex3);
            return null;
        }
    }
    
    private static Resources getResources(final Context context, final String anObject) {
        if ("android".equals(anObject)) {
            return Resources.getSystem();
        }
        final PackageManager packageManager = context.getPackageManager();
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(anObject, 8192);
            if (applicationInfo != null) {
                return packageManager.getResourcesForApplication(applicationInfo);
            }
            return null;
        }
        catch (PackageManager$NameNotFoundException ex) {
            Log.e("IconCompat", String.format("Unable to find pkg=%s for icon", anObject), (Throwable)ex);
            return null;
        }
    }
    
    private static int getType(final Icon icon) {
        if (Build$VERSION.SDK_INT >= 28) {
            return icon.getType();
        }
        try {
            return (int)icon.getClass().getMethod("getType", (Class<?>[])new Class[0]).invoke(icon, new Object[0]);
        }
        catch (NoSuchMethodException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unable to get icon type ");
            sb.append(icon);
            Log.e("IconCompat", sb.toString(), (Throwable)ex);
            return -1;
        }
        catch (InvocationTargetException ex2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Unable to get icon type ");
            sb2.append(icon);
            Log.e("IconCompat", sb2.toString(), (Throwable)ex2);
            return -1;
        }
        catch (IllegalAccessException ex3) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Unable to get icon type ");
            sb3.append(icon);
            Log.e("IconCompat", sb3.toString(), (Throwable)ex3);
            return -1;
        }
    }
    
    private static Uri getUri(final Icon obj) {
        if (Build$VERSION.SDK_INT >= 28) {
            return obj.getUri();
        }
        try {
            return (Uri)obj.getClass().getMethod("getUri", (Class<?>[])new Class[0]).invoke(obj, new Object[0]);
        }
        catch (NoSuchMethodException ex) {
            Log.e("IconCompat", "Unable to get icon uri", (Throwable)ex);
            return null;
        }
        catch (InvocationTargetException ex2) {
            Log.e("IconCompat", "Unable to get icon uri", (Throwable)ex2);
            return null;
        }
        catch (IllegalAccessException ex3) {
            Log.e("IconCompat", "Unable to get icon uri", (Throwable)ex3);
            return null;
        }
    }
    
    private InputStream getUriInputStream(final Context context) {
        final Uri uri = this.getUri();
        final String scheme = uri.getScheme();
        if (!"content".equals(scheme)) {
            if (!"file".equals(scheme)) {
                try {
                    return new FileInputStream(new File((String)this.mObj1));
                }
                catch (FileNotFoundException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unable to load image from path: ");
                    sb.append(uri);
                    Log.w("IconCompat", sb.toString(), (Throwable)ex);
                    return null;
                }
            }
        }
        try {
            return context.getContentResolver().openInputStream(uri);
        }
        catch (Exception ex2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Unable to load image from URI: ");
            sb2.append(uri);
            Log.w("IconCompat", sb2.toString(), (Throwable)ex2);
        }
        return null;
    }
    
    private Drawable loadDrawableInner(final Context context) {
        switch (this.mType) {
            case 6: {
                final InputStream uriInputStream = this.getUriInputStream(context);
                if (uriInputStream == null) {
                    break;
                }
                if (Build$VERSION.SDK_INT >= 26) {
                    return (Drawable)new AdaptiveIconDrawable((Drawable)null, (Drawable)new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(uriInputStream)));
                }
                return (Drawable)new BitmapDrawable(context.getResources(), createLegacyIconFromAdaptiveIcon(BitmapFactory.decodeStream(uriInputStream), false));
            }
            case 5: {
                return (Drawable)new BitmapDrawable(context.getResources(), createLegacyIconFromAdaptiveIcon((Bitmap)this.mObj1, false));
            }
            case 4: {
                final InputStream uriInputStream2 = this.getUriInputStream(context);
                if (uriInputStream2 != null) {
                    return (Drawable)new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(uriInputStream2));
                }
                break;
            }
            case 3: {
                return (Drawable)new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray((byte[])this.mObj1, this.mInt1, this.mInt2));
            }
            case 2: {
                String s;
                if (TextUtils.isEmpty((CharSequence)(s = this.getResPackage()))) {
                    s = context.getPackageName();
                }
                final Resources resources = getResources(context, s);
                try {
                    return ResourcesCompat.getDrawable(resources, this.mInt1, context.getTheme());
                }
                catch (RuntimeException ex) {
                    Log.e("IconCompat", String.format("Unable to load resource 0x%08x from pkg=%s", this.mInt1, this.mObj1), (Throwable)ex);
                    break;
                }
            }
            case 1: {
                return (Drawable)new BitmapDrawable(context.getResources(), (Bitmap)this.mObj1);
            }
        }
        return null;
    }
    
    private static String typeToString(final int n) {
        switch (n) {
            default: {
                return "UNKNOWN";
            }
            case 6: {
                return "URI_MASKABLE";
            }
            case 5: {
                return "BITMAP_MASKABLE";
            }
            case 4: {
                return "URI";
            }
            case 3: {
                return "DATA";
            }
            case 2: {
                return "RESOURCE";
            }
            case 1: {
                return "BITMAP";
            }
        }
    }
    
    public void checkResource(final Context context) {
        if (this.mType == 2) {
            final String s = (String)this.mObj1;
            if (!s.contains(":")) {
                return;
            }
            final String s2 = s.split(":", -1)[1];
            final String s3 = s2.split("/", -1)[0];
            final String str = s2.split("/", -1)[1];
            final String str2 = s.split(":", -1)[0];
            final int identifier = getResources(context, str2).getIdentifier(str, s3, str2);
            if (this.mInt1 != identifier) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Id has changed for ");
                sb.append(str2);
                sb.append("/");
                sb.append(str);
                Log.i("IconCompat", sb.toString());
                this.mInt1 = identifier;
            }
        }
    }
    
    public int getResId() {
        if (this.mType == -1 && Build$VERSION.SDK_INT >= 23) {
            return getResId((Icon)this.mObj1);
        }
        if (this.mType == 2) {
            return this.mInt1;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("called getResId() on ");
        sb.append(this);
        throw new IllegalStateException(sb.toString());
    }
    
    public String getResPackage() {
        if (this.mType == -1 && Build$VERSION.SDK_INT >= 23) {
            return getResPackage((Icon)this.mObj1);
        }
        if (this.mType == 2) {
            return ((String)this.mObj1).split(":", -1)[0];
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("called getResPackage() on ");
        sb.append(this);
        throw new IllegalStateException(sb.toString());
    }
    
    public int getType() {
        if (this.mType == -1 && Build$VERSION.SDK_INT >= 23) {
            return getType((Icon)this.mObj1);
        }
        return this.mType;
    }
    
    public Uri getUri() {
        if (this.mType == -1 && Build$VERSION.SDK_INT >= 23) {
            return getUri((Icon)this.mObj1);
        }
        final int mType = this.mType;
        if (mType != 4 && mType != 6) {
            final StringBuilder sb = new StringBuilder();
            sb.append("called getUri() on ");
            sb.append(this);
            throw new IllegalStateException(sb.toString());
        }
        return Uri.parse((String)this.mObj1);
    }
    
    public Drawable loadDrawable(final Context context) {
        this.checkResource(context);
        if (Build$VERSION.SDK_INT >= 23) {
            return this.toIcon(context).loadDrawable(context);
        }
        final Drawable loadDrawableInner = this.loadDrawableInner(context);
        if (loadDrawableInner != null && (this.mTintList != null || this.mTintMode != IconCompat.DEFAULT_TINT_MODE)) {
            loadDrawableInner.mutate();
            DrawableCompat.setTintList(loadDrawableInner, this.mTintList);
            DrawableCompat.setTintMode(loadDrawableInner, this.mTintMode);
        }
        return loadDrawableInner;
    }
    
    public void onPostParceling() {
        this.mTintMode = PorterDuff$Mode.valueOf(this.mTintModeStr);
        switch (this.mType) {
            case 3: {
                this.mObj1 = this.mData;
                break;
            }
            case 2:
            case 4:
            case 6: {
                this.mObj1 = new String(this.mData, Charset.forName("UTF-16"));
                break;
            }
            case 1:
            case 5: {
                final Parcelable mParcelable = this.mParcelable;
                if (mParcelable != null) {
                    this.mObj1 = mParcelable;
                    break;
                }
                final byte[] mData = this.mData;
                this.mObj1 = mData;
                this.mType = 3;
                this.mInt1 = 0;
                this.mInt2 = mData.length;
                break;
            }
            case -1: {
                final Parcelable mParcelable2 = this.mParcelable;
                if (mParcelable2 != null) {
                    this.mObj1 = mParcelable2;
                    break;
                }
                throw new IllegalArgumentException("Invalid icon");
            }
        }
    }
    
    public void onPreParceling(final boolean b) {
        this.mTintModeStr = this.mTintMode.name();
        switch (this.mType) {
            case 4:
            case 6: {
                this.mData = this.mObj1.toString().getBytes(Charset.forName("UTF-16"));
                break;
            }
            case 3: {
                this.mData = (byte[])this.mObj1;
                break;
            }
            case 2: {
                this.mData = ((String)this.mObj1).getBytes(Charset.forName("UTF-16"));
                break;
            }
            case 1:
            case 5: {
                if (b) {
                    final Bitmap bitmap = (Bitmap)this.mObj1;
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap$CompressFormat.PNG, 90, (OutputStream)byteArrayOutputStream);
                    this.mData = byteArrayOutputStream.toByteArray();
                    break;
                }
                this.mParcelable = (Parcelable)this.mObj1;
                break;
            }
            case -1: {
                if (!b) {
                    this.mParcelable = (Parcelable)this.mObj1;
                    break;
                }
                throw new IllegalArgumentException("Can't serialize Icon created with IconCompat#createFromIcon");
            }
        }
    }
    
    public IconCompat setTintMode(final PorterDuff$Mode mTintMode) {
        this.mTintMode = mTintMode;
        return this;
    }
    
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        switch (this.mType) {
            default: {
                throw new IllegalArgumentException("Invalid icon");
            }
            case 3: {
                bundle.putByteArray("obj", (byte[])this.mObj1);
                break;
            }
            case 2:
            case 4:
            case 6: {
                bundle.putString("obj", (String)this.mObj1);
                break;
            }
            case 1:
            case 5: {
                bundle.putParcelable("obj", (Parcelable)this.mObj1);
                break;
            }
            case -1: {
                bundle.putParcelable("obj", (Parcelable)this.mObj1);
                break;
            }
        }
        bundle.putInt("type", this.mType);
        bundle.putInt("int1", this.mInt1);
        bundle.putInt("int2", this.mInt2);
        final ColorStateList mTintList = this.mTintList;
        if (mTintList != null) {
            bundle.putParcelable("tint_list", (Parcelable)mTintList);
        }
        final PorterDuff$Mode mTintMode = this.mTintMode;
        if (mTintMode != IconCompat.DEFAULT_TINT_MODE) {
            bundle.putString("tint_mode", mTintMode.name());
        }
        return bundle;
    }
    
    @Deprecated
    public Icon toIcon() {
        return this.toIcon(null);
    }
    
    public Icon toIcon(final Context context) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        Icon icon = null;
        switch (this.mType) {
            default: {
                throw new IllegalArgumentException("Unknown type");
            }
            case 6: {
                if (context == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Context is required to resolve the file uri of the icon: ");
                    sb.append(this.getUri());
                    throw new IllegalArgumentException(sb.toString());
                }
                final InputStream uriInputStream = this.getUriInputStream(context);
                if (uriInputStream == null) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Cannot load adaptive icon from uri: ");
                    sb2.append(this.getUri());
                    throw new IllegalStateException(sb2.toString());
                }
                if (sdk_INT >= 26) {
                    icon = Icon.createWithAdaptiveBitmap(BitmapFactory.decodeStream(uriInputStream));
                    break;
                }
                icon = Icon.createWithBitmap(createLegacyIconFromAdaptiveIcon(BitmapFactory.decodeStream(uriInputStream), false));
                break;
            }
            case 5: {
                if (sdk_INT >= 26) {
                    icon = Icon.createWithAdaptiveBitmap((Bitmap)this.mObj1);
                    break;
                }
                icon = Icon.createWithBitmap(createLegacyIconFromAdaptiveIcon((Bitmap)this.mObj1, false));
                break;
            }
            case 4: {
                icon = Icon.createWithContentUri((String)this.mObj1);
                break;
            }
            case 3: {
                icon = Icon.createWithData((byte[])this.mObj1, this.mInt1, this.mInt2);
                break;
            }
            case 2: {
                icon = Icon.createWithResource(this.getResPackage(), this.mInt1);
                break;
            }
            case 1: {
                icon = Icon.createWithBitmap((Bitmap)this.mObj1);
                break;
            }
            case -1: {
                return (Icon)this.mObj1;
            }
        }
        final ColorStateList mTintList = this.mTintList;
        if (mTintList != null) {
            icon.setTintList(mTintList);
        }
        final PorterDuff$Mode mTintMode = this.mTintMode;
        if (mTintMode != IconCompat.DEFAULT_TINT_MODE) {
            icon.setTintMode(mTintMode);
        }
        return icon;
    }
    
    @Override
    public String toString() {
        if (this.mType == -1) {
            return String.valueOf(this.mObj1);
        }
        final StringBuilder sb = new StringBuilder("Icon(typ=");
        sb.append(typeToString(this.mType));
        switch (this.mType) {
            case 4:
            case 6: {
                sb.append(" uri=");
                sb.append(this.mObj1);
                break;
            }
            case 3: {
                sb.append(" len=");
                sb.append(this.mInt1);
                if (this.mInt2 != 0) {
                    sb.append(" off=");
                    sb.append(this.mInt2);
                    break;
                }
                break;
            }
            case 2: {
                sb.append(" pkg=");
                sb.append(this.getResPackage());
                sb.append(" id=");
                sb.append(String.format("0x%08x", this.getResId()));
                break;
            }
            case 1:
            case 5: {
                sb.append(" size=");
                sb.append(((Bitmap)this.mObj1).getWidth());
                sb.append("x");
                sb.append(((Bitmap)this.mObj1).getHeight());
                break;
            }
        }
        if (this.mTintList != null) {
            sb.append(" tint=");
            sb.append(this.mTintList);
        }
        if (this.mTintMode != IconCompat.DEFAULT_TINT_MODE) {
            sb.append(" mode=");
            sb.append(this.mTintMode);
        }
        sb.append(")");
        return sb.toString();
    }
}
