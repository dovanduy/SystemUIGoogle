// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import java.util.ArrayList;

public class ChainHead
{
    private boolean mDefined;
    protected ConstraintWidget mFirst;
    protected ConstraintWidget mFirstMatchConstraintWidget;
    protected ConstraintWidget mFirstVisibleWidget;
    protected boolean mHasComplexMatchWeights;
    protected boolean mHasDefinedWeights;
    protected boolean mHasUndefinedWeights;
    protected ConstraintWidget mHead;
    private boolean mIsRtl;
    protected ConstraintWidget mLast;
    protected ConstraintWidget mLastMatchConstraintWidget;
    protected ConstraintWidget mLastVisibleWidget;
    private int mOrientation;
    protected float mTotalWeight;
    protected ArrayList<ConstraintWidget> mWeightedMatchConstraintsWidgets;
    protected int mWidgetsCount;
    protected int mWidgetsMatchCount;
    
    public ChainHead(final ConstraintWidget mFirst, final int mOrientation, final boolean mIsRtl) {
        this.mTotalWeight = 0.0f;
        this.mIsRtl = false;
        this.mFirst = mFirst;
        this.mOrientation = mOrientation;
        this.mIsRtl = mIsRtl;
    }
    
    private void defineChainProperties() {
        final int n = this.mOrientation * 2;
        ConstraintWidget mFirst = this.mFirst;
        final boolean b = false;
        int i = 0;
        while (i == 0) {
            ++this.mWidgetsCount;
            final ConstraintWidget[] mListNextVisibleWidget = mFirst.mListNextVisibleWidget;
            final int mOrientation = this.mOrientation;
            final ConstraintWidget constraintWidget = null;
            mListNextVisibleWidget[mOrientation] = null;
            mFirst.mListNextMatchConstraintsWidget[mOrientation] = null;
            if (mFirst.getVisibility() != 8) {
                if (this.mFirstVisibleWidget == null) {
                    this.mFirstVisibleWidget = mFirst;
                }
                final ConstraintWidget mLastVisibleWidget = this.mLastVisibleWidget;
                if (mLastVisibleWidget != null) {
                    mLastVisibleWidget.mListNextVisibleWidget[this.mOrientation] = mFirst;
                }
                this.mLastVisibleWidget = mFirst;
                final ConstraintWidget.DimensionBehaviour[] mListDimensionBehaviors = mFirst.mListDimensionBehaviors;
                final int mOrientation2 = this.mOrientation;
                if (mListDimensionBehaviors[mOrientation2] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    final int[] mResolvedMatchConstraintDefault = mFirst.mResolvedMatchConstraintDefault;
                    if (mResolvedMatchConstraintDefault[mOrientation2] == 0 || mResolvedMatchConstraintDefault[mOrientation2] == 3 || mResolvedMatchConstraintDefault[mOrientation2] == 2) {
                        ++this.mWidgetsMatchCount;
                        final float[] mWeight = mFirst.mWeight;
                        final int mOrientation3 = this.mOrientation;
                        final float n2 = mWeight[mOrientation3];
                        if (n2 > 0.0f) {
                            this.mTotalWeight += mWeight[mOrientation3];
                        }
                        if (isMatchConstraintEqualityCandidate(mFirst, this.mOrientation)) {
                            if (n2 < 0.0f) {
                                this.mHasUndefinedWeights = true;
                            }
                            else {
                                this.mHasDefinedWeights = true;
                            }
                            if (this.mWeightedMatchConstraintsWidgets == null) {
                                this.mWeightedMatchConstraintsWidgets = new ArrayList<ConstraintWidget>();
                            }
                            this.mWeightedMatchConstraintsWidgets.add(mFirst);
                        }
                        if (this.mFirstMatchConstraintWidget == null) {
                            this.mFirstMatchConstraintWidget = mFirst;
                        }
                        final ConstraintWidget mLastMatchConstraintWidget = this.mLastMatchConstraintWidget;
                        if (mLastMatchConstraintWidget != null) {
                            mLastMatchConstraintWidget.mListNextMatchConstraintsWidget[this.mOrientation] = mFirst;
                        }
                        this.mLastMatchConstraintWidget = mFirst;
                    }
                }
            }
            final ConstraintAnchor mTarget = mFirst.mListAnchors[n + 1].mTarget;
            ConstraintWidget constraintWidget2 = constraintWidget;
            if (mTarget != null) {
                final ConstraintWidget mOwner = mTarget.mOwner;
                final ConstraintAnchor[] mListAnchors = mOwner.mListAnchors;
                constraintWidget2 = constraintWidget;
                if (mListAnchors[n].mTarget != null) {
                    if (mListAnchors[n].mTarget.mOwner != mFirst) {
                        constraintWidget2 = constraintWidget;
                    }
                    else {
                        constraintWidget2 = mOwner;
                    }
                }
            }
            if (constraintWidget2 != null) {
                mFirst = constraintWidget2;
            }
            else {
                i = 1;
            }
        }
        this.mLast = mFirst;
        if (this.mOrientation == 0 && this.mIsRtl) {
            this.mHead = mFirst;
        }
        else {
            this.mHead = this.mFirst;
        }
        boolean mHasComplexMatchWeights = b;
        if (this.mHasDefinedWeights) {
            mHasComplexMatchWeights = b;
            if (this.mHasUndefinedWeights) {
                mHasComplexMatchWeights = true;
            }
        }
        this.mHasComplexMatchWeights = mHasComplexMatchWeights;
    }
    
    private static boolean isMatchConstraintEqualityCandidate(final ConstraintWidget constraintWidget, final int n) {
        if (constraintWidget.getVisibility() != 8 && constraintWidget.mListDimensionBehaviors[n] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            final int[] mResolvedMatchConstraintDefault = constraintWidget.mResolvedMatchConstraintDefault;
            if (mResolvedMatchConstraintDefault[n] == 0 || mResolvedMatchConstraintDefault[n] == 3) {
                return true;
            }
        }
        return false;
    }
    
    public void define() {
        if (!this.mDefined) {
            this.defineChainProperties();
        }
        this.mDefined = true;
    }
}
