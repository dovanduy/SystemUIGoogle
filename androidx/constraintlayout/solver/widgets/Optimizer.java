// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.LinearSystem;

public class Optimizer
{
    static boolean[] flags;
    
    static {
        Optimizer.flags = new boolean[3];
    }
    
    static void analyze(int n, final ConstraintWidget constraintWidget) {
        constraintWidget.updateResolutionNodes();
        final ResolutionAnchor resolutionNode = constraintWidget.mLeft.getResolutionNode();
        final ResolutionAnchor resolutionNode2 = constraintWidget.mTop.getResolutionNode();
        final ResolutionAnchor resolutionNode3 = constraintWidget.mRight.getResolutionNode();
        final ResolutionAnchor resolutionNode4 = constraintWidget.mBottom.getResolutionNode();
        if ((n & 0x8) == 0x8) {
            n = 1;
        }
        else {
            n = 0;
        }
        final boolean b = constraintWidget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget, 0);
        if (resolutionNode.type != 4 && resolutionNode3.type != 4) {
            if (constraintWidget.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.FIXED && (!b || constraintWidget.getVisibility() != 8)) {
                if (b) {
                    final int width = constraintWidget.getWidth();
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget == null) {
                        if (n != 0) {
                            resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                        }
                        else {
                            resolutionNode3.dependsOn(resolutionNode, width);
                        }
                    }
                    else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget == null) {
                        if (n != 0) {
                            resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                        }
                        else {
                            resolutionNode3.dependsOn(resolutionNode, width);
                        }
                    }
                    else if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget != null) {
                        if (n != 0) {
                            resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                        }
                        else {
                            resolutionNode.dependsOn(resolutionNode3, -width);
                        }
                    }
                    else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget != null) {
                        if (n != 0) {
                            constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                            constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                        }
                        if (constraintWidget.mDimensionRatio == 0.0f) {
                            resolutionNode.setType(3);
                            resolutionNode3.setType(3);
                            resolutionNode.setOpposite(resolutionNode3, 0.0f);
                            resolutionNode3.setOpposite(resolutionNode, 0.0f);
                        }
                        else {
                            resolutionNode.setType(2);
                            resolutionNode3.setType(2);
                            resolutionNode.setOpposite(resolutionNode3, (float)(-width));
                            resolutionNode3.setOpposite(resolutionNode, (float)width);
                            constraintWidget.setWidth(width);
                        }
                    }
                }
            }
            else if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget == null) {
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                if (n != 0) {
                    resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                }
                else {
                    resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                }
            }
            else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget == null) {
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                if (n != 0) {
                    resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                }
                else {
                    resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                }
            }
            else if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget != null) {
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                if (n != 0) {
                    resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                }
                else {
                    resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                }
            }
            else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget != null) {
                resolutionNode.setType(2);
                resolutionNode3.setType(2);
                if (n != 0) {
                    constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                    constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                    resolutionNode.setOpposite(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    resolutionNode3.setOpposite(resolutionNode, 1, constraintWidget.getResolutionWidth());
                }
                else {
                    resolutionNode.setOpposite(resolutionNode3, (float)(-constraintWidget.getWidth()));
                    resolutionNode3.setOpposite(resolutionNode, (float)constraintWidget.getWidth());
                }
            }
        }
        final boolean b2 = constraintWidget.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget, 1);
        if (resolutionNode2.type != 4 && resolutionNode4.type != 4) {
            if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.FIXED && (!b2 || constraintWidget.getVisibility() != 8)) {
                if (b2) {
                    final int height = constraintWidget.getHeight();
                    resolutionNode2.setType(1);
                    resolutionNode4.setType(1);
                    if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget == null) {
                        if (n != 0) {
                            resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                        }
                        else {
                            resolutionNode4.dependsOn(resolutionNode2, height);
                        }
                    }
                    else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget == null) {
                        if (n != 0) {
                            resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                        }
                        else {
                            resolutionNode4.dependsOn(resolutionNode2, height);
                        }
                    }
                    else if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget != null) {
                        if (n != 0) {
                            resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                        }
                        else {
                            resolutionNode2.dependsOn(resolutionNode4, -height);
                        }
                    }
                    else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget != null) {
                        if (n != 0) {
                            constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                            constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                        }
                        if (constraintWidget.mDimensionRatio == 0.0f) {
                            resolutionNode2.setType(3);
                            resolutionNode4.setType(3);
                            resolutionNode2.setOpposite(resolutionNode4, 0.0f);
                            resolutionNode4.setOpposite(resolutionNode2, 0.0f);
                        }
                        else {
                            resolutionNode2.setType(2);
                            resolutionNode4.setType(2);
                            resolutionNode2.setOpposite(resolutionNode4, (float)(-height));
                            resolutionNode4.setOpposite(resolutionNode2, (float)height);
                            constraintWidget.setHeight(height);
                            if (constraintWidget.mBaselineDistance > 0) {
                                constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                            }
                        }
                    }
                }
            }
            else if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget == null) {
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (n != 0) {
                    resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                }
                else {
                    resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                }
                final ConstraintAnchor mBaseline = constraintWidget.mBaseline;
                if (mBaseline.mTarget != null) {
                    mBaseline.getResolutionNode().setType(1);
                    resolutionNode2.dependsOn(1, constraintWidget.mBaseline.getResolutionNode(), -constraintWidget.mBaselineDistance);
                }
            }
            else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget == null) {
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (n != 0) {
                    resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                }
                else {
                    resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                }
                if (constraintWidget.mBaselineDistance > 0) {
                    constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                }
            }
            else if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget != null) {
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (n != 0) {
                    resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                }
                else {
                    resolutionNode2.dependsOn(resolutionNode4, -constraintWidget.getHeight());
                }
                if (constraintWidget.mBaselineDistance > 0) {
                    constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                }
            }
            else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget != null) {
                resolutionNode2.setType(2);
                resolutionNode4.setType(2);
                if (n != 0) {
                    resolutionNode2.setOpposite(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                    resolutionNode4.setOpposite(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                    constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                }
                else {
                    resolutionNode2.setOpposite(resolutionNode4, (float)(-constraintWidget.getHeight()));
                    resolutionNode4.setOpposite(resolutionNode2, (float)constraintWidget.getHeight());
                }
                if (constraintWidget.mBaselineDistance > 0) {
                    constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                }
            }
        }
    }
    
    static boolean applyChainOptimized(final ConstraintWidgetContainer constraintWidgetContainer, final LinearSystem linearSystem, final int n, final int n2, final ChainHead chainHead) {
        final ConstraintWidget mFirst = chainHead.mFirst;
        final ConstraintWidget mLast = chainHead.mLast;
        ConstraintWidget mFirstVisibleWidget = chainHead.mFirstVisibleWidget;
        final ConstraintWidget mLastVisibleWidget = chainHead.mLastVisibleWidget;
        final ConstraintWidget mHead = chainHead.mHead;
        final float mTotalWeight = chainHead.mTotalWeight;
        final ConstraintWidget mFirstMatchConstraintWidget = chainHead.mFirstMatchConstraintWidget;
        final ConstraintWidget mLastMatchConstraintWidget = chainHead.mLastMatchConstraintWidget;
        final ConstraintWidget.DimensionBehaviour dimensionBehaviour = constraintWidgetContainer.mListDimensionBehaviors[n];
        final ConstraintWidget.DimensionBehaviour wrap_CONTENT = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        int n5 = 0;
        int n6 = 0;
        boolean b = false;
        Label_0198: {
            Label_0137: {
                int n3;
                int n4;
                if (n == 0) {
                    if (mHead.mHorizontalChainStyle == 0) {
                        n3 = 1;
                    }
                    else {
                        n3 = 0;
                    }
                    if (mHead.mHorizontalChainStyle == 1) {
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    n5 = n3;
                    n6 = n4;
                    if (mHead.mHorizontalChainStyle != 2) {
                        break Label_0137;
                    }
                }
                else {
                    if (mHead.mVerticalChainStyle == 0) {
                        n3 = 1;
                    }
                    else {
                        n3 = 0;
                    }
                    if (mHead.mVerticalChainStyle == 1) {
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    n5 = n3;
                    n6 = n4;
                    if (mHead.mVerticalChainStyle != 2) {
                        break Label_0137;
                    }
                }
                b = true;
                n5 = n3;
                n6 = n4;
                break Label_0198;
            }
            b = false;
        }
        ConstraintWidget constraintWidget = mFirst;
        int n7 = 0;
        int i = 0;
        int n8 = 0;
        float n9 = 0.0f;
        float n10 = 0.0f;
        while (i == 0) {
            int n11 = n8;
            float n12 = n9;
            float n13 = n10;
            if (constraintWidget.getVisibility() != 8) {
                n11 = n8 + 1;
                int n14;
                if (n == 0) {
                    n14 = constraintWidget.getWidth();
                }
                else {
                    n14 = constraintWidget.getHeight();
                }
                n12 = n9 + n14;
                if (constraintWidget != mFirstVisibleWidget) {
                    n12 += constraintWidget.mListAnchors[n2].getMargin();
                }
                n13 = n10 + constraintWidget.mListAnchors[n2].getMargin() + constraintWidget.mListAnchors[n2 + 1].getMargin();
            }
            final ConstraintAnchor constraintAnchor = constraintWidget.mListAnchors[n2];
            int n15 = n7;
            if (constraintWidget.getVisibility() != 8) {
                n15 = n7;
                if (constraintWidget.mListDimensionBehaviors[n] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    n15 = n7 + 1;
                    if (n == 0) {
                        if (constraintWidget.mMatchConstraintDefaultWidth != 0) {
                            return false;
                        }
                        if (constraintWidget.mMatchConstraintMinWidth != 0 || constraintWidget.mMatchConstraintMaxWidth != 0) {
                            return false;
                        }
                    }
                    else {
                        if (constraintWidget.mMatchConstraintDefaultHeight != 0) {
                            return false;
                        }
                        if (constraintWidget.mMatchConstraintMinHeight != 0 || constraintWidget.mMatchConstraintMaxHeight != 0) {
                            return false;
                        }
                    }
                }
            }
            final ConstraintAnchor mTarget = constraintWidget.mListAnchors[n2 + 1].mTarget;
            ConstraintWidget mOwner = null;
            Label_0502: {
                if (mTarget != null) {
                    mOwner = mTarget.mOwner;
                    final ConstraintAnchor[] mListAnchors = mOwner.mListAnchors;
                    if (mListAnchors[n2].mTarget != null) {
                        if (mListAnchors[n2].mTarget.mOwner == constraintWidget) {
                            break Label_0502;
                        }
                    }
                }
                mOwner = null;
            }
            if (mOwner == null) {
                i = 1;
                mOwner = constraintWidget;
            }
            n7 = n15;
            constraintWidget = mOwner;
            n8 = n11;
            n9 = n12;
            n10 = n13;
        }
        final ResolutionAnchor resolutionNode = mFirst.mListAnchors[n2].getResolutionNode();
        final ConstraintAnchor[] mListAnchors2 = mLast.mListAnchors;
        final int n16 = n2 + 1;
        final ResolutionAnchor resolutionNode2 = mListAnchors2[n16].getResolutionNode();
        final ResolutionAnchor target = resolutionNode.target;
        if (target != null) {
            final ResolutionAnchor target2 = resolutionNode2.target;
            if (target2 != null) {
                if (target.state != 1 && target2.state != 1) {
                    return false;
                }
                if (n7 > 0 && n7 != n8) {
                    return false;
                }
                float n17;
                if (!b && n5 == 0 && n6 == 0) {
                    n17 = 0.0f;
                }
                else {
                    float n18;
                    if (mFirstVisibleWidget != null) {
                        n18 = (float)mFirstVisibleWidget.mListAnchors[n2].getMargin();
                    }
                    else {
                        n18 = 0.0f;
                    }
                    n17 = n18;
                    if (mLastVisibleWidget != null) {
                        n17 = n18 + mLastVisibleWidget.mListAnchors[n16].getMargin();
                    }
                }
                final float resolvedOffset = resolutionNode.target.resolvedOffset;
                final float resolvedOffset2 = resolutionNode2.target.resolvedOffset;
                float n19;
                if (resolvedOffset < resolvedOffset2) {
                    n19 = resolvedOffset2 - resolvedOffset;
                }
                else {
                    n19 = resolvedOffset - resolvedOffset2;
                }
                final float n20 = n19 - n9;
                if (n7 > 0 && n7 == n8) {
                    if (constraintWidget.getParent() != null && constraintWidget.getParent().mListDimensionBehaviors[n] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                        return false;
                    }
                    float n22;
                    final float n21 = n22 = n20 + n9 - n10;
                    if (n5 != 0) {
                        n22 = n21 - (n10 - n17);
                    }
                    float n23 = resolvedOffset;
                    ConstraintWidget constraintWidget2 = mFirstVisibleWidget;
                    if (n5 != 0) {
                        final float n24 = resolvedOffset + mFirstVisibleWidget.mListAnchors[n16].getMargin();
                        final ConstraintWidget constraintWidget3 = mFirstVisibleWidget.mListNextVisibleWidget[n];
                        n23 = n24;
                        constraintWidget2 = mFirstVisibleWidget;
                        if (constraintWidget3 != null) {
                            n23 = n24 + constraintWidget3.mListAnchors[n2].getMargin();
                            constraintWidget2 = mFirstVisibleWidget;
                        }
                    }
                    while (constraintWidget2 != null) {
                        final Metrics sMetrics = LinearSystem.sMetrics;
                        if (sMetrics != null) {
                            --sMetrics.nonresolvedWidgets;
                            ++sMetrics.resolvedWidgets;
                            ++sMetrics.chainConnectionResolved;
                        }
                        final ConstraintWidget constraintWidget4 = constraintWidget2.mListNextVisibleWidget[n];
                        if (constraintWidget4 != null || constraintWidget2 == mLastVisibleWidget) {
                            float n25 = n22 / n7;
                            if (mTotalWeight > 0.0f) {
                                n25 = constraintWidget2.mWeight[n] * n22 / mTotalWeight;
                            }
                            final float n26 = n23 + constraintWidget2.mListAnchors[n2].getMargin();
                            constraintWidget2.mListAnchors[n2].getResolutionNode().resolve(resolutionNode.resolvedTarget, n26);
                            final ResolutionAnchor resolutionNode3 = constraintWidget2.mListAnchors[n16].getResolutionNode();
                            final ResolutionAnchor resolvedTarget = resolutionNode.resolvedTarget;
                            final float n27 = n26 + n25;
                            resolutionNode3.resolve(resolvedTarget, n27);
                            constraintWidget2.mListAnchors[n2].getResolutionNode().addResolvedValue(linearSystem);
                            constraintWidget2.mListAnchors[n16].getResolutionNode().addResolvedValue(linearSystem);
                            n23 = n27 + constraintWidget2.mListAnchors[n16].getMargin();
                        }
                        constraintWidget2 = constraintWidget4;
                    }
                    return true;
                }
                else {
                    if (n20 < n9) {
                        return false;
                    }
                    if (b) {
                        float n28 = resolvedOffset + (n20 - n17) * mFirst.getHorizontalBiasPercent();
                        while (mFirstVisibleWidget != null) {
                            final Metrics sMetrics2 = LinearSystem.sMetrics;
                            if (sMetrics2 != null) {
                                --sMetrics2.nonresolvedWidgets;
                                ++sMetrics2.resolvedWidgets;
                                ++sMetrics2.chainConnectionResolved;
                            }
                            final ConstraintWidget constraintWidget5 = mFirstVisibleWidget.mListNextVisibleWidget[n];
                            float n29 = 0.0f;
                            Label_1354: {
                                if (constraintWidget5 == null) {
                                    n29 = n28;
                                    if (mFirstVisibleWidget != mLastVisibleWidget) {
                                        break Label_1354;
                                    }
                                }
                                int n30;
                                if (n == 0) {
                                    n30 = mFirstVisibleWidget.getWidth();
                                }
                                else {
                                    n30 = mFirstVisibleWidget.getHeight();
                                }
                                final float n31 = (float)n30;
                                final float n32 = n28 + mFirstVisibleWidget.mListAnchors[n2].getMargin();
                                mFirstVisibleWidget.mListAnchors[n2].getResolutionNode().resolve(resolutionNode.resolvedTarget, n32);
                                final ResolutionAnchor resolutionNode4 = mFirstVisibleWidget.mListAnchors[n16].getResolutionNode();
                                final ResolutionAnchor resolvedTarget2 = resolutionNode.resolvedTarget;
                                final float n33 = n32 + n31;
                                resolutionNode4.resolve(resolvedTarget2, n33);
                                mFirstVisibleWidget.mListAnchors[n2].getResolutionNode().addResolvedValue(linearSystem);
                                mFirstVisibleWidget.mListAnchors[n16].getResolutionNode().addResolvedValue(linearSystem);
                                n29 = n33 + mFirstVisibleWidget.mListAnchors[n16].getMargin();
                            }
                            mFirstVisibleWidget = constraintWidget5;
                            n28 = n29;
                        }
                    }
                    else if (n5 != 0 || n6 != 0) {
                        float n34 = 0.0f;
                        Label_1407: {
                            if (n5 == 0) {
                                n34 = n20;
                                if (n6 == 0) {
                                    break Label_1407;
                                }
                            }
                            n34 = n20 - n17;
                        }
                        float n35 = n34 / (n8 + 1);
                        if (n6 != 0) {
                            float n36;
                            if (n8 > 1) {
                                n36 = (float)(n8 - 1);
                            }
                            else {
                                n36 = 2.0f;
                            }
                            n35 = n34 / n36;
                        }
                        float n38;
                        final float n37 = n38 = resolvedOffset + n35;
                        if (n6 != 0) {
                            n38 = n37;
                            if (n8 > 1) {
                                n38 = mFirstVisibleWidget.mListAnchors[n2].getMargin() + resolvedOffset;
                            }
                        }
                        float n39 = n38;
                        ConstraintWidget constraintWidget6 = mFirstVisibleWidget;
                        if (n5 != 0) {
                            n39 = n38;
                            if ((constraintWidget6 = mFirstVisibleWidget) != null) {
                                n39 = n38 + mFirstVisibleWidget.mListAnchors[n2].getMargin();
                                constraintWidget6 = mFirstVisibleWidget;
                            }
                        }
                        while (constraintWidget6 != null) {
                            final Metrics sMetrics3 = LinearSystem.sMetrics;
                            if (sMetrics3 != null) {
                                --sMetrics3.nonresolvedWidgets;
                                ++sMetrics3.resolvedWidgets;
                                ++sMetrics3.chainConnectionResolved;
                            }
                            final ConstraintWidget constraintWidget7 = constraintWidget6.mListNextVisibleWidget[n];
                            float n40 = 0.0f;
                            Label_1709: {
                                if (constraintWidget7 == null) {
                                    n40 = n39;
                                    if (constraintWidget6 != mLastVisibleWidget) {
                                        break Label_1709;
                                    }
                                }
                                int n41;
                                if (n == 0) {
                                    n41 = constraintWidget6.getWidth();
                                }
                                else {
                                    n41 = constraintWidget6.getHeight();
                                }
                                final float n42 = (float)n41;
                                constraintWidget6.mListAnchors[n2].getResolutionNode().resolve(resolutionNode.resolvedTarget, n39);
                                constraintWidget6.mListAnchors[n16].getResolutionNode().resolve(resolutionNode.resolvedTarget, n39 + n42);
                                constraintWidget6.mListAnchors[n2].getResolutionNode().addResolvedValue(linearSystem);
                                constraintWidget6.mListAnchors[n16].getResolutionNode().addResolvedValue(linearSystem);
                                n40 = n39 + (n42 + n35);
                            }
                            constraintWidget6 = constraintWidget7;
                            n39 = n40;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    static void checkMatchParent(final ConstraintWidgetContainer constraintWidgetContainer, final LinearSystem linearSystem, final ConstraintWidget constraintWidget) {
        if (constraintWidgetContainer.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            final int mMargin = constraintWidget.mLeft.mMargin;
            final int n = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            final ConstraintAnchor mLeft = constraintWidget.mLeft;
            mLeft.mSolverVariable = linearSystem.createObjectVariable(mLeft);
            final ConstraintAnchor mRight = constraintWidget.mRight;
            mRight.mSolverVariable = linearSystem.createObjectVariable(mRight);
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, mMargin);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, n);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(mMargin, n);
        }
        if (constraintWidgetContainer.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            final int mMargin2 = constraintWidget.mTop.mMargin;
            final int n2 = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            final ConstraintAnchor mTop = constraintWidget.mTop;
            mTop.mSolverVariable = linearSystem.createObjectVariable(mTop);
            final ConstraintAnchor mBottom = constraintWidget.mBottom;
            mBottom.mSolverVariable = linearSystem.createObjectVariable(mBottom);
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, mMargin2);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, n2);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                final ConstraintAnchor mBaseline = constraintWidget.mBaseline;
                mBaseline.mSolverVariable = linearSystem.createObjectVariable(mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + mMargin2);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(mMargin2, n2);
        }
    }
    
    private static boolean optimizableMatchConstraint(final ConstraintWidget constraintWidget, int n) {
        final ConstraintWidget.DimensionBehaviour[] mListDimensionBehaviors = constraintWidget.mListDimensionBehaviors;
        if (mListDimensionBehaviors[n] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            return false;
        }
        final float mDimensionRatio = constraintWidget.mDimensionRatio;
        final int n2 = 1;
        if (mDimensionRatio != 0.0f) {
            if (n == 0) {
                n = n2;
            }
            else {
                n = 0;
            }
            if (mListDimensionBehaviors[n] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {}
            return false;
        }
        if (n == 0) {
            if (constraintWidget.mMatchConstraintDefaultWidth != 0) {
                return false;
            }
            if (constraintWidget.mMatchConstraintMinWidth != 0 || constraintWidget.mMatchConstraintMaxWidth != 0) {
                return false;
            }
        }
        else {
            if (constraintWidget.mMatchConstraintDefaultHeight != 0) {
                return false;
            }
            if (constraintWidget.mMatchConstraintMinHeight != 0 || constraintWidget.mMatchConstraintMaxHeight != 0) {
                return false;
            }
        }
        return true;
    }
}
