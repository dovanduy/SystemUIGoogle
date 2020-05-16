// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.plugins;

import java.util.function.BiConsumer;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.Requirements;
import com.android.systemui.plugins.annotations.Requires;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import android.util.ArrayMap;

public class VersionInfo
{
    private Class<?> mDefault;
    private final ArrayMap<Class<?>, Version> mVersions;
    
    public VersionInfo() {
        this.mVersions = (ArrayMap<Class<?>, Version>)new ArrayMap();
    }
    
    private void addClass(final Class<?> clazz, final boolean b) {
        if (this.mVersions.containsKey((Object)clazz)) {
            return;
        }
        final ProvidesInterface providesInterface = clazz.getDeclaredAnnotation(ProvidesInterface.class);
        if (providesInterface != null) {
            this.mVersions.put((Object)clazz, (Object)new Version(providesInterface.version(), true));
        }
        final Requires requires = clazz.getDeclaredAnnotation(Requires.class);
        if (requires != null) {
            this.mVersions.put((Object)requires.target(), (Object)new Version(requires.version(), b));
        }
        final Requirements requirements = clazz.getDeclaredAnnotation(Requirements.class);
        final int n = 0;
        if (requirements != null) {
            for (final Requires requires2 : requirements.value()) {
                this.mVersions.put((Object)requires2.target(), (Object)new Version(requires2.version(), b));
            }
        }
        final DependsOn dependsOn = clazz.getDeclaredAnnotation(DependsOn.class);
        if (dependsOn != null) {
            this.addClass(dependsOn.target(), true);
        }
        final Dependencies dependencies = clazz.getDeclaredAnnotation(Dependencies.class);
        if (dependencies != null) {
            final DependsOn[] value2 = dependencies.value();
            for (int length2 = value2.length, j = n; j < length2; ++j) {
                this.addClass(value2[j].target(), true);
            }
        }
    }
    
    private Version createVersion(final Class<?> clazz) {
        final ProvidesInterface providesInterface = clazz.getDeclaredAnnotation(ProvidesInterface.class);
        if (providesInterface != null) {
            return new Version(providesInterface.version(), false);
        }
        return null;
    }
    
    public VersionInfo addClass(final Class<?> mDefault) {
        if (this.mDefault == null) {
            this.mDefault = mDefault;
        }
        this.addClass(mDefault, false);
        return this;
    }
    
    public void checkVersion(final VersionInfo versionInfo) throws InvalidVersionException {
        final ArrayMap arrayMap = new ArrayMap((ArrayMap)this.mVersions);
        versionInfo.mVersions.forEach((BiConsumer)new BiConsumer<Class<?>, Version>() {
            @Override
            public void accept(final Class<?> clazz, final Version version) {
                Object access$100;
                if ((access$100 = arrayMap.remove((Object)clazz)) == null) {
                    access$100 = VersionInfo.this.createVersion(clazz);
                }
                boolean b = false;
                if (access$100 == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(clazz.getSimpleName());
                    sb.append(" does not provide an interface");
                    throw new InvalidVersionException(sb.toString(), false);
                }
                if (((Version)access$100).mVersion != version.mVersion) {
                    if (((Version)access$100).mVersion < version.mVersion) {
                        b = true;
                    }
                    throw new InvalidVersionException(clazz, b, ((Version)access$100).mVersion, version.mVersion);
                }
            }
        });
        arrayMap.forEach((BiConsumer)new BiConsumer<Class<?>, Version>(this) {
            @Override
            public void accept(final Class<?> clazz, final Version version) {
                if (!version.mRequired) {
                    return;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Missing required dependency ");
                sb.append(clazz.getSimpleName());
                throw new InvalidVersionException(sb.toString(), false);
            }
        });
    }
    
    public int getDefaultVersion() {
        return ((Version)this.mVersions.get((Object)this.mDefault)).mVersion;
    }
    
    public <T> boolean hasClass(final Class<T> clazz) {
        return this.mVersions.containsKey((Object)clazz);
    }
    
    public boolean hasVersionInfo() {
        return this.mVersions.isEmpty() ^ true;
    }
    
    public static class InvalidVersionException extends RuntimeException
    {
        private final boolean mTooNew;
        
        public InvalidVersionException(final Class<?> clazz, final boolean mTooNew, final int i, final int j) {
            final StringBuilder sb = new StringBuilder();
            sb.append(clazz.getSimpleName());
            sb.append(" expected version ");
            sb.append(i);
            sb.append(" but had ");
            sb.append(j);
            super(sb.toString());
            this.mTooNew = mTooNew;
        }
        
        public InvalidVersionException(final String message, final boolean mTooNew) {
            super(message);
            this.mTooNew = mTooNew;
        }
        
        public boolean isTooNew() {
            return this.mTooNew;
        }
    }
    
    private static class Version
    {
        private final boolean mRequired;
        private final int mVersion;
        
        public Version(final int mVersion, final boolean mRequired) {
            this.mVersion = mVersion;
            this.mRequired = mRequired;
        }
    }
}
