// 
// Decompiled by Procyon v0.5.36
// 

package androidx.vectordrawable.graphics.drawable;

import java.util.Collection;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.content.res.TypedArray;
import androidx.core.content.res.TypedArrayUtils;
import android.graphics.drawable.Drawable$ConstantState;
import android.graphics.ColorFilter;
import android.graphics.Canvas;
import androidx.core.graphics.drawable.DrawableCompat;
import android.animation.TypeEvaluator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import androidx.collection.ArrayMap;
import java.util.ArrayList;
import android.os.Build$VERSION;
import android.animation.Animator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.Resources$Theme;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.drawable.Drawable$Callback;
import android.animation.ArgbEvaluator;
import android.graphics.drawable.Animatable;

public class AnimatedVectorDrawableCompat extends VectorDrawableCommon implements Animatable
{
    private AnimatedVectorDrawableCompatState mAnimatedVectorState;
    private ArgbEvaluator mArgbEvaluator;
    final Drawable$Callback mCallback;
    private Context mContext;
    
    AnimatedVectorDrawableCompat() {
        this(null, null, null);
    }
    
    private AnimatedVectorDrawableCompat(final Context context) {
        this(context, null, null);
    }
    
    private AnimatedVectorDrawableCompat(final Context mContext, final AnimatedVectorDrawableCompatState mAnimatedVectorState, final Resources resources) {
        this.mArgbEvaluator = null;
        this.mCallback = (Drawable$Callback)new Drawable$Callback() {
            public void invalidateDrawable(final Drawable drawable) {
                AnimatedVectorDrawableCompat.this.invalidateSelf();
            }
            
            public void scheduleDrawable(final Drawable drawable, final Runnable runnable, final long n) {
                AnimatedVectorDrawableCompat.this.scheduleSelf(runnable, n);
            }
            
            public void unscheduleDrawable(final Drawable drawable, final Runnable runnable) {
                AnimatedVectorDrawableCompat.this.unscheduleSelf(runnable);
            }
        };
        this.mContext = mContext;
        if (mAnimatedVectorState != null) {
            this.mAnimatedVectorState = mAnimatedVectorState;
        }
        else {
            this.mAnimatedVectorState = new AnimatedVectorDrawableCompatState(mContext, mAnimatedVectorState, this.mCallback, resources);
        }
    }
    
    public static AnimatedVectorDrawableCompat createFromXmlInner(final Context context, final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) throws XmlPullParserException, IOException {
        final AnimatedVectorDrawableCompat animatedVectorDrawableCompat = new AnimatedVectorDrawableCompat(context);
        animatedVectorDrawableCompat.inflate(resources, xmlPullParser, set, resources$Theme);
        return animatedVectorDrawableCompat;
    }
    
    private void setupAnimatorsForTarget(final String s, final Animator e) {
        e.setTarget(this.mAnimatedVectorState.mVectorDrawable.getTargetByName(s));
        if (Build$VERSION.SDK_INT < 21) {
            this.setupColorAnimator(e);
        }
        final AnimatedVectorDrawableCompatState mAnimatedVectorState = this.mAnimatedVectorState;
        if (mAnimatedVectorState.mAnimators == null) {
            mAnimatedVectorState.mAnimators = new ArrayList<Animator>();
            this.mAnimatedVectorState.mTargetNameMap = new ArrayMap<Animator, String>();
        }
        this.mAnimatedVectorState.mAnimators.add(e);
        this.mAnimatedVectorState.mTargetNameMap.put(e, s);
    }
    
    private void setupColorAnimator(final Animator animator) {
        if (animator instanceof AnimatorSet) {
            final ArrayList childAnimations = ((AnimatorSet)animator).getChildAnimations();
            if (childAnimations != null) {
                for (int i = 0; i < childAnimations.size(); ++i) {
                    this.setupColorAnimator((Animator)childAnimations.get(i));
                }
            }
        }
        if (animator instanceof ObjectAnimator) {
            final ObjectAnimator objectAnimator = (ObjectAnimator)animator;
            final String propertyName = objectAnimator.getPropertyName();
            if ("fillColor".equals(propertyName) || "strokeColor".equals(propertyName)) {
                if (this.mArgbEvaluator == null) {
                    this.mArgbEvaluator = new ArgbEvaluator();
                }
                objectAnimator.setEvaluator((TypeEvaluator)this.mArgbEvaluator);
            }
        }
    }
    
