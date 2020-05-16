// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tileimpl;

import android.os.Message;
import android.os.Looper;
import android.os.Handler;
import android.content.res.Resources;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.widget.Switch;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityEvent;
import android.content.res.TypedArray;
import android.view.View$OnLongClickListener;
import android.view.View$OnClickListener;
import android.text.TextUtils;
import com.android.systemui.R$string;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.android.systemui.plugins.qs.QSTile;
import android.animation.ValueAnimator;
import android.util.Log;
import com.android.settingslib.Utils;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.widget.FrameLayout$LayoutParams;
import android.content.res.ColorStateList;
import android.graphics.drawable.shapes.Shape;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.Path;
import android.util.PathParser;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.widget.LinearLayout$LayoutParams;
import com.android.systemui.R$dimen;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.widget.FrameLayout;
import com.android.systemui.plugins.qs.QSIconView;
import android.widget.TextView;
import android.widget.ImageView;
import com.android.systemui.plugins.qs.QSTileView;

public class QSTileBaseView extends QSTileView
{
    private String mAccessibilityClass;
    private final ImageView mBg;
    private int mCircleColor;
    private final int mColorActive;
    private final int mColorDisabled;
    private final TextView mDetailText;
    private final H mHandler;
    protected QSIconView mIcon;
    private final FrameLayout mIconFrame;
    private final int[] mLocInScreen;
    protected RippleDrawable mRipple;
    private boolean mShowRippleEffect;
    private Drawable mTileBackground;
    private boolean mTileState;
    
