// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.constraintlayout.solver.LinearSystem;

public class ResolutionAnchor extends ResolutionNode
{
    private ResolutionDimension dimension;
    private int dimensionMultiplier;
    ConstraintAnchor myAnchor;
    float offset;
    private ResolutionAnchor opposite;
    private ResolutionDimension oppositeDimension;
    private int oppositeDimensionMultiplier;
    float resolvedOffset;
    ResolutionAnchor resolvedTarget;
    ResolutionAnchor target;
    int type;
    
    public ResolutionAnchor(final ConstraintAnchor myAnchor) {
        this.type = 0;
        this.dimension = null;
        this.dimensionMultiplier = 1;
        this.oppositeDimension = null;
        this.oppositeDimensionMultiplier = 1;
        this.myAnchor = myAnchor;
    }
    
    void addResolvedValue(final LinearSystem linearSystem) {
        final SolverVariable solverVariable = this.myAnchor.getSolverVariable();
        final ResolutionAnchor resolvedTarget = this.resolvedTarget;
        if (resolvedTarget == null) {
            linearSystem.addEquality(solverVariable, (int)this.resolvedOffset);
        }
        else {
            linearSystem.addEquality(solverVariable, linearSystem.createObjectVariable(resolvedTarget.myAnchor), (int)this.resolvedOffset, 6);
        }
    }
    
    public void dependsOn(final int type, final ResolutionAnchor target, final int n) {
        this.type = type;
        this.target = target;
        this.offset = (float)n;
        target.addDependent(this);
    }
    
    public void dependsOn(final ResolutionAnchor target, final int n) {
        this.target = target;
        this.offset = (float)n;
        target.addDependent(this);
    }
    
    public void dependsOn(final ResolutionAnchor target, final int dimensionMultiplier, final ResolutionDimension dimension) {
        (this.target = target).addDependent(this);
        this.dimension = dimension;
        this.dimensionMultiplier = dimensionMultiplier;
        dimension.addDependent(this);
    }
    
    public float getResolvedValue() {
        return this.resolvedOffset;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.target = null;
        this.offset = 0.0f;
        this.dimension = null;
        this.dimensionMultiplier = 1;
        this.oppositeDimension = null;
        this.oppositeDimensionMultiplier = 1;
        this.resolvedTarget = null;
        this.resolvedOffset = 0.0f;
        this.opposite = null;
        this.type = 0;
    }
    
    @Override
    public void resolve() {
        final int state = super.state;
        final int n = 1;
        if (state == 1) {
            return;
        }
        if (this.type == 4) {
            return;
        }
        final ResolutionDimension dimension = this.dimension;
        if (dimension != null) {
            if (dimension.state != 1) {
                return;
            }
            this.offset = this.dimensionMultiplier * dimension.value;
        }
        final ResolutionDimension oppositeDimension = this.oppositeDimension;
        if (oppositeDimension != null) {
            if (oppositeDimension.state != 1) {
                return;
            }
            final float value = oppositeDimension.value;
        }
        if (this.type == 1) {
            final ResolutionAnchor target = this.target;
            if (target == null || target.state == 1) {
                final ResolutionAnchor target2 = this.target;
                if (target2 == null) {
                    this.resolvedTarget = this;
                    this.resolvedOffset = this.offset;
                }
                else {
                    this.resolvedTarget = target2.resolvedTarget;
                    this.resolvedOffset = target2.resolvedOffset + this.offset;
                }
                this.didResolve();
                return;
            }
        }
        if (this.type == 2) {
            final ResolutionAnchor target3 = this.target;
            if (target3 != null && target3.state == 1) {
                final ResolutionAnchor opposite = this.opposite;
                if (opposite != null) {
                    final ResolutionAnchor target4 = opposite.target;
                    if (target4 != null && target4.state == 1) {
                        if (LinearSystem.getMetrics() != null) {
                            final Metrics metrics = LinearSystem.getMetrics();
                            ++metrics.centerConnectionResolved;
                        }
                        this.resolvedTarget = this.target.resolvedTarget;
                        final ResolutionAnchor opposite2 = this.opposite;
                        opposite2.resolvedTarget = opposite2.target.resolvedTarget;
                        final ConstraintAnchor.Type mType = this.myAnchor.mType;
                        final ConstraintAnchor.Type right = ConstraintAnchor.Type.RIGHT;
                        int n2 = 0;
                        int n3 = n;
                        if (mType != right) {
                            if (mType == ConstraintAnchor.Type.BOTTOM) {
                                n3 = n;
                            }
                            else {
                                n3 = 0;
                            }
                        }
                        float n4;
                        float n5;
                        if (n3 != 0) {
                            n4 = this.target.resolvedOffset;
                            n5 = this.opposite.target.resolvedOffset;
                        }
                        else {
                            n4 = this.opposite.target.resolvedOffset;
                            n5 = this.target.resolvedOffset;
                        }
                        final float n6 = n4 - n5;
                        final ConstraintAnchor myAnchor = this.myAnchor;
                        final ConstraintAnchor.Type mType2 = myAnchor.mType;
                        float n7;
                        float n8;
                        if (mType2 != ConstraintAnchor.Type.LEFT && mType2 != ConstraintAnchor.Type.RIGHT) {
                            n7 = n6 - myAnchor.mOwner.getHeight();
                            n8 = this.myAnchor.mOwner.mVerticalBiasPercent;
                        }
                        else {
                            n7 = n6 - this.myAnchor.mOwner.getWidth();
                            n8 = this.myAnchor.mOwner.mHorizontalBiasPercent;
                        }
                        final int margin = this.myAnchor.getMargin();
                        int margin2 = this.opposite.myAnchor.getMargin();
                        if (this.myAnchor.getTarget() == this.opposite.myAnchor.getTarget()) {
                            n8 = 0.5f;
                            margin2 = 0;
                        }
                        else {
                            n2 = margin;
                        }
                        final float n9 = (float)n2;
                        final float n10 = (float)margin2;
                        final float n11 = n7 - n9 - n10;
                        if (n3 != 0) {
                            final ResolutionAnchor opposite3 = this.opposite;
                            opposite3.resolvedOffset = opposite3.target.resolvedOffset + n10 + n11 * n8;
                            this.resolvedOffset = this.target.resolvedOffset - n9 - n11 * (1.0f - n8);
                        }
                        else {
                            this.resolvedOffset = this.target.resolvedOffset + n9 + n11 * n8;
                            final ResolutionAnchor opposite4 = this.opposite;
                            opposite4.resolvedOffset = opposite4.target.resolvedOffset - n10 - n11 * (1.0f - n8);
                        }
                        this.didResolve();
                        this.opposite.didResolve();
                        return;
                    }
                }
            }
        }
        if (this.type == 3) {
            final ResolutionAnchor target5 = this.target;
            if (target5 != null && target5.state == 1) {
                final ResolutionAnchor opposite5 = this.opposite;
                if (opposite5 != null) {
                    final ResolutionAnchor target6 = opposite5.target;
                    if (target6 != null && target6.state == 1) {
                        if (LinearSystem.getMetrics() != null) {
                            final Metrics metrics2 = LinearSystem.getMetrics();
                            ++metrics2.matchConnectionResolved;
                        }
                        final ResolutionAnchor target7 = this.target;
                        this.resolvedTarget = target7.resolvedTarget;
                        final ResolutionAnchor opposite6 = this.opposite;
                        final ResolutionAnchor target8 = opposite6.target;
                        opposite6.resolvedTarget = target8.resolvedTarget;
                        this.resolvedOffset = target7.resolvedOffset + this.offset;
                        opposite6.resolvedOffset = target8.resolvedOffset + opposite6.offset;
                        this.didResolve();
                        this.opposite.didResolve();
                        return;
                    }
                }
            }
        }
        if (this.type == 5) {
            this.myAnchor.mOwner.resolve();
        }
    }
    
