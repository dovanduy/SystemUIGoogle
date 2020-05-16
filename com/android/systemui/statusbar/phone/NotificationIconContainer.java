// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import android.util.Property;
import java.util.function.Consumer;
import com.android.systemui.statusbar.notification.stack.ViewState;
import android.graphics.Paint$Style;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.notification.stack.AnimationFilter;
import com.android.internal.statusbar.StatusBarIcon;
import java.util.ArrayList;
import androidx.collection.ArrayMap;
import android.graphics.Rect;
import com.android.systemui.statusbar.StatusBarIconView;
import android.view.View;
import java.util.HashMap;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.AlphaOptimizedFrameLayout;

public class NotificationIconContainer extends AlphaOptimizedFrameLayout
{
    private static final AnimationProperties ADD_ICON_PROPERTIES;
    private static final AnimationProperties DOT_ANIMATION_PROPERTIES;
    private static final AnimationProperties ICON_ANIMATION_PROPERTIES;
    private static final AnimationProperties UNISOLATION_PROPERTY;
    private static final AnimationProperties UNISOLATION_PROPERTY_OTHERS;
    private static final AnimationProperties sTempProperties;
    private int[] mAbsolutePosition;
    private int mActualLayoutWidth;
    private float mActualPaddingEnd;
    private float mActualPaddingStart;
    private int mAddAnimationStartIndex;
    private boolean mAnimationsEnabled;
    private int mCannedAnimationStartIndex;
    private boolean mChangingViewPositions;
    private boolean mDisallowNextAnimation;
    private int mDotPadding;
    private boolean mDozing;
    private IconState mFirstVisibleIconState;
    private int mIconSize;
    private final HashMap<View, IconState> mIconStates;
    private boolean mIsStaticLayout;
    private StatusBarIconView mIsolatedIcon;
    private View mIsolatedIconForAnimation;
    private Rect mIsolatedIconLocation;
    private IconState mLastVisibleIconState;
    private int mNumDots;
    private boolean mOnLockScreen;
    private float mOpenedAmount;
    private int mOverflowWidth;
    private ArrayMap<String, ArrayList<StatusBarIcon>> mReplacingIcons;
    private int mSpeedBumpIndex;
    private int mStaticDotDiameter;
    private int mStaticDotRadius;
    private float mVisualOverflowStart;
    
