// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.animation;

import java.util.Map;
import androidx.dynamicanimation.animation.SpringForce;
import kotlin.TypeCastException;
import java.util.List;
import android.os.Looper;
import android.util.Log;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.FloatCompanionObject;
import kotlin.collections.ArraysKt;
import java.util.Iterator;
import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import android.util.ArrayMap;
import kotlin.jvm.functions.Function0;
import java.util.ArrayList;
import kotlin.Unit;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import java.util.Set;
import kotlin.jvm.functions.Function1;

public final class PhysicsAnimator<T>
{
    public static final Companion Companion;
    private static Function1<Object, ? extends PhysicsAnimator<?>> instanceConstructor;
    private Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> cancelAction;
    private final ArrayList<Function0<Unit>> endActions;
    private final ArrayList<EndListener<T>> endListeners;
    private final ArrayMap<FloatPropertyCompat<? super T>, FlingAnimation> flingAnimations;
    private final ArrayMap<FloatPropertyCompat<? super T>, FlingConfig> flingConfigs;
    private ArrayList<InternalListener> internalListeners;
    private final ArrayMap<FloatPropertyCompat<? super T>, SpringAnimation> springAnimations;
    private final ArrayMap<FloatPropertyCompat<? super T>, SpringConfig> springConfigs;
    private Function0<Unit> startAction;
    private final T target;
    private final ArrayList<UpdateListener<T>> updateListeners;
    
    static {
        Companion = new Companion(null);
        PhysicsAnimator.instanceConstructor = (Function1<Object, ? extends PhysicsAnimator<?>>)PhysicsAnimator$Companion$instanceConstructor.PhysicsAnimator$Companion$instanceConstructor$1.INSTANCE;
    }
    
    private PhysicsAnimator(final T target) {
        this.target = target;
        this.springAnimations = (ArrayMap<FloatPropertyCompat<? super T>, SpringAnimation>)new ArrayMap();
        this.flingAnimations = (ArrayMap<FloatPropertyCompat<? super T>, FlingAnimation>)new ArrayMap();
        this.springConfigs = (ArrayMap<FloatPropertyCompat<? super T>, SpringConfig>)new ArrayMap();
        this.flingConfigs = (ArrayMap<FloatPropertyCompat<? super T>, FlingConfig>)new ArrayMap();
        this.updateListeners = new ArrayList<UpdateListener<T>>();
        this.endListeners = new ArrayList<EndListener<T>>();
        this.endActions = new ArrayList<Function0<Unit>>();
        this.internalListeners = new ArrayList<InternalListener>();
        this.startAction = (Function0<Unit>)new PhysicsAnimator$startAction.PhysicsAnimator$startAction$1(this);
        this.cancelAction = (Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit>)new PhysicsAnimator$cancelAction.PhysicsAnimator$cancelAction$1(this);
    }
    
    public static final /* synthetic */ Function1 access$getInstanceConstructor$cp() {
        return PhysicsAnimator.instanceConstructor;
    }
    
    private final void clearAnimator() {
        this.springConfigs.clear();
        this.flingConfigs.clear();
        this.updateListeners.clear();
        this.endListeners.clear();
        this.endActions.clear();
    }
    
    private final DynamicAnimation<?> configureDynamicAnimation(final DynamicAnimation<?> dynamicAnimation, final FloatPropertyCompat<? super T> floatPropertyCompat) {
        dynamicAnimation.addUpdateListener((DynamicAnimation.OnAnimationUpdateListener)new PhysicsAnimator$configureDynamicAnimation.PhysicsAnimator$configureDynamicAnimation$1(this, (FloatPropertyCompat)floatPropertyCompat));
        dynamicAnimation.addEndListener((DynamicAnimation.OnAnimationEndListener)new PhysicsAnimator$configureDynamicAnimation.PhysicsAnimator$configureDynamicAnimation$2(this, (FloatPropertyCompat)floatPropertyCompat, (DynamicAnimation)dynamicAnimation));
        return dynamicAnimation;
    }
    
