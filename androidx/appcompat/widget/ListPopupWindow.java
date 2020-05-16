// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.view.MotionEvent;
import android.widget.AbsListView;
import android.view.View$OnTouchListener;
import androidx.core.view.ViewCompat;
import androidx.core.widget.PopupWindowCompat;
import android.widget.PopupWindow$OnDismissListener;
import android.widget.ListView;
import android.view.ViewParent;
import android.view.ViewGroup;
import android.view.View$MeasureSpec;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.widget.LinearLayout;
import android.widget.AbsListView$OnScrollListener;
import android.widget.AdapterView;
import android.content.res.TypedArray;
import androidx.appcompat.R$styleable;
import android.util.AttributeSet;
import androidx.appcompat.R$attr;
import android.util.Log;
import android.os.Build$VERSION;
import android.widget.PopupWindow;
import android.database.DataSetObserver;
import android.widget.AdapterView$OnItemSelectedListener;
import android.widget.AdapterView$OnItemClickListener;
import android.os.Handler;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.content.Context;
import android.widget.ListAdapter;
import java.lang.reflect.Method;
import androidx.appcompat.view.menu.ShowableListMenu;

public class ListPopupWindow implements ShowableListMenu
{
    private static Method sGetMaxAvailableHeightMethod;
    private static Method sSetClipToWindowEnabledMethod;
    private static Method sSetEpicenterBoundsMethod;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownGravity;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private int mDropDownWindowLayoutType;
    private Rect mEpicenterBounds;
    private boolean mForceIgnoreOutsideTouch;
    final Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private AdapterView$OnItemClickListener mItemClickListener;
    private AdapterView$OnItemSelectedListener mItemSelectedListener;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    private boolean mOverlapAnchor;
    private boolean mOverlapAnchorSet;
    PopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private final Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;
    
    static {
        final Class<Boolean> type = Boolean.TYPE;
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT <= 28) {
            try {
                ListPopupWindow.sSetClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", type);
            }
            catch (NoSuchMethodException ex) {
                Log.i("ListPopupWindow", "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
            }
            try {
                ListPopupWindow.sSetEpicenterBoundsMethod = PopupWindow.class.getDeclaredMethod("setEpicenterBounds", Rect.class);
            }
            catch (NoSuchMethodException ex2) {
                Log.i("ListPopupWindow", "Could not find method setEpicenterBounds(Rect) on PopupWindow. Oh well.");
            }
        }
        if (sdk_INT <= 23) {
            try {
                ListPopupWindow.sGetMaxAvailableHeightMethod = PopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", View.class, Integer.TYPE, type);
            }
            catch (NoSuchMethodException ex3) {
                Log.i("ListPopupWindow", "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well.");
            }
        }
    }
    
    public ListPopupWindow(final Context context) {
        this(context, null, R$attr.listPopupWindowStyle);
    }
    
    public ListPopupWindow(final Context context, final AttributeSet set) {
        this(context, set, R$attr.listPopupWindowStyle);
    }
    