    public QSTileBaseView(final Context context, final QSIconView mIcon, final boolean b) {
        super(context);
        this.mHandler = new H();
        this.mLocInScreen = new int[2];
        this.mShowRippleEffect = true;
        context.getResources().getDimensionPixelSize(R$dimen.qs_quick_tile_padding);
        this.mIconFrame = new FrameLayout(context);
        final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.qs_quick_tile_size);
        this.addView((View)this.mIconFrame, (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(dimensionPixelSize, dimensionPixelSize));
        this.mBg = new ImageView(this.getContext());
        final ShapeDrawable imageDrawable = new ShapeDrawable((Shape)new PathShape(new Path(PathParser.createPathFromPathData(context.getResources().getString(17039911))), 100.0f, 100.0f));
        imageDrawable.setTintList(ColorStateList.valueOf(0));
        final int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R$dimen.qs_tile_background_size);
        imageDrawable.setIntrinsicHeight(dimensionPixelSize2);
        imageDrawable.setIntrinsicWidth(dimensionPixelSize2);
        this.mBg.setImageDrawable((Drawable)imageDrawable);
        final FrameLayout$LayoutParams layoutParams = new FrameLayout$LayoutParams(dimensionPixelSize2, dimensionPixelSize2, 17);
        this.mIconFrame.addView((View)this.mBg, (ViewGroup$LayoutParams)layoutParams);
        this.mBg.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        this.mIcon = mIcon;
        this.mIconFrame.addView((View)this.mIcon, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-2, -2, 17));
        final TextView mDetailText = (TextView)LayoutInflater.from(context).inflate(R$layout.qs_tile_detail_text, (ViewGroup)this.mIconFrame, false);
        this.mDetailText = mDetailText;
        this.mIconFrame.addView((View)mDetailText);
        this.mIconFrame.setClipChildren(false);
        this.mIconFrame.setClipToPadding(false);
        final Drawable tileBackground = this.newTileBackground();
        this.mTileBackground = tileBackground;
        if (tileBackground instanceof RippleDrawable) {
            this.setRipple((RippleDrawable)tileBackground);
        }
        this.setImportantForAccessibility(1);
        this.setBackground(this.mTileBackground);
        this.mColorActive = Utils.getColorAttrDefaultColor(context, 16843829);
        this.mColorDisabled = Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16843282));
        Utils.getColorAttrDefaultColor(context, 16842808);
        this.setPadding(0, 0, 0, 0);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.setFocusable(true);
    }
    
    private int getCircleColor(final int i) {
        if (i == 0 || i == 1) {
            return this.mColorDisabled;
        }
        if (i != 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid state ");
            sb.append(i);
            Log.e("QSTileBaseView", sb.toString());
            return 0;
        }
        return this.mColorActive;
    }
    
    private void setRipple(final RippleDrawable mRipple) {
        this.mRipple = mRipple;
        if (this.getWidth() != 0) {
            this.updateRippleSize();
        }
    }
    
    private void updateRippleSize() {
        final int n = this.mIconFrame.getMeasuredWidth() / 2 + this.mIconFrame.getLeft();
        final int n2 = this.mIconFrame.getMeasuredHeight() / 2 + this.mIconFrame.getTop();
        final int n3 = (int)(this.mIcon.getHeight() * 0.85f);
        this.mRipple.setHotspotBounds(n - n3, n2 - n3, n + n3, n2 + n3);
    }
    
    protected boolean animationsEnabled() {
        final boolean shown = this.isShown();
        boolean b = false;
        if (!shown) {
            return false;
        }
        if (this.getAlpha() != 1.0f) {
            return false;
        }
        this.getLocationOnScreen(this.mLocInScreen);
        if (this.mLocInScreen[1] >= -this.getHeight()) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int getDetailY() {
        return this.getTop() + this.getHeight() / 2;
    }
    
    @Override
    public QSIconView getIcon() {
        return this.mIcon;
    }
    
    @Override
    public View getIconWithBackground() {
        return (View)this.mIconFrame;
    }
    
    protected void handleStateChanged(final QSTile.State state) {
        final int circleColor = this.getCircleColor(state.state);
        final boolean animationsEnabled = this.animationsEnabled();
        final int mCircleColor = this.mCircleColor;
        boolean clickable = false;
        if (circleColor != mCircleColor) {
            if (animationsEnabled) {
                final ValueAnimator setDuration = ValueAnimator.ofArgb(new int[] { mCircleColor, circleColor }).setDuration(350L);
                setDuration.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$QSTileBaseView$R4RxHhlQ5aUQCBgq0kdDEHJXn14(this));
                setDuration.start();
            }
            else {
                QSIconViewImpl.setTint(this.mBg, circleColor);
            }
            this.mCircleColor = circleColor;
        }
        this.mDetailText.setTextColor(QSTileImpl.getColorForState(this.getContext(), state.state));
        this.mShowRippleEffect = state.showRippleEffect;
        if (state.state != 0) {
            clickable = true;
        }
        this.setClickable(clickable);
        this.setLongClickable(state.handlesLongClick);
        this.mIcon.setIcon(state, animationsEnabled);
        this.setContentDescription(state.contentDescription);
        final StringBuilder sb = new StringBuilder();
        final int state2 = state.state;
        if (state2 != 0) {
            if (state2 != 1) {
                if (state2 == 2) {
                    if (state instanceof QSTile.BooleanState) {
                        sb.append(super.mContext.getString(R$string.switch_bar_on));
                    }
                }
            }
            else if (state instanceof QSTile.BooleanState) {
                sb.append(super.mContext.getString(R$string.switch_bar_off));
            }
        }
        else {
            sb.append(super.mContext.getString(R$string.tile_unavailable));
        }
        if (!TextUtils.isEmpty(state.stateDescription)) {
            sb.append(", ");
            sb.append(state.stateDescription);
        }
        this.setStateDescription((CharSequence)sb.toString());
        String expandedAccessibilityClassName;
        if (state.state == 0) {
            expandedAccessibilityClassName = null;
        }
        else {
            expandedAccessibilityClassName = state.expandedAccessibilityClassName;
        }
        this.mAccessibilityClass = expandedAccessibilityClassName;
        if (state instanceof QSTile.BooleanState) {
            final boolean value = ((QSTile.BooleanState)state).value;
            if (this.mTileState != value) {
                this.mTileState = value;
            }
        }
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public void init(final View$OnClickListener onClickListener, final View$OnClickListener view$OnClickListener, final View$OnLongClickListener onLongClickListener) {
        this.setOnClickListener(onClickListener);
        this.setOnLongClickListener(onLongClickListener);
    }
    
    @Override
    public void init(final QSTile qsTile) {
        this.init((View$OnClickListener)new _$$Lambda$QSTileBaseView$aVxKNvlJE7IFS8nVmOyLdAcByFA(qsTile), (View$OnClickListener)new _$$Lambda$QSTileBaseView$W9w1scJAVZm5V6Q1VB4ZO5o3C8A(qsTile), (View$OnLongClickListener)new _$$Lambda$QSTileBaseView$STEfvGmwtIL_pMrVYwBQuK3x1jo(qsTile));
        if (qsTile.supportsDetailView()) {
            this.mDetailText.setVisibility(0);
        }
    }
    
    protected Drawable newTileBackground() {
        final TypedArray obtainStyledAttributes = this.getContext().obtainStyledAttributes(new int[] { 16843868 });
        final Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (!TextUtils.isEmpty((CharSequence)this.mAccessibilityClass)) {
            accessibilityEvent.setClassName((CharSequence)this.mAccessibilityClass);
        }
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setSelected(false);
        if (!TextUtils.isEmpty((CharSequence)this.mAccessibilityClass)) {
            accessibilityNodeInfo.setClassName((CharSequence)this.mAccessibilityClass);
            if (Switch.class.getName().equals(this.mAccessibilityClass)) {
                final Resources resources = this.getResources();
                int n;
                if (this.mTileState) {
                    n = R$string.switch_bar_on;
                }
                else {
                    n = R$string.switch_bar_off;
                }
                accessibilityNodeInfo.setText((CharSequence)resources.getString(n));
                accessibilityNodeInfo.setChecked(this.mTileState);
                accessibilityNodeInfo.setCheckable(true);
                if (this.isLongClickable()) {
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_LONG_CLICK.getId(), (CharSequence)this.getResources().getString(R$string.accessibility_long_click_tile)));
                }
            }
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        if (this.mRipple != null) {
            this.updateRippleSize();
        }
    }
    
    @Override
    public void onStateChanged(final QSTile.State state) {
        this.mHandler.obtainMessage(1, (Object)state).sendToTarget();
    }
    
    public void setClickable(final boolean clickable) {
        super.setClickable(clickable);
        Object mRipple;
        if (clickable && this.mShowRippleEffect) {
            mRipple = this.mRipple;
        }
        else {
            mRipple = null;
        }
        this.setBackground((Drawable)mRipple);
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append('[');
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("locInScreen=(");
        sb2.append(this.mLocInScreen[0]);
        sb2.append(", ");
        sb2.append(this.mLocInScreen[1]);
        sb2.append(")");
        sb.append(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(", iconView=");
        sb3.append(this.mIcon.toString());
        sb.append(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(", tileState=");
        sb4.append(this.mTileState);
        sb.append(sb4.toString());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public View updateAccessibilityOrder(final View view) {
        this.setAccessibilityTraversalAfter(view.getId());
        return (View)this;
    }
    
    private class H extends Handler
    {
        public H() {
            super(Looper.getMainLooper());
        }
        
        public void handleMessage(final Message message) {
            if (message.what == 1) {
                QSTileBaseView.this.handleStateChanged((QSTile.State)message.obj);
            }
        }
    }
}
