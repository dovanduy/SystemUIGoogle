// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$MeasureSpec;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public final class QSHeaderInfoLayout extends FrameLayout
{
    private View alarmContainer;
    private final Location location;
    private View ringerContainer;
    private View statusSeparator;
    
    public QSHeaderInfoLayout(final Context context) {
        this(context, null, 0, 0, 14, null);
    }
    
    public QSHeaderInfoLayout(final Context context, final AttributeSet set) {
        this(context, set, 0, 0, 12, null);
    }
    
    public QSHeaderInfoLayout(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0, 8, null);
    }
    
    public QSHeaderInfoLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, set, n, n2);
        this.location = new Location(0, 0);
    }
    
    private final int layoutView(final View view, final int n, final int n2, final int n3, final boolean b) {
        this.location.setLocationFromOffset(n, n3, view.getMeasuredWidth(), b);
        view.layout(this.location.getLeft(), 0, this.location.getRight(), n2);
        return view.getMeasuredWidth();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        final View viewById = this.findViewById(R$id.alarm_container);
        Intrinsics.checkExpressionValueIsNotNull(viewById, "findViewById(R.id.alarm_container)");
        this.alarmContainer = viewById;
        final View viewById2 = this.findViewById(R$id.ringer_container);
        Intrinsics.checkExpressionValueIsNotNull(viewById2, "findViewById(R.id.ringer_container)");
        this.ringerContainer = viewById2;
        final View viewById3 = this.findViewById(R$id.status_separator);
        Intrinsics.checkExpressionValueIsNotNull(viewById3, "findViewById(R.id.status_separator)");
        this.statusSeparator = viewById3;
    }
    
    protected void onLayout(final boolean b, int n, int layoutView, int n2, int n3) {
        final View statusSeparator = this.statusSeparator;
        if (statusSeparator != null) {
            if (statusSeparator.getVisibility() == 8) {
                super.onLayout(b, n, layoutView, n2, n3);
            }
            else {
                final boolean layoutRtl = this.isLayoutRtl();
                n = n2 - n;
                n2 = n3 - layoutView;
                final View alarmContainer = this.alarmContainer;
                if (alarmContainer == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
                    throw null;
                }
                n3 = 0 + this.layoutView(alarmContainer, n, n2, 0, layoutRtl);
                final View statusSeparator2 = this.statusSeparator;
                if (statusSeparator2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
                    throw null;
                }
                layoutView = this.layoutView(statusSeparator2, n, n2, n3, layoutRtl);
                final View ringerContainer = this.ringerContainer;
                if (ringerContainer == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
                    throw null;
                }
                this.layoutView(ringerContainer, n, n2, layoutView + n3, layoutRtl);
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
        throw null;
    }
    
    protected void onMeasure(int measuredWidth, final int n) {
        super.onMeasure(View$MeasureSpec.makeMeasureSpec(View$MeasureSpec.getSize(measuredWidth), Integer.MIN_VALUE), n);
        final int size = View$MeasureSpec.getSize(measuredWidth);
        final View statusSeparator = this.statusSeparator;
        if (statusSeparator != null) {
            if (statusSeparator.getVisibility() != 8) {
                final View alarmContainer = this.alarmContainer;
                if (alarmContainer == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
                    throw null;
                }
                measuredWidth = alarmContainer.getMeasuredWidth();
                final View statusSeparator2 = this.statusSeparator;
                if (statusSeparator2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
                    throw null;
                }
                final int measuredWidth2 = statusSeparator2.getMeasuredWidth();
                final View ringerContainer = this.ringerContainer;
                if (ringerContainer == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
                    throw null;
                }
                final int measuredWidth3 = ringerContainer.getMeasuredWidth();
                final int n2 = View$MeasureSpec.getSize(size) - measuredWidth2;
                final int n3 = n2 / 2;
                if (measuredWidth < n3) {
                    final View ringerContainer2 = this.ringerContainer;
                    if (ringerContainer2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
                        throw null;
                    }
                    this.measureChild(ringerContainer2, View$MeasureSpec.makeMeasureSpec(Math.min(measuredWidth3, n2 - measuredWidth), Integer.MIN_VALUE), n);
                }
                else if (measuredWidth3 < n3) {
                    final View alarmContainer2 = this.alarmContainer;
                    if (alarmContainer2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
                        throw null;
                    }
                    this.measureChild(alarmContainer2, View$MeasureSpec.makeMeasureSpec(Math.min(measuredWidth, n2 - measuredWidth3), Integer.MIN_VALUE), n);
                }
                else {
                    final View alarmContainer3 = this.alarmContainer;
                    if (alarmContainer3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
                        throw null;
                    }
                    this.measureChild(alarmContainer3, View$MeasureSpec.makeMeasureSpec(n3, Integer.MIN_VALUE), n);
                    final View ringerContainer3 = this.ringerContainer;
                    if (ringerContainer3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
                        throw null;
                    }
                    this.measureChild(ringerContainer3, View$MeasureSpec.makeMeasureSpec(n3, Integer.MIN_VALUE), n);
                }
            }
            this.setMeasuredDimension(size, this.getMeasuredHeight());
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("statusSeparator");
        throw null;
    }
    
    private static final class Location
    {
        private int left;
        private int right;
        
        public Location(final int left, final int right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof Location) {
                    final Location location = (Location)o;
                    if (this.left == location.left && this.right == location.right) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public final int getLeft() {
            return this.left;
        }
        
        public final int getRight() {
            return this.right;
        }
        
        @Override
        public int hashCode() {
            return Integer.hashCode(this.left) * 31 + Integer.hashCode(this.right);
        }
        
        public final void setLocationFromOffset(int right, final int left, final int n, final boolean b) {
            if (b) {
                right -= left;
                this.left = right - n;
                this.right = right;
            }
            else {
                this.left = left;
                this.right = left + n;
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Location(left=");
            sb.append(this.left);
            sb.append(", right=");
            sb.append(this.right);
            sb.append(")");
            return sb.toString();
        }
    }
}
