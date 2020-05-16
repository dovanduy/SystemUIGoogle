// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import androidx.collection.LruCache;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat;
import android.content.res.Resources$Theme;
import androidx.core.content.ContextCompat;
import android.graphics.ColorFilter;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.content.res.XmlResourceParser;
import android.content.res.Resources;
import android.util.Log;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import android.os.Build$VERSION;
import android.graphics.PorterDuffColorFilter;
import androidx.appcompat.resources.R$drawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.content.res.ColorStateList;
import androidx.collection.SparseArrayCompat;
import android.graphics.drawable.Drawable$ConstantState;
import java.lang.ref.WeakReference;
import androidx.collection.LongSparseArray;
import android.content.Context;
import java.util.WeakHashMap;
import androidx.collection.SimpleArrayMap;
import android.graphics.PorterDuff$Mode;

public final class ResourceManagerInternal
{
    private static final ColorFilterLruCache COLOR_FILTER_CACHE;
    private static final PorterDuff$Mode DEFAULT_MODE;
    private static ResourceManagerInternal INSTANCE;
    private SimpleArrayMap<String, InflateDelegate> mDelegates;
    private final WeakHashMap<Context, LongSparseArray<WeakReference<Drawable$ConstantState>>> mDrawableCaches;
    private boolean mHasCheckedVectorDrawableSetup;
    private ResourceManagerHooks mHooks;
    private SparseArrayCompat<String> mKnownDrawableIdTags;
    private WeakHashMap<Context, SparseArrayCompat<ColorStateList>> mTintLists;
    private TypedValue mTypedValue;
    
    static {
        DEFAULT_MODE = PorterDuff$Mode.SRC_IN;
        COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    }
    
    public ResourceManagerInternal() {
        this.mDrawableCaches = new WeakHashMap<Context, LongSparseArray<WeakReference<Drawable$ConstantState>>>(0);
    }
    
    private void addDelegate(final String s, final InflateDelegate inflateDelegate) {
        if (this.mDelegates == null) {
            this.mDelegates = new SimpleArrayMap<String, InflateDelegate>();
        }
        this.mDelegates.put(s, inflateDelegate);
    }
    
