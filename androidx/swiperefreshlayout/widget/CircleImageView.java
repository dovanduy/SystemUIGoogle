// 
// Decompiled by Procyon v0.5.36
// 

package androidx.swiperefreshlayout.widget;

import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.Shader$TileMode;
import android.graphics.Paint;
import android.os.Build$VERSION;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.view.ViewCompat;
import android.graphics.drawable.shapes.Shape;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import androidx.swiperefreshlayout.R$styleable;
import android.content.Context;
import android.view.animation.Animation$AnimationListener;
import android.widget.ImageView;

class CircleImageView extends ImageView
{
    private int mBackgroundColor;
    private Animation$AnimationListener mListener;
    private int mShadowRadius;
    
    CircleImageView(final Context context) {
        super(context);
        final float density = this.getContext().getResources().getDisplayMetrics().density;
        final int n = (int)(1.75f * density);
        final int n2 = (int)(0.0f * density);
        this.mShadowRadius = (int)(3.5f * density);
        final TypedArray obtainStyledAttributes = this.getContext().obtainStyledAttributes(R$styleable.SwipeRefreshLayout);
        this.mBackgroundColor = obtainStyledAttributes.getColor(R$styleable.SwipeRefreshLayout_swipeRefreshLayoutProgressSpinnerBackgroundColor, -328966);
        obtainStyledAttributes.recycle();
        ShapeDrawable shapeDrawable;
        if (this.elevationSupported()) {
            shapeDrawable = new ShapeDrawable((Shape)new OvalShape());
            ViewCompat.setElevation((View)this, density * 4.0f);
        }
        else {
            shapeDrawable = new ShapeDrawable((Shape)new OvalShadow(this, this.mShadowRadius));
            this.setLayerType(1, shapeDrawable.getPaint());
            shapeDrawable.getPaint().setShadowLayer((float)this.mShadowRadius, (float)n2, (float)n, 503316480);
            final int mShadowRadius = this.mShadowRadius;
            this.setPadding(mShadowRadius, mShadowRadius, mShadowRadius, mShadowRadius);
        }
        shapeDrawable.getPaint().setColor(this.mBackgroundColor);
        ViewCompat.setBackground((View)this, (Drawable)shapeDrawable);
    }
    
    private boolean elevationSupported() {
        return Build$VERSION.SDK_INT >= 21;
    }
    
    public void onAnimationEnd() {
        super.onAnimationEnd();
        final Animation$AnimationListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onAnimationEnd(this.getAnimation());
        }
    }
    
    public void onAnimationStart() {
        super.onAnimationStart();
        final Animation$AnimationListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onAnimationStart(this.getAnimation());
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        if (!this.elevationSupported()) {
            this.setMeasuredDimension(this.getMeasuredWidth() + this.mShadowRadius * 2, this.getMeasuredHeight() + this.mShadowRadius * 2);
        }
    }
    
    public void setAnimationListener(final Animation$AnimationListener mListener) {
        this.mListener = mListener;
    }
    
    public void setBackgroundColor(final int n) {
        if (this.getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable)this.getBackground()).getPaint().setColor(n);
            this.mBackgroundColor = n;
        }
    }
    
    private static class OvalShadow extends OvalShape
    {
        private CircleImageView mCircleImageView;
        private Paint mShadowPaint;
        private int mShadowRadius;
        
        OvalShadow(final CircleImageView mCircleImageView, final int mShadowRadius) {
            this.mCircleImageView = mCircleImageView;
            this.mShadowPaint = new Paint();
            this.mShadowRadius = mShadowRadius;
            this.updateRadialGradient((int)this.rect().width());
        }
        
        private void updateRadialGradient(final int n) {
            final Paint mShadowPaint = this.mShadowPaint;
            final float n2 = (float)(n / 2);
            mShadowPaint.setShader((Shader)new RadialGradient(n2, n2, (float)this.mShadowRadius, new int[] { 1023410176, 0 }, (float[])null, Shader$TileMode.CLAMP));
        }
        
        public void draw(final Canvas canvas, final Paint paint) {
            final int n = this.mCircleImageView.getWidth() / 2;
            final int n2 = this.mCircleImageView.getHeight() / 2;
            final float n3 = (float)n;
            final float n4 = (float)n2;
            canvas.drawCircle(n3, n4, n3, this.mShadowPaint);
            canvas.drawCircle(n3, n4, (float)(n - this.mShadowRadius), paint);
        }
        
        protected void onResize(final float n, final float n2) {
            super.onResize(n, n2);
            this.updateRadialGradient((int)n);
        }
    }
}
