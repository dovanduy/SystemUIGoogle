// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import java.util.Iterator;
import com.android.systemui.R$id;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.content.res.Configuration;
import android.view.DisplayCutout;
import com.android.internal.annotations.VisibleForTesting;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.view.View;
import android.graphics.Point;
import android.graphics.Rect;
import java.util.List;
import com.android.keyguard.AlphaOptimizedLinearLayout;

public class HeadsUpStatusBarView extends AlphaOptimizedLinearLayout
{
    private int mAbsoluteStartPadding;
    private List<Rect> mCutOutBounds;
    private int mCutOutInset;
    private Point mDisplaySize;
    private int mEndMargin;
    private boolean mFirstLayout;
    private Rect mIconDrawingRect;
    private View mIconPlaceholder;
    private Rect mLayoutedIconRect;
    private int mMaxWidth;
    private Runnable mOnDrawingRectChangedListener;
    private final NotificationEntry.OnSensitivityChangedListener mOnSensitivityChangedListener;
    private NotificationEntry mShowingEntry;
    private int mSysWinInset;
    private TextView mTextView;
    private int[] mTmpPosition;
    
    public HeadsUpStatusBarView(final Context context) {
        this(context, null);
    }
    
    public HeadsUpStatusBarView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public HeadsUpStatusBarView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public HeadsUpStatusBarView(final Context context, final AttributeSet set, int dimensionPixelSize, final int n) {
        super(context, set, dimensionPixelSize, n);
        this.mLayoutedIconRect = new Rect();
        this.mTmpPosition = new int[2];
        this.mFirstLayout = true;
        this.mIconDrawingRect = new Rect();
        this.mOnSensitivityChangedListener = new _$$Lambda$HeadsUpStatusBarView$3EjbzF6YolguGjurT443cwUG0Vs(this);
        final Resources resources = this.getResources();
        this.mAbsoluteStartPadding = resources.getDimensionPixelSize(R$dimen.notification_side_paddings) + resources.getDimensionPixelSize(17105344);
        dimensionPixelSize = resources.getDimensionPixelSize(17105343);
        this.mEndMargin = dimensionPixelSize;
        this.setPaddingRelative(this.mAbsoluteStartPadding, 0, dimensionPixelSize, 0);
        this.updateMaxWidth();
    }
    
    @VisibleForTesting
    public HeadsUpStatusBarView(final Context context, final View mIconPlaceholder, final TextView mTextView) {
        this(context);
        this.mIconPlaceholder = mIconPlaceholder;
        this.mTextView = mTextView;
    }
    
    private void getDisplaySize() {
        if (this.mDisplaySize == null) {
            this.mDisplaySize = new Point();
        }
        this.getDisplay().getRealSize(this.mDisplaySize);
    }
    
    private void updateDrawingRect() {
        final Rect mIconDrawingRect = this.mIconDrawingRect;
        final float n = (float)mIconDrawingRect.left;
        mIconDrawingRect.set(this.mLayoutedIconRect);
        this.mIconDrawingRect.offset((int)this.getTranslationX(), 0);
        if (n != this.mIconDrawingRect.left) {
            final Runnable mOnDrawingRectChangedListener = this.mOnDrawingRectChangedListener;
            if (mOnDrawingRectChangedListener != null) {
                mOnDrawingRectChangedListener.run();
            }
        }
    }
    
