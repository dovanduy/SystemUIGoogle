// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Annotation;

@Repeatable(Dependencies.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependsOn {
    Class<?> target();
}