    public ListPopupWindow(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ListPopupWindow(final Context mContext, final AttributeSet set, final int n, final int n2) {
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownWindowLayoutType = 1002;
        this.mDropDownGravity = 0;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mListItemExpandMaximum = Integer.MAX_VALUE;
        this.mPromptPosition = 0;
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mTempRect = new Rect();
        this.mContext = mContext;
        this.mHandler = new Handler(mContext.getMainLooper());
        final TypedArray obtainStyledAttributes = mContext.obtainStyledAttributes(set, R$styleable.ListPopupWindow, n, n2);
        this.mDropDownHorizontalOffset = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
        final int dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
        this.mDropDownVerticalOffset = dimensionPixelOffset;
        if (dimensionPixelOffset != 0) {
            this.mDropDownVerticalOffsetSet = true;
        }
        obtainStyledAttributes.recycle();
        (this.mPopup = new AppCompatPopupWindow(mContext, set, n, n2)).setInputMethodMode(1);
    }
    
    private int buildDropDown() {
        final DropDownListView mDropDownList = this.mDropDownList;
        boolean b = true;
        int n2;
        if (mDropDownList == null) {
            final Context mContext = this.mContext;
            final DropDownListView dropDownListView = this.createDropDownListView(mContext, this.mModal ^ true);
            this.mDropDownList = dropDownListView;
            final Drawable mDropDownListHighlight = this.mDropDownListHighlight;
            if (mDropDownListHighlight != null) {
                dropDownListView.setSelector(mDropDownListHighlight);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener((AdapterView$OnItemSelectedListener)new AdapterView$OnItemSelectedListener() {
                public void onItemSelected(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                    if (n != -1) {
                        final DropDownListView mDropDownList = ListPopupWindow.this.mDropDownList;
                        if (mDropDownList != null) {
                            mDropDownList.setListSelectionHidden(false);
                        }
                    }
                }
                
                public void onNothingSelected(final AdapterView<?> adapterView) {
                }
            });
            this.mDropDownList.setOnScrollListener((AbsListView$OnScrollListener)this.mScrollListener);
            final AdapterView$OnItemSelectedListener mItemSelectedListener = this.mItemSelectedListener;
            if (mItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(mItemSelectedListener);
            }
            final DropDownListView mDropDownList2 = this.mDropDownList;
            final View mPromptView = this.mPromptView;
            Object contentView;
            if (mPromptView != null) {
                contentView = new LinearLayout(mContext);
                ((LinearLayout)contentView).setOrientation(1);
                final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(-1, 0, 1.0f);
                final int mPromptPosition = this.mPromptPosition;
                if (mPromptPosition != 0) {
                    if (mPromptPosition != 1) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Invalid hint position ");
                        sb.append(this.mPromptPosition);
                        Log.e("ListPopupWindow", sb.toString());
                    }
                    else {
                        ((LinearLayout)contentView).addView((View)mDropDownList2, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                        ((LinearLayout)contentView).addView(mPromptView);
                    }
                }
                else {
                    ((LinearLayout)contentView).addView(mPromptView);
                    ((LinearLayout)contentView).addView((View)mDropDownList2, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                }
                int mDropDownWidth = this.mDropDownWidth;
                int n;
                if (mDropDownWidth >= 0) {
                    n = Integer.MIN_VALUE;
                }
                else {
                    mDropDownWidth = (n = 0);
                }
                mPromptView.measure(View$MeasureSpec.makeMeasureSpec(mDropDownWidth, n), 0);
                final LinearLayout$LayoutParams linearLayout$LayoutParams2 = (LinearLayout$LayoutParams)mPromptView.getLayoutParams();
                n2 = mPromptView.getMeasuredHeight() + linearLayout$LayoutParams2.topMargin + linearLayout$LayoutParams2.bottomMargin;
            }
            else {
                n2 = 0;
                contentView = mDropDownList2;
            }
            this.mPopup.setContentView((View)contentView);
        }
        else {
            final ViewGroup viewGroup = (ViewGroup)this.mPopup.getContentView();
            final View mPromptView2 = this.mPromptView;
            if (mPromptView2 != null) {
                final LinearLayout$LayoutParams linearLayout$LayoutParams3 = (LinearLayout$LayoutParams)mPromptView2.getLayoutParams();
                n2 = mPromptView2.getMeasuredHeight() + linearLayout$LayoutParams3.topMargin + linearLayout$LayoutParams3.bottomMargin;
            }
            else {
                n2 = 0;
            }
        }
        final Drawable background = this.mPopup.getBackground();
        int n4;
        if (background != null) {
            background.getPadding(this.mTempRect);
            final Rect mTempRect = this.mTempRect;
            final int top = mTempRect.top;
            final int n3 = n4 = mTempRect.bottom + top;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -top;
                n4 = n3;
            }
        }
        else {
            this.mTempRect.setEmpty();
            n4 = 0;
        }
        if (this.mPopup.getInputMethodMode() != 2) {
            b = false;
        }
        final int maxAvailableHeight = this.getMaxAvailableHeight(this.getAnchorView(), this.mDropDownVerticalOffset, b);
        if (!this.mDropDownAlwaysVisible && this.mDropDownHeight != -1) {
            final int mDropDownWidth2 = this.mDropDownWidth;
            int n5;
            if (mDropDownWidth2 != -2) {
                if (mDropDownWidth2 != -1) {
                    n5 = View$MeasureSpec.makeMeasureSpec(mDropDownWidth2, 1073741824);
                }
                else {
                    final int widthPixels = this.mContext.getResources().getDisplayMetrics().widthPixels;
                    final Rect mTempRect2 = this.mTempRect;
                    n5 = View$MeasureSpec.makeMeasureSpec(widthPixels - (mTempRect2.left + mTempRect2.right), 1073741824);
                }
            }
            else {
                final int widthPixels2 = this.mContext.getResources().getDisplayMetrics().widthPixels;
                final Rect mTempRect3 = this.mTempRect;
                n5 = View$MeasureSpec.makeMeasureSpec(widthPixels2 - (mTempRect3.left + mTempRect3.right), Integer.MIN_VALUE);
            }
            final int measureHeightOfChildrenCompat = this.mDropDownList.measureHeightOfChildrenCompat(n5, 0, -1, maxAvailableHeight - n2, -1);
            int n6 = n2;
            if (measureHeightOfChildrenCompat > 0) {
                n6 = n2 + (n4 + (this.mDropDownList.getPaddingTop() + this.mDropDownList.getPaddingBottom()));
            }
            return measureHeightOfChildrenCompat + n6;
        }
        return maxAvailableHeight + n4;
    }
    
    private int getMaxAvailableHeight(final View view, final int i, final boolean b) {
        if (Build$VERSION.SDK_INT <= 23) {
            final Method sGetMaxAvailableHeightMethod = ListPopupWindow.sGetMaxAvailableHeightMethod;
            if (sGetMaxAvailableHeightMethod != null) {
                try {
                    return (int)sGetMaxAvailableHeightMethod.invoke(this.mPopup, view, i, b);
                }
                catch (Exception ex) {
                    Log.i("ListPopupWindow", "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
                }
            }
            return this.mPopup.getMaxAvailableHeight(view, i);
        }
        return this.mPopup.getMaxAvailableHeight(view, i, b);
    }
    
    private void removePromptView() {
        final View mPromptView = this.mPromptView;
        if (mPromptView != null) {
            final ViewParent parent = mPromptView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup)parent).removeView(this.mPromptView);
            }
        }
    }
    
