// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import java.util.Objects;
import android.view.WindowInsets$Builder;
import android.graphics.Rect;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Preconditions;
import androidx.core.graphics.Insets;
import android.os.Build$VERSION;
import android.view.WindowInsets;

public class WindowInsetsCompat
{
    static final WindowInsetsCompat EMPTY;
    private final Impl mImpl;
    
    static {
        EMPTY = new WindowInsetsCompat((WindowInsetsCompat)null);
    }
    
    private WindowInsetsCompat(final WindowInsets windowInsets) {
        if (windowInsets == null) {
            this.mImpl = new Impl();
        }
        else {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 29) {
                this.mImpl = (Impl)new Impl29(windowInsets);
            }
            else if (sdk_INT >= 21) {
                this.mImpl = (Impl)new Impl21(windowInsets);
            }
            else if (sdk_INT >= 20) {
                this.mImpl = (Impl)new Impl20(windowInsets);
            }
            else {
                this.mImpl = new Impl();
            }
        }
    }
    
    public WindowInsetsCompat(final WindowInsetsCompat windowInsetsCompat) {
        if (windowInsetsCompat != null) {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 29) {
                this.mImpl = (Impl)new Impl29(windowInsetsCompat.toWindowInsets());
            }
            else if (sdk_INT >= 21) {
                this.mImpl = (Impl)new Impl21(windowInsetsCompat.toWindowInsets());
            }
            else if (sdk_INT >= 20) {
                this.mImpl = (Impl)new Impl20(windowInsetsCompat.toWindowInsets());
            }
            else {
                this.mImpl = new Impl();
            }
        }
        else {
            this.mImpl = new Impl();
        }
    }
    
    static Insets insetInsets(final Insets insets, final int n, final int n2, final int n3, final int n4) {
        final int max = Math.max(0, insets.left - n);
        final int max2 = Math.max(0, insets.top - n2);
        final int max3 = Math.max(0, insets.right - n3);
        final int max4 = Math.max(0, insets.bottom - n4);
        if (max == n && max2 == n2 && max3 == n3 && max4 == n4) {
            return insets;
        }
        return Insets.of(max, max2, max3, max4);
    }
    
    public static WindowInsetsCompat toWindowInsetsCompat(final WindowInsets windowInsets) {
        Preconditions.checkNotNull(windowInsets);
        return new WindowInsetsCompat(windowInsets);
    }
    
    public WindowInsetsCompat consumeDisplayCutout() {
        return this.mImpl.consumeDisplayCutout();
    }
    
    public WindowInsetsCompat consumeStableInsets() {
        return this.mImpl.consumeStableInsets();
    }
    
    public WindowInsetsCompat consumeSystemWindowInsets() {
        return this.mImpl.consumeSystemWindowInsets();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof WindowInsetsCompat && ObjectsCompat.equals(this.mImpl, ((WindowInsetsCompat)o).mImpl));
    }
    
    public Insets getSystemGestureInsets() {
        return this.mImpl.getSystemGestureInsets();
    }
    
    public int getSystemWindowInsetBottom() {
        return this.getSystemWindowInsets().bottom;
    }
    
    public int getSystemWindowInsetLeft() {
        return this.getSystemWindowInsets().left;
    }
    
    public int getSystemWindowInsetRight() {
        return this.getSystemWindowInsets().right;
    }
    
    public int getSystemWindowInsetTop() {
        return this.getSystemWindowInsets().top;
    }
    
    public Insets getSystemWindowInsets() {
        return this.mImpl.getSystemWindowInsets();
    }
    
    @Override
    public int hashCode() {
        final Impl mImpl = this.mImpl;
        int hashCode;
        if (mImpl == null) {
            hashCode = 0;
        }
        else {
            hashCode = mImpl.hashCode();
        }
        return hashCode;
    }
    
    public WindowInsetsCompat inset(final int n, final int n2, final int n3, final int n4) {
        return this.mImpl.inset(n, n2, n3, n4);
    }
    
    public boolean isConsumed() {
        return this.mImpl.isConsumed();
    }
    
    @Deprecated
    public WindowInsetsCompat replaceSystemWindowInsets(final int n, final int n2, final int n3, final int n4) {
        final Builder builder = new Builder(this);
        builder.setSystemWindowInsets(Insets.of(n, n2, n3, n4));
        return builder.build();
    }
    
    public WindowInsets toWindowInsets() {
        return ((Impl20)this.mImpl).mPlatformInsets;
    }
    
    public static final class Builder
    {
        private final BuilderImpl mImpl;
        
        public Builder() {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 29) {
                this.mImpl = new BuilderImpl29();
            }
            else if (sdk_INT >= 20) {
                this.mImpl = new BuilderImpl20();
            }
            else {
                this.mImpl = new BuilderImpl();
            }
        }
        
        public Builder(final WindowInsetsCompat windowInsetsCompat) {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 29) {
                this.mImpl = new BuilderImpl29(windowInsetsCompat);
            }
            else if (sdk_INT >= 20) {
                this.mImpl = new BuilderImpl20(windowInsetsCompat);
            }
            else {
                this.mImpl = new BuilderImpl(windowInsetsCompat);
            }
        }
        
        public WindowInsetsCompat build() {
            return this.mImpl.build();
        }
        
        public Builder setStableInsets(final Insets stableInsets) {
            this.mImpl.setStableInsets(stableInsets);
            return this;
        }
        
        public Builder setSystemWindowInsets(final Insets systemWindowInsets) {
            this.mImpl.setSystemWindowInsets(systemWindowInsets);
            return this;
        }
    }
    
    private static class BuilderImpl
    {
        private WindowInsetsCompat mInsets;
        
        BuilderImpl() {
            this(WindowInsetsCompat.EMPTY);
        }
        
        BuilderImpl(final WindowInsetsCompat mInsets) {
            this.mInsets = mInsets;
        }
        
        WindowInsetsCompat build() {
            return this.mInsets;
        }
        
        void setStableInsets(final Insets insets) {
        }
        
        void setSystemWindowInsets(final Insets insets) {
        }
    }
    
    private static class BuilderImpl20 extends BuilderImpl
    {
        private static Constructor<WindowInsets> sConstructor;
        private static boolean sConstructorFetched = false;
        private static Field sConsumedField;
        private static boolean sConsumedFieldFetched = false;
        private WindowInsets mInsets;
        
        BuilderImpl20() {
            this.mInsets = createWindowInsetsInstance();
        }
        
        BuilderImpl20(final WindowInsetsCompat windowInsetsCompat) {
            this.mInsets = windowInsetsCompat.toWindowInsets();
        }
        
        private static WindowInsets createWindowInsetsInstance() {
            if (!BuilderImpl20.sConsumedFieldFetched) {
                try {
                    BuilderImpl20.sConsumedField = WindowInsets.class.getDeclaredField("CONSUMED");
                }
                catch (ReflectiveOperationException ex) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets.CONSUMED field", (Throwable)ex);
                }
                BuilderImpl20.sConsumedFieldFetched = true;
            }
            final Field sConsumedField = BuilderImpl20.sConsumedField;
            if (sConsumedField != null) {
                try {
                    final WindowInsets windowInsets = (WindowInsets)sConsumedField.get(null);
                    if (windowInsets != null) {
                        return new WindowInsets(windowInsets);
                    }
                }
                catch (ReflectiveOperationException ex2) {
                    Log.i("WindowInsetsCompat", "Could not get value from WindowInsets.CONSUMED field", (Throwable)ex2);
                }
            }
            if (!BuilderImpl20.sConstructorFetched) {
                try {
                    BuilderImpl20.sConstructor = WindowInsets.class.getConstructor(Rect.class);
                }
                catch (ReflectiveOperationException ex3) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets(Rect) constructor", (Throwable)ex3);
                }
                BuilderImpl20.sConstructorFetched = true;
            }
            final Constructor<WindowInsets> sConstructor = BuilderImpl20.sConstructor;
            if (sConstructor != null) {
                try {
                    return sConstructor.newInstance(new Rect());
                }
                catch (ReflectiveOperationException ex4) {
                    Log.i("WindowInsetsCompat", "Could not invoke WindowInsets(Rect) constructor", (Throwable)ex4);
                }
            }
            return null;
        }
        
        @Override
        WindowInsetsCompat build() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mInsets);
        }
        
        @Override
        void setSystemWindowInsets(final Insets insets) {
            final WindowInsets mInsets = this.mInsets;
            if (mInsets != null) {
                this.mInsets = mInsets.replaceSystemWindowInsets(insets.left, insets.top, insets.right, insets.bottom);
            }
        }
    }
    
    private static class BuilderImpl29 extends BuilderImpl
    {
        final WindowInsets$Builder mPlatBuilder;
        
        BuilderImpl29() {
            this.mPlatBuilder = new WindowInsets$Builder();
        }
        
        BuilderImpl29(final WindowInsetsCompat windowInsetsCompat) {
            this.mPlatBuilder = new WindowInsets$Builder(windowInsetsCompat.toWindowInsets());
        }
        
        @Override
        WindowInsetsCompat build() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatBuilder.build());
        }
        
        @Override
        void setStableInsets(final Insets insets) {
            this.mPlatBuilder.setStableInsets(insets.toPlatformInsets());
        }
        
        @Override
        void setSystemWindowInsets(final Insets insets) {
            this.mPlatBuilder.setSystemWindowInsets(insets.toPlatformInsets());
        }
    }
    
    private static class Impl
    {
        Impl() {
        }
        
        WindowInsetsCompat consumeDisplayCutout() {
            return WindowInsetsCompat.EMPTY;
        }
        
        WindowInsetsCompat consumeStableInsets() {
            return WindowInsetsCompat.EMPTY;
        }
        
        WindowInsetsCompat consumeSystemWindowInsets() {
            return WindowInsetsCompat.EMPTY;
        }
        
        Insets getStableInsets() {
            return Insets.NONE;
        }
        
        Insets getSystemGestureInsets() {
            return this.getSystemWindowInsets();
        }
        
        Insets getSystemWindowInsets() {
            return Insets.NONE;
        }
        
        WindowInsetsCompat inset(final int n, final int n2, final int n3, final int n4) {
            return WindowInsetsCompat.EMPTY;
        }
        
        boolean isConsumed() {
            return false;
        }
    }
    
    private static class Impl20 extends Impl
    {
        final WindowInsets mPlatformInsets;
        private Insets mSystemWindowInsets;
        
        Impl20(final WindowInsets mPlatformInsets) {
            this.mSystemWindowInsets = null;
            this.mPlatformInsets = mPlatformInsets;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj || (obj instanceof Impl20 && super.equals(obj) && this.mPlatformInsets.equals((Object)((Impl20)obj).mPlatformInsets));
        }
        
        @Override
        final Insets getSystemWindowInsets() {
            if (this.mSystemWindowInsets == null) {
                this.mSystemWindowInsets = Insets.of(this.mPlatformInsets.getSystemWindowInsetLeft(), this.mPlatformInsets.getSystemWindowInsetTop(), this.mPlatformInsets.getSystemWindowInsetRight(), this.mPlatformInsets.getSystemWindowInsetBottom());
            }
            return this.mSystemWindowInsets;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.mPlatformInsets);
        }
        
        @Override
        WindowInsetsCompat inset(final int n, final int n2, final int n3, final int n4) {
            final Builder builder = new Builder(WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets));
            builder.setSystemWindowInsets(WindowInsetsCompat.insetInsets(this.getSystemWindowInsets(), n, n2, n3, n4));
            builder.setStableInsets(WindowInsetsCompat.insetInsets(((Impl)this).getStableInsets(), n, n2, n3, n4));
            return builder.build();
        }
    }
    
    private static class Impl21 extends Impl20
    {
        private Insets mStableInsets;
        
        Impl21(final WindowInsets windowInsets) {
            super(windowInsets);
            this.mStableInsets = null;
        }
        
        @Override
        WindowInsetsCompat consumeStableInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(super.mPlatformInsets.consumeStableInsets());
        }
        
        @Override
        WindowInsetsCompat consumeSystemWindowInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(super.mPlatformInsets.consumeSystemWindowInsets());
        }
        
        @Override
        final Insets getStableInsets() {
            if (this.mStableInsets == null) {
                this.mStableInsets = Insets.of(super.mPlatformInsets.getStableInsetLeft(), super.mPlatformInsets.getStableInsetTop(), super.mPlatformInsets.getStableInsetRight(), super.mPlatformInsets.getStableInsetBottom());
            }
            return this.mStableInsets;
        }
        
        @Override
        boolean isConsumed() {
            return super.mPlatformInsets.isConsumed();
        }
    }
    
    private static class Impl28 extends Impl21
    {
        Impl28(final WindowInsets windowInsets) {
            super(windowInsets);
        }
        
        @Override
        WindowInsetsCompat consumeDisplayCutout() {
            return WindowInsetsCompat.toWindowInsetsCompat(super.mPlatformInsets.consumeDisplayCutout());
        }
    }
    
    private static class Impl29 extends Impl28
    {
        private Insets mSystemGestureInsets;
        
        Impl29(final WindowInsets windowInsets) {
            super(windowInsets);
            this.mSystemGestureInsets = null;
        }
        
        @Override
        Insets getSystemGestureInsets() {
            if (this.mSystemGestureInsets == null) {
                this.mSystemGestureInsets = Insets.toCompatInsets(super.mPlatformInsets.getSystemGestureInsets());
            }
            return this.mSystemGestureInsets;
        }
        
        @Override
        WindowInsetsCompat inset(final int n, final int n2, final int n3, final int n4) {
            return WindowInsetsCompat.toWindowInsetsCompat(super.mPlatformInsets.inset(n, n2, n3, n4));
        }
    }
}
