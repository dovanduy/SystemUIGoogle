// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import android.content.res.TypedArray;
import androidx.slice.view.R$styleable;
import android.content.Context;

public class RowStyle
{
    private int mActionDividerHeight;
    private int mBottomDividerEndPadding;
    private int mBottomDividerStartPadding;
    private int mContentEndPadding;
    private int mContentStartPadding;
    private int mEndItemEndPadding;
    private int mEndItemStartPadding;
    private int mSubContentEndPadding;
    private int mSubContentStartPadding;
    private int mTitleEndPadding;
    private int mTitleItemEndPadding;
    private int mTitleItemStartPadding;
    private int mTitleStartPadding;
    
    public RowStyle(Context obtainStyledAttributes, final int n) {
        obtainStyledAttributes = (Context)obtainStyledAttributes.getTheme().obtainStyledAttributes(n, R$styleable.RowStyle);
        try {
            this.mTitleItemStartPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_titleItemStartPadding, -1.0f);
            this.mTitleItemEndPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_titleItemEndPadding, -1.0f);
            this.mContentStartPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_contentStartPadding, -1.0f);
            this.mContentEndPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_contentEndPadding, -1.0f);
            this.mTitleStartPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_titleStartPadding, -1.0f);
            this.mTitleEndPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_titleEndPadding, -1.0f);
            this.mSubContentStartPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_subContentStartPadding, -1.0f);
            this.mSubContentEndPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_subContentEndPadding, -1.0f);
            this.mEndItemStartPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_endItemStartPadding, -1.0f);
            this.mEndItemEndPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_endItemEndPadding, -1.0f);
            this.mBottomDividerStartPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_bottomDividerStartPadding, -1.0f);
            this.mBottomDividerEndPadding = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_bottomDividerEndPadding, -1.0f);
            this.mActionDividerHeight = (int)((TypedArray)obtainStyledAttributes).getDimension(R$styleable.RowStyle_actionDividerHeight, -1.0f);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    public int getActionDividerHeight() {
        return this.mActionDividerHeight;
    }
    
    public int getBottomDividerEndPadding() {
        return this.mBottomDividerEndPadding;
    }
    
    public int getBottomDividerStartPadding() {
        return this.mBottomDividerStartPadding;
    }
    
    public int getContentEndPadding() {
        return this.mContentEndPadding;
    }
    
    public int getContentStartPadding() {
        return this.mContentStartPadding;
    }
    
    public int getEndItemEndPadding() {
        return this.mEndItemEndPadding;
    }
    
    public int getEndItemStartPadding() {
        return this.mEndItemStartPadding;
    }
    
    public int getSubContentEndPadding() {
        return this.mSubContentEndPadding;
    }
    
    public int getSubContentStartPadding() {
        return this.mSubContentStartPadding;
    }
    
    public int getTitleEndPadding() {
        return this.mTitleEndPadding;
    }
    
    public int getTitleItemEndPadding() {
        return this.mTitleItemEndPadding;
    }
    
    public int getTitleItemStartPadding() {
        return this.mTitleItemStartPadding;
    }
    
    public int getTitleStartPadding() {
        return this.mTitleStartPadding;
    }
}
