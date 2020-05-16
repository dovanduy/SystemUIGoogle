// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Cache;
import androidx.constraintlayout.solver.ArrayRow;
import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.constraintlayout.solver.LinearSystem;
import java.util.ArrayList;

public class ConstraintWidget
{
    public static float DEFAULT_BIAS = 0.5f;
    protected ArrayList<ConstraintAnchor> mAnchors;
    ConstraintAnchor mBaseline;
    int mBaselineDistance;
    ConstraintAnchor mBottom;
    ConstraintAnchor mCenter;
    ConstraintAnchor mCenterX;
    ConstraintAnchor mCenterY;
    private float mCircleConstraintAngle;
    private Object mCompanionWidget;
    private String mDebugName;
    protected float mDimensionRatio;
    protected int mDimensionRatioSide;
    private int mDrawX;
    private int mDrawY;
    int mHeight;
    float mHorizontalBiasPercent;
    int mHorizontalChainStyle;
    public int mHorizontalResolution;
    ConstraintAnchor mLeft;
    protected ConstraintAnchor[] mListAnchors;
    protected DimensionBehaviour[] mListDimensionBehaviors;
    protected ConstraintWidget[] mListNextMatchConstraintsWidget;
    protected ConstraintWidget[] mListNextVisibleWidget;
    int mMatchConstraintDefaultHeight;
    int mMatchConstraintDefaultWidth;
    int mMatchConstraintMaxHeight;
    int mMatchConstraintMaxWidth;
    int mMatchConstraintMinHeight;
    int mMatchConstraintMinWidth;
    float mMatchConstraintPercentHeight;
    float mMatchConstraintPercentWidth;
    private int[] mMaxDimension;
    protected int mMinHeight;
    protected int mMinWidth;
    protected int mOffsetX;
    protected int mOffsetY;
    ConstraintWidget mParent;
    ResolutionDimension mResolutionHeight;
    ResolutionDimension mResolutionWidth;
    float mResolvedDimensionRatio;
    int mResolvedDimensionRatioSide;
    int[] mResolvedMatchConstraintDefault;
    ConstraintAnchor mRight;
    ConstraintAnchor mTop;
    private String mType;
    float mVerticalBiasPercent;
    int mVerticalChainStyle;
    public int mVerticalResolution;
    private int mVisibility;
    float[] mWeight;
    int mWidth;
    private int mWrapHeight;
    private int mWrapWidth;
    protected int mX;
    protected int mY;
    
