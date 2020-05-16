// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.widget;

import android.content.res.TypedArray;
import android.util.SparseIntArray;
import android.annotation.TargetApi;
import android.util.Log;
import android.view.ViewGroup$MarginLayoutParams;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.os.Build$VERSION;
import android.view.ViewGroup$LayoutParams;
import android.view.View$MeasureSpec;
import androidx.constraintlayout.solver.widgets.ResolutionAnchor;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import android.content.res.Resources$NotFoundException;
import android.support.constraint.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import java.util.HashMap;
import java.util.ArrayList;
import android.view.View;
import android.util.SparseArray;
import android.view.ViewGroup;

public class ConstraintLayout extends ViewGroup
{
    SparseArray<View> mChildrenByIds;
    private ArrayList<ConstraintHelper> mConstraintHelpers;
    private ConstraintSet mConstraintSet;
    private int mConstraintSetId;
    private HashMap<String, Integer> mDesignIds;
    private boolean mDirtyHierarchy;
    private int mLastMeasureHeight;
    int mLastMeasureHeightMode;
    int mLastMeasureHeightSize;
    private int mLastMeasureWidth;
    int mLastMeasureWidthMode;
    int mLastMeasureWidthSize;
    ConstraintWidgetContainer mLayoutWidget;
    private int mMaxHeight;
    private int mMaxWidth;
    private Metrics mMetrics;
    private int mMinHeight;
    private int mMinWidth;
    private int mOptimizationLevel;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets;
    
    public ConstraintLayout(final Context context) {
        super(context);
        this.mChildrenByIds = (SparseArray<View>)new SparseArray();
        this.mConstraintHelpers = new ArrayList<ConstraintHelper>(4);
        this.mVariableDimensionsWidgets = new ArrayList<ConstraintWidget>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 3;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<String, Integer>();
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
        this.init(null);
    }
    
