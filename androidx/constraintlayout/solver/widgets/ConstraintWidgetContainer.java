// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import java.io.PrintStream;
import java.util.Arrays;
import androidx.constraintlayout.solver.LinearSystem;

public class ConstraintWidgetContainer extends WidgetContainer
{
    private boolean mHeightMeasuredTooSmall;
    ChainHead[] mHorizontalChainsArray;
    int mHorizontalChainsSize;
    private boolean mIsRtl;
    private int mOptimizationLevel;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem;
    ChainHead[] mVerticalChainsArray;
    int mVerticalChainsSize;
    private boolean mWidthMeasuredTooSmall;
    
    public ConstraintWidgetContainer() {
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mOptimizationLevel = 3;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
    }
    
    private void addHorizontalChain(final ConstraintWidget constraintWidget) {
        final int mHorizontalChainsSize = this.mHorizontalChainsSize;
        final ChainHead[] mHorizontalChainsArray = this.mHorizontalChainsArray;
        if (mHorizontalChainsSize + 1 >= mHorizontalChainsArray.length) {
            this.mHorizontalChainsArray = Arrays.copyOf(mHorizontalChainsArray, mHorizontalChainsArray.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = new ChainHead(constraintWidget, 0, this.isRtl());
        ++this.mHorizontalChainsSize;
    }
    
    private void addVerticalChain(final ConstraintWidget constraintWidget) {
        final int mVerticalChainsSize = this.mVerticalChainsSize;
        final ChainHead[] mVerticalChainsArray = this.mVerticalChainsArray;
        if (mVerticalChainsSize + 1 >= mVerticalChainsArray.length) {
            this.mVerticalChainsArray = Arrays.copyOf(mVerticalChainsArray, mVerticalChainsArray.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = new ChainHead(constraintWidget, 1, this.isRtl());
        ++this.mVerticalChainsSize;
    }
    
    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }
    
    void addChain(final ConstraintWidget constraintWidget, final int n) {
        if (n == 0) {
            this.addHorizontalChain(constraintWidget);
        }
        else if (n == 1) {
            this.addVerticalChain(constraintWidget);
        }
    }
    
    public boolean addChildrenToSolver(final LinearSystem linearSystem) {
        this.addToSolver(linearSystem);
        for (int size = super.mChildren.size(), i = 0; i < size; ++i) {
            final ConstraintWidget constraintWidget = super.mChildren.get(i);
            if (constraintWidget instanceof ConstraintWidgetContainer) {
                final DimensionBehaviour[] mListDimensionBehaviors = constraintWidget.mListDimensionBehaviors;
                final DimensionBehaviour horizontalDimensionBehaviour = mListDimensionBehaviors[0];
                final DimensionBehaviour verticalDimensionBehaviour = mListDimensionBehaviors[1];
                if (horizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                if (verticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                constraintWidget.addToSolver(linearSystem);
                if (horizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(horizontalDimensionBehaviour);
                }
                if (verticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(verticalDimensionBehaviour);
                }
            }
            else {
                Optimizer.checkMatchParent(this, linearSystem, constraintWidget);
                constraintWidget.addToSolver(linearSystem);
            }
        }
        if (this.mHorizontalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 0);
        }
        if (this.mVerticalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 1);
        }
        return true;
    }
    
    @Override
    public void analyze(final int n) {
        super.analyze(n);
        for (int size = super.mChildren.size(), i = 0; i < size; ++i) {
            super.mChildren.get(i).analyze(n);
        }
    }
    
    public boolean handlesInternalConstraints() {
        return false;
    }
    
    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }
    
    public boolean isRtl() {
        return this.mIsRtl;
    }
    
    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }
    
    @Override
    public void layout() {
        final int mx = super.mX;
        final int my = super.mY;
        final int max = Math.max(0, this.getWidth());
        final int max2 = Math.max(0, this.getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (super.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            this.setX(this.mPaddingLeft);
            this.setY(this.mPaddingTop);
            this.resetAnchors();
            this.resetSolverVariables(this.mSystem.getCache());
        }
        else {
            super.mX = 0;
            super.mY = 0;
        }
        if (this.mOptimizationLevel != 0) {
            if (!this.optimizeFor(8)) {
                this.optimizeReset();
            }
            this.optimize();
            this.mSystem.graphOptimizer = true;
        }
        else {
            this.mSystem.graphOptimizer = false;
        }
        final DimensionBehaviour[] mListDimensionBehaviors = super.mListDimensionBehaviors;
        final DimensionBehaviour dimensionBehaviour = mListDimensionBehaviors[1];
        final DimensionBehaviour dimensionBehaviour2 = mListDimensionBehaviors[0];
        this.resetChains();
        final int size = super.mChildren.size();
        for (int i = 0; i < size; ++i) {
            final ConstraintWidget constraintWidget = super.mChildren.get(i);
            if (constraintWidget instanceof WidgetContainer) {
                ((WidgetContainer)constraintWidget).layout();
            }
        }
        int n2;
        int n = n2 = 0;
        int j = 1;
        while (j != 0) {
            final int n3 = n + 1;
            int addChildrenToSolver = j;
            try {
                this.mSystem.reset();
                addChildrenToSolver = j;
                this.createObjectVariables(this.mSystem);
                for (int k = 0; k < size; ++k) {
                    addChildrenToSolver = j;
                    super.mChildren.get(k).createObjectVariables(this.mSystem);
                }
                addChildrenToSolver = j;
                final boolean b = (addChildrenToSolver = (this.addChildrenToSolver(this.mSystem) ? 1 : 0)) != 0;
                if (b) {
                    addChildrenToSolver = (b ? 1 : 0);
                    this.mSystem.minimize();
                    addChildrenToSolver = (b ? 1 : 0);
                }
            }
            catch (Exception obj) {
                obj.printStackTrace();
                final PrintStream out = System.out;
                final StringBuilder sb = new StringBuilder();
                sb.append("EXCEPTION : ");
                sb.append(obj);
                out.println(sb.toString());
            }
            if (addChildrenToSolver != 0) {
                this.updateChildrenFromSolver(this.mSystem, Optimizer.flags);
            }
            else {
                this.updateFromSolver(this.mSystem);
                for (int l = 0; l < size; ++l) {
                    final ConstraintWidget constraintWidget2 = super.mChildren.get(l);
                    if (constraintWidget2.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getWidth() < constraintWidget2.getWrapWidth()) {
                        Optimizer.flags[2] = true;
                        break;
                    }
                    if (constraintWidget2.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getHeight() < constraintWidget2.getWrapHeight()) {
                        Optimizer.flags[2] = true;
                        break;
                    }
                }
            }
            int n5;
            if (n3 < 8 && Optimizer.flags[2]) {
                int index = 0;
                int max3 = 0;
                int max4 = 0;
                while (index < size) {
                    final ConstraintWidget constraintWidget3 = super.mChildren.get(index);
                    max3 = Math.max(max3, constraintWidget3.mX + constraintWidget3.getWidth());
                    max4 = Math.max(max4, constraintWidget3.mY + constraintWidget3.getHeight());
                    ++index;
                }
                final int max5 = Math.max(super.mMinWidth, max3);
                final int max6 = Math.max(super.mMinHeight, max4);
                boolean b2;
                int n4;
                if (dimensionBehaviour2 == DimensionBehaviour.WRAP_CONTENT && this.getWidth() < max5) {
                    this.setWidth(max5);
                    super.mListDimensionBehaviors[0] = DimensionBehaviour.WRAP_CONTENT;
                    b2 = true;
                    n4 = 1;
                }
                else {
                    b2 = false;
                    n4 = n2;
                }
                n5 = (b2 ? 1 : 0);
                n2 = n4;
                if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    n5 = (b2 ? 1 : 0);
                    n2 = n4;
                    if (this.getHeight() < max6) {
                        this.setHeight(max6);
                        super.mListDimensionBehaviors[1] = DimensionBehaviour.WRAP_CONTENT;
                        n5 = 1;
                        n2 = 1;
                    }
                }
            }
            else {
                n5 = 0;
            }
            final int max7 = Math.max(super.mMinWidth, this.getWidth());
            int n6 = n5;
            if (max7 > this.getWidth()) {
                this.setWidth(max7);
                super.mListDimensionBehaviors[0] = DimensionBehaviour.FIXED;
                n6 = 1;
                n2 = 1;
            }
            final int max8 = Math.max(super.mMinHeight, this.getHeight());
            int n8;
            int n7;
            if (max8 > this.getHeight()) {
                this.setHeight(max8);
                super.mListDimensionBehaviors[1] = DimensionBehaviour.FIXED;
                n7 = (n8 = 1);
            }
            else {
                n8 = n2;
                n7 = n6;
            }
            int n9 = n7;
            int n10 = n8;
            Label_1028: {
                if (n8 == 0) {
                    int n11 = n7;
                    int n12 = n8;
                    if (super.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT) {
                        n11 = n7;
                        n12 = n8;
                        if (max > 0) {
                            n11 = n7;
                            n12 = n8;
                            if (this.getWidth() > max) {
                                this.mWidthMeasuredTooSmall = true;
                                super.mListDimensionBehaviors[0] = DimensionBehaviour.FIXED;
                                this.setWidth(max);
                                n11 = (n12 = 1);
                            }
                        }
                    }
                    n9 = n11;
                    n10 = n12;
                    if (super.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT) {
                        n9 = n11;
                        n10 = n12;
                        if (max2 > 0) {
                            n9 = n11;
                            n10 = n12;
                            if (this.getHeight() > max2) {
                                this.mHeightMeasuredTooSmall = true;
                                super.mListDimensionBehaviors[1] = DimensionBehaviour.FIXED;
                                this.setHeight(max2);
                                j = 1;
                                n2 = 1;
                                break Label_1028;
                            }
                        }
                    }
                }
                n2 = n10;
                j = n9;
            }
            n = n3;
        }
        if (super.mParent != null) {
            final int max9 = Math.max(super.mMinWidth, this.getWidth());
            final int max10 = Math.max(super.mMinHeight, this.getHeight());
            this.mSnapshot.applyTo(this);
            this.setWidth(max9 + this.mPaddingLeft + this.mPaddingRight);
            this.setHeight(max10 + this.mPaddingTop + this.mPaddingBottom);
        }
        else {
            super.mX = mx;
            super.mY = my;
        }
        if (n2 != 0) {
            final DimensionBehaviour[] mListDimensionBehaviors2 = super.mListDimensionBehaviors;
            mListDimensionBehaviors2[0] = dimensionBehaviour2;
            mListDimensionBehaviors2[1] = dimensionBehaviour;
        }
        this.resetSolverVariables(this.mSystem.getCache());
        if (this == this.getRootConstraintContainer()) {
            this.updateDrawPosition();
        }
    }
    
    public void optimize() {
        if (!this.optimizeFor(8)) {
            this.analyze(this.mOptimizationLevel);
        }
        this.solveGraph();
    }
    
    public boolean optimizeFor(final int n) {
        return (this.mOptimizationLevel & n) == n;
    }
    
    public void optimizeForDimensions(final int n, final int n2) {
        if (super.mListDimensionBehaviors[0] != DimensionBehaviour.WRAP_CONTENT) {
            final ResolutionDimension mResolutionWidth = super.mResolutionWidth;
            if (mResolutionWidth != null) {
                mResolutionWidth.resolve(n);
            }
        }
        if (super.mListDimensionBehaviors[1] != DimensionBehaviour.WRAP_CONTENT) {
            final ResolutionDimension mResolutionHeight = super.mResolutionHeight;
            if (mResolutionHeight != null) {
                mResolutionHeight.resolve(n2);
            }
        }
    }
    
    public void optimizeReset() {
        final int size = super.mChildren.size();
        this.resetResolutionNodes();
        for (int i = 0; i < size; ++i) {
            super.mChildren.get(i).resetResolutionNodes();
        }
    }
    
    public void preOptimize() {
        this.optimizeReset();
        this.analyze(this.mOptimizationLevel);
    }
    
    @Override
    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        super.reset();
    }
    
    public void setOptimizationLevel(final int mOptimizationLevel) {
        this.mOptimizationLevel = mOptimizationLevel;
    }
    
    public void setRtl(final boolean mIsRtl) {
        this.mIsRtl = mIsRtl;
    }
    
    public void solveGraph() {
        final ResolutionAnchor resolutionNode = this.getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
        final ResolutionAnchor resolutionNode2 = this.getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
        resolutionNode.resolve(null, 0.0f);
        resolutionNode2.resolve(null, 0.0f);
    }
    
    public void updateChildrenFromSolver(final LinearSystem linearSystem, final boolean[] array) {
        array[2] = false;
        this.updateFromSolver(linearSystem);
        for (int size = super.mChildren.size(), i = 0; i < size; ++i) {
            final ConstraintWidget constraintWidget = super.mChildren.get(i);
            constraintWidget.updateFromSolver(linearSystem);
            if (constraintWidget.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                array[2] = true;
            }
            if (constraintWidget.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                array[2] = true;
            }
        }
    }
}