    public static final float estimateFlingEndValue(final float n, final float n2, final FlingConfig flingConfig) {
        return PhysicsAnimator.Companion.estimateFlingEndValue(n, n2, flingConfig);
    }
    
    public static /* synthetic */ PhysicsAnimator flingThenSpring$default(final PhysicsAnimator physicsAnimator, final FloatPropertyCompat floatPropertyCompat, final float n, final FlingConfig flingConfig, final SpringConfig springConfig, boolean b, final int n2, final Object o) {
        if ((n2 & 0x10) != 0x0) {
            b = false;
        }
        physicsAnimator.flingThenSpring(floatPropertyCompat, n, flingConfig, springConfig, b);
        return physicsAnimator;
    }
    
    private final FlingAnimation getFlingAnimation(final FloatPropertyCompat<? super T> floatPropertyCompat) {
        final ArrayMap<FloatPropertyCompat<? super T>, FlingAnimation> flingAnimations = this.flingAnimations;
        FlingAnimation value;
        if ((value = ((Map<FloatPropertyCompat<? super T>, FlingAnimation>)flingAnimations).get(floatPropertyCompat)) == null) {
            final FlingAnimation flingAnimation = new FlingAnimation((K)this.target, (FloatPropertyCompat<K>)floatPropertyCompat);
            this.configureDynamicAnimation(flingAnimation, floatPropertyCompat);
            value = flingAnimation;
            ((Map<FloatPropertyCompat<? super T>, FlingAnimation>)flingAnimations).put(floatPropertyCompat, value);
        }
        Intrinsics.checkExpressionValueIsNotNull(value, "flingAnimations.getOrPut\u2026     as FlingAnimation })");
        return value;
    }
    
    public static final <T> PhysicsAnimator<T> getInstance(final T t) {
        return PhysicsAnimator.Companion.getInstance(t);
    }
    
    private final SpringAnimation getSpringAnimation(final FloatPropertyCompat<? super T> floatPropertyCompat) {
        final ArrayMap<FloatPropertyCompat<? super T>, SpringAnimation> springAnimations = this.springAnimations;
        SpringAnimation value;
        if ((value = ((Map<FloatPropertyCompat<? super T>, SpringAnimation>)springAnimations).get(floatPropertyCompat)) == null) {
            final SpringAnimation springAnimation = new SpringAnimation((K)this.target, (FloatPropertyCompat<K>)floatPropertyCompat);
            this.configureDynamicAnimation(springAnimation, floatPropertyCompat);
            value = springAnimation;
            ((Map<FloatPropertyCompat<? super T>, SpringAnimation>)springAnimations).put(floatPropertyCompat, value);
        }
        Intrinsics.checkExpressionValueIsNotNull(value, "springAnimations.getOrPu\u2026    as SpringAnimation })");
        return value;
    }
    
    public final PhysicsAnimator<T> addEndListener(final EndListener<T> e) {
        Intrinsics.checkParameterIsNotNull(e, "listener");
        this.endListeners.add(e);
        return this;
    }
    
    public final PhysicsAnimator<T> addUpdateListener(final UpdateListener<T> e) {
        Intrinsics.checkParameterIsNotNull(e, "listener");
        this.updateListeners.add(e);
        return this;
    }
    
    public final boolean arePropertiesAnimating(final Set<? extends FloatPropertyCompat<? super T>> set) {
        Intrinsics.checkParameterIsNotNull(set, "properties");
        final boolean b = set instanceof Collection;
        final boolean b2 = false;
        boolean b3;
        if (b && set.isEmpty()) {
            b3 = b2;
        }
        else {
            final Iterator<Object> iterator = set.iterator();
            do {
                b3 = b2;
                if (iterator.hasNext()) {
                    continue;
                }
                return b3;
            } while (!this.isPropertyAnimating(iterator.next()));
            b3 = true;
        }
        return b3;
    }
    