    public ConstraintLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mChildrenByIds = (SparseArray<View>)new SparseArray();
        this.mConstraintHelpers = new ArrayList<ConstraintHelper>(4);
        this.mVariableDimensionsWidgets = new ArrayList<ConstraintWidget>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 3;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<String, Integer>();
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
        this.init(set);
    }
    
    public ConstraintLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mChildrenByIds = (SparseArray<View>)new SparseArray();
        this.mConstraintHelpers = new ArrayList<ConstraintHelper>(4);
        this.mVariableDimensionsWidgets = new ArrayList<ConstraintWidget>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 3;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<String, Integer>();
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
        this.init(set);
    }
    
    private final ConstraintWidget getTargetWidget(final int n) {
        if (n == 0) {
            return this.mLayoutWidget;
        }
        final View view = (View)this.mChildrenByIds.get(n);
        if (view == this) {
            return this.mLayoutWidget;
        }
        ConstraintWidget widget;
        if (view == null) {
            widget = null;
        }
        else {
            widget = ((LayoutParams)view.getLayoutParams()).widget;
        }
        return widget;
    }
    
    private void init(AttributeSet obtainStyledAttributes) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mChildrenByIds.put(this.getId(), (Object)this);
        this.mConstraintSet = null;
        if (obtainStyledAttributes != null) {
            obtainStyledAttributes = (AttributeSet)this.getContext().obtainStyledAttributes(obtainStyledAttributes, R$styleable.ConstraintLayout_Layout);
            for (int indexCount = ((TypedArray)obtainStyledAttributes).getIndexCount(), i = 0; i < indexCount; ++i) {
                final int index = ((TypedArray)obtainStyledAttributes).getIndex(i);
                if (index == R$styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.mMinWidth);
                }
                else if (index == R$styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.mMinHeight);
                }
                else if (index == R$styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.mMaxWidth);
                }
                else if (index == R$styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.mMaxHeight);
                }
                else if (index == R$styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = ((TypedArray)obtainStyledAttributes).getInt(index, this.mOptimizationLevel);
                }
                else if (index == R$styleable.ConstraintLayout_Layout_constraintSet) {
                    final int resourceId = ((TypedArray)obtainStyledAttributes).getResourceId(index, 0);
                    try {
                        (this.mConstraintSet = new ConstraintSet()).load(this.getContext(), resourceId);
                    }
                    catch (Resources$NotFoundException ex) {
                        this.mConstraintSet = null;
                    }
                    this.mConstraintSetId = resourceId;
                }
            }
            ((TypedArray)obtainStyledAttributes).recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }
    
    private void internalMeasureChildren(final int n, final int n2) {
        final int n3 = this.getPaddingTop() + this.getPaddingBottom();
        final int n4 = this.getPaddingLeft() + this.getPaddingRight();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                final ConstraintWidget widget = layoutParams.widget;
                if (!layoutParams.isGuideline) {
                    if (!layoutParams.isHelper) {
                        widget.setVisibility(child.getVisibility());
                        final int width = layoutParams.width;
                        final int height = layoutParams.height;
                        final boolean horizontalDimensionFixed = layoutParams.horizontalDimensionFixed;
                        boolean b = false;
                        Label_0199: {
                            Label_0196: {
                                if (!horizontalDimensionFixed && !layoutParams.verticalDimensionFixed && (horizontalDimensionFixed || layoutParams.matchConstraintDefaultWidth != 1) && layoutParams.width != -1) {
                                    if (!layoutParams.verticalDimensionFixed) {
                                        if (layoutParams.matchConstraintDefaultHeight == 1) {
                                            break Label_0196;
                                        }
                                        if (layoutParams.height == -1) {
                                            break Label_0196;
                                        }
                                    }
                                    b = false;
                                    break Label_0199;
                                }
                            }
                            b = true;
                        }
                        boolean b2;
                        boolean b3;
                        int measuredWidth;
                        int measuredHeight;
                        if (b) {
                            int n5;
                            if (width == 0) {
                                n5 = ViewGroup.getChildMeasureSpec(n, n4, -2);
                                b2 = true;
                            }
                            else if (width == -1) {
                                n5 = ViewGroup.getChildMeasureSpec(n, n4, -1);
                                b2 = false;
                            }
                            else {
                                b2 = (width == -2);
                                n5 = ViewGroup.getChildMeasureSpec(n, n4, width);
                            }
                            int n6;
                            if (height == 0) {
                                n6 = ViewGroup.getChildMeasureSpec(n2, n3, -2);
                                b3 = true;
                            }
                            else if (height == -1) {
                                n6 = ViewGroup.getChildMeasureSpec(n2, n3, -1);
                                b3 = false;
                            }
                            else {
                                b3 = (height == -2);
                                n6 = ViewGroup.getChildMeasureSpec(n2, n3, height);
                            }
                            child.measure(n5, n6);
                            final Metrics mMetrics = this.mMetrics;
                            if (mMetrics != null) {
                                ++mMetrics.measures;
                            }
                            widget.setWidthWrapContent(width == -2);
                            widget.setHeightWrapContent(height == -2);
                            measuredWidth = child.getMeasuredWidth();
                            measuredHeight = child.getMeasuredHeight();
                        }
                        else {
                            b2 = false;
                            b3 = false;
                            measuredHeight = height;
                            measuredWidth = width;
                        }
                        widget.setWidth(measuredWidth);
                        widget.setHeight(measuredHeight);
                        if (b2) {
                            widget.setWrapWidth(measuredWidth);
                        }
                        if (b3) {
                            widget.setWrapHeight(measuredHeight);
                        }
                        if (layoutParams.needsBaseline) {
                            final int baseline = child.getBaseline();
                            if (baseline != -1) {
                                widget.setBaselineDistance(baseline);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void internalMeasureDimensions(final int n, final int n2) {
        ConstraintLayout constraintLayout = this;
        final int n3 = this.getPaddingTop() + this.getPaddingBottom();
        final int n4 = this.getPaddingLeft() + this.getPaddingRight();
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = constraintLayout.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                final ConstraintWidget widget = layoutParams.widget;
                if (!layoutParams.isGuideline) {
                    if (!layoutParams.isHelper) {
                        widget.setVisibility(child.getVisibility());
                        final int width = layoutParams.width;
                        final int height = layoutParams.height;
                        if (width != 0 && height != 0) {
                            final boolean b = width == -2;
                            final int childMeasureSpec = ViewGroup.getChildMeasureSpec(n, n4, width);
                            final boolean b2 = height == -2;
                            child.measure(childMeasureSpec, ViewGroup.getChildMeasureSpec(n2, n3, height));
                            final Metrics mMetrics = constraintLayout.mMetrics;
                            if (mMetrics != null) {
                                ++mMetrics.measures;
                            }
                            widget.setWidthWrapContent(width == -2);
                            widget.setHeightWrapContent(height == -2);
                            final int measuredWidth = child.getMeasuredWidth();
                            final int measuredHeight = child.getMeasuredHeight();
                            widget.setWidth(measuredWidth);
                            widget.setHeight(measuredHeight);
                            if (b) {
                                widget.setWrapWidth(measuredWidth);
                            }
                            if (b2) {
                                widget.setWrapHeight(measuredHeight);
                            }
                            if (layoutParams.needsBaseline) {
                                final int baseline = child.getBaseline();
                                if (baseline != -1) {
                                    widget.setBaselineDistance(baseline);
                                }
                            }
                            if (layoutParams.horizontalDimensionFixed && layoutParams.verticalDimensionFixed) {
                                widget.getResolutionWidth().resolve(measuredWidth);
                                widget.getResolutionHeight().resolve(measuredHeight);
                            }
                        }
                        else {
                            widget.getResolutionWidth().invalidate();
                            widget.getResolutionHeight().invalidate();
                        }
                    }
                }
            }
        }
        constraintLayout.mLayoutWidget.solveGraph();
        for (int j = 0; j < childCount; ++j) {
            final View child2 = constraintLayout.getChildAt(j);
            if (child2.getVisibility() != 8) {
                final LayoutParams layoutParams2 = (LayoutParams)child2.getLayoutParams();
                final ConstraintWidget widget2 = layoutParams2.widget;
                if (!layoutParams2.isGuideline) {
                    if (!layoutParams2.isHelper) {
                        widget2.setVisibility(child2.getVisibility());
                        int width2 = layoutParams2.width;
                        int height2 = layoutParams2.height;
                        if (width2 == 0 || height2 == 0) {
                            final ResolutionAnchor resolutionNode = widget2.getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
                            final ResolutionAnchor resolutionNode2 = widget2.getAnchor(ConstraintAnchor.Type.RIGHT).getResolutionNode();
                            final boolean b3 = widget2.getAnchor(ConstraintAnchor.Type.LEFT).getTarget() != null && widget2.getAnchor(ConstraintAnchor.Type.RIGHT).getTarget() != null;
                            final ResolutionAnchor resolutionNode3 = widget2.getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
                            final ResolutionAnchor resolutionNode4 = widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).getResolutionNode();
                            final boolean b4 = widget2.getAnchor(ConstraintAnchor.Type.TOP).getTarget() != null && widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).getTarget() != null;
                            if (width2 != 0 || height2 != 0 || !b3 || !b4) {
                                final boolean b5 = constraintLayout.mLayoutWidget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                int n5;
                                if (constraintLayout.mLayoutWidget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                    n5 = 1;
                                }
                                else {
                                    n5 = 0;
                                }
                                if (!b5) {
                                    widget2.getResolutionWidth().invalidate();
                                }
                                if (n5 == 0) {
                                    widget2.getResolutionHeight().invalidate();
                                }
                                int n6 = 0;
                                boolean b6 = false;
                                boolean b7 = false;
                                int n7 = 0;
                                Label_0902: {
                                    if (width2 == 0) {
                                        if (!b5 || !widget2.isSpreadWidth() || !b3 || !resolutionNode.isResolved() || !resolutionNode2.isResolved()) {
                                            n6 = ViewGroup.getChildMeasureSpec(n, n4, -2);
                                            b6 = false;
                                            b7 = true;
                                            n7 = width2;
                                            break Label_0902;
                                        }
                                        width2 = (int)(resolutionNode2.getResolvedValue() - resolutionNode.getResolvedValue());
                                        widget2.getResolutionWidth().resolve(width2);
                                        n6 = ViewGroup.getChildMeasureSpec(n, n4, width2);
                                    }
                                    else {
                                        if (width2 != -1) {
                                            b7 = (width2 == -2);
                                            n6 = ViewGroup.getChildMeasureSpec(n, n4, width2);
                                            n7 = width2;
                                            b6 = b5;
                                            break Label_0902;
                                        }
                                        n6 = ViewGroup.getChildMeasureSpec(n, n4, -1);
                                    }
                                    b7 = false;
                                    b6 = b5;
                                    n7 = width2;
                                }
                                int n8 = 0;
                                boolean b8 = false;
                                Label_1044: {
                                    if (height2 == 0) {
                                        if (n5 == 0 || !widget2.isSpreadHeight() || !b4 || !resolutionNode3.isResolved() || !resolutionNode4.isResolved()) {
                                            n8 = ViewGroup.getChildMeasureSpec(n2, n3, -2);
                                            n5 = 0;
                                            b8 = true;
                                            break Label_1044;
                                        }
                                        height2 = (int)(resolutionNode4.getResolvedValue() - resolutionNode3.getResolvedValue());
                                        widget2.getResolutionHeight().resolve(height2);
                                        n8 = ViewGroup.getChildMeasureSpec(n2, n3, height2);
                                    }
                                    else {
                                        if (height2 != -1) {
                                            b8 = (height2 == -2);
                                            n8 = ViewGroup.getChildMeasureSpec(n2, n3, height2);
                                            break Label_1044;
                                        }
                                        n8 = ViewGroup.getChildMeasureSpec(n2, n3, -1);
                                    }
                                    b8 = false;
                                }
                                child2.measure(n6, n8);
                                final Metrics mMetrics2 = this.mMetrics;
                                if (mMetrics2 != null) {
                                    ++mMetrics2.measures;
                                }
                                widget2.setWidthWrapContent(n7 == -2);
                                widget2.setHeightWrapContent(height2 == -2);
                                final int measuredWidth2 = child2.getMeasuredWidth();
                                final int measuredHeight2 = child2.getMeasuredHeight();
                                widget2.setWidth(measuredWidth2);
                                widget2.setHeight(measuredHeight2);
                                if (b7) {
                                    widget2.setWrapWidth(measuredWidth2);
                                }
                                if (b8) {
                                    widget2.setWrapHeight(measuredHeight2);
                                }
                                if (b6) {
                                    widget2.getResolutionWidth().resolve(measuredWidth2);
                                }
                                else {
                                    widget2.getResolutionWidth().remove();
                                }
                                if (n5 != 0) {
                                    widget2.getResolutionHeight().resolve(measuredHeight2);
                                }
                                else {
                                    widget2.getResolutionHeight().remove();
                                }
                                if (layoutParams2.needsBaseline) {
                                    final int baseline2 = child2.getBaseline();
                                    constraintLayout = this;
                                    if (baseline2 != -1) {
                                        widget2.setBaselineDistance(baseline2);
                                        constraintLayout = this;
                                    }
                                }
                                else {
                                    constraintLayout = this;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void setChildrenConstraints() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: istore_1       
        //     4: aload_0        
        //     5: invokevirtual   android/view/ViewGroup.isInEditMode:()Z
        //     8: istore_2       
        //     9: aload_0        
        //    10: invokevirtual   android/view/ViewGroup.getChildCount:()I
        //    13: istore_3       
        //    14: iconst_0       
        //    15: istore          4
        //    17: iload_2        
        //    18: ifeq            117
        //    21: iconst_0       
        //    22: istore          5
        //    24: iload           5
        //    26: iload_3        
        //    27: if_icmpge       117
        //    30: aload_0        
        //    31: iload           5
        //    33: invokevirtual   android/view/ViewGroup.getChildAt:(I)Landroid/view/View;
        //    36: astore          6
        //    38: aload_0        
        //    39: invokevirtual   android/view/ViewGroup.getResources:()Landroid/content/res/Resources;
        //    42: aload           6
        //    44: invokevirtual   android/view/View.getId:()I
        //    47: invokevirtual   android/content/res/Resources.getResourceName:(I)Ljava/lang/String;
        //    50: astore          7
        //    52: aload_0        
        //    53: iconst_0       
        //    54: aload           7
        //    56: aload           6
        //    58: invokevirtual   android/view/View.getId:()I
        //    61: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    64: invokevirtual   androidx/constraintlayout/widget/ConstraintLayout.setDesignInformation:(ILjava/lang/Object;Ljava/lang/Object;)V
        //    67: aload           7
        //    69: bipush          47
        //    71: invokevirtual   java/lang/String.indexOf:(I)I
        //    74: istore          8
        //    76: aload           7
        //    78: astore          9
        //    80: iload           8
        //    82: iconst_m1      
        //    83: if_icmpeq       97
        //    86: aload           7
        //    88: iload           8
        //    90: iconst_1       
        //    91: iadd           
        //    92: invokevirtual   java/lang/String.substring:(I)Ljava/lang/String;
        //    95: astore          9
        //    97: aload_0        
        //    98: aload           6
        //   100: invokevirtual   android/view/View.getId:()I
        //   103: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //   106: aload           9
        //   108: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setDebugName:(Ljava/lang/String;)V
        //   111: iinc            5, 1
        //   114: goto            24
        //   117: iconst_0       
        //   118: istore          5
        //   120: iload           5
        //   122: iload_3        
        //   123: if_icmpge       157
        //   126: aload_0        
        //   127: aload_0        
        //   128: iload           5
        //   130: invokevirtual   android/view/ViewGroup.getChildAt:(I)Landroid/view/View;
        //   133: invokevirtual   androidx/constraintlayout/widget/ConstraintLayout.getViewWidget:(Landroid/view/View;)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //   136: astore          9
        //   138: aload           9
        //   140: ifnonnull       146
        //   143: goto            151
        //   146: aload           9
        //   148: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.reset:()V
        //   151: iinc            5, 1
        //   154: goto            120
        //   157: aload_0        
        //   158: getfield        androidx/constraintlayout/widget/ConstraintLayout.mConstraintSetId:I
        //   161: iconst_m1      
        //   162: if_icmpeq       220
        //   165: iconst_0       
        //   166: istore          5
        //   168: iload           5
        //   170: iload_3        
        //   171: if_icmpge       220
        //   174: aload_0        
        //   175: iload           5
        //   177: invokevirtual   android/view/ViewGroup.getChildAt:(I)Landroid/view/View;
        //   180: astore          9
        //   182: aload           9
        //   184: invokevirtual   android/view/View.getId:()I
        //   187: aload_0        
        //   188: getfield        androidx/constraintlayout/widget/ConstraintLayout.mConstraintSetId:I
        //   191: if_icmpne       214
        //   194: aload           9
        //   196: instanceof      Landroidx/constraintlayout/widget/Constraints;
        //   199: ifeq            214
        //   202: aload_0        
        //   203: aload           9
        //   205: checkcast       Landroidx/constraintlayout/widget/Constraints;
        //   208: invokevirtual   androidx/constraintlayout/widget/Constraints.getConstraintSet:()Landroidx/constraintlayout/widget/ConstraintSet;
        //   211: putfield        androidx/constraintlayout/widget/ConstraintLayout.mConstraintSet:Landroidx/constraintlayout/widget/ConstraintSet;
        //   214: iinc            5, 1
        //   217: goto            168
        //   220: aload_0        
        //   221: getfield        androidx/constraintlayout/widget/ConstraintLayout.mConstraintSet:Landroidx/constraintlayout/widget/ConstraintSet;
        //   224: astore          9
        //   226: aload           9
        //   228: ifnull          237
        //   231: aload           9
        //   233: aload_0        
        //   234: invokevirtual   androidx/constraintlayout/widget/ConstraintSet.applyToInternal:(Landroidx/constraintlayout/widget/ConstraintLayout;)V
        //   237: aload_0        
        //   238: getfield        androidx/constraintlayout/widget/ConstraintLayout.mLayoutWidget:Landroidx/constraintlayout/solver/widgets/ConstraintWidgetContainer;
        //   241: invokevirtual   androidx/constraintlayout/solver/widgets/WidgetContainer.removeAllChildren:()V
        //   244: aload_0        
        //   245: getfield        androidx/constraintlayout/widget/ConstraintLayout.mConstraintHelpers:Ljava/util/ArrayList;
        //   248: invokevirtual   java/util/ArrayList.size:()I
        //   251: istore          8
        //   253: iload           8
        //   255: ifle            290
        //   258: iconst_0       
        //   259: istore          5
        //   261: iload           5
        //   263: iload           8
        //   265: if_icmpge       290
        //   268: aload_0        
        //   269: getfield        androidx/constraintlayout/widget/ConstraintLayout.mConstraintHelpers:Ljava/util/ArrayList;
        //   272: iload           5
        //   274: invokevirtual   java/util/ArrayList.get:(I)Ljava/lang/Object;
        //   277: checkcast       Landroidx/constraintlayout/widget/ConstraintHelper;
        //   280: aload_0        
        //   281: invokevirtual   androidx/constraintlayout/widget/ConstraintHelper.updatePreLayout:(Landroidx/constraintlayout/widget/ConstraintLayout;)V
        //   284: iinc            5, 1
        //   287: goto            261
        //   290: iconst_0       
        //   291: istore          5
        //   293: iload           5
        //   295: iload_3        
        //   296: if_icmpge       330
        //   299: aload_0        
        //   300: iload           5
        //   302: invokevirtual   android/view/ViewGroup.getChildAt:(I)Landroid/view/View;
        //   305: astore          9
        //   307: aload           9
        //   309: instanceof      Landroidx/constraintlayout/widget/Placeholder;
        //   312: ifeq            324
        //   315: aload           9
        //   317: checkcast       Landroidx/constraintlayout/widget/Placeholder;
        //   320: aload_0        
        //   321: invokevirtual   androidx/constraintlayout/widget/Placeholder.updatePreLayout:(Landroidx/constraintlayout/widget/ConstraintLayout;)V
        //   324: iinc            5, 1
        //   327: goto            293
        //   330: iconst_0       
        //   331: istore          10
        //   333: iload           4
        //   335: istore          5
        //   337: iload           10
        //   339: iload_3        
        //   340: if_icmpge       2060
        //   343: aload_0        
        //   344: iload           10
        //   346: invokevirtual   android/view/ViewGroup.getChildAt:(I)Landroid/view/View;
        //   349: astore          6
        //   351: aload_0        
        //   352: aload           6
        //   354: invokevirtual   androidx/constraintlayout/widget/ConstraintLayout.getViewWidget:(Landroid/view/View;)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //   357: astore          7
        //   359: aload           7
        //   361: ifnonnull       371
        //   364: iload           5
        //   366: istore          8
        //   368: goto            2050
        //   371: aload           6
        //   373: invokevirtual   android/view/View.getLayoutParams:()Landroid/view/ViewGroup$LayoutParams;
        //   376: checkcast       Landroidx/constraintlayout/widget/ConstraintLayout$LayoutParams;
        //   379: astore          9
        //   381: aload           9
        //   383: invokevirtual   androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.validate:()V
        //   386: aload           9
        //   388: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.helped:Z
        //   391: ifeq            404
        //   394: aload           9
        //   396: iload           5
        //   398: putfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.helped:Z
        //   401: goto            469
        //   404: iload_2        
        //   405: ifeq            469
        //   408: aload_0        
        //   409: invokevirtual   android/view/ViewGroup.getResources:()Landroid/content/res/Resources;
        //   412: aload           6
        //   414: invokevirtual   android/view/View.getId:()I
        //   417: invokevirtual   android/content/res/Resources.getResourceName:(I)Ljava/lang/String;
        //   420: astore          11
        //   422: aload_0        
        //   423: iload           5
        //   425: aload           11
        //   427: aload           6
        //   429: invokevirtual   android/view/View.getId:()I
        //   432: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   435: invokevirtual   androidx/constraintlayout/widget/ConstraintLayout.setDesignInformation:(ILjava/lang/Object;Ljava/lang/Object;)V
        //   438: aload           11
        //   440: aload           11
        //   442: ldc_w           "id/"
        //   445: invokevirtual   java/lang/String.indexOf:(Ljava/lang/String;)I
        //   448: iconst_3       
        //   449: iadd           
        //   450: invokevirtual   java/lang/String.substring:(I)Ljava/lang/String;
        //   453: astore          11
        //   455: aload_0        
        //   456: aload           6
        //   458: invokevirtual   android/view/View.getId:()I
        //   461: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //   464: aload           11
        //   466: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setDebugName:(Ljava/lang/String;)V
        //   469: aload           7
        //   471: aload           6
        //   473: invokevirtual   android/view/View.getVisibility:()I
        //   476: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVisibility:(I)V
        //   479: aload           9
        //   481: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.isInPlaceholder:Z
        //   484: ifeq            494
        //   487: aload           7
        //   489: bipush          8
        //   491: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVisibility:(I)V
        //   494: aload           7
        //   496: aload           6
        //   498: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setCompanionWidget:(Ljava/lang/Object;)V
        //   501: aload_0        
        //   502: getfield        androidx/constraintlayout/widget/ConstraintLayout.mLayoutWidget:Landroidx/constraintlayout/solver/widgets/ConstraintWidgetContainer;
        //   505: aload           7
        //   507: invokevirtual   androidx/constraintlayout/solver/widgets/WidgetContainer.add:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget;)V
        //   510: aload           9
        //   512: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.verticalDimensionFixed:Z
        //   515: ifeq            526
        //   518: aload           9
        //   520: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.horizontalDimensionFixed:Z
        //   523: ifne            536
        //   526: aload_0        
        //   527: getfield        androidx/constraintlayout/widget/ConstraintLayout.mVariableDimensionsWidgets:Ljava/util/ArrayList;
        //   530: aload           7
        //   532: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   535: pop            
        //   536: aload           9
        //   538: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.isGuideline:Z
        //   541: ifeq            666
        //   544: aload           7
        //   546: checkcast       Landroidx/constraintlayout/solver/widgets/Guideline;
        //   549: astore          7
        //   551: aload           9
        //   553: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedGuideBegin:I
        //   556: istore          8
        //   558: aload           9
        //   560: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedGuideEnd:I
        //   563: istore          4
        //   565: aload           9
        //   567: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedGuidePercent:F
        //   570: fstore          12
        //   572: iload_1        
        //   573: bipush          17
        //   575: if_icmpge       599
        //   578: aload           9
        //   580: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.guideBegin:I
        //   583: istore          8
        //   585: aload           9
        //   587: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.guideEnd:I
        //   590: istore          4
        //   592: aload           9
        //   594: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.guidePercent:F
        //   597: fstore          12
        //   599: fload           12
        //   601: ldc_w           -1.0
        //   604: fcmpl          
        //   605: ifeq            622
        //   608: aload           7
        //   610: fload           12
        //   612: invokevirtual   androidx/constraintlayout/solver/widgets/Guideline.setGuidePercent:(F)V
        //   615: iload           5
        //   617: istore          8
        //   619: goto            2050
        //   622: iload           8
        //   624: iconst_m1      
        //   625: if_icmpeq       642
        //   628: aload           7
        //   630: iload           8
        //   632: invokevirtual   androidx/constraintlayout/solver/widgets/Guideline.setGuideBegin:(I)V
        //   635: iload           5
        //   637: istore          8
        //   639: goto            2050
        //   642: iload           5
        //   644: istore          8
        //   646: iload           4
        //   648: iconst_m1      
        //   649: if_icmpeq       2050
        //   652: aload           7
        //   654: iload           4
        //   656: invokevirtual   androidx/constraintlayout/solver/widgets/Guideline.setGuideEnd:(I)V
        //   659: iload           5
        //   661: istore          8
        //   663: goto            2050
        //   666: aload           9
        //   668: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.leftToLeft:I
        //   671: iconst_m1      
        //   672: if_icmpne       832
        //   675: aload           9
        //   677: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.leftToRight:I
        //   680: iconst_m1      
        //   681: if_icmpne       832
        //   684: aload           9
        //   686: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.rightToLeft:I
        //   689: iconst_m1      
        //   690: if_icmpne       832
        //   693: aload           9
        //   695: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.rightToRight:I
        //   698: iconst_m1      
        //   699: if_icmpne       832
        //   702: aload           9
        //   704: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.startToStart:I
        //   707: iconst_m1      
        //   708: if_icmpne       832
        //   711: aload           9
        //   713: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.startToEnd:I
        //   716: iconst_m1      
        //   717: if_icmpne       832
        //   720: aload           9
        //   722: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.endToStart:I
        //   725: iconst_m1      
        //   726: if_icmpne       832
        //   729: aload           9
        //   731: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.endToEnd:I
        //   734: iconst_m1      
        //   735: if_icmpne       832
        //   738: aload           9
        //   740: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.topToTop:I
        //   743: iconst_m1      
        //   744: if_icmpne       832
        //   747: aload           9
        //   749: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.topToBottom:I
        //   752: iconst_m1      
        //   753: if_icmpne       832
        //   756: aload           9
        //   758: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.bottomToTop:I
        //   761: iconst_m1      
        //   762: if_icmpne       832
        //   765: aload           9
        //   767: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.bottomToBottom:I
        //   770: iconst_m1      
        //   771: if_icmpne       832
        //   774: aload           9
        //   776: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.baselineToBaseline:I
        //   779: iconst_m1      
        //   780: if_icmpne       832
        //   783: aload           9
        //   785: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.editorAbsoluteX:I
        //   788: iconst_m1      
        //   789: if_icmpne       832
        //   792: aload           9
        //   794: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.editorAbsoluteY:I
        //   797: iconst_m1      
        //   798: if_icmpne       832
        //   801: aload           9
        //   803: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.circleConstraint:I
        //   806: iconst_m1      
        //   807: if_icmpne       832
        //   810: aload           9
        //   812: getfield        android/view/ViewGroup$MarginLayoutParams.width:I
        //   815: iconst_m1      
        //   816: if_icmpeq       832
        //   819: iload           5
        //   821: istore          8
        //   823: aload           9
        //   825: getfield        android/view/ViewGroup$MarginLayoutParams.height:I
        //   828: iconst_m1      
        //   829: if_icmpne       2050
        //   832: aload           9
        //   834: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedLeftToLeft:I
        //   837: istore          5
        //   839: aload           9
        //   841: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedLeftToRight:I
        //   844: istore          4
        //   846: aload           9
        //   848: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedRightToLeft:I
        //   851: istore          13
        //   853: aload           9
        //   855: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedRightToRight:I
        //   858: istore          8
        //   860: aload           9
        //   862: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolveGoneLeftMargin:I
        //   865: istore          14
        //   867: aload           9
        //   869: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolveGoneRightMargin:I
        //   872: istore          15
        //   874: aload           9
        //   876: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.resolvedHorizontalBias:F
        //   879: fstore          12
        //   881: iload_1        
        //   882: bipush          17
        //   884: if_icmpge       1091
        //   887: aload           9
        //   889: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.leftToLeft:I
        //   892: istore          5
        //   894: aload           9
        //   896: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.leftToRight:I
        //   899: istore          4
        //   901: aload           9
        //   903: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.rightToLeft:I
        //   906: istore          13
        //   908: aload           9
        //   910: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.rightToRight:I
        //   913: istore          15
        //   915: aload           9
        //   917: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.goneLeftMargin:I
        //   920: istore          14
        //   922: aload           9
        //   924: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.goneRightMargin:I
        //   927: istore          8
        //   929: aload           9
        //   931: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.horizontalBias:F
        //   934: fstore          12
        //   936: iload           5
        //   938: iconst_m1      
        //   939: if_icmpne       988
        //   942: iload           4
        //   944: iconst_m1      
        //   945: if_icmpne       988
        //   948: aload           9
        //   950: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.startToStart:I
        //   953: istore          16
        //   955: iload           16
        //   957: iconst_m1      
        //   958: if_icmpeq       968
        //   961: iload           16
        //   963: istore          5
        //   965: goto            988
        //   968: aload           9
        //   970: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.startToEnd:I
        //   973: istore          16
        //   975: iload           16
        //   977: iconst_m1      
        //   978: if_icmpeq       988
        //   981: iload           16
        //   983: istore          4
        //   985: goto            988
        //   988: iload           13
        //   990: iconst_m1      
        //   991: if_icmpne       1076
        //   994: iload           15
        //   996: iconst_m1      
        //   997: if_icmpne       1076
        //  1000: aload           9
        //  1002: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.endToStart:I
        //  1005: istore          17
        //  1007: iload           17
        //  1009: iconst_m1      
        //  1010: if_icmpeq       1036
        //  1013: iload           14
        //  1015: istore          13
        //  1017: iload           8
        //  1019: istore          16
        //  1021: iload           17
        //  1023: istore          14
        //  1025: iload           15
        //  1027: istore          8
        //  1029: iload           16
        //  1031: istore          15
        //  1033: goto            1103
        //  1036: aload           9
        //  1038: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.endToEnd:I
        //  1041: istore          17
        //  1043: iload           17
        //  1045: iconst_m1      
        //  1046: if_icmpeq       1076
        //  1049: iload           14
        //  1051: istore          15
        //  1053: iload           8
        //  1055: istore          16
        //  1057: iload           13
        //  1059: istore          14
        //  1061: iload           17
        //  1063: istore          8
        //  1065: iload           15
        //  1067: istore          13
        //  1069: iload           16
        //  1071: istore          15
        //  1073: goto            1103
        //  1076: iload           8
        //  1078: istore          16
        //  1080: iload           15
        //  1082: istore          8
        //  1084: iload           16
        //  1086: istore          15
        //  1088: goto            1091
        //  1091: iload           13
        //  1093: istore          16
        //  1095: iload           14
        //  1097: istore          13
        //  1099: iload           16
        //  1101: istore          14
        //  1103: aload           9
        //  1105: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.circleConstraint:I
        //  1108: istore          16
        //  1110: iload           16
        //  1112: iconst_m1      
        //  1113: if_icmpeq       1149
        //  1116: aload_0        
        //  1117: iload           16
        //  1119: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1122: astore          6
        //  1124: aload           6
        //  1126: ifnull          1711
        //  1129: aload           7
        //  1131: aload           6
        //  1133: aload           9
        //  1135: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.circleAngle:F
        //  1138: aload           9
        //  1140: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.circleRadius:I
        //  1143: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.connectCircularConstraint:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget;FI)V
        //  1146: goto            1711
        //  1149: iload           5
        //  1151: iconst_m1      
        //  1152: if_icmpeq       1197
        //  1155: aload_0        
        //  1156: iload           5
        //  1158: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1161: astore          11
        //  1163: aload           11
        //  1165: ifnull          1194
        //  1168: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.LEFT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1171: astore          6
        //  1173: aload           7
        //  1175: aload           6
        //  1177: aload           11
        //  1179: aload           6
        //  1181: aload           9
        //  1183: getfield        android/view/ViewGroup$MarginLayoutParams.leftMargin:I
        //  1186: iload           13
        //  1188: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1191: goto            1236
        //  1194: goto            1236
        //  1197: iload           4
        //  1199: iconst_m1      
        //  1200: if_icmpeq       1236
        //  1203: aload_0        
        //  1204: iload           4
        //  1206: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1209: astore          6
        //  1211: aload           6
        //  1213: ifnull          1236
        //  1216: aload           7
        //  1218: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.LEFT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1221: aload           6
        //  1223: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.RIGHT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1226: aload           9
        //  1228: getfield        android/view/ViewGroup$MarginLayoutParams.leftMargin:I
        //  1231: iload           13
        //  1233: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1236: iload           14
        //  1238: iconst_m1      
        //  1239: if_icmpeq       1278
        //  1242: aload_0        
        //  1243: iload           14
        //  1245: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1248: astore          6
        //  1250: aload           6
        //  1252: ifnull          1320
        //  1255: aload           7
        //  1257: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.RIGHT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1260: aload           6
        //  1262: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.LEFT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1265: aload           9
        //  1267: getfield        android/view/ViewGroup$MarginLayoutParams.rightMargin:I
        //  1270: iload           15
        //  1272: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1275: goto            1320
        //  1278: iload           8
        //  1280: iconst_m1      
        //  1281: if_icmpeq       1320
        //  1284: aload_0        
        //  1285: iload           8
        //  1287: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1290: astore          11
        //  1292: aload           11
        //  1294: ifnull          1320
        //  1297: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.RIGHT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1300: astore          6
        //  1302: aload           7
        //  1304: aload           6
        //  1306: aload           11
        //  1308: aload           6
        //  1310: aload           9
        //  1312: getfield        android/view/ViewGroup$MarginLayoutParams.rightMargin:I
        //  1315: iload           15
        //  1317: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1320: aload           9
        //  1322: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.topToTop:I
        //  1325: istore          5
        //  1327: iload           5
        //  1329: iconst_m1      
        //  1330: if_icmpeq       1375
        //  1333: aload_0        
        //  1334: iload           5
        //  1336: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1339: astore          11
        //  1341: aload           11
        //  1343: ifnull          1424
        //  1346: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.TOP:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1349: astore          6
        //  1351: aload           7
        //  1353: aload           6
        //  1355: aload           11
        //  1357: aload           6
        //  1359: aload           9
        //  1361: getfield        android/view/ViewGroup$MarginLayoutParams.topMargin:I
        //  1364: aload           9
        //  1366: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.goneTopMargin:I
        //  1369: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1372: goto            1424
        //  1375: aload           9
        //  1377: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.topToBottom:I
        //  1380: istore          5
        //  1382: iload           5
        //  1384: iconst_m1      
        //  1385: if_icmpeq       1424
        //  1388: aload_0        
        //  1389: iload           5
        //  1391: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1394: astore          6
        //  1396: aload           6
        //  1398: ifnull          1424
        //  1401: aload           7
        //  1403: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.TOP:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1406: aload           6
        //  1408: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.BOTTOM:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1411: aload           9
        //  1413: getfield        android/view/ViewGroup$MarginLayoutParams.topMargin:I
        //  1416: aload           9
        //  1418: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.goneTopMargin:I
        //  1421: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1424: aload           9
        //  1426: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.bottomToTop:I
        //  1429: istore          5
        //  1431: iload           5
        //  1433: iconst_m1      
        //  1434: if_icmpeq       1476
        //  1437: aload_0        
        //  1438: iload           5
        //  1440: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1443: astore          6
        //  1445: aload           6
        //  1447: ifnull          1528
        //  1450: aload           7
        //  1452: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.BOTTOM:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1455: aload           6
        //  1457: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.TOP:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1460: aload           9
        //  1462: getfield        android/view/ViewGroup$MarginLayoutParams.bottomMargin:I
        //  1465: aload           9
        //  1467: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.goneBottomMargin:I
        //  1470: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1473: goto            1528
        //  1476: aload           9
        //  1478: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.bottomToBottom:I
        //  1481: istore          5
        //  1483: iload           5
        //  1485: iconst_m1      
        //  1486: if_icmpeq       1528
        //  1489: aload_0        
        //  1490: iload           5
        //  1492: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1495: astore          11
        //  1497: aload           11
        //  1499: ifnull          1528
        //  1502: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.BOTTOM:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1505: astore          6
        //  1507: aload           7
        //  1509: aload           6
        //  1511: aload           11
        //  1513: aload           6
        //  1515: aload           9
        //  1517: getfield        android/view/ViewGroup$MarginLayoutParams.bottomMargin:I
        //  1520: aload           9
        //  1522: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.goneBottomMargin:I
        //  1525: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.immediateConnect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;Landroidx/constraintlayout/solver/widgets/ConstraintWidget;Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;II)V
        //  1528: aload           9
        //  1530: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.baselineToBaseline:I
        //  1533: istore          5
        //  1535: iload           5
        //  1537: iconst_m1      
        //  1538: if_icmpeq       1658
        //  1541: aload_0        
        //  1542: getfield        androidx/constraintlayout/widget/ConstraintLayout.mChildrenByIds:Landroid/util/SparseArray;
        //  1545: iload           5
        //  1547: invokevirtual   android/util/SparseArray.get:(I)Ljava/lang/Object;
        //  1550: checkcast       Landroid/view/View;
        //  1553: astore          11
        //  1555: aload_0        
        //  1556: aload           9
        //  1558: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.baselineToBaseline:I
        //  1561: invokespecial   androidx/constraintlayout/widget/ConstraintLayout.getTargetWidget:(I)Landroidx/constraintlayout/solver/widgets/ConstraintWidget;
        //  1564: astore          6
        //  1566: aload           6
        //  1568: ifnull          1658
        //  1571: aload           11
        //  1573: ifnull          1658
        //  1576: aload           11
        //  1578: invokevirtual   android/view/View.getLayoutParams:()Landroid/view/ViewGroup$LayoutParams;
        //  1581: instanceof      Landroidx/constraintlayout/widget/ConstraintLayout$LayoutParams;
        //  1584: ifeq            1658
        //  1587: aload           11
        //  1589: invokevirtual   android/view/View.getLayoutParams:()Landroid/view/ViewGroup$LayoutParams;
        //  1592: checkcast       Landroidx/constraintlayout/widget/ConstraintLayout$LayoutParams;
        //  1595: astore          11
        //  1597: aload           9
        //  1599: iconst_1       
        //  1600: putfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.needsBaseline:Z
        //  1603: aload           11
        //  1605: iconst_1       
        //  1606: putfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.needsBaseline:Z
        //  1609: aload           7
        //  1611: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.BASELINE:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1614: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1617: aload           6
        //  1619: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.BASELINE:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1622: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1625: iconst_0       
        //  1626: iconst_m1      
        //  1627: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Strength.STRONG:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Strength;
        //  1630: iconst_0       
        //  1631: iconst_1       
        //  1632: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintAnchor.connect:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;IILandroidx/constraintlayout/solver/widgets/ConstraintAnchor$Strength;IZ)Z
        //  1635: pop            
        //  1636: aload           7
        //  1638: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.TOP:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1641: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1644: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintAnchor.reset:()V
        //  1647: aload           7
        //  1649: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.BOTTOM:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1652: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1655: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintAnchor.reset:()V
        //  1658: fload           12
        //  1660: fconst_0       
        //  1661: fcmpl          
        //  1662: iflt            1681
        //  1665: fload           12
        //  1667: ldc_w           0.5
        //  1670: fcmpl          
        //  1671: ifeq            1681
        //  1674: aload           7
        //  1676: fload           12
        //  1678: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHorizontalBiasPercent:(F)V
        //  1681: aload           9
        //  1683: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.verticalBias:F
        //  1686: fstore          12
        //  1688: fload           12
        //  1690: fconst_0       
        //  1691: fcmpl          
        //  1692: iflt            1711
        //  1695: fload           12
        //  1697: ldc_w           0.5
        //  1700: fcmpl          
        //  1701: ifeq            1711
        //  1704: aload           7
        //  1706: fload           12
        //  1708: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVerticalBiasPercent:(F)V
        //  1711: iload_2        
        //  1712: ifeq            1748
        //  1715: aload           9
        //  1717: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.editorAbsoluteX:I
        //  1720: iconst_m1      
        //  1721: if_icmpne       1733
        //  1724: aload           9
        //  1726: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.editorAbsoluteY:I
        //  1729: iconst_m1      
        //  1730: if_icmpeq       1748
        //  1733: aload           7
        //  1735: aload           9
        //  1737: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.editorAbsoluteX:I
        //  1740: aload           9
        //  1742: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.editorAbsoluteY:I
        //  1745: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setOrigin:(II)V
        //  1748: aload           9
        //  1750: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.horizontalDimensionFixed:Z
        //  1753: ifne            1825
        //  1756: aload           9
        //  1758: getfield        android/view/ViewGroup$MarginLayoutParams.width:I
        //  1761: iconst_m1      
        //  1762: if_icmpne       1808
        //  1765: aload           7
        //  1767: getstatic       androidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_PARENT:Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;
        //  1770: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHorizontalDimensionBehaviour:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
        //  1773: aload           7
        //  1775: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.LEFT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1778: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1781: aload           9
        //  1783: getfield        android/view/ViewGroup$MarginLayoutParams.leftMargin:I
        //  1786: putfield        androidx/constraintlayout/solver/widgets/ConstraintAnchor.mMargin:I
        //  1789: aload           7
        //  1791: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.RIGHT:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1794: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1797: aload           9
        //  1799: getfield        android/view/ViewGroup$MarginLayoutParams.rightMargin:I
        //  1802: putfield        androidx/constraintlayout/solver/widgets/ConstraintAnchor.mMargin:I
        //  1805: goto            1843
        //  1808: aload           7
        //  1810: getstatic       androidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT:Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;
        //  1813: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHorizontalDimensionBehaviour:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
        //  1816: aload           7
        //  1818: iconst_0       
        //  1819: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setWidth:(I)V
        //  1822: goto            1843
        //  1825: aload           7
        //  1827: getstatic       androidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED:Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;
        //  1830: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHorizontalDimensionBehaviour:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
        //  1833: aload           7
        //  1835: aload           9
        //  1837: getfield        android/view/ViewGroup$MarginLayoutParams.width:I
        //  1840: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setWidth:(I)V
        //  1843: aload           9
        //  1845: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.verticalDimensionFixed:Z
        //  1848: ifne            1920
        //  1851: aload           9
        //  1853: getfield        android/view/ViewGroup$MarginLayoutParams.height:I
        //  1856: iconst_m1      
        //  1857: if_icmpne       1903
        //  1860: aload           7
        //  1862: getstatic       androidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_PARENT:Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;
        //  1865: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVerticalDimensionBehaviour:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
        //  1868: aload           7
        //  1870: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.TOP:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1873: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1876: aload           9
        //  1878: getfield        android/view/ViewGroup$MarginLayoutParams.topMargin:I
        //  1881: putfield        androidx/constraintlayout/solver/widgets/ConstraintAnchor.mMargin:I
        //  1884: aload           7
        //  1886: getstatic       androidx/constraintlayout/solver/widgets/ConstraintAnchor$Type.BOTTOM:Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;
        //  1889: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.getAnchor:(Landroidx/constraintlayout/solver/widgets/ConstraintAnchor$Type;)Landroidx/constraintlayout/solver/widgets/ConstraintAnchor;
        //  1892: aload           9
        //  1894: getfield        android/view/ViewGroup$MarginLayoutParams.bottomMargin:I
        //  1897: putfield        androidx/constraintlayout/solver/widgets/ConstraintAnchor.mMargin:I
        //  1900: goto            1938
        //  1903: aload           7
        //  1905: getstatic       androidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT:Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;
        //  1908: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVerticalDimensionBehaviour:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
        //  1911: aload           7
        //  1913: iconst_0       
        //  1914: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHeight:(I)V
        //  1917: goto            1938
        //  1920: aload           7
        //  1922: getstatic       androidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED:Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;
        //  1925: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVerticalDimensionBehaviour:(Landroidx/constraintlayout/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
        //  1928: aload           7
        //  1930: aload           9
        //  1932: getfield        android/view/ViewGroup$MarginLayoutParams.height:I
        //  1935: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHeight:(I)V
        //  1938: iconst_0       
        //  1939: istore          8
        //  1941: aload           9
        //  1943: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.dimensionRatio:Ljava/lang/String;
        //  1946: astore          6
        //  1948: aload           6
        //  1950: ifnull          1960
        //  1953: aload           7
        //  1955: aload           6
        //  1957: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setDimensionRatio:(Ljava/lang/String;)V
        //  1960: aload           7
        //  1962: aload           9
        //  1964: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.horizontalWeight:F
        //  1967: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHorizontalWeight:(F)V
        //  1970: aload           7
        //  1972: aload           9
        //  1974: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.verticalWeight:F
        //  1977: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVerticalWeight:(F)V
        //  1980: aload           7
        //  1982: aload           9
        //  1984: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.horizontalChainStyle:I
        //  1987: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHorizontalChainStyle:(I)V
        //  1990: aload           7
        //  1992: aload           9
        //  1994: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.verticalChainStyle:I
        //  1997: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVerticalChainStyle:(I)V
        //  2000: aload           7
        //  2002: aload           9
        //  2004: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintDefaultWidth:I
        //  2007: aload           9
        //  2009: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintMinWidth:I
        //  2012: aload           9
        //  2014: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintMaxWidth:I
        //  2017: aload           9
        //  2019: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintPercentWidth:F
        //  2022: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setHorizontalMatchStyle:(IIIF)V
        //  2025: aload           7
        //  2027: aload           9
        //  2029: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintDefaultHeight:I
        //  2032: aload           9
        //  2034: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintMinHeight:I
        //  2037: aload           9
        //  2039: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintMaxHeight:I
        //  2042: aload           9
        //  2044: getfield        androidx/constraintlayout/widget/ConstraintLayout$LayoutParams.matchConstraintPercentHeight:F
        //  2047: invokevirtual   androidx/constraintlayout/solver/widgets/ConstraintWidget.setVerticalMatchStyle:(IIIF)V
        //  2050: iinc            10, 1
        //  2053: iload           8
        //  2055: istore          5
        //  2057: goto            337
        //  2060: return         
        //  2061: astore          9
        //  2063: goto            111
        //  2066: astore          11
        //  2068: goto            469
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                             
        //  -----  -----  -----  -----  -------------------------------------------------
        //  38     76     2061   2066   Landroid/content/res/Resources$NotFoundException;
        //  86     97     2061   2066   Landroid/content/res/Resources$NotFoundException;
        //  97     111    2061   2066   Landroid/content/res/Resources$NotFoundException;
        //  408    469    2066   2071   Landroid/content/res/Resources$NotFoundException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0469:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void setSelfDimensionBehaviour(int size, int size2) {
        final int mode = View$MeasureSpec.getMode(size);
        size = View$MeasureSpec.getSize(size);
        final int mode2 = View$MeasureSpec.getMode(size2);
        size2 = View$MeasureSpec.getSize(size2);
        final int paddingTop = this.getPaddingTop();
        final int paddingBottom = this.getPaddingBottom();
        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        ConstraintWidget.DimensionBehaviour verticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        this.getLayoutParams();
        ConstraintWidget.DimensionBehaviour horizontalDimensionBehaviour = null;
        Label_0117: {
            if (mode != Integer.MIN_VALUE) {
                if (mode != 0) {
                    if (mode == 1073741824) {
                        size = Math.min(this.mMaxWidth, size) - (paddingLeft + paddingRight);
                        horizontalDimensionBehaviour = verticalDimensionBehaviour;
                        break Label_0117;
                    }
                    horizontalDimensionBehaviour = verticalDimensionBehaviour;
                }
                else {
                    horizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                }
                size = 0;
            }
            else {
                horizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            }
        }
        Label_0174: {
            if (mode2 != Integer.MIN_VALUE) {
                if (mode2 != 0) {
                    if (mode2 == 1073741824) {
                        size2 = Math.min(this.mMaxHeight, size2) - (paddingTop + paddingBottom);
                        break Label_0174;
                    }
                }
                else {
                    verticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                }
                size2 = 0;
            }
            else {
                verticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            }
        }
        this.mLayoutWidget.setMinWidth(0);
        this.mLayoutWidget.setMinHeight(0);
        this.mLayoutWidget.setHorizontalDimensionBehaviour(horizontalDimensionBehaviour);
        this.mLayoutWidget.setWidth(size);
        this.mLayoutWidget.setVerticalDimensionBehaviour(verticalDimensionBehaviour);
        this.mLayoutWidget.setHeight(size2);
        this.mLayoutWidget.setMinWidth(this.mMinWidth - this.getPaddingLeft() - this.getPaddingRight());
        this.mLayoutWidget.setMinHeight(this.mMinHeight - this.getPaddingTop() - this.getPaddingBottom());
    }
    
    private void updateHierarchy() {
        final int childCount = this.getChildCount();
        final int n = 0;
        int n2 = 0;
        int n3;
        while (true) {
            n3 = n;
            if (n2 >= childCount) {
                break;
            }
            if (this.getChildAt(n2).isLayoutRequested()) {
                n3 = 1;
                break;
            }
            ++n2;
        }
        if (n3 != 0) {
            this.mVariableDimensionsWidgets.clear();
            this.setChildrenConstraints();
        }
    }
    
    private void updatePostMeasures() {
        final int childCount = this.getChildCount();
        final int n = 0;
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child instanceof Placeholder) {
                ((Placeholder)child).updatePostMeasure(this);
            }
        }
        final int size = this.mConstraintHelpers.size();
        if (size > 0) {
            for (int j = n; j < size; ++j) {
                this.mConstraintHelpers.get(j).updatePostMeasure(this);
            }
        }
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        super.addView(view, n, viewGroup$LayoutParams);
        if (Build$VERSION.SDK_INT < 14) {
            this.onViewAdded(view);
        }
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    public void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.isInEditMode()) {
            final int childCount = this.getChildCount();
            final float n = (float)this.getWidth();
            final float n2 = (float)this.getHeight();
            for (int i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                if (child.getVisibility() != 8) {
                    final Object tag = child.getTag();
                    if (tag != null && tag instanceof String) {
                        final String[] split = ((String)tag).split(",");
                        if (split.length == 4) {
                            final int int1 = Integer.parseInt(split[0]);
                            final int int2 = Integer.parseInt(split[1]);
                            final int int3 = Integer.parseInt(split[2]);
                            final int int4 = Integer.parseInt(split[3]);
                            final int n3 = (int)(int1 / 1080.0f * n);
                            final int n4 = (int)(int2 / 1920.0f * n2);
                            final int n5 = (int)(int3 / 1080.0f * n);
                            final int n6 = (int)(int4 / 1920.0f * n2);
                            final Paint paint = new Paint();
                            paint.setColor(-65536);
                            final float n7 = (float)n3;
                            final float n8 = (float)n4;
                            final float n9 = (float)(n3 + n5);
                            canvas.drawLine(n7, n8, n9, n8, paint);
                            final float n10 = (float)(n4 + n6);
                            canvas.drawLine(n9, n8, n9, n10, paint);
                            canvas.drawLine(n9, n10, n7, n10, paint);
                            canvas.drawLine(n7, n10, n7, n8, paint);
                            paint.setColor(-16711936);
                            canvas.drawLine(n7, n8, n9, n10, paint);
                            canvas.drawLine(n7, n10, n9, n8, paint);
                        }
                    }
                }
            }
        }
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return (ViewGroup$LayoutParams)new LayoutParams(viewGroup$LayoutParams);
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    public Object getDesignInformation(final int n, final Object o) {
        if (n == 0 && o instanceof String) {
            final String s = (String)o;
            final HashMap<String, Integer> mDesignIds = this.mDesignIds;
            if (mDesignIds != null && mDesignIds.containsKey(s)) {
                return this.mDesignIds.get(s);
            }
        }
        return null;
    }
    
    public View getViewById(final int n) {
        return (View)this.mChildrenByIds.get(n);
    }
    
    public final ConstraintWidget getViewWidget(final View view) {
        if (view == this) {
            return this.mLayoutWidget;
        }
        ConstraintWidget widget;
        if (view == null) {
            widget = null;
        }
        else {
            widget = ((LayoutParams)view.getLayoutParams()).widget;
        }
        return widget;
    }
    
    protected void onLayout(final boolean b, int i, int n, int n2, int drawY) {
        n2 = this.getChildCount();
        final boolean inEditMode = this.isInEditMode();
        n = 0;
        View child;
        LayoutParams layoutParams;
        ConstraintWidget widget;
        int drawX;
        int n3;
        int n4;
        View content;
        for (i = 0; i < n2; ++i) {
            child = this.getChildAt(i);
            layoutParams = (LayoutParams)child.getLayoutParams();
            widget = layoutParams.widget;
            if (child.getVisibility() != 8 || layoutParams.isGuideline || layoutParams.isHelper || inEditMode) {
                if (!layoutParams.isInPlaceholder) {
                    drawX = widget.getDrawX();
                    drawY = widget.getDrawY();
                    n3 = widget.getWidth() + drawX;
                    n4 = widget.getHeight() + drawY;
                    child.layout(drawX, drawY, n3, n4);
                    if (child instanceof Placeholder) {
                        content = ((Placeholder)child).getContent();
                        if (content != null) {
                            content.setVisibility(0);
                            content.layout(drawX, drawY, n3, n4);
                        }
                    }
                }
            }
        }
        n2 = this.mConstraintHelpers.size();
        if (n2 > 0) {
            for (i = n; i < n2; ++i) {
                this.mConstraintHelpers.get(i).updatePostLayout(this);
            }
        }
    }
    
    protected void onMeasure(int resolveSizeAndState, int min) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        System.currentTimeMillis();
        final int mode = View$MeasureSpec.getMode(resolveSizeAndState);
        final int size = View$MeasureSpec.getSize(resolveSizeAndState);
        final int mode2 = View$MeasureSpec.getMode(min);
        final int size2 = View$MeasureSpec.getSize(min);
        if (this.mLastMeasureWidth != -1) {
            final int mLastMeasureHeight = this.mLastMeasureHeight;
        }
        if (mode == 1073741824 && mode2 == 1073741824 && size == this.mLastMeasureWidth) {
            final int mLastMeasureHeight2 = this.mLastMeasureHeight;
        }
        final boolean b = mode == this.mLastMeasureWidthMode && mode2 == this.mLastMeasureHeightMode;
        if (b && size == this.mLastMeasureWidthSize) {
            final int mLastMeasureHeightSize = this.mLastMeasureHeightSize;
        }
        if (b && mode == Integer.MIN_VALUE && mode2 == 1073741824 && size >= this.mLastMeasureWidth) {
            final int mLastMeasureHeight3 = this.mLastMeasureHeight;
        }
        if (b && mode == 1073741824 && mode2 == Integer.MIN_VALUE && size == this.mLastMeasureWidth) {
            final int mLastMeasureHeight4 = this.mLastMeasureHeight;
        }
        this.mLastMeasureWidthMode = mode;
        this.mLastMeasureHeightMode = mode2;
        this.mLastMeasureWidthSize = size;
        this.mLastMeasureHeightSize = size2;
        final int paddingLeft = this.getPaddingLeft();
        final int paddingTop = this.getPaddingTop();
        this.mLayoutWidget.setX(paddingLeft);
        this.mLayoutWidget.setY(paddingTop);
        this.mLayoutWidget.setMaxWidth(this.mMaxWidth);
        this.mLayoutWidget.setMaxHeight(this.mMaxHeight);
        if (sdk_INT >= 17) {
            this.mLayoutWidget.setRtl(this.getLayoutDirection() == 1);
        }
        this.setSelfDimensionBehaviour(resolveSizeAndState, min);
        final int width = this.mLayoutWidget.getWidth();
        final int height = this.mLayoutWidget.getHeight();
        if (this.mDirtyHierarchy) {
            this.mDirtyHierarchy = false;
            this.updateHierarchy();
        }
        final boolean b2 = (this.mOptimizationLevel & 0x8) == 0x8;
        if (b2) {
            this.mLayoutWidget.preOptimize();
            this.mLayoutWidget.optimizeForDimensions(width, height);
            this.internalMeasureDimensions(resolveSizeAndState, min);
        }
        else {
            this.internalMeasureChildren(resolveSizeAndState, min);
        }
        this.updatePostMeasures();
        if (this.getChildCount() > 0) {
            this.solveLinearSystem("First pass");
        }
        final int size3 = this.mVariableDimensionsWidgets.size();
        final int n = paddingTop + this.getPaddingBottom();
        final int n2 = paddingLeft + this.getPaddingRight();
        int combineMeasuredStates;
        if (size3 > 0) {
            final boolean b3 = this.mLayoutWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            final boolean b4 = this.mLayoutWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            int max = Math.max(this.mLayoutWidget.getWidth(), this.mMinWidth);
            int max2 = Math.max(this.mLayoutWidget.getHeight(), this.mMinHeight);
            int i = 0;
            boolean b5 = false;
            combineMeasuredStates = 0;
            while (i < size3) {
                final ConstraintWidget constraintWidget = this.mVariableDimensionsWidgets.get(i);
                final View view = (View)constraintWidget.getCompanionWidget();
                if (view != null) {
                    final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
                    if (!layoutParams.isHelper) {
                        if (!layoutParams.isGuideline) {
                            if (view.getVisibility() != 8 && (!b2 || !constraintWidget.getResolutionWidth().isResolved() || !constraintWidget.getResolutionHeight().isResolved())) {
                                final int width2 = layoutParams.width;
                                int n3;
                                if (width2 == -2 && layoutParams.horizontalDimensionFixed) {
                                    n3 = ViewGroup.getChildMeasureSpec(resolveSizeAndState, n2, width2);
                                }
                                else {
                                    n3 = View$MeasureSpec.makeMeasureSpec(constraintWidget.getWidth(), 1073741824);
                                }
                                final int height2 = layoutParams.height;
                                int n4;
                                if (height2 == -2 && layoutParams.verticalDimensionFixed) {
                                    n4 = ViewGroup.getChildMeasureSpec(min, n, height2);
                                }
                                else {
                                    n4 = View$MeasureSpec.makeMeasureSpec(constraintWidget.getHeight(), 1073741824);
                                }
                                view.measure(n3, n4);
                                final Metrics mMetrics = this.mMetrics;
                                if (mMetrics != null) {
                                    ++mMetrics.additionalMeasures;
                                }
                                final int measuredWidth = view.getMeasuredWidth();
                                final int measuredHeight = view.getMeasuredHeight();
                                int max3 = max;
                                if (measuredWidth != constraintWidget.getWidth()) {
                                    constraintWidget.setWidth(measuredWidth);
                                    if (b2) {
                                        constraintWidget.getResolutionWidth().resolve(measuredWidth);
                                    }
                                    max3 = max;
                                    if (b3 && constraintWidget.getRight() > (max3 = max)) {
                                        max3 = Math.max(max, constraintWidget.getRight() + constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).getMargin());
                                    }
                                    b5 = true;
                                }
                                int max4 = max2;
                                if (measuredHeight != constraintWidget.getHeight()) {
                                    constraintWidget.setHeight(measuredHeight);
                                    if (b2) {
                                        constraintWidget.getResolutionHeight().resolve(measuredHeight);
                                    }
                                    max4 = max2;
                                    if (b4 && constraintWidget.getBottom() > (max4 = max2)) {
                                        max4 = Math.max(max2, constraintWidget.getBottom() + constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM).getMargin());
                                    }
                                    b5 = true;
                                }
                                if (layoutParams.needsBaseline) {
                                    final int baseline = view.getBaseline();
                                    if (baseline != -1 && baseline != constraintWidget.getBaselineDistance()) {
                                        constraintWidget.setBaselineDistance(baseline);
                                        b5 = true;
                                    }
                                }
                                if (sdk_INT >= 11) {
                                    combineMeasuredStates = ViewGroup.combineMeasuredStates(combineMeasuredStates, view.getMeasuredState());
                                    max2 = max4;
                                    max = max3;
                                }
                                else {
                                    max2 = max4;
                                    max = max3;
                                }
                            }
                        }
                    }
                }
                ++i;
            }
            if (b5) {
                this.mLayoutWidget.setWidth(width);
                this.mLayoutWidget.setHeight(height);
                if (b2) {
                    this.mLayoutWidget.solveGraph();
                }
                this.solveLinearSystem("2nd pass");
                boolean b6;
                if (this.mLayoutWidget.getWidth() < max) {
                    this.mLayoutWidget.setWidth(max);
                    b6 = true;
                }
                else {
                    b6 = false;
                }
                boolean b7;
                if (this.mLayoutWidget.getHeight() < max2) {
                    this.mLayoutWidget.setHeight(max2);
                    b7 = true;
                }
                else {
                    b7 = b6;
                }
                if (b7) {
                    this.solveLinearSystem("3rd pass");
                }
            }
            for (int j = 0; j < size3; ++j) {
                final ConstraintWidget constraintWidget2 = this.mVariableDimensionsWidgets.get(j);
                final View view2 = (View)constraintWidget2.getCompanionWidget();
                if (view2 != null && (view2.getMeasuredWidth() != constraintWidget2.getWidth() || view2.getMeasuredHeight() != constraintWidget2.getHeight()) && constraintWidget2.getVisibility() != 8) {
                    view2.measure(View$MeasureSpec.makeMeasureSpec(constraintWidget2.getWidth(), 1073741824), View$MeasureSpec.makeMeasureSpec(constraintWidget2.getHeight(), 1073741824));
                    final Metrics mMetrics2 = this.mMetrics;
                    if (mMetrics2 != null) {
                        ++mMetrics2.additionalMeasures;
                    }
                }
            }
        }
        else {
            combineMeasuredStates = 0;
        }
        final int mLastMeasureWidth = this.mLayoutWidget.getWidth() + n2;
        final int mLastMeasureHeight5 = this.mLayoutWidget.getHeight() + n;
        if (sdk_INT >= 11) {
            resolveSizeAndState = ViewGroup.resolveSizeAndState(mLastMeasureWidth, resolveSizeAndState, combineMeasuredStates);
            final int resolveSizeAndState2 = ViewGroup.resolveSizeAndState(mLastMeasureHeight5, min, combineMeasuredStates << 16);
            min = Math.min(this.mMaxWidth, resolveSizeAndState & 0xFFFFFF);
            final int min2 = Math.min(this.mMaxHeight, resolveSizeAndState2 & 0xFFFFFF);
            resolveSizeAndState = min;
            if (this.mLayoutWidget.isWidthMeasuredTooSmall()) {
                resolveSizeAndState = (min | 0x1000000);
            }
            min = min2;
            if (this.mLayoutWidget.isHeightMeasuredTooSmall()) {
                min = (min2 | 0x1000000);
            }
            this.setMeasuredDimension(resolveSizeAndState, min);
            this.mLastMeasureWidth = resolveSizeAndState;
            this.mLastMeasureHeight = min;
        }
        else {
            this.setMeasuredDimension(mLastMeasureWidth, mLastMeasureHeight5);
            this.mLastMeasureWidth = mLastMeasureWidth;
            this.mLastMeasureHeight = mLastMeasureHeight5;
        }
    }
    
    public void onViewAdded(final View view) {
        if (Build$VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        final ConstraintWidget viewWidget = this.getViewWidget(view);
        if (view instanceof Guideline && !(viewWidget instanceof androidx.constraintlayout.solver.widgets.Guideline)) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final androidx.constraintlayout.solver.widgets.Guideline widget = new androidx.constraintlayout.solver.widgets.Guideline();
            layoutParams.widget = widget;
            layoutParams.isGuideline = true;
            widget.setOrientation(layoutParams.orientation);
        }
        if (view instanceof ConstraintHelper) {
            final ConstraintHelper constraintHelper = (ConstraintHelper)view;
            constraintHelper.validateParams();
            ((LayoutParams)view.getLayoutParams()).isHelper = true;
            if (!this.mConstraintHelpers.contains(constraintHelper)) {
                this.mConstraintHelpers.add(constraintHelper);
            }
        }
        this.mChildrenByIds.put(view.getId(), (Object)view);
        this.mDirtyHierarchy = true;
    }
    
    public void onViewRemoved(final View o) {
        if (Build$VERSION.SDK_INT >= 14) {
            super.onViewRemoved(o);
        }
        this.mChildrenByIds.remove(o.getId());
        final ConstraintWidget viewWidget = this.getViewWidget(o);
        this.mLayoutWidget.remove(viewWidget);
        this.mConstraintHelpers.remove(o);
        this.mVariableDimensionsWidgets.remove(viewWidget);
        this.mDirtyHierarchy = true;
    }
    
    public void removeView(final View view) {
        super.removeView(view);
        if (Build$VERSION.SDK_INT < 14) {
            this.onViewRemoved(view);
        }
    }
    
    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
    }
    
    public void setDesignInformation(int i, final Object o, final Object o2) {
        if (i == 0 && o instanceof String && o2 instanceof Integer) {
            if (this.mDesignIds == null) {
                this.mDesignIds = new HashMap<String, Integer>();
            }
            final String s = (String)o;
            i = s.indexOf("/");
            String substring = s;
            if (i != -1) {
                substring = s.substring(i + 1);
            }
            i = (int)o2;
            this.mDesignIds.put(substring, i);
        }
    }
    
    public void setId(final int id) {
        this.mChildrenByIds.remove(this.getId());
        super.setId(id);
        this.mChildrenByIds.put(this.getId(), (Object)this);
    }
    
    public boolean shouldDelayChildPressedState() {
        return false;
    }
    
    protected void solveLinearSystem(final String s) {
        this.mLayoutWidget.layout();
        final Metrics mMetrics = this.mMetrics;
        if (mMetrics != null) {
            ++mMetrics.resolutions;
        }
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public int baselineToBaseline;
        public int bottomToBottom;
        public int bottomToTop;
        public float circleAngle;
        public int circleConstraint;
        public int circleRadius;
        public boolean constrainedHeight;
        public boolean constrainedWidth;
        public String dimensionRatio;
        int dimensionRatioSide;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
        public int endToEnd;
        public int endToStart;
        public int goneBottomMargin;
        public int goneEndMargin;
        public int goneLeftMargin;
        public int goneRightMargin;
        public int goneStartMargin;
        public int goneTopMargin;
        public int guideBegin;
        public int guideEnd;
        public float guidePercent;
        public boolean helped;
        public float horizontalBias;
        public int horizontalChainStyle;
        boolean horizontalDimensionFixed;
        public float horizontalWeight;
        boolean isGuideline;
        boolean isHelper;
        boolean isInPlaceholder;
        public int leftToLeft;
        public int leftToRight;
        public int matchConstraintDefaultHeight;
        public int matchConstraintDefaultWidth;
        public int matchConstraintMaxHeight;
        public int matchConstraintMaxWidth;
        public int matchConstraintMinHeight;
        public int matchConstraintMinWidth;
        public float matchConstraintPercentHeight;
        public float matchConstraintPercentWidth;
        boolean needsBaseline;
        public int orientation;
        int resolveGoneLeftMargin;
        int resolveGoneRightMargin;
        int resolvedGuideBegin;
        int resolvedGuideEnd;
        float resolvedGuidePercent;
        float resolvedHorizontalBias;
        int resolvedLeftToLeft;
        int resolvedLeftToRight;
        int resolvedRightToLeft;
        int resolvedRightToRight;
        public int rightToLeft;
        public int rightToRight;
        public int startToEnd;
        public int startToStart;
        public int topToBottom;
        public int topToTop;
        public float verticalBias;
        public int verticalChainStyle;
        boolean verticalDimensionFixed;
        public float verticalWeight;
        ConstraintWidget widget;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = -1.0f;
            this.verticalWeight = -1.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }
        
        public LayoutParams(Context obtainStyledAttributes, final AttributeSet set) {
            super(obtainStyledAttributes, set);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = -1.0f;
            this.verticalWeight = -1.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
            obtainStyledAttributes = (Context)obtainStyledAttributes.obtainStyledAttributes(set, R$styleable.ConstraintLayout_Layout);
            final int indexCount = ((TypedArray)obtainStyledAttributes).getIndexCount();
            int n = 0;
        Label_2037_Outer:
            while (true) {
                Label_2043: {
                    if (n >= indexCount) {
                        break Label_2043;
                    }
                    final int index = ((TypedArray)obtainStyledAttributes).getIndex(n);
                    final int value = Table.map.get(index);
                    String string;
                    int length;
                    int index2;
                    String substring;
                    int index3;
                    String substring2 = null;
                    String substring3 = null;
                    float float1;
                    float float2;
                    float circleAngle;
                    int resourceId;
                    int resourceId2;
                    int resourceId3;
                    int resourceId4;
                    int resourceId5;
                    String substring4;
                    int int1;
                    int resourceId6;
                    int resourceId7;
                    int resourceId8;
                    int resourceId9;
                    int int2;
                    int resourceId10;
                    int resourceId11;
                    int resourceId12;
                    int resourceId13;
                    int resourceId14;
                    Block_21_Outer:Block_27_Outer:Block_29_Outer:
                    while (true) {
                        Label_2023: {
                            Block_10: {
                                switch (value) {
                                    default: {
                                        switch (value) {
                                            default: {
                                                break Label_2037;
                                            }
                                            case 50: {
                                                this.editorAbsoluteY = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.editorAbsoluteY);
                                                break Label_2037;
                                            }
                                            case 49: {
                                                this.editorAbsoluteX = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.editorAbsoluteX);
                                                break Label_2037;
                                            }
                                            case 48: {
                                                this.verticalChainStyle = ((TypedArray)obtainStyledAttributes).getInt(index, 0);
                                                break Label_2037;
                                            }
                                            case 47: {
                                                this.horizontalChainStyle = ((TypedArray)obtainStyledAttributes).getInt(index, 0);
                                                break Label_2037;
                                            }
                                            case 46: {
                                                this.verticalWeight = ((TypedArray)obtainStyledAttributes).getFloat(index, this.verticalWeight);
                                                break Label_2037;
                                            }
                                            case 45: {
                                                this.horizontalWeight = ((TypedArray)obtainStyledAttributes).getFloat(index, this.horizontalWeight);
                                                break Label_2037;
                                            }
                                            case 44: {
                                                string = ((TypedArray)obtainStyledAttributes).getString(index);
                                                this.dimensionRatio = string;
                                                this.dimensionRatioSide = -1;
                                                if (string == null) {
                                                    break Label_2037;
                                                }
                                                length = string.length();
                                                index2 = this.dimensionRatio.indexOf(44);
                                                if (index2 > 0 && index2 < length - 1) {
                                                    substring = this.dimensionRatio.substring(0, index2);
                                                    if (substring.equalsIgnoreCase("W")) {
                                                        this.dimensionRatioSide = 0;
                                                    }
                                                    else if (substring.equalsIgnoreCase("H")) {
                                                        this.dimensionRatioSide = 1;
                                                    }
                                                    ++index2;
                                                }
                                                else {
                                                    index2 = 0;
                                                }
                                                index3 = this.dimensionRatio.indexOf(58);
                                                if (index3 < 0 || index3 >= length - 1) {
                                                    break Label_2023;
                                                }
                                                substring2 = this.dimensionRatio.substring(index2, index3);
                                                substring3 = this.dimensionRatio.substring(index3 + 1);
                                                if (substring2.length() > 0 && substring3.length() > 0) {
                                                    break Block_10;
                                                }
                                                break Label_2037;
                                            }
                                        }
                                        break;
                                    }
                                    case 38: {
                                        break Label_2023;
                                    }
                                    case 37: {
                                        break Label_2023;
                                    }
                                    case 36: {
                                        break Label_2023;
                                    }
                                    case 35: {
                                        break Label_2023;
                                    }
                                    case 34: {
                                        break Label_2023;
                                    }
                                    case 33: {
                                        break Label_2023;
                                    }
                                    case 32: {
                                        break Label_2023;
                                    }
                                    case 31: {
                                        break Label_2023;
                                    }
                                    case 30: {
                                        break Label_2023;
                                    }
                                    case 29: {
                                        break Label_2023;
                                    }
                                    case 28: {
                                        break Label_2023;
                                    }
                                    case 27: {
                                        break Label_2023;
                                    }
                                    case 26: {
                                        break Label_2023;
                                    }
                                    case 25: {
                                        break Label_2023;
                                    }
                                    case 24: {
                                        break Label_2023;
                                    }
                                    case 23: {
                                        break Label_2023;
                                    }
                                    case 22: {
                                        break Label_2023;
                                    }
                                    case 21: {
                                        break Label_2023;
                                    }
                                    case 20: {
                                        break Label_2023;
                                    }
                                    case 19: {
                                        break Label_2023;
                                    }
                                    case 18: {
                                        break Label_2023;
                                    }
                                    case 17: {
                                        break Label_2023;
                                    }
                                    case 16: {
                                        break Label_2023;
                                    }
                                    case 15: {
                                        break Label_2023;
                                    }
                                    case 14: {
                                        break Label_2023;
                                    }
                                    case 13: {
                                        break Label_2023;
                                    }
                                    case 12: {
                                        break Label_2023;
                                    }
                                    case 11: {
                                        break Label_2023;
                                    }
                                    case 10: {
                                        break Label_2023;
                                    }
                                    case 9: {
                                        break Label_2023;
                                    }
                                    case 8: {
                                        break Label_2023;
                                    }
                                    case 7: {
                                        break Label_2023;
                                    }
                                    case 6: {
                                        break Label_2023;
                                    }
                                    case 5: {
                                        break Label_2023;
                                    }
                                    case 4: {
                                        break Label_2023;
                                    }
                                    case 3: {
                                        break Label_2023;
                                    }
                                    case 2: {
                                        break Label_2023;
                                    }
                                    case 1: {
                                        break Label_2023;
                                    }
                                }
                            }
                            try {
                                float1 = Float.parseFloat(substring2);
                                float2 = Float.parseFloat(substring3);
                                if (float1 > 0.0f && float2 > 0.0f) {
                                    if (this.dimensionRatioSide == 1) {
                                        Math.abs(float2 / float1);
                                    }
                                    else {
                                        Math.abs(float1 / float2);
                                    }
                                }
                                ++n;
                                continue Label_2037_Outer;
                                // iftrue(Label_2037:, circleAngle >= 0.0f)
                                // iftrue(Label_2037:, resourceId != -1)
                                // iftrue(Label_2037:, resourceId2 != -1)
                                // iftrue(Label_2037:, resourceId3 != -1)
                                // iftrue(Label_2037:, resourceId4 != -1)
                                // iftrue(Label_2037:, resourceId5 != -1)
                                // iftrue(Label_2037:, substring4.length() <= 0)
                                // iftrue(Label_2037:, int1 != 1)
                                // iftrue(Label_2037:, resourceId6 != -1)
                                // iftrue(Label_2037:, resourceId7 != -1)
                                // iftrue(Label_2037:, resourceId8 != -1)
                                // iftrue(Label_2037:, resourceId9 != -1)
                                // iftrue(Label_2037:, int2 != 1)
                                // iftrue(Label_2037:, resourceId10 != -1)
                                // iftrue(Label_2037:, resourceId11 != -1)
                                // iftrue(Label_2037:, resourceId12 != -1)
                                // iftrue(Label_2037:, resourceId13 != -1)
                                // iftrue(Label_2037:, resourceId14 != -1)
                                Block_35: {
                                    while (true) {
                                        Block_25_Outer:Block_26_Outer:Block_31_Outer:
                                        while (true) {
                                            while (true) {
                                                while (true) {
                                                    while (true) {
                                                    Block_32_Outer:
                                                        while (true) {
                                                            Log.e("ConstraintLayout", "layout_constraintWidth_default=\"wrap\" is deprecated.\nUse layout_width=\"WRAP_CONTENT\" and layout_constrainedWidth=\"true\" instead.");
                                                            continue Block_21_Outer;
                                                        Block_22_Outer:
                                                            while (true) {
                                                                while (true) {
                                                                    Block_24: {
                                                                        while (true) {
                                                                            while (true) {
                                                                                Block_15: {
                                                                                    while (true) {
                                                                                        Block_33: {
                                                                                            while (true) {
                                                                                                this.rightToLeft = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                continue Block_21_Outer;
                                                                                                this.horizontalBias = ((TypedArray)obtainStyledAttributes).getFloat(index, this.horizontalBias);
                                                                                                continue Block_21_Outer;
                                                                                                circleAngle = ((TypedArray)obtainStyledAttributes).getFloat(index, this.circleAngle) % 360.0f;
                                                                                                this.circleAngle = circleAngle;
                                                                                                break Block_35;
                                                                                                this.constrainedWidth = ((TypedArray)obtainStyledAttributes).getBoolean(index, this.constrainedWidth);
                                                                                                continue Block_21_Outer;
                                                                                                this.bottomToBottom = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                continue Block_21_Outer;
                                                                                                resourceId = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.leftToRight);
                                                                                                this.leftToRight = resourceId;
                                                                                                break Block_33;
                                                                                                while (true) {
                                                                                                    this.endToStart = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                    continue Block_21_Outer;
                                                                                                    this.startToEnd = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                    continue Block_21_Outer;
                                                                                                    resourceId2 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.endToStart);
                                                                                                    this.endToStart = resourceId2;
                                                                                                    continue Block_25_Outer;
                                                                                                }
                                                                                                this.goneLeftMargin = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.goneLeftMargin);
                                                                                                continue Block_21_Outer;
                                                                                                Log.e("ConstraintLayout", "layout_constraintHeight_default=\"wrap\" is deprecated.\nUse layout_height=\"WRAP_CONTENT\" and layout_constrainedHeight=\"true\" instead.");
                                                                                                continue Block_21_Outer;
                                                                                                this.matchConstraintPercentHeight = Math.max(0.0f, ((TypedArray)obtainStyledAttributes).getFloat(index, this.matchConstraintPercentHeight));
                                                                                                continue Block_21_Outer;
                                                                                                this.goneTopMargin = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.goneTopMargin);
                                                                                                continue Block_21_Outer;
                                                                                                this.guideBegin = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.guideBegin);
                                                                                                continue Block_21_Outer;
                                                                                                this.baselineToBaseline = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                continue Block_21_Outer;
                                                                                                this.matchConstraintPercentWidth = Math.max(0.0f, ((TypedArray)obtainStyledAttributes).getFloat(index, this.matchConstraintPercentWidth));
                                                                                                continue Block_21_Outer;
                                                                                                this.bottomToTop = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                continue Block_21_Outer;
                                                                                                resourceId3 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.rightToLeft);
                                                                                                this.rightToLeft = resourceId3;
                                                                                                continue Block_27_Outer;
                                                                                            }
                                                                                            ((TypedArray)obtainStyledAttributes).recycle();
                                                                                            this.validate();
                                                                                            return;
                                                                                            this.endToEnd = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                            continue Block_21_Outer;
                                                                                            this.verticalBias = ((TypedArray)obtainStyledAttributes).getFloat(index, this.verticalBias);
                                                                                            continue Block_21_Outer;
                                                                                            this.circleRadius = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.circleRadius);
                                                                                            continue Block_21_Outer;
                                                                                            this.orientation = ((TypedArray)obtainStyledAttributes).getInt(index, this.orientation);
                                                                                            continue Block_21_Outer;
                                                                                            try {
                                                                                                this.matchConstraintMinHeight = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.matchConstraintMinHeight);
                                                                                            }
                                                                                            catch (Exception ex) {
                                                                                                if (((TypedArray)obtainStyledAttributes).getInt(index, this.matchConstraintMinHeight) == -2) {
                                                                                                    this.matchConstraintMinHeight = -2;
                                                                                                }
                                                                                            }
                                                                                            continue Block_21_Outer;
                                                                                            try {
                                                                                                this.matchConstraintMaxWidth = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.matchConstraintMaxWidth);
                                                                                            }
                                                                                            catch (Exception ex2) {
                                                                                                if (((TypedArray)obtainStyledAttributes).getInt(index, this.matchConstraintMaxWidth) == -2) {
                                                                                                    this.matchConstraintMaxWidth = -2;
                                                                                                }
                                                                                            }
                                                                                            continue Block_21_Outer;
                                                                                            resourceId4 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.startToStart);
                                                                                            this.startToStart = resourceId4;
                                                                                            break Block_24;
                                                                                            this.leftToLeft = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                            continue Block_21_Outer;
                                                                                            resourceId5 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.circleConstraint);
                                                                                            this.circleConstraint = resourceId5;
                                                                                            Block_36: {
                                                                                                break Block_36;
                                                                                                this.goneRightMargin = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.goneRightMargin);
                                                                                                continue Block_21_Outer;
                                                                                                this.topToBottom = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                continue Block_21_Outer;
                                                                                                this.topToTop = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                                continue Block_21_Outer;
                                                                                                substring4 = this.dimensionRatio.substring(index2);
                                                                                                break Block_15;
                                                                                            }
                                                                                            this.circleConstraint = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                            continue Block_21_Outer;
                                                                                            this.rightToRight = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                            continue Block_21_Outer;
                                                                                        }
                                                                                        this.leftToRight = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                                        continue Block_21_Outer;
                                                                                        this.guidePercent = ((TypedArray)obtainStyledAttributes).getFloat(index, this.guidePercent);
                                                                                        continue Block_21_Outer;
                                                                                        this.guideEnd = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(index, this.guideEnd);
                                                                                        continue Block_21_Outer;
                                                                                        int1 = ((TypedArray)obtainStyledAttributes).getInt(index, 0);
                                                                                        this.matchConstraintDefaultHeight = int1;
                                                                                        continue Block_26_Outer;
                                                                                    }
                                                                                    try {
                                                                                        this.matchConstraintMaxHeight = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.matchConstraintMaxHeight);
                                                                                    }
                                                                                    catch (Exception ex3) {
                                                                                        if (((TypedArray)obtainStyledAttributes).getInt(index, this.matchConstraintMaxHeight) == -2) {
                                                                                            this.matchConstraintMaxHeight = -2;
                                                                                        }
                                                                                    }
                                                                                    continue Block_21_Outer;
                                                                                }
                                                                                Float.parseFloat(substring4);
                                                                                continue Block_21_Outer;
                                                                                this.constrainedHeight = ((TypedArray)obtainStyledAttributes).getBoolean(index, this.constrainedHeight);
                                                                                continue Block_21_Outer;
                                                                                resourceId6 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.topToTop);
                                                                                this.topToTop = resourceId6;
                                                                                continue Block_31_Outer;
                                                                            }
                                                                            resourceId7 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.leftToLeft);
                                                                            this.leftToLeft = resourceId7;
                                                                            continue Block_29_Outer;
                                                                        }
                                                                        try {
                                                                            this.matchConstraintMinWidth = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.matchConstraintMinWidth);
                                                                        }
                                                                        catch (Exception ex4) {
                                                                            if (((TypedArray)obtainStyledAttributes).getInt(index, this.matchConstraintMinWidth) == -2) {
                                                                                this.matchConstraintMinWidth = -2;
                                                                            }
                                                                        }
                                                                        continue Block_21_Outer;
                                                                    }
                                                                    this.startToStart = ((TypedArray)obtainStyledAttributes).getInt(index, -1);
                                                                    continue Block_21_Outer;
                                                                    resourceId8 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.endToEnd);
                                                                    this.endToEnd = resourceId8;
                                                                    continue Block_29_Outer;
                                                                }
                                                                this.goneBottomMargin = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.goneBottomMargin);
                                                                continue Block_21_Outer;
                                                                this.goneStartMargin = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.goneStartMargin);
                                                                continue Block_21_Outer;
                                                                resourceId9 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.bottomToTop);
                                                                this.bottomToTop = resourceId9;
                                                                continue Block_22_Outer;
                                                            }
                                                            this.goneEndMargin = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(index, this.goneEndMargin);
                                                            continue Block_21_Outer;
                                                            int2 = ((TypedArray)obtainStyledAttributes).getInt(index, 0);
                                                            this.matchConstraintDefaultWidth = int2;
                                                            continue Block_32_Outer;
                                                        }
                                                        resourceId10 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.startToEnd);
                                                        this.startToEnd = resourceId10;
                                                        continue Block_26_Outer;
                                                    }
                                                    resourceId11 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.baselineToBaseline);
                                                    this.baselineToBaseline = resourceId11;
                                                    continue Block_29_Outer;
                                                }
                                                resourceId12 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.rightToRight);
                                                this.rightToRight = resourceId12;
                                                continue;
                                            }
                                            resourceId13 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.bottomToBottom);
                                            this.bottomToBottom = resourceId13;
                                            continue Block_25_Outer;
                                        }
                                        resourceId14 = ((TypedArray)obtainStyledAttributes).getResourceId(index, this.topToBottom);
                                        this.topToBottom = resourceId14;
                                        continue;
                                    }
                                }
                                this.circleAngle = (360.0f - circleAngle) % 360.0f;
                                continue Block_21_Outer;
                            }
                            catch (NumberFormatException ex5) {
                                continue;
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = -1.0f;
            this.verticalWeight = -1.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }
        
        @TargetApi(17)
        public void resolveLayoutDirection(int resolvedLeftToRight) {
            final int leftMargin = super.leftMargin;
            final int rightMargin = super.rightMargin;
            super.resolveLayoutDirection(resolvedLeftToRight);
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolveGoneLeftMargin = this.goneLeftMargin;
            this.resolveGoneRightMargin = this.goneRightMargin;
            this.resolvedHorizontalBias = this.horizontalBias;
            this.resolvedGuideBegin = this.guideBegin;
            this.resolvedGuideEnd = this.guideEnd;
            this.resolvedGuidePercent = this.guidePercent;
            resolvedLeftToRight = this.getLayoutDirection();
            final int n = 0;
            if (1 == resolvedLeftToRight) {
                resolvedLeftToRight = 1;
            }
            else {
                resolvedLeftToRight = 0;
            }
            if (resolvedLeftToRight != 0) {
                resolvedLeftToRight = this.startToEnd;
                Label_0161: {
                    if (resolvedLeftToRight != -1) {
                        this.resolvedRightToLeft = resolvedLeftToRight;
                    }
                    else {
                        final int startToStart = this.startToStart;
                        resolvedLeftToRight = n;
                        if (startToStart == -1) {
                            break Label_0161;
                        }
                        this.resolvedRightToRight = startToStart;
                    }
                    resolvedLeftToRight = 1;
                }
                final int endToStart = this.endToStart;
                if (endToStart != -1) {
                    this.resolvedLeftToRight = endToStart;
                    resolvedLeftToRight = 1;
                }
                final int endToEnd = this.endToEnd;
                if (endToEnd != -1) {
                    this.resolvedLeftToLeft = endToEnd;
                    resolvedLeftToRight = 1;
                }
                final int goneStartMargin = this.goneStartMargin;
                if (goneStartMargin != -1) {
                    this.resolveGoneRightMargin = goneStartMargin;
                }
                final int goneEndMargin = this.goneEndMargin;
                if (goneEndMargin != -1) {
                    this.resolveGoneLeftMargin = goneEndMargin;
                }
                if (resolvedLeftToRight != 0) {
                    this.resolvedHorizontalBias = 1.0f - this.horizontalBias;
                }
                if (this.isGuideline && this.orientation == 1) {
                    final float guidePercent = this.guidePercent;
                    if (guidePercent != -1.0f) {
                        this.resolvedGuidePercent = 1.0f - guidePercent;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuideEnd = -1;
                    }
                    else {
                        resolvedLeftToRight = this.guideBegin;
                        if (resolvedLeftToRight != -1) {
                            this.resolvedGuideEnd = resolvedLeftToRight;
                            this.resolvedGuideBegin = -1;
                            this.resolvedGuidePercent = -1.0f;
                        }
                        else {
                            resolvedLeftToRight = this.guideEnd;
                            if (resolvedLeftToRight != -1) {
                                this.resolvedGuideBegin = resolvedLeftToRight;
                                this.resolvedGuideEnd = -1;
                                this.resolvedGuidePercent = -1.0f;
                            }
                        }
                    }
                }
            }
            else {
                resolvedLeftToRight = this.startToEnd;
                if (resolvedLeftToRight != -1) {
                    this.resolvedLeftToRight = resolvedLeftToRight;
                }
                resolvedLeftToRight = this.startToStart;
                if (resolvedLeftToRight != -1) {
                    this.resolvedLeftToLeft = resolvedLeftToRight;
                }
                resolvedLeftToRight = this.endToStart;
                if (resolvedLeftToRight != -1) {
                    this.resolvedRightToLeft = resolvedLeftToRight;
                }
                resolvedLeftToRight = this.endToEnd;
                if (resolvedLeftToRight != -1) {
                    this.resolvedRightToRight = resolvedLeftToRight;
                }
                resolvedLeftToRight = this.goneStartMargin;
                if (resolvedLeftToRight != -1) {
                    this.resolveGoneLeftMargin = resolvedLeftToRight;
                }
                resolvedLeftToRight = this.goneEndMargin;
                if (resolvedLeftToRight != -1) {
                    this.resolveGoneRightMargin = resolvedLeftToRight;
                }
            }
            if (this.endToStart == -1 && this.endToEnd == -1 && this.startToStart == -1 && this.startToEnd == -1) {
                resolvedLeftToRight = this.rightToLeft;
                if (resolvedLeftToRight != -1) {
                    this.resolvedRightToLeft = resolvedLeftToRight;
                    if (super.rightMargin <= 0 && rightMargin > 0) {
                        super.rightMargin = rightMargin;
                    }
                }
                else {
                    resolvedLeftToRight = this.rightToRight;
                    if (resolvedLeftToRight != -1) {
                        this.resolvedRightToRight = resolvedLeftToRight;
                        if (super.rightMargin <= 0 && rightMargin > 0) {
                            super.rightMargin = rightMargin;
                        }
                    }
                }
                resolvedLeftToRight = this.leftToLeft;
                if (resolvedLeftToRight != -1) {
                    this.resolvedLeftToLeft = resolvedLeftToRight;
                    if (super.leftMargin <= 0 && leftMargin > 0) {
                        super.leftMargin = leftMargin;
                    }
                }
                else {
                    resolvedLeftToRight = this.leftToRight;
                    if (resolvedLeftToRight != -1) {
                        this.resolvedLeftToRight = resolvedLeftToRight;
                        if (super.leftMargin <= 0 && leftMargin > 0) {
                            super.leftMargin = leftMargin;
                        }
                    }
                }
            }
        }
        
        public void validate() {
            this.isGuideline = false;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            if (super.width == -2 && this.constrainedWidth) {
                this.horizontalDimensionFixed = false;
                this.matchConstraintDefaultWidth = 1;
            }
            if (super.height == -2 && this.constrainedHeight) {
                this.verticalDimensionFixed = false;
                this.matchConstraintDefaultHeight = 1;
            }
            final int width = super.width;
            if (width == 0 || width == -1) {
                this.horizontalDimensionFixed = false;
                if (super.width == 0 && this.matchConstraintDefaultWidth == 1) {
                    super.width = -2;
                    this.constrainedWidth = true;
                }
            }
            final int height = super.height;
            if (height == 0 || height == -1) {
                this.verticalDimensionFixed = false;
                if (super.height == 0 && this.matchConstraintDefaultHeight == 1) {
                    super.height = -2;
                    this.constrainedHeight = true;
                }
            }
            if (this.guidePercent != -1.0f || this.guideBegin != -1 || this.guideEnd != -1) {
                this.isGuideline = true;
                this.horizontalDimensionFixed = true;
                this.verticalDimensionFixed = true;
                if (!(this.widget instanceof androidx.constraintlayout.solver.widgets.Guideline)) {
                    this.widget = new androidx.constraintlayout.solver.widgets.Guideline();
                }
                ((androidx.constraintlayout.solver.widgets.Guideline)this.widget).setOrientation(this.orientation);
            }
        }
        
        private static class Table
        {
            public static final SparseIntArray map;
            
            static {
                (map = new SparseIntArray()).append(R$styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
                Table.map.append(R$styleable.ConstraintLayout_Layout_android_orientation, 1);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
                Table.map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
            }
        }
    }
}