    private boolean addDrawableToCache(final Context context, final long n, final Drawable drawable) {
        synchronized (this) {
            final Drawable$ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                LongSparseArray<WeakReference<Drawable$ConstantState>> value;
                if ((value = this.mDrawableCaches.get(context)) == null) {
                    value = new LongSparseArray<WeakReference<Drawable$ConstantState>>();
                    this.mDrawableCaches.put(context, value);
                }
                value.put(n, new WeakReference<Drawable$ConstantState>(constantState));
                return true;
            }
            return false;
        }
    }
    
    private void addTintListToCache(final Context context, final int n, final ColorStateList list) {
        if (this.mTintLists == null) {
            this.mTintLists = new WeakHashMap<Context, SparseArrayCompat<ColorStateList>>();
        }
        SparseArrayCompat<ColorStateList> value;
        if ((value = this.mTintLists.get(context)) == null) {
            value = new SparseArrayCompat<ColorStateList>();
            this.mTintLists.put(context, value);
        }
        value.append(n, list);
    }
    
    private void checkVectorDrawableSetup(final Context context) {
        if (this.mHasCheckedVectorDrawableSetup) {
            return;
        }
        this.mHasCheckedVectorDrawableSetup = true;
        final Drawable drawable = this.getDrawable(context, R$drawable.abc_vector_test);
        if (drawable != null && isVectorDrawable(drawable)) {
            return;
        }
        this.mHasCheckedVectorDrawableSetup = false;
        throw new IllegalStateException("This app has been built with an incorrect configuration. Please configure your build for VectorDrawableCompat.");
    }
    
    private static long createCacheKey(final TypedValue typedValue) {
        return (long)typedValue.assetCookie << 32 | (long)typedValue.data;
    }
    
    private Drawable createDrawableIfNeeded(final Context context, final int n) {
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        final TypedValue mTypedValue = this.mTypedValue;
        context.getResources().getValue(n, mTypedValue, true);
        final long cacheKey = createCacheKey(mTypedValue);
        final Drawable cachedDrawable = this.getCachedDrawable(context, cacheKey);
        if (cachedDrawable != null) {
            return cachedDrawable;
        }
        final ResourceManagerHooks mHooks = this.mHooks;
        Drawable drawable;
        if (mHooks == null) {
            drawable = null;
        }
        else {
            drawable = mHooks.createDrawableFor(this, context, n);
        }
        if (drawable != null) {
            drawable.setChangingConfigurations(mTypedValue.changingConfigurations);
            this.addDrawableToCache(context, cacheKey, drawable);
        }
        return drawable;
    }
    
    private static PorterDuffColorFilter createTintFilter(final ColorStateList list, final PorterDuff$Mode porterDuff$Mode, final int[] array) {
        if (list != null && porterDuff$Mode != null) {
            return getPorterDuffColorFilter(list.getColorForState(array, 0), porterDuff$Mode);
        }
        return null;
    }
    
    public static ResourceManagerInternal get() {
        synchronized (ResourceManagerInternal.class) {
            if (ResourceManagerInternal.INSTANCE == null) {
                installDefaultInflateDelegates(ResourceManagerInternal.INSTANCE = new ResourceManagerInternal());
            }
            return ResourceManagerInternal.INSTANCE;
        }
    }
    
    private Drawable getCachedDrawable(final Context key, final long n) {
        synchronized (this) {
            final LongSparseArray<WeakReference<Drawable$ConstantState>> longSparseArray = this.mDrawableCaches.get(key);
            if (longSparseArray == null) {
                return null;
            }
            final WeakReference<Drawable$ConstantState> weakReference = longSparseArray.get(n);
            if (weakReference != null) {
                final Drawable$ConstantState drawable$ConstantState = weakReference.get();
                if (drawable$ConstantState != null) {
                    return drawable$ConstantState.newDrawable(key.getResources());
                }
                longSparseArray.remove(n);
            }
            return null;
        }
    }
    
    public static PorterDuffColorFilter getPorterDuffColorFilter(final int n, final PorterDuff$Mode porterDuff$Mode) {
        synchronized (ResourceManagerInternal.class) {
            PorterDuffColorFilter value;
            if ((value = ResourceManagerInternal.COLOR_FILTER_CACHE.get(n, porterDuff$Mode)) == null) {
                value = new PorterDuffColorFilter(n, porterDuff$Mode);
                ResourceManagerInternal.COLOR_FILTER_CACHE.put(n, porterDuff$Mode, value);
            }
            return value;
        }
    }
    
    private ColorStateList getTintListFromCache(final Context key, final int n) {
        final WeakHashMap<Context, SparseArrayCompat<ColorStateList>> mTintLists = this.mTintLists;
        ColorStateList list = null;
        if (mTintLists != null) {
            final SparseArrayCompat<ColorStateList> sparseArrayCompat = mTintLists.get(key);
            list = list;
            if (sparseArrayCompat != null) {
                list = sparseArrayCompat.get(n);
            }
        }
        return list;
    }
    
    private static void installDefaultInflateDelegates(final ResourceManagerInternal resourceManagerInternal) {
        if (Build$VERSION.SDK_INT < 24) {
            resourceManagerInternal.addDelegate("vector", (InflateDelegate)new VdcInflateDelegate());
            resourceManagerInternal.addDelegate("animated-vector", (InflateDelegate)new AvdcInflateDelegate());
            resourceManagerInternal.addDelegate("animated-selector", (InflateDelegate)new AsldcInflateDelegate());
        }
    }
    
    private static boolean isVectorDrawable(final Drawable drawable) {
        return drawable instanceof VectorDrawableCompat || "android.graphics.drawable.VectorDrawable".equals(drawable.getClass().getName());
    }
    
    private Drawable loadDrawableFromDelegates(final Context context, final int n) {
        final SimpleArrayMap<String, InflateDelegate> mDelegates = this.mDelegates;
        if (mDelegates == null || mDelegates.isEmpty()) {
            return null;
        }
        final SparseArrayCompat<String> mKnownDrawableIdTags = this.mKnownDrawableIdTags;
        if (mKnownDrawableIdTags != null) {
            final String anObject = mKnownDrawableIdTags.get(n);
            if ("appcompat_skip_skip".equals(anObject) || (anObject != null && this.mDelegates.get(anObject) == null)) {
                return null;
            }
        }
        else {
            this.mKnownDrawableIdTags = new SparseArrayCompat<String>();
        }
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        final TypedValue mTypedValue = this.mTypedValue;
        final Resources resources = context.getResources();
        resources.getValue(n, mTypedValue, true);
        final long cacheKey = createCacheKey(mTypedValue);
        final Drawable cachedDrawable = this.getCachedDrawable(context, cacheKey);
        if (cachedDrawable != null) {
            return cachedDrawable;
        }
        final CharSequence string = mTypedValue.string;
        Drawable drawable = cachedDrawable;
        if (string != null) {
            drawable = cachedDrawable;
            if (string.toString().endsWith(".xml")) {
                drawable = cachedDrawable;
                try {
                    final XmlResourceParser xml = resources.getXml(n);
                    drawable = cachedDrawable;
                    final AttributeSet attributeSet = Xml.asAttributeSet((XmlPullParser)xml);
                    int next;
                    do {
                        drawable = cachedDrawable;
                        next = ((XmlPullParser)xml).next();
                    } while (next != 2 && next != 1);
                    if (next != 2) {
                        drawable = cachedDrawable;
                        drawable = cachedDrawable;
                        final XmlPullParserException ex = new XmlPullParserException("No start tag found");
                        drawable = cachedDrawable;
                        throw ex;
                    }
                    drawable = cachedDrawable;
                    final String name = ((XmlPullParser)xml).getName();
                    drawable = cachedDrawable;
                    this.mKnownDrawableIdTags.append(n, name);
                    drawable = cachedDrawable;
                    final InflateDelegate inflateDelegate = this.mDelegates.get(name);
                    Drawable fromXmlInner = cachedDrawable;
                    if (inflateDelegate != null) {
                        drawable = cachedDrawable;
                        fromXmlInner = inflateDelegate.createFromXmlInner(context, (XmlPullParser)xml, attributeSet, context.getTheme());
                    }
                    if ((drawable = fromXmlInner) != null) {
                        drawable = fromXmlInner;
                        fromXmlInner.setChangingConfigurations(mTypedValue.changingConfigurations);
                        drawable = fromXmlInner;
                        this.addDrawableToCache(context, cacheKey, fromXmlInner);
                        drawable = fromXmlInner;
                    }
                }
                catch (Exception ex2) {
                    Log.e("ResourceManagerInternal", "Exception while inflating drawable", (Throwable)ex2);
                }
            }
        }
        if (drawable == null) {
            this.mKnownDrawableIdTags.append(n, "appcompat_skip_skip");
        }
        return drawable;
    }
    
    private Drawable tintDrawable(final Context context, final int n, final boolean b, final Drawable drawable) {
        final ColorStateList tintList = this.getTintList(context, n);
        Drawable drawable2;
        if (tintList != null) {
            Drawable mutate = drawable;
            if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
                mutate = drawable.mutate();
            }
            final Drawable wrap = DrawableCompat.wrap(mutate);
            DrawableCompat.setTintList(wrap, tintList);
            final PorterDuff$Mode tintMode = this.getTintMode(n);
            drawable2 = wrap;
            if (tintMode != null) {
                DrawableCompat.setTintMode(wrap, tintMode);
                drawable2 = wrap;
            }
        }
        else {
            final ResourceManagerHooks mHooks = this.mHooks;
            if (mHooks != null && mHooks.tintDrawable(context, n, drawable)) {
                drawable2 = drawable;
            }
            else {
                drawable2 = drawable;
                if (!this.tintDrawableUsingColorFilter(context, n, drawable)) {
                    drawable2 = drawable;
                    if (b) {
                        drawable2 = null;
                    }
                }
            }
        }
        return drawable2;
    }
    
    static void tintDrawable(final Drawable drawable, final TintInfo tintInfo, final int[] array) {
        if (DrawableUtils.canSafelyMutateDrawable(drawable) && drawable.mutate() != drawable) {
            Log.d("ResourceManagerInternal", "Mutated drawable is not the same instance as the input.");
            return;
        }
        if (!tintInfo.mHasTintList && !tintInfo.mHasTintMode) {
            drawable.clearColorFilter();
        }
        else {
            ColorStateList mTintList;
            if (tintInfo.mHasTintList) {
                mTintList = tintInfo.mTintList;
            }
            else {
                mTintList = null;
            }
            PorterDuff$Mode porterDuff$Mode;
            if (tintInfo.mHasTintMode) {
                porterDuff$Mode = tintInfo.mTintMode;
            }
            else {
                porterDuff$Mode = ResourceManagerInternal.DEFAULT_MODE;
            }
            drawable.setColorFilter((ColorFilter)createTintFilter(mTintList, porterDuff$Mode, array));
        }
        if (Build$VERSION.SDK_INT <= 23) {
            drawable.invalidateSelf();
        }
    }
    
    public Drawable getDrawable(final Context context, final int n) {
        synchronized (this) {
            return this.getDrawable(context, n, false);
        }
    }
    
    Drawable getDrawable(final Context context, final int n, final boolean b) {
        synchronized (this) {
            this.checkVectorDrawableSetup(context);
            Drawable drawable;
            if ((drawable = this.loadDrawableFromDelegates(context, n)) == null) {
                drawable = this.createDrawableIfNeeded(context, n);
            }
            Drawable drawable2;
            if ((drawable2 = drawable) == null) {
                drawable2 = ContextCompat.getDrawable(context, n);
            }
            Drawable tintDrawable;
            if ((tintDrawable = drawable2) != null) {
                tintDrawable = this.tintDrawable(context, n, b, drawable2);
            }
            if (tintDrawable != null) {
                DrawableUtils.fixDrawable(tintDrawable);
            }
            return tintDrawable;
        }
    }
    
    ColorStateList getTintList(final Context context, final int n) {
        synchronized (this) {
            ColorStateList tintListFromCache;
            if ((tintListFromCache = this.getTintListFromCache(context, n)) == null) {
                ColorStateList tintListForDrawableRes;
                if (this.mHooks == null) {
                    tintListForDrawableRes = null;
                }
                else {
                    tintListForDrawableRes = this.mHooks.getTintListForDrawableRes(context, n);
                }
                tintListFromCache = tintListForDrawableRes;
                if (tintListForDrawableRes != null) {
                    this.addTintListToCache(context, n, tintListForDrawableRes);
                    tintListFromCache = tintListForDrawableRes;
                }
            }
            return tintListFromCache;
        }
    }
    
    PorterDuff$Mode getTintMode(final int n) {
        final ResourceManagerHooks mHooks = this.mHooks;
        PorterDuff$Mode tintModeForDrawableRes;
        if (mHooks == null) {
            tintModeForDrawableRes = null;
        }
        else {
            tintModeForDrawableRes = mHooks.getTintModeForDrawableRes(n);
        }
        return tintModeForDrawableRes;
    }
    
    public void onConfigurationChanged(final Context key) {
        synchronized (this) {
            final LongSparseArray<WeakReference<Drawable$ConstantState>> longSparseArray = this.mDrawableCaches.get(key);
            if (longSparseArray != null) {
                longSparseArray.clear();
            }
        }
    }
    
    Drawable onDrawableLoadedFromResources(final Context context, final VectorEnabledTintResources vectorEnabledTintResources, final int n) {
        synchronized (this) {
            Drawable drawable;
            if ((drawable = this.loadDrawableFromDelegates(context, n)) == null) {
                drawable = vectorEnabledTintResources.superGetDrawable(n);
            }
            if (drawable != null) {
                return this.tintDrawable(context, n, false, drawable);
            }
            return null;
        }
    }
    
    public void setHooks(final ResourceManagerHooks mHooks) {
        synchronized (this) {
            this.mHooks = mHooks;
        }
    }
    
    boolean tintDrawableUsingColorFilter(final Context context, final int n, final Drawable drawable) {
        final ResourceManagerHooks mHooks = this.mHooks;
        return mHooks != null && mHooks.tintDrawableUsingColorFilter(context, n, drawable);
    }
    
    static class AsldcInflateDelegate implements InflateDelegate
    {
        @Override
        public Drawable createFromXmlInner(final Context context, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) {
            try {
                return AnimatedStateListDrawableCompat.createFromXmlInner(context, context.getResources(), xmlPullParser, set, resources$Theme);
            }
            catch (Exception ex) {
                Log.e("AsldcInflateDelegate", "Exception while inflating <animated-selector>", (Throwable)ex);
                return null;
            }
        }
    }
    
    private static class AvdcInflateDelegate implements InflateDelegate
    {
        AvdcInflateDelegate() {
        }
        
        @Override
        public Drawable createFromXmlInner(final Context context, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) {
            try {
                return AnimatedVectorDrawableCompat.createFromXmlInner(context, context.getResources(), xmlPullParser, set, resources$Theme);
            }
            catch (Exception ex) {
                Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", (Throwable)ex);
                return null;
            }
        }
    }
    
    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter>
    {
        public ColorFilterLruCache(final int n) {
            super(n);
        }
        
        private static int generateCacheKey(final int n, final PorterDuff$Mode porterDuff$Mode) {
            return (n + 31) * 31 + porterDuff$Mode.hashCode();
        }
        
        PorterDuffColorFilter get(final int n, final PorterDuff$Mode porterDuff$Mode) {
            return this.get(generateCacheKey(n, porterDuff$Mode));
        }
        
        PorterDuffColorFilter put(final int n, final PorterDuff$Mode porterDuff$Mode, final PorterDuffColorFilter porterDuffColorFilter) {
            return this.put(generateCacheKey(n, porterDuff$Mode), porterDuffColorFilter);
        }
    }
    
    private interface InflateDelegate
    {
        Drawable createFromXmlInner(final Context p0, final XmlPullParser p1, final AttributeSet p2, final Resources$Theme p3);
    }
    
    interface ResourceManagerHooks
    {
        Drawable createDrawableFor(final ResourceManagerInternal p0, final Context p1, final int p2);
        
        ColorStateList getTintListForDrawableRes(final Context p0, final int p1);
        
        PorterDuff$Mode getTintModeForDrawableRes(final int p0);
        
        boolean tintDrawable(final Context p0, final int p1, final Drawable p2);
        
        boolean tintDrawableUsingColorFilter(final Context p0, final int p1, final Drawable p2);
    }
    
    private static class VdcInflateDelegate implements InflateDelegate
    {
        VdcInflateDelegate() {
        }
        
        @Override
        public Drawable createFromXmlInner(final Context context, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) {
            try {
                return VectorDrawableCompat.createFromXmlInner(context.getResources(), xmlPullParser, set, resources$Theme);
            }
            catch (Exception ex) {
                Log.e("VdcInflateDelegate", "Exception while inflating <vector>", (Throwable)ex);
                return null;
            }
        }
    }
}