    public final void cancel() {
        final Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> cancelAction = this.cancelAction;
        final Set keySet = this.flingAnimations.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet, "flingAnimations.keys");
        cancelAction.invoke(keySet);
        final Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> cancelAction2 = this.cancelAction;
        final Set keySet2 = this.springAnimations.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet2, "springAnimations.keys");
        cancelAction2.invoke(keySet2);
    }
    
    public final void cancel(final FloatPropertyCompat<? super T>... array) {
        Intrinsics.checkParameterIsNotNull(array, "properties");
        this.cancelAction.invoke(ArraysKt.toSet(array));
    }
    
    public final void cancelInternal$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final Set<? extends FloatPropertyCompat<? super T>> set) {
        Intrinsics.checkParameterIsNotNull(set, "properties");
        for (final FloatPropertyCompat floatPropertyCompat : set) {
            final FlingAnimation flingAnimation = (FlingAnimation)this.flingAnimations.get((Object)floatPropertyCompat);
            if (flingAnimation != null) {
                flingAnimation.cancel();
            }
            final SpringAnimation springAnimation = (SpringAnimation)this.springAnimations.get((Object)floatPropertyCompat);
            if (springAnimation != null) {
                springAnimation.cancel();
            }
        }
    }
    
    public final PhysicsAnimator<T> flingThenSpring(final FloatPropertyCompat<? super T> floatPropertyCompat, final float n, final FlingConfig flingConfig, final SpringConfig springConfig) {
        flingThenSpring$default(this, floatPropertyCompat, n, flingConfig, springConfig, false, 16, null);
        return this;
    }
    
    public final PhysicsAnimator<T> flingThenSpring(final FloatPropertyCompat<? super T> floatPropertyCompat, final float startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core, final FlingConfig flingConfig, SpringConfig copy$default, final boolean b) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        Intrinsics.checkParameterIsNotNull(flingConfig, "flingConfig");
        Intrinsics.checkParameterIsNotNull(copy$default, "springConfig");
        final FlingConfig copy$default2 = FlingConfig.copy$default(flingConfig, 0.0f, 0.0f, 0.0f, 0.0f, 15, null);
        copy$default = SpringConfig.copy$default(copy$default, 0.0f, 0.0f, 0.0f, 0.0f, 15, null);
        float finalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core;
        if (startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core < 0) {
            finalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core = flingConfig.getMin$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
        }
        else {
            finalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core = flingConfig.getMax$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
        }
        if (b && finalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core != -FloatCompanionObject.INSTANCE.getMAX_VALUE() && finalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core != FloatCompanionObject.INSTANCE.getMAX_VALUE()) {
            final float n = finalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core - floatPropertyCompat.getValue(this.target);
            final float n2 = flingConfig.getFriction$frameworks__base__packages__SystemUI__android_common__SystemUI_core() * 4.2f * n;
            float startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core2;
            if (n > 0.0f && startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core >= 0.0f) {
                startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core2 = Math.max(n2, startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core);
            }
            else {
                startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core2 = startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core;
                if (n < 0.0f) {
                    startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core2 = startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core;
                    if (startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core <= 0.0f) {
                        startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core2 = Math.min(n2, startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core);
                    }
                }
            }
            copy$default2.setStartVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core(startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core2);
            copy$default.setFinalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core(finalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core);
        }
        else {
            copy$default2.setStartVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core(startVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core);
        }
        this.flingConfigs.put(floatPropertyCompat, copy$default2);
        this.springConfigs.put(floatPropertyCompat, copy$default);
        return this;
    }
    
    public final Set<FloatPropertyCompat<? super T>> getAnimatedProperties$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
        final Set keySet = this.springConfigs.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet, "springConfigs.keys");
        final Set keySet2 = this.flingConfigs.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet2, "flingConfigs.keys");
        return (Set<FloatPropertyCompat<? super T>>)CollectionsKt.union((Iterable<?>)keySet, (Iterable<?>)keySet2);
    }
    
    public final ArrayList<InternalListener> getInternalListeners$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
        return this.internalListeners;
    }
    
    public final T getTarget() {
        return this.target;
    }
    
    public final boolean isPropertyAnimating(final FloatPropertyCompat<? super T> floatPropertyCompat) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        final SpringAnimation springAnimation = (SpringAnimation)this.springAnimations.get((Object)floatPropertyCompat);
        boolean b = false;
        if (springAnimation == null || !springAnimation.isRunning()) {
            final FlingAnimation flingAnimation = (FlingAnimation)this.flingAnimations.get((Object)floatPropertyCompat);
            if (flingAnimation == null || !flingAnimation.isRunning()) {
                return b;
            }
        }
        b = true;
        return b;
    }
    
    public final PhysicsAnimator<T> spring(final FloatPropertyCompat<? super T> floatPropertyCompat, final float f, final float n, final float n2, final float n3) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        if (PhysicsAnimatorKt.access$getVerboseLogging$p()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Springing ");
            sb.append(PhysicsAnimator.Companion.getReadablePropertyName(floatPropertyCompat));
            sb.append(" to ");
            sb.append(f);
            sb.append('.');
            Log.d("PhysicsAnimator", sb.toString());
        }
        ((Map<FloatPropertyCompat<? super T>, SpringConfig>)this.springConfigs).put(floatPropertyCompat, new SpringConfig(n2, n3, n, f));
        return this;
    }
    
    public final PhysicsAnimator<T> spring(final FloatPropertyCompat<? super T> floatPropertyCompat, final float n, final float n2, final SpringConfig springConfig) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        Intrinsics.checkParameterIsNotNull(springConfig, "config");
        this.spring(floatPropertyCompat, n, n2, springConfig.getStiffness$frameworks__base__packages__SystemUI__android_common__SystemUI_core(), springConfig.getDampingRatio$frameworks__base__packages__SystemUI__android_common__SystemUI_core());
        return this;
    }
    
    public final PhysicsAnimator<T> spring(final FloatPropertyCompat<? super T> floatPropertyCompat, final float n, final SpringConfig springConfig) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        Intrinsics.checkParameterIsNotNull(springConfig, "config");
        this.spring(floatPropertyCompat, n, 0.0f, springConfig);
        return this;
    }
    
    public final void start() {
        this.startAction.invoke();
    }
    
    public final void startInternal$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
        final Looper mainLooper = Looper.getMainLooper();
        Intrinsics.checkExpressionValueIsNotNull(mainLooper, "Looper.getMainLooper()");
        if (!mainLooper.isCurrentThread()) {
            Log.e("PhysicsAnimator", "Animations can only be started on the main thread. If you are seeing this message in a test, call PhysicsAnimatorTestUtils#prepareForTest in your test setup.");
        }
        final ArrayList<Function0> list = new ArrayList<Function0>();
        for (final FloatPropertyCompat<T> floatPropertyCompat : this.getAnimatedProperties$frameworks__base__packages__SystemUI__android_common__SystemUI_core()) {
            final FlingConfig flingConfig = (FlingConfig)this.flingConfigs.get((Object)floatPropertyCompat);
            final SpringConfig springConfig = (SpringConfig)this.springConfigs.get((Object)floatPropertyCompat);
            final float value = floatPropertyCompat.getValue(this.target);
            if (flingConfig != null) {
                list.add((Function0)new PhysicsAnimator$startInternal.PhysicsAnimator$startInternal$1(this, flingConfig, value, (FloatPropertyCompat)floatPropertyCompat));
            }
            if (springConfig != null) {
                if (flingConfig == null) {
                    final SpringAnimation springAnimation = this.getSpringAnimation(floatPropertyCompat);
                    springConfig.applyToAnimation$frameworks__base__packages__SystemUI__android_common__SystemUI_core(springAnimation);
                    list.add((Function0)new PhysicsAnimator$startInternal.PhysicsAnimator$startInternal$2(springAnimation));
                }
                else {
                    this.endListeners.add(0, (EndListener<T>)new PhysicsAnimator$startInternal.PhysicsAnimator$startInternal$3(this, (FloatPropertyCompat)floatPropertyCompat, flingConfig.getMin$frameworks__base__packages__SystemUI__android_common__SystemUI_core(), flingConfig.getMax$frameworks__base__packages__SystemUI__android_common__SystemUI_core(), springConfig));
                }
            }
        }
        this.internalListeners.add(new InternalListener(this.getAnimatedProperties$frameworks__base__packages__SystemUI__android_common__SystemUI_core(), new ArrayList<UpdateListener<T>>((Collection<? extends UpdateListener<T>>)this.updateListeners), new ArrayList<EndListener<T>>((Collection<? extends EndListener<T>>)this.endListeners), new ArrayList<Function0<Unit>>(this.endActions)));
        final Iterator<Object> iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().invoke();
        }
        this.clearAnimator();
    }
    
    public final PhysicsAnimator<T> withEndActions(final Runnable... array) {
        Intrinsics.checkParameterIsNotNull(array, "endActions");
        final ArrayList<Function0<Unit>> endActions = this.endActions;
        final List<Runnable> filterNotNull = ArraysKt.filterNotNull(array);
        final ArrayList c = new ArrayList<Function0<Unit>>(CollectionsKt.collectionSizeOrDefault((Iterable<?>)filterNotNull, 10));
        final Iterator<Object> iterator = filterNotNull.iterator();
        while (iterator.hasNext()) {
            c.add((Function0<Unit>)new PhysicsAnimator$withEndActions$1.PhysicsAnimator$withEndActions$1$1((Runnable)iterator.next()));
        }
        endActions.addAll((Collection<? extends Function0<Unit>>)c);
        return this;
    }
    
    public final PhysicsAnimator<T> withEndActions(final Function0<Unit>... array) {
        Intrinsics.checkParameterIsNotNull(array, "endActions");
        this.endActions.addAll(ArraysKt.filterNotNull(array));
        return this;
    }
    
    public static final class AnimationUpdate
    {
        private final float value;
        private final float velocity;
        
        public AnimationUpdate(final float value, final float velocity) {
            this.value = value;
            this.velocity = velocity;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof AnimationUpdate) {
                    final AnimationUpdate animationUpdate = (AnimationUpdate)o;
                    if (Float.compare(this.value, animationUpdate.value) == 0 && Float.compare(this.velocity, animationUpdate.velocity) == 0) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            return Float.hashCode(this.value) * 31 + Float.hashCode(this.velocity);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("AnimationUpdate(value=");
            sb.append(this.value);
            sb.append(", velocity=");
            sb.append(this.velocity);
            sb.append(")");
            return sb.toString();
        }
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final float estimateFlingEndValue(final float n, float n2, final FlingConfig flingConfig) {
            Intrinsics.checkParameterIsNotNull(flingConfig, "flingConfig");
            n2 /= flingConfig.getFriction$frameworks__base__packages__SystemUI__android_common__SystemUI_core() * 4.2f;
            return Math.min(flingConfig.getMax$frameworks__base__packages__SystemUI__android_common__SystemUI_core(), Math.max(flingConfig.getMin$frameworks__base__packages__SystemUI__android_common__SystemUI_core(), n + n2));
        }
        
        public final <T> PhysicsAnimator<T> getInstance(final T t) {
            Intrinsics.checkParameterIsNotNull(t, "target");
            if (!PhysicsAnimatorKt.getAnimators().containsKey(t)) {
                PhysicsAnimatorKt.getAnimators().put(t, this.getInstanceConstructor$frameworks__base__packages__SystemUI__android_common__SystemUI_core().invoke(t));
            }
            final PhysicsAnimator<?> value = PhysicsAnimatorKt.getAnimators().get(t);
            if (value != null) {
                return (PhysicsAnimator<T>)value;
            }
            throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.util.animation.PhysicsAnimator<T>");
        }
        
        public final Function1<Object, PhysicsAnimator<?>> getInstanceConstructor$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return (Function1<Object, PhysicsAnimator<?>>)PhysicsAnimator.access$getInstanceConstructor$cp();
        }
        
        public final String getReadablePropertyName(final FloatPropertyCompat<?> floatPropertyCompat) {
            Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
            String s;
            if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.TRANSLATION_X)) {
                s = "translationX";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.TRANSLATION_Y)) {
                s = "translationY";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.TRANSLATION_Z)) {
                s = "translationZ";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.SCALE_X)) {
                s = "scaleX";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.SCALE_Y)) {
                s = "scaleY";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.ROTATION)) {
                s = "rotation";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.ROTATION_X)) {
                s = "rotationX";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.ROTATION_Y)) {
                s = "rotationY";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.SCROLL_X)) {
                s = "scrollX";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.SCROLL_Y)) {
                s = "scrollY";
            }
            else if (Intrinsics.areEqual(floatPropertyCompat, DynamicAnimation.ALPHA)) {
                s = "alpha";
            }
            else {
                s = "Custom FloatPropertyCompat instance";
            }
            return s;
        }
    }
    
    public interface EndListener<T>
    {
        void onAnimationEnd(final T p0, final FloatPropertyCompat<? super T> p1, final boolean p2, final boolean p3, final float p4, final float p5, final boolean p6);
    }
    
    public static final class FlingConfig
    {
        private float friction;
        private float max;
        private float min;
        private float startVelocity;
        
        public FlingConfig() {
            this(PhysicsAnimatorKt.access$getDefaultFling$p().friction);
        }
        
        public FlingConfig(final float n) {
            this(n, PhysicsAnimatorKt.access$getDefaultFling$p().min, PhysicsAnimatorKt.access$getDefaultFling$p().max);
        }
        
        public FlingConfig(final float n, final float n2, final float n3) {
            this(n, n2, n3, 0.0f);
        }
        
        public FlingConfig(final float friction, final float min, final float max, final float startVelocity) {
            this.friction = friction;
            this.min = min;
            this.max = max;
            this.startVelocity = startVelocity;
        }
        
        public static /* synthetic */ FlingConfig copy$default(final FlingConfig flingConfig, float friction, float min, float max, float startVelocity, final int n, final Object o) {
            if ((n & 0x1) != 0x0) {
                friction = flingConfig.friction;
            }
            if ((n & 0x2) != 0x0) {
                min = flingConfig.min;
            }
            if ((n & 0x4) != 0x0) {
                max = flingConfig.max;
            }
            if ((n & 0x8) != 0x0) {
                startVelocity = flingConfig.startVelocity;
            }
            return flingConfig.copy(friction, min, max, startVelocity);
        }
        
        public final void applyToAnimation$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final FlingAnimation flingAnimation) {
            Intrinsics.checkParameterIsNotNull(flingAnimation, "anim");
            flingAnimation.setFriction(this.friction);
            flingAnimation.setMinValue(this.min);
            flingAnimation.setMaxValue(this.max);
            flingAnimation.setStartVelocity(this.startVelocity);
        }
        
        public final FlingConfig copy(final float n, final float n2, final float n3, final float n4) {
            return new FlingConfig(n, n2, n3, n4);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof FlingConfig) {
                    final FlingConfig flingConfig = (FlingConfig)o;
                    if (Float.compare(this.friction, flingConfig.friction) == 0 && Float.compare(this.min, flingConfig.min) == 0 && Float.compare(this.max, flingConfig.max) == 0 && Float.compare(this.startVelocity, flingConfig.startVelocity) == 0) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public final float getFriction$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.friction;
        }
        
        public final float getMax$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.max;
        }
        
        public final float getMin$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.min;
        }
        
        @Override
        public int hashCode() {
            return ((Float.hashCode(this.friction) * 31 + Float.hashCode(this.min)) * 31 + Float.hashCode(this.max)) * 31 + Float.hashCode(this.startVelocity);
        }
        
        public final void setMax$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final float max) {
            this.max = max;
        }
        
        public final void setMin$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final float min) {
            this.min = min;
        }
        
        public final void setStartVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final float startVelocity) {
            this.startVelocity = startVelocity;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("FlingConfig(friction=");
            sb.append(this.friction);
            sb.append(", min=");
            sb.append(this.min);
            sb.append(", max=");
            sb.append(this.max);
            sb.append(", startVelocity=");
            sb.append(this.startVelocity);
            sb.append(")");
            return sb.toString();
        }
    }
    
    public final class InternalListener
    {
        private List<? extends Function0<Unit>> endActions;
        private List<? extends EndListener<T>> endListeners;
        private int numPropertiesAnimating;
        private Set<? extends FloatPropertyCompat<? super T>> properties;
        private final ArrayMap<FloatPropertyCompat<? super T>, AnimationUpdate> undispatchedUpdates;
        private List<? extends UpdateListener<T>> updateListeners;
        
        public InternalListener(final Set<? extends FloatPropertyCompat<? super T>> properties, final List<? extends UpdateListener<T>> updateListeners, final List<? extends EndListener<T>> endListeners, final List<? extends Function0<Unit>> endActions) {
            Intrinsics.checkParameterIsNotNull(properties, "properties");
            Intrinsics.checkParameterIsNotNull(updateListeners, "updateListeners");
            Intrinsics.checkParameterIsNotNull(endListeners, "endListeners");
            Intrinsics.checkParameterIsNotNull(endActions, "endActions");
            this.properties = properties;
            this.updateListeners = updateListeners;
            this.endListeners = endListeners;
            this.endActions = endActions;
            this.numPropertiesAnimating = properties.size();
            this.undispatchedUpdates = (ArrayMap<FloatPropertyCompat<? super T>, AnimationUpdate>)new ArrayMap();
        }
        
        private final void maybeDispatchUpdates() {
            if (this.undispatchedUpdates.size() >= this.numPropertiesAnimating && this.undispatchedUpdates.size() > 0) {
                final Iterator<UpdateListener<Object>> iterator = this.updateListeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onAnimationUpdateForProperty(PhysicsAnimator.this.getTarget(), (android.util.ArrayMap<FloatPropertyCompat<? super Object>, AnimationUpdate>)new ArrayMap((ArrayMap)this.undispatchedUpdates));
                }
                this.undispatchedUpdates.clear();
            }
        }
        
        public final boolean onInternalAnimationEnd$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final FloatPropertyCompat<? super T> floatPropertyCompat, final boolean b, final float n, final float n2, final boolean b2) {
            Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
            if (!this.properties.contains(floatPropertyCompat)) {
                return false;
            }
            --this.numPropertiesAnimating;
            this.maybeDispatchUpdates();
            if (((Map)this.undispatchedUpdates).containsKey(floatPropertyCompat)) {
                for (final UpdateListener<Object> updateListener : this.updateListeners) {
                    final Object target = PhysicsAnimator.this.getTarget();
                    final ArrayMap arrayMap = new ArrayMap();
                    ((Map<FloatPropertyCompat<? super Object>, Object>)arrayMap).put((FloatPropertyCompat<? super Object>)floatPropertyCompat, this.undispatchedUpdates.get((Object)floatPropertyCompat));
                    updateListener.onAnimationUpdateForProperty(target, (android.util.ArrayMap<FloatPropertyCompat<? super Object>, AnimationUpdate>)arrayMap);
                }
                this.undispatchedUpdates.remove((Object)floatPropertyCompat);
            }
            final boolean b3 = PhysicsAnimator.this.arePropertiesAnimating(this.properties) ^ true;
            final Iterator<EndListener<Object>> iterator2 = (Iterator<EndListener<Object>>)this.endListeners.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().onAnimationEnd(PhysicsAnimator.this.getTarget(), (FloatPropertyCompat<? super Object>)floatPropertyCompat, b2, b, n, n2, b3);
                if (PhysicsAnimator.this.isPropertyAnimating(floatPropertyCompat)) {
                    return false;
                }
            }
            if (b3 && !b) {
                final Iterator<Function0> iterator3 = (Iterator<Function0>)this.endActions.iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().invoke();
                }
            }
            return b3;
        }
        
        public final void onInternalAnimationUpdate$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final FloatPropertyCompat<? super T> floatPropertyCompat, final float n, final float n2) {
            Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
            if (!this.properties.contains(floatPropertyCompat)) {
                return;
            }
            ((Map<FloatPropertyCompat<? super T>, AnimationUpdate>)this.undispatchedUpdates).put(floatPropertyCompat, new AnimationUpdate(n, n2));
            this.maybeDispatchUpdates();
        }
    }
    
    public static final class SpringConfig
    {
        private float dampingRatio;
        private float finalPosition;
        private float startVelocity;
        private float stiffness;
        
        public SpringConfig() {
            this(PhysicsAnimatorKt.access$getDefaultSpring$p().stiffness, PhysicsAnimatorKt.access$getDefaultSpring$p().dampingRatio);
        }
        
        public SpringConfig(final float n, final float n2) {
            this(n, n2, 0.0f, 0.0f, 8, null);
        }
        
        public SpringConfig(final float stiffness, final float dampingRatio, final float startVelocity, final float finalPosition) {
            this.stiffness = stiffness;
            this.dampingRatio = dampingRatio;
            this.startVelocity = startVelocity;
            this.finalPosition = finalPosition;
        }
        
        public static /* synthetic */ SpringConfig copy$default(final SpringConfig springConfig, float stiffness, float dampingRatio, float startVelocity, float finalPosition, final int n, final Object o) {
            if ((n & 0x1) != 0x0) {
                stiffness = springConfig.stiffness;
            }
            if ((n & 0x2) != 0x0) {
                dampingRatio = springConfig.dampingRatio;
            }
            if ((n & 0x4) != 0x0) {
                startVelocity = springConfig.startVelocity;
            }
            if ((n & 0x8) != 0x0) {
                finalPosition = springConfig.finalPosition;
            }
            return springConfig.copy(stiffness, dampingRatio, startVelocity, finalPosition);
        }
        
        public final void applyToAnimation$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final SpringAnimation springAnimation) {
            Intrinsics.checkParameterIsNotNull(springAnimation, "anim");
            SpringForce spring = springAnimation.getSpring();
            if (spring == null) {
                spring = new SpringForce();
            }
            spring.setStiffness(this.stiffness);
            spring.setDampingRatio(this.dampingRatio);
            spring.setFinalPosition(this.finalPosition);
            springAnimation.setSpring(spring);
            final float startVelocity = this.startVelocity;
            if (startVelocity != 0.0f) {
                springAnimation.setStartVelocity(startVelocity);
            }
        }
        
        public final SpringConfig copy(final float n, final float n2, final float n3, final float n4) {
            return new SpringConfig(n, n2, n3, n4);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof SpringConfig) {
                    final SpringConfig springConfig = (SpringConfig)o;
                    if (Float.compare(this.stiffness, springConfig.stiffness) == 0 && Float.compare(this.dampingRatio, springConfig.dampingRatio) == 0 && Float.compare(this.startVelocity, springConfig.startVelocity) == 0 && Float.compare(this.finalPosition, springConfig.finalPosition) == 0) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public final float getDampingRatio$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.dampingRatio;
        }
        
        public final float getFinalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.finalPosition;
        }
        
        public final float getStiffness$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            return this.stiffness;
        }
        
        @Override
        public int hashCode() {
            return ((Float.hashCode(this.stiffness) * 31 + Float.hashCode(this.dampingRatio)) * 31 + Float.hashCode(this.startVelocity)) * 31 + Float.hashCode(this.finalPosition);
        }
        
        public final void setFinalPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final float finalPosition) {
            this.finalPosition = finalPosition;
        }
        
        public final void setStartVelocity$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final float startVelocity) {
            this.startVelocity = startVelocity;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("SpringConfig(stiffness=");
            sb.append(this.stiffness);
            sb.append(", dampingRatio=");
            sb.append(this.dampingRatio);
            sb.append(", startVelocity=");
            sb.append(this.startVelocity);
            sb.append(", finalPosition=");
            sb.append(this.finalPosition);
            sb.append(")");
            return sb.toString();
        }
    }
    
    public interface UpdateListener<T>
    {
        void onAnimationUpdateForProperty(final T p0, final ArrayMap<FloatPropertyCompat<? super T>, AnimationUpdate> p1);
    }
}
