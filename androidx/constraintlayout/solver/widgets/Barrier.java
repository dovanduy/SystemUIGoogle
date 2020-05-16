// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.constraintlayout.solver.LinearSystem;
import java.util.ArrayList;

public class Barrier extends Helper
{
    private boolean mAllowsGoneWidget;
    private int mBarrierType;
    private ArrayList<ResolutionAnchor> mNodes;
    
    public Barrier() {
        this.mBarrierType = 0;
        this.mNodes = new ArrayList<ResolutionAnchor>(4);
        this.mAllowsGoneWidget = true;
    }
    
    @Override
    public void addToSolver(final LinearSystem linearSystem) {
        final ConstraintAnchor[] mListAnchors = super.mListAnchors;
        mListAnchors[0] = super.mLeft;
        mListAnchors[2] = super.mTop;
        mListAnchors[1] = super.mRight;
        mListAnchors[3] = super.mBottom;
        int n = 0;
        ConstraintAnchor[] mListAnchors2;
        while (true) {
            mListAnchors2 = super.mListAnchors;
            if (n >= mListAnchors2.length) {
                break;
            }
            mListAnchors2[n].mSolverVariable = linearSystem.createObjectVariable(mListAnchors2[n]);
            ++n;
        }
        final int mBarrierType = this.mBarrierType;
        if (mBarrierType >= 0 && mBarrierType < 4) {
            final ConstraintAnchor constraintAnchor = mListAnchors2[mBarrierType];
            while (true) {
                for (int i = 0; i < super.mWidgetsCount; ++i) {
                    final ConstraintWidget constraintWidget = super.mWidgets[i];
                    if (this.mAllowsGoneWidget || constraintWidget.allowedInBarrier()) {
                        final int mBarrierType2 = this.mBarrierType;
                        if ((mBarrierType2 != 0 && mBarrierType2 != 1) || constraintWidget.getHorizontalDimensionBehaviour() != DimensionBehaviour.MATCH_CONSTRAINT) {
                            final int mBarrierType3 = this.mBarrierType;
                            if ((mBarrierType3 != 2 && mBarrierType3 != 3) || constraintWidget.getVerticalDimensionBehaviour() != DimensionBehaviour.MATCH_CONSTRAINT) {
                                continue;
                            }
                        }
                        boolean b = true;
                        final int mBarrierType4 = this.mBarrierType;
                        Label_0243: {
                            if (mBarrierType4 != 0 && mBarrierType4 != 1) {
                                if (this.getParent().getVerticalDimensionBehaviour() != DimensionBehaviour.WRAP_CONTENT) {
                                    break Label_0243;
                                }
                            }
                            else if (this.getParent().getHorizontalDimensionBehaviour() != DimensionBehaviour.WRAP_CONTENT) {
                                break Label_0243;
                            }
                            b = false;
                        }
                        for (int j = 0; j < super.mWidgetsCount; ++j) {
                            final ConstraintWidget constraintWidget2 = super.mWidgets[j];
                            if (this.mAllowsGoneWidget || constraintWidget2.allowedInBarrier()) {
                                final SolverVariable objectVariable = linearSystem.createObjectVariable(constraintWidget2.mListAnchors[this.mBarrierType]);
                                final ConstraintAnchor[] mListAnchors3 = constraintWidget2.mListAnchors;
                                final int mBarrierType5 = this.mBarrierType;
                                mListAnchors3[mBarrierType5].mSolverVariable = objectVariable;
                                if (mBarrierType5 != 0 && mBarrierType5 != 2) {
                                    linearSystem.addGreaterBarrier(constraintAnchor.mSolverVariable, objectVariable, b);
                                }
                                else {
                                    linearSystem.addLowerBarrier(constraintAnchor.mSolverVariable, objectVariable, b);
                                }
                            }
                        }
                        final int mBarrierType6 = this.mBarrierType;
                        if (mBarrierType6 == 0) {
                            linearSystem.addEquality(super.mRight.mSolverVariable, super.mLeft.mSolverVariable, 0, 6);
                            if (!b) {
                                linearSystem.addEquality(super.mLeft.mSolverVariable, super.mParent.mRight.mSolverVariable, 0, 5);
                            }
                            return;
                        }
                        else if (mBarrierType6 == 1) {
                            linearSystem.addEquality(super.mLeft.mSolverVariable, super.mRight.mSolverVariable, 0, 6);
                            if (!b) {
                                linearSystem.addEquality(super.mLeft.mSolverVariable, super.mParent.mLeft.mSolverVariable, 0, 5);
                            }
                            return;
                        }
                        else if (mBarrierType6 == 2) {
                            linearSystem.addEquality(super.mBottom.mSolverVariable, super.mTop.mSolverVariable, 0, 6);
                            if (!b) {
                                linearSystem.addEquality(super.mTop.mSolverVariable, super.mParent.mBottom.mSolverVariable, 0, 5);
                            }
                            return;
                        }
                        else {
                            if (mBarrierType6 != 3) {
                                return;
                            }
                            linearSystem.addEquality(super.mTop.mSolverVariable, super.mBottom.mSolverVariable, 0, 6);
                            if (!b) {
                                linearSystem.addEquality(super.mTop.mSolverVariable, super.mParent.mTop.mSolverVariable, 0, 5);
                            }
                            return;
                        }
                    }
                }
                boolean b = false;
                continue;
            }
        }
    }
    
    @Override
    public boolean allowedInBarrier() {
        return true;
    }
    
