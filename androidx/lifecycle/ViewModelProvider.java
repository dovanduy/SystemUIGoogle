// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

public class ViewModelProvider
{
    private final Factory mFactory;
    private final ViewModelStore mViewModelStore;
    
    public ViewModelProvider(final ViewModelStore mViewModelStore, final Factory mFactory) {
        this.mFactory = mFactory;
        this.mViewModelStore = mViewModelStore;
    }
    
    public <T extends ViewModel> T get(final Class<T> clazz) {
        final String canonicalName = clazz.getCanonicalName();
        if (canonicalName != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("androidx.lifecycle.ViewModelProvider.DefaultKey:");
            sb.append(canonicalName);
            return this.get(sb.toString(), clazz);
        }
        throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
    }
    
    public <T extends ViewModel> T get(final String s, final Class<T> clazz) {
        final ViewModel value = this.mViewModelStore.get(s);
        if (clazz.isInstance(value)) {
            final Factory mFactory = this.mFactory;
            if (mFactory instanceof OnRequeryFactory) {
                ((OnRequeryFactory)mFactory).onRequery(value);
            }
            return (T)value;
        }
        final Factory mFactory2 = this.mFactory;
        ViewModel viewModel;
        if (mFactory2 instanceof KeyedFactory) {
            viewModel = ((KeyedFactory)mFactory2).create(s, clazz);
        }
        else {
            viewModel = mFactory2.create((Class<T>)clazz);
        }
        this.mViewModelStore.put(s, viewModel);
        return (T)viewModel;
    }
    
    public interface Factory
    {
         <T extends ViewModel> T create(final Class<T> p0);
    }
    
    abstract static class KeyedFactory extends OnRequeryFactory implements Factory
    {
        @Override
        public <T extends ViewModel> T create(final Class<T> clazz) {
            throw new UnsupportedOperationException("create(String, Class<?>) must be called on implementaions of KeyedFactory");
        }
        
        public abstract <T extends ViewModel> T create(final String p0, final Class<T> p1);
    }
    
    static class OnRequeryFactory
    {
        void onRequery(final ViewModel viewModel) {
        }
    }
}
