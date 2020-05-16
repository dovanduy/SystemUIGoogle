// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.util.Xml;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Intent;
import org.xmlpull.v1.XmlPullParser;
import android.view.InflateException;
import android.util.AttributeSet;
import android.content.Context;
import java.lang.reflect.Constructor;
import java.util.HashMap;

class PreferenceInflater
{
    private static final HashMap<String, Constructor> CONSTRUCTOR_MAP;
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE;
    private final Object[] mConstructorArgs;
    private final Context mContext;
    private String[] mDefaultPackages;
    private PreferenceManager mPreferenceManager;
    
    static {
        CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class };
        CONSTRUCTOR_MAP = new HashMap<String, Constructor>();
    }
    
    public PreferenceInflater(final Context mContext, final PreferenceManager preferenceManager) {
        this.mConstructorArgs = new Object[2];
        this.mContext = mContext;
        this.init(preferenceManager);
    }
    
    private Preference createItem(final String s, final String[] array, final AttributeSet set) throws ClassNotFoundException, InflateException {
        Label_0222: {
            Constructor<?> constructor;
            if ((constructor = PreferenceInflater.CONSTRUCTOR_MAP.get(s)) != null) {
                break Label_0222;
            }
            try {
                final ClassLoader classLoader = this.mContext.getClassLoader();
                Class<?> forName2;
                if (array != null && array.length != 0) {
                    final int length = array.length;
                    final Class<?> clazz = null;
                    int n = 0;
                    final ClassNotFoundException ex = null;
                    Class<?> forName;
                    while (true) {
                        forName = clazz;
                        if (n < length) {
                            final String str = array[n];
                            try {
                                final StringBuilder sb = new StringBuilder();
                                sb.append(str);
                                sb.append(s);
                                forName = Class.forName(sb.toString(), false, classLoader);
                            }
                            catch (ClassNotFoundException ex) {
                                ++n;
                                continue;
                            }
                            break;
                        }
                        break;
                    }
                    if ((forName2 = forName) == null) {
                        if (ex == null) {
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append(set.getPositionDescription());
                            sb2.append(": Error inflating class ");
                            sb2.append(s);
                            throw new InflateException(sb2.toString());
                        }
                        throw ex;
                    }
                }
                else {
                    forName2 = Class.forName(s, false, classLoader);
                }
                constructor = forName2.getConstructor(PreferenceInflater.CONSTRUCTOR_SIGNATURE);
                constructor.setAccessible(true);
                PreferenceInflater.CONSTRUCTOR_MAP.put(s, constructor);
                final Object[] mConstructorArgs = this.mConstructorArgs;
                mConstructorArgs[1] = set;
                return (Preference)constructor.newInstance(mConstructorArgs);
            }
            catch (Exception ex3) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(set.getPositionDescription());
                sb3.append(": Error inflating class ");
                sb3.append(s);
                final InflateException ex2 = new InflateException(sb3.toString());
                ex2.initCause((Throwable)ex3);
                throw ex2;
            }
            catch (ClassNotFoundException ex4) {
                throw ex4;
            }
        }
    }
    
    private Preference createItemFromTag(String o, final AttributeSet set) {
        try {
            if (-1 == ((String)o).indexOf(46)) {
                o = this.onCreateItem((String)o, set);
            }
            else {
                o = this.createItem((String)o, null, set);
            }
            return (Preference)o;
        }
        catch (Exception ex2) {
            final StringBuilder sb = new StringBuilder();
            sb.append(set.getPositionDescription());
            sb.append(": Error inflating class ");
            sb.append((String)o);
            final InflateException ex = new InflateException(sb.toString());
            ex.initCause((Throwable)ex2);
            throw ex;
        }
        catch (ClassNotFoundException ex4) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(set.getPositionDescription());
            sb2.append(": Error inflating class (not found)");
            sb2.append((String)o);
            final InflateException ex3 = new InflateException(sb2.toString());
            ex3.initCause((Throwable)ex4);
            throw ex3;
        }
        catch (InflateException ex5) {
            throw ex5;
        }
    }
    
    private void init(final PreferenceManager mPreferenceManager) {
        this.mPreferenceManager = mPreferenceManager;
        final StringBuilder sb = new StringBuilder();
        sb.append(Preference.class.getPackage().getName());
        sb.append(".");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(SwitchPreference.class.getPackage().getName());
        sb2.append(".");
        this.setDefaultPackages(new String[] { string, sb2.toString() });
    }
    
    private PreferenceGroup onMergeRoots(final PreferenceGroup preferenceGroup, final PreferenceGroup preferenceGroup2) {
        if (preferenceGroup == null) {
            preferenceGroup2.onAttachedToHierarchy(this.mPreferenceManager);
            return preferenceGroup2;
        }
        return preferenceGroup;
    }
    
    private void rInflate(final XmlPullParser xmlPullParser, final Preference preference, final AttributeSet set) throws XmlPullParserException, IOException {
        final int depth = xmlPullParser.getDepth();
        while (true) {
            final int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next != 2) {
                continue;
            }
            final String name = xmlPullParser.getName();
            if ("intent".equals(name)) {
                try {
                    preference.setIntent(Intent.parseIntent(this.getContext().getResources(), xmlPullParser, set));
                    continue;
                }
                catch (IOException ex2) {
                    final XmlPullParserException ex = new XmlPullParserException("Error parsing preference");
                    ex.initCause((Throwable)ex2);
                    throw ex;
                }
            }
            if ("extra".equals(name)) {
                this.getContext().getResources().parseBundleExtra("extra", set, preference.getExtras());
                try {
                    skipCurrentTag(xmlPullParser);
                    continue;
                }
                catch (IOException ex4) {
                    final XmlPullParserException ex3 = new XmlPullParserException("Error parsing preference");
                    ex3.initCause((Throwable)ex4);
                    throw ex3;
                }
            }
            final Preference itemFromTag = this.createItemFromTag(name, set);
            ((PreferenceGroup)preference).addItemFromInflater(itemFromTag);
            this.rInflate(xmlPullParser, itemFromTag, set);
        }
    }
    
    private static void skipCurrentTag(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        final int depth = xmlPullParser.getDepth();
        int next;
        do {
            next = xmlPullParser.next();
        } while (next != 1 && (next != 3 || xmlPullParser.getDepth() > depth));
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    public Preference inflate(final int n, final PreferenceGroup preferenceGroup) {
        final XmlResourceParser xml = this.getContext().getResources().getXml(n);
        try {
            return this.inflate((XmlPullParser)xml, preferenceGroup);
        }
        finally {
            xml.close();
        }
    }
    
    public Preference inflate(final XmlPullParser xmlPullParser, PreferenceGroup onMergeRoots) {
        synchronized (this.mConstructorArgs) {
            final AttributeSet attributeSet = Xml.asAttributeSet(xmlPullParser);
            this.mConstructorArgs[0] = this.mContext;
            try {
                int next;
                do {
                    next = xmlPullParser.next();
                } while (next != 2 && next != 1);
                if (next == 2) {
                    onMergeRoots = this.onMergeRoots(onMergeRoots, (PreferenceGroup)this.createItemFromTag(xmlPullParser.getName(), attributeSet));
                    this.rInflate(xmlPullParser, onMergeRoots, attributeSet);
                    return onMergeRoots;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append(xmlPullParser.getPositionDescription());
                sb.append(": No start tag found!");
                throw new InflateException(sb.toString());
            }
            catch (IOException ex) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(xmlPullParser.getPositionDescription());
                sb2.append(": ");
                sb2.append(ex.getMessage());
                final InflateException ex2 = new InflateException(sb2.toString());
                ex2.initCause((Throwable)ex);
                throw ex2;
            }
            catch (XmlPullParserException ex4) {
                final InflateException ex3 = new InflateException(ex4.getMessage());
                ex3.initCause((Throwable)ex4);
                throw ex3;
            }
            catch (InflateException ex5) {
                throw ex5;
            }
        }
    }
    
    protected Preference onCreateItem(final String s, final AttributeSet set) throws ClassNotFoundException {
        return this.createItem(s, this.mDefaultPackages, set);
    }
    
    public void setDefaultPackages(final String[] mDefaultPackages) {
        this.mDefaultPackages = mDefaultPackages;
    }
}
