// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.reflect;

import java.util.logging.Level;
import com.google.common.base.StandardSystemProperty;
import java.util.Iterator;
import java.util.jar.Attributes;
import com.google.common.collect.ImmutableSet;
import java.util.jar.Manifest;
import java.net.MalformedURLException;
import com.google.common.collect.UnmodifiableIterator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import java.net.URLClassLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.HashSet;
import java.io.IOException;
import java.util.Set;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import java.net.URISyntaxException;
import com.google.common.base.Preconditions;
import java.io.File;
import java.net.URL;
import java.util.logging.Logger;
import com.google.common.base.Splitter;

public final class ClassPath
{
    private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR;
    private static final Logger logger;
    
    static {
        logger = Logger.getLogger(ClassPath.class.getName());
        CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
    }
    
    static String getClassName(final String s) {
        return s.substring(0, s.length() - 6).replace('/', '.');
    }
    
    static File toFile(final URL url) {
        Preconditions.checkArgument(url.getProtocol().equals("file"));
        try {
            return new File(url.toURI());
        }
        catch (URISyntaxException ex) {
            return new File(url.getPath());
        }
    }
    
    static final class DefaultScanner extends Scanner
    {
        private final SetMultimap<ClassLoader, String> resources;
        
        DefaultScanner() {
            this.resources = MultimapBuilder.hashKeys().linkedHashSetValues().build();
        }
        
        private void scanDirectory(final File obj, final ClassLoader classLoader, final String s, final Set<File> set) throws IOException {
            final File[] listFiles = obj.listFiles();
            if (listFiles == null) {
                final Logger access$100 = ClassPath.logger;
                final StringBuilder sb = new StringBuilder();
                sb.append("Cannot read directory ");
                sb.append(obj);
                access$100.warning(sb.toString());
                return;
            }
            for (final File file : listFiles) {
                final String name = file.getName();
                if (file.isDirectory()) {
                    final File canonicalFile = file.getCanonicalFile();
                    if (set.add(canonicalFile)) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append(s);
                        sb2.append(name);
                        sb2.append("/");
                        this.scanDirectory(canonicalFile, classLoader, sb2.toString(), set);
                        set.remove(canonicalFile);
                    }
                }
                else {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append(s);
                    sb3.append(name);
                    final String string = sb3.toString();
                    if (!string.equals("META-INF/MANIFEST.MF")) {
                        this.resources.get(classLoader).add(string);
                    }
                }
            }
        }
        
        @Override
        protected void scanDirectory(final ClassLoader classLoader, final File file) throws IOException {
            final HashSet<File> set = new HashSet<File>();
            set.add(file.getCanonicalFile());
            this.scanDirectory(file, classLoader, "", set);
        }
        
