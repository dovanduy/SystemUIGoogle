// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.util.FloatProperty;
import java.util.function.Function;
import java.util.function.BiConsumer;
import android.util.Property;
import com.android.systemui.R$id;
import android.view.View;

public abstract class AnimatableProperty
{
    public static final AnimatableProperty X;
    public static final AnimatableProperty Y;
    
    static {
        X = from((android.util.Property<View, Float>)View.X, R$id.x_animator_tag, R$id.x_animator_tag_start_value, R$id.x_animator_tag_end_value);
        Y = from((android.util.Property<View, Float>)View.Y, R$id.y_animator_tag, R$id.y_animator_tag_start_value, R$id.y_animator_tag_end_value);
    }
    
    public static <T extends View> AnimatableProperty from(final Property<T, Float> property, final int n, final int n2, final int n3) {
        return new AnimatableProperty() {
            @Override
            public int getAnimationEndTag() {
                return n3;
            }
            
            @Override
            public int getAnimationStartTag() {
                return n2;
            }
            
            @Override
            public int getAnimatorTag() {
                return n;
            }
            
            @Override
            public Property getProperty() {
                return property;
            }
        };
    }
    
    public static <T extends View> AnimatableProperty from(final String s, final BiConsumer<T, Float> biConsumer, final Function<T, Float> function, final int n, final int n2, final int n3) {
        return new AnimatableProperty() {
            final /* synthetic */ Property val$property = new FloatProperty<T>(s, function, biConsumer) {
                final /* synthetic */ Function val$getter;
                final /* synthetic */ BiConsumer val$setter;
                
                public Float get(final T t) {
                    return this.val$getter.apply(t);
                }
                
                public void setValue(final T t, final float f) {
                    this.val$setter.accept(t, f);
                }
            };
            
            @Override
            public int getAnimationEndTag() {
                return n3;
            }
            
            @Override
            public int getAnimationStartTag() {
                return n2;
            }
            
            @Override
            public int getAnimatorTag() {
                return n;
            }
            
            @Override
            public Property getProperty() {
                return this.val$property;
            }
        };
    }
    
    public abstract int getAnimationEndTag();
    
    public abstract int getAnimationStartTag();
    
    public abstract int getAnimatorTag();
    
    public abstract Property getProperty();
}
