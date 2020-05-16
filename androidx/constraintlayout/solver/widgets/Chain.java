// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.ArrayRow;
import java.util.ArrayList;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.constraintlayout.solver.LinearSystem;

class Chain
{
    static void applyChainConstraints(final ConstraintWidgetContainer constraintWidgetContainer, final LinearSystem linearSystem, final int n) {
        int i = 0;
        int n2;
        ChainHead[] array;
        int n3;
        if (n == 0) {
            n2 = constraintWidgetContainer.mHorizontalChainsSize;
            array = constraintWidgetContainer.mHorizontalChainsArray;
            n3 = 0;
        }
        else {
            n3 = 2;
            n2 = constraintWidgetContainer.mVerticalChainsSize;
            array = constraintWidgetContainer.mVerticalChainsArray;
        }
        while (i < n2) {
            final ChainHead chainHead = array[i];
            chainHead.define();
            if (constraintWidgetContainer.optimizeFor(4)) {
                if (!Optimizer.applyChainOptimized(constraintWidgetContainer, linearSystem, n, n3, chainHead)) {
                    applyChainConstraints(constraintWidgetContainer, linearSystem, n, n3, chainHead);
                }
            }
            else {
                applyChainConstraints(constraintWidgetContainer, linearSystem, n, n3, chainHead);
            }
            ++i;
        }
    }
    