    @Override
    public void analyze(int i) {
        final ConstraintWidget mParent = super.mParent;
        if (mParent == null) {
            return;
        }
        if (!((ConstraintWidgetContainer)mParent).optimizeFor(2)) {
            return;
        }
        i = this.mBarrierType;
        ResolutionAnchor resolutionAnchor;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return;
                    }
                    resolutionAnchor = super.mBottom.getResolutionNode();
                }
                else {
                    resolutionAnchor = super.mTop.getResolutionNode();
                }
            }
            else {
                resolutionAnchor = super.mRight.getResolutionNode();
            }
        }
        else {
            resolutionAnchor = super.mLeft.getResolutionNode();
        }
        resolutionAnchor.setType(5);
        i = this.mBarrierType;
        if (i != 0 && i != 1) {
            super.mLeft.getResolutionNode().resolve(null, 0.0f);
            super.mRight.getResolutionNode().resolve(null, 0.0f);
        }
        else {
            super.mTop.getResolutionNode().resolve(null, 0.0f);
            super.mBottom.getResolutionNode().resolve(null, 0.0f);
        }
        this.mNodes.clear();
        ConstraintWidget constraintWidget;
        int mBarrierType;
        ResolutionAnchor e;
        for (i = 0; i < super.mWidgetsCount; ++i) {
            constraintWidget = super.mWidgets[i];
            if (this.mAllowsGoneWidget || constraintWidget.allowedInBarrier()) {
                mBarrierType = this.mBarrierType;
                if (mBarrierType != 0) {
                    if (mBarrierType != 1) {
                        if (mBarrierType != 2) {
                            if (mBarrierType != 3) {
                                e = null;
                            }
                            else {
                                e = constraintWidget.mBottom.getResolutionNode();
                            }
                        }
                        else {
                            e = constraintWidget.mTop.getResolutionNode();
                        }
                    }
                    else {
                        e = constraintWidget.mRight.getResolutionNode();
                    }
                }
                else {
                    e = constraintWidget.mLeft.getResolutionNode();
                }
                if (e != null) {
                    this.mNodes.add(e);
                    e.addDependent(resolutionAnchor);
                }
            }
        }
    }
    
    @Override
    public void resetResolutionNodes() {
        super.resetResolutionNodes();
        this.mNodes.clear();
    }
    
    @Override
    public void resolve() {
        final int mBarrierType = this.mBarrierType;
        float resolvedOffset = Float.MAX_VALUE;
        ResolutionAnchor resolutionAnchor = null;
        Label_0071: {
            if (mBarrierType != 0) {
                if (mBarrierType != 1) {
                    if (mBarrierType == 2) {
                        resolutionAnchor = super.mTop.getResolutionNode();
                        break Label_0071;
                    }
                    if (mBarrierType != 3) {
                        return;
                    }
                    resolutionAnchor = super.mBottom.getResolutionNode();
                }
                else {
                    resolutionAnchor = super.mRight.getResolutionNode();
                }
                resolvedOffset = 0.0f;
            }
            else {
                resolutionAnchor = super.mLeft.getResolutionNode();
            }
        }
        final int size = this.mNodes.size();
        ResolutionAnchor resolvedTarget = null;
        float n;
        for (int i = 0; i < size; ++i, resolvedOffset = n) {
            final ResolutionAnchor resolutionAnchor2 = this.mNodes.get(i);
            if (resolutionAnchor2.state != 1) {
                return;
            }
            final int mBarrierType2 = this.mBarrierType;
            float n2;
            if (mBarrierType2 != 0 && mBarrierType2 != 2) {
                final float resolvedOffset2 = resolutionAnchor2.resolvedOffset;
                n = resolvedOffset;
                if (resolvedOffset2 <= resolvedOffset) {
                    continue;
                }
                resolvedTarget = resolutionAnchor2.resolvedTarget;
                n2 = resolvedOffset2;
            }
            else {
                final float resolvedOffset3 = resolutionAnchor2.resolvedOffset;
                n = resolvedOffset;
                if (resolvedOffset3 >= resolvedOffset) {
                    continue;
                }
                resolvedTarget = resolutionAnchor2.resolvedTarget;
                n2 = resolvedOffset3;
            }
            n = n2;
        }
        if (LinearSystem.getMetrics() != null) {
            final Metrics metrics = LinearSystem.getMetrics();
            ++metrics.barrierConnectionResolved;
        }
        resolutionAnchor.resolvedTarget = resolvedTarget;
        resolutionAnchor.resolvedOffset = resolvedOffset;
        resolutionAnchor.didResolve();
        final int mBarrierType3 = this.mBarrierType;
        if (mBarrierType3 != 0) {
            if (mBarrierType3 != 1) {
                if (mBarrierType3 != 2) {
                    if (mBarrierType3 != 3) {
                        return;
                    }
                    super.mTop.getResolutionNode().resolve(resolvedTarget, resolvedOffset);
                }
                else {
                    super.mBottom.getResolutionNode().resolve(resolvedTarget, resolvedOffset);
                }
            }
            else {
                super.mLeft.getResolutionNode().resolve(resolvedTarget, resolvedOffset);
            }
        }
        else {
            super.mRight.getResolutionNode().resolve(resolvedTarget, resolvedOffset);
        }
    }
    
    public void setAllowsGoneWidget(final boolean mAllowsGoneWidget) {
        this.mAllowsGoneWidget = mAllowsGoneWidget;
    }
    
    public void setBarrierType(final int mBarrierType) {
        this.mBarrierType = mBarrierType;
    }
}