    public void resolve(final ResolutionAnchor resolvedTarget, final float resolvedOffset) {
        if (super.state == 0 || (this.resolvedTarget != resolvedTarget && this.resolvedOffset != resolvedOffset)) {
            this.resolvedTarget = resolvedTarget;
            this.resolvedOffset = resolvedOffset;
            if (super.state == 1) {
                this.invalidate();
            }
            this.didResolve();
        }
    }
    
    String sType(final int n) {
        if (n == 1) {
            return "DIRECT";
        }
        if (n == 2) {
            return "CENTER";
        }
        if (n == 3) {
            return "MATCH";
        }
        if (n == 4) {
            return "CHAIN";
        }
        if (n == 5) {
            return "BARRIER";
        }
        return "UNCONNECTED";
    }
    
    public void setOpposite(final ResolutionAnchor opposite, final float n) {
        this.opposite = opposite;
    }
    
    public void setOpposite(final ResolutionAnchor opposite, final int oppositeDimensionMultiplier, final ResolutionDimension oppositeDimension) {
        this.opposite = opposite;
        this.oppositeDimension = oppositeDimension;
        this.oppositeDimensionMultiplier = oppositeDimensionMultiplier;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        if (super.state != 1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("{ ");
            sb.append(this.myAnchor);
            sb.append(" UNRESOLVED} type: ");
            sb.append(this.sType(this.type));
            return sb.toString();
        }
        if (this.resolvedTarget == this) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("[");
            sb2.append(this.myAnchor);
            sb2.append(", RESOLVED: ");
            sb2.append(this.resolvedOffset);
            sb2.append("]  type: ");
            sb2.append(this.sType(this.type));
            return sb2.toString();
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("[");
        sb3.append(this.myAnchor);
        sb3.append(", RESOLVED: ");
        sb3.append(this.resolvedTarget);
        sb3.append(":");
        sb3.append(this.resolvedOffset);
        sb3.append("] type: ");
        sb3.append(this.sType(this.type));
        return sb3.toString();
    }
    
    public void update() {
        final ConstraintAnchor target = this.myAnchor.getTarget();
        if (target == null) {
            return;
        }
        if (target.getTarget() == this.myAnchor) {
            this.type = 4;
            target.getResolutionNode().type = 4;
        }
        final int margin = this.myAnchor.getMargin();
        final ConstraintAnchor.Type mType = this.myAnchor.mType;
        int n = 0;
        Label_0074: {
            if (mType != ConstraintAnchor.Type.RIGHT) {
                n = margin;
                if (mType != ConstraintAnchor.Type.BOTTOM) {
                    break Label_0074;
                }
            }
            n = -margin;
        }
        this.dependsOn(target.getResolutionNode(), n);
    }
}