    private void setPopupClipToScreenEnabled(final boolean b) {
        if (Build$VERSION.SDK_INT <= 28) {
            final Method sSetClipToWindowEnabledMethod = ListPopupWindow.sSetClipToWindowEnabledMethod;
            if (sSetClipToWindowEnabledMethod != null) {
                try {
                    sSetClipToWindowEnabledMethod.invoke(this.mPopup, b);
                }
                catch (Exception ex) {
                    Log.i("ListPopupWindow", "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
                }
            }
        }
        else {
            this.mPopup.setIsClippedToScreen(b);
        }
    }
    
    public void clearListSelection() {
        final DropDownListView mDropDownList = this.mDropDownList;
        if (mDropDownList != null) {
            mDropDownList.setListSelectionHidden(true);
            mDropDownList.requestLayout();
        }
    }
    
    DropDownListView createDropDownListView(final Context context, final boolean b) {
        return new DropDownListView(context, b);
    }
    
    @Override
    public void dismiss() {
        this.mPopup.dismiss();
        this.removePromptView();
        this.mPopup.setContentView((View)null);
        this.mDropDownList = null;
        this.mHandler.removeCallbacks((Runnable)this.mResizePopupRunnable);
    }
    
    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }
    
    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }
    
    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }
    
    @Override
    public ListView getListView() {
        return this.mDropDownList;
    }
    
    public int getVerticalOffset() {
        if (!this.mDropDownVerticalOffsetSet) {
            return 0;
        }
        return this.mDropDownVerticalOffset;
    }
    
    public int getWidth() {
        return this.mDropDownWidth;
    }
    
    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }
    
    public boolean isModal() {
        return this.mModal;
    }
    
    @Override
    public boolean isShowing() {
        return this.mPopup.isShowing();
    }
    
    public void setAdapter(final ListAdapter mAdapter) {
        final DataSetObserver mObserver = this.mObserver;
        if (mObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        }
        else {
            final ListAdapter mAdapter2 = this.mAdapter;
            if (mAdapter2 != null) {
                mAdapter2.unregisterDataSetObserver(mObserver);
            }
        }
        if ((this.mAdapter = mAdapter) != null) {
            mAdapter.registerDataSetObserver(this.mObserver);
        }
        final DropDownListView mDropDownList = this.mDropDownList;
        if (mDropDownList != null) {
            mDropDownList.setAdapter(this.mAdapter);
        }
    }
    
    public void setAnchorView(final View mDropDownAnchorView) {
        this.mDropDownAnchorView = mDropDownAnchorView;
    }
    
    public void setAnimationStyle(final int animationStyle) {
        this.mPopup.setAnimationStyle(animationStyle);
    }
    
    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        this.mPopup.setBackgroundDrawable(backgroundDrawable);
    }
    
    public void setContentWidth(final int width) {
        final Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            final Rect mTempRect = this.mTempRect;
            this.mDropDownWidth = mTempRect.left + mTempRect.right + width;
        }
        else {
            this.setWidth(width);
        }
    }
    
    public void setDropDownGravity(final int mDropDownGravity) {
        this.mDropDownGravity = mDropDownGravity;
    }
    
    public void setEpicenterBounds(Rect mEpicenterBounds) {
        if (mEpicenterBounds != null) {
            mEpicenterBounds = new Rect(mEpicenterBounds);
        }
        else {
            mEpicenterBounds = null;
        }
        this.mEpicenterBounds = mEpicenterBounds;
    }
    
    public void setHorizontalOffset(final int mDropDownHorizontalOffset) {
        this.mDropDownHorizontalOffset = mDropDownHorizontalOffset;
    }
    
    public void setInputMethodMode(final int inputMethodMode) {
        this.mPopup.setInputMethodMode(inputMethodMode);
    }
    
    public void setModal(final boolean b) {
        this.mModal = b;
        this.mPopup.setFocusable(b);
    }
    
    public void setOnDismissListener(final PopupWindow$OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }
    
    public void setOnItemClickListener(final AdapterView$OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public void setOverlapAnchor(final boolean mOverlapAnchor) {
        this.mOverlapAnchorSet = true;
        this.mOverlapAnchor = mOverlapAnchor;
    }
    
    public void setPromptPosition(final int mPromptPosition) {
        this.mPromptPosition = mPromptPosition;
    }
    
    public void setSelection(final int selection) {
        final DropDownListView mDropDownList = this.mDropDownList;
        if (this.isShowing() && mDropDownList != null) {
            mDropDownList.setListSelectionHidden(false);
            mDropDownList.setSelection(selection);
            if (mDropDownList.getChoiceMode() != 0) {
                mDropDownList.setItemChecked(selection, true);
            }
        }
    }
    
    public void setVerticalOffset(final int mDropDownVerticalOffset) {
        this.mDropDownVerticalOffset = mDropDownVerticalOffset;
        this.mDropDownVerticalOffsetSet = true;
    }
    
    public void setWidth(final int mDropDownWidth) {
        this.mDropDownWidth = mDropDownWidth;
    }
    
    @Override
    public void show() {
        int buildDropDown = this.buildDropDown();
        final boolean inputMethodNotNeeded = this.isInputMethodNotNeeded();
        PopupWindowCompat.setWindowLayoutType(this.mPopup, this.mDropDownWindowLayoutType);
        final boolean showing = this.mPopup.isShowing();
        boolean outsideTouchable = true;
        if (showing) {
            if (!ViewCompat.isAttachedToWindow(this.getAnchorView())) {
                return;
            }
            final int mDropDownWidth = this.mDropDownWidth;
            int width;
            if (mDropDownWidth == -1) {
                width = -1;
            }
            else if ((width = mDropDownWidth) == -2) {
                width = this.getAnchorView().getWidth();
            }
            final int mDropDownHeight = this.mDropDownHeight;
            if (mDropDownHeight == -1) {
                if (!inputMethodNotNeeded) {
                    buildDropDown = -1;
                }
                if (inputMethodNotNeeded) {
                    final PopupWindow mPopup = this.mPopup;
                    int width2;
                    if (this.mDropDownWidth == -1) {
                        width2 = -1;
                    }
                    else {
                        width2 = 0;
                    }
                    mPopup.setWidth(width2);
                    this.mPopup.setHeight(0);
                }
                else {
                    final PopupWindow mPopup2 = this.mPopup;
                    int width3;
                    if (this.mDropDownWidth == -1) {
                        width3 = -1;
                    }
                    else {
                        width3 = 0;
                    }
                    mPopup2.setWidth(width3);
                    this.mPopup.setHeight(-1);
                }
            }
            else if (mDropDownHeight != -2) {
                buildDropDown = mDropDownHeight;
            }
            final PopupWindow mPopup3 = this.mPopup;
            if (this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) {
                outsideTouchable = false;
            }
            mPopup3.setOutsideTouchable(outsideTouchable);
            final PopupWindow mPopup4 = this.mPopup;
            final View anchorView = this.getAnchorView();
            final int mDropDownHorizontalOffset = this.mDropDownHorizontalOffset;
            final int mDropDownVerticalOffset = this.mDropDownVerticalOffset;
            if (width < 0) {
                width = -1;
            }
            if (buildDropDown < 0) {
                buildDropDown = -1;
            }
            mPopup4.update(anchorView, mDropDownHorizontalOffset, mDropDownVerticalOffset, width, buildDropDown);
        }
        else {
            final int mDropDownWidth2 = this.mDropDownWidth;
            int width4;
            if (mDropDownWidth2 == -1) {
                width4 = -1;
            }
            else if ((width4 = mDropDownWidth2) == -2) {
                width4 = this.getAnchorView().getWidth();
            }
            final int mDropDownHeight2 = this.mDropDownHeight;
            if (mDropDownHeight2 == -1) {
                buildDropDown = -1;
            }
            else if (mDropDownHeight2 != -2) {
                buildDropDown = mDropDownHeight2;
            }
            this.mPopup.setWidth(width4);
            this.mPopup.setHeight(buildDropDown);
            this.setPopupClipToScreenEnabled(true);
            this.mPopup.setOutsideTouchable(!this.mForceIgnoreOutsideTouch && !this.mDropDownAlwaysVisible);
            this.mPopup.setTouchInterceptor((View$OnTouchListener)this.mTouchInterceptor);
            if (this.mOverlapAnchorSet) {
                PopupWindowCompat.setOverlapAnchor(this.mPopup, this.mOverlapAnchor);
            }
            if (Build$VERSION.SDK_INT <= 28) {
                final Method sSetEpicenterBoundsMethod = ListPopupWindow.sSetEpicenterBoundsMethod;
                if (sSetEpicenterBoundsMethod != null) {
                    try {
                        sSetEpicenterBoundsMethod.invoke(this.mPopup, this.mEpicenterBounds);
                    }
                    catch (Exception ex) {
                        Log.e("ListPopupWindow", "Could not invoke setEpicenterBounds on PopupWindow", (Throwable)ex);
                    }
                }
            }
            else {
                this.mPopup.setEpicenterBounds(this.mEpicenterBounds);
            }
            PopupWindowCompat.showAsDropDown(this.mPopup, this.getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
            this.mDropDownList.setSelection(-1);
            if (!this.mModal || this.mDropDownList.isInTouchMode()) {
                this.clearListSelection();
            }
            if (!this.mModal) {
                this.mHandler.post((Runnable)this.mHideSelector);
            }
        }
    }
    
    private class ListSelectorHider implements Runnable
    {
        ListSelectorHider() {
        }
        
        @Override
        public void run() {
            ListPopupWindow.this.clearListSelection();
        }
    }
    
    private class PopupDataSetObserver extends DataSetObserver
    {
        PopupDataSetObserver() {
        }
        
        public void onChanged() {
            if (ListPopupWindow.this.isShowing()) {
                ListPopupWindow.this.show();
            }
        }
        
        public void onInvalidated() {
            ListPopupWindow.this.dismiss();
        }
    }
    
    private class PopupScrollListener implements AbsListView$OnScrollListener
    {
        PopupScrollListener() {
        }
        
        public void onScroll(final AbsListView absListView, final int n, final int n2, final int n3) {
        }
        
        public void onScrollStateChanged(final AbsListView absListView, final int n) {
            if (n == 1 && !ListPopupWindow.this.isInputMethodNotNeeded() && ListPopupWindow.this.mPopup.getContentView() != null) {
                final ListPopupWindow this$0 = ListPopupWindow.this;
                this$0.mHandler.removeCallbacks((Runnable)this$0.mResizePopupRunnable);
                ListPopupWindow.this.mResizePopupRunnable.run();
            }
        }
    }
    
    private class PopupTouchInterceptor implements View$OnTouchListener
    {
        PopupTouchInterceptor() {
        }
        
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            final int action = motionEvent.getAction();
            final int n = (int)motionEvent.getX();
            final int n2 = (int)motionEvent.getY();
            if (action == 0) {
                final PopupWindow mPopup = ListPopupWindow.this.mPopup;
                if (mPopup != null && mPopup.isShowing() && n >= 0 && n < ListPopupWindow.this.mPopup.getWidth() && n2 >= 0 && n2 < ListPopupWindow.this.mPopup.getHeight()) {
                    final ListPopupWindow this$0 = ListPopupWindow.this;
                    this$0.mHandler.postDelayed((Runnable)this$0.mResizePopupRunnable, 250L);
                    return false;
                }
            }
            if (action == 1) {
                final ListPopupWindow this$2 = ListPopupWindow.this;
                this$2.mHandler.removeCallbacks((Runnable)this$2.mResizePopupRunnable);
            }
            return false;
        }
    }
    
    private class ResizePopupRunnable implements Runnable
    {
        ResizePopupRunnable() {
        }
        
        @Override
        public void run() {
            final DropDownListView mDropDownList = ListPopupWindow.this.mDropDownList;
            if (mDropDownList != null && ViewCompat.isAttachedToWindow((View)mDropDownList) && ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount()) {
                final int childCount = ListPopupWindow.this.mDropDownList.getChildCount();
                final ListPopupWindow this$0 = ListPopupWindow.this;
                if (childCount <= this$0.mListItemExpandMaximum) {
                    this$0.mPopup.setInputMethodMode(2);
                    ListPopupWindow.this.show();
                }
            }
        }
    }
}