    @Override
    public void applyTheme(final Resources$Theme resources$Theme) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            DrawableCompat.applyTheme(mDelegateDrawable, resources$Theme);
        }
    }
    
    public boolean canApplyTheme() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        return mDelegateDrawable != null && DrawableCompat.canApplyTheme(mDelegateDrawable);
    }
    
    public void draw(final Canvas canvas) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            mDelegateDrawable.draw(canvas);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.draw(canvas);
        if (this.mAnimatedVectorState.mAnimatorSet.isStarted()) {
            this.invalidateSelf();
        }
    }
    
    public int getAlpha() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return DrawableCompat.getAlpha(mDelegateDrawable);
        }
        return this.mAnimatedVectorState.mVectorDrawable.getAlpha();
    }
    
    public int getChangingConfigurations() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.getChangingConfigurations();
        }
        return this.mAnimatedVectorState.mChangingConfigurations | super.getChangingConfigurations();
    }
    
    public ColorFilter getColorFilter() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return DrawableCompat.getColorFilter(mDelegateDrawable);
        }
        return this.mAnimatedVectorState.mVectorDrawable.getColorFilter();
    }
    
    public Drawable$ConstantState getConstantState() {
        if (super.mDelegateDrawable != null && Build$VERSION.SDK_INT >= 24) {
            return new AnimatedVectorDrawableDelegateState(super.mDelegateDrawable.getConstantState());
        }
        return null;
    }
    
    public int getIntrinsicHeight() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.getIntrinsicHeight();
        }
        return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicHeight();
    }
    
    public int getIntrinsicWidth() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.getIntrinsicWidth();
        }
        return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicWidth();
    }
    
    public int getOpacity() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.getOpacity();
        }
        return this.mAnimatedVectorState.mVectorDrawable.getOpacity();
    }
    
    public void inflate(final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set) throws XmlPullParserException, IOException {
        this.inflate(resources, xmlPullParser, set, null);
    }
    
    public void inflate(final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) throws XmlPullParserException, IOException {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            DrawableCompat.inflate(mDelegateDrawable, resources, xmlPullParser, set, resources$Theme);
            return;
        }
        for (int n = xmlPullParser.getEventType(), depth = xmlPullParser.getDepth(); n != 1 && (xmlPullParser.getDepth() >= depth + 1 || n != 3); n = xmlPullParser.next()) {
            if (n == 2) {
                final String name = xmlPullParser.getName();
                if ("animated-vector".equals(name)) {
                    final TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, resources$Theme, set, AndroidResources.STYLEABLE_ANIMATED_VECTOR_DRAWABLE);
                    final int resourceId = obtainAttributes.getResourceId(0, 0);
                    if (resourceId != 0) {
                        final VectorDrawableCompat create = VectorDrawableCompat.create(resources, resourceId, resources$Theme);
                        create.setAllowCaching(false);
                        create.setCallback(this.mCallback);
                        final VectorDrawableCompat mVectorDrawable = this.mAnimatedVectorState.mVectorDrawable;
                        if (mVectorDrawable != null) {
                            mVectorDrawable.setCallback((Drawable$Callback)null);
                        }
                        this.mAnimatedVectorState.mVectorDrawable = create;
                    }
                    obtainAttributes.recycle();
                }
                else if ("target".equals(name)) {
                    final TypedArray obtainAttributes2 = resources.obtainAttributes(set, AndroidResources.STYLEABLE_ANIMATED_VECTOR_DRAWABLE_TARGET);
                    final String string = obtainAttributes2.getString(0);
                    final int resourceId2 = obtainAttributes2.getResourceId(1, 0);
                    if (resourceId2 != 0) {
                        final Context mContext = this.mContext;
                        if (mContext == null) {
                            obtainAttributes2.recycle();
                            throw new IllegalStateException("Context can't be null when inflating animators");
                        }
                        this.setupAnimatorsForTarget(string, AnimatorInflaterCompat.loadAnimator(mContext, resourceId2));
                    }
                    obtainAttributes2.recycle();
                }
            }
        }
        this.mAnimatedVectorState.setupAnimatorSet();
    }
    
    public boolean isAutoMirrored() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return DrawableCompat.isAutoMirrored(mDelegateDrawable);
        }
        return this.mAnimatedVectorState.mVectorDrawable.isAutoMirrored();
    }
    
    public boolean isRunning() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return ((AnimatedVectorDrawable)mDelegateDrawable).isRunning();
        }
        return this.mAnimatedVectorState.mAnimatorSet.isRunning();
    }
    
    public boolean isStateful() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.isStateful();
        }
        return this.mAnimatedVectorState.mVectorDrawable.isStateful();
    }
    
    public Drawable mutate() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            mDelegateDrawable.mutate();
        }
        return this;
    }
    
    protected void onBoundsChange(final Rect rect) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            mDelegateDrawable.setBounds(rect);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.setBounds(rect);
    }
    
    @Override
    protected boolean onLevelChange(final int n) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.setLevel(n);
        }
        return this.mAnimatedVectorState.mVectorDrawable.setLevel(n);
    }
    
    protected boolean onStateChange(final int[] array) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.setState(array);
        }
        return this.mAnimatedVectorState.mVectorDrawable.setState(array);
    }
    
    public void setAlpha(final int n) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            mDelegateDrawable.setAlpha(n);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.setAlpha(n);
    }
    
    public void setAutoMirrored(final boolean autoMirrored) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            DrawableCompat.setAutoMirrored(mDelegateDrawable, autoMirrored);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.setAutoMirrored(autoMirrored);
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            mDelegateDrawable.setColorFilter(colorFilter);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.setColorFilter(colorFilter);
    }
    
    public void setTint(final int tint) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            DrawableCompat.setTint(mDelegateDrawable, tint);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.setTint(tint);
    }
    
    public void setTintList(final ColorStateList tintList) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            DrawableCompat.setTintList(mDelegateDrawable, tintList);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.setTintList(tintList);
    }
    
    public void setTintMode(final PorterDuff$Mode tintMode) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            DrawableCompat.setTintMode(mDelegateDrawable, tintMode);
            return;
        }
        this.mAnimatedVectorState.mVectorDrawable.setTintMode(tintMode);
    }
    
    public boolean setVisible(final boolean b, final boolean b2) {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            return mDelegateDrawable.setVisible(b, b2);
        }
        this.mAnimatedVectorState.mVectorDrawable.setVisible(b, b2);
        return super.setVisible(b, b2);
    }
    
    public void start() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            ((AnimatedVectorDrawable)mDelegateDrawable).start();
            return;
        }
        if (this.mAnimatedVectorState.mAnimatorSet.isStarted()) {
            return;
        }
        this.mAnimatedVectorState.mAnimatorSet.start();
        this.invalidateSelf();
    }
    
    public void stop() {
        final Drawable mDelegateDrawable = super.mDelegateDrawable;
        if (mDelegateDrawable != null) {
            ((AnimatedVectorDrawable)mDelegateDrawable).stop();
            return;
        }
        this.mAnimatedVectorState.mAnimatorSet.end();
    }
    
    private static class AnimatedVectorDrawableCompatState extends Drawable$ConstantState
    {
        AnimatorSet mAnimatorSet;
        ArrayList<Animator> mAnimators;
        int mChangingConfigurations;
        ArrayMap<Animator, String> mTargetNameMap;
        VectorDrawableCompat mVectorDrawable;
        
        AnimatedVectorDrawableCompatState(final Context context, final AnimatedVectorDrawableCompatState animatedVectorDrawableCompatState, final Drawable$Callback callback, final Resources resources) {
            if (animatedVectorDrawableCompatState != null) {
                this.mChangingConfigurations = animatedVectorDrawableCompatState.mChangingConfigurations;
                final VectorDrawableCompat mVectorDrawable = animatedVectorDrawableCompatState.mVectorDrawable;
                int i = 0;
                if (mVectorDrawable != null) {
                    final Drawable$ConstantState constantState = mVectorDrawable.getConstantState();
                    if (resources != null) {
                        this.mVectorDrawable = (VectorDrawableCompat)constantState.newDrawable(resources);
                    }
                    else {
                        this.mVectorDrawable = (VectorDrawableCompat)constantState.newDrawable();
                    }
                    final VectorDrawableCompat mVectorDrawable2 = this.mVectorDrawable;
                    mVectorDrawable2.mutate();
                    (this.mVectorDrawable = mVectorDrawable2).setCallback(callback);
                    this.mVectorDrawable.setBounds(animatedVectorDrawableCompatState.mVectorDrawable.getBounds());
                    this.mVectorDrawable.setAllowCaching(false);
                }
                final ArrayList<Animator> mAnimators = animatedVectorDrawableCompatState.mAnimators;
                if (mAnimators != null) {
                    final int size = mAnimators.size();
                    this.mAnimators = new ArrayList<Animator>(size);
                    this.mTargetNameMap = new ArrayMap<Animator, String>(size);
                    while (i < size) {
                        final Animator animator = animatedVectorDrawableCompatState.mAnimators.get(i);
                        final Animator clone = animator.clone();
                        final String s = animatedVectorDrawableCompatState.mTargetNameMap.get(animator);
                        clone.setTarget(this.mVectorDrawable.getTargetByName(s));
                        this.mAnimators.add(clone);
                        this.mTargetNameMap.put(clone, s);
                        ++i;
                    }
                    this.setupAnimatorSet();
                }
            }
        }
        
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
        
        public Drawable newDrawable() {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }
        
        public Drawable newDrawable(final Resources resources) {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }
        
        public void setupAnimatorSet() {
            if (this.mAnimatorSet == null) {
                this.mAnimatorSet = new AnimatorSet();
            }
            this.mAnimatorSet.playTogether((Collection)this.mAnimators);
        }
    }
    
    private static class AnimatedVectorDrawableDelegateState extends Drawable$ConstantState
    {
        private final Drawable$ConstantState mDelegateState;
        
        AnimatedVectorDrawableDelegateState(final Drawable$ConstantState mDelegateState) {
            this.mDelegateState = mDelegateState;
        }
        
        public boolean canApplyTheme() {
            return this.mDelegateState.canApplyTheme();
        }
        
        public int getChangingConfigurations() {
            return this.mDelegateState.getChangingConfigurations();
        }
        
        public Drawable newDrawable() {
            final AnimatedVectorDrawableCompat animatedVectorDrawableCompat = new AnimatedVectorDrawableCompat();
            (animatedVectorDrawableCompat.mDelegateDrawable = this.mDelegateState.newDrawable()).setCallback(animatedVectorDrawableCompat.mCallback);
            return animatedVectorDrawableCompat;
        }
        
        public Drawable newDrawable(final Resources resources) {
            final AnimatedVectorDrawableCompat animatedVectorDrawableCompat = new AnimatedVectorDrawableCompat();
            (animatedVectorDrawableCompat.mDelegateDrawable = this.mDelegateState.newDrawable(resources)).setCallback(animatedVectorDrawableCompat.mCallback);
            return animatedVectorDrawableCompat;
        }
        
        public Drawable newDrawable(final Resources resources, final Resources$Theme resources$Theme) {
            final AnimatedVectorDrawableCompat animatedVectorDrawableCompat = new AnimatedVectorDrawableCompat();
            (animatedVectorDrawableCompat.mDelegateDrawable = this.mDelegateState.newDrawable(resources, resources$Theme)).setCallback(animatedVectorDrawableCompat.mCallback);
            return animatedVectorDrawableCompat;
        }
    }
}