    private void updateMaxWidth() {
        final int dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.qs_panel_width);
        if (dimensionPixelSize != this.mMaxWidth) {
            this.mMaxWidth = dimensionPixelSize;
            this.requestLayout();
        }
    }
    
    protected boolean fitSystemWindows(final Rect rect) {
        final boolean layoutRtl = this.isLayoutRtl();
        int mSysWinInset;
        if (layoutRtl) {
            mSysWinInset = rect.right;
        }
        else {
            mSysWinInset = rect.left;
        }
        this.mSysWinInset = mSysWinInset;
        final DisplayCutout displayCutout = this.getRootWindowInsets().getDisplayCutout();
        int mCutOutInset;
        if (displayCutout != null) {
            if (layoutRtl) {
                mCutOutInset = displayCutout.getSafeInsetRight();
            }
            else {
                mCutOutInset = displayCutout.getSafeInsetLeft();
            }
        }
        else {
            mCutOutInset = 0;
        }
        this.mCutOutInset = mCutOutInset;
        this.getDisplaySize();
        this.mCutOutBounds = null;
        if (displayCutout != null && displayCutout.getSafeInsetRight() == 0 && displayCutout.getSafeInsetLeft() == 0) {
            this.mCutOutBounds = (List<Rect>)displayCutout.getBoundingRects();
        }
        if (this.mSysWinInset != 0) {
            this.mCutOutInset = 0;
        }
        return super.fitSystemWindows(rect);
    }
    
    public Rect getIconDrawingRect() {
        return this.mIconDrawingRect;
    }
    
    public NotificationEntry getShowingEntry() {
        return this.mShowingEntry;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.getDisplaySize();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateMaxWidth();
    }
    
    public void onDarkChanged(final Rect rect, final float n, final int n2) {
        this.mTextView.setTextColor(DarkIconDispatcher.getTint(rect, (View)this, n2));
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIconPlaceholder = this.findViewById(R$id.icon_placeholder);
        this.mTextView = (TextView)this.findViewById(R$id.text);
    }
    
    protected void onLayout(final boolean b, int i, int left, int n, int height) {
        super.onLayout(b, i, left, n, height);
        this.mIconPlaceholder.getLocationOnScreen(this.mTmpPosition);
        i = (int)(this.mTmpPosition[0] - this.getTranslationX());
        n = this.mTmpPosition[1];
        left = this.mIconPlaceholder.getWidth() + i;
        height = this.mIconPlaceholder.getHeight();
        this.mLayoutedIconRect.set(i, n, left, height + n);
        this.updateDrawingRect();
        n = this.mAbsoluteStartPadding + this.mSysWinInset + this.mCutOutInset;
        final boolean layoutRtl = this.isLayoutRtl();
        if (layoutRtl) {
            i = this.mDisplaySize.x - left;
        }
        if (i != n) {
            final List<Rect> mCutOutBounds = this.mCutOutBounds;
            left = i;
            Label_0210: {
                if (mCutOutBounds != null) {
                    final Iterator<Rect> iterator = mCutOutBounds.iterator();
                    Rect rect;
                    do {
                        left = i;
                        if (!iterator.hasNext()) {
                            break Label_0210;
                        }
                        rect = iterator.next();
                        if (layoutRtl) {
                            left = this.mDisplaySize.x - rect.right;
                        }
                        else {
                            left = rect.left;
                        }
                    } while (i <= left);
                    left = i - rect.width();
                }
            }
            this.setPaddingRelative(n - left + this.getPaddingStart(), 0, this.mEndMargin, 0);
        }
        if (this.mFirstLayout) {
            this.setVisibility(8);
            this.mFirstLayout = false;
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        int measureSpec = n;
        if (this.mMaxWidth > 0) {
            measureSpec = View$MeasureSpec.makeMeasureSpec(Math.min(View$MeasureSpec.getSize(n), this.mMaxWidth), View$MeasureSpec.getMode(n));
        }
        super.onMeasure(measureSpec, n2);
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable instanceof Bundle) {
            final Bundle bundle = (Bundle)parcelable;
            super.onRestoreInstanceState(bundle.getParcelable("heads_up_status_bar_view_super_parcelable"));
            this.mFirstLayout = bundle.getBoolean("first_layout", true);
            if (bundle.containsKey("visibility")) {
                this.setVisibility(bundle.getInt("visibility"));
            }
            if (bundle.containsKey("alpha")) {
                this.setAlpha(bundle.getFloat("alpha"));
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    public Bundle onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("heads_up_status_bar_view_super_parcelable", super.onSaveInstanceState());
        bundle.putBoolean("first_layout", this.mFirstLayout);
        bundle.putInt("visibility", this.getVisibility());
        bundle.putFloat("alpha", this.getAlpha());
        return bundle;
    }
    
    public void setEntry(final NotificationEntry mShowingEntry) {
        final NotificationEntry mShowingEntry2 = this.mShowingEntry;
        if (mShowingEntry2 != null) {
            mShowingEntry2.removeOnSensitivityChangedListener(this.mOnSensitivityChangedListener);
        }
        if ((this.mShowingEntry = mShowingEntry) != null) {
            CharSequence text = mShowingEntry.headsUpStatusBarText;
            if (mShowingEntry.isSensitive()) {
                text = mShowingEntry.headsUpStatusBarTextPublic;
            }
            this.mTextView.setText(text);
            this.mShowingEntry.addOnSensitivityChangedListener(this.mOnSensitivityChangedListener);
        }
    }
    
    public void setOnDrawingRectChangedListener(final Runnable mOnDrawingRectChangedListener) {
        this.mOnDrawingRectChangedListener = mOnDrawingRectChangedListener;
    }
    
    public void setPanelTranslation(final float translationX) {
        this.setTranslationX(translationX);
        this.updateDrawingRect();
    }
}
