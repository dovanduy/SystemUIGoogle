// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
public @interface Requirements {
    Requires[] value();
}
