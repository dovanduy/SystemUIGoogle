// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.widget;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint$Align;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.content.res.TypedArray;
import android.support.constraint.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

public class Placeholder extends View
{
    private View mContent;
    private int mContentId;
    private int mEmptyVisibility;
    
    public Placeholder(final Context context, final AttributeSet set) {
        super(context, set);
        this.mContentId = -1;
        this.mContent = null;
        this.mEmptyVisibility = 4;
        this.init(set);
    }
    
    private void init(final AttributeSet set) {
        super.setVisibility(this.mEmptyVisibility);
        this.mContentId = -1;
        if (set != null) {
            final TypedArray obtainStyledAttributes = this.getContext().obtainStyledAttributes(set, R$styleable.ConstraintLayout_placeholder);
            for (int indexCount = obtainStyledAttributes.getIndexCount(), i = 0; i < indexCount; ++i) {
                final int index = obtainStyledAttributes.getIndex(i);
                if (index == R$styleable.ConstraintLayout_placeholder_content) {
                    this.mContentId = obtainStyledAttributes.getResourceId(index, this.mContentId);
                }
                else if (index == R$styleable.ConstraintLayout_placeholder_emptyVisibility) {
                    this.mEmptyVisibility = obtainStyledAttributes.getInt(index, this.mEmptyVisibility);
                }
            }
        }
    }
    
    public View getContent() {
        return this.mContent;
    }
    
    public void onDraw(final Canvas canvas) {
        if (this.isInEditMode()) {
            canvas.drawRGB(223, 223, 223);
            final Paint paint = new Paint();
            paint.setARGB(255, 210, 210, 210);
            paint.setTextAlign(Paint$Align.CENTER);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, 0));
            final Rect rect = new Rect();
            canvas.getClipBounds(rect);
            paint.setTextSize((float)rect.height());
            final int height = rect.height();
            final int width = rect.width();
            paint.setTextAlign(Paint$Align.LEFT);
            paint.getTextBounds("?", 0, 1, rect);
            canvas.drawText("?", width / 2.0f - rect.width() / 2.0f - rect.left, height / 2.0f + rect.height() / 2.0f - rect.bottom, paint);
        }
    }
    
    public void updatePostMeasure(final ConstraintLayout constraintLayout) {
        if (this.mContent == null) {
            return;
        }
        final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)this.getLayoutParams();
        final ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams)this.mContent.getLayoutParams();
        layoutParams2.widget.setVisibility(0);
        layoutParams.widget.setWidth(layoutParams2.widget.getWidth());
        layoutParams.widget.setHeight(layoutParams2.widget.getHeight());
        layoutParams2.widget.setVisibility(8);
    }
    
    public void updatePreLayout(final ConstraintLayout constraintLayout) {
        if (this.mContentId == -1 && !this.isInEditMode()) {
            this.setVisibility(this.mEmptyVisibility);
        }
        final View viewById = constraintLayout.findViewById(this.mContentId);
        if ((this.mContent = viewById) != null) {
            ((ConstraintLayout.LayoutParams)viewById.getLayoutParams()).isInPlaceholder = true;
            this.mContent.setVisibility(0);
            this.setVisibility(0);
        }
    }
}