    static void applyChainConstraints(final ConstraintWidgetContainer constraintWidgetContainer, final LinearSystem linearSystem, int n, int margin, final ChainHead chainHead) {
        final ConstraintWidget mFirst = chainHead.mFirst;
        final ConstraintWidget mLast = chainHead.mLast;
        final ConstraintWidget mFirstVisibleWidget = chainHead.mFirstVisibleWidget;
        final ConstraintWidget mLastVisibleWidget = chainHead.mLastVisibleWidget;
        final ConstraintWidget mHead = chainHead.mHead;
        final float mTotalWeight = chainHead.mTotalWeight;
        final ConstraintWidget mFirstMatchConstraintWidget = chainHead.mFirstMatchConstraintWidget;
        final ConstraintWidget mLastMatchConstraintWidget = chainHead.mLastMatchConstraintWidget;
        final boolean b = constraintWidgetContainer.mListDimensionBehaviors[n] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        int n3 = 0;
        int n4 = 0;
        boolean b2 = false;
        Label_0205: {
            int n5 = 0;
            Label_0198: {
                int n2;
                if (n == 0) {
                    if (mHead.mHorizontalChainStyle == 0) {
                        n2 = 1;
                    }
                    else {
                        n2 = 0;
                    }
                    if (mHead.mHorizontalChainStyle == 1) {
                        n3 = 1;
                    }
                    else {
                        n3 = 0;
                    }
                    n4 = n2;
                    n5 = n3;
                    if (mHead.mHorizontalChainStyle != 2) {
                        break Label_0198;
                    }
                }
                else {
                    if (mHead.mVerticalChainStyle == 0) {
                        n2 = 1;
                    }
                    else {
                        n2 = 0;
                    }
                    if (mHead.mVerticalChainStyle == 1) {
                        n3 = 1;
                    }
                    else {
                        n3 = 0;
                    }
                    n4 = n2;
                    n5 = n3;
                    if (mHead.mVerticalChainStyle != 2) {
                        break Label_0198;
                    }
                }
                b2 = true;
                n4 = n2;
                break Label_0205;
            }
            b2 = false;
            n3 = n5;
        }
        ConstraintWidget constraintWidget = mFirst;
        int n6 = 0;
        final int n7 = n3;
        SolverVariable solverVariable;
        while (true) {
            solverVariable = null;
            final ConstraintWidget constraintWidget2 = null;
            if (n6 != 0) {
                break;
            }
            final ConstraintAnchor constraintAnchor = constraintWidget.mListAnchors[margin];
            int n8;
            if (!b && !b2) {
                n8 = 4;
            }
            else {
                n8 = 1;
            }
            final int margin2 = constraintAnchor.getMargin();
            final ConstraintAnchor mTarget = constraintAnchor.mTarget;
            int n9 = margin2;
            if (mTarget != null) {
                n9 = margin2;
                if (constraintWidget != mFirst) {
                    n9 = margin2 + mTarget.getMargin();
                }
            }
            if (b2 && constraintWidget != mFirst && constraintWidget != mFirstVisibleWidget) {
                n8 = 6;
            }
            else if (n4 != 0 && b) {
                n8 = 4;
            }
            final ConstraintAnchor mTarget2 = constraintAnchor.mTarget;
            if (mTarget2 != null) {
                if (constraintWidget == mFirstVisibleWidget) {
                    linearSystem.addGreaterThan(constraintAnchor.mSolverVariable, mTarget2.mSolverVariable, n9, 5);
                }
                else {
                    linearSystem.addGreaterThan(constraintAnchor.mSolverVariable, mTarget2.mSolverVariable, n9, 6);
                }
                linearSystem.addEquality(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, n9, n8);
            }
            if (b) {
                if (constraintWidget.getVisibility() != 8 && constraintWidget.mListDimensionBehaviors[n] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    final ConstraintAnchor[] mListAnchors = constraintWidget.mListAnchors;
                    linearSystem.addGreaterThan(mListAnchors[margin + 1].mSolverVariable, mListAnchors[margin].mSolverVariable, 0, 5);
                }
                linearSystem.addGreaterThan(constraintWidget.mListAnchors[margin].mSolverVariable, constraintWidgetContainer.mListAnchors[margin].mSolverVariable, 0, 6);
            }
            final ConstraintAnchor mTarget3 = constraintWidget.mListAnchors[margin + 1].mTarget;
            ConstraintWidget constraintWidget3 = constraintWidget2;
            if (mTarget3 != null) {
                final ConstraintWidget mOwner = mTarget3.mOwner;
                final ConstraintAnchor[] mListAnchors2 = mOwner.mListAnchors;
                constraintWidget3 = constraintWidget2;
                if (mListAnchors2[margin].mTarget != null) {
                    if (mListAnchors2[margin].mTarget.mOwner != constraintWidget) {
                        constraintWidget3 = constraintWidget2;
                    }
                    else {
                        constraintWidget3 = mOwner;
                    }
                }
            }
            if (constraintWidget3 != null) {
                constraintWidget = constraintWidget3;
            }
            else {
                n6 = 1;
            }
        }
        if (mLastVisibleWidget != null) {
            final ConstraintAnchor[] mListAnchors3 = mLast.mListAnchors;
            final int n10 = margin + 1;
            if (mListAnchors3[n10].mTarget != null) {
                final ConstraintAnchor constraintAnchor2 = mLastVisibleWidget.mListAnchors[n10];
                linearSystem.addLowerThan(constraintAnchor2.mSolverVariable, mListAnchors3[n10].mTarget.mSolverVariable, -constraintAnchor2.getMargin(), 5);
            }
        }
        if (b) {
            final ConstraintAnchor[] mListAnchors4 = constraintWidgetContainer.mListAnchors;
            final int n11 = margin + 1;
            final SolverVariable mSolverVariable = mListAnchors4[n11].mSolverVariable;
            final ConstraintAnchor[] mListAnchors5 = mLast.mListAnchors;
            linearSystem.addGreaterThan(mSolverVariable, mListAnchors5[n11].mSolverVariable, mListAnchors5[n11].getMargin(), 6);
        }
        final ArrayList<ConstraintWidget> mWeightedMatchConstraintsWidgets = chainHead.mWeightedMatchConstraintsWidgets;
        if (mWeightedMatchConstraintsWidgets != null) {
            final int size = mWeightedMatchConstraintsWidgets.size();
            if (size > 1) {
                float n12;
                if (chainHead.mHasUndefinedWeights && !chainHead.mHasComplexMatchWeights) {
                    n12 = (float)chainHead.mWidgetsMatchCount;
                }
                else {
                    n12 = mTotalWeight;
                }
                float n13 = 0.0f;
                ConstraintWidget constraintWidget4 = null;
                float n14;
                for (int i = 0; i < size; ++i, n13 = n14) {
                    final ConstraintWidget constraintWidget5 = mWeightedMatchConstraintsWidgets.get(i);
                    n14 = constraintWidget5.mWeight[n];
                    Label_0908: {
                        if (n14 < 0.0f) {
                            if (chainHead.mHasComplexMatchWeights) {
                                final ConstraintAnchor[] mListAnchors6 = constraintWidget5.mListAnchors;
                                linearSystem.addEquality(mListAnchors6[margin + 1].mSolverVariable, mListAnchors6[margin].mSolverVariable, 0, 4);
                                break Label_0908;
                            }
                            n14 = 1.0f;
                        }
                        if (n14 != 0.0f) {
                            if (constraintWidget4 != null) {
                                final ConstraintAnchor[] mListAnchors7 = constraintWidget4.mListAnchors;
                                final SolverVariable mSolverVariable2 = mListAnchors7[margin].mSolverVariable;
                                final int n15 = margin + 1;
                                final SolverVariable mSolverVariable3 = mListAnchors7[n15].mSolverVariable;
                                final ConstraintAnchor[] mListAnchors8 = constraintWidget5.mListAnchors;
                                final SolverVariable mSolverVariable4 = mListAnchors8[margin].mSolverVariable;
                                final SolverVariable mSolverVariable5 = mListAnchors8[n15].mSolverVariable;
                                final ArrayRow row = linearSystem.createRow();
                                row.createRowEqualMatchDimensions(n13, n12, n14, mSolverVariable2, mSolverVariable3, mSolverVariable4, mSolverVariable5);
                                linearSystem.addConstraint(row);
                            }
                            constraintWidget4 = constraintWidget5;
                            continue;
                        }
                        final ConstraintAnchor[] mListAnchors9 = constraintWidget5.mListAnchors;
                        linearSystem.addEquality(mListAnchors9[margin + 1].mSolverVariable, mListAnchors9[margin].mSolverVariable, 0, 6);
                    }
                    n14 = n13;
                }
            }
        }
        if (mFirstVisibleWidget != null && (mFirstVisibleWidget == mLastVisibleWidget || b2)) {
            final ConstraintAnchor[] mListAnchors10 = mFirst.mListAnchors;
            ConstraintAnchor constraintAnchor3 = mListAnchors10[margin];
            final ConstraintAnchor[] mListAnchors11 = mLast.mListAnchors;
            final int n16 = margin + 1;
            ConstraintAnchor constraintAnchor4 = mListAnchors11[n16];
            SolverVariable mSolverVariable6;
            if (mListAnchors10[margin].mTarget != null) {
                mSolverVariable6 = mListAnchors10[margin].mTarget.mSolverVariable;
            }
            else {
                mSolverVariable6 = null;
            }
            final ConstraintAnchor[] mListAnchors12 = mLast.mListAnchors;
            SolverVariable mSolverVariable7;
            if (mListAnchors12[n16].mTarget != null) {
                mSolverVariable7 = mListAnchors12[n16].mTarget.mSolverVariable;
            }
            else {
                mSolverVariable7 = null;
            }
            if (mFirstVisibleWidget == mLastVisibleWidget) {
                final ConstraintAnchor[] mListAnchors13 = mFirstVisibleWidget.mListAnchors;
                constraintAnchor3 = mListAnchors13[margin];
                constraintAnchor4 = mListAnchors13[n16];
            }
            if (mSolverVariable6 != null && mSolverVariable7 != null) {
                float n17;
                if (n == 0) {
                    n17 = mHead.mHorizontalBiasPercent;
                }
                else {
                    n17 = mHead.mVerticalBiasPercent;
                }
                final int margin3 = constraintAnchor3.getMargin();
                n = constraintAnchor4.getMargin();
                linearSystem.addCentering(constraintAnchor3.mSolverVariable, mSolverVariable6, margin3, n17, mSolverVariable7, constraintAnchor4.mSolverVariable, n, 5);
            }
        }
        else if (n4 != 0 && mFirstVisibleWidget != null) {
            final int mWidgetsMatchCount = chainHead.mWidgetsMatchCount;
            final boolean b3 = mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == mWidgetsMatchCount;
            ConstraintWidget constraintWidget7;
            ConstraintWidget constraintWidget8;
            for (ConstraintWidget constraintWidget6 = constraintWidget7 = mFirstVisibleWidget; constraintWidget6 != null; constraintWidget6 = constraintWidget8) {
                constraintWidget8 = constraintWidget6.mListNextVisibleWidget[n];
                if (constraintWidget8 != null || constraintWidget6 == mLastVisibleWidget) {
                    final ConstraintAnchor constraintAnchor5 = constraintWidget6.mListAnchors[margin];
                    final SolverVariable mSolverVariable8 = constraintAnchor5.mSolverVariable;
                    final ConstraintAnchor mTarget4 = constraintAnchor5.mTarget;
                    SolverVariable mSolverVariable9;
                    if (mTarget4 != null) {
                        mSolverVariable9 = mTarget4.mSolverVariable;
                    }
                    else {
                        mSolverVariable9 = null;
                    }
                    SolverVariable solverVariable2;
                    if (constraintWidget7 != constraintWidget6) {
                        solverVariable2 = constraintWidget7.mListAnchors[margin + 1].mSolverVariable;
                    }
                    else {
                        solverVariable2 = mSolverVariable9;
                        if (constraintWidget6 == mFirstVisibleWidget) {
                            solverVariable2 = mSolverVariable9;
                            if (constraintWidget7 == constraintWidget6) {
                                final ConstraintAnchor[] mListAnchors14 = mFirst.mListAnchors;
                                if (mListAnchors14[margin].mTarget != null) {
                                    solverVariable2 = mListAnchors14[margin].mTarget.mSolverVariable;
                                }
                                else {
                                    solverVariable2 = null;
                                }
                            }
                        }
                    }
                    final int margin4 = constraintAnchor5.getMargin();
                    final ConstraintAnchor[] mListAnchors15 = constraintWidget6.mListAnchors;
                    final int n18 = margin + 1;
                    final int margin5 = mListAnchors15[n18].getMargin();
                    ConstraintAnchor mTarget5;
                    SolverVariable solverVariable3;
                    SolverVariable solverVariable4;
                    if (constraintWidget8 != null) {
                        mTarget5 = constraintWidget8.mListAnchors[margin];
                        solverVariable3 = mTarget5.mSolverVariable;
                        solverVariable4 = constraintWidget6.mListAnchors[n18].mSolverVariable;
                    }
                    else {
                        mTarget5 = mLast.mListAnchors[n18].mTarget;
                        if (mTarget5 != null) {
                            solverVariable3 = mTarget5.mSolverVariable;
                        }
                        else {
                            solverVariable3 = null;
                        }
                        solverVariable4 = constraintWidget6.mListAnchors[n18].mSolverVariable;
                    }
                    int margin6 = margin5;
                    if (mTarget5 != null) {
                        margin6 = margin5 + mTarget5.getMargin();
                    }
                    int margin7 = margin4;
                    if (constraintWidget7 != null) {
                        margin7 = margin4 + constraintWidget7.mListAnchors[n18].getMargin();
                    }
                    if (mSolverVariable8 != null && solverVariable2 != null && solverVariable3 != null && solverVariable4 != null) {
                        if (constraintWidget6 == mFirstVisibleWidget) {
                            margin7 = mFirstVisibleWidget.mListAnchors[margin].getMargin();
                        }
                        if (constraintWidget6 == mLastVisibleWidget) {
                            margin6 = mLastVisibleWidget.mListAnchors[n18].getMargin();
                        }
                        int n19;
                        if (b3) {
                            n19 = 6;
                        }
                        else {
                            n19 = 4;
                        }
                        linearSystem.addCentering(mSolverVariable8, solverVariable2, margin7, 0.5f, solverVariable3, solverVariable4, margin6, n19);
                    }
                }
                constraintWidget7 = constraintWidget6;
            }
        }
        else if (n7 != 0 && mFirstVisibleWidget != null) {
            final int mWidgetsMatchCount2 = chainHead.mWidgetsMatchCount;
            final boolean b4 = mWidgetsMatchCount2 > 0 && chainHead.mWidgetsCount == mWidgetsMatchCount2;
            ConstraintWidget constraintWidget10;
            ConstraintWidget constraintWidget11;
            for (ConstraintWidget constraintWidget9 = constraintWidget10 = mFirstVisibleWidget; constraintWidget9 != null; constraintWidget9 = constraintWidget11) {
                constraintWidget11 = constraintWidget9.mListNextVisibleWidget[n];
                if (constraintWidget9 != mFirstVisibleWidget && constraintWidget9 != mLastVisibleWidget && constraintWidget11 != null) {
                    if (constraintWidget11 == mLastVisibleWidget) {
                        constraintWidget11 = null;
                    }
                    final ConstraintAnchor constraintAnchor6 = constraintWidget9.mListAnchors[margin];
                    final SolverVariable mSolverVariable10 = constraintAnchor6.mSolverVariable;
                    final ConstraintAnchor mTarget6 = constraintAnchor6.mTarget;
                    if (mTarget6 != null) {
                        final SolverVariable mSolverVariable11 = mTarget6.mSolverVariable;
                    }
                    final ConstraintAnchor[] mListAnchors16 = constraintWidget10.mListAnchors;
                    final int n20 = margin + 1;
                    final SolverVariable mSolverVariable12 = mListAnchors16[n20].mSolverVariable;
                    final int margin8 = constraintAnchor6.getMargin();
                    final int margin9 = constraintWidget9.mListAnchors[n20].getMargin();
                    ConstraintAnchor mTarget7;
                    SolverVariable solverVariable5;
                    SolverVariable solverVariable6;
                    if (constraintWidget11 != null) {
                        mTarget7 = constraintWidget11.mListAnchors[margin];
                        solverVariable5 = mTarget7.mSolverVariable;
                        final ConstraintAnchor mTarget8 = mTarget7.mTarget;
                        if (mTarget8 != null) {
                            solverVariable6 = mTarget8.mSolverVariable;
                        }
                        else {
                            solverVariable6 = null;
                        }
                    }
                    else {
                        mTarget7 = constraintWidget9.mListAnchors[n20].mTarget;
                        if (mTarget7 != null) {
                            solverVariable5 = mTarget7.mSolverVariable;
                        }
                        else {
                            solverVariable5 = null;
                        }
                        solverVariable6 = constraintWidget9.mListAnchors[n20].mSolverVariable;
                    }
                    int n21 = margin9;
                    if (mTarget7 != null) {
                        n21 = margin9 + mTarget7.getMargin();
                    }
                    int n22 = margin8;
                    if (constraintWidget10 != null) {
                        n22 = margin8 + constraintWidget10.mListAnchors[n20].getMargin();
                    }
                    int n23;
                    if (b4) {
                        n23 = 6;
                    }
                    else {
                        n23 = 4;
                    }
                    if (mSolverVariable10 != null && mSolverVariable12 != null && solverVariable5 != null && solverVariable6 != null) {
                        linearSystem.addCentering(mSolverVariable10, mSolverVariable12, n22, 0.5f, solverVariable5, solverVariable6, n21, n23);
                    }
                }
                constraintWidget10 = constraintWidget9;
            }
            final ConstraintAnchor constraintAnchor7 = mFirstVisibleWidget.mListAnchors[margin];
            final ConstraintAnchor mTarget9 = mFirst.mListAnchors[margin].mTarget;
            final ConstraintAnchor[] mListAnchors17 = mLastVisibleWidget.mListAnchors;
            n = margin + 1;
            final ConstraintAnchor constraintAnchor8 = mListAnchors17[n];
            final ConstraintAnchor mTarget10 = mLast.mListAnchors[n].mTarget;
            if (mTarget9 != null) {
                if (mFirstVisibleWidget != mLastVisibleWidget) {
                    linearSystem.addEquality(constraintAnchor7.mSolverVariable, mTarget9.mSolverVariable, constraintAnchor7.getMargin(), 5);
                }
                else if (mTarget10 != null) {
                    linearSystem.addCentering(constraintAnchor7.mSolverVariable, mTarget9.mSolverVariable, constraintAnchor7.getMargin(), 0.5f, constraintAnchor8.mSolverVariable, mTarget10.mSolverVariable, constraintAnchor8.getMargin(), 5);
                }
            }
            if (mTarget10 != null && mFirstVisibleWidget != mLastVisibleWidget) {
                linearSystem.addEquality(constraintAnchor8.mSolverVariable, mTarget10.mSolverVariable, -constraintAnchor8.getMargin(), 5);
            }
        }
        if ((n4 != 0 || n7 != 0) && mFirstVisibleWidget != null) {
            ConstraintAnchor constraintAnchor9 = mFirstVisibleWidget.mListAnchors[margin];
            final ConstraintAnchor[] mListAnchors18 = mLastVisibleWidget.mListAnchors;
            n = margin + 1;
            ConstraintAnchor constraintAnchor10 = mListAnchors18[n];
            final ConstraintAnchor mTarget11 = constraintAnchor9.mTarget;
            SolverVariable mSolverVariable13;
            if (mTarget11 != null) {
                mSolverVariable13 = mTarget11.mSolverVariable;
            }
            else {
                mSolverVariable13 = null;
            }
            final ConstraintAnchor mTarget12 = constraintAnchor10.mTarget;
            SolverVariable solverVariable7;
            if (mTarget12 != null) {
                solverVariable7 = mTarget12.mSolverVariable;
            }
            else {
                solverVariable7 = null;
            }
            if (mLast != mLastVisibleWidget) {
                final ConstraintAnchor mTarget13 = mLast.mListAnchors[n].mTarget;
                solverVariable7 = solverVariable;
                if (mTarget13 != null) {
                    solverVariable7 = mTarget13.mSolverVariable;
                }
            }
            if (mFirstVisibleWidget == mLastVisibleWidget) {
                final ConstraintAnchor[] mListAnchors19 = mFirstVisibleWidget.mListAnchors;
                constraintAnchor9 = mListAnchors19[margin];
                constraintAnchor10 = mListAnchors19[n];
            }
            if (mSolverVariable13 != null && solverVariable7 != null) {
                margin = constraintAnchor9.getMargin();
                ConstraintWidget constraintWidget12;
                if (mLastVisibleWidget == null) {
                    constraintWidget12 = mLast;
                }
                else {
                    constraintWidget12 = mLastVisibleWidget;
                }
                n = constraintWidget12.mListAnchors[n].getMargin();
                linearSystem.addCentering(constraintAnchor9.mSolverVariable, mSolverVariable13, margin, 0.5f, solverVariable7, constraintAnchor10.mSolverVariable, n, 5);
            }
        }
    }
}
