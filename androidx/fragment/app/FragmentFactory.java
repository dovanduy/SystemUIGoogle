// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import androidx.collection.SimpleArrayMap;

public class FragmentFactory
{
    private static final SimpleArrayMap<String, Class<?>> sClassMap;
    
    static {
        sClassMap = new SimpleArrayMap<String, Class<?>>();
    }
    
    static boolean isFragmentClass(final ClassLoader classLoader, final String s) {
        try {
            return Fragment.class.isAssignableFrom(loadClass(classLoader, s));
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    private static Class<?> loadClass(final ClassLoader loader, final String name) throws ClassNotFoundException {
        Class<?> forName;
        if ((forName = FragmentFactory.sClassMap.get(name)) == null) {
            forName = Class.forName(name, false, loader);
            FragmentFactory.sClassMap.put(name, forName);
        }
        return forName;
    }
    
    public static Class<? extends Fragment> loadFragmentClass(final ClassLoader classLoader, final String s) {
        try {
            return (Class<? extends Fragment>)loadClass(classLoader, s);
        }
        catch (ClassCastException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unable to instantiate fragment ");
            sb.append(s);
            sb.append(": make sure class is a valid subclass of Fragment");
            throw new Fragment.InstantiationException(sb.toString(), ex);
        }
        catch (ClassNotFoundException ex2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Unable to instantiate fragment ");
            sb2.append(s);
            sb2.append(": make sure class name exists");
            throw new Fragment.InstantiationException(sb2.toString(), ex2);
        }
    }
    
    public abstract Fragment instantiate(final ClassLoader p0, final String p1);
}
