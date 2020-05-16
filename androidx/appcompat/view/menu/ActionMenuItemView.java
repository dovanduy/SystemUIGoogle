// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import android.view.MotionEvent;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import android.view.View;
import androidx.appcompat.widget.TooltipCompat;
import android.text.TextUtils;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.Resources;
import androidx.appcompat.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.ForwardingListener;
import androidx.appcompat.widget.ActionMenuView;
import android.view.View$OnClickListener;
import androidx.appcompat.widget.AppCompatTextView;

public class ActionMenuItemView extends AppCompatTextView implements ItemView, View$OnClickListener, ActionMenuChildView
{
    private boolean mAllowTextWithIcon;
    private boolean mExpandedFormat;
    private ForwardingListener mForwardingListener;
    private Drawable mIcon;
    MenuItemImpl mItemData;
    ItemInvoker mItemInvoker;
    private int mMaxIconSize;
    private int mMinWidth;
    PopupCallback mPopupCallback;
    private int mSavedPaddingLeft;
    private CharSequence mTitle;
    
    public ActionMenuItemView(final Context context) {
        this(context, null);
    }
    
    public ActionMenuItemView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ActionMenuItemView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        final Resources resources = context.getResources();
        this.mAllowTextWithIcon = this.shouldAllowTextWithIcon();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ActionMenuItemView, n, 0);
        this.mMinWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.ActionMenuItemView_android_minWidth, 0);
        obtainStyledAttributes.recycle();
        this.mMaxIconSize = (int)(resources.getDisplayMetrics().density * 32.0f + 0.5f);
        this.setOnClickListener((View$OnClickListener)this);
        this.mSavedPaddingLeft = -1;
        this.setSaveEnabled(false);
    }
    
    private boolean shouldAllowTextWithIcon() {
        final Configuration configuration = this.getContext().getResources().getConfiguration();
        final int screenWidthDp = configuration.screenWidthDp;
        final int screenHeightDp = configuration.screenHeightDp;
        return screenWidthDp >= 480 || (screenWidthDp >= 640 && screenHeightDp >= 480) || configuration.orientation == 2;
    }
    
    private void updateTextButtonVisibility() {
        final boolean empty = TextUtils.isEmpty(this.mTitle);
        boolean b2;
        final boolean b = b2 = true;
        Label_0052: {
            if (this.mIcon != null) {
                if (this.mItemData.showsTextAsAction()) {
                    b2 = b;
                    if (this.mAllowTextWithIcon) {
                        break Label_0052;
                    }
                    if (this.mExpandedFormat) {
                        b2 = b;
                        break Label_0052;
                    }
                }
                b2 = false;
            }
        }
        final boolean b3 = (empty ^ true) & b2;
        final CharSequence charSequence = null;
        CharSequence mTitle;
        if (b3) {
            mTitle = this.mTitle;
        }
        else {
            mTitle = null;
        }
        this.setText(mTitle);
        final CharSequence contentDescription = this.mItemData.getContentDescription();
        if (TextUtils.isEmpty(contentDescription)) {
            CharSequence title;
            if (b3) {
                title = null;
            }
            else {
                title = this.mItemData.getTitle();
            }
            this.setContentDescription(title);
        }
        else {
            this.setContentDescription(contentDescription);
        }
        final CharSequence tooltipText = this.mItemData.getTooltipText();
        if (TextUtils.isEmpty(tooltipText)) {
            CharSequence title2;
            if (b3) {
                title2 = charSequence;
            }
            else {
                title2 = this.mItemData.getTitle();
            }
            TooltipCompat.setTooltipText((View)this, title2);
        }
        else {
            TooltipCompat.setTooltipText((View)this, tooltipText);
        }
    }
    
    @Override
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }
    
    public boolean hasText() {
        return TextUtils.isEmpty(this.getText()) ^ true;
    }
    
    @Override
    public void initialize(final MenuItemImpl mItemData, int visibility) {
        this.mItemData = mItemData;
        this.setIcon(mItemData.getIcon());
        this.setTitle(mItemData.getTitleForItemView(this));
        this.setId(mItemData.getItemId());
        if (mItemData.isVisible()) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        this.setVisibility(visibility);
        this.setEnabled(mItemData.isEnabled());
        if (mItemData.hasSubMenu() && this.mForwardingListener == null) {
            this.mForwardingListener = new ActionMenuItemForwardingListener();
        }
    }
    
    public boolean needsDividerAfter() {
        return this.hasText();
    }
    
    public boolean needsDividerBefore() {
        return this.hasText() && this.mItemData.getIcon() == null;
    }
    
    public void onClick(final View view) {
        final ItemInvoker mItemInvoker = this.mItemInvoker;
        if (mItemInvoker != null) {
            mItemInvoker.invokeItem(this.mItemData);
        }
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mAllowTextWithIcon = this.shouldAllowTextWithIcon();
        this.updateTextButtonVisibility();
    }
    
    @Override
    protected void onMeasure(int a, final int n) {
        final boolean hasText = this.hasText();
        if (hasText) {
            final int mSavedPaddingLeft = this.mSavedPaddingLeft;
            if (mSavedPaddingLeft >= 0) {
                super.setPadding(mSavedPaddingLeft, this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
            }
        }
        super.onMeasure(a, n);
        final int mode = View$MeasureSpec.getMode(a);
        a = View$MeasureSpec.getSize(a);
        final int measuredWidth = this.getMeasuredWidth();
        if (mode == Integer.MIN_VALUE) {
            a = Math.min(a, this.mMinWidth);
        }
        else {
            a = this.mMinWidth;
        }
        if (mode != 1073741824 && this.mMinWidth > 0 && measuredWidth < a) {
            super.onMeasure(View$MeasureSpec.makeMeasureSpec(a, 1073741824), n);
        }
        if (!hasText && this.mIcon != null) {
            super.setPadding((this.getMeasuredWidth() - this.mIcon.getBounds().width()) / 2, this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
        }
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        super.onRestoreInstanceState((Parcelable)null);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mItemData.hasSubMenu()) {
            final ForwardingListener mForwardingListener = this.mForwardingListener;
            if (mForwardingListener != null && mForwardingListener.onTouch((View)this, motionEvent)) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
    
    @Override
    public boolean prefersCondensedTitle() {
        return true;
    }
    
    public void setIcon(final Drawable mIcon) {
        this.mIcon = mIcon;
        if (mIcon != null) {
            final int intrinsicWidth = mIcon.getIntrinsicWidth();
            final int intrinsicHeight = mIcon.getIntrinsicHeight();
            final int mMaxIconSize = this.mMaxIconSize;
            int n = intrinsicWidth;
            int n2 = intrinsicHeight;
            if (intrinsicWidth > mMaxIconSize) {
                n2 = (int)(intrinsicHeight * (mMaxIconSize / (float)intrinsicWidth));
                n = mMaxIconSize;
            }
            final int mMaxIconSize2 = this.mMaxIconSize;
            int n3 = n;
            int n4;
            if ((n4 = n2) > mMaxIconSize2) {
                n3 = (int)(n * (mMaxIconSize2 / (float)n2));
                n4 = mMaxIconSize2;
            }
            mIcon.setBounds(0, 0, n3, n4);
        }
        this.setCompoundDrawables(mIcon, null, null, null);
        this.updateTextButtonVisibility();
    }
    
    public void setItemInvoker(final ItemInvoker mItemInvoker) {
        this.mItemInvoker = mItemInvoker;
    }
    
    public void setPadding(final int mSavedPaddingLeft, final int n, final int n2, final int n3) {
        super.setPadding(this.mSavedPaddingLeft = mSavedPaddingLeft, n, n2, n3);
    }
    
    public void setPopupCallback(final PopupCallback mPopupCallback) {
        this.mPopupCallback = mPopupCallback;
    }
    
    public void setTitle(final CharSequence mTitle) {
        this.mTitle = mTitle;
        this.updateTextButtonVisibility();
    }
    
    private class ActionMenuItemForwardingListener extends ForwardingListener
    {
        public ActionMenuItemForwardingListener() {
            super((View)ActionMenuItemView.this);
        }
        
        @Override
        public ShowableListMenu getPopup() {
            final PopupCallback mPopupCallback = ActionMenuItemView.this.mPopupCallback;
            if (mPopupCallback != null) {
                return mPopupCallback.getPopup();
            }
            return null;
        }
        
        @Override
        protected boolean onForwardingStarted() {
            final ActionMenuItemView this$0 = ActionMenuItemView.this;
            final ItemInvoker mItemInvoker = this$0.mItemInvoker;
            boolean b2;
            final boolean b = b2 = false;
            if (mItemInvoker != null) {
                b2 = b;
                if (mItemInvoker.invokeItem(this$0.mItemData)) {
                    final ShowableListMenu popup = this.getPopup();
                    b2 = b;
                    if (popup != null) {
                        b2 = b;
                        if (popup.isShowing()) {
                            b2 = true;
                        }
                    }
                }
            }
            return b2;
        }
    }
    
    public abstract static class PopupCallback
    {
        public abstract ShowableListMenu getPopup();
    }
}