    static {
        final AnimationProperties dot_ANIMATION_PROPERTIES = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateX();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        dot_ANIMATION_PROPERTIES.setDuration(200L);
        DOT_ANIMATION_PROPERTIES = dot_ANIMATION_PROPERTIES;
        final AnimationProperties icon_ANIMATION_PROPERTIES = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateX();
                mAnimationFilter.animateY();
                mAnimationFilter.animateAlpha();
                mAnimationFilter.animateScale();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        icon_ANIMATION_PROPERTIES.setDuration(100L);
        ICON_ANIMATION_PROPERTIES = icon_ANIMATION_PROPERTIES;
        sTempProperties = new AnimationProperties() {
            private AnimationFilter mAnimationFilter = new AnimationFilter();
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        final AnimationProperties add_ICON_PROPERTIES = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateAlpha();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        add_ICON_PROPERTIES.setDuration(200L);
        add_ICON_PROPERTIES.setDelay(50L);
        ADD_ICON_PROPERTIES = add_ICON_PROPERTIES;
        final AnimationProperties unisolation_PROPERTY_OTHERS = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateAlpha();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        unisolation_PROPERTY_OTHERS.setDuration(110L);
        UNISOLATION_PROPERTY_OTHERS = unisolation_PROPERTY_OTHERS;
        final AnimationProperties unisolation_PROPERTY = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;
            
            {
                final AnimationFilter mAnimationFilter = new AnimationFilter();
                mAnimationFilter.animateX();
                this.mAnimationFilter = mAnimationFilter;
            }
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        unisolation_PROPERTY.setDuration(110L);
        UNISOLATION_PROPERTY = unisolation_PROPERTY;
    }
    
    public NotificationIconContainer(final Context context, final AttributeSet set) {
        super(context, set);
        this.mIsStaticLayout = true;
        this.mIconStates = new HashMap<View, IconState>();
        this.mActualLayoutWidth = Integer.MIN_VALUE;
        this.mActualPaddingEnd = -2.14748365E9f;
        this.mActualPaddingStart = -2.14748365E9f;
        this.mAddAnimationStartIndex = -1;
        this.mCannedAnimationStartIndex = -1;
        this.mSpeedBumpIndex = -1;
        this.mOpenedAmount = 0.0f;
        this.mAnimationsEnabled = true;
        this.mAbsolutePosition = new int[2];
        this.initDimens();
        this.setWillNotDraw(true);
    }
    
    private boolean areAnimationsEnabled(final StatusBarIconView statusBarIconView) {
        return this.mAnimationsEnabled || statusBarIconView == this.mIsolatedIcon;
    }
    
    private int findFirstViewIndexAfter(final float n) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (this.getChildAt(i).getTranslationX() > n) {
                return i;
            }
        }
        return this.getChildCount();
    }
    
    private float getActualPaddingEnd() {
        final float mActualPaddingEnd = this.mActualPaddingEnd;
        if (mActualPaddingEnd == -2.14748365E9f) {
            return (float)this.getPaddingEnd();
        }
        return mActualPaddingEnd;
    }
    
    private float getLayoutEnd() {
        return this.getActualWidth() - this.getActualPaddingEnd();
    }
    
    private float getMaxOverflowStart() {
        return this.getLayoutEnd() - this.mOverflowWidth;
    }
    
    private void initDimens() {
        this.mDotPadding = this.getResources().getDimensionPixelSize(R$dimen.overflow_icon_dot_padding);
        final int dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.overflow_dot_radius);
        this.mStaticDotRadius = dimensionPixelSize;
        this.mStaticDotDiameter = dimensionPixelSize * 2;
    }
    
    private boolean isReplacingIcon(final View view) {
        if (this.mReplacingIcons == null) {
            return false;
        }
        if (!(view instanceof StatusBarIconView)) {
            return false;
        }
        final StatusBarIconView statusBarIconView = (StatusBarIconView)view;
        final Icon sourceIcon = statusBarIconView.getSourceIcon();
        final ArrayList<StatusBarIcon> list = this.mReplacingIcons.get(statusBarIconView.getNotification().getGroupKey());
        return list != null && sourceIcon.sameAs(list.get(0).icon);
    }
    
    private void setIconSize(final int mIconSize) {
        this.mIconSize = mIconSize;
        this.mOverflowWidth = mIconSize + (this.mStaticDotDiameter + this.mDotPadding) * 0;
    }
    
    private void updateState() {
        this.resetViewStates();
        this.calculateIconTranslations();
        this.applyIconStates();
    }
    
    public void applyIconStates() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            final IconState iconState = this.mIconStates.get(child);
            if (iconState != null) {
                iconState.applyToView(child);
            }
        }
        this.mAddAnimationStartIndex = -1;
        this.mCannedAnimationStartIndex = -1;
        this.mDisallowNextAnimation = false;
        this.mIsolatedIconForAnimation = null;
    }
    
    public void calculateIconTranslations() {
        float actualPaddingStart = this.getActualPaddingStart();
        final int childCount = this.getChildCount();
        int n;
        if (this.mOnLockScreen) {
            n = 5;
        }
        else if (this.mIsStaticLayout) {
            n = 4;
        }
        else {
            n = childCount;
        }
        final float layoutEnd = this.getLayoutEnd();
        final float maxOverflowStart = this.getMaxOverflowStart();
        this.mVisualOverflowStart = 0.0f;
        this.mFirstVisibleIconState = null;
        final int mSpeedBumpIndex = this.mSpeedBumpIndex;
        final boolean b = mSpeedBumpIndex != -1 && mSpeedBumpIndex < this.getChildCount();
        int n2 = -1;
        int n4;
        for (int i = 0; i < childCount; ++i, n2 = n4) {
            final View child = this.getChildAt(i);
            final IconState mFirstVisibleIconState = this.mIconStates.get(child);
            if (mFirstVisibleIconState.iconAppearAmount == 1.0f) {
                mFirstVisibleIconState.xTranslation = actualPaddingStart;
            }
            if (this.mFirstVisibleIconState == null) {
                this.mFirstVisibleIconState = mFirstVisibleIconState;
            }
            final int mSpeedBumpIndex2 = this.mSpeedBumpIndex;
            final boolean b2 = (mSpeedBumpIndex2 != -1 && i >= mSpeedBumpIndex2 && mFirstVisibleIconState.iconAppearAmount > 0.0f) || i >= n;
            final boolean b3 = i == childCount - 1;
            float iconScaleIncreased;
            if (this.mOnLockScreen && child instanceof StatusBarIconView) {
                iconScaleIncreased = ((StatusBarIconView)child).getIconScaleIncreased();
            }
            else {
                iconScaleIncreased = 1.0f;
            }
            boolean b4 = b3;
            if (this.mOpenedAmount != 0.0f) {
                b4 = (b3 && !b && !b2);
            }
            mFirstVisibleIconState.visibleState = 0;
            float n3;
            if (b4) {
                n3 = layoutEnd - this.mIconSize;
            }
            else {
                n3 = maxOverflowStart - this.mIconSize;
            }
            final boolean b5 = actualPaddingStart > n3;
            n4 = n2;
            Label_0421: {
                if (n2 == -1) {
                    if (!b2) {
                        n4 = n2;
                        if (!b5) {
                            break Label_0421;
                        }
                    }
                    int n5;
                    if (b4 && !b2) {
                        n5 = i - 1;
                    }
                    else {
                        n5 = i;
                    }
                    this.mVisualOverflowStart = layoutEnd - this.mOverflowWidth;
                    if (!b2) {
                        n4 = n5;
                        if (!this.mIsStaticLayout) {
                            break Label_0421;
                        }
                    }
                    this.mVisualOverflowStart = Math.min(actualPaddingStart, this.mVisualOverflowStart);
                    n4 = n5;
                }
            }
            actualPaddingStart += mFirstVisibleIconState.iconAppearAmount * child.getWidth() * iconScaleIncreased;
        }
        this.mNumDots = 0;
        float n7;
        if (n2 != -1) {
            float mVisualOverflowStart = this.mVisualOverflowStart;
            int n6 = n2;
            while (true) {
                n7 = mVisualOverflowStart;
                if (n6 >= childCount) {
                    break;
                }
                final IconState mLastVisibleIconState = this.mIconStates.get(this.getChildAt(n6));
                final int n8 = this.mStaticDotDiameter + this.mDotPadding;
                mLastVisibleIconState.xTranslation = mVisualOverflowStart;
                final int mNumDots = this.mNumDots;
                if (mNumDots < 1) {
                    if (mNumDots == 0 && mLastVisibleIconState.iconAppearAmount < 0.8f) {
                        mLastVisibleIconState.visibleState = 0;
                    }
                    else {
                        mLastVisibleIconState.visibleState = 1;
                        ++this.mNumDots;
                    }
                    int n9 = n8;
                    if (this.mNumDots == 1) {
                        n9 = n8 * 1;
                    }
                    mVisualOverflowStart += n9 * mLastVisibleIconState.iconAppearAmount;
                    this.mLastVisibleIconState = mLastVisibleIconState;
                }
                else {
                    mLastVisibleIconState.visibleState = 2;
                }
                ++n6;
            }
        }
        else {
            n7 = actualPaddingStart;
            if (childCount > 0) {
                this.mLastVisibleIconState = this.mIconStates.get(this.getChildAt(childCount - 1));
                this.mFirstVisibleIconState = this.mIconStates.get(this.getChildAt(0));
                n7 = actualPaddingStart;
            }
        }
        if (this.mOnLockScreen && n7 < this.getLayoutEnd()) {
            final IconState mFirstVisibleIconState2 = this.mFirstVisibleIconState;
            float xTranslation;
            if (mFirstVisibleIconState2 == null) {
                xTranslation = 0.0f;
            }
            else {
                xTranslation = mFirstVisibleIconState2.xTranslation;
            }
            final IconState mLastVisibleIconState2 = this.mLastVisibleIconState;
            float n10;
            if (mLastVisibleIconState2 != null) {
                n10 = Math.min((float)this.getWidth(), mLastVisibleIconState2.xTranslation + this.mIconSize) - xTranslation;
            }
            else {
                n10 = 0.0f;
            }
            float n11 = (this.getLayoutEnd() - this.getActualPaddingStart() - n10) / 2.0f;
            if (n2 != -1) {
                n11 = ((this.getLayoutEnd() - this.mVisualOverflowStart) / 2.0f + n11) / 2.0f;
            }
            for (int j = 0; j < childCount; ++j) {
                final IconState iconState = this.mIconStates.get(this.getChildAt(j));
                iconState.xTranslation += n11;
            }
        }
        if (this.isLayoutRtl()) {
            for (int k = 0; k < childCount; ++k) {
                final View child2 = this.getChildAt(k);
                final IconState iconState2 = this.mIconStates.get(child2);
                iconState2.xTranslation = this.getWidth() - iconState2.xTranslation - child2.getWidth();
            }
        }
        final StatusBarIconView mIsolatedIcon = this.mIsolatedIcon;
        if (mIsolatedIcon != null) {
            final IconState iconState3 = this.mIconStates.get(mIsolatedIcon);
            if (iconState3 != null) {
                iconState3.xTranslation = this.mIsolatedIconLocation.left - this.mAbsolutePosition[0] - (1.0f - this.mIsolatedIcon.getIconScale()) * this.mIsolatedIcon.getWidth() / 2.0f;
                iconState3.visibleState = 0;
            }
        }
    }
    
    public float getActualPaddingStart() {
        final float mActualPaddingStart = this.mActualPaddingStart;
        if (mActualPaddingStart == -2.14748365E9f) {
            return (float)this.getPaddingStart();
        }
        return mActualPaddingStart;
    }
    
    public int getActualWidth() {
        final int mActualLayoutWidth = this.mActualLayoutWidth;
        if (mActualLayoutWidth == Integer.MIN_VALUE) {
            return this.getWidth();
        }
        return mActualLayoutWidth;
    }
    
    public int getFinalTranslationX() {
        if (this.mLastVisibleIconState == null) {
            return 0;
        }
        float n;
        if (this.isLayoutRtl()) {
            n = this.getWidth() - this.mLastVisibleIconState.xTranslation;
        }
        else {
            n = this.mLastVisibleIconState.xTranslation + this.mIconSize;
        }
        return Math.min(this.getWidth(), (int)n);
    }
    
    public IconState getIconState(final StatusBarIconView key) {
        return this.mIconStates.get(key);
    }
    
    public int getNoOverflowExtraPadding() {
        if (this.mNumDots != 0) {
            return 0;
        }
        int mOverflowWidth;
        if (this.getFinalTranslationX() + (mOverflowWidth = this.mOverflowWidth) > this.getWidth()) {
            mOverflowWidth = this.getWidth() - this.getFinalTranslationX();
        }
        return mOverflowWidth;
    }
    
    public int getPartialOverflowExtraPadding() {
        if (!this.hasPartialOverflow()) {
            return 0;
        }
        int n;
        if (this.getFinalTranslationX() + (n = (1 - this.mNumDots) * (this.mStaticDotDiameter + this.mDotPadding)) > this.getWidth()) {
            n = this.getWidth() - this.getFinalTranslationX();
        }
        return n;
    }
    
    public boolean hasOverflow() {
        return this.mNumDots > 0;
    }
    
    public boolean hasPartialOverflow() {
        final int mNumDots = this.mNumDots;
        boolean b = true;
        if (mNumDots <= 0 || mNumDots >= 1) {
            b = false;
        }
        return b;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.initDimens();
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final Paint paint = new Paint();
        paint.setColor(-65536);
        paint.setStyle(Paint$Style.STROKE);
        canvas.drawRect(this.getActualPaddingStart(), 0.0f, this.getLayoutEnd(), (float)this.getHeight(), paint);
    }
    
    protected void onLayout(final boolean b, int i, int measuredWidth, int measuredHeight, int n) {
        final float n2 = this.getHeight() / 2.0f;
        this.mIconSize = 0;
        View child;
        for (i = 0; i < this.getChildCount(); ++i) {
            child = this.getChildAt(i);
            measuredWidth = child.getMeasuredWidth();
            measuredHeight = child.getMeasuredHeight();
            n = (int)(n2 - measuredHeight / 2.0f);
            child.layout(0, n, measuredWidth, measuredHeight + n);
            if (i == 0) {
                this.setIconSize(child.getWidth());
            }
        }
        this.getLocationOnScreen(this.mAbsolutePosition);
        if (this.mIsStaticLayout) {
            this.updateState();
        }
    }
    
    public void onViewAdded(final View key) {
        super.onViewAdded(key);
        final boolean replacingIcon = this.isReplacingIcon(key);
        if (!this.mChangingViewPositions) {
            final IconState value = new IconState(key);
            if (replacingIcon) {
                value.justAdded = false;
                value.justReplaced = true;
            }
            this.mIconStates.put(key, value);
        }
        final int indexOfChild = this.indexOfChild(key);
        if (indexOfChild < this.getChildCount() - 1 && !replacingIcon && this.mIconStates.get(this.getChildAt(indexOfChild + 1)).iconAppearAmount > 0.0f) {
            final int mAddAnimationStartIndex = this.mAddAnimationStartIndex;
            if (mAddAnimationStartIndex < 0) {
                this.mAddAnimationStartIndex = indexOfChild;
            }
            else {
                this.mAddAnimationStartIndex = Math.min(mAddAnimationStartIndex, indexOfChild);
            }
        }
        if (key instanceof StatusBarIconView) {
            ((StatusBarIconView)key).setDozing(this.mDozing, false, 0L);
        }
    }
    
    public void onViewRemoved(final View key) {
        super.onViewRemoved(key);
        if (key instanceof StatusBarIconView) {
            final boolean replacingIcon = this.isReplacingIcon(key);
            final StatusBarIconView statusBarIconView = (StatusBarIconView)key;
            if (this.areAnimationsEnabled(statusBarIconView) && statusBarIconView.getVisibleState() != 2 && key.getVisibility() == 0 && replacingIcon) {
                final int firstViewIndexAfter = this.findFirstViewIndexAfter(statusBarIconView.getTranslationX());
                final int mAddAnimationStartIndex = this.mAddAnimationStartIndex;
                if (mAddAnimationStartIndex < 0) {
                    this.mAddAnimationStartIndex = firstViewIndexAfter;
                }
                else {
                    this.mAddAnimationStartIndex = Math.min(mAddAnimationStartIndex, firstViewIndexAfter);
                }
            }
            if (!this.mChangingViewPositions) {
                this.mIconStates.remove(key);
                if (this.areAnimationsEnabled(statusBarIconView) && !replacingIcon) {
                    boolean b = false;
                    this.addTransientView((View)statusBarIconView, 0);
                    if (key == this.mIsolatedIcon) {
                        b = true;
                    }
                    final _$$Lambda$NotificationIconContainer$sYOppFQ4vSNRi0SYdFbv716CxNY $$Lambda$NotificationIconContainer$sYOppFQ4vSNRi0SYdFbv716CxNY = new _$$Lambda$NotificationIconContainer$sYOppFQ4vSNRi0SYdFbv716CxNY(this, statusBarIconView);
                    long n;
                    if (b) {
                        n = 110L;
                    }
                    else {
                        n = 0L;
                    }
                    statusBarIconView.setVisibleState(2, true, $$Lambda$NotificationIconContainer$sYOppFQ4vSNRi0SYdFbv716CxNY, n);
                }
            }
        }
    }
    
    public void resetViewStates() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            final IconState iconState = this.mIconStates.get(child);
            iconState.initFrom(child);
            final StatusBarIconView mIsolatedIcon = this.mIsolatedIcon;
            float alpha;
            if (mIsolatedIcon != null && child != mIsolatedIcon) {
                alpha = 0.0f;
            }
            else {
                alpha = 1.0f;
            }
            iconState.alpha = alpha;
            iconState.hidden = false;
        }
    }
    
    public void setActualLayoutWidth(final int mActualLayoutWidth) {
        this.mActualLayoutWidth = mActualLayoutWidth;
    }
    
    public void setActualPaddingEnd(final float mActualPaddingEnd) {
        this.mActualPaddingEnd = mActualPaddingEnd;
    }
    
    public void setActualPaddingStart(final float mActualPaddingStart) {
        this.mActualPaddingStart = mActualPaddingStart;
    }
    
    public void setAnimationsEnabled(final boolean mAnimationsEnabled) {
        if (!mAnimationsEnabled && this.mAnimationsEnabled) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                final View child = this.getChildAt(i);
                final IconState iconState = this.mIconStates.get(child);
                if (iconState != null) {
                    iconState.cancelAnimations(child);
                    iconState.applyToView(child);
                }
            }
        }
        this.mAnimationsEnabled = mAnimationsEnabled;
    }
    
    public void setChangingViewPositions(final boolean mChangingViewPositions) {
        this.mChangingViewPositions = mChangingViewPositions;
    }
    
    public void setDozing(final boolean mDozing, final boolean b, final long n) {
        this.mDozing = mDozing;
        this.mDisallowNextAnimation |= (b ^ true);
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child instanceof StatusBarIconView) {
                ((StatusBarIconView)child).setDozing(mDozing, b, n);
            }
        }
    }
    
    public void setIsStaticLayout(final boolean mIsStaticLayout) {
        this.mIsStaticLayout = mIsStaticLayout;
    }
    
    public void setIsolatedIconLocation(final Rect mIsolatedIconLocation, final boolean b) {
        this.mIsolatedIconLocation = mIsolatedIconLocation;
        if (b) {
            this.updateState();
        }
    }
    
    public void setOnLockScreen(final boolean mOnLockScreen) {
        this.mOnLockScreen = mOnLockScreen;
    }
    
    public void setOpenedAmount(final float mOpenedAmount) {
        this.mOpenedAmount = mOpenedAmount;
    }
    
    public void setReplacingIcons(final ArrayMap<String, ArrayList<StatusBarIcon>> mReplacingIcons) {
        this.mReplacingIcons = mReplacingIcons;
    }
    
    public void setSpeedBumpIndex(final int mSpeedBumpIndex) {
        this.mSpeedBumpIndex = mSpeedBumpIndex;
    }
    
    public void showIconIsolated(final StatusBarIconView mIsolatedIcon, final boolean b) {
        if (b) {
            StatusBarIconView mIsolatedIcon2;
            if (mIsolatedIcon != null) {
                mIsolatedIcon2 = mIsolatedIcon;
            }
            else {
                mIsolatedIcon2 = this.mIsolatedIcon;
            }
            this.mIsolatedIconForAnimation = (View)mIsolatedIcon2;
        }
        this.mIsolatedIcon = mIsolatedIcon;
        this.updateState();
    }
    
    public class IconState extends ViewState
    {
        public float clampedAppearAmount;
        public int customTransformHeight;
        public float iconAppearAmount;
        public int iconColor;
        public boolean isLastExpandIcon;
        public boolean justAdded;
        private boolean justReplaced;
        private final Consumer<Property> mCannedAnimationEndListener;
        private final View mView;
        public boolean needsCannedAnimation;
        public boolean noAnimations;
        public boolean translateContent;
        public boolean useFullTransitionAmount;
        public boolean useLinearTransitionAmount;
        public int visibleState;
        
        public IconState(final View mView) {
            this.iconAppearAmount = 1.0f;
            this.clampedAppearAmount = 1.0f;
            this.justAdded = true;
            this.iconColor = 0;
            this.customTransformHeight = Integer.MIN_VALUE;
            this.mView = mView;
            this.mCannedAnimationEndListener = (Consumer<Property>)new _$$Lambda$NotificationIconContainer$IconState$017TXxTv7ZZuJUNh_HxPGZnMwVo(this);
        }
        
        @Override
        public void applyToView(final View view) {
            if (view instanceof StatusBarIconView) {
                final StatusBarIconView statusBarIconView = (StatusBarIconView)view;
                AnimationProperties animationProperties = null;
                AnimationProperties animationProperties2 = null;
                final boolean access$100 = NotificationIconContainer.this.areAnimationsEnabled(statusBarIconView);
                final boolean b = true;
                final boolean b2 = access$100 && !NotificationIconContainer.this.mDisallowNextAnimation && !this.noAnimations;
                boolean b4;
                if (b2) {
                    int n = 0;
                    Label_0148: {
                        Label_0145: {
                            if (!this.justAdded && !this.justReplaced) {
                                if (this.visibleState == statusBarIconView.getVisibleState()) {
                                    break Label_0145;
                                }
                                animationProperties2 = NotificationIconContainer.DOT_ANIMATION_PROPERTIES;
                            }
                            else {
                                super.applyToView((View)statusBarIconView);
                                if (!this.justAdded || this.iconAppearAmount == 0.0f) {
                                    break Label_0145;
                                }
                                statusBarIconView.setAlpha(0.0f);
                                statusBarIconView.setVisibleState(2, false);
                                animationProperties2 = NotificationIconContainer.ADD_ICON_PROPERTIES;
                            }
                            n = 1;
                            break Label_0148;
                        }
                        n = 0;
                    }
                    AnimationProperties access$101 = animationProperties2;
                    int n2 = n;
                    Label_0232: {
                        if (n == 0) {
                            access$101 = animationProperties2;
                            n2 = n;
                            if (NotificationIconContainer.this.mAddAnimationStartIndex >= 0) {
                                access$101 = animationProperties2;
                                n2 = n;
                                if (NotificationIconContainer.this.indexOfChild(view) >= NotificationIconContainer.this.mAddAnimationStartIndex) {
                                    if (statusBarIconView.getVisibleState() == 2) {
                                        access$101 = animationProperties2;
                                        n2 = n;
                                        if (this.visibleState == 2) {
                                            break Label_0232;
                                        }
                                    }
                                    access$101 = NotificationIconContainer.DOT_ANIMATION_PROPERTIES;
                                    n2 = 1;
                                }
                            }
                        }
                    }
                    final boolean needsCannedAnimation = this.needsCannedAnimation;
                    long n3 = 100L;
                    AnimationProperties access$102 = access$101;
                    int n4 = n2;
                    if (needsCannedAnimation) {
                        final AnimationFilter animationFilter = NotificationIconContainer.sTempProperties.getAnimationFilter();
                        animationFilter.reset();
                        animationFilter.combineFilter(NotificationIconContainer.ICON_ANIMATION_PROPERTIES.getAnimationFilter());
                        NotificationIconContainer.sTempProperties.resetCustomInterpolators();
                        NotificationIconContainer.sTempProperties.combineCustomInterpolators(NotificationIconContainer.ICON_ANIMATION_PROPERTIES);
                        Interpolator interpolator;
                        if (statusBarIconView.showsConversation()) {
                            interpolator = Interpolators.ICON_OVERSHOT_LESS;
                        }
                        else {
                            interpolator = Interpolators.ICON_OVERSHOT;
                        }
                        NotificationIconContainer.sTempProperties.setCustomInterpolator(View.TRANSLATION_Y, interpolator);
                        NotificationIconContainer.sTempProperties.setAnimationEndAction(this.mCannedAnimationEndListener);
                        if (access$101 != null) {
                            animationFilter.combineFilter(access$101.getAnimationFilter());
                            NotificationIconContainer.sTempProperties.combineCustomInterpolators(access$101);
                        }
                        access$102 = NotificationIconContainer.sTempProperties;
                        access$102.setDuration(100L);
                        final NotificationIconContainer this$0 = NotificationIconContainer.this;
                        this$0.mCannedAnimationStartIndex = this$0.indexOfChild(view);
                        n4 = 1;
                    }
                    animationProperties = access$102;
                    boolean b3 = n4 != 0;
                    Label_0505: {
                        if (n4 == 0) {
                            animationProperties = access$102;
                            b3 = (n4 != 0);
                            if (NotificationIconContainer.this.mCannedAnimationStartIndex >= 0) {
                                animationProperties = access$102;
                                b3 = (n4 != 0);
                                if (NotificationIconContainer.this.indexOfChild(view) > NotificationIconContainer.this.mCannedAnimationStartIndex) {
                                    if (statusBarIconView.getVisibleState() == 2) {
                                        animationProperties = access$102;
                                        b3 = (n4 != 0);
                                        if (this.visibleState == 2) {
                                            break Label_0505;
                                        }
                                    }
                                    final AnimationFilter animationFilter2 = NotificationIconContainer.sTempProperties.getAnimationFilter();
                                    animationFilter2.reset();
                                    animationFilter2.animateX();
                                    NotificationIconContainer.sTempProperties.resetCustomInterpolators();
                                    animationProperties = NotificationIconContainer.sTempProperties;
                                    animationProperties.setDuration(100L);
                                    b3 = true;
                                }
                            }
                        }
                    }
                    b4 = b3;
                    if (NotificationIconContainer.this.mIsolatedIconForAnimation != null) {
                        if (view == NotificationIconContainer.this.mIsolatedIconForAnimation) {
                            animationProperties = NotificationIconContainer.UNISOLATION_PROPERTY;
                            if (NotificationIconContainer.this.mIsolatedIcon == null) {
                                n3 = 0L;
                            }
                            animationProperties.setDelay(n3);
                        }
                        else {
                            animationProperties = NotificationIconContainer.UNISOLATION_PROPERTY_OTHERS;
                            if (NotificationIconContainer.this.mIsolatedIcon != null) {
                                n3 = 0L;
                            }
                            animationProperties.setDelay(n3);
                        }
                        b4 = true;
                    }
                }
                else {
                    b4 = false;
                }
                statusBarIconView.setVisibleState(this.visibleState, b2);
                statusBarIconView.setIconColor(this.iconColor, this.needsCannedAnimation && b2);
                if (b4) {
                    this.animateTo((View)statusBarIconView, animationProperties);
                }
                else {
                    super.applyToView(view);
                }
                statusBarIconView.setIsInShelf(this.iconAppearAmount == 1.0f && b);
            }
            this.justAdded = false;
            this.justReplaced = false;
            this.needsCannedAnimation = false;
        }
        
        public boolean hasCustomTransformHeight() {
            return this.isLastExpandIcon && this.customTransformHeight != Integer.MIN_VALUE;
        }
        
        @Override
        public void initFrom(final View view) {
            super.initFrom(view);
            if (view instanceof StatusBarIconView) {
                this.iconColor = ((StatusBarIconView)view).getStaticDrawableColor();
            }
        }
    }
}