        @Override
        protected void scanJarFile(final ClassLoader classLoader, final JarFile jarFile) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory()) {
                    if (jarEntry.getName().equals("META-INF/MANIFEST.MF")) {
                        continue;
                    }
                    this.resources.get(classLoader).add(jarEntry.getName());
                }
            }
        }
    }
    
    abstract static class Scanner
    {
        private final Set<File> scannedUris;
        
        Scanner() {
            this.scannedUris = (Set<File>)Sets.newHashSet();
        }
        
        private static ImmutableList<URL> getClassLoaderUrls(final ClassLoader classLoader) {
            if (classLoader instanceof URLClassLoader) {
                return ImmutableList.copyOf(((URLClassLoader)classLoader).getURLs());
            }
            if (classLoader.equals(ClassLoader.getSystemClassLoader())) {
                return parseJavaClassPath();
            }
            return ImmutableList.of();
        }
        
        static ImmutableMap<File, ClassLoader> getClassPathEntries(final ClassLoader value) {
            final LinkedHashMap<Object, Object> linkedHashMap = Maps.newLinkedHashMap();
            final ClassLoader parent = value.getParent();
            if (parent != null) {
                linkedHashMap.putAll(getClassPathEntries(parent));
            }
            for (final URL url : getClassLoaderUrls(value)) {
                if (url.getProtocol().equals("file")) {
                    final File file = ClassPath.toFile(url);
                    if (linkedHashMap.containsKey(file)) {
                        continue;
                    }
                    linkedHashMap.put(file, value);
                }
            }
            return ImmutableMap.copyOf((Map<? extends File, ? extends ClassLoader>)linkedHashMap);
        }
        
        static URL getClassPathEntry(final File file, final String spec) throws MalformedURLException {
            return new URL(file.toURI().toURL(), spec);
        }
        
        static ImmutableSet<File> getClassPathFromManifest(final File file, Manifest str) {
            if (str == null) {
                return ImmutableSet.of();
            }
            final ImmutableSet.Builder<File> builder = ImmutableSet.builder();
            final String value = str.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
            if (value != null) {
                final Iterator<String> iterator = ClassPath.CLASS_PATH_ATTRIBUTE_SEPARATOR.split(value).iterator();
                while (iterator.hasNext()) {
                    str = (Manifest)iterator.next();
                    try {
                        final URL classPathEntry = getClassPathEntry(file, (String)str);
                        if (!classPathEntry.getProtocol().equals("file")) {
                            continue;
                        }
                        builder.add(ClassPath.toFile(classPathEntry));
                    }
                    catch (MalformedURLException ex) {
                        final Logger access$100 = ClassPath.logger;
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Invalid Class-Path entry: ");
                        sb.append((String)str);
                        access$100.warning(sb.toString());
                    }
                }
            }
            return builder.build();
        }
        
        static ImmutableList<URL> parseJavaClassPath() {
            final ImmutableList.Builder<URL> builder = ImmutableList.builder();
            for (final String str : Splitter.on(StandardSystemProperty.PATH_SEPARATOR.value()).split(StandardSystemProperty.JAVA_CLASS_PATH.value())) {
                try {
                    try {
                        builder.add(new File(str).toURI().toURL());
                    }
                    catch (MalformedURLException thrown) {}
                }
                catch (SecurityException ex) {
                    builder.add(new URL("file", null, new File(str).getAbsolutePath()));
                    continue;
                }
                final Logger access$100 = ClassPath.logger;
                final Level warning = Level.WARNING;
                final StringBuilder sb = new StringBuilder();
                sb.append("malformed classpath entry: ");
                sb.append(str);
                final MalformedURLException thrown;
                access$100.log(warning, sb.toString(), thrown);
            }
            return builder.build();
        }
        
        private void scanFrom(final File obj, final ClassLoader classLoader) throws IOException {
            try {
                if (!obj.exists()) {
                    return;
                }
                if (obj.isDirectory()) {
                    this.scanDirectory(classLoader, obj);
                }
                else {
                    this.scanJar(obj, classLoader);
                }
            }
            catch (SecurityException obj2) {
                final Logger access$100 = ClassPath.logger;
                final StringBuilder sb = new StringBuilder();
                sb.append("Cannot access ");
                sb.append(obj);
                sb.append(": ");
                sb.append(obj2);
                access$100.warning(sb.toString());
            }
        }
        
        private void scanJar(final File p0, final ClassLoader p1) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: dup            
            //     4: aload_1        
            //     5: invokespecial   java/util/jar/JarFile.<init>:(Ljava/io/File;)V
            //     8: astore_3       
            //     9: aload_1        
            //    10: aload_3        
            //    11: invokevirtual   java/util/jar/JarFile.getManifest:()Ljava/util/jar/Manifest;
            //    14: invokestatic    com/google/common/reflect/ClassPath$Scanner.getClassPathFromManifest:(Ljava/io/File;Ljava/util/jar/Manifest;)Lcom/google/common/collect/ImmutableSet;
            //    17: invokevirtual   com/google/common/collect/ImmutableCollection.iterator:()Lcom/google/common/collect/UnmodifiableIterator;
            //    20: astore_1       
            //    21: aload_1        
            //    22: invokeinterface java/util/Iterator.hasNext:()Z
            //    27: ifeq            47
            //    30: aload_0        
            //    31: aload_1        
            //    32: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
            //    37: checkcast       Ljava/io/File;
            //    40: aload_2        
            //    41: invokevirtual   com/google/common/reflect/ClassPath$Scanner.scan:(Ljava/io/File;Ljava/lang/ClassLoader;)V
            //    44: goto            21
            //    47: aload_0        
            //    48: aload_2        
            //    49: aload_3        
            //    50: invokevirtual   com/google/common/reflect/ClassPath$Scanner.scanJarFile:(Ljava/lang/ClassLoader;Ljava/util/jar/JarFile;)V
            //    53: aload_3        
            //    54: invokevirtual   java/util/jar/JarFile.close:()V
            //    57: return         
            //    58: astore_1       
            //    59: aload_3        
            //    60: invokevirtual   java/util/jar/JarFile.close:()V
            //    63: aload_1        
            //    64: athrow         
            //    65: astore_1       
            //    66: return         
            //    67: astore_1       
            //    68: goto            57
            //    71: astore_2       
            //    72: goto            63
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  0      9      65     67     Ljava/io/IOException;
            //  9      21     58     65     Any
            //  21     44     58     65     Any
            //  47     53     58     65     Any
            //  53     57     67     71     Ljava/io/IOException;
            //  59     63     71     75     Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IndexOutOfBoundsException: Index 39 out of bounds for length 39
            //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
            //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
            //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
            //     at java.base/java.util.Objects.checkIndex(Objects.java:372)
            //     at java.base/java.util.ArrayList.get(ArrayList.java:458)
            //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3321)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:113)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        final void scan(final File file, final ClassLoader classLoader) throws IOException {
            if (this.scannedUris.add(file.getCanonicalFile())) {
                this.scanFrom(file, classLoader);
            }
        }
        
        protected abstract void scanDirectory(final ClassLoader p0, final File p1) throws IOException;
        
        protected abstract void scanJarFile(final ClassLoader p0, final JarFile p1) throws IOException;
    }
}