    public ConstraintWidget() {
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mResolvedMatchConstraintDefault = new int[2];
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMaxWidth = 0;
        this.mMatchConstraintPercentWidth = 1.0f;
        this.mMatchConstraintMinHeight = 0;
        this.mMatchConstraintMaxHeight = 0;
        this.mMatchConstraintPercentHeight = 1.0f;
        this.mResolvedDimensionRatioSide = -1;
        this.mResolvedDimensionRatio = 1.0f;
        this.mMaxDimension = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE };
        this.mCircleConstraintAngle = 0.0f;
        this.mLeft = new ConstraintAnchor(this, ConstraintAnchor.Type.LEFT);
        this.mTop = new ConstraintAnchor(this, ConstraintAnchor.Type.TOP);
        this.mRight = new ConstraintAnchor(this, ConstraintAnchor.Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, ConstraintAnchor.Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, ConstraintAnchor.Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_Y);
        final ConstraintAnchor mCenter = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER);
        this.mCenter = mCenter;
        this.mListAnchors = new ConstraintAnchor[] { this.mLeft, this.mRight, this.mTop, this.mBottom, this.mBaseline, mCenter };
        this.mAnchors = new ArrayList<ConstraintAnchor>();
        final DimensionBehaviour fixed = DimensionBehaviour.FIXED;
        this.mListDimensionBehaviors = new DimensionBehaviour[] { fixed, fixed };
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mX = 0;
        this.mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        final float default_BIAS = ConstraintWidget.DEFAULT_BIAS;
        this.mHorizontalBiasPercent = default_BIAS;
        this.mVerticalBiasPercent = default_BIAS;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mWeight = new float[] { -1.0f, -1.0f };
        this.mListNextMatchConstraintsWidget = new ConstraintWidget[] { null, null };
        this.mListNextVisibleWidget = new ConstraintWidget[] { null, null };
        this.addAnchors();
    }
    
    private void addAnchors() {
        this.mAnchors.add(this.mLeft);
        this.mAnchors.add(this.mTop);
        this.mAnchors.add(this.mRight);
        this.mAnchors.add(this.mBottom);
        this.mAnchors.add(this.mCenterX);
        this.mAnchors.add(this.mCenterY);
        this.mAnchors.add(this.mCenter);
        this.mAnchors.add(this.mBaseline);
    }
    
    private void applyConstraints(final LinearSystem linearSystem, final boolean b, final SolverVariable solverVariable, final SolverVariable solverVariable2, final DimensionBehaviour dimensionBehaviour, final boolean b2, final ConstraintAnchor constraintAnchor, final ConstraintAnchor constraintAnchor2, int n, int n2, int n3, int n4, final float n5, final boolean b3, final boolean b4, int n6, int n7, int min, final float n8, final boolean b5) {
        final SolverVariable objectVariable = linearSystem.createObjectVariable(constraintAnchor);
        final SolverVariable objectVariable2 = linearSystem.createObjectVariable(constraintAnchor2);
        final SolverVariable objectVariable3 = linearSystem.createObjectVariable(constraintAnchor.getTarget());
        final SolverVariable objectVariable4 = linearSystem.createObjectVariable(constraintAnchor2.getTarget());
        if (linearSystem.graphOptimizer && constraintAnchor.getResolutionNode().state == 1 && constraintAnchor2.getResolutionNode().state == 1) {
            if (LinearSystem.getMetrics() != null) {
                final Metrics metrics = LinearSystem.getMetrics();
                ++metrics.resolvedWidgets;
            }
            constraintAnchor.getResolutionNode().addResolvedValue(linearSystem);
            constraintAnchor2.getResolutionNode().addResolvedValue(linearSystem);
            if (!b4 && b) {
                linearSystem.addGreaterThan(solverVariable2, objectVariable2, 0, 6);
            }
            return;
        }
        if (LinearSystem.getMetrics() != null) {
            final Metrics metrics2 = LinearSystem.getMetrics();
            ++metrics2.nonresolvedWidgets;
        }
        final boolean connected = constraintAnchor.isConnected();
        final boolean connected2 = constraintAnchor2.isConnected();
        final boolean connected3 = this.mCenter.isConnected();
        int n9;
        if (connected) {
            n9 = 1;
        }
        else {
            n9 = 0;
        }
        int n10 = n9;
        if (connected2) {
            n10 = n9 + 1;
        }
        int n11 = n10;
        if (connected3) {
            n11 = n10 + 1;
        }
        int n12;
        if (b3) {
            n12 = 3;
        }
        else {
            n12 = n6;
        }
        n6 = ConstraintWidget$1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour[dimensionBehaviour.ordinal()];
        if (n6 != 1 && n6 != 2 && n6 != 3 && n6 == 4 && n12 != 4) {
            n6 = 1;
        }
        else {
            n6 = 0;
        }
        if (this.mVisibility == 8) {
            n2 = 0;
            n6 = 0;
        }
        if (b5) {
            if (!connected && !connected2 && !connected3) {
                linearSystem.addEquality(objectVariable, n);
            }
            else if (connected && !connected2) {
                linearSystem.addEquality(objectVariable, objectVariable3, constraintAnchor.getMargin(), 6);
            }
        }
        if (n6 == 0) {
            if (b2) {
                linearSystem.addEquality(objectVariable2, objectVariable, 0, 3);
                if (n3 > 0) {
                    linearSystem.addGreaterThan(objectVariable2, objectVariable, n3, 6);
                }
                if (n4 < Integer.MAX_VALUE) {
                    linearSystem.addLowerThan(objectVariable2, objectVariable, n4, 6);
                }
            }
            else {
                linearSystem.addEquality(objectVariable2, objectVariable, n2, 6);
            }
            n2 = n6;
            n4 = n7;
            n7 = min;
        }
        else {
            if ((n4 = n7) == -2) {
                n4 = n2;
            }
            if ((n = min) == -2) {
                n = n2;
            }
            if (n4 > 0) {
                if (b) {
                    linearSystem.addGreaterThan(objectVariable2, objectVariable, n4, 6);
                }
                else {
                    linearSystem.addGreaterThan(objectVariable2, objectVariable, n4, 6);
                }
                n2 = Math.max(n2, n4);
            }
            min = n2;
            if (n > 0) {
                if (b) {
                    linearSystem.addLowerThan(objectVariable2, objectVariable, n, 1);
                }
                else {
                    linearSystem.addLowerThan(objectVariable2, objectVariable, n, 6);
                }
                min = Math.min(n2, n);
            }
            if (n12 == 1) {
                if (b) {
                    linearSystem.addEquality(objectVariable2, objectVariable, min, 6);
                }
                else if (b4) {
                    linearSystem.addEquality(objectVariable2, objectVariable, min, 4);
                }
                else {
                    linearSystem.addEquality(objectVariable2, objectVariable, min, 1);
                }
            }
            else if (n12 == 2) {
                SolverVariable solverVariable3;
                SolverVariable solverVariable4;
                if (constraintAnchor.getType() != ConstraintAnchor.Type.TOP && constraintAnchor.getType() != ConstraintAnchor.Type.BOTTOM) {
                    solverVariable3 = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.LEFT));
                    solverVariable4 = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.RIGHT));
                }
                else {
                    solverVariable3 = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.TOP));
                    solverVariable4 = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.BOTTOM));
                }
                final ArrayRow row = linearSystem.createRow();
                row.createRowDimensionRatio(objectVariable2, objectVariable, solverVariable4, solverVariable3, n8);
                linearSystem.addConstraint(row);
                n6 = 0;
            }
            final int a = n4;
            n2 = n6;
            n4 = a;
            n7 = n;
            if (n6 != 0) {
                n2 = n6;
                n4 = a;
                n7 = n;
                if (n11 != 2) {
                    n2 = n6;
                    n4 = a;
                    n7 = n;
                    if (!b3) {
                        n4 = (n2 = Math.max(a, min));
                        if (n > 0) {
                            n2 = Math.min(n, n4);
                        }
                        linearSystem.addEquality(objectVariable2, objectVariable, n2, 6);
                        n2 = 0;
                        n7 = n;
                        n4 = a;
                    }
                }
            }
        }
        final SolverVariable solverVariable5 = objectVariable3;
        final SolverVariable solverVariable6 = objectVariable4;
        if (b5 && !b4) {
            Label_1428: {
                Label_1424: {
                    if (!connected && !connected2 && !connected3) {
                        if (!b) {
                            n = 0;
                            break Label_1424;
                        }
                        linearSystem.addGreaterThan(solverVariable2, objectVariable2, 0, 5);
                    }
                    else if (connected && !connected2) {
                        if (b) {
                            linearSystem.addGreaterThan(solverVariable2, objectVariable2, 0, 5);
                        }
                    }
                    else if (!connected && connected2) {
                        linearSystem.addEquality(objectVariable2, solverVariable6, -constraintAnchor2.getMargin(), 6);
                        if (b) {
                            linearSystem.addGreaterThan(objectVariable, solverVariable, 0, 5);
                        }
                    }
                    else if (connected && connected2) {
                        if (n2 != 0) {
                            final SolverVariable solverVariable7 = solverVariable6;
                            if (b && n3 == 0) {
                                linearSystem.addGreaterThan(objectVariable2, objectVariable, 0, 6);
                            }
                            if (n12 == 0) {
                                if (n7 <= 0 && n4 <= 0) {
                                    n = 0;
                                    n2 = 6;
                                }
                                else {
                                    n2 = 4;
                                    n = 1;
                                }
                                linearSystem.addEquality(objectVariable, solverVariable5, constraintAnchor.getMargin(), n2);
                                linearSystem.addEquality(objectVariable2, solverVariable7, -constraintAnchor2.getMargin(), n2);
                                if (n7 <= 0 && n4 <= 0) {
                                    n2 = 0;
                                }
                                else {
                                    n2 = 1;
                                }
                                n4 = 5;
                                n3 = n;
                                n = n2;
                                n2 = n3;
                                n3 = n4;
                            }
                            else if (n12 == 1) {
                                n = (n2 = 1);
                                n3 = 6;
                            }
                            else {
                                if (n12 == 3) {
                                    if (!b3 && this.mResolvedDimensionRatioSide != -1 && n7 <= 0) {
                                        n = 6;
                                    }
                                    else {
                                        n = 4;
                                    }
                                    linearSystem.addEquality(objectVariable, solverVariable5, constraintAnchor.getMargin(), n);
                                    linearSystem.addEquality(objectVariable2, solverVariable7, -constraintAnchor2.getMargin(), n);
                                    n = 1;
                                }
                                else {
                                    n = 0;
                                }
                                n3 = 5;
                                n2 = n;
                            }
                        }
                        else {
                            if (b) {
                                linearSystem.addGreaterThan(objectVariable, solverVariable5, constraintAnchor.getMargin(), 5);
                                linearSystem.addLowerThan(objectVariable2, solverVariable6, -constraintAnchor2.getMargin(), 5);
                            }
                            n3 = 5;
                            n = 1;
                            n2 = 0;
                        }
                        if (n != 0) {
                            linearSystem.addCentering(objectVariable, solverVariable5, constraintAnchor.getMargin(), n5, solverVariable6, objectVariable2, constraintAnchor2.getMargin(), n3);
                        }
                        n3 = 6;
                        if (n2 != 0) {
                            linearSystem.addGreaterThan(objectVariable, solverVariable5, constraintAnchor.getMargin(), n3);
                            linearSystem.addLowerThan(objectVariable2, solverVariable6, -constraintAnchor2.getMargin(), n3);
                        }
                        if (b) {
                            n = 0;
                            linearSystem.addGreaterThan(objectVariable, solverVariable, 0, n3);
                            n2 = n3;
                            break Label_1428;
                        }
                        n = 0;
                        n2 = n3;
                        break Label_1428;
                    }
                    n = 0;
                }
                n2 = 6;
            }
            if (b) {
                linearSystem.addGreaterThan(solverVariable2, objectVariable2, n, n2);
            }
            return;
        }
        if (n11 < 2 && b) {
            linearSystem.addGreaterThan(objectVariable, solverVariable, 0, 6);
            linearSystem.addGreaterThan(solverVariable2, objectVariable2, 0, 6);
        }
    }
    
    public void addToSolver(final LinearSystem linearSystem) {
        final SolverVariable objectVariable = linearSystem.createObjectVariable(this.mLeft);
        final SolverVariable objectVariable2 = linearSystem.createObjectVariable(this.mRight);
        final SolverVariable objectVariable3 = linearSystem.createObjectVariable(this.mTop);
        final SolverVariable objectVariable4 = linearSystem.createObjectVariable(this.mBottom);
        final SolverVariable objectVariable5 = linearSystem.createObjectVariable(this.mBaseline);
        final ConstraintWidget mParent = this.mParent;
        boolean b5;
        boolean b6;
        int n2;
        boolean b7;
        if (mParent != null) {
            final boolean b = mParent != null && mParent.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT;
            final ConstraintWidget mParent2 = this.mParent;
            final boolean b2 = mParent2 != null && mParent2.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT;
            final ConstraintAnchor mLeft = this.mLeft;
            final ConstraintAnchor mTarget = mLeft.mTarget;
            if (mTarget != null && mTarget.mTarget != mLeft) {
                final ConstraintAnchor mRight = this.mRight;
                final ConstraintAnchor mTarget2 = mRight.mTarget;
                if (mTarget2 != null && mTarget2.mTarget == mRight) {
                    ((ConstraintWidgetContainer)this.mParent).addChain(this, 0);
                }
            }
            final ConstraintAnchor mLeft2 = this.mLeft;
            final ConstraintAnchor mTarget3 = mLeft2.mTarget;
            boolean b3 = false;
            Label_0252: {
                if (mTarget3 == null || mTarget3.mTarget != mLeft2) {
                    final ConstraintAnchor mRight2 = this.mRight;
                    final ConstraintAnchor mTarget4 = mRight2.mTarget;
                    if (mTarget4 == null || mTarget4.mTarget != mRight2) {
                        b3 = false;
                        break Label_0252;
                    }
                }
                b3 = true;
            }
            final ConstraintAnchor mTop = this.mTop;
            final ConstraintAnchor mTarget5 = mTop.mTarget;
            if (mTarget5 != null && mTarget5.mTarget != mTop) {
                final ConstraintAnchor mBottom = this.mBottom;
                final ConstraintAnchor mTarget6 = mBottom.mTarget;
                if (mTarget6 != null && mTarget6.mTarget == mBottom) {
                    ((ConstraintWidgetContainer)this.mParent).addChain(this, 1);
                }
            }
            final ConstraintAnchor mTop2 = this.mTop;
            final ConstraintAnchor mTarget7 = mTop2.mTarget;
            boolean b4 = false;
            Label_0385: {
                if (mTarget7 == null || mTarget7.mTarget != mTop2) {
                    final ConstraintAnchor mBottom2 = this.mBottom;
                    final ConstraintAnchor mTarget8 = mBottom2.mTarget;
                    if (mTarget8 == null || mTarget8.mTarget != mBottom2) {
                        b4 = false;
                        break Label_0385;
                    }
                }
                b4 = true;
            }
            if (b && this.mVisibility != 8 && this.mLeft.mTarget == null && this.mRight.mTarget == null) {
                linearSystem.addGreaterThan(linearSystem.createObjectVariable(this.mParent.mRight), objectVariable2, 0, 1);
            }
            if (b2 && this.mVisibility != 8 && this.mTop.mTarget == null && this.mBottom.mTarget == null && this.mBaseline == null) {
                linearSystem.addGreaterThan(linearSystem.createObjectVariable(this.mParent.mBottom), objectVariable4, 0, 1);
            }
            final int n = b2 ? 1 : 0;
            b5 = b3;
            b6 = b4;
            n2 = (b ? 1 : 0);
            b7 = (n != 0);
        }
        else {
            n2 = ((b7 = false) ? 1 : 0);
            b5 = (b6 = b7);
        }
        final int mWidth = this.mWidth;
        final int mMinWidth = this.mMinWidth;
        int n3 = mWidth;
        if (mWidth < mMinWidth) {
            n3 = mMinWidth;
        }
        final int mHeight = this.mHeight;
        final int mMinHeight = this.mMinHeight;
        int n4;
        if ((n4 = mHeight) < mMinHeight) {
            n4 = mMinHeight;
        }
        final boolean b8 = this.mListDimensionBehaviors[0] != DimensionBehaviour.MATCH_CONSTRAINT;
        final boolean b9 = this.mListDimensionBehaviors[1] != DimensionBehaviour.MATCH_CONSTRAINT;
        this.mResolvedDimensionRatioSide = this.mDimensionRatioSide;
        final float mDimensionRatio = this.mDimensionRatio;
        this.mResolvedDimensionRatio = mDimensionRatio;
        int mMatchConstraintDefaultWidth = this.mMatchConstraintDefaultWidth;
        final int mMatchConstraintDefaultHeight = this.mMatchConstraintDefaultHeight;
        int n5 = 0;
        int n10 = 0;
        int n11 = 0;
        int n14 = 0;
        int n15 = 0;
        Label_1061: {
            int n9 = 0;
            Label_1046: {
                if (mDimensionRatio > 0.0f && this.mVisibility != 8) {
                    n5 = mMatchConstraintDefaultWidth;
                    if (this.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && (n5 = mMatchConstraintDefaultWidth) == 0) {
                        n5 = 3;
                    }
                    int n6 = mMatchConstraintDefaultHeight;
                    if (this.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && (n6 = mMatchConstraintDefaultHeight) == 0) {
                        n6 = 3;
                    }
                    final DimensionBehaviour[] mListDimensionBehaviors = this.mListDimensionBehaviors;
                    final DimensionBehaviour dimensionBehaviour = mListDimensionBehaviors[0];
                    final DimensionBehaviour match_CONSTRAINT = DimensionBehaviour.MATCH_CONSTRAINT;
                    int n18 = 0;
                    Label_1008: {
                        if (dimensionBehaviour == match_CONSTRAINT && mListDimensionBehaviors[1] == match_CONSTRAINT && n5 == 3 && n6 == 3) {
                            this.setupDimensionRatio((boolean)(n2 != 0), b7, b8, b9);
                        }
                        else {
                            final DimensionBehaviour[] mListDimensionBehaviors2 = this.mListDimensionBehaviors;
                            final DimensionBehaviour dimensionBehaviour2 = mListDimensionBehaviors2[0];
                            final DimensionBehaviour match_CONSTRAINT2 = DimensionBehaviour.MATCH_CONSTRAINT;
                            if (dimensionBehaviour2 == match_CONSTRAINT2 && n5 == 3) {
                                this.mResolvedDimensionRatioSide = 0;
                                final int n7 = (int)(this.mResolvedDimensionRatio * this.mHeight);
                                if (mListDimensionBehaviors2[1] != match_CONSTRAINT2) {
                                    final int n8 = n4;
                                    n9 = n6;
                                    mMatchConstraintDefaultWidth = 4;
                                    n10 = n7;
                                    n11 = n8;
                                    break Label_1046;
                                }
                                final int n12 = n4;
                                final int n13 = 1;
                                n14 = n6;
                                n10 = n7;
                                n11 = n12;
                                n15 = n13;
                                break Label_1061;
                            }
                            else if (this.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && n6 == 3) {
                                this.mResolvedDimensionRatioSide = 1;
                                if (this.mDimensionRatioSide == -1) {
                                    this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                                }
                                final int n16 = (int)(this.mResolvedDimensionRatio * this.mWidth);
                                final DimensionBehaviour dimensionBehaviour3 = this.mListDimensionBehaviors[0];
                                final DimensionBehaviour match_CONSTRAINT3 = DimensionBehaviour.MATCH_CONSTRAINT;
                                final int n17 = n5;
                                n10 = n3;
                                n18 = n16;
                                if (dimensionBehaviour3 != match_CONSTRAINT3) {
                                    n9 = 4;
                                    n11 = n16;
                                    mMatchConstraintDefaultWidth = n17;
                                    break Label_1046;
                                }
                                break Label_1008;
                            }
                        }
                        n18 = n4;
                    }
                    n10 = n3;
                    final int n19 = n6;
                    n15 = 1;
                    n11 = n18;
                    n14 = n19;
                    break Label_1061;
                }
                final int n20 = n3;
                n9 = mMatchConstraintDefaultHeight;
                n11 = n4;
                n10 = n20;
            }
            final int n21 = 0;
            n14 = n9;
            n5 = mMatchConstraintDefaultWidth;
            n15 = n21;
        }
        final int[] mResolvedMatchConstraintDefault = this.mResolvedMatchConstraintDefault;
        mResolvedMatchConstraintDefault[0] = n5;
        mResolvedMatchConstraintDefault[1] = n14;
        boolean b10 = false;
        Label_1110: {
            if (n15 != 0) {
                final int mResolvedDimensionRatioSide = this.mResolvedDimensionRatioSide;
                if (mResolvedDimensionRatioSide == 0 || mResolvedDimensionRatioSide == -1) {
                    b10 = true;
                    break Label_1110;
                }
            }
            b10 = false;
        }
        final boolean b11 = this.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT && this instanceof ConstraintWidgetContainer;
        final boolean b12 = this.mCenter.isConnected() ^ true;
        if (this.mHorizontalResolution != 2) {
            final ConstraintWidget mParent3 = this.mParent;
            SolverVariable objectVariable6;
            if (mParent3 != null) {
                objectVariable6 = linearSystem.createObjectVariable(mParent3.mRight);
            }
            else {
                objectVariable6 = null;
            }
            final ConstraintWidget mParent4 = this.mParent;
            SolverVariable objectVariable7;
            if (mParent4 != null) {
                objectVariable7 = linearSystem.createObjectVariable(mParent4.mLeft);
            }
            else {
                objectVariable7 = null;
            }
            this.applyConstraints(linearSystem, (boolean)(n2 != 0), objectVariable7, objectVariable6, this.mListDimensionBehaviors[0], b11, this.mLeft, this.mRight, this.mX, n10, this.mMinWidth, this.mMaxDimension[0], this.mHorizontalBiasPercent, b10, b5, n5, this.mMatchConstraintMinWidth, this.mMatchConstraintMaxWidth, this.mMatchConstraintPercentWidth, b12);
        }
        final SolverVariable solverVariable = objectVariable3;
        if (this.mVerticalResolution == 2) {
            return;
        }
        final boolean b13 = this.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT && this instanceof ConstraintWidgetContainer;
        boolean b14 = false;
        Label_1356: {
            if (n15 != 0) {
                final int mResolvedDimensionRatioSide2 = this.mResolvedDimensionRatioSide;
                if (mResolvedDimensionRatioSide2 == 1 || mResolvedDimensionRatioSide2 == -1) {
                    b14 = true;
                    break Label_1356;
                }
            }
            b14 = false;
        }
        boolean b15 = false;
        Label_1452: {
            if (this.mBaselineDistance > 0) {
                if (this.mBaseline.getResolutionNode().state == 1) {
                    this.mBaseline.getResolutionNode().addResolvedValue(linearSystem);
                }
                else {
                    linearSystem.addEquality(objectVariable5, solverVariable, this.getBaselineDistance(), 6);
                    final ConstraintAnchor mTarget9 = this.mBaseline.mTarget;
                    if (mTarget9 != null) {
                        linearSystem.addEquality(objectVariable5, linearSystem.createObjectVariable(mTarget9), 0, 6);
                        b15 = false;
                        break Label_1452;
                    }
                }
            }
            b15 = b12;
        }
        final SolverVariable solverVariable2 = solverVariable;
        final ConstraintWidget mParent5 = this.mParent;
        SolverVariable objectVariable8;
        if (mParent5 != null) {
            objectVariable8 = linearSystem.createObjectVariable(mParent5.mBottom);
        }
        else {
            objectVariable8 = null;
        }
        final ConstraintWidget mParent6 = this.mParent;
        SolverVariable objectVariable9;
        if (mParent6 != null) {
            objectVariable9 = linearSystem.createObjectVariable(mParent6.mTop);
        }
        else {
            objectVariable9 = null;
        }
        this.applyConstraints(linearSystem, b7, objectVariable9, objectVariable8, this.mListDimensionBehaviors[1], b13, this.mTop, this.mBottom, this.mY, n11, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, b14, b6, n14, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, b15);
        if (n15 != 0) {
            if (this.mResolvedDimensionRatioSide == 1) {
                linearSystem.addRatio(objectVariable4, solverVariable2, objectVariable2, objectVariable, this.mResolvedDimensionRatio, 6);
            }
            else {
                linearSystem.addRatio(objectVariable2, objectVariable, objectVariable4, solverVariable2, this.mResolvedDimensionRatio, 6);
            }
        }
        if (this.mCenter.isConnected()) {
            linearSystem.addCenterPoint(this, this.mCenter.getTarget().getOwner(), (float)Math.toRadians(this.mCircleConstraintAngle + 90.0f), this.mCenter.getMargin());
        }
    }
    
    public boolean allowedInBarrier() {
        return this.mVisibility != 8;
    }
    
    public void analyze(final int n) {
        Optimizer.analyze(n, this);
    }
    
    public void connectCircularConstraint(final ConstraintWidget constraintWidget, final float mCircleConstraintAngle, final int n) {
        final ConstraintAnchor.Type center = ConstraintAnchor.Type.CENTER;
        this.immediateConnect(center, constraintWidget, center, n, 0);
        this.mCircleConstraintAngle = mCircleConstraintAngle;
    }
    
    public void createObjectVariables(final LinearSystem linearSystem) {
        linearSystem.createObjectVariable(this.mLeft);
        linearSystem.createObjectVariable(this.mTop);
        linearSystem.createObjectVariable(this.mRight);
        linearSystem.createObjectVariable(this.mBottom);
        if (this.mBaselineDistance > 0) {
            linearSystem.createObjectVariable(this.mBaseline);
        }
    }
    
    public ConstraintAnchor getAnchor(final ConstraintAnchor.Type type) {
        switch (ConstraintWidget$1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[type.ordinal()]) {
            default: {
                throw new AssertionError((Object)type.name());
            }
            case 9: {
                return null;
            }
            case 8: {
                return this.mCenterY;
            }
            case 7: {
                return this.mCenterX;
            }
            case 6: {
                return this.mCenter;
            }
            case 5: {
                return this.mBaseline;
            }
            case 4: {
                return this.mBottom;
            }
            case 3: {
                return this.mRight;
            }
            case 2: {
                return this.mTop;
            }
            case 1: {
                return this.mLeft;
            }
        }
    }
    
    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }
    
    public int getBaselineDistance() {
        return this.mBaselineDistance;
    }
    
    public int getBottom() {
        return this.getY() + this.mHeight;
    }
    
    public Object getCompanionWidget() {
        return this.mCompanionWidget;
    }
    
    public String getDebugName() {
        return this.mDebugName;
    }
    
    public int getDrawX() {
        return this.mDrawX + this.mOffsetX;
    }
    
    public int getDrawY() {
        return this.mDrawY + this.mOffsetY;
    }
    
    public int getHeight() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mHeight;
    }
    
    public float getHorizontalBiasPercent() {
        return this.mHorizontalBiasPercent;
    }
    
    public DimensionBehaviour getHorizontalDimensionBehaviour() {
        return this.mListDimensionBehaviors[0];
    }
    
    public ConstraintWidget getParent() {
        return this.mParent;
    }
    
    public ResolutionDimension getResolutionHeight() {
        if (this.mResolutionHeight == null) {
            this.mResolutionHeight = new ResolutionDimension();
        }
        return this.mResolutionHeight;
    }
    
    public ResolutionDimension getResolutionWidth() {
        if (this.mResolutionWidth == null) {
            this.mResolutionWidth = new ResolutionDimension();
        }
        return this.mResolutionWidth;
    }
    
    public int getRight() {
        return this.getX() + this.mWidth;
    }
    
    protected int getRootX() {
        return this.mX + this.mOffsetX;
    }
    
    protected int getRootY() {
        return this.mY + this.mOffsetY;
    }
    
    public DimensionBehaviour getVerticalDimensionBehaviour() {
        return this.mListDimensionBehaviors[1];
    }
    
    public int getVisibility() {
        return this.mVisibility;
    }
    
    public int getWidth() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mWidth;
    }
    
    public int getWrapHeight() {
        return this.mWrapHeight;
    }
    
    public int getWrapWidth() {
        return this.mWrapWidth;
    }
    
    public int getX() {
        return this.mX;
    }
    
    public int getY() {
        return this.mY;
    }
    
    public boolean hasBaseline() {
        return this.mBaselineDistance > 0;
    }
    
    public void immediateConnect(final ConstraintAnchor.Type type, final ConstraintWidget constraintWidget, final ConstraintAnchor.Type type2, final int n, final int n2) {
        this.getAnchor(type).connect(constraintWidget.getAnchor(type2), n, n2, ConstraintAnchor.Strength.STRONG, 0, true);
    }
    
    public boolean isSpreadHeight() {
        final int mMatchConstraintDefaultHeight = this.mMatchConstraintDefaultHeight;
        boolean b = true;
        if (mMatchConstraintDefaultHeight != 0 || this.mDimensionRatio != 0.0f || this.mMatchConstraintMinHeight != 0 || this.mMatchConstraintMaxHeight != 0 || this.mListDimensionBehaviors[1] != DimensionBehaviour.MATCH_CONSTRAINT) {
            b = false;
        }
        return b;
    }
    
    public boolean isSpreadWidth() {
        final int mMatchConstraintDefaultWidth = this.mMatchConstraintDefaultWidth;
        boolean b2;
        final boolean b = b2 = false;
        if (mMatchConstraintDefaultWidth == 0) {
            b2 = b;
            if (this.mDimensionRatio == 0.0f) {
                b2 = b;
                if (this.mMatchConstraintMinWidth == 0) {
                    b2 = b;
                    if (this.mMatchConstraintMaxWidth == 0) {
                        b2 = b;
                        if (this.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT) {
                            b2 = true;
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    public void reset() {
        this.mLeft.reset();
        this.mTop.reset();
        this.mRight.reset();
        this.mBottom.reset();
        this.mBaseline.reset();
        this.mCenterX.reset();
        this.mCenterY.reset();
        this.mCenter.reset();
        this.mParent = null;
        this.mCircleConstraintAngle = 0.0f;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mX = 0;
        this.mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mWrapWidth = 0;
        this.mWrapHeight = 0;
        final float default_BIAS = ConstraintWidget.DEFAULT_BIAS;
        this.mHorizontalBiasPercent = default_BIAS;
        this.mVerticalBiasPercent = default_BIAS;
        final DimensionBehaviour[] mListDimensionBehaviors = this.mListDimensionBehaviors;
        mListDimensionBehaviors[1] = (mListDimensionBehaviors[0] = DimensionBehaviour.FIXED);
        this.mCompanionWidget = null;
        this.mVisibility = 0;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        final float[] mWeight = this.mWeight;
        mWeight[1] = (mWeight[0] = -1.0f);
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        final int[] mMaxDimension = this.mMaxDimension;
        mMaxDimension[1] = (mMaxDimension[0] = Integer.MAX_VALUE);
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mMatchConstraintPercentWidth = 1.0f;
        this.mMatchConstraintPercentHeight = 1.0f;
        this.mMatchConstraintMaxWidth = Integer.MAX_VALUE;
        this.mMatchConstraintMaxHeight = Integer.MAX_VALUE;
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMinHeight = 0;
        this.mResolvedDimensionRatioSide = -1;
        this.mResolvedDimensionRatio = 1.0f;
        final ResolutionDimension mResolutionWidth = this.mResolutionWidth;
        if (mResolutionWidth != null) {
            mResolutionWidth.reset();
        }
        final ResolutionDimension mResolutionHeight = this.mResolutionHeight;
        if (mResolutionHeight != null) {
            mResolutionHeight.reset();
        }
    }
    
    public void resetAnchors() {
        final ConstraintWidget parent = this.getParent();
        if (parent != null && parent instanceof ConstraintWidgetContainer && ((ConstraintWidgetContainer)this.getParent()).handlesInternalConstraints()) {
            return;
        }
        for (int i = 0; i < this.mAnchors.size(); ++i) {
            this.mAnchors.get(i).reset();
        }
    }
    
    public void resetResolutionNodes() {
        for (int i = 0; i < 6; ++i) {
            this.mListAnchors[i].getResolutionNode().reset();
        }
    }
    
    public void resetSolverVariables(final Cache cache) {
        this.mLeft.resetSolverVariable(cache);
        this.mTop.resetSolverVariable(cache);
        this.mRight.resetSolverVariable(cache);
        this.mBottom.resetSolverVariable(cache);
        this.mBaseline.resetSolverVariable(cache);
        this.mCenter.resetSolverVariable(cache);
        this.mCenterX.resetSolverVariable(cache);
        this.mCenterY.resetSolverVariable(cache);
    }
    
    public void resolve() {
    }
    
    public void setBaselineDistance(final int mBaselineDistance) {
        this.mBaselineDistance = mBaselineDistance;
    }
    
    public void setCompanionWidget(final Object mCompanionWidget) {
        this.mCompanionWidget = mCompanionWidget;
    }
    
    public void setDebugName(final String mDebugName) {
        this.mDebugName = mDebugName;
    }
    
    public void setDimensionRatio(String s) {
        Label_0263: {
            if (s == null || s.length() == 0) {
                break Label_0263;
            }
            final int n = -1;
            final int length = s.length();
            final int index = s.indexOf(44);
            final int n2 = 0;
            int mDimensionRatioSide = n;
            int n3 = n2;
            if (index > 0) {
                mDimensionRatioSide = n;
                n3 = n2;
                if (index < length - 1) {
                    final String substring = s.substring(0, index);
                    if (substring.equalsIgnoreCase("W")) {
                        mDimensionRatioSide = 0;
                    }
                    else {
                        mDimensionRatioSide = n;
                        if (substring.equalsIgnoreCase("H")) {
                            mDimensionRatioSide = 1;
                        }
                    }
                    n3 = index + 1;
                }
            }
            final int index2 = s.indexOf(58);
            Label_0217: {
                if (index2 < 0 || index2 >= length - 1) {
                    break Label_0217;
                }
                final String substring2 = s.substring(n3, index2);
                s = s.substring(index2 + 1);
                while (true) {
                    if (substring2.length() <= 0 || s.length() <= 0) {
                        break Label_0240;
                    }
                    try {
                        final float float1 = Float.parseFloat(substring2);
                        final float float2 = Float.parseFloat(s);
                        while (true) {
                            if (float1 > 0.0f && float2 > 0.0f) {
                                if (mDimensionRatioSide == 1) {
                                    final float mDimensionRatio = Math.abs(float2 / float1);
                                    break Label_0243;
                                }
                                final float mDimensionRatio = Math.abs(float1 / float2);
                                break Label_0243;
                            }
                            float mDimensionRatio = 0.0f;
                            if (mDimensionRatio > 0.0f) {
                                this.mDimensionRatio = mDimensionRatio;
                                this.mDimensionRatioSide = mDimensionRatioSide;
                            }
                            return;
                            this.mDimensionRatio = 0.0f;
                            return;
                            s = s.substring(n3);
                            mDimensionRatio = Float.parseFloat(s);
                            continue;
                        }
                    }
                    // iftrue(Label_0240:, s.length() <= 0)
                    catch (NumberFormatException ex) {
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    public void setFrame(int n, int n2, int n3, int mHeight) {
        final int n4 = n3 - n;
        n3 = mHeight - n2;
        this.mX = n;
        this.mY = n2;
        if (this.mVisibility == 8) {
            this.mWidth = 0;
            this.mHeight = 0;
            return;
        }
        n = n4;
        if (this.mListDimensionBehaviors[0] == DimensionBehaviour.FIXED) {
            n2 = this.mWidth;
            if ((n = n4) < n2) {
                n = n2;
            }
        }
        n2 = n3;
        if (this.mListDimensionBehaviors[1] == DimensionBehaviour.FIXED) {
            mHeight = this.mHeight;
            if ((n2 = n3) < mHeight) {
                n2 = mHeight;
            }
        }
        this.mWidth = n;
        this.mHeight = n2;
        n = this.mMinHeight;
        if (n2 < n) {
            this.mHeight = n;
        }
        n2 = this.mWidth;
        n = this.mMinWidth;
        if (n2 < n) {
            this.mWidth = n;
        }
    }
    
    public void setHeight(final int mHeight) {
        this.mHeight = mHeight;
        final int mMinHeight = this.mMinHeight;
        if (mHeight < mMinHeight) {
            this.mHeight = mMinHeight;
        }
    }
    
    public void setHeightWrapContent(final boolean b) {
    }
    
    public void setHorizontalBiasPercent(final float mHorizontalBiasPercent) {
        this.mHorizontalBiasPercent = mHorizontalBiasPercent;
    }
    
    public void setHorizontalChainStyle(final int mHorizontalChainStyle) {
        this.mHorizontalChainStyle = mHorizontalChainStyle;
    }
    
    public void setHorizontalDimension(int mMinWidth, int mWidth) {
        this.mX = mMinWidth;
        mWidth -= mMinWidth;
        this.mWidth = mWidth;
        mMinWidth = this.mMinWidth;
        if (mWidth < mMinWidth) {
            this.mWidth = mMinWidth;
        }
    }
    
    public void setHorizontalDimensionBehaviour(final DimensionBehaviour dimensionBehaviour) {
        this.mListDimensionBehaviors[0] = dimensionBehaviour;
        if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            this.setWidth(this.mWrapWidth);
        }
    }
    
    public void setHorizontalMatchStyle(final int mMatchConstraintDefaultWidth, final int mMatchConstraintMinWidth, final int mMatchConstraintMaxWidth, final float mMatchConstraintPercentWidth) {
        this.mMatchConstraintDefaultWidth = mMatchConstraintDefaultWidth;
        this.mMatchConstraintMinWidth = mMatchConstraintMinWidth;
        this.mMatchConstraintMaxWidth = mMatchConstraintMaxWidth;
        this.mMatchConstraintPercentWidth = mMatchConstraintPercentWidth;
        if (mMatchConstraintPercentWidth < 1.0f && mMatchConstraintDefaultWidth == 0) {
            this.mMatchConstraintDefaultWidth = 2;
        }
    }
    
    public void setHorizontalWeight(final float n) {
        this.mWeight[0] = n;
    }
    
    public void setMaxHeight(final int n) {
        this.mMaxDimension[1] = n;
    }
    
    public void setMaxWidth(final int n) {
        this.mMaxDimension[0] = n;
    }
    
    public void setMinHeight(final int mMinHeight) {
        if (mMinHeight < 0) {
            this.mMinHeight = 0;
        }
        else {
            this.mMinHeight = mMinHeight;
        }
    }
    
    public void setMinWidth(final int mMinWidth) {
        if (mMinWidth < 0) {
            this.mMinWidth = 0;
        }
        else {
            this.mMinWidth = mMinWidth;
        }
    }
    
    public void setOffset(final int mOffsetX, final int mOffsetY) {
        this.mOffsetX = mOffsetX;
        this.mOffsetY = mOffsetY;
    }
    
    public void setOrigin(final int mx, final int my) {
        this.mX = mx;
        this.mY = my;
    }
    
    public void setParent(final ConstraintWidget mParent) {
        this.mParent = mParent;
    }
    
    public void setVerticalBiasPercent(final float mVerticalBiasPercent) {
        this.mVerticalBiasPercent = mVerticalBiasPercent;
    }
    
    public void setVerticalChainStyle(final int mVerticalChainStyle) {
        this.mVerticalChainStyle = mVerticalChainStyle;
    }
    
    public void setVerticalDimension(int n, int mMinHeight) {
        this.mY = n;
        n = mMinHeight - n;
        this.mHeight = n;
        mMinHeight = this.mMinHeight;
        if (n < mMinHeight) {
            this.mHeight = mMinHeight;
        }
    }
    
    public void setVerticalDimensionBehaviour(final DimensionBehaviour dimensionBehaviour) {
        this.mListDimensionBehaviors[1] = dimensionBehaviour;
        if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            this.setHeight(this.mWrapHeight);
        }
    }
    
    public void setVerticalMatchStyle(final int mMatchConstraintDefaultHeight, final int mMatchConstraintMinHeight, final int mMatchConstraintMaxHeight, final float mMatchConstraintPercentHeight) {
        this.mMatchConstraintDefaultHeight = mMatchConstraintDefaultHeight;
        this.mMatchConstraintMinHeight = mMatchConstraintMinHeight;
        this.mMatchConstraintMaxHeight = mMatchConstraintMaxHeight;
        this.mMatchConstraintPercentHeight = mMatchConstraintPercentHeight;
        if (mMatchConstraintPercentHeight < 1.0f && mMatchConstraintDefaultHeight == 0) {
            this.mMatchConstraintDefaultHeight = 2;
        }
    }
    
    public void setVerticalWeight(final float n) {
        this.mWeight[1] = n;
    }
    
    public void setVisibility(final int mVisibility) {
        this.mVisibility = mVisibility;
    }
    
    public void setWidth(final int mWidth) {
        this.mWidth = mWidth;
        final int mMinWidth = this.mMinWidth;
        if (mWidth < mMinWidth) {
            this.mWidth = mMinWidth;
        }
    }
    
    public void setWidthWrapContent(final boolean b) {
    }
    
    public void setWrapHeight(final int mWrapHeight) {
        this.mWrapHeight = mWrapHeight;
    }
    
    public void setWrapWidth(final int mWrapWidth) {
        this.mWrapWidth = mWrapWidth;
    }
    
    public void setX(final int mx) {
        this.mX = mx;
    }
    
    public void setY(final int my) {
        this.mY = my;
    }
    
    public void setupDimensionRatio(final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        if (this.mResolvedDimensionRatioSide == -1) {
            if (b3 && !b4) {
                this.mResolvedDimensionRatioSide = 0;
            }
            else if (!b3 && b4) {
                this.mResolvedDimensionRatioSide = 1;
                if (this.mDimensionRatioSide == -1) {
                    this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                }
            }
        }
        if (this.mResolvedDimensionRatioSide == 0 && (!this.mTop.isConnected() || !this.mBottom.isConnected())) {
            this.mResolvedDimensionRatioSide = 1;
        }
        else if (this.mResolvedDimensionRatioSide == 1 && (!this.mLeft.isConnected() || !this.mRight.isConnected())) {
            this.mResolvedDimensionRatioSide = 0;
        }
        if (this.mResolvedDimensionRatioSide == -1 && (!this.mTop.isConnected() || !this.mBottom.isConnected() || !this.mLeft.isConnected() || !this.mRight.isConnected())) {
            if (this.mTop.isConnected() && this.mBottom.isConnected()) {
                this.mResolvedDimensionRatioSide = 0;
            }
            else if (this.mLeft.isConnected() && this.mRight.isConnected()) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1) {
            if (b && !b2) {
                this.mResolvedDimensionRatioSide = 0;
            }
            else if (!b && b2) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1) {
            if (this.mMatchConstraintMinWidth > 0 && this.mMatchConstraintMinHeight == 0) {
                this.mResolvedDimensionRatioSide = 0;
            }
            else if (this.mMatchConstraintMinWidth == 0 && this.mMatchConstraintMinHeight > 0) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1 && b && b2) {
            this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
            this.mResolvedDimensionRatioSide = 1;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String mType = this.mType;
        final String s = "";
        String string;
        if (mType != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("type: ");
            sb2.append(this.mType);
            sb2.append(" ");
            string = sb2.toString();
        }
        else {
            string = "";
        }
        sb.append(string);
        String string2 = s;
        if (this.mDebugName != null) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("id: ");
            sb3.append(this.mDebugName);
            sb3.append(" ");
            string2 = sb3.toString();
        }
        sb.append(string2);
        sb.append("(");
        sb.append(this.mX);
        sb.append(", ");
        sb.append(this.mY);
        sb.append(") - (");
        sb.append(this.mWidth);
        sb.append(" x ");
        sb.append(this.mHeight);
        sb.append(") wrap: (");
        sb.append(this.mWrapWidth);
        sb.append(" x ");
        sb.append(this.mWrapHeight);
        sb.append(")");
        return sb.toString();
    }
    
    public void updateDrawPosition() {
        final int mx = this.mX;
        final int my = this.mY;
        this.mDrawX = mx;
        this.mDrawY = my;
    }
    
    public void updateFromSolver(final LinearSystem linearSystem) {
        int objectVariableValue = linearSystem.getObjectVariableValue(this.mLeft);
        int objectVariableValue2 = linearSystem.getObjectVariableValue(this.mTop);
        int objectVariableValue3 = linearSystem.getObjectVariableValue(this.mRight);
        final int objectVariableValue4 = linearSystem.getObjectVariableValue(this.mBottom);
        int n;
        if (objectVariableValue3 - objectVariableValue < 0 || objectVariableValue4 - objectVariableValue2 < 0 || objectVariableValue == Integer.MIN_VALUE || objectVariableValue == Integer.MAX_VALUE || objectVariableValue2 == Integer.MIN_VALUE || objectVariableValue2 == Integer.MAX_VALUE || objectVariableValue3 == Integer.MIN_VALUE || objectVariableValue3 == Integer.MAX_VALUE || objectVariableValue4 == Integer.MIN_VALUE || (n = objectVariableValue4) == Integer.MAX_VALUE) {
            n = (objectVariableValue = 0);
            objectVariableValue2 = (objectVariableValue3 = objectVariableValue);
        }
        this.setFrame(objectVariableValue, objectVariableValue2, objectVariableValue3, n);
    }
    
    public void updateResolutionNodes() {
        for (int i = 0; i < 6; ++i) {
            this.mListAnchors[i].getResolutionNode().update();
        }
    }
    
    public enum DimensionBehaviour
    {
        FIXED, 
        MATCH_CONSTRAINT, 
        MATCH_PARENT, 
        WRAP_CONTENT;
    }
}
